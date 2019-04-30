package br.com.avinfo.avboleto.routes;

import static br.com.avinfo.avboleto.dto.EComandoStatus.PROCESSANDO;
import static br.com.avinfo.avboleto.dto.EComandoStatus.SUCESSO;
import static br.com.avinfo.avboleto.sql.Queries.FIND_COMMANDOS;
import static br.com.avinfo.avboleto.sql.Queries.UPDATE_COMANDO_STATUS;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.com.avinfo.avboleto.dto.ComandoStatus;

@Component
public class ComandosRoute extends RouteBuilder {
	
	static Logger LOG = LoggerFactory.getLogger(ComandosRoute.class);
	
	@Override
	public void configure() throws Exception {
		
		mainLookupComandos();

		dbUpdateComandoStatus();
		
		sendComandoRetorno();
		
		sendComandoException();
		
		insereComando();
		
		comandoFim();
		
	}

	private void mainLookupComandos() {
		from("direct:look-up-comandos")
			.routeId("look-up-comandos")
			.to("sql:" + FIND_COMMANDOS + "?dataSourceRef=dataSource&outputType=StreamList")
			.split(body()).streaming()
				.setHeader("comando-nome", simple("${body[comando]}"))
				.setHeader("comando_ids", simple("${body[id]}"))
				.setHeader("comando-status", constant(PROCESSANDO.getStatus())) // atualiza para status processando (4)
				.to("direct:db-update-comando-status")
				
				.setHeader("comando-status", simple("null"))
				
				.log("Iniciando comando: ${body[comando]}")
				.recipientList(simple("direct:${body[comando]}"))
				
				.to("direct:db-update-comando-status")
		.end();
	}

	private void dbUpdateComandoStatus() {
		from("direct:db-update-comando-status")
			.routeId("db-update-comando-status")
			.choice()
				.when(header("comando-status").isNull())
					.setHeader("comando-status", constant(SUCESSO.getStatus())) // atualiza para status sucesso (2)
			.end()
			.to("sql:" + UPDATE_COMANDO_STATUS + "?dataSourceRef=dataSource")
		.end();
	}

	private void sendComandoRetorno() {
		from("direct:send-comando-retorno")
			.routeId("send-comando-retorno")
			.choice()
				.when(simple("${body[_status]} == 'sucesso'"))
					.log("status: ${body[_status]}, dados: ${body[_dados]}")
					.setHeader("comando-status", constant(ComandoStatus.SUCESSO)) //sucesso
					.choice()
						.when(header("comando-mensagem").isNull())
							.setHeader("comando-mensagem", simple("${body}", String.class))
					.endChoice()
				.otherwise()
					.log("status: erro, ${body}")
					.setHeader("comando-status", constant(ComandoStatus.ERRO))  //erro
					.setHeader("comando-mensagem", simple("erro: ${body}", String.class))
			.end()
			.to("direct:comando-fim")
		.end();
	}

	private void comandoFim() {
		from("direct:comando-fim")
			.log("Comando: ${header.comando-nome} - Executado")
		.end();
	}

	private void insereComando() {
		from("direct:insere-boleto-comando-db")
			.routeId("insere-boleto-comando-db")
			.to("sql:INSERT INTO boletocomando (comando, param1, param2, status) VALUES (:#${body[comando]}, :#${body[param1]}, :#${body[param2]}, :#${body[status]})?dataSourceRef=dataSource");
	}

	private void sendComandoException() {
		/**
		 * Invoca essa rota só em caso de exceção na aplicação
		 */
		from("direct:send-comando-exception")
			.routeId("comando-retorno-exception")
			.log("ERRO: ${body}")
			.setHeader("comando-status", constant(ComandoStatus.ERRO))
			.setHeader("comando-mensagem", simple("erro: ${exception}", String.class))
			.process(exchange -> {
				Throwable ex = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
				LOG.error("Erro-inesperado= " + ex.getMessage(), ex);
			})
			.to("direct:db-update-comando-status")
		.end();
	}

}

package br.com.avinfo.avboleto.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.model.ProcessorDefinition;
import org.springframework.stereotype.Component;

import br.com.avinfo.avboleto.base.BoletoBaseRouteBuilder;
import br.com.avinfo.avboleto.dto.EhBoletoSituacao;

@Component
public class ConsultaBoletoRoute extends BoletoBaseRouteBuilder {

	@Override
	public void configure() throws Exception {
		
		from("direct:consulta-boleto")
			.routeId("consulta-boleto")
			.setHeader(Exchange.HTTP_QUERY, simple("IdIntegracao=${body[param1]}"))
			.setBody(simple("null"))
			.to("direct:ws-consulta-boleto");
		
		consultaBoletoWS();
		updateStatusBoletosConsultados();
			
	}

	private ProcessorDefinition<?> updateStatusBoletosConsultados() {
		return from("direct:update-boleto-consultados")
				.routeId("update-boleto-consultados")
				.choice()
					.when(simple("${body[_status]} == 'sucesso' && ${body[_dados].size()} > 0"))
						.log(LoggingLevel.INFO, LOGGER, "Titulos Retornados: (${body[_dados].size()}) titulos")
						.split(simple("${body[_dados]}"))
							.setProperty("situacaoId", method(EhBoletoSituacao.class, "situacao(${body[situacao]})"))
							.setProperty("situacao", simple("${body[situacao]}"))
								.log(LoggingLevel.INFO, LOGGER, " --> ATUALIZADO Boleto Status[${body[situacao]}]=${body[IdIntegracao]}")
								.to("sql:UPDATE statusboleto SET situacao = :#${property.situacaoId}, descricao = :#${property.situacao} WHERE id_integracao = :#${body[IdIntegracao]}")
						.end()
					.endChoice()
				.end()
			.end();
	}

	private ProcessorDefinition<?> consultaBoletoWS() {
		return reqGetJson("ws-consulta-boleto", "boletos")
				 .to("direct:update-boleto-consultados");
	}

}

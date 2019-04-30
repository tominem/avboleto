package br.com.avinfo.avboleto.routes.sql;

import static br.com.avinfo.avboleto.dto.EComandoStatus.ERRO;
import static br.com.avinfo.avboleto.sql.Queries.FIND_BOLETOS;
import static br.com.avinfo.avboleto.sql.Queries.INSERT_BOLETO_PROTOCOLO;
import static br.com.avinfo.avboleto.sql.Queries.UPDATE_BOLETO_PROTOCOLO_PDF;
import static br.com.avinfo.avboleto.sql.Queries.UPDATE_STATUS_BOLETO_BY_IDS;
import static br.com.avinfo.avboleto.sql.Queries.UPDATE_STATUS_BOLETO_BY_NUMERO_DOCUMENTO;
import static br.com.avinfo.avboleto.sql.Queries.UPDATE_STATUS_BOLETO_PROTOCOLO_BY_IDS;

import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import br.com.avinfo.avboleto.dto.EhBoletoSituacao;
import br.com.avinfo.avboleto.processor.IncluirBoletoProcessor;
import br.com.avinfo.avboleto.sql.Queries;

@Component
public class BoletoSqlRoute extends RouteBuilder {

	private IncluirBoletoProcessor boletoProcessor = new IncluirBoletoProcessor();
	

	@Override
	public void configure() throws Exception {
		
		from("direct:db-consulta-boletos-status-pendente")
			.routeId("db-consulta-boletos-status-pendente")
			.to("sql:" + FIND_BOLETOS + "?dataSourceRef=dataSource&outputType=StreamList")
			.process(boletoProcessor)
		.end();
		
		
		from("direct:db-update-status-boleto-incluir-boleto-sucesso")
			.routeId("db-update-status-boleto-incluir-boleto-sucesso")
			.choice()
				.when(simple("${body[_status]} == 'sucesso'"))
					.split().simple("${body[_dados][_sucesso]}")
						.setProperty("idIntegracao", simple("${body[idintegracao]}"))
						.setProperty("situacao", simple("${body[situacao]}"))
						.setProperty("situacaoId", method(EhBoletoSituacao.class, "situacao(${body[situacao]})"))
						.setProperty("mensagem", constant("Sucesso"))
						.setProperty("numeroDocumento", simple("${body[TituloNumeroDocumento]}"))
						.setProperty("nossoNumero", simple("${body[TituloNossoNumero]}"))
						.log("Boleto Sucesso Numero: ${property.numeroDocumento}")
						.to("direct:db-update-status-boleto-by-numero")
		.end();

		
		from("direct:db-update-status-boleto-incluir-boleto-falha")
			.routeId("db-update-status-boleto-incluir-boleto-falha")
			.choice()
				.when(simple("${body[_status]} == 'sucesso'"))
					.split().simple("${body[_dados][_falha]}")
						.setProperty("idIntegracao", simple("${body[_dados][idintegracao]}"))
						.setProperty("situacao", simple("${body[_dados][situacao]}"))
						.setProperty("situacaoId", method(EhBoletoSituacao.class, "situacao(${body[_dados][situacao]})"))
						.choice()
							.when(simple("${body[_dados][situacao]} == 'FALHA'"))
								.setProperty("mensagem", simple("status_http: ${body[_status_http]}, erro: ${body[_erro]}"))
							.otherwise()
								.setProperty("mensagem", constant("sucesso"))
						.end()
						.setProperty("numeroDocumento", simple("${body[_dados][TituloNumeroDocumento]}"))
						.setProperty("nossoNumero", simple("${body[_dados][TituloNossoNumero]}"))
						.to("direct:db-update-status-boleto-by-numero")
		.end();
		
		from("direct:db-update-status-boleto-incluir-boletos-erro")
			.routeId("db-update-status-boleto-incluir-boletos-erro")
			.choice()
				.when(simple("${body[_status]} == 'erro'"))
					.split().simple("${body[_dados][_falha]}")
						.setProperty("idIntegracao", simple("null"))
						.setProperty("situacao", constant("FALHA"))
						.setProperty("situacaoId", method(EhBoletoSituacao.class, "situacao('FALHA')"))
						.setProperty("mensagem", simple("Erros: ${body[_erros]}"))
						.setProperty("numeroDocumento", simple("${body[TituloNumeroDocumento]}"))
						.setProperty("nossoNumero", simple("${body[TituloNossoNumero]}"))
						.setHeader("ids_filtrados", method(boletoProcessor, "getIdsFiltrados"))
						.setHeader("comando-status", constant(ERRO.getStatus()))
						.to("direct:db-update-status-boleto-by-ids")
		.end();
						
		
		from("direct:db-update-status-boleto-by-ids")
			.routeId("db-update-status-boleto-by-ids")
			.log("db-update-status-boleto-by-ids -- SUCCESS")
		.to("sql:" + UPDATE_STATUS_BOLETO_BY_IDS + "?dataSourceRef=dataSource");	
		
		
		from("direct:db-update-status-boleto-by-numero")
			.routeId("db-update-status-boleto-by-numero")
			.log("UPDATE STATUS BOLETO: Numero-doc: ${property.numeroDocumento} e nosso-numero: ${property.nossoNumero}")
		.to("sql:" + UPDATE_STATUS_BOLETO_BY_NUMERO_DOCUMENTO + "?dataSourceRef=dataSource");
		
		
		from("direct:db-consulta-boletos-status-emitidos-by-ids-filtrados")
			.routeId("db-consulta-boletos-status-emitidos-by-ids-filtrados")
			.setHeader("ids_filtrados", method(boletoProcessor, "getIdsFiltrados"))
			.setProperty("situacaoId", method(EhBoletoSituacao.EMITIDO, "getSituacaoId"))
			.choice()
				.when(simple("${header.ids_filtrados} != null or ${header.ids_filtrados} != ''"))
					.to("sql:" + Queries.FIND_IDS_INTEGRACAO_BOLETOS_DIF_ERRO + "?dataSourceRef=dataSource")
					.process(this::idsIntegracaoProcess)
//				.otherwise()
//					.to("sql:" + FIND_IDS_INTEGRACAO_BOLETOS_BY_SITUACAO + "?dataSourceRef=dataSource")
//					.process(this::idsIntegracaoProcess)
//					.process(ex -> boletoProcessor.setIdsFiltrados(ex.getIn().getBody(String.class)))
		.end();
		
		
		from("direct:db-insert-boleto-protocolo")
			.routeId("db-insert-boleto-protocolo")
			.choice()
				.when(simple("${body[_status]} == 'sucesso'"))
					.setProperty("protocolo", simple("${body[_dados][protocolo]}"))
					.to("sql:" + INSERT_BOLETO_PROTOCOLO + "?dataSourceRef=dataSource")
		.end();
					
		from("direct:db-update-protocolo-boletostatus")
			.routeId("db-update-protocolo-boletostatus")
			.choice()
				.when(simple("${body[_status]} == 'sucesso'"))
					.setHeader("ids_filtrados", method(boletoProcessor, "getIdsFiltrados"))
				    .setProperty("situacao", constant("PROTOCOLADO"))
				    .setProperty("mensagem", simple("${body[_mensagem]}"))
				    .setProperty("situacaoId", method(EhBoletoSituacao.PROTOCOLADO, "getSituacaoId"))
					.setProperty("protocolo", simple("${body[_dados][protocolo]}"))
					.to("sql:" + UPDATE_STATUS_BOLETO_PROTOCOLO_BY_IDS + "?dataSourceRef=dataSource")
		.end();

		
		from("direct:db-update-protocolo-boleto-erro")
			.routeId("db-update-protocolo-boleto-erro")
			.choice()
				.when(simple("${body[_status]} == 'erro'"))
					.setHeader("ids_filtrados", method(boletoProcessor, "getIdsFiltrados"))
				    .setProperty("situacao", constant("FALHA"))
				    .setProperty("mensagem", simple("${body[_mensagem]}"))
				    .setProperty("situacaoId", method(EhBoletoSituacao.FALHA, "getSituacaoId"))
					.setProperty("protocolo", simple("${body[_dados][protocolo]}"))
					.to("sql:" + UPDATE_STATUS_BOLETO_PROTOCOLO_BY_IDS + "?dataSourceRef=dataSource")
					.setBody(simple("null"))
		.end();
		
		
		from("direct:update-boleto-protocolo-pdf")
			.routeId("update-boleto-protocolo-pdf")
			.choice()
				.when(simple("${body} is 'java.lang.byte[]'"))
					.to("sql:" + UPDATE_BOLETO_PROTOCOLO_PDF + "?dataSourceRef=dataSource")
//					.to("file://out?fileName=pdf.pdf")
		.end();
		
		
		/**
		 * Rota somente usada em testes unitarios
		 */
		from("direct:insere-status-boleto")
			.routeId("insere-status-boleto")
			.split(body().tokenize(";"))
			.to("sql:INSERT INTO statusboleto (Id, Descricao, situacao) VALUES "
					+ "(:#${body}, 'PENDENTE', 1)");
		
		from("direct:delete-status-boleto")
			.routeId("delete-status-boleto")
			.to("sql:DELETE FROM statusboleto");
		
		from("direct:delete-boleto-protocolo")
			.routeId("delete-boleto-protocolo")
			.to("sql:DELETE FROM boletoprotocolo");
					
	}
	
	private void idsIntegracaoProcess(Exchange exchange) {
		String ids = "";
		List<?> values = exchange.getIn().getBody(List.class);
		for (Object o : values) {
			Map<?,?> map = (Map<?, ?>) o;
			for (Object item : map.values()) {
				String val = item.toString();
				ids += ids.isEmpty() ? val : ("," + val);
			}
		}
		
		exchange.getIn().setBody(ids.isEmpty() ? null : ids);
	}
	
}

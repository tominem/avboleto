package br.com.avinfo.avboleto.routes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.springframework.stereotype.Component;

import br.com.avinfo.avboleto.base.BoletoBaseRouteBuilder;
import br.com.avinfo.avboleto.dto.EhBoletoSituacao;
import br.com.avinfo.avboleto.processor.FileBase64ReaderProcessor;
import br.com.avinfo.avboleto.sql.Queries;

@Component
public class UploadArquivoRetornoRoute extends BoletoBaseRouteBuilder {

	public static final String FILE_HEADER = "file";

	@Override
	public void configure() throws Exception {
		
		from("direct:upload-retorno")
			.routeId("upload-retorno")
			.setHeader(FILE_HEADER, simple("${body[param1]}"))
			.process(new FileBase64ReaderProcessor())
			.process(convertToMap())
			.marshal("myJsonFormat")
		.to("direct:ws-upload-retorno");
		
		uploadRetornoWS()
			.multicast()
				.to("direct:pega-protocolo-retorno")
				.to("direct:if-send-comando-retorno-erro")
		.end();
				
		pegaProtocoloRetorno();
		consultaProtocoloRetorno();
		updateConciliados();
		updateNaoConciliados();
		consultaStatusConciliados();
		
	}
	
	private Processor convertToMap() {
		return e -> {
			String fileBase64 = e.getIn().getBody(String.class);
			Map<String, String> map = new HashMap<String, String>();
			map.put("arquivo", fileBase64);
			e.getIn().setBody(map);
		};
	}

	private void pegaProtocoloRetorno() {
		from("direct:pega-protocolo-retorno")
			.routeId("pega-protocolo-retorno")
			.choice()
				.when(simple("${body[_status]} == 'sucesso'"))
					.setHeader("protocolo", simple("${body[_dados][protocolo]}"))
					.log(LoggingLevel.INFO, LOGGER, "pega-protocolo-retorno: ${header.protocolo}")
					.marshal("myJsonFormat")
					.to("direct:ws-consulta-protocolo-retorno")
			.endChoice()
		.end();
	}
	
	private ProcessorDefinition<?> consultaProtocoloRetorno() {
		return reqGetJson("ws-consulta-protocolo-retorno", "retornos/${header.protocolo}")
					.multicast()
						.to("direct:update-conciliados")
						.to("direct:update-nao-conciliados")
					.end()
				.to("direct:consulta-status-conciliados");
	}
	
	private ProcessorDefinition<?> updateConciliados() {
		return from("direct:update-conciliados")
					.routeId("update-conciliados")
					.choice()
						.when(simple("${body[_status]} == 'sucesso' && ${body[_dados][titulos].size()} > 0"))
							.setProperty("situacaoId", constant(EhBoletoSituacao.CONCILIADO.getSituacaoId()))
							.setProperty("situacao", constant(EhBoletoSituacao.CONCILIADO.name()))
							.log(LoggingLevel.INFO, LOGGER, "Titulos Conciliados: (${body[_dados][titulos].size()}) encontrados")
							.split(simple("${body[_dados][titulos]}"))
								.log(LoggingLevel.INFO, LOGGER, " --> IdIntegracao[conciliado]=${body[idIntegracao]}")
								.to("sql:UPDATE statusboleto SET situacao = :#${property.situacaoId}, descricao = :#${property.situacao} WHERE id_integracao = :#${body[idIntegracao]}")
							.end()
						.endChoice()
					.end()
				.end();
	}
	
	private ProcessorDefinition<?> updateNaoConciliados() {
		return from("direct:update-nao-conciliados")
					.routeId("update-nao-conciliados")
					.choice()
						.when(simple("${body[_status]} == 'sucesso' && ${body[_dados][titulosNaoConciliados].size()} > 0"))
							.setProperty("situacaoId", constant(EhBoletoSituacao.NAO_CONCILIADOS.getSituacaoId()))
							.setProperty("situacao", constant(EhBoletoSituacao.NAO_CONCILIADOS.name()))
							.log(LoggingLevel.INFO, LOGGER, "Titulos Nao-Conciliados: (${body[_dados][titulosNaoConciliados].size()}) encontrados")
							.split(simple("${body[_dados][titulosNaoConciliados]}"))
								.log(LoggingLevel.INFO, LOGGER, " --> Numero-Documento[Nao-Conciliado]=${body[TituloNumeroDocumento]}")
								.to("sql:UPDATE statusboleto SET situacao = :#${property.situacaoId}, descricao = :#${property.situacao} "
										+ "WHERE id in (Select bol.id from boleto bol WHERE bol.NumeroBoleto = :#${body[TituloNumeroDocumento]})")
							.end()
						.endChoice()
					.end()
				.end();
	}
	
	private ProcessorDefinition<?> consultaStatusConciliados() {
		return from("direct:consulta-status-conciliados")
				.routeId("consulta-status-conciliados")
				.setProperty("situacaoId", constant(EhBoletoSituacao.CONCILIADO.getSituacaoId()))
				.to("sql:" + Queries.FIND_IDS_INTEGRACAO_BOLETOS_BY_SITUACAO + "?dataSourceRef=dataSource")
				.process(ex -> {
					
					List<String> ids = new ArrayList<>();
					
					@SuppressWarnings("unchecked")
					List<Map<?,?>> results = ex.getIn().getBody(List.class);
					if (results != null && results.size() > 0) {
						for (Map<?, ?> map : results) {
							String idIntegracao = map.get("id_integracao").toString();
							ids.add(idIntegracao);
						}
					}
					
					Map<String, String> newBody = new HashMap<>();
					newBody.put("param1", ids.isEmpty() ? null : String.join(",", ids));
					
					ex.getIn().setBody(newBody);
					
				})
				.to("direct:consulta-boleto")
			.end();
	}

	private RouteDefinition uploadRetornoWS() {
		return reqPostJson("ws-upload-retorno", "retornos");
	}

}

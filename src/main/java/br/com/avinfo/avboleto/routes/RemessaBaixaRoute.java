package br.com.avinfo.avboleto.routes;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.LoggingLevel;
import org.apache.camel.model.ProcessorDefinition;

import br.com.avinfo.avboleto.base.BoletoBaseRouteBuilder;
import br.com.avinfo.avboleto.dto.EhBoletoSituacao;

public class RemessaBaixaRoute extends BoletoBaseRouteBuilder{

	@Override
	public void configure() throws Exception {

		from("direct:solicita-remessa-baixa")
			.id("solicita-remessa-baixa")
			.setBody(simple("${body[id]}"))
			.setHeader("idsIntegracao", simple("${body[id]}"))
			.process(ex -> {
				List<String> list = new ArrayList<>();
				String[] values = ex.getIn().getBody(String.class).split(",");
				for (String s : values) {
					list.add(s.trim());
				}
				
				ex.getIn().setBody(list);
			})
			.setHeader("ids_integracao", body())
			.marshal("myJson")
			.to("direct:solitita-remessa-baixa-ws")
			.setHeader("idsIntegracao", simple("${header.idsIntegracao}"))
			.multicast()
				.to("direct:update-status-sucesso-remessa-baixa")
				.to("direct:update-status-erro-remessa-baixa")
				.to("direct:send-comando-retorno")
			.end();
		
		solcitaRemessaBaixaWS();
		updateStatusSucessoRemessaBaixa();
		updateStatusErroRemessaBaixa();
		
	}
	
	private void updateStatusErroRemessaBaixa() {
		from("direct:update-status-sucesso-remessa-baixa")
			.id("update-status-sucesso-remessa-baixa")
			.choice()
			.when(simple("${body[_status]} == 'sucesso' && ${body[_dados][_erro]} != null"))
				.setProperty("situacaoId", constant(EhBoletoSituacao.FALHA.getSituacaoId()))
			    .setProperty("situacao", constant(EhBoletoSituacao.FALHA.name()))
			    .setProperty("mensagem", simple("${body[_dados]}"))
			    .setHeader("protocolo", simple("null"))
			    .log(LoggingLevel.INFO, LOGGER, "update-status-erro-remessa-baixa: ${body[_mensagem]}")
			    .multicast()
					.to("sql:UPDATE statusboleto SET situacao = :#${property.situacaoId}, "
							+ "descricao = :#${property.situacao}, "
							+ "protocolo = :#${header.protocolo}, "
							+ "mensagem = :#${property.mensagem} "
							+ "WHERE id_integracao IN ( :#in:idsIntegracao )")
			.endChoice()
		.end();
	}

	private void updateStatusSucessoRemessaBaixa() {
		from("direct:update-status-sucesso-remessa-baixa")
			.id("update-status-sucesso-remessa-baixa")
			.choice()
			.when(simple("${body[_status]} == 'sucesso' && ${body[_dados][protocolo]} != null"))
				.setProperty("situacaoId", method(EhBoletoSituacao.class, "situacao(${body[_dados][situacao]})"))
				.setProperty("situacao", simple("${body[_dados][situacao]}"))
				.setHeader("protocolo", simple("${body[_dados][protocolo]}"))
				.log(LoggingLevel.INFO, LOGGER, "update-status-sucesso-remessa-baixa: ${header.protocolo}")
				.multicast()
					.to("sql:UPDATE statusboleto SET situacao = :#${property.situacaoId}, "
							+ "descricao = :#${property.situacao}, "
							+ "protocolo = :#${header[protocolo]} "
							+ "WHERE id_integracao IN ( :#in:idsIntegracao )")
			.endChoice()
		.end();
	}

	private ProcessorDefinition<?> solcitaRemessaBaixaWS() {
		return reqGetJson("ws-solicita-remessa-baixa", "boletos/baixa/lote");
	}

}

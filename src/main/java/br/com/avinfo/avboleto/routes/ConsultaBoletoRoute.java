package br.com.avinfo.avboleto.routes;

import java.util.Map;

import org.apache.camel.model.MulticastDefinition;
import org.apache.camel.model.ProcessorDefinition;

import br.com.avinfo.avboleto.base.BoletoBaseRouteBuilder;
import br.com.avinfo.avboleto.dto.EhBoletoSituacao;

public class ConsultaBoletoRoute extends BoletoBaseRouteBuilder {

	@Override
	public void configure() throws Exception {
		
		from("direct:consulta-boleto")
			.routeId("consulta-boleto")
			.to("direct:ws-consulta-boleto");
		
		wsConsultaBoleto();
		updateStatusBoletoConsultados();
		
	}

	private MulticastDefinition wsConsultaBoleto() {
		return reqGetJson("ws-consulta-protocolo-retorno", "retornos/${body[param1]}")
				.multicast()
					.to("direct:update-status-boleto-consultados")
					.to("direct:send-comando-retorno");
	}
	
	@SuppressWarnings("unchecked")
	private ProcessorDefinition<?> updateStatusBoletoConsultados() {
		return from("direct:update-status-boleto-consultados")
				.routeId("update-status-boleto-consultados")
				.choice()
					.when(simple("${body[sucesso]} && ${body[_dados].size()} > 0"))
						.split(simple("${body[_dados]}"))
							.setProperty("situacao", simple("${body[situacao]}"))
							.process(ex -> {
								
								Map<String, Object> body = ex.getIn().getBody(Map.class);
								String situacao = body.get("situacao").toString();
								
								Map<String, Object> properties = ex.getProperties();
								properties.put("situacaoId", EhBoletoSituacao.situacao(situacao));
								
							})
							.to("sql:UPDATE statusboleto SET situacao = :#${property.situacaoId}, descricao = :#${property.situacao} where id_integracao = :#${body[IdIntegracao]}")
						.end()
				.endChoice()
				.end();
							
	}

}

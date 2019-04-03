package br.com.avinfo.avboleto.routes.api;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpMethods;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import br.com.avinfo.avboleto.dto.SolicitaPDFDTO;

@Component
public class BoletoApiRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		from("direct:api-incluir-boleto")
			.routeId("api-incluir-boleto")
			.choice()
				.when(simple("${body} != null && ${body.size()} > 0"))
					.marshal().json(JsonLibrary.Jackson)
					.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
					.setHeader("cnpj-sh", simple("{{tecnosped.boleto.api.cnpjsh}}"))
					.setHeader("token-sh", simple("{{tecnosped.boleto.api.tokensh}}"))
					.setHeader("cnpj-cedente", constant("01001001000113")) //TODO pegar do banco de dados
					.setHeader(Exchange.HTTP_METHOD, HttpMethods.POST)
					.log("requisiçao-ws: ${body}")
					.to("http4://{{tecnosped.boleto.api.host}}/v{{tecnosped.boleto.api.version}}/boletos/lote?throwExceptionOnFailure=false")
					.unmarshal()
					.json(JsonLibrary.Jackson)
					.log("Retorno-ws: ${body}")
					.to("direct:db-update-status-boleto-incluir-boleto-sucesso")
					.to("direct:db-update-status-boleto-incluir-boleto-falha")
					.to("direct:db-update-status-boleto-incluir-boletos-erro")
				.endChoice()
		.end();
		
		
		from("direct:api-solicita-boletos-pdf")
			.routeId("api-solicita-boletos-pdf")
			.choice()
				.when(simple("${body} != null"))
					.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
					.setHeader("cnpj-sh", simple("{{tecnosped.boleto.api.cnpjsh}}"))
					.setHeader("token-sh", simple("{{tecnosped.boleto.api.tokensh}}"))
					.setHeader("cnpj-cedente", constant("01001001000113")) //TODO pegar do banco de dados 
					.setHeader(Exchange.HTTP_METHOD, HttpMethods.POST)
					.process(this::solicitaBoletoProcess)
					.marshal().json(JsonLibrary.Jackson)
					.log("requisiçao-ws: ${body}")
					.to("http4://{{tecnosped.boleto.api.host}}/v{{tecnosped.boleto.api.version}}/boletos/impressao/lote?throwExceptionOnFailure=false")
					.unmarshal().json(JsonLibrary.Jackson)
					.to("direct:db-insert-boleto-protocolo")
					.to("direct:db-update-protocolo-boletostatus")
					.to("direct:db-update-protocolo-boleto-erro")
		.end();
		
		from("direct:api-consulta-protocolo-pdf")
			.routeId("api-consulta-protocolo-pdf")
			.choice()
				.when().simple("${body} != null && ${body} != '' && ${property.protocolo} != null")
					.setHeader("cnpj-sh", simple("{{tecnosped.boleto.api.cnpjsh}}"))
					.setHeader("token-sh", simple("{{tecnosped.boleto.api.tokensh}}"))
					.setHeader("cnpj-cedente", constant("01001001000113"))
					.setHeader(Exchange.HTTP_METHOD, HttpMethods.GET)
					.setBody(simple("null"))
					.recipientList(simple("http4://{{tecnosped.boleto.api.host}}/v{{tecnosped.boleto.api.version}}/boletos/impressao/lote/${property.protocolo}?throwExceptionOnFailure=false"))
					.convertBodyTo(byte[].class)
					.to("direct:protocolo-pdf-timer")
		.end();
		
		
			
		from("direct:protocolo-pdf-timer")	
			.routeId("protocolo-pdf-timer")
			.choice()
				.when(simple("${body} not is 'java.lang.byte[]'"))
					.to("direct:api-consulta-protocolo-pdf")
				.otherwise()
					.to("direct:update-boleto-protocolo-pdf")
		.end();
				
		
	}
	
	private void solicitaBoletoProcess(Exchange exchange) {
		SolicitaPDFDTO dto = new SolicitaPDFDTO();
		dto.setTipoImpressao("1");
		
		String idsIntegracao = exchange.getIn().getBody(String.class);
		for (String idIntegracao : idsIntegracao.split(",")) {
			dto.addBoleto(idIntegracao);
		}
		
		exchange.getIn().setBody(dto);
		
	}

}

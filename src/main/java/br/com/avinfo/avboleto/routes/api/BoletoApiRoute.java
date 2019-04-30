package br.com.avinfo.avboleto.routes.api;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpMethods;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.com.avinfo.avboleto.dto.SolicitaPDFDTO;

@Component
public class BoletoApiRoute extends RouteBuilder {
	
	@Value(value="${habilitar.https:false}")
	private Boolean habilitarHttps;

	@Override
	public void configure() throws Exception {

		mainIncluirBoleto();
		
		from("direct:api-solicita-boletos-pdf")
			.routeId("api-solicita-boletos-pdf")
			.choice()
				.when(simple("${body} != null"))
					.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
					.setHeader("cnpj-sh", simple("{{tecnosped.boleto.api.cnpjsh}}"))
					.setHeader("token-sh", simple("{{tecnosped.boleto.api.tokensh}}"))
					.setHeader("cnpj-cedente", method("avHeader", "getCNPJCedente"))
					.setHeader(Exchange.HTTP_METHOD, HttpMethods.POST)
					.process(this::solicitaBoletoProcess)
					.marshal().json(JsonLibrary.Jackson)
					.log("[api-solicita-boletos-pdf]-requisicao-url: {{tecnosped.boleto.api.host}}/v{{tecnosped.boleto.api.version}}/boletos/impressao/lote")
					.log("[api-solicita-boletos-pdf]-requisicao-ws-body: ${body}")
					.toF("%s://{{tecnosped.boleto.api.host}}/v{{tecnosped.boleto.api.version}}/boletos/impressao/lote?throwExceptionOnFailure=false", protocol())
					.unmarshal().json(JsonLibrary.Jackson)
					.log("[api-solicita-boletos-pdf]-retorno-ws-body: ${body}")
					.to("direct:db-insert-boleto-protocolo")
					.to("direct:db-update-protocolo-boletostatus")
					.to("direct:db-update-protocolo-boleto-erro")
//				.otherwise()
//					// tenta inserir novamente após os boletos serem inseridos pela primeira vez
//					.log("Iniciando o ciclo novamente, boleto-ids: (${header.IdsFiltrados}) provavelmente estão com status SALVO")
//					.delay(5000)
//					.to("direct:incluir-boleto")
		.end();
		
		from("direct:api-consulta-protocolo-pdf")
			.routeId("api-consulta-protocolo-pdf")
			.choice()
				.when().simple("${body} != null && ${body} != '' && ${property.protocolo} != null")
					.setHeader("cnpj-sh", simple("{{tecnosped.boleto.api.cnpjsh}}"))
					.setHeader("token-sh", simple("{{tecnosped.boleto.api.tokensh}}"))
					.setHeader("cnpj-cedente", method("avHeader", "getCNPJCedente"))	
					.setHeader(Exchange.HTTP_METHOD, HttpMethods.GET)
					.setBody(simple("null"))
					.log("[api-solicita-boletos-pdf]-requisicao-url: {{tecnosped.boleto.api.host}}/v{{tecnosped.boleto.api.version}}/boletos/impressao/lote")
					.recipientList(simpleF("%s://{{tecnosped.boleto.api.host}}/v{{tecnosped.boleto.api.version}}/boletos/impressao/lote/${property.protocolo}?throwExceptionOnFailure=false", protocol()))
					.convertBodyTo(byte[].class)
					.to("direct:protocolo-pdf-timer")
					.endChoice()
				.when().simple("${body} != null && ${body} != '' && ${property.protocolo} == null")  // se protocolo null
					// tenta inserir novamente após os boletos serem inseridos pela primeira vez
					.log("Iniciando o ciclo novamente, boleto-ids: (${header.ids_filtrados}) provavelmente estão com status SALVO")
					.delay(5000)
					.to("direct:incluir-boleto")
				.endChoice()
			.end()
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

	private void mainIncluirBoleto() {
		from("direct:api-incluir-boleto")
			.routeId("api-incluir-boleto")
			.choice()
				.when(simple("${body} != null && ${body.size()} > 0"))
					.marshal().json(JsonLibrary.Jackson)
					.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
					.setHeader("cnpj-sh", simple("{{tecnosped.boleto.api.cnpjsh}}"))
					.setHeader("token-sh", simple("{{tecnosped.boleto.api.tokensh}}"))
					.setHeader("cnpj-cedente", method("avHeader", "getCNPJCedente"))
					.setHeader(Exchange.HTTP_METHOD, HttpMethods.POST)
					.log("[api-incluir-boleto]-requisicao-url: {{tecnosped.boleto.api.host}}/v{{tecnosped.boleto.api.version}}/boletos/lote")
					.log("[api-incluir-boleto]-requisicao-ws-body: ${body}")
					.toF("%s://{{tecnosped.boleto.api.host}}/v{{tecnosped.boleto.api.version}}/boletos/lote?throwExceptionOnFailure=false", protocol())
					.unmarshal()
					.json(JsonLibrary.Jackson)
					.log("[api-incluir-boleto]-retorno-ws-body: ${body}")
					.to("direct:db-update-status-boleto-incluir-boleto-sucesso")
					.to("direct:db-update-status-boleto-incluir-boleto-falha")
					.to("direct:db-update-status-boleto-incluir-boletos-erro")
				.endChoice()
		.end();
		
	}
	
	private String protocol() {
		return habilitarHttps ? "https4" : "http4";
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

package br.com.avinfo.avboleto.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CadastroBoletoRoute extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		
		from("direct:incluir-boleto")
			.routeId("incluir-boleto-main")
			.setProperty("comando_ids", simple("${body[id]}"))
			.to("direct:db-consulta-boletos-status-pendente")
			.to("direct:api-incluir-boleto")
			.to("direct:db-consulta-boletos-status-emitidos-by-ids-filtrados")
			.to("direct:api-solicita-boletos-pdf")
			.to("direct:api-consulta-protocolo-pdf")
			.setHeader("comando_ids", simple("${property.comando_ids}"))
			.to("direct:db-update-comando-status")
		.end();
			
	}
	
}

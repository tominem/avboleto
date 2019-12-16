package br.com.avinfo.avboleto.routes;

import org.springframework.stereotype.Component;

import br.com.avinfo.avboleto.base.BoletoBaseRouteBuilder;

@Component
public class ConsultaBoletoProtocoloRoute extends BoletoBaseRouteBuilder {

	@Override
	public void configure() throws Exception {
		
		from("direct:consulta-boleto-protocolo")
			.routeId("consulta-boleto-protocolo")
			.setHeader("protocolo", simple("${body[param1]}"))
			.setBody(simple("null"))
			.to("direct:ws-consulta-protocolo-retorno");
			
	}

}

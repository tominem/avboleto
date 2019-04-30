package br.com.avinfo.avboleto.routes;

import java.util.ArrayList;

import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import br.com.avinfo.avboleto.base.HttpBaseRouteBuilder;

@Component
public class DescartarBoletoRoute extends HttpBaseRouteBuilder {

	@Override
	public void configure() throws Exception {
		
		from("direct:descartar-boleto")
			.routeId("descartar-boleto")
			.setHeader("param1", simple("${body[param1]}"))
			.setHeader("list", constant(new ArrayList<String>()))
			.split(header("param1").tokenize(";"))
				.script().simple("${header.list.add(${body})}")
			.end()
			.setBody(header("list"))
			.marshal()
			.json(JsonLibrary.Jackson)
			.to("direct:ws-descartar-boleto");
		
		descartarBoletoWS()
			.to("direct:send-comando-retorno");
		
	}
	
	private RouteDefinition descartarBoletoWS() {
		return reqPostJson("ws-descartar-boleto", "boletos/descarta/lote");
	}

}

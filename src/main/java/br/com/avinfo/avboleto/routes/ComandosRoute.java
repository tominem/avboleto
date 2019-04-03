package br.com.avinfo.avboleto.routes;

import static br.com.avinfo.avboleto.sql.Queries.FIND_COMMANDOS;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ComandosRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		from("direct:look-up-comandos")
			.routeId("look-up-comandos")
			.to("sql:" + FIND_COMMANDOS + "?dataSourceRef=dataSource&outputType=StreamList")
			.split(body()).streaming()
				.log("COMANDO: ${body[comando]}")
				.recipientList(simple("direct:${body[comando]}"))
		.end();

		
	}

}

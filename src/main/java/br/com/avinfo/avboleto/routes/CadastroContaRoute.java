package br.com.avinfo.avboleto.routes;

import static br.com.avinfo.avboleto.sql.Queries.FIND_CONTA_BY_ID;

import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import br.com.avinfo.avboleto.base.HttpBaseRouteBuilder;

@Component
public class CadastroContaRoute extends HttpBaseRouteBuilder {

	@Override
	public void configure() throws Exception {
		
		from("direct:cadastrar-conta")
			.routeId("cadastrar-conta")
			.setHeader("conta-id", simple("${body[param1]}"))
			.to("sql:" + FIND_CONTA_BY_ID + "?dataSourceRef=dataSource")
			.setBody(simple("${body[0]}"))
			.process("fromDatabaseToMap")
			.marshal()
			.json(JsonLibrary.Jackson)
			.to("direct:ws-cadastrar-conta");
		
		updateContaIdIntegracao();
		
		cadastroContaWS()
			.multicast()
				.to("direct:update-conta-idIntegracao")
				.to("direct:send-comando-retorno");
		
	}
	
	private RouteDefinition cadastroContaWS() {
		return reqPostJson("ws-cadastrar-conta", "cedentes/contas");
	}

	private void updateContaIdIntegracao() {
		from("direct:update-conta-idIntegracao")
			.routeId("update-conta-idIntegracao")
			.choice()
				.when(simple("${body[_status]} == 'sucesso'"))
					.to("sql:UPDATE conta30i SET IdIntegracao = :#${body[_dados][id]} WHERE sr_recno = :#${header.conta-id}")
			.end()
		.end();
	}

}

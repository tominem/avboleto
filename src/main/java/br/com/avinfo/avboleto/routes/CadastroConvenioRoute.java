package br.com.avinfo.avboleto.routes;

import static br.com.avinfo.avboleto.sql.Queries.FIND_CONVENIO;

import org.springframework.stereotype.Component;

import br.com.avinfo.avboleto.base.HttpBaseRouteBuilder;

@Component
public class CadastroConvenioRoute extends HttpBaseRouteBuilder {

	@Override
	public void configure() throws Exception {
		
		from("direct:cadastrar-convenio")
			.routeId("cadastrar-convenio")
			.to("sql:" + FIND_CONVENIO + "?dataSourceRef=dataSource")
			.setBody(simple("${body[0]}"))
			.process("fromDatabaseToMap")
			.marshal("myJsonFormat")
			.to("direct:ws-cadastro-convenio");
		
		cadastroConvenioWS();
		
	}

	private void cadastroConvenioWS() {
		
		reqPostJson("ws-cadastro-convenio", "cedentes/contas/convenios")
			.to("direct:send-comando-retorno");
		
	}

}

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
			.setHeader("convenio-id", simple("${body[param1]}"))
			.to("sql:" + FIND_CONVENIO + "?dataSourceRef=dataSource")
			.setBody(simple("${body[0]}"))
			.process("fromDatabaseToMap")
			.marshal("myJsonFormat")
			.to("direct:ws-cadastrar-convenio");
		
		from("direct:alterar-convenio")
			.routeId("alterar-convenio")
			.setHeader("convenio-id", simple("${body[param1]}"))
			.to("sql:" + FIND_CONVENIO + "?dataSourceRef=dataSource")
			.setBody(simple("${body[0]}"))
			.process("fromDatabaseToMap")
			.setHeader("IdIntegracao", simple("${body[IdIntegracao]}"))
			.marshal("myJsonFormat")
			.to("direct:ws-alterar-convenio");
		
		cadastrarConvenioWS();
		alterarConvenioWS();
		
		updateConvenioIdIntegracao();
		
	}

	private void alterarConvenioWS() {
		reqPutJson("ws-alterar-convenio", "cedentes/contas/convenios/${header.IdIntegracao}")
			.multicast()
				.to("direct:update-convenio-idIntegracao")
				.to("direct:send-comando-retorno");
	}

	private void cadastrarConvenioWS() {
		reqPostJson("ws-cadastrar-convenio", "cedentes/contas/convenios")
			.multicast()
				.to("direct:update-convenio-idIntegracao")
				.to("direct:send-comando-retorno");
	}
	
	private void updateConvenioIdIntegracao() {
		from("direct:update-convenio-idIntegracao")
			.routeId("update-convenio-idIntegracao")
			.choice()
				.when(simple("${body[_status]} == 'sucesso'"))
					.to("sql:UPDATE convenio SET IdIntegracao = :#${body[_dados][id]} WHERE Id = :#${header.convenio-id}")
			.end()
		.end();
	}

}

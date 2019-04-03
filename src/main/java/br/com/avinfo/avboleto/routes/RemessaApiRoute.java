package br.com.avinfo.avboleto.routes;

import static br.com.avinfo.avboleto.sql.Queries.FIND_IDS_INTEGRACAO_BOLETOS_PARA_GERAR_REMESSA;
import static br.com.avinfo.avboleto.sql.Queries.UPDATE_STATUS_BOLETO_REMESSA;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpMethods;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import br.com.avinfo.avboleto.sql.Queries;

@Component
public class RemessaApiRoute extends RouteBuilder {

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void configure() throws Exception {
		
		from("direct:gerar-remessa")
			.routeId("gerar-remessa")
			.setHeader("comando_ids", simple("${body[id]}")) // pega id do comando
			.process(ex -> {
				
				Map map = ex.getIn().getBody(Map.class);
				if(map != null) {
					ex.getIn().setHeader("nossos_numeros", map.get("param1").toString());
				}
				
			})
			.to("sql:" + FIND_IDS_INTEGRACAO_BOLETOS_PARA_GERAR_REMESSA + "?dataSourceRef=dataSource")
			.process(ex -> {
				List<String> ids = new ArrayList<>();
				
				List<Map<?,?>> results = ex.getIn().getBody(List.class);
				if (results != null && results.size() > 0) {
					for (Map<?, ?> map : results) {
						String idIntegracao = map.get("id_integracao").toString();
						ids.add(idIntegracao);
					}
				}
				
				ex.getIn().setBody(ids.isEmpty() ? null : ids);
				
			})
			.to("direct:api-gerar-remessa")
			//.to("direct:db-update-comando-status")
		.end();
		
		
		from("direct:api-gerar-remessa")
			.routeId("api-gerar-remessa")
			.choice()
				.when(simple("${body} != null && ${body} != ''"))
					.marshal().json(JsonLibrary.Jackson)
					.log("AFTER: ${body}")
					.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
					.setHeader("cnpj-sh", simple("{{tecnosped.boleto.api.cnpjsh}}"))
					.setHeader("token-sh", simple("{{tecnosped.boleto.api.tokensh}}"))
					.setHeader("cnpj-cedente", constant("01001001000113"))
					.setHeader(Exchange.HTTP_METHOD, HttpMethods.POST)
					.to("http4://{{tecnosped.boleto.api.host}}/v{{tecnosped.boleto.api.version}}/remessas/lote?throwExceptionOnFailure=false")
					.unmarshal().json(JsonLibrary.Jackson)
					.log("RESULT WS: ${body}")
					.to("direct:db-insert-remessa-sucesso")
//					.to("direct:db-insert-remessa-falha")
		.end();
		
		
		from("direct:db-insert-remessa-sucesso")
			.routeId("db-insert-remessa-sucesso")
			.choice()
				.when(simple("${body[_status]} == 'sucesso'"))
					.split().simple("${body[_dados][_sucesso]}")
						.to("sql:" + Queries.INSERT_REMESSA + "?dataSourceRef=dataSource")
						.setHeader("ids_integracao", simple("${body[titulos]}"))
						.process(ex -> {
							
							String ids = "";
							
							Map map = ex.getIn().getBody(Map.class);
							if (map != null) {
								List<Map<?,?>> results = (List<Map<?, ?>>) map.get("titulos");
								if (results != null && results.size() > 0) {
									for (Map<?, ?> map2 : results) {
										String idIntegracao = map2.get("idintegracao").toString();
										ids += ids.isEmpty() ? idIntegracao : ("," + idIntegracao);
									}
								}
								
								ex.getIn().setHeader("ids_integracao", ids.isEmpty() ? null : ids);
							}
							
						})
						.to("sql:" + UPDATE_STATUS_BOLETO_REMESSA + "?dataSourceRef=dataSource")
		.end();
		
	}

}

package br.com.avinfo.avboleto.routes;

import java.util.ArrayList;

import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import br.com.avinfo.avboleto.base.HttpBaseRouteBuilder;
import br.com.avinfo.avboleto.dto.EhBoletoSituacao;

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
			.multicast()
				.to("direct:update-decartar-sucesso")
				.to("direct:update-decartar-falha")
				.to("direct:send-comando-retorno");
		
		updateDescartarSucesso();
		updateDescartarFalha();
		
	}
	
	private void updateDescartarSucesso() {
		from("direct:update-decartar-sucesso")
			.routeId("update-decartar-sucesso")
			.choice()
				.when(simple("${body[_dados][_sucesso].size()} > 0"))
					.setHeader("situacao", constant(EhBoletoSituacao.DESCARTADO.getSituacaoId()))
					.setHeader("descricao", constant(EhBoletoSituacao.DESCARTADO.name()))
					.setHeader("sucessos", simple("${body[_dados][_sucesso]}"))
					.split(header("sucessos")).streaming()
						.setHeader("mensagem", simple("Descartado com sucesso: ${body}", String.class))
						.setHeader("idIntegracao", simple("${body[idintegracao]}"))
						.to("sql:UPDATE statusboleto SET situacao = :#${header.situacao}, "
								+ "Descricao = :#${header.descricao}, mensagem = :#${header.mensagem} "
								+ "WHERE id_integracao = :#${header.idIntegracao}")
		.end();
	}

	private void updateDescartarFalha() {
		from("direct:update-decartar-falha")
		.routeId("update-decartar-falha")
		.choice()
			.when(simple("${body[_dados][_falha].size()} > 0"))
				.setHeader("situacao", constant(EhBoletoSituacao.FALHA.getSituacaoId()))
				.setHeader("descricao", constant(EhBoletoSituacao.FALHA.name()))
				.setHeader("falhas", simple("${body[_dados][_falha]}"))
				.split(header("falhas")).streaming()
					.setHeader("mensagem", simple("${body}", String.class))
					.setHeader("idIntegracao", simple("${body[idintegracao]}"))
					.to("sql:UPDATE statusboleto SET situacao = :#${header.situacao}, "
							+ "Descricao = :#${header.descricao}, mensagem = :#${header.mensagem} "
							+ "WHERE id_integracao = :#${header.idIntegracao}")
		.end();
	}

	private RouteDefinition descartarBoletoWS() {
		return reqPostJson("ws-descartar-boleto", "boletos/descarta/lote");
	}

}

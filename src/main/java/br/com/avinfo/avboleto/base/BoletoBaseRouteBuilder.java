package br.com.avinfo.avboleto.base;

import org.apache.camel.model.ExpressionNode;

import br.com.avinfo.avboleto.dto.EhBoletoSituacao;

public abstract class BoletoBaseRouteBuilder extends HttpBaseRouteBuilder{

	public ExpressionNode updateStatusBoletoSucesso(String route, EhBoletoSituacao boletoSituacao) {
		return
			fromF("direct:%s", route)
			.routeId(route)
			.choice()
				.when(simple("${body[_status]} == 'sucesso' && ${body[_dados][_sucesso].size()} > 0"))
					.setHeader("situacao", constant(boletoSituacao.getSituacaoId()))
					.setHeader("descricao", constant(boletoSituacao.name()))
					.setHeader("sucessos", simple("${body[_dados][_sucesso]}"))
					.split(header("sucessos")).streaming()
						.setHeader("mensagem", simple("Descartado com sucesso: ${body}", String.class))
						.setHeader("idIntegracao", simple("${body[idintegracao]}"))
						.to("sql:UPDATE statusboleto SET situacao = :#${header.situacao}, "
								+ "Descricao = :#${header.descricao}, mensagem = :#${header.mensagem} "
								+ "WHERE id_integracao = :#${header.idIntegracao}");
	}

	public ExpressionNode updateStatusBoletoFalha(String route) {
		return 
			fromF("direct:%s", route)
			.routeId(route)
			.choice()
				.when(simple("${body[_status]} == 'sucesso' && ${body[_dados][_falha].size()} > 0"))
					.setHeader("situacao", constant(EhBoletoSituacao.FALHA.getSituacaoId()))
					.setHeader("descricao", constant(EhBoletoSituacao.FALHA.name()))
					.setHeader("falhas", simple("${body[_dados][_falha]}"))
					.split(header("falhas")).streaming()
						.setHeader("mensagem", simple("${body}", String.class))
						.setHeader("idIntegracao", simple("${body[idintegracao]}"))
						.to("sql:UPDATE statusboleto SET situacao = :#${header.situacao}, "
								+ "Descricao = :#${header.descricao}, mensagem = :#${header.mensagem} "
								+ "WHERE id_integracao = :#${header.idIntegracao}");
	}

}

package br.com.avinfo.avboleto.base;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.component.http4.HttpMethods;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Value;

public abstract class HttpBaseRouteBuilder extends BaseRouteBuilder {
	
	@Value(value="${habilitar.https:false}")
	protected Boolean habilitarHttps;
	
	public RouteDefinition reqPostJson(String from, String resource) {
		return doRequest(from, "application/json", HttpMethods.POST, resource);
	}

	private RouteDefinition doRequest(String from, String contentType, Expression method, String resource) {
		return fromF("direct:%s", from)
				.routeId(from)
				.setHeader(Exchange.CONTENT_TYPE, constant(contentType))
				.setHeader("cnpj-sh", simple("{{tecnosped.boleto.api.cnpjsh}}"))
				.setHeader("token-sh", simple("{{tecnosped.boleto.api.tokensh}}"))
				.setHeader("cnpj-cedente", method("avHeader", "getCNPJCedente"))
				.setHeader(Exchange.HTTP_METHOD, method)
				.log("["+ from +"]-requisicao-url: {{tecnosped.boleto.api.host}}/v{{tecnosped.boleto.api.version}}/" + resource)
				.log("["+ from +"]-requisicao-ws-body: ${body}")
				.toF("%s://{{tecnosped.boleto.api.host}}/v{{tecnosped.boleto.api.version}}/%s?httpClient.socketTimeout=5000&throwExceptionOnFailure=false",
						protocol(), resource)
				.unmarshal()
				.json(JsonLibrary.Jackson)
				.log("["+ from +"]-retorno-ws-body: ${body}");
	}
	
	private String protocol() {
		return habilitarHttps ? "https4" : "http4";
	}
	
}

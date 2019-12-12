package br.com.avinfo.avboleto.base;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.LoggingLevel;
import org.apache.camel.component.http4.HttpMethods;
import org.apache.camel.model.ExpressionNode;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public abstract class HttpBaseRouteBuilder extends BaseRouteBuilder {
	
	public static Logger LOGGER = LoggerFactory.getLogger(HttpBaseRouteBuilder.class);
	
	@Value(value="${habilitar.https:false}")
	protected Boolean habilitarHttps;
	
	public RouteDefinition reqPostJson(String from, String resource) {
		return doRequest(from, "application/json", HttpMethods.POST, resource);
	}

	public ExpressionNode reqGetJson(String from, String resource) {
		return doRequestRecipientList(from, null, HttpMethods.GET, resource);
	}

	public ExpressionNode reqPutJson(String from, String resource) {
		return doRequestRecipientList(from, "application/json", HttpMethods.PUT, resource);
	}

	private RouteDefinition doRequest(String from, String contentType, Expression method, String resource) {
		RouteDefinition routeDefinition = fromF("direct:%s", from)
			.routeId(from);
				
		if (contentType != null) {
			routeDefinition.setHeader(Exchange.CONTENT_TYPE, constant(contentType));
		}
			
		return routeDefinition
				.setHeader("cnpj-sh", simple("{{tecnosped.boleto.api.cnpjsh}}"))
				.setHeader("token-sh", simple("{{tecnosped.boleto.api.tokensh}}"))
				.setHeader("cnpj-cedente", method("avHeader", "getCNPJCedente"))
				.setHeader(Exchange.HTTP_METHOD, method)
				.log(LoggingLevel.DEBUG, LOGGER, "["+ from +"]-headers: ${headers}")
				.log(LoggingLevel.INFO, LOGGER, "["+ from +"]-requisicao-url: "+ protocol() +"://{{tecnosped.boleto.api.host}}/v{{tecnosped.boleto.api.version}}/" + resource)
				.log(LoggingLevel.INFO, LOGGER, "["+ from +"]-requisicao-ws-body: ${body}")
				.toF("%s://{{tecnosped.boleto.api.host}}/v{{tecnosped.boleto.api.version}}/%s?httpClient.socketTimeout=5000&throwExceptionOnFailure=false",
						protocolHttp4(), resource)
				.unmarshal()
				.json(JsonLibrary.Jackson)
				.log(LoggingLevel.INFO, LOGGER, "["+ from +"]-retorno-ws-body: ${body}");
	}

	private ExpressionNode doRequestRecipientList(String from, String contentType, Expression method, String resource) {
		RouteDefinition routeDefinition = fromF("direct:%s", from)
				.routeId(from);
		
		if (contentType != null) {
			routeDefinition.setHeader(Exchange.CONTENT_TYPE, constant(contentType));
		}
		
		return routeDefinition
				.setHeader(Exchange.CONTENT_TYPE, constant(contentType))
				.setHeader("cnpj-sh", simple("{{tecnosped.boleto.api.cnpjsh}}"))
				.setHeader("token-sh", simple("{{tecnosped.boleto.api.tokensh}}"))
				.setHeader("cnpj-cedente", method("avHeader", "getCNPJCedente"))
				.setHeader(Exchange.HTTP_METHOD, method)
				.log(LoggingLevel.DEBUG, LOGGER, "["+ from +"]-headers: ${headers}")
				.log(LoggingLevel.INFO, LOGGER, "["+ from +"]-requisicao-url: "+ protocol() +"://{{tecnosped.boleto.api.host}}/v{{tecnosped.boleto.api.version}}/" + resource)
				.log(LoggingLevel.INFO, LOGGER, "["+ from +"]-requisicao-ws-body: ${body}")
				.recipientList(simple(String.format("%s://{{tecnosped.boleto.api.host}}/v{{tecnosped.boleto.api.version}}/%s?httpClient.socketTimeout=5000&throwExceptionOnFailure=false",
						protocolHttp4(), resource)))
				.unmarshal()
				.json(JsonLibrary.Jackson)
				.log(LoggingLevel.INFO, LOGGER, "["+ from +"]-retorno-ws-body: ${body}");
	}
	
	private String protocolHttp4() {
		return habilitarHttps ? "https4" : "http4";
	}

	private String protocol() {
		return habilitarHttps ? "https" : "http";
	}
	
}

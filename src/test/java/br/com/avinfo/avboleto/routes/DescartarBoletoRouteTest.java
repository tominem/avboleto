package br.com.avinfo.avboleto.routes;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.DisableJmx;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@DisableJmx(true)
@ActiveProfiles(value="test")
public class DescartarBoletoRouteTest {
	
	@Autowired
	private CamelContext camelContext;
	
	@EndpointInject(uri = "direct:look-up-comandos")
	private ProducerTemplate lookUpComandos;

	@EndpointInject(uri = "direct:insere-boleto-comando-db")
	private ProducerTemplate insereBoletoComando;

	@EndpointInject(uri = "direct:find-last-ids-integracao")
	private ProducerTemplate findLastTwoIdsIntegracao;
	
	@EndpointInject(uri = "mock:dead")
	private MockEndpoint mock;

	@Before
	public void setUp() throws Exception {
		camelContext.addRoutes(new RouteBuilder() {
			
			@Override
			public void configure() throws Exception {
				from("direct:find-last-ids-integracao")
				.routeId("find-last-ids-integracao")
				.to("sql:SELECT id_integracao FROM statusboleto limit 2")
				.split(simple("${body}"), new AggregationStrategy() {
					
					StringBuffer sb;
					
					@Override
					public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
						if (oldExchange == null) {
							sb = new StringBuffer();
							sb.append(newExchange.getIn().getBody(Map.class).get("id_integracao"));
							newExchange.getIn().setBody(sb);
							return newExchange;
						} else {
							sb = oldExchange.getIn().getBody(StringBuffer.class);
							sb.append(";").append(newExchange.getIn().getBody(Map.class).get("id_integracao"));
							return oldExchange;
						}
					}
				}).log("${body}");
				
			}
		});
		
		RouteDefinition definition = camelContext.getRouteDefinitions().get(0);
		definition.adviceWith(camelContext, new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				onException(Exception.class).maximumRedeliveries(0);
			}
		});
		
	}

	@Test
	public void shouldSucceed() throws Exception {
		mock.expectedMessageCount(1);
		
		StringBuffer idsIntegracao = (StringBuffer) findLastTwoIdsIntegracao.requestBody("");
		
		Map<String, Object> params = new HashMap<>();
		params.put("comando", "descartar-boleto");
		params.put("param1", idsIntegracao);
		params.put("status", 1);
		
		insereBoletoComando.sendBody(params);
		lookUpComandos.sendBody("");
		
		mock.assertIsSatisfied();
	}

}

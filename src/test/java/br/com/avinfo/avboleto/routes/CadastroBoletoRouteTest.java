package br.com.avinfo.avboleto.routes;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.DisableJmx;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@DisableJmx(true)
public class CadastroBoletoRouteTest {
	
	@Autowired
	private CamelContext camelContext;
	
	@EndpointInject(uri = "direct:look-up-comandos")
	private ProducerTemplate lookUpComandos;

	@EndpointInject(uri = "direct:insere-boleto-comando-db")
	private ProducerTemplate insereBoletoComando;
	
	@EndpointInject(uri = "mock:dead")
	private MockEndpoint mock;

	@Before
	public void setUp() throws Exception {
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
		Map<String, Object> params = new HashMap<>();
		params.put("comando", "incluir-boleto");
		params.put("param1", "999127");
		params.put("status", 1);
		
		insereBoletoComando.sendBody(params);
		lookUpComandos.sendBody("");
		
		mock.assertIsSatisfied();	
	}

}

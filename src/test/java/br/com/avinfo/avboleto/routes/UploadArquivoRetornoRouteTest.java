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
import org.springframework.test.context.ActiveProfiles;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@DisableJmx(true)
@ActiveProfiles("test")
public class UploadArquivoRetornoRouteTest {
	
	@Autowired
	private CamelContext camelContext;
	
	@EndpointInject(uri = "direct:insere-boleto-comando-db")
	private ProducerTemplate insereBoletoComando;
	
	@EndpointInject(uri = "direct:look-up-comandos")
	private ProducerTemplate lookUpComandos;
	
	@EndpointInject(uri = "mock:dead")
	private MockEndpoint mock;

	@Before
	public void setUp() throws Exception {
		camelContext.addRoutes(new RouteBuilder() {
			
			@Override
			public void configure() throws Exception {
				
//				from("direct:update-numero-conta-test")
//					.to("sql:UPDATE conta30i SET numcon30 = CONCAT('0', CAST(numcon30 AS UNSIGNED) + 1), codBeneficiario30 = numcon30 WHERE sr_recno = 3");
				
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
		
		Map<String, Object> contaParams = new HashMap<>();
		contaParams.put("comando", "upload-retorno");
		contaParams.put("param1", "/Users/tom/Documents/leo/arquivo_retonro/3169_00977942_20191122_C240_00.ret");
		contaParams.put("status", 1);
		insereBoletoComando.sendBody(contaParams);
		
		lookUpComandos.sendBody("");
		
		mock.assertIsSatisfied();	
	}

}

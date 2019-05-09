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

import br.com.avinfo.avboleto.dto.EhTipoImpressao;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@DisableJmx(true)
@ActiveProfiles("test")
public class RemessaApiRouteTest {
	
	@Autowired
	private CamelContext camelContext;
	
	@EndpointInject(uri = "direct:look-up-comandos")
	private ProducerTemplate lookUpComandos;

	@EndpointInject(uri = "direct:insere-status-boleto")
	private ProducerTemplate insereStatusBoleto;

	@EndpointInject(uri = "direct:delete-status-boleto")
	private ProducerTemplate deleteBoletoComando;

	@EndpointInject(uri = "direct:delete-boleto-protocolo")
	private ProducerTemplate deleteBoletoProtocolo;

	@EndpointInject(uri = "direct:update-numero-conta-test")
	private ProducerTemplate updateNumeroContaTest;
	
	@EndpointInject(uri = "direct:insere-boleto-comando-db")
	private ProducerTemplate insereBoletoComando;
	
	@EndpointInject(uri = "mock:dead")
	private MockEndpoint mock;


	@Before
	public void setUp() throws Exception {
		camelContext.addRoutes(new RouteBuilder() {
			
			@Override
			public void configure() throws Exception {
				
				from("direct:update-numero-conta-test")
					.to("sql:UPDATE conta30i SET numcon30 = CONCAT('0', CAST(numcon30 AS UNSIGNED) + 1), codBeneficiario30 = numcon30 WHERE sr_recno = 3");
				
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
		deleteBoletoComando.sendBody("");
		deleteBoletoProtocolo.sendBody("");
		updateNumeroContaTest.sendBody("");

		Map<String, Object> contaParams = new HashMap<>();
		contaParams.put("comando", "cadastrar-conta");
		contaParams.put("param1", 3);
		contaParams.put("status", 1);
		insereBoletoComando.sendBody(contaParams);
		
		Map<String, Object> convenioParams = new HashMap<>();
		convenioParams.put("comando", "cadastrar-convenio");
		convenioParams.put("param1", 1);
		convenioParams.put("status", 1);
		insereBoletoComando.sendBody(convenioParams);
		
		Map<String, Object> params1 = new HashMap<>();
		params1.put("comando", "incluir-boleto");
		params1.put("param1", "999124");
		params1.put("param2", EhTipoImpressao.PDF_NORMAL.getCodigo());
		params1.put("status", 1);
		
		Map<String, Object> params2 = new HashMap<>();
		params2.put("comando", "incluir-boleto");
		params2.put("param1", "999125");
		params2.put("param2", EhTipoImpressao.PDF_CARNE.getCodigo());
		params2.put("status", 1);
		
		Map<String, Object> gerarRemessaParam = new HashMap<>();
		gerarRemessaParam.put("comando", "gerar-remessa");
		gerarRemessaParam.put("param1", "1036,1037");
		gerarRemessaParam.put("status", 1);
		
		insereStatusBoleto.sendBody("9;10");
		insereBoletoComando.sendBody(params1);
		insereBoletoComando.sendBody(params2);
		insereBoletoComando.sendBody(gerarRemessaParam);
		lookUpComandos.sendBody("");
		
		mock.assertIsSatisfied();	
	}

}

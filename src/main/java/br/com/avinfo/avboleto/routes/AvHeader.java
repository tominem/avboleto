package br.com.avinfo.avboleto.routes;

import static br.com.avinfo.avboleto.sql.Queries.FIND_CNPJ_CONTROLE;

import java.util.List;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AvHeader extends RouteBuilder{
	
	@Autowired
	private CamelContext camel;
	
	private ProducerTemplate producerTemplate;
	
	private String cnpjCedente;

	@Override
	public void configure() throws Exception {
		from("direct:get-cnpj-controle")
			.routeId("get-cnpj-controle")
			.to("sql:" + FIND_CNPJ_CONTROLE + "?dataSourceRef=dataSource");
	}
	
	@SuppressWarnings("unchecked")
	public String getCNPJCedente() {
		try {
			if (cnpjCedente == null) {
				producerTemplate = createProducer();
				List<Map<String, Object>> requestBody = (List<Map<String, Object>>) producerTemplate.requestBody("direct:get-cnpj-controle", "");
				cnpjCedente = requestBody.get(0).get("cgc").toString().trim();
			}
			
			return cnpjCedente;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} 
	}

	private ProducerTemplate createProducer() throws Exception {
		DefaultProducerTemplate template = new DefaultProducerTemplate(camel);
		template.start();
		return template;
	}

}

package br.com.avinfo.avboleto.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class JoinProcessor implements Processor {

	private String fieldName;

	public JoinProcessor(String fieldName) {
		this.fieldName = fieldName;
	}
	
	@Override
	public void process(Exchange ex) throws Exception {
		List<String> ids = new ArrayList<>();
		
		@SuppressWarnings("unchecked")
		List<Map<?,?>> results = ex.getIn().getBody(List.class);
		if (results != null && results.size() > 0) {
			for (Map<?, ?> map : results) {
				String idIntegracao = map.get(fieldName).toString();
				ids.add(idIntegracao);
			}
		}
		
		ex.getIn().setBody(ids.isEmpty() ? null : ids);
		
	}

}

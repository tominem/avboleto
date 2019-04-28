package br.com.avinfo.avboleto.processor;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class FromDatabaseToMap implements Processor{

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void process(Exchange exchange) throws Exception {
		
		Map map = exchange.getIn().getBody(Map.class);
		Map<String, Object> newMap = new HashMap<>(map);
		map.forEach((k,v)-> {
			if (v instanceof String) {
				String trimmed = ((String) v).trim();
				
				if(trimmed.equals("true") ||  trimmed.equals("false")) {
					newMap.put(k.toString(), new Boolean(trimmed));
				} else {
					newMap.put(k.toString(), trimmed);
				}
				
			} else {
				newMap.put(k.toString(), v);
			}
		});
		
		exchange.getIn().setBody(newMap);
		
	}

}

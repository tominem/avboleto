package br.com.avinfo.avboleto.processor;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public abstract class AvinfoSqlProcessor<T> implements Processor{

	private Map<String, Object> map;

	@SuppressWarnings("unchecked")
	@Override
	public void process(Exchange exchange) throws Exception {
		setMap(exchange.getIn().getBody(Map.class));
		exchange.getIn().setBody(getRow());
	}
	
	public abstract T getRow();

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}
	
	public String trim(String key) {
		Object value = this.map.get(key);
		return value != null && !value.toString().isEmpty() ? value.toString().trim() : null;
	}

}

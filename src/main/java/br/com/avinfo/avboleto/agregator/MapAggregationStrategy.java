package br.com.avinfo.avboleto.agregator;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class MapAggregationStrategy implements AggregationStrategy {

	private StringBuffer sb;
	private String splitter = ",";
	private String key;
	
	public MapAggregationStrategy(String key) {
		this.key = key;
	}
	
	public MapAggregationStrategy(String splitter, String key) {
		this.splitter = splitter;
		this.key = key;
	}
	
	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		
		if (oldExchange == null) {
			sb = new StringBuffer();
			sb.append(newExchange.getIn().getBody(Map.class).get(key));
			newExchange.getIn().setBody(sb);
			return newExchange;
		} else {
			sb = oldExchange.getIn().getBody(StringBuffer.class);
			sb.append(splitter).append(newExchange.getIn().getBody(Map.class).get(key));
			return oldExchange;
		}
		
	}

}

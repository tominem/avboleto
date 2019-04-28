package br.com.avinfo.avboleto.dataFormat;

import org.apache.camel.component.jackson.JacksonDataFormat;
import org.springframework.stereotype.Component;

@Component
public class MyJsonFormat extends JacksonDataFormat{
	
	public MyJsonFormat() {
		super();
		setInclude("NON_NULL");
	}

}

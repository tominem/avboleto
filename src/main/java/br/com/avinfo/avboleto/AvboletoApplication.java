package br.com.avinfo.avboleto;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AvboletoApplication extends RouteBuilder{
	
	private Logger LOG = LoggerFactory.getLogger(AvboletoApplication.class);
	
	@Value(value="${periodo.varredura.seg}")
	private String periodo;

	public static void main(String[] args) {
		SpringApplication.run(AvboletoApplication.class, args);
	}

	@Override
	public void configure() throws Exception {
		
		String periodoSef = String.valueOf(Long.valueOf(periodo) * 1000);
		
		LOG.info("Inicializando o avinfo-boleto ...");

		from("timer:principal?period=" + periodoSef)
			.routeId("principal")
			.log(LoggingLevel.DEBUG, "Iniciando a varredura por comandos ...")
			.to("direct:look-up-comandos")
			.log("comando: ${body} finalizado");
	}
	
//	@Bean
//	  public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
//	    return args -> {
//
//	      System.out.println("Let's inspect the beans provided by Spring Boot:");
//
//	      String[] beanNames = ctx.getBeanDefinitionNames();
//	      Arrays.sort(beanNames);
//	      for (String beanName : beanNames) {
//	        System.out.println(beanName);
//	      }
//	    };
//	  }    

}

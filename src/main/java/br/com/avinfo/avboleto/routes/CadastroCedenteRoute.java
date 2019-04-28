package br.com.avinfo.avboleto.routes;

import static br.com.avinfo.avboleto.sql.Queries.FIND_CEDENTE;

import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import br.com.avinfo.avboleto.base.HttpBaseRouteBuilder;
import br.com.avinfo.avboleto.dto.CedenteReqDTO;

@Component
public class CadastroCedenteRoute extends HttpBaseRouteBuilder {

	@Override
	@SuppressWarnings("unchecked")
	public void configure() throws Exception {
		
		from("direct:cadastrar-cedente")
			.routeId("cadastrar-cedente")
			.to("sql:"+ FIND_CEDENTE +"?dataSourceRef=dataSource")
			.choice()
				.when(simple("${body.size()} > 0"))
					.process(new Processor() {
						
						private Map<String, Object> map;
		
						@Override
						public void process(Exchange exchange) throws Exception {
							
							List<Map<String, Object>> body = (List<Map<String, Object>>) exchange.getIn().getBody();
							Map<String, Object> val = body.get(0);
							
							setMap(val);
							
							CedenteReqDTO cedente = new CedenteReqDTO();
							cedente.setCedenteRazaoSocial        (trim("razao")); 
							cedente.setCedenteNomeFantasia       (trim("fantasia")); 
							cedente.setCedenteCPFCNPJ            (trim("cgc")); 
							cedente.setCedenteEnderecoLogradouro (trim("endereco"));
							cedente.setCedenteEnderecoNumero     (trim("numend")); 
//							cedente.setCedenteEnderecoComplemento(trim("")); 
							cedente.setCedenteEnderecoBairro     (trim("bairro"));  
							cedente.setCedenteEnderecoCEP        (trim("cep"));     
							cedente.setCedenteEnderecoCidadeIBGE (trim("codigo"));  
							cedente.setCedenteTelefone           (trim("telefone"));
							cedente.setCedenteEmail              (trim("email"));   
							
							exchange.getIn().setBody(cedente);
							
						}
						
						private void setMap(Map<String, Object> val) {
							this.map = val;
						}
		
						private String trim(String key) {
							return this.map.get(key).toString().trim();
						}
						
					})
					.log("result: ${body}")
					.marshal()
					.json(JsonLibrary.Jackson)
					.to("direct:ws-cadastro-cedente")
				.endChoice()
			.end();
		
		cadastroCedenteWS();
		
	}

	public void cadastroCedenteWS() {
		
		reqPostJson("ws-cadastro-cedente", "cedentes")
			.to("direct:send-comando-retorno");
		
	}

}

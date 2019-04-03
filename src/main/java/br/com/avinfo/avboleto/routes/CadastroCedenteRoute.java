package br.com.avinfo.avboleto.routes;

import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpMethods;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import br.com.avinfo.avboleto.dto.CedenteReqDTO;

@Component
public class CadastroCedenteRoute extends RouteBuilder {

	@Override
	@SuppressWarnings("unchecked")
	public void configure() throws Exception {
		
		from("direct:cadastro-cedente")
			.routeId("cadastro-cedente")
			.to("sql:{{find-cedente}}?dataSourceRef=dataSource")
			.choice()
				.when(simple("${body.size()} > 0 && ${body[0][ext_id]} != null"))
					.to("direct:cadastro-convenio")
					.endChoice()
				.otherwise()
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
		
	}

	public void updateCodigoClienteById() {
		from("direct:update-codigo-cedente-id")
			.to("sql:UPDATE controle SET ext_id = :#${body[_dados][id]}?dataSourceRef=dataSource");
	}

	public void cadastroCedente() {
		from("direct:ws-cadastro-cedente")
			.routeId("ws-cadastro-cedente")
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
			.setHeader("cnpj-sh", simple("{{tecnosped.boleto.api.cnpjsh}}"))
			.setHeader("token-sh", simple("{{tecnosped.boleto.api.tokensh}}"))
			.setHeader(Exchange.HTTP_METHOD, HttpMethods.POST)
			.to("http4://{{tecnosped.boleto.api.host}}/v{{tecnosped.boleto.api.version}}/cedentes?throwExceptionOnFailure=false")
			.unmarshal()
			.json(JsonLibrary.Jackson)
			.choice()
				.when(simple("${body[_status]} == 'sucesso'"))
					.log("status: ${body[_status]}, dados: ${body[_dados]}")
					.to("direct:update-codigo-cedente-id")
					.endChoice()
				.otherwise()
					.log("status: ${body[_status]}, erros: ${body[_dados]}")
					.endChoice()
			.end()
			.to("direct:cadastro-convenio");
	}

}

package br.com.avinfo.avboleto.routes;

import static br.com.avinfo.avboleto.sql.Queries.FIND_CONTA_BY_ID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import br.com.avinfo.avboleto.base.HttpBaseRouteBuilder;
import br.com.avinfo.avboleto.dto.ContaDTO;

@Component
public class CadastroContaRoute extends HttpBaseRouteBuilder {

	@Override
	public void configure() throws Exception {
		
		from("direct:cadastrar-conta")
			.routeId("cadastrar-conta")
			.setHeader("conta-id", simple("${body[param1]}"))
			.to("sql:" + FIND_CONTA_BY_ID + "?dataSourceRef=dataSource")
			.process(new Processor() {
				
				private Map<String, Object> map = new HashMap<>();

				@SuppressWarnings("unchecked")
				@Override
				public void process(Exchange exchange) throws Exception {
					
					List<Map<String, Object>> val = (List<Map<String, Object>>) exchange.getIn().getBody();
					
					ContaDTO contaDTO = null;
					
					if (val != null && val.size() > 0) {
						
						setMap(val.get(0));
						String conta = trim("ContaNumero");
						String contaDigito = conta.substring(conta.lastIndexOf("-") + 1, conta.length());
						String numeroAgencia = trim("ContaAgencia");
						String digitoAgencia = numeroAgencia.substring(numeroAgencia.lastIndexOf("-") + 1, numeroAgencia.length());
						contaDTO = new ContaDTO();
						contaDTO.setContaCodigoBanco(trim("ContaCodigoBanco"));
						contaDTO.setContaAgencia(
								numeroAgencia != null ? numeroAgencia.substring(0, numeroAgencia.lastIndexOf("-"))
										: null);
						contaDTO.setContaAgenciaDV(digitoAgencia);
						contaDTO.setContaNumero(conta != null ? conta.substring(0, conta.lastIndexOf("-")) : null);
						contaDTO.setContaNumeroDV(contaDigito);
						contaDTO.setContaTipo(trim("ContaTipo"));
						contaDTO.setContaCodigoBeneficiario(trim("ContaCodigoBeneficiario"));
					}
					
					exchange.getIn().setBody(contaDTO);
					
				}
				
				private void setMap(Map<String, Object> val) {
					this.map = val;
				}

				private String trim(String key) {
					try {
						return this.map.get(key).toString().trim();
					} catch (Exception e) {
						throw new RuntimeException("Erro ao carregar campo: "+ key +", erro: " + e.getMessage(), e);
					}
				}
				
			})
			.marshal()
			.json(JsonLibrary.Jackson)
			.to("direct:ws-cadastrar-conta");
		
		updateContaIdIntegracao();
		
		cadastroContaWS()
			.multicast()
				.to("direct:update-conta-idIntegracao")
				.to("direct:send-comando-retorno");
		
	}
	
	private RouteDefinition cadastroContaWS() {
		return reqPostJson("ws-cadastrar-conta", "cedentes/contas");
	}

	private void updateContaIdIntegracao() {
		from("direct:update-conta-idIntegracao")
			.routeId("update-conta-idIntegracao")
			.choice()
				.when(simple("${body[_status]} == 'sucesso'"))
					.to("sql:UPDATE conta30i SET IdIntegracao = :#${body[_dados][id]} WHERE sr_recno = :#${header.conta-id}")
			.end()
		.end();
	}

}

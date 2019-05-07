package br.com.avinfo.avboleto.processor;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.camel.Exchange;

import br.com.avinfo.avboleto.dto.BoletoReqDTO;

public class IncluirBoletoProcessor extends AvinfoSqlProcessor<BoletoReqDTO>{

	private String idsFiltrados = "";
	
	private SimpleDateFormat sdfInput = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat sdfOutput = new SimpleDateFormat("dd/MM/yyyy");

	
	@SuppressWarnings("unchecked")
	@Override
	public void process(Exchange exchange) throws Exception {

		idsFiltrados = "";
		
		List<?> data = exchange.getIn().getBody(List.class);
		List<BoletoReqDTO> boletos = new ArrayList<>();
		data.forEach(row -> {
			
			Map<String, Object> dataRow = (Map<String, Object>) row;
			setMap(dataRow);
			
			boletos.add(getRow());
			
		});
		
		exchange.getIn().setBody(boletos);
		
	}
		
	@Override
	public BoletoReqDTO getRow() {
		try {
			
			idsFiltrados += idsFiltrados.isEmpty() ? trim("BoletoId") : ("," + trim("BoletoId"));
			
			String tituloDataEmissao = trim("TituloDataEmissao");
			tituloDataEmissao = sdfOutput.format(sdfInput.parse(tituloDataEmissao));
			String tituloDataVencimento = trim("TituloDataVencimento");
			tituloDataVencimento = sdfOutput.format(sdfInput.parse(tituloDataVencimento));

			BoletoReqDTO boletoReq = new BoletoReqDTO();
			boletoReq.setCedenteContaNumero(trim("CedenteContaNumero"));
			boletoReq.setCedenteContaNumeroDV(trim("CedenteContaNumeroDV"));
			boletoReq.setCedenteConvenioNumero(trim("CedenteConvenioNumero"));
			boletoReq.setCedenteContaCodigoBanco(trim("CedenteContaCodigoBanco"));
			boletoReq.setSacadoCPFCNPJ(trim("SacadoCPFCNPJ"));
			boletoReq.setSacadoEmail(trim("SacadoEmail"));
			boletoReq.setSacadoEnderecoNumero(trim("SacadoEnderecoNumero"));
			boletoReq.setSacadoEnderecoBairro(trim("SacadoEnderecoBairro"));
			boletoReq.setSacadoEnderecoCEP(trim("SacadoEnderecoCEP"));
			boletoReq.setSacadoEnderecoCidade("SacadoEnderecoCidade");
			boletoReq.setSacadoEnderecoComplemento(trim("SacadoEnderecoComplemento"));
			boletoReq.setSacadoEnderecoLogradouro(trim("SacadoEnderecoLogradouro"));
			boletoReq.setSacadoEnderecoPais(trim("SacadoEnderecoPais"));
			boletoReq.setSacadoEnderecoUF(trim("SacadoEnderecoUF"));
			boletoReq.setSacadoNome(trim("SacadoNome"));
			boletoReq.setSacadoTelefone(trim("SacadoTelefone"));
			boletoReq.setSacadoCelular(trim("SacadoCelular"));
			boletoReq.setTituloDataEmissao(tituloDataEmissao);
			boletoReq.setTituloDataVencimento(tituloDataVencimento);
			boletoReq.setTituloMensagem01(trim("TituloMensagem01"));
			boletoReq.setTituloMensagem02(trim("TituloMensagem02"));
			boletoReq.setTituloMensagem03(trim("TituloMensagem03"));
			boletoReq.setTituloNossoNumero(trim("TituloNossoNumero"));
			boletoReq.setTituloNumeroDocumento(trim("TituloNumeroDocumento"));
			boletoReq.setTituloValor(getCurrency());
			boletoReq.setTituloLocalPagamento(trim("TituloLocalPagamento"));
			
			return boletoReq;
			
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private String getCurrency() {
		Locale.setDefault(new Locale("pt", "BR"));
		String tituloValorStr = trim("TituloValor");
		BigDecimal tituloValorBD = new BigDecimal(tituloValorStr);
		String value = new DecimalFormat("##0.00").format(tituloValorBD);
		return value;
	}
	
	public String getIdsFiltrados() {
		return idsFiltrados;
	}

	public void setIdsFiltrados(String idsFiltrados) {
		this.idsFiltrados = idsFiltrados;
	}

}

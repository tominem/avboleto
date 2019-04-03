package br.com.avinfo.avboleto.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
@JsonInclude(Include.NON_NULL)
public class SolicitaPDFDTO {
	
	private String TipoImpressao;
	
	private List<String> Boletos;

	public String getTipoImpressao() {
		return TipoImpressao;
	}

	public void setTipoImpressao(String tipoImpressao) {
		TipoImpressao = tipoImpressao;
	}

	public List<String> getBoletos() {
		return Boletos;
	}

	public void setBoletos(List<String> boletos) {
		Boletos = boletos;
	}

	public void addBoleto(String boletoIdIntegracao) {
		if (Boletos == null) {
			Boletos = new ArrayList<>();
		}
		Boletos.add(boletoIdIntegracao);
	}
	
}

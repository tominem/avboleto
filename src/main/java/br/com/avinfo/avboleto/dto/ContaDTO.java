package br.com.avinfo.avboleto.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
@JsonInclude(Include.NON_NULL)
public class ContaDTO {

	private String ContaCodigoBanco;
	private String ContaAgencia;
	private String ContaAgenciaDV;
	private String ContaNumero;
	private String ContaNumeroDV;
	private String ContaTipo;
	private String ContaCodigoBeneficiario;
	
	public ContaDTO() {}

	public String getContaCodigoBanco() {
		return ContaCodigoBanco;
	}

	public void setContaCodigoBanco(String contaCodigoBanco) {
		ContaCodigoBanco = contaCodigoBanco;
	}

	public String getContaAgencia() {
		return ContaAgencia;
	}

	public void setContaAgencia(String contaAgencia) {
		ContaAgencia = contaAgencia;
	}

	public String getContaAgenciaDV() {
		return ContaAgenciaDV;
	}

	public void setContaAgenciaDV(String contaAgenciaDV) {
		ContaAgenciaDV = contaAgenciaDV;
	}

	public String getContaNumero() {
		return ContaNumero;
	}

	public void setContaNumero(String contaNumero) {
		ContaNumero = contaNumero;
	}

	public String getContaNumeroDV() {
		return ContaNumeroDV;
	}

	public void setContaNumeroDV(String contaNumeroDV) {
		ContaNumeroDV = contaNumeroDV;
	}

	public String getContaTipo() {
		return ContaTipo;
	}

	public void setContaTipo(String contaTipo) {
		ContaTipo = contaTipo;
	}

	public String getContaCodigoBeneficiario() {
		return ContaCodigoBeneficiario;
	}

	public void setContaCodigoBeneficiario(String contaCodigoBeneficiario) {
		ContaCodigoBeneficiario = contaCodigoBeneficiario;
	}

}

package br.com.avinfo.avboleto.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
@JsonInclude(Include.NON_NULL)
public class CedenteReqDTO {

	private String CedenteRazaoSocial;
	private String CedenteNomeFantasia;
	private String CedenteCPFCNPJ;
	private String CedenteEnderecoLogradouro;
	private String CedenteEnderecoNumero;
	private String CedenteEnderecoComplemento;
	private String CedenteEnderecoBairro;
	private String CedenteEnderecoCEP;
	private String CedenteEnderecoCidadeIBGE;
	private String CedenteTelefone;
	private String CedenteEmail;

	// Getter Methods

	public String getCedenteRazaoSocial() {
		return CedenteRazaoSocial;
	}

	public String getCedenteNomeFantasia() {
		return CedenteNomeFantasia;
	}

	public String getCedenteCPFCNPJ() {
		return CedenteCPFCNPJ;
	}

	public String getCedenteEnderecoLogradouro() {
		return CedenteEnderecoLogradouro;
	}

	public String getCedenteEnderecoNumero() {
		return CedenteEnderecoNumero;
	}

	public String getCedenteEnderecoComplemento() {
		return CedenteEnderecoComplemento;
	}

	public String getCedenteEnderecoBairro() {
		return CedenteEnderecoBairro;
	}

	public String getCedenteEnderecoCEP() {
		return CedenteEnderecoCEP;
	}

	public String getCedenteEnderecoCidadeIBGE() {
		return CedenteEnderecoCidadeIBGE;
	}

	public String getCedenteTelefone() {
		return CedenteTelefone;
	}

	public String getCedenteEmail() {
		return CedenteEmail;
	}

	// Setter Methods

	public void setCedenteRazaoSocial(String CedenteRazaoSocial) {
		this.CedenteRazaoSocial = CedenteRazaoSocial;
	}

	public void setCedenteNomeFantasia(String CedenteNomeFantasia) {
		this.CedenteNomeFantasia = CedenteNomeFantasia;
	}

	public void setCedenteCPFCNPJ(String CedenteCPFCNPJ) {
		this.CedenteCPFCNPJ = CedenteCPFCNPJ;
	}

	public void setCedenteEnderecoLogradouro(String CedenteEnderecoLogradouro) {
		this.CedenteEnderecoLogradouro = CedenteEnderecoLogradouro;
	}

	public void setCedenteEnderecoNumero(String CedenteEnderecoNumero) {
		this.CedenteEnderecoNumero = CedenteEnderecoNumero;
	}

	public void setCedenteEnderecoComplemento(String CedenteEnderecoComplemento) {
		this.CedenteEnderecoComplemento = CedenteEnderecoComplemento;
	}

	public void setCedenteEnderecoBairro(String CedenteEnderecoBairro) {
		this.CedenteEnderecoBairro = CedenteEnderecoBairro;
	}

	public void setCedenteEnderecoCEP(String CedenteEnderecoCEP) {
		this.CedenteEnderecoCEP = CedenteEnderecoCEP;
	}

	public void setCedenteEnderecoCidadeIBGE(String CedenteEnderecoCidadeIBGE) {
		this.CedenteEnderecoCidadeIBGE = CedenteEnderecoCidadeIBGE;
	}

	public void setCedenteTelefone(String CedenteTelefone) {
		this.CedenteTelefone = CedenteTelefone;
	}

	public void setCedenteEmail(String CedenteEmail) {
		this.CedenteEmail = CedenteEmail;
	}

	@Override
	public String toString() {
		return "CedenteReqDTO [CedenteRazaoSocial=" + CedenteRazaoSocial + ", CedenteNomeFantasia="
				+ CedenteNomeFantasia + ", CedenteCPFCNPJ=" + CedenteCPFCNPJ + ", CedenteEnderecoLogradouro="
				+ CedenteEnderecoLogradouro + ", CedenteEnderecoNumero=" + CedenteEnderecoNumero
				+ ", CedenteEnderecoComplemento=" + CedenteEnderecoComplemento + ", CedenteEnderecoBairro="
				+ CedenteEnderecoBairro + ", CedenteEnderecoCEP=" + CedenteEnderecoCEP + ", CedenteEnderecoCidadeIBGE="
				+ CedenteEnderecoCidadeIBGE + ", CedenteTelefone=" + CedenteTelefone + ", CedenteEmail=" + CedenteEmail
				+ "]";
	}

}

package br.com.avinfo.avboleto.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
@JsonInclude(Include.NON_NULL)
public class BoletoReqDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String CedenteContaNumero;
	private String CedenteContaNumeroDV;
	private String CedenteConvenioNumero;
	private String CedenteContaCodigoBanco;
	private String SacadoCPFCNPJ;
	private String SacadoEmail;
	private String SacadoEnderecoNumero;
	private String SacadoEnderecoBairro;
	private String SacadoEnderecoCEP;
	private String SacadoEnderecoCidade;
	private String SacadoEnderecoComplemento;
	private String SacadoEnderecoLogradouro;
	private String SacadoEnderecoPais;
	private String SacadoEnderecoUF;
	private String SacadoNome;
	private String SacadoTelefone;
	private String SacadoCelular;
	private String TituloDataEmissao;
	private String TituloDataVencimento;
	private String TituloMensagem01;
	private String TituloMensagem02;
	private String TituloMensagem03;
	private String TituloNossoNumero;
	private String TituloNumeroDocumento;
	private String TituloValor;
	private String TituloLocalPagamento;

	// Getter Methods

	public String getCedenteContaNumero() {
		return CedenteContaNumero;
	}

	public String getCedenteContaNumeroDV() {
		return CedenteContaNumeroDV;
	}

	public String getCedenteConvenioNumero() {
		return CedenteConvenioNumero;
	}

	public String getCedenteContaCodigoBanco() {
		return CedenteContaCodigoBanco;
	}

	public String getSacadoCPFCNPJ() {
		return SacadoCPFCNPJ;
	}

	public String getSacadoEmail() {
		return SacadoEmail;
	}

	public String getSacadoEnderecoNumero() {
		return SacadoEnderecoNumero;
	}

	public String getSacadoEnderecoBairro() {
		return SacadoEnderecoBairro;
	}

	public String getSacadoEnderecoCEP() {
		return SacadoEnderecoCEP;
	}

	public String getSacadoEnderecoCidade() {
		return SacadoEnderecoCidade;
	}

	public String getSacadoEnderecoComplemento() {
		return SacadoEnderecoComplemento;
	}

	public String getSacadoEnderecoLogradouro() {
		return SacadoEnderecoLogradouro;
	}

	public String getSacadoEnderecoPais() {
		return SacadoEnderecoPais;
	}

	public String getSacadoEnderecoUF() {
		return SacadoEnderecoUF;
	}

	public String getSacadoNome() {
		return SacadoNome;
	}

	public String getSacadoTelefone() {
		return SacadoTelefone;
	}

	public String getSacadoCelular() {
		return SacadoCelular;
	}

	public String getTituloDataEmissao() {
		return TituloDataEmissao;
	}

	public String getTituloDataVencimento() {
		return TituloDataVencimento;
	}

	public String getTituloMensagem01() {
		return TituloMensagem01;
	}

	public String getTituloMensagem02() {
		return TituloMensagem02;
	}

	public String getTituloMensagem03() {
		return TituloMensagem03;
	}

	public String getTituloNossoNumero() {
		return TituloNossoNumero;
	}

	public String getTituloNumeroDocumento() {
		return TituloNumeroDocumento;
	}

	public String getTituloValor() {
		return TituloValor;
	}

	public String getTituloLocalPagamento() {
		return TituloLocalPagamento;
	}

	// Setter Methods

	public void setCedenteContaNumero(String CedenteContaNumero) {
		this.CedenteContaNumero = CedenteContaNumero;
	}

	public void setCedenteContaNumeroDV(String CedenteContaNumeroDV) {
		this.CedenteContaNumeroDV = CedenteContaNumeroDV;
	}

	public void setCedenteConvenioNumero(String CedenteConvenioNumero) {
		this.CedenteConvenioNumero = CedenteConvenioNumero;
	}

	public void setCedenteContaCodigoBanco(String CedenteContaCodigoBanco) {
		this.CedenteContaCodigoBanco = CedenteContaCodigoBanco;
	}

	public void setSacadoCPFCNPJ(String SacadoCPFCNPJ) {
		this.SacadoCPFCNPJ = SacadoCPFCNPJ;
	}

	public void setSacadoEmail(String SacadoEmail) {
		this.SacadoEmail = SacadoEmail;
	}

	public void setSacadoEnderecoNumero(String SacadoEnderecoNumero) {
		this.SacadoEnderecoNumero = SacadoEnderecoNumero;
	}

	public void setSacadoEnderecoBairro(String SacadoEnderecoBairro) {
		this.SacadoEnderecoBairro = SacadoEnderecoBairro;
	}

	public void setSacadoEnderecoCEP(String SacadoEnderecoCEP) {
		this.SacadoEnderecoCEP = SacadoEnderecoCEP;
	}

	public void setSacadoEnderecoCidade(String SacadoEnderecoCidade) {
		this.SacadoEnderecoCidade = SacadoEnderecoCidade;
	}

	public void setSacadoEnderecoComplemento(String SacadoEnderecoComplemento) {
		this.SacadoEnderecoComplemento = SacadoEnderecoComplemento;
	}

	public void setSacadoEnderecoLogradouro(String SacadoEnderecoLogradouro) {
		this.SacadoEnderecoLogradouro = SacadoEnderecoLogradouro;
	}

	public void setSacadoEnderecoPais(String SacadoEnderecoPais) {
		this.SacadoEnderecoPais = SacadoEnderecoPais;
	}

	public void setSacadoEnderecoUF(String SacadoEnderecoUF) {
		this.SacadoEnderecoUF = SacadoEnderecoUF;
	}

	public void setSacadoNome(String SacadoNome) {
		this.SacadoNome = SacadoNome;
	}

	public void setSacadoTelefone(String SacadoTelefone) {
		this.SacadoTelefone = SacadoTelefone;
	}

	public void setSacadoCelular(String SacadoCelular) {
		this.SacadoCelular = SacadoCelular;
	}

	public void setTituloDataEmissao(String TituloDataEmissao) {
		this.TituloDataEmissao = TituloDataEmissao;
	}

	public void setTituloDataVencimento(String TituloDataVencimento) {
		this.TituloDataVencimento = TituloDataVencimento;
	}

	public void setTituloMensagem01(String TituloMensagem01) {
		this.TituloMensagem01 = TituloMensagem01;
	}

	public void setTituloMensagem02(String TituloMensagem02) {
		this.TituloMensagem02 = TituloMensagem02;
	}

	public void setTituloMensagem03(String TituloMensagem03) {
		this.TituloMensagem03 = TituloMensagem03;
	}

	public void setTituloNossoNumero(String TituloNossoNumero) {
		this.TituloNossoNumero = TituloNossoNumero;
	}

	public void setTituloNumeroDocumento(String TituloNumeroDocumento) {
		this.TituloNumeroDocumento = TituloNumeroDocumento;
	}

	public void setTituloValor(String TituloValor) {
		this.TituloValor = TituloValor;
	}

	public void setTituloLocalPagamento(String TituloLocalPagamento) {
		this.TituloLocalPagamento = TituloLocalPagamento;
	}

}

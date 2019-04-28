package br.com.avinfo.avboleto.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
@JsonInclude(Include.NON_NULL)
public class ConvenioDTO {
 
 private String ConvenioNumero;
 private String ConvenioDescricao;
 private String ConvenioCarteira;
 private String ConvenioEspecie;
 private String ConvenioPadraoCNAB;
 private String ConvenioNumeroRemessa;
 private boolean ConvenioReiniciarDiariamente;
 private Long Conta;


 // Getter Methods 

 public String getConvenioNumero() {
  return ConvenioNumero;
 }

 public String getConvenioDescricao() {
  return ConvenioDescricao;
 }

 public String getConvenioCarteira() {
  return ConvenioCarteira;
 }

 public String getConvenioEspecie() {
  return ConvenioEspecie;
 }

 public String getConvenioPadraoCNAB() {
  return ConvenioPadraoCNAB;
 }

 public String getConvenioNumeroRemessa() {
  return ConvenioNumeroRemessa;
 }

 public boolean getConvenioReiniciarDiariamente() {
  return ConvenioReiniciarDiariamente;
 }

 public Long getConta() {
  return Conta;
 }

 // Setter Methods 

 public void setConvenioNumero(String ConvenioNumero) {
  this.ConvenioNumero = ConvenioNumero;
 }

 public void setConvenioDescricao(String ConvenioDescricao) {
  this.ConvenioDescricao = ConvenioDescricao;
 }

 public void setConvenioCarteira(String ConvenioCarteira) {
  this.ConvenioCarteira = ConvenioCarteira;
 }

 public void setConvenioEspecie(String ConvenioEspecie) {
  this.ConvenioEspecie = ConvenioEspecie;
 }

 public void setConvenioPadraoCNAB(String ConvenioPadraoCNAB) {
  this.ConvenioPadraoCNAB = ConvenioPadraoCNAB;
 }

 public void setConvenioNumeroRemessa(String ConvenioNumeroRemessa) {
  this.ConvenioNumeroRemessa = ConvenioNumeroRemessa;
 }

 public void setConvenioReiniciarDiariamente(boolean ConvenioReiniciarDiariamente) {
  this.ConvenioReiniciarDiariamente = ConvenioReiniciarDiariamente;
 }

 public void setConta(Long Conta) {
  this.Conta = Conta;
 }
}
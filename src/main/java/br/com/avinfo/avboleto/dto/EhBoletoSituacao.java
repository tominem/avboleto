package br.com.avinfo.avboleto.dto;

import java.util.Arrays;

public enum EhBoletoSituacao {
	
	PENDENTE (1),
	
	SALVO (2),
	
	FALHA(3),
	
	EMITIDO(4),
	
	PROTOCOLADO(5),
	
	REJEITADO(6),
	
	REGISTRADO(7),
	
	LIQUIDADO(8),
	
	BAIXADO(9);
	
	private int situacaoId;
	
	private EhBoletoSituacao(int situacaoId) {
		this.situacaoId = situacaoId;
	}

	public int getSituacaoId() {
		return situacaoId;
	}
	
	public static int situacao(String situacao) {
		return valueFrom(situacao).getSituacaoId();
	}

	public static EhBoletoSituacao valueFrom(String situacao) {
		for (EhBoletoSituacao boletoStatus : Arrays.asList(EhBoletoSituacao.values())) {
			if (boletoStatus.toString().equalsIgnoreCase(situacao)) {
				return boletoStatus;
			}
		}
		return null;
	}
	
}

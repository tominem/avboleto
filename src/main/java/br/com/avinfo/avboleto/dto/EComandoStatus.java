package br.com.avinfo.avboleto.dto;

public enum EComandoStatus {
	
	PENDENTE(1), 
	
	SUCESSO(2),
	
	ERRO(3),
	
	PROCESSANDO(4);
	
	private int status;

	private EComandoStatus(int status) {
		this.setStatus(status);
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	};
	
}

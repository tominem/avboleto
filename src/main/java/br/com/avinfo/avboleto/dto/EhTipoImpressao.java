package br.com.avinfo.avboleto.dto;

public enum EhTipoImpressao {
	
	PDF_NORMAL(0),

	PDF_CARNE(1),

	PDF_CARNE_TRIPLO(2),

	PDF_DUPLA(3),

	PDF_NORMAL_COM_MARCA_DAGUA(4),

	PDF_PERSONALIZADA(99);
	
	private int codigo;

	EhTipoImpressao(int codigo) {
		this.codigo = codigo;
	}
	
	public int getCodigo() {
		return codigo;
	}

}

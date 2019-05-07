package br.com.avinfo.avboleto.routes;

import java.text.DecimalFormat;
import java.util.Locale;

import org.junit.Test;

public class DcimalFormatTest {
	
	@Test
	public void test() {
		Locale.setDefault(new Locale("pt", "BR"));
		DecimalFormat decimalFormat = new DecimalFormat("##0.00");
		String valorFormatado = decimalFormat.format(32423412005.1);
		System.out.println( valorFormatado );
	}

}

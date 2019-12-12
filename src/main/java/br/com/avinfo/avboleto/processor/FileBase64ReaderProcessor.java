package br.com.avinfo.avboleto.processor;

import static br.com.avinfo.avboleto.routes.UploadArquivoRetornoRoute.FILE_HEADER;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

public class FileBase64ReaderProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		
		final Message message = exchange.getIn();
		
		Optional<Object> filePath = ofNullable(message.getHeader(FILE_HEADER));
		filePath.ifPresent(fp -> {
			
			try {
				
				byte[] fileContent = FileUtils.readFileToByteArray(new File(fp.toString()));
				message.setBody(Base64.encodeBase64String(fileContent));
				
			} catch (IOException e) {
				String errorMessage = format("Erro ao tentar ler o arquivo de retorno em (%s), detalhe: %s", fp.toString(), e.getMessage());
				throw new RuntimeException(errorMessage, e);
			}
			
		});
		
	}

}

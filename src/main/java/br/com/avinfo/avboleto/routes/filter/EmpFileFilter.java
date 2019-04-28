package br.com.avinfo.avboleto.routes.filter;

import java.io.File;

import org.springframework.stereotype.Component;

@Component(value="empBean")
public class EmpFileFilter {

	
	public File[] getFiles(String path) {
		return new File(path).listFiles(file -> {
			return file.isDirectory() == false;
		});
	}
	

}

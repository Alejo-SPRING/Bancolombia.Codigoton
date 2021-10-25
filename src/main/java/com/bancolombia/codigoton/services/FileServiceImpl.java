package com.bancolombia.codigoton.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.stereotype.Service;

/**
 * @author Alejandro
 *
 */
@Service
public class FileServiceImpl implements IFileService{

	/**
	 * Se trae el contenido de un documento.
	 * @param fileName Nombre del documento para obtener su contenido.
	 * @return Lineas de texto del documento. 
	 */
	@Override
	public List<String> readFile(final String fileName) throws IOException {
		Path filters = Paths.get("formatInput").toAbsolutePath();
		return Files.readAllLines(filters.resolve(fileName));
	}
	
}

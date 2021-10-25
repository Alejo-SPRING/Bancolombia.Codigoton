package com.bancolombia.codigoton.services;

import java.io.IOException;
import java.util.List;

public interface IFileService {

	List<String> readFile(final String fileName) throws IOException;

}

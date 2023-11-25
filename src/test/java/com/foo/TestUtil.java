package com.foo;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * - Unmarshall JSON objects from classPath
 */
@Component
public class TestUtil {

    private final ObjectMapper jsonMapper;
    private final ResourceLoader resourceLoader;

    public TestUtil(ObjectMapper jsonMapper, ResourceLoader resourceLoader) {
        this.jsonMapper = jsonMapper;
        this.resourceLoader = resourceLoader;
    }

    /**
     * Unmarshall test resource file into an object of type as specified by typeReference
     */
    public <T> T unmarshall(String filePath, TypeReference<T> typeReference) throws IOException {
        var inputStream = new ClassPathResource(filePath).getInputStream();
        return jsonMapper.readValue(inputStream, typeReference);
    }

    /**
     * Read a  test resources file into a string
     * @param filePath
     * @throws URISyntaxException
     * @throws IOException
     */
    public String getResourceAsString(String filePath) throws URISyntaxException, IOException {
        var resource = new ClassPathResource(filePath);
        return Files.readString(Paths.get(resource.getURI()));
    }

    /**
     * Read from a resources file into a byte[]
     * @param filePath
     * @throws URISyntaxException
     * @throws IOException
     */
    public byte[] getResourceAsByteArray(String filePath) throws URISyntaxException, IOException {
        var resource = new ClassPathResource(filePath);
        return Files.readAllBytes(Paths.get(resource.getURI()));
    }
}

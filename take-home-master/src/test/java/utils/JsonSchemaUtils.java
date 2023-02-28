package utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import dataclass.GameInfo;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class JsonSchemaUtils {
    ObjectMapper objectMapper = new ObjectMapper();
    public ProcessingReport checkJsonSchema(String jsonSchemaPath, String URI) throws IOException, URISyntaxException, ProcessingException {
        URI apiUrl = new URI(URI);
        JsonNode schemaNode = JsonLoader.fromResource(jsonSchemaPath);
        JsonSchema schema = JsonSchemaFactory.byDefault().getJsonSchema(schemaNode);
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(apiUrl);
        CloseableHttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        String jsonResponse = entity != null ? EntityUtils.toString(entity) : null;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseNode = mapper.readTree(jsonResponse);
        ProcessingReport report = schema.validate(responseNode);
        return report;
    }

    public GameInfo parseJsonResponse(String response) throws JsonProcessingException {
        GameInfo gameInfo = objectMapper.readValue(response, GameInfo.class);
        return gameInfo;
    }
}
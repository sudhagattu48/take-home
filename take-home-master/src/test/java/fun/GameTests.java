package fun;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import dataclass.GameInfo;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import utils.JsonSchemaUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

public class GameTests {
    JsonSchemaUtils jsonSchemaUtils = new JsonSchemaUtils();
    private final static String JSON_SCHEMA_ACTIVITY_PATH = "/schema/json-schema.json";
    CloseableHttpClient httpClient = HttpClients.createDefault();

    @Test
    @DisplayName("To verify the status code of the valid response")
    public void test_valid_response() throws IOException {
        Game game = new Game(1, "TestGame");
        HttpUriRequest request = new HttpGet("http://localhost:8080/game?name=" + game.getText());
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        Assertions.assertEquals(httpResponse.getStatusLine().getStatusCode(), HttpStatus.SC_OK);
        Assertions.assertNotNull(httpResponse.getStatusLine().getReasonPhrase());
    }

    @Test
    @DisplayName("To verify the status code of the invalid response")
    public void test_invalid_response() throws IOException {
        Game game = new Game(2, "///sdsds?");
        HttpGet request = new HttpGet("http://localhost:8080/game?name=" + game.getText());
        CloseableHttpResponse response = httpClient.execute(request);
        StatusLine statusLine = response.getStatusLine();
        String responseBody = EntityUtils.toString(response.getEntity());
        if (responseBody.contains(game.getText())){
            System.out.println(responseBody);
        }
        Assertions.assertEquals(statusLine.getStatusCode(), HttpStatus.SC_OK);
    }

    @Test
    @DisplayName("To verify the status code of the blank space")
    public void test_blank_game_name_response() throws IOException {
        Game game = new Game(1, "");
        HttpGet request = new HttpGet("http://localhost:8080/game?name=" + game.getText());
        CloseableHttpResponse response = httpClient.execute(request);
        StatusLine statusLine = response.getStatusLine();
        String responseBody = EntityUtils.toString(response.getEntity());
        Assertions.assertEquals(statusLine.getStatusCode(), HttpStatus.SC_OK);
        Assertions.assertNotNull(responseBody);
    }

    @Test
    @DisplayName("To verify the status code and response if user don't pass any game name")
    public void test_invalid_parameter() throws IOException {
        HttpGet request = new HttpGet("http://localhost:8080/");
        CloseableHttpResponse response = httpClient.execute(request);
        StatusLine statusLine = response.getStatusLine();
        String responseBody = EntityUtils.toString(response.getEntity());
        Assertions.assertEquals(statusLine.getStatusCode(), HttpStatus.SC_NOT_FOUND);
        System.out.println(responseBody);
    }

    @Test
    @DisplayName("To verify the status code and response if user don't pass any game name")
    public void test_invalid_parameter_name() throws IOException {
        HttpGet request = new HttpGet("http://localhost:8080/name?game=sudoku");
        CloseableHttpResponse response = httpClient.execute(request);
        StatusLine statusLine = response.getStatusLine();
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println(responseBody);
        Assertions.assertEquals(statusLine.getStatusCode(), HttpStatus.SC_NOT_FOUND);
    }


    @Test
    @DisplayName("To verify the Json schema")
    public void validateJsonSchema() throws IOException, URISyntaxException, ProcessingException {
        String URI = "http://localhost:8080/game?name=";
        ProcessingReport report = jsonSchemaUtils.checkJsonSchema(JSON_SCHEMA_ACTIVITY_PATH, URI);
        Assertions.assertTrue(report.isSuccess(), "Test case failed, error in schema");

    }

    @Test
    @DisplayName("Storing the response into data class")
    public void test_storeResponse() throws IOException {
        Game game = new Game(1, "Test");
        HttpGet request = new HttpGet("http://localhost:8080/game?name="+game.getText());
        CloseableHttpResponse response = httpClient.execute(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        GameInfo gameInfo = jsonSchemaUtils.parseJsonResponse(responseBody);
        Assertions.assertEquals(gameInfo.getText(),"Playing " +game.getText()+ " is fun!");

    }
}
package project.Engine.Http;

import project.Engine.main;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * A utility class to make Http GET, POST, PATCH requests to the given endpoint URL
 * and return the Http response as a string.
 * It is up to the adapter object that connects to this class to decide how to handle the string response.
 *
 * Single responsibility principle achieved.
 */
public class Http {

    /**
     * A utility method to make Http Get requests to the given lookup URL
     * and return the Http response as a string.
     * It is up to the adapter object that made the call to decide how to handle the string response.
     */
    public static HttpResponse<String> search(String lookupURL) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(lookupURL))
                .setHeader("Authorization", main.API_KEY)   // singleton
                .GET()
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            System.out.println("Network connection was interrupted");
            System.out.println(e.getMessage());
        }
        return response;
    }

    /**
     * https://zetcode.com/java/getpostrequest/
     *
     * A utility method to make Http POST request to the given endpoint URL.
     * The content body of the POST request it sends is of type JSON!
     * It returns the successfully created JSON object in the web service as a HttpResponse String object.
     */
    public static HttpResponse<String> post(String endpointURL, JSONObject jsonObject) {
        HttpResponse<String> response = null;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpointURL))
                    .setHeader("Authorization", main.API_KEY)
                    .header("Content-Type","application/json")  // JSON content type must be set
                    .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString()))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (JsonProcessingException e) {
            System.out.println("Unable to convert HashMap Http POST request body to string");
            System.out.println(e.getMessage());
        } catch (InterruptedException | IOException e) {
            System.out.println("Network connection was interrupted");
            System.out.println(e.getMessage());
        }
        return response;
    }

    /**
     * https://stackoverflow.com/questions/58841919/java-11-httprequest-with-patch-method
     */
    public static HttpResponse<String> patch(String endpointURL, JSONObject jsonObject) {
        HttpResponse<String> response = null;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpointURL))
                    .setHeader("Authorization", main.API_KEY)
                    .header("Content-Type","application/json")  // JSON content type must be set
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonObject.toString()))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (JsonProcessingException e) {
            System.out.println("Unable to convert HashMap Http PATCH request body to string");
            System.out.println(e.getMessage());
        } catch (InterruptedException | IOException e) {
            System.out.println("Network connection was interrupted");
            System.out.println(e.getMessage());
        }
        return response;
    }

    /**
     * https://stackoverflow.com/questions/63560572/how-to-send-delete-request-using-httpclient
     */
    public static HttpResponse<String> delete(String deleteURL) {
        // everything is basically the same as making a http GET request except for invoking .DELETE()
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(deleteURL))
                .setHeader("Authorization", main.API_KEY)   // singleton
                .DELETE()
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            System.out.println("Network connection was interrupted");
            System.out.println(e.getMessage());
        }
        return response;
    }
}

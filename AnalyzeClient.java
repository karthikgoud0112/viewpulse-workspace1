import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AnalyzeClient {
    public static void main(String[] args) throws Exception {
        String text = "I am so happy and excited about this product!";
        if (args.length > 0) {
            text = String.join(" ", args);
        }

        // build JSON safely (escape double quotes in the text)
        String json = "{\"text\":\"" + text.replace("\"", "\\\"") + "\"}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:5000/analyze"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

        System.out.println("HTTP " + resp.statusCode());
        System.out.println(resp.body());
    }
}

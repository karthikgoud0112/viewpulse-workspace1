// AnalyzeClientParse.java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalyzeClientParse {
    public static void main(String[] args) throws Exception {
        String text = (args.length > 0) ? String.join(" ", args) : "I am so happy and excited about this product!";
        String json = "{\"text\":\"" + text.replace("\"", "\\\"") + "\"}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:5000/analyze"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        String body = resp.body();
        System.out.println("HTTP " + resp.statusCode());

        // Simple regex extractors (works for this API shape)
        String emotion = extractString(body, "\"emotion\"\\s*:\\s*\"(.*?)\"");
        String confidence = extractString(body, "\"confidence\"\\s*:\\s*([0-9eE+\\-\\.]+)");

        System.out.println("Emotion: " + (emotion != null ? emotion : "n/a"));
        System.out.println("Confidence: " + (confidence != null ? confidence : "n/a"));
        // print full body if you want
        // System.out.println(body);
    }

    private static String extractString(String text, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        return m.find() ? m.group(1) : null;
    }
}

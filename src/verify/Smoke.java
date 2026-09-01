import io.kraken.client.KrakenIoClient;
import io.kraken.client.impl.DefaultKrakenIoClient;
import io.kraken.client.model.request.ImageUrlUploadRequest;
import java.net.URL;

// Java 7 source. Proves the published artifact and its dependencies load and
// run on a Java 7 JVM, which GitHub Actions cannot check (setup-java has no 7).
public class Smoke {
    public static void main(String[] args) throws Exception {
        System.out.println("java.version   = " + System.getProperty("java.version"));
        System.out.println("class.version  = " + System.getProperty("java.class.version"));

        KrakenIoClient client =
            new DefaultKrakenIoClient("api-key", "api-secret", "http://127.0.0.1:1");
        System.out.println("client         = " + client.getClass().getName());

        ImageUrlUploadRequest req =
            ImageUrlUploadRequest.builder(new URL("https://example.com/a.jpg")).build();
        System.out.println("request        = " + req.getClass().getName());
        System.out.println("OK");
    }
}

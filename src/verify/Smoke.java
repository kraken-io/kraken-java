/**
 * Copyright (C) 2015 Nekkra UG (oss@kraken.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

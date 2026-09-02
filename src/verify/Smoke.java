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
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.kraken.client.KrakenIoClient;
import io.kraken.client.impl.DefaultKrakenIoClient;
import io.kraken.client.model.request.ImageUrlUploadRequest;
import io.kraken.client.model.response.SuccessfulUploadResponse;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;

/**
 * End-to-end check on the oldest supported JVM.
 *
 * The functional suite needs json-unit and mockserver, both Java 8 only, so it
 * cannot run here. This does the same thing with nothing but the JDK: serve a
 * canned response from com.sun.net.httpserver, point the client at it, and
 * assert a full request/response cycle works — Jersey, Jackson and the model
 * classes included.
 */
public class Smoke {

    private static final String RESPONSE =
        "{\"success\":true,\"file_name\":\"header.jpg\",\"original_size\":100,"
      + "\"kraked_size\":50,\"saved_bytes\":50,"
      + "\"kraked_url\":\"http://dl.kraken.io/abc/header.jpg\"}";

    private static volatile String receivedBody;

    public static void main(String[] args) throws Exception {
        System.out.println("java.version   = " + System.getProperty("java.version"));
        System.out.println("class.version  = " + System.getProperty("java.class.version"));

        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", new HttpHandler() {
            public void handle(HttpExchange exchange) throws java.io.IOException {
                byte[] in = new byte[8192];
                int n = exchange.getRequestBody().read(in);
                receivedBody = n > 0 ? new String(in, 0, n, "UTF-8") : "";
                byte[] out = RESPONSE.getBytes("UTF-8");
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, out.length);
                OutputStream os = exchange.getResponseBody();
                os.write(out);
                os.close();
            }
        });
        server.start();

        try {
            String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
            KrakenIoClient client = new DefaultKrakenIoClient("api-key", "api-secret", baseUrl);

            ImageUrlUploadRequest request =
                ImageUrlUploadRequest.builder(new URL("https://example.com/header.jpg")).build();

            SuccessfulUploadResponse response = client.imageUrlUpload(request);

            check(response != null, "response was null");
            check(Boolean.TRUE.equals(response.getSuccess()), "success was not true");
            check("header.jpg".equals(response.getFileName()), "file_name did not round-trip");
            check(Integer.valueOf(50).equals(response.getKrakedSize()), "kraked_size did not round-trip");
            check("http://dl.kraken.io/abc/header.jpg".equals(response.getKrakedUrl()),
                  "kraked_url did not round-trip");

            check(receivedBody != null && receivedBody.contains("api-key"),
                  "auth was not serialised into the request");
            check(receivedBody.contains("header.jpg"), "image url was not serialised");

            System.out.println("request sent  = " + receivedBody);
            System.out.println("response      = " + response.getKrakedUrl());
            System.out.println("OK");
        } finally {
            server.stop(0);
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException("FAILED: " + message);
        }
    }
}

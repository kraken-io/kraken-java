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
package io.kraken.client.impl;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import io.kraken.client.AbstractFunctionalTest;
import io.kraken.client.KrakenIoClient;
import io.kraken.client.exception.KrakenIoException;
import io.kraken.client.exception.KrakenIoRequestException;
import io.kraken.client.model.RGBA;
import io.kraken.client.model.request.*;
import io.kraken.client.model.resize.FillResize;
import io.kraken.client.model.response.SuccessfulUploadCallbackUrlResponse;
import io.kraken.client.model.response.SuccessfulUploadResponse;
import org.apache.commons.fileupload.MultipartStream;
import org.junit.Before;
import org.junit.Test;
import org.mockserver.mock.Expectation;
import org.mockserver.model.Header;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
public class DefaultKrakenIoClientFunctionalTest extends AbstractFunctionalTest {

    private static final Pattern MULTIPART_BOUNDARY_PATTERN = Pattern.compile("multipart/form-data;boundary=(.*)");

    private KrakenIoClient krakenIoClient;

    @Before
    public void setUp() throws Exception {
        krakenIoClient = new DefaultKrakenIoClient("somekey", "somesecret", "http://localhost:1080", 3000);
    }

    @Test
    public void testDirectUploadSimple() throws Exception {
        final DirectUploadRequest directUploadRequest = DirectUploadRequest.builder(new ByteArrayInputStream(loadFileBinary("test.jpg"))).build();
        internalTestDirectUpload(directUploadRequest, loadFileString("krakenIoRequestDirectSimple.json"));
    }

    @Test
    public void testDirectUploadResize() throws Exception {
        final FillResize fillResize = new FillResize(150, 150, new RGBA(100, 100, 100, BigDecimal.ONE));
        final DirectUploadRequest directUploadRequest = DirectUploadRequest.builder(new ByteArrayInputStream(loadFileBinary("test.jpg"))).withResize(fillResize).build();
        internalTestDirectUpload(directUploadRequest, loadFileString("krakenIoRequestDirectResize.json"));
    }

    private void internalTestDirectUpload(DirectUploadRequest directUploadRequest, String requestJson) throws Exception {
        getMockServerClient()
                .when(
                        request()
                                .withPath("/v1/upload")
                                .withHeader(new Header("Content-Type", "multipart/form-data.*"))
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(new Header("Content-Type", "application/json"))
                                .withBody(loadFileString("krakenIoResponse200.json"))
                );

        final SuccessfulUploadResponse successfulUploadResponse = krakenIoClient.directUpload(directUploadRequest);
        assertThat(successfulUploadResponse.getStatus(), is(200));
        assertThat(successfulUploadResponse.getSuccess(), is(true));
        assertThat(successfulUploadResponse.getFileName(), is("header.jpg"));
        assertThat(successfulUploadResponse.getKrakedUrl(), is("http://dl.kraken.io/ecdfa5c55d5668b1b5fe9e420554c4ee/header.jpg"));
        assertThat(successfulUploadResponse.getOriginalSize(), is(100));
        assertThat(successfulUploadResponse.getSavedBytes(), is(50));
        assertThat(successfulUploadResponse.getKrakedSize(), is(50));

        final Expectation[] expectations = getMockServerClient().retrieveAsExpectations(
                request()
                        .withPath("/v1/upload")
                        .withHeader(new Header("Content-Type", "multipart/form-data.*"))
        );

        assertThat(Arrays.asList(expectations), hasSize(1));

        final ByteArrayInputStream content = new ByteArrayInputStream(expectations[0].getHttpRequest().getBodyAsRawBytes());
        final MultipartStream multipartStream = new MultipartStream(content, getMultipartBoundary(expectations[0]));

        final ByteArrayOutputStream image = new ByteArrayOutputStream();
        final ByteArrayOutputStream request = new ByteArrayOutputStream();

        final String requestHeaders = multipartStream.readHeaders();
        multipartStream.readBodyData(request);
        multipartStream.readBoundary();

        final String imageHeaders = multipartStream.readHeaders();
        multipartStream.readBodyData(image);
        multipartStream.readBoundary();

        assertThat(requestHeaders, containsString("Content-Type: application/json"));
        assertThat(imageHeaders, containsString("filename="));
        assertThat(image.toByteArray(), is(loadFileBinary("test.jpg")));
        assertThat(new String(request.toByteArray(), Charsets.UTF_8), jsonEquals(requestJson));
    }

    @Test
    public void testDirectFileUploadSimple() throws Exception {
        final DirectFileUploadRequest directFileUploadRequest = DirectFileUploadRequest.builder(new File(Resources.getResource(DefaultKrakenIoClientFunctionalTest.class, "test.jpg").toURI())).build();
        internalTestDirectFileUpload(directFileUploadRequest, loadFileString("krakenIoRequestDirectSimple.json"));
    }

    @Test
    public void testDirectFileUploadResize() throws Exception {
        final FillResize fillResize = new FillResize(150, 150, new RGBA(100, 100, 100, BigDecimal.ONE));
        final DirectFileUploadRequest directFileUploadRequest = DirectFileUploadRequest.builder(new File(Resources.getResource(DefaultKrakenIoClientFunctionalTest.class, "test.jpg").toURI())).withResize(fillResize).build();
        internalTestDirectFileUpload(directFileUploadRequest, loadFileString("krakenIoRequestDirectResize.json"));
    }

    private void internalTestDirectFileUpload(DirectFileUploadRequest directFileUploadRequest, String requestJson) throws Exception {
        getMockServerClient()
                .when(
                        request()
                                .withPath("/v1/upload")
                                .withHeader(new Header("Content-Type", "multipart/form-data.*"))
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(new Header("Content-Type", "application/json"))
                                .withBody(loadFileString("krakenIoResponse200.json"))
                );

        final SuccessfulUploadResponse successfulUploadResponse = krakenIoClient.directUpload(directFileUploadRequest);
        assertThat(successfulUploadResponse.getStatus(), is(200));
        assertThat(successfulUploadResponse.getSuccess(), is(true));
        assertThat(successfulUploadResponse.getFileName(), is("header.jpg"));
        assertThat(successfulUploadResponse.getKrakedUrl(), is("http://dl.kraken.io/ecdfa5c55d5668b1b5fe9e420554c4ee/header.jpg"));
        assertThat(successfulUploadResponse.getOriginalSize(), is(100));
        assertThat(successfulUploadResponse.getSavedBytes(), is(50));
        assertThat(successfulUploadResponse.getKrakedSize(), is(50));

        final Expectation[] expectations = getMockServerClient().retrieveAsExpectations(
                request()
                        .withPath("/v1/upload")
                        .withHeader(new Header("Content-Type", "multipart/form-data.*"))
        );

        assertThat(Arrays.asList(expectations), hasSize(1));
        final ByteArrayInputStream content = new ByteArrayInputStream(expectations[0].getHttpRequest().getBodyAsRawBytes());
        final MultipartStream multipartStream = new MultipartStream(content, getMultipartBoundary(expectations[0]));

        final ByteArrayOutputStream image = new ByteArrayOutputStream();
        final ByteArrayOutputStream request = new ByteArrayOutputStream();

        final String requestHeaders = multipartStream.readHeaders();
        multipartStream.readBodyData(request);
        multipartStream.readBoundary();

        final String imageHeaders = multipartStream.readHeaders();
        multipartStream.readBodyData(image);
        multipartStream.readBoundary();

        assertThat(requestHeaders, containsString("Content-Type: application/json"));
        assertThat(imageHeaders, containsString("filename=\"test.jpg\""));
        assertThat(imageHeaders, containsString("Content-Type: image/jpeg"));
        assertThat(image.toByteArray(), is(loadFileBinary("test.jpg")));
        assertThat(new String(request.toByteArray(), Charsets.UTF_8), jsonEquals(requestJson));
    }

    @Test
    public void testDirectUploadCallbackUrlSimple() throws Exception {
        final DirectUploadCallbackUrlRequest directUploadCallbackUrlRequest = DirectUploadCallbackUrlRequest.builder(new ByteArrayInputStream(loadFileBinary("test.jpg")), new URL("http://somehost/somecallback")).build();
        internalTestDirectUploadCallbackUrl(directUploadCallbackUrlRequest, loadFileString("krakenIoRequestDirectCallbackUrlSimple.json"));
    }

    @Test
    public void testDirectUploadCallbackUrlResize() throws Exception {
        final FillResize fillResize = new FillResize(150, 150, new RGBA(100, 100, 100, BigDecimal.ONE));
        final DirectUploadCallbackUrlRequest directUploadCallbackUrlRequest = DirectUploadCallbackUrlRequest.builder(new ByteArrayInputStream(loadFileBinary("test.jpg")), new URL("http://somehost/somecallback")).withResize(fillResize).build();
        internalTestDirectUploadCallbackUrl(directUploadCallbackUrlRequest, loadFileString("krakenIoRequestDirectCallbackUrlResize.json"));
    }

    private void internalTestDirectUploadCallbackUrl(DirectUploadCallbackUrlRequest directUploadCallbackUrlRequest, String requestJson) throws Exception {
        getMockServerClient()
                .when(
                        request()
                                .withPath("/v1/upload")
                                .withHeader(new Header("Content-Type", "multipart/form-data.*"))
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(new Header("Content-Type", "application/json"))
                                .withBody(loadFileString("krakenIoCallbackUrlResponse200.json"))
                );

        final SuccessfulUploadCallbackUrlResponse successfulUploadCallbackUrlResponse = krakenIoClient.directUpload(directUploadCallbackUrlRequest);
        assertThat(successfulUploadCallbackUrlResponse.getId(), is("18fede37617a787649c3f60b9f1f280d"));

        final Expectation[] expectations = getMockServerClient().retrieveAsExpectations(
                request()
                        .withPath("/v1/upload")
                        .withHeader(new Header("Content-Type", "multipart/form-data.*"))
        );

        assertThat(Arrays.asList(expectations), hasSize(1));

        final ByteArrayInputStream content = new ByteArrayInputStream(expectations[0].getHttpRequest().getBodyAsRawBytes());
        final MultipartStream multipartStream = new MultipartStream(content, getMultipartBoundary(expectations[0]));

        final ByteArrayOutputStream image = new ByteArrayOutputStream();
        final ByteArrayOutputStream request = new ByteArrayOutputStream();

        final String requestHeaders = multipartStream.readHeaders();
        multipartStream.readBodyData(request);
        multipartStream.readBoundary();

        final String imageHeaders = multipartStream.readHeaders();
        multipartStream.readBodyData(image);
        multipartStream.readBoundary();

        assertThat(requestHeaders, containsString("Content-Type: application/json"));
        assertThat(imageHeaders, containsString("filename="));
        assertThat(image.toByteArray(), is(loadFileBinary("test.jpg")));
        assertThat(new String(request.toByteArray(), Charsets.UTF_8), jsonEquals(requestJson));
    }

    @Test
    public void testImageUrlUploadSimple() throws Exception {
        final ImageUrlUploadRequest imageUrlUploadRequest = ImageUrlUploadRequest.builder(new URL("http://somehost/image")).build();
        internalTestImageUrlUpload(imageUrlUploadRequest, loadFileString("krakenIoRequestImageUrlSimple.json"));
    }

    @Test
    public void testImageUrlUploadResize() throws Exception {
        final FillResize fillResize = new FillResize(150, 150, new RGBA(100, 100, 100, BigDecimal.ONE));
        final ImageUrlUploadRequest imageUrlUploadRequest = ImageUrlUploadRequest.builder(new URL("http://somehost/image")).withResize(fillResize).build();
        internalTestImageUrlUpload(imageUrlUploadRequest, loadFileString("krakenIoRequestImageUrlResize.json"));
    }

    private void internalTestImageUrlUpload(ImageUrlUploadRequest imageUrlUploadRequest, String requestJson) throws Exception {
        getMockServerClient()
                .when(
                        request()
                                .withPath("/v1/url")
                                .withHeader(new Header("Content-Type", "application/json"))
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(new Header("Content-Type", "application/json"))
                                .withBody(loadFileString("krakenIoResponse200.json"))
                );

        final SuccessfulUploadResponse successfulUploadResponse = krakenIoClient.imageUrlUpload(imageUrlUploadRequest);
        assertThat(successfulUploadResponse.getStatus(), is(200));
        assertThat(successfulUploadResponse.getSuccess(), is(true));
        assertThat(successfulUploadResponse.getFileName(), is("header.jpg"));
        assertThat(successfulUploadResponse.getKrakedUrl(), is("http://dl.kraken.io/ecdfa5c55d5668b1b5fe9e420554c4ee/header.jpg"));
        assertThat(successfulUploadResponse.getOriginalSize(), is(100));
        assertThat(successfulUploadResponse.getSavedBytes(), is(50));
        assertThat(successfulUploadResponse.getKrakedSize(), is(50));

        final Expectation[] expectations = getMockServerClient().retrieveAsExpectations(
                request()
                        .withPath("/v1/url")
                        .withHeader(new Header("Content-Type", "application/json"))
        );

        assertThat(Arrays.asList(expectations), hasSize(1));
        assertThat(expectations[0].getHttpRequest().getFirstHeader("Content-Type"), is("application/json"));
        assertThat(new String(expectations[0].getHttpRequest().getBody().getRawBytes(), Charsets.UTF_8), jsonEquals(requestJson));
    }

    @Test
    public void testImageUrlUploadCallbackUrlSimple() throws Exception {
        final ImageUrlUploadCallbackUrlRequest imageUrlUploadCallbackUrlRequest = ImageUrlUploadCallbackUrlRequest
                .builder(new URL("http://somehost/image"), new URL("http://somehost/somecallback"))
                .build();
        internalTestImageUrlUploadCallbackUrl(imageUrlUploadCallbackUrlRequest, loadFileString("krakenIoRequestCallbackUrlSimple.json"));
    }

    @Test
    public void testImageUrlUploadCallbackUrlResize() throws Exception {
        final FillResize fillResize = new FillResize(150, 150, new RGBA(100, 100, 100, BigDecimal.ONE));
        final ImageUrlUploadCallbackUrlRequest imageUrlUploadCallbackUrlRequest = ImageUrlUploadCallbackUrlRequest
                .builder(new URL("http://somehost/image"), new URL("http://somehost/somecallback"))
                .withResize(fillResize)
                .build();
        internalTestImageUrlUploadCallbackUrl(imageUrlUploadCallbackUrlRequest, loadFileString("krakenIoRequestCallbackUrlResize.json"));
    }

    public void internalTestImageUrlUploadCallbackUrl(ImageUrlUploadCallbackUrlRequest imageUrlUploadCallbackUrlRequest, String requestJson) throws Exception {
        getMockServerClient()
                .when(
                        request()
                                .withPath("/v1/url")
                                .withHeader(new Header("Content-Type", "application/json"))
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(new Header("Content-Type", "application/json"))
                                .withBody(loadFileString("krakenIoCallbackUrlResponse200.json"))
                );

        final SuccessfulUploadCallbackUrlResponse successfulUploadCallbackUrlResponse = krakenIoClient.imageUrlUpload(imageUrlUploadCallbackUrlRequest);
        assertThat(successfulUploadCallbackUrlResponse.getId(), is("18fede37617a787649c3f60b9f1f280d"));

        final Expectation[] expectations = getMockServerClient().retrieveAsExpectations(
                request()
                        .withPath("/v1/url")
                        .withHeader(new Header("Content-Type", "application/json"))
        );

        assertThat(Arrays.asList(expectations), hasSize(1));
        assertThat(expectations[0].getHttpRequest().getFirstHeader("Content-Type"), is("application/json"));
        assertThat(new String(expectations[0].getHttpRequest().getBody().getRawBytes(), Charsets.UTF_8), jsonEquals(requestJson));
    }

    @Test
    public void testImageUrlUploadCallbackUrl_400() throws Exception {
        getMockServerClient()
                .when(
                        request()
                                .withPath("/v1/url")
                                .withHeader(new Header("Content-Type", "application/json"))
                )
                .respond(
                        response()
                                .withStatusCode(400)
                                .withHeader(new Header("Content-Type", "application/json"))
                                .withBody(loadFileString("krakenIoResponse400.json"))
                );

        try {
            krakenIoClient.imageUrlUpload(ImageUrlUploadCallbackUrlRequest.builder(new URL("http://somehost/image"), new URL("http://somehost/somecallback")).build());
        } catch (KrakenIoRequestException e) {
            assertThat(e.getFailedUploadResponse().getStatus(), is(400));
            assertThat(e.getFailedUploadResponse().getSuccess(), is(false));
        }
    }

    @Test
    public void testImageUrlUploadCallbackUrl_401() throws Exception {
        getMockServerClient()
                .when(
                        request()
                                .withPath("/v1/url")
                                .withHeader(new Header("Content-Type", "application/json"))
                )
                .respond(
                        response()
                                .withStatusCode(401)
                                .withHeader(new Header("Content-Type", "application/json"))
                                .withBody(loadFileString("krakenIoResponse401.json"))
                );

        try {
            krakenIoClient.imageUrlUpload(ImageUrlUploadCallbackUrlRequest.builder(new URL("http://somehost/image"), new URL("http://somehost/somecallback")).build());
        } catch (KrakenIoRequestException e) {
            assertThat(e.getFailedUploadResponse().getStatus(), is(401));
            assertThat(e.getFailedUploadResponse().getSuccess(), is(false));
        }
    }

    @Test
    public void testImageUrlUploadCallbackUrl_403() throws Exception {
        getMockServerClient()
                .when(
                        request()
                                .withPath("/v1/url")
                                .withHeader(new Header("Content-Type", "application/json"))
                )
                .respond(
                        response()
                                .withStatusCode(403)
                                .withHeader(new Header("Content-Type", "application/json"))
                                .withBody(loadFileString("krakenIoResponse403.json"))
                );

        try {
            krakenIoClient.imageUrlUpload(ImageUrlUploadCallbackUrlRequest.builder(new URL("http://somehost/image"), new URL("http://somehost/somecallback")).build());
        } catch (KrakenIoRequestException e) {
            assertThat(e.getFailedUploadResponse().getStatus(), is(403));
            assertThat(e.getFailedUploadResponse().getSuccess(), is(false));
        }
    }

    @Test
    public void testImageUrlUploadCallbackUrl_413() throws Exception {
        getMockServerClient()
                .when(
                        request()
                                .withPath("/v1/url")
                                .withHeader(new Header("Content-Type", "application/json"))
                )
                .respond(
                        response()
                                .withStatusCode(413)
                                .withHeader(new Header("Content-Type", "application/json"))
                                .withBody(loadFileString("krakenIoResponse413.json"))
                );

        try {
            krakenIoClient.imageUrlUpload(ImageUrlUploadCallbackUrlRequest.builder(new URL("http://somehost/image"), new URL("http://somehost/somecallback")).build());
        } catch (KrakenIoRequestException e) {
            assertThat(e.getFailedUploadResponse().getStatus(), is(413));
            assertThat(e.getFailedUploadResponse().getSuccess(), is(false));
        }
    }

    @Test
    public void testImageUrlUploadCallbackUrl_415() throws Exception {
        getMockServerClient()
                .when(
                        request()
                                .withPath("/v1/url")
                                .withHeader(new Header("Content-Type", "application/json"))
                )
                .respond(
                        response()
                                .withStatusCode(415)
                                .withHeader(new Header("Content-Type", "application/json"))
                                .withBody(loadFileString("krakenIoResponse415.json"))
                );

        try {
            krakenIoClient.imageUrlUpload(ImageUrlUploadCallbackUrlRequest.builder(new URL("http://somehost/image"), new URL("http://somehost/somecallback")).build());
        } catch (KrakenIoRequestException e) {
            assertThat(e.getFailedUploadResponse().getStatus(), is(415));
            assertThat(e.getFailedUploadResponse().getSuccess(), is(false));
        }
    }

    @Test
    public void testImageUrlUploadCallbackUrl_422() throws Exception {
        getMockServerClient()
                .when(
                        request()
                                .withPath("/v1/url")
                                .withHeader(new Header("Content-Type", "application/json"))
                )
                .respond(
                        response()
                                .withStatusCode(422)
                                .withHeader(new Header("Content-Type", "application/json"))
                                .withBody(loadFileString("krakenIoResponse422.json"))
                );

        try {
            krakenIoClient.imageUrlUpload(ImageUrlUploadCallbackUrlRequest.builder(new URL("http://somehost/image"), new URL("http://somehost/somecallback")).build());
        } catch (KrakenIoRequestException e) {
            assertThat(e.getFailedUploadResponse().getStatus(), is(422));
            assertThat(e.getFailedUploadResponse().getSuccess(), is(false));
        }
    }

    @Test
    public void testImageUrlUploadCallbackUrl_500() throws Exception {
        getMockServerClient()
                .when(
                        request()
                                .withPath("/v1/url")
                                .withHeader(new Header("Content-Type", "application/json"))
                )
                .respond(
                        response()
                                .withStatusCode(500)
                                .withHeader(new Header("Content-Type", "application/json"))
                                .withBody(loadFileString("krakenIoResponse500.json"))
                );

        try {
            krakenIoClient.imageUrlUpload(ImageUrlUploadCallbackUrlRequest.builder(new URL("http://somehost/image"), new URL("http://somehost/somecallback")).build());
        } catch (KrakenIoRequestException e) {
            assertThat(e.getFailedUploadResponse().getStatus(), is(500));
            assertThat(e.getFailedUploadResponse().getSuccess(), is(false));
        }
    }

    @Test
    public void testImageUrlUploadCallbackUrl_500_WrongFormat() throws Exception {
        getMockServerClient()
                .when(
                        request()
                                .withPath("/v1/url")
                                .withHeader(new Header("Content-Type", "application/json"))
                )
                .respond(
                        response()
                                .withStatusCode(500)
                                .withHeader(new Header("Content-Type", "application/json"))
                                .withBody("{}")
                );

        try {
            krakenIoClient.imageUrlUpload(ImageUrlUploadCallbackUrlRequest.builder(new URL("http://somehost/image"), new URL("http://somehost/somecallback")).build());
        } catch (KrakenIoException e) {
            assertThat(e, notNullValue());
        }
    }

    @Test
    public void testImageUrlUpload_400() throws Exception {
        getMockServerClient()
                .when(
                        request()
                                .withPath("/v1/url")
                                .withHeader(new Header("Content-Type", "application/json"))
                )
                .respond(
                        response()
                                .withStatusCode(400)
                                .withHeader(new Header("Content-Type", "application/json"))
                                .withBody(loadFileString("krakenIoResponse400.json"))
                );

        try {
            krakenIoClient.imageUrlUpload(ImageUrlUploadRequest.builder(new URL("http://somehost/image")).build());
        } catch (KrakenIoRequestException e) {
            assertThat(e.getFailedUploadResponse().getStatus(), is(400));
            assertThat(e.getFailedUploadResponse().getSuccess(), is(false));
        }
    }

    @Test
    public void testImageUrlUpload_401() throws Exception {
        getMockServerClient()
                .when(
                        request()
                                .withPath("/v1/url")
                                .withHeader(new Header("Content-Type", "application/json"))
                )
                .respond(
                        response()
                                .withStatusCode(401)
                                .withHeader(new Header("Content-Type", "application/json"))
                                .withBody(loadFileString("krakenIoResponse401.json"))
                );

        try {
            krakenIoClient.imageUrlUpload(ImageUrlUploadRequest.builder(new URL("http://somehost/image")).build());
        } catch (KrakenIoRequestException e) {
            assertThat(e.getFailedUploadResponse().getStatus(), is(401));
            assertThat(e.getFailedUploadResponse().getSuccess(), is(false));
        }
    }

    @Test
    public void testImageUrlUpload_403() throws Exception {
        getMockServerClient()
                .when(
                        request()
                                .withPath("/v1/url")
                                .withHeader(new Header("Content-Type", "application/json"))
                )
                .respond(
                        response()
                                .withStatusCode(403)
                                .withHeader(new Header("Content-Type", "application/json"))
                                .withBody(loadFileString("krakenIoResponse403.json"))
                );

        try {
            krakenIoClient.imageUrlUpload(ImageUrlUploadRequest.builder(new URL("http://somehost/image")).build());
        } catch (KrakenIoRequestException e) {
            assertThat(e.getFailedUploadResponse().getStatus(), is(403));
            assertThat(e.getFailedUploadResponse().getSuccess(), is(false));
        }
    }

    @Test
    public void testImageUrlUpload_413() throws Exception {
        getMockServerClient()
                .when(
                        request()
                                .withPath("/v1/url")
                                .withHeader(new Header("Content-Type", "application/json"))
                )
                .respond(
                        response()
                                .withStatusCode(413)
                                .withHeader(new Header("Content-Type", "application/json"))
                                .withBody(loadFileString("krakenIoResponse413.json"))
                );

        try {
            krakenIoClient.imageUrlUpload(ImageUrlUploadRequest.builder(new URL("http://somehost/image")).build());
        } catch (KrakenIoRequestException e) {
            assertThat(e.getFailedUploadResponse().getStatus(), is(413));
            assertThat(e.getFailedUploadResponse().getSuccess(), is(false));
        }
    }

    @Test
    public void testImageUrlUpload_415() throws Exception {
        getMockServerClient()
                .when(
                        request()
                                .withPath("/v1/url")
                                .withHeader(new Header("Content-Type", "application/json"))
                )
                .respond(
                        response()
                                .withStatusCode(415)
                                .withHeader(new Header("Content-Type", "application/json"))
                                .withBody(loadFileString("krakenIoResponse415.json"))
                );

        try {
            krakenIoClient.imageUrlUpload(ImageUrlUploadRequest.builder(new URL("http://somehost/image")).build());
        } catch (KrakenIoRequestException e) {
            assertThat(e.getFailedUploadResponse().getStatus(), is(415));
            assertThat(e.getFailedUploadResponse().getSuccess(), is(false));
        }
    }

    @Test
    public void testImageUrlUpload_422() throws Exception {
        getMockServerClient()
                .when(
                        request()
                                .withPath("/v1/url")
                                .withHeader(new Header("Content-Type", "application/json"))
                )
                .respond(
                        response()
                                .withStatusCode(422)
                                .withHeader(new Header("Content-Type", "application/json"))
                                .withBody(loadFileString("krakenIoResponse422.json"))
                );

        try {
            krakenIoClient.imageUrlUpload(ImageUrlUploadRequest.builder(new URL("http://somehost/image")).build());
        } catch (KrakenIoRequestException e) {
            assertThat(e.getFailedUploadResponse().getStatus(), is(422));
            assertThat(e.getFailedUploadResponse().getSuccess(), is(false));
        }
    }

    @Test
    public void testImageUrlUpload_500() throws Exception {
        getMockServerClient()
                .when(
                        request()
                                .withPath("/v1/url")
                                .withHeader(new Header("Content-Type", "application/json"))
                )
                .respond(
                        response()
                                .withStatusCode(500)
                                .withHeader(new Header("Content-Type", "application/json"))
                                .withBody(loadFileString("krakenIoResponse500.json"))
                );

        try {
            krakenIoClient.imageUrlUpload(ImageUrlUploadRequest.builder(new URL("http://somehost/image")).build());
        } catch (KrakenIoRequestException e) {
            assertThat(e.getFailedUploadResponse().getStatus(), is(500));
            assertThat(e.getFailedUploadResponse().getSuccess(), is(false));
        }
    }

    @Test
    public void testImageUrlUpload_500_WrongFormat() throws Exception {
        getMockServerClient()
                .when(
                        request()
                                .withPath("/v1/url")
                                .withHeader(new Header("Content-Type", "application/json"))
                )
                .respond(
                        response()
                                .withStatusCode(500)
                                .withHeader(new Header("Content-Type", "application/json"))
                                .withBody("{}")
                );

        try {
            krakenIoClient.imageUrlUpload(ImageUrlUploadRequest.builder(new URL("http://somehost/image")).build());
        } catch (KrakenIoException e) {
            assertThat(e, notNullValue());
        }
    }

    private byte[] getMultipartBoundary(Expectation expectation) {
        final String contentType = expectation.getHttpRequest().getFirstHeader("Content-Type");
        final Matcher matcher = MULTIPART_BOUNDARY_PATTERN.matcher(contentType);
        matcher.matches();
        return matcher.group(1).getBytes();
    }

    private String loadFileString(String fileName) throws IOException {
        return Resources.toString(Resources.getResource(DefaultKrakenIoClientFunctionalTest.class, fileName), Charsets.UTF_8);
    }

    private byte[] loadFileBinary(String fileName) throws IOException {
        return Resources.toByteArray(Resources.getResource(DefaultKrakenIoClientFunctionalTest.class, fileName));
    }
}
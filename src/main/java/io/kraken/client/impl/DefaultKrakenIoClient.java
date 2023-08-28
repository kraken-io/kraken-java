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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import io.kraken.client.KrakenIoClient;
import io.kraken.client.exception.KrakenIoException;
import io.kraken.client.exception.KrakenIoRequestException;
import io.kraken.client.model.Auth;
import io.kraken.client.model.AuthWrapper;
import io.kraken.client.model.request.*;
import io.kraken.client.model.response.AbstractUploadResponse;
import io.kraken.client.model.response.FailedUploadResponse;
import io.kraken.client.model.response.SuccessfulUploadCallbackUrlResponse;
import io.kraken.client.model.response.SuccessfulUploadResponse;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.text.MessageFormat;
import java.util.UUID;
import java.util.logging.Level;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
public class DefaultKrakenIoClient implements KrakenIoClient {

    private static final java.util.logging.Logger JERSEY_LOGGER = java.util.logging.Logger.getLogger(DefaultKrakenIoClient.class.getCanonicalName());

    private static final String DEFAULT_BASE_URL = "https://api.kraken.io";
    private static final String DIRECT_UPLOAD_ENDPOINT = "{0}/v1/upload";
    private static final String IMAGE_URL_ENDPOINT = "{0}/v1/url";
    private static final String DATA_PART = "data";
    private static final String UPLOAD_PART = "upload";

    private static final int CLIENT_TIMEOUT = 3000;

    private final jakarta.ws.rs.client.Client client;
    private final String apiKey;
    private final String apiSecret;
    private final String directUploadUrl;
    private final String imageUrl;

    public DefaultKrakenIoClient(String apiKey, String apiSecret) {
        this(apiKey, apiSecret, DEFAULT_BASE_URL, CLIENT_TIMEOUT);
    }

    public DefaultKrakenIoClient(String apiKey, String apiSecret, String baseUrl) {
        this(apiKey, apiSecret, baseUrl, CLIENT_TIMEOUT);
    }

    public DefaultKrakenIoClient(String apiKey, String apiSecret, int timeout) {
        this(apiKey, apiSecret, DEFAULT_BASE_URL, timeout);
    }

    public DefaultKrakenIoClient(String apiKey, String apiSecret, String baseUrl, int timeout) {
        checkNotNull(apiKey, "apiKey must not be null");
        checkArgument(!apiKey.isEmpty(), "apiKey must not be empty");
        checkNotNull(apiSecret, "apiSecret must not be null");
        checkArgument(!apiSecret.isEmpty(), "apiSecret must not be empty");
        checkNotNull(baseUrl, "baseUrl must not be null");
        checkArgument(!baseUrl.isEmpty(), "baseUrl must not be empty");
        checkNotNull(timeout, "timeout must not be null");

        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.directUploadUrl = MessageFormat.format(DIRECT_UPLOAD_ENDPOINT, baseUrl);
        this.imageUrl = MessageFormat.format(IMAGE_URL_ENDPOINT, baseUrl);
        this.client = createClient(createObjectMapper(), timeout);
    }

    private Client createClient(ObjectMapper objectMapper, int timeout) {
        final ClientConfig clientConfig = new ClientConfig();
        clientConfig.property(ClientProperties.FOLLOW_REDIRECTS, true);
        clientConfig.property(ClientProperties.CONNECT_TIMEOUT, timeout);
        clientConfig.property(ClientProperties.READ_TIMEOUT, timeout);
        clientConfig.property(ClientProperties.FEATURE_AUTO_DISCOVERY_DISABLE, true);

        final JacksonJsonProvider jacksonJsonProvider = new JacksonJsonProvider(objectMapper);
        final Client client = ClientBuilder.newClient(clientConfig).register(jacksonJsonProvider).register(MultiPartFeature.class);
        final Feature feature = new LoggingFeature(JERSEY_LOGGER, Level.FINE, LoggingFeature.Verbosity.PAYLOAD_ANY, null);
        client.register(feature);
        return client;
    }

    private ObjectMapper createObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        objectMapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        objectMapper.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);

        return objectMapper;
    }

    @Override
    public SuccessfulUploadResponse directUpload(DirectUploadRequest directUploadRequest) {
        return directUpload(directUploadRequest, new StreamDataBodyPart(UPLOAD_PART, directUploadRequest.getImage(), UUID.randomUUID().toString()));
    }

    @Override
    public SuccessfulUploadResponse directUpload(DirectFileUploadRequest directFileUploadRequest) {
        return directUpload(directFileUploadRequest, new FileDataBodyPart(UPLOAD_PART, directFileUploadRequest.getImage()));
    }

    @Override
    public SuccessfulUploadResponse imageUrlUpload(ImageUrlUploadRequest imageUrlUploadRequest) {
        final WebTarget webTarget = client.target(imageUrl);
        final Response response = webTarget.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(wrapAuth(imageUrlUploadRequest), MediaType.APPLICATION_JSON_TYPE));
        return handleResponse(response);
    }

    @Override
    public SuccessfulUploadCallbackUrlResponse directUpload(DirectUploadCallbackUrlRequest directUploadCallbackUrlRequest) {
        return directCallbackUrlUpload(directUploadCallbackUrlRequest, new StreamDataBodyPart(UPLOAD_PART, directUploadCallbackUrlRequest.getImage(), UUID.randomUUID().toString()));
    }

    @Override
    public SuccessfulUploadCallbackUrlResponse directUpload(DirectFileUploadCallbackUrlRequest directFileUploadCallbackUrlRequest) {
        return directCallbackUrlUpload(directFileUploadCallbackUrlRequest, new FileDataBodyPart(UPLOAD_PART, directFileUploadCallbackUrlRequest.getImage()));
    }

    @Override
    public SuccessfulUploadCallbackUrlResponse imageUrlUpload(ImageUrlUploadCallbackUrlRequest imageUrlUploadCallbackUrlRequest) {
        final WebTarget webTarget = client.target(imageUrl);
        final Response response = webTarget.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(wrapAuth(imageUrlUploadCallbackUrlRequest), MediaType.APPLICATION_JSON_TYPE));
        return handleCallbackUrlResponse(response);
    }

    private AuthWrapper wrapAuth(AbstractUploadRequest abstractUploadRequest) {
        return new AuthWrapper(new Auth(apiKey, apiSecret), abstractUploadRequest);
    }

    private SuccessfulUploadResponse directUpload(AbstractUploadRequest abstractUploadRequest, BodyPart bodyPart) {
        return handleResponse(handleRequest(abstractUploadRequest, bodyPart));
    }

    private SuccessfulUploadCallbackUrlResponse directCallbackUrlUpload(AbstractUploadRequest abstractUploadRequest, BodyPart bodyPart) {
        return handleCallbackUrlResponse(handleRequest(abstractUploadRequest, bodyPart));
    }

    private Response handleRequest(AbstractUploadRequest abstractUploadRequest, BodyPart bodyPart) {
        final WebTarget webTarget = client.target(directUploadUrl);

        final MultiPart multiPart = new MultiPart();
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);

        multiPart.bodyPart(new FormDataBodyPart(DATA_PART, wrapAuth(abstractUploadRequest), MediaType.APPLICATION_JSON_TYPE));
        multiPart.bodyPart(bodyPart);

        return webTarget.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(multiPart, multiPart.getMediaType()));
    }

    private SuccessfulUploadResponse handleResponse(Response response) {
        try {
            final AbstractUploadResponse abstractUploadResponse = response.readEntity(AbstractUploadResponse.class);
            abstractUploadResponse.setStatus(response.getStatus());

            if (response.getStatus() == 200) {
                return (SuccessfulUploadResponse) abstractUploadResponse;
            } else {
                throw new KrakenIoRequestException("Kraken.io request failed", (FailedUploadResponse) abstractUploadResponse);
            }
        } catch (KrakenIoRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new KrakenIoException("Failed to unmarshall response", e);
        }
    }

    private SuccessfulUploadCallbackUrlResponse handleCallbackUrlResponse(Response response) {
        try {
            if (response.getStatus() == 200) {
                return response.readEntity(SuccessfulUploadCallbackUrlResponse.class);
            } else {
                final FailedUploadResponse failedUploadResponse = response.readEntity(FailedUploadResponse.class);
                failedUploadResponse.setStatus(response.getStatus());
                throw new KrakenIoRequestException("Kraken.io request failed", failedUploadResponse);
            }
        } catch (KrakenIoRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new KrakenIoException("Failed to unmarshall response", e);
        }
    }
}

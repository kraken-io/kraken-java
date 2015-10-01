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

import com.google.common.io.Resources;
import io.kraken.client.KrakenIoClient;
import io.kraken.client.model.RGBA;
import io.kraken.client.model.request.DirectFileUploadRequest;
import io.kraken.client.model.resize.FillResize;
import io.kraken.client.model.response.SuccessfulUploadResponse;

import java.io.File;
import java.math.BigDecimal;
import java.net.URISyntaxException;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
public class ManualTest {

    // Enter credentials here
    private static final String API_KEY = "";
    private static final String API_SECRET = "";

    public static void main(String[] args) throws URISyntaxException {

        final KrakenIoClient krakenIoClient = new DefaultKrakenIoClient(API_KEY, API_SECRET);
        final DirectFileUploadRequest directFileUploadRequest = DirectFileUploadRequest.builder(
                new File(Resources.getResource(ManualTest.class, "test.jpg").toURI()))
                .withLossy(true)
                .withResize(new FillResize(150, 100, new RGBA(45, 45, 145, BigDecimal.ONE)))
                .build();

        final SuccessfulUploadResponse successfulUploadResponse = krakenIoClient.directUpload(directFileUploadRequest);
        System.out.println(successfulUploadResponse.getKrakedUrl());
    }
}

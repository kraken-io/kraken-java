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
package io.kraken.client.exception;

import io.kraken.client.model.response.FailedUploadResponse;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
public class KrakenIoRequestException extends KrakenIoException {

    private final FailedUploadResponse failedUploadResponse;

    public KrakenIoRequestException(String message, FailedUploadResponse failedUploadResponse) {
        super(message);
        this.failedUploadResponse = failedUploadResponse;
    }

    public KrakenIoRequestException(String message, Throwable cause, FailedUploadResponse failedUploadResponse) {
        super(message, cause);
        this.failedUploadResponse = failedUploadResponse;
    }

    public FailedUploadResponse getFailedUploadResponse() {
        return failedUploadResponse;
    }
}

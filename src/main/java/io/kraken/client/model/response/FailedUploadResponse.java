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
package io.kraken.client.model.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
public class FailedUploadResponse extends AbstractUploadResponse {

    private final String message;

    @JsonCreator
    public FailedUploadResponse(@JsonProperty("success") Boolean success,
                                @JsonProperty("message") String message) {
        super(success);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        try {
            return "AbstractUploadResponse:" + objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "SuccessfulUploadResponse{" +
                    "success='" + getSuccess() + '\'' +
                    ", status='" + getStatus() + '\'' +
                    ", status='" + getMessage() + '\'' +
                    '}';
        }
    }
}

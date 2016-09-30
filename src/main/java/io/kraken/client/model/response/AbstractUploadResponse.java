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

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.EXISTING_PROPERTY, visible = true, property="success")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SuccessfulUploadResponse.class, name = "true"),
        @JsonSubTypes.Type(value = FailedUploadResponse.class, name = "false")
})
public abstract class AbstractUploadResponse {

    @JsonIgnore
    protected final ObjectMapper objectMapper = new ObjectMapper();

    private final Boolean success;

    @JsonIgnore
    private Integer status;

    @JsonCreator
    protected AbstractUploadResponse(@JsonProperty("success") Boolean success) {
        this.success = success;
    }

    public Boolean getSuccess() {
        return success;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

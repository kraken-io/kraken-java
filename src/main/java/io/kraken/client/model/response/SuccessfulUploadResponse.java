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

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
public class SuccessfulUploadResponse extends AbstractUploadResponse {

    private final String fileName;
    private final Integer originalSize;
    private final Integer krakedSize;
    private final Integer savedBytes;
    private final String krakedUrl;

    @JsonCreator
    public SuccessfulUploadResponse(@JsonProperty("success") Boolean success,
                                    @JsonProperty("file_name") String fileName,
                                    @JsonProperty("original_size") Integer originalSize,
                                    @JsonProperty("kraked_size") Integer krakedSize,
                                    @JsonProperty("saved_bytes") Integer savedBytes,
                                    @JsonProperty("kraked_url") String krakedUrl) {
        super(success);
        this.fileName = fileName;
        this.originalSize = originalSize;
        this.krakedSize = krakedSize;
        this.savedBytes = savedBytes;
        this.krakedUrl = krakedUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public Integer getOriginalSize() {
        return originalSize;
    }

    public Integer getKrakedSize() {
        return krakedSize;
    }

    public Integer getSavedBytes() {
        return savedBytes;
    }

    public String getKrakedUrl() {
        return krakedUrl;
    }
}

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
package io.kraken.client.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kraken.client.model.Convert;
import io.kraken.client.model.Metadata;
import io.kraken.client.model.resize.AbstractResize;

import java.io.File;
import java.net.URL;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
public class DirectFileUploadRequest extends AbstractUploadRequest {

    @JsonIgnore
    private final File image;

    private DirectFileUploadRequest(Boolean webp,
                                    Boolean lossy,
                                    Integer quality,
                                    AbstractResize resize,
                                    Set<Metadata> preserveMeta,
                                    Convert convert,
                                    File image) {
        super(true, webp, lossy, quality, resize, preserveMeta, convert);

        checkNotNull(image, "image must not be null");
        this.image = image;
    }

    public File getImage() {
        return image;
    }

    public static Builder builder(File image) {
        return new Builder(image);
    }

    public static class Builder extends AbstractUploadRequest.Builder<Builder> {
        private final File image;

        private Builder(File image) {
            super(Builder.class);
            this.image = image;
        }

        public DirectFileUploadRequest build() {
            return new DirectFileUploadRequest(
                    webp,
                    lossy,
                    quality,
                    resize,
                    preserveMeta,
                    convert,
                    image
            );
        }
    }
}

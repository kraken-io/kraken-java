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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.kraken.client.model.Convert;
import io.kraken.client.model.Metadata;
import io.kraken.client.model.resize.AbstractResize;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
public abstract class AbstractUploadRequest {

    private final Boolean dev;
    private final Boolean wait;
    private final Boolean webp;
    private final Boolean lossy;
    private final Integer quality;
    private final AbstractResize resize;
    @JsonProperty("preserve_meta")
    private final Set<Metadata> preserveMeta;
    private final Convert convert;

    @JsonCreator
    protected AbstractUploadRequest(Boolean dev,
                                    Boolean wait,
                                    Boolean webp,
                                    Boolean lossy,
                                    Integer quality,
                                    AbstractResize resize,
                                    Set<Metadata> preserveMeta,
                                    Convert convert) {
        checkNotNull(dev, "dev must not be null");
        checkNotNull(wait, "wait must not be null");
        checkNotNull(lossy, "lossy must not be null");
        checkArgument(quality == null || (quality != null && quality >= 1 && quality <= 100), "quality must be between 1-100");
        checkArgument(lossy != null || (lossy == null && quality == null), "quality can only be set if lossy is set");

        this.dev = dev;
        this.wait = wait;
        this.webp = webp;
        this.lossy = lossy;
        this.quality = quality;
        this.resize = resize;
        this.preserveMeta = preserveMeta;
        this.convert = convert;
    }

    public Boolean getDev() {
        return dev;
    }

    public Boolean getWait() {
        return wait;
    }

    public Boolean getWebp() {
        return webp;
    }

    public Boolean getLossy() {
        return lossy;
    }

    public Integer getQuality() {
        return quality;
    }

    public AbstractResize getResize() {
        return resize;
    }

    public Set<Metadata> getPreserveMeta() {
        return preserveMeta;
    }

    public Convert getConvert() {
        return convert;
    }

    protected static class Builder<T extends Builder> {
        protected final Class<T> clazz;

        protected Boolean dev = false;
        protected Boolean webp = false;
        protected Boolean lossy = false;
        protected Integer quality;
        protected AbstractResize resize;
        protected Set<Metadata> preserveMeta = new HashSet<Metadata>();
        protected Convert convert;

        public Builder(Class<T> clazz) {
            this.clazz = clazz;
        }

        public T withLossy(boolean lossy) {
            this.lossy = lossy;
            if (lossy == false) {
                this.quality = null;
            }

            return clazz.cast(this);
        }

        public T withDev(boolean dev) {
            this.dev = dev;
            return clazz.cast(this);
        }

        public T withWebp(boolean webp) {
            this.webp = webp;
            return clazz.cast(this);
        }

        public T withQuality(int quality) {
            this.lossy = true;
            this.quality = quality;
            return clazz.cast(this);
        }

        public T withResize(AbstractResize resize) {
            this.resize = resize;
            return clazz.cast(this);
        }

        public T withPreserveMeta(Metadata metadata) {
            preserveMeta.add(metadata);
            return clazz.cast(this);
        }

        public T withConvert(Convert convert) {
            this.convert = convert;
            return clazz.cast(this);
        }
    }
}

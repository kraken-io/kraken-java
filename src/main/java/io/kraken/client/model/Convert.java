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
package io.kraken.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.kraken.client.serializer.RGBASerializer;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
public class Convert {

    private final ImageFormat format;

    @JsonSerialize(using = RGBASerializer.class)
    private final RGBA background;

    private final Boolean keepExtension;

    @JsonCreator
    public Convert(@JsonProperty("format") ImageFormat format,
                   @JsonProperty("background") RGBA background,
                   @JsonProperty("keep_extension") Boolean keepExtension) {
        this.format = format;
        this.background = background;
        this.keepExtension = keepExtension;
    }

    public ImageFormat getFormat() {
        return format;
    }

    public RGBA getBackground() {
        return background;
    }

    public Boolean getKeepExtension() {
        return keepExtension;
    }
}

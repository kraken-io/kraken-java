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
package io.kraken.client.model.resize;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.kraken.client.model.RGBA;
import io.kraken.client.model.Strategy;
import io.kraken.client.serializer.RGBASerializer;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
public class FillResize extends AbstractDimensionBasedResize {

    @JsonSerialize(using = RGBASerializer.class)
    private final RGBA background;

    @JsonCreator
    public FillResize(@JsonProperty("width") Integer width,
                      @JsonProperty("height") Integer height,
                      @Nullable @JsonProperty("background") RGBA background) {
        super(Strategy.FILL, width, height);
        this.background = background;
    }

    public RGBA getBackground() {
        return background;
    }
}

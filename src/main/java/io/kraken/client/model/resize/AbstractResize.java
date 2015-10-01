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
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.kraken.client.model.Strategy;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.EXISTING_PROPERTY, visible = true, property="strategy")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FitResize.class, name = "fit"),
        @JsonSubTypes.Type(value = ExactResize.class, name = "exact"),
        @JsonSubTypes.Type(value = AutoResize.class, name = "auto"),
        @JsonSubTypes.Type(value = CropResize.class, name = "crop"),
        @JsonSubTypes.Type(value = FillResize.class, name = "fill"),
        @JsonSubTypes.Type(value = LandscapeResize.class, name = "landscape"),
        @JsonSubTypes.Type(value = PortraitResize.class, name = "portrait"),
        @JsonSubTypes.Type(value = SquareResize.class, name = "square")
})
public abstract class AbstractResize {

    private final Strategy strategy;

    @JsonCreator
    protected AbstractResize(@JsonProperty("strategy") Strategy strategy) {
        this.strategy = strategy;
    }

    public Strategy getStrategy() {
        return strategy;
    }
}

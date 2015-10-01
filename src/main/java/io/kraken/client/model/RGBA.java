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

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
public class RGBA {

    private final Integer red;
    private final Integer green;
    private final Integer blue;
    private final BigDecimal alpha;

    @JsonCreator
    public RGBA(@JsonProperty("red") Integer red,
                @JsonProperty("green") Integer green,
                @JsonProperty("blue") Integer blue,
                @JsonProperty("alpha") BigDecimal alpha) {
        checkNotNull(red, "red must not be null");
        checkNotNull(green, "green must not be null");
        checkNotNull(blue, "blue must not be null");
        checkNotNull(alpha, "alpha must not be null");
        checkArgument(red >= 0 && red <= 256, "red must be between 0-256");
        checkArgument(green >= 0 && green <= 256, "green must be between 0-256");
        checkArgument(blue >= 0 && blue <= 256, "blue must be between 0-256");
        checkArgument(alpha.compareTo(BigDecimal.valueOf(0.0)) > -1 && alpha.compareTo(BigDecimal.valueOf(1.0)) < 1, "red must be between 0-1");

        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public Integer getRed() {
        return red;
    }

    public Integer getGreen() {
        return green;
    }

    public Integer getBlue() {
        return blue;
    }

    public BigDecimal getAlpha() {
        return alpha;
    }
}

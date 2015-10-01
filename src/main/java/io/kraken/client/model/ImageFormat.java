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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Emir Dizdarevic
 * @since 1.0.0
 */
public enum ImageFormat {
    JPEG("jpeg"),
    PNG("png"),
    GIF("gif");

    private static Map<String, ImageFormat> REVERSE_LOOKUP = new HashMap<String, ImageFormat>();
    static {
        for (ImageFormat strategy : values()) {
            REVERSE_LOOKUP.put(strategy.getValue(), strategy);
        }
    }

    private final String value;

    ImageFormat(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ImageFormat fromString(String value) {
        return REVERSE_LOOKUP.get(value);
    }

    @Override
    public String toString() {
        return value;
    }
}

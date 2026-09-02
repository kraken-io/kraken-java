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
package io.kraken.client.internal;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

/**
 * The replacement for Guava's Preconditions must behave exactly as Guava's did,
 * because 32 call sites across the client rely on it. Guava is still on the test
 * classpath, so each case is asserted against the real thing rather than against
 * what it is assumed to do.
 */
public class PreconditionsTest {

    /**
     * Guava is a test dependency pinned to a patched release, and those require
     * Java 8. On a Java 7 JVM it cannot load, so the comparisons are skipped
     * there — the behaviour tests below still run, which is the point: this
     * class must work on the oldest runtime the client supports.
     */
    private static boolean guavaLoads;

    @BeforeClass
    public static void detectGuava() {
        try {
            Class.forName("com.google.common.base.Preconditions");
            guavaLoads = true;
        } catch (Throwable t) {
            guavaLoads = false;
        }
    }

    private static void requireGuava() {
        assumeTrue("guava needs Java 8; skipping the comparison", guavaLoads);
    }

    @Test
    public void checkNotNullReturnsTheReferenceItWasGiven() {
        String value = "kraken";
        assertSame(value, Preconditions.checkNotNull(value));
        assertSame(value, Preconditions.checkNotNull(value, "message"));
    }

    @Test
    public void checkNotNullThrowsTheSameTypeAndMessageAsGuava() {
        requireGuava();
        NullPointerException ours = null;
        NullPointerException theirs = null;
        try {
            Preconditions.checkNotNull(null, "apiKey must not be null");
        } catch (NullPointerException e) {
            ours = e;
        }
        try {
            com.google.common.base.Preconditions.checkNotNull(null, "apiKey must not be null");
        } catch (NullPointerException e) {
            theirs = e;
        }
        if (ours == null || theirs == null) {
            fail("both implementations must throw");
        }
        assertEquals(theirs.getClass(), ours.getClass());
        assertEquals(theirs.getMessage(), ours.getMessage());
    }

    @Test
    public void checkNotNullWithoutMessageMatchesGuava() {
        requireGuava();
        NullPointerException ours = null;
        NullPointerException theirs = null;
        try {
            Preconditions.checkNotNull(null);
        } catch (NullPointerException e) {
            ours = e;
        }
        try {
            com.google.common.base.Preconditions.checkNotNull(null);
        } catch (NullPointerException e) {
            theirs = e;
        }
        assertEquals(theirs.getClass(), ours.getClass());
        assertEquals(theirs.getMessage(), ours.getMessage());
    }

    @Test
    public void checkArgumentPassesWhenTheConditionHolds() {
        Preconditions.checkArgument(true);
        Preconditions.checkArgument(true, "never thrown");
    }

    @Test
    public void checkArgumentThrowsTheSameTypeAndMessageAsGuava() {
        requireGuava();
        IllegalArgumentException ours = null;
        IllegalArgumentException theirs = null;
        try {
            Preconditions.checkArgument(false, "apiKey must not be empty");
        } catch (IllegalArgumentException e) {
            ours = e;
        }
        try {
            com.google.common.base.Preconditions.checkArgument(false, "apiKey must not be empty");
        } catch (IllegalArgumentException e) {
            theirs = e;
        }
        if (ours == null || theirs == null) {
            fail("both implementations must throw");
        }
        assertEquals(theirs.getClass(), ours.getClass());
        assertEquals(theirs.getMessage(), ours.getMessage());
    }

    @Test
    public void checkArgumentWithoutMessageMatchesGuava() {
        requireGuava();
        IllegalArgumentException ours = null;
        IllegalArgumentException theirs = null;
        try {
            Preconditions.checkArgument(false);
        } catch (IllegalArgumentException e) {
            ours = e;
        }
        try {
            com.google.common.base.Preconditions.checkArgument(false);
        } catch (IllegalArgumentException e) {
            theirs = e;
        }
        assertEquals(theirs.getClass(), ours.getClass());
        assertEquals(theirs.getMessage(), ours.getMessage());
    }

    @Test
    public void aNonStringMessageIsStringifiedTheSameWay() {
        requireGuava();
        NullPointerException ours = null;
        NullPointerException theirs = null;
        try {
            Preconditions.checkNotNull(null, 42);
        } catch (NullPointerException e) {
            ours = e;
        }
        try {
            com.google.common.base.Preconditions.checkNotNull(null, 42);
        } catch (NullPointerException e) {
            theirs = e;
        }
        assertEquals(theirs.getMessage(), ours.getMessage());
    }

    @Test
    public void checkNotNullThrowsNullPointerExceptionWithTheGivenMessage() {
        try {
            Preconditions.checkNotNull(null, "apiKey must not be null");
            fail("expected NullPointerException");
        } catch (NullPointerException e) {
            assertEquals("apiKey must not be null", e.getMessage());
        }
    }

    @Test
    public void checkArgumentThrowsIllegalArgumentExceptionWithTheGivenMessage() {
        try {
            Preconditions.checkArgument(false, "apiKey must not be empty");
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("apiKey must not be empty", e.getMessage());
        }
    }
}

package org.ikayzo.sdl;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

/**
 * @author Samael Bate (singingbush)
 *         created on 13/07/2017
 */
public class DataTypesTest {

    @Test // "String \"with escape support\""
    public void testStringsWithEscapeSupport() {
        final Object value = SDL.value("\"String \\\"with escape support\\\"\"");
        assertNotNull(value);
        assertTrue("Should be an String", value.getClass().isAssignableFrom(String.class));
        final String stringValue = String.class.cast(value);
        assertEquals("String \\\"with escape support\\\"", stringValue);
    }

    @Test // `String "without escape support"`
    public void testStringsWithoutEscapeSupport() {
        final Object value = SDL.value("`String \"without escape support\"`");
        assertNotNull(value);
        assertTrue("Should be an String", value.getClass().isAssignableFrom(String.class));
        final String stringValue = String.class.cast(value);
        assertEquals("String \"without escape support\"", stringValue);
    }

    // Numbers

    @Test // 10 == 32-bit numbers (Integer)
    public void test32bitIntegers() {
        final Object value = SDL.value("10");
        assertNotNull(value);
        assertTrue("Should be an Integer", value.getClass().isAssignableFrom(Integer.class));
    }

    @Test // 10.5 == 64-bit floating point numbers (Double)
    public void test64bitDouble() {
        final Object value = SDL.value("10.5");
        assertNotNull(value);
        assertTrue("Should be an Double", value.getClass().isAssignableFrom(Double.class));
    }

    @Test // 10L == 64-bit numbers (Long)
    public void test64bitLong() {
        final Object value = SDL.value("10L");
        assertNotNull(value);
        assertTrue("Should be an Long", value.getClass().isAssignableFrom(Long.class));
    }

    @Test // 10BD == 128-bit numbers (BigDecimal)
    public void test128bitBigDecimal() {
        final Object value = SDL.value("10BD");
        assertNotNull(value);
        assertTrue("Should be an BigDecimal", value.getClass().isAssignableFrom(BigDecimal.class));
    }

    @Test // 10.5f == 32-bit float
    public void test32bitFloat() {
        final Object value = SDL.value("10.5f");
        assertNotNull(value);
        assertTrue("Should be an Float", value.getClass().isAssignableFrom(Float.class));
    }

    @Test
    public void testBooleans() {
        assertTrue(sdlBoolean("true"));
        assertTrue(sdlBoolean("on"));

        assertFalse(sdlBoolean("false"));
        assertFalse(sdlBoolean("off"));
    }

    @Test // "null" should be parsed as a null
    public void testNull() {
        final Object value = SDL.value("null");
        assertNull(value);
    }

    @Test // 2015/12/06 12:00:00.000-UTC // Date/time value (UTC timezone)
    public void testDateTimeWithZoneInfo() {
        final Object value = SDL.value("2015/12/06 12:00:00.000-UTC");
        assertNotNull(value);
        assertTrue("Should be an GregorianCalendar", value.getClass().isAssignableFrom(GregorianCalendar.class));
    }

    @Test // 2015/12/06 12:00:00.000 // Date/time value (local timezone)
    public void testDateTimeLocal() {
        final Object value = SDL.value("2015/12/06 12:00:00.000");
        assertNotNull(value);
        assertTrue("Should be an GregorianCalendar", value.getClass().isAssignableFrom(GregorianCalendar.class));
    }

    @Test // 2015/12/06 // Date value
    public void testDate() {
        final Object value = SDL.value("2015/12/06");
        assertNotNull(value);
        assertTrue("Should be an GregorianCalendar", value.getClass().isAssignableFrom(GregorianCalendar.class));
    }

    @Test // time
    public void testTimeSDLTimeSpan() {
        final Object value = SDL.value("12:14:34");
        assertNotNull(value);
        assertTrue("Should be an SDLTimeSpan", value.getClass().isAssignableFrom(SDLTimeSpan.class));
    }

    @Test // time
    public void testTimeSDLTimeSpan_withMillis() {
        final Object value = SDL.value("12:14:34.123");
        assertNotNull(value);
        assertTrue("Should be an SDLTimeSpan", value.getClass().isAssignableFrom(SDLTimeSpan.class));
    }

    @Test // time
    public void testTimeSDLTimeSpan_DaysHoursMinsSecs() {
        final Object value = SDL.value("2d:12:14:34");
        assertNotNull(value);
        assertTrue("Should be an SDLTimeSpan", value.getClass().isAssignableFrom(SDLTimeSpan.class));
    }

    @Test // base 64
    public void testBinaryData() {
        final Object value = SDL.value("[sdf789GSfsb2+3324sf2]");
        assertNotNull(value);
        assertTrue("Should be an byte[]", value.getClass().isAssignableFrom(byte[].class));
    }

    private Boolean sdlBoolean(final String text) {
        final Object value = SDL.value(text);
        assertNotNull(value);
        assertTrue("Should be an Boolean", value.getClass().isAssignableFrom(Boolean.class));
        return Boolean.class.cast(value);
    }

}

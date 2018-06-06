package com.domain;

import com.singingbush.sdl.Parser;
import com.singingbush.sdl.SDLParseException;
import com.singingbush.sdl.Tag;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * These tests are in a different package to 'com.singingbush.sdl' to ensure that the API makes sense
 * @author Samael Bate (singingbush)
 * created on 06/06/18
 */
public class SdlangTest {

    @Test
    public void testParserConstructorWithReader() throws IOException, SDLParseException {
        final Reader reader = new StringReader("employee \"Mr Smith\" email=\"smith@domain.org\" active=true // comments");

        final List<Tag> tags = new Parser(reader).parse();

        assertEquals(1, tags.size());
        final Tag tag = tags.get(0);
        assertEquals("employee", tag.getName());
        assertEquals("Mr Smith", String.class.cast(tag.getValue()));
        assertEquals("Mr Smith", tag.getValue(String.class));
        assertEquals(2, tag.getAttributes().size());
        assertEquals("smith@domain.org", tag.getAttribute("email"));
        assertEquals(true, tag.getAttribute("active"));
    }

    @Test
    public void testParserConstructorWithString() throws IOException, SDLParseException {
        assertTrue(new Parser("").parse().isEmpty());

        final List<Tag> tags = new Parser("matrix {\n 8 64 87; 31 34 18 \n}").parse();

        assertEquals(1, tags.size());
        final Tag tag = tags.get(0);
        assertEquals("matrix", tag.getName());
        assertEquals(2, tag.getChildren().size());

        assertEquals(3, tag.getChildren().get(0).getValues().size());
        assertEquals(3, tag.getChildren().get(1).getValues().size());
    }
}

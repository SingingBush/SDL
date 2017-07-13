package org.ikayzo.sdl;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Samael Bate (singingbush)
 *         created on 14/07/2017
 */
public class ParserTest {

    @Test
    public void testComments() throws IOException, SDLParseException {
        final InputStreamReader inputStream = loadTestResource("comments.sdl");

        final List<Tag> tags = new Parser(inputStream).parse();

        assertEquals("There should be 'tag bar=false'", 1, tags.size());
        assertEquals("tag", tags.get(0).getName());
        assertFalse("tag bar should be false", (Boolean) tags.get(0).getAttribute("bar"));
    }

    @Test
    public void testDatatypes() throws IOException, SDLParseException {
        final InputStreamReader inputStream = loadTestResource("datatypes.sdl");

        final List<Tag> tags = new Parser(inputStream).parse();

        assertFalse(tags.isEmpty());
        assertEquals(19, tags.size());
    }

    @Test
    public void testDetails() throws IOException, SDLParseException {
        final InputStreamReader inputStream = loadTestResource("details.sdl");

        final List<Tag> tags = new Parser(inputStream).parse();

        assertFalse(tags.isEmpty());
        assertEquals(7, tags.size());
        final Tag tag = new Tag("title");
        tag.addValue("Some title");
        assertTrue(tags.contains(tag));

        assertTrue(tags.contains(new Tag("this-is_a.valid$tag-name")));

        // todo: fix the namespaced tags
//		assertTrue(tags.contains(new Tag("renderer:options")));
//		assertTrue(tags.contains(new Tag("physics:options")));
    }

    @Test
    public void testExample() throws IOException, SDLParseException {
        final InputStreamReader inputStream = loadTestResource("example.sdl");

        final List<Tag> tags = new Parser(inputStream).parse();

        assertFalse(tags.isEmpty());
        assertEquals(6, tags.size());
    }

    @Test
    public void testParserWithString() throws IOException, SDLParseException {
        final List<Tag> tags = new Parser("author \"Peter Parker\" email=\"peter@example.org\" active=true").parse();
        assertFalse(tags.isEmpty());
        assertEquals(1, tags.size());
        assertEquals("author", tags.get(0).getName());
    }

    private InputStreamReader loadTestResource(final String testResourceFile) throws UnsupportedEncodingException {
        final InputStream testData = this.getClass()
                .getClassLoader()
                .getResourceAsStream(testResourceFile);
        return new InputStreamReader(testData, "UTF-8");
    }
}

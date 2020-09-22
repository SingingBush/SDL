package com.domain;

import com.singingbush.sdl.Interpreter;
import com.singingbush.sdl.SDLParseException;
import com.singingbush.sdl.Tag;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Samael Bate (singingbush)
 * created on 23/09/2020
 */
public class InterpreterTest {

    @Test
    public void testInterperatorBasicUsage() throws IOException, SDLParseException {
        final Reader reader = new StringReader("employee \"Mr Smith\" email=\"smith@domain.org\" active=true // comments");
        final List<Tag> tags = new Interpreter().read(reader);

        final Tag root = new Tag("root");
        for(final Tag tag : tags) {
            root.addChild(tag);
        }
        System.out.println(root);

        assertEquals(1, tags.size());
        assertEquals("employee", tags.get(0).getName());
        assertEquals("Mr Smith", tags.get(0).getValue());
        assertEquals("smith@domain.org", tags.get(0).getAttribute("email"));
    }

    @Ignore // cannot pass until new features implemented (like URL and version)
    @Test
    public void testInterperatorFullUsage() throws IOException, SDLParseException {
        final InputStreamReader reader = loadTestResource("sdl3_tests.sdl");
        final List<Tag> tags = new Interpreter().read(reader);

        final Tag root = new Tag("root");
        for(final Tag tag : tags) {
            root.addChild(tag);
        }
        System.out.println(root);

    }

    private InputStreamReader loadTestResource(final String testResourceFile) throws UnsupportedEncodingException {
        final InputStream testData = this.getClass()
            .getClassLoader()
            .getResourceAsStream(testResourceFile);
        return new InputStreamReader(testData, "UTF-8");
    }
}

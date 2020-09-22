package com.singingbush.sdl;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Leuck
 */
public class Interpreter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("y/M/d");
    // private static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("y/M/d@H:m:s.S/V");

    private SDLLexer lexer;
    private SDLParser parser;

    public Interpreter() { }

//    public SDLParser getParser() {	return parser; }
//    public SDLLexer getLexer() { return lexer; }
//
//    public void setLexer(SDLLexer lexer) { this.lexer = lexer; }
//    public void setParser(SDLParser parser) { this.parser = parser; }

    public static void main(String... args) throws Exception {

    }

    public List<Tag> read(final Reader reader) throws IOException, SDLParseException {
        this.lexer = new SDLLexer(CharStreams.fromReader(reader));
        this.parser = new SDLParser(new CommonTokenStream(this.lexer));

        this.parser.setBuildParseTree(true);

        SDLParser.TagListContext tagListCtx = this.parser.tagList();
        final List<Tag> tags = new ArrayList<>();

        if(tagListCtx != null && tagListCtx.getChildCount() > 0) {
            int childCount = tagListCtx.getChildCount();

            for(int i = 0; i < childCount; i++) {
                final ParseTree child = tagListCtx.getChild(i);
                if(child instanceof SDLParser.TagContext) {
                    tags.add(makeTag((SDLParser.TagContext)child));
                }
            }
        }

        return tags;
    }

    public List<Tag> read(String code) throws SDLParseException {
        try {
            return read(new StringReader(code));
        } catch(IOException e) {
            // can't happen
            e.printStackTrace();
            return null;
        }
    }

    private Tag makeTag(@Nullable SDLParser.TagContext tree) throws SDLParseException {
        @Nullable final SDLParser.NsNameContext nsNameCtx = tree != null ? tree.nsName() : null;

        String namespace = "", name = "root";

        if(nsNameCtx != null) {
            if(nsNameCtx.COLON() != null) {
                namespace = nsNameCtx.ID(0).getText().trim();
                name = nsNameCtx.ID(1).getText().trim();
            } else {
                name = nsNameCtx.ID(0).getText().trim();
            }
        }

        final Tag tag = new Tag(namespace, name);

        @Nullable final SDLParser.ValueListContext valuesCtx = tree != null ? tree.valueList() : null;

        if(valuesCtx != null) {
            for(SDLParser.ValueContext vc : valuesCtx.value()) {
                tag.addValue(makeValue(vc));
            }
        }

        @Nullable final SDLParser.AttributeListContext attsCtx = tree != null ? tree.attributeList() : null;

        if(attsCtx != null) {
            for(SDLParser.AttributeContext att : attsCtx.attribute()) {
                final SDLParser.NsNameContext attNSNameCtx = att.nsName();
                String attNamespace = "", attName = "";

                if(attNSNameCtx != null) {
                    if(attNSNameCtx.COLON()!=null) {
                        attNamespace = attNSNameCtx.ID(0).getText().trim();
                        attName = attNSNameCtx.ID(1).getText().trim();
                    } else {
                        attName = attNSNameCtx.ID(0).getText().trim();
                    }
                }

                tag.setAttribute(attNamespace, attName, makeValue(att.value()));
            }
        }

        // Adding child tags
        @Nullable final SDLParser.TagListContext tagListContext = tree != null ? tree.tagList() : null;

        if(tagListContext != null) {
            int tagCount = tagListContext.getChildCount();
            for(int i=0; i< tagCount; i++) {
                tag.addChild(makeTag(tagListContext.tag(i)));
            }
        }

        return tag;
    }

    private SdlValue makeValue(SDLParser.ValueContext valueContext) throws SDLParseException {

        String text = valueContext.getText();

        //// Strings --- ---

        if(valueContext.StringLiteral() != null) return new SdlValue(formatString(text), SdlType.STRING);

        // handles naked strings
        if(valueContext.ID() != null) return new SdlValue(text, SdlType.STRING_MULTILINE);

        //// Numbers --- ---

        if(valueContext.IntegerLiteral() != null) return new SdlValue(Integer.valueOf(text), SdlType.NUMBER);

        if(valueContext.LongLiteral() != null) return new SdlValue(Long.valueOf(text), SdlType.NUMBER);

        if(valueContext.RealLiteral() != null) {
            char lastChar = text.charAt(text.length()-1);
            if(lastChar == 'f' || lastChar == 'F') return new SdlValue(Float.valueOf(text), SdlType.NUMBER);
            if(lastChar == 'm' || lastChar == 'M') return new SdlValue(new BigDecimal(text), SdlType.NUMBER);
            return new SdlValue(Double.valueOf(text), SdlType.NUMBER);
        }

        if(valueContext.BinLiteral() != null) {
            System.err.println("binary literal is not implemented yet");
            //return Integer.parseInt(text.substring(2), 2);
        }

        if(valueContext.HexLiteral() != null) {
            System.err.println("hex literal is not implemented yet");
            //return Integer.parseInt(text.substring(2), 16);
        }

        //// Booleans --- ---

        if(valueContext.TRUE() != null) return new SdlValue(Boolean.TRUE, SdlType.BOOLEAN);

        if(valueContext.FALSE() != null) return new SdlValue(Boolean.FALSE, SdlType.BOOLEAN);

        //// Other simple types --- ---

        if(valueContext.NULL() != null) return new SdlValue(null, SdlType.NULL);

        if(valueContext.CharLiteral() != null) return new SdlValue(text.charAt(1), SdlType.CHARACTER);

        if(valueContext.URL() != null) {
            System.err.println("URL is not implemented yet");
//            try {
//                // todo: return new SdlValue(new URL(text), SdlType.URL);
//            } catch (MalformedURLException e) {
//                // already parsed - should never happen
//                throw new SDLParseException("Malformed URL", -1, -1, e);
//            }
        }

        if(valueContext.Version() != null) {
            System.err.println("Version is not implemented yet");

//            String[] ints = text.split("\\.");
//            int major = Integer.parseInt(ints[0]), minor = Integer.parseInt(ints[1]), micro = Integer.parseInt(ints[2]);
//            return new SdlValue(new Version(major, minor, micro, ints.length == 4 ? ints[3] : null), SdlType.VERSION);
        }

        //// TODO: Bin64 --- ---

        //// TODO: Duration --- ---

        //// DateTime --- ---
        // Can be a LocalDate, LocalDateTime or ZonedDateTime

        SDLParser.DateTimeContext dateTimeCtx = valueContext.dateTime();
        if(dateTimeCtx!=null) {

            final TerminalNode timeNode = dateTimeCtx.Time();

            if(timeNode==null) {
                return new SdlValue(LocalDate.parse(text, DATE_FORMATTER), SdlType.DATE);
            } else {
                String timeWithZone = timeNode.getText().substring(1);
                return new SdlValue(
                    makeDateTime(LocalDate.parse(dateTimeCtx.Date().getText(), DATE_FORMATTER), timeWithZone),
                    SdlType.DATETIME);
            }
        }

        //// TODO: Range --- ---

        //// List --- ---

        SDLParser.ListContext listCtx = valueContext.list();
        if(listCtx != null) {
            System.err.println("lists are not implemented yet");
            //return makeList(listCtx);
        }

        //// Map --- ---

        SDLParser.MapContext mapContext = valueContext.map();
        if(mapContext != null) {
            System.err.println("maps are not implemented yet");
            //return makeMap(mapContext);
        }

        throw new Error("Unknown literal type: " + text);
    }

    /**
     * Returns a LocalDateTime or ZonedDateTime if the timezone is present
     *
     * TODO: Test, fix "z" zone and other mappings. Check strange offsets in tests.
     */
    private Object makeDateTime(LocalDate date, String timeWithZone) {
        int i=0;
        for(; i<timeWithZone.length(); i++) {
            char c = timeWithZone.charAt(i);
            if(c!=':' && c!='.' && c!='_' && !(c>='0' && c<='9')) break;
        }
        String timeString = timeWithZone.substring(0, i).replace("_", "");
        if(timeString.charAt(1)==':')
            timeString = "0" + timeString;
        if(timeString.charAt(timeString.length()-2)==':')
            timeString = timeString + "0";

        String zone = timeWithZone.substring(i);
        if(zone.isEmpty()) {
            zone = null;
        } else if(zone.startsWith("/")) {
            zone = zone.substring(1);
        }

        final LocalTime time = LocalTime.parse(timeString);

        if(zone == null)
            return LocalDateTime.of(date, time);

        return ZonedDateTime.of(date, time, ZoneId.of(zone, ZoneId.SHORT_IDS));
    }

    private List makeList(SDLParser.ListContext listCtx) throws SDLParseException {
        List list = new ArrayList();

        for(SDLParser.ValueContext val: listCtx.value()) {
            list.add(makeValue(val));
        }

        return list;
    }

    private Map makeMap(SDLParser.MapContext mapCtx) throws SDLParseException {
        Map map = new HashMap();

        for(SDLParser.PairContext pair:mapCtx.pair()) {
            map.put(makeValue(pair.value(0)), makeValue(pair.value(1)));
        }

        return map;
    }

    private String formatString(String text) {
        // TODO: Handle raw Strings `text`, @"text" and @"""texr"""

        if(text.startsWith("\"\"\"")) {
            text = text.substring(3, text.length()-3);
            if(text.startsWith("\n"))
                text = text.substring(1);
        } else if(text.charAt(0) == '"') {
            text = text.substring(1, text.length()-1);
        } else if(text.charAt(0) == '`') {
            text = text.substring(1, text.length()-1);
        }

        return text;
    }

}

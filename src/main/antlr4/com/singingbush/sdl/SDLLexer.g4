lexer grammar SDLLexer;

/**
 * SDL3 Lexer
 *
 * @author Daniel Leuck
 */

 channels {
   WHITESPACE,
   COMMENTS
 }

NULL: 'null' | 'nil';

TRUE: 'true' | 'on';
FALSE: 'false' | 'off';

// Note: This requires a "protocol:/" at the beginning, which excludes URLs such as jar:file:/home/duke/duke.jar!
// The trainling':/' is necessary to disabiguate protocol: and namespace:
URL: [a-zA-Z][a-zA-Z0-9.+\-]* ':' '/' [a-zA-Z0-9.+\-_~:/?#[\]@!$&'()*;,%=]+;

// Numbers --- ---

// SECTION: literals

fragment DecDigit: '0'..'9';
fragment DecDigitNoZero: '1'..'9';
fragment DecDigitOrSeparator: DecDigit | '_';

fragment DecDigits
    : DecDigit DecDigitOrSeparator* DecDigit
    | DecDigit
    ;

fragment DoubleExponent: [eE] [+-]? DecDigits;

RealLiteral
    : '-'? (FloatLiteral | DoubleLiteral | DecimalLiteral)
    ;

FloatLiteral
    : DoubleLiteral [fF]
    | DecDigits [fF]
    ;

DoubleLiteral
    : DecDigits? '.' DecDigits DoubleExponent?
    | DecDigits DoubleExponent
    ;

DecimalLiteral
    : DoubleLiteral [mM]
    | DecDigits [mM]
    ;

IntegerLiteral
    : '-'? (DecDigitNoZero DecDigitOrSeparator* DecDigit | DecDigit)
    ;

fragment HexDigit: [0-9a-fA-F];
fragment HexDigitOrSeparator: HexDigit | '_';

HexLiteral
    : '0' [xX] HexDigit HexDigitOrSeparator* HexDigit
    | '0' [xX] HexDigit
    ;

fragment BinDigit: [01];
fragment BinDigitOrSeparator: BinDigit | '_';

BinLiteral
    : '0' [bB] BinDigit BinDigitOrSeparator* BinDigit
    | '0' [bB] BinDigit
    ;

LongLiteral
    : (IntegerLiteral | HexLiteral | BinLiteral) 'L'
    ;

// Version --- ---

Version: IntegerLiteral '.' IntegerLiteral '.' IntegerLiteral ('.' IntegerLiteral)? ('.' ID)?;

// String --- ---

StringLiteral
   : ( '@'?  ( BlockStringLiteral | SimpleString ) ) | RawBlockStringLiteral
   ;

SimpleString: '"' (Esc | SafeCodePoint)* '"';

BlockStringLiteral
   : '"""' ([\t\r\n] | '""' ~'"' | '"' ~'"' | Esc | SafeCodePoint)* '"""'
   ;

RawBlockStringLiteral
   : '`' ('\\' '`' | ~'`')* '`';

fragment EmptyString: '""';

////
fragment UniCharacterLiteral
    : '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

fragment EscapedIdentifier
    : '\\' ('t' | 'b' | 'r' | 'n' | '\'' | '"' | '\\' | '$')
    ;

fragment Esc
    : UniCharacterLiteral
    | EscapedIdentifier
    ;

fragment Unicode // TODO - Add six HEX version
   : 'u' HexDigit HexDigit HexDigit HexDigit
   ;

fragment SafeCodePoint
   : ~ ["\\\u0000-\u001F]
   ;

fragment CharSafeCodePoint
   : ~ ['\\\u0000-\u001F]
   ;

CharLiteral: '\'' (Esc | CharSafeCodePoint) '\'';

// End String --- ---

// Date, Time and Duration

fragment DAYS: 'day';
fragment HOURS: 'h';
fragment MINUTES: 'min';
fragment SECONDS: 's';

// If days are included the label "days" is required. All others are optional.
// We can't use the unit durations for other segments because the labels are optional.
CompoundDuration: '-'? (DayDuration ':')? DecDigits HOURS? ':' DecDigits MINUTES? ':'
          DecDigits ('.' DecDigits DoubleExponent?)? SECONDS?;

fragment Number: IntegerLiteral | DoubleLiteral;

DayDuration: Number 'day';
HourDuration: Number 'h';
MinuteDuration: Number 'min';
SecondDuration: Number 's';
MillisecondDuration: Number 'ms';
NanosecondDuration: Number 'ns';

/* Moved to the parser
DateTime:
    '-'? DecDigits '/' DecDigit DecDigit? '/' DecDigit DecDigit? // Date
    ('@' DecDigits HOURS? ':' DecDigits MINUTES? (':' DecDigits ('.' DecDigits DoubleExponent?)? SECONDS?)?)? // Time
    ('/' (TimeZone | GMT))?; // TimeZone or GMT offset
*/

// Example: 1980/5/23@12:30:15.123_534_623/PST
Date: '-'? DecDigits '/' DecDigit DecDigit? '/' DecDigit DecDigit?;
// should also support "T" for time like ISO-8601?
Time: AT DecDigits':' DecDigits (':' DecDigits ('.' DecDigits DoubleExponent?)?)? TimeZone?;
fragment TimeZone: '/'? ([a-zA-Z]+) ([+\-] DecDigit DecDigit? (':' DecDigit DecDigit)?)?;

// Range Operators
InclusiveRangeOp: '...';
ExclusiveRangeOp: '<.<';
ExclusiveLeftOp: '<..';
ExclusiveRightOp: '..<';

// Punctuation
DOT: '.';
COLON: ':';
SEMICOLON: ';';
EQUALS: '=';
OPEN: '{';
CLOSE: '}';
LPAREN: '(';
RPAREN: ')';
LSQUARE: '[';
RSQUARE: ']';
SLASH: '/';
DASH: '-';
AT: '@';
PLUS: '+';
UNDERSCORE: '_';

// Encodings --- ---

BASE64: 'base64';

// Complies with "The Base64 Alphabet" as specified in Table 1 of RFC 4648
BASE64_DATA: '(' [A-Za-z0-9+=/ \t\r\n]* ')'; // '(' [ \t\r\n]* ([A-Za-z0-9+=/]* [ \t\r\n]*)* ')';

// Identifiers --- ---

fragment IDStart: [\p{Alpha}\p{General_Category=Other_Letter}\p{Emoji_Presentation}];

// Note: I'm not sure if "." should be included here. We need $ and _ for compatibility with other languages
// (e.g. XML, Java, C#, etc.)
fragment IDChar: [$\-\p{Alnum}\p{General_Category=Other_Letter}\p{Emoji}\p{Join_Control}\p{Variation_Selector}_];

// IDs can start with any unicode letter or emoji followed by any unicode letter, number, emoji, $, -, or _
ID: IDStart IDChar*;

// COMMENTS --- ---

BlockComment: '/*' ( BlockComment | . )*? '*/' -> skip; //-> channel(COMMENTS);

LineComment: ('#' | '//') ~[\u000A\u000D]* -> skip; //-> channel(COMMENTS);

// WHITE_SPACE --- ---
WS: ([ \t\r] | '\\' '\n')+ -> skip;
NL: ('\n' | ';')+;
// NL: (('\n' | ';') WS* )+ ('\n' | ';')*;

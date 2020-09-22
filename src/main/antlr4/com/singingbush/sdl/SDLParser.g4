parser grammar SDLParser;

options { tokenVocab = SDLLexer; }

/**
 * SDL3 Lexer
 *
 * @author Daniel Leuck
 */
tagList: NL* (tag NL*)*;
tag:
    ( ( nsName valueList? ) | (valueList) ) attributeList? (';' | NL | ('{' tagList '}'))?;

value:
    // Strings
    StringLiteral | CharLiteral | ID
    // Numbers
    | IntegerLiteral | HexLiteral | BinLiteral | RealLiteral | LongLiteral
    | TRUE | FALSE
    | NULL
    // Data Structures
    | list | map
    // Date, DateTime and Duration
    | dateTime
    | duration
    // etc
    | URL
    | range
    | Version
    // Encodings
    | base64
    ;

// Duration

duration: CompoundDuration | DayDuration | HourDuration | MinuteDuration | SecondDuration | MillisecondDuration
          | NanosecondDuration;

// Range --- ---

rangeOp: InclusiveRangeOp | ExclusiveRangeOp | ExclusiveLeftOp | ExclusiveRightOp;

intRange: ('_' rangeOp IntegerLiteral) | (IntegerLiteral rangeOp '_') | (IntegerLiteral rangeOp IntegerLiteral);
realRange: ('_' rangeOp RealLiteral) | (RealLiteral rangeOp '_') | (RealLiteral rangeOp RealLiteral);
durationRange: ('_' rangeOp duration) | (duration rangeOp '_') | (duration rangeOp duration);
dateTimeRange: ('_' rangeOp dateTime) | (dateTime rangeOp '_') | (dateTime rangeOp dateTime);
versionRange: ('_' rangeOp Version) | (Version rangeOp '_') | (Version rangeOp Version);

range: intRange | realRange | durationRange | dateTimeRange | versionRange;

// Tag Parts --- ---

valueList: value+;

attribute: nsName '=' value;

attributeList: attribute+;

// Tag name or attribute key, optionally prefixed by a namespace
nsName: (ID ':')? ID;

// DATA STRUCTURES --- ---

list: '[' NL* (COMMA? value NL*)* ']';

pair: NL* value '=' NL* value NL*;

map: ('[' NL* pair (COMMA? pair)* NL* ']') | ('[' '=' ']');

base64: BASE64 BASE64_DATA;

dateTime: Date Time?;
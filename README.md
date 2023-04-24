# Regrunch

[![CircleCI](https://dl.circleci.com/status-badge/img/gh/knok16/regrunch/tree/master.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/knok16/regrunch/tree/master)

Regrunch is a multiplatform Kotlin library to:

* parse regular expressions;
* convert regular expressions into finite automata;
* list strings accepted by finite automata;
* answer to some decisions properties of finite automata.

Plus on its base CLI application is built to generate strings from regex pattern that can be used for wordlist
generation.

## Regrunch CLI

Regrunch is a wordlist generator where you can specify a regex to be used in generating the wordlists.
The wordlists are created through converting passed regex into deterministic finite automata and extracting all string
that it accepts.
Additionally, to regex you can determine the amount of characters and set of characters.

### How to build

#### Prerequisites

Regrunch uses [gradle](https://gradle.org/install/) as build tool. Gradle wrapper goes with project, as result it
requires only a Java JDK version 8 or higher to be installed.

#### Build Instruction

```
git clone --depth 1 --branch latest-release https://github.com/knok16/regrunch.git
cd regrunch
./gradlew linkReleaseExecutableNative
cp ./build/bin/native/releaseExecutable/regrunch.kexe regrunch
./regrunch --help
```

### Basic help

```
$ ./regrunch --help   
Usage: regrunch [OPTIONS] REGEX

  Generate strings from regex

Options:
  -m, --max-length INT             Limits length of strings generated from
                                   regex, required in case regex defines
                                   infinite amount of strings
  -a, --alphabet [ascii|printable-ascii]
                                   Symbols set used for string generation
  -s, --symbols TEXT               Symbols used for string generation
  -h, --help                       Show this message and exit

Arguments:
  REGEX  Regex to generate strings
```

### Supported regex constructs

| Name                             | Example                                           | Is supported | Note                                                                                                             |
|----------------------------------|---------------------------------------------------|--------------|------------------------------------------------------------------------------------------------------------------|
| Concatenation                    | `abc`                                             | Yes          |                                                                                                                  |
| Union                            | `a\|b`, `a\|`, `\|a`                              | Yes          |                                                                                                                  |
| Quantifiers                      |
| Kleene star                      | `a*`                                              | Yes          |                                                                                                                  |
| Plus quantifier (1 or more)      | `a+`                                              | Yes          |                                                                                                                  |
| Question mark (0 or 1)           | `a?`                                              | Yes          |                                                                                                                  |
| Exact repeats                    | `a{2}`, `a{3,}`, `a{4,6}`                         | Yes          |                                                                                                                  |
| Lazy quantifiers                 | `a*?`, `a+?`, `a??`, `a{2}?`, `a{3,}?`, `a{4,6}?` | Yes          |                                                                                                                  |
| Possessive quantifiers           | `a*+`, `a+=`, `a?+`, `a{2}+`, `a{3,}+`, `a{4,6}+` | No           |                                                                                                                  |
| Shorthand character classes      |
| Dot character                    | `.`                                               | Yes          |                                                                                                                  |
| Digit character                  | `\d`                                              | Yes          |                                                                                                                  |
| Non-digit character              | `\D`                                              | Yes          |                                                                                                                  |
| Whitespace character             | `\s`                                              | Yes          |                                                                                                                  |
| Non-whitespace character         | `\S`                                              | Yes          |                                                                                                                  |
| Word character                   | `\w`                                              | Yes          |                                                                                                                  |
| Non-word character               | `\W`                                              | Yes          |                                                                                                                  |
| Hexadecimal notation             | `\x3e`                                            | Yes          |                                                                                                                  |
| Unicode notation                 | `\u003e`                                          | Yes          |                                                                                                                  |
| Octal notation                   | `\044`                                            | No           | `\044` will be treated as simple character concatenation `044`                                                   |
| Control character notation       | `\cA`, `\cb`,`\cf`                                | Yes          |                                                                                                                  |
| \ escape                         | `\\`                                              | Yes          |                                                                                                                  |
| Non-printable character          |
| Tab character (0x09)             | `\t`                                              | Yes          |                                                                                                                  |
| Carriage return character (0x0D) | `\r`                                              | Yes          |                                                                                                                  |
| Line feed character (0x0A)       | `\n`                                              | Yes          |                                                                                                                  |
| Vertical tab                     | `\v`                                              | Yes          |                                                                                                                  |
| Horizontal tab                   | `\h`                                              | Yes          |                                                                                                                  |
| Bell character (0x07)            | `\a`                                              | Yes          |                                                                                                                  |
| Backspace character (0x08)       | `\b`                                              | Yes          | `\b` correspond to backspace character only when used in character class notation: `[\b]`                        |
| Escape character (0x1B)          | `\e`                                              | Yes          |                                                                                                                  |
| Form feed character (0x0C)       | `\f`                                              | Yes          |                                                                                                                  |
| Character classes                | `[0248]`                                          | Yes          |                                                                                                                  |
| Negation in character classes    | `[^0248]`                                         | Yes          |                                                                                                                  |
| Ranges in character classes      | `[0-36-9b-d]`                                     | Yes          |                                                                                                                  |
| Shorthands in character classes  | `[ab\d]`                                          | Yes          |                                                                                                                  |
| Character class subtraction      | `[0-9-[0-6-[0-3]]]`                               | No           |                                                                                                                  |
| Character class intersection     | `[0-9&&[0-6]&&[4-9]]`                             | No           |                                                                                                                  |
| Capturing groups                 | `(\d\d)-(\d\d)`                                   | Yes          |                                                                                                                  |
| Non capturing groups             | `(?:a\|b)c`                                       | Yes          |                                                                                                                  |
| Lookahead groups                 | `a(?=b)`, `a(?!b)`                                | No           |                                                                                                                  |
| Lookbehind groups                | `(?<=a)b`, `(?<!a)b`                              | No           |                                                                                                                  |
| Atomic groups                    | `a(?>bc\|b)c`                                     | No           |                                                                                                                  |
| Anchors                          |                                                   | No           | Parser will parse regular expression properly, but string generator will report that it does not support anchors |
| Start and end of line            | `$abc^`,                                          | No           |                                                                                                                  |
| Start and end of string          | `\Aabc\Z`                                         | No           |                                                                                                                  |
| End of string only               | `abc\z`                                           | No           |                                                                                                                  |
| Previous match                   | `\Gabc`                                           | No           |                                                                                                                  |
| Word boundary                    | `.+\b.+`                                          | No           | `\b` correspond to backspace character when used in character class notation: `[\b]`                             |
| Non word boundary                | `.+\B.+`                                          | No           |                                                                                                                  |

### Examples of usage

| Use-case                                                                                       | Regular expression                                                | CLI command                                                                    | Output                                                                                                                                 |
|------------------------------------------------------------------------------------------------|-------------------------------------------------------------------|--------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| All 3 digit combinations                                                                       | `\d{3}`                                                           | `./regrunch '\d{3}'`                                                           | 000<br/>001<br/>002<br/>...<br/>998<br/>999                                                                                            |
| All 3 digit combinations                                                                       | `[0-9]{3}`                                                        | `./regrunch '[0-9]{3}'`                                                        | 000<br/>001<br/>002<br/>...<br/>998<br/>999                                                                                            |
| All 3 digit combinations of arabic digits                                                      | `[٠-٩]{3}`                                                        | `./regrunch '[٠-٩]{3}'`                                                        | ٠٠٠<br/>٠٠١<br/>٠٠٢<br/>...<br/>٩٩٨<br/>٩٩٩                                                                                            |
| Georgian alphabet                                                                              | `[ა-ჰ]`                                                           | `./regrunch '[ა-ჰ]'`                                                           |                                                                                                                                        |
| All positive 3 digit numbers                                                                   | `[1-9][0-9]{0,2}`                                                 | `./regrunch '[1-9][0-9]{0,2}'`                                                 | 1<br/>10<br/>100<br/>101<br/>...<br/>998<br/>999                                                                                       |
| All positive 3 digit numbers                                                                   | `[1-9][0-9]*`                                                     | `./regrunch --max-length 3 '[1-9][0-9]*'`                                      | 1<br/>10<br/>100<br/>101<br/>...<br/>998<br/>999                                                                                       |
| All ip addresses in 192.168.0.0/16                                                             | `192\.168(\.(25[0-5]\|2[0-4][0-9]\|1[0-9][0-9]\|[1-9]?[0-9])){2}` | `./regrunch '192\.168(\.(25[0-5]\|2[0-4][0-9]\|1[0-9][0-9]\|[1-9]?[0-9])){2}'` | 192.168.0.0<br/>192.168.0.1<br/>192.168.0.10<br/>192.168.0.100<br/>...<br/>192.168.220.240<br/>...<br/>192.168.99.98<br/>192.168.99.99 |
| All 6 bit masks                                                                                | `[01]{6}`                                                         | `./regrunch '[01]{6}'`                                                         | 000000<br/>000001<br/>000010<br/>000011<br/>...<br/>111110<br/>111111                                                                  |
| All words with 4, 5 and 6 letters followed by 2 digits and 1 of special characters !, % or #   | `[a-zA-Z]{4,6}\d\d[!%#]`                                          | `./regrunch '[a-zA-Z]{4,6}\d\d[!%#]'`                                          | AAAA00!<br/>AAAA00#<br/>AAAA00%<br/>AAAA01!<br/>...<br/>zzzzzz99#<br/>zzzzzz99%                                                        |
| All combination of character cases for word 'taylor' followed by 2 digits and exclamation mark | `[Tt][Aa][Yy][Ll][Oo][Rr][0-9]{2}!`                               | `./regrunch '[Tt][Aa][Yy][Ll][Oo][Rr][0-9]{2}!'`                               | TAYLOR00!<br/>TAYLOR01!<br/>TAYLOR02!<br/>TAYLOR03!<br/>...<br/>taYLOr55!<br/>...<br/>taylor98!<br/>taylor99!                          |

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

### Examples of usage

| Use-case                                                                                       | Regular expression                                                | CLI command                                                                    | Output                                                                                                                                 |
|------------------------------------------------------------------------------------------------|-------------------------------------------------------------------|--------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| All 3 digit combinations                                                                       | `\d{3}`                                                           | `./regrunch "\d{3}"`                                                           | 000<br/>001<br/>002<br/>...<br/>998<br/>999                                                                                            |
| All 3 digit combinations                                                                       | `[0-9]{3}`                                                        | `./regrunch "[0-9]{3}"`                                                        | 000<br/>001<br/>002<br/>...<br/>998<br/>999                                                                                            |
| All positive 3 digit numbers                                                                   | `[1-9][0-9]{0,2}`                                                 | `./regrunch "[1-9][0-9]{0,2}"`                                                 | 1<br/>10<br/>100<br/>101<br/>...<br/>998<br/>999                                                                                       |
| All positive 3 digit numbers                                                                   | `[1-9][0-9]*`                                                     | `./regrunch --max-length 3 "[1-9][0-9]*"`                                      | 1<br/>10<br/>100<br/>101<br/>...<br/>998<br/>999                                                                                       |
| All ip addresses in 192.168.0.0/16                                                             | `192\.168(\.(25[0-5]\|2[0-4][0-9]\|1[0-9][0-9]\|[1-9]?[0-9])){2}` | `./regrunch "192\.168(\.(25[0-5]\|2[0-4][0-9]\|1[0-9][0-9]\|[1-9]?[0-9])){2}"` | 192.168.0.0<br/>192.168.0.1<br/>192.168.0.10<br/>192.168.0.100<br/>...<br/>192.168.220.240<br/>...<br/>192.168.99.98<br/>192.168.99.99 |
| All 6 bit masks                                                                                | `[01]{6}`                                                         | `./regrunch "[01]{6}"`                                                         | 000000<br/>000001<br/>000010<br/>000011<br/>...<br/>111110<br/>111111                                                                  |
| All words with 4, 5 and 6 letters followed by 2 digits and 1 of special characters !, % or #   | `[a-zA-Z]{4,6}\d\d[!%#]`                                          | `./regrunch "[a-zA-Z]{4,6}\d\d[!%#]"`                                          | AAAA00!<br/>AAAA00#<br/>AAAA00%<br/>AAAA01!<br/>...<br/>zzzzzz99#<br/>zzzzzz99%                                                        |
| All combination of character cases for word 'taylor' followed by 2 digits and exclamation mark | `[Tt][Aa][Yy][Ll][Oo][Rr][0-9]{2}!`                               | `./regrunch "[Tt][Aa][Yy][Ll][Oo][Rr][0-9]{2}!"`                               | TAYLOR00!<br/>TAYLOR01!<br/>TAYLOR02!<br/>TAYLOR03!<br/>...<br/>taYLOr55!<br/>...<br/>taylor98!<br/>taylor99!                          |

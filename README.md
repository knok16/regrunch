# Regrunch

[![CircleCI](https://dl.circleci.com/status-badge/img/gh/knok16/regrunch/tree/master.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/knok16/regrunch/tree/master)

Regrunch is a multiplatform Kotlin library to:

* parse regular expressions;
* convert regular expressions into finite automata;
* list strings accepted by finite automata;
* answer to some decisions properties of finite automata.

Plus on its base CLI application is built to generate strings from regex pattern that can be used for wordlist
generation.

```
$ ./regrunch.exe --help   
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

### Example
```
$ ./regrunch.exe "[01]{3}"         
000
001
010
011
100
101
110
111
```

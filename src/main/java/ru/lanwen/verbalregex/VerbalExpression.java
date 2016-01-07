package ru.lanwen.verbalregex;

import static java.lang.String.valueOf;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerbalExpression {

    private final Pattern pattern;

    public static class Builder {

        private StringBuilder prefixes;
        private StringBuilder source;
        private StringBuilder suffixes;
        private int modifiers;

        /**
         * Package private. Use {@link #regex()} to build a new one
         *
         * @since 1.2
         */
        Builder() {
            this("", "", "", Pattern.MULTILINE);
        }
        
        /**
         * Package private. Use {@link #regex(Builder)} to build a new one
         * 
         * @since 1.2
         */
        Builder(Builder builder){
            this(builder.prefixes.toString(), builder.source.toString(), builder.suffixes.toString(),
                    builder.modifiers);
        }

        /**
         * Package private. Use parameters to build a new one
         * 
         * @since 1.2
         */
        Builder(String prefixesStr, String sourceStr, String suffixesStr, int modifiers) {
            this.prefixes = new StringBuilder(prefixesStr);
            this.source = new StringBuilder(sourceStr);
            this.suffixes = new StringBuilder(suffixesStr);
            this.modifiers = modifiers;
        }

        /**
         * Escapes any non-word char with two backslashes
         * used by any method, except {@link #add(String)}
         *
         * @param pValue - the string for char escaping
         * @return sanitized string value
         */
        private String sanitize(final String pValue) {
            return pValue.replaceAll("[\\W]", "\\\\$0");
        }

        /**
         * Counts occurrences of some substring in whole string
         * Same as org.apache.commons.lang3.StringUtils#countMatches(String, java.lang.String)
         * by effect. Used to count braces for {@link #or(String)} method
         *
         * @param where - where to find
         * @param what  - what needs to count matches
         * @return 0 if nothing found, count of occurrences instead
         */
        private int countOccurrencesOf(String where, String what) {
            return (where.length() - where.replace(what, "").length()) / what.length();
        }

        public VerbalExpression build() {
            Pattern pattern = Pattern.compile(new StringBuilder(prefixes)
                    .append(source).append(suffixes).toString(), modifiers);
            return new VerbalExpression(pattern);
        }

        /**
         * Append literal expression
         * Everything added to the expression should go trough this method
         * (keep in mind when creating your own methods).
         * All existing methods already use this, so for basic usage, you can just ignore this method.
         * <p/>
         * Example:
         * regex().add("\n.*").build() // produce exact "\n.*" regexp
         *
         * @param pValue - literal expression, not sanitized
         * @return this builder
         */
        public Builder add(final String pValue) {
            this.source.append(pValue);
            return this;
        }

        /**
         * Append a regex from builder and wrap it with unnamed group (?: ... )
         *
         * @param regex - VerbalExpression.Builder, that not changed
         * @return this builder
         * @since 1.2
         */
        public Builder add(final Builder regex) {
            return this.group().add(regex.build().toString()).endGr();
        }

        /**
         * Enable or disable the expression to start at the beginning of the line
         *
         * @param pEnable - enables or disables the line starting
         * @return this builder
         */
        public Builder startOfLine(final boolean pEnable) {
            this.prefixes.append(pEnable ? "^" : "");
            if (!pEnable) {
                this.prefixes = new StringBuilder(this.prefixes.toString().replace("^", ""));
            }
            return this;
        }

        /**
         * Mark the expression to start at the beginning of the line
         * Same as {@link #startOfLine(boolean)} with true arg
         *
         * @return this builder
         */
        public Builder startOfLine() {
            return startOfLine(true);
        }

        /**
         * Enable or disable the expression to end at the last character of the line
         *
         * @param pEnable - enables or disables the line ending
         * @return this builder
         */
        public Builder endOfLine(final boolean pEnable) {
            this.suffixes.append(pEnable ? "$" : "");
            if (!pEnable) {
                this.suffixes = new StringBuilder(this.suffixes.toString().replace("$", ""));
            }
            return this;
        }

        /**
         * Mark the expression to end at the last character of the line
         * Same as {@link #endOfLine(boolean)} with true arg
         *
         * @return this builder
         */
        public Builder endOfLine() {
            return endOfLine(true);
        }

        /**
         * Add a string to the expression
         *
         * @param pValue - the string to be looked for (sanitized)
         * @return this builder
         */
        public Builder then(final String pValue) {
            return this.add("(?:" + sanitize(pValue) + ")");
        }

        /**
         * Add a string to the expression
         * Syntax sugar for {@link #then(String)} - use it in case:
         * regex().find("string") // when it goes first
         *
         * @param value - the string to be looked for (sanitized)
         * @return this builder
         */
        public Builder find(final String value) {
            return this.then(value);
        }

        /**
         * Add a string to the expression that might appear once (or not)
         * Example:
         * The following matches all strings that contain http:// or https://
         * VerbalExpression regex = regex()
         * .find("http")
         * .maybe("s")
         * .then("://")
         * .anythingBut(" ").build();
         * regex.test("http://")    //true
         * regex.test("https://")   //true
         *
         * @param pValue - the string to be looked for
         * @return this builder
         */
        public Builder maybe(final String pValue) {
            return this.then(pValue).add("?");
        }
        
        /**
         * Add a regex to the expression that might appear once (or not)
         * Example:
         * The following matches all names that have a prefix or not.
         * VerbalExpression.Builder namePrefix = regex().oneOf("Mr.", "Ms.");
	 * VerbalExpression name = regex()
	 *	.maybe(namePrefix)
	 *	.space()
	 *	.zeroOrMore()
	 *	.word()
	 *	.oneOrMore()
	 *	.build();
         * regex.test("Mr. Bond/")    //true
         * regex.test("James")   //true
         *
         * @param pValue - the string to be looked for
         * @return this builder
         */
        public Builder maybe(final Builder regex) {
            return this.group().add(regex).endGr().add("?");
        }

        /**
         * Add expression that matches anything (includes empty string)
         *
         * @return this builder
         */
        public Builder anything() {
            return this.add("(?:.*)");
        }

        /**
         * Add expression that matches anything, but not passed argument
         *
         * @param pValue - the string not to match
         * @return this builder
         */
        public Builder anythingBut(final String pValue) {
            return this.add("(?:[^" + sanitize(pValue) + "]*)");
        }

        /**
         * Add expression that matches something that might appear once (or more)
         *
         * @return this builder
         */
        public Builder something() {
            return this.add("(?:.+)");
        }

        public Builder somethingButNot(final String pValue) {
            return this.add("(?:[^" + sanitize(pValue) + "]+)");
        }

        /**
         * Add universal line break expression
         *
         * @return this builder
         */
        public Builder lineBreak() {
            return this.add("(?:\\n|(\\r\\n))");
        }

        /**
         * Shortcut for {@link #lineBreak()}
         *
         * @return this builder
         */
        public Builder br() {
            return this.lineBreak();
        }

        /**
         * Add expression to match a tab character ('\u0009')
         *
         * @return this builder
         */
        public Builder tab() {
            return this.add("(?:\\t)");
        }

        /**
         * Add word, same as [a-zA-Z_0-9]+
         *
         * @return this builder
         */
        public Builder word() {
            return this.add("(?:\\w+)");
        }


        /*
           --- Predefined character classes
         */

        /**
         * Add word character, same as [a-zA-Z_0-9]
         *
         * @return this builder
         */
        public Builder wordChar() {
            return this.add("(?:\\w)");
        }


        /**
         * Add non-word character: [^\w]
         *
         * @return this builder
         */
        public Builder nonWordChar() {
            return this.add("(?:\\W)");
        }

        /**
         * Add non-digit: [^0-9]
         *
         * @return this builder
         */
        public Builder nonDigit() {
            return this.add("(?:\\D)");
        }

        /**
         * Add same as [0-9]
         *
         * @return this builder
         */
        public Builder digit() {
            return this.add("(?:\\d)");
        }

        /**
         * Add whitespace character, same as [ \t\n\x0B\f\r]
         *
         * @return this builder
         */
        public Builder space() {
            return this.add("(?:\\s)");
        }

        /**
         * Add non-whitespace character: [^\s]
         *
         * @return this builder
         */
        public Builder nonSpace() {
            return this.add("(?:\\S)");
        }


        /*
           --- / end of predefined character classes
         */


        public Builder anyOf(final String pValue) {
            this.add("[" + sanitize(pValue) + "]");
            return this;
        }

        /**
         * Shortcut to {@link #anyOf(String)}
         *
         * @param value - CharSequence every char from can be matched
         * @return this builder
         */
        public Builder any(final String value) {
            return this.anyOf(value);
        }

        /**
         * Add expression to match a range (or multiply ranges)
         * Usage: .range(from, to [, from, to ... ])
         * Example: The following matches a hexadecimal number:
         * regex().range( "0", "9", "a", "f") // produce [0-9a-f]
         *
         * @param pArgs - pairs for range
         * @return this builder
         */
        public Builder range(final String... pArgs) {
            StringBuilder value = new StringBuilder("[");
            for (int firstInPairPosition = 1; firstInPairPosition < pArgs.length; firstInPairPosition += 2) {
                String from = sanitize(pArgs[firstInPairPosition - 1]);
                String to = sanitize(pArgs[firstInPairPosition]);

                value.append(from).append("-").append(to);
            }
            value.append("]");

            return this.add(value.toString());
        }

        public Builder addModifier(final char pModifier) {
            switch (pModifier) {
                case 'd':
                    modifiers |= Pattern.UNIX_LINES;
                    break;
                case 'i':
                    modifiers |= Pattern.CASE_INSENSITIVE;
                    break;
                case 'x':
                    modifiers |= Pattern.COMMENTS;
                    break;
                case 'm':
                    modifiers |= Pattern.MULTILINE;
                    break;
                case 's':
                    modifiers |= Pattern.DOTALL;
                    break;
                case 'u':
                    modifiers |= Pattern.UNICODE_CASE;
                    break;
                case 'U':
                    modifiers |= Pattern.UNICODE_CHARACTER_CLASS;
                    break;
                default:
                    break;
            }

            return this;
        }

        public Builder removeModifier(final char pModifier) {
            switch (pModifier) {
                case 'd':
                    modifiers ^= Pattern.UNIX_LINES;
                    break;
                case 'i':
                    modifiers ^= Pattern.CASE_INSENSITIVE;
                    break;
                case 'x':
                    modifiers ^= Pattern.COMMENTS;
                    break;
                case 'm':
                    modifiers ^= Pattern.MULTILINE;
                    break;
                case 's':
                    modifiers ^= Pattern.DOTALL;
                    break;
                case 'u':
                    modifiers ^= Pattern.UNICODE_CASE;
                    break;
                case 'U':
                    modifiers ^= Pattern.UNICODE_CHARACTER_CLASS;
                    break;
                default:
                    break;
            }

            return this;
        }

        public Builder withAnyCase(final boolean pEnable) {
            if (pEnable) {
                this.addModifier('i');
            } else {
                this.removeModifier('i');
            }
            return this;
        }

        /**
         * Turn ON matching with ignoring case
         * Example:
         * // matches "a"
         * // matches "A"
         * regex().find("a").withAnyCase()
         *
         * @return this builder
         */
        public Builder withAnyCase() {
            return withAnyCase(true);
        }

        public Builder searchOneLine(final boolean pEnable) {
            if (pEnable) {
                this.removeModifier('m');
            } else {
                this.addModifier('m');
            }
            return this;
        }

        /**
         * Convenient method to show that string usage count is exact count, range count or simply one or more
         * Usage:
         * regex().multiply("abc")                                  // Produce (?:abc)+
         * regex().multiply("abc", null)                            // Produce (?:abc)+
         * regex().multiply("abc", (int)from)                       // Produce (?:abc){from}
         * regex().multiply("abc", (int)from, (int)to)              // Produce (?:abc){from, to}
         * regex().multiply("abc", (int)from, (int)to, (int)...)    // Produce (?:abc)+
         *
         * @param pValue - the string to be looked for
         * @param count  - (optional) if passed one or two numbers, it used to show count or range count
         * @return this builder
         * @see #oneOrMore()
         * @see #then(String)
         * @see #zeroOrMore()
         */
        public Builder multiple(final String pValue, final int... count) {
            if (count == null) {
                return this.then(pValue).oneOrMore();
            }
            switch (count.length) {
                case 1:
                    return this.then(pValue).count(count[0]);
                case 2:
                    return this.then(pValue).count(count[0], count[1]);
                default:
                    return this.then(pValue).oneOrMore();
            }
        }

        /**
         * Adds "+" char to regexp
         * Same effect as {@link #atLeast(int)} with "1" argument
         * Also, used by {@link #multiple(String, int...)} when second argument is null, or have length more than 2
         *
         * @return this builder
         * @since 1.2
         */
        public Builder oneOrMore() {
            return this.add("+");
        }

        /**
         * Adds "*" char to regexp, means zero or more times repeated
         * Same effect as {@link #atLeast(int)} with "0" argument
         *
         * @return this builder
         * @since 1.2
         */
        public Builder zeroOrMore() {
            return this.add("*");
        }

        /**
         * Add count of previous group
         * for example:
         * .find("w").count(3) // produce - (?:w){3}
         *
         * @param count - number of occurrences of previous group in expression
         * @return this Builder
         */
        public Builder count(final int count) {
            this.source.append("{").append(count).append("}");
            return this;
        }

        /**
         * Produce range count
         * for example:
         * .find("w").count(1, 3) // produce (?:w){1,3}
         *
         * @param from - minimal number of occurrences
         * @param to   - max number of occurrences
         * @return this Builder
         * @see #count(int)
         */
        public Builder count(final int from, final int to) {
            this.source.append("{").append(from).append(",").append(to).append("}");
            return this;
        }

        /**
         * Produce range count with only minimal number of occurrences
         * for example:
         * .find("w").atLeast(1) // produce (?:w){1,}
         *
         * @param from - minimal number of occurrences
         * @return this Builder
         * @see #count(int)
         * @see #oneOrMore()
         * @see #zeroOrMore()
         * @since 1.2
         */
        public Builder atLeast(final int from) {
            return this.add("{").add(valueOf(from)).add(",}");
        }

        /**
         * Add a alternative expression to be matched
         *
         * Issue #32
         *
         * @param pValue - the string to be looked for
         * @return this builder
         */
        public Builder or(final String pValue) {
            this.prefixes.append("(?:");

            int opened = countOccurrencesOf(this.prefixes.toString(), "(");
            int closed = countOccurrencesOf(this.suffixes.toString(), ")");

            if (opened >= closed) {
                this.suffixes = new StringBuilder(")" + this.suffixes.toString());
            }

            this.add(")|(?:");
            if (pValue != null) {
                this.then(pValue);
            }
            return this;
        }
        
        /**
         * Adds an alternative expression to be matched
         * based on an array of values
         *
         * @param pValues - the strings to be looked for
         * @return this builder
         * @since 1.3
         */
        public Builder oneOf(final String... pValues) {
            if(pValues != null && pValues.length > 0) {
        	this.add("(?:");
        	for(int i = 0; i < pValues.length; i++) {
        	    String value = pValues[i];
        	    this.add("(?:");
        	    this.add(value);
        	    this.add(")");
        	    if(i < pValues.length - 1) {
        	        this.add("|");
        	    }
        	}
        	this.add(")");
            }
            return this;
        }

        /**
         * Adds capture - open brace to current position and closed to suffixes
         *
         * @return this builder
         */
        public Builder capture() {
            this.suffixes.append(")");
            return this.add("(");
        }

        /**
         * Shortcut for {@link #capture()}
         *
         * @return this builder
         * @since 1.2
         */
        public Builder capt() {
            return this.capture();
        }

        /**
         * Same as {@link #capture()}, but don't save result
         * May be used to set count of duplicated captures, without creating a new saved capture
         * Example:
         * // Without group() - count(2) applies only to second capture
         * regex().group()
         * .capt().range("0", "1").endCapt().tab()
         * .capt().digit().count(5).endCapt()
         * .endGr().count(2);
         *
         * @return this builder
         * @since 1.2
         */
        public Builder group() {
            this.suffixes.append(")");
            return this.add("(?:");
        }

        /**
         * Close brace for previous capture and remove last closed brace from suffixes
         * Can be used to continue build regex after capture or to add multiply captures
         *
         * @return this builder
         */
        public Builder endCapture() {
            if (this.suffixes.indexOf(")") != -1) {
                this.suffixes.setLength(suffixes.length() - 1);
                return this.add(")");
            } else {
                throw new IllegalStateException("Can't end capture (group) when it not started");
            }
        }

        /**
         * Shortcut for {@link #endCapture()}
         *
         * @return this builder
         * @since 1.2
         */
        public Builder endCapt() {
            return this.endCapture();
        }

        /**
         * Closes current unnamed and unmatching group
         * Shortcut for {@link #endCapture()}
         * Use it with {@link #group()} for prettify code
         * Example:
         * regex().group().maybe("word").count(2).endGr()
         *
         * @return this builder
         * @since 1.2
         */
        public Builder endGr() {
            return this.endCapture();
        }
    }

    /**
     * Use builder {@link #regex()} (or {@link #regex(ru.lanwen.verbalregex.VerbalExpression.Builder)})
     * to create new instance of VerbalExpression
     *
     * @param pattern - {@link java.util.regex.Pattern} that constructed by builder
     */
    private VerbalExpression(final Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * Test that full string matches regular expression
     *
     * @param pToTest - string to check match
     * @return true if matches exact string, false otherwise
     */
    public boolean testExact(final String pToTest) {
        boolean ret = false;
        if (pToTest != null) {
            ret = pattern.matcher(pToTest).matches();
        }
        return ret;
    }

    /**
     * Test that full string contains regex
     *
     * @param pToTest - string to check match
     * @return true if string contains regex, false otherwise
     */
    public boolean test(final String pToTest) {
        boolean ret = false;
        if (pToTest != null) {
            ret = pattern.matcher(pToTest).find();
        }
        return ret;
    }

    /**
     * Extract full string that matches regex
     * Same as {@link #getText(String, int)} for 0 group
     *
     * @param toTest - string to extract from
     * @return group 0, extracted from text
     */
    public String getText(final String toTest) {
        return getText(toTest, 0);
    }

    /**
     * Extract exact group from string
     *
     * @param toTest - string to extract from
     * @param group  - group to extract
     * @return extracted group
     * @since 1.1
     */
    public String getText(final String toTest, final int group) {
        Matcher m = pattern.matcher(toTest);
        StringBuilder result = new StringBuilder();
        while (m.find()) {
            result.append(m.group(group));
        }
        return result.toString();
    }


    @Override
    public String toString() {
        return pattern.pattern();
    }

    /**
     * Creates new instance of VerbalExpression builder from cloned builder
     *
     * @param pBuilder - instance to clone
     * @return new VerbalExpression.Builder copied from passed
     * @since 1.1
     */
    public static Builder regex(final Builder pBuilder) {
        return new Builder(pBuilder);
    }

    /**
     * Creates new instance of VerbalExpression builder
     *
     * @return new VerbalExpression.Builder
     * @since 1.1
     */
    public static Builder regex() {
        return new Builder();
    }
}

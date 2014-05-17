package ru.lanwen.verbalregex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerbalExpression {

    private final Pattern pattern;

    public static class Builder {

        private StringBuilder prefixes = new StringBuilder();
        private StringBuilder source = new StringBuilder();
        private StringBuilder suffixes = new StringBuilder();
        private int modifiers = Pattern.MULTILINE;

        private String sanitize(final String pValue) {
            return pValue.replaceAll("[\\W]", "\\\\$0");
        }

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
        public Builder add(String pValue) {
            this.source.append(pValue);
            return this;
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
         * @return
         */
        public Builder anythingButNot(final String pValue) {
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

        public Builder multiple(final String pValue) {
            switch (pValue.charAt(0)) {
                case '*':
                case '+':
                    return this.add(pValue);
                default:
                    return this.add(this.sanitize(pValue) + '+');
            }
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
         * Add a alternative expression to be matched
         *
         * @param pValue - the string to be looked for
         * @return this builder
         */
        public Builder or(final String pValue) {
            this.prefixes.append("(");

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
         * Adds capture - open brace to current position and closed to suffixes
         *
         * @return this builder
         */
        public Builder capture() {
            this.suffixes.append(")");
            return this.add("(");
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
                throw new IllegalStateException("Can't end capture when it not started");
            }
        }
    }


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
     */
    public static Builder regex(final Builder pBuilder) {
        Builder builder = new Builder();

        builder.prefixes = new StringBuilder(pBuilder.prefixes);
        builder.source = new StringBuilder(pBuilder.source);
        builder.suffixes = new StringBuilder(pBuilder.suffixes);
        builder.modifiers = pBuilder.modifiers;

        return builder;
    }

    /**
     * Creates new instance of VerbalExpression builder
     *
     * @return new VerbalExpression.Builder
     */
    public static Builder regex() {
        return new Builder();
    }
}

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

        public Builder add(String pValue) {
            this.source.append(pValue);
            return this;
        }

        public Builder startOfLine(boolean pEnable) {
            this.prefixes.append(pEnable ? "^" : "");
            return this;
        }

        public Builder startOfLine() {
            return startOfLine(true);
        }

        public Builder endOfLine(final boolean pEnable) {
            this.suffixes.append(pEnable ? "$" : "");
            return this;
        }

        public Builder endOfLine() {
            return endOfLine(true);
        }

        public Builder then(String pValue) {
            return this.add("(?:" + sanitize(pValue) + ")");
        }

        public Builder find(String value) {
            return this.then(value);
        }

        public Builder maybe(final String pValue) {
            return this.then(pValue).add("?");
        }

        public Builder anything() {
            return this.add("(?:.*)");
        }

        public Builder anythingButNot(final String pValue) {
            return this.add("(?:[^" + sanitize(pValue) + "]*)");
        }

        public Builder something() {
            return this.add("(?:.+)");
        }

        public Builder somethingButNot(final String pValue) {
            return this.add("(?:[^" + sanitize(pValue) + "]+)");
        }

        public Builder lineBreak() {
            return this.add("(?:\\n|(\\r\\n))");
        }

        public Builder br() {
            return this.lineBreak();
        }

        /**
         * @return tab character ('\u0009')
         */
        public Builder tab() {
            return this.add("(?:\\t)");
        }

        /**
         * @return word, same as [a-zA-Z_0-9]+
         */
        public Builder word() {
            return this.add("(?:\\w+)");
        }


        /*
           --- Predefined character classes
         */

        /**
         * @return word character, same as [a-zA-Z_0-9]
         */
        public Builder wordChar() {
            return this.add("(?:\\w)");
        }


        /**
         * @return non-word character: [^\w]
         */
        public Builder nonWordChar() {
            return this.add("(?:\\W)");
        }

        /**
         * @return non-digit: [^0-9]
         */
        public Builder nonDigit() {
            return this.add("(?:\\D)");
        }

        /**
         * @return same as [0-9]
         */
        public Builder digit() {
            return this.add("(?:\\d)");
        }

        /**
         * @return whitespace character, same as [ \t\n\x0B\f\r]
         */
        public Builder space() {
            return this.add("(?:\\s)");
        }

        /**
         * @return non-whitespace character: [^\s]
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

        public Builder range(String... pArgs) {
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

        public Builder withAnyCase(boolean pEnable) {
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

        public Builder searchOneLine(boolean pEnable) {
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
        public Builder count(int count) {
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
        public Builder count(int from, int to) {
            this.source.append("{").append(from).append(",").append(to).append("}");
            return this;
        }

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
         * @return this builder
         */
        public Builder capture() {
            this.suffixes.append(")");
            return this.add("(");
        }

        /**
         * Close brace for previous capture and remove last closed brace from suffixes
         * Can be used to continue build regex after capture or to add multiply captures
         * @return this builder
         */
        public Builder endCapture() {
            if(this.suffixes.indexOf(")") != -1) {
                this.suffixes.setLength(suffixes.length() - 1);
                return this.add(")");
            } else {
                throw new IllegalStateException("Can't end capture when it not started");
            }
        }
    }

    public boolean testExact(final String pToTest) {
        boolean ret = false;
        if (pToTest != null) {
            ret = pattern.matcher(pToTest).matches();
        }
        return ret;
    }

    public boolean test(final String pToTest) {
        boolean ret = false;
        if (pToTest != null) {
            ret = pattern.matcher(pToTest).find();
        }
        return ret;
    }

    private VerbalExpression(final Pattern pattern) {
        this.pattern = pattern;
    }

    public String getText(String toTest) {
        return getText(toTest, 0);
    }

    public String getText(String toTest, int group) {
        Matcher m = pattern.matcher(toTest);
        StringBuilder result = new StringBuilder();
        while (m.find()) {
            result.append(m.group(group));
        }
        return result.toString();
    }

    public static Builder regex(final Builder pBuilder) {
        Builder builder = new Builder();

        builder.prefixes = new StringBuilder(pBuilder.prefixes);
        builder.source = new StringBuilder(pBuilder.source);
        builder.suffixes = new StringBuilder(pBuilder.suffixes);
        builder.modifiers = pBuilder.modifiers;

        return builder;
    }

    public static Builder regex() {
        return new Builder();
    }

    @Override
    public String toString() {
        return pattern.pattern();
    }
}

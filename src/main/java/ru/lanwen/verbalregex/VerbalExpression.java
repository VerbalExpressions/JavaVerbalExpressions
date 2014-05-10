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
            this.add("(?:" + sanitize(pValue) + ")");
            return this;
        }

        public Builder find(String value) {
            this.then(value);
            return this;
        }

        public Builder maybe(final String pValue) {
            return this.then(pValue).add("?");
        }

        public Builder anything() {
            this.add("(?:.*)");
            return this;
        }

        public Builder anythingButNot(final String pValue) {
            this.add("(?:[^" + sanitize(pValue) + "]*)");
            return this;
        }

        public Builder something() {
            this.add("(?:.+)");
            return this;
        }

        public Builder somethingButNot(final String pValue) {
            this.add("(?:[^" + sanitize(pValue) + "]+)");
            return this;
        }

        public Builder lineBreak() {
            this.add("(?:\\n|(\\r\\n))");
            return this;
        }

        public Builder br() {
            this.lineBreak();
            return this;
        }

        public Builder tab() {
            this.add("\\t");
            return this;
        }

        public Builder word() {
            this.add("\\w+");
            return this;
        }

        public Builder anyOf(final String pValue) {
            this.add("[" + sanitize(pValue) + "]");
            return this;
        }

        public Builder any(final String value) {
            this.anyOf(value);
            return this;
        }

        public Builder range(String... pArgs) {
            String value = "[";
            for (int _to = 1; _to < pArgs.length; _to += 2) {
                String from = sanitize((String) pArgs[_to - 1]);
                String to = sanitize((String) pArgs[_to]);

                value += from + "-" + to;
            }
            value += "]";

            this.add(value);
            return this;
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
            if(this.suffixes.length() > 0 && this.suffixes.indexOf(")") + 1 == this.suffixes.length()) {
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

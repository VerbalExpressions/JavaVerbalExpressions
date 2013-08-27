
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerbalExpression {

    private Pattern pattern;

    public static class Builder {

        private String prefixes = "", source = "", suffixes = "";
        private Pattern pattern;
        private int modifiers = Pattern.MULTILINE;

        private String sanitize(final String pValue) {
            Matcher matcher = Pattern.compile("").matcher("").usePattern(Pattern.compile("[^\\w]"));
            int lastEnd = 0;
            String result = "";
            matcher.reset(pValue);
            boolean matcherCalled = false;
            while (matcher.find()) {
                matcherCalled = true;
                if (matcher.start() != lastEnd) {
                    result += pValue.substring(lastEnd, matcher.start());
                }
                result += "\\" + pValue.substring(matcher.start(), matcher.end());
                lastEnd = matcher.end();
            }
            if (!matcherCalled) {
                return pValue;
            }
            return result;
        }

        public Builder add(String pValue) {
            this.source += pValue;
            return this;
        }

        public VerbalExpression build() {
            pattern = Pattern.compile(this.prefixes + this.source + this.suffixes, this.modifiers);
            return new VerbalExpression(this);
        }

        public Builder startOfLine(boolean pEnable) {
            this.prefixes = pEnable ? "^" : "";
            return this;
        }

        public Builder startOfLine() {
            return startOfLine(true);
        }

        public Builder endOfLine(final boolean pEnable) {
            this.suffixes = pEnable ? "$" : "";
            return this;
        }

        public Builder endOfLine() {
            return endOfLine(true);
        }

        public Builder then(String pValue) {
            this.add("(" + sanitize(pValue) + ")");
            return this;
        }

        public Builder find(String value) {
            this.then(value);
            return this;
        }

        public Builder maybe(final String pValue) {
            this.add("(" + sanitize(pValue) + ")?");
            return this;
        }

        public Builder anything() {
            this.add("(.*)");
            return this;
        }

        public Builder anythingButNot(final String pValue) {
            this.add("([^" + sanitize(pValue) + "]*)");
            return this;
        }

        public Builder something() {
            this.add("(.+)");
            return this;
        }

        public Builder somethingButNot(final String pValue) {
            this.add("([^" + sanitize(pValue) + "]+)");
            return this;
        }

        public Builder lineBreak() {
            this.add("(\\n|(\\r\\n))");
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

        public Builder range(final Object[] pArgs) {
            String value = "[";
            for (int _from = 0; _from < pArgs.length; _from += 2) {
                int _to = _from + 1;
                if (pArgs.length <= _to) {
                    break;
                }
                int from = Integer.getInteger(sanitize((String) pArgs[_from]));
                int to = Integer.getInteger(sanitize((String) pArgs[_to]));

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
            String value = this.sanitize(pValue);
            switch (value.charAt(0)) {
                case '*':
                case '+':
                    break;
                default:
                    value += '+';
            }
            this.add(value);
            return this;
        }

        public Builder or(final String pValue) {
            if (this.prefixes.indexOf("(") == -1) {
                this.prefixes += "(";
            }
            if (this.suffixes.indexOf(")") == -1) {
                this.suffixes = ")" + this.suffixes;
            }

            this.add(")|(");
            if (pValue != null) {
                this.then(pValue);
            }
            return this;
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

    private VerbalExpression(final Builder pBuilder) {
        pattern = pBuilder.pattern;
    }

    @Override
    public String toString() {
        return pattern.pattern();
    }
}

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class VerbalExpression {
	private final StringBuilder source = new StringBuilder();
    private String prefixes = "", suffixes = "", pattern = "";
    private int modifiers = Pattern.MULTILINE;

    public VerbalExpression () {
        this.updatePattern();
    }

    private String sanitize(final String value) {
        final Matcher matcher = Pattern.compile("").matcher("").usePattern(Pattern.compile("[^\\w]"));
        int lastEnd = 0;
        final StringBuilder resultBuilder = new StringBuilder();
        matcher.reset(value);
        boolean matcherCalled = false;
        while (matcher.find()) {
            matcherCalled = true;
            if (matcher.start() != lastEnd) {
            	resultBuilder.append(value.substring(lastEnd, matcher.start()));
            }
            resultBuilder.append("\\");
            resultBuilder.append(value.substring(matcher.start(), matcher.end()));
            lastEnd = matcher.end();
        }
        if (!matcherCalled) return value;
        return resultBuilder.toString();
    }

    public VerbalExpression add(final String value) {
    	source.append(value);
        return this.updatePattern();
    }

    public VerbalExpression updatePattern() {
    	final String regex = this.prefixes + this.source.toString() + this.suffixes;
        final Pattern p = Pattern.compile(regex, this.modifiers);
        this.pattern = p.pattern();
        return this;
    }

    public VerbalExpression startOfLine(final boolean enable) {
        this.prefixes = enable ? "^" : "";
        this.updatePattern();
        return this;
    }

    public VerbalExpression startOfLine() {
        return startOfLine(true);
    }

    public VerbalExpression endOfLine(final boolean enable) {
        this.suffixes = enable ? "$" : "";
        this.updatePattern();
        return this;
    }

    public VerbalExpression endOfLine() {
        return endOfLine(true);
    }

    public VerbalExpression then(String value) {
        value = sanitize(value);
        this.add("(" + value + ")");
        return this;
    }

    public VerbalExpression find(final String value) {
        this.then(value);
        return this;
    }

    public VerbalExpression maybe(String value) {
        value = sanitize(value);
        this.add("(" + value + ")?");
        return this;
    }

    public VerbalExpression anything() {
        this.add("(.*)");
        return this;
    }

    public VerbalExpression anythingBut(String value) {
        value = sanitize(value);
        this.add("([^" + value + "]*)");
        return this;
    }

    public VerbalExpression something() {
        this.add("(.+)");
        return this;
    }

    public VerbalExpression somethingBut(String value) {
        value = sanitize(value);
        this.add("([^" + value + "]+)");
        return this;
    }

    public VerbalExpression replace(final String source, final String value) {
        this.updatePattern();
        final Matcher matcher = Pattern.compile(this.pattern).matcher(this.source);
        final String afterReplace = matcher.replaceAll(value);
        this.source.setLength(0);
        this.source.append(afterReplace);
        return this;
    }

    public VerbalExpression lineBreak() {
        this.add("(\\n|(\\r\\n))");
        return this;
    }

    public VerbalExpression br() {
        this.lineBreak();
        return this;
    }

    public VerbalExpression tab() {
        this.add("\\t");
        return this;
    }

    public VerbalExpression word() {
        this.add("\\w+");
        return this;
    }

    public VerbalExpression anyOf(String value) {
        value = sanitize(value);
        this.add("[" + value + "]");
        return this;
    }

    public VerbalExpression any(final String value) {
        this.anyOf(value);
        return this;
    }

    public VerbalExpression range(final Object[] args) {
    	final StringBuilder valueBuilder = new StringBuilder();
    	valueBuilder.append("[");
        for(int _from = 0; _from < args.length; _from += 2) {
            final int _to = _from+1;
            if (args.length <= _to) break;
            final int from = Integer.getInteger(sanitize((String)args[_from]));
            final int to = Integer.getInteger(sanitize((String)args[_to]));

            valueBuilder.append(from);
            valueBuilder.append("-");
            valueBuilder.append(to);
        }
        valueBuilder.append("]");
        
        this.add(valueBuilder.toString());
        return this;
    }

    public VerbalExpression addModifier(final char modifier) {
        switch (modifier) {
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

        this.updatePattern();
        return this;
    }

    public VerbalExpression removeModifier(final char modifier) {
        switch (modifier) {
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

        this.updatePattern();
        return this;
    }

    public VerbalExpression withAnyCase(final boolean enable) {
        if (enable) this.addModifier( 'i' );
        else this.removeModifier( 'i' );
        this.updatePattern();
        return this;
    }

    public VerbalExpression withAnyCase() {
        return withAnyCase(true);
    }

    public VerbalExpression searchOneLine(final boolean enable) {
        if (enable) this.removeModifier( 'm' );
        else this.addModifier( 'm' );
        this.updatePattern();
        return this;
    }

    public VerbalExpression multiple(String value) {
        value = this.sanitize(value);
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

    public VerbalExpression or(final String value) {
        if (this.prefixes.indexOf("(") == -1) this.prefixes += "(";
        if (this.suffixes.indexOf(")") == -1) this.suffixes = ")" + this.suffixes;

        this.add(")|(");
        if (value != null) this.then(value);
        return this;
    }

    public boolean testExact(final String toTest) {
        return Pattern.compile(this.pattern, this.modifiers).matcher(toTest).matches();
    }

    public boolean test(final String toTest) {
        return Pattern.compile(this.pattern, this.modifiers).matcher(toTest).find();
    }

    @Override
	public String toString() {
        return this.pattern.toString();
    }
}

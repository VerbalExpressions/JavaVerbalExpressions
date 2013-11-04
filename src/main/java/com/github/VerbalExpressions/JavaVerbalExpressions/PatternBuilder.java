package com.github.VerbalExpressions.JavaVerbalExpressions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternBuilder {

	private String prefixes = "", source = "", suffixes = "";
	private Pattern pattern;
	private int modifiers = Pattern.MULTILINE;

	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	private String sanitize(final String pValue) {
		Matcher matcher = Pattern.compile("").matcher("")
				.usePattern(Pattern.compile("[^\\w]"));
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

	public PatternBuilder add(String pValue) {
		this.source += pValue;
		return this;
	}

	public PatternBuilder build() {
		pattern = Pattern.compile(this.prefixes + this.source + this.suffixes,
				this.modifiers);
		return this;
	}

	public PatternBuilder startOfLine(boolean pEnable) {
		this.prefixes = pEnable ? "^" : "";
		return this;
	}

	public PatternBuilder startOfLine() {
		return startOfLine(true);
	}

	public PatternBuilder endOfLine(final boolean pEnable) {
		this.suffixes = pEnable ? "$" : "";
		return this;
	}

	public PatternBuilder endOfLine() {
		return endOfLine(true);
	}

	public PatternBuilder then(String pValue) {
		this.add("(" + sanitize(pValue) + ")");
		return this;
	}

	public PatternBuilder find(String value) {
		this.then(value);
		return this;
	}

	public PatternBuilder maybe(final String pValue) {
		this.add("(" + sanitize(pValue) + ")?");
		return this;
	}

	public PatternBuilder anything() {
		this.add("(.*)");
		return this;
	}

	public PatternBuilder anythingButNot(final String pValue) {
		this.add("([^" + sanitize(pValue) + "]*)");
		return this;
	}

	public PatternBuilder something() {
		this.add("(.+)");
		return this;
	}

	public PatternBuilder somethingButNot(final String pValue) {
		this.add("([^" + sanitize(pValue) + "]+)");
		return this;
	}

	public PatternBuilder lineBreak() {
		this.add("(\\n|(\\r\\n))");
		return this;
	}

	public PatternBuilder br() {
		this.lineBreak();
		return this;
	}

	public PatternBuilder tab() {
		this.add("\\t");
		return this;
	}

	public PatternBuilder word() {
		this.add("\\w+");
		return this;
	}

	public PatternBuilder anyOf(final String pValue) {
		this.add("[" + sanitize(pValue) + "]");
		return this;
	}

	public PatternBuilder any(final String value) {
		this.anyOf(value);
		return this;
	}

	public PatternBuilder range(String... pArgs) {
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

	public PatternBuilder addModifier(final char pModifier) {
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

	public PatternBuilder removeModifier(final char pModifier) {
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

	public PatternBuilder withAnyCase(boolean pEnable) {
		if (pEnable) {
			this.addModifier('i');
		} else {
			this.removeModifier('i');
		}
		return this;
	}

	public PatternBuilder withAnyCase() {
		return withAnyCase(true);
	}

	public PatternBuilder searchOneLine(boolean pEnable) {
		if (pEnable) {
			this.removeModifier('m');
		} else {
			this.addModifier('m');
		}
		return this;
	}

	public PatternBuilder multiple(final String pValue) {
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

	public PatternBuilder or(final String pValue) {
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

    @Override
    public String toString() {
        return this.getPattern().pattern();
    }

}
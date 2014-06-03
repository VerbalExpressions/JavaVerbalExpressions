JavaVerbalExpressions
=====================
[<img src="http://img.shields.io/github/release/VerbalExpressions/JavaVerbalExpressions.svg?style=flat">](https://github.com/VerbalExpressions/JavaVerbalExpressions/releases/latest)
[<img src="http://img.shields.io/badge/ported%20from-%20JSVerbalExpressions-orange.svg?style=flat">](https://github.com/VerbalExpressions/JSVerbalExpressions)

VerbalExpressions is a Java library that helps to construct difficult regular expressions.



##Getting Started

Maven Dependency:

```xml
<dependency>
  <groupId>ru.lanwen.verbalregex</groupId>
  <artifactId>java-verbal-expressions</artifactId>
  <version>1.2</version>
</dependency>
```

You can use *SNAPSHOT* dependency with adding to `pom.xml`:
```xml
<repositories>
  <repository>
    <id>ossrh</id>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
  </repository>
</repositories>
```

##Examples
```java
VerbalExpression testRegex = VerbalExpression.regex()
                                                .startOfLine().then("http").maybe("s")
	           				.then("://")
	           				.maybe("www.").anythingButNot(" ")
	           				.endOfLine()
	           				.build();

// Create an example URL
String url = "https://www.google.com";

// Use VerbalExpression's testExact() method to test if the entire string matches the regex
testRegex.testExact(url); //True

testRegex.toString(); // Outputs the regex used:
                      // ^(?:http)(?:s)?(?:\:\/\/)(?:www\.)?(?:[^\ ]*)$

```

```java
VerbalExpression testRegex = VerbalExpression.regex()
                                                .startOfLine().then("abc").or("def")
                                                .build();

String testString = "defzzz";

//Use VerbalExpression's test() method to test if parts if the string match the regex
testRegex.test(testString);       // true
testRegex.testExact(testString);  // false
testRegex.getText(testString);    // returns: def
```

Builder can be cloned:
```java
VerbalExpression regex = regex(regex().anything().addModifier('i')).endOfLine().build();
``` 

Or can be used in another regex: 
```java
VerbalExpression.Builder digits = regex().capt().digit().oneOrMore().endCapt().tab();
VerbalExpression regex2 = regex().add(digits).add(digits).build();
``` 

Feel free to use any predefined char groups: 
```java
regex().wordChar().nonWordChar()
   .space().nonSpace()
   .digit().nonDigit()
```

Define captures:
```java 
String text = "aaabcd";
VerbalExpression regex = regex()
                .find("a")
                .capture().find("b").anything().endCapture().then("cd").build();

regex.getText(text)     // returns "abcd"
regex.getText(text, 1)  // returns "b"
``` 

## More complex examples 
* [Parse long strings example](/VerbalExpressions/JavaVerbalExpressions/wiki/Parse-long-strings-example)

## Other implementations  
You can view all implementations on [VerbalExpressions.github.io](http://VerbalExpressions.github.io) 

[
[Javascript](https://github.com/VerbalExpressions/JSVerbalExpressions) - 
[PHP](https://github.com/VerbalExpressions/PHPVerbalExpressions) - 
[Python](https://github.com/VerbalExpressions/PythonVerbalExpressions) - 
[C#](https://github.com/VerbalExpressions/CSharpVerbalExpressions) - 
[Objective-C](https://github.com/VerbalExpressions/ObjectiveCVerbalExpressions) - 
[Ruby](https://github.com/ryan-endacott/verbal_expressions) - 
[Groovy](https://github.com/VerbalExpressions/GroovyVerbalExpressions) - 
[Haskell](https://github.com/VerbalExpressions/HaskellVerbalExpressions) - 
[C++](https://github.com/VerbalExpressions/CppVerbalExpressions) - ... ([moarr](https://github.com/VerbalExpressions)) ] 

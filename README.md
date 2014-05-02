JavaVerbalExpressions
=====================
VerbalExpressions is a Java library that helps to construct difficult regular expressions - ported from the wonderful [JSVerbalExpressions](https://github.com/VerbalExpressions/JSVerbalExpressions).

##Examples
```java

VerbalExpression testRegex = new VerbalExpression.Builder()
	           					 .startOfLine()
	           					 .then("http")
	           					 .maybe("s")
	           					 .then("://")
	           					 .maybe("www.")
	           					 .anythingButNot(" ")
	           					 .endOfLine()
	           					 .build();

// Create an example URL
String url = "https://www.google.com";

// Use VerbalExpression's testExact() method to test if the entire string matches
// the regex
testRegex.testExact(url); //True

testRegex.toString(); // Ouputs the regex used: 
					  // ^(http)(s)?(\:\/\/)(www\.)?([^\ ]*)$

VerbalExpression testRegex = new VerbalExpression.Builder()
                                 .startOfLine()
                                 .then("abc")
                                 .or("def")
                                 .build();

String testString = "defzzz";

//Use VerbalExpression's test() method to test if parts if the string match the regex
testRegex.test(testString); //true
testRegex.testExact(testString); //false


```														 

## Other implementations  
You can view all implementations on [VerbalExpressions.github.io](http://VerbalExpressions.github.io)
- [Javascript](https://github.com/VerbalExpressions/JSVerbalExpressions)
- [PHP](https://github.com/VerbalExpressions/PHPVerbalExpressions)
- [Python](https://github.com/VerbalExpressions/PythonVerbalExpressions)
- [C#](https://github.com/VerbalExpressions/CSharpVerbalExpressions)
- [Objective-C](https://github.com/VerbalExpressions/ObjectiveCVerbalExpressions)
- [Ruby](https://github.com/ryan-endacott/verbal_expressions)
- [Groovy](https://github.com/VerbalExpressions/GroovyVerbalExpressions)
- [Haskell](https://github.com/VerbalExpressions/HaskellVerbalExpressions)
- [C++](https://github.com/VerbalExpressions/CppVerbalExpressions)

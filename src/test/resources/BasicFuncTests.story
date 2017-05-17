Narrative: Basic Scenarios


Scenario: Something doesn't match null

Given a check that: Null object doesn't have something
When create a a regex that matches something
Then the check is false for the regex expression

Scenario: Something doesn't match empty string

Given a check that: empty string doesn't have something
When create a a regex that matches something
Then the check is false for the regex expression

Scenario: Something matches letter a

Given a check that: a
When create a a regex that matches something
Then the regular expression matches: a

Scenario: Anything matches string

Given a regular expression that matches anything
Then the regular expression does match: its a string

Scenario: Anything does not match empty string
Given a regular expression that matches anything
Then the regular expression does not match: ""

Scenario: Anything matches to space
Given a regular expression that matches anything
Then the regular expression does match: " "

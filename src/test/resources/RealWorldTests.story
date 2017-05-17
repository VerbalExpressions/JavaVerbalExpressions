Narrative:Real World Scenarios


Scenario: Telephone number regex matches with spaces

Given a regular expression that matches telephone numbers
Then the regular expression does match: +097 234 243

Scenario: Telephone number regex matches without spaces

Given a regular expression that matches telephone numbers
Then the regular expression does match: +097234243

Scenario: Telephone number regex matches with dashes

Given a regular expression that matches telephone numbers
Then the regular expression does match: +097-234-243

Scenario: Finding partial string

Given regular expression that finds Star Wars:  with one of:
|sample|
|The Phantom Menace|
|Attack of the Clones|
|Revenge Of The Sith|
|The Force Awakens|
|New Hope|
|The Empire Strikes Back|
|Return of the Jedi|
Then the regular expression does match: Star Wars: The Empire Strikes Back



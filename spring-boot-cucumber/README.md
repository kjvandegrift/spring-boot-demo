## Build Cucumber Application
``` bash
# The Cucumber feature file is located in 
# "test/resources/features/calculator.feature"

# The test/CalculatorControllerTest.java file kicks off 
# the test execution causing Cucumber to run the feature file
# Run cucumber tests
$ gradle test

# Cucumber results displayed after "CalculatorControllerTest >" in gradle results.
# Generating HTML cucumber reports requires Jenkins plugin
```
## Build Cucumber Application
``` bash
# Add plugins to build.gradle
dependencies {  
    testCompile(group: 'io.cucumber', name: 'cucumber-java', version: '4.3.0')  
    testCompile(group: 'io.cucumber', name: 'cucumber-junit', version: '4.3.0')  
    testCompile(group: 'io.cucumber', name: 'cucumber-spring', version: '4.3.0')  
  }  
}
# Add feature files to test/resources

# Run cucumber tests
$ gradle test
# Generating HTML cucumber reports requires Jenkins plugin
$ gradle allureRepot allureServe
```
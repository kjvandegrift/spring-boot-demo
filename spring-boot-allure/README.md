## Build Allure Application
``` bash
# Install Allure commandline application 
# Test report generation on gradle test results
$ allure serve /home/path/to/project/build/test-results/test
# Add plugins to build.gradle
buildscript {
    repositories {
        mavenCentral()
        jcenter()
        maven {
            url "https://plugins.gradle.org/m2/"
        }
    }
    dependencies {
        classpath("io.qameta.allure:allure-gradle:2.8.1")
    }
}

allure {
    autoconfigure = true
    version = '2.10.0'

    useJUnit5 {
        version = '2.10.0'
    }
}
# Generate allure tests
$ gradle test
# Generate HTML allure reports and open in a browser
$ gradle allureRepot allureServe
```

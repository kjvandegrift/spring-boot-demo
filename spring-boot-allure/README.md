## Build Allure Application
``` bash
# Generate allure tests
$ gradle test
# Generate HTML allure reports and open in a browser
$ gradle allureRepot allureServe
# Test report generation on gradle test results
$ allure serve /home/path/to/project/build/test-results/test
```

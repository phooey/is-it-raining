# Is it raining?
[![Build Status](https://travis-ci.org/phooey/is-it-raining.svg?branch=master)](https://travis-ci.org/phooey/is-it-raining)

## The test pyramid
After reading some very interesting articles on how to efficiently work with test automation, specifically about the so called ["Test Pyramid"](https://martinfowler.com/bliki/TestPyramid.html), I wanted to try the techniques out myself in a practical example project.

After reading the article ["The Practical Test Pyramid"](https://martinfowler.com/articles/practical-test-pyramid.html) by Ham Vocke, and studying [the source code](https://github.com/hamvocke/spring-testing) made available on GitHub, I decided to implement a similar project myself.

## The project
The project is also based on [Spring Boot](https://spring.io/projects/spring-boot) and uses the [Dark Sky API](https://darksky.net/dev) to retrieve weather information for a specified location.

Instead of implementing my own client to consume the Dark Sky API, I used the [darksky-forecast-api](https://github.com/200Puls/darksky-forecast-api) available in the [Maven](https://maven.apache.org/) Central Repository.

The development was done with the [Spring Tool Suite](https://spring.io/tools), based on [Eclipse](https://www.eclipse.org/) with the [SonarLint](https://www.sonarlint.org/) extension for static code analysis. The [EclEmma](https://www.eclemma.org/) plugin for Eclipse, and [JaCoCo Maven plug-in](https://www.eclemma.org/jacoco/trunk/doc/maven.html) is used to analyze the code coverage of the tests.

Version control was done with [Git](https://git-scm.com/), [GitHub](https://github.com/) and [EGit](https://www.eclipse.org/egit/)

## The application
The application is called *"Is it raining?"*. It is a simple web application, and has a simple front-end that uses the [HTML5 Geolocation API](https://developer.mozilla.org/en-US/docs/Web/API/Geolocation_API) to retrieve the geographic coordinates of the user. The front-end is otherwise pure HTML/CSS/JavaScript (no jQuery!) and calls a REST API on a back-end implemented with Spring Boot, with the coordinates as parameters. The REST API uses the Dark Sky API to retrieve a weather report for the location, and generates and returns JSON data with a report to state if it is raining currently at the specified location, and what the chances of rain are today.

The provided coordinates for the requested location are truncated to three decimal points, giving an accuracy of roughly 100 meters. A counter is implemented to make sure no more than 1000 API calls are made per day to the Dark Sky API, the maximum number of free API calls per day.

The application is obviously simple enough that it could be a pure front-end application, directly consuming and parsing the weather API. But since we use an API that requires a private API key, and for the purpose of putting the testing in focus, this is instead done in a back-end.

The front-end includes a ["Fork me on GitHub" ribbon](https://github.com/simonwhitaker/github-fork-ribbon-css), that links to the GitHub repository of this application. It is based on pure CSS, and therefore loads a stylesheet for it from a CDN. If the stylesheets fails to load it will be displayed as a normal link.

## The testing
The application was implemented in a test-driven way, and the idea is to have a as complete automated test-coverage as possible, while testing all functionality on as low a level in the test pyramid as possible.

### Unit tests
The base of the tests are unit tests. For the back-end they are based on [JUnit](https://junit.org), and where applicable SpringBootTest and the SpringRunner. Mocking external dependencies in the unit tests are done using [Mockito](https://site.mockito.org/).

All logic that can be tested with unit tests, e.g. the parsing of the weather data from the Dark Sky API, and the logic in the Spring Boot Controllers are tested with these tests.

The same goes for the front-end, where the unit-tests for the `app.js` file are written using [Jasmine](https://jasmine.github.io/) and executed with maven through the [Jasmin Maven Plugin](https://searls.github.io/jasmine-maven-plugin/).

### Service/Integration tests

#### Integration tests
The tests include one set of integration tests, making sure the application correctly integrates with the Dark Sky API.

This is done by using [WireMock](http://wiremock.org/) to stub out a request to the Dark Sky API by the application, and giving a canned real response stored in a JSON file, and making sure the generated rain report has parsed the informaton in the JSON data correctly. These tests do indirectly also test the third party library used to interact with the Dark Sky API, which we should assume is already tested, but they still add value in testing that our weather provider code can integrate correctly with a real JSON data response from the API.

#### Service tests
There is one set of API tests for the REST API using Spring's MockMvc to make sure the REST API provided by the application will respond to HTTP requests as expected. The logic of the controllers are already tested with unit tests, so these tests focus on that the actual HTTP responses are correct. As we use Spring's MockMvc we do not have to actually start the HTTP server to test this, making the tests much faster.

### End-to-End tests
To finally see that the application is working as expected in End-To-End use cases, there are two types of End-to-End tests that have been implemented. The End-To-End tests are really time consuming compared to all our previous tests (run-time of seconds, rather than milliseconds), and we therefore try to keep the number of test cases to a minimum and test only what has not already been tested at lower levels. The execution time of these tests is a little bit too long for doing TDD efficiently, and these tests should arguably rather be executed at a later phase than the unit tests.

#### REST API End-to-End test
There is one test for the provided REST API endpoint using SpringBootTest to actually spin up the web server, and use [REST Assured](http://rest-assured.io/) to send a real HTTP GET request to it. WireMock is then used to stub out a real response from the Dark Sky API, to be able to assert that the JSON data representing the rain report is generated correctly. This test is there to make sure that a running instance of the application actually responds to HTTP requests as expected, and this is the first time we actually completely spin up our back-end.

#### GUI End-to-End test
There is one GUI driven End-to-End test to make sure the application is working as expected when operated through it's front-end. They use SpringBootTest to spin up the whole application, [Selenium](http://rest-assured.io/) and [WebDriverManager](https://github.com/bonigarcia/webdrivermanager) to open a [Firefox](https://www.mozilla.org/en-US/firefox/) web driver that loads the front-end. The tests use Firefox in headless mode, and therefore require a Firefox version with headless support to be installed.

The test spoofs an HTML5 Geolocation response and request a rain report for the spoofed location from the back-end (also stubbed out using WireMock), and asserts that the resulting information displayed in the front-end is as expected. This test can be considered an End-to-End acceptance test.

It also makes sure that an attribution link for the Dark Sky API is displayed on the page, in accordance with the [Terms of Service](https://darksky.net/dev/docs/terms) for the Dark Sky API.

Further tests of the JavaScript logic and error-handling in the front-end are not done with Selenium tests, since it is already covered by the JavaScript unit tests (just like for the Java back-end code), to follow the test pyramid mind-set. That's why we only test a positive scenario here.

## Building and running the application:
First get your own API Key from [Dark Sky](https://darksky.net/dev), then paste your key into the file `src/main/resources/darksky.apikey.properties.example` and rename the file to `darksky.apikey.properties`.

To run the tests execute the following command:
`mvn test`

To run the tests and build an application jar file with Maven, issue the following command:
`mvn package`

The built jar file is then available in the "target" subdirectory and can be executed with the following command (exchange <VERSION> for the version you built):
`java -jar is-it-raining-<VERSION>.jar`

The application is then by default listening to HTTP connections on port 8080.

You can also use [Docker](https://www.docker.com/) and the provided `Dockerfile` to run the application inside a docker container after building the jar file with Maven.

Please note that for HTML5 geolocation to work in some browsers (e.g. Chrome 50.0+) you need to serve the application over https instead of http. This can be achieved by e.g. running the application behind a reverse proxy.

An example script `start_container.sh` is provided to show how this could be done, starting a docker container and connecting it to a network called "letsencrypt_default" where a reverse proxy is set up to forward the calls the docker container, encrypting the external communication. To set up a reverse proxy like this, take a look at [linuxserver/letsencrypt
](https://hub.docker.com/r/linuxserver/letsencrypt/).

## Continuous Integration
There is a simple `.travisyml` file in the project, and the code is automatically built, and all tests executed by [Travis](https://travis-ci.com) on all new commits and pull requests, as the service is very generously freely provided to all public open source GitHub projects.

The build results are displayed with the build status image at the top of this README, and can also be viewed on the following page:
https://travis-ci.org/phooey/is-it-raining

## Links:
* https://martinfowler.com/bliki/TestPyramid.html
* https://martinfowler.com/articles/practical-test-pyramid.html
* https://github.com/hamvocke/spring-testing

## Third party software used for developing the application:
* https://spring.io/tools
* https://www.eclipse.org/
* https://www.sonarlint.org/
* https://git-scm.com/
* https://github.com/
* https://www.eclipse.org/egit/
* https://www.eclemma.org/
* https://www.eclemma.org/jacoco/trunk/doc/maven.html
* https://travis-ci.com

## Third party software used in the application:
* https://spring.io/projects/spring-boot
* https://maven.apache.org/
* https://darksky.net/dev
* https://github.com/200Puls/darksky-forecast-api
* https://commons.apache.org/proper/commons-lang/
* https://junit.org
* https://site.mockito.org/
* http://wiremock.org/
* http://rest-assured.io/
* https://www.seleniumhq.org/
* https://github.com/bonigarcia/webdrivermanager
* https://www.mozilla.org/en-US/firefox/
* https://github.com/simonwhitaker/github-fork-ribbon-css
* https://jasmine.github.io/
* https://searls.github.io/jasmine-maven-plugin/

## Licenses:
For licenses of the used third party software, please refer to the links given above. For the license of this application, see the `LICENSE` file and/or any respective source files. For a guaranteed up-to-date list of third party software used, refer to the projects `pom.xml` file.

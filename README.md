# ImgurService API

3 APIs:

- GET /v1/images/upload/:jobId to retrieve the status of an upload images job
- POST /v1/images/upload to submit a request to upload a set of image URLs to Imgur
- GET /v1/images to get the links of all images uploaded to Imgur

# Run the Application
Once you have checkout your project in your IDE,
simply run "sbt start" to run the application or click the "run" button in Intellij IDE

# Run the Application in Docker
Simply run the following command, edit the version number as you wish
- docker build -t imgur_service_app:v1 .
- docker run -it -p 9000:9000 imgur_service_app:v1

# Test the application
simply run "sbt test" to run all unit tests and integration tests

to run the tests of a single file:
simple right click the class name and click "Run ...."

IntegrationSpec contains all the tests for integration tests

# Versioning
The application uses the following convention for versioning: **_major.minor.hotfix_**.

The following details which the version bump is appliable
- major: any non-compatible changes are added
- minor: any backward-compatible changes are added
- hotfix: all bug and hot fixes

# Scala formatting commands
sbt scalafmt

# Other Information


# TODO
- add more error handling
- add more unit tests
- use coursier to speed up sbt process

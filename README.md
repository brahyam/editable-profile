# SparkNetworks Challenge - FrontEnd
### Summary
Application that allows the modification of an user's profile including fetching and saving data
 to a remote server.

### Features
Includes FrontEnd (Android App) and Backend (NodeJs App using FeathersJs). More information about
how the backend was built and how to set it up for tests can be found in /server/README.md.

The application is structured in 2 views:
*  **Profile:** which allows only the visualization of the fields that are marked as displayed on 
platform and the edition of the most common.
*  **EditProfile:** which allows the edition of every field except for height which is only 
provided at sign up.

The application uses MVP Architecture together with dependency injection to provide the objects 
needed for each view/presenter dynamically. Also it applies the repository pattern to provide access
 to a cache of objects which are kept in sync with a local data storage and a remote data storage. 
 The local data storage provides persistence when the app subsequently opens without internet. 

### Libraries/Dependencies
* [Dagger2](http://google.github.io/dagger/) and 
[Dagger-Android](https://google.github.io/dagger//android.html) for dependency injection to achieve 
a decoupled more testable logic
* [Room](https://developer.android.com/topic/libraries/architecture/room) for storing data locally 
creating redundancy on network failure
* [Retrofit2](http://square.github.io/retrofit/) for retrieving remote data
* [Retrofit2 converter-gson](https://github.com/square/retrofit/tree/master/retrofit-converters/gson)
* [okHttp Logging Interceptor](https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor)
  for intercepting and logging all request and response made using retrofit 
for converting json to java classes automatically
* [Picasso](http://square.github.io/picasso/) for loading images efficiently into the views and some 
transformations
* [Android Debug DB](https://github.com/amitshekhariitbhu/Android-Debug-Database) 
 for live browsing DB content and debugging issues related to local data storage
* [Mockito](https://github.com/mockito/mockito)  for mocking classes for unit testing

### Setup/Run instructions
**IMPORTANT:** To be able to test the app you need to be running the backend server before starting
it. You can find the instructions about how to in /server/README.md

App can be compiled and tested using Android Studio. You will need to download build tools version
27.0.0 to be able to build the app.

It is configured to be tested with the embedded server out of the box if you use the emulator. If
you are planning to test on a handheld device you will need to find the IP of the machine that will
run the server and modify API_URL value in app/build.gradle:22 with the correct IP.

The easiest way would be to run [Ngrok](https://ngrok.com/) and get a public url for your local
server, then use that url in the API_URL value.  

For running unit tests you can run then manually on Android Studio or execute:
`./gradlew test` in the project main folder.

### Limitations

*  Current starting profile id is hardcoded in profile/ProfileActivity.java
*  Current backend server does not offer any security or file upload filtering

### Assumptions
Each of the attributes provided by the API have unique ids regardless of the type





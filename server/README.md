# SparkNetworks Challenge - Backend
### Summary
Backend NodeJs application that provides a profile data API allowing CRUD operations and storing data on a local
NeDB

### Features
The application uses FeathersJS framework to provide a profile REST API. It also provides support
for uploading images to the local filesystem.

Additionally is statically serving .json files (single_choice_attributes.json and cities.json) to
provide attribute and city data to the front end app.

To facilitate testing app is using an embedded database called NeDB which does not need to install
 any separate component. Also is already creating a profile with test information on start which id
  is hardcoded in the Android app.

### Libraries/Dependencies
* [Feathersjs](https://feathersjs.com/) An open source REST and realtime API layer for modern 
applications.
* [NeDB](https://github.com/louischatriot/nedb) Embedded persistent or in memory database for 
Node.js, 100% JavaScript, no binary dependency
* [Multer](https://github.com/expressjs/multer) Multer is a node.js middleware for handling 
multipart/form-data, which is primarily used for uploading files. 

### Setup/Run instructions
To run the server you need to install all of the dependencies first using npm.

Make sure you have [NodeJS](https://nodejs.org/) and [npm](https://www.npmjs.com/) installed.

Once installed you should be able to run these commands in a terminal:

 For node `node --version` and npm `npm --version`
 
Then go to the projects /server folder and: 

Install dependencies running : `npm install`

Start the app running : `npm start`

App will start running in port 3030. To make sure is running you can visit with your browser:
`http://localhost:3030/` 

### Endpoints
The app exposes the following endpoints:

* GET `http://localhost:3030/profiles` to retrieve a list of available profiles
* GET `http://localhost:3030/profiles/{id}` exchanging {id} for the profile id to retrieve a single
profile
* POST `http://localhost:3030/profiles` to save a new profile, sending the profile json in the body 
of the request
* PATCH `http://localhost:3030/profiles` to update an already existing profile, sending the profile
object as json in the body
* DELETE `http://localhost:3030/profiles/{id}` to delete an existing profile exchanging {id} for the
profile id
* POST `http://localhost:3030/upload` to upload a new profile image with the file with multi-part 
encoding and the name of the field as "profileImage". The method will return a json object with the
final "fileName" which is the one can be used later to retrieve it from the server 
* GET `http://localhost:3030/cities.json` to get a json with the list of cities provided for the exercise
* GET `http://localhost:3030/single_choice_attributes.json` to get a json with the list of single 
choice attributes

### Limitations
*  Server does not offer any type security or file upload filtering
*  Because of time constraints I decided not to create additional API endpoints for single choice
attributes and city data as they required specific formatting.


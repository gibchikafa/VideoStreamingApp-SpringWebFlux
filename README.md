# VideoStreamingApp-SpringWebFlux
VideoStreamingApp-SpringWebFlux

## Description
The goal of this project is to build a high intensive data streaming application using reactive web programming in Spring WebFlux. A good use case for an intensive data stream is video streaming. One client can send a stream of live video data and other clients (subscribers) can receive the stream. In this application, a user can create a channel to record live video on web browser and other users can stream the live video on web browser. This was achieved with less than a second of latency on the local machine. However, it is not the purpose of this project to discuss video encoding or video quality. The project mainly focuses on the architecture of streaming data from one client to multiple subscribers.

# Technologies used
  # Core/Backend
      Spring WebFlux (Java)
          • Reactive Rest API
          • Reactive WebSockets
          • Security with Json Web Token (JWT)
  # Database
      MongoDB.
  # Front-End
      ReactJs, Bulma CSS Framework, and Media Source Extensions for displaying video stream.
  # Testing
      JUnit5, Mockito, WebTestClient

# Running the project
  # Backend
  Import the videostreamingbackend in your favourite IDE. Have Java 1.8 or higher installed. Install MongoDB on your computer. Run main class VideoStreamingApplication as Java application. Server will start at localhost:8080.
  
  # Frontend
  Open terminal and move into videostreamingreact folder. Do npm install. This will install all dependancies of the application. Then run the following command: npm start. This will start the frontend at localhost:3000/. Go to your browser and enter localhost:3000/. You will land on the following page:
  <p align="center">
  <img src="Screenshot from 2020-04-16 10-29-36.png" alt="accessibility text">
  </p>
  

/*
 * Main app
 * 
 */

 package edu.nwmissouri.isl

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import scala.concurrent.Await
import scala.util.Failure
import scala.util.Success

/** Custom executable app */
object Main extends App {

  val host = "0.0.0.0"
  val port = 9000

  // creates Akka actors
  implicit val system: ActorSystem = ActorSystem(name = "topicapi")

  // creates Akka streams
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  // provides default execution context
  import system.dispatcher

  // used for routes
  import akka.http.scaladsl.server.Directives._

 /**
  * Define default route handler
  */
  def defaultRoute = path("") {
    get {
      val message = "Hello, Big Data Topics!"
      complete(message)
    }
  }

  // bind our route to the host and port
  val binding = Http().bindAndHandle(defaultRoute, host, port)

  // listen to the binding and log results
  binding.onComplete {
    case Success(_) => println(s"Success! Listening on port ${port}")
    case Failure(error) => println(s"Failed: ${error.getMessage}")
  }

 // await the result of the binding
  import scala.concurrent.duration._
  Await.result(binding, 3.seconds)

}

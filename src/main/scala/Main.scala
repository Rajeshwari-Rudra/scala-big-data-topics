/*
 * Main app
 * 
 */

 package edu.nwmissouri.isl

import akka.actor.ActorSystem
import akka.event.{LoggingAdapter, Logging}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.util.Failure
import scala.util.Success

/** Custom executable app */
object Main extends App {

  // read config from src/main/resources/application.conf
  val config = ConfigFactory.load()
  val host = config.getString("http.interface")
  val port = config.getInt("http.port")

  // creates Akka actors
  implicit val system: ActorSystem = ActorSystem(name = "topicapi")

  // creates Akka streams
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  // provides default execution context
  import system.dispatcher

    // get Akka logger
  val logger = Logging(system, getClass)

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
  val futureBinding = Http().newServerAt(host, port).bind(defaultRoute)

  // listen to the binding and log results
  futureBinding.onComplete {
    case Success(binding) => 
      val address = binding.localAddress
      val host =  address.getHostString
      val port = address.getPort
      val nl = sys.props("line.separator")
      system.log.info(s"${nl}Server listening at http://${host}:${port}/" )
    case Failure(error) => 
    println(s"Failed: ${error.getMessage}")
  }

 // await the result of the binding
  import scala.concurrent.duration._
  Await.result(futureBinding, 3.seconds)

}

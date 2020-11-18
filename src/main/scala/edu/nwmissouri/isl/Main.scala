/* Main app */
package edu.nwmissouri.isl

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

import scala.util.Failure
import scala.util.Success
import scala.util.Properties

import edu.nwmissouri.isl.topic.TopicRegistry
import edu.nwmissouri.isl.topic.TopicRoutes

import edu.nwmissouri.isl.user.UserRegistry
import edu.nwmissouri.isl.user.UserRoutes

/** Custom executable app */
object Main {

  //#start-http-server
  private def startHttpServer(
      routes: Route
  )(implicit system: ActorSystem[_]): Unit = {

    // Akka HTTP still needs a classic ActorSystem to start
    import system.executionContext

    // read config from src/main/resources/application.conf
    val config = ConfigFactory.load()
    val host = config.getString("http.interface")
    val devport = config.getInt("http.port")
    val port = Properties.envOrElse("PORT", devport.toString()).toInt

    // bind our routes to the host and port
    val futureBinding = Http().newServerAt(host, port).bind(routes)

    // listen to the binding and log results
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        val host = address.getHostString
        val port = address.getPort
        val nl = sys.props("line.separator")
        system.log.info(s"${nl}Server listening at http://${host}:${port}/")
      case Failure(error) =>
        val msg = error.getMessage
        system.log.error(
          s"Failed to bind HTTP endpoint, terminating system: ${msg}"
        )
        system.terminate()
    }
  }
  //#start-http-server

  //#start-http-server
  def main(args: Array[String]): Unit = {
    //#server-bootstrapping
    val rootBehavior = Behaviors.setup[Nothing] { context =>
      /** default route handler */

      def defaultRoute = path("") {
        get {
          val message = "Hello, Big Data Topics!"
          complete(message)
        }
      }

      // import user routes
      val userRegistryActor = context.spawn(UserRegistry(), "UserRegistryActor")
      context.watch(userRegistryActor)
      val uRouter = new UserRoutes(userRegistryActor)(context.system)

      // import topic routes
      val topicRegistryActor =
        context.spawn(TopicRegistry(), "TopicRegistryActor")
      context.watch(topicRegistryActor)
      val tRouter = new TopicRoutes(topicRegistryActor)(context.system)

      object AppRouter {
        val routes = defaultRoute ~ uRouter.userRoutes ~ tRouter.topicRoutes
      }

      startHttpServer(AppRouter.routes)(context.system)
      Behaviors.empty
    }
    val system = ActorSystem[Nothing](rootBehavior, "scala-big-data-topics")
    //#server-bootstrapping
  }
}

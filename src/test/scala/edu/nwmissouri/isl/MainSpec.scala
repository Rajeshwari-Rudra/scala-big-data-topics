/** Specificaiton for main app. */
package edu.nwmissouri.isl

import akka.http.scaladsl.model.{ ContentTypes, HttpEntity, StatusCodes }
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import org.scalatest._

class MainSpec extends WordSpec with Matchers with ScalatestRouteTest {

  val defaultRoute =
    get {
      concat(
        pathSingleSlash {
          val message = "Hello, Big Data Engineers!"
          val html = "<h1>" + message + "</h1>" +
            "<p><a href='/users'>Users</a></p>" +
            "<p><a href='/topics'>Topics</a></p>"
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, html))
        },
        pathPrefix("hello") {
          complete("Hello, Big Data Engineers!")
        }
      )
    }

  "The default endpoint" should {

    "return a default page for GET requests to the root path" in {
      // tests:
      Get() ~> defaultRoute ~> check {
        status === StatusCodes.OK
        val message = "Hello, Big Data Engineers!"
        val html = "<h1>" + message + "</h1>" +
          "<p><a href='/users'>Users</a></p>" +
          "<p><a href='/topics'>Topics</a></p>"
        responseAs[String] shouldEqual html
      }
    }

    "return a greeting for GET requests to /hello" in {
      // tests:
      Get("/hello") ~> defaultRoute ~> check {
        status === StatusCodes.OK
        val message = "Hello, Big Data Engineers!"
        responseAs[String] shouldEqual message
      }
    }

    "leave GET requests to other paths unhandled" in {
      // tests:
      Get("/unknown") ~> defaultRoute ~> check {
        handled shouldBe false
      }
    }

  }
}

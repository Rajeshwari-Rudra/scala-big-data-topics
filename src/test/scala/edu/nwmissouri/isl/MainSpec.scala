/** Specificaiton for main app. */
package edu.nwmissouri.isl

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import Directives._
import org.scalatest._

class MainSpec extends WordSpec with Matchers with ScalatestRouteTest {

  val defaultRoute =
    get {
      concat(
        pathSingleSlash {
          complete {
            "Hello, Big Data Topics!"
          }
        }
      )
    }

  "The default endpoint" should {

    "return a greeting for GET requests to the root path" in {
      // tests:
      Get() ~> defaultRoute ~> check {
        status === StatusCodes.OK
        val message = "Hello, Big Data Topics!"
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

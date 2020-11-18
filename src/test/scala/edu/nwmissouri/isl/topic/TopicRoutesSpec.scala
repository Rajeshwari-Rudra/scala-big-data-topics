package edu.nwmissouri.isl.topic

//#topic-routes-spec
//#test-top
import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

//#set-up
class TopicRoutesSpec
    extends WordSpec
    with Matchers
    with ScalaFutures
    with ScalatestRouteTest {
  //#test-top

  // the Akka HTTP route testkit does not yet support a typed actor system (https://github.com/akka/akka-http/issues/2036)
  // so we have to adapt for now
  lazy val testKit = ActorTestKit()
  implicit def typedSystem = testKit.system
  override def createActorSystem(): akka.actor.ActorSystem =
    testKit.system.classicSystem

  // Here we need to implement all the abstract members of TopicRoutes.
  // We use the real TopicRegistryActor to test it while we hit the Routes,
  // but we could "mock" it by implementing it in-place or by using a TestProbe
  // created with testKit.createTestProbe()
  import edu.nwmissouri.isl.topic.Topic
  import edu.nwmissouri.isl.topic.TopicRegistry
  import edu.nwmissouri.isl.topic.TopicRoutes
  val topicRegistry = testKit.spawn(TopicRegistry())
  lazy val topicRoutes = new TopicRoutes(topicRegistry).topicRoutes

  // use the json formats to marshal and unmarshall objects in the test
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import edu.nwmissouri.isl.topic.TopicJsonFormats._
  //#set-up

  //#actual-test
  "TopicRoutes" should {
    "return no topics if no present (GET /topics)" in {
      // note that there's no need for the host part in the uri:
      val request = HttpRequest(uri = "/topics")

      request ~> topicRoutes ~> check {
        status should ===(StatusCodes.OK)

        // we expect the response to be json:
        contentType should ===(ContentTypes.`application/json`)

        // and no entries should be in the list:
        entityAs[String] should ===("""{"topics":[]}""")
      }
    }
    //#actual-test

    //#testing-post
    "be able to add topics (POST /topics)" in {
      val topic = Topic("Scala", 100, "Language")
      val topicEntity =
        Marshal(topic)
          .to[MessageEntity]
          .futureValue // futureValue is from ScalaFutures

      // using the RequestBuilding DSL:
      val request = Post("/topics").withEntity(topicEntity)

      request ~> topicRoutes ~> check {
        status should ===(StatusCodes.Created)

        // we expect the response to be json:
        contentType should ===(ContentTypes.`application/json`)

        // and we know what message we're expecting back:
        entityAs[String] should ===(
          """{"description":"Topic Scala created."}"""
        )
      }
    }
    //#testing-post

    "be able to remove topics (DELETE /topics)" in {
      // topic the RequestBuilding DSL provided by ScalatestRouteSpec:
      val request = Delete(uri = "/topics/Scala")

      request ~> topicRoutes ~> check {
        status should ===(StatusCodes.OK)

        // we expect the response to be json:
        contentType should ===(ContentTypes.`application/json`)

        // and no entries should be in the list:
        entityAs[String] should ===(
          """{"description":"Topic Scala deleted."}"""
        )
      }
    }
    //#actual-test
  }
  //#actual-test

  //#set-up
}
//#set-up
//#topic-routes-spec

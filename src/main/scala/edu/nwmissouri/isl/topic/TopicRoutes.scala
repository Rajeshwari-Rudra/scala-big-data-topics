package edu.nwmissouri.isl.topic

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

import scala.concurrent.Future
import edu.nwmissouri.isl.topic.TopicRegistry._
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout

//#import-json-formats
//#topic-routes-class
class TopicRoutes(topicRegistry: ActorRef[TopicRegistry.Command])(implicit
    val system: ActorSystem[_]
) {

  //#topic-routes-class
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import TopicJsonFormats._
  //#import-json-formats

  // If ask takes more time than this to complete the request is failed
  private implicit val timeout =
    Timeout.create(system.settings.config.getDuration("app.routes.ask-timeout"))

  def getTopics(): Future[Topics] =
    topicRegistry.ask(GetTopics)
  def getTopic(name: String): Future[GetTopicResponse] =
    topicRegistry.ask(GetTopic(name, _))
  def createTopic(topic: Topic): Future[ActionPerformed] =
    topicRegistry.ask(CreateTopic(topic, _))
  def deleteTopic(name: String): Future[ActionPerformed] =
    topicRegistry.ask(DeleteTopic(name, _))

  //#all-routes
  //#topics-get-post
  //#topics-get-delete
  val topicRoutes: Route =
    pathPrefix("topics") {
      concat(
        //#topics-get-delete
        pathEnd {
          concat(
            get {
              complete(getTopics())
            },
            post {
              entity(as[Topic]) { topic =>
                onSuccess(createTopic(topic)) { performed =>
                  complete((StatusCodes.Created, performed))
                }
              }
            }
          )
        },
        //#topics-get-delete
        //#topics-get-post
        path(Segment) { name =>
          concat(
            get {
              //#retrieve-topic-info
              rejectEmptyResponse {
                onSuccess(getTopic(name)) { response =>
                  complete(response.maybeTopic)
                }
              }
              //#retrieve-topic-info
            },
            delete {
              //#topics-delete-logic
              onSuccess(deleteTopic(name)) { performed =>
                complete((StatusCodes.OK, performed))
              }
              //#topics-delete-logic
            }
          )
        }
      )
      //#topics-get-delete
    }
  //#all-routes
}

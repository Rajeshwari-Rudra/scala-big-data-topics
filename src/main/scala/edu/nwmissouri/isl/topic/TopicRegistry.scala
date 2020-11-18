package edu.nwmissouri.isl.topic

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import scala.collection.immutable

//#topic-case-classes
final case class Topic(name: String, value: Int, category: String)
final case class Topics(topics: immutable.Seq[Topic])
//#topic-case-classes

object TopicRegistry {
  // actor protocol
  sealed trait Command
  final case class GetTopics(replyTo: ActorRef[Topics]) extends Command
  final case class CreateTopic(topic: Topic, replyTo: ActorRef[ActionPerformed])
      extends Command
  final case class GetTopic(name: String, replyTo: ActorRef[GetTopicResponse])
      extends Command
  final case class DeleteTopic(name: String, replyTo: ActorRef[ActionPerformed])
      extends Command

  final case class GetTopicResponse(maybeTopic: Option[Topic])
  final case class ActionPerformed(description: String)

  def apply(): Behavior[Command] = registry(Set.empty)

  private def registry(topics: Set[Topic]): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetTopics(replyTo) =>
        replyTo ! Topics(topics.toSeq)
        Behaviors.same
      case CreateTopic(topic, replyTo) =>
        replyTo ! ActionPerformed(s"Topic ${topic.name} created.")
        registry(topics + topic)
      case GetTopic(name, replyTo) =>
        replyTo ! GetTopicResponse(topics.find(_.name == name))
        Behaviors.same
      case DeleteTopic(name, replyTo) =>
        replyTo ! ActionPerformed(s"Topic $name deleted.")
        registry(topics.filterNot(_.name == name))
    }
}
//#topic-registry-actor

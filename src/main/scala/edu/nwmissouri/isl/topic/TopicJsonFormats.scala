package edu.nwmissouri.isl.topic

import TopicRegistry.ActionPerformed

//#json-formats
import spray.json.DefaultJsonProtocol

object TopicJsonFormats {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val topicJsonFormat = jsonFormat3(Topic)
  implicit val topicsJsonFormat = jsonFormat1(Topics)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}
//#json-formats

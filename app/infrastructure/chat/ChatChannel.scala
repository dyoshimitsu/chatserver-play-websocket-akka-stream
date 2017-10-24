package infrastructure.chat

import akka.NotUsed
import akka.stream.scaladsl.{ Sink, Source }

case class ChatChannel(
    sink : Sink[String, NotUsed],
    source: Source[String, NotUsed]
)

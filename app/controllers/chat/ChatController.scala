package controllers.chat

import javax.inject.Inject

import akka.actor._
import akka.stream._
import akka.stream.scaladsl.{ Flow, Keep }
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket
import services.chat.ChatService

/**
 * WebSocket Chat Server using AKka Stream.
 */
class ChatController @Inject()(
    implicit val system: ActorSystem,
    implicit val materializer: Materializer,
    streamChatService: ChatService
) {

  def start(roomId: String) = WebSocket.accept[String, String] { request =>

    streamChatService.start(roomId).bus
  }
}

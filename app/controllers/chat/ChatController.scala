package controllers.chat

import javax.inject.Inject

import akka.actor._
import akka.stream._
import akka.stream.scaladsl.{Flow, Keep}
import domains.chat.ChatMessage
import play.api.libs.json.JsValue
import play.api.libs.streams.ActorFlow
import play.api.mvc.{Action, WebSocket}
import services.chat.ChatService
import play.api.mvc._


/**
 * WebSocket Chat Server using AKka Stream.
 */
class ChatController @Inject()(
    implicit val system: ActorSystem,
    implicit val materializer: Materializer,
    streamChatService: ChatService
) extends Controller {

  def room(roomId: String) = Action {
    val url = sys.env.getOrElse("CHAT_URL","localhost:9000")
    Ok(views.html.index(url, roomId))
  }

  def start(roomId: String) = WebSocket.accept[JsValue, JsValue] { request =>

    val userName = request.queryString("user_name").headOption.getOrElse("anon")
    val userInput: Flow[JsValue, ChatMessage, _] = ActorFlow.actorRef[JsValue, ChatMessage](out => ChatRequestActor.props(out, userName))
    val room = streamChatService.start(roomId, userName)
    val userOutPut: Flow[ChatMessage, JsValue, _] = ActorFlow.actorRef[ChatMessage, JsValue](out => ChatResponseActor.props(out,userName))

    userInput.viaMat(room.bus)(Keep.right).viaMat(userOutPut)(Keep.right)
  }
}

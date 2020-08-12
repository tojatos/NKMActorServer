package com.tosware.NKM

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import com.tosware.NKM.actors.Game._
import com.tosware.NKM.actors.NKMData.GetHexMaps
import com.tosware.NKM.actors._
import com.tosware.NKM.models._
import com.tosware.NKM.serializers.NKMJsonProtocol

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

object Main extends App with NKMJsonProtocol with SprayJsonSupport {
  implicit val system: ActorSystem = ActorSystem("NKMServer")

  val nkmData = system.actorOf(NKMData.props())
  implicit val timeout: Timeout = Timeout(2 seconds)
  val hexMaps = Await.result((nkmData ? GetHexMaps).mapTo[List[HexMap]], 2 seconds)
  val game = system.actorOf(Game.props("1", hexMaps.head))
  val skeleton =
    pathPrefix("api") {
      get {
        path("state") {
          complete((game ? GetState).mapTo[GameState])
        } ~
        path("maps") {
          complete((nkmData ? GetHexMaps).mapTo[List[HexMap]])
        }
      }
    }
  Http().newServerAt("localhost", 8080).bindFlow(skeleton)

  def test(): Unit = {
//    import system.dispatcher
    val playerNames = List("Ryszard", "Ania", "Ola")
    val characters: List[NKMCharacter] = List[NKMCharacter](
      NKMCharacter("Aqua", 12, Stat(32), Stat(43), Stat(4), Stat(34), Stat(4)),
      NKMCharacter("Dekomori Sanae", 14, Stat(32), Stat(43), Stat(4), Stat(34), Stat(4)),
      NKMCharacter("Touka", 0, Stat(34), Stat(43), Stat(4), Stat(34), Stat(5))
    )

    val touka = characters.find(_.name == "Touka").get

    playerNames.foreach(n => game ! AddPlayer(n))
//    val players = Await.result((game ? GetState).mapTo[GameState].map(s => s.players), 2 seconds)

    characters.foreach(c => game ! AddCharacter("Ola", c))

    game ! PlaceCharacter(HexCoordinates(4, 5), touka)
    game ! MoveCharacter(HexCoordinates(0, 0), touka)
  }

  test()
}

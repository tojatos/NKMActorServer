package com.tosware.NKM.serializers

import com.tosware.NKM.actors.Game._
import com.tosware.NKM.models._
import spray.json._

trait NKMJsonProtocol extends DefaultJsonProtocol {

  implicit object HexCellEffectJsonFormat extends RootJsonFormat[HexCellEffect] {
    override def write(obj: HexCellEffect): JsValue = obj match {
      case _ => deserializationError("Not implemented yet")
    }
    override def read(json: JsValue): HexCellEffect = json match {
      case _ => deserializationError("Cannot deserialize abstract class")
    }
  }

  implicit object HexCellTypeJsonFormat extends RootJsonFormat[HexCellType] {
    override def write(obj: HexCellType): JsValue = obj match {
      case Transparent => JsString("Transparent")
      case Normal => JsString("Normal")
      case Wall => JsString("Wall")
      case SpawnPoint => JsString("SpawnPoint")
    }
    override def read(json: JsValue): HexCellType = json match {
      case JsString(value) => value match {
        case "Transparent" => Transparent
        case "Normal" => Normal
        case "Wall" => Wall
        case "SpawnPoint" => SpawnPoint
        case _ => deserializationError(s"Unable to parse:\n$value")
      }
      case _ => deserializationError(s"Unable to parse:\n$json")
    }
  }

  // Start with simple ones, finish with the most complex
  // if format A depends on B, then B should be defined first (or we get a NullPointerException from spray)
  implicit val hexCoordinatesFormat: RootJsonFormat[HexCoordinates] = jsonFormat2(HexCoordinates)
  implicit val statFormat: RootJsonFormat[Stat] = jsonFormat1(Stat)
  implicit val phaseFormat: RootJsonFormat[Phase] = jsonFormat1(Phase)
  implicit val turnFormat: RootJsonFormat[Turn] = jsonFormat1(Turn)
  implicit val nkmCharacterFormat: RootJsonFormat[NKMCharacter] = jsonFormat8(NKMCharacter)
  implicit val playerFormat: RootJsonFormat[Player] = jsonFormat2(Player)
  implicit val hexCellFormat: RootJsonFormat[HexCell] = jsonFormat5(HexCell)
  implicit val hexMapFormat: RootJsonFormat[HexMap] = jsonFormat2(HexMap)
  implicit val gameStateFormat: RootJsonFormat[GameState] = jsonFormat5(GameState.apply)

  implicit val loginFormat: RootJsonFormat[Credentials] = jsonFormat2(Credentials)
  implicit val registerRequestFormat: RootJsonFormat[RegisterRequest] = jsonFormat3(RegisterRequest)

  implicit object EventJsonFormat extends RootJsonFormat[Event] {
    // Events
    implicit val playerAddedFormat: RootJsonFormat[PlayerAdded] = jsonFormat1(PlayerAdded)
    implicit val characterAddedFormat: RootJsonFormat[CharacterAdded] = jsonFormat2(CharacterAdded)
    implicit val characterPlacedFormat: RootJsonFormat[CharacterPlaced] = jsonFormat2(CharacterPlaced)
    implicit val characterMovedFormat: RootJsonFormat[CharacterMoved] = jsonFormat2(CharacterMoved)
    implicit val mapSetFormat: RootJsonFormat[MapSet] = jsonFormat1(MapSet)

    val playerAddedId: JsString = JsString("PlayerAdded")
    val characterAddedId: JsString = JsString("CharacterAdded")
    val characterPlacedId: JsString = JsString("CharacterPlaced")
    val characterMovedId: JsString = JsString("CharacterMoved")
    val mapSetId: JsString = JsString("MapSet")

    override def write(obj: Event): JsValue = obj match {
      case e: PlayerAdded => JsArray(Vector(playerAddedId, playerAddedFormat.write(e)))
      case e: CharacterAdded => JsArray(Vector(characterAddedId, characterAddedFormat.write(e)))
      case e: CharacterPlaced => JsArray(Vector(characterPlacedId, characterPlacedFormat.write(e)))
      case e: CharacterMoved => JsArray(Vector(characterMovedId, characterMovedFormat.write(e)))
      case e: MapSet => JsArray(Vector(mapSetId, mapSetFormat.write(e)))
    }
    override def read(json: JsValue): Event = json match {
      case JsArray(Vector(`playerAddedId`, jsEvent)) => playerAddedFormat.read(jsEvent)
      case JsArray(Vector(`characterAddedId`, jsEvent)) => characterAddedFormat.read(jsEvent)
      case JsArray(Vector(`characterPlacedId`, jsEvent)) => characterPlacedFormat.read(jsEvent)
      case JsArray(Vector(`characterMovedId`, jsEvent)) => characterMovedFormat.read(jsEvent)
      case JsArray(Vector(`mapSetId`, jsEvent)) => mapSetFormat.read(jsEvent)
    }
  }


}

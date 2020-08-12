package com.tosware.NKM.serializers

import reflect.runtime.universe.TypeTag
import scala.reflect.runtime.{universe => ru}
import com.tosware.NKM.actors.Game._
import com.tosware.NKM.models._
import spray.json._

import scala.reflect.ClassTag

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
  implicit val nkmCharacterFormat: RootJsonFormat[NKMCharacter] = jsonFormat7(NKMCharacter)
  implicit val playerFormat: RootJsonFormat[Player] = jsonFormat2(Player)
  implicit val hexCellFormat: RootJsonFormat[HexCell] = jsonFormat5(HexCell)
  implicit val hexMapFormat: RootJsonFormat[HexMap] = jsonFormat2(HexMap)
  implicit val gameStateFormat: RootJsonFormat[GameState] = jsonFormat5(GameState)

  // Events
  implicit val playerAddedFormat: RootJsonFormat[PlayerAdded] = jsonFormat1(PlayerAdded)
  implicit val characterAddedFormat: RootJsonFormat[CharacterAdded] = jsonFormat2(CharacterAdded)
  implicit val characterPlacedFormat: RootJsonFormat[CharacterPlaced] = jsonFormat2(CharacterPlaced)
  implicit val characterMovedFormat: RootJsonFormat[CharacterMoved] = jsonFormat2(CharacterMoved)

  val eventFormatMap: Map[Class[_ <: Event], RootJsonFormat[_ <: Event]] = Map(
    classOf[PlayerAdded] -> playerAddedFormat,
    classOf[CharacterAdded] -> characterAddedFormat,
    classOf[CharacterPlaced] -> characterPlacedFormat,
    classOf[CharacterMoved] -> characterMovedFormat,
  )

  def eventType1[P1 :Event, T <: Product :ClassTag](construct: P1 => T): RootJsonFormat[T] = jsonFormat1(construct)

  def getEventId(t: Class[_ <: Event]): String = t.getName

  val idEventMap: Map[String, Class[_ <: Event]] = eventFormatMap.map {
    case (k, v) => (getEventId(k), k)
  }
  val eventIdMap: Map[Class[_ <: Event], String] = idEventMap.map(_.swap)


  implicit object EventJsonFormat extends RootJsonFormat[Event] {
    override def write(obj: _ <: Event): JsValue = {
      val (id, json) = obj match {
        case e: PlayerAdded => (eventIdMap(classOf[PlayerAdded]), playerAddedFormat.write(e))
        case e: CharacterAdded => (eventIdMap(classOf[CharacterAdded]), characterAddedFormat.write(e))
        case e: CharacterPlaced => (eventIdMap(classOf[CharacterPlaced]), characterPlacedFormat.write(e))
        case e: CharacterMoved => (eventIdMap(classOf[CharacterMoved]), characterMovedFormat.write(e))
      }
      JsArray(Vector(JsString(id), json))
      }

     override def read(json: JsValue): Event = json match {
       case JsArray(Vector(JsString(id), jsEvent)) => eventFormatMap(idEventMap(id)).read(jsEvent)
     }
  }
}

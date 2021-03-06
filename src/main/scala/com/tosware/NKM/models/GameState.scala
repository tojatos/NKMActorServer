package com.tosware.NKM.models

case class GameState(hexMap: HexMap,
                     characterIdsOutsideMap: List[String],
                     phase: Phase,
                     turn: Turn,
                     players: List[Player])
object GameState {
  def empty: GameState = GameState(HexMap("empty", List()), List(), Phase(0), Turn(0), List())
}

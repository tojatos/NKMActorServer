package com.tosware.NKM.models

case class Stat(value: Int)

case class NKMCharacter(id: String,
                        name: String,
                        healthPoints: Int,
                        attackPoints: Stat,
                        basicAttackRange: Stat,
                        speed: Stat,
                        psychicalDefense: Stat,
                        magicalDefense: Stat)

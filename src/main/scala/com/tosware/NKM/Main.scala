package com.tosware.NKM

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.tosware.NKM.services.HttpService

import scala.language.postfixOps

object Main extends App with HttpService {
  override implicit val system: ActorSystem = ActorSystem("NKMServer")

  sys.env.getOrElse("DEBUG", "false").toBooleanOption match {
    case Some(true) =>
      Http().newServerAt("0.0.0.0", 8080).bind(routes)
      println("Started http server")
    case _ =>
      Http().newServerAt("0.0.0.0", 8080).enableHttps(getHttps).bindFlow(routes)
      println("Started https server")
  }


}

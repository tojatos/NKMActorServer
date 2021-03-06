package api

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.tosware.NKM.models.{Credentials, JwtContent, RegisterRequest}
import com.tosware.NKM.services.HttpService
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import pdi.jwt.{JwtAlgorithm, JwtSprayJson}
import spray.json._

import scala.language.postfixOps
import scala.util.Success

class ApiSpec extends AnyWordSpec with Matchers with ScalatestRouteTest with HttpService
{
  "API" must {
    "refuse incorrect register attempt" in {
      Post("/api/register") ~> Route.seal(routes) ~> check {
        status shouldEqual BadRequest
      }
    }
    "refuse incorrect login attempt" in {
      Post("/api/login") ~> Route.seal(routes) ~> check {
        status shouldEqual BadRequest
      }
    }
    "refuse invalid credentials" in {
      Post("/api/login", Credentials("username", "wrong_password")) ~> Route.seal(routes) ~> check {
        status shouldEqual Unauthorized
      }
    }
    "allow registration" in {
      Post("/api/register", RegisterRequest("username", "username@example.com", "password")) ~> routes ~> check {
        status shouldEqual Created
      }
    }
    "return token when valid credentials are provided" in {
      Post("/api/register", RegisterRequest("test", "test@example.com", "password")) ~> routes
      Post("/api/login", Credentials("test", "password")) ~> routes ~> check {
        status shouldBe OK

        val token = responseAs[String]
        JwtSprayJson.decode(token, jwtSecretKey, Seq(JwtAlgorithm.HS256)) match {
          case Success(claim) => claim.content.parseJson.convertTo[JwtContent] shouldEqual JwtContent("test")
          case _ => fail
        }
      }
    }
    "disallow registration for taken username" in {
      Post("/api/register", RegisterRequest("test_user", "test_user@example.com", "password")) ~> routes
      Post("/api/register", RegisterRequest("test_user", "test_user2@example.com", "password")) ~> routes ~> check {
        status shouldEqual Conflict
      }
    }
    "disallow registration for taken email" in {
      Post("/api/register", RegisterRequest("test_user", "test_user@example.com", "password")) ~> routes
      Post("/api/register", RegisterRequest("test_user2", "test_user@example.com", "password")) ~> routes ~> check {
        status shouldEqual Conflict
      }
    }
  }
}

package controllers

import scala.concurrent.ExecutionContext.Implicits.global

import play.api.mvc._
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.libs.Crypto

class LoginController(ws: WSClient, fbConfig: LoginController.FacebookConfig, configSecret: String)
    extends Controller {

  val loginPage = Action { implicit request =>
    val redirectUri = routes.LoginController.loginCallback.absoluteURL()
    val fbUrl =
      s"https://www.facebook.com/v2.8/dialog/oauth?client_id=${fbConfig.appId}&redirect_uri=$redirectUri"
    SeeOther(fbUrl)
  }

  def loginCallback = Action.async { implicit request =>
    val redirectUri = routes.LoginController.loginCallback.absoluteURL()
    val code = request.queryString("code").head
    val tokenUrl =
      s"https://graph.facebook.com/v2.8/oauth/access_token?client_id=${fbConfig.appId}&redirect_uri=$redirectUri&client_secret=${fbConfig.appSecret}&code=$code"
    ws.url(tokenUrl).get().flatMap { tokenResponse =>
      val token = (tokenResponse.json \ "access_token").as[String]
      val meUrl = s"https://graph.facebook.com/v2.8/me?access_token=$token"
      ws.url(meUrl).get().map { meResponse =>
        val userName = (meResponse.json \ "name").as[String]
        SeeOther(routes.RootController.index.url).withSession(
          request.session + ("userName" -> userName)
        )
      }
    }
  }

  def loginSign(name: String, secret: String) = Action {
    if (secret != configSecret) {
      Unauthorized("bad secret")
    } else {
      Ok(Crypto.encryptAES(name))
    }
  }

  def loginPresigned(token: String) = Action { request =>
    val userName = Crypto.decryptAES(token)
    SeeOther(routes.RootController.index.url).withSession(
      request.session + ("userName" -> userName)
    )
  }

  val logout = Action {
    Ok("logged out").withNewSession
  }

}

object LoginController {
  case class FacebookConfig(appId: String, appSecret: String)
}

package controllers

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.mvc._
import play.api.libs.json.Json
import services.{Messages, WishService}

class RootController(wishes: WishService, secret: String, msgs: Messages) extends Controller {

  def withUser(
      f: String => Request[AnyContent] => Future[Result]): Action[AnyContent] =
    Action.async { request =>
      request.session
        .get("userName")
        .map { userName =>
          f(userName)(request)
        }
        .getOrElse {
          Future.successful(
            SeeOther(routes.LoginController.loginPage.url)
          )
        }
    }

  val index = withUser { userName => implicit request =>
    wishes.getWishFor(userName).flatMap {
      case (wish, pseudonym, wisheeO) =>
        wishes.participants().flatMap { participants =>
          wisheeO.map { wishee =>
            wishes.getWishFor(wishee).map {
              case (letter, signature, _) =>
                Ok(views.html.view(letter, signature))
            }
          }.getOrElse(
            Future.successful(
              Ok(
                views.html.edit(userName, wish, pseudonym, participants, msgs)
              )
            )
          )
        }
    }
  }

  val setWish = withUser { userName => implicit request =>
    val form = request.body.asFormUrlEncoded.get
    val newWish = form("wishText").headOption.getOrElse("")
    val pseudonym = form("pseudonym").headOption.getOrElse(userName)
    wishes.setWishFor(userName, newWish, pseudonym).map { _ =>
      SeeOther(routes.RootController.index.url).flashing(
        "success" -> msgs.msgEnRoute
      )
    }
  }

  def shuffle(providedSecret: String) = withUser { userName => request =>
    if (providedSecret == secret) {
      wishes.shuffle().map(_ => Ok("shuffled"))
    } else {
      Future.successful(Unauthorized("bad secret"))
    }
  }

}

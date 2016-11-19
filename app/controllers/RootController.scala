package controllers

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.mvc._
import play.api.libs.json.Json
import services.WishService

class RootController(wishes: WishService) extends Controller {

  def withUser(f: String => Request[AnyContent] => Future[Result]): Action[AnyContent] =
    Action.async { request =>
      request.session.get("userName").map { userName =>
        f(userName)(request)
      }.getOrElse {
        Future.successful(
          SeeOther(routes.LoginController.loginPage.url)
        )
      }
    }

  val index = withUser { userName => implicit request =>
    wishes.getWishFor(userName).map { case (wish, pseudonym) =>
      Ok(
        views.html.index(userName, wish, pseudonym)
      )
    }
  }

  val setWish = withUser { userName => implicit request =>
    val form = request.body.asFormUrlEncoded.get
    val newWish = form("wishText").headOption.getOrElse("")
    val pseudonym = form("pseudonym").headOption.getOrElse(userName)
    wishes.setWishFor(userName, newWish, pseudonym).map { _ =>
      SeeOther(routes.RootController.index.url).flashing(
        "success" -> "Message is on its way."
      )
    }
  }

}

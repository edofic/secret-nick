package controllers

import play.api.mvc._
import play.api.libs.json.Json
import services.WishService

class RootController(wishes: WishService) extends Controller {

  def withUser(f: String => Request[AnyContent] => Result): Action[AnyContent] =
    Action { request =>
      request.session.get("userName").map { userName =>
        f(userName)(request)
      }.getOrElse {
        SeeOther(routes.LoginController.loginPage.url)
      }
    }

  val index = withUser { userName => implicit request =>
    val (wish, pseudonym) = wishes.getWishFor(userName)
    Ok(
      views.html.index(userName, wish, pseudonym)
    )
  }

  val setWish = withUser { userName => implicit request =>
    val form = request.body.asFormUrlEncoded.get
    val newWish = form("wishText").headOption.getOrElse("")
    val pseudonym = form("pseudonym").headOption.getOrElse(userName)
    wishes.setWithFor(userName, newWish, pseudonym)
    SeeOther(routes.RootController.index.url).flashing(
      "success" -> "Message is on its way."
    )
  }

}

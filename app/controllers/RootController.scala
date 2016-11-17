package controllers

import play.api.mvc._
import play.api.libs.json.Json
import services.WishService

class RootController(wishes: WishService) extends Controller {

  def withUser(f: String => Request[AnyContent] => Result): Action[AnyContent] =
    Action { request =>
      val userName: String = request.session("userName")
      f(userName)(request)
    }

  val index = withUser { userName => implicit request =>
    val wish = wishes.getWishFor(userName)
    Ok(
      views.html.index(userName, wish)
    )
  }

  val setWish = withUser { userName => implicit request =>
    val newWish = request.body.asFormUrlEncoded.flatMap(_("wishText").headOption).getOrElse("")
    wishes.setWithFor(userName, newWish)
    SeeOther(routes.RootController.index.url).flashing(
      "success" -> "Message is on its way."
    )
  }

}

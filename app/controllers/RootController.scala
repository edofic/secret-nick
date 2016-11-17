package controllers

import play.api.mvc._
import play.api.libs.json.Json
import services.WishService

class RootController(wishes: WishService) extends Controller {


  val index: Action[AnyContent] = Action { implicit request =>
    val name = request.session("name")
    val wish = wishes.getWishFor(name)
    Ok(
      views.html.index(name, wish)
    )
  }

  val setWish = Action { implicit request =>
    val newWish = request.body.asFormUrlEncoded.flatMap(_("wishText").headOption).getOrElse("")
    val name = request.session("name")
    wishes.setWithFor(name, newWish)
    SeeOther(routes.RootController.index.url).flashing(
      "success" -> "Message is on its way."
    )
  }

  def setState(name: String, wish: String) = Action { implicit request =>
    wishes.setWithFor(name, wish)
    Ok("done").withSession(request.session + ("name", name))
  }

}

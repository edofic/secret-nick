package controllers

import play.api.mvc._

class RootController extends Controller {

  val index: Action[AnyContent] = Action { implicit request =>
    Ok("hello world")
  }

}

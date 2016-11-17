package controllers

import play.api.mvc._
import play.api.libs.json.Json

class LoginController extends Controller {

  def loginPage(name: String) = Action { request =>
    SeeOther(routes.RootController.index.url).withSession(
      request.session + ("userName", name)
    )
  }

}


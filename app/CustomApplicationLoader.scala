import play.api._
import play.api.libs.ws.ahc.{AhcWSClient, AhcWSClientConfig}

import router.Routes

class CustomApplicationLoader extends ApplicationLoader {
  def load(context: ApplicationLoader.Context) = {
    new Components(context).application
  }

  class Components(context: ApplicationLoader.Context) extends BuiltInComponentsFromContext(context) {

    lazy val ws = AhcWSClient(AhcWSClientConfig())

    lazy val wishService = services.WishService.InMemory
    lazy val fbConfig = controllers.LoginController.FacebookConfig(
      configuration.getString("fb.id").get,
      configuration.getString("fb.secret").get
    )

    lazy val rootController = new controllers.RootController(wishService)
    lazy val loginController = new controllers.LoginController(ws, fbConfig)
    lazy val assets = new controllers.Assets(httpErrorHandler)


    lazy val router = new Routes(
      httpErrorHandler,
      rootController,
      loginController,
      assets
    )
  }
}

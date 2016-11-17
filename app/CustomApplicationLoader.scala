import play.api._
import router.Routes

class CustomApplicationLoader extends ApplicationLoader {
  def load(context: ApplicationLoader.Context) = {
    new Components(context).application
  }

  class Components(context: ApplicationLoader.Context) extends BuiltInComponentsFromContext(context) {

    lazy val wishService = services.WishService.InMemory
    lazy val rootController = new controllers.RootController(wishService)
    lazy val assets = new controllers.Assets(httpErrorHandler)

    lazy val router = new Routes(
      httpErrorHandler,
      rootController,
      assets
    )
  }
}

import play.api._
import play.api.libs.ws.ahc.{AhcWSClient, AhcWSClientConfig}
import slick.driver.PostgresDriver

import router.Routes

class CustomApplicationLoader extends ApplicationLoader {
  def load(context: ApplicationLoader.Context) = {
    val components = new Components(context)
    components.applicationEvolutions // trigger lazy val
    components.application
  }

  class Components(context: ApplicationLoader.Context)
      extends BuiltInComponentsFromContext(context)
      with db.slick.SlickComponents
      with db.slick.evolutions.SlickEvolutionsComponents
      with db.evolutions.EvolutionsComponents
      with db.HikariCPComponents {

    lazy val ws = AhcWSClient(AhcWSClientConfig())
    lazy val dbConfig =
      api.dbConfig[PostgresDriver](db.slick.DbName("default"))

    lazy val wishService = new services.WishService.DbBased(dbConfig)
    lazy val fbConfig = controllers.LoginController.FacebookConfig(
      configuration.getString("fb.id").get,
      configuration.getString("fb.secret").get
    )
    lazy val shuffleSecret = configuration.getString("shuffleSecret").get

    lazy val msgs = configuration.getString("lang").get match {
      case "en" => services.Messages.En
      case "sl" => services.Messages.Sl
    }

    lazy val rootController =
      new controllers.RootController(wishService, shuffleSecret, msgs)
    lazy val loginController = new controllers.LoginController(ws, fbConfig, shuffleSecret)
    lazy val assets = new controllers.Assets(httpErrorHandler)

    lazy val router = new Routes(
      httpErrorHandler,
      rootController,
      loginController,
      assets
    )
  }
}

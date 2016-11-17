package services

trait WishService {
  def getWishFor(name: String): String
  def setWithFor(name: String, wish: String): Unit
}

object WishService {
  private var state: Map[String, String] = Map()
  object InMemory extends WishService {
    def getWishFor(name: String): String =
      state.get(name).getOrElse("")

    def setWithFor(name: String, wish: String): Unit = {
      state = state.updated(name, wish)
    }
  }

}

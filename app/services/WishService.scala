package services

trait WishService {
  def getWishFor(name: String): (String, String)
  def setWithFor(name: String, wish: String, pseudonym: String): Unit
}

object WishService {
  private var state: Map[String, (String, String)] = Map()
  object InMemory extends WishService {
    def getWishFor(name: String): (String, String) =
      state.get(name).getOrElse(("", ""))

    def setWithFor(name: String, wish: String, pseudonym: String): Unit = {
      state = state.updated(name, (wish, pseudonym))
    }
  }

}

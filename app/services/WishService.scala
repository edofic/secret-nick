package services

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import slick.backend.DatabaseConfig
import slick.driver.PostgresDriver

trait WishService {
  def getWishFor(name: String): Future[(String, String)]
  def setWishFor(name: String, wish: String, pseudonym: String): Future[Unit]
  def participants(): Future[Seq[String]]
}

object WishService {

  object InMemory extends WishService {
    private[this] var state: Map[String, (String, String)] = Map()
    def getWishFor(name: String): Future[(String, String)] = Future.successful(
      state.get(name).getOrElse(("", ""))
    )

    def setWishFor(name: String, wish: String, pseudonym: String): Future[Unit] =
      Future.successful {
        state = state.updated(name, (wish, pseudonym))
      }

    def participants(): Future[Seq[String]] = Future.successful {
      state.collect{
        case (name, (wish, pseudonym)) if wish.trim.length > 0 && pseudonym.trim.length > 0 => name
      }.toSeq
    }
  }

  class DbBased(dbConfig: DatabaseConfig[PostgresDriver]) extends WishService {
    import dbConfig.driver.api._
    import models.Wish

    def getWishFor(name: String): Future[(String, String)] = {
      val query = for {
        wish <- Wish.table
        if wish.name === name
      } yield (wish.wish, wish.pseudonym)
      dbConfig.db.run(query.result).map(
        _.headOption getOrElse ("", "")
      )
    }

    def setWishFor(name: String, wish: String, pseudonym: String): Future[Unit] = {
      val query = Wish.table.insertOrUpdate(
        Wish(name, pseudonym, wish)
      )
      dbConfig.db.run(query).map(_ => ())
    }

    def participants(): Future[Seq[String]] = {
      val query = for {
        wish <- Wish.table
        if wish.wish.trim.length > 0
        if wish.pseudonym.trim.length > 0
      } yield wish.name
      dbConfig.db.run(query.result)
    }

  }

}

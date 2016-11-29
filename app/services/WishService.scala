package services

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

import slick.backend.DatabaseConfig
import slick.driver.PostgresDriver

trait WishService {
  def getWishFor(name: String): Future[(String, String, Option[String])]
  def setWishFor(name: String, wish: String, pseudonym: String): Future[Unit]
  def participants(): Future[Seq[String]]
  def shuffle(): Future[Unit]
}

object WishService {

  object InMemory extends WishService {
    private[this] var state: Map[String, (String, String, Option[String])] =
      Map()
    def getWishFor(name: String): Future[(String, String, Option[String])] =
      Future.successful(
        state.get(name).getOrElse(("", "", None))
      )

    def setWishFor(name: String,
                   wish: String,
                   pseudonym: String): Future[Unit] =
      Future.successful {
        state = state.updated(name, (wish, pseudonym, None))
      }

    def participants(): Future[Seq[String]] = Future.successful {
      state.collect {
        case (name, (wish, pseudonym, wishee))
            if wish.trim.length > 0 && pseudonym.trim.length > 0 =>
          name
      }.toSeq
    }

    def shuffle(): Future[Unit] = Future.successful {
      val names = Random.shuffle(state.keys).toVector
      if (names.length < 2) {
        ()
      } else {
        val wishees = names.tail :+ names.head
        val allocations = (names zip wishees).toMap
        state = state.map {
          case (name, (wish, pseudonym, currentWishee)) =>
            val newWishee = allocations.get(name).orElse(currentWishee)
            (name, (wish, pseudonym, newWishee))
        }
      }
    }
  }

  class DbBased(dbConfig: DatabaseConfig[PostgresDriver]) extends WishService {
    import dbConfig.driver.api._
    import models.Wish

    def getWishFor(name: String): Future[(String, String, Option[String])] = {
      val query = for {
        wish <- Wish.table
        if wish.name === name
      } yield (wish.wish, wish.pseudonym, wish.wishee)
      dbConfig.db
        .run(query.result)
        .map(
          _.headOption getOrElse ("", "", None)
        )
    }

    def setWishFor(name: String,
                   wish: String,
                   pseudonym: String): Future[Unit] = {
      val query = Wish.table.insertOrUpdate(
        Wish(name, pseudonym, wish, None)
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

    def shuffle(): Future[Unit] = {
      participants().flatMap { participantList =>
        if (participantList.size < 2) {
          Future.successful(())
        } else {
          val names = Random.shuffle(participantList).toVector
          val wishees = names.tail :+ names.head
          val allocations = (names zip wishees).toMap

          val updates = (names zip wishees).map {
            case (name, wishee) =>
              Wish.table
                .filter(_.name === name)
                .map(_.wishee)
                .update(Some(wishee))
          }
          dbConfig.db.run(DBIO.sequence(updates)).map(_ => ())
        }
      }
    }

    /*
      val participants: Seq[String] = ???
      val names = Random.shuffle(participants).toVector
      if (names.length < 2) {
        ()
      } else {
        val wishees = names.tail :+ names.head
        val allocations = (names zip wishees).toMap
        ???
      }
   */
  }

}

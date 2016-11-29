package models

import slick.driver.PostgresDriver.api._

case class Wish(
    name: String,
    pseudonym: String,
    wish: String,
    wishee: Option[String]
)

object Wish {
  class Wishes(tag: Tag) extends Table[Wish](tag, "wishes") {
    def name = column[String]("name", O.PrimaryKey)
    def pseudonym = column[String]("pseudonym")
    def wish = column[String]("wish")
    def wishee = column[Option[String]]("wishee")

    def * =
      (name, pseudonym, wish, wishee) <> ((Wish.apply _).tupled, Wish.unapply)
  }

  val table = TableQuery[Wishes]
}

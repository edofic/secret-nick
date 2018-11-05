package services

trait Messages {
  val hi: String
  val logout: String
  val dearNick: String
  val msgEnRoute: String
  val signature: String
  val pseudonym: String
  val send: String
  val participants: String
}

object Messages {
  object En extends Messages {
    val hi = "Hi"
    val logout = "logout"
    val dearNick = "Dear St. Niklaus ..."
    val msgEnRoute = "Message is on its way."
    val signature = "Yours truly,"
    val pseudonym = "Make up a name"
    val send = "Send"
    val participants = "Participant list"
  }
  object Sl extends Messages {
    val hi = "Zdravo"
    val logout = "Odjava"
    val dearNick = "Dragi Miklavž ..."
    val msgEnRoute = "Sporocilo je na poti."
    val signature = "Lep pozdrav,"
    val pseudonym = "izmisli si ime"
    val send = "Pošlji"
    val participants = "Sodelujoči"
  }
}

package utils

import dao.JanusClient.jg
import lib.StringContainer
import models.fields.ModelUserName
import models.{Model, UserModel}
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

object Bootstrap extends App {

  // Create a new application context

  val appBldr: Application = new GuiceApplicationBuilder().build


  val mockUser1 = UserModel(userName = StringContainer[ModelUserName]("Franklin"))
  val mockUser2 = UserModel(userName = StringContainer[ModelUserName]("Rienzi"))
  val mockUsers = Seq(mockUser1, mockUser2)

  private def setUp(): Unit = mockUsers.foreach(mu => UserModel.add(mu, jg))

  // Main
  setUp()

  println(jg.V().has(Model.Name, "Rienzi"))


}

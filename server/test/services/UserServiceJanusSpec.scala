package services

import models.UserModel
import org.scalatest.FunSpec
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito.when

import scala.concurrent.Future

class UserServiceJanusSpec extends FunSpec with MockitoSugar {


  val userServiceMock = mock[UserServiceJanus]


  describe("") {


    it("") {

      when(userServiceMock.findAllUsers).thenReturn(Future { List.empty[UserModel] })

    }

  }

}

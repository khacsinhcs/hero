package controllers

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test.{FakeRequest, Injecting}
import play.api.test.Helpers._

class HostControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {
  "Host Api GET" should {
    "Get specific host with correct name " in {
      val request = FakeRequest(GET, "/api/host/DEV")
      val response = route(app, request).get
      status(response) mustBe OK
    }
  }
}

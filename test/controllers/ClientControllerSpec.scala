package controllers

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.libs.json.{JsObject, JsString, JsValue}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Injecting}

class ClientControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {
  "POST to client" should {
    "save success" in {
      val json: JsValue = JsObject(Seq(
        "short_name" -> JsString("sinhle"),
        "api_key" -> JsString("whatthefuck")
      ))

      val request = FakeRequest(POST, "/api/client").withJsonBody(json)
      val response = route(app, request).get
      status(response) mustBe OK
    }
  }

}

package controllers

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.libs.json.{JsObject, JsString, JsValue}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Injecting}

class HostControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {
  "Retrieve host" should {
    "Get specific host with correct name " in {
      val request = FakeRequest(GET, "/api/host/DEV")
      val response = route(app, request).get
      status(response) mustBe OK
    }

    "Get specific host with wrong name " in {
      val request = FakeRequest(GET, "/api/host/HELLO")
      val response = route(app, request).get
      status(response) mustBe NOT_FOUND
    }

    "Get all host" in {
      val request = FakeRequest(GET, "/api/hosts")
      val response = route(app, request).get
      status(response) mustBe OK
      val value = contentAsJson(response)
      println(value)
    }
  }

  "Create new Host" should {
    "success" in {
      val json: JsValue = JsObject(Seq(
        "name" -> JsString("hello"),
        "gateway" -> JsString("gateway"),
        "mls" -> JsString("mls"),
        "web" -> JsString("web")
      ))

      val request = FakeRequest(POST, "/api/host").withJsonBody(json)
      val response = route(app, request).get
      status(response) mustBe OK
    }

    "fail with missing information" in {
      val json: JsValue = JsObject(Seq(
        "name" -> JsString("hello"),
        "mls" -> JsString("mls"),
        "web" -> JsString("web")
      ))

      val request = FakeRequest(POST, "/api/host").withJsonBody(json)
      val response = route(app, request).get
      status(response) mustBe BAD_REQUEST
    }

    "Accept redundant value" in {
      val json: JsValue = JsObject(Seq(
        "name" -> JsString("hello"),
        "gateway" -> JsString("gateway"),
        "mls" -> JsString("mls"),
        "web" -> JsString("web"),
        "something" -> JsString("Some thing weird")
      ))
      val request = FakeRequest(POST, "/api/host").withJsonBody(json)
      val response = route(app, request).get
      status(response) mustBe OK
    }
  }

  "Delete host" should {
    "success and then can't get this host" in {
      val deleteRequest = FakeRequest(DELETE, "/api/host/DEV")
      val deleteResponse = route(app, deleteRequest).get
      status(deleteResponse) mustBe OK

      val getRequest = FakeRequest(GET, "/api/host/DEV")
      val getResponse = route(app, getRequest).get
      status(getResponse) mustBe NOT_FOUND
    }
  }
}

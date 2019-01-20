package model

object serverdto {

  case class Host(name: String, gateway: String, web: String, host: String)
  import play.api.libs.json._

  implicit val hostWrite = new Writes[Host] {
    override def writes(o: Host): JsValue = Json.obj(
      "name" -> o.name,
      "gateway" -> o.gateway,
      "web" -> o.web,
      "host" -> o.host
    )
  }

  case class Client(shortName: String, apiKey: String)

}


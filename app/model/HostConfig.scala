package model

object HostConfig {
  case class Host(name: String, gateway: String, web: String, mls: String)

  import play.api.libs.functional.syntax._
  import play.api.libs.json._

  implicit val hostWrite = new Writes[Host] {
    override def writes(o: Host): JsValue = Json.obj(
      "name" -> o.name,
      "gateway" -> o.gateway,
      "web" -> o.web,
      "mls" -> o.mls
    )
  }

  implicit val hostRead: Reads[Host] = (
    (JsPath \ "name").read[String] and
      (JsPath \ "gateway").read[String] and
      (JsPath \ "web").read[String] and
      (JsPath \ "mls").read[String]
    ) (Host.apply _)
}
package model

import com.alab.mvc.Data

object HostConfig {
  case class Host(name: String, gateway: String, web: String, mls: String) extends Serializable with Data

  import play.api.libs.functional.syntax._
  import play.api.libs.json._

  implicit val hostWrite: Writes[Host] = (host: Host) => Json.obj(
    "name" -> host.name,
    "gateway" -> host.gateway,
    "web" -> host.web,
    "mls" -> host.mls
  )

  implicit val hostRead: Reads[Host] = (
    (JsPath \ "name").read[String] and
      (JsPath \ "gateway").read[String] and
      (JsPath \ "web").read[String] and
      (JsPath \ "mls").read[String]
    ) (Host.apply _)
}
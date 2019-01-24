package model

import com.alab.mvc.Data

object CustomerConfig {
  case class Client(shortName: String, apiKey: String) extends Data


  import play.api.libs.functional.syntax._
  import play.api.libs.json._

  implicit val customerWrite: Writes[Client] = (host: Client) => Json.obj(
    "shortName" -> host.shortName,
    "apiKey" -> host.apiKey
  )

  implicit val customerRead: Reads[Client] = (
    (JsPath \ "shortName").read[String] and
      (JsPath \ "apiKey").read[String]
    ) (Client.apply _)
}
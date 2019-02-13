import com.alab.mvc.Data

package object model {

  case class Host(name: String, gateway: String, web: String, mls: String) extends Serializable with Data

  case class Client(shortName: String, apiKey: String) extends Data

}

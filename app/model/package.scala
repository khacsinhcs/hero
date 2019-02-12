import com.alab.mvc.Data

package object model {

  case class Client(shortName: String, apiKey: String) extends Data

}

package model

import com.alab.mvc.Data

package object CustomerConfig {

  case class Client(shortName: String, apiKey: String) extends Data

}
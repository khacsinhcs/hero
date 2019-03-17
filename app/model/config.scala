package model

import com.alab.conf._
import com.alab.generate.CaseClassGenerator
import model.config.{ClientConf, HostConf}

package object config {

  object HostConf extends Type(n = "Host", des = "Environment") {
    val name: Field[String] = f("name", StringKey)
    val gateway: Field[String] = f("gateway", StringType)
    val web: Field[String] = f("web", StringType)
    val mls: Field[String] = f("mls", StringType)
  }

  object ClientConf extends Type(n = "Client", des = "Client configuration") {
    val short_name: Field[String] = f("short_name", StringKey)
    val api_key: Field[String] = f("api_key", StringType)
  }

}

object Bootstrap {
  def allTypes(): Unit = {
    HostConf
    ClientConf
  }

  def main(array: Array[String]): Unit = {
    val all = TypeSystem.types.values map (CaseClassGenerator generate _) mkString("", "\n", "")
    println(all)
  }
}
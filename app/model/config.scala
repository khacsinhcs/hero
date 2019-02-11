package model

import com.alab.conf.{StringKey, StringType, Type}
import model.config.{ClientConf, HostConf}

package object config {

  object HostConf extends Type(n = "Host", des = "Environment") {
    val name = f("name", StringKey)
    val gateway = f("gateway", StringType)
    val web = f("web", StringType)
    val mls = f("mls", StringType)
  }

  object ClientConf extends Type(n = "Client", des = "Client configuration") {
    val short_name = f("short_name", StringKey)
    val api_key = f("api_key", StringType)
  }

}

object Bootstrap {
  def allTypes(): Unit = {
    HostConf
    ClientConf
  }
}
package actors

import javax.inject.Inject
import model.HostConfig.Host
import play.api.cache.redis.CacheAsyncApi

import scala.concurrent.ExecutionContext

object HostActor {

}

class HostActor @Inject()(val executionContext: ExecutionContext, val cache: CacheAsyncApi) extends CacheBaseActor[String, Host] {
  override def receive: Receive = {
    case None => print("fuck you")
    case _ => super.receive(_)
  }

  override val typeName: String = "Host"

  override def keyOf(t: Host): String = t.name
}

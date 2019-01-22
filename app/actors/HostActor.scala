package actors

import akka.actor.Props
import javax.inject.Inject
import model.HostConfig.Host
import play.api.cache.redis.CacheAsyncApi

import scala.concurrent.ExecutionContext

object HostActor {
  def prop(executionContext: ExecutionContext, cache: CacheAsyncApi) = Props(classOf[HostActor], executionContext, cache)
}

class HostActor @Inject()(val executionContext: ExecutionContext, val cache: CacheAsyncApi) extends CacheBaseActor[String, Host] {

  override val typeName: String = "Host"

  override def keyOf: Host => String = (t: Host) => t.name
}

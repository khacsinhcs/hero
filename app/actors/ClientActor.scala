package actors

import akka.actor.Props
import javax.inject.Inject
import model.CustomerConfig.Client
import play.api.cache.redis.CacheApi

import scala.concurrent.ExecutionContext

object ClientActor {
  def prop(executionContext: ExecutionContext, cache: CacheApi) = Props(classOf[ClientActor], executionContext, cache)
}

class ClientActor @Inject()(val executionContext: ExecutionContext, val cache: CacheApi) extends CacheBaseActor[String, Client] {
  override val typeName: String = "Client"

  override def keyOf: Client => String = client => client.shortName
}

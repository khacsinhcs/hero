import actors.HostActor
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport
import server.InitData

class Module extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    bind(classOf[InitData]).asEagerSingleton()
    bindActor[HostActor]("hostActor")
  }
}

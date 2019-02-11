import com.google.inject.AbstractModule
import model.Bootstrap
import play.api.libs.concurrent.AkkaGuiceSupport
import server.InitData

class Module extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    Bootstrap allTypes()
    bind(classOf[InitData]).asEagerSingleton()
  }
}

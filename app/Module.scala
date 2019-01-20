import com.google.inject.AbstractModule
import server.InitData

class Module extends AbstractModule{
  override def configure(): Unit = {
    bind(classOf[InitData]).asEagerSingleton()
  }
}

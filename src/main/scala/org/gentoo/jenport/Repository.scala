import java.io.File
import org.apache.maven.repository.internal.MavenRepositorySystemUtils
import org.eclipse.aether.{DefaultRepositorySystemSession, RepositorySystem, RepositorySystemSession}
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory
import org.eclipse.aether.impl.DefaultServiceLocator
import org.eclipse.aether.repository.{LocalRepository, RemoteRepository}
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory
import org.eclipse.aether.spi.connector.transport.TransporterFactory
import org.eclipse.aether.transport.file.FileTransporterFactory
import org.eclipse.aether.transport.http.HttpTransporterFactory
import org.eclipse.aether.util.repository.SimpleArtifactDescriptorPolicy

object ARepositorySystem {
  def apply: RepositorySystem = {
    val locator: DefaultServiceLocator = MavenRepositorySystemUtils.newServiceLocator();
    locator.setErrorHandler(new DefaultServiceLocator.ErrorHandler {
      override def serviceCreationFailed(ty: Class[_], impl: Class[_], ex: Throwable): Unit = {
        ex.printStackTrace()
      }
    })
    locator.addService(classOf[RepositoryConnectorFactory], classOf[BasicRepositoryConnectorFactory])
    locator.addService(classOf[TransporterFactory], classOf[FileTransporterFactory])
    locator.addService(classOf[TransporterFactory], classOf[HttpTransporterFactory])
    locator.getService(classOf[RepositorySystem])
  }
}

object ADefaultRepositorySystemSession {
  def apply(repoSys: RepositorySystem, localRepoDir: File = new File(s"${sys.props("user.home")}/.m2/repository")):
      DefaultRepositorySystemSession = {
    val session: DefaultRepositorySystemSession = MavenRepositorySystemUtils.newSession()
    val localRepo = new LocalRepository(localRepoDir)
    session.setLocalRepositoryManager(repoSys.newLocalRepositoryManager(session, localRepo))
    val artDescPol = new SimpleArtifactDescriptorPolicy(false, true)
    session.setArtifactDescriptorPolicy(artDescPol)
    session
  }
}

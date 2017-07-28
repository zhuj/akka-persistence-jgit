import java.io.File
import java.nio.charset.Charset

import org.apache.commons.io.IOUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.PersonIdent

import scala.util.Success

object test {
  import org.eclipse.jgit.storage.file.FileRepositoryBuilder
  import org.metha.utils.akka.persistence.jgit._
  def main(args: Array[String]): Unit = {


    // val P = "/opt/projects/W-LOCAL/akka-persistence-cassandra"
    // val P = "/opt/projects/W-LOCAL/akka-persistence-jgit/test"
    val P = "./test"

    withResource(
      new FileRepositoryBuilder()
        .setGitDir(new File(s"${P}/.git"))
        .build()
    ) { repo =>

      val commiter = new PersonIdent("qwe", "qwe@mail")
      repo.commitBranchEntry("branch1", "tmp1", commiter, s"[${System.nanoTime()}] test commit - 1.1")(s"test content ${System.nanoTime()}".getBytes).get
      repo.commitBranchEntry("branch1", "tmp2", commiter, s"[${System.nanoTime()}] test commit - 1.2")(s"test content ${System.nanoTime()}".getBytes).get
      repo.commitBranchEntry("branch2", "tmp1", commiter, s"[${System.nanoTime()}] test commit - 2.1")(s"test content ${System.nanoTime()}".getBytes).get
      repo.commitBranchEntry("branch2", "tmp3", commiter, s"[${System.nanoTime()}] test commit - 2.3")(s"test content ${System.nanoTime()}".getBytes).get

      repo.withFileHistory("branch1", "tmp1") { commits =>
        Success {
          commits.foreach { revCommit => println(s"${revCommit}: ${revCommit.getShortMessage}") }
        }
      }

      repo.withFileHistory("branch2", "tmp1") { commits =>
        Success {
          commits.foreach { revCommit => println(s"${revCommit}: ${revCommit.getShortMessage}") }
        }
      }

      repo.withFileContent("branch2", "tmp1")("b4aefbbeca0a361a37bc2f308d26f473eeaa4e38") { stream =>
        Success {
          println(IOUtils.toString(stream, Charset.forName("UTF-8")))
        }
      }

      repo.withFileContent("branch2", "tmp3")("b4aefbbeca0a361a37bc2f308d26f473eeaa4e38") { stream =>
        Success {
          println(IOUtils.toString(stream, Charset.forName("UTF-8")))
        }
      }


      Success {}
    }.get



  }
}

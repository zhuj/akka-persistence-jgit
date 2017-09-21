import java.io.File
import java.nio.charset.Charset

import org.apache.commons.io.IOUtils
import org.eclipse.jgit.lib.PersonIdent
import play.api.libs.json.{JsObject, Json}

import scala.util.{Success, Try}

object test2 {
  import org.eclipse.jgit.storage.file.FileRepositoryBuilder
  import org.mentha.utils.akka.persistence.jgit._
  def main(args: Array[String]): Unit = {


    val P = "."
    withResource(
      new FileRepositoryBuilder()
        .setGitDir(new File(s"${P}/.git"))
        .build()
    ) { repo =>

      val obj = repo.withBranchTree[JsObject]("master")("d20b20085a2fb136d8db0167b39cf715499cd959")(
        construct = JsObject.empty,
        body = (stream) => Try { JsObject.empty /*Json.parse(stream).asInstanceOf[JsObject]*/ },
        aggregator = (current, name, value) => { current + (name -> value) }
      )

      println(obj.get)

      Success {}
    }.get



  }
}

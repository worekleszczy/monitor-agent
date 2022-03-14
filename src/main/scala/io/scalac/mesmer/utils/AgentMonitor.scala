package io.scalac.mesmer.utils

import org.slf4j.LoggerFactory

import java.lang.instrument.{ClassFileTransformer, Instrumentation}
import java.nio.file.{Files, Path, Paths, StandardOpenOption}
import java.security.ProtectionDomain
import scala.annotation.unused

object AgentMonitor {


  private val log = LoggerFactory.getLogger(AgentMonitor.getClass)


  def premain(@unused arg: String, instrumentation: Instrumentation): Unit = {

    val file = sys.props.getOrElse("dump-classes-dir", "monitor-classes")
    val path = Paths.get(file).toAbsolutePath

    log.debug(s"Dumping class files to $path")

    val saver = new Saver(path)

    val transformer: ClassFileTransformer = new ClassFileTransformer {
      override def transform(loader: ClassLoader, className: String, classBeingRedefined: Class[_], protectionDomain: ProtectionDomain, classfileBuffer: Array[Byte]): Array[Byte] = {
        log.info(s"Loading class $className with classLoader: $loader")
        saver.save(className, loader, classfileBuffer)
        super.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer)
      }
    }

    instrumentation.addTransformer(transformer, false)

  }
}


final class Saver(base: Path) {

  private val log = LoggerFactory.getLogger(classOf[Saver])

  def save(className: String, loader: ClassLoader, bytes: Array[Byte]): Unit = {

    val loaderDir = if (loader eq null) "bootstrap" else loader.toString.replaceAll("[@#$!.]", "")
    val relative = className.replace(".", "/")

    val target = base.resolve(loaderDir).resolve(relative + ".class")
    Files.createDirectories(target.getParent)
    Files.createFile(target)
    Files.write(target, bytes, StandardOpenOption.TRUNCATE_EXISTING)
  }
}



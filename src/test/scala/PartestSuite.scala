package scala.actors.migration

import org.junit._
import org.junit.rules.TestName
import Assert._
import java.io.File

abstract class PartestSuite extends NamedSuite {

  private var buffer: StringBuffer = new StringBuffer

  def print(x: Any): Unit = buffer.append(x.toString)
  def println(x: Any): Unit = buffer.append(x.toString + "\n")

  def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(f)
    try { op(p) } finally {
      p.close()
    }
  }

  def assertPartest() = {
    val file = scala.io.Source.fromFile(makeFilePath)
    val checkString = file.mkString
    file.close()

    if (!checkString.equals(buffer.toString)) {
      printToFile(new File(checkFile + "-" + testName.getMethodName + ".log"))(op => op.print(buffer.toString))
    }

    assertEquals("Test output does not match the provided check file.", checkString, buffer.toString)
  }

  //TODO cleanup
  @After
  def invokePartest = {
    val file = scala.io.Source.fromFile(makeFilePath)
    val checkString = file.mkString
    file.close()
    if (!checkString.equals(buffer.toString)) {
      printToFile(new File(checkFile + "-" + testName.getMethodName + ".log"))(op => op.print(buffer.toString))
    }

    buffer = new StringBuffer
  }

  val checkFile: String

  def makeFilePath = "src/test/scala/" + checkFile + ".check"

}

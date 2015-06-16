package org.gentoo.jenport

import org.eclipse.aether.{DefaultRepositorySystemSession, RepositorySystem}

abstract class CommandOpt
case class Invalid() extends CommandOpt
case class InvalidCommand(s: String) extends CommandOpt
case class InvalidOpt(s: String) extends CommandOpt
case class GlobalFlags(opts: Map[Symbol, Any]) extends CommandOpt
case class CurrentVersionCommand(as: List[String]) extends CommandOpt

object Jenport {
  def main(args: Array[String]): Unit = {
    val usage = "Usage: jenport COMMAND [FLAGS]\n" +
    "   or: jenport [GLOBAL FLAGS]\n\n" +
    "Global flags:\n" +
    " -h --help              Show this help text\n" +
    " -V --version           Print version information\n\n" +
    "Commands:\n" +
    "  current-version       List current versions of groupId:artifactId packages"   

    if (args.length == 0) println(usage)
    val arglist = args.toList

    def nextOption(map : Map[Symbol, Any], as: List[String]) : Map[Symbol, Any] = {
      def isSwitch(s : String) = (s(0) == '-')
      as match {
        case Nil => map
        case ("-h" | "--help") :: _ => {
          Map('help -> "")
        }
        case x :: xs => {
          Map('unknown -> x)
        }
      }
    }

    def commandOrOpt(as: List[String]): CommandOpt = {
      as match {
        case "current-version" :: tail => CurrentVersionCommand(tail)
        case x :: xs => {
          if (x(0) == '-') {
            GlobalFlags(nextOption(Map(), as))
          } else {
            InvalidOpt(x)
          }
        }
        case Nil => {
          Invalid()
        }
      }
    }

    commandOrOpt(arglist) match {
      case Invalid() => println(usage)
      case InvalidCommand(x) => println("Unknown command: " + x + "\n" + usage)
      case InvalidOpt(x) => println("Unknown option: " + x + "\n" + usage)
      case GlobalFlags(m) => {
        m map { case (k, v) =>
          k match {
            case 'help => println(usage)
            case _ => println("Unknown option: " + v + "\n" + usage)
          }
        }
      }
      case CurrentVersionCommand(as) => {
        val repoSys = RepositorySystemFactory.create
        val sess = DefaultRepositorySystemSessionFactory.create(repoSys)
        val remoteRepos = RemoteRepositoryFactory.create
        as map { a =>
          val rangeRequest = VersionRangeRequestFactory.create(a, remoteRepos)
          val rangeResult = repoSys.resolveVersionRange(sess, rangeRequest);
          val currentVersion = rangeResult.getHighestVersion
          println(a + ": " + currentVersion)
        }
      }
    }
  }
}

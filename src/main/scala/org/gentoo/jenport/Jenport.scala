abstract class CommandOpt
case class Invalid() extends CommandOpt
case class GlobalFlags(opts: Map[Symbol, Any]) extends CommandOpt
case class CurrentVersionCommand(as: List[String]) extends CommandOpt

object Jenport {
  def main(args: Array[String]) = {
    val usage = "Usage: jenport COMMAND [FLAGS]\n" +
    "   or: jenport [GLOBAL FLAGS]\n\n" +
    "Global flags:\n" +
    " -h --help              Show this help text\n" +
    " -V --version           Print version information\n"

    if (args.length == 0) println(usage)
    val arglist = args.toList

    def nextOption(map : Map[Symbol, Any], as: List[String]) : Map[Symbol, Any] = {
      def isSwitch(s : String) = (s(0) == '-')
      as match {
        case Nil => map
        case ("-h" | "--help") :: _ => {
          println(usage)
          map
        }
        case x :: xs => {
          println("Unknown option " + x)
          map
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
            println("Unknown option " + x)
          }
        }
        case Nil => {
          println(usage)
          Invalid()
        }
      }
      if (!as.isEmpty && (as.head(0) == '-')) {
        GlobalFlags(nextOption(Map(), as))
      } else {
        println(usage)
        Invalid()
      }
    }
  }
}

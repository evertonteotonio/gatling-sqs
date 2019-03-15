package aws.lambda.action


import aws.protocol.AwsProtocol
import aws.lambda.LambdaCheck
import io.gatling.commons.util.Clock
import io.gatling.commons.validation._
import io.gatling.core.action._
import io.gatling.core.check.Check
import io.gatling.core.session.{Expression, Session}
import io.gatling.core.stats.StatsEngine
import io.gatling.commons.stats.{OK, Status}
import io.gatling.core.util.NameGen
import akka.actor.{ActorSystem, Props}
import com.amazonaws.services.sqs.model.SendMessageBatchResult
import io.gatling.core.Predef.Session

object LambdaAction extends NameGen {

  def apply(functionName: Expression[String], payload: Option[Expression[String]], checks: List[LambdaCheck], protocol: AwsProtocol, system: ActorSystem, statsEngine: StatsEngine, next: Action, clock : Clock) = {
    val actor = system.actorOf(LambdaActionActor.props(functionName, payload, checks, protocol, statsEngine, next, clock))
    new ExitableActorDelegatingAction(genName("Lambda"), statsEngine,clock, next, actor)
  }
}

object LambdaActionActor {
  def props(functionName: Expression[String], payload: Option[Expression[String]], checks: List[LambdaCheck], protocol: AwsProtocol, statsEngine: StatsEngine, next: Action, clock : Clock): Props =
    Props(new LambdaActionActor(functionName, payload, checks, protocol, statsEngine, next,clock))
}

case class Request(functionName:String)

class LambdaActionActor(
                         functionName: Expression[String],
                         payload: Option[Expression[String]],
                         checks: List[LambdaCheck],
                         protocol: AwsProtocol,
                         val statsEngine: StatsEngine,
                         val next: Action,
                         val clock: Clock
                       ) extends ActionActor {

  override def execute(session: Session) = {
    //    println(session)
    var funcName:String=""
    functionName(session).flatMap { resolvedFunctionName => {
      funcName = resolvedFunctionName
      "".success
    }

    }

    //    if (payload.isDefined) {
    //      payload.get(session).flatMap { resolvePayload =>
    //        request.setPayload(resolvePayload).success
    //      }
    //    }
    var optionalBatchResult : Option[SendMessageBatchResult] = None
    var optionalThrowable : Option[Throwable] = None

    val startTime = now(clock)
    try {
      //      println("calling awsClient ...")

      optionalBatchResult = Some(protocol.awsQueue.sendMessageBatch(SqsOrder.getBatchRequest(session,protocol)))

      //      if (optionalBatchResult.isDefined) {
      //        println("result size : " + optionalBatchResult.get.getSuccessful.size())
      //      }

    } catch {
      case t: Throwable => optionalThrowable = Some(t)
    }
    val endTime = now(clock)

    if (optionalThrowable.isEmpty) {
      val result = optionalBatchResult.get
      if (!result.getSuccessful.isEmpty) {
        val resultPayload = result.toString
        val (newSession, error) = Check.check(resultPayload, session, checks)
        error match {
          case None                        => {
            statsEngine.logResponse(session, funcName, startTime, endTime, OK, None, None)
            next ! newSession
          }
          case Some(Failure(errorMessage)) => {
            statsEngine.logResponse(session, funcName, startTime, endTime, Status("KO"), None, Some(errorMessage))
            next ! newSession.markAsFailed
          }
        }
      } else {
        statsEngine.logResponse(session, funcName,  startTime, endTime, Status("KO"), None, Some(s"Failed messages ${result.getFailed.size()}"))
        next ! session.markAsFailed
      }
    } else {
      val throwable = optionalThrowable.get
      statsEngine.logResponse(session, funcName, startTime, endTime, Status("KO"), None, Some(throwable.getMessage))
      next ! session.markAsFailed
    }
  }

  @inline
  private def now(clock:Clock) = clock.nowMillis

}


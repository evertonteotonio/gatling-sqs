package aws.lambda.check

import java.util

import io.gatling.commons.validation._
import io.gatling.core.check.CheckResult
import io.gatling.core.session.Session
import aws.lambda._

case class LambdaCustomCheck(func: String => Boolean, failureMessage: String = "Lambda check failed") extends LambdaCheck {
  override def check(response: String, session: Session)(implicit preparedCache: util.Map[Any, Any]): Validation[CheckResult] = {
  func(response) match {
    case true => CheckResult.NoopCheckResultSuccess
    case _  => Failure(failureMessage)
  }
}
}

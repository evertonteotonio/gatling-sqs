package aws

import aws.lambda.process.LambdaProcessBuilder
import aws.lambda.check.LambdaCheckSupport
import aws.protocol.{ AwsProtocol, AwsProtocolBuilder, AwsProtocolBuilderBase }
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.session.Expression

import scala.language.implicitConversions

trait AwsDsl extends LambdaCheckSupport {

  val Aws = AwsProtocolBuilderBase

  def lambda(functionName: Expression[String]) = LambdaProcessBuilder(functionName)

  implicit def awsProtocolBuilder2awsProtocol(builder: AwsProtocolBuilder): AwsProtocol = builder.build
  implicit def lambdaProcessBuilder2ActionBuilder(builder: LambdaProcessBuilder): ActionBuilder = builder.build()

}

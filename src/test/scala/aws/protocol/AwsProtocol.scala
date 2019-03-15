package aws.protocol

import io.gatling.core.CoreComponents
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.protocol.{Protocol, ProtocolKey}
import com.amazonaws.services.sqs.AmazonSQS

object AwsProtocol {

  val AwsProtocolKey = new ProtocolKey[AwsProtocol, AwsComponents] {

    type Protocol = AwsProtocol
    type Components = AwsComponents

    def protocolClass: Class[io.gatling.core.protocol.Protocol] = classOf[AwsProtocol].asInstanceOf[Class[io.gatling.core.protocol.Protocol]]

    def defaultProtocolValue(configuration: GatlingConfiguration): AwsProtocol = throw new IllegalStateException("Can't provide a default value for AwsProtocol")

    def newComponents(coreComponents: CoreComponents): AwsProtocol => AwsComponents = {
      awsProtocol => AwsComponents(awsProtocol)
    }
  }
}

case class AwsProtocol(
    batchSize: Int,
    queueUrl: String,
    awsQueue: AmazonSQS
) extends Protocol {

  type Components = AwsComponents
}

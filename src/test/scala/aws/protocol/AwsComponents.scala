package aws.protocol

import io.gatling.core.protocol.ProtocolComponents
import io.gatling.core.session.Session

import akka.actor.ActorRef

case class AwsComponents(awsProtocol: AwsProtocol) extends ProtocolComponents {

  override def onStart: Session => Session = ProtocolComponents.NoopOnStart
  override def onExit: Session => Unit = ProtocolComponents.NoopOnExit

}

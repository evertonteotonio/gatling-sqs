package aws.protocol

import com.amazonaws.services.sqs.AmazonSQS

object AwsProtocolBuilderBase {
  def batchSize(batchSize: Int) = AwsProtocolBuilderAwsQueueUrlStep(batchSize)
}

case class AwsProtocolBuilderAwsQueueUrlStep(batchSize : Int) {
  def queueUrl(queueUrl: String) = AwsProtocolBuilderAwsQueueStep(batchSize, queueUrl)
}

case class AwsProtocolBuilderAwsQueueStep(batchSize: Int, queueUrl: String) {
  def awsQueue(awsQueue: AmazonSQS) = AwsProtocolBuilder(batchSize, queueUrl, awsQueue)
}

case class AwsProtocolBuilder(batchSize: Int, queueUrl: String, awsQueue: AmazonSQS) {

  def build = AwsProtocol(
    batchSize = batchSize,
    queueUrl = queueUrl,
    awsQueue = awsQueue
  )
}

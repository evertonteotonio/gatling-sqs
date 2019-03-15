package frontline.sample


import com.amazonaws.regions.Regions
import io.gatling.core.Predef._
import aws.Predef._
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.services.sqs.{AmazonSQS, AmazonSQSClientBuilder}

import scala.concurrent.duration._
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration

/**
  * Send message to AWS SQS FIFO queue in batch mode
  * max batchSize is 10
  */
class BasicSimulation extends Simulation {

  val hjorders = ssv("hjorders.csv").circular
  val shipQueue = "sqs-dev-hj_shipstation_orders-na"
  val devQueueUrl = "sqs-dev-aswathy-na.fifo"
  val endpoint = "https://us-west-2.queue.amazonaws.com/xxxxxxx"
  val accessKey = "xxxxx"
  val secretKey = "yyyy"
  val batchSize = 10

  var client:AmazonSQS  = AmazonSQSClientBuilder
        .standard()
        .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
        .withEndpointConfiguration(new EndpointConfiguration(endpoint, Regions.US_WEST_2.getName )) // update region if needed sqs fifo queue
        .build()

  val awsConfig = Aws
      .batchSize(batchSize)
    .queueUrl(s"$endpoint/$devQueueUrl")
    .awsQueue(client)


  val lambdaScenario = scenario("SQS FIFO Batch")
    .feed(hjorders,batchSize) // grabs 10 records from the csv at once
    .exec(
      lambda("Send Batch").sendBatch("")
    )

    setUp(
      lambdaScenario.inject(
        //      atOnceUsers(1)
        //        constantUsersPerSec(300) during (333 seconds)  // 1M
        //              constantUsersPerSec(300) during (160 seconds) // 0.5M
        constantUsersPerSec(100) during (10 seconds) // 10K
      )
    ).protocols(awsConfig)

}

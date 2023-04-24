package frontline.sample


import com.amazonaws.regions.Regions
import io.gatling.core.Predef._
import aws.Predef._
import aws.protocol.AwsProtocolBuilder
import com.amazon.sqs.javamessaging.{ProviderConfiguration, SQSConnectionFactory}
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials, BasicSessionCredentials}
import com.amazonaws.services.sqs.{AmazonSQS, AmazonSQSClientBuilder}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.jms.Predef._
import io.gatling.jms.protocol.JmsProtocolBuilder
import play.api.libs.json._
import java.io.FileInputStream
import scala.io.{BufferedSource, Source}


/**
  * Send message to AWS SQS FIFO queue in batch mode
  * max batchSize is 10
  */

class BasicSimulation extends Simulation {

  val devQueueUrl = "sqs_classified_events-republication-detection-stage"
  val endpoint = "https://sqs.eu-west-1.amazonaws.com"
  val accessKey = "xxx"
  val secretKey = "xxx"
  val session_token = "xxx"
  val batchSize = 10
  val awsCreds = new BasicSessionCredentials(accessKey, secretKey, session_token)
  var client: AmazonSQS = AmazonSQSClientBuilder
    .standard()
    .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
    .withEndpointConfiguration(new EndpointConfiguration(endpoint, Regions.EU_WEST_1.getName)) // update region if needed sqs fifo queue
    .build()

  val awsConfig: AwsProtocolBuilder = Aws
      .batchSize(batchSize)
    .queueUrl(s"$endpoint/$devQueueUrl")
    .awsQueue(client)

  val jsonfile: String = Source.fromFile("src/test/resources/test.json").getLines().mkString
  //client.sendMessage("sqs_classified_events-republication-detection-stage", jsonfile)

  val jmsProtocol: JmsProtocolBuilder = jms.connectionFactory(new SQSConnectionFactory(new ProviderConfiguration(), client))
  val scn: ScenarioBuilder = scenario("SQS Perf Test")
    .exec(
      jms("SendMessage").send
        .queue(devQueueUrl).objectMessage(jsonfile)
        //textMessage(jsonfile)



    )
  setUp(
    scn.inject(
      atOnceUsers(1))
      //constantUsersPerSec (300) during (333 seconds) // 1M
      //              constantUsersPerSec(300) during (160 seconds) // 0.5M
      //constantUsersPerSec (100) during (10 seconds) // 10K
      // other example: Let's have 10 regular users and 2 admins, and ramp them over 10 seconds so we don't hammer
      // the server
      //users.inject(rampUsers(10).during(10)), admins.inject(rampUsers(2).during(10))
  ).protocols(jmsProtocol)


}

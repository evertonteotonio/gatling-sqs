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
  val accessKey = "ASIA5QZRJFFBMSHU56X7"
  val secretKey = "z9s1d0PTBvsbawGUfAVliB7JhLV2BaH1xkA/+Tzl"
  val session_token = "IQoJb3JpZ2luX2VjEPT//////////wEaCWV1LXdlc3QtMSJHMEUCIQC6EbI6bc4AJmOAOJr6G7+4z0Np020mdOEPLIYqn7EbCwIgbyO/l4tEjMvZvriVlvm3ctqATpyQlzEUa0GnkUFcckoqvwMI7f//////////ARAAGgw5Mjk0MjY5MDk1MDYiDLoVJbYbA+WJKsLt8yqTA/PuhXWLjfR6yZAcI3ZjYEETLMUGgM+NhiDXKXYkK2xWBz5WhWm/Fymd7tLuvctu4+v6ssl27SkazMt29yDhBTOCr6QXpVWO/qv7cOqjHs0e2Hi4U5YW3UKuqNCSW/t1GlD677MkpP4LvCAb+ug7ntonqrWuGqqE1ahjkpEP8iL8PC0sqlr36cqYHxAgcPEaVchWVILBYS7/UIa8c45DVSY9lKZi/8rBmzJWFwwAHPjbMyayYvYCM0X5MfWZ09lPD2FodXPs4jdvw53cUNOuQDYXM4EqOSCv4p9smxbzocHmXTOHXBxiX2DEClyqjBh6NNjbbq3ta3L+JM8fNwIiZELCGSUEQh7mgH+wRK1asAv9rWHAhL/g85H85FyP/wnSJEMTuVTw9ZPGV2rrp7dVR/f5eDUdrTce5f+0qHZeV81ISZRMhRlC32plHDKgTFlWH+Yugi9VFPkXtkFCwvutqyMMwEMGoe8i+0ATNtWQtN1CEA3IKwYCWUJojclRNf3NrKYVPodpra+F82HYdpH7omFQyeEwudOZogY6pgEukOyLRuPTh9tC+Zqg0uM+stQmHUUU1+givGheenRYfmwXDurj4MAPe29WyKpo/7MphbBZNVrlAcL8bs/NjkVRNKgpnQcXso5amjwFD4mXa5Pn1cCii7VBxugk0XVt2Wux5f9NSZrPVWQRg8NLEC9MtdeDXvc/yFA2quJugrEgTL2zzQLH+rH4+qus7Lq8SFrP+iSI2snHRsXaIC69sgD1D+v+wayY"
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

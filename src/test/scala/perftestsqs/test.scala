/*package perftestsqs
// SQS
import com.amazon.sqs.javamessaging._
import com.amazonaws.auth._
import com.amazonaws.regions._
import com.amazonaws.services.sqs._
//Gatling JMS DSL
import io.gatling.jms.Predef._


class test {
  val sqsClient= {
    val builder = AmazonSQSAsyncClientBuilder.standard
    // configure the credentials strategy of your choice
    builder.setCredentials(new AWSStaticCredentialProvider(new BasicAWSCredentials("myKey", "myToken")))
    builder.setRegion(Regions.EU_WEST_3.getName)
    builder.build
  }
  after{
    sqsClient.shutdown()
  }

} */
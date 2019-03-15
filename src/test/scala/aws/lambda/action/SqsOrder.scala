package aws.lambda.action

import java.util.UUID

import aws.protocol.AwsProtocol
import com.amazonaws.services.sqs.model.{SendMessageBatchRequest, SendMessageBatchRequestEntry}
import io.gatling.core.session.Session

import scala.collection.JavaConverters._

object SqsOrder {

  @inline
  def getSelectStatus(select:String):String = {
    if (select.equals("1")) "select" else "non-select"
  }

  @inline
  def getOrder(session: Session, count:Int) : String = {
    val messageBody =
      "{\"orderNumber\": \"" +  uuid() + "\"," +
        "\"customerCode\":\"" + session("user_account_code"+count).as[String] + "\"," +
        "\"shipToName\": \"Customer  Test\"," +
        "\"shipToAddr1\": \"0000 Meadow Oaks Dr\"," +
        "\"shipToCity\": \"Colorado Springs\"," +
        "\"shipToState\": \"CO\"," +
        "\"shipToZip\":\"80921-3681\"," +
        "\"shipToCountryName\": \"US\"," +
        "\"shipToPhone\":\"111 111 1111\"," +
        "\"processingCode\":\"N\"," +
        "\"orderOrigin\":\"" + session("order_origin"+count).as[String] + "\"," +
        "\"customerType\":\"" + getSelectStatus(session("select_status"+count).as[String]) + "\"," +
        "\"orderDate\":\"2019-01-30 03:05:51\"," +
        "\"orderLineItem\":[" +
        "{\"lineNumber\":\"1\",\"itemNumber\":\"1803-DO-B62\",\"itemDescription\":\"FabFitFun Fall 2018 Box\",\"unitInsuranceAmount\":49.99,\"qty\":1},{\"lineNumber\":\"2\",\"itemNumber\":\"PRE-LI-001\",\"itemDescription\":\"PreHeels\",\"unitInsuranceAmount\":9,\"qty\":1},{\"lineNumber\":\"3\",\"itemNumber\":\"LIV-BE-002\",\"itemDescription\":\"Living Proof Full Dry Volume Blast\",\"unitInsuranceAmount\":9,\"qty\":3},{\"lineNumber\":\"4\",\"itemNumber\":\"BRI-BE-001\",\"itemDescription\":\"Briogeo Rosarco Milk Spray\",\"unitInsuranceAmount\":7,\"qty\":1},{\"lineNumber\":\"5\",\"itemNumber\":\"DRB-BE-001\",\"itemDescription\":\"Dr. Brandt Microdermabrasion Age Defying Exfoliator\",\"unitInsuranceAmount\":19,\"qty\":2},{\"lineNumber\":\"6\",\"itemNumber\":\"DAI-BE-002\",\"itemDescription\":\"Daily Concepts Your Exfoliating Gloves\",\"unitInsuranceAmount\":5,\"qty\":1},{\"lineNumber\":\"7\",\"itemNumber\":\"SCH-BE-010\",\"itemDescription\":\"Dr. D Schwab Shimmery Sun Lotion\",\"unitInsuranceAmount\":14,\"qty\":1},{\"lineNumber\":\"8\",\"itemNumber\":\"BEN-LI-002\",\"itemDescription\":\"Bentgo All-In-One Stackable Lunchbox - Purple\",\"unitInsuranceAmount\":7,\"qty\":1},{\"lineNumber\":\"9\",\"itemNumber\":\"BEN-LI-003\",\"itemDescription\":\"Bentgo All-In-One Stackable Lunchbox (blue)\",\"unitInsuranceAmount\":7,\"qty\":1},{\"lineNumber\":\"10\",\"itemNumber\":\"TAG-FI-007\",\"itemDescription\":\"MYTAGALONGS Resistance Bands Trio\",\"unitInsuranceAmount\":9,\"qty\":1},{\"lineNumber\":\"11\",\"itemNumber\":\"DRB-BE-007\",\"itemDescription\":\"dr. brandt PoreDermabrasion\",\"unitInsuranceAmount\":19,\"qty\":2},{\"lineNumber\":\"12\",\"itemNumber\":\"MUR-BE-004\",\"itemDescription\":\"Murad Essential-C Cleanser\",\"unitInsuranceAmount\":14,\"qty\":1},{\"lineNumber\":\"13\",\"itemNumber\":\"MAJ-FI-001\",\"itemDescription\":\"Maji Sports Trigger Point Ball (blue)\",\"unitInsuranceAmount\":7,\"qty\":1},{\"lineNumber\":\"14\",\"itemNumber\":\"SPO-BE-018\",\"itemDescription\":\"Spongellé Body Wash Infused Buffer in French Lavender\",\"unitInsuranceAmount\":7,\"qty\":1},{\"lineNumber\":\"15\",\"itemNumber\":\"BAU-LI-001\",\"itemDescription\":\"Baublerella Bling Brush\",\"unitInsuranceAmount\":9,\"qty\":1}" +
        "]}"
    messageBody
  }

  @inline
  def getOrder(session: Session) : String = {
    val messageBody =
      "{\"orderNumber\": \"" + uuid() + "\"," +
        "\"customerCode\":\"" + session("user_account_code").as[String] + "\"," +
        "\"shipToName\": \"Customer  Test\"," +
        "\"shipToAddr1\": \"0000 Meadow Oaks Dr\"," +
        "\"shipToCity\": \"Colorado Springs\"," +
        "\"shipToState\": \"CO\"," +
        "\"shipToZip\":\"80921-3681\"," +
        "\"shipToCountryName\": \"US\"," +
        "\"shipToPhone\":\"111 111 1111\"," +
        "\"processingCode\":\"N\"," +
        "\"orderOrigin\":\"" + session("order_origin").as[String] + "\"," +
        "\"customerType\":\"" + getSelectStatus(session("select_status").as[String]) + "\"," +
        "\"orderDate\":\"2019-01-30 03:05:51\"," +
        "\"orderLineItem\":[" +
        "{\"lineNumber\":\"1\",\"itemNumber\":\"1803-DO-B62\",\"itemDescription\":\"FabFitFun Fall 2018 Box\",\"unitInsuranceAmount\":49.99,\"qty\":1},{\"lineNumber\":\"2\",\"itemNumber\":\"PRE-LI-001\",\"itemDescription\":\"PreHeels\",\"unitInsuranceAmount\":9,\"qty\":1},{\"lineNumber\":\"3\",\"itemNumber\":\"LIV-BE-002\",\"itemDescription\":\"Living Proof Full Dry Volume Blast\",\"unitInsuranceAmount\":9,\"qty\":3},{\"lineNumber\":\"4\",\"itemNumber\":\"BRI-BE-001\",\"itemDescription\":\"Briogeo Rosarco Milk Spray\",\"unitInsuranceAmount\":7,\"qty\":1},{\"lineNumber\":\"5\",\"itemNumber\":\"DRB-BE-001\",\"itemDescription\":\"Dr. Brandt Microdermabrasion Age Defying Exfoliator\",\"unitInsuranceAmount\":19,\"qty\":2},{\"lineNumber\":\"6\",\"itemNumber\":\"DAI-BE-002\",\"itemDescription\":\"Daily Concepts Your Exfoliating Gloves\",\"unitInsuranceAmount\":5,\"qty\":1},{\"lineNumber\":\"7\",\"itemNumber\":\"SCH-BE-010\",\"itemDescription\":\"Dr. D Schwab Shimmery Sun Lotion\",\"unitInsuranceAmount\":14,\"qty\":1},{\"lineNumber\":\"8\",\"itemNumber\":\"BEN-LI-002\",\"itemDescription\":\"Bentgo All-In-One Stackable Lunchbox - Purple\",\"unitInsuranceAmount\":7,\"qty\":1},{\"lineNumber\":\"9\",\"itemNumber\":\"BEN-LI-003\",\"itemDescription\":\"Bentgo All-In-One Stackable Lunchbox (blue)\",\"unitInsuranceAmount\":7,\"qty\":1},{\"lineNumber\":\"10\",\"itemNumber\":\"TAG-FI-007\",\"itemDescription\":\"MYTAGALONGS Resistance Bands Trio\",\"unitInsuranceAmount\":9,\"qty\":1},{\"lineNumber\":\"11\",\"itemNumber\":\"DRB-BE-007\",\"itemDescription\":\"dr. brandt PoreDermabrasion\",\"unitInsuranceAmount\":19,\"qty\":2},{\"lineNumber\":\"12\",\"itemNumber\":\"MUR-BE-004\",\"itemDescription\":\"Murad Essential-C Cleanser\",\"unitInsuranceAmount\":14,\"qty\":1},{\"lineNumber\":\"13\",\"itemNumber\":\"MAJ-FI-001\",\"itemDescription\":\"Maji Sports Trigger Point Ball (blue)\",\"unitInsuranceAmount\":7,\"qty\":1},{\"lineNumber\":\"14\",\"itemNumber\":\"SPO-BE-018\",\"itemDescription\":\"Spongellé Body Wash Infused Buffer in French Lavender\",\"unitInsuranceAmount\":7,\"qty\":1},{\"lineNumber\":\"15\",\"itemNumber\":\"BAU-LI-001\",\"itemDescription\":\"Baublerella Bling Brush\",\"unitInsuranceAmount\":9,\"qty\":1}" +
        "]}"
    messageBody
  }

  def getBatchRequest(session:Session, protocol: AwsProtocol) : SendMessageBatchRequest = {
    var entries: scala.collection.mutable.ListBuffer[SendMessageBatchRequestEntry] = new scala.collection.mutable.ListBuffer[SendMessageBatchRequestEntry]()
    for (i <- 1 to protocol.batchSize) {
      entries+= new SendMessageBatchRequestEntry(s"$i", SqsOrder.getOrder(session, i))
        .withMessageGroupId(s"$i" + uuid()).
        withMessageDeduplicationId(s"$i" + uuid())
    }
    val batchRequest:SendMessageBatchRequest  = new SendMessageBatchRequest(protocol.queueUrl)
    batchRequest.setEntries(entries.asJava)
    batchRequest
  }

  @inline
  private def uuid() : String = UUID.randomUUID.toString()
}

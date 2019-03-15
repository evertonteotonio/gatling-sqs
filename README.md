# gatling-sqs
Load Testing AWS SQS Queue

This gatling load test uses a custom gatling protocol to send mesagges in batching mode to a AWS SQS Fifo Queue.
For testings purposes we have a csv feeder that includes some orders data to send to sqs.

Some parameters needed to run this test are :
* devQueueUrl : the sqs fifo queue name
* endpoint : aws sqs endpoint url
* accessKey : credentials
* secretKey : credentials
* AWS Region : the queue region

See frontline.sample.BasicSimulation

After updating these parameters you can actually run the load test:

mvn clean  gatling:test -Dgatling.simulationClass=frontline.sample.BasicSimulation

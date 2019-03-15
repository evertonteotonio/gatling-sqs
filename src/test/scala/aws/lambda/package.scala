package aws

import io.gatling.core.check.{ Check, Preparer, Specializer }
import io.gatling.commons.validation.Success

package object lambda {

  /**
   * Type for Lambda checks
   */
  type LambdaCheck = Check[String]
  
  val LambdaStringExtender: Specializer[LambdaCheck, String] =
     (check: LambdaCheck) => check
  
  val LambdaStringPreparer: Preparer[String, String] = 
     (result: String) => Success(result)
  
}

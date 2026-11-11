/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.perftests.cis

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.performance.conf.ServicesConfiguration
import uk.gov.hmrc.perftests.BaseRequests

object CisRdsRequests extends ServicesConfiguration with BaseRequests {

  private val useStub: Boolean =
    sys.props.get("useStub").exists(_.toBoolean)

  val getCisTaxpayerUrl: String =
    if (useStub)
      s"$rdsHost/rds-datacache-proxy/cis-taxpayer"
    else
      s"$rdsHost/rds-datacache-proxy/cis/"
  val getClientListStatusUrl: String = s"$rdsCisUrl/client-list-status"
  val getClientListUrl: String       = s"$rdsCisUrl/client-list"
  val hasClientUrl: String           = s"$rdsCisUrl/has-client"
  val prepopContractorUrl: String    = s"$rdsCisUrl/prepop-contractor"
  val prepopSubcontractorUrl: String = s"$rdsCisUrl/prepop-subcontractor"

  def commonHeaders: Map[CharSequence, String] = Map(
    HttpHeaderNames.Authorization -> "#{bearerToken}",
    HttpHeaderNames.ContentType   -> "application/json",
    "X-Session-ID"                -> "693b2579c9ae70489252dba5"
  )

  val getClientListStatus: HttpRequestBuilder =
    http("GET Client list status")
      .get(getClientListStatusUrl)
      .queryParam("credentialId", "CRED01")
      .queryParam("serviceName", "CIS")
      .headers(commonHeaders)
      .check(status.is(200))

  val getClientList: HttpRequestBuilder =
    http("GET Client list")
      .get(getClientListUrl)
      .queryParam("irAgentId", "123456")
      .queryParam("credentialId", "CRED01")
      .headers(commonHeaders)
      .check(status.is(200))

  val hasClient: HttpRequestBuilder =
    http("GET Has client")
      .get(hasClientUrl)
      .queryParam("irAgentId", "123456")
      .queryParam("credentialId", "CRED01")
      .queryParam("taxOfficeNumber", "101")
      .queryParam("taxOfficeReference", "AB0001")
      .headers(commonHeaders)
      .check(status.is(200))

  val getCisTaxpayer: HttpRequestBuilder =
    http("POST Get CIS taxpayer")
      .post(getCisTaxpayerUrl)
      .headers(commonHeaders)
      .body(
        StringBody(
          """{
            |  "taxOfficeNumber": "754",
            |  "taxOfficeReference": "EZ10800"
            |}""".stripMargin
        )
      )
      .asJson
      .check(status.is(200))

  val getContractorPrepop: HttpRequestBuilder =
    http("POST Contractor prepopulation")
      .post(prepopContractorUrl)
      .headers(commonHeaders)
      .body(
        StringBody(
          """{
            |  "taxOfficeNumber": "754",
            |  "taxOfficeReference": "EZ10350",
            |  "accountOfficeReference": "754PA87654350"
            |}""".stripMargin
        )
      )
      .asJson
      .check(status.is(200))

  val getSubcontractorPrepop: HttpRequestBuilder =
    http("POST Subcontractor prepopulation")
      .post(prepopSubcontractorUrl)
      .headers(commonHeaders)
      .body(
        StringBody(
          if (useStub)
            """{
              |  "taxOfficeNumber": "754",
              |  "taxOfficeReference": "EZ10420",
              |  "accountOfficeReference": "754PA87654400"
              |}""".stripMargin
          else
            """{
              |  "taxOfficeNumber": "754",
              |  "taxOfficeReference": "EZ10400",
              |  "accountOfficeReference": "754PA87654400"
              |}""".stripMargin
        )
      )
      .asJson
      .check(status.is(200))
}

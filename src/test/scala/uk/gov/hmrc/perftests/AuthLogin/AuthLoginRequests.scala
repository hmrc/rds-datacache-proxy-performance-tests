/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.perftests.AuthLogin

import io.gatling.core.Predef._
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.http.Predef._
import uk.gov.hmrc.performance.conf.ServicesConfiguration
import uk.gov.hmrc.perftests.BaseRequests

import scala.util.Random

object AuthLoginRequests extends ServicesConfiguration with BaseRequests {

  def getAuthToken(authPayload: () => String): List[ActionBuilder] = exec(
    http("Insert Auth Record for Agent")
      .post(authLoginApiUrl)
      .headers(Map(HttpHeaderNames.ContentType -> "application/json"))
      .body(StringBody(_ => authPayload()))
      .asJson
      .check(status.is(201))
      .check(header(HttpHeaderNames.Authorization).optional.saveAs("bearerToken"))
  ).actionBuilders

  def authPayloadCisAgent: String = {
    val CredIdLength = 16
    val credId       = Random.alphanumeric.take(CredIdLength).mkString

    s"""
       |{
       |  "credId": "$credId",
       |  "credentialStrength": "strong",
       |  "confidenceLevel": 50,
       |  "credentialRole": "User",
       |  "affinityGroup": "Agent",
       |  "excludeGnapToken": true,
       |  "enrolments": [
       |    {
       |      "key": "IR-PAYE-AGENT",
       |      "identifiers": [
       |        {
       |          "key": "IRAgentReference",
       |          "value": "123456"
       |        }
       |      ],
       |      "state": "Activated"
       |    }
       |  ]
       |}
       |""".stripMargin
  }
}

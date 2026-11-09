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

package uk.gov.hmrc.perftests.simulation

import io.gatling.core.action.builder.ActionBuilder
import uk.gov.hmrc.performance.simulation.PerformanceTestRunner
import uk.gov.hmrc.perftests.AuthLogin.AuthLoginRequests._
import uk.gov.hmrc.perftests.BaseRequests
import uk.gov.hmrc.perftests.cis.CisRdsRequests._

trait CisRdsAPI extends PerformanceTestRunner with BaseRequests {
  val cisRdsDatacache: List[ActionBuilder] =
    getAuthToken(() => authPayloadCisAgent) ++
      List[ActionBuilder](
        getClientListStatus,
        getClientList,
        hasClient,
        getCisTaxpayer,
        getContractorPrepop,
        getSubcontractorPrepop
      )

  setup(
    "cis-rds-datacache",
    "CIS RDS Datacache Proxy read requests"
  ) withActions (
    cisRdsDatacache: _*
    )
}
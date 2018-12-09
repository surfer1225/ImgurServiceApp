import play.api.libs.json.Json
import play.api.test._

class IntegrationSpec extends PlaySpecification {
  "Application" should {
    // Test application readiness
    "be reachable" in new WithApplication {
      private val home = route(app, FakeRequest(GET, "/")).get
      status(home) must equalTo(OK)
      contentAsString(home) must contain("Your new application is ready.")
    }

    "get job not found" in new WithApplication {
      private val getJobResp = route(app, FakeRequest(GET, "/v1/images/upload/1")).get
      status(getJobResp) must equalTo(OK)
      contentAsString(getJobResp) mustEqual "Job id not found"
    }
  }
}

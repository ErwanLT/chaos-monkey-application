package fr.eletutour.chaosmonkey;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;
import java.util.*;
import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class ChaosSimulation extends Simulation {

    private static final HttpProtocolBuilder HTTP_PROTOCOL = setupProtocol();
    private static final ScenarioBuilder USER_JOURNEY_SCENARIO = buildUserJourneyScenario();

    public ChaosSimulation() {
        setUp(
            initSession.injectOpen(atOnceUsers(1)),
            USER_JOURNEY_SCENARIO.injectOpen(rampUsers(50).during(Duration.ofSeconds(60)))
        )
        .protocols(HTTP_PROTOCOL)
        .assertions(
            global().responseTime().max().lte(5000),
            global().successfulRequests().percent().gt(95.0)
        );
    }

    private static HttpProtocolBuilder setupProtocol() {
        return http.baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json")
            .userAgentHeader("Gatling/ChaosSimulation-Java");
    }
    
    // This scenario runs once to fetch all video IDs and populate the session for subsequent scenarios
    private static final ScenarioBuilder initSession = scenario("Init")
        .exec(http("Get All Video IDs")
            .get("/api/catalog/videos")
            .check(status().is(200))
            .check(jsonPath("$..id").findAll().saveAs("allVideoIds")));


    private static ScenarioBuilder buildUserJourneyScenario() {
        // Feeder for user IDs (assuming 10 users exist in the DB)
        Iterator<Map<String, Object>> userFeeder = Stream.generate(() -> {
            Map<String, Object> record = new HashMap<>();
            record.put("userId", new Random().nextInt(10) + 1);
            return record;
        }).iterator();

        return scenario("Realistic User Journey")
            .feed(userFeeder)
            // 1. User browses the catalog (already done in init, we just use the data)
            .exec(session -> {
                // Pick a random video ID from the list fetched during initialization
                List<String> allVideoIds = session.getList("allVideoIds");
                String randomVideoId = allVideoIds.get(new Random().nextInt(allVideoIds.size()));
                return session.set("videoId", randomVideoId);
            })
            .pause(Duration.ofSeconds(1), Duration.ofSeconds(3))

            // 2. User views details of a specific video
            .exec(http("Get Video Details")
                .get("/api/catalog/videos/#{videoId}")
                .check(status().is(200)))
            .pause(Duration.ofSeconds(2), Duration.ofSeconds(5))

            // 3. User checks their recommendations
            .exec(http("Get Recommendations")
                .get("/api/recommendations/#{userId}")
                .check(status().is(200)))
            .pause(Duration.ofSeconds(2), Duration.ofSeconds(5))

            // 4. User starts watching the video
            .exec(http("Start Streaming")
                .post("/api/streaming/start")
                .body(StringBody("{\"userId\": #{userId}, \"videoId\": #{videoId}}"))
                .check(status().is(200)))
            .pause(Duration.ofSeconds(10), Duration.ofSeconds(30)) // Simulate watching

            // 5. User's progress is updated
            .exec(http("Update Progress")
                .post("/api/streaming/progress")
                .body(StringBody(session -> {
                    int progress = new Random().nextInt(100);
                    return "{\"userId\": " + session.get("userId") + ", \"videoId\": " + session.get("videoId") + ", \"progress\": " + progress + "}";
                }))
                .check(status().is(200)));
    }
}
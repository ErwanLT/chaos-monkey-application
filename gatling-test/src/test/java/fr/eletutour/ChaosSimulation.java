package fr.eletutour;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class ChaosSimulation extends Simulation {

    private static final String BASE_URL = "http://localhost:8080";

    private static final HttpProtocolBuilder HTTP_PROTOCOL = http.baseUrl(BASE_URL)
            .acceptHeader("*/*")
            .contentTypeHeader("application/json")
            .userAgentHeader("Gatling/ChaosSimulation-Java");

    private static final Iterator<Map<String, Object>> journeyFeeder =
            Stream.generate(() -> {
                Map<String, Object> record = new HashMap<>();
                double profile = ThreadLocalRandom.current().nextDouble();
                String journeyType;

                if (profile < 0.5) {
                    journeyType = "browser";
                } else if (profile < 0.9) {
                    journeyType = "watcher";
                } else {
                    journeyType = "social_watcher";
                }
                record.put("journeyType", journeyType);
                record.put("userId", ThreadLocalRandom.current().nextInt(1, 11));
                return record;
            }).iterator();

    // --- Actions ---

    private static final ChainBuilder browseCatalog =
            exec(http("Get All Video IDs")
                    .get("/api/catalog/videos")
                    .check(status().is(200))
                    .check(jsonPath("$[*].id").findAll().saveAs("allVideoIds")));

    private static final ChainBuilder selectRandomVideo =
            exec(session -> {
                List<Object> allVideoIds = session.getList("allVideoIds");
                if (allVideoIds == null || allVideoIds.isEmpty()) {
                    return session.markAsFailed();
                }
                Object randomVideoId = allVideoIds.get(ThreadLocalRandom.current().nextInt(allVideoIds.size()));
                return session.set("videoId", randomVideoId);
            });

    private static final ChainBuilder viewVideoDetails =
            exec(http("Get Video Details")
                    .get("/api/catalog/videos/#{videoId}")
                    .check(status().is(200))
                    .check(jsonPath("$.trailerUrl").optional().saveAs("trailerUrl")));

    private static final ChainBuilder getRecommendations =
            exec(http("Get Recommendations")
                    .get("/api/recommendations/#{userId}")
                    .check(status().is(200)));

    private static final ChainBuilder startStreaming =
            exec(http("Start Streaming")
                    .post("/api/streaming/start")
                    .body(StringBody("{\"userId\": #{userId}, \"videoId\": #{videoId}}"))
                    .check(status().is(200)));

    private static final ChainBuilder streamVideoChunk =
            exec(http("Stream Video Chunk")
                    .get("/api/streaming/video/#{videoId}")
                    .header("Accept", "video/mp4,*/*;q=0.8")
                    .header("Range", "bytes=0-1048575")
                    .check(status().in(200, 206)));

    private static final ChainBuilder updateProgress =
            exec(session -> {
                return session.set("currentProgress", ThreadLocalRandom.current().nextInt(1, 101));
            })
                    .exec(http("Update Progress")
                            .post("/api/streaming/progress")
                            .body(StringBody("{\"userId\": #{userId}, \"videoId\": #{videoId}, \"progress\": #{currentProgress}}"))
                            .check(status().is(200)));

    // --- Journeys ---

    private static final ChainBuilder browserJourney =
            group("Browser Journey").on(
                    exec(browseCatalog)
                            .exitHereIfFailed()
                            .pause(Duration.ofSeconds(1), Duration.ofSeconds(2))
                            .exec(selectRandomVideo)
                            .exitHereIfFailed()
                            .pause(Duration.ofSeconds(1), Duration.ofSeconds(3))
                            .exec(viewVideoDetails)
                            .exitHereIfFailed()
            );

    private static final ChainBuilder watcherJourney =
            group("Watcher Journey").on(
                    exec(browserJourney)
                            .pause(Duration.ofSeconds(2), Duration.ofSeconds(5))
                            .doIf(session -> {
                                if (!session.contains("trailerUrl")) return false;
                                String trailerUrl = session.getString("trailerUrl");
                                return trailerUrl != null && !trailerUrl.isBlank();
                            }).then(
                                    exec(startStreaming)
                                            .exitHereIfFailed()
                                            .repeat(3).on(
                                                    pause(Duration.ofSeconds(1), Duration.ofSeconds(2))
                                                            .exec(streamVideoChunk)
                                                            .exitHereIfFailed()
                                            )
                                            .pause(Duration.ofSeconds(5), Duration.ofSeconds(10))
                                            .exec(updateProgress)
                            )
            );

    private static final ChainBuilder socialWatcherJourney =
            group("Social Watcher Journey").on(
                    exec(browseCatalog)
                            .exitHereIfFailed()
                            .pause(Duration.ofSeconds(1), Duration.ofSeconds(2))
                            .exec(getRecommendations)
                            .exitHereIfFailed()
                            .pause(Duration.ofSeconds(2), Duration.ofSeconds(5))
                            .exec(selectRandomVideo)
                            .exitHereIfFailed()
                            .pause(Duration.ofSeconds(1), Duration.ofSeconds(3))
                            .exec(viewVideoDetails)
                            .exitHereIfFailed()
                            .pause(Duration.ofSeconds(2), Duration.ofSeconds(5))
                            .doIf(session -> {
                                if (!session.contains("trailerUrl")) return false;
                                String trailerUrl = session.getString("trailerUrl");
                                return trailerUrl != null && !trailerUrl.isBlank();
                            }).then(
                                    exec(startStreaming)
                                            .exitHereIfFailed()
                                            .repeat(2).on(
                                                    pause(Duration.ofSeconds(1), Duration.ofSeconds(2))
                                                            .exec(streamVideoChunk)
                                                            .exitHereIfFailed()
                                            )
                                            .pause(Duration.ofSeconds(5), Duration.ofSeconds(10))
                                            .exec(updateProgress)
                            )
            );

    // --- Scenario ---

    private static final ScenarioBuilder userJourneyScenario = scenario("Realistic User Journey")
            .feed(journeyFeeder)
            .doSwitch("#{journeyType}").on(
                    onCase("browser").then(browserJourney),
                    onCase("watcher").then(watcherJourney),
                    onCase("social_watcher").then(socialWatcherJourney)
            );

    public ChaosSimulation() {
        System.out.printf("[ChaosSimulation] baseUrl=%s%n", BASE_URL);

        setUp(
                userJourneyScenario.injectOpen(
                        incrementUsersPerSec(5)
                                .times(5)
                                .eachLevelLasting(Duration.ofSeconds(30))
                                .separatedByRampsLasting(Duration.ofSeconds(10))
                                .startingFrom(10)
                )
        ).protocols(HTTP_PROTOCOL)
                .assertions(
                        global().successfulRequests().percent().gt(95.0),
                        global().responseTime().percentile3().lt(1500),
                        details("Watcher Journey", "Start Streaming").responseTime().percentile3().lt(1200),
                        details("Social Watcher Journey", "Start Streaming").responseTime().percentile3().lt(1200),
                        details("Watcher Journey", "Stream Video Chunk").failedRequests().percent().lt(5.0),
                        details("Social Watcher Journey", "Stream Video Chunk").failedRequests().percent().lt(5.0)
                );
    }
}

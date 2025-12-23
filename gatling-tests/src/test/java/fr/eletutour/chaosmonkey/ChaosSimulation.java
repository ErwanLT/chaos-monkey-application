package fr.eletutour.chaosmonkey;

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

    private static final HttpProtocolBuilder HTTP_PROTOCOL = http.baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
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
                    // On extrait tous les IDs. Attention : jsonPath doit correspondre à ton JSON
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
                    .check(status().is(200)));

    private static final ChainBuilder getRecommendations =
            exec(http("Get Recommendations")
                    .get("/api/recommendations/#{userId}")
                    .check(status().is(200)));

    private static final ChainBuilder startStreaming =
            exec(http("Start Streaming")
                    .post("/api/streaming/start")
                    // Utilisation de #{videoId} avec guillemets si c'est un String
                    .body(StringBody("{\"userId\": #{userId}, \"videoId\": \"#{videoId}\"}"))
                    .check(status().is(200)));

    private static final ChainBuilder updateProgress =
            exec(session -> {
                // On génère la valeur aléatoire dans la session AVANT l'appel
                return session.set("currentProgress", ThreadLocalRandom.current().nextInt(1, 101));
            })
                    .exec(http("Update Progress")
                            .post("/api/streaming/progress")
                            // Syntaxe propre utilisant les variables de session
                            .body(StringBody("{\"userId\": #{userId}, \"videoId\": \"#{videoId}\", \"progress\": #{currentProgress}}"))
                            .check(status().is(200)));

    // --- Journeys ---

    private static final ChainBuilder browserJourney =
            group("Browser Journey").on(
                    exec(browseCatalog)
                            .pause(Duration.ofSeconds(1), Duration.ofSeconds(2))
                            .exec(selectRandomVideo)
                            .pause(Duration.ofSeconds(1), Duration.ofSeconds(3))
                            .exec(viewVideoDetails)
            );

    private static final ChainBuilder watcherJourney =
            group("Watcher Journey").on(
                    exec(browserJourney)
                            .pause(Duration.ofSeconds(2), Duration.ofSeconds(5))
                            .exec(startStreaming)
                            .pause(Duration.ofSeconds(5), Duration.ofSeconds(10))
                            .exec(updateProgress)
            );

    private static final ChainBuilder socialWatcherJourney =
            group("Social Watcher Journey").on(
                    exec(browseCatalog)
                            .pause(Duration.ofSeconds(1), Duration.ofSeconds(2))
                            .exec(getRecommendations)
                            .pause(Duration.ofSeconds(2), Duration.ofSeconds(5))
                            .exec(selectRandomVideo)
                            .pause(Duration.ofSeconds(1), Duration.ofSeconds(3))
                            .exec(viewVideoDetails)
                            .pause(Duration.ofSeconds(2), Duration.ofSeconds(5))
                            .exec(startStreaming)
                            .pause(Duration.ofSeconds(5), Duration.ofSeconds(10))
                            .exec(updateProgress)
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
        setUp(
                userJourneyScenario.injectOpen(
                        incrementUsersPerSec(5) // On commence à 5 users/sec
                                .times(5)           // On augmente 5 fois
                                .eachLevelLasting(Duration.ofSeconds(30)) // Chaque palier dure 30s
                                .separatedByRampsLasting(Duration.ofSeconds(10)) // Transition douce
                                .startingFrom(10)   // Départ à 10 users/sec
                )
        ).protocols(HTTP_PROTOCOL);
    }
}
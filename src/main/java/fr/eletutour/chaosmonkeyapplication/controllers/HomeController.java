package fr.eletutour.chaosmonkeyapplication.controllers;

import fr.eletutour.chaosmonkeyapplication.exception.RequestTimeoutException;
import fr.eletutour.chaosmonkeyapplication.models.Video;
import fr.eletutour.chaosmonkeyapplication.services.CatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);
    private final CatalogService catalogService;

    public HomeController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    /**
     * Helper pour exécuter une tâche avec un timeout strict et fermeture forcée du
     * thread.
     */
    private <T> T executeWithTimeout(String taskName, Supplier<T> supplier) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            log.info("[{}] ⏳ Début attente (Timeout réglé à 2s)...", taskName);
            Future<T> future = executor.submit(() -> {
                long start = System.currentTimeMillis();
                log.info("[{}] 🟢 Thread Worker démarré", taskName);
                T result = supplier.get();
                log.info("[{}] ✅ Service terminé proprement en {} ms", taskName,
                        (System.currentTimeMillis() - start));
                return result;
            });

            return future.get(2, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.error("[{}] 💥 TIMEOUT déclenché ! Fermeture forcée du thread.", taskName);
            throw new RequestTimeoutException("Le service a mis trop de temps.");
        } catch (InterruptedException | ExecutionException e) {
            log.error("[{}] ❌ Erreur : {}", taskName, e.getMessage());
            throw new RuntimeException("Erreur lors de l'exécution", e);
        } finally {
            executor.shutdownNow();
        }
    }

    @GetMapping("/")
    public String home(Model model) {
        long startTotal = System.currentTimeMillis();
        log.info(">>> [HOME] Entrée dans la méthode home()");

        // Pour la home, on a 3 tâches en parallèle, donc on utilise 3 threads
        ExecutorService executor = Executors.newFixedThreadPool(3);
        try {
            log.info("[HOME] ⏳ Attente globale des 3 tâches (Max 2s)...");

            Future<List<Video>> pFuture = executor.submit(catalogService::getPopularVideos);
            Future<List<Video>> tFuture = executor.submit(catalogService::getTopRatedVideos);
            Future<List<Video>> aFuture = executor.submit(catalogService::getAllVideos);

            // On récupère tout (le get() bloquera jusqu'à 2s max par tâche)
            // Note: Comme elles tournent en parallèle, le temps total sera ~Max(tâches)
            List<Video> popularVideos = pFuture.get(2, TimeUnit.SECONDS);
            List<Video> topRatedVideos = tFuture.get(2, TimeUnit.SECONDS);
            List<Video> allVideos = aFuture.get(2, TimeUnit.SECONDS);

            log.info("[HOME] 🏁 Tout est arrivé à temps !");

            Video featuredVideo = popularVideos.isEmpty() ? null : popularVideos.get(0);
            model.addAttribute("featuredVideo", featuredVideo);
            model.addAttribute("popularVideos", popularVideos);
            model.addAttribute("topRatedVideos", topRatedVideos);
            model.addAttribute("genres", allVideos.stream().collect(Collectors.groupingBy(Video::getGenre)));

            log.info("<<< [HOME] Sortie après {} ms", (System.currentTimeMillis() - startTotal));
            return "index";

        } catch (TimeoutException e) {
            log.error("[HOME] 💥 TIMEOUT GLOBAL après {} ms", (System.currentTimeMillis() - startTotal));
            throw new RequestTimeoutException("Le chargement de la home a pris trop de temps.");
        } catch (Exception e) {
            log.error("[HOME] ❌ Erreur : {}", e.getMessage());
            throw new RuntimeException("Erreur lors du chargement de la home", e);
        } finally {
            executor.shutdownNow();
        }
    }

    @GetMapping("/movies")
    public String movies(Model model) {
        log.info(">>> [MOVIES] Entrée dans la méthode movies()");
        List<Video> movies = executeWithTimeout("MOVIES-TASK",
                () -> catalogService.getVideosByType(Video.VideoType.MOVIE));

        Video featuredVideo = movies.isEmpty() ? null : movies.get(0);
        model.addAttribute("featuredVideo", featuredVideo);
        model.addAttribute("genres", movies.stream().collect(Collectors.groupingBy(Video::getGenre)));

        return "index";
    }

    @GetMapping("/series")
    public String series(Model model) {
        log.info(">>> [SERIES] Entrée dans la méthode series()");
        List<Video> series = executeWithTimeout("SERIES-TASK",
                () -> catalogService.getVideosByType(Video.VideoType.SERIES));

        Video featuredVideo = series.isEmpty() ? null : series.get(0);
        model.addAttribute("featuredVideo", featuredVideo);
        model.addAttribute("genres", series.stream().collect(Collectors.groupingBy(Video::getGenre)));

        return "index";
    }

    @GetMapping("/new-popular")
    public String newPopular(Model model) {
        log.info(">>> [NEW-POPULAR] Entrée dans la méthode newPopular()");
        List<Video> popular = executeWithTimeout("POPULAR-TASK", catalogService::getPopularVideos);

        Video featuredVideo = popular.isEmpty() ? null : popular.get(0);
        model.addAttribute("featuredVideo", featuredVideo);
        model.addAttribute("genres", Collections.singletonMap("Trending Now", popular));

        return "index";
    }

    @GetMapping("/my-list")
    public String myList(Model model) {
        log.info(">>> [MY-LIST] Entrée dans la méthode myList()");
        List<Video> allVideos = executeWithTimeout("MYLIST-TASK", catalogService::getAllVideos);

        List<Video> myList = allVideos.stream().limit(5).collect(Collectors.toList());
        Video featuredVideo = myList.isEmpty() ? null : myList.get(0);

        model.addAttribute("featuredVideo", featuredVideo);
        model.addAttribute("genres", Collections.singletonMap("My List", myList));

        return "index";
    }

    @GetMapping("/search")
    public String search(@RequestParam("q") String query, Model model) {
        log.info(">>> [SEARCH] Entrée dans la méthode search() pour '{}'", query);
        List<Video> searchResults = executeWithTimeout("SEARCH-TASK", () -> catalogService.searchVideos(query));

        Video featuredVideo = searchResults.isEmpty() ? null : searchResults.get(0);
        model.addAttribute("featuredVideo", featuredVideo);
        model.addAttribute("genres", Collections.singletonMap("Search Results for '" + query + "'", searchResults));

        return "index";
    }
}

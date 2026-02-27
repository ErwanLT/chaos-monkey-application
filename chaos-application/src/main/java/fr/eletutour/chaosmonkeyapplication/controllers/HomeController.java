package fr.eletutour.chaosmonkeyapplication.controllers;

import fr.eletutour.chaosmonkeyapplication.configurations.UIConfiguration;
import fr.eletutour.chaosmonkeyapplication.exception.CatalogException;
import fr.eletutour.chaosmonkeyapplication.exception.RequestTimeoutException;
import fr.eletutour.chaosmonkeyapplication.models.Video;
import fr.eletutour.chaosmonkeyapplication.services.CatalogService;
import fr.eletutour.chaosmonkeyapplication.services.SectionService;
import fr.eletutour.chaosmonkeyapplication.services.StreamingService;
import fr.eletutour.chaosmonkeyapplication.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.security.Principal;

@Controller
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);
    private final CatalogService catalogService;
    private final UIConfiguration uiConfiguration;
    private final StreamingService streamingService;
    private final SectionService sectionService;
    private final UserService userService;
    private static final Long DEFAULT_USER_ID = 1L;

    public HomeController(CatalogService catalogService, UIConfiguration uiConfiguration,
            StreamingService streamingService,
            SectionService sectionService,
            UserService userService) {
        this.catalogService = catalogService;
        this.uiConfiguration = uiConfiguration;
        this.streamingService = streamingService;
        this.sectionService = sectionService;
        this.userService = userService;
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

            Video featuredVideo = popularVideos.isEmpty() ? null : popularVideos.getFirst();
            model.addAttribute("featuredVideo", featuredVideo);
            model.addAttribute("popularVideos", popularVideos);
            model.addAttribute("topRatedVideos", topRatedVideos);
            model.addAttribute("genres", allVideos.stream().collect(Collectors.groupingBy(Video::getGenre)));

            log.info("<<< [HOME] Sortie après {} ms", (System.currentTimeMillis() - startTotal));
            if ("v2".equals(uiConfiguration.getUiVersion())) {
                return "index_v2";
            }
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

        if (movies.isEmpty()) {
            throw new CatalogException(CatalogException.CatalogError.VIDEO_NOT_FOUND, "Aucun film trouvé");
        }

        Video featuredVideo = movies.getFirst();
        model.addAttribute("featuredVideo", featuredVideo);
        model.addAttribute("genres", movies.stream().collect(Collectors.groupingBy(Video::getGenre)));

        if ("v2".equals(uiConfiguration.getUiVersion())) {
            return "index_v2";
        }
        return "index";
    }

    @GetMapping("/series")
    public String series(Model model) {
        log.info(">>> [SERIES] Entrée dans la méthode series()");
        List<Video> series = executeWithTimeout("SERIES-TASK",
                () -> catalogService.getVideosByType(Video.VideoType.SERIES));

        if (series.isEmpty()) {
            throw new CatalogException(CatalogException.CatalogError.VIDEO_NOT_FOUND, "Aucune série trouvée");
        }

        Video featuredVideo = series.getFirst();
        model.addAttribute("featuredVideo", featuredVideo);
        model.addAttribute("genres", series.stream().collect(Collectors.groupingBy(Video::getGenre)));

        if ("v2".equals(uiConfiguration.getUiVersion())) {
            return "index_v2";
        }
        return "index";
    }

    @GetMapping("/new-popular")
    public String newPopular(Model model) {
        log.info(">>> [NEW-POPULAR] Entrée dans la méthode newPopular()");
        List<Video> popular = executeWithTimeout("POPULAR-TASK", catalogService::getPopularVideos);

        Video featuredVideo = popular.isEmpty() ? null : popular.getFirst();
        model.addAttribute("featuredVideo", featuredVideo);
        model.addAttribute("genres", Collections.singletonMap("Trending Now", popular));

        if ("v2".equals(uiConfiguration.getUiVersion())) {
            return "index_v2";
        }
        return "index";
    }

    @GetMapping("/my-list")
    public String myList(Model model) {
        log.info(">>> [MY-LIST] Entrée dans la méthode myList()");
        List<Video> allVideos = executeWithTimeout("MYLIST-TASK", catalogService::getAllVideos);

        List<Video> myList = allVideos.stream().limit(5).collect(Collectors.toList());
        Video featuredVideo = myList.isEmpty() ? null : myList.getFirst();

        model.addAttribute("featuredVideo", featuredVideo);
        model.addAttribute("genres", Collections.singletonMap("My List", myList));

        if ("v2".equals(uiConfiguration.getUiVersion())) {
            return "index_v2";
        }
        return "index";
    }

    @GetMapping("/search")
    public String search(@RequestParam("q") String query, Model model) {
        log.info(">>> [SEARCH] Entrée dans la méthode search() pour '{}'", query);
        List<Video> searchResults = executeWithTimeout("SEARCH-TASK", () -> catalogService.searchVideos(query));

        if (searchResults.isEmpty()) {
            throw new CatalogException(CatalogException.CatalogError.VIDEO_NOT_FOUND,
                    "Aucun résultat pour '" + query + "'");
        }

        Video featuredVideo = searchResults.getFirst();
        model.addAttribute("featuredVideo", featuredVideo);
        model.addAttribute("genres", Collections.singletonMap("Search Results for '" + query + "'", searchResults));

        if ("v2".equals(uiConfiguration.getUiVersion())) {
            return "index_v2";
        }
        return "index";
    }

    @GetMapping("/section")
    public String section(@RequestParam("name") String name, Model model) {
        log.info(">>> [SECTION] Entrée dans la méthode section() pour '{}'", name);
        List<Video> sectionVideos = executeWithTimeout("SECTION-TASK", () -> sectionService.getVideosBySection(name));

        if (sectionVideos.isEmpty()) {
            throw new CatalogException(CatalogException.CatalogError.VIDEO_NOT_FOUND,
                    "Aucune vidéo pour la section '" + name + "'");
        }

        Video featuredVideo = sectionVideos.getFirst();
        model.addAttribute("featuredVideo", featuredVideo);
        model.addAttribute("genres", sectionVideos.stream().collect(Collectors.groupingBy(Video::getGenre)));

        if ("v2".equals(uiConfiguration.getUiVersion())) {
            return "index_v2";
        }
        return "index";
    }

    private Long resolveCurrentUserId(Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            log.warn("[WATCH] Aucun principal en session, fallback userId={}", DEFAULT_USER_ID);
            return DEFAULT_USER_ID;
        }

        String principalName = principal.getName();
        try {
            return Long.parseLong(principalName);
        } catch (NumberFormatException ignored) {
            return userService.getUserByEmail(principalName)
                    .map(user -> user.getId())
                    .orElseGet(() -> {
                        log.warn("[WATCH] Principal '{}' introuvable en base, fallback userId={}",
                                principalName, DEFAULT_USER_ID);
                        return DEFAULT_USER_ID;
                    });
        }
    }

    @GetMapping("/watch/{id}")
    public String watch(@PathVariable Long id, Model model, Principal principal) {
        log.info(">>> [WATCH] Entrée dans la méthode watch() pour videoId={}", id);
        Long currentUserId = resolveCurrentUserId(principal);

        // 1. Récupérer les métadonnées de la vidéo
        Optional<Video> videoOpt = catalogService.getVideoById(id);
        if (videoOpt.isEmpty()) {
            throw new CatalogException(CatalogException.CatalogError.VIDEO_NOT_FOUND, "id=" + id);
        }
        model.addAttribute("video", videoOpt.get());
        model.addAttribute("userId", currentUserId);

        // 2. Initialiser la session de streaming
        log.info("[WATCH] Initialisation stream pour userId={}, videoId={}", currentUserId, id);
        
        try {
            Map<String, Object> streamInfo = streamingService.startStream(currentUserId, id);
            log.info("[WATCH] Le service de streaming a répondu : status={}, quality={}",
                    streamInfo.get("status"), streamInfo.get("quality"));
            model.addAttribute("streamInfo", streamInfo);
        } catch (Exception e) {
            log.error("[WATCH] Erreur lors de l'appel au service de streaming : {}", e.getMessage());
            // Fallback manuel si l'appel API échoue
            Map<String, Object> fallbackInfo = Map.of(
                "status", "TEMPORARILY_UNAVAILABLE",
                "quality", "N/A",
                "streamUrl", "",
                "error", "Impossible de joindre le service de streaming"
            );
            model.addAttribute("streamInfo", fallbackInfo);
        }

        return "player_v2";
    }

    /**
     * Endpoint réactif pour le streaming de la vidéo.
     * Appelle le service de streaming pour obtenir la ressource de façon réactive.
     */
    @GetMapping(value = "/api/streaming/video/{id}", produces = "video/mp4")
    @ResponseBody
    public Mono<Resource> streamVideo(@PathVariable Long id) {
        log.info("[CONTROLLER] Requête de flux réactif pour la vidéo ID: {}", id);
        return streamingService.getVideoResource(id);
    }
}

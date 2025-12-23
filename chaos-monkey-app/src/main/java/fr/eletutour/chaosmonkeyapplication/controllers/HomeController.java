package fr.eletutour.chaosmonkeyapplication.controllers;

import fr.eletutour.chaosmonkeyapplication.models.Video;
import fr.eletutour.chaosmonkeyapplication.services.CatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final CatalogService catalogService;

    public HomeController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Video> popularVideos = catalogService.getPopularVideos();
        List<Video> topRatedVideos = catalogService.getTopRatedVideos();

        Video featuredVideo = popularVideos.isEmpty() ? null : popularVideos.get(0);

        model.addAttribute("featuredVideo", featuredVideo);
        model.addAttribute("popularVideos", popularVideos);
        model.addAttribute("topRatedVideos", topRatedVideos);
        model.addAttribute("genres",
                catalogService.getAllVideos().stream().collect(Collectors.groupingBy(Video::getGenre)));

        return "index";
    }

    @GetMapping("/movies")
    public String movies(Model model) {
        List<Video> movies = catalogService.getVideosByType(Video.VideoType.MOVIE);
        Video featuredVideo = movies.isEmpty() ? null : movies.get(0);

        model.addAttribute("featuredVideo", featuredVideo);
        model.addAttribute("genres", movies.stream().collect(Collectors.groupingBy(Video::getGenre)));

        return "index";
    }

    @GetMapping("/series")
    public String series(Model model) {
        List<Video> series = catalogService.getVideosByType(Video.VideoType.SERIES);
        Video featuredVideo = series.isEmpty() ? null : series.get(0);

        model.addAttribute("featuredVideo", featuredVideo);
        model.addAttribute("genres", series.stream().collect(Collectors.groupingBy(Video::getGenre)));

        return "index";
    }

    @GetMapping("/new-popular")
    public String newPopular(Model model) {
        List<Video> popular = catalogService.getPopularVideos();
        Video featuredVideo = popular.isEmpty() ? null : popular.get(0);

        model.addAttribute("featuredVideo", featuredVideo);
        model.addAttribute("genres", java.util.Collections.singletonMap("Trending Now", popular));

        return "index";
    }

    @GetMapping("/my-list")
    public String myList(Model model) {
        List<Video> myList = catalogService.getAllVideos().stream().limit(5).collect(Collectors.toList());
        Video featuredVideo = myList.isEmpty() ? null : myList.get(0);

        model.addAttribute("featuredVideo", featuredVideo);
        model.addAttribute("genres", java.util.Collections.singletonMap("My List", myList));

        return "index";
    }

    @GetMapping("/search")
    public String search(@RequestParam("q") String query, Model model) {
        List<Video> searchResults = catalogService.searchVideos(query);
        Video featuredVideo = searchResults.isEmpty() ? null : searchResults.get(0);

        model.addAttribute("featuredVideo", featuredVideo);
        model.addAttribute("genres",
                java.util.Collections.singletonMap("Search Results for '" + query + "'", searchResults));

        return "index";
    }
}

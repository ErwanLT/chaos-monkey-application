package fr.eletutour.chaosmonkeyapplication;

import fr.eletutour.chaosmonkeyapplication.models.*;
import fr.eletutour.chaosmonkeyapplication.repositories.*;
import fr.eletutour.chaosmonkeyapplication.services.RecommendationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DataLoader implements CommandLineRunner {

    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final WatchHistoryRepository watchHistoryRepository;
    private final RecommendationService recommendationService;

    public DataLoader(VideoRepository videoRepository,
            UserRepository userRepository,
            WatchHistoryRepository watchHistoryRepository,
            RecommendationService recommendationService) {
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
        this.watchHistoryRepository = watchHistoryRepository;
        this.recommendationService = recommendationService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🎬 Loading Netflix-like streaming data...");

        // Load Videos
        List<Video> videos = loadVideos();

        // Load Users
        List<User> users = loadUsers();

        // Load Watch History
        loadWatchHistory(users, videos);

        // Generate Recommendations
        for (User user : users) {
            recommendationService.generateRecommendations(user.getId());
        }

        System.out.println("✅ Streaming service data loaded successfully!");
        System.out.println("📊 Total Videos: " + videos.size());
        System.out.println("👥 Total Users: " + users.size());
    }

    private List<Video> loadVideos() {
        List<Video> videos = new ArrayList<>();

        // Action Movies
        videos.add(videoRepository.save(new Video("The Dark Sentinel", "A vigilante fights crime in a dystopian city",
                "Action", 2023, 142, 8.5, "/thumbnails/dark-sentinel.jpg", Video.VideoType.MOVIE)));
        videos.add(videoRepository.save(new Video("Velocity", "High-speed chase across continents", "Action", 2022, 118,
                7.8, "/thumbnails/velocity.jpg", Video.VideoType.MOVIE)));
        videos.add(videoRepository.save(new Video("Iron Protocol", "Elite soldiers on a dangerous mission", "Action",
                2021, 135, 8.2, "/thumbnails/iron-protocol.jpg", Video.VideoType.MOVIE)));
        videos.add(videoRepository.save(new Video("Rogue Agent", "A spy goes off the grid", "Action", 2023, 128, 7.9,
                "/thumbnails/rogue-agent.jpg", Video.VideoType.MOVIE)));

        // Sci-Fi Movies
        videos.add(videoRepository.save(new Video("Quantum Horizon", "Scientists discover parallel universes", "Sci-Fi",
                2023, 156, 9.1, "/thumbnails/quantum-horizon.jpg", Video.VideoType.MOVIE)));
        videos.add(videoRepository.save(new Video("Nebula Station", "Life on a distant space station", "Sci-Fi", 2022,
                145, 8.7, "/thumbnails/nebula-station.jpg", Video.VideoType.MOVIE)));
        videos.add(videoRepository.save(new Video("Synthetic Dreams", "AI achieves consciousness", "Sci-Fi", 2021, 132,
                8.9, "/thumbnails/synthetic-dreams.jpg", Video.VideoType.MOVIE)));
        videos.add(videoRepository.save(new Video("The Last Colony", "Humanity's final outpost", "Sci-Fi", 2020, 148,
                8.4, "/thumbnails/last-colony.jpg", Video.VideoType.MOVIE)));
        videos.add(videoRepository.save(new Video("Chronos Effect", "Time travel paradox thriller", "Sci-Fi", 2023, 139,
                8.8, "/thumbnails/chronos-effect.jpg", Video.VideoType.MOVIE)));

        // Drama Movies
        videos.add(videoRepository.save(new Video("Echoes of Silence", "A musician's journey to redemption", "Drama",
                2023, 124, 8.6, "/thumbnails/echoes-silence.jpg", Video.VideoType.MOVIE)));
        videos.add(videoRepository.save(new Video("The Painter's Legacy", "An artist's final masterpiece", "Drama",
                2022, 118, 8.3, "/thumbnails/painters-legacy.jpg", Video.VideoType.MOVIE)));
        videos.add(videoRepository.save(new Video("Broken Bridges", "Family reunion after decades", "Drama", 2021, 112,
                7.9, "/thumbnails/broken-bridges.jpg", Video.VideoType.MOVIE)));
        videos.add(videoRepository.save(new Video("Whispers in the Wind", "Love story across generations", "Drama",
                2023, 128, 8.1, "/thumbnails/whispers-wind.jpg", Video.VideoType.MOVIE)));

        // Comedy Movies
        videos.add(videoRepository.save(new Video("The Mishap", "Everything that can go wrong, does", "Comedy", 2023,
                98, 7.5, "/thumbnails/mishap.jpg", Video.VideoType.MOVIE)));
        videos.add(videoRepository.save(new Video("Office Chaos", "Corporate comedy at its finest", "Comedy", 2022, 105,
                7.8, "/thumbnails/office-chaos.jpg", Video.VideoType.MOVIE)));
        videos.add(videoRepository.save(new Video("Wedding Crashers 2.0", "Digital age wedding disasters", "Comedy",
                2023, 102, 7.2, "/thumbnails/wedding-crashers.jpg", Video.VideoType.MOVIE)));

        // Thriller Movies
        videos.add(videoRepository.save(new Video("The Vanishing", "A detective hunts a serial killer", "Thriller",
                2023, 134, 8.4, "/thumbnails/vanishing.jpg", Video.VideoType.MOVIE)));
        videos.add(videoRepository.save(new Video("Midnight Protocol", "Hacker uncovers conspiracy", "Thriller", 2022,
                126, 8.1, "/thumbnails/midnight-protocol.jpg", Video.VideoType.MOVIE)));
        videos.add(videoRepository.save(new Video("Silent Witness", "Courtroom psychological thriller", "Thriller",
                2021, 119, 7.9, "/thumbnails/silent-witness.jpg", Video.VideoType.MOVIE)));

        // Horror Movies
        videos.add(videoRepository.save(new Video("The Haunting of Blackwood", "Cursed mansion horror", "Horror", 2023,
                108, 7.6, "/thumbnails/blackwood.jpg", Video.VideoType.MOVIE)));
        videos.add(videoRepository.save(new Video("Shadows Below", "Underground terror awakens", "Horror", 2022, 95,
                7.3, "/thumbnails/shadows-below.jpg", Video.VideoType.MOVIE)));

        // Series - Drama
        videos.add(videoRepository.save(new Video("Crown of Thorns - Season 1", "Medieval power struggle", "Drama",
                2023, 480, 9.2, "/thumbnails/crown-thorns.jpg", Video.VideoType.SERIES)));
        videos.add(videoRepository.save(new Video("Crown of Thorns - Season 2", "The war continues", "Drama", 2023, 480,
                9.3, "/thumbnails/crown-thorns-s2.jpg", Video.VideoType.SERIES)));
        videos.add(videoRepository.save(new Video("Silicon Valley Dreams", "Tech startup drama", "Drama", 2022, 400,
                8.5, "/thumbnails/silicon-valley.jpg", Video.VideoType.SERIES)));
        videos.add(videoRepository.save(new Video("The Diplomat", "International political intrigue", "Drama", 2023,
                420, 8.7, "/thumbnails/diplomat.jpg", Video.VideoType.SERIES)));

        // Series - Sci-Fi
        videos.add(videoRepository.save(new Video("Starbound - Season 1", "Interstellar exploration", "Sci-Fi", 2023,
                520, 9.0, "/thumbnails/starbound.jpg", Video.VideoType.SERIES)));
        videos.add(videoRepository.save(new Video("Starbound - Season 2", "New worlds discovered", "Sci-Fi", 2023, 520,
                9.1, "/thumbnails/starbound-s2.jpg", Video.VideoType.SERIES)));
        videos.add(videoRepository.save(new Video("The Expanse: Aftermath", "Post-war space opera", "Sci-Fi", 2022, 450,
                8.9, "/thumbnails/expanse-aftermath.jpg", Video.VideoType.SERIES)));
        videos.add(videoRepository.save(new Video("Cyberpunk Chronicles", "Dystopian future noir", "Sci-Fi", 2023, 380,
                8.6, "/thumbnails/cyberpunk.jpg", Video.VideoType.SERIES)));

        // Series - Crime
        videos.add(videoRepository.save(new Video("Detective Noir", "Hard-boiled detective stories", "Crime", 2023, 360,
                8.4, "/thumbnails/detective-noir.jpg", Video.VideoType.SERIES)));
        videos.add(videoRepository.save(new Video("The Cartel", "Drug empire rise and fall", "Crime", 2022, 480, 8.8,
                "/thumbnails/cartel.jpg", Video.VideoType.SERIES)));
        videos.add(videoRepository.save(new Video("White Collar Crimes", "Financial fraud investigation", "Crime", 2023,
                340, 8.2, "/thumbnails/white-collar.jpg", Video.VideoType.SERIES)));

        // Series - Comedy
        videos.add(videoRepository.save(new Video("The IT Crowd Reboot", "Tech support comedy", "Comedy", 2023, 240,
                8.1, "/thumbnails/it-crowd.jpg", Video.VideoType.SERIES)));
        videos.add(videoRepository.save(new Video("Apartment 42", "Roommate shenanigans", "Comedy", 2022, 220, 7.9,
                "/thumbnails/apartment-42.jpg", Video.VideoType.SERIES)));

        // Documentaries
        videos.add(videoRepository.save(new Video("Planet Earth: The Future", "Climate change documentary",
                "Documentary", 2023, 180, 9.4, "/thumbnails/planet-earth.png", Video.VideoType.DOCUMENTARY)));
        videos.add(videoRepository.save(new Video("The AI Revolution", "Artificial intelligence impact", "Documentary",
                2023, 95, 8.7, "/thumbnails/ai-revolution.png", Video.VideoType.DOCUMENTARY)));
        videos.add(videoRepository.save(new Video("Ocean Depths", "Deep sea exploration", "Documentary", 2022, 120, 9.0,
                "/thumbnails/ocean-depths.png", Video.VideoType.DOCUMENTARY)));
        videos.add(videoRepository.save(new Video("Ancient Civilizations", "Lost cities uncovered", "Documentary", 2023,
                240, 8.9, "/thumbnails/ancient-civs.jpg", Video.VideoType.DOCUMENTARY)));
        videos.add(videoRepository.save(new Video("The Space Race 2.0", "Modern space exploration", "Documentary", 2023,
                110, 8.8, "/thumbnails/space-race.jpg", Video.VideoType.DOCUMENTARY)));
        videos.add(videoRepository.save(new Video("Wildlife Warriors", "Conservation heroes", "Documentary", 2022, 85,
                8.5, "/thumbnails/wildlife-warriors.jpg", Video.VideoType.DOCUMENTARY)));
        videos.add(videoRepository.save(new Video("The Crypto Phenomenon", "Blockchain revolution", "Documentary", 2023,
                102, 8.3, "/thumbnails/crypto.jpg", Video.VideoType.DOCUMENTARY)));
        videos.add(videoRepository.save(new Video("Mind Matters", "Neuroscience breakthroughs", "Documentary", 2023, 95,
                8.6, "/thumbnails/mind-matters.jpg", Video.VideoType.DOCUMENTARY)));

        // Romance
        videos.add(videoRepository.save(new Video("Love in Paris", "Romantic comedy in France", "Romance", 2023, 108,
                7.8, "/thumbnails/love-paris.jpg", Video.VideoType.MOVIE)));
        videos.add(videoRepository.save(new Video("Second Chances", "Finding love after loss", "Romance", 2022, 115,
                8.0, "/thumbnails/second-chances.jpg", Video.VideoType.MOVIE)));
        videos.add(videoRepository.save(new Video("The Letter", "Long-distance love story", "Romance", 2023, 102, 7.6,
                "/thumbnails/letter.jpg", Video.VideoType.MOVIE)));

        // Fantasy
        videos.add(videoRepository.save(new Video("Realm of Dragons", "Epic fantasy adventure", "Fantasy", 2023, 165,
                8.9, "/thumbnails/realm-dragons.jpg", Video.VideoType.MOVIE)));
        videos.add(videoRepository.save(new Video("The Sorcerer's Apprentice Returns", "Magic school adventures",
                "Fantasy", 2022, 138, 8.2, "/thumbnails/sorcerer.jpg", Video.VideoType.MOVIE)));
        videos.add(videoRepository.save(new Video("Legends of Avalon", "Arthurian legend reimagined", "Fantasy", 2023,
                520, 9.0, "/thumbnails/avalon.jpg", Video.VideoType.SERIES)));

        System.out.println("✅ Loaded " + videos.size() + " videos");
        return videos;
    }

    private List<User> loadUsers() {
        List<User> users = new ArrayList<>();

        users.add(userRepository.save(new User("john.doe@email.com", "JohnD", User.SubscriptionType.PREMIUM, "USA")));
        users.add(
                userRepository.save(new User("sarah.smith@email.com", "SarahS", User.SubscriptionType.STANDARD, "UK")));
        users.add(
                userRepository.save(new User("mike.wilson@email.com", "MikeW", User.SubscriptionType.BASIC, "Canada")));
        users.add(userRepository
                .save(new User("emma.brown@email.com", "EmmaB", User.SubscriptionType.PREMIUM, "Australia")));
        users.add(userRepository
                .save(new User("alex.garcia@email.com", "AlexG", User.SubscriptionType.STANDARD, "Spain")));
        users.add(userRepository
                .save(new User("lisa.martin@email.com", "LisaM", User.SubscriptionType.PREMIUM, "France")));
        users.add(userRepository
                .save(new User("david.lee@email.com", "DavidL", User.SubscriptionType.BASIC, "South Korea")));
        users.add(userRepository
                .save(new User("maria.rodriguez@email.com", "MariaR", User.SubscriptionType.STANDARD, "Mexico")));
        users.add(userRepository
                .save(new User("james.taylor@email.com", "JamesT", User.SubscriptionType.PREMIUM, "USA")));
        users.add(userRepository
                .save(new User("anna.mueller@email.com", "AnnaM", User.SubscriptionType.BASIC, "Germany")));

        System.out.println("✅ Loaded " + users.size() + " users");
        return users;
    }

    private void loadWatchHistory(List<User> users, List<Video> videos) {
        Random random = new Random();
        int historyCount = 0;

        // Generate random watch history for users
        for (User user : users) {
            int videosWatched = 3 + random.nextInt(8); // 3-10 videos per user

            for (int i = 0; i < videosWatched; i++) {
                Video randomVideo = videos.get(random.nextInt(videos.size()));
                int progress = 20 + random.nextInt(80); // 20-100% progress

                watchHistoryRepository.save(new WatchHistory(user.getId(), randomVideo.getId(), progress));

                // Increment view count
                randomVideo.setViewCount(randomVideo.getViewCount() + 1);
                videoRepository.save(randomVideo);

                historyCount++;
            }
        }

        System.out.println("✅ Loaded " + historyCount + " watch history entries");
    }
}

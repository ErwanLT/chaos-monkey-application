package fr.eletutour.chaosmonkeyapplication;

import fr.eletutour.chaosmonkeyapplication.models.User;
import fr.eletutour.chaosmonkeyapplication.models.Video;
import fr.eletutour.chaosmonkeyapplication.models.WatchHistory;
import fr.eletutour.chaosmonkeyapplication.repositories.UserRepository;
import fr.eletutour.chaosmonkeyapplication.repositories.VideoRepository;
import fr.eletutour.chaosmonkeyapplication.repositories.WatchHistoryRepository;
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
        videos.add(videoRepository.save(new Video("The Dark Sentinel",
                "A vigilante fights crime in a dystopian city",
                "Action", 2023, 142, 8.5, "/thumbnails/dark-sentinel.png", "/trailer/dark-sentinel.mp4",
                "Jean Benbois, Archibald Aquin, Sandra Nicouette", Video.VideoType.MOVIE)));
        videos.add(videoRepository
                .save(new Video("Velocity", "High-speed chase across continents", "Action", 2022, 118,
                        7.8, "/thumbnails/velocity.png", null,
                        "Élie Vafranco, Jean Trenscène, Laure Aison", Video.VideoType.MOVIE)));
        videos.add(videoRepository.save(new Video("Iron Protocol", "Elite soldiers on a dangerous mission",
                "Action",
                2021, 135, 8.2, "/thumbnails/iron-protocol.png", null,
                "Alex Terrieur, Sarah Pelle, Bob Leponge", Video.VideoType.MOVIE)));
        videos.add(videoRepository
                .save(new Video("Rogue Agent", "A spy goes off the grid", "Action", 2023, 128, 7.9,
                        "/thumbnails/rogue-agent.png", null,
                        "Guy Tare, Henri Cochet, Bella Maman", Video.VideoType.MOVIE)));

        // Sci-Fi Movies
        videos.add(videoRepository.save(new Video("Quantum Horizon", "Scientists discover parallel universes",
                "Sci-Fi",
                2023, 156, 9.1, "/thumbnails/quantum-horizon.png", null,
                "Marc Assin, Anne Onyme, Ella Bora", Video.VideoType.MOVIE)));
        videos.add(videoRepository.save(new Video("Nebula Station", "Life on a distant space station", "Sci-Fi",
                2022,
                145, 8.7, "/thumbnails/nebula-station.png", null,
                "Sam Suffit, Zoé Taure, Céline Dion", Video.VideoType.MOVIE)));
        videos.add(videoRepository
                .save(new Video("Synthetic Dreams", "AI achieves consciousness", "Sci-Fi", 2021, 132,
                        8.9, "/thumbnails/synthetic-dreams.png", null,
                        "Rémi Fasol, Harry Covert, Anna Conda", Video.VideoType.MOVIE)));
        videos.add(videoRepository
                .save(new Video("The Last Colony", "Humanity's final outpost", "Sci-Fi", 2020, 148,
                        8.4, "/thumbnails/last-colony.png", null,
                        "Matt Rakette, Jessie Kalamar, Jeff Ouille", Video.VideoType.MOVIE)));
        videos.add(videoRepository.save(new Video("Chronos Effect", "Time travel paradox thriller", "Sci-Fi",
                2023, 139,
                8.8, "/thumbnails/chronos-effect.png", "trailer/chronos-effect.mp4",
                "Léon Ardo, José Phine, Élie Ott", Video.VideoType.MOVIE)));

        // Drama Movies
        videos.add(videoRepository
                .save(new Video("Echoes of Silence", "A musician's journey to redemption", "Drama",
                        2023, 124, 8.6, "/thumbnails/echoes-silence.png", null,
                        "Brad Ley, Gaga Lette, Sam Ourai", Video.VideoType.MOVIE)));
        videos.add(videoRepository
                .save(new Video("The Painter's Legacy", "An artist's final masterpiece", "Drama",
                        2022, 118, 8.3, "/thumbnails/painters-legacy.png", null,
                        "Meryl Streep, Tom Hanks, Julia Roberts", Video.VideoType.MOVIE)));
        videos.add(videoRepository
                .save(new Video("Broken Bridges", "Family reunion after decades", "Drama",
                        2021, 112, 7.9, "/thumbnails/broken-bridges.png", null,
                        "Morgan Lefou, Michèle Cane, Robert Dehors", Video.VideoType.MOVIE)));
        videos.add(videoRepository
                .save(new Video("Whispers in the Wind", "Love story across generations", "Drama",
                        2023, 128, 8.1, "/thumbnails/whispers-wind.png", null,
                        "Rachel Macadam, Ryan Gosling, James Garnier", Video.VideoType.MOVIE)));

        // Comedy Movies
        videos.add(videoRepository
                .save(new Video("The Mishap", "Everything that can go wrong, does", "Comedy",
                        2023, 98, 7.5, "/thumbnails/mishap.png", null,
                        "Ben Stiller, Owen Wilson, Will Ferrell", Video.VideoType.MOVIE)));
        videos.add(videoRepository
                .save(new Video("Office Chaos", "Corporate comedy at its finest", "Comedy",
                        2022, 105, 7.8, "/thumbnails/office-chaos.png", null,
                        "Steve Carell, Rainn Wilson, John Krasinski", Video.VideoType.MOVIE)));
        videos.add(videoRepository
                .save(new Video("Wedding Crashers 2.0", "Digital age wedding disasters", "Comedy",
                        2023, 102, 7.2, "/thumbnails/wedding-crashers.png", null,
                        "Vincent Vaugn, Owen Wilson, Isla Fisher", Video.VideoType.MOVIE)));

        // Thriller Movies
        videos.add(videoRepository.save(new Video("The Vanishing", "A detective hunts a serial killer", "Thriller",
                2023, 134, 8.4, "/thumbnails/vanishing.png", null, "Jean Bon, Harry Cover, Violette Lavie",
                Video.VideoType.MOVIE)));
        videos.add(videoRepository.save(new Video("Midnight Protocol", "Hacker uncovers conspiracy", "Thriller", 2022,
                126, 8.1, "/thumbnails/midnight-protocol.png", null,
                "Ramasse Malette, Christiane Laitière, Portia Double", Video.VideoType.MOVIE)));
        videos.add(videoRepository.save(new Video("Silent Witness", "Courtroom psychological thriller", "Thriller",
                2021, 119, 7.9, "/thumbnails/silent-witness.png", null, "Denise Lave, Françoise Mac, Coralie Quins",
                Video.VideoType.MOVIE)));

        // Horror Movies
        videos.add(videoRepository.save(new Video("The Haunting of Blackwood", "Cursed mansion horror", "Horror", 2023,
                108, 7.6, "/thumbnails/blackwood.png", null, "Tony Colette, Alex Loup, Millie Chapeau",
                Video.VideoType.MOVIE)));
        videos.add(videoRepository.save(new Video("Shadows Below", "Underground terror awakens", "Horror", 2022, 95,
                7.3, "/thumbnails/shadows-below.png", null, "Loupita Niongo, Winston Duc, Élisabeth Mousse",
                Video.VideoType.MOVIE)));

        // Series - Drama
        videos.add(videoRepository.save(new Video("Crown of Thorns - Season 1", "Medieval power struggle", "Drama",
                2023, 480, 9.2, "/thumbnails/crown-thorns.png", null, "Olive Colmant, Toby Menzy, Hélène Bonhomme",
                Video.VideoType.SERIES)));
        videos.add(videoRepository.save(new Video("Crown of Thorns - Season 2", "The war continues", "Drama", 2023, 480,
                9.3, "/thumbnails/crown-thorns-s2.png", null, "Olive Colmant, Josse O'Conor, Emma Corinne",
                Video.VideoType.SERIES)));
        videos.add(videoRepository.save(new Video("Silicon Valley Dreams", "Tech startup drama", "Drama", 2022, 400,
                8.5, "/thumbnails/silicon-valley.png", null, "Thomas Milieu, T.J. Meunier, Camille Nanjiani",
                Video.VideoType.SERIES)));
        videos.add(videoRepository.save(new Video("The Diplomat", "International political intrigue", "Drama", 2023,
                420, 8.7, "/thumbnails/diplomat.png", null, "Kévin Russell, Rufin Sewell, David Jasi",
                Video.VideoType.SERIES)));

        // Series - Sci-Fi
        videos.add(videoRepository.save(new Video("Starbound - Season 1", "Interstellar exploration", "Sci-Fi", 2023,
                520, 9.0, "/thumbnails/starbound.png", null, "Pierre Pascal, Belle Ramée, Anne Torve",
                Video.VideoType.SERIES)));
        videos.add(videoRepository.save(new Video("Starbound - Season 2", "New worlds discovered", "Sci-Fi", 2023, 520,
                9.1, "/thumbnails/starbound-s2.png", null, "Pierre Pascal, Belle Ramée, Nicolas Offre",
                Video.VideoType.SERIES)));
        videos.add(videoRepository.save(new Video("The Expanse: Aftermath", "Post-war space opera", "Sci-Fi", 2022, 450,
                8.9, "/thumbnails/expanse-aftermath.png", null, "Étienne Droit, Dominique Tipée, Wesley Chatam",
                Video.VideoType.SERIES)));
        videos.add(videoRepository.save(new Video("Cyberpunk Chronicles", "Dystopian future noir", "Sci-Fi", 2023, 380,
                8.6, "/thumbnails/cyberpunk.png", null, "Kévin Rêve, Carrie-Anne Mousse, Laurent Poisson",
                Video.VideoType.SERIES)));

        // Series - Crime
        videos.add(videoRepository.save(new Video("Detective Noir", "Hard-boiled detective stories", "Crime", 2023, 360,
                8.4, "/thumbnails/detective-noir.png", null, "Bryan Cranston, Aaron Paul, Anna Gunn",
                Video.VideoType.SERIES)));
        videos.add(videoRepository.save(new Video("The Cartel", "Drug empire rise and fall", "Crime", 2022, 480, 8.8,
                "/thumbnails/cartel.png", null, "Wagner Moura, Boyd Holbrook, Pedro Pascal", Video.VideoType.SERIES)));
        videos.add(videoRepository.save(new Video("White Collar Crimes", "Financial fraud investigation", "Crime", 2023,
                340, 8.2, "/thumbnails/white-collar.png", null, "Matt Bomer, Tim DeKay, Willie Garson",
                Video.VideoType.SERIES)));

        // Series - Comedy
        videos.add(videoRepository.save(new Video("The IT Crowd Reboot", "Tech support comedy", "Comedy", 2023, 240,
                8.1, "/thumbnails/it-crowd.png", null, "Chris ODowd, Richard Ayoade, Katherine Parkinson",
                Video.VideoType.SERIES)));
        videos.add(videoRepository.save(new Video("Apartment 42", "Roommate shenanigans", "Comedy", 2022, 220, 7.9,
                "/thumbnails/apartment-42.png", null, "Kaley Cuoco, Jim Parsons, Johnny Galecki",
                Video.VideoType.SERIES)));

        // Documentaries
        videos.add(videoRepository.save(new Video("Planet Earth: The Future", "Climate change documentary",
                "Documentary", 2023, 180, 9.4, "/thumbnails/planet-earth.png", null, "David Attenborough",
                Video.VideoType.DOCUMENTARY)));
        videos.add(videoRepository.save(new Video("The AI Revolution", "Artificial intelligence impact",
                "Documentary", 2023, 95, 8.7, "/thumbnails/ai-revolution.png", null,
                "Sam Altman, Elon Musk, Geoffrey Hinton", Video.VideoType.DOCUMENTARY)));
        videos.add(videoRepository.save(new Video("Ocean Depths", "Deep sea exploration", "Documentary", 2022, 120, 9.0,
                "/thumbnails/ocean-depths.png", null, "James Cameron", Video.VideoType.DOCUMENTARY)));
        videos.add(videoRepository.save(new Video("Ancient Civilizations", "Lost cities uncovered",
                "Documentary", 2023, 240, 8.9, "/thumbnails/ancient-civs.png", null, "Graham Hancock",
                Video.VideoType.DOCUMENTARY)));
        videos.add(videoRepository.save(new Video("The Space Race 2.0", "Modern space exploration", "Documentary", 2023,
                110, 8.8, "/thumbnails/space-race.png", null, "Neil deGrasse Tyson", Video.VideoType.DOCUMENTARY)));
        videos.add(videoRepository.save(new Video("Wildlife Warriors", "Conservation heroes", "Documentary",
                2022, 85, 8.5, "/thumbnails/wildlife-warriors.png", null, "Steve Irwin Family",
                Video.VideoType.DOCUMENTARY)));
        videos.add(videoRepository.save(new Video("The Crypto Phenomenon", "Blockchain revolution", "Documentary", 2023,
                102, 8.3, "/thumbnails/crypto.png", null, "Vitalik Buterin", Video.VideoType.DOCUMENTARY)));
        videos.add(videoRepository.save(new Video("Mind Matters", "Neuroscience breakthroughs", "Documentary", 2023, 95,
                8.6, "/thumbnails/mind-matters.png", null, "Andrew Huberman", Video.VideoType.DOCUMENTARY)));

        // Romance
        videos.add(videoRepository.save(new Video("Love in Paris", "Romantic comedy in France", "Romance", 2023, 108,
                7.8, "/thumbnails/love-paris.png", null, "Lily Collins, Lucas Bravo, Camille Razat",
                Video.VideoType.MOVIE)));
        videos.add(videoRepository.save(new Video("Second Chances", "Finding love after loss", "Romance", 2022, 115,
                8.0, "/thumbnails/second-chances.png", null, "Julia Roberts, George Clooney", Video.VideoType.MOVIE)));
        videos.add(videoRepository.save(new Video("The Letter", "Long-distance love story", "Romance", 2023, 102, 7.6,
                "/thumbnails/letter.png", null, "Amanda Seyfried, Channing Tatum", Video.VideoType.MOVIE)));

        // Fantasy
        videos.add(videoRepository.save(new Video("Realm of Dragons", "Epic fantasy adventure", "Fantasy", 2023, 165,
                8.9, "/thumbnails/realm-dragons.png", null, "Emilia Clarke, Kit Harington, Peter Dinklage",
                Video.VideoType.MOVIE)));
        videos.add(videoRepository.save(new Video("The Sorcerer's Apprentice Returns", "Magic school adventures",
                "Fantasy", 2022, 138, 8.2, "/thumbnails/sorcerer.png", null,
                "Daniel Radcliffe, Emma Watson, Rupert Grint", Video.VideoType.MOVIE)));
        videos.add(videoRepository.save(new Video("Legends of Avalon", "Arthurian legend reimagined", "Fantasy", 2023,
                520, 9.0, "/thumbnails/avalon.png", null, "Charlie Hunnam, Jude Law, Astrid Bergès-Frisbey",
                Video.VideoType.SERIES)));

        System.out.println("✅ Loaded " + videos.size() + " videos");
        return videos;
    }

    private List<User> loadUsers() {
        List<User> users = new ArrayList<>();

        users.add(userRepository
                .save(new User("john.doe@email.com", "JohnD", User.SubscriptionType.PREMIUM, "USA")));
        users.add(
                userRepository.save(new User("sarah.smith@email.com", "SarahS",
                        User.SubscriptionType.STANDARD, "UK")));
        users.add(
                userRepository.save(new User("mike.wilson@email.com", "MikeW",
                        User.SubscriptionType.BASIC, "Canada")));
        users.add(userRepository
                .save(new User("emma.brown@email.com", "EmmaB", User.SubscriptionType.PREMIUM,
                        "Australia")));
        users.add(userRepository
                .save(new User("alex.garcia@email.com", "AlexG", User.SubscriptionType.STANDARD,
                        "Spain")));
        users.add(userRepository
                .save(new User("lisa.martin@email.com", "LisaM", User.SubscriptionType.PREMIUM,
                        "France")));
        users.add(userRepository
                .save(new User("david.lee@email.com", "DavidL", User.SubscriptionType.BASIC,
                        "South Korea")));
        users.add(userRepository
                .save(new User("maria.rodriguez@email.com", "MariaR", User.SubscriptionType.STANDARD,
                        "Mexico")));
        users.add(userRepository
                .save(new User("james.taylor@email.com", "JamesT", User.SubscriptionType.PREMIUM,
                        "USA")));
        users.add(userRepository
                .save(new User("anna.mueller@email.com", "AnnaM", User.SubscriptionType.BASIC,
                        "Germany")));

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

                watchHistoryRepository
                        .save(new WatchHistory(user.getId(), randomVideo.getId(), progress));

                // Increment view count
                randomVideo.setViewCount(randomVideo.getViewCount() + 1);
                videoRepository.save(randomVideo);

                historyCount++;
            }
        }

        System.out.println("✅ Loaded " + historyCount + " watch history entries");
    }
}

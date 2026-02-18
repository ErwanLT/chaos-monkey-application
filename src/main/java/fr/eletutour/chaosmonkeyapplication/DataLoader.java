package fr.eletutour.chaosmonkeyapplication;

import fr.eletutour.chaosmonkeyapplication.configurations.UIConfiguration;
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
        private final UIConfiguration uiConfiguration;

        public DataLoader(VideoRepository videoRepository,
                        UserRepository userRepository,
                        WatchHistoryRepository watchHistoryRepository,
                        RecommendationService recommendationService,
                        UIConfiguration uiConfiguration) {
                this.videoRepository = videoRepository;
                this.userRepository = userRepository;
                this.watchHistoryRepository = watchHistoryRepository;
                this.recommendationService = recommendationService;
                this.uiConfiguration = uiConfiguration;
        }

        @Override
        public void run(String... args) throws Exception {
                System.out.println("🎬 Loading streaming data...");

                List<Video> videos;
                if ("v2".equals(uiConfiguration.getUiVersion())) {
                        System.out.println(" DISNEY+ MODE: Loading Disney/Tech themed data");
                        videos = loadDisneyTechVideos();
                } else {
                        System.out.println(" NETFLIX MODE: Loading Netflix-like streaming data");
                        videos = loadVideos();
                }

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

        private List<Video> loadDisneyTechVideos() {
                List<Video> videos = new ArrayList<>();

                videos.add(videoRepository.save(new Video("1001 Bugs",
                                "Une colonie de scripts lutte contre un gang de malwares extorqueurs.",
                                "Animation", 2023, 95, 8.1, "/thumbnails/disney/1001-bugs.png", null,
                                "Flik Flak, Princess Atta-octet, Hopper.exe", "Disney", Video.VideoType.MOVIE)));
                videos.add(videoRepository.save(new Video("Garbage Collector",
                                "Dans un futur lointain, un robot de garbage collection de données solitaire trouve un sens à sa vie.",
                                "Animation", 2008, 98, 9.3, "/thumbnails/disney/garbage-collector.png", null,
                                "WALL-E, EVE-lyn, AUTO-pilot", "Pixar", Video.VideoType.MOVIE)));
                videos.add(videoRepository.save(new Video("Finding `nemo.dat`",
                                "Un parent parcourt l'océan de données pour retrouver son fichier fils perdu.",
                                "Animation", 2003, 100, 8.9, "/thumbnails/disney/nemo.png", null,
                                "Marlin.sh, Dory.mem, Nemo.dat", "Pixar", Video.VideoType.MOVIE)));
                videos.add(videoRepository.save(new Video("The Incredibles: Super-Coders",
                                "Une famille de programmeurs aux talents extraordinaires est forcée de cacher ses compétences.",
                                "Animation", 2004, 115, 8.7, "/thumbnails/disney/sup-coders.png", null,
                                "Mr. Compilable, Elastigirl-IDE, Dash.sh", "Pixar", Video.VideoType.MOVIE)));
                videos.add(videoRepository.save(new Video("Codatouille",
                                "Un jeune script d'IA aspire à devenir un 'chef' développeur dans un grand restaurant de code parisien.",
                                "Animation", 2007, 111, 8.5, "/thumbnails/disney/codatouille.png", null,
                                "Rémy.py, Alfredo Linguini, Chef Skinner", "Pixar", Video.VideoType.MOVIE)));
                videos.add(videoRepository.save(new Video("Cloud Up",
                                "Un retraité virtualise sa maison dans le cloud pour échapper à la gentrification numérique.",
                                "Animation", 2009, 96, 8.6, "/thumbnails/disney/cloud-up.png", null,
                                "Carl Fredricksen, Russell.zip, Dug.cloud", "Pixar", Video.VideoType.MOVIE)));
                videos.add(videoRepository.save(new Video("Frontend and the Backend",
                                "Une développeuse front-end tombe amoureuse d'un monstrueux mais puissant système backend legacy.",
                                "Romance", 2017, 129, 8.0, "/thumbnails/disney/front-back.png", null,
                                "Belle.js, The Beast (Legacy COBOL), Gaston.IO", "Disney", Video.VideoType.MOVIE)));
                videos.add(videoRepository.save(new Video("Mulan: The JIT Compilation",
                                "Un script Python se fait passer pour un binaire C++ pour rejoindre l'armée des processus haute performance.",
                                "Action", 1998, 88, 8.1, "/thumbnails/disney/mulan.png", null,
                                "Fa Mulan.py, Li Shang.dll, Mushu (the debugger)", "Disney", Video.VideoType.MOVIE)));
                videos.add(videoRepository.save(new Video("Inside-System",
                                "Les microservices personnifiés gèrent la stabilité émotionnelle d'un jeune système d'exploitation.",
                                "Animation", 2015, 360, 8.8, "/thumbnails/disney/inside-system.png", null,
                                "Joy.dll, Sadness.log, Anger.err", "Pixar", Video.VideoType.MOVIE)));
                videos.add(videoRepository.save(new Video("The Code King",
                                "Le cycle de vie du développement logiciel, raconté à travers la savane des serveurs.",
                                "Animation", 1994, 420, 9.0, "/thumbnails/disney/code-king.png", null,
                                "Simba.sh, Mufasa.exe, Scar.vbs", "Disney", Video.VideoType.MOVIE)));
                videos.add(videoRepository.save(new Video("Tarzan: Raised by COBOL",
                                "Un jeune programmeur élevé par des systèmes COBOL doit s'adapter au monde moderne des API REST.",
                                "Drama", 1999, 380, 7.9, "/thumbnails/disney/tarzan.png", null,
                                "Tarzan, Jane Porter (API Specialist), Clayton.dll (deprecated)", "Disney",
                                Video.VideoType.MOVIE)));
                videos.add(videoRepository.save(new Video("Le projet hanté et les 999 bugs",
                                "Un manoir de code anciens abrite 999 bugs fantomatique",
                                "Drama", 2003, 180, 8.0, "/thumbnails/disney/manor.png", null,
                                "Eddy Murphy, plein de CVE", "Disney", Video.VideoType.MOVIE)));
                videos.add(videoRepository.save(new Video("Story: The OS Update",
                                "Des jouets dotés d'une IA avancée craignent d'être remplacés par un nouveau modèle plus performant.",
                                "Sci-Fi", 1995, 81, 9.2, "/thumbnails/disney/story.png", null,
                                "Woody.ROM, Buzz Lightyear 2.0, Mr. Potato Headless", "Pixar", Video.VideoType.MOVIE)));
                videos.add(videoRepository.save(new Video("Doata: The Packet Journey",
                                "Une jeune 'requête' quitte son île (serveur local) pour traverser le grand océan (Internet) et restaurer le cœur du réseau.",
                                "Adventure", 2016, 107, 8.8, "/thumbnails/disney/doata.png", null,
                                "Moana.request, Maui (the demigod proxy), Te Fiti (the root server)", "Disney",
                                Video.VideoType.MOVIE)));
                videos.add(videoRepository.save(new Video("Termin-aladdin",
                                "Un jeune homme des rues trouve un terminal magique contenant un 'génie' capable d'exécuter n'importe quelle commande sudo.",
                                "Fantasy", 1992, 90, 8.4, "/thumbnails/disney/terminaladin.png", null,
                                "Aladdin, Génie.sh, Jafar.root", "Disney", Video.VideoType.MOVIE)));
                videos.add(videoRepository.save(new Video("Frozen System",
                                "Une reine crashe accidentellement son royaume avec un 'kernel panic' hivernal persistant.",
                                "Fantasy", 2013, 102, 8.2, "/thumbnails/disney/frozen.png", null,
                                "Elsa.sys, Anna.exe, Olaf.tmp", "Disney", Video.VideoType.MOVIE)));
                videos.add(videoRepository.save(new Video("Hercules CPU",
                                "Le fils de Zeus, un CPU légendaire, doit accomplir 12 travaux (benchmarks) pour prouver sa valeur sur le mont Olympe des serveurs.",
                                "Fantasy", 1997, 93, 8.3, "/thumbnails/disney/hercules.png", null,
                                "Hercules, Philoctetes (The Compiler), Hades (The Overheater)", "Disney",
                                Video.VideoType.MOVIE)));
                videos.add(videoRepository.save(new Video("Cinder-thread-a",
                                "Un thread de basse priorité, aidé par une fée (le Scheduler), obtient une chance de s'exécuter au bal du CPU.",
                                "Romance", 1950, 74, 7.9, "/thumbnails/disney/cinderthreada.png", null,
                                "LowPriority.thread, Scheduler.guide, CPU.palace, Priority.gown, NiceValue.stars, Execution.sparkles, TimeCycle.clock, Debug.helpers, Kernel.magic", "Disney",
                                Video.VideoType.MOVIE)));

                videos.add(videoRepository.save(new Video("Beskar Protocol",
                                "Un chasseur de primes solitaire parcourt une galaxie de micro-services en ruine, protégeant une précieuse instance legacy convoitée par un Empire de systèmes distribués.",
                                "Sci-Fi", 2019, 666, 9.9, "/thumbnails/disney/beskar.png", null,
                                "Din.bat, Grogu.sh, Bo-Katan.yml, MoffGideon.exe, Armorer.conf, IG-11.service, Kuiil.init, CaraDune.sys, GreefKarga.api",
                                "Star Wars", Video.VideoType.SERIES)));
                videos.add(videoRepository.save(new Video("ClusterVision",
                                "Une ingénieure DevOps altère la réalité de son cluster Kubernetes pour recréer un environnement stable où aucun incident n’existe… jusqu’à ce que les métriques commencent à mentir.",
                                "Sci-Fi", 2021, 888, 9.8, "/thumbnails/disney/clustervision.png", null,
                                "Wanda.k8s, Vision.pod, Agatha.debug, Pietro.reload, VisionReplicaSet, Hex.yaml, Westview.namespace, MetricLiar.service, ScarletWitch.configMap, Quicksilver.deployment",
                                "Marvel", Video.VideoType.SERIES)));
                videos.add(videoRepository.save(new Video("The Book of Backups",
                                "Un ancien chasseur d’incidents tente de régner sur le territoire oublié des sauvegardes jamais testées.",
                                "Sci-Fi", 2021, 555, 8.7, "/thumbnails/disney/bookofbackups.png", null,
                                "BobaFett.incident, Fennec.restore, BibFortuna.backup, Jabba.tape, Sarlacc.corrupt, Gamorrean.sysadmin, Tatooine.archive, Mandalorian.legacy, Tusken.scavenger, PeliMotto.droid",
                                "Star Wars", Video.VideoType.SERIES)));
                videos.add(videoRepository.save(new Video("Merge Knight",
                                "Un développeur possède plusieurs personnalités Git : feature branch héroïque, hotfix nocturne, commit sauvage à 3h du matin. Il ne sait plus qui a déployé en prod.",
                                "Psychological Thriller", 2022, 444, 9.1, "/thumbnails/disney/mergeknight.png", null,
                                "Marc.feature, Steven.hotfix, Jake.commit3am, Khonshu.mergeConflict, Layla.branch, Taweret.rollback, ArthurHarrow.deploy, Ammit.audit, Gus.fishScript, StevenGrant.config",
                                "Marvel", Video.VideoType.SERIES)));

                // National Geographic
                videos.add(videoRepository.save(new Video("The World According to Jeff Codeblum",
                                "Jeff Codeblum explore les structures de données cachées du quotidien.",
                                "Documentary", 2019, 30, 7.8, "/thumbnails/disney/jeff-goldblum.png", null,
                                "Jeff Codeblum", "National Geographic", Video.VideoType.SERIES)));
                videos.add(videoRepository.save(new Video("Limitless Scaling with Chris Hemsworth",
                                "Chris Hemsworth teste les limites de l'élasticité du cloud sous charge extrême.",
                                "Documentary", 2022, 45, 8.2, "/thumbnails/disney/limitless.png", null,
                                "Chris Hemsworth", "National Geographic", Video.VideoType.SERIES)));
                videos.add(videoRepository.save(new Video("Welcome to Localhost",
                                "Will Smith explore les environnements de dev isolés et non documentés.",
                                "Documentary", 2021, 40, 8.0, "/thumbnails/disney/welcome-earth.png", null,
                                "Will Smith", "National Geographic", Video.VideoType.SERIES)));

                System.out.println("✅ Loaded " + videos.size() + " Disney/Tech videos");
                return videos;
        }

        private List<Video> loadVideos() {
                List<Video> videos = new ArrayList<>();

                // Action Movies
                videos.add(videoRepository.save(new Video("The Dark Sentinel",
                                "Un justicier combat le crime dans une ville dystopique",
                                "Action", 2023, 142, 8.5, "/thumbnails/dark-sentinel.png", "/trailer/dark-sentinel.mp4",
                                "Jean Benbois, Archibald Aquin, Sandra Nicouette", Video.VideoType.MOVIE)));
                videos.add(videoRepository
                                .save(new Video("Velocity",
                                                "Course-poursuite à grande vitesse à travers les continents", "Action",
                                                2022, 118,
                                                7.8, "/thumbnails/velocity.png", null,
                                                "Élie Vafranco, Jean Trenscène, Laure Aison", Video.VideoType.MOVIE)));
                videos.add(videoRepository.save(new Video("Iron Protocol", "Soldats d'élite en mission dangereuse",
                                "Action",
                                2021, 135, 8.2, "/thumbnails/iron-protocol.png", null,
                                "Alex Terrieur, Sarah Pelle, Bob Leponge", Video.VideoType.MOVIE)));
                videos.add(videoRepository
                                .save(new Video("Rogue Agent", "Un espion disparaît des radars", "Action", 2023, 128,
                                                7.9,
                                                "/thumbnails/rogue-agent.png", null,
                                                "Guy Tare, Henri Cochet, Bella Maman", Video.VideoType.MOVIE)));

                // Sci-Fi Movies
                videos.add(videoRepository.save(
                                new Video("Quantum Horizon", "Des scientifiques découvrent des univers parallèles",
                                                "Sci-Fi",
                                                2023, 156, 9.1, "/thumbnails/quantum-horizon.png", null,
                                                "Marc Assin, Anne Onyme, Ella Bora", Video.VideoType.MOVIE)));
                videos.add(videoRepository
                                .save(new Video("Nebula Station", "La vie sur une station spatiale lointaine", "Sci-Fi",
                                                2022,
                                                145, 8.7, "/thumbnails/nebula-station.png", null,
                                                "Sam Suffit, Zoé Taure, Céline Dion", Video.VideoType.MOVIE)));
                videos.add(videoRepository
                                .save(new Video("Synthetic Dreams", "L'IA atteint la conscience", "Sci-Fi", 2021, 132,
                                                8.9, "/thumbnails/synthetic-dreams.png", null,
                                                "Rémi Fasol, Harry Covert, Anna Conda", Video.VideoType.MOVIE)));
                videos.add(videoRepository
                                .save(new Video("The Last Colony", "Le dernier avant-poste de l'humanité", "Sci-Fi",
                                                2020, 148,
                                                8.4, "/thumbnails/last-colony.png", null,
                                                "Matt Rakette, Jessie Kalamar, Jeff Ouille", Video.VideoType.MOVIE)));
                videos.add(videoRepository.save(new Video("Chronos Effect",
                                "Thriller sur le paradoxe du voyage dans le temps", "Sci-Fi",
                                2023, 139,
                                8.8, "/thumbnails/chronos-effect.png", "trailer/chronos-effect.mp4",
                                "Léon Ardo, José Phine, Élie Ott", Video.VideoType.MOVIE)));

                // Drama Movies
                videos.add(videoRepository
                                .save(new Video("Echoes of Silence", "Le voyage d'un musicien vers la rédemption",
                                                "Drama",
                                                2023, 124, 8.6, "/thumbnails/echoes-silence.png", null,
                                                "Brad Ley, Gaga Lette, Sam Ourai", Video.VideoType.MOVIE)));
                videos.add(videoRepository
                                .save(new Video("The Painter's Legacy", "Le chef-d'œuvre final d'un artiste", "Drama",
                                                2022, 118, 8.3, "/thumbnails/painters-legacy.png", null,
                                                "Meryl Streep, Tom Hanks, Julia Roberts", Video.VideoType.MOVIE)));
                videos.add(videoRepository
                                .save(new Video("Broken Bridges", "Réunion de famille après des décennies", "Drama",
                                                2021, 112, 7.9, "/thumbnails/broken-bridges.png", null,
                                                "Morgan Lefou, Michèle Cane, Robert Dehors", Video.VideoType.MOVIE)));
                videos.add(videoRepository
                                .save(new Video("Whispers in the Wind", "Histoire d'amour à travers les générations",
                                                "Drama",
                                                2023, 128, 8.1, "/thumbnails/whispers-wind.png", null,
                                                "Rachel Macadam, Ryan Gosling, James Garnier", Video.VideoType.MOVIE)));

                // Comedy Movies
                videos.add(videoRepository
                                .save(new Video("The Mishap", "Tout ce qui peut mal tourner, tourne mal", "Comedy",
                                                2023, 98, 7.5, "/thumbnails/mishap.png", null,
                                                "Ben Stiller, Owen Wilson, Will Ferrell", Video.VideoType.MOVIE)));
                videos.add(videoRepository
                                .save(new Video("Office Chaos", "La comédie d'entreprise à son meilleur", "Comedy",
                                                2022, 105, 7.8, "/thumbnails/office-chaos.png", null,
                                                "Steve Carell, Rainn Wilson, John Krasinski", Video.VideoType.MOVIE)));
                videos.add(videoRepository
                                .save(new Video("Wedding Crashers 2.0", "Catastrophes de mariage à l'ère numérique",
                                                "Comedy",
                                                2023, 102, 7.2, "/thumbnails/wedding-crashers.png", null,
                                                "Vincent Vaugn, Owen Wilson, Isla Fisher", Video.VideoType.MOVIE)));

                // Thriller Movies
                videos.add(videoRepository
                                .save(new Video("The Vanishing", "Un détective traque un tueur en série", "Thriller",
                                                2023, 134, 8.4, "/thumbnails/vanishing.png", null,
                                                "Jean Bon, Harry Cover, Violette Lavie",
                                                Video.VideoType.MOVIE)));
                videos.add(videoRepository.save(new Video("Midnight Protocol", "Un hacker découvre une conspiration",
                                "Thriller", 2022,
                                126, 8.1, "/thumbnails/midnight-protocol.png", null,
                                "Ramasse Malette, Christiane Laitière, Portia Double", Video.VideoType.MOVIE)));
                videos.add(videoRepository.save(
                                new Video("Silent Witness", "Thriller psychologique en salle d'audience", "Thriller",
                                                2021, 119, 7.9, "/thumbnails/silent-witness.png", null,
                                                "Denise Lave, Françoise Mac, Coralie Quins",
                                                Video.VideoType.MOVIE)));

                // Horror Movies
                videos.add(videoRepository.save(new Video("The Haunting of Blackwood", "Horreur dans un manoir maudit",
                                "Horror", 2023,
                                108, 7.6, "/thumbnails/blackwood.png", null, "Tony Colette, Alex Loup, Millie Chapeau",
                                Video.VideoType.MOVIE)));
                videos.add(videoRepository.save(
                                new Video("Shadows Below", "La terreur souterraine se réveille", "Horror", 2022, 95,
                                                7.3, "/thumbnails/shadows-below.png", null,
                                                "Loupita Niongo, Winston Duc, Élisabeth Mousse",
                                                Video.VideoType.MOVIE)));

                // Series - Drama
                videos.add(videoRepository
                                .save(new Video("Crown of Thorns - Season 1", "Lutte de pouvoir médiévale", "Drama",
                                                2023, 480, 9.2, "/thumbnails/crown-thorns.png", null,
                                                "Olive Colmant, Toby Menzy, Hélène Bonhomme",
                                                Video.VideoType.SERIES)));
                videos.add(videoRepository
                                .save(new Video("Crown of Thorns - Season 2", "La guerre continue", "Drama", 2023, 480,
                                                9.3, "/thumbnails/crown-thorns-s2.png", null,
                                                "Olive Colmant, Josse O'Conor, Emma Corinne",
                                                Video.VideoType.SERIES)));
                videos.add(videoRepository.save(
                                new Video("Silicon Valley Dreams", "Drame de startup technologique", "Drama", 2022, 400,
                                                8.5, "/thumbnails/silicon-valley.png", null,
                                                "Thomas Milieu, T.J. Meunier, Camille Nanjiani",
                                                Video.VideoType.SERIES)));
                videos.add(videoRepository.save(new Video("The Diplomat", "Intrigue politique internationale", "Drama",
                                2023,
                                420, 8.7, "/thumbnails/diplomat.png", null, "Kévin Russell, Rufin Sewell, David Jasi",
                                Video.VideoType.SERIES)));

                // Series - Sci-Fi
                videos.add(videoRepository.save(new Video("Starbound - Season 1", "Exploration interstellaire",
                                "Sci-Fi", 2023,
                                520, 9.0, "/thumbnails/starbound.png", null, "Pierre Pascal, Belle Ramée, Anne Torve",
                                Video.VideoType.SERIES)));
                videos.add(videoRepository.save(new Video("Starbound - Season 2", "Nouveaux mondes découverts",
                                "Sci-Fi", 2023, 520,
                                9.1, "/thumbnails/starbound-s2.png", null, "Pierre Pascal, Belle Ramée, Nicolas Offre",
                                Video.VideoType.SERIES)));
                videos.add(videoRepository.save(
                                new Video("The Expanse: Aftermath", "Opéra spatial post-guerre", "Sci-Fi", 2022, 450,
                                                8.9, "/thumbnails/expanse-aftermath.png", null,
                                                "Étienne Droit, Dominique Tipée, Wesley Chatam",
                                                Video.VideoType.SERIES)));
                videos.add(videoRepository
                                .save(new Video("Cyberpunk Chronicles", "Futur dystopique noir", "Sci-Fi", 2023, 380,
                                                8.6, "/thumbnails/cyberpunk.png", null,
                                                "Kévin Rêve, Carrie-Anne Mousse, Laurent Poisson",
                                                Video.VideoType.SERIES)));

                // Series - Crime
                videos.add(videoRepository.save(new Video("Detective Noir", "Histoires de détectives à l'ancienne",
                                "Crime", 2023, 360,
                                8.4, "/thumbnails/detective-noir.png", null, "Bryan Cranston, Aaron Paul, Anna Gunn",
                                Video.VideoType.SERIES)));
                videos.add(videoRepository.save(new Video("The Cartel", "Ascension et chute d'un empire de la drogue",
                                "Crime", 2022, 480, 8.8,
                                "/thumbnails/cartel.png", null, "Wagner Moura, Boyd Holbrook, Pedro Pascal",
                                Video.VideoType.SERIES)));
                videos.add(videoRepository.save(new Video("White Collar Crimes", "Enquête sur la fraude financière",
                                "Crime", 2023,
                                340, 8.2, "/thumbnails/white-collar.png", null, "Matt Bomer, Tim DeKay, Willie Garson",
                                Video.VideoType.SERIES)));

                // Series - Comedy
                videos.add(videoRepository.save(
                                new Video("The IT Crowd Reboot", "Comédie de support technique", "Comedy", 2023, 240,
                                                8.1, "/thumbnails/it-crowd.png", null,
                                                "Chris ODowd, Richard Ayoade, Katherine Parkinson",
                                                Video.VideoType.SERIES)));
                videos.add(videoRepository.save(new Video("Apartment 42", "Mésaventures de colocataires", "Comedy",
                                2022, 220, 7.9,
                                "/thumbnails/apartment-42.png", null, "Kaley Cuoco, Jim Parsons, Johnny Galecki",
                                Video.VideoType.SERIES)));

                // Documentaries
                videos.add(videoRepository
                                .save(new Video("Planet Earth: The Future", "Documentaire sur le changement climatique",
                                                "Documentary", 2023, 180, 9.4, "/thumbnails/planet-earth.png", null,
                                                "David Attenborough",
                                                Video.VideoType.DOCUMENTARY)));
                videos.add(videoRepository.save(new Video("The AI Revolution",
                                "L'impact de l'intelligence artificielle",
                                "Documentary", 2023, 95, 8.7, "/thumbnails/ai-revolution.png", null,
                                "Sam Altman, Elon Musk, Geoffrey Hinton", Video.VideoType.DOCUMENTARY)));
                videos.add(videoRepository.save(new Video("Ocean Depths", "Exploration des grands fonds marins",
                                "Documentary", 2022, 120, 9.0,
                                "/thumbnails/ocean-depths.png", null, "James Cameron", Video.VideoType.DOCUMENTARY)));
                videos.add(videoRepository.save(new Video("Ancient Civilizations", "Villes perdues découvertes",
                                "Documentary", 2023, 240, 8.9, "/thumbnails/ancient-civs.png", null, "Graham Hancock",
                                Video.VideoType.DOCUMENTARY)));
                videos.add(videoRepository.save(
                                new Video("The Space Race 2.0", "Exploration spatiale moderne", "Documentary", 2023,
                                                110, 8.8, "/thumbnails/space-race.png", null, "Neil deGrasse Tyson",
                                                Video.VideoType.DOCUMENTARY)));
                videos.add(videoRepository.save(new Video("Wildlife Warriors", "Héros de la conservation",
                                "Documentary",
                                2022, 85, 8.5, "/thumbnails/wildlife-warriors.png", null, "Steve Irwin Family",
                                Video.VideoType.DOCUMENTARY)));
                videos.add(videoRepository.save(new Video("The Crypto Phenomenon", "La révolution de la blockchain",
                                "Documentary", 2023,
                                102, 8.3, "/thumbnails/crypto.png", null, "Vitalik Buterin",
                                Video.VideoType.DOCUMENTARY)));
                videos.add(videoRepository
                                .save(new Video("Mind Matters", "Avancées en neurosciences", "Documentary", 2023, 95,
                                                8.6, "/thumbnails/mind-matters.png", null, "Andrew Huberman",
                                                Video.VideoType.DOCUMENTARY)));

                // Romance
                videos.add(videoRepository.save(new Video("Love in Paris", "Comédie romantique en France", "Romance",
                                2023, 108,
                                7.8, "/thumbnails/love-paris.png", null, "Lily Collins, Lucas Bravo, Camille Razat",
                                Video.VideoType.MOVIE)));
                videos.add(videoRepository.save(
                                new Video("Second Chances", "Trouver l'amour après la perte", "Romance", 2022, 115,
                                                8.0, "/thumbnails/second-chances.png", null,
                                                "Julia Roberts, George Clooney", Video.VideoType.MOVIE)));
                videos.add(videoRepository
                                .save(new Video("The Letter", "Histoire d'amour à distance", "Romance", 2023, 102, 7.6,
                                                "/thumbnails/letter.png", null, "Amanda Seyfried, Channing Tatum",
                                                Video.VideoType.MOVIE)));

                // Fantasy
                videos.add(videoRepository
                                .save(new Video("Realm of Dragons", "Aventure fantastique épique", "Fantasy", 2023, 165,
                                                8.9, "/thumbnails/realm-dragons.png", null,
                                                "Emilia Clarke, Kit Harington, Peter Dinklage",
                                                Video.VideoType.MOVIE)));
                videos.add(videoRepository.save(
                                new Video("The Sorcerer's Apprentice Returns", "Aventures dans une école de magie",
                                                "Fantasy", 2022, 138, 8.2, "/thumbnails/sorcerer.png", null,
                                                "Daniel Radcliffe, Emma Watson, Rupert Grint", Video.VideoType.MOVIE)));
                videos.add(videoRepository.save(
                                new Video("Legends of Avalon", "La légende arthurienne réinventée", "Fantasy", 2023,
                                                520, 9.0, "/thumbnails/avalon.png", null,
                                                "Charlie Hunnam, Jude Law, Astrid Bergès-Frisbey",
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

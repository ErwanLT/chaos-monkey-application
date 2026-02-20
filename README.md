# 🎬 Chaos+ Application

Une application de streaming vidéo inspirée de **Disney+**, intégrant **Chaos Monkey** pour démontrer les principes du Chaos Engineering.

## 📋 Description

Cette application Spring Boot simule un service de streaming vidéo avec une interface utilisateur inspirée de Disney+. Elle intègre **Chaos Monkey for Spring Boot** pour injecter des pannes contrôlées et tester la résilience de l'application.

L'application propose deux interfaces :
- **v1** : Interface inspirée de Netflix (Chaosflix)
- **v2** : Interface inspirée de Disney+ (Chaos+), avec hero section, carousels par marque et pages d'erreur dédiées

### Fonctionnalités principales

- 📺 **Catalogue de vidéos** : Gestion d'un catalogue de plus de 50 vidéos
- 🎨 **Double interface UI** : Choix entre le thème Netflix (v1) et Disney+ (v2)
- 🏷️ **Sections par marque** : Disney, Pixar, Marvel, Star Wars, National Geographic
- 🔍 **Recherche** : Recherche de vidéos par titre
- 👥 **Gestion des utilisateurs** : Système d'utilisateurs avec profils
- 🎯 **Recommandations** : Système de recommandations basé sur l'historique
- ▶️ **Streaming** : Simulation de streaming avec gestion de la qualité
- 📊 **Historique de visionnage** : Suivi de la progression et des vidéos complétées
- 🐵 **Chaos Engineering** : Injection de latence et d'exceptions via Chaos Monkey
- ⚠️ **Pages d'erreur thématiques** : Pages d'erreur dédiées pour chaque UI (v1/v2)
- 📚 **Documentation API** : Swagger UI avec informations complètes (contact, licence, serveur)

## 🛠️ Technologies utilisées

| Technologie                  | Version |
|------------------------------|---------|
| Java                         | 21      |
| Spring Boot                  | 4.0.2   |
| Spring Data JPA              | —       |
| H2 Database                  | —       |
| Chaos Monkey for Spring Boot | 4.0.0   |
| Spring Boot Actuator         | —       |
| SpringDoc OpenAPI            | 3.0.1   |
| Thymeleaf                    | —       |
| Bootstrap                    | 5.3     |
| Maven                        | 3.6+    |
| Resilience4j                 | 2.3.0   |

## 🚀 Installation et démarrage

### Prérequis

- Java 21
- Maven 3.6+

### Démarrage de l'application

```bash
# Cloner le projet
git clone <repository-url>
cd chaos-monkey-application

# Compiler et lancer l'application
./mvnw spring-boot:run
```

L'application démarre sur `http://localhost:8080`

## 🎨 Configuration de l'interface

L'interface active est contrôlée par la propriété `app.ui` dans `application.properties` :

```properties
# Interface Disney+ (v2) — par défaut
app.ui=v2

# Interface Netflix (v1)
app.ui=v1
```

| Valeur | Interface | Thème                                |
|--------|-----------|--------------------------------------|
| `v1`   | Chaosflix | Inspiré Netflix (fond noir, rouge)   |
| `v2`   | Chaos+    | Inspiré Disney+ (fond #040714, bleu) |

## 📡 API Endpoints

### Interface Web (MVC)

| Méthode | Endpoint               | Description                           |
|---------|------------------------|---------------------------------------|
| GET     | `/`                    | Page d'accueil                        |
| GET     | `/movies`              | Films uniquement                      |
| GET     | `/series`              | Séries uniquement                     |
| GET     | `/new-popular`         | Nouveautés & tendances                |
| GET     | `/my-list`             | Ma liste                              |
| GET     | `/search?q={query}`    | Recherche de vidéos                   |
| GET     | `/section?name={name}` | Contenu par section (Disney, Marvel…) |

> [!NOTE]
> Les endpoints MVC retournent une page d'erreur thématique (v1 ou v2) si aucun résultat n'est trouvé.

### Catalogue (REST)

- `GET /api/catalog/videos` — Liste toutes les vidéos
- `GET /api/catalog/videos/{id}` — Détails d'une vidéo
- `GET /api/catalog/genres` — Liste des genres disponibles
- `GET /api/catalog/videos/genre/{genre}` — Vidéos par genre
- `GET /api/catalog/videos/search?title={title}` — Recherche de vidéos

### Utilisateurs (REST)

- `GET /api/users` — Liste des utilisateurs
- `GET /api/users/{id}` — Détails d'un utilisateur
- `POST /api/users` — Créer un utilisateur

### Streaming (REST)

- `POST /api/streaming/start` — Démarrer un streaming
- `POST /api/streaming/progress` — Mettre à jour la progression
- `GET /api/streaming/history/{userId}` — Historique de visionnage
- `GET /api/streaming/completed/{userId}` — Vidéos complétées
- `GET /api/streaming/quality?networkSpeed={speed}` — Qualité de streaming

### Recommandations (REST)

- `GET /api/recommendations/{userId}` — Recommandations personnalisées
- `GET /api/recommendations/trending` — Vidéos tendances
- `GET /api/recommendations/popular` — Vidéos populaires

## ⚠️ Gestion des erreurs

L'application dispose d'un `GlobalExceptionHandler` qui intercepte toutes les exceptions métier et redirige vers des pages d'erreur adaptées à l'interface active.

| Exception                 | Page d'erreur               |
|---------------------------|-----------------------------|
| `CatalogException`        | `catalog-error.html`        |
| `UserException`           | `user-error.html`           |
| `StreamingException`      | `streaming-error.html`      |
| `RecommendationException` | `recommendation-error.html` |
| `TimeoutException`        | `timeout.html`              |

Les pages d'erreur sont disponibles en deux versions :
- `src/main/resources/static/errors/` — Thème v1 (Netflix)
- `src/main/resources/static/errors/v2/` — Thème v2 (Disney+)

## 📚 Documentation API (Swagger UI)

Une fois l'application démarrée :

- **Swagger UI** : `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON** : `http://localhost:8080/api-docs`

La documentation inclut les informations de contact, la licence Apache 2.0, les termes d'utilisation et les détails du serveur.

## 🐵 Chaos Monkey

### Activation

> [!IMPORTANT]
> Pour activer Chaos Monkey, il est **indispensable** d'ajouter la ligne suivante dans `application.properties` :
> ```properties
> spring.profiles.active=chaos-monkey
> ```
> Sans cette configuration, Chaos Monkey sera présent mais **restera inactif**.

### Endpoints Actuator & Resilience

- `GET /actuator/chaosmonkey` — État de Chaos Monkey
- `POST /actuator/chaosmonkey/enable` — Activer Chaos Monkey
- `POST /actuator/chaosmonkey/disable` — Désactiver Chaos Monkey
- `GET /actuator/health` — Santé (inclut l'état des Circuit Breakers)
- `GET /actuator/circuitbreakers` — Détails des Circuit Breakers
- `GET /actuator/retries` — Détails des mécanismes de Retry

### Exemple d'utilisation

```bash
# Vérifier le statut
curl http://localhost:8080/actuator/chaosmonkey

# Activer Chaos Monkey
curl -X POST http://localhost:8080/actuator/chaosmonkey/enable

# Désactiver Chaos Monkey
curl -X POST http://localhost:8080/actuator/chaosmonkey/disable
```

### Types de pannes injectées

- **Latence** : Ralentissement des réponses
- **Exceptions** : Erreurs aléatoires
- **Assaults** : Attaques sur les services, repositories et controllers

### 🎯 Services critiques à tester

#### 1. **StreamingService** ⚠️ CRITIQUE
Le service de streaming est le cœur de l'application. Une défaillance impacte directement l'expérience utilisateur.

**Scénarios de test :**
- Injection de latence lors du démarrage d'un stream
- Timeout lors de la récupération de la qualité de streaming

**Impact potentiel :** Perte de revenus, frustration utilisateur

#### 2. **RecommendationService** 🔴 HAUTE PRIORITÉ
Les recommandations personnalisées sont essentielles pour la rétention utilisateur.

**Scénarios de test :**
- Latence excessive lors du calcul des recommandations
- Indisponibilité temporaire du service

**Impact potentiel :** Baisse de l'engagement

#### 3. **CatalogService** 🟠 PRIORITÉ MOYENNE
Le catalogue doit être disponible pour permettre la découverte de contenu.

**Scénarios de test :**
- Ralentissement de la recherche de vidéos
- Timeout sur les requêtes de catalogue

**Impact potentiel :** Navigation difficile, expérience utilisateur dégradée

#### 4. **UserService** 🟡 PRIORITÉ STANDARD

**Scénarios de test :**
- Latence lors de la récupération du profil
- Timeout sur les requêtes utilisateur

#### 💡 Stratégie de test recommandée

1. **Phase 1** : Tester `StreamingService` avec latence progressive (100ms → 500ms → 2s)
2. **Phase 2** : Injecter des exceptions aléatoires dans `RecommendationService` (taux 10% → 30%)
3. **Phase 3** : Combiner latence sur `CatalogService` + exceptions sur `StreamingService`
4. **Phase 4** : Test de charge avec Chaos Monkey actif sur tous les services

**Métriques à surveiller :**
- Taux d'erreur par endpoint
- Temps de réponse (p50, p95, p99)
- Nombre de sessions abandonnées

## 💾 Base de données

L'application utilise H2, une base de données en mémoire.

**Console H2 :** `http://localhost:8080/h2-console`

**Paramètres de connexion :**
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: *(vide)*

### Données de démonstration

Au démarrage, l'application charge automatiquement :
- 50+ vidéos de différents genres et sections (Disney, Pixar, Marvel, Star Wars, National Geographic)
- 10 utilisateurs avec historique de visionnage
- Recommandations pré-calculées

## 🏗️ Architecture

```
chaos-monkey-application/
├── controllers/
│   ├── HomeController          # Interface web (MVC)
│   ├── CatalogController       # API REST catalogue
│   ├── UserController          # API REST utilisateurs
│   ├── StreamingController     # API REST streaming
│   └── RecommendationController
├── services/
│   ├── CatalogService
│   ├── SectionService          # Filtrage par section (Disney, Marvel…)
│   ├── UserService
│   ├── StreamingService
│   └── RecommendationService
├── exception/
│   ├── GlobalExceptionHandler  # Gestion centralisée des erreurs
│   ├── CatalogException
│   ├── UserException
│   ├── StreamingException
│   ├── RecommendationException
│   └── RequestTimeoutException
├── configurations/
│   ├── UIConfiguration         # Sélection de l'interface (v1/v2)
│   └── OpenApiConfiguration    # Configuration Swagger UI
├── models/
│   ├── Video
│   ├── User
│   ├── WatchHistory
│   └── Recommendation
└── DataLoader                  # Chargement des données initiales
```

**Ressources statiques :**
```
src/main/resources/
├── static/
│   ├── css/
│   │   ├── style.css           # Thème v1 (Netflix)
│   │   └── disney.css          # Thème v2 (Disney+)
│   ├── errors/                 # Pages d'erreur v1
│   ├── errors/v2/              # Pages d'erreur v2 (Disney+)
│   └── favicon/
│       └── chaos+.png
└── templates/
    ├── index.html              # Interface v1
    └── index_v2.html           # Interface v2 (Disney+)
```

## 🧪 Scénarios de test

### 1. Test de Résilience (Retry & Fallback)

Ce scénario démontre comment l'application survit à des erreurs intermittentes grâce à Resilience4j.

```bash
# 1. Configurer Chaos Monkey pour injecter des exceptions (100% de probabilité)
# On cible le service de streaming
curl -X POST http://localhost:8080/actuator/chaosmonkey/assaults \
  -H "Content-Type: application/json" \
  -d '{"level": 1, "exceptionsActive": true, "watchedCustomServices": ["fr.eletutour.chaosmonkeyapplication.services.StreamingService"]}'

# 2. Activer Chaos Monkey
curl -X POST http://localhost:8080/actuator/chaosmonkey/enable

# 3. Tenter de démarrer un stream
curl -X POST http://localhost:8080/api/streaming/start \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "videoId": 1}'

# 4. Observer le comportement :
#    → Dans les logs : Vous verrez 3 tentatives (Retry)
#    → Résultat API : Le fallback retourne un statut "TEMPORARILY_UNAVAILABLE" 
#      au lieu d'une erreur 500.
```

### 2. Test du Circuit Breaker

Démontre la protection des ressources système.

```bash
# 1. Provoquer plusieurs échecs sur les recommandations
# Le circuit breaker est configuré sur une fenêtre de 10 appels (failureRateThreshold: 50%)
for i in {1..10}; do curl http://localhost:8080/api/recommendations/1; done

# 2. Vérifier l'état du circuit breaker
curl http://localhost:8080/actuator/circuitbreakers

# 3. Observer le fallback :
#    → Même quand le service est "cassé", l'API retourne du contenu 
#      populaire par défaut ("Popular now (Fallback)")
```

## 📝 Licence

Apache 2.0 — Voir [apache.org/licenses](https://www.apache.org/licenses/LICENSE-2.0)

## 👨‍💻 Auteur

**Erwan Le Tutour** — [eletutour.fr](https://eletutour.fr)

---

> **Note** : Cette application est inspirée de la Simian Army de Netflix et est destinée à des fins éducatives pour comprendre les principes du Chaos Engineering.

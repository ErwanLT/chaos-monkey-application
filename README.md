# 🎬 Chaos Monkey Application

Une application de streaming vidéo inspirée de Netflix, intégrant **Chaos Monkey** pour démontrer les principes du Chaos Engineering.

## 📋 Description

Cette application Spring Boot simule un service de streaming vidéo avec des fonctionnalités similaires à Netflix (catalogue de vidéos, recommandations, historique de visionnage). Elle intègre **Chaos Monkey for Spring Boot** pour injecter des pannes contrôlées et tester la résilience de l'application.

### Fonctionnalités principales

- 📺 **Catalogue de vidéos** : Gestion d'un catalogue de plus de 50 vidéos
- 👥 **Gestion des utilisateurs** : Système d'utilisateurs avec profils
- 🎯 **Recommandations** : Système de recommandations basé sur l'historique
- ▶️ **Streaming** : Simulation de streaming avec gestion de la qualité
- 📊 **Historique de visionnage** : Suivi de la progression et des vidéos complétées
- 🐵 **Chaos Engineering** : Injection de latence et d'exceptions via Chaos Monkey

## 🛠️ Technologies utilisées

- **Java 17**
- **Spring Boot 2.5.15**
- **Spring Data JPA**
- **H2 Database** (base de données en mémoire)
- **Chaos Monkey for Spring Boot 2.5.1**
- **Spring Boot Actuator**
- **SpringDoc OpenAPI 2.8.14** (documentation API avec Swagger UI)
- **Maven**

## 🚀 Installation et démarrage

### Prérequis

- Java 17 ou supérieur
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

## 📡 API Endpoints

### Catalogue

- `GET /api/catalog/videos` - Liste toutes les vidéos
- `GET /api/catalog/videos/{id}` - Détails d'une vidéo
- `GET /api/catalog/genres` - Liste des genres disponibles
- `GET /api/catalog/videos/genre/{genre}` - Vidéos par genre
- `GET /api/catalog/videos/search?title={title}` - Recherche de vidéos

### Utilisateurs

- `GET /api/users` - Liste des utilisateurs
- `GET /api/users/{id}` - Détails d'un utilisateur
- `POST /api/users` - Créer un utilisateur

### Streaming

- `POST /api/streaming/start` - Démarrer un streaming
  ```json
  {
    "userId": 1,
    "videoId": 1
  }
  ```
- `POST /api/streaming/progress` - Mettre à jour la progression
  ```json
  {
    "userId": 1,
    "videoId": 1,
    "progress": 45
  }
  ```
- `GET /api/streaming/history/{userId}` - Historique de visionnage
- `GET /api/streaming/completed/{userId}` - Vidéos complétées
- `GET /api/streaming/quality?networkSpeed={speed}` - Qualité de streaming

### Recommandations

- `GET /api/recommendations/{userId}` - Recommandations personnalisées
- `GET /api/recommendations/trending` - Vidéos tendances
- `GET /api/recommendations/popular` - Vidéos populaires

## 📚 Documentation API (SpringDoc OpenAPI)

L'application intègre **SpringDoc OpenAPI** pour générer automatiquement une documentation interactive de tous les endpoints REST.

### Accès à la documentation

Une fois l'application démarrée, la documentation est accessible via :

- **Swagger UI** (interface interactive) : `http://localhost:8080/swagger-ui.html`
- **API Docs** (JSON OpenAPI) : `http://localhost:8080/api-docs`

### Fonctionnalités Swagger UI

- 📖 Documentation complète de tous les endpoints
- 🧪 Test interactif des API directement depuis le navigateur
- 📝 Schémas des modèles de données (Video, User, WatchHistory, etc.)
- 🏷️ Organisation par tags (Catalogue, Utilisateurs, Streaming, Recommandations)
- 📊 Exemples de requêtes et réponses

### Configuration

La configuration SpringDoc est définie dans `application.properties` :

```properties
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
```

## 🐵 Chaos Monkey

### Configuration

Chaos Monkey est activé et contrôlable via Spring Boot Actuator.

> [!IMPORTANT]
> Pour activer Chaos Monkey, il est **indispensable** d'ajouter la ligne suivante dans `src/main/resources/application.properties` :
> ```properties
> spring.profiles.active=chaos-monkey
> ```
> Sans cette configuration, Chaos Monkey sera présent dans l'application mais **restera inactif**.

**Configuration complète dans `application.properties` :**
```properties
# Actuator endpoints for Chaos Monkey control
management.endpoints.web.exposure.include=health,info,chaosmonkey
management.endpoint.chaosmonkey.enabled=true

# Activation du profil Chaos Monkey (OBLIGATOIRE)
spring.profiles.active=chaos-monkey
```

### Endpoints Actuator

- `GET /actuator/chaosmonkey` - État de Chaos Monkey
- `POST /actuator/chaosmonkey/enable` - Activer Chaos Monkey
- `POST /actuator/chaosmonkey/disable` - Désactiver Chaos Monkey
- `GET /actuator/chaosmonkey/status` - Statut actuel
- `GET /actuator/health` - Santé de l'application

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

Dans une architecture de streaming vidéo, certains services sont plus critiques que d'autres. Voici les services prioritaires pour le Chaos Engineering :

#### 1. **StreamingService** ⚠️ CRITIQUE
Le service de streaming est le cœur de l'application. Une défaillance impacte directement l'expérience utilisateur.

**Scénarios de test :**
- Injection de latence lors du démarrage d'un stream
- Perte de connexion pendant la lecture
- Échec de mise à jour de la progression
- Timeout lors de la récupération de la qualité de streaming

**Impact potentiel :** Perte de revenus, frustration utilisateur, abandon de la plateforme

#### 2. **RecommendationService** 🔴 HAUTE PRIORITÉ
Les recommandations personnalisées sont essentielles pour la rétention utilisateur et l'engagement.

**Scénarios de test :**
- Latence excessive lors du calcul des recommandations
- Échec de récupération de l'historique de visionnage
- Erreurs lors de l'agrégation des tendances
- Indisponibilité temporaire du service

**Impact potentiel :** Baisse de l'engagement, diminution du temps de visionnage

#### 3. **CatalogService** 🟠 PRIORITÉ MOYENNE
Le catalogue doit être disponible pour permettre la découverte de contenu.

**Scénarios de test :**
- Ralentissement de la recherche de vidéos
- Échec de chargement des métadonnées
- Erreurs lors du filtrage par genre
- Timeout sur les requêtes de catalogue

**Impact potentiel :** Navigation difficile, expérience utilisateur dégradée

#### 4. **UserService** 🟡 PRIORITÉ STANDARD
La gestion des utilisateurs doit être résiliente mais peut tolérer des dégradations gracieuses.

**Scénarios de test :**
- Latence lors de la récupération du profil
- Échec de création d'utilisateur
- Timeout sur les requêtes utilisateur

**Impact potentiel :** Problèmes d'authentification, impossibilité de créer de nouveaux comptes

#### 💡 Stratégie de test recommandée

1. **Phase 1** : Tester `StreamingService` avec latence progressive (100ms → 500ms → 2s)
2. **Phase 2** : Injecter des exceptions aléatoires dans `RecommendationService` (taux 10% → 30%)
3. **Phase 3** : Combiner latence sur `CatalogService` + exceptions sur `StreamingService`
4. **Phase 4** : Test de charge avec Chaos Monkey actif sur tous les services

**Métriques à surveiller :**
- Taux d'erreur par endpoint
- Temps de réponse (p50, p95, p99)
- Taux de retry
- Nombre de sessions abandonnées

## 💾 Base de données

L'application utilise H2, une base de données en mémoire. La console H2 est accessible à :

```
http://localhost:8080/h2-console
```

**Paramètres de connexion :**
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: *(vide)*

### Données de démonstration

Au démarrage, l'application charge automatiquement :
- 50+ vidéos de différents genres
- 10 utilisateurs
- Historique de visionnage
- Recommandations

## 🏗️ Architecture

```
chaos-monkey-application/
├── controllers/          # Endpoints REST
│   ├── CatalogController
│   ├── UserController
│   ├── StreamingController
│   └── RecommendationController
├── services/            # Logique métier
│   ├── CatalogService
│   ├── UserService
│   ├── StreamingService
│   └── RecommendationService
├── repositories/        # Accès aux données
│   ├── VideoRepository
│   ├── UserRepository
│   ├── WatchHistoryRepository
│   └── RecommendationRepository
├── models/             # Entités JPA
│   ├── Video
│   ├── User
│   ├── WatchHistory
│   └── Recommendation
└── DataLoader          # Chargement des données initiales
```

## 🧪 Tests et démonstration

### Scénario de test typique

1. **Lister les vidéos disponibles**
   ```bash
   curl http://localhost:8080/api/catalog/videos
   ```

2. **Démarrer un streaming**
   ```bash
   curl -X POST http://localhost:8080/api/streaming/start \
     -H "Content-Type: application/json" \
     -d '{"userId": 1, "videoId": 1}'
   ```

3. **Activer Chaos Monkey**
   ```bash
   curl -X POST http://localhost:8080/actuator/chaosmonkey/enable
   ```

4. **Observer les comportements**
   - Latences aléatoires
   - Erreurs intermittentes
   - Résilience de l'application

## 📝 Licence

Ce projet est un exemple de démonstration pour le Chaos Engineering.

## 👨‍💻 Auteur

Erwan Le Tutour

---

**Note** : Cette application est inspirée de la Simian Army de Netflix et est destinée à des fins éducatives pour comprendre les principes du Chaos Engineering.

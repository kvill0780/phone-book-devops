# 📱 Phone Book Application - Projet DevOps

![CI/CD Pipeline](https://github.com/kvill0780/phone-book-devops/actions/workflows/ci-cd.yml/badge.svg)

Application de gestion de contacts avec architecture microservices, CI/CD automatisé, et déploiement Kubernetes.

## 🚀 Démarrage Rapide (3 minutes)

### Prérequis
- Docker & Docker Compose
- Git

### Installation

```bash
# 1. Cloner le projet
git clone https://github.com/kvill0780/phone-book-devops.git
cd phone-book-devops

# 2. Lancer avec Docker Compose
docker compose up -d

# 3. Accéder à l'application
# Frontend: http://localhost:8000
# Backend API: http://localhost:8080/api
# Grafana: http://localhost:3000 (admin/admin)
# Prometheus: http://localhost:9090
```

**C'est tout !** L'application est prête en 3 minutes.

## 📁 Architecture

```
┌─────────────────────────────────────────────┐
│          KUBERNETES / DOCKER COMPOSE        │
│                                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ Frontend │  │ Backend  │  │  MySQL   │   │
│  │  React   │  │  Spring  │  │   8.0    │   │
│  │  x2      │  │  x2      │  │   x1     │   │
│  └──────────┘  └──────────┘  └──────────┘   │
│                                             │
│  ┌──────────┐  ┌───────────┐  ┌──────────┐  │
│  │  Redis   │  │Prometheus │  │ Grafana  │  │
│  │  Cache   │  │ Metrics   │  │Dashboard │  │
│  └──────────┘  └───────────┘  └──────────┘  │
└─────────────────────────────────────────────┘
```

**Stack Technique** :
- Frontend : React + Vite + TailwindCSS
- Backend : Spring Boot 3.5 + JPA + Security
- Database : MySQL 8.0 + Redis 7
- Monitoring : Prometheus + Grafana
- CI/CD : GitHub Actions
- Orchestration : Kubernetes + Docker Compose

## 🔄 CI/CD Pipeline

### Architecture du Pipeline

Le pipeline GitHub Actions s'exécute automatiquement sur chaque push/pull request :

```
┌──────────────────────────────────────────────────────────┐
│                    GITHUB ACTIONS WORKFLOW               │
├──────────────────────────────────────────────────────────┤
│                                                          │
│  Trigger: push/pull_request sur main                     │
│           ↓                                              │
│  ┌──────────────────┐  ┌──────────────────┐              │
│  │  test-backend    │  │  test-frontend   │              │
│  │  (Maven tests)   │  │  (npm tests)     │              │
│  └────────┬─────────┘  └────────┬─────────┘              │
│           └──────────┬───────────┘                       │
│                      ↓                                   │
│  ┌──────────────────┐  ┌──────────────────┐              │
│  │  build-backend   │  │  build-frontend  │              │
│  │  (Docker image)  │  │  (Docker image)  │              │
│  └────────┬─────────┘  └────────┬─────────┘              │
│           └──────────┬───────────┘                       │
│                      ↓                                   │
│           ┌──────────────────┐                           │
│           │  security-scan   │                           │
│           │  (Trivy)         │                           │
│           └──────────────────┘                           │
│                      ↓                                   │
│           ┌──────────────────┐                           │
│           │  Push to Docker  │                           │
│           │  Hub (latest +   │                           │
│           │  SHA tag)        │                           │
│           └──────────────────┘                           │
└──────────────────────────────────────────────────────────┘
```

### Logique Détaillée du Pipeline

#### 1. **Déclencheurs (Triggers)**
```yaml
on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]
```
- **Push sur main** : Exécute le pipeline complet (test → build → scan → push)
- **Pull Request** : Exécute tests et build pour validation avant merge

#### 2. **Job: test-backend**
```yaml
runs-on: ubuntu-latest
steps:
  1. Checkout du code
  2. Setup Java 21
  3. Cache Maven dependencies (~/.m2/repository)
  4. Exécution: mvn clean test
  5. Upload des rapports de tests (artifacts)
```
**Objectif** : Valider que tous les tests unitaires backend passent (15 tests)

#### 3. **Job: test-frontend**
```yaml
runs-on: ubuntu-latest
steps:
  1. Checkout du code
  2. Setup Node.js 20
  3. Cache npm dependencies
  4. Installation: npm install --legacy-peer-deps
  5. Exécution: npm test
```
**Objectif** : Valider la syntaxe et les dépendances frontend

#### 4. **Job: build-backend**
```yaml
needs: test-backend  # Attend la réussite des tests
steps:
  1. Checkout du code
  2. Login Docker Hub (secrets)
  3. Setup Docker Buildx (multi-platform)
  4. Build image: kvill0780/phone-book-backend:latest
  5. Tag avec SHA: kvill0780/phone-book-backend:${{ github.sha }}
  6. Push vers Docker Hub
```
**Objectif** : Créer l'image Docker backend et la publier avec 2 tags (latest + commit SHA)

#### 5. **Job: build-frontend**
```yaml
needs: test-frontend
steps:
  1. Checkout du code
  2. Login Docker Hub
  3. Build image: kvill0780/phone-book-frontend:latest
  4. Tag avec SHA
  5. Push vers Docker Hub
```
**Objectif** : Créer l'image Docker frontend et la publier

#### 6. **Job: security-scan**
```yaml
needs: [build-backend, build-frontend]
steps:
  1. Checkout du code
  2. Pull des images depuis Docker Hub
  3. Scan Trivy backend (vulnérabilités HIGH/CRITICAL)
  4. Scan Trivy frontend
  5. Upload des rapports SARIF vers GitHub Security
```
**Objectif** : Détecter les vulnérabilités de sécurité dans les images Docker

### Stratégie de Tagging

- **`latest`** : Toujours la dernière version stable sur main
- **`${{ github.sha }}`** : Tag unique par commit (traçabilité, rollback)

### Secrets GitHub Requis

| Secret | Description | Utilisation |
|--------|-------------|-------------|
| `DOCKER_USERNAME` | Username Docker Hub | Authentification pour push images |
| `DOCKER_PASSWORD` | Token Docker Hub | Authentification sécurisée |

**Configuration** :
```bash
# Dans GitHub: Settings → Secrets and variables → Actions → New repository secret
DOCKER_USERNAME=kvill0780
DOCKER_PASSWORD=<votre_token_dockerhub>
```

### Optimisations du Pipeline

1. **Cache Maven/npm** : Réduit le temps de build de 3-4 minutes à ~1 minute
2. **Jobs parallèles** : test-backend et test-frontend s'exécutent simultanément
3. **Docker Buildx** : Support multi-platform (amd64, arm64)
4. **Artifacts** : Conservation des rapports de tests pendant 7 jours

### Monitoring du Pipeline

- **Badge CI/CD** : ![CI/CD](https://github.com/kvill0780/phone-book-devops/actions/workflows/ci-cd.yml/badge.svg)
- **GitHub Actions** : Onglet "Actions" pour voir l'historique
- **Durée moyenne** : ~5-7 minutes pour un pipeline complet
- **Taux de succès** : Visible dans l'historique des workflows

## 🐳 Docker Compose (Développement Local)

```bash
# Démarrer tous les services
docker compose up -d

# Voir les logs
docker compose logs -f

# Arrêter
docker compose down

# Nettoyer tout (volumes inclus)
docker compose down -v
```

**Services disponibles** :
- Backend : http://localhost:8080
- Frontend : http://localhost:8000
- **Swagger UI** : http://localhost:8080/swagger-ui.html
- **API Docs** : http://localhost:8080/api-docs
- MySQL : localhost:3306
- Redis : localhost:6379
- Prometheus : http://localhost:9090
- Grafana : http://localhost:3000

## ☸️ Kubernetes (Production)

### Prérequis
- Kubernetes cluster (minikube, kind, ou cloud)
- kubectl installé

### Déploiement

```bash
# 1. Démarrer minikube (si local)
minikube start --driver=docker
minikube addons enable ingress
minikube addons enable metrics-server

# 2. Créer les secrets
cd k8s
chmod +x create-secrets.sh
./create-secrets.sh

# 3. Déployer l'application
chmod +x deploy.sh
./deploy.sh

# 4. Vérifier le déploiement
kubectl get pods -n phone-book
kubectl get svc -n phone-book
kubectl get ingress -n phone-book

# 5. Accéder à l'application
# Ajouter à /etc/hosts :
echo "$(minikube ip) phone-book.local" | sudo tee -a /etc/hosts

# URLs :
# - Application: http://phone-book.local
# - Grafana: http://phone-book.local/grafana
# - Prometheus: http://phone-book.local/prometheus
```

### Commandes Utiles

```bash
# Scaler le backend
kubectl scale deployment backend --replicas=5 -n phone-book

# Voir les logs
kubectl logs -f deployment/backend -n phone-book

# Redémarrer un deployment
kubectl rollout restart deployment/backend -n phone-book

# Supprimer tout
kubectl delete namespace phone-book
```

## 📊 Monitoring

### Grafana
- URL : http://localhost:3000 (Docker) ou http://phone-book.local/grafana (K8s)
- Login : admin / admin
- Dashboard : "Phone Book - Application Overview"

**Métriques disponibles** :
- HTTP Requests Rate
- Response Time (p95)
- JVM Memory Usage
- Active Pods
- Error Rate
- Database Connections (MySQL + Redis)

### Prometheus
- URL : http://localhost:9090 (Docker) ou http://phone-book.local/prometheus (K8s)
- Targets : Backend, MySQL Exporter, Redis Exporter

## 🧪 Tests

### Backend
```bash
cd phone-book-backend
mvn test
```

### Frontend
```bash
cd phone-book-frontend
npm install --legacy-peer-deps
npm test
```

### Tests d'intégration (avec Docker Compose)
```bash
docker compose up -d
# Attendre 30s que tout démarre
curl http://localhost:8080/actuator/health
curl http://localhost:8000
```

## 🔐 Sécurité

- **Secrets** : Générés automatiquement avec `openssl rand -base64`
- **JWT** : Authentification par tokens
- **Network Policies** : Isolation des pods en Kubernetes
- **Security Scan** : Trivy dans le pipeline CI/CD
- **HTTPS** : Configurable via Ingress TLS

## 📈 Scalabilité

### Auto-scaling (HPA)
```bash
# Activer l'auto-scaling
kubectl autoscale deployment backend \
  --cpu-percent=70 \
  --min=2 \
  --max=10 \
  -n phone-book

# Vérifier
kubectl get hpa -n phone-book
```

### Scaling manuel
```bash
# Docker Compose
docker compose up -d --scale backend=3 --scale frontend=2

# Kubernetes
kubectl scale deployment backend --replicas=5 -n phone-book
```

## 🐛 Troubleshooting

### Docker Compose
```bash
# Voir les logs d'un service
docker compose logs backend

# Redémarrer un service
docker compose restart backend

# Vérifier l'état
docker compose ps
```

### Kubernetes
```bash
# Pod ne démarre pas
kubectl describe pod <pod-name> -n phone-book
kubectl logs <pod-name> -n phone-book

# Service inaccessible
kubectl get svc -n phone-book
kubectl get endpoints -n phone-book

# Ingress ne fonctionne pas
kubectl describe ingress phone-book-ingress -n phone-book
```

### Problèmes courants

**MySQL ne démarre pas** :
```bash
# Docker Compose
docker compose down -v  # Supprime les volumes
docker compose up -d

# Kubernetes
kubectl delete pvc mysql-pvc -n phone-book
kubectl delete pod mysql-0 -n phone-book
```

**Backend ne se connecte pas à MySQL** :
- Vérifier que MySQL est prêt : `docker compose logs mysql`
- Attendre 30-60s après le démarrage de MySQL

**Frontend ne charge pas** :
- Vérifier les logs : `docker compose logs frontend`
- Vérifier nginx.conf : backend doit être accessible

## 📝 Structure du Projet

```
phone-book-devops/
├── .github/workflows/       # CI/CD GitHub Actions
│   └── ci-cd.yml           # Pipeline principal
├── phone-book-backend/     # Backend Spring Boot
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── phone-book-frontend/    # Frontend React
│   ├── src/
│   ├── Dockerfile
│   └── package.json
├── k8s/                    # Manifests Kubernetes
│   ├── base/               # Deployments, Services, ConfigMaps
│   ├── create-secrets.sh   # Génération secrets
│   └── deploy.sh           # Script de déploiement
├── docker compose.yml      # Orchestration locale
├── prometheus.yml          # Config Prometheus
├── README.md              # Ce fichier
├── QUICKSTART.md          # Guide rapide
└── RAPPORT.md             # Rapport technique
```

## 🤝 Contribution

```bash
git checkout -b feature/ma-feature
git commit -m "feat: description"
git push origin feature/ma-feature
# Ouvrir une Pull Request
```

## 📚 Documentation

- **[README.md](README.md)** - Guide complet (ce fichier)
- **[QUICKSTART.md](QUICKSTART.md)** - Démarrage en 3 minutes
- **[RAPPORT.md](RAPPORT.md)** - Rapport technique détaillé

## 🎯 Fonctionnalités

- ✅ Architecture microservices
- ✅ CI/CD automatisé (GitHub Actions)
- ✅ Containerisation (Docker)
- ✅ Orchestration (Kubernetes)
- ✅ Monitoring (Prometheus + Grafana)
- ✅ Caching (Redis)
- ✅ Security scanning (Trivy)
- ✅ Auto-scaling (HPA)
- ✅ Network policies
- ✅ Health checks
- ✅ Rolling updates
- ✅ Secrets management

## 👥 Auteur

**Étudiant MIAGE L3** - Projet DevOps 2024-2025

## 📄 Licence

MIT License

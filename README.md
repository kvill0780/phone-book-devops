# 📱 Phone Book Application - Projet DevOps

![CI/CD Pipeline](https://github.com/kvill/phone-book/actions/workflows/ci-cd.yml/badge.svg)
![PR Checks](https://github.com/kvill/phone-book/actions/workflows/pr-check.yml/badge.svg)

Application de gestion de contacts avec architecture microservices, déployée sur Kubernetes avec pipeline CI/CD automatisé.

## 🏗️ Architecture

```
┌─────────────────────────────────────────────┐
│             KUBERNETES CLUSTER              │
│                                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ Frontend │  │ Backend  │  │  MySQL   │   │
│  │  (React) │  │ (Spring) │  │          │   │
│  │  x2      │  │  x2      │  │  x1      │   │
│  └──────────┘  └──────────┘  └──────────┘   │
│                                             │
│  ┌──────────┐  ┌───────────┐  ┌──────────┐  │
│  │  Redis   │  │Prometheus │  │ Grafana  │  │
│  │  Cache   │  │ Metrics   │  │Dashboard │  │
│  └──────────┘  └───────────┘  └──────────┘  │
│                                             │
│              ┌─────────────────┐            │
│              │ Ingress NGINX   │            │
│              └─────────────────┘            │
└─────────────────────────────────────────────┘
```

### Composants

- **Frontend** : React + Vite + TailwindCSS
- **Backend** : Spring Boot 3.5 + JPA + Security
- **Database** : MySQL 8.0
- **Cache** : Redis 7
- **Monitoring** : Prometheus + Grafana
- **Orchestration** : Kubernetes
- **CI/CD** : GitHub Actions

## 🚀 Démarrage Rapide

> **📖 [Guide de démarrage complet](QUICKSTART.md)** - Tout ce qu'il faut pour démarrer en 3 minutes

### Prérequis

- Docker & Docker Compose
- Kubernetes (minikube, kind, ou cloud)
- kubectl
- Node.js 20+
- Java 17+
- Maven 3.8+

### 1. Développement Local (Docker Compose)

```bash
# Cloner le repository
git clone https://github.com/kvill/phone-book.git
cd phone-book

# Créer les fichiers de secrets
mkdir -p secrets
echo "admin" > secrets/mysql_password.txt
echo "admin" > secrets/grafana_password.txt

# Lancer l'application
docker-compose up -d

# Accéder à l'application
# Frontend: http://localhost:3000
# Backend API: http://localhost:8080/api
# Grafana: http://localhost:3001
```

### 2. Déploiement Kubernetes

```bash
# Appliquer les manifests
cd k8s
./deploy.sh

# Ou manuellement
kubectl apply -f base/

# Vérifier le déploiement
kubectl get pods -n phone-book
kubectl get svc -n phone-book
```

### 3. Accès à l'application

Ajouter à `/etc/hosts` :
```
127.0.0.1 phone-book.local
```

URLs :
- **Application** : http://phone-book.local
- **API** : http://phone-book.local/api
- **Grafana** : http://phone-book.local/grafana (admin / admin)
- **Prometheus** : http://phone-book.local/prometheus

## 📁 Structure du Projet

```
projet-devops/
├── .github/
│   └── workflows/          # GitHub Actions CI/CD
│       ├── ci-cd.yml       # Pipeline principal
│       └── pr-check.yml    # Vérification PR
├── spring-phone-book/      # Backend Spring Boot
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── phone-book-frontend/    # Frontend React
│   ├── src/
│   ├── Dockerfile
│   └── package.json
├── k8s/                    # Manifests Kubernetes
│   ├── base/
│   │   ├── namespace.yaml
│   │   ├── configmap.yaml
│   │   ├── secrets.yaml
│   │   ├── mysql-deployment.yaml
│   │   ├── redis-deployment.yaml
│   │   ├── backend-deployment.yaml
│   │   ├── frontend-deployment.yaml
│   │   ├── prometheus-deployment.yaml
│   │   ├── grafana-deployment.yaml
│   │   └── ingress.yaml
│   ├── deploy.sh           # Script de déploiement
│   └── README.md
├── docker-compose.yml      # Développement local
├── prometheus.yml          # Configuration Prometheus
└── README.md
```

## 🔄 Pipeline CI/CD

### Workflow automatisé

```mermaid
graph LR
    A[Push Code] --> B[Build Backend]
    A --> C[Build Frontend]
    B --> D[Run Tests]
    C --> E[Run Tests]
    D --> F[Build Docker Images]
    E --> F
    F --> G[Push to Docker Hub]
    G --> H[Deploy to Kubernetes]
    H --> I[Verify Deployment]
```

### Déclencheurs

- **Push sur `main`** : Build + Test + Deploy
- **Push sur `develop`** : Build + Test
- **Pull Request** : Build + Test + Quality Checks

### Configuration

Voir [.github/SETUP.md](.github/SETUP.md) pour configurer les secrets GitHub Actions.

## 🧪 Tests

### Backend
```bash
cd spring-phone-book
mvn test
```

### Frontend
```bash
cd phone-book-frontend
npm test
```

## 📊 Monitoring

### Prometheus
- Métriques applicatives
- Métriques système
- Alertes configurables

### Grafana
- Dashboards pré-configurés
- Visualisation temps réel
- Alerting

### Métriques disponibles
- Nombre de requêtes HTTP
- Temps de réponse
- Taux d'erreur
- Utilisation CPU/Mémoire
- Connexions base de données
- Cache hit/miss ratio

## 🔐 Sécurité

- **Secrets Kubernetes** : Mots de passe chiffrés
- **JWT Authentication** : Tokens sécurisés
- **Rate Limiting** : Protection contre brute force
- **HTTPS** : Ingress avec TLS (optionnel)
- **Network Policies** : Isolation des pods
- **RBAC** : Contrôle d'accès Kubernetes

## 📈 Scalabilité

### Scaling horizontal
```bash
# Scaler le backend
kubectl scale deployment backend --replicas=5 -n phone-book

# Scaler le frontend
kubectl scale deployment frontend --replicas=3 -n phone-book
```

### Auto-scaling (HPA)
```bash
kubectl autoscale deployment backend \
  --cpu-percent=70 \
  --min=2 \
  --max=10 \
  -n phone-book
```

## 🐛 Troubleshooting

### Logs
```bash
# Logs backend
kubectl logs -f deployment/backend -n phone-book

# Logs frontend
kubectl logs -f deployment/frontend -n phone-book

# Logs MySQL
kubectl logs -f deployment/mysql -n phone-book
```

### Debug
```bash
# Entrer dans un pod
kubectl exec -it <pod-name> -n phone-book -- /bin/bash

# Vérifier les services
kubectl get svc -n phone-book

# Vérifier les endpoints
kubectl get endpoints -n phone-book
```

## 🤝 Contribution

1. Fork le projet
2. Créer une branche (`git checkout -b feature/AmazingFeature`)
3. Commit les changements (`git commit -m 'Add AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

## 📝 Documentation Complète

### Documents Principaux
- **[INDEX.md](INDEX.md)** - Index de toute la documentation
- **[RAPPORT.md](RAPPORT.md)** - Rapport technique complet (10 pages)
- **[RESUME-EXECUTIF.md](RESUME-EXECUTIF.md)** - Synthèse pour la direction
- **[QUICKSTART.md](QUICKSTART.md)** - Guide de démarrage rapide
- **[PRESENTATION.md](PRESENTATION.md)** - Guide de présentation du projet

### Guides Techniques
- **[LIVRABLES.md](LIVRABLES.md)** - Validation des livrables
- **[ANNEXES.md](ANNEXES.md)** - Annexes détaillées
- **[GRAFANA-SCREENSHOTS.md](GRAFANA-SCREENSHOTS.md)** - Guide captures Grafana
- **[CICD-STATUS.md](CICD-STATUS.md)** - État du pipeline CI/CD
- **[GITHUB-SETUP.md](GITHUB-SETUP.md)** - Configuration GitHub

### Parcours Recommandés
1. **Démarrage rapide** : README → QUICKSTART → `docker-compose up`
2. **Compréhension** : README → RAPPORT → ANNEXES
3. **Présentation** : RESUME-EXECUTIF → PRESENTATION → LIVRABLES

## 👥 Auteurs

- **Étudiant MIAGE L3** - Ingénieur DevOps Junior

## 📄 Licence

Ce projet est sous licence MIT - voir le fichier [LICENSE](LICENSE) pour plus de détails.

## 🙏 Remerciements

- Spring Boot Team
- React Team
- Kubernetes Community
- Prometheus & Grafana Teams
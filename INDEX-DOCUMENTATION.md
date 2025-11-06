# 📚 Index de la Documentation - Projet DevOps Phone Book

## 🎯 Navigation Rapide

Ce document vous guide vers la bonne documentation selon votre besoin.

---

## 🚀 Pour Démarrer

### Je veux comprendre le projet
👉 **[README.md](./README.md)** - Vue d'ensemble, architecture, technologies

### Je veux lancer l'application rapidement
👉 **[QUICKSTART.md](./QUICKSTART.md)** - Guide de démarrage en 3 minutes

### Je veux déployer sur Kubernetes
👉 **[k8s/README-DEPLOYMENT.md](./k8s/README-DEPLOYMENT.md)** - Guide de déploiement K8s

---

## 📖 Documentation Technique

### Je veux le rapport technique complet
👉 **[RAPPORT.md](./RAPPORT.md)** - Rapport technique détaillé (5-10 pages)

### Je veux vérifier la conformité aux exigences
👉 **[CONFORMITE-PROJET.md](./CONFORMITE-PROJET.md)** - Checklist de conformité

### Je veux préparer la soutenance
👉 **[SOUTENANCE.md](./SOUTENANCE.md)** - Guide de présentation et démo

---

## 🔧 Guides Spécifiques

### Monitoring & Dashboards

#### Je veux configurer Grafana
👉 **[GUIDE-DASHBOARDS-GRAFANA.md](./GUIDE-DASHBOARDS-GRAFANA.md)** - Configuration complète

#### Je veux créer des dashboards
👉 **[grafana-dashboards/README.md](./grafana-dashboards/README.md)** - Templates et exemples

#### Je veux documenter les captures d'écran
👉 **[screenshots/README.md](./screenshots/README.md)** - Guide des captures

---

## 📁 Structure du Projet

```
projet-devops/
│
├── 📄 README.md                    # Point d'entrée principal
├── 📄 QUICKSTART.md                # Démarrage rapide
├── 📄 RAPPORT.md                   # Rapport technique
├── 📄 CONFORMITE-PROJET.md         # Vérification conformité
├── 📄 SOUTENANCE.md                # Guide de présentation
├── 📄 GUIDE-DASHBOARDS-GRAFANA.md  # Configuration monitoring
├── 📄 INDEX-DOCUMENTATION.md       # Ce fichier
│
├── 📁 phone-book-frontend/         # Code React
│   ├── src/                        # Sources React
│   ├── Dockerfile                  # Image Docker frontend
│   └── package.json                # Dépendances npm
│
├── 📁 spring-phone-book/           # Code Spring Boot
│   ├── src/                        # Sources Java
│   ├── Dockerfile                  # Image Docker backend
│   └── pom.xml                     # Dépendances Maven
│
├── 📁 k8s/                         # Manifests Kubernetes
│   ├── base/                       # Configurations de base
│   │   ├── backend-deployment.yaml
│   │   ├── frontend-deployment.yaml
│   │   ├── mysql-deployment.yaml
│   │   ├── redis-deployment.yaml
│   │   ├── prometheus-deployment.yaml
│   │   ├── grafana-deployment.yaml
│   │   ├── configmap.yaml
│   │   ├── secrets.yaml
│   │   ├── ingress.yaml
│   │   └── hpa.yaml
│   ├── overlays/                   # Environnements (dev/prod)
│   ├── deploy.sh                   # Script de déploiement
│   ├── deploy-minikube.sh          # Déploiement local
│   └── verify-deployment.sh        # Vérification
│
├── 📁 .github/workflows/           # Pipelines CI/CD
│   ├── ci-cd.yml                   # Pipeline principal
│   ├── pr-check.yml                # Validation PR
│   └── local-test.yml              # Tests locaux
│
├── 📁 grafana-dashboards/          # Dashboards JSON
│   └── README.md                   # Guide d'import
│
├── 📁 screenshots/                 # Captures Grafana
│   └── README.md                   # Guide des captures
│
├── 📁 secrets/                     # Secrets (gitignored)
│   ├── mysql_password.txt
│   └── grafana_password.txt
│
├── 📄 docker-compose.yml           # Environnement local
├── 📄 prometheus.yml               # Config Prometheus
├── 📄 Makefile                     # Commandes utiles
├── 📄 setup.sh                     # Script d'installation
├── 📄 generate-traffic.sh          # Génération de trafic
└── 📄 test-cicd.sh                 # Tests CI/CD
```

---

## 🎓 Par Rôle

### Je suis Développeur
1. **[README.md](./README.md)** - Comprendre l'architecture
2. **[QUICKSTART.md](./QUICKSTART.md)** - Lancer en local
3. **[docker compose.yml](./docker-compose.yml)** - Environnement dev
4. **Frontend** : `phone-book-frontend/`
5. **Backend** : `spring-phone-book/`

### Je suis DevOps
1. **[k8s/README-DEPLOYMENT.md](./k8s/README-DEPLOYMENT.md)** - Déploiement
2. **[.github/workflows/](./github/workflows/)** - Pipelines CI/CD
3. **[GUIDE-DASHBOARDS-GRAFANA.md](./GUIDE-DASHBOARDS-GRAFANA.md)** - Monitoring
4. **[Makefile](./Makefile)** - Commandes automatisées

### Je suis Chef de Projet
1. **[CONFORMITE-PROJET.md](./CONFORMITE-PROJET.md)** - État d'avancement
2. **[RAPPORT.md](./RAPPORT.md)** - Documentation technique
3. **[SOUTENANCE.md](./SOUTENANCE.md)** - Préparation présentation

### Je suis Évaluateur
1. **[README.md](./README.md)** - Vue d'ensemble
2. **[CONFORMITE-PROJET.md](./CONFORMITE-PROJET.md)** - Vérification exigences
3. **[RAPPORT.md](./RAPPORT.md)** - Rapport technique
4. **[screenshots/](./screenshots/)** - Preuves visuelles

---

## 🔍 Par Sujet

### Docker & Conteneurisation
- **[phone-book-frontend/Dockerfile](./phone-book-frontend/Dockerfile)** - Frontend
- **[spring-phone-book/Dockerfile](./spring-phone-book/Dockerfile)** - Backend
- **[docker-compose.yml](./docker-compose.yml)** - Orchestration locale

### Kubernetes
- **[k8s/base/](./k8s/base/)** - Tous les manifests
- **[k8s/deploy.sh](./k8s/deploy.sh)** - Script de déploiement
- **[k8s/README-DEPLOYMENT.md](./k8s/README-DEPLOYMENT.md)** - Documentation

### CI/CD
- **[.github/workflows/ci-cd.yml](./.github/workflows/ci-cd.yml)** - Pipeline principal
- **[.github/workflows/pr-check.yml](./.github/workflows/pr-check.yml)** - Validation PR
- **[test-cicd.sh](./test-cicd.sh)** - Tests du pipeline

### Monitoring
- **[prometheus.yml](./prometheus.yml)** - Configuration Prometheus
- **[k8s/base/prometheus-deployment.yaml](./k8s/base/prometheus-deployment.yaml)** - Déploiement
- **[k8s/base/grafana-deployment.yaml](./k8s/base/grafana-deployment.yaml)** - Déploiement
- **[GUIDE-DASHBOARDS-GRAFANA.md](./GUIDE-DASHBOARDS-GRAFANA.md)** - Guide complet

### Sécurité
- **[k8s/base/secrets.yaml](./k8s/base/secrets.yaml)** - Secrets K8s
- **[secrets/](./secrets/)** - Fichiers de secrets (gitignored)
- **Backend Security** : `spring-phone-book/src/.../config/SecurityConfig.java`

---

## 📊 Livrables du Projet

### ✅ Livrables Obligatoires

| Livrable | Fichier | Status |
|----------|---------|--------|
| Code source GitHub | Tout le repo | ✅ |
| README.md | [README.md](./README.md) | ✅ |
| Dockerfiles | `*/Dockerfile` | ✅ |
| Manifests K8s | [k8s/base/](./k8s/base/) | ✅ |
| Workflow GitHub Actions | [.github/workflows/](./github/workflows/) | ✅ |
| Dashboard Grafana | [screenshots/](./screenshots/) | ⚠️ À compléter |
| Rapport technique | [RAPPORT.md](./RAPPORT.md) | ✅ |

### 📝 Documentation Complémentaire

| Document | Description | Fichier |
|----------|-------------|---------|
| Guide démarrage | Lancement rapide | [QUICKSTART.md](./QUICKSTART.md) |
| Conformité | Vérification exigences | [CONFORMITE-PROJET.md](./CONFORMITE-PROJET.md) |
| Soutenance | Guide présentation | [SOUTENANCE.md](./SOUTENANCE.md) |
| Dashboards | Configuration Grafana | [GUIDE-DASHBOARDS-GRAFANA.md](./GUIDE-DASHBOARDS-GRAFANA.md) |
| Index | Navigation (ce fichier) | [INDEX-DOCUMENTATION.md](./INDEX-DOCUMENTATION.md) |

---

## 🚀 Commandes Rapides

### Démarrage Local (Docker Compose)
```bash
# Lire d'abord
cat QUICKSTART.md

# Lancer
docker-compose up -d

# Accéder
# Frontend: http://localhost:8000
# Backend: http://localhost:8080
# Grafana: http://localhost:3000
```

### Déploiement Kubernetes
```bash
# Lire d'abord
cat k8s/README-DEPLOYMENT.md

# Déployer
cd k8s
./deploy.sh

# Vérifier
./verify-deployment.sh
```

### Monitoring
```bash
# Lire d'abord
cat GUIDE-DASHBOARDS-GRAFANA.md

# Accéder à Grafana
kubectl port-forward -n phone-book svc/grafana 3000:3000

# Générer du trafic
./generate-traffic.sh
```

---

## 🆘 Aide & Support

### Problèmes Courants

#### L'application ne démarre pas
1. Vérifier Docker : `docker ps`
2. Vérifier les logs : `docker-compose logs`
3. Consulter : [QUICKSTART.md](./QUICKSTART.md)

#### Le déploiement K8s échoue
1. Vérifier le cluster : `kubectl get nodes`
2. Vérifier les pods : `kubectl get pods -n phone-book`
3. Consulter : [k8s/README-DEPLOYMENT.md](./k8s/README-DEPLOYMENT.md)

#### Grafana ne montre pas de données
1. Vérifier Prometheus : `kubectl get pods -n phone-book | grep prometheus`
2. Générer du trafic : `./generate-traffic.sh`
3. Consulter : [GUIDE-DASHBOARDS-GRAFANA.md](./GUIDE-DASHBOARDS-GRAFANA.md)

#### Le pipeline CI/CD échoue
1. Vérifier GitHub Actions : onglet "Actions"
2. Consulter les logs du workflow
3. Vérifier les secrets : Settings → Secrets

---

## 📞 Contact & Contribution

### Structure de l'équipe
- **DevOps Lead** : Configuration infrastructure
- **Backend Dev** : Spring Boot application
- **Frontend Dev** : React application
- **QA** : Tests et validation

### Workflow Git
```bash
# Créer une branche
git checkout -b feature/ma-fonctionnalite

# Commit
git add .
git commit -m "feat: description"

# Push et créer PR
git push origin feature/ma-fonctionnalite
```

---

## 🎯 Prochaines Étapes

### Avant la soutenance
- [ ] Lire [SOUTENANCE.md](./SOUTENANCE.md)
- [ ] Tester le déploiement complet
- [ ] Créer les captures Grafana
- [ ] Préparer la démonstration
- [ ] Relire [RAPPORT.md](./RAPPORT.md)

### Après la soutenance
- [ ] Implémenter les améliorations suggérées
- [ ] Ajouter tests E2E
- [ ] Configurer alerting
- [ ] Déployer en production

---

## 📚 Ressources Externes

### Documentation Officielle
- [Docker Documentation](https://docs.docker.com/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [GitHub Actions](https://docs.github.com/en/actions)
- [Prometheus](https://prometheus.io/docs/)
- [Grafana](https://grafana.com/docs/)

### Tutoriels
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [React Best Practices](https://react.dev/learn)
- [Kubernetes Best Practices](https://kubernetes.io/docs/concepts/configuration/overview/)

---

## ✅ Checklist Finale

### Documentation
- [x] README.md complet
- [x] QUICKSTART.md rédigé
- [x] RAPPORT.md technique
- [x] CONFORMITE-PROJET.md vérifié
- [x] SOUTENANCE.md préparé
- [x] GUIDE-DASHBOARDS-GRAFANA.md créé
- [x] INDEX-DOCUMENTATION.md (ce fichier)

### Code
- [x] Frontend fonctionnel
- [x] Backend fonctionnel
- [x] Dockerfiles optimisés
- [x] Tests unitaires

### Infrastructure
- [x] Manifests Kubernetes
- [x] Pipeline CI/CD
- [x] Monitoring Prometheus
- [x] Dashboards Grafana

### Livrables
- [x] Code sur GitHub
- [x] Documentation complète
- [ ] Captures d'écran Grafana (à compléter)
- [x] Rapport technique

---

**Dernière mise à jour** : 6 novembre 2025  
**Version** : 1.0  
**Statut** : ✅ Projet conforme et prêt pour soutenance

---

**Navigation** : [⬆️ Retour en haut](#-index-de-la-documentation---projet-devops-phone-book)

# 🚀 Quick Start Guide

## Prérequis

- Docker & Docker Compose
- (Optionnel) Kubernetes (minikube/kind)
- (Optionnel) kubectl

## 🎯 Démarrage en 3 minutes

### 1. Cloner le projet

```bash
git clone https://github.com/kvill0780/phone-book-devops.git
cd phone-book-devops
```

### 2. Lancer avec Docker Compose

```bash
# Créer les secrets
mkdir -p secrets
echo "admin" > secrets/mysql_password.txt
echo "admin" > secrets/grafana_password.txt

# Lancer l'application
docker-compose up -d

# Attendre 30 secondes que tout démarre
sleep 30
```

### 3. Accéder à l'application

- **Application** : http://localhost:8000
- **Backend API** : http://localhost:8080/api
- **Grafana** : http://localhost:3000 (admin/admin)
- **Prometheus** : http://localhost:9090

## 📊 Vérifier que tout fonctionne

```bash
# Voir les conteneurs
docker compose ps

# Voir les logs
docker compose logs -f backend

# Tester l'API
curl http://localhost:8080/actuator/health
```

## 🎓 Déploiement Kubernetes (Optionnel)

### Prérequis
```bash
# Démarrer minikube
minikube start

# Activer les addons
minikube addons enable ingress
minikube addons enable metrics-server
```

### Déployer
```bash
# Charger les images locales
docker build -t projetdevops-backend:latest spring-phone-book/
docker build -t projetdevops-frontend:latest phone-book-frontend/
minikube image load projetdevops-backend:latest
minikube image load projetdevops-frontend:latest

# Déployer sur Kubernetes
cd k8s
./deploy.sh

# Vérifier
kubectl get pods -n phone-book
```

### Accéder
```bash
# Obtenir l'IP Minikube
minikube ip

# Ajouter à /etc/hosts
echo "$(minikube ip) phone-book.local" | sudo tee -a /etc/hosts

# Accéder
# http://phone-book.local
```

## 🛠️ Commandes utiles

### Docker Compose
```bash
# Arrêter
docker compose down

# Redémarrer un service
docker compose restart backend

# Voir les logs
docker compose logs -f

# Nettoyer
docker compose down -v
```

### Kubernetes
```bash
# Voir les pods
kubectl get pods -n phone-book

# Logs d'un pod
kubectl logs -f <pod-name> -n phone-book

# Redémarrer un deployment
kubectl rollout restart deployment backend -n phone-book

# Nettoyer
kubectl delete namespace phone-book
```

## 🐛 Troubleshooting

### Backend ne démarre pas
```bash
# Vérifier MySQL est prêt
docker compose logs mysql

# Vider le cache Redis
docker exec phone-book-redis redis-cli FLUSHALL
```

### Frontend ne charge pas
```bash
# Vérifier les logs
docker compose logs frontend

# Reconstruire
docker compose up -d --build frontend
```

### Port déjà utilisé
```bash
# Changer les ports dans docker-compose.yml
# Exemple: "8001:80" au lieu de "8000:80"
```

## 📚 Documentation complète

- [README.md](README.md) - Vue d'ensemble
- [RAPPORT.md](RAPPORT.md) - Rapport technique
- [ANNEXES.md](ANNEXES.md) - Annexes détaillées
- [GITHUB-SETUP.md](GITHUB-SETUP.md) - Configuration GitHub
- [CICD-STATUS.md](CICD-STATUS.md) - État CI/CD

## 🎯 Prochaines étapes

1. Créer un compte utilisateur
2. Ajouter des contacts
3. Créer des groupes
4. Explorer Grafana pour les métriques
5. Tester le scaling Kubernetes

## ✅ Checklist de vérification

- [ ] Docker Compose lance tous les services
- [ ] Application accessible sur http://localhost:8000
- [ ] Backend répond sur http://localhost:8080/actuator/health
- [ ] Grafana accessible sur http://localhost:3000
- [ ] Prometheus accessible sur http://localhost:9090
- [ ] Création de compte fonctionne
- [ ] Ajout de contact fonctionne
- [ ] Recherche fonctionne

## 🆘 Support

En cas de problème:
1. Vérifier les logs: `docker compose logs`
2. Vérifier les ports: `docker compose ps`
3. Nettoyer et redémarrer: `docker-compose down -v && docker-compose up -d`
4. Consulter [ANNEXES.md](ANNEXES.md) section Troubleshooting

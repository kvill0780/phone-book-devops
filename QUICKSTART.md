# ⚡ Quick Start - Phone Book DevOps

Démarrez l'application en **3 minutes** !

## 🎯 Option 1 : Docker Compose (Recommandé)

### Prérequis
- Docker Desktop installé
- Git

### Étapes

```bash
# 1. Cloner
git clone https://github.com/kvill0780/phone-book-devops.git
cd phone-book-devops

# 2. Lancer
docker compose up -d

# 3. Attendre 30 secondes que tout démarre
sleep 30

# 4. Accéder
open http://localhost:8000
```

### URLs

| Service | URL | Credentials |
|---------|-----|-------------|
| Frontend | http://localhost:8000 | - |
| Backend API | http://localhost:8080/api | - |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | - |
| Grafana | http://localhost:3000 | admin / admin |
| Prometheus | http://localhost:9090 | - |

### Vérification

```bash
# Voir les services
docker compose ps

# Tous les services doivent être "Up" et "healthy"
```

## ☸️ Option 2 : Kubernetes (Minikube)

### Prérequis
- Minikube installé
- kubectl installé

### Étapes

```bash
# 1. Cloner
git clone https://github.com/kvill0780/phone-book-devops.git
cd phone-book-devops

# 2. Démarrer minikube
minikube start --driver=docker
minikube addons enable ingress

# 3. Déployer
cd k8s
chmod +x create-secrets.sh deploy.sh
./create-secrets.sh
./deploy.sh

# 4. Configurer l'accès
echo "$(minikube ip) phone-book.local" | sudo tee -a /etc/hosts

# 5. Accéder
open http://phone-book.local
```

### URLs

| Service | URL | Credentials |
|---------|-----|-------------|
| Frontend | http://phone-book.local | - |
| Grafana | http://phone-book.local/grafana | admin / admin |
| Prometheus | http://phone-book.local/prometheus | - |

### Vérification

```bash
# Voir les pods
kubectl get pods -n phone-book

# Tous les pods doivent être "Running" et "1/1" ou "2/2"
```

## 🧪 Tester l'Application

### Créer un compte
1. Ouvrir http://localhost:8000 (ou http://phone-book.local)
2. Cliquer sur "S'inscrire"
3. Créer un compte : username / password
4. Se connecter

### Ajouter un contact
1. Cliquer sur "Nouveau Contact"
2. Remplir : Prénom, Nom, Téléphone
3. Sauvegarder

### Voir les métriques
1. Ouvrir Grafana : http://localhost:3000
2. Login : admin / admin
3. Dashboard : "Phone Book - Application Overview"
4. Voir les métriques en temps réel

## 🛑 Arrêter l'Application

### Docker Compose
```bash
docker compose down
```

### Kubernetes
```bash
kubectl delete namespace phone-book
minikube stop
```

## 🐛 Problèmes ?

### Docker Compose

**Erreur "port already in use"** :
```bash
# Changer les ports dans docker compose.yml
# Ou arrêter le service qui utilise le port
```

**Service ne démarre pas** :
```bash
docker compose logs <service-name>
docker compose restart <service-name>
```

### Kubernetes

**Pods en "Pending"** :
```bash
# Vérifier les ressources
kubectl describe pod <pod-name> -n phone-book
```

**Ingress ne fonctionne pas** :
```bash
# Vérifier que l'addon est activé
minikube addons list | grep ingress
minikube addons enable ingress
```

## 📚 Aller Plus Loin

- **[README.md](README.md)** - Documentation complète
- **[RAPPORT.md](RAPPORT.md)** - Rapport technique

## 🎉 C'est Tout !

Votre application Phone Book est maintenant opérationnelle avec :
- ✅ Frontend React
- ✅ Backend Spring Boot
- ✅ Base de données MySQL
- ✅ Cache Redis
- ✅ Monitoring Prometheus + Grafana

**Bon développement ! 🚀**

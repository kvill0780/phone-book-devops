# 📦 Déploiement Kubernetes

## 🎯 Deux modes de déploiement

### Mode 1: Développement Local (Minikube)

Pour tester localement avec Minikube :

```bash
# Build les images localement
docker build -t kvill0780/phone-book-backend:latest spring-phone-book/
docker build -t kvill0780/phone-book-frontend:latest phone-book-frontend/

# Charger dans Minikube
minikube image load kvill0780/phone-book-backend:latest
minikube image load kvill0780/phone-book-frontend:latest

# Déployer
cd k8s
./deploy.sh
```

### Mode 2: Production (Docker Hub)

Les images sont automatiquement poussées sur Docker Hub via CI/CD :

```bash
# Les images sont sur Docker Hub
# kvill0780/phone-book-backend:latest
# kvill0780/phone-book-frontend:latest

# Déployer directement
cd k8s
./deploy.sh
```

## 🔧 Configuration actuelle

Les manifests utilisent maintenant :
- `image: kvill0780/phone-book-backend:latest`
- `image: kvill0780/phone-book-frontend:latest`
- `imagePullPolicy: Always`

Cela signifie que Kubernetes téléchargera toujours les images depuis Docker Hub.

## 📝 Note importante

Pour que le déploiement fonctionne, il faut :

1. **Avoir les images sur Docker Hub** :
   - Soit via le pipeline CI/CD (automatique)
   - Soit en les poussant manuellement :
   ```bash
   docker push kvill0780/phone-book-backend:latest
   docker push kvill0780/phone-book-frontend:latest
   ```

2. **Ou utiliser Minikube en local** :
   - Charger les images avec `minikube image load`
   - Les images seront disponibles localement

## 🚀 Déploiement rapide

```bash
# 1. Démarrer Minikube
minikube start

# 2. Activer addons
minikube addons enable ingress
minikube addons enable metrics-server

# 3. Déployer
cd k8s
./deploy.sh

# 4. Vérifier
kubectl get pods -n phone-book

# 5. Accéder
echo "$(minikube ip) phone-book.local" | sudo tee -a /etc/hosts
# Ouvrir http://phone-book.local
```

# ⚡ Démarrage Rapide - 3 Minutes

## Option 1 : Docker Compose (Développement Local)

```bash
# 1. Cloner le repo
git clone https://github.com/kvill0780/phone-book-devops.git
cd phone-book-devops

# 2. Lancer l'application
docker-compose up -d

# 3. Attendre que tout démarre (30-60s)
docker-compose ps

# 4. Accéder à l'application
# Frontend:    http://localhost:8000
# Backend API: http://localhost:8080/api
# Grafana:     http://localhost:3000 (admin/admin)
# Prometheus:  http://localhost:9090
```

### Générer du trafic
```bash
./generate-traffic.sh
```

### Arrêter
```bash
docker-compose down -v
```

---

## Option 2 : Kubernetes (Production)

### Prérequis
- Kubernetes cluster (minikube, kind, ou cloud)
- kubectl configuré

### Déploiement

```bash
# 1. Créer les secrets
cd k8s
./create-secrets.sh
# ⚠️ Sauvegarder les mots de passe affichés !

# 2. Déployer
./deploy.sh

# 3. Vérifier
kubectl get pods -n phone-book
# Attendre que tous les pods soient "Running"

# 4. Accéder à l'application
# Ajouter à /etc/hosts:
echo "127.0.0.1 phone-book.local" | sudo tee -a /etc/hosts

# Pour minikube:
minikube addons enable ingress
minikube tunnel  # Dans un terminal séparé

# Application: http://phone-book.local
```

### Port-forwarding (alternative)
```bash
# Backend
kubectl port-forward -n phone-book svc/backend 8080:8080

# Frontend
kubectl port-forward -n phone-book svc/frontend 8000:80

# Grafana
kubectl port-forward -n phone-book svc/grafana 3000:3000
```

### Nettoyer
```bash
kubectl delete namespace phone-book
```

---

## 🔧 Configuration CI/CD

Pour activer le pipeline automatique :

1. **Créer un compte Docker Hub** (si pas déjà fait)

2. **Configurer les secrets GitHub** :
   - Aller dans Settings → Secrets and variables → Actions
   - Ajouter :
     - `DOCKER_USERNAME` : votre username Docker Hub
     - `DOCKER_PASSWORD` : créer un token sur https://hub.docker.com/settings/security

3. **Push sur main** :
   ```bash
   git add .
   git commit -m "feat: mon changement"
   git push origin main
   ```

4. **Vérifier** : Onglet "Actions" sur GitHub

📖 **Guide complet** : [.github/CICD-SETUP-GUIDE.md](.github/CICD-SETUP-GUIDE.md)

---

## 📊 Monitoring

```bash
# Accéder à Grafana
kubectl port-forward -n phone-book svc/grafana 3000:3000
# http://localhost:3000 (admin/admin)

# Dashboard pré-configuré : "Phone Book - Application Overview"

# Générer du trafic pour voir les métriques
./generate-traffic.sh
```

---

## 🐛 Problèmes Courants

### Les pods ne démarrent pas
```bash
kubectl describe pod <pod-name> -n phone-book
kubectl logs <pod-name> -n phone-book
```

### Erreur "ImagePullBackOff"
- Vérifier que les images existent sur Docker Hub
- Ou builder localement : `docker-compose build`

### MySQL ne démarre pas
- Augmenter les ressources du cluster
- Vérifier les PVC : `kubectl get pvc -n phone-book`

### Le pipeline CI/CD échoue
- Vérifier que les secrets GitHub sont configurés
- Voir [.github/CICD-SETUP-GUIDE.md](.github/CICD-SETUP-GUIDE.md)

---

## 📚 Documentation Complète

- **[README.md](README.md)** - Vue d'ensemble et architecture
- **[RAPPORT.md](RAPPORT.md)** - Rapport technique détaillé
- **[.github/CICD-SETUP-GUIDE.md](.github/CICD-SETUP-GUIDE.md)** - Configuration CI/CD

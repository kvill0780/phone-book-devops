# 🚀 Guide de Configuration CI/CD

## Prérequis

Pour activer le pipeline CI/CD complet, vous devez configurer les secrets GitHub suivants.

## 📝 Secrets GitHub à Configurer

### 1. Docker Hub (OBLIGATOIRE pour build/push)

Allez dans **Settings → Secrets and variables → Actions → New repository secret**

#### `DOCKER_USERNAME`
- **Valeur** : Votre nom d'utilisateur Docker Hub (ex: `kvill0780`)
- **Utilisation** : Login Docker Hub

#### `DOCKER_PASSWORD`
- **Valeur** : Votre token d'accès Docker Hub (PAS votre mot de passe)
- **Comment obtenir** :
  1. Allez sur https://hub.docker.com/settings/security
  2. Cliquez "New Access Token"
  3. Nom: `github-actions`
  4. Permissions: `Read, Write, Delete`
  5. Copiez le token généré

### 2. Kubernetes (OPTIONNEL pour auto-deploy)

#### `KUBE_CONFIG`
- **Valeur** : Votre fichier kubeconfig encodé en base64
- **Comment obtenir** :
  ```bash
  # Encoder votre kubeconfig
  cat ~/.kube/config | base64 -w 0
  
  # Ou pour macOS
  cat ~/.kube/config | base64
  ```
- **Note** : Si ce secret n'est pas configuré, le déploiement automatique sera ignoré

## ✅ Vérification

Une fois les secrets configurés :

1. **Vérifier les secrets** :
   - Allez dans Settings → Secrets and variables → Actions
   - Vous devriez voir : `DOCKER_USERNAME`, `DOCKER_PASSWORD`, (optionnel: `KUBE_CONFIG`)

2. **Tester le pipeline** :
   ```bash
   # Faire un commit sur main
   git add .
   git commit -m "test: trigger CI/CD pipeline"
   git push origin main
   ```

3. **Suivre l'exécution** :
   - Allez dans l'onglet "Actions" de votre repo GitHub
   - Cliquez sur le workflow "CI/CD Pipeline"
   - Vérifiez que toutes les étapes passent au vert ✅

## 🔄 Workflow du Pipeline

```
┌─────────────────────────────────────────────────────┐
│  Push sur main/develop ou Pull Request             │
└─────────────────────────────────────────────────────┘
                      ↓
        ┌─────────────────────────────┐
        │   Tests (Parallèle)         │
        │  ├─ Backend (Maven)         │
        │  └─ Frontend (npm)          │
        └─────────────────────────────┘
                      ↓
        ┌─────────────────────────────┐
        │   Build & Push (si push)    │
        │  ├─ Backend → Docker Hub    │
        │  └─ Frontend → Docker Hub   │
        └─────────────────────────────┘
                      ↓
        ┌─────────────────────────────┐
        │   Deploy (si main + secret) │
        │  └─ Kubernetes cluster      │
        └─────────────────────────────┘
```

## 🎯 Comportement du Pipeline

### Sur Pull Request
- ✅ Tests backend
- ✅ Tests frontend
- ✅ Linting
- ✅ Build Docker (sans push)
- ❌ Pas de déploiement

### Sur Push vers `develop`
- ✅ Tests backend
- ✅ Tests frontend
- ✅ Build & Push vers Docker Hub (tag: `latest`)
- ❌ Pas de déploiement

### Sur Push vers `main`
- ✅ Tests backend
- ✅ Tests frontend
- ✅ Build & Push vers Docker Hub (tags: `latest` + `git-sha`)
- ✅ Déploiement Kubernetes (si `KUBE_CONFIG` configuré)
- ✅ Vérification du déploiement

## 🐛 Troubleshooting

### Le build Docker échoue
```
Error: Cannot perform an interactive login from a non TTY device
```
**Solution** : Vérifiez que `DOCKER_USERNAME` et `DOCKER_PASSWORD` sont bien configurés

### Le déploiement Kubernetes est ignoré
```
Skipping deploy job (condition not met)
```
**Solution** : Normal si `KUBE_CONFIG` n'est pas configuré. Le pipeline build/push fonctionne quand même.

### Les tests échouent
```
Tests in error: ...
```
**Solution** : Corrigez les tests avant de push. Le pipeline bloque maintenant si les tests échouent.

## 📊 Badges de Statut

Ajoutez ces badges dans votre README.md :

```markdown
![CI/CD Pipeline](https://github.com/VOTRE-USERNAME/VOTRE-REPO/actions/workflows/ci-cd.yml/badge.svg)
![PR Checks](https://github.com/VOTRE-USERNAME/VOTRE-REPO/actions/workflows/pr-check.yml/badge.svg)
```

## 🔐 Sécurité

- ✅ Les secrets ne sont JAMAIS affichés dans les logs
- ✅ Les tokens Docker Hub ont des permissions limitées
- ✅ Le kubeconfig est encodé en base64
- ⚠️ Ne commitez JAMAIS de secrets dans le code

## 📚 Ressources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Docker Hub Access Tokens](https://docs.docker.com/docker-hub/access-tokens/)
- [Kubernetes Authentication](https://kubernetes.io/docs/reference/access-authn-authz/authentication/)

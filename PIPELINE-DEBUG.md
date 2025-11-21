# 🔍 Debug du Pipeline CI/CD

## ⚠️ Pipeline en Échec

**Dernier status** : ❌ Failure (il y a 31 minutes)

---

## 🚀 ACTIONS IMMÉDIATES

### 1. Nouveau Pipeline Lancé

Un nouveau pipeline vient d'être déclenché. Il devrait apparaître dans quelques secondes.

**Où vérifier** :
- GitHub → Actions → Workflow "CI/CD Pipeline"
- URL : https://github.com/kvill0780/phone-book-devops/actions

---

### 2. Surveiller le Pipeline

**Étapes à vérifier** :

1. ✅ **Test Backend** (Maven)
   - Doit passer en ~2 min
   - Si échec : problème dans le code Java

2. ✅ **Test Frontend** (npm)
   - Doit passer en ~1 min
   - Si échec : problème dans le code React

3. ✅ **Build Backend** (Docker)
   - Doit passer en ~3 min
   - Si échec : problème Dockerfile ou secrets

4. ✅ **Build Frontend** (Docker)
   - Doit passer en ~2 min
   - Si échec : problème Dockerfile ou secrets

5. ✅ **Push to Docker Hub**
   - Doit passer en ~1 min
   - Si échec : vérifier secrets DOCKER_USERNAME et DOCKER_PASSWORD

---

## 🐛 ERREURS COURANTES

### Erreur 1 : Tests Backend Échouent

**Symptôme** : "Tests in error" ou "BUILD FAILURE"

**Solution** :
```bash
# Tester localement
cd phone-book-backend
mvn clean test

# Si ça échoue, corriger les tests
```

---

### Erreur 2 : Tests Frontend Échouent

**Symptôme** : "npm test failed"

**Solution** :
```bash
# Tester localement
cd phone-book-frontend
npm test

# Si ça échoue, corriger les tests
```

---

### Erreur 3 : Docker Login Failed

**Symptôme** : "Error: Cannot perform an interactive login"

**Solution** :
1. Vérifier que les secrets GitHub sont bien configurés
2. Settings → Secrets → Actions
3. Vérifier `DOCKER_USERNAME` et `DOCKER_PASSWORD`

---

### Erreur 4 : Image Build Failed

**Symptôme** : "failed to solve" ou "Dockerfile not found"

**Solution** :
- Vérifier que les Dockerfiles existent
- Vérifier les chemins dans `.github/workflows/ci-cd.yml`

---

## ✅ SI LE PIPELINE PASSE

Tu verras :
- ✅ Test Backend : Success
- ✅ Test Frontend : Success
- ✅ Build Backend : Success
- ✅ Build Frontend : Success
- ✅ Push to Docker Hub : Success

**Temps total** : ~8-10 minutes

---

## 📊 PENDANT CE TEMPS

Pendant que le pipeline tourne, tu peux :

1. **Prendre les captures d'écran** :
   - http://localhost:3000 (Grafana)
   - http://localhost:9090 (Prometheus)
   - http://localhost:8000 (Frontend)

2. **Vérifier les services locaux** :
   ```bash
   docker compose ps
   ```

3. **Préparer la soutenance** :
   - Relire README.md
   - Relire RAPPORT.md

---

## 🎯 OBJECTIF

**Pipeline doit être ✅ VERT** pour valider le projet !

Si le pipeline échoue encore :
1. Clique sur le workflow
2. Regarde les logs
3. Identifie l'erreur
4. Corrige et push à nouveau

---

## 📞 AIDE

**Commandes utiles** :

```bash
# Voir le status Git
git status

# Voir les derniers commits
git log --oneline -5

# Relancer le pipeline
git commit --allow-empty -m "ci: trigger pipeline"
git push origin main

# Tester localement
docker compose up -d
docker compose ps
```

---

**Le nouveau pipeline devrait apparaître dans 1-2 minutes sur GitHub Actions !**

Surveille-le ici : https://github.com/kvill0780/phone-book-devops/actions

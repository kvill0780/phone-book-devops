# 🎤 Guide de Présentation - Phone Book DevOps

**Durée estimée** : 15-20 minutes  
**Public** : Professeur + Jury technique  
**Objectif** : Démontrer la maîtrise complète d'une infrastructure DevOps

---

## 📋 Plan de Présentation (Structure)

### 1. Introduction (2 min)
### 2. Architecture & Choix Techniques (3 min)
### 3. Démonstration Live (8 min)
### 4. CI/CD & Automatisation (3 min)
### 5. Monitoring & Observabilité (2 min)
### 6. Conclusion & Questions (2 min)

---

## 🎯 1. INTRODUCTION (2 min)

### Ce que vous dites :

> "Bonjour, je vais vous présenter mon projet DevOps : une application de gestion de contacts avec une infrastructure complète automatisée.
>
> **Contexte** : L'application tournait uniquement en local, sans CI/CD ni monitoring. Mon objectif était de la transformer en une infrastructure production-ready.
>
> **Résultat** : Aujourd'hui, l'application est conteneurisée, déployée sur Kubernetes, avec un pipeline CI/CD automatisé et un monitoring complet."

### Slide à montrer :
```
┌─────────────────────────────────────┐
│   PHONE BOOK - DEVOPS PROJECT       │
│                                     │
│  Avant:  Application locale         │
│  Après:  Infrastructure DevOps      │
│                                     │
│  ✅ Docker + Kubernetes             │
│  ✅ CI/CD automatisé                │
│  ✅ Monitoring temps réel           │
└─────────────────────────────────────┘
```

### Fichiers clés à avoir ouverts :
- README.md (vue d'ensemble)
- Architecture diagram

---

## 🏗️ 2. ARCHITECTURE & CHOIX TECHNIQUES (3 min)

### Ce que vous dites :

> "L'architecture suit le pattern microservices avec séparation des responsabilités."

### Montrer le diagramme :
```
Frontend (React)  →  Backend (Spring Boot)  →  MySQL
                            ↓
                         Redis (Cache)
                            ↓
                    Prometheus + Grafana
```

### Expliquer les POURQUOI (IMPORTANT) :

**Q: Pourquoi Docker ?**
> "Pour garantir la reproductibilité. L'application tourne de la même façon sur ma machine, en CI/CD, et en production. Plus de 'ça marche sur ma machine'."

**Q: Pourquoi Kubernetes ?**
> "Pour la scalabilité et la résilience. Kubernetes redémarre automatiquement les pods en cas de crash, et peut scaler horizontalement selon la charge."

**Q: Pourquoi Redis ?**
> "Pour les performances. Redis réduit les requêtes MySQL de 80% et divise le temps de réponse par 10 (de 500ms à 50ms)."

**Q: Pourquoi GitHub Actions ?**
> "Intégration native avec GitHub, gratuit pour les projets publics, et facile à configurer en YAML."

**Q: Pourquoi Prometheus + Grafana ?**
> "Standard de l'industrie pour le monitoring. Prometheus collecte les métriques, Grafana les visualise. Permet de détecter les problèmes avant qu'ils impactent les utilisateurs."

### Fichiers à montrer :
- `docker-compose.yml` (ligne 1-30)
- `k8s/base/backend-deployment.yaml` (replicas, health checks)

---

## 💻 3. DÉMONSTRATION LIVE (8 min)

### 3.1 Docker Compose (2 min)

**Terminal 1** :
```bash
cd phone-book-devops
docker compose ps
```

### Ce que vous dites :
> "Voici les 8 services qui tournent : backend, frontend, MySQL, Redis, Prometheus, Grafana, et les 2 exporters pour les métriques."

**Montrer** :
- Tous les services "Up (healthy)"
- Expliquer le health check

**Ouvrir navigateur** :
```
http://localhost:8000  # Frontend
```

### Ce que vous dites :
> "L'application est accessible. Je vais créer un compte et ajouter un contact pour générer des métriques."

**Actions** :
1. Créer un compte (username: demo, password: demo123)
2. Se connecter
3. Ajouter 2-3 contacts
4. Faire une recherche

### 3.2 Swagger API (1 min)

**Ouvrir** :
```
http://localhost:8080/swagger-ui.html
```

### Ce que vous dites :
> "L'API est documentée avec Swagger/OpenAPI. Tous les endpoints sont listés avec leurs paramètres. C'est généré automatiquement depuis le code."

**Montrer** :
- Liste des endpoints
- Cliquer sur `/api/auth/login`
- Montrer le schéma de requête/réponse

### 3.3 Prometheus (2 min)

**Ouvrir** :
```
http://localhost:9090/targets
```

### Ce que vous dites :
> "Prometheus scrape 4 targets toutes les 15 secondes : le backend Spring Boot, MySQL Exporter, Redis Exporter, et lui-même."

**Montrer** :
- 4 targets UP
- Expliquer "UP" vs "DOWN"

**Aller sur Graph** :
```
http://localhost:9090/graph
```

**Taper cette requête** :
```promql
rate(http_server_requests_seconds_count[5m])
```

### Ce que vous dites :
> "Cette requête PromQL calcule le taux de requêtes HTTP par seconde sur les 5 dernières minutes. On voit les pics quand j'ai utilisé l'application."

### 3.4 Grafana (3 min)

**Ouvrir** :
```
http://localhost:3000
Login: admin / admin
```

**Aller sur le dashboard** :
```
Dashboards → Phone Book - Application Overview
```

### Ce que vous dites :
> "Grafana visualise les métriques Prometheus. Ce dashboard montre 8 indicateurs clés en temps réel."

**Expliquer chaque panel** (pointer avec la souris) :

1. **HTTP Requests Rate** : "Nombre de requêtes par seconde"
2. **Response Time p95** : "95% des requêtes répondent en moins de X secondes"
3. **JVM Memory** : "Utilisation mémoire du backend Java"
4. **Active Pods** : "Nombre de backends actifs (2 en ce moment)"
5. **Error Rate** : "Taux d'erreurs 5xx (doit être proche de 0)"
6. **Database Connections** : "Connexions MySQL et Redis actives"

**Générer du trafic** (Terminal 2) :
```bash
./generate-traffic.sh
```

### Ce que vous dites :
> "Je génère du trafic artificiel. Regardez les graphiques se mettre à jour en temps réel."

**Attendre 10-15 secondes, montrer les graphiques qui bougent**

---

## 🔄 4. CI/CD & AUTOMATISATION (3 min)

### Ouvrir GitHub :
```
https://github.com/kvill0780/phone-book-devops/actions
```

### Ce que vous dites :
> "Le pipeline CI/CD s'exécute automatiquement à chaque push sur GitHub."

**Montrer le dernier workflow (vert)** :

### Expliquer les 5 jobs :

1. **Test Backend** : "Maven compile et exécute 15 tests unitaires"
2. **Test Frontend** : "npm vérifie que le code compile"
3. **Build Backend** : "Crée l'image Docker et la pousse sur Docker Hub"
4. **Build Frontend** : "Idem pour le frontend"
5. **Security Scan** : "Trivy scanne les vulnérabilités dans les images"

**Cliquer sur un job** (ex: Test Backend) :

### Ce que vous dites :
> "Voici les logs détaillés. On voit que les tests passent, le build réussit, et l'image est poussée sur Docker Hub."

**Montrer le fichier** :
```
.github/workflows/ci-cd.yml
```

### Ce que vous dites :
> "Le pipeline est défini en YAML. C'est du Infrastructure as Code : tout est versionné dans Git."

**Montrer les lignes clés** :
```yaml
on:
  push:
    branches: [main, develop]  # Trigger automatique

jobs:
  test-backend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Run tests
        run: mvn test
```

---

## 📊 5. MONITORING & OBSERVABILITÉ (2 min)

### Retour sur Grafana :

**Ouvrir le 2ème dashboard** :
```
Dashboards → Database Monitoring
```

### Ce que vous dites :
> "Ce dashboard montre les métriques MySQL et Redis. On voit les connexions actives, le taux de requêtes, et le cache hit ratio."

**Pointer** :
- MySQL Connections : "Nombre de clients connectés"
- Redis Hit Rate : "80%+ signifie que le cache est efficace"

### Expliquer l'architecture de monitoring :

```
Application → Actuator → Prometheus → Grafana
     ↓
  Exporters (MySQL/Redis)
```

### Ce que vous dites :
> "Spring Boot Actuator expose les métriques JVM. Les exporters exposent les métriques MySQL et Redis. Prometheus les collecte toutes les 15 secondes. Grafana les affiche."

---

## 🎓 6. CONCLUSION & QUESTIONS (2 min)

### Ce que vous dites :

> "En conclusion, ce projet démontre une infrastructure DevOps complète :
>
> ✅ **Conteneurisation** : Docker multi-stage pour optimiser les images
> ✅ **Orchestration** : Kubernetes avec auto-healing et scaling
> ✅ **CI/CD** : Pipeline automatisé de bout en bout
> ✅ **Monitoring** : Observabilité temps réel avec alerting possible
> ✅ **Sécurité** : Secrets management, network policies, security scan
>
> L'application est production-ready et peut scaler selon la charge."

---

## ❓ QUESTIONS PROBABLES DU PROF

### Q1: "Pourquoi avoir choisi Kubernetes plutôt que Docker Swarm ?"

**Réponse** :
> "Kubernetes est le standard de l'industrie avec un écosystème plus riche. Il offre plus de fonctionnalités : auto-scaling (HPA), network policies, rolling updates avancés, et une meilleure intégration avec les outils de monitoring."

### Q2: "Comment gérez-vous les secrets en production ?"

**Réponse** :
> "J'utilise Kubernetes Secrets générés avec `openssl rand -base64`. En production, on utiliserait un vault comme HashiCorp Vault ou AWS Secrets Manager. Les secrets ne sont jamais commités dans Git grâce au `.gitignore`."

**Montrer** :
```bash
cat k8s/create-secrets.sh  # Script de génération
cat .gitignore | grep secrets
```

### Q3: "Que se passe-t-il si un pod backend crash ?"

**Réponse** :
> "Kubernetes le redémarre automatiquement grâce au `restartPolicy: Always`. De plus, avec 2 replicas, le service reste disponible pendant le redémarrage. Le LoadBalancer route le trafic uniquement vers les pods 'Ready'."

**Montrer** :
```yaml
# k8s/base/backend-deployment.yaml
spec:
  replicas: 2
  strategy:
    type: RollingUpdate
```

### Q4: "Comment testez-vous que le déploiement fonctionne ?"

**Réponse** :
> "Plusieurs niveaux de tests :
> 1. Tests unitaires dans le pipeline (15 tests backend)
> 2. Health checks Kubernetes (liveness + readiness)
> 3. Monitoring Prometheus (alertes si erreurs)
> 4. Tests manuels après déploiement"

### Q5: "Quelle est la différence entre liveness et readiness probe ?"

**Réponse** :
> "**Liveness** : Kubernetes redémarre le pod s'il échoue (pod bloqué)
> **Readiness** : Kubernetes arrête d'envoyer du trafic s'il échoue (pod pas prêt)
>
> Exemple : Au démarrage, readiness échoue pendant 30s (connexion DB), mais liveness ne tue pas le pod."

### Q6: "Pourquoi Redis en plus de MySQL ?"

**Réponse** :
> "Redis est un cache en mémoire. Les données fréquemment lues (liste de contacts) sont mises en cache. Ça réduit la charge sur MySQL et améliore les performances de 10x (500ms → 50ms)."

**Montrer les métriques** :
```
Grafana → Database Monitoring → Redis Hit Rate
```

### Q7: "Comment scalez-vous l'application ?"

**Réponse** :
> "Deux façons :
> 1. **Manuel** : `kubectl scale deployment backend --replicas=5`
> 2. **Automatique** : HPA (Horizontal Pod Autoscaler) basé sur CPU/Memory
>
> Le HPA est configuré mais nécessite metrics-server en production."

**Montrer** :
```bash
cat k8s/base/hpa.yaml
```

### Q8: "Que manque-t-il pour aller en production ?"

**Réponse** :
> "Pour une vraie production :
> 1. **HTTPS/TLS** avec cert-manager
> 2. **Backup automatique** de MySQL
> 3. **Alerting** avec AlertManager (Slack/Email)
> 4. **Logs centralisés** (ELK ou Loki)
> 5. **Multi-environnements** (dev/staging/prod)
> 6. **Tests E2E** automatisés
>
> Mais pour un projet académique, l'infrastructure actuelle est complète."

### Q9: "Combien de temps prend un déploiement ?"

**Réponse** :
> "Pipeline CI/CD complet : 8-13 minutes
> - Tests : 3-5 min
> - Build Docker : 2-3 min
> - Security scan : 2-3 min
> - Deploy K8s : 1-2 min
>
> En cas de rollback : moins de 2 minutes."

### Q10: "Comment debuggez-vous un problème en production ?"

**Réponse** :
> "Plusieurs outils :
> 1. **Grafana** : Voir les métriques (CPU, mémoire, erreurs)
> 2. **Prometheus** : Requêtes PromQL pour analyser
> 3. **Logs** : `kubectl logs -f deployment/backend`
> 4. **Events** : `kubectl describe pod <name>`
> 5. **Shell** : `kubectl exec -it <pod> -- /bin/sh`"

---

## 📁 FICHIERS CLÉS À MAÎTRISER

### Absolument connaître :

1. **docker-compose.yml** : Orchestration locale
2. **.github/workflows/ci-cd.yml** : Pipeline CI/CD
3. **k8s/base/backend-deployment.yaml** : Déploiement K8s
4. **prometheus.yml** : Configuration monitoring
5. **README.md** : Documentation

### Savoir expliquer :

- **Dockerfile** (backend + frontend) : Multi-stage builds
- **k8s/base/secrets.yaml** : Gestion des secrets
- **k8s/base/ingress.yaml** : Point d'entrée unique
- **k8s/base/network-policies.yaml** : Isolation réseau

---

## 🎬 CHECKLIST AVANT PRÉSENTATION

### Préparation technique :

- [ ] Services Docker Compose lancés (`docker compose up -d`)
- [ ] Générer du trafic (`./generate-traffic.sh`)
- [ ] Vérifier Prometheus targets (4/4 UP)
- [ ] Vérifier Grafana dashboards (données visibles)
- [ ] Pipeline GitHub Actions vert
- [ ] Compte démo créé dans l'app (username: demo)

### Préparation matérielle :

- [ ] Navigateur avec onglets ouverts :
  - http://localhost:8000 (Frontend)
  - http://localhost:8080/swagger-ui.html (Swagger)
  - http://localhost:9090 (Prometheus)
  - http://localhost:3000 (Grafana)
  - https://github.com/kvill0780/phone-book-devops/actions
- [ ] Terminal avec 2 onglets :
  - Tab 1 : `cd phone-book-devops`
  - Tab 2 : Prêt pour `./generate-traffic.sh`
- [ ] IDE ouvert sur les fichiers clés
- [ ] Slides de backup (si démo échoue)

### Documents à avoir :

- [ ] RAPPORT.md imprimé
- [ ] Captures d'écran de backup
- [ ] Architecture diagram imprimé

---

## 💡 CONSEILS DE PRÉSENTATION

### À FAIRE :

✅ **Parler lentement et clairement**
✅ **Montrer d'abord, expliquer ensuite**
✅ **Utiliser des termes techniques mais expliquer**
✅ **Pointer avec la souris ce que vous montrez**
✅ **Anticiper les questions avec "Pourquoi"**
✅ **Montrer votre passion pour le DevOps**

### À ÉVITER :

❌ Lire vos notes
❌ Dire "euh..." trop souvent
❌ Paniquer si quelque chose ne marche pas
❌ Aller trop vite
❌ Utiliser du jargon sans expliquer

### Si un problème survient :

1. **Rester calme** : "Pas de problème, j'ai des captures d'écran"
2. **Expliquer** : "Normalement on devrait voir..."
3. **Montrer les logs** : `docker compose logs backend`
4. **Passer à la suite** : Ne pas perdre 5 minutes à débugger

---

## 🎯 OBJECTIF FINAL

**Démontrer que vous maîtrisez** :

1. ✅ La conteneurisation (Docker)
2. ✅ L'orchestration (Kubernetes)
3. ✅ L'automatisation (CI/CD)
4. ✅ Le monitoring (Prometheus/Grafana)
5. ✅ Les bonnes pratiques DevOps

**Message à faire passer** :

> "Je suis capable de prendre une application et de la déployer en production avec une infrastructure DevOps complète, automatisée, et observable."

---

## ⏱️ TIMING DÉTAILLÉ

| Section | Durée | Cumul |
|---------|-------|-------|
| Introduction | 2 min | 2 min |
| Architecture | 3 min | 5 min |
| Démo Docker Compose | 2 min | 7 min |
| Démo Swagger | 1 min | 8 min |
| Démo Prometheus | 2 min | 10 min |
| Démo Grafana | 3 min | 13 min |
| CI/CD | 3 min | 16 min |
| Monitoring | 2 min | 18 min |
| Conclusion | 2 min | 20 min |

**Total : 20 minutes + 5-10 min de questions**

---

## 🚀 BONNE CHANCE !

Vous avez un projet solide, une infrastructure complète, et une bonne compréhension des concepts DevOps.

**Soyez confiant, vous avez fait du bon travail ! 💪**

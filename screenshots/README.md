# 📸 Screenshots Grafana

Ce dossier contient les captures d'écran des dashboards Grafana pour la documentation du projet.

## 📋 Captures à Réaliser

### 1. Dashboard Application Overview
**Fichier** : `dashboard-overview.png`

**Contenu** :
- Statistiques globales (total contacts, requêtes/min, taux d'erreur)
- Graphique du trafic HTTP en temps réel
- Distribution des temps de réponse
- Status codes (pie chart)

---

### 2. Dashboard Performance
**Fichier** : `dashboard-performance.png`

**Contenu** :
- Latence P50, P95, P99 par endpoint
- Temps de réponse moyen
- Throughput (requêtes/seconde)
- Comparaison avant/après optimisations

---

### 3. Dashboard Infrastructure
**Fichier** : `dashboard-infrastructure.png`

**Contenu** :
- CPU usage par pod
- Memory usage par pod
- Network I/O
- Pods status (running/failed)
- Disk I/O

---

### 4. Dashboard Base de Données
**Fichier** : `dashboard-database.png`

**Contenu** :
- Connexions MySQL actives/idle
- Query execution time
- Redis cache hit ratio
- Cache operations/sec

---

### 5. Dashboard Sécurité
**Fichier** : `dashboard-security.png`

**Contenu** :
- Rate limiting (requêtes bloquées)
- Circuit breaker status
- Tentatives d'authentification (succès/échecs)
- JVM metrics (heap, GC)

---

## 🎯 Instructions de Capture

### Préparation
1. Démarrer l'application (Kubernetes ou Docker Compose)
2. Accéder à Grafana (http://localhost:3000)
3. Se connecter (admin/admin)
4. Importer ou créer les dashboards

### Génération de Trafic
```bash
# Lancer le script de génération de trafic
./generate-traffic.sh

# Ou manuellement
for i in {1..100}; do
  curl -X GET http://localhost:8080/api/contacts \
    -H "Authorization: Bearer $TOKEN"
  sleep 0.1
done
```

### Capture d'Écran
1. Attendre que les métriques soient visibles (2-3 minutes)
2. Ajuster la période d'affichage (Last 15 minutes)
3. Prendre une capture plein écran
4. Nommer selon la convention : `dashboard-[nom].png`
5. Ajouter annotations si nécessaire

### Format Recommandé
- **Résolution** : 1920x1080 minimum
- **Format** : PNG
- **Qualité** : Haute (pas de compression excessive)
- **Annotations** : Optionnelles mais recommandées

---

## 📝 Documentation des Captures

Pour chaque capture, créer une section dans `RAPPORT.md` :

```markdown
### Dashboard [NOM]

![Dashboard](./screenshots/dashboard-[nom].png)

**Métriques affichées** :
- [Métrique 1] : [Description]
- [Métrique 2] : [Description]

**Observations** :
- [Observation 1]
- [Observation 2]

**Interprétation** :
- [Analyse des résultats]
```

---

## ✅ Checklist

- [ ] Dashboard Overview capturé
- [ ] Dashboard Performance capturé
- [ ] Dashboard Infrastructure capturé
- [ ] Dashboard Database capturé
- [ ] Dashboard Security capturé
- [ ] Captures annotées si nécessaire
- [ ] Documentation ajoutée au RAPPORT.md
- [ ] Fichiers nommés correctement
- [ ] Résolution suffisante (1920x1080+)
- [ ] Métriques visibles et lisibles

---

**Note** : Les captures doivent montrer des données réelles, pas des dashboards vides. Générer du trafic avant de capturer !

# 📊 Grafana Dashboards JSON

Ce dossier contient les définitions JSON des dashboards Grafana pour le projet Phone Book.

## 📦 Dashboards Disponibles

### 1. application-overview.json
**Description** : Vue d'ensemble de l'application avec métriques clés

**Panels** :
- Total contacts (stat)
- Requêtes par minute (time series)
- Taux d'erreur (gauge)
- Temps de réponse P95 (time series)
- Distribution des status codes (pie chart)

**Import** :
```bash
# Via Grafana UI
Dashboards → Import → Upload JSON file

# Via API
curl -X POST http://localhost:3000/api/dashboards/db \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $GRAFANA_API_KEY" \
  -d @application-overview.json
```

---

### 2. infrastructure-monitoring.json
**Description** : Monitoring de l'infrastructure Kubernetes

**Panels** :
- CPU usage par pod
- Memory usage par pod
- Network I/O
- Pods status
- Disk usage

---

### 3. database-metrics.json
**Description** : Métriques MySQL et Redis

**Panels** :
- Connexions MySQL (actives/idle/max)
- Query execution time
- Redis cache hit ratio
- Cache operations/sec
- Connection pool metrics

---

### 4. security-dashboard.json
**Description** : Métriques de sécurité et performance

**Panels** :
- Rate limiting (blocked requests)
- Circuit breaker status
- Authentication attempts
- JVM heap memory
- Garbage collection time

---

## 🚀 Import Rapide

### Méthode 1 : Via Grafana UI
1. Ouvrir Grafana (http://localhost:3000)
2. Aller dans **Dashboards** → **Import**
3. Cliquer **Upload JSON file**
4. Sélectionner le fichier .json
5. Choisir le datasource **Prometheus**
6. Cliquer **Import**

### Méthode 2 : Via API
```bash
# Définir l'API key
export GRAFANA_API_KEY="your-api-key"

# Importer tous les dashboards
for file in *.json; do
  curl -X POST http://localhost:3000/api/dashboards/db \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $GRAFANA_API_KEY" \
    -d @$file
done
```

### Méthode 3 : Provisioning (Recommandé pour Production)
```yaml
# grafana-provisioning.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: grafana-dashboards
  namespace: phone-book
data:
  application-overview.json: |
    {{ .Files.Get "grafana-dashboards/application-overview.json" | indent 4 }}
```

---

## 🛠️ Création de Dashboards Personnalisés

### Template de Base
```json
{
  "dashboard": {
    "title": "Mon Dashboard",
    "tags": ["phone-book"],
    "timezone": "browser",
    "schemaVersion": 16,
    "version": 1,
    "refresh": "30s",
    "panels": []
  }
}
```

### Ajouter un Panel
```json
{
  "id": 1,
  "title": "Total Contacts",
  "type": "stat",
  "gridPos": { "h": 8, "w": 6, "x": 0, "y": 0 },
  "targets": [
    {
      "expr": "sum(contacts_created_total) - sum(contacts_deleted_total)",
      "refId": "A"
    }
  ],
  "options": {
    "colorMode": "value",
    "graphMode": "area",
    "orientation": "auto"
  }
}
```

---

## 📊 Dashboards Communautaires Recommandés

### Spring Boot Statistics (ID: 4701)
```bash
# Import via ID
Dashboards → Import → ID: 4701
```

**Métriques** :
- JVM memory
- HTTP requests
- Thread pools
- Logback events
- Database connections

### Kubernetes Cluster Monitoring (ID: 315)
```bash
# Import via ID
Dashboards → Import → ID: 315
```

**Métriques** :
- Cluster resources
- Node status
- Pod metrics
- Network traffic

### MySQL Overview (ID: 7362)
```bash
# Import via ID
Dashboards → Import → ID: 7362
```

**Métriques** :
- Connections
- Queries per second
- InnoDB metrics
- Replication status

---

## 🔧 Configuration du Datasource

### Prometheus Datasource
```yaml
apiVersion: 1
datasources:
  - name: Prometheus
    type: prometheus
    access: proxy
    url: http://prometheus:9090
    isDefault: true
    editable: true
```

### Variables de Dashboard
```json
{
  "templating": {
    "list": [
      {
        "name": "namespace",
        "type": "query",
        "datasource": "Prometheus",
        "query": "label_values(kube_pod_info, namespace)",
        "refresh": 1
      },
      {
        "name": "pod",
        "type": "query",
        "datasource": "Prometheus",
        "query": "label_values(kube_pod_info{namespace=\"$namespace\"}, pod)",
        "refresh": 1
      }
    ]
  }
}
```

---

## 📝 Export des Dashboards

### Via Grafana UI
1. Ouvrir le dashboard
2. Cliquer sur l'icône **Share**
3. Onglet **Export**
4. Cocher **Export for sharing externally**
5. Cliquer **Save to file**

### Via API
```bash
# Lister tous les dashboards
curl -H "Authorization: Bearer $GRAFANA_API_KEY" \
  http://localhost:3000/api/search?type=dash-db

# Export d'un dashboard spécifique
curl -H "Authorization: Bearer $GRAFANA_API_KEY" \
  http://localhost:3000/api/dashboards/uid/[dashboard-uid] \
  | jq '.dashboard' > dashboard-export.json
```

---

## 🎨 Personnalisation

### Thèmes
- **Dark** (par défaut)
- **Light**

### Refresh Rates
- 5s (monitoring en temps réel)
- 30s (recommandé)
- 1m (vue d'ensemble)
- 5m (analyse historique)

### Time Ranges
- Last 5 minutes
- Last 15 minutes (recommandé pour démo)
- Last 1 hour
- Last 24 hours
- Custom range

---

## ✅ Checklist

- [ ] Datasource Prometheus configuré
- [ ] Dashboard Application Overview créé
- [ ] Dashboard Infrastructure créé
- [ ] Dashboard Database créé
- [ ] Dashboard Security créé
- [ ] Variables de dashboard configurées
- [ ] Refresh rate configuré (30s)
- [ ] Dashboards exportés en JSON
- [ ] Documentation des panels rédigée
- [ ] Tests avec données réelles effectués

---

## 📚 Ressources

- [Grafana Dashboard Best Practices](https://grafana.com/docs/grafana/latest/best-practices/)
- [PromQL Cheat Sheet](https://promlabs.com/promql-cheat-sheet/)
- [Grafana Dashboard Examples](https://grafana.com/grafana/dashboards/)

---

**Note** : Les dashboards JSON seront ajoutés au fur et à mesure de leur création. Commencer par importer les dashboards communautaires, puis créer des dashboards personnalisés selon les besoins.

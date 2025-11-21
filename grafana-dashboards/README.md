# 📊 Dashboards Grafana

## Dashboard Pré-Configuré

Le dashboard **"Phone Book - Application Overview"** est automatiquement provisionné au démarrage de Grafana.

### Accès

```bash
# Kubernetes
kubectl port-forward -n phone-book svc/grafana 3000:3000

# Docker Compose
# Grafana déjà accessible sur http://localhost:3000

# Login : admin / admin
```

### Contenu du Dashboard

1. **HTTP Requests Rate** - Nombre de requêtes par seconde
2. **HTTP Response Time (p95)** - Temps de réponse 95e percentile
3. **JVM Memory Usage** - Utilisation mémoire du backend
4. **Active Backend Pods** - Nombre de pods actifs
5. **Error Rate (5xx)** - Taux d'erreurs serveur
6. **Database Connections** - Connexions MySQL et Redis

### Générer du Trafic

Pour voir des données dans le dashboard :

```bash
./generate-traffic.sh
```

## Configuration Technique

Les dashboards sont provisionnés via ConfigMaps Kubernetes :

- `k8s/base/grafana-datasources-configmap.yaml` - Datasource Prometheus
- `k8s/base/grafana-dashboards-configmap.yaml` - Dashboard JSON

Pas de configuration manuelle nécessaire ! 🎉

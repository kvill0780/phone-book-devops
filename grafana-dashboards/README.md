# 📊 Grafana Dashboards - Import Guide

Ce dossier contient 3 dashboards Grafana prêts à importer.

## 📁 Dashboards Disponibles

### 1. phone-book-overview.json
**Vue d'ensemble de l'application**

Panels :
- HTTP Requests Rate
- Response Time (p95)
- JVM Memory Usage
- Active Backend Pods
- Error Rate (5xx)
- Database Connections
- JVM Threads
- Redis Cache Hit Ratio

### 2. database-monitoring.json
**Monitoring MySQL et Redis**

Panels :
- MySQL Connections
- MySQL Queries Rate
- MySQL Uptime
- MySQL Bytes Sent
- Redis Connected Clients
- Redis Memory Usage
- Redis Operations Rate
- Redis Keyspace
- Redis Hit Rate

### 3. jvm-performance.json
**Performance JVM détaillée**

Panels :
- Heap Memory Usage
- Non-Heap Memory Usage
- GC Pause Time
- GC Count
- Thread Count
- Thread States
- Classes Loaded
- CPU Usage
- System CPU Usage
- System Load Average

## 📥 Import dans Grafana

### Méthode 1 : Via l'interface Web

1. Ouvrir Grafana : http://localhost:3000
2. Login : admin / admin
3. Menu (☰) → Dashboards → Import
4. Cliquer sur "Upload JSON file"
5. Sélectionner un fichier .json
6. Cliquer sur "Import"

### Méthode 2 : Via API

```bash
# Dashboard 1 : Overview
curl -X POST http://admin:admin@localhost:3000/api/dashboards/db \
  -H "Content-Type: application/json" \
  -d @grafana-dashboards/phone-book-overview.json

# Dashboard 2 : Database
curl -X POST http://admin:admin@localhost:3000/api/dashboards/db \
  -H "Content-Type: application/json" \
  -d @grafana-dashboards/database-monitoring.json

# Dashboard 3 : JVM
curl -X POST http://admin:admin@localhost:3000/api/dashboards/db \
  -H "Content-Type: application/json" \
  -d @grafana-dashboards/jvm-performance.json
```

### Méthode 3 : Import automatique (Docker Compose)

Les dashboards sont déjà auto-provisionnés via ConfigMap dans Kubernetes.

Pour Docker Compose, ils sont montés automatiquement au démarrage.

## ✅ Vérification

Après import :
1. Menu (☰) → Dashboards → Browse
2. Vérifier que les 3 dashboards apparaissent
3. Ouvrir chaque dashboard
4. Vérifier que les données s'affichent

## 🧪 Générer des Données

Pour voir des métriques dans les dashboards :

```bash
# Générer du trafic
./generate-traffic.sh

# Ou manuellement
for i in {1..100}; do
  curl http://localhost:8080/actuator/health
  sleep 0.1
done
```

## 🎨 Personnalisation

Vous pouvez modifier les dashboards :
1. Ouvrir le dashboard
2. Cliquer sur ⚙️ (Settings) en haut à droite
3. Modifier les panels
4. Sauvegarder

Pour exporter après modification :
1. Dashboard Settings → JSON Model
2. Copier le JSON
3. Sauvegarder dans un fichier

## 📊 Variables Disponibles

Les dashboards utilisent ces variables :
- `$job` : Job Prometheus (spring-boot, mysql-exporter, redis-exporter)
- `$instance` : Instance du service

## 🔗 Datasource

Tous les dashboards utilisent la datasource "Prometheus" configurée automatiquement.

Si besoin de reconfigurer :
- Configuration → Data sources → Prometheus
- URL : http://prometheus:9090

## 📸 Captures d'Écran

Pour la présentation, prendre des screenshots de :
1. phone-book-overview.json (vue principale)
2. database-monitoring.json (métriques DB)
3. Un panel en détail avec données réelles

## 🐛 Troubleshooting

**Dashboard vide / pas de données** :
```bash
# Vérifier Prometheus
curl http://localhost:9090/api/v1/targets

# Vérifier que le backend expose des métriques
curl http://localhost:8080/actuator/prometheus

# Générer du trafic
./generate-traffic.sh
```

**Erreur "Data source not found"** :
- Vérifier que Prometheus est configuré dans Grafana
- Configuration → Data sources → Add Prometheus
- URL : http://prometheus:9090

**Panels en erreur** :
- Vérifier les requêtes PromQL dans le panel
- Tester la requête dans Prometheus : http://localhost:9090/graph

# 📊 Guide Monitoring - Grafana + Prometheus

## 🚀 Démarrage Rapide

```bash
# 1. Démarrer les services
docker compose up -d

# 2. Attendre 30 secondes
sleep 30

# 3. Générer du trafic (optionnel)
./generate-traffic.sh
```

## 📍 URLs d'Accès

| Service | URL | Credentials |
|---------|-----|-------------|
| Prometheus | http://localhost:9090 | - |
| Grafana | http://localhost:3000 | admin / admin |
| Backend Metrics | http://localhost:8080/actuator/prometheus | - |

## ✅ Vérification Prometheus

### 1. Vérifier les Targets

**URL** : http://localhost:9090/targets

**Attendu** : 4 targets UP
- `spring-boot` (backend:8080)
- `mysql-exporter` (mysql-exporter:9104)
- `redis-exporter` (redis-exporter:9121)
- `prometheus` (localhost:9090)

### 2. Tester des Requêtes

**URL** : http://localhost:9090/graph

**Requêtes à tester** :

```promql
# Taux de requêtes HTTP
rate(http_server_requests_seconds_count[5m])

# Temps de réponse p95
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))

# Utilisation mémoire JVM
jvm_memory_used_bytes{area="heap"}

# Connexions MySQL actives
mysql_global_status_threads_connected

# Cache hit ratio Redis
redis_keyspace_hits_total / (redis_keyspace_hits_total + redis_keyspace_misses_total)
```

### 3. Vérifier la Configuration

**URL** : http://localhost:9090/config

Vérifier que les 4 jobs sont configurés :
- `spring-boot`
- `mysql-exporter`
- `redis-exporter`
- `prometheus`

## 📊 Vérification Grafana

### 1. Connexion

1. Ouvrir http://localhost:3000
2. Login : `admin`
3. Password : `admin`
4. (Optionnel) Changer le mot de passe ou skip

### 2. Vérifier la Datasource

**Navigation** : Configuration (⚙️) → Data sources

**Attendu** :
- ✅ Datasource "Prometheus" configurée
- ✅ URL : http://prometheus:9090
- ✅ Status : Working (bouton "Test")

**Test** :
```bash
# Cliquer sur "Test" en bas de la page
# Devrait afficher : "Data source is working"
```

### 3. Vérifier le Dashboard

**Navigation** : Dashboards (☰) → Browse

**Attendu** :
- ✅ Dashboard "Phone Book - Application Overview"

**Ouvrir le dashboard** :
1. Cliquer sur "Phone Book - Application Overview"
2. Vérifier les 6 panneaux :
   - HTTP Requests Rate
   - Response Time (p95)
   - JVM Memory Usage
   - Active Pods
   - Error Rate
   - Database Connections

### 4. Panels du Dashboard

#### Panel 1 : HTTP Requests Rate
```promql
rate(http_server_requests_seconds_count[5m])
```
**Attendu** : Graphique avec taux de requêtes/seconde

#### Panel 2 : Response Time (p95)
```promql
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))
```
**Attendu** : Temps de réponse en secondes

#### Panel 3 : JVM Memory Usage
```promql
jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"} * 100
```
**Attendu** : Pourcentage d'utilisation mémoire

#### Panel 4 : Active Pods
```promql
count(up{job="spring-boot"} == 1)
```
**Attendu** : Nombre de pods backend actifs

#### Panel 5 : Error Rate
```promql
rate(http_server_requests_seconds_count{status=~"5.."}[5m])
```
**Attendu** : Taux d'erreurs 5xx

#### Panel 6 : Database Connections
```promql
mysql_global_status_threads_connected
redis_connected_clients
```
**Attendu** : Connexions MySQL et Redis

## 🧪 Générer des Métriques

### Option 1 : Script Automatique

```bash
# Générer du trafic pendant 60 secondes
./generate-traffic.sh

# Personnaliser
DURATION=120 RPS=10 ./generate-traffic.sh
```

### Option 2 : Requêtes Manuelles

```bash
# Health check
curl http://localhost:8080/actuator/health

# Métriques Prometheus
curl http://localhost:8080/actuator/prometheus

# API (génère des erreurs 401 mais crée des métriques)
for i in {1..100}; do
  curl http://localhost:8080/api/contacts
  sleep 0.1
done
```

### Option 3 : Utiliser l'Application

1. Ouvrir http://localhost:8000
2. Créer un compte
3. Se connecter
4. Ajouter des contacts
5. Faire des recherches
6. Voir les métriques dans Grafana

## 📸 Captures d'Écran Requises

### Pour Prometheus

1. **Targets** : http://localhost:9090/targets
   - Montrer les 4 targets UP

2. **Graph** : http://localhost:9090/graph
   - Requête : `rate(http_server_requests_seconds_count[5m])`
   - Montrer le graphique avec données

### Pour Grafana

1. **Datasources** : Configuration → Data sources
   - Montrer "Prometheus" avec status "Working"

2. **Dashboard** : Phone Book - Application Overview
   - Montrer les 6 panels avec données réelles
   - Zoom sur un panel intéressant

3. **Explore** : Explore (🧭)
   - Tester une requête PromQL
   - Montrer les résultats

## 🐛 Troubleshooting

### Prometheus ne scrape pas les targets

```bash
# Vérifier que les services sont UP
docker compose ps

# Vérifier les logs Prometheus
docker compose logs prometheus

# Vérifier la config Prometheus
curl http://localhost:9090/api/v1/status/config
```

### Grafana ne se connecte pas à Prometheus

```bash
# Vérifier que Prometheus est accessible depuis Grafana
docker compose exec grafana curl http://prometheus:9090/api/v1/query?query=up

# Recréer la datasource si nécessaire
# Configuration → Data sources → Add data source → Prometheus
# URL: http://prometheus:9090
```

### Pas de données dans les dashboards

```bash
# 1. Vérifier que le backend est UP
curl http://localhost:8080/actuator/health

# 2. Générer du trafic
./generate-traffic.sh

# 3. Attendre 15-30 secondes (scrape interval)

# 4. Rafraîchir Grafana
```

### Dashboard non visible

```bash
# Vérifier les ConfigMaps
docker compose exec grafana ls -la /etc/grafana/provisioning/dashboards/

# Redémarrer Grafana
docker compose restart grafana
```

## 📊 Métriques Disponibles

### Backend (Spring Boot Actuator)

```
http_server_requests_seconds_count
http_server_requests_seconds_sum
jvm_memory_used_bytes
jvm_memory_max_bytes
jvm_threads_live
auth_login_attempts_total
contacts_created_total
```

### MySQL Exporter

```
mysql_global_status_threads_connected
mysql_global_status_queries
mysql_global_status_slow_queries
mysql_global_status_uptime
```

### Redis Exporter

```
redis_connected_clients
redis_keyspace_hits_total
redis_keyspace_misses_total
redis_memory_used_bytes
```

## 🎯 Objectifs de Vérification

- [ ] Prometheus accessible (http://localhost:9090)
- [ ] 4 targets UP dans Prometheus
- [ ] Grafana accessible (http://localhost:3000)
- [ ] Datasource Prometheus configurée
- [ ] Dashboard "Phone Book" visible
- [ ] 6 panels affichent des données
- [ ] Trafic généré avec script
- [ ] Captures d'écran prises

## 📚 Ressources

- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
- [PromQL Cheat Sheet](https://promlabs.com/promql-cheat-sheet/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)

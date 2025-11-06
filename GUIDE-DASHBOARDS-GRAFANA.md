# 📊 Guide des Dashboards Grafana

## 🎯 Objectif

Ce guide vous aide à créer, configurer et documenter les dashboards Grafana pour le projet Phone Book.

---

## 🚀 Accès à Grafana

### Méthode 1 : Via Ingress (Production)
```bash
# Si ingress configuré avec DNS
https://grafana.votre-domaine.com
```

### Méthode 2 : Via Port-Forward (Local/Dev)
```bash
# Kubernetes
kubectl port-forward -n phone-book svc/grafana 3000:3000

# Docker Compose
# Grafana déjà exposé sur http://localhost:3000
```

### Méthode 3 : Via Minikube
```bash
minikube service grafana -n phone-book
```

**Credentials par défaut** :
- Username: `admin`
- Password: `admin`

---

## 📋 Dashboards Recommandés

### 1. 🖥️ Dashboard Application Overview

**Métriques à afficher** :

#### Panel 1 : Statistiques Globales (Stat)
- **Total Contacts** : `sum(contacts_created_total) - sum(contacts_deleted_total)`
- **Requêtes/min** : `rate(http_server_requests_seconds_count[1m])`
- **Taux d'erreur** : `rate(http_server_requests_seconds_count{status=~"5.."}[5m])`
- **Uptime Backend** : `process_uptime_seconds`

#### Panel 2 : Trafic HTTP (Time Series)
```promql
# Requêtes par endpoint
sum(rate(http_server_requests_seconds_count[5m])) by (uri)
```

#### Panel 3 : Temps de Réponse (Time Series)
```promql
# P95 latency
histogram_quantile(0.95, 
  sum(rate(http_server_requests_seconds_bucket[5m])) by (le, uri)
)
```

#### Panel 4 : Status Codes (Pie Chart)
```promql
sum(http_server_requests_seconds_count) by (status)
```

---

### 2. 💾 Dashboard Base de Données

#### Panel 1 : Connexions MySQL
```promql
# Connexions actives
hikaricp_connections_active{pool="HikariPool-1"}

# Connexions en attente
hikaricp_connections_pending{pool="HikariPool-1"}
```

#### Panel 2 : Queries Performance
```promql
# Temps d'exécution des queries
rate(hikaricp_connections_usage_seconds_sum[5m]) / 
rate(hikaricp_connections_usage_seconds_count[5m])
```

#### Panel 3 : Cache Redis
```promql
# Cache hits
cache_gets{result="hit"}

# Cache misses
cache_gets{result="miss"}

# Hit ratio
cache_gets{result="hit"} / (cache_gets{result="hit"} + cache_gets{result="miss"})
```

---

### 3. 🔧 Dashboard Infrastructure

#### Panel 1 : CPU Usage (Gauge)
```promql
# CPU par pod
sum(rate(container_cpu_usage_seconds_total{namespace="phone-book"}[5m])) by (pod)
```

#### Panel 2 : Memory Usage (Time Series)
```promql
# Mémoire utilisée par pod
container_memory_working_set_bytes{namespace="phone-book"} / 1024 / 1024
```

#### Panel 3 : Pods Status (Stat)
```promql
# Pods running
count(kube_pod_status_phase{namespace="phone-book", phase="Running"})

# Pods failed
count(kube_pod_status_phase{namespace="phone-book", phase="Failed"})
```

#### Panel 4 : Network I/O (Time Series)
```promql
# Network in
rate(container_network_receive_bytes_total{namespace="phone-book"}[5m])

# Network out
rate(container_network_transmit_bytes_total{namespace="phone-book"}[5m])
```

---

### 4. 🔐 Dashboard Sécurité & Performance

#### Panel 1 : Rate Limiting
```promql
# Requêtes bloquées par rate limiting
rate(rate_limit_blocked_total[5m])
```

#### Panel 2 : Circuit Breaker
```promql
# État du circuit breaker
resilience4j_circuitbreaker_state{name="contactService"}

# Appels échoués
rate(resilience4j_circuitbreaker_calls_seconds_count{kind="failed"}[5m])
```

#### Panel 3 : Authentification
```promql
# Tentatives de login
rate(auth_attempts_total[5m])

# Échecs d'authentification
rate(auth_failures_total[5m])
```

#### Panel 4 : JVM Metrics
```promql
# Heap memory
jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"} * 100

# GC time
rate(jvm_gc_pause_seconds_sum[5m])
```

---

## 🎨 Configuration des Dashboards

### Import de Dashboards Pré-configurés

#### Dashboard Spring Boot
1. Aller dans **Dashboards** → **Import**
2. Utiliser l'ID : **4701** (Spring Boot Statistics)
3. Sélectionner datasource : **Prometheus**
4. Cliquer **Import**

#### Dashboard Kubernetes
1. **Dashboards** → **Import**
2. ID : **315** (Kubernetes cluster monitoring)
3. Datasource : **Prometheus**
4. **Import**

#### Dashboard MySQL
1. **Dashboards** → **Import**
2. ID : **7362** (MySQL Overview)
3. Datasource : **Prometheus**
4. **Import**

---

## 📸 Captures d'Écran à Réaliser

### Checklist des captures

- [ ] **Dashboard Overview** - Vue d'ensemble de l'application
  - Afficher trafic en temps réel
  - Métriques clés (contacts, requêtes, erreurs)
  
- [ ] **Dashboard Performance** - Temps de réponse et latence
  - Graphiques de latence P50, P95, P99
  - Distribution des temps de réponse
  
- [ ] **Dashboard Infrastructure** - État du cluster
  - CPU/Memory par pod
  - Status des déploiements
  - Network I/O
  
- [ ] **Dashboard Base de Données** - Métriques MySQL + Redis
  - Connexions actives
  - Cache hit ratio
  - Query performance
  
- [ ] **Dashboard Sécurité** - Rate limiting, circuit breaker
  - Requêtes bloquées
  - État circuit breaker
  - Tentatives d'authentification

---

## 📝 Template de Documentation des Captures

Pour chaque capture d'écran, documenter :

```markdown
### Dashboard : [NOM DU DASHBOARD]

![Screenshot](./screenshots/dashboard-[nom].png)

**Description** : [Brève description de ce que montre le dashboard]

**Métriques affichées** :
1. **[Nom métrique 1]** : [Explication]
   - Valeur normale : [plage]
   - Alerte si : [condition]

2. **[Nom métrique 2]** : [Explication]
   - Valeur normale : [plage]
   - Alerte si : [condition]

**Interprétation** :
- ✅ **Vert** : [Signification]
- ⚠️ **Orange** : [Signification]
- 🔴 **Rouge** : [Signification]

**Actions recommandées** :
- Si [condition] → [action]
- Si [condition] → [action]
```

---

## 🔔 Configuration des Alertes

### Alerte 1 : High Error Rate
```yaml
Alert: HighErrorRate
Expression: rate(http_server_requests_seconds_count{status=~"5.."}[5m]) > 0.05
Duration: 5m
Severity: critical
Message: "Taux d'erreur élevé : {{ $value }}%"
```

### Alerte 2 : High Memory Usage
```yaml
Alert: HighMemoryUsage
Expression: container_memory_working_set_bytes / container_spec_memory_limit_bytes > 0.9
Duration: 5m
Severity: warning
Message: "Utilisation mémoire élevée sur {{ $labels.pod }}"
```

### Alerte 3 : Pod Down
```yaml
Alert: PodDown
Expression: kube_pod_status_phase{phase="Running"} == 0
Duration: 1m
Severity: critical
Message: "Pod {{ $labels.pod }} est down"
```

---

## 🛠️ Commandes Utiles

### Générer du Trafic pour Tests
```bash
# Script de génération de trafic
./generate-traffic.sh

# Ou manuellement
for i in {1..100}; do
  curl -X GET http://localhost:8080/api/contacts \
    -H "Authorization: Bearer $TOKEN"
  sleep 0.1
done
```

### Vérifier les Métriques Prometheus
```bash
# Port-forward Prometheus
kubectl port-forward -n phone-book svc/prometheus 9090:9090

# Accéder à http://localhost:9090
# Tester les queries PromQL
```

### Export des Dashboards
```bash
# Via API Grafana
curl -H "Authorization: Bearer $GRAFANA_API_KEY" \
  http://localhost:3000/api/dashboards/uid/[dashboard-uid] \
  > dashboard-backup.json
```

---

## 📦 Dashboards JSON à Créer

Créer un dossier `grafana-dashboards/` avec :

```
grafana-dashboards/
├── application-overview.json
├── infrastructure-monitoring.json
├── database-metrics.json
├── security-dashboard.json
└── README.md
```

### Template de Dashboard JSON
```json
{
  "dashboard": {
    "title": "Phone Book - Application Overview",
    "tags": ["phone-book", "application"],
    "timezone": "browser",
    "panels": [
      {
        "title": "Total Contacts",
        "type": "stat",
        "targets": [
          {
            "expr": "sum(contacts_created_total) - sum(contacts_deleted_total)"
          }
        ]
      }
    ]
  }
}
```

---

## 🎓 Ressources Complémentaires

### Documentation Officielle
- [Grafana Dashboards](https://grafana.com/docs/grafana/latest/dashboards/)
- [PromQL Queries](https://prometheus.io/docs/prometheus/latest/querying/basics/)
- [Spring Boot Metrics](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.metrics)

### Dashboards Communautaires
- [Grafana Dashboard Library](https://grafana.com/grafana/dashboards/)
- [Spring Boot Dashboard 4701](https://grafana.com/grafana/dashboards/4701)
- [Kubernetes Dashboard 315](https://grafana.com/grafana/dashboards/315)

---

## ✅ Checklist Finale

Avant de considérer le monitoring comme complet :

- [ ] Grafana accessible et fonctionnel
- [ ] Datasource Prometheus configuré
- [ ] Au moins 3 dashboards créés
- [ ] Captures d'écran réalisées (5 minimum)
- [ ] Documentation des métriques rédigée
- [ ] Alertes configurées (3 minimum)
- [ ] Dashboards exportés en JSON
- [ ] Guide d'interprétation rédigé
- [ ] Tests de charge effectués
- [ ] Validation avec l'équipe

---

**Prochaine étape** : Suivre ce guide pour créer et documenter vos dashboards Grafana ! 📊

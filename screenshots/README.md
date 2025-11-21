# 📸 Captures d'Écran pour le Rapport

## Captures Requises

### 1. Docker Compose ✅
- [ ] `docker-compose ps` - tous les services "Up (healthy)"
- [ ] Frontend dans le navigateur (http://localhost:8000)
- [ ] Backend health check (http://localhost:8080/actuator/health)

### 2. Kubernetes ✅
- [ ] `kubectl get pods -n phone-book` - tous "Running"
- [ ] `kubectl get svc -n phone-book`
- [ ] Application accessible (via Ingress ou port-forward)

### 3. GitHub Actions ✅
- [ ] Pipeline CI/CD passé au vert (onglet Actions)
- [ ] Détail d'un workflow : tests, build, push

### 4. Grafana ✅
- [ ] Dashboard "Phone Book - Application Overview"
- [ ] Graphiques avec données réelles :
  - HTTP Requests Rate
  - Response Time (p95)
  - JVM Memory Usage
  - Active Pods
  - Error Rate
  - Database Connections
- [ ] Liste des datasources (Prometheus)

### 5. Prometheus ✅
- [ ] Page "Status → Targets" - tous "UP"
- [ ] Exemple de requête avec résultats

## Comment Prendre les Captures

### Préparer l'environnement
```bash
# 1. Lancer l'application
docker-compose up -d
# OU
cd k8s && ./deploy.sh

# 2. Générer du trafic
./generate-traffic.sh

# 3. Accéder à Grafana
kubectl port-forward -n phone-book svc/grafana 3000:3000
# http://localhost:3000 (admin/admin)
```

### Nommage des fichiers
```
01-docker-compose-ps.png
02-frontend-browser.png
03-kubectl-get-pods.png
04-kubectl-get-svc.png
05-github-actions-pipeline.png
06-grafana-dashboard-overview.png
07-grafana-http-requests.png
08-grafana-response-time.png
09-grafana-jvm-memory.png
10-grafana-database-connections.png
11-prometheus-targets.png
12-prometheus-query.png
```

## Intégration dans le Rapport

Dans `RAPPORT.md`, section Annexes :

```markdown
### Captures d'écran

#### Docker Compose
![Docker Compose Services](screenshots/01-docker-compose-ps.png)

#### Kubernetes
![Kubernetes Pods](screenshots/03-kubectl-get-pods.png)

#### Grafana Dashboard
![Grafana Overview](screenshots/06-grafana-dashboard-overview.png)

#### Prometheus Targets
![Prometheus Targets](screenshots/11-prometheus-targets.png)
```

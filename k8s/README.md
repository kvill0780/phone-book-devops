# Kubernetes Deployment Guide

## Quick Deploy

```bash
cd k8s
./create-secrets.sh  # Generate secure secrets
./deploy.sh          # Deploy all components
```

## Prerequisites

- Kubernetes cluster (minikube, kind, or cloud)
- kubectl configured
- Ingress controller (for minikube: `minikube addons enable ingress`)
- **Optional**: metrics-server (for HPA autoscaling)

## Install metrics-server (Optional)

For HPA (Horizontal Pod Autoscaler) to work:

```bash
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml

# For minikube, add --kubelet-insecure-tls flag:
kubectl patch deployment metrics-server -n kube-system --type='json' \
  -p='[{"op": "add", "path": "/spec/template/spec/containers/0/args/-", "value": "--kubelet-insecure-tls"}]'
```

Verify:
```bash
kubectl get apiservice v1beta1.metrics.k8s.io
kubectl top nodes
```

## Network Policies

Network policies are applied by default to enforce least privilege:

- Frontend → Backend only
- Backend → MySQL + Redis only
- Prometheus → All exporters (read-only)
- Grafana → Prometheus only

To disable (for debugging):
```bash
kubectl delete networkpolicies --all -n phone-book
```

## Troubleshooting

### Pods not starting
```bash
kubectl get pods -n phone-book
kubectl describe pod <pod-name> -n phone-book
kubectl logs <pod-name> -n phone-book
```

### HPA not working
```bash
# Check metrics-server
kubectl get apiservice v1beta1.metrics.k8s.io
kubectl top pods -n phone-book

# If not installed, see "Install metrics-server" above
```

### Network connectivity issues
```bash
# Temporarily disable network policies
kubectl delete networkpolicies --all -n phone-book

# Test connectivity
kubectl exec -it <backend-pod> -n phone-book -- curl mysql:3306
```

## Manual Deployment

If you prefer step-by-step:

```bash
kubectl apply -f base/namespace.yaml
kubectl apply -f base/secrets.yaml
kubectl apply -f base/configmap.yaml
kubectl apply -f base/mysql-deployment.yaml
kubectl apply -f base/redis-deployment.yaml
kubectl wait --for=condition=ready pod -l app=mysql -n phone-book --timeout=180s
kubectl apply -f base/backend-deployment.yaml
kubectl apply -f base/frontend-deployment.yaml
kubectl apply -f base/prometheus-deployment.yaml
kubectl apply -f base/grafana-datasources-configmap.yaml
kubectl apply -f base/grafana-dashboards-configmap.yaml
kubectl apply -f base/grafana-deployment.yaml
kubectl apply -f base/mysql-exporter-deployment.yaml
kubectl apply -f base/redis-exporter-deployment.yaml
kubectl apply -f base/ingress.yaml
kubectl apply -f base/hpa.yaml  # Only if metrics-server installed
kubectl apply -f base/network-policies.yaml  # Optional
```

## Scaling

```bash
# Manual scaling
kubectl scale deployment backend --replicas=5 -n phone-book

# Auto-scaling (requires metrics-server)
kubectl autoscale deployment backend --cpu-percent=70 --min=2 --max=10 -n phone-book
```

## Cleanup

```bash
kubectl delete namespace phone-book
```

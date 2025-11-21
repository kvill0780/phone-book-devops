#!/bin/bash
set -e

echo "🚀 Deploying Phone Book Application to Kubernetes"
echo "=================================================="

# Check if kubectl is available
if ! command -v kubectl &> /dev/null; then
    echo "❌ kubectl not found. Please install kubectl first."
    exit 1
fi

# Apply manifests in order
echo "📦 Creating namespace..."
kubectl apply -f base/namespace.yaml

echo "🔐 Creating secrets..."
if [ -f base/secrets.yaml ]; then
    kubectl apply -f base/secrets.yaml
else
    echo "⚠️  secrets.yaml not found. Run ./create-secrets.sh first!"
    echo "   Using example secrets for now (NOT SECURE FOR PRODUCTION)"
    kubectl apply -f base/secrets.example.yaml
fi

echo "⚙️  Creating ConfigMap..."
kubectl apply -f base/configmap.yaml

echo "💾 Deploying MySQL..."
kubectl apply -f base/mysql-deployment.yaml

echo "🔴 Deploying Redis..."
kubectl apply -f base/redis-deployment.yaml

echo "⏳ Waiting for database to be ready..."
if ! kubectl wait --for=condition=ready pod -l app=mysql -n phone-book --timeout=180s; then
    echo "❌ MySQL failed to start. Check logs:"
    kubectl logs -l app=mysql -n phone-book --tail=50
    exit 1
fi

echo "🔧 Deploying Backend..."
kubectl apply -f base/backend-deployment.yaml
if [ $? -ne 0 ]; then
    echo "❌ Backend deployment failed"
    exit 1
fi

echo "🎨 Deploying Frontend..."
kubectl apply -f base/frontend-deployment.yaml
if [ $? -ne 0 ]; then
    echo "❌ Frontend deployment failed"
    exit 1
fi

echo "📊 Deploying Prometheus..."
kubectl apply -f base/prometheus-deployment.yaml

echo "📈 Deploying Grafana datasources and dashboards..."
kubectl apply -f base/grafana-datasources-configmap.yaml
kubectl apply -f base/grafana-dashboards-configmap.yaml

echo "📈 Deploying Grafana..."
kubectl apply -f base/grafana-deployment.yaml

echo "📡 Deploying MySQL Exporter..."
kubectl apply -f base/mysql-exporter-deployment.yaml

echo "📡 Deploying Redis Exporter..."
kubectl apply -f base/redis-exporter-deployment.yaml

echo "🌐 Creating Ingress..."
kubectl apply -f base/ingress.yaml

echo "📏 Creating HPA (requires metrics-server)..."
if kubectl get apiservice v1beta1.metrics.k8s.io &> /dev/null; then
    kubectl apply -f base/hpa.yaml
    echo "✅ HPA configured"
else
    echo "⚠️  metrics-server not found. HPA will not work."
    echo "   Install: kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml"
    echo "   Skipping HPA for now..."
fi

echo "🔒 Applying Network Policies (optional)..."
if [ -f base/network-policies.yaml ]; then
    kubectl apply -f base/network-policies.yaml
    echo "✅ Network policies applied"
else
    echo "⚠️  network-policies.yaml not found, skipping..."
fi

echo ""
echo "✅ Deployment complete!"
echo ""
echo "📋 Checking deployment status..."
kubectl get pods -n phone-book
echo ""
echo "🔗 Services:"
kubectl get svc -n phone-book
echo ""
echo "📝 To access the application:"
echo "1. Add to /etc/hosts: 127.0.0.1 phone-book.local"
echo "2. Enable Ingress (minikube): minikube addons enable ingress"
echo "3. Access: http://phone-book.local"
echo ""
echo "📊 Monitoring:"
echo "  Grafana: http://phone-book.local/grafana (admin/admin)"
echo "  Prometheus: http://phone-book.local/prometheus"
echo ""
echo "🔍 Useful commands:"
echo "  kubectl get pods -n phone-book"
echo "  kubectl logs -f deployment/backend -n phone-book"
echo "  kubectl describe pod <pod-name> -n phone-book"
echo "  kubectl port-forward -n phone-book svc/grafana 3000:3000"

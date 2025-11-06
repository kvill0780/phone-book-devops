#!/bin/bash
set -e

echo "🚀 Deploying Phone Book Application to Minikube"
echo "=================================================="

# Use minikube kubectl
KUBECTL="minikube kubectl --"

# Check if minikube is running
if ! minikube status &> /dev/null; then
    echo "❌ Minikube is not running. Starting minikube..."
    minikube start
fi

# Enable ingress addon
echo "🔌 Enabling Ingress addon..."
minikube addons enable ingress

# Apply manifests in order
echo "📦 Creating namespace..."
$KUBECTL apply -f base/namespace.yaml

echo "🔐 Creating secrets..."
if [ -f base/secrets.yaml ]; then
    $KUBECTL apply -f base/secrets.yaml
else
    echo "⚠️  secrets.yaml not found, using secrets.example.yaml"
    $KUBECTL apply -f base/secrets.example.yaml
fi

echo "⚙️  Creating ConfigMap..."
$KUBECTL apply -f base/configmap.yaml

echo "💾 Deploying MySQL..."
$KUBECTL apply -f base/mysql-deployment.yaml

echo "🔴 Deploying Redis..."
$KUBECTL apply -f base/redis-deployment.yaml

echo "⏳ Waiting for database to be ready (this may take 2-3 minutes)..."
$KUBECTL wait --for=condition=ready pod -l app=mysql -n phone-book --timeout=300s || echo "⚠️  MySQL taking longer than expected, continuing..."

echo "🔧 Deploying Backend..."
$KUBECTL apply -f base/backend-deployment.yaml

echo "🎨 Deploying Frontend..."
$KUBECTL apply -f base/frontend-deployment.yaml

echo "📊 Deploying Prometheus..."
$KUBECTL apply -f base/prometheus-deployment.yaml

echo "📈 Deploying Grafana..."
$KUBECTL apply -f base/grafana-deployment.yaml

echo "🌐 Creating Ingress..."
$KUBECTL apply -f base/ingress.yaml

echo "📏 Creating HPA..."
$KUBECTL apply -f base/hpa.yaml

echo ""
echo "✅ Deployment complete!"
echo ""
echo "📋 Checking deployment status..."
$KUBECTL get pods -n phone-book
echo ""
echo "🔗 Services:"
$KUBECTL get svc -n phone-book
echo ""
echo "📝 To access the application:"
echo ""
echo "Option 1 - Via Ingress (Recommended):"
echo "  1. Add to /etc/hosts: \$(minikube ip) phone-book.local"
echo "  2. Access: http://phone-book.local"
echo ""
echo "Option 2 - Via Port Forward:"
echo "  kubectl port-forward -n phone-book svc/frontend 8000:80"
echo "  Access: http://localhost:8000"
echo ""
echo "Option 3 - Via Minikube Service:"
echo "  minikube service frontend -n phone-book"
echo ""
echo "🔍 Useful commands:"
echo "  minikube kubectl -- get pods -n phone-book"
echo "  minikube kubectl -- logs -f deployment/backend -n phone-book"
echo "  minikube kubectl -- describe pod <pod-name> -n phone-book"
echo "  minikube dashboard"
echo ""
echo "🌐 Minikube IP: \$(minikube ip)"

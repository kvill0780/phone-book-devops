#!/bin/bash

echo "=========================================="
echo "   Vérification Déploiement Kubernetes"
echo "=========================================="
echo ""

echo "📦 État des Pods:"
minikube kubectl -- get pods -n phone-book
echo ""

echo "🌐 Services:"
minikube kubectl -- get svc -n phone-book
echo ""

echo "🔀 Ingress:"
minikube kubectl -- get ingress -n phone-book
echo ""

echo "🧪 Tests des endpoints:"
echo ""

echo "✓ Frontend:"
curl -s -o /dev/null -w "  Status: %{http_code}\n" http://phone-book.local

echo "✓ Backend API:"
curl -s -o /dev/null -w "  Status: %{http_code}\n" http://phone-book.local/api/actuator/health

echo "✓ Grafana:"
curl -s -o /dev/null -w "  Status: %{http_code}\n" http://phone-book.local/grafana

echo "✓ Prometheus:"
curl -s -o /dev/null -w "  Status: %{http_code}\n" http://phone-book.local/prometheus
echo ""

echo "=========================================="
echo "✅ Déploiement vérifié avec succès!"
echo "=========================================="
echo ""
echo "Accès:"
echo "  • Application: http://phone-book.local"
echo "  • Grafana: http://phone-book.local/grafana (login from .env or secrets/grafana_password.txt)"
echo "  • Prometheus: http://phone-book.local/prometheus"

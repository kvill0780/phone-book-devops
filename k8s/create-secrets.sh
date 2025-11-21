#!/bin/bash
set -e

echo "🔐 Creating Kubernetes Secrets for Phone Book Application"
echo "=========================================================="

# Generate random passwords
MYSQL_ROOT_PASSWORD=$(openssl rand -base64 16)
MYSQL_PASSWORD=$(openssl rand -base64 16)
GRAFANA_PASSWORD=$(openssl rand -base64 16)

echo "✅ Generated secure passwords"

# Create namespace if not exists
kubectl create namespace phone-book --dry-run=client -o yaml | kubectl apply -f -

# Create MySQL secret
kubectl create secret generic mysql-secret \
  --from-literal=MYSQL_ROOT_PASSWORD="$MYSQL_ROOT_PASSWORD" \
  --from-literal=MYSQL_PASSWORD="$MYSQL_PASSWORD" \
  --namespace=phone-book \
  --dry-run=client -o yaml | kubectl apply -f -

echo "✅ Created mysql-secret"

# Create Grafana secret
kubectl create secret generic grafana-secret \
  --from-literal=GF_SECURITY_ADMIN_PASSWORD="$GRAFANA_PASSWORD" \
  --namespace=phone-book \
  --dry-run=client -o yaml | kubectl apply -f -

echo "✅ Created grafana-secret"

echo ""
echo "📝 Save these credentials securely:"
echo "-----------------------------------"
echo "MySQL Root Password: $MYSQL_ROOT_PASSWORD"
echo "MySQL User Password: $MYSQL_PASSWORD"
echo "Grafana Admin Password: $GRAFANA_PASSWORD"
echo ""
echo "⚠️  Store these in a password manager, NOT in Git!"

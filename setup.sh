#!/bin/bash
set -e

echo "🚀 Phone Book Application - Setup Script"
echo "=========================================="

# Create secrets directory
echo "📁 Creating secrets directory..."
mkdir -p secrets

# Generate secrets if they don't exist
if [ ! -f secrets/mysql_root_password.txt ]; then
    echo "🔐 Generating MySQL root password..."
    openssl rand -base64 32 > secrets/mysql_root_password.txt
fi

if [ ! -f secrets/mysql_password.txt ]; then
    echo "🔐 Generating MySQL user password..."
    openssl rand -base64 32 > secrets/mysql_password.txt
fi

if [ ! -f secrets/grafana_password.txt ]; then
    echo "🔐 Generating Grafana password..."
    openssl rand -base64 16 > secrets/grafana_password.txt
fi

# Set proper permissions
chmod 600 secrets/*.txt

echo "✅ Secrets created successfully!"
echo ""
echo "📋 Next steps:"
echo "1. Review secrets in ./secrets/ directory"
echo "2. Run: docker-compose up -d"
echo "3. Wait 30-60 seconds for services to start"
echo "4. Access:"
echo "   - Frontend: http://localhost"
echo "   - Backend API: http://localhost:8080/api"
echo "   - Grafana: http://localhost:3000 (admin / admin)"
echo "   - Prometheus: http://localhost:9090"

#!/bin/bash
set -e

echo "🐳 Building Docker images for Minikube"
echo "======================================="

# Use Minikube's Docker daemon
echo "🔧 Configuring Docker to use Minikube's daemon..."
eval $(minikube docker-env)

# Build backend
echo ""
echo "🔨 Building backend image..."
cd spring-phone-book
docker build -t kvill0780/phone-book-backend:latest .
cd ..

# Build frontend
echo ""
echo "🔨 Building frontend image..."
cd phone-book-frontend
docker build -t kvill0780/phone-book-frontend:latest .
cd ..

echo ""
echo "✅ Images built successfully!"
echo ""
echo "📋 Checking images in Minikube:"
docker images | grep phone-book

echo ""
echo "🚀 You can now deploy to Kubernetes with:"
echo "   cd k8s && ./deploy-minikube.sh"

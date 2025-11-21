#!/bin/bash

echo "🚦 Generating traffic to Phone Book Application"
echo "=============================================="

# Configuration
BACKEND_URL="${BACKEND_URL:-http://localhost:8080}"
DURATION="${DURATION:-60}"
REQUESTS_PER_SECOND="${RPS:-5}"

echo "Backend URL: $BACKEND_URL"
echo "Duration: ${DURATION}s"
echo "Requests/sec: $REQUESTS_PER_SECOND"
echo ""

# Check if backend is reachable
if ! curl -s -f "$BACKEND_URL/actuator/health" > /dev/null; then
    echo "❌ Backend not reachable at $BACKEND_URL"
    echo "   Make sure the application is running"
    exit 1
fi

echo "✅ Backend is reachable"
echo "🚀 Starting traffic generation..."
echo ""

# Function to make requests
make_requests() {
    local endpoint=$1
    local method=${2:-GET}
    
    for i in $(seq 1 $REQUESTS_PER_SECOND); do
        curl -s -X $method "$BACKEND_URL$endpoint" > /dev/null &
    done
}

# Generate traffic for specified duration
end_time=$((SECONDS + DURATION))

while [ $SECONDS -lt $end_time ]; do
    # Health check
    make_requests "/actuator/health" "GET"
    
    # Metrics endpoint
    make_requests "/actuator/prometheus" "GET"
    
    # API endpoints (will fail without auth, but generates metrics)
    make_requests "/api/contacts" "GET"
    make_requests "/api/auth/login" "POST"
    
    # Wait 1 second before next batch
    sleep 1
    
    # Progress indicator
    remaining=$((end_time - SECONDS))
    echo -ne "⏱️  Time remaining: ${remaining}s\r"
done

echo ""
echo "✅ Traffic generation complete!"
echo ""
echo "📊 Check metrics in:"
echo "   - Prometheus: http://localhost:9090"
echo "   - Grafana: http://localhost:3000"

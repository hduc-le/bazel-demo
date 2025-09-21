#!/bin/bash

# Demo script for Bazel monorepo with cross-language services
echo "🏗️  Bazel Monorepo Demo - Python + Kotlin Services"
echo "=================================================="

# Build all services
echo "Building all services..."
bazel build //:all_services

if [ $? -eq 0 ]; then
    echo "✅ All services built successfully!"
else
    echo "❌ Build failed!"
    exit 1
fi

echo ""
echo "📋 Available commands:"
echo "  # Run Python service (port 8000):"
echo "  bazel run //:python_service"
echo ""
echo "  # Run Kotlin service (port 8080):"
echo "  bazel run //:kotlin_service"
echo ""
echo "🔗 API Endpoints:"
echo ""
echo "Python Service (http://localhost:8000):"
echo "  GET /                    - Hello world"
echo "  GET /health              - Health check"
echo "  GET /items/{item_id}     - Get item by ID"
echo "  GET /data/analysis       - Data analysis demo"
echo "  GET /call-kotlin         - Call Kotlin service"
echo "  GET /shared-models/user/{user_id} - User data"
echo ""
echo "Kotlin Service (http://localhost:8080):"
echo "  GET /                    - Hello world"
echo "  GET /health              - Health check"
echo "  GET /users/{id}          - Get user by ID"
echo "  GET /products            - List products"
echo "  GET /call-python         - Call Python service"
echo ""
echo "🚀 Cross-Language Features:"
echo "  • Both services can communicate with each other"
echo "  • Shared data models for consistency"
echo "  • Unified build system with Bazel"
echo "  • Dependency management across languages"
echo ""
echo "📝 To test cross-language communication:"
echo "  1. Start both services in separate terminals"
echo "  2. Test Python → Kotlin: curl http://localhost:8000/call-kotlin"
echo "  3. Test Kotlin → Python: curl http://localhost:8080/call-python"

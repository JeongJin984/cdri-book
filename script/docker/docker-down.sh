#!/bin/bash

# Move to the directory where docker-compose.yml is located
cd "$(dirname "$0")" || exit

# Stop environment
echo "Stopping environment..."
docker compose down -v

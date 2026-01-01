#!/bin/bash

# Move to project root
cd "$(dirname "$0")/../.." || exit

# Build Docker image
echo "Building Docker image: cdri-book..."
docker build -t cdri-book .

# Start environment
echo "Starting environment..."
cd script/docker || exit
docker compose up -d

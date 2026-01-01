@echo off

rem Move to project root
cd /d %~dp0\..\..

rem Build Docker image
echo Building Docker image: cdri-book...
docker build -t cdri-book .

rem Start environment
echo Starting environment...
cd /d %~dp0\..\docker
docker compose up -d

@echo off

rem Move to the directory where docker-compose.yml is located
cd /d %~dp0

rem Stop environment
echo Stopping environment...
docker-compose down -v

@echo off
echo Running AdminIntegrationTest...
call mvnw.cmd test -Dtest=AdminIntegrationTest
echo.
echo Running AuthIntegrationTest...
call mvnw.cmd test -Dtest=AuthIntegrationTest
pause 
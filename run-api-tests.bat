@echo off
echo ============================================
echo  Running Weatherbit API Tests
echo ============================================
echo.

mvn clean verify -Dcucumber.filter.tags="@api"

echo.
echo ============================================
echo  Opening Serenity Report...
echo ============================================
start "" "target\site\serenity\index.html"

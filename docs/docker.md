# Docker

## Zbudowanie obrazu na różne platformy i wypchnięcie do docker hub
```shell
docker buildx build --platform linux/amd64,linux/arm64,linux/arm/v7 -t mgarbowski/pap-projekt-backend:latest --push .
```

## Docker networks
Kontenery znajdujące się w jednej sieci widzą się nawzajem w ramach DNS po nazwach kontenerów

Żeby server Nginx służący jako reverse proxy dla strony mgarbowski.pl mógł przekazywać zapytania do aplikacji backendowej,
oba kontenery muszą być w tej samej sieci `mgarbowski-pl-network`

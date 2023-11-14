# Docker

## Platformy
[Serwer](./server.md) ma procesor ARM, więc obraz zbudowany na komputerze z procesorem intela nie będzie działać.

Można wykorzystać QEMU i zbudować obraz na inną platformę niż ta, na której się buduje
[https://docs.docker.com/build/building/multi-platform/]()

## Zbudowanie obrazu na różne platformy i wypchnięcie do docker hub
```shell
docker buildx build --platform linux/amd64,linux/arm64,linux/arm/v7 -t mgarbowski/pap-projekt-backend:latest --push .
```

## Docker networks
Kontenery znajdujące się w jednej sieci widzą się nawzajem w ramach DNS po nazwach kontenerów

Żeby server Nginx służący jako reverse proxy dla strony mgarbowski.pl mógł przekazywać zapytania do aplikacji backendowej,
oba kontenery muszą być w tej samej sieci `mgarbowski-pl-network`

## Komendy
* `docker compose up` - uruchomienie kontenerów wg. pliku `docker-compose.yml` w aktualnym katalogu
* `docker compose down` - wyłączenie kontenerów wg. pliku `docker-compose.yml` w aktualnym katalogu
* `docker compose logs` - wyświetlenie logów kontenerów wg. pliku `docker-compose.yml` w aktualnym katalogu
* `docker ps` - lista aktywnych kontenerów


## Uruchomienie lokalne kontenerów
Gdyby zaszła potrzeba lokalnego uruchomienia kontenerów, jest dostępny plik [docker-compose-dev.yml](../docker-compose-dev.yml).
Pozwala na zbudowanie obrazów zgodnie z Dockerfiles
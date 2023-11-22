# Deployment

## Obrazy kontenerów
Obrazy kontenerów są publikowane w rejestrze [Docker Hub](https://hub.docker.com)
* [mgarbowski/pap-projekt-backend:latest](https://hub.docker.com/repository/docker/mgarbowski/pap-projekt-backend/general)
* [mgarbowski/pap-projekt-frontend:latest](https://hub.docker.com/repository/docker/mgarbowski/pap-projekt-frontend/general)

Do publikowania kontenerów jest wymagane hasło. Są publicznie dostępne do pobrania.

## Ręczne uruchamianie
W katalogu `/srv/pap` umieszczamy plik [docker-compose.yml](../docker-compose.yml) zawierający konfigurację uruchomienia
kontenerów oraz drugi plik nazwany np. `docker-compose.deployment.yml` zawierający hasła lub inne tajne informacje, 
których nie zawieramy w repozytorium git.

Komenda `docker compose -f docker-compose.yml -f docker-compose.deployment.yml up --pull=always -d`
* Uruchomi kontenery `pap-frontend`, `pap-backend`, `pap-database` zgodnie z konfiguracją w podanych plikach
* `--pull=always` zapewni że przed uruchomieniem zostaną pobrane najnowsze obrazu z Docker Hub
* `-d` uruchomi kontenery w trybie detached (odczepione od aktywnego terminala, w tle)

Logi kontenerów można wyświetlić komendą `docker compose logs` (będąc w tym samym katalogu co plik `docker-compose.yml`)

## GitHub Actions
Akcje uruchamiają się automatycznie lub na żądanie (przycisk Run workflow w zakładce Actions na GitHubie)

### Uruchomienie testów
* [GitHub](https://github.com/mGarbowski/pap-projekt/actions/workflows/gradle.yml)
* [gradle.yml](../.github/workflows/backend-tests.yml)

Działanie
* Instaluje JDK
* Uruchamia polecenie `gradle build` (w tym wykonanie testów)

TODO: Uruchamianie wszystkich testów i mądrzejsze nazwy

### Wdrożenie obrazów z Docker Hub
* [GitHub](https://github.com/mGarbowski/pap-projekt/actions/workflows/deploy-from-docker-hub.yml)
* [deploy-from-docker-hub.yml](../.github/workflows/deploy-from-docker-hub.yml)
* [deploy.sh](../deploy.sh)

Działanie
* Wczytuje hasła, klucze i konfigurację połączenia z serwerem z GitHub Secrets
* Kopiuje na serwer plik `docker-compose.yml`
* Tworzy na serwerze plik `docker-compose.deployment.yml`
* Uruchamia kontenery


### Zbudowanie obrazów, opublikowanie i wdrożenie
TODO
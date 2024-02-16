# Wdrożenie

## Obrazy kontenerów

Obrazy kontenerów są publikowane w rejestrze [Docker Hub](https://hub.docker.com)

* [mgarbowski/pap-projekt-backend:latest](https://hub.docker.com/repository/docker/mgarbowski/pap-projekt-backend/general)
* [mgarbowski/pap-projekt-frontend:latest](https://hub.docker.com/repository/docker/mgarbowski/pap-projekt-frontend/general)

Do publikowania kontenerów jest wymagane hasło. Są publicznie dostępne do pobrania.

## Ręczne uruchamianie

W katalogu `/srv/pap` umieszczamy plik [docker-compose.yml](../deployment/docker-compose.yml) zawierający konfigurację
uruchomienia
kontenerów oraz drugi plik nazwany np. `docker-compose.deployment.yml` zawierający hasła lub inne tajne informacje,
których nie zawieramy w repozytorium git.

Komenda `docker compose -f docker-compose.yml -f docker-compose.deployment.yml up --pull=always -d`

* Uruchomi kontenery `pap-frontend`, `pap-backend`, `pap-database` zgodnie z konfiguracją w podanych plikach
* `--pull=always` zapewni że przed uruchomieniem zostaną pobrane najnowsze obrazu z Docker Hub
* `-d` uruchomi kontenery w trybie detached (odczepione od aktywnego terminala, w tle)

Logi kontenerów można wyświetlić komendą `docker compose logs` (będąc w tym samym katalogu co plik `docker-compose.yml`)

## GitHub Actions

Do automatyzowania typowych zadań wykorzystujemy GitHub Actions.

Zadania są podzielone na pojedyncze, reużywalne akcje łączone w większe workflows.

### Uruchomienie testów

Utworzenie pull request na branch `main` skutkuje uruchomieniem wszystkich testów

* [all-tests](../.github/workflows/all-tests.yml)
* [backend-tests](../.github/workflows/backend-tests.yml)
* [frontend-tests](../.github/workflows/frontend-tests.yml)

### Wdrożenie aplikacji na serwerze

[full-deployment](../.github/workflows/full-deployment.yml)

Bot GitHub Actions loguje się na serwer produkcyjny jako użytkownik `pap`. Tajne informacje (hasła klucze) są
wstrzykiwane z poziomu `Settings > Security > Secrets and variables > Actions`.

Pełne wdrożenie polega na uruchomieniu wszystkich testów, zbudowaniu obrazów kontenerów i opublikowanie ich na Docker
Hub (patrz [docker](./docker.md)) oraz uruchomienie aplikacji na serwerze. Zawartość bazy danych zostaje zachowana
pomiędzy wdrożeniami, ale w przypadku zmiany modelu danych konieczne jest przeprowadzenie odpowiedniej migracji.

## Kopie zapasowe

TODO

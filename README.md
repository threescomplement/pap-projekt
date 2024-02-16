# Platforma do oceniania kursów SJO

Aplikacja służy do wystawiania i przeglądania przez studentów opinii o kursach językowych oferowanych przez
Studium Języków Obcych Politechniki Warszawskiej. Aplikacja powstała jako projekt semestralny na przedmiot
Programowanie Aplikacyjne w semestrze 2023Z.

![Tests](https://github.com/mGarbowski/pap-projekt/actions/workflows/all-tests.yml/badge.svg)

## Autorzy

* Mikołaj Garbowski
* Michał Łuszczek
* Maksym Bieńkowski

## Technologia

* Backend
    * aplikacja Spring Boot
    * baza danych PostgreSQL
    * uwierzytelnianie i autoryzacja realizowane z użyciem JSON Web Tokens
* Frontend - klient webowy, aplikacja w React
* Komunikacja między warstwami przez REST API

## Dokumentacja

Dokumentacja projektu znajduje się w katalogu [docs](./docs)

* [Instrukcja uruchomienia, instalacji i pracy z backendem](./docs/backend.md)
* [Instrukcja uruchomienia, instalacji i pracy z frontendem](./docs/frontend.md)
* [Praca z serwerem](./docs/server.md)
* [Pliki konfiguracyjne i hasła](./docs/configuration.md)
* [Praca z Dockerem](./docs/docker.md)
* [Wdrażanie](./docs/deployment.md)
* [Architektura](./docs/architecture.md)
* [Scenariusze testów end-to-end](./docs/test-scenarios.md)

## Kontakt i wsparcie

Możesz wesprzeć projekt finansowo na [buycoffe.to](https://buycoffee.to/mgarbowski).

Znalazłeś błąd, chcesz zaproponować nową funkcjonalność lub wprowadzić swoje poprawki? Możesz założyć własne
[issue](https://github.com/mGarbowski/pap-projekt/issues) i stworzyć pull request.

Kod źródłowy projektu jest otwarty, ale pozostaje własnością jego autorów.

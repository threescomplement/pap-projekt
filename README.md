# Programowanie Aplikacyjne 2023Z - projekt

[![Java CI with Gradle](https://github.com/mGarbowski/pap-projekt/actions/workflows/gradle.yml/badge.svg)](https://github.com/mGarbowski/pap-projekt/actions/workflows/gradle.yml)

Projekt semestralny

Równoległe repozytoria na GitHub i wydziałowym GitLab

* [GitHub](https://github.com/mGarbowski/pap-projekt)
* [GitLab](https://gitlab-stud.elka.pw.edu.pl/mgarbows/pap2023z-z02)

## Autorzy
* Mikołaj Garbowski
* Michał Łuszczek
* Maksym Bieńkowski
* Tomasz Kurzela

## Opis projektu
Aplikacja służąca do wystawiania przez studentów i przeglądania opinii o kursach językowych oferowanych przez 
Studium Języków Obcych Politechniki Warszawskiej.

### Funkcjonalności
* Rejestracja i logowanie użytkowników
* Przeglądanie oferty kursów SJO i prowadzących
* Wystawianie opinii kursom SJO (operacje CRUD)
* Wprowadzanie danych o kursach (przez administratora lub w sposób zautomatyzowany)

### Technologia
* Backend - aplikacja Spring Boot
  * połączona z relacyjną bazą danych przez JPA
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

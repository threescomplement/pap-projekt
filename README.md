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

## Struktura repozytorium, technologie

### Backend

* Aplikacja - Java, Spring Framework
* Budowanie aplikacji - Gradle
* Testy jednostkowe - JUnit
* Środowisko uruchomieniowe - Docker

Polecam instalację JDK i Gradle za pośrednictwem [SDKman](https://sdkman.io/). Aktualnie skompilowany projekt ma być
kompatybilny z Javą 17 ale można używać nowszego JDK.

### Gradle

Gradle jest zestawem narzędzi do budowania, uruchamiania testów, zarządzania zależnościami itd. Konfiguracja dla Gradle
znajduje się w plikach [build.gradle.kts](./build.gradle.kts) i [settings.gradle.kts](./settings.gradle.kts).

Gradle rejestruje różne zadania, które potrafi wykonywać, można je wykonywać przez wtyczkę do IntelliJ albo z powłoki
używając skryptu [gradlew](./gradlew) dla Linuxa.

#### Baza danych
Na chwilę obecną uruchomienie aplikacji czy nawet uruchomienie testów wymaga chodzącej bazy danych
```bash
docker compose up -d database
```

Żeby położyć wszystkie kontenery
```bash
docker compose down
```

#### Uruchomienie lokalnie aplikacji
```bash
./gradlew bootRun
```

#### Uruchomienie testów
```bash
./gradlew check
```

#### Budowanie aplikacji
Aplikację możemy zbudować do fat-jar - plik `.jar` wykonywalny przez JVM, który poza aplikacją zawiera wszystkie zależności
```bash
./gradlew build
```

Pojawia się plik `./build/libs/pap-0.0.1-SNAPSHOT.jar` (interesuje nas ten bez "plain" w nazwie). Taki plik można uruchomić podając flagę:

```bash
java -jar ./build/libs/pap-0.0.1-SNAPSHOT.jar
```

#### Sprzątanie
Wyniki kompilacji itd. można wyczyścić komendą:

```bash
./gradlew clean
```

#### Uruchomienie całości w kontenerach
Całą aplikację (backend i bazę danych) można uruchomić jako 2 kontenery.
Najpierw trzeba zbudować .jar
```bash
./gradlew build
docker compose up
```
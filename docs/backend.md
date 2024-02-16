# Backend

Kod aplikacji backendowej znajduje się w katalogu [backend](../backend)

* Aplikacja - Java, Spring Framework
* Budowanie aplikacji - Gradle
* Testy jednostkowe - JUnit
* Środowisko uruchomieniowe - Docker

Polecam instalację JDK i Gradle za pośrednictwem [SDKman](https://sdkman.io/). Aktualnie skompilowany projekt ma być
kompatybilny z Javą 17.

## Gradle

Gradle jest zestawem narzędzi do budowania, uruchamiania testów, zarządzania zależnościami itd. Konfiguracja dla Gradle
znajduje się w plikach [build.gradle.kts](../backend/build.gradle.kts)
i [settings.gradle.kts](../backend/build.gradle.kts).

Gradle rejestruje różne zadania, które potrafi wykonywać, można je wykonywać przez wtyczkę do IntelliJ albo z poziomu
terminala używając skryptu [gradlew](../backend/gradlew) dla Linuxa.

### Uruchomienie lokalnie aplikacji

```bash
./gradlew bootRun
```

### Uruchomienie testów

```bash
./gradlew check
```

### Budowanie aplikacji

Aplikację możemy zbudować do fat-jar - plik `.jar` wykonywalny przez JVM, który poza aplikacją zawiera wszystkie
zależności

```bash
./gradlew build
```

Pojawia się plik `./build/libs/pap-0.0.1-SNAPSHOT.jar` (interesuje nas ten bez "plain" w nazwie). Plik .jar można
uruchomić komendą

```bash
java -jar ./build/libs/pap-0.0.1-SNAPSHOT.jar
```

### Sprzątanie

Wyniki kompilacji itd. można wyczyścić komendą:

```bash
./gradlew clean
```

## Profile

Spring umożliwia zdefiniowanie wielu profili aplikacji różniących się konfiguracją i funkcjonalnością.
Każdy profil ma odpowiadający mu plik .yml w katalogu [resources](../backend/src/main/resources). Plik 
[application.yml](../backend/src/main/resources/application.yml) zawiera konfigurację wspólną dla wszystkich profili.

* `prod` - profil produkcyjny, uruchamiany na serwerze
* `dev` - profil deweloperski, do uruchamiania lokalnie przez programistę
    * korzysta z bazy danych H2 - in-memory, wygodna i niewymagająca konfiguracji
    * wypełnia bazę przykładowymi danymi
* `dev-postgres` - nakładka na profil `dev`, zamiast H2 używa PostgreSQL
    * wymaga uruchomienia lokalnej instancji PostgreSQL
    * `docker compose -f ./deployment/docker-compose-dev.yml up database`
* `test` - profil testowy, do uruchamiania na nim testów
    * korzysta z bazy danych H2

## Struktura projektu

Klasy są pogrupowany w pakiety dziedzinami (zgodnie z zaleceniami dokumentacji Spring). Typowo w pakiecie znajdują się

* Entity
    * z anotacją `@Entity`
    * modeluje dane przechowywane w bazie
    * np. `Course`
* Repository
    * interfejs do interakcji z bazą danych
    * wykorzystuje Spring Data JPA
    * automatycznie generuje implementacje podstawowych operacji CRUD
    * umożliwia definiowanie własnych operacji przez sygnatury metod lub przez zapytania w JPA Query Language
    * np. `CourseRepository`
* Service
    * realizuje logikę biznesową aplikacji
    * np. `CourseService`
* Controller
    * definiuje endpointy REST API
    * przetwarzanie zapytań i budowanie odpowiedzi HTTP
    * np. `CourseController`
    * odpowiedzi JSON zawierają linki do powiązanych zasobów zgodnie z HATEOAS
* DTO
    * data transfer object
    * obiekty konwertowane do formatu JSON i zwracane przez API
    * np. `CourseDTO`

## Testy

Testy wykorzystują biblioteki JUnit, Mockito i Spring Boot Test. Testowane jest przede wszystkim publiczne API
i poprawne generowanie odpowiedzi HTTP (testy integracyjne).

Do testów aplikacja jest uruchamiana w profilu `test`.

## Lombok

Projekt wykorzystuje bibliotekę Lombok do automatycznego generowania powtarzalnego kodu typu boilerplate.
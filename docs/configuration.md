# Konfiguracja aplikacja
Aplikacja frontendowa i backendowa mogą potrzebować pewnych wartości, których z różnych względów lepiej nie hardcodować.

Hasła, klucze i inne tajne wartości w ogóle nie powinny się znajdować w repozytorium git.

Adresy URL pod którymi jest widziana aplikacja, szczegóły połączenia z bazą danych mogą się różnić między
lokalną deweloperską instancją aplikacji, a właściwą "produkcyjną" instancją na serwerze - te parametry wczytujemy z plików konfiguracyjnych

## Konfiguracja Spring Boot
Spring wspiera podzielenie konfiguracji na profile, obecnie mamy 2 profile
* `dev` - domyślny profil przeznaczony do uruchamiania aplikacji lokalnie, łączy się z bazą danych H2 in-memory
* `prod` - przeznaczony do uruchomienia na docelowym serwerze

### Pliki konfiguracyjne
Konfiguracja aplikacji Spring Boot znajduje się w katalogu [resources](../src/main/resources/) w plikach
* [application.yml](../src/main/resources/application.yml) - wspólna konfiguracja dla wszystkich profili
* [application-dev.yml](../src/main/resources/application-dev.yml) - konfiguracja tylko dla profilu `dev`
* [application-prod.yml](../src/main/resources/application-prod.yml) - konfiguracja tylko dla profilu `prod`


### Wybór profilu
Uruchamiając aplikację spakowaną do pliku `.jar` podajemy argument z linii poleceń
```shell
java -jar build/libs/pap-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

W konfiguracji uruchomienia w IntelliJ ustawiamy opcję `active profile`.

Generalnie nie ma potrzeby tego robić, bo `dev` jest profilem domyślnym

### Beans
Bean może być oznaczony anotacją `@Profile`, żeby był używany tylko dla wybranego profilu, np.

```java
    @Bean()
    @Profile("dev")
    public CommandLineRunner addDummyUsers(UserRepository repository) {
        return (args) -> {
            repository.save(new User(...));
            repository.save(new User(...));
        };
    }
```

Spowoduje zapisanie przykładowych użytkowników w bazie danych tylko na profilu deweloperskim - wygodne do lokalnego testowania.

## Aplikacja React
Aplikacja wczytuje zmienne środowiskowe z pliku `.env`, można się do nich odwołać w kodzie przez `process.emv.NAZWA_ZMIENNEJ`.
TODO: trzeba będzie to dopracować na potrzeby wdrożenia

## Hasła, klucze itd

### Hasło do Gmaila
Do poprawnego działania usługi wysyłania maili przez aplikacje Spring, musimy podać hasło do konta na Gmailu.
Hasło może być wczytane z pliku `application.yml` ale odradzam, żeby przypadkiem nie zapisać go w git.

Najlepiej podać hasło przez argument linii poleceń
```shell
java -jar build/libs/pap-0.0.1-SNAPSHOT.jar --spring.mail.password=<hasło>
```

Wygodnie jest ustawić tą flagę w konfiguracji uruchomienia w IntelliJ.

### GitHub secrets
W projekcie na GitHubie można dodać secrets - raz dodane nie mogą być odczytane przez użytkownika ale mogą być wstrzykiwane
podczas wykonywania GitHub Actions.

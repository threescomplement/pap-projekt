# Konfiguracja aplikacji

## Wybór profilu

Uruchamiając aplikację spakowaną do pliku `.jar` podajemy argument z linii poleceń. Profile są opisane w dokumencie
[bakcend](./backend.md).

```shell
java -jar build/libs/pap-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

W konfiguracji uruchomienia w IntelliJ ustawiamy opcję `active profile`. Domyślnym profilem jest `dev`.

Do przechowywania lokalnie konfiguracji nie znajdującej się w repozytorium git (np. hasło do Gmail) można utworzyć
`application-local.yml` i uruchamiać aplikację z profilami `dev,local`

### Beans

Bean może być oznaczony anotacją `@Profile`, żeby był używany tylko dla wybranego profilu, np.

```java

@Bean()
@Profile("dev")
public CommandLineRunner addDummyUsers(UserRepository repository) {
    return (args) -> {
        repository.save(new User(...))
        repository.save(new User(...))
    };
}
```

Spowoduje zapisanie przykładowych użytkowników w bazie danych tylko na profilu deweloperskim - wygodne do lokalnego
testowania.

## Aplikacja React

Aplikacja wczytuje zmienne środowiskowe z plików `.env.development` albo `.env.production`, można się do nich odwołać
w kodzie przez `process.emv.NAZWA_ZMIENNEJ`.

## Hasła, klucze itd

### Hasło do Gmaila

Do poprawnego działania usługi wysyłania maili przez aplikacje Spring, musimy podać hasło do konta na Gmailu.

Najlepiej podać hasło przez argument linii poleceń albo `application-local.yml` ignorowany przez git.

```shell
java -jar build/libs/pap-0.0.1-SNAPSHOT.jar --spring.mail.password=<hasło>
```

Wygodnie jest ustawić tę flagę w konfiguracji uruchomienia w IntelliJ.

### GitHub secrets

Wszystkie sekrety wykorzystywane przy wdrożeniu są zapisane w projekcie na GitHubie w
`Settings > Security > Secrets and variables > Actions`. Raz zapisanych nie da się później odczytać, tylko nadpisać.
Dodane w ten sposób wartości mogą być wstrzykiwane w akcjach.

Przy wdrożeniu, sekrety są zapisywane do pliku `/srv/pap/docker-compose.deployment.yml`.

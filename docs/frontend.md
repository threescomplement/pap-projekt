# Frontend
Frontend do aplikacji stanowi aplikacja webowa w języku TypeScript, wykorzystuje React i Tailwind CSS.
W planach są migracja stylowania całej aplikacji do Tailwind CSS i migracja samej aplikacji do NextJS.

Kod związany z aplikacji frontendowej znajduje się w katalogu [frontend](../frontend)

## Instalacja
Do pracy potrzebne są lokalnie zainstalowane NodeJS i npm

Dla Ubuntu
```shell
sudo apt update
sudo apt install nodejs npm
```

## Uruchomienie
Głównym plikiem konfiguracyjnym dla aplikacji jest [package.json](../frontend/package.json). Znajdują się tam m. in.
zależności i skrypty uruchomieniowe. Przed uruchomieniem czegokolwiek musimy zainstalować biblioteki

```shell
cd frontend
npm install
```

Każde z poniższych poleceń można dodać w IntelliJ jako konfigurację uruchomienia (co bardzo polecam).

### Uruchomienie serwera deweloperskiego
```shell
npm start
```

Buduje aplikację i startuje serwer deweloperski, który umożliwia wygodną pracę. Automatycznie powinno otworzyć się
okno przeglądarki z otwartą aplikacją domyślnie pod `http://localhost:3000`. Po zmodyfikowaniu pliku źródłowego
i zapisaniu go strona od razu się przeładowuje bez konieczności wyłączania i włączania (wymagane tylko w szczególnych 
przypadkach).

### Uruchomienie testów
```shell
npm test
```

Korzystamy z biblioteki Jest, domyślnej dla `create-react-app`.
Ostrzeżenia ESLint są traktowane jako błędy.

### Budowanie aplikacji
```shell
npm run build
```

Tworzy zoptymalizowany build aplikacji, przystosowany do wdrożenia na serwerze.

## Struktura katalogów
W głównym katalogu [frontend](../frontend) znajdują się pliki konfiguracyjne. W plikach `.env.production` 
i `.env.development` zdefiniowane są odpowiednie zmienne środowiskowe, do których możemy odwołać się w aplikacji przez
`process.env.NAZWA_ZMIENNEJ`. Aplikacja uruchomiona przez `npm start` używa profilu `development`, a zbudowana przez
`npm run build` używa profilu `production`.

Katalog [src](../frontend/src) zawiera właściwy kod źródłowy. Punktem wejścia aplikacji jest 
[index.tsx](../frontend/src/index.tsx), głównym komponentem jest [App.tsx](../frontend/src/App.tsx).

Katalog [lib](../frontend/src/lib) zawiera zwykłe pliki `.ts` zawierające logikę biznesową.

Katalog [pages](../frontend/src/pages) zawiera komponenty podstron używanych przez React Router 
(patrz na [App.tsx](../frontend/src/App.tsx)).

Katalog [hooks](../frontend/src/hooks) zawiera własne React Hooks. 
(patrz [Reusing logic with custom hooks](https://react.dev/learn/reusing-logic-with-custom-hooks))

Katalog [components](../frontend/src/components) zawiera pozostałe komponenty.

Katalog [ui](../frontend/src/ui) zawiera stylowanie.
# Frontend
Frontend do aplikacji stanowi aplikacja webowa w frameworku React, używamy języka TypeScript.

Kod związany z frontendem znajduje się w katalogu [frontend](../frontend)

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
i zapisaniu go, strona od razu się przeładowuje bez konieczności wyłączania i włączania (wymagane tylko w szczególnych 
przypadkach).

### Uruchomienie testów
```shell
npm test
```

Korzystamy z biblioteki Jest, domyślnej dla `create-react-app`

### Budowanie aplikacji
```shell
npm run build
```

Tworzy zoptymalizowany build aplikacji przystosowany do wdrożenia na serwerze.

## Struktura katalogów
W głównym katalogu [frontend](../frontend) znajdują się pliki konfiguracyjne i `.env` określający zmienne środowiskowe,
do których możemy odwołać się w aplikacji `process.env.NAZWA_ZMIENNEJ`.

Katalog [src](../frontend/src) zawiera właściwy kod źródłowy. Punktem wejścia aplikacji jest 
[index.tsx](../frontend/src/index.tsx), głównym komponentem jest [App.tsx](../frontend/src/App.tsx).

Katalog [lib](../frontend/src/lib) zawiera zwykłe pliki `.ts` zawierające logikę biznesową.

Katalog [pages](../frontend/src/pages) zawiera komponenty obsługujące routing podstrony aplikacji używane przez React 
Router (patrz na [App.tsx](../frontend/src/App.tsx)).

Katalog [hooks](../frontend/src/hooks) zawiera własne hooki. 
(patrz [Reusing logic with custom hooks](https://react.dev/learn/reusing-logic-with-custom-hooks))

Katalog [components](../frontend/src/components) zawiera pozostałe komponenty.
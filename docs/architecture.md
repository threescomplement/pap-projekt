# Architektura aplikacji

## Serwer
Szczegóły dostępne na stronie [server](./server.md)

## Architektura
![diagram architektury](./images/architecture.png)

Aplikacja składa się z komponentów
* Reverse proxy
* Serwer HTTP - serwujący aplikację frontendową
* Aplikacja Spring Boot
* Baza danych - PostgreSQL

### Komunikacja sieciowa
Każdy z komponentów jest uruchomiony w oddzielnym kontenerze Dockera.
Wszystkie znajdują się w jednej sieci (docker network), w związku z tym, w komunikacji sieciowej między kontenerami
można używać nazw kontenerów jako nazw domenowych, ponieważ kontenery używają wewnętrznego DNS Dockera.
Tzn. zapytanie `http://pap-backend/` wysłane przez reverse proxy zostanie odebrane przez kontener `pap-backend`.

Komunikacja między kontenerami używa protokołu HTTP - nie musi być szyfrowana, ponieważ jest wewnętrzna dla serwera.

### Reverse Proxy
Ze względu na to, że ten sam serwer obsługuje również inne subdomeny, wykorzystujemy reverse proxy (Nginx), nie jest
przedmiotem tego projektu.

Jedna instancja Nginx obsługuje zapytania HTTP i HTTPS ze świata zewnętrznego i jeśli dotyczą subdomen
`pap.mgarbowski.pl` lub `api.pap.mgarbowski.pl` przekazuje zapytania do odpowiednich kontenerów.

Nginx obsługuje komunikację HTTPS, zapytania klientów do subdomen `mgarbowski.pl` są szyfrowane. Zapytania HTTP
(port 80) są automatycznie przekierowywane na port 443 (HTTPS).

Używamy certyfikatów wystawionych przez [Let's Encrypt](https://letsencrypt.org/). Za pozyskiwanie i okresowe
odświeżanie certyfikatów odpowiada program [certbot](https://certbot.eff.org/)

### Serwer HTTP
Kolejna instancja Nginx serwująca statyczne pliki aplikacji frontendowej - aplikacja React spakowana do jednego
pliku `.js`.

### Aplikacja Spring Boot
REST API przyjmuje zapytania i zwraca odpowiedzi w formacie JSON, komunikuje się z bazą danych

### Baza danych
Relacyjna baza danych PostgreSQL. Dane są nieulotne, przechowywane w Docker Volume w katalogu `/srv/pap/.data`

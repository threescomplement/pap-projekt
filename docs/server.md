# Praca z serwerem

Wdrażamy aplikację na serwerze dostępnym pod domeną `mgarbowski.pl`.
Jest to Raspberry Pi 4B z procesorem w architekturze aarch64 (arm64/v8) z systemem Ubuntu Server 23.04.
Infrastrukturą zarządza Mikołaj Garbowski.

## SSH

Mamy dostęp do serwera przez SSH, z uwierzytelnieniem kluczem publicznym, nie ma możliwości użycia hasła.
Usługa jest dostępna na niestandardowym porcie `2222`.

```shell
ssh -p 2222 <username>@mgarbowski.pl
```

## Praca z serwerem

Programiści i użytkownik bota CI/CD należą do grup `pap-admins` i `docker`.
Wszelkie pliki związane z projektem powinny należeć do grupy `pap-admins` i znajdować się w katalogu `/srv/pap`.

## Firewall

Firewall w routerze przepuszcza jedynie porty 80, 443 i 2222. Na serwerze jest też uruchomiony firewall `ufw`.

## Deployment

Sczegóły opisane w [deployment](./deployment.md)

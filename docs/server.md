# Praca z serwerem
Wdrażamy aplikację na serwerze dostępnym pod domeną `mgarbowski.pl`. 
Jest to Raspberry Pi 4B z procesorem w architekturze aarch64 (arm64/v8) z systemem Ubuntu Server 23.04 i znajduje się u mnie w domu ;)

## SSH
Mamy dostęp do serwera przez SSH, z uwierzytelnieniem kluczem publicznym, nie ma możliwości użycia hasła.
Usługa jest dostępna na niestandardowym porcie `2222`.

```shell
ssh -p 2222 <username>@mgarbowski.pl
```

Polecam dodać na własnym komputerze dodać alias, żeby nie musieć za każdym razem pisać pełnej komendy.

Żeby logować się na serwer komendą `pi`, do pliku `~/.bashrc` należy dodać linijkę:

```shell
alias pi="ssh -p 2222 <username>@mgarbowski.pl"
```

## Praca z serwerem
Wszyscy należymy do grupy `pap-admins` i wszelkie pliki związane z projektem powinny należeć do tej grupy
i znajdować się w katalogu `/srv/pap`.

Wszyscy należymy również do grupy `docker`, przez co możemy zarządzać Dockerem.

Nie macie `sudo` ale i tak proszę o ostrożność i nie psucie mojego setupu z aktywnymi kontenerami, nie śmiecenie itd.

## Firewall
W momencie pisania tego dokumentu, firewall w routerze przepuszcza jedynie porty 80, 443 i 2222.
Na serwerze jest też uruchomiony firewall `ufw`.

Nie chcę otwierać firewalla na świat dopóki aplikacja nie będzie odpowiednio zabezpieczona.
Na własny użytek można to ominąć przez [tunelowanie SSH](https://linuxize.com/post/how-to-setup-ssh-tunneling/)
gdybyście mieli taką potrzebę.

## Deployment
Sczegóły opisane w [deployment](./deployment.md)

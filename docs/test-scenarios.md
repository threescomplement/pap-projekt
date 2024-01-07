# Scenariusze testów end-to-end

## Logowanie i rejestracja użytkownika

### Prawidłowa rejestracja
1. Wejdź w zakładkę Rejestracja
2. Wypełnij formularz prawidłowymi wartościami
3. Wciśnij przycisk Rejestracja
4. Przekierowanie na stronę Rejestracja Ukończona
5. Przychodzi mail na podany adres
6. Link w mailu prowadzi do strony z przyciskiem Potwierdź
7. Po potwierdzeniu użytkownik może się zalogować podanym loginem i hasłem

### Zajęty login
1. Wejdź w zakładkę Rejestracja
2. Wypełnij formularz, podaj login istniejącego użytkownika
3. Wyświetla się komunikat, że login jest już zajęty

### Zajęty adres email
1. Wejdź w zakładkę Rejestracja
2. Wypełnij formularz, podaj email istniejącego użytkownika
3. Wyświetla się komunikat, że istnieje już użytkownik o podanym adresie

### Prawidłowe
1. Wejdź w zakładkę Login
2. Podaj login i hasło istniejącego użytkownika (admin, password)
3. Przekierowanie na stronę własnego profilu

### Nieprawidłowe dane logowania
1. Wejdź w zakładkę Login
2. Podaj nieprawidłowy login lub hasło
3. Wyświetla się komunikat "nieprawidłowy login lub hasło"

### Konto nie zostało potwierdzone
1. Wejdź w zakładkę Rejestracja
2. Wypełnij i prześlij formularz rejestracji z prawidłowymi danymi
3. Następuje przekierowanie, wciśnij przycisk Login bez potwierdzania konta linkiem wysłanym na maila
4. Przekierowanie do formularza do logowania
5. Wypełnij i prześlij z wcześniej podanym loginem i hasłem
6. Wyświetla się komunikat "potwierdź adres email przed zalogowaniem się"

### Zmiana hasła
1. Zarejestruj konto na swój adres email i zaloguj się
2. Wyloguj się
3. Naciśnij "Nie pamiętam hasła"
4. Podaj email i poproś o wysłanie wiadomości
5. Wyświetla się komunikat, że wysłano link na podany adres
6. Wejdź w link w wysłanym mailu
7. Podaj nowe hasło i wciśnij przycisk
8. Zaloguj się używając loginu i nowego hasła
 
## Panel admina

### Dostęp do panelu
1. Zaloguj się na konto administratora (admin, password)
2. Sprawdź czy na pasku pojawia się link Panel administratora i przejdź do panelu
3. Wyloguj się
4. Zaloguj się na konto zwykłego użytkownika (rdeckard, password)
5. Upewnij się, że na pasku obok "Welcome rdeckard" nie pojawia się link do panelu administratora

### Importer, prawidłowe użycie
1. Zaloguj się na konto administratora (admin, password)
2. Wybierz w panelu plik z danymi do zaimportowania (do pobrania https://github.com/mGarbowski/sjo-scraper/blob/main/courses.json)
3. Wciśnij przycisk "Zaimportuj dane"
4. Wyświetla się komunikat o prawidłowym zaimportowaniu
5. Wejdź w zakładkę Kursy, zobacz czy pojawiają się kursy z podanego pliku
6. Wejdź w zakładkę Lektorzy, zobacz czy pojawiają się lektorzy z podanego pliku

### Importer, nieprawidłowy plik
1. Zaloguj się na konto administratora (admin, password)
2. Wybierz w panelu nieprawidłowy plik z danymi do zaimportowania (obrazek, json nie pasujący do schematu)
3. Wciśnij przycisk "Zaimportuj dane"
4. Wyświetla się komunikat o błędzie

### Usuwanie czyjegoś konta
1. Zaloguj się na konto użytkownika (rdeckard, password)
2. Wyloguj się
3. Zaloguj się na konto administratora (admin, password)
4. Przejdź do panelu administratora
5. Usuń konto rdeckard
6. Wyloguj się
7. Spróbuj zalogować się na konto rdeckard
8. Wyświetla się komunikat o nieudanym logowaniu

### Nadania uprawnień administratora
1. Zaloguj się na konto użytkownika (rdeckard, password)
2. Upewnij się, że na pasku nie ma linku do panelu administratora
3. Wyloguj się
4. Zaloguj się jako administrator (admin, password)
5. Przejdź do panelu administratora
6. Zmień rolę użytkownika rdeckard na ROLE_ADMIN i zapisz
7. Wyloguj się
8. Zaloguj się na konto użytkownika (rdeckard, password)
9. Upewnij się, że możesz teraz przejść do panelu administratora

### Ręczna aktywacja konta
1. Zarejestruj nowe konto, bez potwierdzania maila
2. Zaloguj się na konto administratora
3. Przejdź do panelu admina
4. Upewnij się, że wcześniej utworzone konto nie jest aktywne
5. Ustaw konto jako aktywne i zapisz zmiany
6. Wyloguj się i zaloguj jako wcześniej zarejestrowany użytkownik

### Zmiana adresu email
1. Zaloguj się jako administrator
2. Przejdź do panelu administratora
3. Zmień email użytkownika (rdeckard) na inną wartość i zapisz
4. Wyloguj się i zaloguj jako zmieniony użytkownik (rdeckard, password)
5. Upewnij się, że na stronie profilu wyświetla się zmieniony adres email


TODO: obsługa zgłoszeń

## Interakcja z kursami, lektorami i opiniami

### Przeglądanie kursów
1. Zaloguj się jako zwykły użytkownik (rdeckard, password)
2. Wejdź w zakładkę kursy
3. Przetestuj każdy z filtrów język, poziom, typ, moduł pojedynczo i kilka na raz
4. Przetestuj wyszukiwanie po nazwie (niewrażliwe na wielkość liter)
5. Kliknij w nazwę kursu, przekierowanie do strony kursu
6. Wyświetlane są
    * prowadzący
    * moduł, typ, język, pełna nazwa
    * zagregowane oceny użytkowników i ich liczba
    * lista opinii wystawionych przez użytkowników

### Przeglądanie lektorów
1. Zaloguj się jako zwykły użytkownik (rdeckard, password)
2. Wejdź w zakładkę Lektorzy
3. Wyświetla się tabela z wszystkimi prowadzącymi (imię, nazwisko, średnie oceny, liczba opinii)
4. Przetestuj filtrowanie po nauczanym języku i wyszukiwanie po nazwisku
5. Kliknięcie w imię i nazwisko przekierowuje do strony pojedynczego lektora
6. Strona lektora zawiera jego imię i nazwisko, zagregowane oceny i liczbę ocen, tabelę nauczanych kursów i listę opinii użytkowników

### Opinie i komentarze
1. Zaloguj się jako normalny użytkownik (rdeckard, password)
2. Przejdź do zakładki kursy i przejdź do strony pojedynczego kursu
3. Wybierz opcję, napisz opinię i dodaj tekstową opinię i oceny liczbowe
4. Zatwierdź dodawanie opinii
5. Przekierowanie do strony kursu, dodana opinia wyświetla się na liście
6. Naciśnięcie "czytaj więcej" przenosi na stronę pojedynczej opinii
7. Dodaj komentarz, powinien wyświetlić się na liście pod opinią
8. Edytuj własny komentarz
9. Usuń własny komentarz
10. Edytuj własną opinię
11. Usuń własną opinię
12. Przy komentarzach i opiniach innych użytkowników nie wyświetlają się przyciski do usuwania i edycji

### Usuwanie treści jako administrator
1. Zaloguj się jako administrator
2. Przejdź do zakładki kursy i wybierz kurs
3. Przy każdej opinii powinien pojawiać się przycisk do usuwania, ale nie do edycji
4. Usuń opinię z tego poziomu
5. Wybierz opinię i przejdź na jej stronę
6. Przy komentarzach powinny wyświetlać się przyciski do usuwania, ale nie do edycji
7. Usuń jeden z komentarzy
8. Usuń opinię z tego poziomu przyciskiem na górze strony, powinno nastąpić przekierowanie do strony kursu, którego dotyczyła opinia
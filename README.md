# Temat pracy

Wariant gry terenowej/miejskiej opartej na scenariuszu przestrzennym wykorzystującej
urządzenie mobilne jako integralny element rozgrywki (koncepcja zbliżona do geocachingu
i steganografii przestrzennej).

Zadaniem gracza jest odszukanie kolejnych nieznanych obiektów (przedmiotów lub grup
przedmiotów), których lokalizacja jest podawana jedynie w sposób przybliżony na podstawie
współrzędnych GPS. Potwierdzenie odnalezienia przedmiotu jest realizowane poprzez wy-
konanie zdjęcia, rozpoznanie przez urzadzenie obiektów na zdjęciu i weryfikację czy obiekt
poszukiwany się tam znajduje. Potwierdzony obiekt odblokowuje wskazówkę odnoszącą do
lokalizacji kolejnego przedmiotu.

Wystartowanie rozgrywki jest realizowane poprzez wczytanie odpowiedniego kodu QR
przez gracza. Kod QR daje informację o adresie URL, skąd aplikacja automatycznie pobiera
dane gry. Po stronie serwera dostępne powinny być statystyki graczy w tym informacja stanie
gry.

Do obsługi kodów QR oraz rozpoznawania obiektów na obrazie można wykorzystać
dostępne biblioteki programistyczne.

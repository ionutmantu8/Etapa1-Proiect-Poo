# *Etapa 1 Proiect Poo*
## Mantu Ionut - 323CA

### Descriere generala

Am inceput implementarea temei prin creearea pachetelor `user`, `banking` si `commandutils`.

In pachetul `user` am definit clasele `User`, `Account` si `Card`. In logica mea de implementare, fiecare utilizator are mai multe tranzactii, comercianti catre care face plati si conturi, fiecare cont avand mai multe carduri.

In pachetul `banking` se afla clasele `Transaction`, `ExchangeRate` si `Commerciants` cu campurile necesare fiecaruia. De asemenea, clasa `Bank` contine o functie de tipul `ArrayNode`, `startBanking`, care este entry point-ul catre implementarea mea. In aceasta functie, imi iau toate datele din input si apoi iterez prin toate comenzile, facand ceea ce se cere la fiecare, iar apoi intorc `ArrayNode`-ul care trebuie scris la output.

In implementarea comenzilor, mi-am definit cate o clasa pentru fiecare dintre ele si, observand ca tot trebuiesc adaugate functionalitati, am utilizat `Visitor Design Pattern`, facand noile functionalitati mai usor de adaugat prin adaugarea unei noi "locatii" unde visitorul sa viziteze si sa faca ceea ce este necesar. De asemenea, observand ca trebuie sa initializez obiecte pentru fiecare comanda, m-am gandit si la folosirea `Factory Design Pattern` pentru a crea obiectele necesare in functie de comanda primita.De asemenea clasele se mostenesc une pe alta la comenzi,in functie de parametrii care sunt necesari in implementarea acesteia.
### Implementarea comenzilor

#### Add Account
-   Se cauta in lista de useri user-ul cu emailul asociat la input si daca acesta exista, ii creez un cont nou si adaug tranzactia corespunzatoare in lista de tranzactii.

#### Add Funds
-   Se cauta in fiecare lista de conturi a fiecarui user contul dat ca input si daca acesta exista, balanta sa va creste cu cat s-a dorit in input.

#### Create Card
-   Se cauta contul asociat unui user si se creeaza un nou card pentru acel cont, adaugand tranzactia corespunzatoare in lista de tranzactii.

#### Print Users
-   Printeaza lista de utilizatori impreuna cu toate conturile si cardurile lor.

#### Delete Account
-   Se cauta contul asociat unui user si se sterge daca nu are fonduri, adaugand tranzactia corespunzatoare in lista de tranzactii.Daca inca mai sunt fonduri se da un mesaj de eroare,iar contul nu va fi sters.

#### Create One-Time Card
-   Se creeaza un card de unica folosinta pentru un cont, adaugand tranzactia corespunzatoare in lista de tranzactii.

#### Delete Card
-   Se sterge un card asociat unui cont, adaugand tranzactia corespunzatoare in lista de tranzactii.

#### Pay Online
-   Proceseaza o plata online folosind un card, actualizand soldul contului daca sunt suficienti bani dupa convertirea sumei de platit in moneda contului, iar daca acesta a fost One Time cardul va fi sters si altul nou va fi creat,adaugand tranzactia corespunzatoare in lista de tranzactii.De asemena daca cardul este inghetat plata nu se va face sau daca cardul are mai putini bani decat balanta minima sau daca nu are suficienti bani pentru a face plata cardul se va ingheta si plata nu se va realiza.

#### Send Money
-   Trimite bani de la un cont la altul, daca sender-ul are suficienti bani in moneda sa.Receiver-ul este cautat fie dupa IBAN fie dupa alias.Se actualizeaza soldurile conturilor si adaugand tranzactiile corespunzatoare in listele de tranzactii ale utilizatorilor.

#### Set Alias
-   Seteaza un alias pentru un cont.

#### Print Transactions
-   Printeaza toate tranzactiile realizate de un user.

#### Set Min Balance
-   Seteaza soldul minim pentru un cont.

#### Check Card Status
-   Verifica statusul unui card si actualizeaza tranzactiile corespunzatoare.

#### Change Interest Rate
-   Schimba rata dobanzii pentru un cont de economii, adaugand tranzactia corespunzatoare in lista de tranzactii.

#### Split Payment
-   Proceseaza o plata impartita intre mai multe conturi,iar daca un cont nu are suficienti bani in moneda sa plata nu se va realiza, actualizand soldurile conturilor si adaugand tranzactiile corespunzatoare in listele de tranzactii ale utilizatorilor.

#### Report
-   Genereaza un raport pentru un cont, incluzand tranzactiile dintr-o perioada specificata.

#### Spendings Report
-   Genereaza un raport al cheltuielilor pentru un cont, incluzand tranzactiile dintr-o perioada specificata.

#### Add Interest
-   Adauga dobanda unui cont de economii, actualizand soldul contului si adaugand tranzactia corespunzatoare in lista de tranzactii.
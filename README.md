# Webchat aplikace

Semestrální projekt z předmětu NSS

## Požadavky
* vyber vhodne technologie: 
  * Java Spring Boot a React
* vyuziti spolecne DB: 
  * PostgreSQL
* vyuziti cache:
  * Hazelcast
* vyuziti messaging principu: 
  * Kafka
* zabezpeceni pomoci bud basic authorization nebo pomoci OAuth2: 
  * Spring Security
* vyuziti Inteceptors: 
  * trida LoggingInterceptor - slouzi k logovani authorizace
* vyuziti jedne z technologie (SOAP, REST, graphQL, Java RMI, Corba, XML-RPC): 
  * REST a WebSocket
* nasazeni na produkcni server: 
  * https://649d9ffffbb27c00089fc890--famous-stroopwafel-b987b1.netlify.app/ (Frontend část aplikace.
    Nepodařilo se nastavit WebSocket spojení, a proto se nedá po přihlašení načist date ze serveru)
* vyber vhodne architektury: 
  * Event Base
* inicializacni postup: 
  * [Program start](#program-start)
* vyuziti elasticsearch: 
  * není
* pouziti alespon 5 design patternu: 
  * Observer, Chain of responsibility, Builder, Template Method, Visitor
* use cases: 
    - Zaregistrovat se/Přihlásit se
    - Napsat otázku/odpověď <- extend u otázky: Zvolit kategorii otázky
    - Změnit osobní údaje
    - Otevřít/uzavřít chat
    - Vytvořit/editovat/smazat účet zaměstnance
    - Vytvořit/editovat účet hráče
    - Vytvořit/editovat/smazat kategorie
    - Vytvořit/editovat/smazat role
    - Přidat/odstranit kategorie v chatu


## Program start
1) Spusťte Docker, který slouží pro použití Kafky. Ve složce `/chatgc/kafka_conf` spusťte příkaz:
```
docker compose -f docker-compose.yml up
```
2) Poté ve složce `/chatgc` spusťte Spring aplikaci příkazem (vyžaduje JDK 16+):
```
mvn spring-boot:run
```
5) React aplikace se nachází ve složce `/chat-ui` a spouští se příkazem:
```
npm start
```

## Nastavení přístupu
Aplikace má dva typy uživatelů - Employee a Player. Employee s role 'admin' má přístup ke všemu. 
Pro první inicializaci systému nejsou žádná data předem definována. 
Pokud v systému není zaregistrován žádný admin, může se zaregistrovat na adrese `http://localhost:8080/api/register/emp`, 
kde musí uvést jako body requestu následující informace: **username, email a password**. Tato adresa není dostupná z frontend části aplikace.

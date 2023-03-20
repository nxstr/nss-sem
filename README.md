# nss-sem



## Popis

Semestralni prace predstavuje webchat pro technickou podporu nejakeho projektu, ve kterem mohou komunikovat prihlasene uzivatele s operatorem technicke podpory (zatim nevim jak to adekvatne pojmenovat)

Chatum budou pridelene tzv. tagy, ktere budou oznacovat stav chatu (otazka je vyresena a neco podobne) a pro ktere pracovniky je otazka prirazena.

Vsechny pracovniky budou mit role, vzhledem ke kterym budou mit pristup k chatum s nekterymi tagy. Napriklad: jsou tagy "moderator", "helper", "alert". admin ma pristup ke vsem chatum, vcetne tech ktere tagy nemaji, moderator ma pristup k chatum, ktere maji tag "moderator", "helper", "alert". Helper ma pristup k chatum s tagy "helper", "alert".
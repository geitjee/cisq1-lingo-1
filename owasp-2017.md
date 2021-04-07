# Vulnerability Analysis

## A1:2017 Injection
### Description
Gebruikers kunnen opdrachten naar de database sturen doormiddel van bijvoorbeeld een input veld voor gebruikersnaam. 
Dan wordt een verzoek naar de database gestuurd met de input van de gebruiker.

### Risk
Gebruikers kunnen in hun input een aanval code zetten waardoor ze alle informatie uit de database kunnen krijgen en zelfs systeem commando's kunnen uitvoeren.

### Counter-measures
Je kan alle dynamische verzoeken weghalen maar dan heb je geen gebruiker input meer.
Ook kan je de standaard querry statement loshouden van de gebruiker inputs.
Een hele simpele is om bepaalde karakters niet toe te staan zo zijn veel commando's niet toegestaan als input doordat je bijvoorbeeld geen letters toestaat.
Limiteren van het aantal resultaten dat uit de database kan komen is handig zodat niet alle gegevens uit de database kunnen komen, dit helpt niet tegen de aanvallen maar zorgt wel dat het effect ervan minder groot is.


## A2:2017 Broken Authentication
### Description
Gebruikers kunnen zonder authenticatie op je website komen.
Dit kan doormiddel van o.a. het gokken van inloggegevens.

### Risk
Gebruikers kunnen bij de persoonlijke inhoud van de ingelogde persoon en ook is het mogelijk dat de gebruikte inloggegevens op andere websites te gebruiken zijn.

### Counter-measures
Meerdere authenticatie factoren bijvoorbeeld een code die je via je telefoon/email krijgt als laatste verificatie.
Verplicht moeilijk te raden wachtwoorden, zo kan je instellen dat het wachtwoord tenminste 1 hoofdletter en cijfer moet bevatten.
Een limiet aan het aantal login pogingen.
Door te zorgen dat de *'session id'* snel verloopt is het minder makkelijk voor de volgende computer gebruiker om nog bij de ingelogde webpagina te komen.


## A3:2017 Sensitive Data Exposure
### Description
De webpagina heeft gevoelige data opgeslagen. Aanvallen kunnen deze gevoelige data publiceren.

### Risk
gevoelige data kan gebruikt of gepubliceerd worden.

### Counter-measures
Door TLS en HTTPS te gebruiken versleutel je de webpagina en kan een aanvaller minder makkelijk tussen de gebruiker en webpagina komen.
Door je gevoelige data te versleutelen is deze minder makkelijk te lezen.
Deel je data in en kijk of het nodig is om deze op te slaan.
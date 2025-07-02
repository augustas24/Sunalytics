# team-15

# Hvordan appen kjøres og avhengigheter:

Appen krever internettforbindelse for å fungere optimalt, ettersom den henter data fra eksterne API-er.
Dersom brukeren ikke har internett, vil appen fortsatt kjøre uten å krasje, men funksjonaliteten vil være svært begrenset.
For å bruke funksjonen "Min posisjon", må brukeren gi tillatelse til posisjonstilgang. Dette håndteres via systemets vanlige dialog for tillatelser, enten på fysisk enhet eller emulator.


Appen kan kjøres på en emulator i Android Studio eller direkte på en Android-enhet  

For å kjøre prosjektet i Android Studio:  
1: Hent .zip-filen, og pakk den ut.  
2: I menylinjen, velg Clean - Build Project.  
3: Opprett en emulator av typen Resizable Emulator med API-nivå 34.  
4: Klikk på den grønne pilen ("Run app") øverst i menyen.  

For å kjøre på telefon:  
1: Aktiver "Developer mode" på Android-enheten.  
2: Koble enheten til datamaskinen. Navnet på telefonen dukker opp under "Devices".  
3: Kjør appen i Android Studio. Da installeres den på enheten.  
4: Etter installasjon kan appen åpnes som en vanlig app på enheten.  


# Brukte biblioteker:

Appen benytter seg av flere kjente biblioteker som er gjennomgått i kurset:

Ktor: Brukes for nettverkskommunikasjon og henting av data fra API-er.

Hilt: Håndterer dependency injection for å gjøre koden mer modulær og testbar.

Room: Brukes for lokal datalagring.

Coil: Et lettvektsbibliotek for bildehåndtering i Jetpack Compose.



# I tillegg har vi tatt i bruk noen eksterne biblioteker som ikke har vært dekket i kurset:

Mapbox SDK for Android: Brukes til kartvisning og til reverse geokoding. Mapbox gir stor fleksibilitet og støtter moderne kartvisualisering i Compose.

ComposeCharts (av Ehsannarmani): Et bibliotek som lar oss lage forskjellige typer diagrammer i Jetpack Compose.
Vi har valgt å bruke linjediagrammer både i HomeScreen og WeatherScreen, fordi de gir en tydelig og konsekvent visning av data på tvers av ulike skjermstørrelser.
Diagrammene mates med verdier som Double, og tilhørende etiketter som String.

Mocck: Anvendt til å lage mock-ups av API-responser for å utføre tester.

Ved å kombinere disse bibliotekene har vi kunnet bygge en app som både er informativ og brukervennlig.



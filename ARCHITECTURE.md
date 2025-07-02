# team-15

# Introduksjon

Dette dokumentet beskriver arkitekturen brukt i appen, med mål om å hjelpe utviklere som skal sette seg inn i prosjektet, vedlikeholde det og videreutvikle funksjonaliteten.
Dokumentet forklarer strukturen i prosjektet, teknologivalg og designmønstre, samt hvordan objektorienterte prinsipper som lav kobling og høy kohesjon er ivaretatt.


# Arkitekturmønster: MVVM

Prosjektet benytter en MVVM-arkitektur (Model-View-ViewModel), som skiller presentasjonslogikk fra forretningslogikk.
Dette legger til rette for god testbarhet, gjenbruk og enklere vedlikehold:

View (UI): Inneholder kun presentasjonslag. Den observerer dataeksponeringer fra ViewModel og gjenspeiler tilstanden i UI. Logikk i View holdes til et minimum.

ViewModel: Henter og prosesserer data fra Repository-lag, og eksponerer tilstanden til View via State-objekter.

Model/Repository: Ansvarlig for datainnhenting og forretningslogikk. Henter data fra eksterne API-er via DataSource-klasser og eksponerer det på en strukturert måte.


# Teknologier og biblioteker:

Hilt: Brukes for dependency injection og bidrar til lav kobling ved å injisere avhengigheter der de trengs, uten at klasser må vite hvordan instansene lages.

Room: Lokalt lagringssystem for persistente data.

Ktor: Brukes for å gjøre HTTP-kall mot eksterne API-er.

Coil: Brukes til effektiv bildeinnlasting i Jetpack Compose.

Mapbox SDK for Android: Brukes til kartvisning og reverse geokoding.

Geokoder API (Entur): Benyttes til geokoding.

ComposeCharts (Ehsannarmani): Brukes til å visualisere data gjennom linjediagrammer.

Mocck: Anvendt til å lage mock-ups av API-responser for å utføre tester.


# Modulær struktur og ansvar:

Vi har lagt stor vekt på Separation of Concerns, der hver komponent eller klasse har et tydelig og avgrenset ansvar. Noen eksempler:

Map-funksjonalitet:

    EnturDataSource og MapboxDataSource inngår i GeocodingRepository.

    GeocodingRepository brukes av MapViewModel, som tilhører MapScreen.

Hvakosterstrømmen, Frost og PvGis:

    PriceDataSource inngår i PriceRepository, som eksponerer data til HomeViewModel.

    Sol og vær:

        PvgisDataSource (Solar) og FrostDataSource benytter begge SolarRepository.

        SolarRepository og PriceRepository brukes i HomeViewModel.

    Skjermer og ViewModels:

        HomeScreen benytter både HomeViewModel og MapViewModel, ettersom den viser både lokasjon og data relatert til sol og strømpris.

        WeatherScreen benytter HomeViewModel for å hente allerede tilgjengelig data.

Dette oppsettet gjør det mulig å utvikle og teste hvert element isolert, samtidig som det forenkler videreutvikling og feilsøking.

# Objektorienterte prinsipper

Lav kobling: Oppnås gjennom bruk av Hilt, som gir løs kobling mellom komponenter ved å la avhengigheter injiseres automatisk.

Høy kohesjon: Hver klasse og modul har ett tydelig ansvarsområde. Eksempelvis er PriceRepository kun ansvarlig for å hente og bearbeide strømpriser.

Gjenbruk og utvidbarhet: Arkitekturen legger til rette for enkel utvidelse, f.eks. ved å legge til nye API-kilder som nye DataSource-klasser, som deretter integreres i egne Repository-klasser og ViewModels.


# Designmønstre

MVVM: Gir et tydelig skille mellom visning og logikk.

UDF (Unidirectional Data Flow): Brukt i kommunikasjonen mellom ViewModel og View gjennom State-objekter i Compose. Dette gjør datatilstand og UI-synkronisering forutsigbar og enkel å følge.

# Målplattform og API-nivå

Appen er utviklet for Android API-nivå 26 og oppover. Dette valget ble tatt fordi vi benytter funksjonalitet som krever API 26 eller høyere, spesielt LocalDateTime.now() i forbindelse med tidsreferanser i getTimeReference.
Ved å sette minimum API til 26 sikrer vi støtte for moderne Java 8-tids-API-er, og unngår behovet for kompatibilitetsbiblioteker eller andre  workarounds. Vi har primært testet og utviklet applikasjonen i en Resizable Emulator med API-nivå 34. På dette nivået vises alle visuelle komponenter som tiltenkt.

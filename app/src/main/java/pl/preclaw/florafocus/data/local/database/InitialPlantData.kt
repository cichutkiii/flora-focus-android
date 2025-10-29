package pl.preclaw.florafocus.data.local.database

import pl.preclaw.florafocus.data.local.entities.*

/**
 * Initial seed data for PlantCatalog
 * 
 * Contains 8 example plants with full data:
 * - 5 vegetables (tomato, cucumber, lettuce, pepper, carrot)
 * - 2 herbs (basil, parsley)
 * - 1 flower (marigold)
 */
object InitialPlantData {

    fun getInitialPlants(): List<PlantCatalogEntity> {
        return listOf(
            getTomato(),
            getCucumber(),
            getLettuce(),
            getPepper(),
            getCarrot(),
            getBasil(),
            getParsley(),
            getMarigold()
        )
    }

    // ==================== VEGETABLES ====================

    /**
     * Pomidor (Tomato) - Solanum lycopersicum
     * Family: Solanaceae
     * Classic garden vegetable with full growth cycle
     */
    private fun getTomato(): PlantCatalogEntity {
        return PlantCatalogEntity(
            id = "plant_tomato_001",
            commonName = "Pomidor",
            latinName = "Solanum lycopersicum",
            family = "Solanaceae",
            plantType = PlantType.VEGETABLE,
            
            // Growing Requirements
            lightRequirements = LightRequirements.FULL_SUN,
            soilType = "Próchniczna, żyzna, przepuszczalna",
            soilPHMin = 6.0f,
            soilPHMax = 7.0f,
            wateringFrequency = WateringFrequency.DAILY,
            growthDifficulty = GrowthDifficulty.MEDIUM,
            
            // Properties
            toxicity = false,
            edible = true,
            hardiness = "Roślina jednoroczna, wrażliwa na mróz",
            
            // Companion Planting
            companionPlantIds = listOf("plant_basil_001", "plant_marigold_001"),
            incompatiblePlantIds = listOf("plant_potato_001", "plant_pepper_001"), // same family
            
            // Growing Periods
            sowingPeriodStart = "03-15", // 15 marca
            sowingPeriodEnd = "05-15",   // 15 maja
            harvestPeriodStart = "07-01", // lipiec
            harvestPeriodEnd = "10-15",   // październik
            daysToHarvestMin = 60,
            daysToHarvestMax = 85,
            averageYield = "5-8 kg z rośliny",
            
            // Growth Phases
            growthPhases = listOf(
                GrowthPhaseData(
                    id = "tomato_phase_germination",
                    phaseName = GrowthPhaseName.GERMINATION,
                    displayName = "Kiełkowanie",
                    averageDurationDaysMin = 7,
                    averageDurationDaysMax = 14,
                    description = "Nasiona kiełkują w ciepłym podłożu",
                    careInstructions = listOf(
                        "Temperatura 20-25°C",
                        "Utrzymuj podłoże wilgotne ale nie mokre",
                        "Osłoń folią do momentu skiełkowania"
                    ),
                    visualIndicators = listOf("Pojawienie się liścieni"),
                    autoTasks = listOf(
                        AutoTaskData(
                            taskTitle = "Sprawdź kiełkowanie",
                            taskDescription = "Sprawdź czy nasiona skiełkowały. Usuń folię jeśli tak.",
                            taskType = TaskType.CUSTOM,
                            triggerDayOffset = 7,
                            priority = TaskPriority.MEDIUM
                        )
                    )
                ),
                GrowthPhaseData(
                    id = "tomato_phase_vegetative",
                    phaseName = GrowthPhaseName.VEGETATIVE,
                    displayName = "Wzrost wegetatywny",
                    averageDurationDaysMin = 30,
                    averageDurationDaysMax = 40,
                    description = "Roślina rozwija łodygę i liście",
                    careInstructions = listOf(
                        "Podlewaj regularnie, unikaj moczenia liści",
                        "Nawóż azotem co 2 tygodnie",
                        "Przesadź na stałe miejsce po 6 tygodniach"
                    ),
                    visualIndicators = listOf("Rozwój prawdziwych liści", "Wzrost łodygi"),
                    autoTasks = listOf(
                        AutoTaskData(
                            taskTitle = "Przygotuj podpory",
                            taskDescription = "Przygotuj paliki lub sznurki do podpierania pomidorów",
                            taskType = TaskType.STAKING,
                            triggerDayOffset = 20,
                            priority = TaskPriority.HIGH
                        ),
                        AutoTaskData(
                            taskTitle = "Nawóź pomidory",
                            taskDescription = "Nawóź nawozem azotowym (NPK 10-5-5)",
                            taskType = TaskType.FERTILIZING,
                            triggerDayOffset = 14,
                            priority = TaskPriority.MEDIUM
                        )
                    )
                ),
                GrowthPhaseData(
                    id = "tomato_phase_flowering",
                    phaseName = GrowthPhaseName.FLOWERING,
                    displayName = "Kwitnienie",
                    averageDurationDaysMin = 14,
                    averageDurationDaysMax = 21,
                    description = "Pojawiają się kwiaty, rozpoczyna się zapylanie",
                    careInstructions = listOf(
                        "Zwiększ nawożenie potasem i fosforem (NPK 5-10-10)",
                        "Delikatnie potrząsaj rośliny dla lepszego zapylenia",
                        "Usuń pędy boczne (pasynkowanie)"
                    ),
                    visualIndicators = listOf("Żółte kwiaty", "Zapach kwiatów"),
                    autoTasks = listOf(
                        AutoTaskData(
                            taskTitle = "Pasynkowanie",
                            taskDescription = "Usuń pędy boczne (pasynki) wyrastające z kątów liści",
                            taskType = TaskType.PINCHING,
                            triggerDayOffset = 7,
                            priority = TaskPriority.HIGH
                        ),
                        AutoTaskData(
                            taskTitle = "Zmień nawóz",
                            taskDescription = "Przejdź na nawóz potasowo-fosforowy (NPK 5-10-10)",
                            taskType = TaskType.FERTILIZING,
                            triggerDayOffset = 0,
                            priority = TaskPriority.MEDIUM
                        )
                    )
                ),
                GrowthPhaseData(
                    id = "tomato_phase_fruiting",
                    phaseName = GrowthPhaseName.FRUITING,
                    displayName = "Owocowanie",
                    averageDurationDaysMin = 30,
                    averageDurationDaysMax = 45,
                    description = "Zawiązują się i rosną owoce",
                    careInstructions = listOf(
                        "Kontynuuj pasynkowanie co tydzień",
                        "Nawóż potasem co 10 dni",
                        "Sprawdzaj podpory - ciężkie owoce"
                    ),
                    visualIndicators = listOf("Zielone owoce", "Zwiększający się rozmiar"),
                    autoTasks = listOf(
                        AutoTaskData(
                            taskTitle = "Pasynkowanie tygodniowe",
                            taskDescription = "Cotygodniowe usuwanie pędów bocznych",
                            taskType = TaskType.PINCHING,
                            triggerDayOffset = 7,
                            priority = TaskPriority.HIGH
                        ),
                        AutoTaskData(
                            taskTitle = "Sprawdź podpory",
                            taskDescription = "Upewnij się że podpory są mocne - owoce przybierają na wadze",
                            taskType = TaskType.SUPPORT_CHECK,
                            triggerDayOffset = 15,
                            priority = TaskPriority.MEDIUM
                        )
                    )
                ),
                GrowthPhaseData(
                    id = "tomato_phase_ripening",
                    phaseName = GrowthPhaseName.RIPENING,
                    displayName = "Dojrzewanie",
                    averageDurationDaysMin = 10,
                    averageDurationDaysMax = 20,
                    description = "Owoce zmieniają kolor i dojrzewają",
                    careInstructions = listOf(
                        "Ogranicz podlewanie - lepszy smak",
                        "Zbieraj na bieżąco dojrzałe owoce",
                        "Usuń chore lub uszkodzone owoce"
                    ),
                    visualIndicators = listOf("Zmiana koloru z zielonego na czerwony", "Miękkie owoce"),
                    autoTasks = listOf(
                        AutoTaskData(
                            taskTitle = "Pierwszy zbiór",
                            taskDescription = "Zbierz pierwsze dojrzałe pomidory!",
                            taskType = TaskType.HARVESTING,
                            triggerDayOffset = 10,
                            priority = TaskPriority.HIGH
                        )
                    )
                )
            ),
            
            // Media & Tags
            imageUrls = emptyList(),
            tags = listOf("warzywo", "owoc", "jednoroczne", "jadalne", "popularne"),
            
            // Description
            description = "Pomidor to jedna z najpopularniejszych upraw w ogrodach przydomowych. " +
                    "Wymaga ciepła, słońca i regularnej pielęgnacji, ale nagradza obfitymi plonami " +
                    "smacznych owoców.",
            careInstructions = "Podlewaj regularnie u podstawy rośliny. Unikaj moczenia liści. " +
                    "Pasynkuj co tydzień. Nawóż co 2 tygodnie nawozem wieloskładnikowym.",
            
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }

    /**
     * Ogórek (Cucumber) - Cucumis sativus
     * Family: Cucurbitaceae
     */
    private fun getCucumber(): PlantCatalogEntity {
        return PlantCatalogEntity(
            id = "plant_cucumber_001",
            commonName = "Ogórek",
            latinName = "Cucumis sativus",
            family = "Cucurbitaceae",
            plantType = PlantType.VEGETABLE,
            
            lightRequirements = LightRequirements.FULL_SUN,
            soilType = "Żyzna, próchniczna, dobrze nawodniona",
            soilPHMin = 6.0f,
            soilPHMax = 7.0f,
            wateringFrequency = WateringFrequency.DAILY,
            growthDifficulty = GrowthDifficulty.EASY,
            
            toxicity = false,
            edible = true,
            hardiness = "Roślina jednoroczna, wrażliwa na mróz",
            
            companionPlantIds = listOf("plant_lettuce_001"),
            incompatiblePlantIds = listOf("plant_tomato_001"), // konkurencja o wodę
            
            sowingPeriodStart = "04-15",
            sowingPeriodEnd = "06-15",
            harvestPeriodStart = "06-15",
            harvestPeriodEnd = "09-30",
            daysToHarvestMin = 50,
            daysToHarvestMax = 70,
            averageYield = "10-15 kg z rośliny",
            
            growthPhases = listOf(
                GrowthPhaseData(
                    id = "cucumber_phase_germination",
                    phaseName = GrowthPhaseName.GERMINATION,
                    displayName = "Kiełkowanie",
                    averageDurationDaysMin = 5,
                    averageDurationDaysMax = 10,
                    description = "Szybkie kiełkowanie w ciepłym podłożu",
                    careInstructions = listOf(
                        "Temperatura min. 18°C",
                        "Utrzymuj wysoką wilgotność"
                    ),
                    visualIndicators = listOf("Liścienie wychodzą z ziemi"),
                    autoTasks = emptyList()
                ),
                GrowthPhaseData(
                    id = "cucumber_phase_vegetative",
                    phaseName = GrowthPhaseName.VEGETATIVE,
                    displayName = "Wzrost pędów",
                    averageDurationDaysMin = 20,
                    averageDurationDaysMax = 30,
                    description = "Roślina rozwija pędy i liście",
                    careInstructions = listOf(
                        "Podlewaj obficie codziennie",
                        "Przygotuj podpory lub siatkę do wspinania"
                    ),
                    visualIndicators = listOf("Długie pędy", "Duże liście"),
                    autoTasks = listOf(
                        AutoTaskData(
                            taskTitle = "Postaw podpory",
                            taskDescription = "Ogórki potrzebują podpór do wspinania się",
                            taskType = TaskType.STAKING,
                            triggerDayOffset = 15,
                            priority = TaskPriority.HIGH
                        )
                    )
                ),
                GrowthPhaseData(
                    id = "cucumber_phase_flowering",
                    phaseName = GrowthPhaseName.FLOWERING,
                    displayName = "Kwitnienie",
                    averageDurationDaysMin = 10,
                    averageDurationDaysMax = 14,
                    description = "Żółte kwiaty - męskie i żeńskie",
                    careInstructions = listOf(
                        "Kontynuuj obfite podlewanie",
                        "Nie stosuj pestycydów - pszczoły potrzebne do zapylania"
                    ),
                    visualIndicators = listOf("Żółte kwiaty"),
                    autoTasks = emptyList()
                ),
                GrowthPhaseData(
                    id = "cucumber_phase_fruiting",
                    phaseName = GrowthPhaseName.FRUITING,
                    displayName = "Owocowanie",
                    averageDurationDaysMin = 15,
                    averageDurationDaysMax = 20,
                    description = "Owoce szybko rosną",
                    careInstructions = listOf(
                        "Zbieraj regularnie - stymuluje dalsze owocowanie",
                        "Podlewaj rano lub wieczorem"
                    ),
                    visualIndicators = listOf("Małe ogórki po kwiatach"),
                    autoTasks = listOf(
                        AutoTaskData(
                            taskTitle = "Zbiór ogórków",
                            taskDescription = "Zbieraj ogórki co 2-3 dni - młode są najsmaczniejsze",
                            taskType = TaskType.HARVESTING,
                            triggerDayOffset = 5,
                            priority = TaskPriority.HIGH
                        )
                    )
                )
            ),
            
            imageUrls = emptyList(),
            tags = listOf("warzywo", "jadalne", "łatwe", "szybki wzrost"),
            description = "Ogórek to popularne warzywo o dużych wymaganiach wodnych. " +
                    "Szybko rośnie i obficie owocuje przez całe lato.",
            careInstructions = "Podlewaj codziennie, najlepiej rano. Zbieraj owoce regularnie. " +
                    "Prowadź pędy na podporach.",
            
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }

    /**
     * Sałata (Lettuce) - Lactuca sativa
     * Family: Asteraceae
     */
    private fun getLettuce(): PlantCatalogEntity {
        return PlantCatalogEntity(
            id = "plant_lettuce_001",
            commonName = "Sałata",
            latinName = "Lactuca sativa",
            family = "Asteraceae",
            plantType = PlantType.VEGETABLE,
            
            lightRequirements = LightRequirements.PARTIAL_SUN,
            soilType = "Lekka, próchniczna",
            soilPHMin = 6.0f,
            soilPHMax = 7.5f,
            wateringFrequency = WateringFrequency.EVERY_2_DAYS,
            growthDifficulty = GrowthDifficulty.EASY,
            
            toxicity = false,
            edible = true,
            hardiness = "Odporna na chłód, wrażliwa na upał",
            
            companionPlantIds = listOf("plant_cucumber_001", "plant_carrot_001"),
            incompatiblePlantIds = emptyList(),
            
            sowingPeriodStart = "03-15",
            sowingPeriodEnd = "08-31",
            harvestPeriodStart = "05-01",
            harvestPeriodEnd = "10-31",
            daysToHarvestMin = 40,
            daysToHarvestMax = 60,
            averageYield = "1 główka z rośliny",
            
            growthPhases = listOf(
                GrowthPhaseData(
                    id = "lettuce_phase_germination",
                    phaseName = GrowthPhaseName.GERMINATION,
                    displayName = "Kiełkowanie",
                    averageDurationDaysMin = 7,
                    averageDurationDaysMax = 14,
                    description = "Kiełkuje w chłodniejszych warunkach",
                    careInstructions = listOf("Temperatura 10-20°C", "Utrzymuj wilgotność"),
                    visualIndicators = listOf("Małe liścienie"),
                    autoTasks = emptyList()
                ),
                GrowthPhaseData(
                    id = "lettuce_phase_leaf_growth",
                    phaseName = GrowthPhaseName.LEAF_GROWTH,
                    displayName = "Wzrost liści",
                    averageDurationDaysMin = 30,
                    averageDurationDaysMax = 45,
                    description = "Formowanie główki lub rozetki liści",
                    careInstructions = listOf(
                        "Podlewaj regularnie",
                        "Unikaj przesuszenia - sałata będzie gorzka"
                    ),
                    visualIndicators = listOf("Coraz większa rozetka liści"),
                    autoTasks = listOf(
                        AutoTaskData(
                            taskTitle = "Sprawdź glebę",
                            taskDescription = "Upewnij się że gleba jest wilgotna",
                            taskType = TaskType.SOIL_CHECK,
                            triggerDayOffset = 15,
                            priority = TaskPriority.MEDIUM
                        )
                    )
                ),
                GrowthPhaseData(
                    id = "lettuce_phase_harvest",
                    phaseName = GrowthPhaseName.HARVEST,
                    displayName = "Zbiór",
                    averageDurationDaysMin = 5,
                    averageDurationDaysMax = 10,
                    description = "Gotowa do zbioru",
                    careInstructions = listOf("Zbierz przed wybiciem szypuły kwiatowej"),
                    visualIndicators = listOf("Zwarta główka", "Odpowiedni rozmiar"),
                    autoTasks = listOf(
                        AutoTaskData(
                            taskTitle = "Zbierz sałatę",
                            taskDescription = "Sałata gotowa do zbioru!",
                            taskType = TaskType.HARVESTING,
                            triggerDayOffset = 0,
                            priority = TaskPriority.HIGH
                        )
                    )
                )
            ),
            
            imageUrls = emptyList(),
            tags = listOf("warzywo", "jadalne", "łatwe", "szybki wzrost", "chłodne pory roku"),
            description = "Sałata to szybko rosnące warzywo liściowe, idealne dla początkujących. " +
                    "Można ją siać od wczesnej wiosny do późnego lata.",
            careInstructions = "Podlewaj regularnie, unikaj przesuszenia. Zbieraj młode liście " +
                    "lub całą główkę przed wybiciem kwiatostanu.",
            
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }

    /**
     * Papryka (Pepper) - Capsicum annuum
     * Family: Solanaceae
     */
    private fun getPepper(): PlantCatalogEntity {
        return PlantCatalogEntity(
            id = "plant_pepper_001",
            commonName = "Papryka",
            latinName = "Capsicum annuum",
            family = "Solanaceae",
            plantType = PlantType.VEGETABLE,
            
            lightRequirements = LightRequirements.FULL_SUN,
            soilType = "Żyzna, próchniczna, dobrze odwodniona",
            soilPHMin = 6.0f,
            soilPHMax = 7.0f,
            wateringFrequency = WateringFrequency.EVERY_2_DAYS,
            growthDifficulty = GrowthDifficulty.MEDIUM,
            
            toxicity = false,
            edible = true,
            hardiness = "Roślina jednoroczna, wrażliwa na mróz i chłód",
            
            companionPlantIds = listOf("plant_basil_001"),
            incompatiblePlantIds = listOf("plant_tomato_001"), // same family - disease risk
            
            sowingPeriodStart = "02-15",
            sowingPeriodEnd = "04-15",
            harvestPeriodStart = "07-15",
            harvestPeriodEnd = "10-31",
            daysToHarvestMin = 70,
            daysToHarvestMax = 90,
            averageYield = "2-4 kg z rośliny",
            
            growthPhases = listOf(
                GrowthPhaseData(
                    id = "pepper_phase_germination",
                    phaseName = GrowthPhaseName.GERMINATION,
                    displayName = "Kiełkowanie",
                    averageDurationDaysMin = 10,
                    averageDurationDaysMax = 21,
                    description = "Powolne kiełkowanie - potrzebuje ciepła",
                    careInstructions = listOf(
                        "Temperatura 25-28°C",
                        "Wilgotne podłoże",
                        "Cierpliwość - może trwać nawet 3 tygodnie"
                    ),
                    visualIndicators = listOf("Liścienie"),
                    autoTasks = emptyList()
                ),
                GrowthPhaseData(
                    id = "pepper_phase_vegetative",
                    phaseName = GrowthPhaseName.VEGETATIVE,
                    displayName = "Wzrost wegetatywny",
                    averageDurationDaysMin = 40,
                    averageDurationDaysMax = 55,
                    description = "Rozwój krzaka i liści",
                    careInstructions = listOf(
                        "Przesadź na stałe po przymrozkach",
                        "Nawóż azotem",
                        "Zapewnij ciepło (min. 15°C w nocy)"
                    ),
                    visualIndicators = listOf("Rozgałęziona roślina", "Ciemnozielone liście"),
                    autoTasks = listOf(
                        AutoTaskData(
                            taskTitle = "Nawóż paprykę",
                            taskDescription = "Nawóż nawozem azotowym",
                            taskType = TaskType.FERTILIZING,
                            triggerDayOffset = 20,
                            priority = TaskPriority.MEDIUM
                        )
                    )
                ),
                GrowthPhaseData(
                    id = "pepper_phase_flowering",
                    phaseName = GrowthPhaseName.FLOWERING,
                    displayName = "Kwitnienie",
                    averageDurationDaysMin = 14,
                    averageDurationDaysMax = 21,
                    description = "Małe białe kwiaty",
                    careInstructions = listOf(
                        "Zmniejsz nawożenie azotem",
                        "Zwiększ fosfor i potas",
                        "Unikaj dużych wahań temperatury"
                    ),
                    visualIndicators = listOf("Białe/kremowe kwiaty"),
                    autoTasks = emptyList()
                ),
                GrowthPhaseData(
                    id = "pepper_phase_fruiting",
                    phaseName = GrowthPhaseName.FRUITING,
                    displayName = "Owocowanie",
                    averageDurationDaysMin = 20,
                    averageDurationDaysMax = 35,
                    description = "Zawiązują się owoce",
                    careInstructions = listOf(
                        "Podlewaj regularnie",
                        "Nawóż potasem",
                        "Usuń pierwsze owoce dla lepszego plonu"
                    ),
                    visualIndicators = listOf("Małe zielone papryki"),
                    autoTasks = listOf(
                        AutoTaskData(
                            taskTitle = "Usuń pierwszy owoc",
                            taskDescription = "Rozważ usunięcie pierwszego owocu - roślina da większy plon",
                            taskType = TaskType.PRUNING,
                            triggerDayOffset = 5,
                            priority = TaskPriority.LOW
                        )
                    )
                ),
                GrowthPhaseData(
                    id = "pepper_phase_ripening",
                    phaseName = GrowthPhaseName.RIPENING,
                    displayName = "Dojrzewanie",
                    averageDurationDaysMin = 15,
                    averageDurationDaysMax = 30,
                    description = "Owoce zmieniają kolor",
                    careInstructions = listOf(
                        "Zbieraj na bieżąco",
                        "Zielone owoce też są jadalne",
                        "Czerwone/żółte = pełna dojrzałość"
                    ),
                    visualIndicators = listOf("Zmiana koloru z zielonego"),
                    autoTasks = listOf(
                        AutoTaskData(
                            taskTitle = "Zbiór papryki",
                            taskDescription = "Zbierz dojrzałe papryki",
                            taskType = TaskType.HARVESTING,
                            triggerDayOffset = 15,
                            priority = TaskPriority.MEDIUM
                        )
                    )
                )
            ),
            
            imageUrls = emptyList(),
            tags = listOf("warzywo", "jadalne", "wymaga ciepła", "długi sezon"),
            description = "Papryka to ciepłolubna roślina o długim okresie wegetacji. " +
                    "Wymaga cierpliwości ale nagradza smacznymi, kolorowymi owocami.",
            careInstructions = "Zapewnij ciepło i słońce. Podlewaj regularnie ale umiarkowanie. " +
                    "Nawóż potasem w fazie owocowania.",
            
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }

    /**
     * Marchew (Carrot) - Daucus carota
     * Family: Apiaceae
     */
    private fun getCarrot(): PlantCatalogEntity {
        return PlantCatalogEntity(
            id = "plant_carrot_001",
            commonName = "Marchew",
            latinName = "Daucus carota",
            family = "Apiaceae",
            plantType = PlantType.VEGETABLE,
            
            lightRequirements = LightRequirements.FULL_SUN,
            soilType = "Lekka, piaszczysta, głęboka",
            soilPHMin = 6.0f,
            soilPHMax = 7.0f,
            wateringFrequency = WateringFrequency.EVERY_2_DAYS,
            growthDifficulty = GrowthDifficulty.EASY,
            
            toxicity = false,
            edible = true,
            hardiness = "Odporna na chłód, może zimować w gruncie",
            
            companionPlantIds = listOf("plant_lettuce_001"),
            incompatiblePlantIds = emptyList(),
            
            sowingPeriodStart = "03-15",
            sowingPeriodEnd = "07-31",
            harvestPeriodStart = "07-01",
            harvestPeriodEnd = "11-30",
            daysToHarvestMin = 70,
            daysToHarvestMax = 100,
            averageYield = "3-5 kg z m²",
            
            growthPhases = listOf(
                GrowthPhaseData(
                    id = "carrot_phase_germination",
                    phaseName = GrowthPhaseName.GERMINATION,
                    displayName = "Kiełkowanie",
                    averageDurationDaysMin = 14,
                    averageDurationDaysMax = 21,
                    description = "Powolne kiełkowanie",
                    careInstructions = listOf(
                        "Utrzymuj stałą wilgotność",
                        "Nie pozwól przeschnąć powierzchni"
                    ),
                    visualIndicators = listOf("Delikatne kiełki"),
                    autoTasks = emptyList()
                ),
                GrowthPhaseData(
                    id = "carrot_phase_leaf_growth",
                    phaseName = GrowthPhaseName.LEAF_GROWTH,
                    displayName = "Wzrost liści",
                    averageDurationDaysMin = 30,
                    averageDurationDaysMax = 40,
                    description = "Rozwój nadziemnej części",
                    careInstructions = listOf(
                        "Przerwij rośliny do 5-7 cm odstępu",
                        "Podlewaj regularnie",
                        "Spulchniaj glebę"
                    ),
                    visualIndicators = listOf("Pierzaste liście"),
                    autoTasks = listOf(
                        AutoTaskData(
                            taskTitle = "Przerwij marchew",
                            taskDescription = "Usuń słabsze rośliny, zostaw co 5-7 cm",
                            taskType = TaskType.THINNING,
                            triggerDayOffset = 25,
                            priority = TaskPriority.HIGH
                        )
                    )
                ),
                GrowthPhaseData(
                    id = "carrot_phase_root_development",
                    phaseName = GrowthPhaseName.ROOT_DEVELOPMENT,
                    displayName = "Rozwój korzenia",
                    averageDurationDaysMin = 30,
                    averageDurationDaysMax = 50,
                    description = "Główny korzeń rośnie w głąb",
                    careInstructions = listOf(
                        "Podlewaj równomiernie - zbyt mokro = pękanie",
                        "Nie nawozuj azotem - wyrośnie liść zamiast korzenia"
                    ),
                    visualIndicators = listOf("Widoczna górna część korzenia"),
                    autoTasks = emptyList()
                ),
                GrowthPhaseData(
                    id = "carrot_phase_harvest",
                    phaseName = GrowthPhaseName.HARVEST,
                    displayName = "Dojrzałość",
                    averageDurationDaysMin = 10,
                    averageDurationDaysMax = 20,
                    description = "Gotowa do zbioru",
                    careInstructions = listOf(
                        "Zbieraj wybiórczo - większe korzenie",
                        "Można zostawić w ziemi na zimę (młodsze)"
                    ),
                    visualIndicators = listOf("Gruba pomarańczowa marchew"),
                    autoTasks = listOf(
                        AutoTaskData(
                            taskTitle = "Zbiór marchwi",
                            taskDescription = "Marchew gotowa do zbioru! Zbieraj w suchą pogodę.",
                            taskType = TaskType.HARVESTING,
                            triggerDayOffset = 0,
                            priority = TaskPriority.MEDIUM
                        )
                    )
                )
            ),
            
            imageUrls = emptyList(),
            tags = listOf("warzywo", "korzeniowe", "jadalne", "łatwe", "długie przechowywanie"),
            description = "Marchew to popularne warzywo korzeniowe, łatwe w uprawie. " +
                    "Wymaga głębokiej, lekkiej gleby i cierpliwości.",
            careInstructions = "Siej rzadko lub przerwij po wschodach. Podlewaj równomiernie. " +
                    "Nie nawozuj świeżym obornikiem.",
            
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }

    // ==================== HERBS ====================

    /**
     * Bazylia (Basil) - Ocimum basilicum
     * Family: Lamiaceae
     * Excellent companion for tomatoes
     */
    private fun getBasil(): PlantCatalogEntity {
        return PlantCatalogEntity(
            id = "plant_basil_001",
            commonName = "Bazylia",
            latinName = "Ocimum basilicum",
            family = "Lamiaceae",
            plantType = PlantType.HERB,
            
            lightRequirements = LightRequirements.FULL_SUN,
            soilType = "Próchniczna, przepuszczalna",
            soilPHMin = 6.0f,
            soilPHMax = 7.5f,
            wateringFrequency = WateringFrequency.DAILY,
            growthDifficulty = GrowthDifficulty.EASY,
            
            toxicity = false,
            edible = true,
            hardiness = "Roślina jednoroczna, bardzo wrażliwa na mróz",
            
            companionPlantIds = listOf("plant_tomato_001", "plant_pepper_001"),
            incompatiblePlantIds = emptyList(),
            
            sowingPeriodStart = "04-01",
            sowingPeriodEnd = "06-30",
            harvestPeriodStart = "05-15",
            harvestPeriodEnd = "10-15",
            daysToHarvestMin = 40,
            daysToHarvestMax = 60,
            averageYield = "Ciągły zbiór liści",
            
            growthPhases = listOf(
                GrowthPhaseData(
                    id = "basil_phase_germination",
                    phaseName = GrowthPhaseName.GERMINATION,
                    displayName = "Kiełkowanie",
                    averageDurationDaysMin = 5,
                    averageDurationDaysMax = 10,
                    description = "Szybkie kiełkowanie w cieple",
                    careInstructions = listOf("Temperatura min. 20°C", "Wilgotne podłoże"),
                    visualIndicators = listOf("Małe okrągłe liścienie"),
                    autoTasks = emptyList()
                ),
                GrowthPhaseData(
                    id = "basil_phase_leaf_growth",
                    phaseName = GrowthPhaseName.LEAF_GROWTH,
                    displayName = "Wzrost liści",
                    averageDurationDaysMin = 30,
                    averageDurationDaysMax = 45,
                    description = "Roślina krzewi się",
                    careInstructions = listOf(
                        "Przycinaj wierzchołki - roślina się rozgałęzi",
                        "Podlewaj regularnie",
                        "Nie pozwól zakwitnąć - liście stracą smak"
                    ),
                    visualIndicators = listOf("Duże aromatyczne liście"),
                    autoTasks = listOf(
                        AutoTaskData(
                            taskTitle = "Przytnij wierzchołki",
                            taskDescription = "Przytnij górne pędy - bazylia rozrośnie się na boki",
                            taskType = TaskType.PINCHING,
                            triggerDayOffset = 25,
                            priority = TaskPriority.MEDIUM
                        )
                    )
                ),
                GrowthPhaseData(
                    id = "basil_phase_harvest",
                    phaseName = GrowthPhaseName.HARVEST,
                    displayName = "Zbiór ciągły",
                    averageDurationDaysMin = 60,
                    averageDurationDaysMax = 90,
                    description = "Zbieraj liście regularnie",
                    careInstructions = listOf(
                        "Zrywaj liście od góry",
                        "Usuwaj pąki kwiatowe",
                        "Zbieraj rano - najlepszy aromat"
                    ),
                    visualIndicators = listOf("Bujny krzak"),
                    autoTasks = listOf(
                        AutoTaskData(
                            taskTitle = "Usuń pąki kwiatowe",
                            taskDescription = "Regularnie usuwaj pąki kwiatowe aby bazylia nie przestała rosnąć",
                            taskType = TaskType.PINCHING,
                            triggerDayOffset = 30,
                            priority = TaskPriority.MEDIUM
                        )
                    )
                )
            ),
            
            imageUrls = emptyList(),
            tags = listOf("zioło", "jadalne", "aromatyczne", "companion planting", "łatwe"),
            description = "Bazylia to aromatyczne zioło, doskonały dodatek do potraw. " +
                    "Świetnie rośnie obok pomidorów - wzajemnie się wspierają.",
            careInstructions = "Podlewaj regularnie, przycinaj wierzchołki. Nie pozwól zakwitnąć. " +
                    "Sadzaj obok pomidorów - odpędza szkodniki.",
            
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }

    /**
     * Pietruszka (Parsley) - Petroselinum crispum
     * Family: Apiaceae
     */
    private fun getParsley(): PlantCatalogEntity {
        return PlantCatalogEntity(
            id = "plant_parsley_001",
            commonName = "Pietruszka",
            latinName = "Petroselinum crispum",
            family = "Apiaceae",
            plantType = PlantType.HERB,
            
            lightRequirements = LightRequirements.PARTIAL_SUN,
            soilType = "Żyzna, próchniczna, głęboka",
            soilPHMin = 6.0f,
            soilPHMax = 7.0f,
            wateringFrequency = WateringFrequency.EVERY_2_DAYS,
            growthDifficulty = GrowthDifficulty.EASY,
            
            toxicity = false,
            edible = true,
            hardiness = "Dwuletnia, odporna na mróz",
            
            companionPlantIds = listOf("plant_tomato_001", "plant_carrot_001"),
            incompatiblePlantIds = emptyList(),
            
            sowingPeriodStart = "03-15",
            sowingPeriodEnd = "08-31",
            harvestPeriodStart = "06-01",
            harvestPeriodEnd = "11-30",
            daysToHarvestMin = 70,
            daysToHarvestMax = 90,
            averageYield = "Ciągły zbiór liści i korzeni",
            
            growthPhases = listOf(
                GrowthPhaseData(
                    id = "parsley_phase_germination",
                    phaseName = GrowthPhaseName.GERMINATION,
                    displayName = "Kiełkowanie",
                    averageDurationDaysMin = 14,
                    averageDurationDaysMax = 28,
                    description = "Bardzo powolne kiełkowanie",
                    careInstructions = listOf(
                        "Namocz nasiona przed siewem",
                        "Utrzymuj stałą wilgotność",
                        "Cierpliwość - może trwać miesiąc!"
                    ),
                    visualIndicators = listOf("Małe kiełki"),
                    autoTasks = emptyList()
                ),
                GrowthPhaseData(
                    id = "parsley_phase_leaf_growth",
                    phaseName = GrowthPhaseName.LEAF_GROWTH,
                    displayName = "Wzrost liści",
                    averageDurationDaysMin = 50,
                    averageDurationDaysMax = 70,
                    description = "Rozwój rozetki liści",
                    careInstructions = listOf(
                        "Podlewaj regularnie",
                        "Nawóż co 3-4 tygodnie",
                        "Spulchniaj glebę"
                    ),
                    visualIndicators = listOf("Ciemnozielone karbowane liście"),
                    autoTasks = emptyList()
                ),
                GrowthPhaseData(
                    id = "parsley_phase_harvest",
                    phaseName = GrowthPhaseName.HARVEST,
                    displayName = "Zbiór",
                    averageDurationDaysMin = 90,
                    averageDurationDaysMax = 120,
                    description = "Zbieraj liście i/lub korzeń",
                    careInstructions = listOf(
                        "Zrywaj zewnętrzne liście",
                        "Zostaw środkowe - będą rosnąć",
                        "Korzeń zbieraj jesienią (1 rok uprawy)"
                    ),
                    visualIndicators = listOf("Bujne liście", "Gruby korzeń"),
                    autoTasks = listOf(
                        AutoTaskData(
                            taskTitle = "Zbiór pietruszki",
                            taskDescription = "Zbieraj zewnętrzne liście pietruszki według potrzeb",
                            taskType = TaskType.HARVESTING,
                            triggerDayOffset = 0,
                            priority = TaskPriority.LOW
                        )
                    )
                )
            ),
            
            imageUrls = emptyList(),
            tags = listOf("zioło", "jadalne", "dwuletnie", "odporne na mróz", "korzeń i liść"),
            description = "Pietruszka to popularne zioło kulinarne. Można zbierać liście lub " +
                    "korzenie. Dwuletnia - w drugim roku kwitnie.",
            careInstructions = "Podlewaj regularnie. Zbieraj liście od zewnątrz. " +
                    "Korzeń zbieraj jesienią pierwszego roku lub wiosną drugiego.",
            
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }

    // ==================== FLOWERS ====================

    /**
     * Nagietek (Marigold) - Calendula officinalis
     * Family: Asteraceae
     * Companion plant - repels pests
     */
    private fun getMarigold(): PlantCatalogEntity {
        return PlantCatalogEntity(
            id = "plant_marigold_001",
            commonName = "Nagietek",
            latinName = "Calendula officinalis",
            family = "Asteraceae",
            plantType = PlantType.FLOWER,
            
            lightRequirements = LightRequirements.FULL_SUN,
            soilType = "Przeciętna, dobrze odwodniona",
            soilPHMin = 6.0f,
            soilPHMax = 7.0f,
            wateringFrequency = WateringFrequency.EVERY_2_DAYS,
            growthDifficulty = GrowthDifficulty.EASY,
            
            toxicity = false,
            edible = true, // płatki jadalne
            hardiness = "Jednoroczny, odporny na chłód",
            
            companionPlantIds = listOf("plant_tomato_001", "plant_cucumber_001", "plant_pepper_001"),
            incompatiblePlantIds = emptyList(),
            
            sowingPeriodStart = "03-15",
            sowingPeriodEnd = "06-30",
            harvestPeriodStart = "06-01",
            harvestPeriodEnd = "10-31",
            daysToHarvestMin = 50,
            daysToHarvestMax = 70,
            averageYield = "Liczne kwiaty przez całe lato",
            
            growthPhases = listOf(
                GrowthPhaseData(
                    id = "marigold_phase_germination",
                    phaseName = GrowthPhaseName.GERMINATION,
                    displayName = "Kiełkowanie",
                    averageDurationDaysMin = 7,
                    averageDurationDaysMax = 14,
                    description = "Szybkie kiełkowanie",
                    careInstructions = listOf("Temperatura 15-20°C", "Wilgotne podłoże"),
                    visualIndicators = listOf("Liścienie"),
                    autoTasks = emptyList()
                ),
                GrowthPhaseData(
                    id = "marigold_phase_vegetative",
                    phaseName = GrowthPhaseName.VEGETATIVE,
                    displayName = "Wzrost",
                    averageDurationDaysMin = 30,
                    averageDurationDaysMax = 40,
                    description = "Rozwój krzaczka",
                    careInstructions = listOf(
                        "Podlewaj umiarkowanie",
                        "Nie wymaga nawożenia"
                    ),
                    visualIndicators = listOf("Gęsty krzaczek"),
                    autoTasks = emptyList()
                ),
                GrowthPhaseData(
                    id = "marigold_phase_flowering",
                    phaseName = GrowthPhaseName.FLOWERING,
                    displayName = "Kwitnienie",
                    averageDurationDaysMin = 60,
                    averageDurationDaysMax = 90,
                    description = "Obfite kwitnienie przez całe lato",
                    careInstructions = listOf(
                        "Usuwaj przekwitłe kwiaty - stymuluje nowe",
                        "Zbieraj kwiaty do celów leczniczych"
                    ),
                    visualIndicators = listOf("Pomarańczowe/żółte kwiaty"),
                    autoTasks = listOf(
                        AutoTaskData(
                            taskTitle = "Usuń przekwitłe kwiaty",
                            taskDescription = "Regularnie usuwaj przekwitłe kwiaty nagietka",
                            taskType = TaskType.PRUNING,
                            triggerDayOffset = 30,
                            priority = TaskPriority.LOW
                        )
                    )
                )
            ),
            
            imageUrls = emptyList(),
            tags = listOf("kwiat", "companion planting", "odstraszacz szkodników", "łatwe", "jadalne płatki"),
            description = "Nagietek to piękny kwiat o właściwościach leczniczych. " +
                    "Doskonały jako companion plant - odpędza mszyce i inne szkodniki.",
            careInstructions = "Siej bezpośrednio do gruntu. Usuwaj przekwitłe kwiaty. " +
                    "Sadzaj obok warzyw - chroni przed szkodnikami.",
            
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
}

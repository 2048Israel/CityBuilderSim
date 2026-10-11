package ham.citybuildersim;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * The Earth's geology as its deposits read it: each USGS province's class - ocean, shield, fold belt, rift, shelf, basin or other - by the data's name rules and the hand-checked table, its known petroleum, and the weights, the Z that keeps the land's count of fields today's and the type odds an Earth world's fields are drawn with (spec-earth 4).
 *
 * WHY THIS EXISTS (0.7.117, batch E3; the project's spec-earth.md 4 and 8,
 * runs/spec-materials.md 2.4; Jerus's answers of 2026-10-10, "all rec").
 * Until E3 an Earth world drew its fields as a generated one does, at
 * today's densities on its cells' land (E2): iron as thick on the Paris
 * basin as on the Yilgarn shield, oil as thick in the Alps as under the
 * North Sea. The study rasterised the USGS World Energy provinces at 0.25
 * degree (earth-1's geology layer), classed each by its name with a rule
 * list (tools/earth/geology.py) and weighted each resource's density by the
 * class. This holds those classes, the weights and the constants derived
 * from them: World draws an Earth world's fields on real geology with them,
 * and Deposit types a field by them. A generated world reads none of it
 * (w = 1, Z = 1, its types flat) and draws to the bit what it drew.
 *
 * THE CLASSES. The data's provinces layer carries each province's class by
 * geology.py's rules, the first match winning. The rules leave 219 of the
 * 1,023 "other" and misfire on some: every "OCS" planning area is ocean,
 * Cook Inlet and the Gulf of Mexico's shelf with it; "Foredeep" matches
 * "deep", so Mesopotamia's foreland basin is ocean; "Uplift" makes Ghawar a
 * fold belt; "platform" puts Iowa on a shelf. HAND is the hand-checked
 * table: each of the 219 placed by its name and where it lies (8 stay
 * other: flood-basalt plateaus, oceanic islands and basement highs under a
 * shelf sea), and 68 misfires corrected (★E3-1), every row with the class
 * the rules gave it and why it moves. A data file whose province under a
 * row is not the table's is refused (of()): a re-baked file is checked
 * again before it is read.
 *
 * THE DENSITY ON EARTH (World.countOf()). A cell's mean count of a
 * resource's fields is today's density x its land km2 x w_r(class) x Z_r,
 * the class read at each of the cells pass's 16 points
 * (EarthTerrain.cellsPass()), so a cell across two provinces takes each
 * its share. Oil's is also x (1 + log10(1 + known petroleum /
 * OIL_SCALE_MMBOE)), and its area takes in the sea of shelf, basin and
 * rift provinces: offshore oil, as the generated world's first place
 * allows.
 *
 * Z (measure(), the constants Z_IRON to Z_STONE). Z_r = the land / the land
 * x w_r over the planet north of ANTARCTIC_LAT, every 4' cell of the far
 * layers at its area on the sphere and its class where its centre lies, so
 * the land's expected count of each resource's fields is today's (oil's
 * weights with its petroleum, its offshore sea on top, as the study's 0.701
 * has it). The study's figures were measured on the rules' classes before
 * the hand pass, and three of them do not follow from its own table (★E3-2).
 *
 * THE TYPES (runs/spec-materials.md 2.4, "On Earth (E3)"). A field's type
 * leans on the class at its centre by an odds ratio: coking coal x3 in fold
 * belts and x0.5 on shields, limestone x2 in basins and on shelves, x0.5 in
 * fold belts and x0.25 on shields, iron and copper flat; one constant a
 * resource (COKING_ODDS, LIMESTONE_ODDS) holds the Earth-wide share - over
 * the fields expected in each class - at the generated world's TYPE_SHARES.
 *
 * EarthTerrainCheck 10 to 14 hold it, on the fixture and on the full file.
 */
public final class Geology {

    /** A province's class, in the order of the data's names: what its rocks hold. */
    public enum Kind {
        OCEAN("ocean"), SHIELD("shield"), FOLD_BELT("fold belt"), RIFT("rift"), SHELF("shelf"), BASIN("basin"), OTHER("other");

        private final String label;

        Kind(String label) { this.label = label; }

        /** Its name as the data and the table write it. */
        public String label() { return label; }

        /** The kind a name names; null for none. */
        public static Kind of(String label) {
            for (Kind k : values()) if (k.label.equals(label)) return k;
            return null;
        }
    }

    /* =====================================================================
       THE WEIGHTS (spec-earth 4's table), in Kind's order - ocean, shield,
       fold belt, rift, shelf, basin, other. The table has no ocean column:
       an ocean province's land is an island the rules could not place, and
       takes other's weights (★E3-3).
       ===================================================================== */

    /** Iron's weight by class: four on a shield, nothing on a shelf, half in a rift or basin (spec-earth 4). */
    static final double[] W_IRON = { 1, 4, 1, 0.5, 0, 0.5, 1 };

    /** Oil's weight by class: three on a shelf, two in a rift or basin, a twentieth on a shield (spec-earth 4). */
    static final double[] W_OIL = { 0.5, 0.05, 1, 2, 3, 2, 0.5 };

    /** Coal's weight by class: two in a basin, a tenth on a shield, nothing on a shelf (spec-earth 4). */
    static final double[] W_COAL = { 0.5, 0.1, 1, 1, 0, 2, 0.5 };

    /** Copper's weight by class: four in a fold belt, three tenths in a basin, nothing on a shelf (spec-earth 4). */
    static final double[] W_COPPER = { 1, 1, 4, 1, 0, 0.3, 1 };

    /** Uranium's weight by class: two on a shield, one and a half in a basin, half in a fold belt (spec-earth 4). */
    static final double[] W_URANIUM = { 1, 2, 0.5, 1, 0, 1.5, 1 };

    /** Stone's weight by class: two on a shield or in a fold belt, nothing on a shelf (spec-earth 4). */
    static final double[] W_STONE = { 1, 2, 2, 1, 0, 1, 1 };

    /* =====================================================================
       Z, measured on earth-1 with the hand table (measure(); EarthTerrainCheck
       13 recomputes each to its rounding). The study's, on the rules' classes:
       iron 0.888, oil 0.701, coal 0.959, copper 0.846, uranium 1.049, stone
       0.751.
       ===================================================================== */

    /** Iron's Z: the land over the land x W_IRON north of ANTARCTIC_LAT, 0.7219 on earth-1 with the hand table. */
    public static final double Z_IRON = 0.7219;

    /** Oil's Z: the land over the land x W_OIL x its petroleum factor north of ANTARCTIC_LAT, 0.6033 on earth-1 with the hand table (the offshore sea on top). */
    public static final double Z_OIL = 0.6033;

    /** Coal's Z: the land over the land x W_COAL north of ANTARCTIC_LAT, 0.7884 on earth-1 with the hand table. */
    public static final double Z_COAL = 0.7884;

    /** Copper's Z: the land over the land x W_COPPER north of ANTARCTIC_LAT, 0.7322 on earth-1 with the hand table. */
    public static final double Z_COPPER = 0.7322;

    /** Uranium's Z: the land over the land x W_URANIUM north of ANTARCTIC_LAT, 0.7518 on earth-1 with the hand table. */
    public static final double Z_URANIUM = 0.7518;

    /** Stone's Z: the land over the land x W_STONE north of ANTARCTIC_LAT, 0.6915 on earth-1 with the hand table. */
    public static final double Z_STONE = 0.6915;

    /** The latitude south of which Z's sums leave the ground out: 60 S, the study's "Antarctica excluded" (★E3-4: its ice holds what no city founded on the ice can reach, and E4 refuses ice). */
    public static final double ANTARCTIC_LAT = -60;

    /** Oil's petroleum factor's scale, in million barrels of oil equivalent: 1,000 - oil is x (1 + log10(1 + known / 1,000)) (spec-earth 4): x2.13 under the Anglo-Dutch Basin's 12,640. */
    public static final double OIL_SCALE_MMBOE = 1000;

    /* =====================================================================
       THE TYPES' ODDS (runs/spec-materials.md 2.4, "On Earth (E3)"): the
       leaning type's share in a class is c x ratio / (1 + c x ratio), in
       Kind's order. The study, on the rules' classes: limestone .886 in
       basins and .493 on shields, 70.00% in all.
       ===================================================================== */

    /** Coking coal's odds ratio by class: three in a fold belt, a half on a shield, one elsewhere (spec-materials 2.4). */
    static final double[] COKING_RATIO = { 1, 0.5, 3, 1, 1, 1, 1 };

    /** Limestone's odds ratio by class: two in a basin or on a shelf, a half in a fold belt, a quarter on a shield (spec-materials 2.4). */
    static final double[] LIMESTONE_RATIO = { 1, 0.25, 0.5, 1, 2, 2, 1 };

    /** Coking coal's odds where the ratio is one: 0.135295 on earth-1 with the hand table (six figures), so its Earth-wide share is THERMAL's complement, 15%. */
    public static final double COKING_ODDS = 0.135295;

    /** Limestone's odds where the ratio is one: 4.10888 on earth-1 with the hand table (six figures), so its Earth-wide share is TYPE_SHARES' 70%: .892 in a basin, .507 on a shield. */
    public static final double LIMESTONE_ODDS = 4.10888;

    /* =====================================================================
       THE HAND-CHECKED TABLE (★E3-1). A row: the province's row in the data,
       its USGS code, its name, the class geology.py's rules gave it, the
       class it is given here, and why. Every province the rules leave
       "other" is here, and every misfire found among the rest (the ones with
       land or a shelf sea that matter; a deep-sea "ridge" or "rise" stays).
       ===================================================================== */

    /** The hand-checked classes, a row a province: row | code | name | the rules' class | the class here | why. */
    static final String HAND = """
        9 | 5080 | Chukchi Shelf OCS | ocean | shelf | a shelf: "OCS" matched the ocean rule
        10 | 1202 | Anabar-Olenek High | other | shield | the Anabar Shield of the Siberian craton
        13 | 5081 | Beaufort Shelf OCS | ocean | shelf | a shelf: "OCS" matched the ocean rule
        21 | 5001 | Northern Alaska | other | basin | the Colville foreland basin and the North Slope
        28 | 5082 | Hope Basin OCS | ocean | basin | a shelf basin: "OCS" matched the ocean rule
        31 | 1007 | Timan High | other | fold belt | the Timan ridge (the Timanides)
        35 | 4017 | Vestford-Helgeland | other | shelf | the Norwegian shelf off Helgeland
        36 | 5002 | Central Alaska | other | fold belt | interior Alaska's accreted terranes, the Cordillera
        40 | 1212 | Kempendiay Region | other | basin | the Vilyuy syneclise's southern rim
        43 | 5002 | Central Alaska | other | fold belt | interior Alaska's accreted terranes, as row 36
        44 | 5086 | Norton Basin OCS | ocean | basin | a shelf basin: "OCS" matched the ocean rule
        46 | 5002 | Central Alaska | other | fold belt | interior Alaska's accreted terranes, as row 36
        48 | 5002 | Central Alaska | other | fold belt | interior Alaska's accreted terranes, as row 36
        49 | 5087 | St. Matthew-Hall Basin OCS | ocean | basin | a shelf basin: "OCS" matched the ocean rule
        50 | 5002 | Central Alaska | other | fold belt | interior Alaska's accreted terranes, as row 36
        52 | 5002 | Central Alaska | other | fold belt | interior Alaska's accreted terranes, as row 36
        53 | 5003 | Southern Alaska | other | fold belt | southern Alaska's ranges and accreted terranes
        55 | 1206 | Yenisey Ridge | ocean | fold belt | the Yenisei Ridge, a Neoproterozoic orogen: "ridge" matched the ocean rule
        58 | 1215 | Aldan Uplift | fold belt | shield | the Aldan anteclise, Precambrian basement: "uplift" matched the fold-belt rule
        63 | 1015 | Volga-Ural Region | other | basin | the Volga-Ural petroleum province, a platform basin
        68 | 1211 | Cis-Patom Foredeep | ocean | basin | a foreland basin: "Foredeep" matched the ocean rule's "deep"
        75 | 5002 | Central Alaska | other | fold belt | interior Alaska's accreted terranes, as row 36
        77 | 5002 | Central Alaska | other | fold belt | interior Alaska's accreted terranes, as row 36
        78 | 5089 | Gulf of Alaska Shelf OCS | ocean | shelf | a shelf: "OCS" matched the ocean rule
        79 | 5088 | Cook Inlet OCS | ocean | basin | the Cook Inlet forearc basin: "OCS" matched the ocean rule
        80 | 5002 | Central Alaska | other | fold belt | interior Alaska's accreted terranes, as row 36
        81 | 5003 | Southern Alaska | other | fold belt | southern Alaska, as row 53
        86 | 5003 | Southern Alaska | other | fold belt | southern Alaska, as row 53
        88 | 5085 | St. George Basin OCS | ocean | basin | a shelf basin: "OCS" matched the ocean rule
        89 | 5003 | Southern Alaska | other | fold belt | southern Alaska, as row 53
        90 | 5084 | North Aleutian Basin OCS | ocean | basin | a shelf basin: "OCS" matched the ocean rule
        91 | 5090 | Shumagin-Kodiak Shelf OCS | ocean | shelf | a shelf: "OCS" matched the ocean rule
        92 | 5002 | Central Alaska | other | fold belt | interior Alaska's accreted terranes, as row 36
        93 | 1209 | Angara-Lena Terrace | other | basin | the Siberian platform's cover (the Angara-Lena step)
        94 | 5003 | Southern Alaska | other | fold belt | southern Alaska, as row 53
        95 | 5002 | Central Alaska | other | fold belt | interior Alaska's accreted terranes, as row 36
        96 | 1319 | Central Okhotsk High | other | other | a submarine high of the Okhotsk plate: no class fits
        104 | 1004 | Belorussian-Voronezh High | other | shield | the Voronezh and Belarusian basement highs of the East European craton
        106 | 5003 | Southern Alaska | other | fold belt | southern Alaska, as row 53
        107 | 5003 | Southern Alaska | other | fold belt | southern Alaska, as row 53
        111 | 5003 | Southern Alaska | other | fold belt | southern Alaska, as row 53
        114 | 5003 | Southern Alaska | other | fold belt | southern Alaska, as row 53
        116 | 5003 | Southern Alaska | other | fold belt | southern Alaska, as row 53
        117 | 5003 | Southern Alaska | other | fold belt | southern Alaska, as row 53
        118 | 5003 | Southern Alaska | other | fold belt | southern Alaska, as row 53
        121 | 5003 | Southern Alaska | other | fold belt | southern Alaska, as row 53
        125 | 5003 | Southern Alaska | other | fold belt | southern Alaska, as row 53
        128 | 5003 | Southern Alaska | other | fold belt | southern Alaska, as row 53
        129 | 5003 | Southern Alaska | other | fold belt | southern Alaska, as row 53
        130 | 5003 | Southern Alaska | other | fold belt | southern Alaska, as row 53
        131 | 5003 | Southern Alaska | other | fold belt | southern Alaska, as row 53
        135 | 5003 | Southern Alaska | other | fold belt | southern Alaska, as row 53
        137 | 5003 | Southern Alaska | other | fold belt | southern Alaska, as row 53
        140 | 5003 | Southern Alaska | other | fold belt | southern Alaska, as row 53
        146 | 5003 | Southern Alaska | other | fold belt | southern Alaska, as row 53
        148 | 1220 | Bureya-Dunbey Region | other | shield | the Bureya massif
        152 | 5003 | Southern Alaska | other | fold belt | southern Alaska, as row 53
        154 | 5003 | Southern Alaska | other | fold belt | southern Alaska, as row 53
        155 | 4046 | Bohemia | other | shield | the Bohemian Massif (the rules class a massif shield)
        157 | 5003 | Southern Alaska | other | fold belt | southern Alaska, as row 53
        171 | 1018 | Mugodzhary-South Emba | other | fold belt | the Mugodzhar hills, the southern Urals
        172 | 4041 | Armoricia | other | shield | the Armorican Massif (the rules class a massif shield)
        185 | 5051 | Superior | other | shield | the Superior Province of the Canadian Shield
        186 | 5004 | Western Oregon-Washington | other | fold belt | the Cascade arc and the Coast Range
        187 | 5005 | Eastern Oregon-Washington | other | other | the Columbia River flood basalts: no class fits
        189 | 5017 | Idaho-Snake River Downwarp | other | basin | the Snake River Plain, a downwarp (sag)
        190 | 5028 | North-Central Montana | other | basin | the plains of Montana, the Sweetgrass arch
        194 | 4057 | Transylvania | other | basin | the Transylvanian Basin
        198 | 5091 | Pacific Northwest OCS | ocean | shelf | the continental margin: "OCS" matched the ocean rule
        208 | 4052 | Jura | other | fold belt | the Jura Mountains
        210 | 5072 | New England | other | fold belt | the Appalachian orogen in New England
        221 | 1178 | East Aral Slope | shelf | basin | the Turan platform's cover: "slope" matched the shelf rule
        227 | 4056 | Lion-Camargue | other | basin | the Gulf of Lion margin and the Camargue
        231 | 5029 | Southwest Montana | other | fold belt | south-west Montana's thrust belt and Laramide ranges
        241 | 1104 | Crimea High | other | fold belt | the Crimean Mountains
        246 | 5071 | Adirondack Uplift | fold belt | shield | the Adirondacks, Grenville Precambrian: "uplift" matched the fold-belt rule
        247 | 5037 | Southwestern Wyoming | other | basin | the Greater Green River Basin
        251 | 5052 | Iowa Shelf | shelf | basin | the North American platform's cover: "shelf" matched the shelf rule
        263 | 4062 | Tuscany-Latium-Paola | other | fold belt | the northern Apennines
        267 | 5099 | Atlantic Mesozoic OCS | ocean | shelf | the continental margin: "OCS" matched the ocean rule
        276 | 5006 | Klamath-Sierra Nevada | other | fold belt | the Klamath Mountains and the Sierra Nevada
        281 | 3124 | Luxi Jiaoliao Uplift | fold belt | shield | the Precambrian Luxi and Jiaoliao blocks: "uplift" matched the fold-belt rule
        284 | 5053 | Cambridge Arch-Central Kansas Uplift | fold belt | basin | an arch of the North American platform: "uplift" matched the fold-belt rule
        291 | 1153 | Karabogaz-Karakum High | other | basin | an arch of the Turan platform's cover
        294 | 2085 | Thrace/Samsun | other | basin | the Thrace Basin
        296 | 5218 | Georges Bank | other | shelf | a bank of the continental shelf
        302 | 2081 | Lesser Caucasus | other | fold belt | the Lesser Caucasus
        305 | 4075 | Aegean | other | fold belt | the Hellenides and the Aegean arc
        308 | 5055 | Nemaha Uplift | fold belt | basin | an arch of the North American platform: "uplift" matched the fold-belt rule
        310 | 5070 | Atlantic Coastal Plain | ocean | basin | the coastal plain's sedimentary wedge: "plain" matched the ocean rule
        313 | 2083 | Tuz/Corum | other | basin | the Tuz Golu and Corum basins
        315 | 2079 | Adana/Sivas | other | basin | the Adana and Sivas basins
        321 | 5057 | Ozark Uplift | fold belt | basin | the Ozark dome of the North American platform: "uplift" matched the fold-belt rule
        322 | 3140 | Shanxi Plateau | other | basin | the Shanxi coal basins on the North China craton
        324 | 5092 | Central California OCS | ocean | shelf | the continental margin: "OCS" matched the ocean rule
        325 | 2080 | Araks | other | fold belt | the Armenian highland, in the Caucasus-Iran orogen
        326 | 4078 | Betic Zone | basin | fold belt | the Betic Cordillera: "zone" matched the basin rule
        332 | 5069 | Piedmont | other | fold belt | the Appalachian Piedmont
        335 | 1157 | Pamir High | other | fold belt | the Pamirs
        345 | 4066 | Sicily | other | fold belt | the Sicilian fold belt
        351 | 8001 | Southeast Afghanistan | other | fold belt | south-east Afghanistan's accreted blocks
        356 | 4088 | Andalucia | other | fold belt | the Betic Cordillera
        357 | 5060 | Cherokee Platform | shelf | basin | the North American platform's cover: "platform" matched the shelf rule
        360 | 2075 | Euphrates/Mardin | other | basin | the Arabian platform at Mardin and the Euphrates
        362 | 3133 | Qiangtang Terrane | other | fold belt | the Qiangtang terrane of the Tibetan plateau
        365 | 2052 | Tellian Foredeep | ocean | basin | a foreland basin: "Foredeep" matched the ocean rule's "deep"
        366 | 2076 | Haleb | other | basin | the Aleppo plateau, the Arabian platform
        370 | 5024 | Northern Arizona | other | basin | the Colorado Plateau's flat-lying cover
        380 | 4076 | Crete | other | fold belt | the Hellenic arc
        382 | 2074 | Khleisha Uplift | fold belt | basin | an arch of the Arabian platform: "uplift" matched the fold-belt rule
        384 | 5025 | Southern Arizona-Southwestern New Mexico | other | fold belt | the Laramide porphyry-copper arc beneath the Basin and Range
        385 | 8023 | Central Afghanistan | other | fold belt | central Afghanistan's accreted blocks, the Hindu Kush
        386 | 2078 | Beirut | other | fold belt | Mount Lebanon, the Levant fold belt
        389 | 2077 | Palmyra Zone | basin | fold belt | the Palmyrides: "zone" matched the basin rule
        393 | 2024 | Mesopotamian Foredeep Basin | ocean | basin | the Mesopotamian foreland basin: "Foredeep" matched the ocean rule's "deep"
        395 | 5026 | South-Central New Mexico | other | rift | the Rio Grande Rift
        404 | 3133 | Qiangtang Terrane | other | fold belt | the Qiangtang terrane, as row 362
        405 | 2092 | Cen. Iranian Microcontinents | other | fold belt | central Iran's blocks and the Urumieh-Dokhtar arc
        406 | 5061 | Southern Oklahoma | other | fold belt | the inverted Southern Oklahoma aulacogen (the Wichita and Arbuckle mountains)
        411 | 2023 | Widyan Basin-Int. Platform | shelf | basin | the Arabian interior platform: "platform" matched the shelf rule
        413 | 5093 | Santa Barbara-Ventura Basin OCS | ocean | basin | the Santa Barbara-Ventura Basin: "OCS" matched the ocean rule
        414 | 5015 | San Diego-Oceanside | other | fold belt | the Peninsular Ranges
        416 | 8026 | Kohat-Potwar | other | basin | the Potwar foreland basin
        421 | 3122 | Lhasa Terrane | other | fold belt | the Lhasa terrane, the Gangdese arc
        423 | 5095 | Inner Borderland OCS | ocean | basin | the inner borderland's basins: "OCS" matched the ocean rule
        425 | 5094 | Los Angeles Basin OCS | ocean | basin | the Los Angeles Basin: "OCS" matched the ocean rule
        427 | 8024 | Afghan | other | fold belt | the Afghan block
        428 | 2028 | Rutbah Uplift | fold belt | basin | an arch of the Arabian platform: "uplift" matched the fold-belt rule
        430 | 8025 | Sulaiman-Kirthar | other | fold belt | the Sulaiman and Kirthar fold-thrust belts
        431 | 2027 | North Harrah Volcanics | fold belt | other | a basalt field: "volcanic" matched the fold-belt rule; no class fits
        433 | 5094 | Los Angeles Basin OCS | ocean | basin | the Los Angeles Basin: "OCS" matched the ocean rule
        435 | 3133 | Qiangtang Terrane | other | fold belt | the Qiangtang terrane, as row 362
        438 | 5015 | San Diego-Oceanside | other | fold belt | the Peninsular Ranges, as row 414
        439 | 5015 | San Diego-Oceanside | other | fold belt | the Peninsular Ranges, as row 414
        442 | 5015 | San Diego-Oceanside | other | fold belt | the Peninsular Ranges, as row 414
        443 | 8042 | Indus | other | basin | the Indus foreland basin
        444 | 2040 | Cyrenacia Uplift | fold belt | basin | the Cyrenaica platform: "uplift" matched the fold-belt rule
        445 | 5015 | San Diego-Oceanside | other | fold belt | the Peninsular Ranges, as row 414
        451 | 2049 | Nefusn Uplift | fold belt | basin | an arch of the Saharan platform: "uplift" matched the fold-belt rule
        452 | 5333 | Baja California Backbone | other | fold belt | the Peninsular Ranges batholith
        455 | 8022 | Baluchistan | other | fold belt | the Chagai arc and Baluchistan's fold belts
        469 | 5050 | Florida Peninsula | other | basin | the Florida carbonate platform, onshore
        474 | 8028 | Himalayan Foreland | fold belt | basin | the Indo-Gangetic foreland basin: "Himalaya" matched the fold-belt rule
        476 | 5098 | Gulf Mesozoic OCS | ocean | shelf | the Gulf of Mexico's eastern margin: "OCS" matched the ocean rule
        485 | 5097 | Gulf Cenozoic OCS | ocean | shelf | the Gulf of Mexico's Cenozoic margin: "OCS" matched the ocean rule
        486 | 2065 | Canary Islands | other | other | oceanic volcanic islands: no class fits
        488 | 5333 | Baja California Backbone | other | fold belt | the Peninsular Ranges batholith, as row 452
        489 | 5321 | Coahuila Platform | shelf | basin | the Coahuila platform, onshore: "platform" matched the shelf rule
        493 | 0 | Ocean | other | ocean | the ocean
        494 | 2046 | Fezzan Uplift | fold belt | basin | an arch of the Saharan platform: "uplift" matched the fold-belt rule
        497 | 8021 | Makran | other | fold belt | the Makran accretionary prism
        503 | 8034 | Assam | other | basin | the Assam foreland basin
        506 | 8035 | North Burma | other | fold belt | the Kachin ranges of northern Myanmar
        508 | 8006 | Tenasserim-Shan | other | fold belt | the Shan plateau and Tenasserim ranges, the Sibumasu tin belt
        509 | 2068 | Reguibate Uplift | fold belt | shield | the Reguibat Shield: "uplift" matched the fold-belt rule
        510 | 5329 | Sinaloa | other | fold belt | the Sinaloa arc batholiths
        515 | 7041 | Hoggar | other | shield | the Tuareg Shield
        516 | 2057 | Thiemboka Uplift | fold belt | basin | an arch of the Saharan platform: "uplift" matched the fold-belt rule
        519 | 2021 | Greater Ghawar Uplift | fold belt | basin | the Arabian platform's Ghawar arch: "uplift" matched the fold-belt rule
        520 | 7075 | Erdis Kufra | other | basin | the Erdis and Kufra basins
        523 | 2044 | Nubian Uplift | fold belt | basin | an arch of the Saharan platform: "uplift" matched the fold-belt rule
        525 | 8043 | Bombay | other | basin | the Mumbai offshore basin and Saurashtra
        526 | 8005 | Indo-Burman | other | fold belt | the Indo-Burman ranges
        531 | 5050 | Florida Peninsula | other | basin | the Florida carbonate platform, as row 469
        532 | 5308 | Yucatan Platform | shelf | basin | the Yucatan carbonate platform, onshore: "platform" matched the shelf rule
        534 | 5318 | Central Mesa | other | fold belt | Mexico's Mesa Central, volcanic cover on folded Mesozoic rocks
        535 | 7066 | Chad | other | basin | the Chad Basin
        536 | 8032 | Damodar | other | rift | the Damodar Gondwana graben
        537 | 8031 | Satpura_Brahmani | other | rift | the Satpura Gondwana graben
        538 | 8030 | Chindwara | other | rift | the Gondwana grabens of Chhindwara
        550 | 7013 | Senegal | other | basin | the Senegal margin basin
        552 | 2015 | Central Oman Platform | shelf | basin | the Oman interior platform: "platform" matched the shelf rule
        553 | 8048 | Irrawaddy | other | basin | the Irrawaddy Basin
        556 | 7055 | Iullemmeden | other | basin | the Iullemmeden Basin
        557 | 3508 | Thailand Mesozoic Basin Belt | fold belt | basin | a belt of Mesozoic basins: "belt" matched the fold-belt rule
        559 | 2013 | Huqf-Haushi Uplift | fold belt | basin | an arch of the Arabian platform: "uplift" matched the fold-belt rule
        561 | 5314 | Jalisco-Oaxaca Platform | shelf | fold belt | the Sierra Madre del Sur: "platform" matched the shelf rule
        563 | 8046 | Mahanadi | other | rift | the Mahanadi Gondwana graben
        568 | 3607 | Philippine Accretionary Prism | other | fold belt | the Philippine accretionary prism
        569 | 8033 | Pranhita-Godavari | other | rift | the Pranhita-Godavari Gondwana graben
        575 | 7087 | Khartoum | other | rift | the Khartoum and Blue Nile rift basins
        577 | 2010 | Ghudun-Khasfeh Flank Province | other | basin | a flank of the Arabian platform's basins
        585 | 3502 | Khorat Platform | shelf | basin | the Khorat plateau's basin: "platform" matched the shelf rule
        604 | 8045 | Krishna-Godavari | other | basin | the Krishna-Godavari Basin
        609 | 7161 | Amhara Plateau | other | other | the Ethiopian flood-basalt plateau: no class fits
        610 | 8063 | Konkan | other | basin | the Konkan margin basin
        614 | 6113 | Middle America Province | other | fold belt | the Central American volcanic arc
        622 | 7146 | Sud | other | rift | the Sudd, the Central African Rift System's basins (Muglad, Melut)
        623 | 8062 | Lakshadweep | other | ocean | atolls on the Chagos-Laccadive ridge
        626 | 7105 | Baffa | other | basin | the Bove Basin
        628 | 7121 | Nigerian Massive | other | shield | the Nigerian basement complex (a massif)
        631 | 8044 | Cauvery | other | basin | the Cauvery Basin
        633 | 7114 | Volta | other | basin | the Volta Basin
        638 | 6091 | Lower Magdelena | other | basin | the Lower Magdalena Basin
        640 | 7255 | Somali | other | basin | the Ogaden and Somali basins
        642 | 3612 | Visayan | other | fold belt | the Visayas' arc islands
        646 | 7136 | Benue | other | rift | the Benue Trough
        649 | 6105 | Sierra Nevada de Santa Marta | other | fold belt | the Sierra Nevada de Santa Marta
        655 | 7173 | West African Coastal | shelf | basin | West Africa's coastal basins: "coastal" matched the shelf rule
        658 | 3704 | Malay Peninsula | other | fold belt | the Malay Peninsula's ranges, the tin belt
        662 | 8004 | Sri Lanka | other | shield | Sri Lanka's Precambrian complexes
        663 | 6090 | Middle Magdelena | other | basin | the Middle Magdalena Basin
        666 | 3610 | Sulu Arch | basin | fold belt | the Sulu Arc: "Arch" matched the basin rule
        667 | 3831 | Sumatra/Java Accretionary Prism | other | fold belt | the Sumatra-Java accretionary prism
        670 | 3705 | Rajang-Crocker Accretionary Prism | other | fold belt | the Rajang-Crocker accretionary prism
        673 | 8061 | Maldives | other | ocean | atolls on the Chagos-Laccadive ridge
        682 | 7225 | Zaire | other | basin | the Congo (Zaire) Basin, intracratonic
        684 | 6089 | Upper Magdelena | other | basin | the Upper Magdalena Basin
        689 | 3816 | Ketuneau/Sintang Terrane | other | fold belt | Borneo's accreted terranes
        690 | 7203 | West-central Coastal | shelf | basin | the Gabon, Congo and Kwanza coastal basins: "coastal" matched the shelf rule
        693 | 3829 | Sulawesi Accretionary Prism | other | fold belt | the Sulawesi accretionary prism
        708 | 3805 | Bintuni/Sulawati Province | other | basin | the Bintuni and Salawati basins
        713 | 3819 | Meratus High | other | fold belt | the Meratus mountains
        719 | 7273 | Tanzania Coastal | shelf | basin | the Tanzania coastal basin: "coastal" matched the shelf rule
        728 | 7417 | Seychelles | other | shield | the Seychelles' Precambrian granite microcontinent
        735 | 6006 | Andean Province | other | fold belt | the Andes
        737 | 3005 | Solomon Islands | other | fold belt | the Solomon Islands arc
        743 | 6013 | Sucunduri Province | other | shield | the Amazonian craton (Tapajos)
        751 | 6019 | Araripe Province | other | basin | the Araripe Basin
        757 | 6015 | Xingu Province | other | shield | the Amazonian craton (Xingu)
        767 | 3834 | Sumba Province | other | fold belt | Sumba, in the Banda forearc
        768 | 6018 | Diamantina Province | other | shield | the Sao Francisco craton (Chapada Diamantina)
        770 | 6014 | Parecis Province | other | basin | the Parecis Basin
        771 | 7291 | Luffillian Arch | basin | fold belt | the Lufilian Arc, the Copperbelt: "Arch" matched the basin rule
        773 | 7285 | Etosha | other | basin | the Owambo (Etosha) Basin
        775 | 3015 | Indispensable Reef | other | ocean | reefs on oceanic crust
        777 | 3013 | Melanesia Border Plateau | other | ocean | a submarine plateau
        778 | 7373 | Morondava | other | basin | the Morondava Basin
        781 | 3013 | Melanesia Border Plateau | other | ocean | a submarine plateau
        784 | 3954 | Pine Creek Geosyncline | other | shield | the Pine Creek inlier of the North Australian craton
        786 | 7396 | Tamatave | other | shield | eastern Madagascar's Precambrian basement
        788 | 7381 | Madagascar | other | shield | Madagascar's Precambrian basement
        794 | 3955 | Queensland Plateau | other | ocean | a submarine plateau
        799 | 7325 | Kalahari | other | basin | the Kalahari Basin
        802 | 7343 | Mozambique Coastal | shelf | basin | the Mozambique coastal basin: "coastal" matched the shelf rule
        811 | 3933 | Halls Creek Province | other | shield | the Halls Creek orogen, Precambrian as Australia's blocks
        815 | 3017 | Mellish Reef | other | ocean | a reef on a submarine plateau
        817 | 7303 | Orange River Coastal | fold belt | basin | the Orange margin basin: "Orange" matched the fold-belt rule's "range"
        819 | 3018 | New Caledonia | other | fold belt | New Caledonia's ophiolite nappe
        823 | 3939 | Marion Terrain | other | ocean | a submarine carbonate plateau
        825 | 3002 | Bellona Plateau | other | ocean | a submarine plateau
        836 | 3951 | Paterson Province | other | shield | the Paterson orogen, Precambrian as Australia's blocks
        847 | 6007 | Familina Province | other | fold belt | the Famatina belt and Sierras Pampeanas
        852 | 7355 | Karoo | other | basin | the Karoo Basin
        857 | 7363 | South African Coastal | shelf | fold belt | the Cape Fold Belt, onshore: "coastal" matched the shelf rule
        866 | 3902 | Albany-Fraser Province | other | shield | the Albany-Fraser orogen, Precambrian as Australia's blocks
        867 | 3957 | Stuart Shelf | shelf | shield | the Gawler craton under a thin platform cover: "shelf" matched the shelf rule
        882 | 3918 | Challenger Plateau | other | ocean | a submarine plateau of Zealandia
        894 | 6008 | Canadon Asfalto | other | rift | the Canadon Asfalto rift basin
        901 | 6009 | Deseado-Falklands Province | other | shield | the Deseado massif (the rules class a massif shield)
        906 | 6060 | Falklands Plateau | other | ocean | a submarine plateau; the Falkland Islands on it
        907 | 6062 | Malvinas Plateau | other | ocean | a submarine plateau
        915 | 7430 | Antarctica Peninsula | other | fold belt | the Antarctic Peninsula, a Mesozoic-Cenozoic magmatic arc
        916 | 7540 | Wilkes Onshore | other | shield | Wilkes Land, the East Antarctic craton
        918 | 7460 | Queen Maud Onshore | other | shield | Dronning Maud Land, the East Antarctic craton
        920 | 7510 | Marie Byrd Onshore | other | rift | West Antarctica, the West Antarctic Rift System
        921 | 1 | Ocean | other | ocean | the world ocean
        926 | 5203 | North Greenland Platform | shelf | basin | the Franklinian basin in Peary Land: "platform" matched the shelf rule
        934 | 1072 | Severnaya Zemlya High | other | fold belt | Severnaya Zemlya, the Kara terrane, as row 939
        936 | 1068 | Ushakov High | other | other | a basement high on the Kara shelf: no class fits
        938 | 4011 | Svalbard High | other | fold belt | Svalbard's Caledonian basement and the West Spitsbergen fold belt
        939 | 1176 | Taimyr-Kara High | other | fold belt | the Taimyr fold belt
        941 | 1070 | Central Kara High | other | other | a basement high on the Kara shelf: no class fits
        943 | 1257 | De Long High | other | other | a basement high on the East Siberian shelf: no class fits
        948 | 1067 | Vize High | other | other | a basement high on the Kara shelf: no class fits
        979 | 1301 | Gangut High | other | fold belt | the Koryak fold belt at Anadyr, as row 980
        980 | 1301 | Gangut High | other | fold belt | the Koryak fold belt at Anadyr
        981 | 5083 | Navarin Basin OCS | ocean | basin | a shelf basin: "OCS" matched the ocean rule
        982 | 1 | Ocean | other | ocean | the ocean
        983 | 5076 | Bering Shelf-Margin Basins OCS | ocean | shelf | the Bering shelf's margin: "OCS" matched the ocean rule
        984 | 5077 | Aleutian Arc OCS | ocean | fold belt | the Aleutian arc: "OCS" matched the ocean rule
        985 | 5077 | Aleutian Arc OCS | ocean | fold belt | the Aleutian arc: "OCS" matched the ocean rule
        986 | 3013 | Melanesia Border Plateau | other | ocean | a submarine plateau
        987 | 3013 | Melanesia Border Plateau | other | ocean | a submarine plateau
        988 | 3013 | Melanesia Border Plateau | other | ocean | a submarine plateau
        989 | 3013 | Melanesia Border Plateau | other | ocean | a submarine plateau
        990 | 3013 | Melanesia Border Plateau | other | ocean | a submarine plateau
        991 | 3973 | Fiji Islands | other | fold belt | the Fiji arc, as row 992
        992 | 3973 | Fiji Islands | other | fold belt | the Fiji arc
        993 | 1 | Ocean | other | ocean | the ocean
        995 | 3973 | Fiji Islands | other | fold belt | the Fiji arc, as row 992
        996 | 3973 | Fiji Islands | other | fold belt | the Fiji arc, as row 992
        997 | 1 | Ocean | other | ocean | the ocean
        1006 | 1 | Ocean | other | ocean | the ocean
        1012 | 1 | Ocean | other | ocean | the ocean
        1013 | 7490 | Polar Province | other | shield | East Antarctica's interior, the East Antarctic craton
        1018 | 4028 | Mid-North Sea High | other | basin | the North Sea's platform and northern England's Carboniferous
        """;

    /** A row of the hand-checked table. */
    public record Hand(int row, String code, String name, Kind rules, Kind kind, String why) { }

    /** The hand-checked table, parsed, in its rows' order. */
    public static final List<Hand> TABLE = parse();

    private static List<Hand> parse() {
        java.util.ArrayList<Hand> out = new java.util.ArrayList<>();
        for (String line : HAND.split("\n")) {
            if (line.isBlank()) continue;
            String[] f = line.split("\\|", -1);
            if (f.length != 6) throw new IllegalStateException("Geology.HAND: " + line);
            Kind rules = Kind.of(f[3].trim()), kind = Kind.of(f[4].trim());
            if (rules == null || kind == null) throw new IllegalStateException("Geology.HAND: a class neither the data nor the table knows, " + line);
            out.add(new Hand(Integer.parseInt(f[0].trim()), f[1].trim(), f[2].trim(), rules, kind, f[5].trim()));
        }
        return Collections.unmodifiableList(out);
    }

    /* =====================================================================
       THE WEIGHTS AND ODDS, read
       ===================================================================== */

    /** A resource's weight in a class: its row of the table; one for forest, which is not in fields. */
    public static double weight(Resource r, Kind k) {
        switch (r) {
            case IRON:    return W_IRON[k.ordinal()];
            case OIL:     return W_OIL[k.ordinal()];
            case COAL:    return W_COAL[k.ordinal()];
            case COPPER:  return W_COPPER[k.ordinal()];
            case URANIUM: return W_URANIUM[k.ordinal()];
            case STONE:   return W_STONE[k.ordinal()];
            default:      return 1;
        }
    }

    /** A resource's Z; one for forest. */
    public static double z(Resource r) {
        switch (r) {
            case IRON:    return Z_IRON;
            case OIL:     return Z_OIL;
            case COAL:    return Z_COAL;
            case COPPER:  return Z_COPPER;
            case URANIUM: return Z_URANIUM;
            case STONE:   return Z_STONE;
            default:      return 1;
        }
    }

    /** Oil's petroleum factor for a province's known petroleum, MMBOE: 1 + log10(1 + known / OIL_SCALE_MMBOE). */
    public static double petroleumFactor(double knownMmboe) {
        return 1 + StrictMath.log10(1 + Math.max(0, knownMmboe) / OIL_SCALE_MMBOE);
    }

    /** Whether a class's sea holds oil: a shelf's, a basin's and a rift's (spec-earth 4). */
    public static boolean seaHoldsOil(Kind k) {
        return k == Kind.SHELF || k == Kind.BASIN || k == Kind.RIFT;
    }

    /** The leaning type's share at odds c and a ratio: c x ratio / (1 + c x ratio). */
    static double lean(double odds, double ratio) {
        double o = odds * ratio;
        return o / (1 + o);
    }

    /**
     * The share of a resource's fields of its first type in a class, on
     * Earth: coal's thermal one less coking's lean (COKING_ODDS), stone's
     * limestone its lean (LIMESTONE_ODDS), iron's and copper's
     * Deposit.TYPE_SHARES as on a generated world (flat); 1 for a resource
     * of one type. Deposit.type()'s threshold.
     */
    public static double firstShare(Resource r, Kind k) {
        switch (r) {
            case COAL:  return 1 - lean(COKING_ODDS, COKING_RATIO[k.ordinal()]);
            case STONE: return lean(LIMESTONE_ODDS, LIMESTONE_RATIO[k.ordinal()]);
            default:
                List<Deposit.FieldType> types = r.typesOf();
                return types.size() < 2 ? 1 : Deposit.TYPE_SHARES[types.get(0).ordinal()];
        }
    }

    /* =====================================================================
       ONE DATA FILE'S GEOLOGY: its provinces' classes and the grid
       ===================================================================== */

    /** Each reader's geology, made once (of()). */
    private static final Map<EarthData, Geology> OF = new IdentityHashMap<>();

    private final EarthData data;
    private final byte[] grid;
    private final int cpd;
    /** Each province's class (row 0, no province, other), and its class by the rules alone. */
    private final Kind[] kinds, rules;
    /** Each province's petroleum factor. */
    private final double[] petroleum;
    /** Each resource's multiplier for each province, w x Z (x the petroleum factor for oil), by Resource's ordinal. */
    private final double[][] multipliers;

    /** The geology of a data file, made once a reader: its classes the rules' with the hand table over them. Refused where a hand row's province is not the data's. */
    public static Geology of(EarthData d) {
        synchronized (OF) {
            return OF.computeIfAbsent(d, Geology::new);
        }
    }

    private Geology(EarthData d) {
        this.data = d;
        this.grid = d.raw("geology");
        this.cpd = d.layer("geology").cellsPerDegree;
        List<EarthData.Province> ps = d.provinces();
        int n = 1;
        for (EarthData.Province p : ps) n = Math.max(n, p.row() + 1);
        kinds = new Kind[n];
        rules = new Kind[n];
        petroleum = new double[n];
        java.util.Arrays.fill(kinds, Kind.OTHER);
        java.util.Arrays.fill(rules, Kind.OTHER);
        java.util.Arrays.fill(petroleum, 1);
        for (EarthData.Province p : ps) {
            Kind k = Kind.of(p.kind());
            if (k == null) throw new IllegalStateException("earth data: province " + p.row() + " (" + p.name() + ") has a class the rules do not give, " + p.kind());
            kinds[p.row()] = rules[p.row()] = k;
            petroleum[p.row()] = petroleumFactor(p.knownMmboe());
        }
        for (Hand h : TABLE) {
            EarthData.Province p = h.row() < n ? d.province(h.row()) : null;
            if (p == null || !p.name().trim().equals(h.name()) || !p.code().equals(h.code()) || rules[h.row()] != h.rules())
                throw new IllegalStateException("Geology.HAND row " + h.row() + " is " + h.code() + " " + h.name() + " (" + h.rules().label() + ") and the data's is "
                        + (p == null ? "missing" : p.code() + " " + p.name() + " (" + p.kind() + ")") + ": the table was checked against earth-1");
            kinds[h.row()] = h.kind();
        }
        Resource[] all = Resource.values();
        multipliers = new double[all.length][n];
        for (Resource r : all) {
            for (int row = 0; row < n; row++) {
                multipliers[r.ordinal()][row] = weight(r, kinds[row]) * z(r) * (r == Resource.OIL ? petroleum[row] : 1);
            }
        }
    }

    /** The data it was read from. */
    public EarthData data() { return data; }

    /** The rows a province can have: one past the highest. */
    public int rows() { return kinds.length; }

    /** The province at a point, its row in the data's table; 0 for none (EarthData.provinceAt()'s rule, on the grid in hand). */
    public int provinceAt(double lat, double lon) {
        long g = Math.min(180L * cpd - 1, Math.max(0, (long) StrictMath.floor((90 - lat) * cpd)));
        double t = (lon + 180) % 360;
        if (t < 0) t += 360;
        if (t >= 360) t -= 360;
        long c = (long) StrictMath.floor(t * cpd) % (360L * cpd);
        int at = (int) (2 * (g * 360 * cpd + c));
        return ((grid[at] & 0xFF) << 8) | (grid[at + 1] & 0xFF);
    }

    /** A province's class, the hand table's over the rules'; OTHER for row 0 (no province) or a row past the table. */
    public Kind kindOf(int row) { return row > 0 && row < kinds.length ? kinds[row] : Kind.OTHER; }

    /** A province's class by geology.py's rules alone, as the data carries it. */
    public Kind rulesKindOf(int row) { return row > 0 && row < rules.length ? rules[row] : Kind.OTHER; }

    /** A province's petroleum factor (petroleumFactor() of its known petroleum); 1 for none. */
    public double petroleumOf(int row) { return row > 0 && row < petroleum.length ? petroleum[row] : 1; }

    /** What a province does to a resource's density: w x Z, and for oil x its petroleum factor - the spot's multiplier the founding card is to name (E4). One for forest. */
    public double multiplier(Resource r, int row) {
        return row >= 0 && row < kinds.length ? multipliers[r.ordinal()][row] : weight(r, Kind.OTHER) * z(r);
    }

    /** Whether a province's sea holds oil (seaHoldsOil() of its class). */
    public boolean seaHoldsOil(int row) { return seaHoldsOil(kindOf(row)); }

    /* =====================================================================
       MEASURING THE PLANET (what Z and the odds are derived from; the check's)
       ===================================================================== */

    /**
     * Every 4' cell of the far layers, its area on the sphere: the land
     * (dry and lake) and the sea in each province, km2, north of
     * ANTARCTIC_LAT and in all; by row. What Z, the odds and the class
     * shares are worked out from (zOf(), oddsOf()) - about half a second
     * on the full file.
     */
    public record Measure(double[] land, double[] sea, double[] landAll) {

        /** The land north of ANTARCTIC_LAT, km2. */
        public double totalLand() {
            double s = 0;
            for (double v : land) s += v;
            return s;
        }

        /** The land in each class north of ANTARCTIC_LAT, km2, by Kind's order, each province by the class `kinds` gives its row. */
        public double[] landByKind(Kind[] kinds) {
            double[] out = new double[Kind.values().length];
            for (int row = 0; row < land.length; row++) out[kindAt(kinds, row).ordinal()] += land[row];
            return out;
        }

        /** ...and the land in all, Antarctica in. */
        public double[] landAllByKind(Kind[] kinds) {
            double[] out = new double[Kind.values().length];
            for (int row = 0; row < landAll.length; row++) out[kindAt(kinds, row).ordinal()] += landAll[row];
            return out;
        }

        /** A resource's Z with the classes `kinds` gives each row and the petroleum factors `petroleum` (oil's): the land over the land x w (x the factor), north of ANTARCTIC_LAT. */
        public double zOf(Resource r, Kind[] kinds, double[] petroleum) {
            double all = 0, weighted = 0;
            for (int row = 0; row < land.length; row++) {
                all += land[row];
                weighted += land[row] * weight(r, kindAt(kinds, row)) * (r == Resource.OIL ? petroleum[row] : 1);
            }
            return all / weighted;
        }

        /** Oil's offshore area against its land's, both x w x the petroleum factor: what the sea of shelves, basins and rifts adds to the land's count. */
        public double offshoreOil(Kind[] kinds, double[] petroleum) {
            double onLand = 0, atSea = 0;
            for (int row = 0; row < land.length; row++) {
                Kind k = kindAt(kinds, row);
                double w = weight(Resource.OIL, k) * petroleum[row];
                onLand += land[row] * w;
                if (seaHoldsOil(k)) atSea += sea[row] * w;
            }
            return atSea / onLand;
        }

        /** The fields expected of a resource in each class, as shares: its land x w there over the sum, north of ANTARCTIC_LAT - what an Earth-wide type share is weighted by. */
        public double[] fieldShares(Resource r, Kind[] kinds) {
            double[] by = landByKind(kinds), out = new double[by.length];
            double all = 0;
            for (Kind k : Kind.values()) all += out[k.ordinal()] = by[k.ordinal()] * weight(r, k);
            for (int i = 0; i < out.length; i++) out[i] /= all;
            return out;
        }

        /** The Earth-wide share of the leaning type at odds c: each class's lean weighted by fieldShares(). */
        public static double shareAt(double[] fieldShares, double[] ratio, double odds) {
            double s = 0;
            for (int i = 0; i < fieldShares.length; i++) s += fieldShares[i] * lean(odds, ratio[i]);
            return s;
        }

        /** The odds whose Earth-wide share is `target`, by bisection on its logarithm to the last bit. */
        public static double oddsFor(double[] fieldShares, double[] ratio, double target) {
            double lo = -30, hi = 30;
            for (int i = 0; i < 200; i++) {
                double mid = (lo + hi) / 2;
                if (shareAt(fieldShares, ratio, StrictMath.exp(mid)) < target) lo = mid; else hi = mid;
            }
            return StrictMath.exp((lo + hi) / 2);
        }

        private static Kind kindAt(Kind[] kinds, int row) { return row > 0 && row < kinds.length ? kinds[row] : Kind.OTHER; }
    }

    /** Its classes by row, the hand table's over the rules' (a copy). */
    public Kind[] kinds() { return kinds.clone(); }

    /** Its classes by row by the rules alone (a copy). */
    public Kind[] rulesKinds() { return rules.clone(); }

    /** Its petroleum factors by row (a copy). */
    public double[] petroleum() { return petroleum.clone(); }

    /** Measures the planet on its data (Measure): every 4' cell of far.land and far.lake, its centre's province, its area on the sphere at EarthProj.RADIUS_M. */
    public Measure measure() {
        EarthData.Layer farLand = data.layer("far.land");
        int fcpd = farLand.cellsPerDegree, cols = 360 * fcpd, n = kinds.length;
        double[] land = new double[n], sea = new double[n], landAll = new double[n];
        double r2 = EarthProj.RADIUS_M / 1000 * (EarthProj.RADIUS_M / 1000);
        for (int b = 0; b < 180; b++) {
            byte[] dry = data.grid("far.land", b), lake = data.grid("far.lake", b);
            for (int r = 0; r < fcpd; r++) {
                double lat1 = b - 89 - r / (double) fcpd, lat0 = lat1 - 1.0 / fcpd, lat = (lat0 + lat1) / 2;
                double area = r2 * StrictMath.toRadians(1.0 / fcpd) * (StrictMath.sin(StrictMath.toRadians(lat1)) - StrictMath.sin(StrictMath.toRadians(lat0)));
                boolean counted = lat >= ANTARCTIC_LAT;
                for (int c = 0; c < cols; c++) {
                    int v = (dry[r * cols + c] & 0xFF) + (lake[r * cols + c] & 0xFF);
                    int row = provinceAt(lat, -180 + (c + 0.5) / fcpd);
                    double l = v / (double) EarthData.FAR_LEVELS * area, s = Math.max(0, EarthData.FAR_LEVELS - v) / (double) EarthData.FAR_LEVELS * area;
                    landAll[row] += l;
                    if (!counted) continue;
                    land[row] += l;
                    sea[row] += s;
                }
            }
        }
        return new Measure(land, sea, landAll);
    }
}

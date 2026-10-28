"""The world pipeline's shared ground (batch WN0, 2026-10-28): the nations, their numbers, the years and the dials
every stage reads. FORMAT.txt is the file these stages make.

WHY: four stages (geo.py, econ_paths.py, econ_trade.py, bake.py) are written side by side; a nation's number or a
freight dial written twice would drift. Run every stage with python -I (the sources are downloads); a stage puts
its own folder on sys.path itself to import this.
"""

# The 196 (spec-nations 3.1, D1): the 193 UN members, Taiwan, Hong Kong and Macau, by ISO 3166 alpha-3, A to Z.
# A nation's number is its place here plus one.
NATIONS = ('AFG AGO ALB AND ARE ARG ARM ATG AUS AUT AZE BDI BEL BEN BFA BGD BGR BHR BHS BIH BLR BLZ BOL BRA BRB BRN '
           'BTN BWA CAF CAN CHE CHL CHN CIV CMR COD COG COL COM CPV CRI CUB CYP CZE DEU DJI DMA DNK DOM DZA ECU EGY '
           'ERI ESP EST ETH FIN FJI FRA FSM GAB GBR GEO GHA GIN GMB GNB GNQ GRC GRD GTM GUY HKG HND HRV HTI HUN IDN '
           'IND IRL IRN IRQ ISL ISR ITA JAM JOR JPN KAZ KEN KGZ KHM KIR KNA KOR KWT LAO LBN LBR LBY LCA LIE LKA LSO '
           'LTU LUX LVA MAC MAR MCO MDA MDG MDV MEX MHL MKD MLI MLT MMR MNE MNG MOZ MRT MUS MWI MYS NAM NER NGA NIC NLD '
           'NOR NPL NRU NZL OMN PAK PAN PER PHL PLW PNG POL PRK PRT PRY QAT ROU RUS RWA SAU SDN SEN SGP SLB SLE SLV '
           'SMR SOM SRB SSD STP SUR SVK SVN SWE SWZ SYC SYR TCD TGO THA TJK TKM TLS TON TTO TUN TUR TUV TWN TZA UGA '
           'UKR URY USA UZB VCT VEN VNM VUT WSM YEM ZAF ZMB ZWE').split()
assert len(NATIONS) == 196 and NATIONS == sorted(set(NATIONS))

# The three that are not UN members.
NOT_UN = ('HKG', 'MAC', 'TWN')

# The nations with no coast of the open sea (the Caspian's shores count as none): their freight starts by road.
LANDLOCKED = set(('AFG AND ARM AUT AZE BDI BFA BLR BOL BTN BWA CAF CHE CZE ETH HUN KAZ KGZ LAO LIE LSO LUX MDA MKD '
                  'MLI MNG MWI NER NPL PRY RWA SMR SRB SSD SVK SWZ TCD TJK TKM UGA UZB ZMB ZWE').split())

# The fixture's twelve (FORMAT.txt, THE FIXTURE): the earth fixture's windows' nations (Rotterdam's three, Paris,
# Perth, Halifax, Medellin), the largest economies, and one landlocked.
FIXTURE_NATIONS = 'AUS BEL CAN CHE CHN COL DEU FRA GBR JPN NLD USA'.split()


def number(iso3):
    """A nation's number in the full file, 1..196."""
    return NATIONS.index(iso3) + 1


FIRST_YEAR = 2000   # the paths' first year: Historical starts in January 2000 (D13)
LAST_DATA_YEAR = 2025   # the last year of published output, prices and rates
LAST_YEAR = 2100    # the people's last year (WPP 2024's horizon)
YEARS = LAST_YEAR - FIRST_YEAR + 1
MONTHS = (LAST_DATA_YEAR - FIRST_YEAR + 1) * 12   # 2000-01 to 2025-12: the monthly rates
BASE_YEAR = 2024    # the trade's year: BACI V202601's newest

# ----- the reference freight (FORMAT.txt, trade and freight); the dials go to meta with these sentences -----
# A tonne-km by sea, US$: between PortCheck's dry bulk rows and a container's (est.).
SEA_USD_TKM = 0.003
# A tonne-km by land, US$: a rail and lorry mix, under PortCheck's lorry at 0.080 (est.).
LAND_USD_TKM = 0.05
# A tonne's handling on a trade between two nations, US$: the ports, the papers, the legs to the quay (est.).
HANDLING_USD_T = 10.0
# The land km a sea km is worth on the cheapest route: the two rates' ratio.
LAND_PER_SEA = LAND_USD_TKM / SEA_USD_TKM
# A road's length over the great circle between two neighbours' nodes (est.).
LAND_DETOUR = 1.3
# A sea route's length over the great circle, for an island CERDI lacks (est.).
SEA_DETOUR = 1.25

DIALS = [('SEA_USD_TKM', SEA_USD_TKM, "A tonne-km by sea, US$: between PortCheck's dry bulk rows and a container's (est.)."),
         ('LAND_USD_TKM', LAND_USD_TKM, "A tonne-km by land, US$: a rail and lorry mix, under PortCheck's lorry at 0.080 (est.)."),
         ('HANDLING_USD_T', HANDLING_USD_T, "A tonne's handling on a trade between two nations, US$ (est.)."),
         ('LAND_PER_SEA', LAND_PER_SEA, "The land km a sea km is worth on the cheapest route: the two rates' ratio."),
         ('LAND_DETOUR', LAND_DETOUR, "A road's length over the great circle between two neighbours' nodes (est.)."),
         ('SEA_DETOUR', SEA_DETOUR, "A sea route's length over the great circle, for an island CERDI lacks (est.).")]

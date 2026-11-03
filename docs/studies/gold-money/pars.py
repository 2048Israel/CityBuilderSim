"""The 1900 gold pars, derived from the coinage laws' fine gold per unit (the gold-money study, 2026-11-02).

Facts in law are nobody's (docs/studies/licences.md); the arithmetic is shown so every par in the study is a sum.
A troy ounce is 480 grains = 31.1034768 g; a grain is 0.06479891 g. Run: python -I docs/studies/gold-money/pars.py
"""
GRAIN = 0.06479891
OUNCE = 480 * GRAIN
USD_1900 = 23.22 * GRAIN          # Gold Standard Act 1900: 25.8 grains nine-tenths fine
USD_1934 = OUNCE / 35             # Proclamation 2072, 31 Jan 1934: $35 an ounce

LAWS = [  # money, grams of fine gold a unit, the law
    ("Japanese yen", 0.75, "Coinage Act 1897: 1 yen = 750 mg fine"),
    ("Swedish krona", 1000 / 2480, "Scandinavian Monetary Union 1873: 2,480 kronor a kg fine"),
    ("British pound", 113.0016 * GRAIN, "the sovereign: 113.0016 grains fine a pound"),
    ("German mark", 1000 / 2790, "Coinage Act 1873: 2,790 marks a kg fine"),
    ("French franc", 0.9 * 1000 / 3100, "Latin Union: 3,100 francs a kg of gold nine-tenths fine"),
    ("Russian ruble", 0.774234, "the reform of 1897"),
    ("Austro-Hungarian krone", 1000 / 3280, "the reform of 1892: 3,280 kronen a kg fine"),
    ("Dutch guilder", 0.6048, "the law of 1875"),
    ("Indian rupee", 113.0016 * GRAIN / 15, "fixed at 15 a pound sterling, 1899"),
    ("Canadian dollar", USD_1900, "at par with the US$"),
]

if __name__ == "__main__":
    print(f"US$ 1900: {USD_1900:.6f} g fine, so ${OUNCE / USD_1900:.4f} an ounce; 1934: {USD_1934:.6f} g, $35.0000 an ounce")
    print(f"a money that kept its gold through 1934 rose {35 / (OUNCE / USD_1900) - 1:.2%} against the US$")
    print(f"{'money':24} {'g fine':>9} {'per US$ 1900':>13} {'per US$ 1934':>13}  law")
    for name, grams, law in LAWS:
        print(f"{name:24} {grams:9.6f} {USD_1900 / grams:13.5f} {USD_1934 / grams:13.4f}  {law}")

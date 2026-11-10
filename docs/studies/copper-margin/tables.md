# The copper margin study's tables (CU1)

What the probes in [probes.md](probes.md) printed, for [spec-copper-margin.md](../spec-copper-margin.md). Money is
thousands of D$, a year's sum unless a line says otherwise; the months are the default playtest's (month 1,200 is
the year 2000, 2,400 the year 2100). "Parity" is a good's band over its literal band, over the cathode's same
ratio: 1 is the literals' own ratio.

## 1. LS1's city: 0.9.60 (092130f4), the default playtest, seed 0, 4,000 months

The works standing (rod = Rod Mill, wire = Wire & Cable Works; the first six columns are the mines, the leach plant, the smelter and the two refineries):

```
month      mine   bigmine     leach     smelt    erefin  scrapref       rod      wire   pop    copper-level  p
  600         0         0         0         0         0         0         0         2     16239 0.706600    18
 1200         0         0         0         0         0         0         0        11     22169  1.07469    16
 1440         0         0         0         0         0         0         2         4     27651 0.837124    31
 1680         0         0         0         0         0         0         0        15     76483  1.31026    37
 1800         0         0         0         0         0         0         2        15     97156  1.28309    10
 1920         0         0         0         0         0         0        90        15    140972 0.947232    94
 2160         0         0         0         0         0         0       128        17    184336 0.824343    59
 2400         0         0         0         0         0         0       276        34    280754  1.26297    77
 2880         0         0         0         0         0         0       730        52    401125 0.935400    15
 3264         0         0         0         0         0         0       981       162    638790 0.999311     2
 3600         0         0         0         0         0         0      1318       191    768731  1.05051     6
 3996         0         0         0         0         0         0      1679       313    922976  1.17499 2.993
```

Copper Refining's books, a year to the month:

```
month   revenue     in/rev  pay/rev  util/rev  tax+int/rev net/rev  workers  rate  | refining rev/GDP
 1440  4.337e+06   95.5%    1.1%    0.0%     2.8%    -21.8%      155  0.54  |  36.1%  va/GDP   1.6%
 1680          0     -       -       -        -         -          0  0.90  |   0.0%  va/GDP   0.0%
 1800  1.415e+07   61.3%    0.8%    0.0%     8.3%      2.6%      122  0.48  |   7.3%  va/GDP   2.8%
 1920  7.741e+08   61.3%    0.8%    0.0%     4.9%     32.5%     6598  0.79  | 139.8%  va/GDP  54.0%
 2160  5.307e+09   59.4%    1.1%    0.0%     5.0%     34.3%     9618  0.69  | 107.7%  va/GDP  43.8%
 2400  2.155e+10   57.3%    1.2%    0.0%     5.2%     36.0%    20811  0.70  | 143.5%  va/GDP  61.3%
 2880  4.346e+10   46.2%    2.3%    0.0%     9.9%     41.1%    55338  0.39  | 122.1%  va/GDP  65.7%
 3264  8.743e+11   52.1%    2.0%    0.0%    10.9%     34.0%    74471  0.38  | 148.6%  va/GDP  71.2%
 3600  2.506e+12   47.7%    2.8%    0.0%    11.8%     36.6%   100442  0.27  | 120.0%  va/GDP  62.8%
 3996  2.183e+13   46.9%    1.8%    0.0%    15.1%     34.7%   128267  0.31  | 136.3%  va/GDP  72.4%
```

The year's tonnes (thousands) and the books against the probe's own sum:

```
month 2400
  COPPER        mkt imp   18202.6 exp       0.0 loc       0.0 | ref made       0.0 sold       0.0 exp       0.0 capt       0.0 | bought loc       0.0 imp   18202.6 | mine made       0.0 | sales          0 buys   1.16e+10
  COPPER_ROD    mkt imp       0.0 exp   17496.3 loc     585.4 | ref made   18081.7 sold     585.4 exp   17496.3 capt       0.0 | bought loc       0.0 imp       0.0 | mine made       0.0 | sales  2.128e+10 buys          0
  WIRE_CABLE    mkt imp       0.0 exp     583.6 loc       0.0 | ref made       0.0 sold       0.0 exp       0.0 capt       0.0 | bought loc       0.0 imp       0.0 | mine made       0.0 | sales          0 buys          0
  books: revenue 2.1553e+10 inputs 1.234e+10 | probe's sales at the month's prices 2.1278e+10, buys 1.1604e+10, old copper paid 0 (0.0 kt of 0.1 kt arisen)
month 3996
  COPPER        mkt imp   50489.8 exp       0.0 loc       0.0 | ref made       0.0 sold       0.0 exp       0.0 capt       0.0 | bought loc       0.0 imp   50489.8 | mine made       0.0 | sales          0 buys  1.002e+13
  COPPER_ROD    mkt imp       0.0 exp   47873.1 loc    2515.9 | ref made   50389.0 sold    2515.9 exp   47873.1 capt       0.0 | bought loc       0.0 imp       0.0 | mine made       0.0 | sales  2.214e+13 buys          0
  WIRE_CABLE    mkt imp       0.0 exp    2515.9 loc       0.0 | ref made       0.0 sold       0.0 exp       0.0 capt       0.0 | bought loc       0.0 imp       0.0 | mine made       0.0 | sales          0 buys          0
  books: revenue 2.1832e+13 inputs 1.0241e+13 | probe's sales at the month's prices 2.2137e+13, buys 1.0024e+13, old copper paid 0 (0.0 kt of 0.5 kt arisen)
```

Each copper good's band over parity with the cathode: with nothing traded (floor/ceiling), then as the city's own tonnes leave it (set):

```
each good's band over its literal band, over cathode's same ratio (1 = parity with cathode): nothing traded floor/ceiling | as set floor/ceiling
 1200 cathode floor/literal 0.7589 | conc 1.09/1.11 set 1.09/1.11 | anode 0.77/0.80 set 0.77/0.80 | copper 1.00/1.02 set 1.00/1.02 | scrap 1.01/1.06 set 1.01/1.06 | rod 1.01/1.03 set 1.01/1.04 | acid 1.35/1.33 set 1.35/1.33 | wire_c 1.18/1.21 set 1.18/1.21 |
 1440 cathode floor/literal 0.5293 | conc 1.18/1.24 set 1.18/1.24 | anode 0.80/0.86 set 0.80/0.86 | copper 1.00/1.04 set 1.00/1.04 | scrap 1.11/1.24 set 1.11/1.24 | rod 1.09/1.13 set 1.09/1.13 | acid 1.74/1.83 set 1.74/1.83 | wire_c 1.40/1.52 set 1.40/1.52 |
 1800 cathode floor/literal 0.5301 | conc 1.38/1.38 set 1.38/1.38 | anode 0.93/0.96 set 0.93/0.96 | copper 1.00/1.02 set 1.00/1.02 | scrap 1.69/1.61 set 1.69/1.61 | rod 1.66/1.61 set 1.66/1.64 | acid 1.74/1.57 set 1.74/1.57 | wire_c 1.43/1.43 set 1.43/1.43 |
 1920 cathode floor/literal 0.3321 | conc 1.46/1.52 set 1.46/1.52 | anode 1.01/1.07 set 1.01/1.07 | copper 1.00/1.06 set 1.01/1.08 | scrap 2.01/2.02 set 2.01/2.02 | rod 1.96/1.91 set 1.85/1.88 | acid 2.62/2.45 set 2.62/2.45 | wire_c 2.22/2.23 set 2.21/2.23 |
 2400 cathode floor/literal 0.3263 | conc 1.46/1.53 set 1.46/1.53 | anode 0.86/0.93 set 0.86/0.93 | copper 1.00/1.06 set 1.02/1.09 | scrap 2.58/2.56 set 2.58/2.56 | rod 2.58/2.50 set 1.93/2.45 | acid 2.42/2.39 set 2.42/2.39 | wire_c 2.13/2.17 set 2.13/2.17 |
 2880 cathode floor/literal 0.2117 | conc 1.51/1.68 set 1.51/1.68 | anode 0.92/1.05 set 0.92/1.05 | copper 1.00/1.12 set 1.01/1.14 | scrap 3.05/3.13 set 3.05/3.13 | rod 2.86/2.79 set 2.42/2.74 | acid 3.46/3.50 set 3.46/3.50 | wire_c 3.16/3.25 set 3.16/3.25 |
 3996 cathode floor/literal 0.2784 | conc 1.45/1.55 set 1.45/1.55 | anode 0.88/0.97 set 0.88/0.97 | copper 1.00/1.08 set 1.01/1.09 | scrap 2.82/2.83 set 2.82/2.83 | rod 2.66/2.58 set 2.33/2.56 | acid 3.29/2.81 set 3.29/2.81 | wire_c 2.77/2.76 set 2.77/2.75 |
```

What the planner weighs (a month's earnings over the works' cost):

```
month 1440
  Copper Smelter         earns -1.055e+05 cost  3.759e+06 score -0.0281  MONEY would lose $105;462.8k a month on it
  Electrolytic Refinery  earns  3.274e+04 cost  1.353e+06 score  0.0242  MONEY would earn $32;740.6k a month on $1;353;370k; which would not clear 1.25 times the interes
  Scrap Copper Refinery  earns -1.959e+04 cost  2.105e+06 score -0.0093  MONEY would lose $19;591.6k a month on it
  Rod Mill               earns     -881.5 cost   3.69e+05 score -0.0024  MONEY would lose $881.5k a month on it
month 2400
  Copper Smelter         earns -3.019e+06 cost  9.574e+07 score -0.0315  MONEY would lose $3;019;348.7k a month on it
  Electrolytic Refinery  earns  3.122e+05 cost  3.447e+07 score  0.0091  MONEY would earn $312;241.9k a month on $34;465;352k; which would not clear 1.25 times the inter
  Scrap Copper Refinery  earns -2.337e+06 cost  5.361e+07 score -0.0436  MONEY would lose $2;337;062.8k a month on it
  Rod Mill               earns  2.582e+06 cost  9.141e+06 score  0.2825  null null
month 3996
  Copper Smelter         earns   -8.2e+08 cost  2.958e+10 score -0.0277  MONEY would lose $820;046;633.6k a month on it
  Electrolytic Refinery  earns -7.797e+07 cost  1.065e+10 score -0.0073  MONEY would lose $77;966;275.6k a month on it
  Scrap Copper Refinery  earns -4.586e+08 cost  1.657e+10 score -0.0277  MONEY would lose $458;583;923.3k a month on it
  Rod Mill               earns    5.4e+08 cost  2.884e+09 score  0.1873  null null
```

## 2. Today's main: 0.9.69 (cce06c9c), the default playtest

### seed 0, 4,000 months

```
month      mine   bigmine     leach     smelt    erefin  scrapref       rod      wire   pop    copper-level  p
  600         0         0         0         0         0         0         0         3     14785 0.706600    30
  720         0         0         0         0         0         0         0         7     13574  1.05299    47
 1200         0         0         0         0         0         0         0        22     97199  1.07469    11
 2400         0         0         0         0         0         0         0        61    562463  1.26297    14
 3996         0         0         0         0         0         0         0       195   1379379  1.17499    61
month 2400
  Copper Smelter         earns -4.875e+05 cost    1.9e+07 score -0.0257  MONEY would lose $487;517.7k a month on it
  Electrolytic Refinery  earns     -268.7 cost  6.839e+06 score -0.0000  MONEY would lose $268.7k a month on it
  Scrap Copper Refinery  earns  -3.89e+05 cost  1.064e+07 score -0.0366  MONEY would lose $388;983.8k a month on it
  Rod Mill               earns  7.602e+05 cost  1.816e+06 score  0.4186  null null
```

### seed 1

```
month      mine   bigmine     leach     smelt    erefin  scrapref       rod      wire   pop    copper-level  p
  600         0         0         0         0         0         0         0         0      9508 0.706600    10
 1200         0         0         0         0         0         0         0        14     48320  1.07469    84
 1800         0         0         0         0         0         0        13        42    169374  1.28309    37
 2040         0         0         0         0         0         0       163        46    270797  1.27995    98
 2280         0         0         0         0         0         0       295        81    412567 0.721669    34
 2400         0         0         0         0         0         0       363        81    427917  1.26297    28
 1800  4.999e+07   64.2%    0.7%    0.0%     2.6%      6.5%      880  0.66  |  39.3%  va/GDP  14.1%
 2040  1.537e+09   49.6%    0.7%    0.0%     6.1%     42.8%    12369  0.53  | 125.7%  va/GDP  63.4%
 2280  3.998e+09   62.2%    2.0%    0.0%     4.8%     30.2%    22168  0.41  | 104.3%  va/GDP  39.5%
 2400  6.758e+09   54.6%    1.6%    0.0%     5.6%     37.5%    27103  0.44  | 139.1%  va/GDP  63.1%
 1200 cathode floor/literal 0.7589 | conc 1.09/1.11 set 1.09/1.11 | anode 0.77/0.80 set 0.77/0.80 | copper 1.00/1.02 set 1.00/1.02 | scrap 1.01/1.06 set 1.01/1.06 | rod 1.01/1.03 set 1.01/1.08 | acid 1.35/1.33 set 1.35/1.33 | wire_c 1.18/1.21 set 1.17/1.21 |
 1800 cathode floor/literal 0.5301 | conc 1.38/1.38 set 1.38/1.38 | anode 0.93/0.96 set 0.93/0.96 | copper 1.00/1.02 set 1.00/1.02 | scrap 1.69/1.61 set 1.69/1.61 | rod 1.66/1.61 set 1.61/1.61 | acid 1.74/1.57 set 1.74/1.57 | wire_c 1.43/1.43 set 1.43/1.43 |
 2400 cathode floor/literal 0.3263 | conc 1.46/1.53 set 1.46/1.53 | anode 0.86/0.93 set 0.86/0.93 | copper 1.00/1.06 set 1.01/1.08 | scrap 2.58/2.56 set 2.58/2.56 | rod 2.58/2.50 set 2.00/2.46 | acid 2.42/2.39 set 2.42/2.39 | wire_c 2.13/2.17 set 2.13/2.17 |
month 2400
  Copper Smelter         earns -8.032e+05 cost  3.415e+07 score -0.0235  MONEY would lose $803;192.1k a month on it
  Electrolytic Refinery  earns  3.683e+04 cost  1.229e+07 score  0.0030  MONEY would earn $36;832.1k a month on $12;294;265k; which would not clear 1.25 times the intere
  Scrap Copper Refinery  earns -5.834e+05 cost  1.912e+07 score -0.0305  MONEY would lose $583;426.6k a month on it
  Rod Mill               earns  6.477e+05 cost  3.265e+06 score  0.1984  null null
```

### seed 2

```
month      mine   bigmine     leach     smelt    erefin  scrapref       rod      wire   pop    copper-level  p
  600         0         0         0         0         0         0         0         9     21925 0.706600    29
 1200         0         0         0         0         0         0         0        24     86228  1.07469    11
 1800         0         0         0         0         0         0         8        37    256771  1.28309    92
 2040         0         0         0         0         0         0       132        40    381905  1.27995    17
 2280         0         0         0         0         0         0       247        55    548562 0.721669    80
 2400         0         0         0         0         0         0       397        62    612856  1.26297    63
 1800  3.238e+07   66.5%    1.4%    0.0%     3.1%      1.9%      494  0.29  |  10.6%  va/GDP   3.6%
 2040  2.588e+09   49.6%    0.7%    0.0%     6.0%     42.7%    10026  0.62  |  98.1%  va/GDP  49.4%
 2280   9.89e+09   64.1%    1.6%    0.0%     4.3%     29.4%    18715  0.53  |  85.2%  va/GDP  30.6%
 2400  1.666e+10   56.2%    1.6%    0.0%     5.3%     36.5%    30252  0.43  | 125.7%  va/GDP  55.1%
 1200 cathode floor/literal 0.7672 | conc 1.08/1.05 set 1.08/1.05 | anode 0.76/0.77 set 0.76/0.77 | copper 1.00/1.00 set 1.00/1.00 | scrap 1.06/1.01 set 1.06/1.01 | rod 1.01/0.98 set 1.01/1.07 | acid 1.34/1.16 set 1.34/1.16 | wire_c 1.27/1.20 set 1.26/1.19 |
 1800 cathode floor/literal 0.5301 | conc 1.38/1.38 set 1.38/1.38 | anode 0.93/0.96 set 0.93/0.96 | copper 1.00/1.02 set 1.00/1.02 | scrap 1.69/1.61 set 1.69/1.61 | rod 1.66/1.61 set 1.66/1.63 | acid 1.74/1.57 set 1.74/1.57 | wire_c 1.43/1.43 set 1.43/1.43 |
 2400 cathode floor/literal 0.3263 | conc 1.46/1.53 set 1.46/1.53 | anode 0.86/0.93 set 0.86/0.93 | copper 1.00/1.06 set 1.02/1.08 | scrap 2.58/2.56 set 2.58/2.56 | rod 2.58/2.50 set 1.97/2.46 | acid 2.42/2.39 set 2.42/2.39 | wire_c 2.13/2.17 set 2.13/2.17 |
month 2400
  Copper Smelter         earns -1.752e+06 cost  5.865e+07 score -0.0299  MONEY would lose $1;751;912.4k a month on it
  Electrolytic Refinery  earns  1.206e+05 cost  2.112e+07 score  0.0057  MONEY would earn $120;635.3k a month on $21;115;288k; which would not clear 1.25 times the inter
  Scrap Copper Refinery  earns -1.285e+06 cost  3.285e+07 score -0.0391  MONEY would lose $1;285;046.0k a month on it
  Rod Mill               earns  1.421e+06 cost  5.731e+06 score  0.2480  null null
```

### seed 3

```
month      mine   bigmine     leach     smelt    erefin  scrapref       rod      wire   pop    copper-level  p
  600         0         0         0         0         0         0         0         0     18535 0.706600    24
 1200         0         0         0         0         0         0         0        18     60377  1.07469    51
 1800         0         0         0         0         0         0         8        26    179159  1.28309    15
 2040         0         0         0         0         0         0       195        26    350652  1.27995    30
 2280         0         0         0         0         0         0       311        44    444978 0.721669    83
 2400         0         0         0         0         0         0       446        53    521214  1.26297    58
 1800  1.466e+07   63.2%    0.6%    0.0%     2.9%      6.5%      496  0.75  |  21.4%  va/GDP   7.9%
 2040  3.573e+08   49.1%    1.3%    0.0%     6.4%     42.9%    14806  0.31  | 119.6%  va/GDP  60.9%
 2280  1.394e+09   66.5%    1.6%    0.0%     4.3%     27.4%    23770  0.55  | 117.4%  va/GDP  39.4%
 2400  2.091e+09   59.5%    1.4%    0.0%     5.1%     33.8%    34417  0.54  | 148.0%  va/GDP  59.9%
 1200 cathode floor/literal 0.7672 | conc 1.08/1.05 set 1.08/1.05 | anode 0.76/0.77 set 0.76/0.77 | copper 1.00/1.00 set 1.00/1.00 | scrap 1.06/1.01 set 1.06/1.01 | rod 1.01/0.98 set 1.01/1.06 | acid 1.34/1.16 set 1.34/1.16 | wire_c 1.27/1.20 set 1.26/1.20 |
 1800 cathode floor/literal 0.5301 | conc 1.38/1.38 set 1.38/1.38 | anode 0.93/0.96 set 0.93/0.96 | copper 1.00/1.02 set 1.00/1.02 | scrap 1.69/1.61 set 1.69/1.61 | rod 1.66/1.61 set 1.66/1.61 | acid 1.74/1.57 set 1.74/1.57 | wire_c 1.43/1.43 set 1.43/1.43 |
 2400 cathode floor/literal 0.3263 | conc 1.46/1.53 set 1.46/1.53 | anode 0.86/0.93 set 0.86/0.93 | copper 1.00/1.06 set 1.02/1.09 | scrap 2.58/2.56 set 2.58/2.56 | rod 2.58/2.50 set 1.85/2.44 | acid 2.42/2.39 set 2.42/2.39 | wire_c 2.13/2.17 set 2.13/2.17 |
month 2400
  Copper Smelter         earns -1.985e+05 cost  8.184e+06 score -0.0243  MONEY would lose $198;538.9k a month on it
  Electrolytic Refinery  earns  1.795e+04 cost  2.946e+06 score  0.0061  MONEY would earn $17;951.3k a month on $2;946;229k; which would not clear 1.25 times the interes
  Scrap Copper Refinery  earns -1.496e+05 cost  4.583e+06 score -0.0326  MONEY would lose $149;581.1k a month on it
  Rod Mill               earns  1.424e+05 cost  7.786e+05 score  0.1830  null null
```

### --earth rotterdam, seed 0

```
month      mine   bigmine     leach     smelt    erefin  scrapref       rod      wire   pop    copper-level  p
  600         0         0         0         0         0         0         0         2      7401 0.706600    26
 1200         0         0         0         0         0         0         0         5     10896  1.07469    14
 1800         0         0         0         0         0         0         0         2     14285  1.28309    16
 2400         0         0         0         0         0         0         0         0     14481  1.26297    12
 1200 cathode floor/literal 0.7544 | conc 1.05/1.07 set 1.05/1.07 | anode 0.77/0.79 set 0.77/0.79 | copper 1.00/1.01 set 1.00/1.01 | scrap 1.07/1.11 set 1.07/1.11 | rod 1.04/1.05 set 1.04/1.06 | acid 1.53/1.40 set 1.53/1.40 | wire_c 1.31/1.31 set 1.31/1.31 |
 1800 cathode floor/literal 0.5243 | conc 1.37/1.40 set 1.37/1.40 | anode 0.94/0.97 set 0.94/0.97 | copper 1.00/1.03 set 1.00/1.03 | scrap 1.71/1.71 set 1.71/1.71 | rod 1.71/1.71 set 1.71/1.72 | acid 2.26/2.04 set 2.26/2.04 | wire_c 1.77/1.76 set 1.77/1.76 |
 2400 cathode floor/literal 0.3205 | conc 1.45/1.56 set 1.45/1.56 | anode 0.86/0.96 set 0.86/0.96 | copper 1.00/1.09 set 1.00/1.09 | scrap 2.59/2.63 set 2.59/2.63 | rod 2.68/2.69 set 2.68/2.69 | acid 3.70/3.37 set 3.70/3.37 | wire_c 2.62/2.72 set 2.62/2.72 |
month 2400
  Copper Smelter         earns -7.196e+06 cost  1.152e+08 score -0.0625  MONEY would lose $7;195;514.7k a month on it
  Electrolytic Refinery  earns -3.706e+05 cost  4.148e+07 score -0.0089  MONEY would lose $370;565.5k a month on it
  Scrap Copper Refinery  earns -6.288e+06 cost  6.452e+07 score -0.0974  MONEY would lose $6;287;591.2k a month on it
  Rod Mill               earns  1.149e+07 cost  1.182e+07 score  0.9716  null null
```

### seed 0 on the Historical path (-Dplaytest.world=HISTORICAL), 4,000 months

```
month      mine   bigmine     leach     smelt    erefin  scrapref       rod      wire   pop    copper-level  p
  600         0         0         0         0         0         0         0         1      9004 0.340762    8.
 1200         0         0         0         0         0         0         1        43     76737 0.340762    14
 1800         0         0         0         0         0         0         0        57    283247  1.13276    45
 2400         0         0         0         0         0         0       251        68    638130  1.12382    59
 3996         0         0         0         0         0         0      1724       120   1271719  1.08397     7
 1200  5.332e+04   93.1%    2.6%    0.0%    16.0%    -36.5%       78  0.51  |   1.4%  va/GDP   0.1%
 2400  9.901e+09   71.4%    1.5%    0.0%     3.4%     22.9%    19046  0.33  | 150.1%  va/GDP  42.9%
 3996   5.05e+12   68.7%    1.9%    0.0%     8.8%     20.1%   132559  0.24  | 202.9%  va/GDP  63.5%
 1200 cathode floor/literal 0.3356 | conc 0.96/1.03 set 0.96/1.03 | anode 0.97/1.03 set 0.97/1.03 | copper 1.00/1.07 set 1.00/1.08 | scrap 0.88/1.12 set 0.88/1.12 | rod 0.98/1.05 set 1.01/1.16 | acid 4.65/3.38 set 4.65/3.38 | wire_c 3.78/3.45 set 3.26/3.31 |
 1800 cathode floor/literal 1.008 | conc 1.08/1.05 set 1.08/1.05 | anode 1.01/1.00 set 1.01/1.00 | copper 1.00/0.99 set 1.00/0.99 | scrap 1.16/1.10 set 1.16/1.10 | rod 1.13/1.10 set 1.14/1.17 | acid 1.03/0.90 set 1.03/0.90 | wire_c 0.99/0.95 set 0.95/0.94 |
 2400 cathode floor/literal 0.6647 | conc 1.12/1.12 set 1.12/1.12 | anode 0.93/0.94 set 0.93/0.94 | copper 1.00/1.01 set 1.05/1.08 | scrap 1.63/1.55 set 1.63/1.55 | rod 1.61/1.59 set 1.55/1.53 | acid 1.37/1.28 set 1.37/1.28 | wire_c 1.38/1.32 set 1.38/1.32 |
month 2400
  Copper Smelter         earns -8.677e+05 cost  4.796e+07 score -0.0181  MONEY would lose $867;717.8k a month on it
  Electrolytic Refinery  earns  2.115e+05 cost  1.727e+07 score  0.0122  null null
  Scrap Copper Refinery  earns -5.286e+05 cost  2.686e+07 score -0.0197  MONEY would lose $528;556.2k a month on it
  Rod Mill               earns  8.529e+05 cost  4.713e+06 score  0.1810  null null
```

## 3. The world alone: every traded good's band with nothing traded (WorldDrift)

Copper's goods over parity with the cathode:

```
site standin: each good's floor over its literal export price, over cathode's same ratio (1 = the literals' ratio)
Random: the median of 12 paths [10th-90th]; H: the Historical path
year  COPPER_CONC                   ANODE                         COPPER_SCRAP                  COPPER_ROD                    WIRE_CABLE                  
1900  0.93 [0.89-1.00] H 0.98       0.97 [0.94-1.01] H 0.96       0.81 [0.75-0.86] H 0.85       1.00 [0.96-1.02] H 0.98       4.94 [3.45-6.03] H 3.74       
1950  1.00 [0.93-1.02] H 0.98       0.96 [0.90-1.01] H 0.96       1.02 [0.99-1.08] H 0.85       1.03 [1.00-1.06] H 0.98       1.16 [0.83-1.71] H 3.74       
2000  1.01 [0.80-1.10] H 0.98       0.79 [0.74-0.88] H 0.96       1.00 [0.96-1.12] H 0.85       1.07 [1.02-1.19] H 0.98       1.26 [0.79-1.83] H 3.74       
2025  1.11 [0.89-1.20] H 1.00       0.77 [0.72-0.88] H 1.00       1.06 [0.90-1.11] H 1.00       1.09 [1.06-1.17] H 1.00       1.51 [0.77-1.91] H 1.00       
2050  1.29 [0.99-1.49] H 1.08       0.82 [0.74-0.97] H 1.01       1.25 [0.99-1.53] H 1.11       1.41 [1.14-1.58] H 1.11       1.49 [0.96-2.14] H 1.29       
2100  1.49 [1.20-1.79] H 1.11       0.95 [0.76-1.08] H 0.93       2.27 [1.38-2.53] H 1.63       2.41 [1.40-2.63] H 1.61       1.90 [1.21-2.88] H 1.34       
2150  1.57 [1.38-1.77] H 1.00       0.98 [0.83-1.14] H 0.88       2.47 [1.65-2.75] H 1.54       2.66 [1.71-2.89] H 1.54       2.05 [1.43-3.20] H 1.42       
2200  1.63 [1.49-1.77] H 1.03       1.03 [0.85-1.11] H 0.86       2.57 [1.80-3.06] H 1.58       2.68 [1.96-3.23] H 1.60       2.02 [1.52-2.79] H 1.47       
2230  1.65 [1.41-1.80] H 1.06       1.05 [0.88-1.14] H 0.87       2.66 [1.99-3.48] H 1.58       2.84 [2.14-3.50] H 1.60       2.25 [1.51-3.06] H 1.56       
site halifax: each good's floor over its literal export price, over cathode's same ratio (1 = the literals' ratio)
Random: the median of 12 paths [10th-90th]; H: the Historical path
year  COPPER_CONC                   ANODE                         COPPER_SCRAP                  COPPER_ROD                    WIRE_CABLE                  
1900  0.91 [0.88-0.98] H 0.89       0.98 [0.95-1.03] H 0.97       0.81 [0.75-0.86] H 0.84       1.00 [0.97-1.03] H 0.98       4.91 [3.45-6.08] H 3.74       
1950  1.01 [0.95-1.03] H 0.89       0.96 [0.90-1.00] H 0.97       1.04 [0.99-1.08] H 0.84       1.01 [1.00-1.06] H 0.98       1.15 [0.82-1.73] H 3.74       
2000  1.03 [0.82-1.11] H 0.89       0.78 [0.74-0.88] H 0.97       1.04 [1.00-1.17] H 0.84       1.07 [1.02-1.17] H 0.98       1.28 [0.78-1.80] H 3.74       
2025  1.10 [0.93-1.19] H 1.00       0.77 [0.71-0.88] H 1.00       1.08 [0.95-1.17] H 1.00       1.08 [1.05-1.17] H 1.00       1.62 [0.76-2.02] H 1.00       
2050  1.30 [1.01-1.49] H 1.07       0.81 [0.74-0.98] H 1.01       1.30 [1.04-1.63] H 1.11       1.41 [1.16-1.55] H 1.10       1.54 [1.03-2.34] H 1.29       
2100  1.49 [1.19-1.79] H 1.11       0.95 [0.76-1.08] H 0.93       2.26 [1.44-2.75] H 1.60       2.45 [1.38-2.66] H 1.62       2.08 [1.31-3.20] H 1.43       
2150  1.56 [1.38-1.77] H 0.99       0.98 [0.83-1.14] H 0.88       2.64 [1.72-2.98] H 1.51       2.68 [1.72-2.97] H 1.55       2.25 [1.54-3.53] H 1.53       
2200  1.63 [1.49-1.77] H 1.02       1.03 [0.85-1.11] H 0.86       2.73 [1.93-3.32] H 1.57       2.69 [1.97-3.32] H 1.61       2.24 [1.67-3.15] H 1.58       
2230  1.65 [1.40-1.80] H 1.05       1.05 [0.87-1.14] H 0.87       2.86 [2.17-3.68] H 1.58       2.86 [2.12-3.57] H 1.61       2.50 [1.67-3.40] H 1.67       
site rotterdam: each good's floor over its literal export price, over cathode's same ratio (1 = the literals' ratio)
Random: the median of 12 paths [10th-90th]; H: the Historical path
year  COPPER_CONC                   ANODE                         COPPER_SCRAP                  COPPER_ROD                    WIRE_CABLE                  
1900  0.97 [0.95-1.04] H 1.02       0.99 [0.97-1.02] H 1.03       0.88 [0.85-0.92] H 0.93       0.99 [0.97-1.00] H 1.00       5.00 [3.47-6.15] H 3.97       
1950  0.99 [0.93-1.03] H 1.02       0.95 [0.89-0.99] H 1.03       1.08 [1.05-1.10] H 0.93       1.03 [1.02-1.05] H 1.00       1.23 [0.85-1.78] H 3.97       
2000  1.02 [0.81-1.09] H 1.02       0.78 [0.74-0.88] H 1.03       1.08 [1.02-1.24] H 0.93       1.07 [1.03-1.16] H 1.00       1.36 [0.75-1.76] H 3.97       
2025  1.07 [0.89-1.16] H 1.00       0.77 [0.72-0.87] H 1.00       1.12 [0.97-1.25] H 1.02       1.11 [1.06-1.19] H 1.00       1.64 [0.77-1.96] H 1.04       
2050  1.29 [0.98-1.48] H 1.07       0.82 [0.74-0.98] H 1.01       1.32 [1.08-1.68] H 1.15       1.40 [1.16-1.57] H 1.14       1.58 [1.14-2.97] H 1.36       
2100  1.49 [1.19-1.78] H 1.10       0.95 [0.76-1.08] H 0.93       2.31 [1.46-2.84] H 1.62       2.43 [1.42-2.63] H 1.60       2.51 [1.55-4.10] H 1.55       
2150  1.55 [1.38-1.77] H 0.99       0.98 [0.83-1.14] H 0.88       2.70 [1.75-3.07] H 1.53       2.63 [1.70-2.92] H 1.53       2.81 [1.92-4.44] H 1.71       
2200  1.63 [1.48-1.77] H 1.02       1.03 [0.85-1.11] H 0.86       2.81 [2.07-3.49] H 1.59       2.68 [1.96-3.21] H 1.59       2.83 [2.16-4.18] H 1.73       
2230  1.65 [1.39-1.80] H 1.05       1.05 [0.88-1.14] H 0.88       2.98 [2.31-3.68] H 1.60       2.82 [2.20-3.49] H 1.59       3.18 [2.21-4.19] H 1.87       
```

Every works' feed over its sales (a works with a good the market does not trade both ways is left out):

```
site standin: a works' feed over its sales, feed at the ceiling (imported) and output at the floor (exported), case A;
literal, then Random's median of 12 paths [10th-90th] and (H) the Historical path
works                      sector             literal | 2000                     | 2100                     | 2200                     | H 2050 / 2100 / 2200
Industrial Bakery          Industry             0.38  | 0.40 [0.17-1.09]         | 0.29 [0.12-0.65]         | 0.40 [0.22-0.79]         | 0.49 / 0.34 / 0.37
Bakery                     Industry             0.38  | 0.40 [0.17-1.08]         | 0.29 [0.12-0.65]         | 0.40 [0.22-0.79]         | 0.49 / 0.34 / 0.37
Electric Arc Furnace       Heavy Industry       0.96  | 0.92 [0.79-1.07]         | 0.91 [0.76-1.21]         | 0.88 [0.76-1.43]         | 0.94 / 1.10 / 1.17
Steel Mini-Mill            Heavy Industry       0.96  | 0.92 [0.79-1.07]         | 0.91 [0.76-1.21]         | 0.88 [0.76-1.43]         | 0.94 / 1.10 / 1.17
Iron Mine                  Mining               0.02  | 0.04 [0.02-0.08]         | 0.02 [0.01-0.07]         | 0.02 [0.01-0.07]         | 0.02 / 0.03 / 0.02
Fabrication Shop           Manufacturing        0.44  | 0.42 [0.28-0.54]         | 0.42 [0.22-0.60]         | 0.37 [0.25-1.03]         | 0.51 / 0.47 / 0.40
Machine Works              Manufacturing        0.12  | 0.12 [0.09-0.17]         | 0.11 [0.08-0.16]         | 0.11 [0.07-0.21]         | 0.13 / 0.11 / 0.10
Fabrication Works          Manufacturing        0.44  | 0.42 [0.28-0.54]         | 0.42 [0.22-0.60]         | 0.37 [0.25-1.03]         | 0.51 / 0.47 / 0.40
Mixed Farm                 Agriculture          0.04  | 0.06 [0.03-0.08]         | 0.05 [0.03-0.10]         | 0.04 [0.03-0.09]         | 0.04 / 0.04 / 0.03
Grain Farm                 Agriculture          0.04  | 0.05 [0.02-0.19]         | 0.05 [0.03-0.10]         | 0.04 [0.03-0.08]         | 0.03 / 0.04 / 0.03
Livestock Farm             Agriculture          0.04  | 0.07 [0.04-0.09]         | 0.05 [0.03-0.10]         | 0.04 [0.03-0.08]         | 0.04 / 0.04 / 0.03
Meat Works                 Food Processing      0.73  | 0.67 [0.52-1.07]         | 0.58 [0.46-0.93]         | 0.75 [0.53-0.99]         | 0.79 / 0.70 / 0.76
Snack & Oils Plant         Food Processing      0.31  | 0.31 [0.28-0.39]         | 0.33 [0.28-0.40]         | 0.32 [0.27-0.40]         | 0.31 / 0.32 / 0.32
Bottling Plant             Food Processing      0.07  | 0.05 [0.02-0.27]         | 0.05 [0.03-0.16]         | 0.04 [0.03-0.09]         | 0.06 / 0.07 / 0.09
Pelletising Plant          Heavy Industry       0.94  | 0.75 [0.64-0.84]         | 0.95 [0.88-1.14]         | 0.94 [0.83-1.17]         | 0.87 / 1.02 / 1.13
Coke Ovens                 Heavy Industry       0.95  | 0.91 [0.73-1.17]         | 0.82 [0.77-1.24]         | 0.79 [0.70-1.03]         | 1.03 / 0.89 / 0.98
Mini Blast Furnace         Heavy Industry       0.96  | 0.95 [0.66-1.20]         | 1.07 [0.73-1.75]         | 1.26 [0.62-1.97]         | 0.81 / 1.32 / 1.28
Blast Furnace              Heavy Industry       0.86  | 0.83 [0.61-1.02]         | 0.94 [0.66-1.52]         | 1.10 [0.56-1.68]         | 0.73 / 1.16 / 1.14
Oxygen Steel Shop          Heavy Industry       1.09  | 1.01 [0.89-1.15]         | 1.08 [0.96-1.38]         | 1.08 [0.95-1.66]         | 1.06 / 1.18 / 1.19
Large Mini-Mill            Heavy Industry       0.96  | 0.92 [0.79-1.07]         | 0.91 [0.76-1.21]         | 0.88 [0.76-1.43]         | 0.94 / 1.10 / 1.17
Large Open-Pit Mine        Mining               0.02  | 0.04 [0.02-0.08]         | 0.02 [0.01-0.07]         | 0.02 [0.01-0.07]         | 0.02 / 0.03 / 0.02
Surface Coal Mine          Coal Mining          0.03  | 0.05 [0.01-0.08]         | 0.03 [0.01-0.05]         | 0.02 [0.01-0.04]         | 0.04 / 0.02 / 0.02
Large Surface Mine         Coal Mining          0.03  | 0.05 [0.01-0.08]         | 0.03 [0.01-0.05]         | 0.02 [0.01-0.04]         | 0.04 / 0.02 / 0.02
Cement Plant               Cement & Concrete    1.42  | 1.43 [1.21-1.64]         | 1.51 [1.31-1.71]         | 1.54 [1.41-1.70]         | 1.41 / 1.59 / 1.48
Large Cement Plant         Cement & Concrete    1.42  | 1.43 [1.21-1.64]         | 1.51 [1.31-1.71]         | 1.54 [1.41-1.70]         | 1.41 / 1.59 / 1.48
Lime Kiln                  Cement & Concrete    0.79  | 0.80 [0.67-0.92]         | 0.84 [0.72-0.95]         | 0.84 [0.77-0.93]         | 0.78 / 0.89 / 0.82
Leach & SX-EW Plant        Copper Mining        0.06  | 0.07 [0.04-0.10]         | 0.13 [0.09-0.23]         | 0.17 [0.13-0.24]         | 0.07 / 0.08 / 0.09
Copper Smelter             Copper Refining      0.96  | 1.28 [0.92-1.37]         | 1.63 [1.38-1.80]         | 1.60 [1.51-1.70]         | 1.04 / 1.18 / 1.20
Electrolytic Refinery      Copper Refining      1.02  | 0.83 [0.76-0.91]         | 1.04 [0.83-1.22]         | 1.15 [0.98-1.28]         | 1.05 / 0.98 / 0.92
Scrap Copper Refinery      Copper Refining      0.96  | 1.02 [0.94-1.14]         | 2.27 [1.36-2.72]         | 2.48 [1.93-3.23]         | 1.09 / 1.55 / 1.54
Rod Mill                   Copper Refining      1.03  | 0.97 [0.91-1.01]         | 0.46 [0.44-0.76]         | 0.43 [0.34-0.58]         | 0.94 / 0.66 / 0.67
Wire & Cable Works         Manufacturing        0.83  | 0.69 [0.51-1.08]         | 0.95 [0.78-1.53]         | 1.04 [0.79-1.58]         | 0.72 / 0.99 / 0.90
Sawmill                    Wood Products        0.81  | 0.82 [0.72-0.92]         | 0.94 [0.69-1.12]         | 0.99 [0.83-1.25]         | 0.82 / 0.76 / 0.91
Large Sawmill              Wood Products        0.81  | 0.82 [0.72-0.92]         | 0.94 [0.69-1.12]         | 0.99 [0.83-1.25]         | 0.82 / 0.76 / 0.91
Panel Mill                 Wood Products        0.64  | 0.69 [0.58-0.88]         | 0.80 [0.65-1.06]         | 0.89 [0.53-1.28]         | 0.69 / 0.68 / 0.80
Pulp Mill                  Wood Products        0.53  | 0.54 [0.51-0.59]         | 0.61 [0.58-0.67]         | 0.62 [0.58-0.69]         | 0.50 / 0.58 / 0.59
Paper Mill                 Wood Products        0.99  | 1.08 [0.92-1.22]         | 1.04 [0.81-1.49]         | 1.15 [0.86-1.45]         | 1.12 / 1.02 / 1.05
Wood Pellet Mill           Wood Products        0.66  | 0.61 [0.55-0.71]         | 0.73 [0.70-0.80]         | 0.78 [0.67-0.84]         | 0.62 / 0.68 / 0.64
```

How far the goods' bands stand apart:

```
site standin: the spread of every traded good's floor over its literal, around the median good's (sd of the log), Random's median of paths, and H
1900  Random median sd 0.77 [0.65-0.90]  H 0.63
1950  Random median sd 0.45 [0.24-0.62]  H 0.63
2000  Random median sd 0.45 [0.28-0.78]  H 0.63
2025  Random median sd 0.49 [0.32-0.71]  H 0.00
2050  Random median sd 0.42 [0.30-0.67]  H 0.14
2100  Random median sd 0.42 [0.33-0.78]  H 0.17
2150  Random median sd 0.50 [0.28-0.70]  H 0.23
2200  Random median sd 0.49 [0.39-0.63]  H 0.22
2230  Random median sd 0.50 [0.39-0.58]  H 0.19
```

Who sets the band (Sellers; the default path, the stand-in site; prices over the good's literal floor):

```
2025 COPPER     makers 196: mill price over the literal floor, lowest 0.47, median 0.75, highest 0.79 | the site's floor 0.51, ceiling 0.54
     cheapest landed sellers: DR Congo mill 0.47 landed 0.56 cap 6.19e+03 k units/mo; Peru mill 0.52 landed 0.58 cap 481 k units/mo; Mozambique mill 0.50 landed 0.61 cap 37.8 k units/mo;
     best sinks: United States buys from DR Congo, netback 0.51, pays 0.54, cap 1.22e+03 k units/mo; Samoa buys from Myanmar, netback 0.49, pays 0.58, cap 0.432 k units/mo; Ecuador buys from Peru, netback 0.47, pays 0.54, cap 43.2 k units/mo;
2025 COPPER_ROD makers 196: mill price over the literal floor, lowest 0.54, median 0.61, highest 1.03 | the site's floor 0.56, ceiling 0.58
     cheapest landed sellers: Canada mill 0.56 landed 0.61 cap 415 k units/mo; Peru mill 0.58 landed 0.65 cap 253 k units/mo; United States mill 0.63 landed 0.65 cap 1.59e+03 k units/mo;
     best sinks: United States buys from Canada, netback 0.56, pays 0.59, cap 1.56e+03 k units/mo; Guatemala buys from Guatemala, netback 0.54, pays 0.60, cap 46.9 k units/mo; Guyana buys from Guyana, netback 0.54, pays 0.60, cap 12.4 k units/mo;
2060 COPPER     makers 196: mill price over the literal floor, lowest 0.38, median 1.76, highest 1.86 | the site's floor 0.43, ceiling 0.46
     cheapest landed sellers: DR Congo mill 0.38 landed 0.48 cap 2.84e+04 k units/mo; Bahrain mill 0.49 landed 0.61 cap 1.01 k units/mo; Egypt mill 0.53 landed 0.62 cap 15.1 k units/mo;
     best sinks: United States buys from DR Congo, netback 0.43, pays 0.45, cap 1.55e+03 k units/mo; Kyrgyzstan buys from Uzbekistan, netback 0.32, pays 0.71, cap 18.9 k units/mo; Kazakhstan buys from Kazakhstan, netback 0.31, pays 0.66, cap 205 k units/mo;
2060 COPPER_ROD makers 196: mill price over the literal floor, lowest 0.80, median 0.95, highest 1.54 | the site's floor 0.88, ceiling 0.89
     cheapest landed sellers: Canada mill 0.88 landed 0.93 cap 528 k units/mo; Egypt mill 0.88 landed 0.97 cap 631 k units/mo; Cuba mill 0.94 landed 0.99 cap 32.9 k units/mo;
     best sinks: United States buys from Canada, netback 0.88, pays 0.91, cap 1.85e+03 k units/mo; Tonga buys from Tonga, netback 0.86, pays 0.95, cap 0.290 k units/mo; New Zealand buys from Indonesia, netback 0.86, pays 0.95, cap 73.2 k units/mo;
2100 COPPER     makers 196: mill price over the literal floor, lowest 0.25, median 1.70, highest 1.88 | the site's floor 0.30, ceiling 0.33
     cheapest landed sellers: DR Congo mill 0.25 landed 0.35 cap 8.17e+04 k units/mo; Egypt mill 0.37 landed 0.46 cap 26.4 k units/mo; Bahrain mill 0.34 landed 0.46 cap 1.81 k units/mo;
     best sinks: United States buys from DR Congo, netback 0.30, pays 0.32, cap 3.98e+03 k units/mo; Mongolia buys from Mongolia, netback 0.18, pays 0.51, cap 139 k units/mo; Kazakhstan buys from Kazakhstan, netback 0.17, pays 0.51, cap 447 k units/mo;
2100 COPPER_ROD makers 196: mill price over the literal floor, lowest 0.67, median 0.82, highest 1.49 | the site's floor 0.75, ceiling 0.76
     cheapest landed sellers: Canada mill 0.75 landed 0.80 cap 1.30e+03 k units/mo; Egypt mill 0.75 landed 0.84 cap 1.38e+03 k units/mo; United States mill 0.83 landed 0.86 cap 4.41e+03 k units/mo;
     best sinks: United States buys from Canada, netback 0.75, pays 0.77, cap 4.36e+03 k units/mo; New Zealand buys from Indonesia, netback 0.73, pays 0.83, cap 107 k units/mo; Tonga buys from Tonga, netback 0.73, pays 0.81, cap 0.618 k units/mo;
2200 COPPER     makers 196: mill price over the literal floor, lowest 0.17, median 1.42, highest 1.64 | the site's floor 0.22, ceiling 0.25
     cheapest landed sellers: DR Congo mill 0.17 landed 0.27 cap 4.97e+05 k units/mo; Egypt mill 0.25 landed 0.35 cap 111 k units/mo; Bahrain mill 0.24 landed 0.36 cap 6.46 k units/mo;
     best sinks: United States buys from DR Congo, netback 0.22, pays 0.24, cap 7.72e+03 k units/mo; Bahrain buys from Bahrain, netback 0.11, pays 0.24, cap 228 k units/mo; Mongolia buys from Mongolia, netback 0.11, pays 0.44, cap 851 k units/mo;
2200 COPPER_ROD makers 196: mill price over the literal floor, lowest 0.52, median 0.67, highest 1.29 | the site's floor 0.58, ceiling 0.60
     cheapest landed sellers: Canada mill 0.58 landed 0.63 cap 4.39e+03 k units/mo; Egypt mill 0.60 landed 0.69 cap 6.57e+03 k units/mo; United States mill 0.67 landed 0.70 cap 7.85e+03 k units/mo;
     best sinks: United States buys from Canada, netback 0.58, pays 0.61, cap 7.83e+03 k units/mo; Japan buys from Egypt, netback 0.56, pays 0.64, cap 1.88e+03 k units/mo; Timor-Leste buys from Indonesia, netback 0.55, pays 0.66, cap 77.4 k units/mo;
```

## 4. The prototype: today's main with the curves re-anchored (anchor.py), 2,400 months

The works at m2400, the sectors that carry the year (revenue less inputs over the year's GDP, and workers), and the last month's trade:

### seed 1, as main

```
month      mine   bigmine     leach     smelt    erefin  scrapref       rod      wire   pop    copper-level  p
 1200         0         0         0         0         0         0         0        14     48320  1.07469    84
 1800         0         0         0         0         0         0        13        42    169374  1.28309    37
 2400         0         0         0         0         0         0       363        81    427917  1.26297    28
 1200 pop    48320 staffed    26311 GDP/yr  6.455e+06 index    84.793 rate    67.199 | Construction 29% (3k w); Manufacturing 24% (9k w); Automotive 20% (5k w); Real Estate 6% (0k w); Luxury Retail 5% (0k w); Freight 4% (0k w); Restaurants 4% (0k w)
 1800 pop   169374 staffed    98707 GDP/yr  1.273e+08 index    379.95 rate    307.55 | Manufacturing 30% (44k w); Automotive 20% (27k w); Construction 14% (6k w); Copper Refining 14% (1k w); Luxury Retail 7% (0k w); Real Estate 5% (2k w); Freight 3% (2k w)
 2400 pop   427917 staffed   254914 GDP/yr  4.857e+09 index    2820.4 rate    2403.7 | Copper Refining 63% (27k w); Luxury Retail 10% (4k w); Manufacturing 9% (104k w); Automotive 5% (54k w); Construction 4% (13k w); Real Estate 3% (5k w); Freight 3% (9k w)
 2400 cathode floor/literal 0.3263 | conc 1.46/1.53 set 1.46/1.53 | anode 0.86/0.93 set 0.86/0.93 | copper 1.00/1.06 set 1.01/1.08 | scrap 2.58/2.56 set 2.58/2.56 | rod 2.58/2.50 set 2.00/2.46 | acid 2.42/2.39 set 2.42/2.39 | wire_c 2.13/2.17 set 2.13/2.17 |
last month: exports 595,603,344  imports 334,178,105  interest -843,086  => current account 262,268,325
the run:   Nothing. Every month passed the audit and every reload matched.
```

### seed 1, copper: copper's four on the cathode

```
month      mine   bigmine     leach     smelt    erefin  scrapref       rod      wire   pop    copper-level  p
 1200         0         0         0         0         0         0         1        14     66521  1.07469    44
 1800         0         0         0         0         0         0         1        46    174267  1.28309    14
 2400         0         0         0         0         0         0         4       276    409099  1.26297    76
 1200 pop    66521 staffed    37526 GDP/yr  5.633e+06 index    44.644 rate     38.29 | Manufacturing 32% (10k w); Construction 20% (3k w); Automotive 14% (6k w); Freight 6% (1k w); Business Services 6% (6k w); Real Estate 6% (1k w); Luxury Retail 5% (0k w)
 1800 pop   174267 staffed    98389 GDP/yr  5.191e+07 index    140.55 rate    119.06 | Manufacturing 39% (42k w); Automotive 18% (23k w); Construction 17% (7k w); Luxury Retail 10% (0k w); Real Estate 5% (2k w); Freight 4% (3k w); Restaurants 3% (1k w)
 2400 pop   409099 staffed   248081 GDP/yr  7.437e+08 index    769.88 rate     692.6 | Manufacturing 54% (135k w); Luxury Retail 13% (2k w); Automotive 11% (43k w); Construction 6% (14k w); Freight 6% (8k w); Real Estate 6% (5k w); Restaurants 3% (3k w)
 2400 cathode floor/literal 0.3263 | conc 1.01/1.05 set 1.01/1.05 | anode 0.99/1.07 set 0.99/1.07 | copper 1.00/1.06 set 1.00/1.06 | scrap 1.03/1.03 set 1.03/1.03 | rod 1.05/1.01 set 1.05/1.11 | acid 2.42/2.39 set 2.42/2.39 | wire_c 2.13/2.17 set 2.12/2.16 |
last month: exports 81,967,990  imports 45,895,262  interest -113,793  => current account 36,186,521
the run:   Nothing. Every month passed the audit and every reload matched.
```

### seed 1, copperwire: and wire and cable as rod plus fabrication

```
month      mine   bigmine     leach     smelt    erefin  scrapref       rod      wire   pop    copper-level  p
 1200         0         0         0         0         0         0         0         1     43497  1.07469    68
 1800         0         0         0         0         0         0         0         1    194524  1.28309    17
 2400         0         0         0         0         0         0         0         1    444545  1.26297    91
 1200 pop    43497 staffed    26556 GDP/yr  5.373e+06 index    68.509 rate    58.127 | Manufacturing 27% (8k w); Construction 27% (3k w); Automotive 26% (6k w); Real Estate 4% (0k w); Materials 4% (1k w); Luxury Retail 3% (0k w); Restaurants 2% (0k w)
 1800 pop   194524 staffed   112979 GDP/yr  6.839e+07 index    172.42 rate    144.19 | Manufacturing 35% (36k w); Automotive 23% (33k w); Construction 13% (7k w); Luxury Retail 10% (0k w); Real Estate 5% (2k w); Restaurants 3% (2k w); Heavy Industry 3% (4k w)
 2400 pop   444545 staffed   267877 GDP/yr  6.947e+08 index    915.66 rate    837.63 | Manufacturing 37% (110k w); Automotive 28% (95k w); Luxury Retail 13% (2k w); Real Estate 8% (6k w); Construction 5% (5k w); Restaurants 3% (4k w); Retail 2% (7k w)
 2400 cathode floor/literal 0.3263 | conc 1.01/1.05 set 1.01/1.05 | anode 0.99/1.07 set 0.99/1.07 | copper 1.00/1.06 set 1.00/1.06 | scrap 1.03/1.03 set 1.03/1.03 | rod 1.05/1.01 set 1.05/1.02 | acid 2.42/2.39 set 2.42/2.39 | wire_c 1.35/1.38 set 1.35/1.38 |
last month: exports 48,858,876  imports 18,732,938  interest -123,820  => current account 30,249,758
the run:         worst, month 1213: district (-1,0), m1212 to m1213: 22 of its 353 boxes moved: 4 x Studio Apartments (drawn 45->
```

### seed 1, all: every series' followers on its own good

```
month      mine   bigmine     leach     smelt    erefin  scrapref       rod      wire   pop    copper-level  p
 1200         0         0         0         0         0         0         1        24     98622  1.07469    64
 1800         0         0         0         0         0         0         0        37    265581  1.28309    29
 2400         0         0         0         0         0         0         0       293    564774  1.26297    11
 1200 pop    98622 staffed    54896 GDP/yr  1.185e+07 index    64.577 rate    56.392 | Manufacturing 40% (22k w); Automotive 15% (10k w); Construction 15% (4k w); Luxury Retail 8% (0k w); Real Estate 5% (1k w); Materials 4% (4k w); Restaurants 3% (1k w)
 1800 pop   265581 staffed   156277 GDP/yr  1.776e+08 index    290.21 rate    257.88 | Manufacturing 37% (60k w); Automotive 24% (49k w); Construction 13% (9k w); Luxury Retail 12% (1k w); Real Estate 5% (3k w); Restaurants 3% (2k w); Freight 3% (5k w)
 2400 pop   564774 staffed   348860 GDP/yr  1.203e+09 index    1164.4 rate      1061 | Manufacturing 48% (170k w); Automotive 16% (88k w); Luxury Retail 13% (3k w); Real Estate 7% (7k w); Construction 6% (16k w); Freight 4% (8k w); Restaurants 3% (5k w)
 2400 cathode floor/literal 0.3263 | conc 1.01/1.05 set 1.01/1.05 | anode 0.99/1.07 set 0.99/1.07 | copper 1.00/1.06 set 1.00/1.06 | scrap 1.03/1.03 set 1.03/1.03 | rod 1.05/1.01 set 1.05/1.11 | acid 2.42/2.39 set 2.42/2.39 | wire_c 2.13/2.17 set 2.12/2.16 |
last month: exports 124,421,731  imports 68,477,174  interest -291,486  => current account 56,236,043
the run:   Nothing. Every month passed the audit and every reload matched.
```

### seed 1, allwire: and wire and cable

```
month      mine   bigmine     leach     smelt    erefin  scrapref       rod      wire   pop    copper-level  p
 1200         0         0         0         0         0         0         0         0    120735  1.07469    57
 1800         0         0         0         0         0         0         0         0    272632  1.28309    19
 2400         0         0         0         0         0         0         0         0    619483  1.26297    10
 1200 pop   120735 staffed    67523 GDP/yr   1.26e+07 index    57.325 rate    48.942 | Manufacturing 34% (16k w); Automotive 22% (16k w); Construction 13% (3k w); Luxury Retail 8% (0k w); Business Services 8% (14k w); Real Estate 5% (1k w); Restaurants 4% (1k w)
 1800 pop   272632 staffed   161194 GDP/yr  1.112e+08 index    198.36 rate    163.69 | Manufacturing 33% (53k w); Automotive 25% (56k w); Construction 16% (8k w); Luxury Retail 11% (1k w); Real Estate 5% (3k w); Restaurants 3% (2k w); Freight 2% (3k w)
 2400 pop   619483 staffed   382926 GDP/yr  1.371e+09 index    1075.1 rate    961.78 | Manufacturing 35% (151k w); Automotive 29% (139k w); Luxury Retail 15% (3k w); Construction 8% (11k w); Real Estate 6% (8k w); Restaurants 3% (5k w); Freight 2% (7k w)
 2400 cathode floor/literal 0.3263 | conc 1.01/1.05 set 1.01/1.05 | anode 0.99/1.07 set 0.99/1.07 | copper 1.00/1.06 set 1.00/1.06 | scrap 1.03/1.03 set 1.03/1.03 | rod 1.05/1.01 set 1.05/1.01 | acid 2.42/2.39 set 2.42/2.39 | wire_c 1.35/1.38 set 1.35/1.38 |
last month: exports 92,216,437  imports 43,343,221  interest -283,561  => current account 49,156,778
the run:   Nothing. Every month passed the audit and every reload matched.
```

### seed 1, path: every good's reference band on its price path

```
month      mine   bigmine     leach     smelt    erefin  scrapref       rod      wire   pop    copper-level  p
 1200         0         0         0         0         0         0         0         0     94696  1.07469    68
 1800         0         0         0         0         0         0         0         6    218662  1.28309    54
 2400         0         0         0         0         0         0         0        18    368011  1.26297    47
 1200 pop    94696 staffed    51549 GDP/yr   1.06e+07 index    68.673 rate    55.349 | Heavy Industry 22% (5k w); Automotive 20% (12k w); Construction 16% (4k w); Manufacturing 11% (4k w); Wood Products 7% (2k w); Real Estate 7% (1k w); Business Services 6% (9k w)
 1800 pop   218662 staffed   124401 GDP/yr  2.044e+08 index    540.84 rate    459.82 | Heavy Industry 36% (37k w); Automotive 16% (36k w); Construction 16% (8k w); Manufacturing 11% (16k w); Luxury Retail 7% (0k w); Real Estate 6% (2k w); Restaurants 3% (2k w)
 2400 pop   368011 staffed   216553 GDP/yr  2.723e+09 index    4787.4 rate    4285.7 | Manufacturing 35% (67k w); Automotive 15% (61k w); Luxury Retail 13% (1k w); Heavy Industry 12% (10k w); Business Services 7% (35k w); Real Estate 7% (5k w); Restaurants 4% (3k w)
 2400 cathode floor/literal 1.503 | conc 1.01/1.06 set 1.01/1.06 | anode 0.99/1.07 set 0.99/1.07 | copper 1.00/1.06 set 1.00/1.06 | scrap 1.03/1.02 set 1.03/1.02 | rod 1.06/1.03 set 1.06/1.03 | acid 0.68/0.67 set 0.68/0.67 | wire_c 0.61/0.62 set 0.61/0.62 |
last month: exports 199,966,154  imports 87,211,271  interest -373,051  => current account 113,127,933
the run:         worst, month 1209: district (-1,-1), m1208 to m1209: 29 of its 246 boxes moved: 6 x Studio Apartments (drawn 20-
```

### seed 2, as main

```
month      mine   bigmine     leach     smelt    erefin  scrapref       rod      wire   pop    copper-level  p
 1200         0         0         0         0         0         0         0        24     86228  1.07469    11
 1800         0         0         0         0         0         0         8        37    256771  1.28309    92
 2400         0         0         0         0         0         0       397        62    612856  1.26297    63
 1200 pop    86228 staffed    49069 GDP/yr  1.613e+07 index    111.42 rate    94.129 | Manufacturing 44% (19k w); Construction 15% (4k w); Automotive 13% (6k w); Luxury Retail 6% (0k w); Real Estate 4% (1k w); Restaurants 4% (1k w); Heavy Industry 3% (1k w)
 1800 pop   256771 staffed   151646 GDP/yr  3.052e+08 index    926.33 rate    878.09 | Manufacturing 41% (75k w); Construction 14% (10k w); Automotive 13% (24k w); Luxury Retail 9% (1k w); Real Estate 7% (3k w); Copper Refining 4% (0k w); Restaurants 3% (2k w)
 2400 pop   612856 staffed   381392 GDP/yr  1.325e+10 index    6367.7 rate    5692.5 | Copper Refining 55% (30k w); Luxury Retail 13% (5k w); Manufacturing 13% (149k w); Automotive 6% (68k w); Freight 3% (17k w); Construction 3% (23k w); Real Estate 3% (8k w)
 2400 cathode floor/literal 0.3263 | conc 1.46/1.53 set 1.46/1.53 | anode 0.86/0.93 set 0.86/0.93 | copper 1.00/1.06 set 1.02/1.08 | scrap 2.58/2.56 set 2.58/2.56 | rod 2.58/2.50 set 1.97/2.46 | acid 2.42/2.39 set 2.42/2.39 | wire_c 2.13/2.17 set 2.13/2.17 |
last month: exports 1,647,818,600  imports 944,722,400  interest -1,404,146  => current account 704,500,346
the run:         worst, month 2397: district (-2,-3), m2396 to m2397: 7 of its 185 boxes moved: 6 x Low-Rise Apartments (drawn 99
```

### seed 2, copper: copper's four on the cathode

```
month      mine   bigmine     leach     smelt    erefin  scrapref       rod      wire   pop    copper-level  p
 1200         0         0         0         0         0         0         0        21    108690  1.07469    31
 1800         0         0         0         0         0         0         0        59    257625  1.28309    11
 2400         0         0         0         0         0         0         0       314    562580  1.26297    40
 1200 pop   108690 staffed    61357 GDP/yr  5.185e+06 index    31.961 rate    29.304 | Manufacturing 37% (23k w); Construction 23% (5k w); Automotive 16% (12k w); Real Estate 6% (1k w); Luxury Retail 6% (0k w); Materials 4% (5k w); Restaurants 3% (1k w)
 1800 pop   257625 staffed   151419 GDP/yr  6.111e+07 index    114.06 rate    109.84 | Manufacturing 45% (72k w); Automotive 17% (32k w); Construction 11% (8k w); Luxury Retail 11% (1k w); Real Estate 6% (3k w); Freight 3% (3k w); Restaurants 3% (2k w)
 2400 pop   562580 staffed   346986 GDP/yr  5.484e+08 index    403.99 rate    415.23 | Manufacturing 51% (177k w); Automotive 15% (82k w); Luxury Retail 13% (2k w); Construction 5% (17k w); Real Estate 5% (7k w); Freight 5% (9k w); Restaurants 3% (5k w)
 2400 cathode floor/literal 0.3263 | conc 1.01/1.05 set 1.01/1.05 | anode 0.99/1.07 set 0.99/1.07 | copper 1.00/1.06 set 1.00/1.06 | scrap 1.03/1.03 set 1.03/1.03 | rod 1.05/1.01 set 1.05/1.12 | acid 2.42/2.39 set 2.42/2.39 | wire_c 2.13/2.17 set 2.12/2.16 |
last month: exports 62,025,993  imports 32,328,592  interest 119,903  => current account 29,577,498
the run:   Nothing. Every month passed the audit and every reload matched.
```

### seed 2, copperwire: and wire and cable as rod plus fabrication

```
month      mine   bigmine     leach     smelt    erefin  scrapref       rod      wire   pop    copper-level  p
 1200         0         0         0         0         0         0         0         1    112647  1.07469    61
 1800         0         0         0         0         0         0         0         1    306043  1.28309    32
 2400         0         0         0         0         0         0         0         1    582397  1.26297    59
 1200 pop   112647 staffed    62872 GDP/yr   1.18e+07 index    61.513 rate    56.962 | Manufacturing 31% (14k w); Automotive 20% (14k w); Construction 15% (4k w); Luxury Retail 7% (0k w); Business Services 7% (12k w); Real Estate 6% (1k w); Restaurants 4% (1k w)
 1800 pop   306043 staffed   184268 GDP/yr  1.852e+08 index    329.19 rate    317.09 | Manufacturing 40% (67k w); Automotive 21% (46k w); Construction 11% (9k w); Luxury Retail 8% (1k w); Real Estate 6% (4k w); Business Services 4% (21k w); Restaurants 3% (3k w)
 2400 pop   582397 staffed   356373 GDP/yr  5.796e+08 index    596.35 rate    586.11 | Manufacturing 44% (173k w); Automotive 23% (99k w); Luxury Retail 12% (2k w); Real Estate 7% (7k w); Construction 5% (8k w); Restaurants 3% (5k w); Retail 2% (10k w)
 2400 cathode floor/literal 0.3263 | conc 1.01/1.05 set 1.01/1.05 | anode 0.99/1.07 set 0.99/1.07 | copper 1.00/1.06 set 1.00/1.06 | scrap 1.03/1.03 set 1.03/1.03 | rod 1.05/1.01 set 1.05/1.02 | acid 2.42/2.39 set 2.42/2.39 | wire_c 1.35/1.38 set 1.35/1.38 |
last month: exports 47,657,532  imports 17,963,782  interest 163,730  => current account 29,530,019
the run:   Nothing. Every month passed the audit and every reload matched.
```

### seed 2, all: every series' followers on its own good

```
month      mine   bigmine     leach     smelt    erefin  scrapref       rod      wire   pop    copper-level  p
 1200         0         0         0         0         0         0         0        22    137016  1.07469    32
 1800         0         0         0         0         0         0         4        61    296656  1.28309    83
 2400         0         0         0         0         0         0         0       305    595738  1.26297    26
 1200 pop   137016 staffed    77472 GDP/yr  7.834e+06 index    32.175 rate    30.936 | Manufacturing 32% (20k w); Automotive 18% (14k w); Construction 18% (5k w); Business Services 9% (18k w); Real Estate 6% (1k w); Luxury Retail 5% (0k w); Freight 3% (1k w)
 1800 pop   296656 staffed   175830 GDP/yr  5.793e+07 index    83.758 rate    84.292 | Manufacturing 43% (75k w); Automotive 23% (51k w); Construction 10% (9k w); Luxury Retail 8% (1k w); Real Estate 6% (4k w); Freight 4% (4k w); Restaurants 3% (3k w)
 2400 pop   595738 staffed   369781 GDP/yr  3.089e+08 index    264.68 rate    276.92 | Manufacturing 46% (174k w); Automotive 16% (96k w); Luxury Retail 16% (4k w); Real Estate 7% (7k w); Construction 6% (18k w); Freight 4% (7k w); Restaurants 3% (5k w)
 2400 cathode floor/literal 0.3263 | conc 1.01/1.05 set 1.01/1.05 | anode 0.99/1.07 set 0.99/1.07 | copper 1.00/1.06 set 1.00/1.06 | scrap 1.03/1.03 set 1.03/1.03 | rod 1.05/1.01 set 1.05/1.11 | acid 2.42/2.39 set 2.42/2.39 | wire_c 2.13/2.17 set 2.12/2.16 |
last month: exports 30,872,795  imports 16,755,920  interest -104,728  => current account 14,221,603
the run:   Nothing. Every month passed the audit and every reload matched.
```

### seed 2, allwire: and wire and cable

```
month      mine   bigmine     leach     smelt    erefin  scrapref       rod      wire   pop    copper-level  p
 1200         0         0         0         0         0         0         0         0    102625  1.07469    40
 1800         0         0         0         0         0         0         0         0    304775  1.28309    16
 2400         0         0         0         0         0         0         0         0    556788  1.26297    38
 1200 pop   102625 staffed    57608 GDP/yr  8.317e+06 index    40.092 rate    37.188 | Manufacturing 40% (16k w); Automotive 16% (10k w); Construction 11% (3k w); Business Services 7% (11k w); Luxury Retail 6% (0k w); Real Estate 5% (1k w); Materials 4% (4k w)
 1800 pop   304775 staffed   184110 GDP/yr  1.037e+08 index    166.88 rate    158.78 | Manufacturing 37% (62k w); Automotive 21% (46k w); Luxury Retail 11% (1k w); Construction 11% (9k w); Real Estate 5% (4k w); Business Services 4% (24k w); Restaurants 3% (3k w)
 2400 pop   556788 staffed   338486 GDP/yr   4.14e+08 index     380.6 rate    402.96 | Manufacturing 36% (137k w); Automotive 26% (112k w); Luxury Retail 16% (3k w); Real Estate 7% (7k w); Construction 5% (7k w); Restaurants 3% (5k w); Cement & Concrete 2% (6k w)
 2400 cathode floor/literal 0.3263 | conc 1.01/1.05 set 1.01/1.05 | anode 0.99/1.07 set 0.99/1.07 | copper 1.00/1.06 set 1.00/1.06 | scrap 1.03/1.03 set 1.03/1.03 | rod 1.05/1.01 set 1.05/1.01 | acid 2.42/2.39 set 2.42/2.39 | wire_c 1.35/1.38 set 1.35/1.38 |
last month: exports 32,688,023  imports 16,582,995  interest -106,878  => current account 16,211,906
the run:   Nothing. Every month passed the audit and every reload matched.
```

### seed 2, path: every good's reference band on its price path

```
month      mine   bigmine     leach     smelt    erefin  scrapref       rod      wire   pop    copper-level  p
 1200         0         0         0         0         0         1         2         0     27220  1.07469    44
 1800         0         0         0         0         0         1         1         1    132085  1.28309    29
 2400         0         0         0         0         0         1         1        12    358903  1.26297    12
 1200 pop    27220 staffed    16471 GDP/yr  2.183e+06 index    44.492 rate    37.647 | Construction 28% (3k w); Automotive 27% (4k w); Manufacturing 10% (1k w); Heavy Industry 6% (0k w); Business Services 5% (2k w); Real Estate 4% (0k w); Luxury Retail 3% (0k w)
 1800 pop   132085 staffed    76374 GDP/yr  7.627e+07 index    293.13 rate    259.96 | Heavy Industry 30% (15k w); Construction 17% (6k w); Automotive 14% (18k w); Manufacturing 11% (7k w); Luxury Retail 7% (0k w); Business Services 6% (12k w); Real Estate 5% (1k w)
 2400 pop   358903 staffed   220573 GDP/yr  7.573e+08 index    1236.6 rate    1150.2 | Manufacturing 33% (60k w); Automotive 15% (53k w); Heavy Industry 12% (28k w); Luxury Retail 11% (1k w); Construction 10% (16k w); Real Estate 7% (4k w); Restaurants 3% (3k w)
 2400 cathode floor/literal 1.503 | conc 1.01/1.06 set 1.01/1.06 | anode 0.99/1.07 set 0.99/1.07 | copper 1.00/1.06 set 1.00/1.06 | scrap 1.03/1.02 set 1.03/1.03 | rod 1.06/1.03 set 1.06/1.03 | acid 0.68/0.67 set 0.68/0.67 | wire_c 0.61/0.62 set 0.61/0.62 |
last month: exports 67,357,062  imports 36,451,203  interest -68,775  => current account 30,974,634
the run:   Nothing. Every month passed the audit and every reload matched.
```

### s0allwire

```
month      mine   bigmine     leach     smelt    erefin  scrapref       rod      wire   pop    copper-level  p
 1200         0         0         0         0         0         0         0         1    114144  1.07469    51
 1800         0         0         0         0         0         0         0         1    260945  1.28309    15
 2400         0         0         0         0         0         0         0         1    467904  1.26297    99
 1200 pop   114144 staffed    62784 GDP/yr  1.257e+07 index    51.034 rate    45.284 | Manufacturing 44% (23k w); Automotive 20% (16k w); Construction 15% (4k w); Luxury Retail 5% (0k w); Real Estate 5% (1k w); Materials 4% (5k w); Restaurants 3% (1k w)
 1800 pop   260945 staffed   155114 GDP/yr  8.545e+07 index     155.8 rate    122.16 | Manufacturing 42% (71k w); Automotive 25% (49k w); Construction 12% (6k w); Luxury Retail 10% (1k w); Real Estate 5% (3k w); Restaurants 3% (2k w); Retail 2% (4k w)
 2400 pop   467904 staffed   285549 GDP/yr  1.046e+09 index    990.68 rate    869.36 | Manufacturing 36% (124k w); Automotive 25% (95k w); Luxury Retail 17% (2k w); Construction 8% (10k w); Real Estate 6% (5k w); Restaurants 3% (4k w); Freight 2% (4k w)
last month: exports 70,683,341  imports 29,530,574  interest -217,333  => current account 41,370,100
the run:   Nothing. Every month passed the audit and every reload matched.
```

### s3allwire

```
month      mine   bigmine     leach     smelt    erefin  scrapref       rod      wire   pop    copper-level  p
 1200         0         0         0         0         0         0         0         1    123683  1.07469    17
 1800         0         0         0         0         0         0         0         1    317450  1.28309    11
 2400         0         0         0         0         0         0         0         1    688798  1.26297    46
 1200 pop   123683 staffed    68477 GDP/yr  5.345e+06 index    17.925 rate    14.565 | Manufacturing 38% (24k w); Construction 21% (4k w); Automotive 21% (22k w); Luxury Retail 7% (0k w); Real Estate 5% (1k w); Restaurants 3% (1k w); Freight 2% (1k w)
 1800 pop   317450 staffed   190174 GDP/yr  7.867e+07 index    118.27 rate    97.649 | Manufacturing 38% (77k w); Automotive 26% (68k w); Construction 15% (9k w); Luxury Retail 9% (1k w); Real Estate 6% (4k w); Restaurants 3% (3k w); Retail 2% (5k w)
 2400 pop   688798 staffed   426232 GDP/yr  5.341e+08 index    467.78 rate    422.19 | Manufacturing 35% (169k w); Automotive 31% (171k w); Luxury Retail 15% (4k w); Real Estate 8% (9k w); Construction 4% (6k w); Restaurants 3% (6k w); Retail 2% (14k w)
last month: exports 36,721,178  imports 15,755,680  interest -116,354  => current account 21,081,852
the run:   Nothing. Every month passed the audit and every reload matched.
```

# Currency.java - 436 lines · 36 methods · 11 constants · model

`ham/citybuildersim/Currency.java` - generated 2026-11-10 by CodeMap; line numbers are as of that run.

> What the city's money is called, and how it is written.
> 
> ONE PLACE THAT DECIDES, which is the whole reason this exists rather than a
> string literal per screen. Twenty screens each writing "$" is twenty screens
> that will disagree the first time one of them has to say WHICH dollar.
> 
> THE CONVENTION IS JERUS'S, from when the exchange rate went in and every
> screen started having to say which currency it meant:
> 
>     USD 10,000  ->  D$14,560        when both are on the screen
>                 ->  $14,560         when it is the only money in sight
> 
> Which is how anybody writes about money: nobody says "CAD" in Toronto, and
> everybody does the moment a US price is beside it. The player spends their
> whole game in one currency and should not be made to read a currency code to
> buy a house; they should be made to read one the instant a foreign price is
> on the same line, because that is the instant it matters.
> 
> ==================== EACH CITY NAMES ITS OWN (0.7.10) ====================
> 
> Until 0.7.10 this class was five static finals: every city's money was the
> Danzik dollar. Jerus, on the new-game screen: "a small thing at the start
> of the game where you choose the name of the city and the currency", named
> from the city with "a tick to manually input the currency name and a 3
> letter code if wanted". So a Currency is a VALUE now, one per city, held on
> the city's founding record (Founding) and read through the game -
> Game.getCurrency() - by every screen, the year book and the playtest.
> 
> READ THROUGH THE GAME, NOT STATIC STATE SET ON FOUNDING, because one process
> holds several cities at once: NewGameCheck keeps a pristine city beside a
> played one, the playtest reloads into a second Game to compare, and the
> screens outlive every city they draw. A static would name whichever city
> was founded or loaded LAST, so the first city's year book could print the
> second city's money. Carried by the city, a name cannot leak from one city
> into the next by construction - buildWorld()'s argument again.
> 
> WHAT IS DERIVED FROM A CITY'S NAME (fromCityName()):
> 
>   - the name is "<City> dollar" and the plural "<City> dollars";
>   - the code is the first three letters A-Z of the name, upper-cased
>     (deriveCode() says what happens to accents, other scripts, short names
>     and the foreign code);
>   - the symbol stays "$" (see SYMBOL);
>   - the qualified symbol is the name's first letter, upper-cased, and "$":
>     Arden gives the A$.
> 
> WHAT IS TYPED BY HAND (typed()): a name and a three-letter code. The code
> must be exactly three letters A-Z and never the foreign one (codeProblem()).
> The plural is the name with an "s" unless it already ends in one - English
> has irregular plurals this cannot know, and the player can type the name so
> the plural reads ("crown" gives "crowns"); the symbol stays "$" unless a
> sign is typed too (FM1, below); the qualified symbol is the typed name's
> first letter and "$".
> 
> THE FOREIGN MONEY IS NOT A CHOICE. It stays the US dollar, US$, USD, as a
> static: the world holds exactly one other money and no city renames it.
> 
> ==================== THE SIGN AND ITS PLACE (FM1) ====================
> 
> Jerus, 2026-11-02: his Rotterdam typed "fRotterdam Guilder" for the sign,
> ... (19 more lines in the source)

**Uses:** [ForeignAccounts](ForeignAccounts.md) (1)

**Used by (29):** [DataSave](DataSave.md), [Denomination](Denomination.md), [DenominationCheck](DenominationCheck.md), [FinancesScreen](FinancesScreen.md), [FittingCheck](FittingCheck.md), [Formats](Formats.md), [Founding](Founding.md), [FoundingScreen](FoundingScreen.md), [Game](Game.md), [HistoryCheck](HistoryCheck.md), [HistoryScreen](HistoryScreen.md), [LandClassCheck](LandClassCheck.md), [LandClassView](LandClassView.md), [LandMap](LandMap.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [Money](Money.md), [MoneySignCheck](MoneySignCheck.md), [NewGameCheck](NewGameCheck.md), [PolicyScreen](PolicyScreen.md), [RankingsScreen](RankingsScreen.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SavedCities](SavedCities.md), [SummaryScreen](SummaryScreen.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md), [YearBook](YearBook.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 90 | · the world's |
| 103 | · the rules |
| 129 | · the one a city had before 0.7.10 |
| 141 | · the value |
| 274 | · derived from the city |
| 338 | · typed by hand |
| 422 | · as a value |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 93 | `Currency.FOREIGN_NAME` | `"US dollar"` | The world's money, which the game holds exactly one of. |
| 94 | `Currency.FOREIGN_CODE` | `"USD"` |  |
| 95 | `Currency.FOREIGN_SYMBOL` | `"US$"` |  |
| 98 | `Currency.FOREIGN_CENT_SYMBOL` | `"US\u00a2"` | ...and its hundredth, for what one local dollar buys once it is worth less than one of them. |
| 112 | `Currency.SYMBOL` | `"$"` | Written alone, where nothing foreign is in sight: "$" for a city that has not typed a sign of its own (typed(name, code, sign, after)). |
| 115 | `Currency.DERIVED_NOUN` | `"dollar"` | What a derived currency is: "<City> dollar". |
| 118 | `Currency.CODE_LENGTH` | `3` | Exactly this many letters A-Z in a code, as ISO 4217 has. |
| 121 | `Currency.CODE_PAD` | `'X'` | What a derived code is padded with when the name has fewer than three letters A-Z: ISO 4217's own letter for money that belongs to no country (XAU, XDR). |
| 124 | `Currency.MAX_NAME_LENGTH` | `32` | The longest name a player may type for their money: a line on the trade tab's rate, not a policy. |
| 127 | `Currency.MAX_SIGN_LENGTH` | `3` | The longest sign a player may type for their money, in characters: "kr", "zł", "CHF", "T¥" fit, and a figure beside it still reads on a narrow column. |
| 138 | `Currency.DANZIK` | `new Currency("Danzik dollar", "Danzik dollars", "DZD", SYMBOL, "D$")` | THE DANZIK DOLLAR: every city's money until 0.7.10, and so the money of every save written before then, which has no currency of its own on file (Founding.legacy()). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 143 | `private final String name, plural, code, symbol, qualifiedSymbol` |  |
| 146 | `private final boolean signAfter` | Whether the sign is written after the figure ("2.6M kr") rather than before it ("$2.6M"). |
| 157 | `private static volatile Currency writing` | WHICH MONEY THE STATIC FORMATTERS WRITE. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 88 | 349 | **type** `public final class Currency` | What the city's money is called, and how it is written. |

### the world's (lines 90-102)

| line | len | member | says |
|---:|---:|---|---|
| 101 | 1 | `public static String foreign(String amount)` | Foreign money, always marked, because it is never the default here. |

### the rules (lines 103-128)

### the one a city had before 0.7.10 (lines 129-140)

### the value (lines 141-273)

| line | len | member | says |
|---:|---:|---|---|
| 160 | 1 | `public static Currency writing()` | The money the static formatters are writing now: "$" before until a city says otherwise. |
| 163 | 1 | `public static void use(Currency c)` | The city whose money the static formatters write from here (null: the Danzik dollar). |
| 166 | 3 | `public Currency(String name, String plural, String code, String symbol, String qualifiedSymbol)` | A currency exactly as given, its sign before the figure - the load path's door, which reads back what was saved without judging it. |
| 171 | 8 | `public Currency(String name, String plural, String code, String symbol, String qualifiedSymbol, boolean signAfter)` | A currency exactly as given, its sign before the figure or after it. |
| 181 | 1 | `public String name()` | "Arden dollar". |
| 183 | 1 | `public String plural()` | "Arden dollars". |
| 185 | 1 | `public String code()` | "ARD". |
| 187 | 1 | `public String symbol()` | "$" - written alone. |
| 189 | 1 | `public String qualifiedSymbol()` | "A$" - written where a foreign figure is on the same line. |
| 191 | 1 | `public boolean signAfter()` | Whether the sign follows the figure ("2.6M kr") rather than leads it ("$2.6M"). |
| 204 | 1 | `public String write(String number)` | An already formatted number with this money's sign put around it: before, sign + separator + number; after, number + separator + sign. |
| 207 | 1 | `public String writeQualified(String number)` | The same with the qualified sign, where a foreign figure is in sight. |
| 210 | 1 | `public String local(String amount)` | Local money, on a screen with no foreign figure on it. |
| 213 | 1 | `public String qualified(String amount)` | Local money, on a screen that also shows dollars. |
| 215 | 3 | `private String place(String sign, String number)` |  |
| 220 | 5 | `private String gap(String sign)` | The space between a sign and the figure beside it: one where the sign's letter touches the number. |
| 232 | 6 | `public String qualify(String written)` | A figure this money wrote with its sign, rewritten with its qualified sign in its place: "$5" is "D$5", "-$5" is "-D$5", "5¥" is "5 T¥", "5 kr" stays "5 kr". |
| 252 | 3 | `public static String marked(String mark, String written)` | A figure written in writing()'s money, rewritten in a foreign money's mark, which always goes BEFORE the figure with no space added (the marks carry their own: ForeignAccounts.MONEY_MARK has "CHF " and "XAU "). |
| 256 | 11 | `private String markedIn(String mark, String written)` |  |
| 269 | 1 | `public String rateUnit()` | "Arden dollars per USD", as the exchange rate's unit. |
| 272 | 1 | `public String describe()` | "the Arden dollar, A$, ARD" - the founding screen's line. |

### derived from the city (lines 274-337)

| line | len | member | says |
|---:|---:|---|---|
| 281 | 5 | `public static Currency fromCityName(String city)` | The city's money named after the city: "Arden" gives the Arden dollar, A$, ARD. |
| 303 | 10 | `public static String deriveCode(String city)` | The code a city's name gives its money: its first three letters A-Z, upper-cased. |
| 315 | 10 | `static String initial(String name)` | The first letter of a name, upper-cased - any script - or the pad when it has none. |
| 327 | 10 | `private static String asciiLetters(String s)` | The name's letters A-Z, accents folded and upper-cased, everything else dropped. |

### typed by hand (lines 338-421)

| line | len | member | says |
|---:|---:|---|---|
| 345 | 3 | `public static Currency typed(String name, String code)` | The money as the player named it, with the "$" before the figure, or null when either half does not pass (nameProblem(), codeProblem()). |
| 355 | 8 | `public static Currency typed(String name, String code, String sign, boolean after)` | The money as the player named it and signed it, or null when any of the three does not pass (nameProblem(), codeProblem(), signProblem()). |
| 371 | 10 | `public static String signProblem(String sign)` | Why a typed sign will not do, in the player's words, or null when it will: at least one and at most MAX_SIGN_LENGTH characters once trimmed, none of them a digit, a space or one of + - − . |
| 389 | 6 | `public static boolean needsLetter(String sign)` | Whether a sign is also one the vault's moneys are written with, so a figure in it is ambiguous the moment a foreign one is in sight: "$", or a sign that is, or ends, one of ForeignAccounts.MONEY_MARK trimmed ("¥" ends... |
| 397 | 3 | `public static String qualifiedFor(String name, String sign)` | The sign a figure is written with where a foreign figure is in sight: the money's first letter before it where needsLetter() says so (D$, T¥), else the sign alone. |
| 402 | 7 | `public static String nameProblem(String name)` | Why a typed name will not do, in the player's words, or null when it will. |
| 415 | 6 | `public static String codeProblem(String code)` | Why a typed code will not do, in the player's words, or null when it will: exactly CODE_LENGTH letters A-Z (upper-cased first), and never FOREIGN_CODE - the world's money is not the city's to issue. |

### as a value (lines 422-436)

| line | len | member | says |
|---:|---:|---|---|
| 425 | 5 | `public boolean equals(Object o)` |  |
| 432 | 1 | `public int hashCode()` |  |
| 435 | 1 | `public String toString()` |  |


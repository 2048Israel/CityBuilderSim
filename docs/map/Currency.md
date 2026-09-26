# Currency.java - 263 lines · 21 methods · 10 constants · model

`ham/citybuildersim/Currency.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

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
> the plural reads ("crown" gives "crowns"); the symbol stays "$"; the
> qualified symbol is the typed name's first letter and "$".
> 
> THE FOREIGN MONEY IS NOT A CHOICE. It stays the US dollar, US$, USD, as a
> static: the world holds exactly one other money and no city renames it.

**Used by (19):** [DataSave](DataSave.md), [Denomination](Denomination.md), [DenominationCheck](DenominationCheck.md), [FinancesScreen](FinancesScreen.md), [Founding](Founding.md), [FoundingScreen](FoundingScreen.md), [Game](Game.md), [HistoryScreen](HistoryScreen.md), [LongPlaytest](LongPlaytest.md), [Money](Money.md), [NewGameCheck](NewGameCheck.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SummaryScreen](SummaryScreen.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md), [YearBook](YearBook.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 66 | · the world's |
| 79 | · the rules |
| 101 | · the one a city had before 0.7.10 |
| 113 | · the value |
| 149 | · derived from the city |
| 213 | · typed by hand |
| 249 | · as a value |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 69 | `Currency.FOREIGN_NAME` | `"US dollar"` | The world's money, which the game holds exactly one of. |
| 70 | `Currency.FOREIGN_CODE` | `"USD"` |  |
| 71 | `Currency.FOREIGN_SYMBOL` | `"US$"` |  |
| 74 | `Currency.FOREIGN_CENT_SYMBOL` | `"US\u00a2"` | ...and its hundredth, for what one local dollar buys once it is worth less than one of them. |
| 87 | `Currency.SYMBOL` | `"$"` | Written alone, where nothing foreign is in sight: "$" for every city, derived or typed. |
| 90 | `Currency.DERIVED_NOUN` | `"dollar"` | What a derived currency is: "<City> dollar". |
| 93 | `Currency.CODE_LENGTH` | `3` | Exactly this many letters A-Z in a code, as ISO 4217 has. |
| 96 | `Currency.CODE_PAD` | `'X'` | What a derived code is padded with when the name has fewer than three letters A-Z: ISO 4217's own letter for money that belongs to no country (XAU, XDR). |
| 99 | `Currency.MAX_NAME_LENGTH` | `32` | The longest name a player may type for their money: a line on the trade tab's rate, not a policy. |
| 110 | `Currency.DANZIK` | `new Currency("Danzik dollar", "Danzik dollars", "DZD", SYMBOL, "D$")` | THE DANZIK DOLLAR: every city's money until 0.7.10, and so the money of every save written before then, which has no currency of its own on file (Founding.legacy()). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 115 | `private final String name, plural, code, symbol, qualifiedSymbol` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 64 | 200 | **type** `public final class Currency` | What the city's money is called, and how it is written. |

### the world's (lines 66-78)

| line | len | member | says |
|---:|---:|---|---|
| 77 | 1 | `public static String foreign(String amount)` | Foreign money, always marked, because it is never the default here. |

### the rules (lines 79-100)

### the one a city had before 0.7.10 (lines 101-112)

### the value (lines 113-148)

| line | len | member | says |
|---:|---:|---|---|
| 118 | 7 | `public Currency(String name, String plural, String code, String symbol, String qualifiedSymbol)` | A currency exactly as given - the load path's door, which reads back what was saved without judging it. |
| 127 | 1 | `public String name()` | "Arden dollar". |
| 129 | 1 | `public String plural()` | "Arden dollars". |
| 131 | 1 | `public String code()` | "ARD". |
| 133 | 1 | `public String symbol()` | "$" - written alone. |
| 135 | 1 | `public String qualifiedSymbol()` | "A$" - written where a foreign figure is on the same line. |
| 138 | 1 | `public String local(String amount)` | Local money, on a screen with no foreign figure on it. |
| 141 | 1 | `public String qualified(String amount)` | Local money, on a screen that also shows dollars. |
| 144 | 1 | `public String rateUnit()` | "Arden dollars per USD", as the exchange rate's unit. |
| 147 | 1 | `public String describe()` | "the Arden dollar, A$, ARD" - the founding screen's line. |

### derived from the city (lines 149-212)

| line | len | member | says |
|---:|---:|---|---|
| 156 | 5 | `public static Currency fromCityName(String city)` | The city's money named after the city: "Arden" gives the Arden dollar, A$, ARD. |
| 178 | 10 | `public static String deriveCode(String city)` | The code a city's name gives its money: its first three letters A-Z, upper-cased. |
| 190 | 10 | `static String initial(String name)` | The first letter of a name, upper-cased - any script - or the pad when it has none. |
| 202 | 10 | `private static String asciiLetters(String s)` | The name's letters A-Z, accents folded and upper-cased, everything else dropped. |

### typed by hand (lines 213-248)

| line | len | member | says |
|---:|---:|---|---|
| 220 | 7 | `public static Currency typed(String name, String code)` | The money as the player named it, or null when either half does not pass (nameProblem(), codeProblem()). |
| 229 | 7 | `public static String nameProblem(String name)` | Why a typed name will not do, in the player's words, or null when it will. |
| 242 | 6 | `public static String codeProblem(String code)` | Why a typed code will not do, in the player's words, or null when it will: exactly CODE_LENGTH letters A-Z (upper-cased first), and never FOREIGN_CODE - the world's money is not the city's to issue. |

### as a value (lines 249-263)

| line | len | member | says |
|---:|---:|---|---|
| 252 | 5 | `public boolean equals(Object o)` |  |
| 259 | 1 | `public int hashCode()` |  |
| 262 | 1 | `public String toString()` |  |


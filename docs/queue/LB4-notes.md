# LB4 - Rankings: the city in play beside its own slot, and a lighter read of the saved cities (0.9.64, 2026-11-09)

The brief is `docs/queue/LB4.md` (LB3-D and LB3-R, Jerus 2026-10-31: "Take all (Recommended)"). This session ran with
the orchestrator's app environment, W=150000 (compaction near 118k), so what must survive is written here when decided.

## Decided (2026-11-09)

1. **The city in play (LB3-D).** `SaveHeader` gains `Long worldSeed` (Gson fills it from DataSave's key of that name);
   `SavedCities.Played(name, seed)`, `Played.of(Game)` from `Game.getCityName()` and `Game.getWorldSeed()`, and
   `Played.NONE`, which matches nothing. A slot is the city in play when its header's name equals and its header's seed
   is present and equal. A save with no seed in its header never matches: `DataSave.getFounding` derives one for an old
   save at load, and the header cannot, so a match there would be a guess.
2. `Rankings.Saved` gains `boolean inPlay` (a two-argument constructor kept, false). `SavedCities.now(Played)` marks
   the slot; `at(month, Played)` and `stoppedAt(month, Played)` leave it out. The harness callers that predate LB4 pass
   `Played.NONE`, so their premise does not move.
3. The screen: the row says "this city, as saved · slot N", the drawer "This city, as saved · slot N · ...". At Your
   age the not-at-age line names it, "Name (slot N) is this city, as saved", rather than dropping it silently or
   saying it keeps no figures. `placeSaved`'s cache keys on the city in play as well (a load from slot 3 leaves the
   snapshot and maybe the month unchanged). `Rankings.sentence` names no saved city and is unchanged.
4. **The read (LB3-R).** `HistorySave.readKept(Reader, Set<String>)`: Gson with an exclusion strategy that skips every
   field of `HistorySave` but the kept series and what `slim` keeps (month, foldedMonths), so their JSON values are
   skipped by `JsonReader.skipValue` and never built; `List<Double>` and `List<Long>` read into read-only packed lists
   over `double[]` / `long[]` (a null entry kept by a mask, so `get` and `equals` match the whole parse). `readSlot`
   reads the bytes (`Files.readAllBytes`, the handle closed before parsing, LB3's gate) and parses those. `slim` stays.
5. One Sonnet slice does both (they share `SavedCities` and `RankingsCheck`); the memory is measured by UiProbe's
   `-Dprobe.saved=N` over copies of its grown city in its own scratch folder, never the real slots.

## Done

- One Sonnet slice (`target/deepseek/brief-lb4.md`) wrote it all; reviewed as a stranger's diff, committed in one.
  Two departures from its brief, both kept: the new harness sections are RankingsCheck 19 (the city in play) and 20
  (the packed read), since 18 was taken; and `ui/KeptBench`'s two `now()` calls pass `Played.NONE` to compile.
- The review found nothing that writes to a kept list after the read (`Rankings` sorts a copy of its rows;
  `CityMeasures` and `alignedTail` only read), so read-only packed lists are safe where the boxed ones were.
- The match is on the founding: `Game.getCityName()` reads the founding, so a city is never renamed and its slot
  cannot drift from it. A second city founded with the same name and the same world seed matches too; it is the same
  founding, and at Your age it would nearly tie with the city in play the same way.

## Measured (UiProbe, `-Dprobe.saved=N`, the grown city copied into its own scratch folder's slots)

| run | slots | history each | retained old -> new | peak old -> new | ms old -> new |
|---|---|---|---|---|---|
| `UiProbe 24 2`, saved=3 | 3 | 0.9 MB | 2.8 -> 0.9 MB | 162.3 -> 120.4 MB | 114 -> 60 |
| `UiProbe 600 1`, saved=3 | 3 | 1.6 MB | 4.9 -> 1.5 MB | 220.2 -> 148.9 MB | 163 -> 83 |
| `UiProbe 6000 1`, saved=10 | 10 | 13.0 MB | 96.8 -> 32.0 MB | 1295.3 -> 1219.3 MB | 1216 -> 929 |

Retained is what the board holds after the read: a third of before, about 2.8 MB a slot at 6,000 months, which is the
58 kept series as `double[]`. Peak sums the heap pools' peaks, the city in play included, so it overstates; the
read's own share fell by about 76 MB over ten slots. The long run timed beside `--pick`, so its ms are only a
comparison within the run. Every row of each run was marked "this city, as saved" (3 of 3, 3 of 3, 10 of 10).

## Checks

- The ensemble is skipped: no model file moved, so the trace is main's (batch.md 6).
- `--pick` without the world named reddened KeptCheck (17) and ResearchViewCheck (1), "world-1.bin not named"; the
  rerun names `-Dcbs.earth`/`-Dcbs.world` as the gate does and exits **3**: main's recorded reds only (PriceLevelCheck,
  ScaleCheck, KeptCheck), 2 timing SOFT (SaveSlotCheck, KeptCheck), Stale 0 firm, the maps regenerated unchanged.
- `--pick` asks for a saves review of `HistorySave.java`: nothing a save writes or the game's load reads moved. The
  change is a second read for the board alone (`readKept`, the packed lists), and slim's trim of `foldedMonths` moved
  into a helper both share; SaveFileCheck, ReadPathCheck, HistoryCheck and SaveSlotCheck ran green.

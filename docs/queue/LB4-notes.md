# LB4 - Rankings: the city in play beside its own slot, and a lighter read of the saved cities ({next}, 2026-11-09)

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

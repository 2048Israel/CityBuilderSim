# WD1 - the world file's makers from FAOSTAT, USGS and EIA

Queued 2026-10-31, settling WN0-M the recommended way (yes, the downloads
included), on Jerus's "Take all (Recommended)" (BP1 item 9).

## The finding (WN0 notes, ★2)

What each nation makes is the study's fallback estimate everywhere: use is
the good's world use by the nation's share of world output, and make = use -
imports + exports, so the sums close. FAOSTAT (food, wood), USGS (ores,
metals) and EIA (crude, products, coal) were not among WN0's downloads.

## What it does

1. **The downloads**, each into its own folder of
   `C:/Users/Jerus/Documents/world-sources/` as WN0's were, and each read
   into `docs/studies/licences.md` first, LC1's rule (FAOSTAT CC BY 4.0; USGS
   and EIA public domain); `tools/world/ATTRIBUTION.txt` credits them.
2. **Their figures in the pipeline** (`tools/world/`): a nation's output of a
   good from them where they have it, the fallback where not, the note saying
   which, by good.
3. **The re-bake**, after LC3's (one bake at a time; WN0-R's release follows
   LC3's): the fixture re-cut, the sha, the release asset as LC3 does it;
   WN3's world prices read again on it, and the ensemble if Historical's trace
   moves. WN3-D (pending) waits on a re-bake too.

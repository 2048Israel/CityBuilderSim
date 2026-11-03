# CT1 - colour themes (optional)

Optional: in `QUEUE.md`'s Optional section, outside the order, and started
only when Jerus says. Jerus, 2026-11-02: "since palette is a class... would
it be possible to in the settings adjust the colour pallete and have some
defualts, its a larger problem than a quick batch dont do it, if it easy is
doable then thats great, but not nescessary"; then "ya just have it at the
end of the queue as an optional batch".

## What stands in the way (found 2026-11-02)

1. `ui/Palette.java`'s colours are `public static final String` literals
   (`STAGE = "#0b1118"` and the rest). They are compile-time constants,
   which javac copies into every class that reads them (39 files), so
   changing one at run time reaches none of those classes. Load them from a
   theme at start instead (an initialiser that is not a constant), so a
   theme chosen in Settings applies on the next start. A live switch can
   come later if wanted; most screens rebuild on the clock already.
2. Not every colour is in the palette. About 60 hex literals in `ui/` sit
   outside it, plus `Color.web` and `Color.rgb` calls (about 220, some
   reading the palette), the map's terrain colours and the charts'. Move
   them in first, or a theme leaves patches of the old one. Palette's own
   header says the screens adopt it one at a time.
3. A light theme is the hard one: many colours were chosen against the dark
   ground. Dark variants are easy; light comes last.

## What it does

- Settings gets a theme picker with presets:
  - the current dark one (the default);
  - a colour-blind one, where GOOD, WARN and BAD (the only colours that
    carry news, by Palette's rule) become pairs a red-green colour-blind
    player can tell apart; red against green is the pair about one man in
    twelve cannot;
  - perhaps a high-contrast one;
  - light last, or in a later batch.
- A colour per role chosen by hand, if cheap once the presets work.
- A harness: no hex literal or `Color.web("#...")` left in `ui/` outside the
  palette, and every preset defines every role.
- Screens only; the trace does not move.

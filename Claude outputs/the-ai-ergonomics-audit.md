# The structure audit: making the repository easy for an AI to keep building

*2026-09-18. Jerus's brief: "go around, commenting, inside the project, analyzing it, thinking of ways of how to better optimize stuff, not optimize the game, but optimize the structure so that its easier for ai to continue building the game, also perhaps even building java files that are just tools to help the ai find stuff outside of its context window."*

This is that pass. It looked at the tree as it stood this morning (0.6.7, save format 27, 174 source files and 120,500 lines before today's additions), at the 224 documents in this project, and at the loop a session actually runs — staging, building, checking, deploying, writing up. The first half says what makes the work slow or risky for an assistant today; the second says what was built and changed, and what is recommended but not done because it is Jerus's call.

Nothing in the game's behaviour changed. The suite reads exactly as it did this morning: 55 of 56 pass, with `InfrastructureCheck`'s *"...and its shops can actually be supplied"* still the one red line, deferred since the restaurants batch.

## 1. What is hard for an AI here, and why

### Two files are a third of the code, and no model can read either

`UserInterface.java` is 25,098 lines and 1.29 MB. `Game.java` is 8,196 lines. Between them they are 27% of the tree. A model's context window holds perhaps a fifth of the interface file, so no session has ever read it; every change to a screen has been made by grepping for a banner, guessing an offset, reading a slice, and hoping the slice was the right one. It usually was, because the banners are good — the interface file is really a hundred screens filed one after another, each opened by a `/* ===== TITLE` comment. But a hundred screens in one file means every session that touches any of them stages 1.29 MB, and a one-line change to the bank screen produces a diff against a file that also holds the people screen, the clock and the save dialog.

The same is true of `Game.java` in a milder form: twenty banner sections, each a mechanic (the construction subsidy, the households buying cars, the luxury counter, the crime causes, what the city eats, the currency reform), around a 540-line `nextMonth()` that is the month's spine.

### There was no table of contents

The banners are the navigation, and they were discoverable only by grepping. A session that wanted "the bank's branch logic" had to know to search for `BRANCH`, then read from the hit until it found the end. There was nothing that said: the bank screen is lines 7120 to 7331 and has these forty methods, `wantsBranch` is in `Bank.java` at line 611, `DEPOSITS_PER_BRANCH` is 250,000 and the sentence above it says what it means. That knowledge existed only in whichever session had last read the file, and it left with that session.

### The to-do list had become the changelog

`claude/todo.md` was 329 KB this morning, up from 169 KB three days ago. Of its 5,289 lines, the first 3,545 were forty-five version blocks stacked newest-first above the list, and the list itself began at line 3,546. Every session was told, correctly, to read the todo first — and paid something like eighty thousand tokens to reach section 0. The blocks are valuable: they are the record of what shipped, what it found, and what it left open. They are not the list, and a list nobody can reach is not a list.

### The design record is invisible from the repository

Every design note, the rules, the loop and the todo live in this claude.ai project, and the manual is a private artifact. A Claude Code session on the PC, a chat with the repository attached, or any other assistant sees `README.md` and the code and nothing else. The README is good, but it points at a private link for everything that matters, and its own numbers had drifted: it said ten sectors, 157 files, fifty-five buildings, fifty harnesses and build 0.6.0, against fifteen, 174, seventy-three, fifty-six and 0.6.7.

### Stale copies in the project

Twenty-seven Java files were uploaded to this project on 2026-09-01 as documents: `Game.java`, `UserInterface.java`, `EconomyManager.java`, `DataSave.java` and the rest, plus `CommercialHandler.java`, `IndustrialHandler.java`, `ConstructionHandler.java` and `MenuManager.java`, which no longer exist in the tree. They are two and a half weeks and about fifty batches old. A search of the project can return them as if they were current, and an assistant that trusts the project over the PC would be reading code that has since been replaced by the sector template. They should go.

### Line endings are mixed, and every edit has to know which

120 files are CRLF and 55 are LF, by whichever editor last saved them. There was no `.gitattributes`. A session editing a file from Linux has to check its ending first and preserve it, or the diff is the whole file; NetBeans on Windows does the reverse. This has been handled by hand on every deploy and it has cost at least one whole-file diff.

### What is already right, and should not be touched

The things that make this codebase workable for an assistant are worth naming so that nothing below undoes them. Every class opens with a sentence saying what it is and a `WHY` paragraph saying what it replaced and what went wrong with the old one; that paragraph is the expensive part of the knowledge and it is in the one place a reader will find it. Screens and mechanics are introduced by banners, often dated, and a quoted `Jerus:` line inside one is the requirement in the designer's words. The sector template turned five handlers in five shapes into one class per business and a registry, and eight sectors have been added since at an afternoon each. `GameVersion` is the one place the version is written and `Build EXE.bat` reads it from there. Harness cities use `GameFiles.scratch()` and cannot touch a real save. The four harness rules in the README are the reason the suite still means something at fifty-six files. The year book exists so a run can be read without a save. None of that changes.

## 2. What was built today

Everything below is in the tree on the PC, verified byte-for-byte, and compiles with the game. The generators add nothing to the game's behaviour; they read `src/main/java` as text and write `docs/`.

### The tools package: `ham.citybuildersim.tools`

`JavaScan` is a structural reader of one Java file without a compiler: a tokenizer that handles strings, text blocks, char literals and comments, plus brace counting and just enough grammar to tell a method from a field from an inner type. It records every banner section at any depth, every member with its start and end line, its signature, the first sentence of the comment above it, and every string literal with the call it sits in. It does not use javac's Tree API, deliberately: that lives in `jdk.compiler`, which the packaged runtime does not carry, and it drops comments, which are the part of this codebase worth indexing. Checked against `javap`, it finds every declared member; the only names `javap` has that the scanner does not are record accessors and the methods of anonymous classes inside bodies, which is correct.

On top of it, five tools, each a `main`:

| tool | writes or prints |
|---|---|
| `CodeMap` | `docs/map/README.md`, one row per file (size, methods, what it is, how many files use it, the game version read from `GameVersion`), and `docs/map/NAME.md` per file: the class header, what it uses and what uses it, its sections with line numbers, its constants, its fields, and every method under the section it sits in, with its line, length and first sentence |
| `Dials` | `docs/dials.md`: all 681 `static final` constants, by file, with value, line and the sentence above each |
| `MonthOrder` | `docs/month-order.md`: the top-level statements of `Game.nextMonth`, `Game.startOfMonthUpdate`, `SimulationEngine.simulateMonth` and the rest of the spine, numbered, with the comment above each and a link to where each call lands |
| `HarnessMap` | `docs/harnesses.md`: every labelled assertion in every harness (3,061 of them) under the section it prints, which harnesses mention which class, and whether every `*Check` file is in `AllChecks` |
| `Maps` | runs the four above; `Regenerate maps.bat` on the PC calls it |
| `Where` | `Where nextMonth` lists every member of that name with file and lines; `Where Game.nextMonth -print` prints just that method, numbered, for pasting into a chat; `Where "THE BANK"` finds a banner by title |
| `SaveDump` | looks inside a save or history file without loading the game: the header, every top-level key with its shape, and any dotted path into it; arrays of numbers print length, first, last, min, max and sum |

The whole `docs/` tree is 1.9 MB, which sounds like the problem restated, but it is split so that a reader opens 23 KB (`map/README.md`) and then one file. `map/UserInterface.md` is 96 KB against the source's 1,290; `map/Game.md` 44 against 396. A session that wants the bank screen reads the index, finds "THE BANK" at line 7120 with its methods beneath it, and stages two hundred lines.

### The repository files

`CLAUDE.md` at the root is the briefing an assistant reads first: what to open before touching source, the standing rules (the save slots, "yes, always", the harness rules, `Sectors.KEYS` order, flows are saved not reconstructed, the month is a sequence, when `SAVE_FORMAT` moves), the shape of the tree and the conventions the indexes rely on, how a batch is done both on the PC and from a cloud session, and where the shape already is for adding a sector, a saved field, a screen section, a dial or a harness. `AGENTS.md` points every other assistant at it. `.gitattributes` says LF everywhere and CRLF for `.bat`, with the one-time renormalise command in its comment. `Regenerate maps.bat` rebuilds `docs/` after a Clean and Build. The README's counts are current and it now describes the generated documents and points at `CLAUDE.md`; its status line no longer states a build number, because `GameVersion` does.

### The project files

`claude/todo.md` is the list alone: the head is two lines and section 0 starts at line 5. `claude/changelog.md` holds the forty-five version blocks, verbatim, newest first, with today's block on top, and beneath them the twenty-three "Done" blocks that sat inside the list's section 6. Nothing was deleted or reworded; the two files together are the old file plus their headers. `claude/index.md` maps all 200 design notes by subsystem — start here, money and the bank, treasury and taxes, sectors, transport, people, land, the interface, saves and packaging, playtests — one line each, from the titles and the changelog's blocks. And this note.

## 3. Recommended, and not done, because it is Jerus's call

**Normalise the line endings, once.** `.gitattributes` is in place; the tree is still mixed. On the PC, in a commit of its own: `git add --renormalize .` then `git commit -m "Normalise line endings to LF"`. Every CRLF file shows as changed in that commit and never again. Until then the "keep each file's own ending" rule stands, and `CLAUDE.md` says so.

**Split `UserInterface.java` along its banners, screen by screen.** This is the largest structural gain available and the only one that changes what a session can hold in view. The file already is a hundred classes filed in one; the map shows the seams exactly — `SEATS AGAINST WHO WOULD COME` is 1,175 lines, `THE RECORD` 1,121, `A STATEMENT LINE THAT OPENS` 1,049, `THE STAT CARD` 869, `INFRASTRUCTURE` 774, `THE CLOCK` 666, `BORROW` 639. The shape would be a `ui/` package, one class per screen holding that section's methods and fields, given the `UserInterface` (for the shared helpers: the theme, `money()`, the strips, `clearMenu()`) and the `Game`. Done mechanically, a section at a time, with `build-ui.sh` and `BuildMenuCheck` between each and the game opened on the PC after each batch of five or so. Twenty sessions' worth if done alone; far less if done as the tail of ordinary batches, moving whichever screen a batch touches anyway. What it needs from Jerus is the decision, and an order: the screens changed most often (the sector economy, the people screen, the bank and the treasury) pay back first.

**Then `Game.java`, more gently.** Its sections are mechanics, not screens, and several already have the shape of a class: the households' car purchase, the luxury counter, the crime causes, what the city eats, the currency reform. Each could move to its own file with `Game` keeping the seam and the month. Lower priority than the interface; the file is a third of the size and `docs/month-order.md` now makes its spine legible.

**Do not move the harnesses to their own package yet.** It would clean the listing, but fifty-six harnesses call about twenty package-private members of the model at seventy-odd sites (`spend`, `deskBuysFromAbroad`, `setCashForTest`, `subsidiseForTest`, `resolveDirectory` and the rest). Those would have to become public or be given a test seam, and the seam is the harder design question. The map's area column already separates them; the cost is not worth it now.

**Mirror the working documents into the repository.** A Claude Code session on the PC cannot see this project. The least that would fix it is a `docs/notes/` folder holding `todo.md`, `changelog.md` and `index.md`, written by each cloud session as part of its deploy, so the repository carries the list and the record even if the design notes stay here. The most would be all two hundred notes, which is 650 KB of prose in git — cheap, but then there are two copies of every note. Jerus's choice; the least is recommended and costs nothing per batch.

**Delete the twenty-seven stale Java files from the project.** They are the 2026-09-01 uploads listed in section 1. `project_delete` can do it on his word; they are not text this session wrote.

**Prune the list itself.** After the split, `todo.md` is 1,350 lines, and section 2 still carries the built narratives of 2026-09-10 and 2026-09-11 (the sector template, the long sick, the households remember) alongside the two open questions of Jerus's that came out of them. A pass that moves the built prose to the changelog and leaves each open item as a line with a pointer would bring the list under five hundred lines. Not done today because the list is the record of his decisions and the pruning wants his eye on what is still open.

**Bring the manual up to date.** The artifact is at version 5, describing 0.5.15. The tree is 0.6.7 and three sectors, the transport stack, the consumption basket and the vehicles have shipped since. That is a documentation batch of its own — `the-documentation-catches-up.md` describes the last one — and `changelog.md` now gives it a clean list of what to add.

**Two things the harness map turned up, for the list rather than the structure.** Eleven model classes are mentioned by no harness: `Automotive`, `LuxuryRetail`, `HeavyIndustry`, `Materials`, `BuildingInstance`, `BusinessDebt`, `Currency`, `GamePrefs`, `Investor`, `ShadowBasket` and `SimulationEngine`. Some are reached through other classes (`SimulationEngine` runs in every harness that runs a month) and some are plain data; `Automotive` and `LuxuryRetail` are the two that a `CarCheck` and a luxury check ought to name. And two harnesses do not label their checks through a helper, `LongPlaytest` and `BuildMenuCheck`, so the map cannot list what they assert — `LongPlaytest` prints findings, which is its design.

## 4. How the next session starts

Read `CLAUDE.md` in the repository, then `docs/map/README.md`. Read the top block of `claude/changelog.md` for the state of the tree and section 0 of `claude/todo.md` for what is open. For anything in a specific file, open its map before its source. Regenerate the maps after a batch and commit `docs/` with it; add the batch to the changelog's top and the write-up to the project, and `claude/index.md` gets its line.

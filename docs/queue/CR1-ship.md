## changelog
### {next} — CR1, a Credits page from the title screen — 2026-11-09, ready, see docs/queue/CR1-notes.md
The title screen and the Esc menu have a Credits button under Settings, and the page it opens shows
the two data files' credits in full, the Earth's (`ATTRIBUTION-2.txt`) and the nations' (`world/ATTRIBUTION.txt`),
then the fonts' notice (IBM Plex Sans and Mono, Copyright © 2017 IBM Corp., SIL Open Font License 1.1) and the
licence's own text. The jar carries both files straight from `tools/` (`pom.xml`'s resources), so the page and the
copies beside the exe have one source each, and a notice a file gains is on the page with it. Both files' "ships
beside the exe" lines now name the page. `Credits` is the page's model; `CreditsCheck` proves every file
`Build EXE.bat` ships as a credit is on the page word for word, the fonts are all IBM Plex under that notice, and
a build missing a file says where it is. Checked by eye.
## pending
| CR1-L | the libraries' notices (Gson's Apache 2.0, OpenJFX's GPL v2 with the Classpath Exception) on the Credits page ({next}) | ★1: the brief named the data and the fonts only | docs/queue/CR1-notes.md |

<!-- readiness: provisional -->
## changelog
### {next} — CR1-L, the libraries' notices on the Credits page — 2026-11-17, ready, see docs/queue/CR1-L-notes.md
The Credits page now shows Gson's Apache 2.0 licence and OpenJFX's GPL v2
with the Classpath Exception, its additional notices and the graphics
module's JPEG and Mesa notices, after the fonts. These releases omit legal
files from their jars, so the build packages one exact upstream release
copy of each; CreditsCheck pins their versions, hashes and full page text.
The packaged runtime's notices on the page are queued as CR1-J.

## queued
| CR1-J | Show the packaged Java runtime's notices on the Credits page, reading its actual runtime/legal inventory and resolving its internal references; audit accompanying Windows runtime notices | docs/queue/CR1-L-notes.md |

<!-- readiness: provisional -->
## changelog
### {next} ? CM2, the replay's loaded move on the globe ? 2026-11-14, ready, see docs/queue/CM2-notes.md
Loaded replay moves now ask and draw ground charts across the whole visible globe disc in GROUND, removing coarse
planet-picture patches: the seven-move probe at 1920 ? 1080 reports no unpainted frames, worker asks or picture evictions.
Glides across months retain their decoded maps and already-loaded land contexts during loading; M6's worst frame gap fell
from 270.3 ms to 35.6 ms. Full-resolution moving globes were measured and lost frames, so half resolution is retained.
The normal game is unchanged. ReplayCheck covers retained-map ownership, file-free playback and release, and GlobeCheck
covers the wider ground coverage while preserving ordinary pixels. See the notes for measurements and validation.

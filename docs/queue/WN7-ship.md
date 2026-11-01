## changelog
### {next} — WN7, ships and trains to their partners — 2026-11-01, ready, see docs/queue/WN7-notes.md
The nations line's last batch: with it WN0-WN7 are whole (the world file, the nations and their paths, the map, the
market, the moneys, land from its nation, the screens, and this). Each boat and train now carries its partner, drawn
from the month's tonnes of its cargo to each nation (PartnerTrade keeps them by kind; CallPartners draws), and a boat
leaves its offing on the open exit nearest its partner's bearing: SeaRoutes gives every offing an exit per 5-degree
bearing, the open sea on it. Trains go only to the host and nations within 2,500 km by land, on the one world line.
Each lane forks at its offing toward the exits a twentieth of its kind's tonnes take (at most 8 a kind; 1-7 from the
13 ports measured), and a boat or a train under the pointer names itself and its partner ("Capesize · from Brazil").
Nothing in the model reads any of it: the playtest's eight trace files are main's to the byte, so no ensemble.
PartnerCallsCheck (new, 9 sections); a frame's boats at 10B 0.249 ms with partners, 0.227 without (budget 0.5).
## pending
| WN7-C | Eye-check WN7 ({next}) on an Earth city with a port (Halifax or Rotterdam) and on a generated one: the lane forking at its offing toward its partners (a few forks, not a starburst), boats leaving on the forks and fanning, a small partner's boat leaving off them, and the label on a boat and on a train under the pointer (its words, whether white at .45 over the halo reads or is faint, ★4). | needs his eye | docs/queue/WN7-notes.md §5, §8 |

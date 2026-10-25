## changelog
### {next} — ET4, highways and rail priced by slope — 2026-10-25, ready but MapCheck 8 (main's, FW11), see docs/queue/ET4-notes.md

A highway or a railway line (the Elevated Highway, the Rail Spur, the Freight Line) is now a **run**: its land comes from
every class of ground in the city's proportions, not the buildable pool alone, so a mountain city's highways use its steep
ground (`LandManager.Use.RUN`, `runsSqFt` saved, `runRoom()` keeping the buildings room). An order pays the **cuts,
viaducts and tunnels** its land needs at the city's mix (`RunWorks`: a highway's works $110 a m² on foothill, a $2,080
viaduct on steep, a $2,330 tunnel on very steep; a railway's 2.7 times), as builders' work owed on the site, in the
quote, Build's card and the Freight investor's cost. On the map a run on steep ground is a viaduct, on very steep a tunnel
(portals, a dashed line). New RunWorksCheck. A flat city reads a run as a building: the default trace and Rotterdam's
byte-identical (no ensemble); Medellín ends at 245,314 people (432,191), rail sidings near twice as dear.
## pending
| ET4-C | ET4 ({next}) makes highways and rail runs: their land from every class of ground in the city's proportions (runsSqFt, runRoom, the pads a run displaces counted prepared), their cuts, viaducts and tunnels priced at the city's mix as builders' work (RunWorks: foothill $110, steep a viaduct $2,080, very steep a tunnel $2,330 a m² of a highway's land, a railway's 2.7 times), drawn as viaducts and tunnels. Not done: no screen seen in a played game (renders only, docs/queue/ET4/; Medellín's runs touch no very steep ground, so the tunnel render is the fixture's); tunnel portals at a tile's edge and on a 45-degree stretch; the build advice's ground at the dry price (ET3-C); found: BusinessInvestment.totalCostOf() omits a building's foothill pad (ET2) | His calls (ET4-notes, Calls): ★1 the rates, from DOT unit costs, the EC's tunnel study and the pad's bench - keep (rec.); ★2 rail's factor 2.7 (6% / 2.2% grades) moves Medellín to 245,314 people (432,191; the land alone 454,499) as a Rail Spur nearly doubles - keep (rec.), the spec's 2.2, or a spur priced as yard ground; ★3 works' points at the run's own founding price a point - keep (rec.); ★4 tunnel portals at tile edges and 45-degree stretches - a small follow-up (rec.) | [ET4-notes](docs/queue/ET4-notes.md) |

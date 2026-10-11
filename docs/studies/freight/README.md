# freight/ - beside spec-freight-and-infrastructure.md

- `today-rail-yard.png`, `today-highway-ramps.png` (and its 4x crops,
  `today-highway-ramps-zoom4x.png`), `today-shore-ports.png`: the map as
  0.7.123 draws it, by the game's own `TileRaster` at 8 px a plot (L0), from
  MapCheck's `yardCity` at x30 and section 9's coastal town.
- `RenderToday.java`: the throwaway harness that drew them. It sits in package
  `ham.citybuildersim` to reach MapCheck's package-private fixtures and is
  compiled outside the tree; nothing in the game reads it. To run it (Linux,
  Java 21, the gson jar Maven ships):

      R=<a clone>; S=<a scratch dir>; GSON=/opt/apache-maven-3.9.11/lib/gson-2.13.1.jar
      find $R/src/main/java -name "*.java" | grep -v "/ui/" | grep -v -e BuildMenuCheck.java -e /CityBuilderSim.java > $S/sources.txt
      javac -nowarn -encoding UTF-8 -d $S/classes -cp $GSON @$S/sources.txt
      javac -nowarn -encoding UTF-8 -d $S/hclasses -cp $S/classes:$GSON RenderToday.java
      java -Djava.awt.headless=true -Xmx8g -cp $S/hclasses:$S/classes:$R/src/main/resources:$GSON ham.citybuildersim.RenderToday $S 30

- The mockups: `1-the-map.html`, `2-freight-sector.html`,
  `3-how-its-tonnes-move.html`, `4-build-infrastructure.html`, and
  `freight-study.html`, which holds all four (the artifact "Freight &
  Infrastructure Study"). Static pages; open them in a browser.

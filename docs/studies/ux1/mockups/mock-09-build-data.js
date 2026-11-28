// UX1 mockups 9 and 10: Build › Industry in the big city (the playtest at month 2,406), the 18 groups gathered in five
// families. Read from the game's pictures: Food's five buildings (makes, all in, months, the bakeries' staff and losses),
// the bread and processed-meat lines, Industry's 340 standing, the materials yard and the builders. The rest is made up
// to agree with them: 6 of the 340 the city's own, 334 the market's.
// A building: [name, icon, the city's, the market's, makes a month, all in ($M), its price in months of what it adds,
// staff, to its owner a month ($M: + earns, - would lose), the market's small ones (Refining)].
const FAM = [
  { key: 'food', name: 'Food', h: 45, s: 72, groups: [
    { name: 'Food mills', what: 'bread', made: 0, used: 6828, unit: 'kg', b: [
      ['Industrial Bakery', 'MILL', 0, 0, '213k kg bread', 69000, 12.7, 65, -739.6],
      ['Bakery', 'MILL', 0, 0, '232k kg bread', 32800, 5.5, 54, -576.9]] },
    { name: 'Food processing', what: 'processed meats', made: 0, used: 10128, unit: 'kg', b: [
      ['Meat Works', 'FOOD', 0, 0, '5,000 kg meats', 3800, 10.2, 38, -41.3],
      ['Snack & Oils Plant', 'CAN', 0, 0, '8,000 kg cooking fats', 4400, 6.7, 31, -28.6],
      ['Bottling Plant', 'DROP', 0, 0, '60,000 kg drinks', 3200, 4.6, 26, -6.9]] }] },
  { key: 'metals', name: 'Metals', h: 212, s: 34, groups: [
    { name: 'Iron Mining', what: 'iron ore', made: 31200, used: 44000, unit: 't', b: [
      ['Iron Mine', 'ORE', 0, 8, '9,000 t ore', 6100, 8.4, 42, 38.2],
      ['Large Open-Pit Mine', 'ORE', 0, 2, '36,000 t ore', 21700, 7.9, 128, 121.5],
      ['Magnetite Mine & Concentrator', 'ORE', 0, 0, '14,000 t concentrate', 18300, 13.1, 96, -22.4],
      ['Large Magnetite Complex', 'ORE', 0, 0, '48,000 t concentrate', 58900, 12.2, 240, -51.0]] },
    { name: 'Gold Mining', what: 'gold', text: 'gold: all sold abroad', b: [
      ['Gold Mine & Mill', 'ORE', 0, 1, '85 kg gold', 9400, 9.8, 70, 18.7]] },
    { name: 'Iron & Steel', what: 'steel', made: 22800, used: 61400, unit: 't', b: [
      ['Electric Arc Furnace', 'FLAME', 0, 6, '1,400 t steel', 17800, 8.6, 44, -96.0],
      ['Steel Mini-Mill', 'INGOT', 2, 9, '1,200 t steel', 24900, 7.8, 64, -218.4],
      ['Scrap Yard', 'LORRY', 1, 14, '3,000 t scrap', 2900, 3.9, 18, 22.6],
      ['Pelletising Plant', 'ORE', 0, 2, '20,000 t pellets', 14200, 9.5, 52, 9.8],
      ['Coke Ovens', 'FLAME', 0, 3, '6,000 t coke', 11600, 10.4, 46, -14.5],
      ['Mini Blast Furnace', 'FLAME', 0, 1, '9,000 t iron', 22300, 11.0, 88, -63.1],
      ['Blast Furnace', 'FLAME', 0, 0, '40,000 t iron', 86500, 9.6, 260, -140.7],
      ['Oxygen Steel Shop', 'INGOT', 0, 0, '42,000 t steel', 47900, 6.9, 140, -88.3],
      ['Large Mini-Mill', 'INGOT', 0, 0, '6,000 t steel', 71000, 8.3, 170, -402.6],
      ['Hydrogen Steel Works', 'INGOT', 0, 0, '12,000 t steel', 164000, 18.9, 310, -911.0]] },
    { name: 'Copper', what: 'copper', made: 2140, used: 3900, unit: 't', b: [
      ['Copper Mine & Mill', 'ORE', 0, 4, '700 t copper in ore', 12800, 9.0, 80, 14.1],
      ['Large Copper Mine', 'ORE', 0, 1, '2,800 t copper in ore', 44600, 8.7, 260, 46.0],
      ['Leach & SX-EW Plant', 'FLASK', 0, 2, '400 t cathode', 8200, 7.1, 36, 11.9],
      ['Sulphide Leach & SX-EW Plant', 'FLASK', 0, 0, '900 t cathode', 21400, 9.9, 70, -3.1],
      ['Copper Smelter', 'FLAME', 0, 1, '2,000 t anode', 31000, 11.6, 120, -24.8],
      ['Electrolytic Refinery', 'FLASK', 0, 1, '2,000 t cathode', 18900, 6.4, 64, 7.5],
      ['Scrap Copper Refinery', 'INGOT', 0, 2, '600 t cathode', 5300, 4.8, 22, 16.3],
      ['Rod Mill', 'GEAR', 0, 1, '1,800 t rod', 6700, 5.2, 30, 4.4]] }] },
  { key: 'making', name: 'Making', h: 22, s: 68, groups: [
    { name: 'Fabrication & machinery', what: 'machinery', made: 68, used: 100, unit: '%', text: 'machinery: 68% of what the city uses', b: [
      ['Fabrication Shop', 'GEAR', 0, 54, '120 t steel goods', 21000, 4.9, 16, 81.0],
      ['Machine Works', 'GEAR', 0, 30, '40 machines', 74000, 5.6, 48, 310.0],
      ['Fabrication Works', 'INDUSTRY', 0, 31, '1,400 t steel goods', 296400, 4.1, 410, 1900.0],
      ['Wire & Cable Works', 'GEAR', 0, 12, '600 t cable', 58000, 6.3, 40, 126.0]] }] },
  { key: 'materials', name: 'Materials', h: 95, s: 44, groups: [
    { name: 'Stone', what: 'stone', made: 410000, used: 380000, unit: 't', b: [
      ['Quarry', 'ORE', 0, 15, '18,000 t stone', 3100, 4.4, 22, 6.2],
      ['Large Quarry', 'ORE', 0, 5, '28,000 t stone', 10800, 4.0, 70, 27.5],
      ['Dimension Stone Quarry', 'ORE', 0, 0, '2,000 t cut stone', 4600, 9.2, 34, -5.4]] },
    { name: 'Cement', what: 'cement', made: 35400, used: 58000, unit: 't', b: [
      ['Cement Plant', 'FLAME', 0, 8, '3,200 t cement', 26400, 7.0, 90, 41.8],
      ['Large Cement Plant', 'FLAME', 0, 2, '6,000 t cement', 44000, 6.1, 150, 98.0],
      ['Lime Kiln', 'FLAME', 0, 1, '900 t lime', 4200, 5.3, 20, 3.9]] },
    { name: 'Wood', what: 'lumber', made: 33600, used: 41000, unit: 'm³', b: [
      ['Logging Camp', 'TREES', 0, 18, '2,500 m³ logs', 900, 3.1, 14, 4.8],
      ['Harvesting Operation', 'TREES', 0, 9, '9,000 m³ logs', 5600, 3.6, 40, 19.0],
      ['Tree Plantation', 'TREES', 0, 6, '4,000 m³ logs', 2200, 6.8, 12, 2.1],
      ['Woodlot', 'TREES', 0, 4, '600 m³ logs', 300, 5.0, 4, 0.6],
      ['Sawmill', 'MILL', 0, 13, '1,600 m³ lumber', 4000, 4.9, 30, 7.7],
      ['Large Sawmill', 'MILL', 0, 4, '3,200 m³ lumber', 13900, 4.5, 84, 29.4],
      ['Panel Mill', 'MILL', 0, 3, '2,400 m³ panels', 9100, 6.0, 46, 5.0],
      ['Pulp Mill', 'PAPER', 0, 2, '5,000 t pulp', 27300, 8.8, 110, -19.6],
      ['Paper Mill', 'PAPER', 0, 2, '4,000 t paper', 19800, 7.4, 80, 3.3],
      ['Wood Pellet Mill', 'MILL', 0, 1, '2,000 t pellets', 2600, 6.6, 12, -1.2]] },
    { name: 'Building materials', what: 'materials', text: 'materials: 0 in the yard, $126.9M a unit abroad', short: true, b: [
      ['Construction Materials Plant', 'CRANE', 0, 2, '24 units', 9600, 3.2, 36, 412.0]] },
    { name: 'Builders', what: 'builders', text: 'builders: 30,253 pts a month, 7.4 months queued', short: true, b: [
      ["Builders' Yard", 'CRANE', 2, 14, '600 pts of building', 3300, 4.6, 60, 2.4],
      ['Construction Depot', 'CRANE', 1, 8, '2,200 pts of building', 11000, 5.8, 180, 11.0]] }] },
  { key: 'energy', name: 'Energy', h: 190, s: 62, groups: [
    { name: 'Coal', what: 'coal', made: 71000, used: 96000, unit: 't', b: [
      ['Surface Coal Mine', 'ORE', 0, 5, '6,000 t coal', 7000, 5.2, 40, 9.4],
      ['Large Surface Mine', 'ORE', 0, 2, '20,000 t coal', 24500, 4.8, 130, 38.0],
      ['Underground Coal Mine', 'ORE', 0, 2, '5,500 t coal', 12200, 9.6, 120, -6.6],
      ['Coal Preparation Plant', 'INDUSTRY', 0, 2, '12,000 t washed coal', 5500, 6.0, 24, 2.2]] },
    { name: 'Oil', what: 'crude', made: 52, used: 100, unit: '%', text: 'crude: 52% of what the refineries take', b: [
      ['Oil Well', 'WELL', 0, 7, '9,000 bbl crude', 2400, 3.9, 6, 5.1]] },
    { name: 'Oil platforms', what: 'crude', text: 'none at sea', b: [
      ['Offshore Platform', 'PLATFORM', 0, 0, 'holds 12 wells', 210000, 14.2, 160, -44.0],
      ['Platform Well', 'WELL', 0, 0, '15,000 bbl crude', 18000, 9.0, 8, -3.0]] },
    { name: 'Oil pipelines', what: 'crude', text: 'crude carried in', b: [
      ['Crude Pipeline', 'TANKER', 0, 2, 'carries 300k bbl', 16000, 7.5, 10, 4.0]] },
    { name: 'Refining', what: 'fuels', made: 47, used: 100, unit: '%', text: 'fuels: 47% of what the city burns', small: true, b: [
      ['Oil Refinery', 'FLAME', 0, 2, '60,000 bbl fuels', 96000, 8.1, 300, -12.0, 1],
      ['Crude Unit', 'TANK', 0, 1, '40,000 bbl distillate', 38000, 7.3, 90, 6.0],
      ['Reformer', 'FLASK', 0, 1, '9,000 bbl petrol', 21000, 8.0, 40, 2.0],
      ['Cracking Unit', 'FLAME', 0, 1, '14,000 bbl petrol', 33000, 8.8, 60, -1.5],
      ['Hydrocracker', 'FLASK', 0, 0, '12,000 bbl diesel', 41000, 10.1, 60, -8.2],
      ['Alkylation Unit', 'FLASK', 0, 1, '4,000 bbl alkylate', 12000, 7.7, 24, 1.1],
      ['Coker', 'FLAME', 0, 0, '6,000 bbl, petcoke', 29000, 11.4, 50, -9.9],
      ['Lube Plant', 'DROP', 0, 1, '1,500 bbl lubricants', 9000, 6.2, 30, 3.4],
      ['Asphalt Unit', 'ROADS', 0, 1, '2,000 t asphalt', 6000, 5.9, 20, 1.8]] },
    { name: 'Oil storage', what: 'crude', text: 'stores 1.2M bbl', b: [
      ['Tank Farm', 'TANK', 0, 3, 'stores 400k bbl', 4800, 6.5, 6, 1.1]] }] }];

const nf = n => n.toLocaleString('en-US');
/** $M as the game writes money: $412.0M, $24.9B, $1.9T. */
function money(m, sign = false) {
  const a = Math.abs(m), s = m < 0 ? '−' : sign ? '+' : '';
  return s + (a >= 1e6 ? '$' + (a / 1e6).toFixed(1) + 'T' : a >= 1000 ? '$' + (a / 1000).toFixed(1) + 'B' : '$' + a.toFixed(1) + 'M');
}
/** A group's supply line and its share made here (null when it has none). */
function supply(g) {
  if (g.text) return [g.text, g.unit === '%' ? g.made / g.used : g.short ? 0.05 : null];
  return [`${g.what}: ${nf(g.made)} made of ${nf(g.used)} ${g.unit} used`, g.made / g.used];
}
const shareColour = s => s === null ? '#5f7189' : s < 0.5 ? '#f85149' : s < 0.95 ? '#e3b341' : '#3fb950';
/** A building's colour: its family's hue, a shade by its place in its group. */
function shade(f, i, n, a = 1) {
  const l = n === 1 ? 64 : 52 + 26 * i / (n - 1);
  return a === 1 ? `hsl(${f.h} ${f.s}% ${l}%)` : `hsl(${f.h} ${f.s}% ${l}% / ${a})`;
}
/** The best per $ in a group: the fewest months among those whose owner would earn (none when it stands alone). */
function best(g) {
  const pay = g.b.filter(b => b[8] > 0);
  if (g.b.length < 2 || !pay.length) return null;
  return pay.reduce((a, b) => b[6] < a[6] ? b : a)[0];
}
const famCount = (f, k) => f.groups.reduce((t, g) => t + g.b.reduce((u, b) => u + b[k] + (k === 3 && b[9] ? b[9] : 0), 0), 0);
const famKinds = f => f.groups.reduce((t, g) => t + g.b.length * (g.small ? 2 : 1), 0);

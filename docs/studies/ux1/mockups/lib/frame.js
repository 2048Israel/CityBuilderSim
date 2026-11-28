// UX1's mockups: the frame every picture shares - the map's ground, the top strip, the rail, the alert row, the needs.
// A page calls frame({...}) and then adds its own HTML; nothing here is the game's code, only its look.

const AREA = { people: '#2ec4b6', money: '#5aa9ff', business: '#a78bfa', building: '#f17cb0' };

/** One of the game's outline icons as inline SVG. */
function icon(name, size = 18, color = 'currentColor', width = 2) {
  const d = ICON[name] || ICON.INFO;
  return `<svg class="i" width="${size}" height="${size}" viewBox="0 0 24 24" stroke="${color}" stroke-width="${width}"><path d="${d}"/></svg>`;
}

/** A status ring as the game draws them: a track and an arc, the share in the middle (share 0..1; shown may differ). */
function ring(share, size = 54, color = '#3fb950', text = null, stroke = 5) {
  const r = (size - stroke) / 2, c = 2 * Math.PI * r, f = Math.max(0, Math.min(1, share));
  const label = text === null ? Math.round(share * 100) + '%' : text;
  return `<svg class="ring" width="${size}" height="${size}" viewBox="0 0 ${size} ${size}">
    <circle cx="${size/2}" cy="${size/2}" r="${r}" fill="none" stroke="#243246" stroke-width="${stroke}"/>
    <circle cx="${size/2}" cy="${size/2}" r="${r}" fill="none" stroke="${color}" stroke-width="${stroke}" stroke-linecap="round"
      stroke-dasharray="${c*f} ${c}" transform="rotate(-90 ${size/2} ${size/2})"/>
    <text x="50%" y="50%" dominant-baseline="central" text-anchor="middle" fill="#e6edf3" font-family="PlexMono" font-size="${Math.round(size/4.2)}" font-weight="500">${label}</text></svg>`;
}

/** A need's small circle: a filled dot ringed by how far it is met. */
function dot(share, color, size = 20) {
  const r = size / 2 - 2.5, c = 2 * Math.PI * r, f = Math.max(0, Math.min(1, share));
  return `<svg width="${size}" height="${size}" viewBox="0 0 ${size} ${size}" style="flex:none">
    <circle cx="${size/2}" cy="${size/2}" r="${r}" fill="${color}22" stroke="#2a3a50" stroke-width="3"/>
    <circle cx="${size/2}" cy="${size/2}" r="${r}" fill="none" stroke="${color}" stroke-width="3" stroke-linecap="round"
      stroke-dasharray="${c*f} ${c}" transform="rotate(-90 ${size/2} ${size/2})"/></svg>`;
}

const RAIL = [
  ['build', 'BUILD', 'Build', 'building'], ['land', 'LAND', 'Land office', 'building'], ['people', 'POPULATION', 'People', 'people'],
  ['services', 'SERVICES', 'Services', 'people'], ['infra', 'INFRASTRUCTURE', 'Infrastructure', 'building'], ['sectors', 'SECTOR', 'Sectors', 'business'],
  ['government', 'GOVERNMENT', 'Government', 'money'], ['finances', 'FINANCES', 'Finances', 'money'], ['bank', 'BANK', 'Bank', 'money'],
  ['trade', 'TRADE', 'Trade', 'business'], ['policy', 'POLICY', 'Policy', 'money'], ['history', 'REPORTS', 'History', 'money'], ['rankings', 'TROPHY', 'Rankings', 'money']];

/** The young town's figures (slot 1, July 1909, month 115) and the big city's (the playtest at month 2,406). */
const TOWN = { date: '1 July 1909', month: 'month 115', treasury: ['$435k', '+$10k a month'], people: ['1,888', '+1'], gdp: ['$4.3M/yr', '+83.6% real'],
  inflation: ['9.8%', 'expect 3.0%'], idle: ['42.2%', 'far too many'], rate: ['R$ 13.00%', 'R$1.41 per US$'], inbox: 6 };
const CITY = { date: '1 June 2100', month: 'month 2,406', treasury: ['$187.9T', '+$608.1B a month'], people: ['174,226', '▼ 134'], gdp: ['$46.9T/yr', '-7.0% real'],
  inflation: ['-3.0%', 'expect -1.0%'], idle: ['12.0%', 'high'], rate: ['D$ 2.36%', 'D$4,558 per US$'], inbox: 12 };

function strip(f, extra = '') {
  const fig = (k, c, v, s, vc = '') => `<div class="fig"><div class="k" style="color:${c}">${k}</div><div class="v ${vc}">${v}<small>${s}</small></div></div>`;
  return `<div class="strip">
    <div class="play"><svg width="14" height="14" viewBox="0 0 24 24"><path d="M6 4l14 8-14 8z" fill="#fff"/></svg></div>
    <div class="date"><b>${f.date}</b><span>${f.month} &nbsp;‹ 1× ›</span></div>
    ${fig('Treasury', AREA.money, f.treasury[0], f.treasury[1])}
    ${fig('Population', AREA.people, f.people[0], f.people[1] + ' this month')}
    ${fig('GDP', AREA.money, f.gdp[0], f.gdp[1])}
    ${fig('Inflation', AREA.money, f.inflation[0], f.inflation[1], f === TOWN ? 'bad' : 'warn')}
    ${fig('Out of work', AREA.people, f.idle[0], f.idle[1], f === TOWN ? 'bad' : 'warn')}
    ${fig('Rate', AREA.money, f.rate[0], f.rate[1])}
    <div class="grow"></div>${extra}
    <div class="chip">${icon('CRANE', 15, '#9fb0c3')} 0 building</div>
    <div class="chip aaa">AAA</div>
    <div class="chip">${icon('MAIL', 15)}<span class="badge" style="background:#5aa9ff">${f.inbox}</span></div>
    <div class="chip">${icon('MENU', 15)}</div></div>`;
}

function rail(on) {
  return `<div class="rail">${RAIL.map(([k, ic, label, a]) =>
    `<div class="tab ${k === on ? 'on' : ''}" style="--c:${AREA[a]}">${icon(ic, 19, k === on ? AREA[a] : '#9fb0c3')}<span>${label}</span></div>`).join('')}</div>`;
}

/** The needs: one small circle and a word each, grouped by area, the open one lit. */
const NEEDS_TOWN = [
  ['People', [['Homes', .96, 'GOOD', '96% housed'], ['Jobs', .58, 'BAD', '42% idle'], ['Food', 1, 'GOOD', 'all fed']]],
  ['Services', [['Power', 1, 'GOOD', '334%'], ['Water', 1, 'GOOD', 'all'], ['Health', .9, 'WARN', '90% least'], ['Schools', 1, 'GOOD', '100%'], ['Safety', 1, 'GOOD', 'in hand']]],
  ['Ground', [['Streets', .48, 'BAD', '48%'], ['Freight', .8, 'WARN', 'tight'], ['Land', .7, 'WARN', '1,136 mo']]]];
const STATE = { GOOD: '#3fb950', WARN: '#e3b341', BAD: '#f85149' };
function needs(list = NEEDS_TOWN, on = '') {
  return `<div class="needs"><div class="t">NEEDS</div>${list.map(([group, items], gi) =>
    (gi ? '<hr>' : '') + items.map(([w, s, st, sub]) =>
      `<div class="need ${w === on ? 'on' : ''}">${dot(s, STATE[st])}<div class="w">${w}<small>${sub}</small></div></div>`).join('')).join('')}</div>`;
}

/** The alert row's marks: [kind icon, state, count, title]. */
function marks(list, left) {
  return `<div class="alerts" style="left:${left}px">${list.map(m => typeof m === 'string' ? m :
    `<div class="mark ${m[1]}">${icon(m[0], 19, m[1] === 'bad' ? '#ff8a80' : m[1] === 'warn' ? '#e3b341' : '#a9d1ff')}${m[2] > 1 ? `<span class="n">${m[2]}</span>` : ''}</div>`).join('')}</div>`;
}

function frame({ ground = 'town', figures = TOWN, on = '', needsOn = '', needsList = NEEDS_TOWN, showNeeds = true, alerts = null, alertsLeft = 652, stripExtra = '', dim = false }) {
  document.body.insertAdjacentHTML('afterbegin',
    `<div class="ground" style="background-image:url(lib/ground-${ground}.png)"></div>${dim ? '<div class="dim"></div>' : ''}` +
    strip(figures, stripExtra) + rail(on) + (showNeeds ? needs(needsList, needsOn) : '') + (alerts ? marks(alerts, alertsLeft) : '') +
    `<div class="zoom"><div>+</div><div>−</div><div>${icon('MAP', 16, '#9fb0c3')}</div></div>`);
}

/** The mockup's own label: white, so it is never taken for the game's interface. */
function note(x, y, html, cls = '') { return `<div class="note ${cls}" style="left:${x}px;top:${y}px">${html}</div>`; }
function num(x, y, n) { return `<div class="num" style="left:${x}px;top:${y}px">${n}</div>`; }
function outline(x, y, w, h) { return `<div class="ring-out" style="left:${x}px;top:${y}px;width:${w}px;height:${h}px"></div>`; }

/** The big city's needs (the playtest at month 2,406, its Build page and the audit): pass as needsList. */
const NEEDS_CITY = [
  ['People', [['Jobs', .88, 'WARN', '12% idle'], ['Food', .12, 'BAD', '88% short']]],
  ['Services', [['Power', 1, 'GOOD', '222%'], ['Water', 1, 'GOOD', 'all'], ['Health', .01, 'BAD', 'childcare 1%'], ['Schools', 0, 'BAD', 'none'], ['Safety', 1, 'GOOD', 'in hand']]],
  ['Ground', [['Streets', .95, 'WARN', 'tight'], ['Land', .06, 'BAD', '4.6 km² free']]]];

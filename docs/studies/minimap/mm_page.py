import base64, sys
mm = sys.argv[1]
out = sys.argv[2]
def img(name):
    return 'data:image/png;base64,' + base64.b64encode(open(f'{mm}/{name}', 'rb').read()).decode()

rail = ['Build', 'Land', 'People', 'Services', 'Sectors', 'Government', 'Finances', 'Bank', 'Trade', 'Policy', 'History']
rail_html = ''.join(f'<div class="rb{" on" if n == "Land" else ""}"><i></i><span>{n}</span></div>' for n in rail)

html = f'''<title>Minimap Study</title>
<style>
/* Layout: a working sheet in the game's own colours - the problem first, the three rules side by side, then the window with the map in it. */
:root {{
  color-scheme: dark;
  --stage: #0b1118;   /* Palette.STAGE */
  --panel: #101924;   /* Palette.PANEL */
  --raised: #152130;  /* Palette.RAISED */
  --edge: #24354a;    /* Palette.EDGE */
  --text: #e6edf3;    /* Palette.TEXT */
  --text-2: #a9b8c9;  /* Palette.TEXT_2 */
  --text-3: #8496ab;  /* Palette.TEXT_3 */
  --accent: #5aa9ff;  /* Palette.ACCENT */
  --warn: #e3b341;    /* Palette.WARN */
  --sans: "IBM Plex Sans", system-ui, -apple-system, "Segoe UI", sans-serif;
  --mono: "IBM Plex Mono", ui-monospace, "SFMono-Regular", Consolas, monospace;
}}
body {{ background: var(--stage); color: var(--text); font: 15px/1.55 var(--sans); }}
main {{ max-width: 1180px; margin: 0 auto; padding-inline: 20px; padding-block: 28px 56px; display: grid; gap: 40px; }}
h1 {{ font-size: 24px; font-weight: 600; margin: 0; text-wrap: balance; }}
h2 {{ font-size: 17px; font-weight: 600; margin: 0; text-wrap: balance; }}
p {{ margin: 0; max-width: 68ch; color: var(--text-2); }}
.eyebrow {{ font: 500 11px/1 var(--mono); letter-spacing: .08em; text-transform: uppercase; color: var(--text-3); }}
header {{ display: grid; gap: 10px; }}
section {{ display: grid; gap: 16px; }}
.pair {{ display: grid; grid-template-columns: repeat(auto-fit, minmax(min(100%, 320px), 1fr)); gap: 20px; }}
figure {{ margin: 0; display: grid; gap: 8px; align-content: start; min-width: 0; }}
figure img {{ width: 100%; height: auto; image-rendering: pixelated; border: 1px solid var(--edge); border-radius: 8px; background: var(--panel); }}
figcaption {{ font-size: 13px; color: var(--text-2); }}
figcaption b {{ color: var(--text); font-weight: 600; }}
.tag {{ display: inline-block; font: 500 11px/1 var(--mono); padding: 4px 7px; border-radius: 4px; border: 1px solid var(--edge); color: var(--text-2); margin-right: 6px; }}
.tag.bad {{ color: #ffb4a8; border-color: #6b2b24; }}
.tag.good {{ color: #a8e6b4; border-color: #22553a; }}
.scroll {{ overflow-x: auto; }}
table.rules {{ border-collapse: collapse; font-size: 13px; }}
table.rules th, table.rules td {{ border-top: 1px solid var(--edge); padding: 12px 14px; vertical-align: top; text-align: left; }}
table.rules th {{ font-weight: 600; color: var(--text); white-space: nowrap; }}
table.rules thead th {{ border-top: 0; font: 500 11px/1.3 var(--mono); letter-spacing: .06em; text-transform: uppercase; color: var(--text-3); }}
table.rules img {{ display: block; image-rendering: pixelated; border: 1px solid var(--edge); border-radius: 4px; }}
table.rules td small {{ display: block; color: var(--text-3); margin-top: 6px; max-width: 30ch; }}
/* the window, at 0.69 of 1,389 x 868 */
.window {{ width: 958px; height: 599px; border: 1px solid var(--edge); border-radius: 10px; overflow: hidden; display: grid; grid-template-rows: 50px 1fr; background: var(--stage); position: relative; }}
.hdr {{ background: var(--panel); border-bottom: 1px solid var(--edge); display: flex; align-items: center; gap: 10px; padding-inline: 12px; font-size: 10px; color: var(--text-2); }}
.hdr .clock {{ font: 600 12px var(--sans); color: var(--text); width: 112px; }}
.hdr .money {{ font: 600 16px var(--mono); color: var(--text); padding-right: 10px; border-right: 1px solid var(--edge); }}
.hdr .tile {{ display: grid; gap: 1px; width: 92px; }}
.hdr .tile b {{ font: 600 11px var(--mono); color: var(--text); }}
.hdr .tile i {{ display: block; height: 8px; border-bottom: 1px solid var(--accent); opacity: .7; }}
.hdr .chip {{ margin-left: auto; padding: 4px 8px; border: 1px solid var(--edge); border-radius: 999px; color: var(--warn); }}
.hdr .env {{ width: 18px; height: 13px; border: 1px solid var(--text-3); border-radius: 2px; }}
.body {{ display: grid; grid-template-columns: 52px 1fr; min-height: 0; }}
.rail {{ background: var(--panel); border-right: 1px solid var(--edge); display: flex; flex-direction: column; align-items: center; padding-top: 4px; }}
.rb {{ width: 52px; height: 37px; display: grid; justify-items: center; align-content: center; gap: 2px; font-size: 7.5px; color: var(--text-3); }}
.rb i {{ width: 12px; height: 12px; border: 1.5px solid currentColor; border-radius: 3px; }}
.rb.on {{ color: var(--accent); }}
.stamp {{ margin-top: auto; width: 48px; display: grid; gap: 3px; justify-items: center; padding-bottom: 6px; font-size: 7px; color: var(--text-3); }}
.stamp img {{ width: 48px; height: 32px; image-rendering: pixelated; border: 1px solid var(--accent); border-radius: 3px; display: block; }}
.gear {{ width: 14px; height: 14px; border: 1.5px solid var(--text-3); border-radius: 50%; margin-bottom: 8px; }}
.page {{ padding: 18px 22px; display: grid; gap: 12px; align-content: start; }}
.page .line {{ height: 9px; background: var(--raised); border-radius: 3px; }}
.page .card {{ height: 120px; background: var(--raised); border: 1px solid var(--edge); border-radius: 8px; }}
.hover {{ position: absolute; left: 58px; bottom: 34px; width: 200px; display: grid; gap: 4px; background: var(--panel); border: 1px solid var(--edge); border-radius: 8px; padding: 6px; box-shadow: 0 8px 28px rgba(0,0,0,.45); }}
.hover img {{ width: 100%; height: auto; image-rendering: pixelated; border-radius: 4px; display: block; }}
.hover span {{ font-size: 8px; color: var(--text-2); }}
.note {{ font-size: 12px; color: var(--text-3); }}
.options {{ display: grid; grid-template-columns: repeat(auto-fit, minmax(min(100%, 300px), 1fr)); gap: 16px; }}
.opt {{ border-top: 2px solid var(--edge); padding-top: 12px; display: grid; gap: 6px; align-content: start; }}
.opt.rec {{ border-top-color: var(--accent); }}
.opt h3 {{ margin: 0; font-size: 14px; font-weight: 600; }}
.opt p {{ font-size: 13px; }}
@media (prefers-reduced-motion: no-preference) {{ .stamp img {{ transition: box-shadow .2s; }} }}
</style>
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=IBM+Plex+Mono:wght@500;600&family=IBM+Plex+Sans:wght@400;600&display=swap">
<main>
<header>
  <div class="eyebrow">CityBuilderSim · docs/studies/spec-globe.md §8 · illustrative city</div>
  <h1>A small map always in view, and a big city that does not turn into blobs</h1>
  <p>The city below is made up for the study: about a million people on 4.5 by 3 districts (35 by 23 km), a port, a core, rings of homes, industry along the port and the railway. It is drawn with the game's colours and each rule as the study describes it. It is not a save.</p>
</header>

<section>
  <div class="eyebrow">Why it blobs</div>
  <h2>Today a whole district is one colour</h2>
  <p>Far out, the map paints each 7.68 km district one flat tint: the kind of building that takes the most ground in it, stronger the fuller it is (<code>MapTiles.tintOf()</code>). A city of a dozen districts is a dozen squares, whatever the screen's size. The fix is to draw each district from where its buildings actually stand.</p>
  <div class="pair">
    <figure><img src="{img('mm-today-panel.png')}" alt="Today's rule: the city as a few flat squares of orange, beige and purple" width="288" height="192"><figcaption><span class="tag bad">today</span><b>One tint a district.</b> The core, the homes and the port are three squares.</figcaption></figure>
    <figure><img src="{img('mm-thumbs-panel.png')}" alt="District pictures: the core, the rings of homes, the industry along the port and the railway" width="288" height="192"><figcaption><span class="tag good">proposed</span><b>Each district from its own street plan.</b> The offices in the core, the flats round it, the homes thinning out, the industry on the port and the rail line, the highways.</figcaption></figure>
  </div>
</section>

<section>
  <div class="eyebrow">Three rules, three sizes</div>
  <h2>What each rule shows as the map gets smaller</h2>
  <p>Each picture at its real size in pixels. The stamp is what sits in the rail; the panel is the hover preview; the full width is the expanded map at the same zoom.</p>
  <div class="scroll">
  <table class="rules">
    <thead><tr><th></th><th>Stamp · 72 × 48 · 480 m a px</th><th>Panel · 288 × 192 · 120 m a px</th><th>Expanded · 576 × 384 · 60 m a px</th></tr></thead>
    <tbody>
      <tr><th>Today<br><span class="tag bad">one tint a district</span></th><td><img src="{img('mm-today-stamp.png')}" width="72" height="48" alt=""></td><td><img src="{img('mm-today-panel.png')}" width="288" height="192" alt=""></td><td><img src="{img('mm-today-full.png')}" width="576" height="384" alt=""><small>The squares stay squares however close you are, until the map switches to painted tiles.</small></td></tr>
      <tr><th>Shares<br><span class="tag">a district's mix, dithered</span></th><td><img src="{img('mm-shares-stamp.png')}" width="72" height="48" alt=""></td><td><img src="{img('mm-shares-panel.png')}" width="288" height="192" alt=""></td><td><img src="{img('mm-shares-full.png')}" width="576" height="384" alt=""><small>Honest about the mix, blind to where things are: the fallback when a district is only a few pixels.</small></td></tr>
      <tr><th>District pictures<br><span class="tag good">recommended</span></th><td><img src="{img('mm-thumbs-stamp.png')}" width="72" height="48" alt=""></td><td><img src="{img('mm-thumbs-panel.png')}" width="288" height="192" alt=""></td><td><img src="{img('mm-thumbs-full.png')}" width="576" height="384" alt=""><small>Each district a 32 × 32 picture of itself from its plan, kept and redrawn only when it changes; the highways, railway and city's edge always a pixel wide.</small></td></tr>
    </tbody>
  </table>
  </div>
</section>

<section>
  <div class="eyebrow">Where it sits</div>
  <h2>A stamp at the foot of the rail, the map on hover, the full map on click</h2>
  <p>The header already fits the money block and five tiles to a 1,280-pixel window, and anything floating over the page hides what is under it (the income dome once hid the Memorial Cemetery's price). The rail's foot has room under its eleven buttons, covers nothing, and is on screen from every tab.</p>
  <div class="scroll">
  <div class="window" role="img" aria-label="The game window at 0.69 scale with the minimap stamp at the foot of the rail and its hover preview open">
    <div class="hdr">
      <div class="clock">1 June 2304 · ▶ 1×</div>
      <div class="money">D$18.70M</div>
      <div class="tile">Population<b>1,231,880</b><i></i></div>
      <div class="tile">GDP<b>D$4.1B</b><i></i></div>
      <div class="tile">Inflation<b>2.4%</b><i></i></div>
      <div class="tile">Out of work<b>6.1%</b><i></i></div>
      <div class="tile">Rate<b>3.25%</b><i></i></div>
      <div class="chip">Needs you · 3</div>
      <div class="env"></div>
    </div>
    <div class="body">
      <div class="rail">{rail_html}
        <div class="stamp"><img src="{img('mm-thumbs-stamp.png')}" alt="">Map</div>
        <div class="gear"></div>
      </div>
      <div class="page"><div class="line" style="width:30%"></div><div class="line" style="width:55%"></div><div class="card"></div><div class="card"></div></div>
    </div>
    <div class="hover"><img src="{img('mm-thumbs-panel.png')}" alt=""><span>Click for the full map</span></div>
  </div>
  </div>
  <p class="note">The window at 0.69 of 1,389 × 868; the header's figures and the rail's labels are placeholders. The preview shows while the pointer is on the stamp.</p>
  <div class="options">
    <div class="opt rec"><h3>Rail foot (recommended)</h3><p>Always visible, covers nothing, one click from every tab. The stamp is small, so it shows the city's shape and mix; the hover preview shows the detail.</p></div>
    <div class="opt"><h3>A card at the page's bottom left</h3><p>Big enough to read without hovering (288 × 192), collapsible to a chip. It covers the bottom left of whatever page is open, which is where row labels sit.</p></div>
    <div class="opt"><h3>Top of the City overview drawer</h3><p>Opens with the "Needs you" chip and covers nothing, but it is not always in view, which was the point.</p></div>
  </div>
</section>
</main>
'''
open(out, 'w', encoding='utf-8').write(html)
print(len(html))

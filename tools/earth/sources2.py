# earth-2's credits: earth-1's sources (sources.py) with GEBCO's wider use and its own requested credit, and the
# heights the slope comes from (bake2.NEW_SOURCES). Writes tools/earth/ATTRIBUTION-2.txt, shipped beside earth-2.bin
# (the Credits page's text). Every licence below was read from its publisher's own text on 2026-10-12 (the study's
# section 1 quotes each).   python3 -I sources2.py
import sys, os, copy
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import sources as S1
from bake2 import NEW_SOURCES, GEBCO_2

HERE = os.path.dirname(os.path.abspath(__file__))


def main():
    srcs = copy.deepcopy(S1.SOURCES)
    for s in srcs:
        if s['key'] == 'gebco':
            s['credit'] = GEBCO_2['credit']; s['gives'] += '; ' + GEBCO_2['gives_more']
            s['licence'] = ('Public domain; may be copied, published, adapted and commercially exploited; the source must be '
                            'acknowledged; no official status or endorsement may be implied; not for navigation (GEBCO, '
                            '"Terms of use for the GEBCO Grid and derived information products")')
        if s['key'] == 'grwl':
            s['gives'] += '; rivers 420 m wide or more cut the landmasses (earth-2\'s landmass layer)'
        if s['key'] == 'hydrolakes':
            s['gives'] += '; the lakes cut the landmasses'
    srcs = srcs[:1] + NEW_SOURCES + srcs[1:]
    lines = ['earth-2: the Earth data of CityBuilderSim', '',
             'earth-2.bin is derived from the data sets below. Their licences allow this use; their credits follow,',
             'each in the form its publisher asks for. The game shows the same text on its Credits page.', '']
    for s in srcs:
        lines += [s['name'] + ' (' + s['version'] + ')', '  Credit:  ' + s['credit'], '  Licence: ' + s['licence'],
                  '  Used for: ' + s['gives'], '  Source:  ' + ', '.join(s['urls']), '']
    lines += ['Not for navigation. GEBCO\'s grid, and so this file, must not be used for navigation or for any',
              'purpose involving safety at sea. Neither GEBCO, the IHO, the IOC, NASA, ESA, the HydroLAKES or GRWL',
              'authors, the USGS nor Natural Earth endorse this game.', '',
              'Where a source does not reach (marked in the file\'s meta layer as well):']
    lines += ['  - ' + f for f in S1.FALLBACKS]
    lines += ['  - North of 61 N and south of 56 S, and in any tile NASADEM has no heights for, the slope is an estimate',
              '    from GEBCO 2026\'s 15" grid (the layer slope.est marks those tiles).']
    lines += ['', 'No borders are drawn; place names are for search only.', '']
    with open(os.path.join(HERE, 'ATTRIBUTION-2.txt'), 'w', encoding='utf-8') as f:
        f.write('\n'.join(lines))
    print('wrote ATTRIBUTION-2.txt')


if __name__ == '__main__':
    main()

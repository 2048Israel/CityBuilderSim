# earth-2's credits: earth-1's sources (sources.py) with GEBCO's wider use and its own requested credit, and the
# heights the slope comes from (NEW_SOURCES). sources() is the list bake2.py puts in earth-2.bin's meta; main() writes
# tools/earth/ATTRIBUTION-2.txt from it, shipped beside earth-2.bin
# (data/earth-2-ATTRIBUTION.txt in the exe's folder; a Credits page in the game is CR1). Every licence below was read
# from its publisher's own text on 2026-10-12 (the study's section 1 quotes each) and again on 2026-10-31 (LC1,
# docs/studies/licences.md).   python3 -I sources2.py
import sys, os, copy
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import sources as S1

HERE = os.path.dirname(os.path.abspath(__file__))

NEW_SOURCES = [
    dict(key='nasadem', name='NASADEM Merged DEM Global 1 arc second', version='V001',
         urls=['https://doi.org/10.5067/MEaSUREs/NASADEM/NASADEM_HGT.001',
               'https://planetarycomputer.microsoft.com/dataset/nasadem (the copy read)'],
         licence='No restriction (NASA EOSDIS Data Use Guidance: data from a NASA-led mission without a marked licence are '
                 'CC0 1.0; the NASADEM_HGT page: "openly shared, without restriction")',
         credit='NASA JPL (2020). NASADEM Merged DEM Global 1 arc second V001. NASA EOSDIS Land Processes DAAC. '
                'https://doi.org/10.5067/MEaSUREs/NASADEM/NASADEM_HGT.001',
         gives='the slope of the ground from 61 N to 56 S (NASADEM\'s row of tiles at 60-61 N is its own fill): heights averaged to 3" (90 m), the slope by Horn\'s method, '
               'each 15" cell\'s share of land at or above each threshold'),
    dict(key='srtmgl1', name='NASA Shuttle Radar Topography Mission Global 1 arc second (SRTMGL1)', version='V003',
         urls=['https://doi.org/10.5067/MEaSUREs/SRTM/SRTMGL1.003', 'https://step.esa.int/auxdata/dem/SRTMGL1/ (the copy read)'],
         licence='No restriction (NASA EOSDIS Data Use Guidance, CC0 1.0)',
         credit='NASA JPL (2013). NASA Shuttle Radar Topography Mission Global 1 arc second. NASA EOSDIS Land Processes '
                'DAAC. https://doi.org/10.5067/MEaSUREs/SRTM/SRTMGL1.003',
         gives='the same, for any tile NASADEM\'s copy lacks'),
]
GEBCO_2 = dict(credit='GEBCO Bathymetric Compilation Group 2026 (2026). The GEBCO_2026 Grid - a continuous terrain model '
                      'for oceans and land at 15 arc-second intervals. NERC EDS British Oceanographic Data Centre NOC. '
                      'doi:10.5285/4f68d5c7-45eb-f999-e063-7086abc036fa',
               gives_more='the sea\'s depth (1\') and its shallow share (15"); the slope of the ground where NASADEM does not '
                          'reach (north of 61 N, south of 56 S), as an estimate from its 15" slope; which sea a short '
                          'crossing may span (the landmasses)')




def sources():
    """earth-1's sources as earth-2 uses them, and the new ones: the list ATTRIBUTION-2.txt and earth-2.bin's meta print."""
    srcs = copy.deepcopy(S1.SOURCES)
    for s in srcs:
        if s['key'] == 'gebco':
            s['credit'] = GEBCO_2['credit']; s['gives'] += '; ' + GEBCO_2['gives_more']
            s['licence'] = ('Public domain; may be copied, published, adapted and commercially exploited; the source must be '
                            'acknowledged; no official status or endorsement may be implied; not for navigation (GEBCO, '
                            '"Terms of use for the GEBCO Grid and derived information products"); the copy read, at CEDA, is '
                            'under the Open Government Licence v3.0: contains public sector information licensed under the '
                            'Open Government Licence v3.0 (https://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/)')
        if s['key'] == 'grwl':
            s['gives'] += '; rivers wider than 1,020 m cut the landmasses (earth-2\'s landmass layer)'
        if s['key'] == 'hydrolakes':
            s['gives'] += '; the lakes are water a short crossing may span or not (the landmasses)'
    return srcs[:1] + NEW_SOURCES + srcs[1:]


def main():
    srcs = sources()
    lines = ['earth-2: the Earth data of CityBuilderSim', '',
             'earth-2.bin is derived from the data sets below. Their licences allow this use; their credits follow,',
             'each in the form its publisher asks for. It ships beside the exe as data/earth-2-ATTRIBUTION.txt.', '']
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
    with open(os.path.join(HERE, 'ATTRIBUTION-2.txt'), 'w', encoding='utf-8', newline='\n') as f:   # LF, as the repository stores it
        f.write('\n'.join(lines))
    print('wrote ATTRIBUTION-2.txt')


if __name__ == '__main__':
    main()

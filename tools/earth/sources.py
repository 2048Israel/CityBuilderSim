# The sources of earth-1: where each comes from, its licence and its credit. Writes out/sources.json (also
# embedded in earth-1.bin's meta layer) and out/ATTRIBUTION.txt (shipped beside the file, and the Credits page's text).
# Every credit line below is the source's own requested form (read 2026-10-08 from the file or page named).
import sys, os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from common import *

SOURCES = [
    dict(key='gebco', name='GEBCO 2026 Grid (ice-surface elevation) and Type Identifier (TID) grid', version='GEBCO_2026',
         urls=['https://dap.ceda.ac.uk/bodc/gebco/global/gebco_2026/type_identifier_grid/netcdf/gebco_2026_tid.zip',
               'https://dap.ceda.ac.uk/bodc/gebco/global/gebco_2026/ice_surface_elevation/netcdf/GEBCO_2026.nc'],
         licence='Public domain; may be copied, adapted and used commercially; the source must be acknowledged; '
                 'not to be used for navigation (GEBCO Grid terms of use, GEBCO_Grid_terms_of_use.pdf)',
         credit='GEBCO Compilation Group (2026) GEBCO 2026 Grid (doi:10.5285/4f68d5c7-45eb-f999-e063-7086abc036fa)',
         gives='which 15" cells are ocean (TID != 0; grown by one cell, it tells sea from inland water); '
               'the coast where WorldCover has no tile; Antarctica south of 60 S (elevation >= 0, read by range)'),
    dict(key='worldcover', name='ESA WorldCover 10 m 2021 v200', version='v200 (2021)',
         urls=['https://esa-worldcover.s3.eu-central-1.amazonaws.com/v200/2021/map/'],
         licence='CC BY 4.0 (https://creativecommons.org/licenses/by/4.0/)',
         credit='© ESA WorldCover project 2021 / Contains modified Copernicus Sentinel data (2021) processed by '
                'ESA WorldCover consortium. Zanaga, D. et al. (2022) ESA WorldCover 10 m 2021 v200. '
                'https://doi.org/10.5281/zenodo.7254221',
         gives='the sea share of coastal 15" cells (water at 10 m); forest, bare ground and snow and ice at 2\' '
               '(from the 40 m overview)'),
    dict(key='hydrolakes', name='HydroLAKES v1.0 (polygons)', version='1.0',
         urls=['https://data.hydrosheds.org/file/hydrolakes/HydroLAKES_polys_v10_shp.zip'],
         licence='CC BY 4.0 (HydroLAKES Technical Documentation v1.0, section 4.1); the authors ask that the data '
                 'not be redistributed whole in its original format: earth-1 holds only derived shares and points',
         credit='Messager, M.L., Lehner, B., Grill, G., Nedeva, I., Schmitt, O. (2016): Estimating the volume and age '
                'of water stored in global lakes using a geo-statistical approach. Nature Communications: 13603. '
                'doi:10.1038/ncomms13603. Data is available at www.hydrosheds.org.',
         gives='lakes of 10 km2 or more as 15" shares; smaller lakes (10 ha to 10 km2) as a point and an area'),
    dict(key='grwl', name='Global River Widths from Landsat (GRWL) Database, vector product', version='V01.01',
         urls=['https://zenodo.org/records/1297434/files/GRWL_vector_V01.01.zip'],
         licence='CC BY 4.0 (Zenodo record 1297434)',
         credit='Allen, G.H. and Pavelsky, T.M. (2018) Global extent of rivers and streams. Science 361(6402), '
                '585-588. doi:10.1126/science.aat0636. Data: GRWL Database V01.01, doi:10.5281/zenodo.1297434',
         gives='river centrelines (rivers 30 m or wider) with their width, simplified to 30 m'),
    dict(key='usgs', name='Geologic Provinces of the World, 2000 World Petroleum Assessment (WEP_PRVG)', version='2000',
         urls=['https://doi.org/10.5066/P9RMSU2R'],
         licence='Public domain (U.S. Geological Survey); use constraints: None (WEP_PRVG.xml)',
         credit='Osmonson, L.M., Persits, F.M., Steinshouer, D.W., Klett, T.R. (2000) Geologic Provinces of the World, '
                '2000 World Petroleum Assessment, U.S. Geological Survey, https://doi.org/10.5066/P9RMSU2R',
         gives='the geologic province of each 0.25 degree cell, its class (by name) and known petroleum'),
    dict(key='naturalearth', name='Natural Earth 1:10m Populated Places (simple)', version='5.1.2',
         urls=['https://www.naturalearthdata.com/downloads/10m-cultural-vectors/10m-populated-places/'],
         licence='Public domain (naturalearthdata.com/about/terms-of-use)',
         credit='Made with Natural Earth. Free vector and raster map data @ naturalearthdata.com.',
         gives='7,342 place names and positions, for search'),
]

FALLBACKS = [
    'South of 60 S (Antarctica and its islands; 10,800 1-degree tiles) ESA WorldCover has no data: the coast there is '
    'GEBCO 2026\'s elevation >= 0 as a whole-cell mask (no refinement), and land cover is marked "no WorldCover data" '
    'with the ice bit set.',
    'The 30,141 1-degree tiles north of 60 S with no WorldCover tile (open sea, and 17,454 15" cells GEBCO calls land) '
    'take GEBCO\'s TID mask (ocean or not) for their coast; land there is marked "no WorldCover data".',
]


def main():
    os.makedirs(OUT, exist_ok=True)
    write_json(OUT + '/sources.json', dict(sources=SOURCES, fallbacks=FALLBACKS))
    lines = ['earth-1: the Earth data of CityBuilderSim', '',
             'earth-1.bin is derived from the data sets below. Their licences allow this use; their credits follow,',
             'each in the form its publisher asks for. The game shows the same text on its Credits page.', '']
    for s in SOURCES:
        lines += [s['name'] + ' (' + s['version'] + ')', '  Credit:  ' + s['credit'], '  Licence: ' + s['licence'],
                  '  Used for: ' + s['gives'], '  Source:  ' + ', '.join(s['urls']), '']
    lines += ['Not for navigation. GEBCO\'s grid, and so this file, must not be used for navigation or for any',
              'purpose involving safety at sea. Neither GEBCO, the IHO, the IOC, ESA, the HydroLAKES or GRWL authors,',
              'the USGS nor Natural Earth endorse this game.', '',
              'Where a source does not reach (marked in the file\'s meta layer as well):']
    lines += ['  - ' + f for f in FALLBACKS]
    lines += ['', 'No borders are drawn; place names are for search only.', '']
    with open(OUT + '/ATTRIBUTION.txt', 'w', encoding='utf-8') as f:
        f.write('\n'.join(lines))
    log('sources', 'wrote sources.json and ATTRIBUTION.txt')


if __name__ == '__main__':
    main()

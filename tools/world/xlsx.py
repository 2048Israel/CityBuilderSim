"""A workbook's sheet as rows of strings, by the standard library alone (zipfile and ElementTree): the world
pipeline reads CERDI's distances, the Penn World Table and the Maddison Project from .xlsx downloads.

WHY: the pipeline installs nothing (WN0's terms), and these three ship as workbooks.
"""
import zipfile
import xml.etree.ElementTree as ET

NS = '{http://schemas.openxmlformats.org/spreadsheetml/2006/main}'
REL = '{http://schemas.openxmlformats.org/officeDocument/2006/relationships}id'


def _col(ref):
    """A cell reference's column, 0-based: 'C12' is 2."""
    n = 0
    for ch in ref:
        if not ch.isalpha():
            break
        n = n * 26 + ord(ch.upper()) - 64
    return n - 1


def sheets(path):
    """The workbook's sheet names, in order."""
    with zipfile.ZipFile(path) as z:
        wb = ET.fromstring(z.read('xl/workbook.xml'))
        return [s.get('name') for s in wb.iter(NS + 'sheet')]


def rows(path, sheet=None):
    """Yields each row of a sheet (the first when none is named) as a list of strings, '' for an empty cell."""
    with zipfile.ZipFile(path) as z:
        names = z.namelist()
        wb = ET.fromstring(z.read('xl/workbook.xml'))
        rels = ET.fromstring(z.read('xl/_rels/workbook.xml.rels'))
        target = {r.get('Id'): r.get('Target') for r in rels}
        part = None
        for s in wb.iter(NS + 'sheet'):
            if sheet is None or s.get('name') == sheet:
                part = target[s.get(REL)]
                break
        if part is None:
            raise KeyError('no sheet ' + str(sheet) + ' in ' + path)
        part = part.lstrip('/')
        if not part.startswith('xl/'):
            part = 'xl/' + part
        strings = []
        if 'xl/sharedStrings.xml' in names:
            for si in ET.fromstring(z.read('xl/sharedStrings.xml')).iter(NS + 'si'):
                strings.append(''.join(t.text or '' for t in si.iter(NS + 't')))
        with z.open(part) as f:
            for _, el in ET.iterparse(f):
                if el.tag != NS + 'row':
                    continue
                out = []
                for c in el.iter(NS + 'c'):
                    i = _col(c.get('r', ''))
                    v = c.find(NS + 'v')
                    t = c.get('t')
                    if t == 'inlineStr':
                        val = ''.join(x.text or '' for x in c.iter(NS + 't'))
                    else:
                        val = v.text if v is not None and v.text is not None else ''
                        if t == 's' and val:
                            val = strings[int(val)]
                    while len(out) < i:
                        out.append('')
                    out.append(val)
                el.clear()
                yield out

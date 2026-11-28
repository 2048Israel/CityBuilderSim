# Reads ui/Icons.java into lib/icons.js for the mockups (UX1): python icons2js.py <Icons.java> <icons.js>
import re, sys, json
src = open(sys.argv[1], encoding='utf-8').read()
out = {}
for m in re.finditer(r'public static final String ([A-Z_0-9]+)\s*=(.*?);', src, re.S):
    lits = re.findall(r'"([^"]*)"', m.group(2))
    if lits:
        out[m.group(1)] = ''.join(lits)
with open(sys.argv[2], 'w', encoding='utf-8', newline='\n') as f:
    f.write("// The game's outline icons, read from ui/Icons.java (Lucide, ISC; 24x24, a 2px stroke): GENERATED, UX1.\n")
    f.write('const ICON = ' + json.dumps(out, indent=0) + ';\n')
print(len(out))

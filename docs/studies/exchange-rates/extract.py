import json, sys
d = json.load(open(sys.argv[1], encoding='utf-8'))
cols = ['fxRate','fxParity','priceIndex','policyRate','expectedInflation','credibility','currentAccount','exportsAbroad','importsAbroad','reservesUsd','gdp','indexGroceries','indexRent','weightRent','indexLuxury','indexMeals','indexServices','weightGroceries','weightLuxury','weightMeals','weightServices']
n = min(len(d[c]) for c in cols)
with open(sys.argv[2], 'w', newline='\n') as f:
    f.write('month,' + ','.join(cols) + '\n')
    for i in range(n):
        f.write(str(i) + ',' + ','.join(repr(float(d[c][i])) for c in cols) + '\n')
print(n, 'rows')

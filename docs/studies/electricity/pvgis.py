"""PW3: a solar farm's yield by latitude band, from PVGIS 5.3 (the European Commission's JRC).

Run: python -I docs/studies/electricity/pvgis.py > docs/studies/electricity/out/pvgis.txt
It asks PVGIS's PVcalc for each place below (the ERA5 radiation database, which covers the globe; a 1 kWp c-Si
system, PVGIS's default 14% losses), twice: a fixed rack facing the equator at PVGIS's optimal tilt (its optimiser of
tilt and azimuth together returns an 89-degree rack facing the pole near the equator: Nairobi 573 kWh/kWp; facing the
equator, 1,545), and a tracker on a horizontal north-south
axis (PVcalc's inclined axis at 0 degrees; PVcalc ignores trackingtype: the utility tracker, research E25). A place south
of the equator has its months turned six onto the north's calendar before it joins its band, so a band's month is
the hemisphere's own season. Each band (10 degrees wide, centred on 0, 10, ... 70) averages its places' annual yield
(kWh a kWp a year) and their monthly yields; the monthly shape is each band's month over its twelve months' mean.
The US reference is the mean of seven US places between 30 and 36 degrees north, where EIA's fleet stands.

Source: PVGIS (c) European Communities, 2001-2025, https://re.jrc.ec.europa.eu/pvg_tools/; content of the
Commission's websites is CC BY 4.0 unless otherwise indicated (https://commission.europa.eu/legal-notice_en, the
notice PVGIS's pages link). Changes: the places' figures averaged into bands and normalised, below.
"""
import json
import time
import urllib.request

PLACES = [
    # band 0
    ("Singapore", 1.35, 103.82), ("Quito", -0.18, -78.47), ("Nairobi", -1.29, 36.82), ("Kinshasa", -4.32, 15.31),
    ("Manaus", -3.12, -60.02), ("Kampala", 0.35, 32.58), ("Pontianak", -0.03, 109.33), ("Libreville", 0.42, 9.47),
    # band 10
    ("Lagos", 6.52, 3.38), ("Bogota", 4.71, -74.07), ("Colombo", 6.93, 79.85), ("Addis Ababa", 9.03, 38.74),
    ("Caracas", 10.49, -66.88), ("Ho Chi Minh City", 10.82, 106.63), ("Chennai", 13.08, 80.27), ("Abuja", 9.08, 7.40),
    ("Dakar", 14.72, -17.47), ("Luanda", -8.84, 13.23), ("Lima", -12.05, -77.04), ("Jakarta", -6.20, 106.85),
    ("Dar es Salaam", -6.79, 39.21), ("Recife", -8.05, -34.88), ("Darwin", -12.46, 130.84),
    # band 20
    ("Mexico City", 19.43, -99.13), ("Mumbai", 19.08, 72.88), ("Hong Kong", 22.32, 114.17), ("Khartoum", 15.50, 32.56),
    ("Riyadh", 24.71, 46.68), ("Havana", 23.11, -82.37), ("Hanoi", 21.03, 105.85), ("Lusaka", -15.39, 28.32),
    ("Brasilia", -15.79, -47.88), ("Rio de Janeiro", -22.91, -43.17), ("Antananarivo", -18.88, 47.51),
    ("Windhoek", -22.56, 17.08), ("Alice Springs", -23.70, 133.88), ("Townsville", -19.26, 146.82),
    # band 30
    ("Cairo", 30.04, 31.24), ("Shanghai", 31.23, 121.47), ("Lahore", 31.55, 74.34), ("Houston", 29.76, -95.37),
    ("Phoenix", 33.45, -112.07), ("Casablanca", 33.57, -7.59), ("Baghdad", 33.31, 44.37), ("Los Angeles", 34.05, -118.24),
    ("Delhi", 28.61, 77.21), ("Johannesburg", -26.20, 28.05), ("Perth", -31.95, 115.86), ("Santiago", -33.45, -70.67),
    ("Sydney", -33.87, 151.21), ("Cape Town", -33.92, 18.42), ("Montevideo", -34.90, -56.16), ("Cordoba", -31.42, -64.18),
    ("Durban", -29.86, 31.02), ("Brisbane", -27.47, 153.03), ("Buenos Aires", -34.60, -58.38),
    # band 40
    ("Tokyo", 35.68, 139.69), ("Tehran", 35.69, 51.39), ("Madrid", 40.42, -3.70), ("Beijing", 39.90, 116.41),
    ("New York", 40.71, -74.01), ("Denver", 39.74, -104.99), ("Istanbul", 41.01, 28.98), ("Rome", 41.90, 12.50),
    ("Tashkent", 41.30, 69.24), ("Seoul", 37.57, 126.98), ("Salt Lake City", 40.76, -111.89), ("Athens", 37.98, 23.73),
    ("Melbourne", -37.81, 144.96), ("Auckland", -36.85, 174.76), ("Hobart", -42.88, 147.33),
    ("Christchurch", -43.53, 172.64), ("Bahia Blanca", -38.72, -62.27),
    # band 50
    ("Paris", 48.86, 2.35), ("Berlin", 52.52, 13.40), ("London", 51.51, -0.13), ("Warsaw", 52.23, 21.01),
    ("Kyiv", 50.45, 30.52), ("Seattle", 47.61, -122.33), ("Montreal", 45.50, -73.57), ("Vancouver", 49.28, -123.12),
    ("Winnipeg", 49.90, -97.14), ("Harbin", 45.80, 126.53), ("Astana", 51.17, 71.43), ("Ulaanbaatar", 47.89, 106.91),
    ("Amsterdam", 52.37, 4.90), ("Punta Arenas", -53.16, -70.92), ("Comodoro Rivadavia", -45.86, -67.48),
    ("Dunedin", -45.87, 170.50),
    # band 60
    ("Moscow", 55.76, 37.62), ("Stockholm", 59.33, 18.07), ("Oslo", 59.91, 10.75), ("Helsinki", 60.17, 24.94),
    ("St Petersburg", 59.93, 30.36), ("Anchorage", 61.22, -149.90), ("Edinburgh", 55.95, -3.19),
    ("Novosibirsk", 55.03, 82.92), ("Yakutsk", 62.03, 129.73), ("Whitehorse", 60.72, -135.06), ("Bergen", 60.39, 5.32),
    ("Fairbanks", 64.84, -147.72), ("Reykjavik", 64.15, -21.94), ("Nuuk", 64.18, -51.72), ("Ushuaia", -54.80, -68.30),
    # band 70
    ("Tromso", 69.65, 18.96), ("Murmansk", 68.97, 33.08), ("Norilsk", 69.35, 88.20), ("Utqiagvik", 71.29, -156.79),
    ("Rovaniemi", 66.50, 25.73), ("Vorkuta", 67.50, 64.05), ("Inuvik", 68.36, -133.72),
]

US_REFERENCE = [("Bakersfield", 35.37, -119.02), ("Albuquerque", 35.08, -106.65), ("Phoenix", 33.45, -112.07),
                ("Dallas", 32.78, -96.80), ("Oklahoma City", 35.47, -97.52), ("Atlanta", 33.75, -84.39),
                ("Charlotte", 35.23, -80.84)]

API = "https://re.jrc.ec.europa.eu/api/v5_3/PVcalc?lat={lat}&lon={lon}&peakpower=1&loss=14&raddatabase=PVGIS-ERA5&outputformat=json"


def ask(lat, lon, tracked):
    try:
        return ask_once(lat, lon, "&fixed=0&inclined_axis=1&inclinedaxisangle=0" if tracked
                        else f"&optimalinclination=1&aspect={0 if lat >= 0 else 180}")
    except Exception:
        if tracked:
            raise
        return ask_once(lat, lon, f"&angle={abs(lat):.0f}&aspect={0 if lat >= 0 else 180}")


def ask_once(lat, lon, mount):
    url = API.format(lat=lat, lon=lon) + mount
    for attempt in range(3):
        try:
            with urllib.request.urlopen(url, timeout=60) as r:
                data = json.load(r)
            out = data["outputs"]
            key = next(iter(out["monthly"]))
            months = [m["E_m"] for m in out["monthly"][key]]
            return out["totals"][key]["E_y"], months
        except Exception as e:  # PVGIS asks for no more than 30 calls a second; wait and retry
            last = e
            if getattr(e, "code", None) == 500:
                break
            time.sleep(2 + attempt * 3)
    raise last


def northern(lat, months):
    return months if lat >= 0 else months[6:] + months[:6]


def main():
    bands = {}
    print("place | lat | lon | fixed kWh/kWp | tracked kWh/kWp")
    for name, lat, lon in PLACES:
        fy, fm = ask(lat, lon, False)
        ty, tm = ask(lat, lon, True)
        print(f"{name} | {lat} | {lon} | {fy:.0f} | {ty:.0f}", flush=True)
        band = int(min(70, 10 * round(abs(lat) / 10)))
        bands.setdefault(band, []).append((fy, ty, northern(lat, fm), northern(lat, tm)))
        time.sleep(0.2)
    ref = []
    for name, lat, lon in US_REFERENCE:
        fy, _ = ask(lat, lon, False)
        ty, _ = ask(lat, lon, True)
        ref.append((fy, ty))
        print(f"US reference {name} | {lat} | {lon} | {fy:.0f} | {ty:.0f}")
    rf = sum(r[0] for r in ref) / len(ref)
    rt = sum(r[1] for r in ref) / len(ref)
    print(f"\nUS reference (mean of {len(ref)}): fixed {rf:.1f}, tracked {rt:.1f} kWh/kWp a year")
    print(f"US reference fixed over tracked: {rf / rt:.5f}")
    print("\nband | places | fixed kWh/kWp | tracked kWh/kWp | fixed/ref tracked | tracked/ref tracked")
    shapes = []
    for band in sorted(bands):
        rows = bands[band]
        n = len(rows)
        fy = sum(r[0] for r in rows) / n
        ty = sum(r[1] for r in rows) / n
        print(f"{band} | {n} | {fy:.1f} | {ty:.1f} | {fy / rt:.5f} | {ty / rt:.5f}")
        for label, k in (("fixed", 2), ("tracked", 3)):
            m = [sum(r[k][i] for r in rows) / n for i in range(12)]
            mean = sum(m) / 12
            shapes.append(f"{band} {label} | " + ", ".join(f"{x / mean:.3f}" for x in m))
    print("\nband mount | month shape, Jan..Dec on the north's calendar (each month over the twelve's mean)")
    for line in shapes:
        print(line)


if __name__ == "__main__":
    main()

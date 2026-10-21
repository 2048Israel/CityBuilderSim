"""Builds the study's boards: each src-N-*.html with its {{NAME}} images embedded as data URIs and {{BORDERS_SVG}} as polylines from world-borders.txt, written as N-*.html, so a board is one file.
Run from this folder: python build-pages.py"""
import base64, glob, os, re
IMAGES = {"BASE": "world-base.png"}
for src in sorted(glob.glob("src-*.html")):
    html = open(src, encoding="utf-8").read()
    for key, name in IMAGES.items():
        if "{{" + key + "}}" in html:
            data = base64.b64encode(open(name, "rb").read()).decode("ascii")
            html = html.replace("{{" + key + "}}", "data:image/png;base64," + data)
    if "{{BORDERS_SVG}}" in html:
        lines = [l.strip() for l in open("world-borders.txt", encoding="utf-8") if l.strip()]
        html = html.replace("{{BORDERS_SVG}}", "".join('<polyline points="%s"/>' % l for l in lines))
    out = src[4:]
    open(out, "w", encoding="utf-8", newline="\n").write(html)
    print(out, len(html))

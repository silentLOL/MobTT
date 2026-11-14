"""Builds the Play Store graphics from raw emulator screenshots (see store/google-play/README.md).

usage: compose.py <raw-en-dir> <raw-de-dir>   icon, feature graphics and phone screenshots (en-US, de-DE)
       compose.py --feature-graphics          only the feature graphic of every locale in TAGLINE
Needs Pillow and rsvg-convert. Raw shots are 1080x2424 phone screenshots named 1-home ... 6-settings.
"""
import subprocess, sys
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[3]
OUT = ROOT / "store/google-play"
FONT = ROOT / "android/core/localization/src/main/res/font"
RED_TOP, RED_BOT = (218, 41, 28), (163, 24, 14)
CAPTIONS = {
    "en-US": {"1-home": "Your Club and Favorites on Home", "2-club": "Teams and next Fixtures at a glance",
              "3-league": "League tables, your Team highlighted", "4-player": "Rating history as a chart",
              "6-settings": "Light, dark, English or German"},
    "de-DE": {"1-home": "Dein Verein und Favoriten am Start", "2-club": "Mannschaften und nächste Begegnungen",
              "3-league": "Ligatabellen, deine Mannschaft markiert", "4-player": "Ratingverlauf als Diagramm",
              "6-settings": "Hell, dunkel, Deutsch oder Englisch"},
}
TAGLINE = {"en-US": "Table tennis results, in your pocket", "de-DE": "Tischtennis-Ergebnisse für die Hosentasche",
           "hr": "Rezultati stolnog tenisa u tvom džepu", "tr-TR": "Masa tenisi sonuçları cebinde",
           "ro": "Rezultate de tenis de masă, în buzunarul tău", "hu-HU": "Asztalitenisz-eredmények a zsebedben",
           "uk": "Результати настільного тенісу в кишені"}
NUMBERS = {"1-home": 1, "2-club": 2, "3-league": 3, "4-player": 4, "6-settings": 5}


def font(name, size):
    return ImageFont.truetype(str(FONT / f"inter_{name}.ttf"), size)


def gradient(w, h):
    img = Image.new("RGB", (w, h))
    px = img.load()
    for y in range(h):
        for x in range(w):
            t = (x / w + y / h) / 2
            px[x, y] = tuple(round(a + (b - a) * t) for a, b in zip(RED_TOP, RED_BOT))
    return img


def wrap(draw, text, fnt, width):
    lines, cur = [], ""
    for word in text.split():
        trial = f"{cur} {word}".strip()
        if draw.textlength(trial, font=fnt) <= width:
            cur = trial
        else:
            lines.append(cur)
            cur = word
    return lines + [cur]


def phone(raw, caption, dest):
    w, h = 1242, 2208  # 9:16, inside Play's 320..3840 px and 2:1 limits
    canvas = gradient(w, h)
    d = ImageDraw.Draw(canvas)
    f = font("bold", 76)
    y = 130
    for line in wrap(d, caption, f, w - 200):
        d.text((w // 2, y), line, font=f, fill="white", anchor="ma")
        y += 96
    shot = Image.open(raw).convert("RGB")
    th = 1700
    tw = round(shot.width * th / shot.height)
    shot = shot.resize((tw, th), Image.LANCZOS)
    mask = Image.new("L", shot.size, 0)
    ImageDraw.Draw(mask).rounded_rectangle((0, 0, tw, th + 120), radius=70, fill=255)
    canvas.paste(shot, ((w - tw) // 2, 400), mask)
    canvas.save(dest)


def feature(locale):
    w, h = 1024, 500
    canvas = gradient(w, h)
    subprocess.run(["rsvg-convert", "-w", "340", "-h", "340", str(ROOT / "docs/brand/launch-mark.svg"), "-o", str(OUT / "tools/.mark.png")], check=True)
    canvas.paste(Image.open(str(OUT / "tools/.mark.png")).convert("RGBA"), (50, 80), Image.open(str(OUT / "tools/.mark.png")).convert("RGBA"))
    d = ImageDraw.Draw(canvas)
    d.text((400, 170), "MobTT", font=font("extrabold", 120), fill="white")
    f = font("semibold", 34)
    y = 320
    for line in wrap(d, TAGLINE[locale], f, 560):
        d.text((404, y), line, font=f, fill=(255, 255, 255))
        y += 46
    canvas.save(OUT / locale / "graphics/feature-graphic.png")


def main(raw_en, raw_de):
    subprocess.run(["rsvg-convert", "-w", "512", "-h", "512", str(ROOT / "docs/brand/app-icon.svg"),
                    "-o", str(OUT / "icon/icon-512.png")], check=True)
    for locale, raw in (("en-US", raw_en), ("de-DE", raw_de)):
        (OUT / locale / "graphics/phone").mkdir(parents=True, exist_ok=True)
        feature(locale)
        for key, caption in CAPTIONS[locale].items():
            phone(Path(raw) / f"{key}.png", caption, OUT / locale / f"graphics/phone/{NUMBERS[key]:02d}-{key.split('-')[1]}.png")


if sys.argv[1] == "--feature-graphics":
    for locale in TAGLINE:
        (OUT / locale / "graphics").mkdir(parents=True, exist_ok=True)
        feature(locale)
else:
    main(sys.argv[1], sys.argv[2])

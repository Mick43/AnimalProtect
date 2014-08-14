AnimalProtect
=============

Bietet normalen Spielern auf einem Server an, Tiere vor anderen Spielern zu protecten.

Commands:
---------
- `/ap <command>` - Übersicht aller AnimalProtect-Kommandos
- `/lockanimal` - Sichert das ausgewählte Tier vor anderen Spielern.
- `/unlockanimal`- Entsichert das ausgewählte Tier.
- `/animalinfo` - Gibt Informationen über das ausgewählte Tier aus.
- `/listanimals` - Listet alle gesicherten Tiere eines Spielers auf.
- `/respawnanimal` - Respawnt das angegebene Tier.
- `/tpanimal` - Teleportiert den Sender zu dem angegebenen Tier.
- `/animaldebug` - Gibt Debug-Informationen über das Plugin aus.
- `/lockedanimals` - Gibt die Anzahl der Locks eines Spielers an.

Datenbank-Struktur:
---------
 ## |         Name       |      Typ     | Null | Standard
----|--------------------|--------------|------|----------
 01 | id                 | int(11)      | Nein | kein(e)
 02 | owner              | int(11)      | Nein | kein(e)
 03 | animaltype         | enum(..)     | Nein | kein(e)
 04 | last_x             | smallint(5)  | Nein | kein(e)
 05 | last_y             | smallint(3)  | Nein | kein(e)
 06 | last_z             | smallint(5)  | Nein | kein(e)
 07 | alive              | tinyint(1)   | Nein | kein(e)
 08 | nametag            | varchar(255) | Nein | kein(e)
 09 | maxhp              | double       | Nein | kein(e)
 10 | deathcause         | enum(..)     | Nein | kein(e)
 11 | color              | varchar(40)  | Nein | kein(e)
 12 | armor              | enum(..)     | Nein | kein(e)
 13 | horse_jumpstrength | double       | Nein | kein(e)
 14 | horse_style        | enum(..)     | Nein | kein(e)
 15 | horse_variant      | enum(..)     | Nein | kein(e)
 16 | uuid               | char(36)     | Nein | kein(e)
 17 | created_at         | timestamp    | Nein | CURRENT_TIMESTAMP

AnimalProtect
=============

Description:



Todo:
------
- Leer

Ideen:
------
- Bei jedem Pferde-Inventar-Event mit der Prism-API eine costum Action in die Queue schreiben.
(Somit werden Entity-Inventar-Sachen wieder geloggt und Prism muss nicht die DB mit item-inserts vollspammen)
- Wenn keine Verbindung zur Datenbank besteht, evtl. die Querys in eine Datei schreiben, die dann beim
  nächsten Start gelesen wird und bei der dann alle Querys ausgeführt werden.

Datenbank-Struktur:
---------
- ap_owners: `id`, `name`
- ap_locks: `id`, `entity_id`, `owner_id`
- ap_entities: `id`, `uuid`, `last_x`, `last_y`, `last_z`, `animaltype`, `nametag`, `maxhp`, `alive`, `deathcause`, `color`, `armor`, `horse_jumpstrength`, `horse_style`, `horse_variant`

Commands:
---------
- `/ap <command>` - Eine �bersicht aller AnimalProtect-Befehle
- `/lockanimal` - Protectet das ausgewählte Entity.
- `/unlockanimal` - Entfernt die Protection des ausgewählten Entities. (TODO!) (Optional: <name><id>)
- `/lockinfo` - Gibt Informationen über das ausgewählte Entity aus.
- `/locklist <name>` - Listet alle gelockten Tiere eines Spielers auf. (Optional: <name>)
- `/locklimit <name>` - Sagt dir, wie viele Spieler du noch locken kannst. (Optional: <name>)
- `/locktp <id> <owner>` - Teleportiert dich zu dem angegebenen Entity. (Optional: <owner>)
- `/lockrespawn <id> <owner>` - Respawnt ein Entity in der Welt neu. (Optional: <owner>)
- `/lockdebug` - Gibt DEBUG-Informationen über das Plugin selber aus.

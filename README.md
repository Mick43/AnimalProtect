AnimalProtect
=============

Description:



Todo:
------
- Command: /locklist - In dem neuen Package schreiben.
- Command: /locktp - In dem neuen Package schreiben.
- Command: /lockrespawn - In dem neuen Package schreiben.
- EntityList.disconnect() - schreiben
- Datenbank-Struktur verbessern.

Ideen:
------
- Bei jedem Pferde-Inventar-Event mit der Prism-API eine costum Action in die Queue schreiben.
(Somit werden Entity-Inventar-Sachen wieder geloggt und Prism muss nicht die DB mit item-inserts vollspammen)
- Wenn keine Verbindung zur Datenbank besteht, evtl. die Querys in eine Datei schreiben, die dann beim
  nächsten Start gelesen wird und bei der dann alle Querys ausgeführt werden.

Commands:
---------
`/lockanimal` - Protectet das ausgewählte Entity.
`/lockinfo` - Gibt Informationen über das ausgewählte Entity aus.
`/locklist <name>` - Listet alle gelockten Tiere eines Spielers auf. (Optional: <name>)
`/locklimit <name>` - Sagt dir, wie viele Spieler du noch locken kannst. (Optional: <name>)
`/locktp <id> <owner>` - Teleportiert dich zu dem angegebenen Entity. (Optional: <owner>)
`/lockrespawn <id> <owner>` - Respawnt ein Entity in der Welt neu. (Optional: <owner>)
`/lockdebug` - Gibt DEBUG-Informationen über das Plugin selber aus.

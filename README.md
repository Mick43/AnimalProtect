AnimalProtect
=============

Description:



Todo:
- Command: /locklist - In dem neuen Package schreiben.
- Command: /locktp - In dem neuen Package schreiben.
- Command: /lockrespawn - In dem neuen Package schreiben.
- EntityList.disconnect() - schreiben
- Datenbank-Struktur verbessern.

Ideen:
- Bei jedem Pferde-Inventar-Event mit der Prism-API eine costum Action in die Queue schreiben.
(Somit werden Entity-Inventar-Sachen wieder geloggt und Prism muss nicht die DB mit item-inserts vollspammen)
- Wenn keine Verbindung zur Datenbank besteht, evtl. die Querys in eine Datei schreiben, die dann beim
  nächsten Start gelesen wird und bei der dann alle Querys ausgeführt werden.

AnimalProtect
=============

Description:



Todo:
- Command: /locklist - verbessern: Mehrere Seiten haben, /locklist <name> adden.
- Command: /locktp <owner> <id> - Teleportiert sich zu einem gelockten Tier [AdminBefehl]
- Command: /lockrespawn <owner> <id> - Respawnt das gelockte Tier an der Stelle des Senders [AdminBefehl]
- last_XYZ von einem Entity nach jedem onIrgendwasEvent updaten.
- Bei vielen SQL-Befehlen das ganze verbessern mit InnerJoin
- Bei ArrayList<object> get() in EntityList.java nicht ein SQL-Befehl für jedes Entity einzelnt

Ideen:
- Bei jedem Pferde-Inventar-Event mit der Prism-API eine costum Action in die Queue schreiben.
(Somit werden Entity-Inventar-Sachen wieder geloggt und Prism muss nicht die DB mit item-inserts vollspammen)
- ...

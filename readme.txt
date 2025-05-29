IJA Projekt dokumentace

Auto?i:
Iaroslav Zhdanovich 	xzhdan00
Denys Malytskyi 		xmalytd00

Kratk? popis projektu:

Tento projekt je implementace hry inspirovan? hrou LightBulb pro Android.
Hra se sklad? z hrac? desky o rozm?ru MxN pol?. V ka?d?m poli?ku je n?jak? z elementu: vodi?, zdroj nebo ??rovka. 

Oprot? zad?n? v na?em n?vrhu jsou v?dy vyu?ity v?echna poli?ka.

Jsou podporov?ny 3 r?zn? obt??nosti: Easy(5õ5), Medium(7õ7), Hard(10õ10).

Na konci hry, kdy jsou v?echny ??rovky napojen? ke zdroji, hr?? uvid? zpr?vu o vit?zstv? kde bude uvedeno kolik krok? celkov? ud?lal a jak dlouho mu ?e?en? trvalo. 

Zp?sob p?ekladu:

V na?em navrhu pro p?eklad a spu?t?n? je vyu?it n?stroj maven.

Pro p?eklad a spu?t?n? hry na Linux vyu?ijte:

mvn clean package javafx:run

Pro p?eklad a spu?t?n? hry na Windows vyu?ijte maven wrapper v zdrojov?ch souborech projektu:

.\mvnw clean javafx:run


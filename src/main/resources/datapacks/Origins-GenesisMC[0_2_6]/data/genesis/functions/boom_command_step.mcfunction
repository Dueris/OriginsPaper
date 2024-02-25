execute positioned ~ ~ ~ run particle minecraft:sonic_boom
execute positioned ~ ~ ~ run particle minecraft:dust 0.122 0.614 1.014 0.5 ~ ~ ~ 0.5 0.6 0.5 1 64 force
execute at @e[distance=..6] run damage @s 2 origins:sonic_boom
execute at @e[distance=..4] run damage @s 4 origins:sonic_boom
execute at @e[distance=..2] run damage @s 6 origins:sonic_boom
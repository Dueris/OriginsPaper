tag @s add mimic_warden_starting
execute positioned ~ ~ ~ run particle minecraft:sonic_boom
execute positioned ~ ~ ~ run particle minecraft:dust{color:[0.122,0.614,1.014],scale:0.5} ~ ~ ~ 0.5 0.6 0.5 1 64 force
execute as @e[distance=..6,tag=!mimic_warden_starting] run damage @s 2 origins:sonic_boom
execute as @e[distance=..4,tag=!mimic_warden_starting] run damage @s 4 origins:sonic_boom
execute as @e[distance=..2,tag=!mimic_warden_starting] run damage @s 6 origins:sonic_boom
tag @s remove mimic_warden_starting
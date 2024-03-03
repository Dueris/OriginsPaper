execute unless block ~ ~ ~ #shoot:non_solid run function shoot:hit_block
execute if score #hit chaintemp matches 0 as @e[tag=!chainray,distance=..1.5] at @s run function shoot:hit_entity
scoreboard players add #distance chaintemp 1
execute if score #hit chaintemp matches 0 if score #distance chaintemp matches ..400 positioned ^ ^ ^0.1 run function shoot:ray
particle minecraft:dust .6 0 1 .7 ~ ~ ~ 0.1 0.1 0.1 1 1 normal
particle minecraft:dust .7 0 .8 .7 ~ ~ ~ 0.1 0.1 0.1 1 1 normal
particle minecraft:dust .4 .3 1 .7 ~ ~ ~ 0.1 0.1 0.1 1 1 normal
particle minecraft:dust .4 0 .8 .7 ~ ~ ~ 0.1 0.1 0.1 1 1 normal
particle minecraft:dust .3 0 .8 .7 ~ ~ ~ 0.1 0.1 0.1 1 1 normal
particle minecraft:dust .6 0 1 .7 ~ ~ ~ 0.1 0.1 0.1 1 1 normal
particle minecraft:dust .7 0 .8 .7 ~ ~ ~ 0.1 0.1 0.1 1 1 normal
particle minecraft:dust .4 .3 1 .7 ~ ~ ~ 0.1 0.1 0.1 1 1 normal
particle minecraft:dust .4 0 .8 .7 ~ ~ ~ 0.1 0.1 0.1 1 1 normal
particle minecraft:dust .3 0 .8 .7 ~ ~ ~ 0.1 0.1 0.1 1 1 normal
particle minecraft:wax_off ~ ~ ~ 0.1 0.1 0.1 1 1 normal

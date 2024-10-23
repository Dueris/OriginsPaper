# OriginsPaper

#### Bringing custom origins to PaperMC.

OriginsPaper is an unofficial port of the Origins Mod to PaperMC servers. Over the course of 1 1/2 years of development,
OriginsPaper has come a long way from where it started, now supporting most custom origins from the original mod. Best
of
all, its compatible with Geyser/Bedrock clients, and requires no mods or resource packs to be downloaded to play!

## Important Notice

OriginsPaper is in NO WAY affiliated with the Origins Mod for Fabric. All issues should be reported to the GitHub
repository
for OriginsPaper, not Origins. Any inconsistencies from the Origins mod and OriginsPaper should be reported to
OriginsPaper as well.
OriginsPaper is built on Paper, so Spigot/CraftBukkit servers will not work, and I don't have plans to make it
supported.
Please pay attention to the version OriginsPaper works on when downloading. OriginsPaper is **HIGHLY** version dependant
and will
not load on any other server version than the one its made for.

## How do I add custom origins?

It's as simple as dragging and dropping the zip file into the datapacks folder of the overworld, and restarting your
server. As of v1.0.0, most custom origins should work perfectly fine! If you encounter any issues please report it to
the GitHub repository or the discord server.

## Going forward

With custom origins being nearly completed, OriginsPaper is thinking about expanding the scope of custom origins
support.
From Pehuki, to other mods, OriginsPaper will attempt to include as much custom origins support as possible, so any
origin
can be played.

## PowerTypes that don't work with OriginsPaper

- ModifyFovPower -- requires render modifications
- OverlayPower -- specifically with custom colors
- PreventFeatureRenderPower -- requires render modifications
- ShaderPower -- requires render moifications
- SprintingPower -- vanilla/paper doesnt sync the sprinting attribute correctly
- WalkOnFluidPower -- collisions are controlled mostly by the client, this would need to work on both c/s
- IgnoreWaterPower -- collisions are controlled mostly by the client, this would need to work on both c/s
- ModifyFluidRenderPower -- requires render modifications
- PosePower -- causes visual glitch that would require an epilepsy warning, not implementing.
- ShakingPower -- requires render modifications
- LavaVisionPower -- requires render modifications
- ModifyCameraSubmersionPower -- requires render modifications
- ModifyVelocityPower -- causes visual glitches and cant be synced with the client to smooth it out

## Donate

If you like the plugin and want to support me, consider donating! I have put thousands of hours and millions of lines of
code into this project, and it would help me greatly if you donated.

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/V7V4O31UU)
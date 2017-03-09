# WurmServerModLauncher

Wurm Unlimited Mod Launcher

ModLauncher for WurmUnlimited and a set of mods

* Announcer: Server-wide player login announcement
* BagOfHolding: magicly increase the size of containers
* CreatureAgeMod: Animals grow up faster
* CropMod: Prevent crops from overgrowing
* HarvestHelper: Get exact season start times
* InbreedWarning: Receive a warning when breeding related animals
* ServerPacks: Provides additional pack download service for mods
* SpellMod: Gives priests all spells and removes priest restrictions

Demo status (conversions of other mods for demonstration and testing purposes): 

* CreatureDemo: AbsolutelyNobodys creature mods
* DigToGround: Dig to ground, dredge to ship (or ground)
* HitchingPost: Arkoniks Hitchingpost mod

## Installation

* Download latest release [server-modlauncer-xxx.zip](https://github.com/ago1024/WurmServerModLauncher/releases)
* Extract contents to Wurm Unlimited server root.
* For windows: execute `patcher.bat`
* For linux: first give execution rights for patcher.sh by using following command `chmod +x patcher.sh` then execute this file `./patcher.sh`
* To launch server use `WurmServerLauncher-patched` instead `WurmServerLauncher`

## Configuration

All Configuration files for mods can be found in `mods/` directory. For each configuration option, consult mod readme, o configuration file itself.


## Update

* Backup existing mods
* Download latest release [server-modlauncer-xxx.zip](https://github.com/ago1024/WurmServerModLauncher/releases)
* Extract contents to Wurm Unlimited server root and replace existing files
* Remove `WurmServerLauncher-patched` and launch `patcher.sh` for linux or `patcher.bat` for windows


Check the [Wiki](https://github.com/ago1024/WurmServerModLauncher/wiki) for details
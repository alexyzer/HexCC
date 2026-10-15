### [Hex Casting](https://modrinth.com/mod/hex-casting) - [CC:Tweaked](https://modrinth.com/mod/cc-tweaked) interop: Cast spells with a peripheral.
**Hex**Casting + **C**omputer **C**raft. Not to be confused with [JHexCC](https://gitlab.com/eleksploded/hexcc) (HexC Compiler)
___
To use this mod, you need to get [Mindsplice Staff](https://hexcasting.hexxy.media/v/0.11.4/1.0/en_us/?nospoiler#greatwork/fanciful_staves) first.

Equip the Mindsplice Staff on a [Turtle](https://tweaked.cc/module/turtle.html) or [Pocket Computer](https://tweaked.cc/module/pocket.html) to get access to the `staff` peripheral.
___
### Visit [Staff API documentation](https://github.com/alexyzer/HexCC/wiki/Staff-API-documentation) to read about available methods.
Example code to cast spell from the [Focus](https://hexcasting.hexxy.media/v/0.11.4/1.0/en_us/?nospoiler#items/focus) in selected slot:
```
staff = peripheral.find("staff")

staff.cast("aqqqqq") --Scribe's Reflection
staff.cast()         --Hermes' Gambit
```
___

### The mod is currently in beta!
Following "Extra" features are planned, some with config toggle.
- Fake player implementation for Turtle Casting Environment.
- Mishaps affecting the executing computer (fuel drainage, shutdowns, block breaking).
- Equipping media phials as peripherals.
- Utility library "hexlib", packaging convenient and extra features using this API.
___
### Currently implemented:
- `staff` peripheral functionality. State does not yet save on reload.
- Data-driven system of allowing/disallowing certain iotas crossing Lua -> HexCasting boundary.
- Iota-specific validation system extensible through Java-side HexCCAPI. This feature is raw and may change in later builds.

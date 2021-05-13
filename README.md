# About SpaceGravityThingy v0.9.3
SpaceGravityThingy is a small Open Source game made in Processing.
The objective is flying close to planets with your small spaceship and gather research about them! Currently there is no definitive goal other than having fun and build up your highscore.
(still in development)

# Controls
* Movement: can be switched from either wasd or arrow keys (in-game settings)
* other: if you crash, revert to your base by clicking 'r'

# Changelog
(can be seen separately in changelog.txt)

--- REWORKED ---
* Score System:
   - now called Research System
   - you get more research the closer you fly to the planet (fly safe though... pls ._.)
   - planets have custom research multipliers
   - research gets added every second
   - research needs to be flown back to the home base to be stored
   - research not stored in your home base will get lost upon death/reset
   - stored research gets saved to a json file upon closing the game using the "QUIT" button on the main menu (will be useful in coming updates)

* Planet Data Storage:
   - planet data is being stored in a json file
   - can support up-to 10 planets
   - custom planets are possible (game-breaking ones too!! :D)

* Settings Data Storage:
   - settings stored in a json file, which is been read from at game start (saving isn't possible at this moment so you have to manually edit the json-file to save settings)


--- ADDED ---
* Planet Atmospheres:
   - slow you down drastically if you fly too close

* Sprites/Textures:
   - planet features
   - home base

* Music:
   - title screen music
   - ambient music

* Soundeffects for buttons

* Credits screen

* Settings:
   - separation into Visual / Controls and Sound Settings
   - even more setting options! :D

* Bugs (probably)


--- FIXED ---
* Bugs:
   - shield and explosion not showing up
   - removed fullscreen option on game window
   - player taking multiple damage from one collision (added invincibility frame)

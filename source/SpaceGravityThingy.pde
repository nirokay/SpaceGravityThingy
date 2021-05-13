//  ----------------------------------------------------------------------------------
//  --------------------------------- v0.9.3 -----------------------------------------
//                               SpaceGravityThingy
//  --------------------------------- by Niro ----------------------------------------
//  ----------------------------------------------------------------------------------
//  
//                               Special thanks to:
//  
//                                   QuarkNova
//                                 alexander1350
//                                    Madiwka
// 
//                                      <3
//  
//  ----------------------------------------------------------------------------------

import processing.sound.*;

//Music
SoundFile ambient01;             // Ingame Ambient Music
SoundFile titlescreen;           // Title Screen Music
//SFX
SoundFile alarmbeep;
SoundFile research;
SoundFile shield;
SoundFile explosion;
//GUI Sound
SoundFile button_press;
SoundFile option_on;
SoundFile option_off;

//Credits Screen
Credits credits;                 // Credits Screen

//Player, Planets and Gameplay Data
PImage img_polar01;
PImage img_gas01;
PImage img_clouds01;
PImage img_clouds02;
PImage img_terrain01;
PImage img_mountains01;

PFont font_sp;
PFont font_sp_bold;

JSONArray planetdata;
JSONObject playerdata;
JSONObject settings;

Spaceship ship;                  // Player
Spaceship menu;                  // Spaceship in the Menu
//Base
PImage img_base;
Base base;
//Particles
Particle shield1;                // Player shield
Particle ex0;                    // EXP
Particle ex1;                    // LOS
Particle ex2;                    // ION
//Setting Buttons
Button scorebubble_toggle;       // ship's research range shown
Button display_orbit;            // orbit distances of planets are displayed
Button display_navi;             // displays navigation planets
Button display_advnavi;          // displays advanced navigation (lines)
Button display_path;             // displays ship path
Button controlls_wasd;           // Switch to WASD Controlls
Button toggle_better_atmosphere; // better atmosphere rendering
Button play_music;               // enable music
Button play_sfx;                 // enable soundeffects

//Enums for gamestate
public float gameState;          // (checks which screen to draw
//  Scene Control
public static final int EXIT_GAME =-1;
public static final int MENU = 0;
public static final int GAME = 1;
public static final int SETTINGS = 2;
public static final int CREDITS = 3;
//  Settings
public static final int DEFAULT = 0;
public static final int VISUALS = 1;
public static final int CONTROLS = 2;
public static final int SOUND = 3;


//Global Variables
public boolean gameStarted;      // checks if game is started or not (important for settings screen)
public boolean inSettings;       // checks if in settings
public float cooldown;           // button cooldown                       (menu buttons and settings buttons)
public int blub;                 // Expansion of text when hovering over  (menu buttons)

public int shieldTimer;          // variable for the hit-shield display lenght
public int debrisTimer;          // the same but for explosions and debris
public boolean debrisActive;     // checks if debris and explosion is active (crash)
public float varText;            // quick and easy way to edit text size
public int lives;                // ammount of player lives/shields
public boolean playerDeath;      // checks if player is crashed or alive

public float distance = 20;      // reach of player score bubble
public int score;                // score counter
public int highscore;            // highscore counter
public int scoreTimer;           // how often score ticks up
public int timer = 0;            // the timer for score up tick

//  Player Trail Array
public float[] pathX = new float[500];
public float[] pathY = new float[500];
public boolean startPath;

//  Planet Array
Planet[] planet = new Planet[10];



//  ----------------------------------------------------------------------------------
//  ----------------------------------------------------------------------------------
//                                       SETUP
//  ----------------------------------------------------------------------------------
//  ----------------------------------------------------------------------------------

void setup() {
  size(1200, 675);
  smooth(8);
  frameRate(60);
  surface.setTitle("SpaceGravityThingy Version 0.9.3");
  surface.setResizable(false);

  //  --------------------------------------------------------------------------------
  //   MUSIC AND SOUND
  //  --------------------------------------------------------------------------------
  //  Sound Effects
  alarmbeep = new SoundFile(this, "soundeffects/sfx_alarm.mp3");
  research = new SoundFile(this, "soundeffects/sfx_research.wav");
  shield = new SoundFile(this, "soundeffects/sfx_shield.wav");
  explosion = new SoundFile(this, "soundeffects/sfx_explosion.wav");

  //  GUI Sounds
  button_press = new SoundFile(this, "soundeffects/gui_button.mp3");
  option_on = new SoundFile(this, "soundeffects/gui_option_on.mp3");
  option_off = new SoundFile(this, "soundeffects/gui_option_off.mp3");
  //  Music
  //    Title Screen
  titlescreen = new SoundFile(this, "music/titlescreen.mp3");
  //    Game
  ambient01 = new SoundFile(this, "music/ambient01.mp3");

  //  --------------------------------------------------------------------------------
  //   TITLE SCREEN
  //  --------------------------------------------------------------------------------
  menu = new Spaceship(-10 * distance, 3*height/5 - 40, 10, 0.1, 0, 0);

  //  --------------------------------------------------------------------------------
  //   GAMEPLAY
  //  --------------------------------------------------------------------------------
  //Base
  base = new Base(width/2, height/2, 40, 200);

  //  Player
  ship = new Spaceship(base.x, base.y, 10, 0.1, 0, 0);
  startPath = false;
  scoreTimer = 0;
  ship.invFrame = 0;

  // Planets
  planetdata = loadJSONArray("planetdata.json");
  for (int i = 0; i < planetdata.size(); i++) {
    JSONObject planetValue = planetdata.getJSONObject(i);

    int id = planetValue.getInt("id");
    String name = planetValue.getString("name");
    float multi = planetValue.getFloat("mult");
    float x = planetValue.getFloat("x");
    float y = planetValue.getFloat("y");
    float r = planetValue.getFloat("r");
    float m = planetValue.getFloat("m");

    boolean atmos = planetValue.getBoolean("has_atmosphere");
    if (atmos == true) {
      float atmos_h = planetValue.getFloat("atmos_height");
      float atmos_d = planetValue.getFloat("atmos_density");
      planet[id] = new Planet(x, y, r, m, name, multi, true, atmos_h, atmos_d);
    } else {
      planet[id] = new Planet(x, y, r, m, name, multi);
    }
    planet[id].description = planetValue.getString("desc");

    planet[id].gas = planetValue.getInt("gas");
    planet[id].polar = planetValue.getInt("poles");
    planet[id].terrain = planetValue.getInt("terrain");
    planet[id].mountains = planetValue.getInt("mountains");
    planet[id].clouds = planetValue.getInt("clouds");
  }

  //  General Gameplay Rules
  gameStarted = false;
  lives = 3;
  gameState = 0;
  debrisActive = false;
  playerDeath = false;

  //  --------------------------------------------------------------------------------
  //   CREDITS
  //  --------------------------------------------------------------------------------
  credits = new Credits(0, 20, false);

  //  --------------------------------------------------------------------------------
  //   SETTINGS
  //  --------------------------------------------------------------------------------
  float buttonWidth = 20;
  float buttonPlusX = 20;
  float buttonPlusY = 90;
  //  Settings Options
  settings = loadJSONObject("settings.json");
  //    Visuals
  display_orbit = new Button(buttonPlusX, buttonPlusY, buttonWidth, "display_orbits");
  display_orbit.state = settings.getInt("display_orbits");
  toggle_better_atmosphere = new Button(buttonPlusX+500, buttonPlusY+30, buttonWidth, "better_atmosphere");
  toggle_better_atmosphere.state = settings.getInt("better_atmosphere");

  scorebubble_toggle = new Button(buttonPlusX, buttonPlusY+90, buttonWidth, "research_radius");
  scorebubble_toggle.state = settings.getInt("research_radius");
  display_path = new Button(buttonPlusX, buttonPlusY+120, buttonWidth, "ship_path");

  display_path.state = settings.getInt("ship_path");
  display_navi = new Button(buttonPlusX, buttonPlusY+180, buttonWidth, "ship_navi");
  display_navi.state = settings.getInt("ship_navi");
  display_advnavi = new Button(buttonPlusX+20, buttonPlusY+210, buttonWidth, "better_navi");
  display_advnavi.state = settings.getInt("better_navi");


  //    Controlls
  controlls_wasd = new Button(buttonPlusX, buttonPlusY, buttonWidth, "wasd_controlls");
  controlls_wasd.state = settings.getInt("wasd_controlls");


  //    Sound
  play_music = display_orbit = new Button(buttonPlusX, buttonPlusY, buttonWidth, "music");
  play_music.state = settings.getInt("music");
  play_sfx = display_orbit = new Button(buttonPlusX, buttonPlusY+30, buttonWidth, "sfx");
  play_sfx.state = settings.getInt("sfx");

  //  Load Data
  planetdata = loadJSONArray("planetdata.json");

  playerdata = loadJSONObject("playerdata.json");
  highscore = playerdata.getInt("research_storage");

  font_sp = loadFont("fonts/sp.vlw");
  font_sp_bold = loadFont("fonts/sp_bold.vlw");

  img_base = loadImage("graphics/base.png");
  
  img_polar01 = loadImage("graphics/polar01.png");
  img_gas01 = loadImage("graphics/gas01.png");
  img_clouds01 = loadImage("graphics/clouds01.png");
  img_clouds02 = loadImage("graphics/clouds02.png");
  img_terrain01 = loadImage("graphics/terrain01.png");
  img_mountains01 = loadImage("graphics/mountains01.png");

  println("Hello, thanks for downloading this silly little game! :)");
  println("This game is still in development, feel free to report any issues you experience.");
  println("Thank you and I hope you enjoy ^^");
}



//  ----------------------------------------------------------------------------------
//  ----------------------------------------------------------------------------------
//                                        DRAW
//  ----------------------------------------------------------------------------------
//  ----------------------------------------------------------------------------------

void draw() {
  //  Cooldown for buttons
  if (cooldown > 0) {
    cooldown = cooldown - 1;
  }

  //  Scene Controll
  switch(floor(gameState)) {
  case EXIT_GAME:
    println("Quitting Game");
    playerdata.setInt("research_storage", highscore);
    saveJSONObject(playerdata, "data/playerdata.json");
    exit();
    break;

  case MENU:
    menu();
    gameStarted = false;
    break;

  case GAME:
    translate(-ship.x+width/2, -ship.y+height/2);
    gameplay();
    gameStarted = true;
    break;

  case SETTINGS:
    sett();
    inSettings = true;
    break;

  case CREDITS:
    credits();
    break;
  }
  settingsUpdate();
}



//  ----------------------------------------------------------------------------------
//  ----------------------------------------------------------------------------------
//                                      GAMEPLAY
//  ----------------------------------------------------------------------------------
//  ----------------------------------------------------------------------------------

void gameplay() {
  background(10, 10, 10);

  ship.pathDraw = true;
  if (titlescreen.isPlaying() == true) {
    titlescreen.pause();
  }

  if (ambient01.isPlaying() == false && play_music.state == 1) {
    ambient01.amp(0.4);
    ambient01.play();
  }

  for (int i = 0; i < planetdata.size(); i++) {
    JSONObject planetValue = planetdata.getJSONObject(i);
    int r = planetValue.getInt("red");
    int g = planetValue.getInt("green");
    int b = planetValue.getInt("blue");
    planet[i].display(color(r, g, b));

    if (playerDeath == false) {
      planet[i].gravity(ship);
      planet[i].collision_effect(ship);
      planet[i].in_atmos_effect(ship);
    }
  }
  //  Home Bubble / Home Base
  base.display();
  base.parking();
  //  Display and Update Player
  ship.display();
  ship.update();
  ship.reset();

  if (playerDeath == false) {
    //  Normal Gameplay / Player Alive
    ship.controlls();
    scoreCount();
  } else {
    deathScreen();
  }
  particles();
  gui(ship.x, ship.y);
}


//  Score counting
void scoreCount() {
  scoreTimer = scoreTimer + 1;
  if (scoreTimer > 50) {
    scoreTimer = 0;
  }

  for (int i = 0; i < planetdata.size(); i++) {
    if (planet[i].near(ship) == true) {
      float multiplier = round( planet[i].researchMulti * round(100 * 1/(dist(ship.x, ship.y, planet[i].plaX, planet[i].plaY) - planet[i].plaR/2)));
      if (scoreTimer == 0 && ship.researchCount > 0) {
        score = score + int(multiplier);
        ship.researchCount = ship.researchCount - 1;
        // Sound
        if (play_sfx.state == 1 && scoreTimer == 0) {
          research.amp(random(0.05, 0.1));
          research.play();
        }
      }
      textFont(font_sp, 15);
      fill(255);
      text("+" + multiplier, ship.x, ship.y - ship.sizeSS*2);
    }
  }
}

void particles() {

  shield1 = new Particle(ship.x, ship.y, 30, shieldTimer);
  ex0 = new Particle(ship.x, ship.y, 30, debrisTimer);
  ex1 = new Particle(ship.x, ship.y, 20, debrisTimer);
  ex2 = new Particle(ship.x, ship.y, 10, debrisTimer);

  if (shieldTimer > 0) {
    //  Shield Display
    shield1.display(20, 250, 100);

    shieldTimer = shieldTimer - 1;
  }
  if (debrisTimer > 0) {
    //  Debris Display
    ex0.display(random(230, 250), random(50, 70), random(10, 20));
    ex1.display(random(230, 250), random(50, 70), random(10, 20));
    ex2.display(random(230, 250), random(50, 70), random(10, 20));

    debrisTimer = debrisTimer - 1;
  }
}

void deathScreen() {
  //  Normal Gameplay + Check if Player is dead
  float xOff = ship.x - width/2;
  float yOff = ship.y - height/2;


  //  Player Death Speed
  ship.speedX = 0;
  ship.speedY = 0;
  //  Debris "Memory"
  if (debrisActive == false) {
    if (play_sfx.state == 1) {
      explosion.play();
    }

    debrisTimer = 150; //Debris Disappearance Timer
    debrisActive = true;
  }
  //  Death Screen
  float varText = 26;
  fill(255);
  textFont(font_sp_bold, width/varText);
  textAlign(CENTER, BOTTOM);
  text("You crashed! You lost " + score + " research!", width/2 + xOff, height/2 + yOff);
  textFont(font_sp_bold, width/varText/2);
  textAlign(CENTER, TOP);
  text("press 'r' to restart", width/2 + xOff, height/2 + yOff);
}



//  ----------------------------------------------------------------------------------
//  ----------------------------------------------------------------------------------
//                                       GUI
//  ----------------------------------------------------------------------------------
//  ----------------------------------------------------------------------------------

void gui(float xOff, float yOff) {
  xOff = xOff - width/2;
  yOff = yOff - height/2;

  //  Life Display
  float posX = 20 + xOff;
  float posY = 15*height/16 + yOff;
  float circleSize = 20;
  float circleSep = 50;

  //  Background Hearts
  fill(40, 40, 40);
  noStroke();
  circle(posX, posY, circleSize*1.5);
  circle(posX + circleSep, posY, circleSize*1.5);
  circle(posX + circleSep*2, posY, circleSize*1.5);

  //  Foreground Hearts
  if (lives >= 1) {
    fill(200, 20, 20);
    circle(posX, posY, circleSize);
    if (lives >= 2) {
      fill(200, 20, 20);
      circle(posX + circleSep, posY, circleSize);
    }
    if (lives >= 3) {
      fill(200, 20, 20);
      circle(posX + circleSep*2, posY, circleSize);
    }
  }
  varText = width/70;
  textFont(font_sp, varText);
  textAlign(CENTER, TOP);
  fill(255);
  text("Shields:", posX + circleSep, 15*height/16 - circleSize*1.6 + yOff);

  //  Display Stored Research
  varText = width/50;
  textFont(font_sp, varText);
  textAlign(CENTER, TOP);
  fill(255);
  text("Research Stored: " + highscore, 5.5*width/7 + xOff, 15*height/16 + yOff);
  //  Display Current Research
  varText = width/50;
  textFont(font_sp, varText);
  textAlign(CENTER, BOTTOM);
  fill(255);
  text("Research: " + score, 5.5*width/7 + xOff, height - 2*varText + yOff);
  //  Research Space Bar
  researchbar(width/2 - 150 + xOff, 50 + yOff, 300, 50, 15);

  //  Display Speed
  varText = width/40;
  textFont(font_sp, varText);
  textAlign(CENTER, CENTER);
  fill(255);
  text("Speed: " + round(10*ship.speed(ship)), 2*width/8 + xOff, 15*height/16 + yOff);

  //  Pause Button
  noStroke();
  fill(100);
  menuButton(3.5*width/4+40 + xOff, 3.5*height/4+10 + yOff, height/8-20, height/8-20, 2, "II", 20);
}

void researchbar(float tempX, float tempY, float tempW, float tempH, float tempFrame) {
  // Rectangles
  float x = tempX;
  float y = tempY;
  float w = tempW;
  float h = tempH;
  float f = tempFrame;
  // Smooth Edges
  float edge = 30;
  // Bar Progress
  float current = ship.researchCount;
  float max = ship.researchLimit;

  //  Bar Background
  fill(30);
  rect(x - f, y - f, w + 2*f, h + 2*f, edge);
  rect(x+w/2, y+h/4 - f/4, w/2+30, h/2 + f/2, edge/3);
  //  Bar
  switch(ceil(5*current/max)) {
    // Black if 0
  case 0:
    fill(0);
    break;
    // Red if under 20%
  case 1:
    fill(#bc1a1a);
    break;
    // Yellow if over 40% under 
  case 2:
    fill(#bca91a);
    break;
  case 3:
    fill(#bca91a);
    break;
    // Aqua if Above 60%
  default:
    fill(#1abc8e);
    break;
  }
  rect(x, y, w*(current/max), h, edge/2);


  textFont(font_sp, 30);
  textAlign(CENTER, CENTER);
  fill(255);
  text(current + " / " + max, x+w/2, y+h/2);
}


//  ----------------------------------------------------------------------------------
//                                     MENUBUTTON
//  ----------------------------------------------------------------------------------

void menuButton(float x, float y, float w, float h, float dest, String text, int textSize) {
  //  int dest -> Window Destination ( 'draw()' for more info )
  float xOff;
  float yOff;
  //  Gameplay Screen Correction
  if (gameState == 1) {
    xOff = ship.x - width/2;
    yOff = ship.y - height/2;
  } else {
    xOff = 0;
    yOff = 0;
  }

  //  Click Check
  if (mouseX + xOff >= x && mouseY + yOff >= y && mouseX + xOff <= x+w && mouseY + yOff <= y+h) {
    if (blub < 1) {
      blub = blub + 1;
    }
    if (mousePressed && cooldown <=  0) {
      gameState = dest;
      cooldown = 50;  // Cooldown to avoid multiple clicking
      if (play_sfx.state == 1) {
        button_press.amp(random(0.1, 0.3));
        button_press.play();
      }
    }
  } else {
    if (blub > 0) {
      blub = blub - 1;
    }
  }
  //  Button Draw
  rect(x, y, w, h, 20);
  //  Text Draw
  textAlign(CENTER, CENTER);
  fill(255);
  textFont(font_sp_bold, textSize + blub*5);
  text(text, x + w/2, y + h/2);
}


//  ----------------------------------------------------------------------------------
//  ----------------------------------------------------------------------------------
//                                         MENU
//  ----------------------------------------------------------------------------------
//  ----------------------------------------------------------------------------------

void menu() {
  background(12);

  //  Music Control
  if (ambient01.isPlaying() == true) {
    ambient01.pause();
  }
  if (titlescreen.isPlaying() == false && play_music.state == 1) {
    titlescreen.amp(0.5);
    titlescreen.play();
  }

  //  Menu Spaceship
  menu.display();
  if (frameCount > 400) {
    menu.speedX = 1;
    menu.speedY = -0.07;
    menu.update();
  }


  //  Menu Buttons
  float xPos = 300;
  float yPos = 300;
  float transformation = 40;
  noStroke();
  //  Start Button
  fill(0);
  menuButton(xPos, yPos + transformation, width - 2*xPos, height - 2*yPos+40, 1, "Start Game", 40);
  //  Quit Button
  fill(0);
  transformation = 200;
  menuButton(width/2-100, height/2-50 + transformation, 200, 75, -1, "Quit Game", 30);
  //  Settings Button
  fill(100);
  menuButton(7*width/8+40, 7*height/8+10, height/8-20, height/8-20, 2, "II", 20);
  //  Credits Button
  fill(100);
  menuButton(40, 7*height/8+10, height/8, height/8-20, 3, "Credits", 15);

  //  Game Title Text
  textAlign(CENTER, CENTER);
  textFont(font_sp_bold, 60);
  fill(255);
  String titleScreen = "SpaceGravityThingy";
  float splash = textWidth("SpaceGravityThingy");
  text(titleScreen, width/2, height/3);
  rotate(-0.15);
  float add = 1.5 * sin(frameCount/20);
  textFont(font_sp, 25 + add);
  fill(#f7e30e);
  text("by Niro", width/2 + splash/2 - 40, height/3 + 180);
  rotate(0);
}



//  ----------------------------------------------------------------------------------
//  ----------------------------------------------------------------------------------
//                                      SETTINGS
//  ----------------------------------------------------------------------------------
//  ----------------------------------------------------------------------------------

void sett() {
  background(20);
  String settingsText = "Settings Screen";

  switch(int(10*gameState)-20) {
  case DEFAULT:
    settingsDefault();
    break;

  case VISUALS:
    settingsText = "Visual Settings";
    settingsVisuals();
    settingsMenuButton();
    break;

  case CONTROLS:
    settingsText = "Controls Settings";
    settingsControls();
    settingsMenuButton();
    break;

  case SOUND:
    settingsText = "Sound Settings";
    settingsSound();
    break;
  }

  //  Top Text
  textFont(font_sp_bold, 50);
  textAlign(LEFT, TOP);
  fill(255);
  text(settingsText, 10, 20);

  //  Return Button
  int destLoc;
  String icon;
  if (gameStarted == true) {
    //  Return to Game
    destLoc = 1;
    icon = ">";
    fill(100);
    menuButton(7*width/8+40, 7*height/8-70, height/8-20, height/8-20, 0, "Menu", 20);
  } else {

    destLoc = 0;
    icon = "<";
  }
  //  Return to Game (Game in progress) / Return to Menu (Game not started)
  fill(100);
  menuButton(7*width/8+40, 7*height/8+10, height/8-20, height/8-20, destLoc, icon, 20);
}


// Different Settings Screens

void settingsDefault() {
  float row = 200;
  float collumn = 4;
  float w = 320;
  float h = 90;
  int sizeText = 35;
  fill(0);
  menuButton(1*width/collumn - w/2 - 50, row - h/2, w, h, 2.1, "Visuals", sizeText);
  fill(0);
  menuButton(2*width/collumn - w/2, row - h/2, w, h, 2.2, "Controlls", sizeText);
  fill(0);
  menuButton(3*width/collumn - w/2 + 50, row - h/2, w, h, 2.3, "Sound", sizeText);
}

void settingsVisuals() {
  display_orbit.display("Display Planet Orbits");
  check(display_orbit);

  toggle_better_atmosphere.display("Better Atmosphere");
  check(toggle_better_atmosphere);

  scorebubble_toggle.display("Show Research Range");
  check(scorebubble_toggle);

  display_path.display("Draw Ship Trails");
  check(display_path);

  display_navi.display("Show Planet Navigation");
  check(display_navi);

  display_advnavi.display("Show Advanced Planet Navigation");
  check(display_advnavi);
}
void settingsControls() {
  controlls_wasd.display("Switch to WASD Controlls");
  check(controlls_wasd);
}
void settingsSound() {
  play_music.display("Enable Music");
  check(play_music);

  play_sfx.display("Enable Soundeffects");
  check(play_sfx);
}


void settingsUpdate() {
  if (inSettings == true && (gameState < 2 || gameState >= 3)) {
    //  Visuals
    settings.setInt("display_orbits", display_orbit.state);
    settings.setInt("better_atmosphere", toggle_better_atmosphere.state);

    settings.setInt("research_radius", scorebubble_toggle.state);
    settings.setInt("ship_path", display_path.state);

    settings.setInt("ship_navi", display_navi.state);
    settings.setInt("better_navi", display_advnavi.state);

    //  Controlls
    settings.setInt("wasd_controlls", controlls_wasd.state);

    //  Sound
    settings.setInt("music", play_music.state);
    settings.setInt("sfx", play_sfx.state);

    inSettings = false;
  }
}

void settingsMenuButton() {
  fill(0);
  menuButton(40, 7*height/8+10, height/8, height/8-20, 2, "Back", 15);
}



//  ----------------------------------------------------------------------------------
//  ----------------------------------------------------------------------------------
//                                      CREDITS
//  ----------------------------------------------------------------------------------
//  ----------------------------------------------------------------------------------

void credits() {
  background(12);

  //  Credits Showing
  credits.display();
  credits.update(0, 0);

  fill(100);
  menuButton(40, 7*height/8+10, height/8, height/8-20, 0, "Menu", 15);
}

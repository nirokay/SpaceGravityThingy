import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.sound.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class SpaceGravityThingy extends PApplet {

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

public void setup() {
  
  
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
  menu = new Spaceship(-10 * distance, 3*height/5 - 40, 10, 0.1f, 0, 0);

  //  --------------------------------------------------------------------------------
  //   GAMEPLAY
  //  --------------------------------------------------------------------------------
  //Base
  base = new Base(width/2, height/2, 40, 200);

  //  Player
  ship = new Spaceship(base.x, base.y, 10, 0.1f, 0, 0);
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

public void draw() {
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

public void gameplay() {
  background(10, 10, 10);

  ship.pathDraw = true;
  if (titlescreen.isPlaying() == true) {
    titlescreen.pause();
  }

  if (ambient01.isPlaying() == false && play_music.state == 1) {
    ambient01.amp(0.4f);
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
public void scoreCount() {
  scoreTimer = scoreTimer + 1;
  if (scoreTimer > 50) {
    scoreTimer = 0;
  }

  for (int i = 0; i < planetdata.size(); i++) {
    if (planet[i].near(ship) == true) {
      float multiplier = round( planet[i].researchMulti * round(100 * 1/(dist(ship.x, ship.y, planet[i].plaX, planet[i].plaY) - planet[i].plaR/2)));
      if (scoreTimer == 0 && ship.researchCount > 0) {
        score = score + PApplet.parseInt(multiplier);
        ship.researchCount = ship.researchCount - 1;
        // Sound
        if (play_sfx.state == 1 && scoreTimer == 0) {
          research.amp(random(0.05f, 0.1f));
          research.play();
        }
      }
      textFont(font_sp, 15);
      fill(255);
      text("+" + multiplier, ship.x, ship.y - ship.sizeSS*2);
    }
  }
}

public void particles() {

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

public void deathScreen() {
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

public void gui(float xOff, float yOff) {
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
  circle(posX, posY, circleSize*1.5f);
  circle(posX + circleSep, posY, circleSize*1.5f);
  circle(posX + circleSep*2, posY, circleSize*1.5f);

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
  text("Shields:", posX + circleSep, 15*height/16 - circleSize*1.6f + yOff);

  //  Display Stored Research
  varText = width/50;
  textFont(font_sp, varText);
  textAlign(CENTER, TOP);
  fill(255);
  text("Research Stored: " + highscore, 5.5f*width/7 + xOff, 15*height/16 + yOff);
  //  Display Current Research
  varText = width/50;
  textFont(font_sp, varText);
  textAlign(CENTER, BOTTOM);
  fill(255);
  text("Research: " + score, 5.5f*width/7 + xOff, height - 2*varText + yOff);
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
  menuButton(3.5f*width/4+40 + xOff, 3.5f*height/4+10 + yOff, height/8-20, height/8-20, 2, "II", 20);
}

public void researchbar(float tempX, float tempY, float tempW, float tempH, float tempFrame) {
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
    fill(0xffbc1a1a);
    break;
    // Yellow if over 40% under 
  case 2:
    fill(0xffbca91a);
    break;
  case 3:
    fill(0xffbca91a);
    break;
    // Aqua if Above 60%
  default:
    fill(0xff1abc8e);
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

public void menuButton(float x, float y, float w, float h, float dest, String text, int textSize) {
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
        button_press.amp(random(0.1f, 0.3f));
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

public void menu() {
  background(12);

  //  Music Control
  if (ambient01.isPlaying() == true) {
    ambient01.pause();
  }
  if (titlescreen.isPlaying() == false && play_music.state == 1) {
    titlescreen.amp(0.5f);
    titlescreen.play();
  }

  //  Menu Spaceship
  menu.display();
  if (frameCount > 400) {
    menu.speedX = 1;
    menu.speedY = -0.07f;
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
  rotate(-0.15f);
  float add = 1.5f * sin(frameCount/20);
  textFont(font_sp, 25 + add);
  fill(0xfff7e30e);
  text("by Niro", width/2 + splash/2 - 40, height/3 + 180);
  rotate(0);
}



//  ----------------------------------------------------------------------------------
//  ----------------------------------------------------------------------------------
//                                      SETTINGS
//  ----------------------------------------------------------------------------------
//  ----------------------------------------------------------------------------------

public void sett() {
  background(20);
  String settingsText = "Settings Screen";

  switch(PApplet.parseInt(10*gameState)-20) {
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

public void settingsDefault() {
  float row = 200;
  float collumn = 4;
  float w = 320;
  float h = 90;
  int sizeText = 35;
  fill(0);
  menuButton(1*width/collumn - w/2 - 50, row - h/2, w, h, 2.1f, "Visuals", sizeText);
  fill(0);
  menuButton(2*width/collumn - w/2, row - h/2, w, h, 2.2f, "Controlls", sizeText);
  fill(0);
  menuButton(3*width/collumn - w/2 + 50, row - h/2, w, h, 2.3f, "Sound", sizeText);
}

public void settingsVisuals() {
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
public void settingsControls() {
  controlls_wasd.display("Switch to WASD Controlls");
  check(controlls_wasd);
}
public void settingsSound() {
  play_music.display("Enable Music");
  check(play_music);

  play_sfx.display("Enable Soundeffects");
  check(play_sfx);
}


public void settingsUpdate() {
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

public void settingsMenuButton() {
  fill(0);
  menuButton(40, 7*height/8+10, height/8, height/8-20, 2, "Back", 15);
}



//  ----------------------------------------------------------------------------------
//  ----------------------------------------------------------------------------------
//                                      CREDITS
//  ----------------------------------------------------------------------------------
//  ----------------------------------------------------------------------------------

public void credits() {
  background(12);

  //  Credits Showing
  credits.display();
  credits.update(0, 0);

  fill(100);
  menuButton(40, 7*height/8+10, height/8, height/8-20, 0, "Menu", 15);
}
public class Base {

  private float x, y;             // Position
  private float s;                // Base Size
  private float a;                // Base Area

  Base(float tempX, float tempY, float tempS, float tempA) {
    x = tempX;
    y = tempY;
    s = tempS;
    a = tempA;
  }



  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------
  //                                       DRAW
  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------

  public void display() {
    noStroke();
    fill(0xffffffff, 50);
    ellipse(x, y, s, s);

    int baseScale = 400;
    img_base.resize(baseScale, baseScale);
    image(img_base, base.x - baseScale/2, base.y - baseScale/2);

    if ((display_advnavi.state == 1 && dist(x, y, ship.x, ship.y) > 200) || ship.mapShow() == true) {
      homepath();
    }

    if (display_navi.state == 1) {
    }
  }


  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------
  //                                 NAVIGATION HELP
  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------

  public void homepath() {
    strokeWeight(3);
    stroke(200);
    line(x, y, ship.x, ship.y);

    fill(255);
    textFont(font_sp, 15);
    textAlign(CENTER, CENTER);
    text("Distance: " + round(dist(x, y, ship.x, ship.y)), ship.x, ship.y + 20);
  }



  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------
  //                                     PARKING
  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------

  public void parking() {
    if (timer > 0) {
      timer = timer -1;
    } else {
      timer = 0;
    }

    if (dist(ship.x, ship.y, x, y) < s/2) {
      if (ship.speed(ship) <= 1.5f) {
        ship.speedX = ship.speedX * 0.95f;
        ship.speedY = ship.speedY * 0.95f;
        if (ship.speed(ship) <= 0.01f) {
          textAlign(CENTER, CENTER);
          fill(255);
          textFont(font_sp, 20);
          text("Parked at home base", ship.x, ship.y - 120);
          // Score save
          highscore = highscore + score;
          score = 0;
          ship.researchCount = ship.researchLimit;
        }
      } else {
        timer = 300;
      }
      if (timer > 0) {
        textAlign(CENTER, CENTER);
        fill(255);
        textFont(font_sp, 20);
        text("Slow down to a speed of 15 to park", ship.x, ship.y - 120);
      }
    }
  }
}

public class Button {

  private float x, y;     // Position
  private float w;        // Width
  private int state;      // Memory if activated or not
  private String json;    // json path
  private float pop = 0;  // Text Pop Effect

  Button(float tempX, float tempY, float tempW, String tempJSON) {
    x = tempX;
    y = tempY;
    w = tempW;
    json = tempJSON;
  }



  public void display(String text) {
    if (state == 1) {
      fill(30, 150, 30);
    } else {
      fill(150, 30, 30);
    }
    rect(x, y + w/4, w, w);

    varText = 50;
    fill(255);
    textFont(font_sp, width/varText + pop);
    textAlign(LEFT, CENTER);
    text(text, x + 1.5f*w, y + w/2 + 5);
  }
}

public void check(Button button) {
  if (mouseX >= button.x && mouseX <= button.x+button.w && mouseY >= button.y && mouseY <= button.y+button.w && cooldown <= 0) {
    fill(255, 255, 255, 40);
    rect(button.x, button.y + button.w/4, button.w, button.w);
    button.pop = 3;
    if (mousePressed) {
      cooldown = 20;
      if (button.state == 1) {
        button.state = 0;
        // Sound
        if (play_sfx.state == 1) {
          option_off.amp(0.2f);
          option_off.play();
        }
        // Sound End
      } else {
        button.state = 1;
        // Sound
        if (play_sfx.state == 1) {
          option_on.amp(0.2f);
          option_on.play();
        }
        // Sound End
      }
    }
  } else {
    button.pop = 0;
  }
}

public void update(Button button) {
  if(inSettings == true && (gameState < 2 || gameState >= 3)) {
    settings.setInt(button.json, button.state);
  }
}
public class Credits {
  private float x, y;            // Offset from Center
  private boolean scroll;        // Scrolling Credits

  Credits(float tempX, float tempY, boolean doScroll) {
    x = tempX + width/2;
    y = tempY;
    scroll = doScroll;
  }



  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------
  //                                       DRAW
  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------

  public void display() {
    int spacing = 2;               // Spacing between categories
    int ft = 20;                   // Font size
    int cath = 0;
    //  Credits Text here
    cutext("CREDITS", 30, true, 0);
    cutext("---- Programming ----", ft, true, 1);
    cutext("Niro",ft, false, 2);
    cath = 2 + spacing;
    
    cutext("---- Music ----", ft, true, cath);
    cutext("spacetheme - by YSKNYC", ft, false, cath+1);
    cutext("Watching the Stars - by yd", ft, false, cath+2);
    cath = cath + 2 + spacing;
    
    cutext("---- Soundeffects ----", ft, true, cath);
    cutext("dklon, quboddup, Jesús Lastra, Écrivain", ft, false, cath+1);
    cutext("Sudocolon, NenadSimic", ft, false, cath+2);
  }

  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------
  //                                  BACKGROUND STUFF
  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------


  public void update(float xScroll, float yScroll) {
    if (scroll == true) {
      x = x + xScroll;
      y = y + yScroll;
    }
  }

  public void cutext(String txt, float size, boolean boldness, int position) {
    PFont fontType;
    if(boldness == true) {
      fontType = font_sp_bold;
    } else {
      fontType = font_sp;
    }
    textFont(fontType, size);
    textAlign(CENTER, CENTER);
    text(txt, x, y + (size*position*2));
  }
}
public class Particle {

  private float y, x;                // Position
  private float s;                   // Size
  private float opacity;             // Opacity

  Particle(float tempX, float tempY, float tempS, float tempOp) {
    x = tempX;
    y = tempY;
    s = tempS;
    opacity = tempOp;
  }



  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------
  //                                       DRAW
  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------

  public void display(float r, float g, float b) {
    fill(r, g, b, opacity);
    noStroke();
    ellipse(x, y, s, s);
  }
  
}
public class Planet {
  private float plaX, plaY;              // Position
  private float plaR;                    // Diameter... yes R means diameter, move along, don't ask
  private float plaM;                    // Planet Mass
  private float inf;                     // Gravity Orbit Influence
  private float researchMulti;           // multiplicator for research gain


  private boolean atmos;                 // has atmosphere?
  private float atmosheight;             // height of atmosphere
  private float atmosdensity;            // density of atmosphere

  private int gas;
  private int polar;
  private int terrain;
  private int mountains;
  private int clouds;

  private boolean orbiting;              // Is the player orbiting the planet?
  private float nameDisplayTime;         // Displays Name upon Orbit
  private String name;                   // Planet Name
  private String description;            // Planet Description
  private int uniCol;                  // Planet Colour

  //  Planet without Atmosphere
  Planet (float tempX, float tempY, float tempR, float tempM, String tempName, float tempMulti) {
    plaX = tempX;
    plaY = tempY;
    plaR = tempR;
    plaM = tempM;
    name = tempName;
    researchMulti = tempMulti;
  }

  //  Planet with Atmosphere
  Planet (float tempX, float tempY, float tempR, float tempM, String tempName, float tempMulti, boolean hasAtmosphere, float tempAtmosHeight, float tempAtmosDensity) {
    plaX = tempX;
    plaY = tempY;
    plaR = tempR;
    plaM = tempM;
    name = tempName;
    researchMulti = tempMulti;

    atmos = hasAtmosphere;
    atmosheight = tempAtmosHeight/2;
    atmosdensity = tempAtmosDensity;
  }



  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------
  //                                       DRAW
  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------

  public void display(int col) {
    uniCol = col;

    if (display_orbit.state == 1) {
      drawOrbit();
    }
    if (atmos == true) {
      drawAtmos();
    }
    drawSurface();

    //  Navigation Display
    if (dist(ship.x, ship.y, plaX, plaY) > 2*inf/3) {
      //  Line Navigation
      if (display_advnavi.state == 1) {
        advancedNavi();
      }
      //Ball Navigation
      if (display_navi.state == 1) {
        showNavi();
      }
    }
    details();
    //  Displays Welcome Orbit Message
    namedisplay();
  }



  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------
  //                                   PLANET DISPLAY
  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------

  //  Surface
  public void drawSurface() {
    fill(uniCol);
    ellipse(plaX, plaY, plaR, plaR);
  }
  //  Atmosphere
  public void drawAtmos() {
    if (toggle_better_atmosphere.state == 1) {
      int q =0;
      for (float i = atmosheight; i > plaR/2; i --) {
        i = i - round(atmosheight/10);
        q++;
        fill(uniCol, atmosdensity*10 + q);
        ellipse(plaX, plaY, plaR + atmosheight - q, plaR + atmosheight - q);
      }
    }
    fill(uniCol, atmosdensity + 10);
    ellipse(plaX, plaY, plaR + atmosheight, plaR + atmosheight);
  }
  //  Orbit Display
  public void drawOrbit() {
    inf = plaR*8;
    noStroke();
    fill(uniCol, 10);
    ellipse(plaX, plaY, inf, inf);
  }

  public void details() {
    //if(gas != 0) {
    //  img_gas01.resize(int(plaR), int(plaR));
    //  image(img_gas01, plaX - plaR/2, plaY - plaR/2);
    //}

    //if(polar != 0) {
    //  img_polar01.resize(int(plaR), int(plaR));
    //  image(img_polar01, plaX - plaR/2, plaY - plaR/2);
    //}
    switch(gas) {
    case 1:
      img_gas01.resize(PApplet.parseInt(plaR), PApplet.parseInt(plaR));
      image(img_gas01, plaX - plaR/2, plaY - plaR/2);
      break;
    }
    
    switch(mountains) {
    case 1:
      img_mountains01.resize(PApplet.parseInt(plaR), PApplet.parseInt(plaR));
      image(img_mountains01, plaX - plaR/2, plaY - plaR/2);
      break;
    }
    
    switch(terrain) {
      case 1:
      img_terrain01.resize(PApplet.parseInt(plaR), PApplet.parseInt(plaR));
      image(img_terrain01, plaX - plaR/2, plaY - plaR/2);
    }
    
    switch(polar) {
    case 1:
      img_polar01.resize(PApplet.parseInt(plaR), PApplet.parseInt(plaR));
      image(img_polar01, plaX - plaR/2, plaY - plaR/2);
      break;
    }
    
    switch(clouds) {
    case 1:
      img_clouds01.resize(PApplet.parseInt(plaR), PApplet.parseInt(plaR));
      image(img_clouds01, plaX - plaR/2, plaY - plaR/2);
      break;
    case 2:
      img_clouds02.resize(PApplet.parseInt(plaR), PApplet.parseInt(plaR));
      image(img_clouds02, plaX - plaR/2, plaY - plaR/2);
      break;
    }
  }



  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------
  //                                COLLISION AND SCORE
  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------

  //  Collision with Surface
  public boolean collision(Spaceship object) {
    float d = dist(plaX, plaY, object.x, object.y);
    if (d < object.sizeSS/2 + plaR/2) {
      return true;
    } else {
      return false;
    }
  }
  public void collision_effect(Spaceship object) {
    if (collision(object) == true && object.invFrame <= 0) {
      if (lives > 0) {
        //  Lose 1 life - still alive
        object.speedX = ship.speedX * -1;
        object.speedY = ship.speedY * -1;
        lives = lives - 1;
        object.invFrame = 5;
        if (shieldTimer < 100) {
          shieldTimer = 100; //Shield Disappearance Timer
          shield.play();
        }
      } else {
        playerDeath = true;
      }
    }
  }

  //  Collision with Atmosphere
  public boolean in_atmos(Spaceship object) {
    float d = dist(plaX, plaY, object.x, object.y);
    if (d < object.sizeSS/2 + (plaR + atmosheight)/2) {
      return true;
    } else {
      return false;
    }
  }
  public void in_atmos_effect(Spaceship object) {
    if (in_atmos(object) == true && atmos == true) {
      float den = 4*(atmosheight/2 - (dist(plaX, plaY, object.x, object.y) - plaR/2) ) / atmosheight/2;
      float multi = exp(atmosheight/2 - atmosheight/2*den);
      float frX = 0.47f * 0.5f * den*atmosdensity * sq(object.speedX);
      float frY = 0.47f * 0.5f * den*atmosdensity * sq(object.speedY);
      if (multi < 0) {
        multi = 0;
      }
      if (frY < 0) {
        frY = 0;
      }
      if (frX < 0) {
        frX = 0;
      }
      object.speedX = object.speedX / (frX+1);
      object.speedY = object.speedY / (frY+1);
    }
  }

  //  Near Planet
  public boolean near(Spaceship object) {
    float d = dist(plaX, plaY, object.x, object.y);
    if (d < object.sizeSS/2 * distance + plaR/2 && d > object.sizeSS/2 + plaR/2) {
      return true;
    } else {
      return false;
    }
  }

  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------
  //                                GRAVITY CALCULATION
  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------

  //  Gravity Calculation
  public void gravity(Spaceship player) {
    if (dist(ship.x, ship.y, plaX, plaY) <= inf/2) {
      float grav = (player.mass*plaM/sq(dist(plaX, plaY, player.x, player.y)));
      float acc = inf/(dist(plaX, plaY, player.x, player.y)) * grav;
      player.speedX = player.speedX + (plaX-player.x)*acc;
      player.speedY = player.speedY + (plaY-player.y)*acc;
    }
  }



  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------
  //                                 NAVIGATION HELP
  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------

  //  Ball Navigation
  public void showNavi() {
    float xD = 7.14f * log(dist(ship.x, ship.y, plaX, ship.y));
    float yD = 7.14f * log(dist(ship.x, ship.y, ship.x, plaY));
    float xDir = 1;
    float yDir = 1;

    if (plaX <= ship.x) {
      xDir = -1;
    }
    if (plaY <= ship.y) {
      yDir = -1;
    }

    noStroke();
    ellipse(ship.x + xD*xDir, ship.y + yD*yDir, plaR/10, plaR/10);
  }

  //  (Advanced) Line Navigation
  public void advancedNavi() {
    stroke(uniCol);
    strokeWeight(0.5f);
    line(ship.x, ship.y, plaX, plaY);
  }



  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------
  //                                ORBIT ENTER MESSAGE
  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------

  //  Orbit Enter Name Display
  public void namedisplay() {
    if (dist(ship.x, ship.y, plaX, plaY) <= inf/2) {
      if (orbiting == false) {
        nameDisplayTime = 200;
        orbiting = true;
      } else {
        if (nameDisplayTime >= 0) {
          nameDisplayTime = nameDisplayTime -1;
          showName();
        }
      }
    } else {
      orbiting = false;
    }
  }
  public void showName() {
    textAlign(CENTER, BOTTOM);
    fill(255);
    textFont(font_sp_bold, 30);
    text("Orbit of " + name + " entered.", ship.x, ship.y - 120);
    textAlign(CENTER, TOP);
    textFont(font_sp, 20);
    text(description, ship.x, ship.y -120);
  }
}

public class Spaceship {
  private float x = width/2;         // Position
  private float y = height/2;        // Position
  private float speedX, speedY;      // Speed
  private float sizeSS;              // Size
  private float mass;                // Spaceship Mass

  private float invFrame;            // Invincibility Frame

  private float speedChange = 0.02f;  // Acceleration
  private int i = 0;
  private boolean pathDraw;

  private float researchLimit = 50;  // amount of maximal research "blasts"
  private float researchCount;       // amount of research "blasts left"

  Spaceship(float tempX, float tempY, float tempSize, float tempM, float tempSpeedX, float tempSpeedY) {
    x = tempX;
    y = tempY;
    speedX = tempSpeedX;
    speedY = tempSpeedY;
    sizeSS = tempSize;
    mass = tempM;
  }



  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------
  //                                       DRAW
  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------

  public void display() {
    if (pathDraw == true && display_path.state == 1) {
      pathCalc();
      pathDraw();
    }

    if (scorebubble_toggle.state == 1) {
      //Score Bubble
      fill(255, 255, 255, 50);
      noStroke();
      ellipse(x, y, sizeSS * distance, sizeSS * distance);
    }

    //Spaceship
    fill(200, 50, 50);
    strokeWeight(1);
    stroke(255);
    ellipse(x, y, sizeSS, sizeSS);
  }



  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------
  //                             PLAYER MOVEMENT / PLAYER INPUT
  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------

  //  Position Update
  public void update() {
    x = x + speedX;
    y = y + speedY;
    if (invFrame > 0) {
      invFrame = invFrame - 1;
    }
    //  "Drag"
    speedX = speedX*0.999999f;
    speedY = speedY*0.999999f;
  }

  //  Control Input
  public void controlls() {
    //  Arrow Keys Controlls
    if (controlls_wasd.state == 0) {
      if (keyPressed && key == CODED) {
        if (keyCode == LEFT) {
          speedX = speedX - speedChange;
        } else if (keyCode == RIGHT) {
          speedX = speedX + speedChange;
        }
      }
      if (keyPressed && key == CODED) {
        if (keyCode == UP) {
          speedY = speedY - speedChange;
        } else if (keyCode == DOWN) {
          speedY = speedY + speedChange;
        }
      }
    }
    //  WASD Controlls
    else {
      if (keyPressed) {
        if (key == 'a') {
          speedX = speedX - speedChange;
        } else if (key == 'd') {
          speedX = speedX + speedChange;
        }
      }
      if (keyPressed) {
        if (key == 'w') {
          speedY = speedY - speedChange;
        } else if (key == 's') {
          speedY = speedY + speedChange;
        }
      }
    }
  }

  //  Player Reset
  public void reset() {
    if (keyPressed && key == 'r') {
        x = base.x;
        y = base.y;
        speedX = 0;
        speedY = 0;

        lives = 3;
        score = 0;
        researchCount = researchLimit;

        //Reset Player State "Memory"
        debrisActive = false;
        playerDeath = false;

        shieldTimer = 0;
        debrisTimer = 0;
        invFrame = 0;
    }
  }

  public boolean mapShow() {
    if (keyPressed && key == 'm') {
      return true;
    } else {
      return false;
    }
  }



  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------
  //                                    VISUAL STUFF
  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------

  //  Path Array Saving
  public void pathCalc() {
    if (i < pathX.length || i < pathY.length) {
      pathX[i] = ship.x;
      pathY[i] = ship.y;
      i = i +1;
    }
    if (i >= pathX.length || i >= pathY.length) {
      i = 0;
    }
  }
  //  Path Drawing
  public void pathDraw() {
    int q;
    for (q = 0; q < pathX.length || q < pathY.length; q++) {
      if ((pathX[q] >=  ship.x - width/2 && pathX[q] <= ship.x + width/2) && (pathY[q] >= ship.y - height/2 && pathY[q] <= ship.y + height/2)) {
        fill(80);
        noStroke();
        ellipse(pathX[q], pathY[q], 2, 2);
      }
    }
    if (q >= pathX.length || q >= pathY.length) {
      q = 0;
    }
  }

  //  Speed Calculator  -  GUI
  public float speed(Spaceship name) {
    return ( sqrt(sq(name.speedX)) + sqrt(sq(name.speedY)) );
  }
}
  public void settings() {  size(1200, 675);  smooth(8); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "SpaceGravityThingy" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}

//Planets and Player
Planet p0;                     // Blue Planet
Planet p1;                     // Grey Planet/Moon
Planet p2;                     // Red Planet
Planet p3;                     // Purple Planet
Spaceship ship;                // Player
//Particles
Particle shield1;              // Player shield
Particle ex0;                  // EXP
Particle ex1;                  // LOS
Particle ex2;                  // ION
//Setting Buttons
Button scorebubble_toggle;     // ship's research range shown
Button display_orbit;          // orbit distances of planets are displayed
Button display_navi;           // 

//Global Variables
public int gameState;          // (checks which screen to draw
public boolean gameStarted;    // checks if game is started or not (important for settings screen)
public float cooldown;         // button cooldown
public int blub;               // Expansion of text when hovering over buttons

public int shieldTimer;        // variable for the hit-shield display lenght
public int debrisTimer;        // the same but for explosions and debris
public boolean debrisActive;   // checks if debris and explosion is active (crash)
public float varText;          // quick and easy way to edit text size
public int lives;              // ammount of player lives/shields
public boolean playerDeath;    // checks if player is crashed or alive

public float distance = 20;    // reach of player score bubble
public int score;              // score counter
public int highscore;          // highscore counter

public float[] pathX = new float[500];
public float[] pathY = new float[500];
public boolean startPath;


void setup() {
  //Window Settings - ( invisible in the java file... MUHAHAHA! )
  size(1200, 675, P2D);        // <- see this? exactly!
  smooth(8);                   // what are you still doing here?
  startPath = false;

  //Gameplay
  ship = new Spaceship(width/2, height/2, 10, 0.1, 0, 0);
  //Variables:       x    y    r    m    name
  p0 = new Planet(-1000, -100, 100, 15, "Kerbin");
  p1 = new Planet(-600, 700, 60, 10, "Crystallos");
  p2 = new Planet(1500, 700, 150, 20, "Guerros");
  p3 = new Planet(500, -800, 200, 50, "Regiis");
  //Particles
  shield1 = new Particle(ship.x, ship.y, 30, shieldTimer);
  ex0 = new Particle(ship.x, ship.y, 30, debrisTimer);
  ex1 = new Particle(ship.x, ship.y, 20, debrisTimer);
  ex2 = new Particle(ship.x, ship.y, 10, debrisTimer);
  //General Gameplay Rules
  lives = 3;
  gameState = 0;
  debrisActive = false;
  playerDeath = false;

  //Settings Screen
  float buttonWidth = 20;
  float buttonPlusX = 20;
  float buttonPlusY = 90;

  scorebubble_toggle = new Button(buttonPlusX, buttonPlusY, buttonWidth);
  display_orbit = new Button(buttonPlusX, buttonPlusY+30, buttonWidth);
  display_navi = new Button(buttonPlusX, buttonPlusY+60, buttonWidth);

  //Default Settings
  gameStarted = false;
  scorebubble_toggle.state = 1;
  display_orbit.state = 1;
  display_navi.state = 1;
}

void draw() {
  //Cooldown for buttons
  if (cooldown > 0) {
    cooldown = cooldown - 1;
  }

  //
  //Window Choice --- -1: Exit Game --- 0: Menu --- 1: Game --- 2: Settings Screen ---
  //

  if (gameState == -1) {
    //Exit Game ( -1 )
    exit();
  } else if (gameState == 0) {
    //Draw Menu ( 0 )
    menu();
    gameStarted = false;
  } else if (gameState == 1) {
    //Draw Gameplay Screen ( 1 )
    translate(-ship.x+width/2, -ship.y+height/2);
    gameplay();

    gameStarted = true;
  } else if (gameState == 2) {
    //Draw Settings ( 2 )
    sett();
  }
}

//
//SEARCHGAMEPLAY --- --- --- 
//
void gameplay() {
  background(10, 10, 10);

  //Object Display
  p0.display(#1939aa);
  p1.display(#70937c);
  p2.display(#ba6a50);
  p3.display(#a01845);

  //Planet Gravity
  if (playerDeath == false) {
    p0.gravity(ship);
    p1.gravity(ship);
    p2.gravity(ship);
    p3.gravity(ship);
  }
  //Home Bubble
  float homeBubble = 40;
  noStroke();
  fill(100);
  ellipse(width/2, height/2, homeBubble, homeBubble);
  if (dist(ship.x, ship.y, width/2, height/2) < homeBubble/2 && ship.speed(ship) <= 10) {
    ship.speedX = ship.speedX * 0.95;
    ship.speedY = ship.speedY * 0.95;
    if (ship.speed(ship) <= 0.01) {
      textAlign(CENTER, CENTER);
      fill(255);
      textSize(14);
      text("Parked at home base", ship.x, ship.y - 120);
    }
  }
  //Ship
  ship.display();
  ship.update();
  ship.reset();

  //Collision with Planets
  if (ship.collision(p0) == true || ship.collision(p1) == true || ship.collision(p2) == true || ship.collision(p3)) {
    if (lives > 0) {
      //Lose 1 life - still alive
      ship.speedX = ship.speedX * -1;
      ship.speedY = ship.speedY * -1;
      lives = lives - 1;

      shieldTimer = 100; //Shield Disappearance Timer
    } else {
      playerDeath = true;
    }
  }

  //Normal Gameplay + Check if Player is dead
  float xOff = ship.x - width/2;
  float yOff = ship.y - height/2;

  if (playerDeath == false) {
    //Normal Gameplay / Player Alive
    ship.controlls();
    scoreCount();
  } else {

    //Player Death Speed
    ship.speedX = 0;
    ship.speedY = 0;
    //Debris "Memory"
    if (debrisActive == false) {
      debrisTimer = 50; //Debris Disappearance Timer
      debrisActive = true;
    }
    //Death Screen
    float varText = 26;
    fill(255);
    textSize(width/varText);
    textAlign(CENTER, BOTTOM);
    text("You crashed! Your final score was " + score + "!", width/2 + xOff, height/2 + yOff);
    textSize(width/varText/2);
    textAlign(CENTER, TOP);
    text("press 'r' to restart", width/2 + xOff, height/2 + yOff);

    //Debris Movement --- NOT WORKING! :(
    ex0.update(0, -1);
    ex1.update(1, 1);
    ex2.update(-1, 1);
  }

  //Particle Controll
  if (shieldTimer > 0) {
    //Shield Display
    shield1.display(20, 250, 100);
    shield1.update(ship.speedX, ship.speedY);

    shieldTimer = shieldTimer - 1;
  }
  if (debrisTimer > 0) {
    //Debris Display
    ex0.display(random(230, 250), random(50, 70), random(10, 20));
    ex0.update(0, -1);
    ex1.display(random(230, 250), random(50, 70), random(10, 20));
    ex1.update(-1, 1);
    ex2.display(random(230, 250), random(50, 70), random(10, 20));
    ex2.update(1, 1);

    debrisTimer = debrisTimer - 1;
  }
  gui(ship.x, ship.y);
}

//Score counting
void scoreCount() {
  if (ship.near(p0) == true || ship.near(p1) == true || ship.near(p2) == true || ship.near(p3) == true) {
    if (random(0, 1) < 0.2) {
      score = score + 1;
    }
  }
}

void menuButton(float x, float y, float w, float h, int dest, String text, int textSize) {
  //dest is the destination once the button is clicked
  float xOff;
  float yOff;
  if (gameState == 1) {
    xOff = ship.x - width/2;
    yOff = ship.y - height/2;
  } else {
    xOff = 0;
    yOff = 0;
  }
  if (mouseX + xOff >= x && mouseY + yOff >= y && mouseX + xOff <= x+w && mouseY + yOff <= y+h) {
    if (blub < 1) {
      blub = blub + 1;
    }
    if (mousePressed && cooldown <=  0) {
      gameState = dest;
      cooldown = 50;
    }
  } else {
    if (blub > 0) {
      blub = blub - 1;
    }
  }
  rect(x, y, w, h, 20);

  textAlign(CENTER, CENTER);
  fill(255);
  textSize(textSize + blub*5);
  text(text, x + w/2, y + h/2);
}

//
//SEARCHGUI --- --- --- 
//
void gui(float xOff, float yOff) {
  xOff = xOff - width/2;
  yOff = yOff - height/2;
  //Movement controlls
  varText = width/80;
  textSize(varText);
  textAlign(CENTER, CENTER);
  fill(255);
  text("/// arrow keys to move, r to reset ///", width/2 + xOff, height + yOff - varText);

  //Life Display
  float posX = 20 + xOff;
  float posY = 15*height/16 + yOff;
  float circleSize = 20;
  float circleSep = 50;
  fill(40, 40, 40);
  noStroke();
  circle(posX, posY, circleSize*1.5);
  circle(posX + circleSep, posY, circleSize*1.5);
  circle(posX + circleSep*2, posY, circleSize*1.5);

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
  varText = width/80;
  textSize(varText);
  textAlign(CENTER, TOP);
  fill(255);
  text("Shields:", posX + circleSep, 15*height/16 - circleSize*1.55 + yOff);


  //Highscore Update
  if (score > highscore) {
    highscore = score;
  } else {
  }
  //Display Highscore
  varText = width/60;
  textSize(varText);
  textAlign(CENTER, TOP);
  fill(255);
  text("Highscore: " + highscore, 5.5*width/7 + xOff, 15*height/16 + yOff);
  //Display Current Score
  varText = width/60;
  textSize(varText);
  textAlign(CENTER, BOTTOM);
  fill(255);
  text("Score: " + score, 5.5*width/7 + xOff, height - 2*varText + yOff);

  //Display Speed
  varText = width/50;
  textSize(varText);
  textAlign(CENTER, CENTER);
  fill(255);
  text("Speed: " + round(10*ship.speed(ship)), 2*width/8 + xOff, 15*height/16 + yOff);

  //Pause
  noStroke();
  fill(100);
  menuButton(3.5*width/4+40 + xOff, 3.5*height/4+10 + yOff, height/8-20, height/8-20, 2, "II", 20);
}

//
//SEARCHMENU ---- --- --- 
//
void menu() {
  background(12);
  float xPos = 300;
  float yPos = 300;
  float transformation = 40;
  noStroke();
  fill(0);
  menuButton(xPos, yPos + transformation, width - 2*xPos, height - 2*yPos+40, 1, "Start Game", 40);
  fill(0);
  transformation = 200;
  menuButton(width/2-100, height/2-50 + transformation, 200, 75, -1, "Quit Game", 30);
  fill(100);
  menuButton(7*width/8+40, 7*height/8+10, height/8-20, height/8-20, 2, "II", 20);
  
  textAlign(CENTER, CENTER);
  textSize(60);
  fill(255);
  String titleScreen = "SpaceGravityThingy";
  float splash = textWidth("SpaceGravityThingy");
  text(titleScreen, width/2, height/3);
  rotate(-0.15);
  textSize(20);
  fill(#f7e30e);
  text("by Niro", width/2 + splash/2 - 40, height/3 + 180);
  rotate(0);
}

//
//SEARCHSETTINGS
//

void sett() {
  //Settings Screen
  noStroke();
  fill(0, 0, 0, 50);
  rect(-10, -10, width+20, height+20);

  //Pause Text
  textSize(50);
  textAlign(LEFT, TOP);
  fill(255);
  text("Settings Screen", 10, 20 );

  //Return to game Text
  varText = width/80;
  textSize(varText);
  textAlign(CENTER, CENTER);
  fill(255);
  text("/// press 'a' to return to game ///", width/2, height - varText);

  //Options Display
  scorebubble_toggle.display("Toggle Score Bubble");
  display_orbit.display("Display Planet Orbits");
  display_navi.display("Show Planet Navigation");

  //Option Checks
  check(scorebubble_toggle);
  check(display_orbit);
  check(display_navi);

  //Return Button
  int destLoc;
  String icon;
  if (gameStarted == true) {
    // Return to Game
    destLoc = 1;
    icon = ">";
    // + Additional "Home" Button
    fill(100);
    menuButton(7*width/8+40, 7*height/8-70, height/8-20, height/8-20, 0, "<", 20);
  } else {
    destLoc = 0;
    icon = "<";
  }
  // Return to Game (Game in progress) / Return to Menu (Game not started)
  fill(100);
  menuButton(7*width/8+40, 7*height/8+10, height/8-20, height/8-20, destLoc, icon, 20);
}

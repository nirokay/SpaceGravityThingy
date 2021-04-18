import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class SpaceGravityThingy extends PApplet {

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


public void setup() {
  //Window Settings - ( invisible in the java file... MUHAHAHA! )
          // <- see this? exactly!
                     // what are you still doing here?
  startPath = false;

  //Gameplay
  ship = new Spaceship(width/2, height/2, 10, 0.1f, 0, 0);
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

public void draw() {
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
public void gameplay() {
  background(10, 10, 10);

  //Object Display
  p0.display(0xff1939aa);
  p1.display(0xff70937c);
  p2.display(0xffba6a50);
  p3.display(0xffa01845);

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
    ship.speedX = ship.speedX * 0.95f;
    ship.speedY = ship.speedY * 0.95f;
    if (ship.speed(ship) <= 0.01f) {
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
public void scoreCount() {
  if (ship.near(p0) == true || ship.near(p1) == true || ship.near(p2) == true || ship.near(p3) == true) {
    if (random(0, 1) < 0.2f) {
      score = score + 1;
    }
  }
}

public void menuButton(float x, float y, float w, float h, int dest, String text, int textSize) {
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
public void gui(float xOff, float yOff) {
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
  circle(posX, posY, circleSize*1.5f);
  circle(posX + circleSep, posY, circleSize*1.5f);
  circle(posX + circleSep*2, posY, circleSize*1.5f);

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
  text("Shields:", posX + circleSep, 15*height/16 - circleSize*1.55f + yOff);


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
  text("Highscore: " + highscore, 5.5f*width/7 + xOff, 15*height/16 + yOff);
  //Display Current Score
  varText = width/60;
  textSize(varText);
  textAlign(CENTER, BOTTOM);
  fill(255);
  text("Score: " + score, 5.5f*width/7 + xOff, height - 2*varText + yOff);

  //Display Speed
  varText = width/50;
  textSize(varText);
  textAlign(CENTER, CENTER);
  fill(255);
  text("Speed: " + round(10*ship.speed(ship)), 2*width/8 + xOff, 15*height/16 + yOff);

  //Pause
  noStroke();
  fill(100);
  menuButton(3.5f*width/4+40 + xOff, 3.5f*height/4+10 + yOff, height/8-20, height/8-20, 2, "II", 20);
}

//
//SEARCHMENU ---- --- --- 
//
public void menu() {
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
  rotate(-0.15f);
  textSize(20);
  fill(0xfff7e30e);
  text("by Niro", width/2 + splash/2 - 40, height/3 + 180);
  rotate(0);
}

//
//SEARCHSETTINGS
//

public void sett() {
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
public class Button {

  private float x, y;     // Position
  private float w;        // Width
  private int state;      // Memory if activated or not

  Button(float tempX, float tempY, float tempW) {
    x = tempX;
    y = tempY;
    w = tempW;
  }

  public void display(String text) {
    if (state == 1) {
      fill(30, 150, 30);
    } else {
      fill(150, 30, 30);
    }
    rect(x, y + w/4, w, w);

    varText = 60;
    fill(255);
    textSize(width/varText);
    textAlign(LEFT, CENTER);
    text(text, x + 1.5f*w, y + w/2);
  }
}

public void check(Button button) {
  if (mousePressed && mouseX >= button.x && mouseX <= button.x+button.w && mouseY >= button.y && mouseY <= button.y+button.w && cooldown <= 0) {
    cooldown = 20;
    if (button.state == 1) {
      button.state = 0;
    } else {
      button.state = 1;
    }
  }
}
public class Particle {

  private float y, x;                // Position
  private float s;                   // Size
  private float spX, spY;            // Speed (unsused)
  private float opacity;             // Opacity

  Particle(float tempX, float tempY, float tempS, float tempOp) {
    x = tempX;
    y = tempY;
    s = tempS;
    opacity = tempOp;
  }

  public void display(float r, float g, float b) {
    fill(r, g, b, opacity);
    noStroke();
    ellipse(x, y, s, s);
  }

  public void update(float spX, float spY) {
    x = x + spX;
    y = y + spY;
  }
}
public class Planet {
  private float plaX, plaY;              // Position
  private float plaR;                    // Diameter... yes R means diameter, move along, don't ask
  private float plaM;                    // Planet Mass
  private float inf;                     // Gravity Orbit Influence

  private boolean orbiting;              // Is the player orbiting the planet?
  private float nameDisplayTime;         // Displays Name upon Orbit
  private String name;                   // Planet Name

  Planet (float tempX, float tempY, float tempR, float tempM, String tempName) {
    plaX = tempX;
    plaY = tempY;
    plaR = tempR;
    plaM = tempM;
    name = tempName;
  }

  public void display(int col) {
    //Out of Screen

    if (display_orbit.state == 1) {
      //Orbits
      inf = plaR*8;
      noStroke();
      fill(col, 10);
      ellipse(plaX, plaY, inf, inf);
    }
    //Draw Planets
    fill(col);
    ellipse(plaX, plaY, plaR, plaR);

    if (display_navi.state == 1) {
      //Direction
      if (dist(ship.x, ship.y, plaX, plaY) > 2*inf/3) {
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
        ellipse(ship.x + xD*xDir, ship.y + yD*yDir, plaR/10, plaR/10);
      }
    }
    if (dist(ship.x, ship.y, plaX, plaY) <= inf/2) {
      if (orbiting == false) {
        nameDisplayTime = 200;
        orbiting = true;
      } else {
        if (nameDisplayTime >= 0) {
          nameDisplayTime = nameDisplayTime -1;
          nameDisplay();
        }
      }
    } else {
      orbiting = false;
    }
  }

  // Gravity Calculation
  public void gravity(Spaceship player) {
    if (dist(ship.x, ship.y, plaX, plaY) <= inf/2) {
      float grav = (player.mass*plaM/sq(dist(plaX, plaY, player.x, player.y)));
      float acc = inf/(dist(plaX, plaY, player.x, player.y)) * grav;
      //player.speedX = player.speedX + (plaX-player.x) * sqrt(acc*plaM*(2/dist(plaX, plaY, player.x, player.y)));
      //player.speedY = player.speedY + (plaY-player.y) * sqrt(acc*plaM*(2/dist(plaX, plaY, player.x, player.y)));
      player.speedX = player.speedX + (plaX-player.x)*acc;
      player.speedY = player.speedY + (plaY-player.y)*acc;
    }
  }

  // Orbit Enter Message
  public void nameDisplay() {
    textAlign(CENTER, CENTER);
    fill(255);
    textSize(25);
    text("Orbit of " + name + " entered.", ship.x, ship.y - 120);
  }
}
public class Spaceship {
  private float x = width/2;         // Position
  private float y = height/2;        // Position
  private float speedX, speedY;      // Speed
  private float sizeSS;              // Size
  private float mass;                // Spaceship Mass

  private float speedChange = 0.02f;  // Acceleration
  private int i = 0;

  Spaceship(float tempX, float tempY, float tempSize, float tempM, float tempSpeedX, float tempSpeedY) {
    x = tempX;
    y = tempY;
    speedX = tempSpeedX;
    speedY = tempSpeedY;
    sizeSS = tempSize;
    mass = tempM;
  }


  public void display() {
    pathCalc();
    pathDraw();    
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

  public void update() {
    x = x + speedX;
    y = y + speedY;
  }

  public void controlls() {
    if (keyPressed && key == CODED) {
      if (keyCode == UP) {
        speedY = speedY - speedChange;
      }
      if (keyCode == DOWN) {
        speedY = speedY + speedChange;
      }
      if (keyCode == LEFT) {
        speedX = speedX - speedChange;
      }
      if (keyCode == RIGHT) {
        speedX = speedX + speedChange;
      }
    }
  }

  public void reset() {
    if (keyPressed) {
      if (key == 'r') {
        x = width/2;
        y = height/2;
        speedX = 0;
        speedY = 0;

        lives = 3;
        score = 0;

        //Reset Player State "Memory"
        debrisActive = false;
        playerDeath = false;
      }
    }
  }

  //Speed Calculator
  public float speed(Spaceship name) {
    return sqrt((name.speedX*name.speedX)/2 + (name.speedY*name.speedY)/2);
  }

  //Collision
  public boolean collision(Planet object) {
    float d = dist(x, y, object.plaX, object.plaY);
    if (d < sizeSS/2 + object.plaR/2) {
      return true;
    } else {
      return false;
    }
  }

  //Score Range
  public boolean near(Planet object) {
    float d = dist(x, y, object.plaX, object.plaY);
    if (d < sizeSS/2 * distance + object.plaR/2 && d > sizeSS/2 + object.plaR/2) {
      return true;
    } else {
      return false;
    }
  }

  //Path
  public void pathCalc() {
    if (i < pathX.length || i < pathY.length) {
      println(i);
      pathX[i] = ship.x;
      pathY[i] = ship.y;
      println(pathX[i] + " and " + pathY[i]);
      i = i +1;
    }
    if (i >= pathX.length || i >= pathY.length) {
      i = 0;
    }
  }
  public void pathDraw() {
    int q;
    for (q = 0; q < pathX.length || q < pathY.length; q++) {
      if ((pathX[q] >=  ship.x - width/2 && pathX[q] <= ship.x + width/2) && (pathY[q] >= ship.y - height/2 && pathY[q] <= ship.y + height/2)) {
        fill(255, 255, 255, 20);
        noStroke();
        ellipse(pathX[q], pathY[q], 5, 5);
      }
    }
    if (q >= pathX.length || q >= pathY.length) {
      q = 0;
    }
  }
}
  public void settings() {  size(1200, 675, P2D);  smooth(8); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "SpaceGravityThingy" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}

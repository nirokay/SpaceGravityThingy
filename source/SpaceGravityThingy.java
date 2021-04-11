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
Planet p1;
Planet p2;
Planet p3;
Spaceship ship;
//Particles
Particle shield1;
Particle ex1;
Particle ex2;
Particle ex3;
//Title Buttons
//  Button titlescreen_start;
//  Button titlescreen_options;
//Setting Buttons
Button scorebubble_toggle;
Button screenwrap_toggle;
Button display_orbit;

//Variables
public int gameMenu;

public int shieldTimer;
public int debrisTimer;
public float varText;
public int lives;

public float distance = 20;
public int score;
public int highscore;


public void setup() {
  //Window Settings
  
  

  //Gameplay
  p1 = new Planet(width/2.5f, height/1.6f - 20, 100, 1);
  p2 = new Planet(width/4, 3*height/4 - 20, 60, 6);
  p3 = new Planet(5*width/7, height/3, 150, 4);
  ship = new Spaceship(width/8, height/2, 10, 0, 0);
  //General Rules
  lives = 3;
  gameMenu = 0;

  //Settings Screen
  float buttonWidth = 20;
  float buttonPlusX = 20;
  float buttonPlusY = 90;
  scorebubble_toggle = new Button(buttonPlusX, buttonPlusY, buttonWidth);
  screenwrap_toggle = new Button(buttonPlusX*2, buttonPlusY+30, buttonWidth);
  display_orbit = new Button(buttonPlusX, buttonPlusY+60, buttonWidth);
  //Default Settings
  scorebubble_toggle.state = 1;
  screenwrap_toggle.state = 1;
  display_orbit.state = 1;
}

public void draw() {
  universalControlls();
  //Window Choice 0:Menu --- 1: Game --- 2: Settings Screen
  if (gameMenu == 0) {
    //Draw Menu (0)
    menu(50);
  } else if (gameMenu == 2) {
    //Draw Settings (2)
    sett();
  } else {
    //Draw Gameplay ( - )
    background(12, 12, 12);

    //Object Display
    p1.display(50, 50, 200);
    p2.display(50, 40, 45);
    p3.display(175, 50, 20);
    //Planet Gravity
    p1.gravity(ship);
    p2.gravity(ship);
    p3.gravity(ship);
    //Ship
    ship.display();
    ship.update();
    ship.reset();
    //Particles
    shield1 = new Particle(ship.x, ship.y, 30, shieldTimer);
    ex1 = new Particle(ship.x, ship.y, 30, debrisTimer);
    ex2 = new Particle(ship.x, ship.y, 20, debrisTimer);
    ex3 = new Particle(ship.x, ship.y, 10, debrisTimer);

    //Collision with Planets
    if (ship.collision(p1) == true || ship.collision(p2) == true || ship.collision(p3) == true) {
      if (lives > 0) {
        //Lose 1 life - still alive
        ship.speedX = ship.speedX * -1;
        ship.speedY = ship.speedY * -1;
        lives = lives - 1;

        shieldTimer = 100; //Shield Disappearance Timer
      } else {
        //Player Death
        ship.speedX = 0;
        ship.speedY = 0;

        debrisTimer = 50; //Debris Disappearance Timer

        //Death Screen
        float varText = 26;
        fill(255);
        textSize(width/varText);
        textAlign(CENTER, BOTTOM);
        text("You crashed! Your final score was " + score + "!", width/2, height/2);
        textSize(width/varText/2);
        textAlign(CENTER, TOP);
        text("press 'r' to restart", width/2, height/2);

        //Debris Movement --- NOT WORKING!
        ex1.update(0, -1);
        ex2.update(1, 1);
        ex3.update(-1, 1);
      }
    } else {

      //Normal Gameplay
      ship.controlls();
      scoreCount();
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
      ex1.display(random(230, 250), random(50, 70), random(10, 20));
      ex1.update(0, -1);
      ex2.display(random(230, 250), random(50, 70), random(10, 20));
      ex2.update(-1, 1);
      ex3.display(random(230, 250), random(50, 70), random(10, 20));
      ex3.update(1, 1);

      debrisTimer = debrisTimer - 1;
    }

    //Gameplay Background Stuff
    gui();
  }
}

public void universalControlls() {
  if (keyPressed) {
    if (key == 'a') {
      gameMenu = 1; //Game Window
    }
    if (key == 's') {
      gameMenu = 2; //Settings Menu
    }
  }
}

public void scoreCount() {
  if (ship.near(p1) == true || ship.near(p2) == true || ship.near(p3) == true) {
    if (random(0, 1) < 0.2f) {
      score = score + 1;
    }
  }
}

//
//SEARCHGUI ---
//

public void gui() {
  //Bottom Rectangle to draw on
  stroke(255);
  strokeWeight(7);
  fill(0);
  rect(-50, 7*height/8, width + 100, height/7);

  //Movement controlls
  varText = width/80;
  textSize(varText);
  textAlign(CENTER, CENTER);
  fill(255);
  text("/// arrow keys to move, r to reset /// press 's' for settings ///", width/2, height - varText);

  //Life Display
  float posX = 20;
  float posY = 15*height/16;
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
  text("Lives:", posX + circleSep, 15*height/16 - circleSize*1.55f);


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
  text("Highscore: " + highscore, 7*width/8, 15*height/16);
  //Display Current Score
  varText = width/60;
  textSize(varText);
  textAlign(CENTER, BOTTOM);
  fill(255);
  text("Score: " + score, 7*width/8, height - 2*varText);

  //Display Speed
  varText = width/50;
  textSize(varText);
  textAlign(CENTER, CENTER);
  fill(255);
  text("Speed: " + round(10*ship.speed(ship)), 2*width/8, 15*height/16);
}

//
//SEARCHMENU ----
//

public void menu(float menuOffset) {
  //titlescreen_start = new Button(0, height/5, 200);
  //Menu Screen
  background(5);

  //MenuText
  textSize(50);
  textAlign(CENTER, BOTTOM);
  fill(255);
  text("Space Flight Thingy, idk myself", width/2, height/3-20);
  textSize(20);
  textAlign(CENTER, TOP);
  fill(255);
  text("/// Press 'a' to start /// Press 's' forsettings ///", width/2, height/3+10);
  textSize(15);
  textAlign(CENTER, TOP);
  fill(255);
  text("by Niro", width/2, height/3-20);

  //Planets
  fill(150, 44, 20);
  noStroke();
  circle(width/2, height/2  + menuOffset, 150);
  fill(40, 80, 60);
  noStroke();
  circle(width/2+55, height/2+40  + menuOffset, 50);

  //Ship Bubble
  fill(60, 60, 60);
  noStroke();
  ellipse(width/2 - 20, 2*height/3 + menuOffset, 40, 40);
  //Spaceship
  fill(200, 50, 50);
  strokeWeight(1);
  stroke(255);
  ellipse(width/2 - 20, 2*height/3 + menuOffset, 10, 10);

  //titlescreen_start.display("Start Game");
  //titlescreen_start.check(titlescreen_start);
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
  screenwrap_toggle.display("Toggle Score Bubble Screen Wraping");
  display_orbit.display("Display Planet Orbits");

  //Option Checks
  check(scorebubble_toggle);
  check(screenwrap_toggle);
  check(display_orbit);
}
public class Button {

  private float x, y; //Position
  private float w; //Width
  private int state;

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
  if (mousePressed && mouseX >= button.x && mouseX <= button.x+button.w && mouseY >= button.y && mouseY <= button.y+button.w) {
    if (button.state == 1) {
      button.state = 0;
    } else {
      button.state = 1;
    }
  }
}
public class Particle {

  private float y, x; //Position
  private float s; //Size
  private float speedX, speedY; //Speed
  private float opacity;

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
  private float plaX, plaY; //Position
  private float plaR; //Radius
  private float plaG; //Gravity
  private float gravMulti = 0.00004f;


  Planet (float tempX, float tempY, float tempR, float tempG) {
    plaX = tempX;
    plaY = tempY;
    plaR = tempR;
    plaG = tempG;
  }

  public void display(float r, float g, float b) {
    if (display_orbit.state == 1) {
      //Orbits
      noStroke();
      fill(r, g, b, 10);
      ellipse(plaX, plaY, plaR*4, plaR*4);
    }
    //Draw Planets
    fill(r, g, b);
    ellipse(plaX, plaY, plaR, plaR);
  }

  public void gravity(Spaceship player) {
    float acc = log(plaG*(dist(plaX, plaY, player.x, player.y)));
    if (dist(plaX, plaY, player.x, player.y) < plaR*2) {
      player.speedX = player.speedX + gravMulti*(plaX-player.x)*acc;
      player.speedY = player.speedY + gravMulti*(plaY-player.y)*acc;
    }
  }
}
public class Spaceship {
  private float x, y; //Position
  private float speedX, speedY; //Speed
  private float sizeSS; //Size

  private float speedChange = 0.2f;

  Spaceship(float tempX, float tempY, float tempSize, float tempSpeedX, float tempSpeedY) {
    x = tempX;
    y = tempY;
    speedX = tempSpeedX;
    speedY = tempSpeedY;
    sizeSS = tempSize;
  }


  public void display() {
    if (scorebubble_toggle.state == 1) {
      //Score Bubble
      fill(255, 255, 255, 50);
      noStroke();
      ellipse(x, y, sizeSS * distance, sizeSS * distance);

      if (screenwrap_toggle.state == 1) {
        //Mirror Sides
        fill(255, 255, 255, 50);
        ellipse(x + width, y, sizeSS * distance, sizeSS * distance);
        ellipse(x, y + 7*height/8, sizeSS * distance, sizeSS * distance);
        ellipse(x - width, y, sizeSS * distance, sizeSS * distance);
        ellipse(x, y - 7*height/8, sizeSS * distance, sizeSS * distance);
        //Mirror Diagonals
        ellipse(x + width, y + 7*height/8, sizeSS * distance, sizeSS * distance);
        ellipse(x - width, y + 7*height/8, sizeSS * distance, sizeSS * distance);
        ellipse(x - width, y - 7*height/8, sizeSS * distance, sizeSS * distance);
        ellipse(x + width, y - 7*height/8, sizeSS * distance, sizeSS * distance);
      }
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

    if (x > width) {
      x = 0;
    } else if (x < 0) {
      x = width;
    }

    if (y > 7*height/8) {
      y = 0;
    } else if (y < 0) {
      y = 7*height/8;
    }
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
        x = width/8;
        y = height/2;
        speedX = 0;
        speedY = 0;

        lives = 3;
        score = 0;
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

  //Heat System
  public void heat() {
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

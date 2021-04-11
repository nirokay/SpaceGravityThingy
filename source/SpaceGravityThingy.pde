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


void setup() {
  //Window Settings
  size(1200, 675);
  smooth(8);

  //Gameplay
  p1 = new Planet(width/2.5, height/1.6 - 20, 100, 1);
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

void draw() {
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

void universalControlls() {
  if (keyPressed) {
    if (key == 'a') {
      gameMenu = 1; //Game Window
    }
    if (key == 's') {
      gameMenu = 2; //Settings Menu
    }
  }
}

void scoreCount() {
  if (ship.near(p1) == true || ship.near(p2) == true || ship.near(p3) == true) {
    if (random(0, 1) < 0.2) {
      score = score + 1;
    }
  }
}

//
//SEARCHGUI ---
//

void gui() {
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
  text("Lives:", posX + circleSep, 15*height/16 - circleSize*1.55);


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

void menu(float menuOffset) {
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
  screenwrap_toggle.display("Toggle Score Bubble Screen Wraping");
  display_orbit.display("Display Planet Orbits");

  //Option Checks
  check(scorebubble_toggle);
  check(screenwrap_toggle);
  check(display_orbit);
}

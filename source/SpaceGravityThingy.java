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

Planet p1;
Spaceship ship;

public float varText;
public int lives;

public float distance = 20;
public int score;
public int highscore;

public void setup() {
  
  
  p1 = new Planet(width/2, height/2, 200, 0);
  ship = new Spaceship(20, 20, 10, 0, 0);
  lives = 3;
}

public void draw() {
  background(12, 12, 12);
  
  p1.display();

  ship.display();
  ship.update();
  ship.reset();

  if (ship.collision(p1) == true) {
    if (lives > 0) {
      ship.speedX = ship.speedX * -1;
      ship.speedY = ship.speedY * -1;
      lives = lives - 1;
    } else {
      ship.speedX = 0;
      ship.speedY = 0;

      float varText = 26;
      fill(255);
      textSize(width/varText);
      textAlign(CENTER, BOTTOM);
      text("You crashed! Your final score was " + score + "!", width/2, height/2);
      textSize(width/varText/2);
      textAlign(CENTER, TOP);
      text("press 'r' to restart", width/2, height/2);
    }
  } else {
    ship.controlls();
  }

  gui();
  scoreCount();
}

public void scoreCount() {
  if (ship.near(p1) == true) {
    score = score + 1;
  } else {
  }
}


public void gui() {
  //Rectagle
  stroke(255);
  strokeWeight(7);
  fill(0);
  rect(-50, 7*height/8, width + 100, height/7);
  
  //Movement controlls
  varText = width/80;
  textSize(varText);
  textAlign(CENTER, CENTER);
  fill(255);
  text("arrow keys to move, r to reset", width/2, height - varText);


  //Life counter
  varText = width/30;
  textSize(varText);
  textAlign(LEFT, CENTER);
  fill(255);
  text(lives, 20, height - varText);

  
  //Highscore
  if (score > highscore) {
    highscore = score;
  } else {
  }
  varText = width/60;
  textSize(varText);
  textAlign(CENTER, TOP);
  fill(255);
  text("Highscore: " + highscore, 7*width/8, height - 2*varText);
  
  //Score
  varText = width/60;
  textSize(varText);
  textAlign(CENTER, BOTTOM);
  fill(255);
  text("Score: " + score, 7*width/8, height - 2*varText);
  
  
  //Speed
  varText = width/50;
  textSize(varText);
  textAlign(CENTER, TOP);
  fill(255);
  text("Speed: " + round(10*ship.speed(ship)), 2*width/8, height - 2*varText);
}

class Particle {

  private float y, x; //Position
  private float s; //Size
  //private int ammount;

  Particle(float tempX, float tempY, float tempS) {
    x = tempX;
    y = tempY;
    s = tempS;
  }

  public void display() {
  }
}

class Planet {
  private float plaX, plaY; //Position
  private float plaR; //Radius

  private float plaG; //Gravity


  Planet (float tempX, float tempY, float tempR, float tempG) {
    plaX = tempX;
    plaY = tempY;
    plaR = tempR;
    plaG = tempG;
  }

  public void display() {
    fill(50, 50, 150);
    noStroke();
    ellipse(plaX, plaY, plaR, plaR);
  }
}

class Spaceship {
  private float x, y; //Position
  private float speedX, speedY, speedLimit; //Speed
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
    //Search Bubble
    fill(255, 255, 255, 50);
    noStroke();
    ellipse(x, y, sizeSS * distance, sizeSS * distance);

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
        x = 20;
        y = 20;
        speedX = 0;
        speedY = 0;

        lives = 3;
        score = 0;
      }
    }
  }

  public float speed(Spaceship name) {
    return sqrt((name.speedX*name.speedX)/2 + (name.speedY*name.speedY)/2);
  }

  public boolean collision(Planet object) {
    float d = dist(x, y, object.plaX, object.plaY);
    if (d < sizeSS/2 + object.plaR/2) {
      return true;
    } else {
      return false;
    }
  }

  public boolean near(Planet object) {
    float d = dist(x, y, object.plaX, object.plaY);
    if (d < sizeSS/2 * distance + object.plaR/2 && d > sizeSS/2 + object.plaR/2) {
      return true;
    } else {
      return false;
    }
  }

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

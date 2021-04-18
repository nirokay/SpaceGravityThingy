public class Spaceship {
  private float x = width/2;         // Position
  private float y = height/2;        // Position
  private float speedX, speedY;      // Speed
  private float sizeSS;              // Size
  private float mass;                // Spaceship Mass

  private float speedChange = 0.02;  // Acceleration
  private int i = 0;

  Spaceship(float tempX, float tempY, float tempSize, float tempM, float tempSpeedX, float tempSpeedY) {
    x = tempX;
    y = tempY;
    speedX = tempSpeedX;
    speedY = tempSpeedY;
    sizeSS = tempSize;
    mass = tempM;
  }


  void display() {
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

  void update() {
    x = x + speedX;
    y = y + speedY;
  }

  void controlls() {
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

  void reset() {
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
  float speed(Spaceship name) {
    return sqrt((name.speedX*name.speedX)/2 + (name.speedY*name.speedY)/2);
  }

  //Collision
  boolean collision(Planet object) {
    float d = dist(x, y, object.plaX, object.plaY);
    if (d < sizeSS/2 + object.plaR/2) {
      return true;
    } else {
      return false;
    }
  }

  //Score Range
  boolean near(Planet object) {
    float d = dist(x, y, object.plaX, object.plaY);
    if (d < sizeSS/2 * distance + object.plaR/2 && d > sizeSS/2 + object.plaR/2) {
      return true;
    } else {
      return false;
    }
  }

  //Path
  void pathCalc() {
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
  void pathDraw() {
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

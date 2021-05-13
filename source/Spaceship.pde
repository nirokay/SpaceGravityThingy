public class Spaceship {
  private float x = width/2;         // Position
  private float y = height/2;        // Position
  private float speedX, speedY;      // Speed
  private float sizeSS;              // Size
  private float mass;                // Spaceship Mass

  private float invFrame;            // Invincibility Frame

  private float speedChange = 0.02;  // Acceleration
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

  void display() {
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
  void update() {
    x = x + speedX;
    y = y + speedY;
    if (invFrame > 0) {
      invFrame = invFrame - 1;
    }
    //  "Drag"
    speedX = speedX*0.999999;
    speedY = speedY*0.999999;
  }

  //  Control Input
  void controlls() {
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
  void reset() {
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

  boolean mapShow() {
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
  void pathCalc() {
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
  void pathDraw() {
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
  float speed(Spaceship name) {
    return ( sqrt(sq(name.speedX)) + sqrt(sq(name.speedY)) );
  }
}

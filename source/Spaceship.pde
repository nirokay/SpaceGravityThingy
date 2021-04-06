class Spaceship {
  private float x, y; //Position
  private float speedX, speedY, speedLimit; //Speed
  private float sizeSS; //Size

  private float speedChange = 0.2;

  Spaceship(float tempX, float tempY, float tempSize, float tempSpeedX, float tempSpeedY) {
    x = tempX;
    y = tempY;
    speedX = tempSpeedX;
    speedY = tempSpeedY;
    sizeSS = tempSize;
  }


  void display() {
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

  void update() {
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
        x = 20;
        y = 20;
        speedX = 0;
        speedY = 0;

        lives = 3;
        score = 0;
      }
    }
  }

  float speed(Spaceship name) {
    return sqrt((name.speedX*name.speedX)/2 + (name.speedY*name.speedY)/2);
  }

  boolean collision(Planet object) {
    float d = dist(x, y, object.plaX, object.plaY);
    if (d < sizeSS/2 + object.plaR/2) {
      return true;
    } else {
      return false;
    }
  }

  boolean near(Planet object) {
    float d = dist(x, y, object.plaX, object.plaY);
    if (d < sizeSS/2 * distance + object.plaR/2 && d > sizeSS/2 + object.plaR/2) {
      return true;
    } else {
      return false;
    }
  }

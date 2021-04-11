public class Spaceship {
  private float x, y; //Position
  private float speedX, speedY; //Speed
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

  //Heat System
  void heat() {
  }
}

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

  void display() {
    noStroke();
    fill(#ffffff, 50);
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

  void homepath() {
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

  void parking() {
    if (timer > 0) {
      timer = timer -1;
    } else {
      timer = 0;
    }

    if (dist(ship.x, ship.y, x, y) < s/2) {
      if (ship.speed(ship) <= 1.5) {
        ship.speedX = ship.speedX * 0.95;
        ship.speedY = ship.speedY * 0.95;
        if (ship.speed(ship) <= 0.01) {
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

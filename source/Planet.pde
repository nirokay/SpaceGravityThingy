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

  void display(color col) {
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
        float xD = 7.14 * log(dist(ship.x, ship.y, plaX, ship.y));
        float yD = 7.14 * log(dist(ship.x, ship.y, ship.x, plaY));

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
  void gravity(Spaceship player) {
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
  void nameDisplay() {
    textAlign(CENTER, CENTER);
    fill(255);
    textSize(25);
    text("Orbit of " + name + " entered.", ship.x, ship.y - 120);
  }
}

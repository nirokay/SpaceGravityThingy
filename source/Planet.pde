public class Planet {
  private float plaX, plaY; //Position
  private float plaR; //Radius
  private float plaG; //Gravity
  private float gravMulti = 0.00004;


  Planet (float tempX, float tempY, float tempR, float tempG) {
    plaX = tempX;
    plaY = tempY;
    plaR = tempR;
    plaG = tempG;
  }

  void display(float r, float g, float b) {
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

  void gravity(Spaceship player) {
    float acc = log(plaG*(dist(plaX, plaY, player.x, player.y)));
    if (dist(plaX, plaY, player.x, player.y) < plaR*2) {
      player.speedX = player.speedX + gravMulti*(plaX-player.x)*acc;
      player.speedY = player.speedY + gravMulti*(plaY-player.y)*acc;
    }
  }
}

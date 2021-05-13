public class Planet {
  private float plaX, plaY;              // Position
  private float plaR;                    // Diameter... yes R means diameter, move along, don't ask
  private float plaM;                    // Planet Mass
  private float inf;                     // Gravity Orbit Influence
  private float researchMulti;           // multiplicator for research gain


  private boolean atmos;                 // has atmosphere?
  private float atmosheight;             // height of atmosphere
  private float atmosdensity;            // density of atmosphere

  private int gas;
  private int polar;
  private int terrain;
  private int mountains;
  private int clouds;

  private boolean orbiting;              // Is the player orbiting the planet?
  private float nameDisplayTime;         // Displays Name upon Orbit
  private String name;                   // Planet Name
  private String description;            // Planet Description
  private color uniCol;                  // Planet Colour

  //  Planet without Atmosphere
  Planet (float tempX, float tempY, float tempR, float tempM, String tempName, float tempMulti) {
    plaX = tempX;
    plaY = tempY;
    plaR = tempR;
    plaM = tempM;
    name = tempName;
    researchMulti = tempMulti;
  }

  //  Planet with Atmosphere
  Planet (float tempX, float tempY, float tempR, float tempM, String tempName, float tempMulti, boolean hasAtmosphere, float tempAtmosHeight, float tempAtmosDensity) {
    plaX = tempX;
    plaY = tempY;
    plaR = tempR;
    plaM = tempM;
    name = tempName;
    researchMulti = tempMulti;

    atmos = hasAtmosphere;
    atmosheight = tempAtmosHeight/2;
    atmosdensity = tempAtmosDensity;
  }



  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------
  //                                       DRAW
  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------

  void display(color col) {
    uniCol = col;

    if (display_orbit.state == 1) {
      drawOrbit();
    }
    if (atmos == true) {
      drawAtmos();
    }
    drawSurface();

    //  Navigation Display
    if (dist(ship.x, ship.y, plaX, plaY) > 2*inf/3) {
      //  Line Navigation
      if (display_advnavi.state == 1) {
        advancedNavi();
      }
      //Ball Navigation
      if (display_navi.state == 1) {
        showNavi();
      }
    }
    details();
    //  Displays Welcome Orbit Message
    namedisplay();
  }



  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------
  //                                   PLANET DISPLAY
  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------

  //  Surface
  void drawSurface() {
    fill(uniCol);
    ellipse(plaX, plaY, plaR, plaR);
  }
  //  Atmosphere
  void drawAtmos() {
    if (toggle_better_atmosphere.state == 1) {
      int q =0;
      for (float i = atmosheight; i > plaR/2; i --) {
        i = i - round(atmosheight/10);
        q++;
        fill(uniCol, atmosdensity*10 + q);
        ellipse(plaX, plaY, plaR + atmosheight - q, plaR + atmosheight - q);
      }
    }
    fill(uniCol, atmosdensity + 10);
    ellipse(plaX, plaY, plaR + atmosheight, plaR + atmosheight);
  }
  //  Orbit Display
  void drawOrbit() {
    inf = plaR*8;
    noStroke();
    fill(uniCol, 10);
    ellipse(plaX, plaY, inf, inf);
  }

  void details() {
    //if(gas != 0) {
    //  img_gas01.resize(int(plaR), int(plaR));
    //  image(img_gas01, plaX - plaR/2, plaY - plaR/2);
    //}

    //if(polar != 0) {
    //  img_polar01.resize(int(plaR), int(plaR));
    //  image(img_polar01, plaX - plaR/2, plaY - plaR/2);
    //}
    switch(gas) {
    case 1:
      img_gas01.resize(int(plaR), int(plaR));
      image(img_gas01, plaX - plaR/2, plaY - plaR/2);
      break;
    }
    
    switch(mountains) {
    case 1:
      img_mountains01.resize(int(plaR), int(plaR));
      image(img_mountains01, plaX - plaR/2, plaY - plaR/2);
      break;
    }
    
    switch(terrain) {
      case 1:
      img_terrain01.resize(int(plaR), int(plaR));
      image(img_terrain01, plaX - plaR/2, plaY - plaR/2);
    }
    
    switch(polar) {
    case 1:
      img_polar01.resize(int(plaR), int(plaR));
      image(img_polar01, plaX - plaR/2, plaY - plaR/2);
      break;
    }
    
    switch(clouds) {
    case 1:
      img_clouds01.resize(int(plaR), int(plaR));
      image(img_clouds01, plaX - plaR/2, plaY - plaR/2);
      break;
    case 2:
      img_clouds02.resize(int(plaR), int(plaR));
      image(img_clouds02, plaX - plaR/2, plaY - plaR/2);
      break;
    }
  }



  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------
  //                                COLLISION AND SCORE
  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------

  //  Collision with Surface
  boolean collision(Spaceship object) {
    float d = dist(plaX, plaY, object.x, object.y);
    if (d < object.sizeSS/2 + plaR/2) {
      return true;
    } else {
      return false;
    }
  }
  void collision_effect(Spaceship object) {
    if (collision(object) == true && object.invFrame <= 0) {
      if (lives > 0) {
        //  Lose 1 life - still alive
        object.speedX = ship.speedX * -1;
        object.speedY = ship.speedY * -1;
        lives = lives - 1;
        object.invFrame = 5;
        if (shieldTimer < 100) {
          shieldTimer = 100; //Shield Disappearance Timer
          shield.play();
        }
      } else {
        playerDeath = true;
      }
    }
  }

  //  Collision with Atmosphere
  boolean in_atmos(Spaceship object) {
    float d = dist(plaX, plaY, object.x, object.y);
    if (d < object.sizeSS/2 + (plaR + atmosheight)/2) {
      return true;
    } else {
      return false;
    }
  }
  void in_atmos_effect(Spaceship object) {
    if (in_atmos(object) == true && atmos == true) {
      float den = 4*(atmosheight/2 - (dist(plaX, plaY, object.x, object.y) - plaR/2) ) / atmosheight/2;
      float multi = exp(atmosheight/2 - atmosheight/2*den);
      float frX = 0.47 * 0.5 * den*atmosdensity * sq(object.speedX);
      float frY = 0.47 * 0.5 * den*atmosdensity * sq(object.speedY);
      if (multi < 0) {
        multi = 0;
      }
      if (frY < 0) {
        frY = 0;
      }
      if (frX < 0) {
        frX = 0;
      }
      object.speedX = object.speedX / (frX+1);
      object.speedY = object.speedY / (frY+1);
    }
  }

  //  Near Planet
  boolean near(Spaceship object) {
    float d = dist(plaX, plaY, object.x, object.y);
    if (d < object.sizeSS/2 * distance + plaR/2 && d > object.sizeSS/2 + plaR/2) {
      return true;
    } else {
      return false;
    }
  }

  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------
  //                                GRAVITY CALCULATION
  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------

  //  Gravity Calculation
  void gravity(Spaceship player) {
    if (dist(ship.x, ship.y, plaX, plaY) <= inf/2) {
      float grav = (player.mass*plaM/sq(dist(plaX, plaY, player.x, player.y)));
      float acc = inf/(dist(plaX, plaY, player.x, player.y)) * grav;
      player.speedX = player.speedX + (plaX-player.x)*acc;
      player.speedY = player.speedY + (plaY-player.y)*acc;
    }
  }



  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------
  //                                 NAVIGATION HELP
  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------

  //  Ball Navigation
  void showNavi() {
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

    noStroke();
    ellipse(ship.x + xD*xDir, ship.y + yD*yDir, plaR/10, plaR/10);
  }

  //  (Advanced) Line Navigation
  void advancedNavi() {
    stroke(uniCol);
    strokeWeight(0.5);
    line(ship.x, ship.y, plaX, plaY);
  }



  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------
  //                                ORBIT ENTER MESSAGE
  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------

  //  Orbit Enter Name Display
  void namedisplay() {
    if (dist(ship.x, ship.y, plaX, plaY) <= inf/2) {
      if (orbiting == false) {
        nameDisplayTime = 200;
        orbiting = true;
      } else {
        if (nameDisplayTime >= 0) {
          nameDisplayTime = nameDisplayTime -1;
          showName();
        }
      }
    } else {
      orbiting = false;
    }
  }
  void showName() {
    textAlign(CENTER, BOTTOM);
    fill(255);
    textFont(font_sp_bold, 30);
    text("Orbit of " + name + " entered.", ship.x, ship.y - 120);
    textAlign(CENTER, TOP);
    textFont(font_sp, 20);
    text(description, ship.x, ship.y -120);
  }
}

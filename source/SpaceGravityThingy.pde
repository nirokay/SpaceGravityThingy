Planet p1;
Spaceship ship;

public float varText;
public int lives;

public float distance = 20;
public int score;
public int highscore;

void setup() {
  size(1200, 675);
  smooth(8);
  p1 = new Planet(width/2, height/2, 200, 0);
  ship = new Spaceship(20, 20, 10, 0, 0);
  lives = 3;
}

void draw() {
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

void scoreCount() {
  if (ship.near(p1) == true) {
    score = score + 1;
  } else {
  }
}


void gui() {
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

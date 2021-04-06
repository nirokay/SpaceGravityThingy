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

  void display() {
    fill(50, 50, 150);
    noStroke();
    ellipse(plaX, plaY, plaR, plaR);
  }
}

public class Particle {

  private float y, x;                // Position
  private float s;                   // Size
  private float opacity;             // Opacity

  Particle(float tempX, float tempY, float tempS, float tempOp) {
    x = tempX;
    y = tempY;
    s = tempS;
    opacity = tempOp;
  }



  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------
  //                                       DRAW
  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------

  void display(float r, float g, float b) {
    fill(r, g, b, opacity);
    noStroke();
    ellipse(x, y, s, s);
  }
  
}

public class Button {

  private float x, y; //Position
  private float w; //Width
  private int state;

  Button(float tempX, float tempY, float tempW) {
    x = tempX;
    y = tempY;
    w = tempW;
  }

  void display(String text) {
    if (state == 1) {
      fill(30, 150, 30);
    } else {
      fill(150, 30, 30);
    }
    rect(x, y + w/4, w, w);

    varText = 60;
    fill(255);
    textSize(width/varText);
    textAlign(LEFT, CENTER);
    text(text, x + 1.5*w, y + w/2);
  }
}

void check(Button button) {
  if (mousePressed && mouseX >= button.x && mouseX <= button.x+button.w && mouseY >= button.y && mouseY <= button.y+button.w) {
    if (button.state == 1) {
      button.state = 0;
    } else {
      button.state = 1;
    }
  }
}

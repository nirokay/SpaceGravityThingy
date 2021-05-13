public class Button {

  private float x, y;     // Position
  private float w;        // Width
  private int state;      // Memory if activated or not
  private String json;    // json path
  private float pop = 0;  // Text Pop Effect

  Button(float tempX, float tempY, float tempW, String tempJSON) {
    x = tempX;
    y = tempY;
    w = tempW;
    json = tempJSON;
  }



  void display(String text) {
    if (state == 1) {
      fill(30, 150, 30);
    } else {
      fill(150, 30, 30);
    }
    rect(x, y + w/4, w, w);

    varText = 50;
    fill(255);
    textFont(font_sp, width/varText + pop);
    textAlign(LEFT, CENTER);
    text(text, x + 1.5*w, y + w/2 + 5);
  }
}

void check(Button button) {
  if (mouseX >= button.x && mouseX <= button.x+button.w && mouseY >= button.y && mouseY <= button.y+button.w && cooldown <= 0) {
    fill(255, 255, 255, 40);
    rect(button.x, button.y + button.w/4, button.w, button.w);
    button.pop = 3;
    if (mousePressed) {
      cooldown = 20;
      if (button.state == 1) {
        button.state = 0;
        // Sound
        if (play_sfx.state == 1) {
          option_off.amp(0.2);
          option_off.play();
        }
        // Sound End
      } else {
        button.state = 1;
        // Sound
        if (play_sfx.state == 1) {
          option_on.amp(0.2);
          option_on.play();
        }
        // Sound End
      }
    }
  } else {
    button.pop = 0;
  }
}

void update(Button button) {
  if(inSettings == true && (gameState < 2 || gameState >= 3)) {
    settings.setInt(button.json, button.state);
  }
}

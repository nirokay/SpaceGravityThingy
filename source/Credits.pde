public class Credits {
  private float x, y;            // Offset from Center
  private boolean scroll;        // Scrolling Credits

  Credits(float tempX, float tempY, boolean doScroll) {
    x = tempX + width/2;
    y = tempY;
    scroll = doScroll;
  }



  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------
  //                                       DRAW
  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------

  void display() {
    int spacing = 2;               // Spacing between categories
    int ft = 20;                   // Font size
    int cath = 0;
    //  Credits Text here
    cutext("CREDITS", 30, true, 0);
    cutext("---- Programming ----", ft, true, 1);
    cutext("Niro",ft, false, 2);
    cath = 2 + spacing;
    
    cutext("---- Music ----", ft, true, cath);
    cutext("spacetheme - by YSKNYC", ft, false, cath+1);
    cutext("Watching the Stars - by yd", ft, false, cath+2);
    cath = cath + 2 + spacing;
    
    cutext("---- Soundeffects ----", ft, true, cath);
    cutext("dklon, quboddup, Jesús Lastra, Écrivain", ft, false, cath+1);
    cutext("Sudocolon, NenadSimic", ft, false, cath+2);
  }

  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------
  //                                  BACKGROUND STUFF
  //  ----------------------------------------------------------------------------------
  //  ----------------------------------------------------------------------------------


  void update(float xScroll, float yScroll) {
    if (scroll == true) {
      x = x + xScroll;
      y = y + yScroll;
    }
  }

  void cutext(String txt, float size, boolean boldness, int position) {
    PFont fontType;
    if(boldness == true) {
      fontType = font_sp_bold;
    } else {
      fontType = font_sp;
    }
    textFont(fontType, size);
    textAlign(CENTER, CENTER);
    text(txt, x, y + (size*position*2));
  }
}

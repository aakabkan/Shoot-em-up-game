import javax.swing.*;

public class SpaceGameMain{
  public static void main(String[] args){
    JFrame frame = new SpaceController();
    frame.pack();
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
}

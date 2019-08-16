import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.Timer.*;
import javax.swing.JOptionPane.*;
import java.text.*;

public class SpaceListener implements ActionListener, KeyListener{
  private SpaceController control;
  private SpaceSave save;
  private SpaceLoad load;
  public SpaceDelete delete;

  public SpaceListener(SpaceController control){
    this.control = control;
    save = new SpaceSave(control);
    load = new SpaceLoad(control);
    delete = new SpaceDelete(control);
  }

  public void keyPressed(KeyEvent e){
    int key = e.getKeyCode();
    if (!control.gameEnded && control.area.updated){
      if (key == KeyEvent.VK_UP) {//move our space ship up
        if (control.area.us.getY()>control.area.step+control.area.shipSize/3){
          control.area.us.setY(control.area.us.getY()-control.area.step);
          control.area.updated = false;
        }
      }
      else if (key == KeyEvent.VK_DOWN) {//move our space ship down
        if (control.area.us.getY()<control.frameSizeY-2*control.area.shipSize/3-control.area.step){
          control.area.us.setY(control.area.us.getY()+control.area.step);
          control.area.updated = false;
        }
      }
      else if (key == KeyEvent.VK_RIGHT) {//move our space ship forward
        control.area.step*=4;
        control.spaceCanvas.repaint();
        control.area.step/=4;
      }
      else if (key == KeyEvent.VK_SPACE) {//here we shoot
        control.area.shots.add(new SpaceArtifact(control.area.us.getX()+control.area.shipSize,control.area.us.getY()));
        control.area.updated = false;
      }
      else if (key == KeyEvent.VK_F2){
        stopTimer();
        save.saveGame();
        restartTimer();
      }
      else if (key == KeyEvent.VK_F3){
        stopTimer();
        save.saveAs();
        restartTimer();
      }
    }
    if (key == KeyEvent.VK_F1){
      newGame();
    }
    else if (key == KeyEvent.VK_F6){
      stopTimer();
      load.loadGame();
      restartTimer();
    }
    else if (key == KeyEvent.VK_F7){
      stopTimer();
      load.quickLoad();
      restartTimer();
    }
    else if (key == KeyEvent.VK_ENTER){
      pauseGame();
    }
    control.spaceCanvas.requestFocus();
  }
  public void keyReleased(KeyEvent e){}
  public void keyTyped(KeyEvent e){}

  public void actionPerformed(ActionEvent e) {
    Object source = e.getSource();
    stopTimer();
    if (source.equals(control.itHow)){
      howToPlay();
    }
    else if (source.equals(control.newGame) || source.equals(control.itNew)){
      newGame();
    }
    else if (source.equals(control.pauseBut) || source.equals(control.itNew)){
      pauseGame();
    }
    else if (source.equals(control.saveBut) || source.equals(control.itQSave)){
      save.saveGame();
    }
    else if (source.equals(control.saveAsBut) || source.equals(control.itSave)){
      save.saveAs();
    }
    else if (source.equals(control.loadBut) || source.equals(control.itLoad)){
      load.loadGame();
    }
    else if (source.equals(control.qLoadBut) || source.equals(control.itQLoad)){
      load.quickLoad();
    }
    else if (source.equals(control.itDelete)){
      try{
        delete.askForDelete();
      }
      catch (IOException ex){}
    }
    restartTimer();
    control.spaceCanvas.requestFocus();
  }

  public void stopTimer(){
    if (control.pauseBut.getText().equals("Pause")){
      control.timer.stop();
    }
  }

  public void restartTimer(){
    if (control.pauseBut.getText().equals("Pause") && !control.gameEnded){
      control.timer.restart();
    }
  }

  public void howToPlay(){
    JOptionPane.showMessageDialog(null, "Steer your ship with the arrow keys. \nShoot enemy ships with space. \nCollect golds and diamonds to gain scores. \nTry to catch a transparent box for protection from the blue beams, which otherwise may kill you. \nSometimes you will face a large enemy ship. These are hard to kill, but gives a lot of scores. \nWatchout! Do never shoot on your allied ships (the ones looking like your own one)");
  }

  public void newGame(){
    if (!control.gameEnded){
      int ans = JOptionPane.showConfirmDialog(null, null, "Game is not over. Do you still want to restart? ",JOptionPane.YES_NO_OPTION);
      if (ans!=0){
        return;
      }
    }
    control.init();
    if (!control.timer.isRunning()){
      control.timer.restart();
    }
    control.spaceCanvas.repaint();
  }

  public void pauseGame(){
    if (control.pauseBut.getText().equals("Pause")){
      control.timer.stop();
      control.pauseBut.setText("Continue");
    }
    else{
      if (!control.gameEnded){
        control.timer.restart();
        control.pauseBut.setText("Pause");
      }
    }
  }

}

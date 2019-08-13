import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.Timer.*;
import javax.swing.JOptionPane.*;
import java.text.*;

public class SpaceListener implements ActionListener, KeyListener{
  private SpaceGameMain main;
  private SpaceSave save;
  private SpaceLoad load;
  public SpaceDelete delete;

  public SpaceListener(SpaceGameMain main){
    this.main = main;
    save = new SpaceSave(main);
    load = new SpaceLoad(main);
    delete = new SpaceDelete(main);
  }

  public void keyPressed(KeyEvent e){
    int key = e.getKeyCode();
    if (!main.gameEnded && main.area.updated){
      if (key == KeyEvent.VK_UP) {//move our space ship up
        if (main.area.us.getY()>main.area.step+main.area.shipSize/3){
          main.area.us.setY(main.area.us.getY()-main.area.step);
          main.area.updated = false;
        }
      }
      else if (key == KeyEvent.VK_DOWN) {//move our space ship down
        if (main.area.us.getY()<main.frameSizeY-2*main.area.shipSize/3-main.area.step){
          main.area.us.setY(main.area.us.getY()+main.area.step);
          main.area.updated = false;
        }
      }
      else if (key == KeyEvent.VK_RIGHT) {//move our space ship forward
        main.area.step*=4;
        main.spaceCanvas.repaint();
        main.area.step/=4;
      }
      else if (key == KeyEvent.VK_SPACE) {//here we shoot
        main.area.shots.add(new SpaceArtifact(main.area.us.getX()+main.area.shipSize,main.area.us.getY()));
        main.area.updated = false;
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
    main.spaceCanvas.requestFocus();
  }
  public void keyReleased(KeyEvent e){}
  public void keyTyped(KeyEvent e){}

  public void actionPerformed(ActionEvent e) {
    Object source = e.getSource();
    stopTimer();
    if (source.equals(main.itHow)){
      howToPlay();
    }
    else if (source.equals(main.newGame) || source.equals(main.itNew)){
      newGame();
    }
    else if (source.equals(main.pauseBut) || source.equals(main.itNew)){
      pauseGame();
    }
    else if (source.equals(main.saveBut) || source.equals(main.itQSave)){
      save.saveGame();
    }
    else if (source.equals(main.saveAsBut) || source.equals(main.itSave)){
      save.saveAs();
    }
    else if (source.equals(main.loadBut) || source.equals(main.itLoad)){
      load.loadGame();
    }
    else if (source.equals(main.qLoadBut) || source.equals(main.itQLoad)){
      load.quickLoad();
    }
    else if (source.equals(main.itDelete)){
      try{
        delete.askForDelete();
      }
      catch (IOException ex){}
    }
    restartTimer();
    main.spaceCanvas.requestFocus();
  }

  public void stopTimer(){
    if (main.pauseBut.getText().equals("Pause")){
      main.timer.stop();
    }
  }

  public void restartTimer(){
    if (main.pauseBut.getText().equals("Pause") && !main.gameEnded){
      main.timer.restart();
    }
  }

  public void howToPlay(){
    JOptionPane.showMessageDialog(null, "Steer your ship with the arrow keys. \nShoot enemy ships with space. \nCollect golds and diamonds to gain scores. \nTry to catch a transparent box for protection from the blue beams, which otherwise may kill you. \nSometimes you will face a large enemy ship. These are hard to kill, but gives a lot of scores. \nWatchout! Do never shoot on your allied ships (the ones looking like your own one)");
  }

  public void newGame(){
    if (!main.gameEnded){
      int ans = JOptionPane.showConfirmDialog(null, null, "Game is not over. Do you still want to restart? ",JOptionPane.YES_NO_OPTION);
      if (ans!=0){
        return;
      }
    }
    main.init();
    if (!main.timer.isRunning()){
      main.timer.restart();
    }
    main.spaceCanvas.repaint();
  }

  public void pauseGame(){
    if (main.pauseBut.getText().equals("Pause")){
      main.timer.stop();
      main.pauseBut.setText("Continue");
    }
    else{
      if (!main.gameEnded){
        main.timer.restart();
        main.pauseBut.setText("Pause");
      }
    }
  }

}

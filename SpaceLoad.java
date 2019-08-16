import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.JOptionPane.*;
import java.text.*;

public class SpaceLoad{
  public SpaceController control;

  public SpaceLoad(SpaceController control){
    this.control = control;
  }

  public void loadGame(){//asks for what game to be loaded, then calls quickLoad().
    try{
      BufferedReader reader = new BufferedReader(new FileReader(control.allSaves));
      String saveNames = "";
      String line;
      while ((line = reader.readLine()) != null){
        saveNames = line + "\n" + saveNames;
      }
      String chosenFile = JOptionPane.showInputDialog(null, "Which of the following files do you want to load? \n\n" + saveNames);
      if (chosenFile != null){
        File loadFile = new File("./saves\\" + chosenFile + ".sg");
        if (loadFile.isFile()){
          control.saveFile = loadFile;
          quickLoad();
        }
        else{
          JOptionPane.showMessageDialog(null, "chosen file doesn't exist", null, JOptionPane.ERROR_MESSAGE);
          loadGame();
        }
      }
      reader.close();
    }
    catch (Exception e){
      System.err.println("Error: "+ e.getMessage());
    }
  }

  public void quickLoad(){//loads the requested file or the most recent saved/loaded file.
    try{
      if (control.saveFile==null){
        BufferedReader reader = new BufferedReader(new FileReader(control.allSaves));
        String line;
        String lastLine=null;
        while ((line = reader.readLine()) != null){
          lastLine = line;
        }
        control.saveFile = new File("./saves\\" + lastLine.substring(0, lastLine.length()-20) + ".sg");
        reader.close();
      }
      String filename = control.saveFile.getName();
      if (control.saveFile.isFile() && filename.substring(filename.length()-3, filename.length()).equals(".sg")){
          BufferedReader reader = new BufferedReader(new FileReader(control.saveFile));
          control.init();
          control.area.score = Integer.parseInt(reader.readLine());
          control.scoreLabel.setText("Score: " + control.area.score);
          control.area.us.setY(Integer.parseInt(reader.readLine()));
          readEnemies(reader, control.area.enemies);
          readArtifacts(reader, control.area.shots);
          final int nBeams = Integer.parseInt(reader.readLine());
          for (int i=0; i<nBeams; i++){
            SpaceBeam beam = new SpaceBeam(Integer.parseInt(reader.readLine()));
            control.area.beams.add(beam);
          }
          readArtifacts(reader, control.area.boxes);
          readArtifacts(reader, control.area.golds);
          readArtifacts(reader, control.area.diamonds);
          readEnemies(reader, control.area.bigEnemies);
          readEnemies(reader, control.area.allies);
          control.spaceCanvas.repaint();
          if ((control.pauseBut.getText().equals("Pause")) && control.gameEnded){
            control.timer.restart();
            control.gameEnded = false;
          }
          reader.close();
      }
    }
    catch (Exception e){
      System.err.println("Error: "+ e.getMessage());
    }
  }

  public void readEnemies(BufferedReader reader, LinkedList<SpaceShip> enemies) throws IOException{//read in the data for the enemies
    final int nEnemies = Integer.parseInt(reader.readLine());
    for (int i=0; i<nEnemies; i++){
      SpaceShip enemy = new SpaceShip(Integer.parseInt(reader.readLine()),Integer.parseInt(reader.readLine()),Integer.parseInt(reader.readLine()));
      enemies.add(enemy);
      if (enemy.hitPoint==0){
        control.area.destroyed.add(enemy);
      }
      if (enemies.equals(control.area.bigEnemies)){
        enemy.big = true;
      }
      else if (enemies.equals(control.area.allies)){
        enemy.friendly = true;
      }
    }
  }

  public void readArtifacts(BufferedReader reader, LinkedList<SpaceArtifact> arts) throws IOException{//read in the data for the artifacts
    final int nArts = Integer.parseInt(reader.readLine());
    for (int i=0; i<nArts; i++){
      SpaceArtifact art = new SpaceArtifact(Integer.parseInt(reader.readLine()),Integer.parseInt(reader.readLine()));
      arts.add(art);
    }
  }
}

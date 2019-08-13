import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.JOptionPane.*;
import java.text.*;

public class SpaceLoad{
  public SpaceGameMain main;

  public SpaceLoad(SpaceGameMain main){
    this.main = main;
  }

  public void loadGame(){//asks for what game to be loaded, then calls quickLoad().
    try{
      BufferedReader reader = new BufferedReader(new FileReader(main.allSaves));
      String saveNames = "";
      String line;
      while ((line = reader.readLine()) != null){
        saveNames = line + "\n" + saveNames;
      }
      String chosenFile = JOptionPane.showInputDialog(null, "Which of the following files do you want to load? \n\n" + saveNames);
      if (chosenFile != null){
        File loadFile = new File("./saves\\" + chosenFile + ".sg");
        if (loadFile.isFile()){
          main.saveFile = loadFile;
          quickLoad();
        }
        else{
          JOptionPane.showMessageDialog(null, "chosen file doesn't exist", null, JOptionPane.ERROR_MESSAGE);
          loadGame();
        }
      }
      reader.close();
    }
    catch (Exception e){}
  }

  public void quickLoad(){//loads the requested file or the most recent saved/loaded file.
    try{
      if (main.saveFile==null){
        BufferedReader reader = new BufferedReader(new FileReader(main.allSaves));
        String line;
        String lastLine=null;
        while ((line = reader.readLine()) != null){
          lastLine = line;
        }
        main.saveFile = new File("./saves\\" + lastLine.substring(0, lastLine.length()-20) + ".sg");
        reader.close();
      }
      String filename = main.saveFile.getName();
      if (main.saveFile.isFile() && filename.substring(filename.length()-3, filename.length()).equals(".sg")){
          BufferedReader reader = new BufferedReader(new FileReader(main.saveFile));
          main.init();
          main.area.score = Integer.parseInt(reader.readLine());
          main.scoreLabel.setText("Score: " + main.area.score);
          main.area.us.setY(Integer.parseInt(reader.readLine()));
          readEnemies(reader, main.area.enemies);
          readArtifacts(reader, main.area.shots);
          final int nBeams = Integer.parseInt(reader.readLine());
          for (int i=0; i<nBeams; i++){
            SpaceBeam beam = new SpaceBeam(Integer.parseInt(reader.readLine()));
            main.area.beams.add(beam);
          }
          readArtifacts(reader, main.area.boxes);
          readArtifacts(reader, main.area.golds);
          readArtifacts(reader, main.area.diamonds);
          readEnemies(reader, main.area.bigEnemies);
          readEnemies(reader, main.area.allies);
          main.spaceCanvas.repaint();
          if ((main.pauseBut.getText().equals("Pause")) && main.gameEnded){
            main.timer.restart();
            main.gameEnded = false;
          }
          reader.close();
      }
    }
    catch (Exception e){}
  }

  public void readEnemies(BufferedReader reader, LinkedList<SpaceShip> enemies) throws IOException{//read in the data for the enemies
    final int nEnemies = Integer.parseInt(reader.readLine());
    for (int i=0; i<nEnemies; i++){
      SpaceShip enemy = new SpaceShip(Integer.parseInt(reader.readLine()),Integer.parseInt(reader.readLine()),Integer.parseInt(reader.readLine()));
      enemies.add(enemy);
      if (enemy.hitPoint==0){
        main.area.destroyed.add(enemy);
      }
      if (enemies.equals(main.area.bigEnemies)){
        enemy.big = true;
      }
      else if (enemies.equals(main.area.allies)){
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

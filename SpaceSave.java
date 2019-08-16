import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.JOptionPane.*;
import java.text.*;

public class SpaceSave{
  public SpaceController control;

  public SpaceSave(SpaceController control){
    this.control = control;
  }

  public void saveGame(){//if no recent save, calls save as; otherwise saves in the existing file.
    if (!control.gameEnded){
      try{
        if (control.saveFile==null){
          saveAs();
          return;
        }
        String filename = control.saveFile.getName().substring(0,control.saveFile.getName().length()-3);
        updateSaves(filename);
        BufferedWriter writer = new BufferedWriter(new FileWriter(control.saveFile));
        writer.write(control.area.score + "\n");
        writer.write(control.area.us.getY() + "\n");
        LinkedList<SpaceObject>[] lists = new LinkedList[] {control.area.enemies, control.area.shots, control.area.beams, control.area.boxes, control.area.golds, control.area.diamonds, control.area.bigEnemies, control.area.allies};
        for (int i=0; i < lists.length; i++){
          writeObjects(writer, lists[i]);
        }
        writer.close();
      }
      catch (Exception e){
        System.err.println("Error: "+ e.getMessage());
      }
    }
  }

  public void writeObjects(BufferedWriter writer, LinkedList<SpaceObject> list) throws IOException{//writes to file
    writer.write(list.size() + "\n");
    for (SpaceObject obj : list){
      if (list.equals(control.area.enemies) || list.equals(control.area.bigEnemies) || list.equals(control.area.allies)){
        writeEnemy(writer, (SpaceShip)obj);
      }
      else if (list.equals(control.area.beams)) {
        writer.write(obj.getX() + "\n");
      }
      else{
        writeCoordinates(writer, obj);
      }
    }
  }

  public void writeEnemy(BufferedWriter writer, SpaceShip enemy) throws IOException{
    writeCoordinates(writer, enemy);
    writer.write(enemy.hitPoint + "\n");
  }

  public void writeCoordinates(BufferedWriter writer, SpaceObject obj) throws IOException{
    writer.write(obj.getX() + "\n");
    writer.write(obj.getY() + "\n");
  }

  public void saveAs() {//asks for the filename for the file to saved, checks if the file exists, and then calls saveGame().
    if (!control.gameEnded){
      try{
        String filename = JOptionPane.showInputDialog(null, "Name file to save: ");
        File saveAsFile = new File("./saves\\" + filename + ".sg");
        if (saveAsFile.isFile()){
          int ans = JOptionPane.showConfirmDialog(null, null, "File already exists. Overwrite? ",JOptionPane.YES_NO_CANCEL_OPTION);
          if (ans==1){
            saveAs();
            return;
          }
          else if (ans==2){
            return;
          }
        }
        else{
          File dir = new File("./saves");
          dir.mkdir();
          saveAsFile.createNewFile();
        }
        control.saveFile = saveAsFile;
        saveGame();
      }
      catch (Exception e){
        System.err.println("Error: "+ e.getMessage());
      }
    }
  }

  public void updateSaves(String filename) throws IOException{//put the most recently save file last in the list.
    control.listener.delete.deleteSaved(filename);
    control.saveFile.createNewFile();
    Calendar c1 = Calendar.getInstance();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String time = format.format(c1.getTime());
    BufferedWriter writer = new BufferedWriter(new FileWriter(control.allSaves,true));
    writer.write("\n" + filename + " " + time);
    writer.close();
  }
}

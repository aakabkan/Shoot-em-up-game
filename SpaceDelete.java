import java.io.*;
import javax.swing.JOptionPane.*;
import javax.swing.*;
import java.text.*;

public class SpaceDelete{
  public SpaceGameMain main;

  public SpaceDelete(SpaceGameMain main){
    this.main = main;
  }

  public void askForDelete() throws IOException{//asks for what file should be deleted, calls deleteSaved.
    BufferedReader reader = new BufferedReader(new FileReader(main.allSaves));
    String saveNames = "";
    String line;
    while ((line = reader.readLine()) != null){
      saveNames = line + "\n" + saveNames;
    }
    String chosenFile = JOptionPane.showInputDialog(null, "Which of the following saves do you want to delete? \n\n" + saveNames);
    if (chosenFile != null){
      File loadFile = new File(chosenFile + ".sg");
      if (loadFile.isFile()){
        int ans = JOptionPane.showConfirmDialog(null, null, "Are you sure you want to delete " + chosenFile + "?", JOptionPane.YES_NO_CANCEL_OPTION);
        if (ans==0){
          deleteSaved(chosenFile);
        }
        if (ans==2){
          reader.close();
          return;
        }
      }
      else{
        JOptionPane.showMessageDialog(null, "chosen file doesn't exist", null, JOptionPane.ERROR_MESSAGE);
      }
    }
    reader.close();
    askForDelete();
  }

  public void deleteSaved(String filename) throws IOException{//delete the chosen file and updates allSaves
      BufferedReader reader = new BufferedReader(new FileReader(main.allSaves));
      String saves = "";
      String line;
      while((line = reader.readLine()) != null) {
        if (line.length()-20 >0){
          if(!(line.substring(0, line.length()-20)).equals(filename)){
            if (saves.equals("")){saves = line;}
            else{saves = saves + "\n" + line;}
          }
        }
      }
      BufferedWriter writer = new BufferedWriter(new FileWriter(main.allSaves));
      writer.write(saves);
      writer.close();
      reader.close();
      File file = new File(filename + ".sg");
      file.delete();
  }
}
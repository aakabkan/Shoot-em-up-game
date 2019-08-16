import java.io.*;

public class SpaceHighScore{

  private File highScoreFile;
  private SpaceController control;

  public SpaceHighScore(SpaceController control){
    this.control = control;
    highScoreFile = new File("highscore.txt");
    try{
      if (!highScoreFile.isFile()){
        highScoreFile.createNewFile();
      }
    }
    catch (IOException e){}
  }

  public void updateHighScoreLabel(){//checks if we have a new high score and change the label then.
    try{
      BufferedReader reader = new BufferedReader(new FileReader(highScoreFile));
      String highScore = reader.readLine();
      if (highScore==null){
        highScore = "0";
        writeHighScore(highScore);
      }
      control.highScoreLabel.setText("High Score: " + highScore);
      if (control.area.score>Integer.parseInt(highScore)){
        highScore = Integer.toString(control.area.score);
        writeHighScore(highScore);
        control.highScoreLabel.setText("New High Score: " + control.area.score + "!");
      }
      reader.close();
    }
    catch (IOException e){}
  }

  public void writeHighScore(String highScore) throws IOException{//if a new high score, change in the highscore file.
      BufferedWriter writer = new BufferedWriter(new FileWriter(highScoreFile));
      writer.write(highScore);
      writer.close();
  }
}

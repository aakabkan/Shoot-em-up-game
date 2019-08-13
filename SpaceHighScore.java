import java.io.*;

public class SpaceHighScore{

  private File highScoreFile;
  private SpaceGameMain main;

  public SpaceHighScore(SpaceGameMain main){
    this.main = main;
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
      main.highScoreLabel.setText("High Score: " + highScore);
      if (main.area.score>Integer.parseInt(highScore)){
        highScore = Integer.toString(main.area.score);
        writeHighScore(highScore);
        main.highScoreLabel.setText("New High Score: " + main.area.score + "!");
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

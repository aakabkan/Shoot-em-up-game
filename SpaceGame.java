import java.util.*;
import java.io.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.Timer.*;

public class SpaceGame extends JFrame implements ActionListener{
  JPanel spacePanel;
  private final static int frameSizeX=1900;
  private final static int frameSizeY=900;
  private int r=frameSizeY/25;//the size of the spaceship
  public int y, x=20;//the positions for the spaceship
  private Timer timer;
  private int interval=100;//time between every update in ms
  private int step=10;//number of pixels for a move
  private ArrayList<SpaceEnemy> enemies;//all existing enemies
  private ArrayList<SpaceShot> shots;//the placements of the shots
  private ArrayList<SpaceEnemy> destroyed;//the recently destroyed enemies
  private static final Color ORANGE = new Color(226,128,0);
  private boolean ended;//to check if we lost
  private int score;
  private JLabel scoreLabel;
  private JLabel highScoreLabel;
  private JButton newGame;
  private File file;//to read high score
  private boolean reachingEnd;//if we should create new enemies or just let them start over

  public SpaceGame(){
    file = new File("highscore.txt");
    try{
      if (!file.isFile()){
        file.createNewFile();
      }
    }
    catch (IOException e){}
    spacePanel = new SpacePanel();
    spacePanel.setPreferredSize(new Dimension(frameSizeX,frameSizeY));
    spacePanel.setBackground(Color.black);
    scoreLabel = new JLabel("Score: ");
    highScoreLabel = new JLabel("High Score: ");
    scoreLabel.setFont(new Font("Serif", Font.BOLD,16));
    highScoreLabel.setFont(new Font("Serif", Font.BOLD,16));
    init();
    JPanel bigPanel = new JPanel();
    JPanel subPanel = new JPanel();
    subPanel.setBackground(Color.gray);
    newGame = new JButton("Nytt spel");
    newGame.addActionListener(this);
    JLabel emptyLabel = new JLabel();
    emptyLabel.setPreferredSize(new Dimension(10,10));
    subPanel.add(scoreLabel);
    subPanel.add(emptyLabel);
    subPanel.add(newGame);
    subPanel.add(emptyLabel);
    subPanel.add(highScoreLabel);
    bigPanel.setLayout(new BoxLayout(bigPanel, BoxLayout.Y_AXIS));
    bigPanel.add(spacePanel);
    bigPanel.add(subPanel);
    add(bigPanel);
  }

  public void init(){//resets every value to the start position
    y=frameSizeX/4;
    enemies = new ArrayList();
    shots = new ArrayList();
    destroyed = new ArrayList();
    score = 0;
    ended = false;
    reachingEnd = false;
    scoreLabel.setText("Score: " + score);
    readHighScore();
  }

  public void readHighScore(){//to update the high score label
    try{
      BufferedReader reader = new BufferedReader(new FileReader(file));
      String highScore = reader.readLine();
      if (highScore==null){
        highScore = "0";
        writeHighScore(highScore);
      }
      highScoreLabel.setText("High Score: " + highScore);
      if (score>Integer.parseInt(highScore)){
        highScore = Integer.toString(score);
        writeHighScore(highScore);
        highScoreLabel.setText("New High Score: " + score + "!");
      }
      reader.close();
    }
    catch (IOException e){}
  }

  public void writeHighScore(String highScore){//help for the above method
    try{
      BufferedWriter writer = new BufferedWriter(new FileWriter(file));
      writer.write(highScore);
      writer.close();
    }
    catch (IOException e){}
  }

  class SpacePanel extends JPanel implements ActionListener, KeyListener{//for the game area
    public SpacePanel(){
      repaint();
      timer = new Timer(interval, this);
      timer.start();
      setFocusable(true);
      addKeyListener(this);
    }

    public void paintComponent(Graphics g){
      try{
        super.paintComponent(g);
        g.setColor(Color.green);
        g.fillOval(x,y,r,r/5);//this and next line paints our spaceship
        g.fillOval(x+r/3,y-r/4,r/5,2*r/3);
        if (!reachingEnd){//if the first enemies haven't crossed the entire screen
          if (Math.random()<(double)1/2){//creates new enemies at random time
            SpaceEnemy enemy = new SpaceEnemy(frameSizeX,frameSizeY);
            enemies.add(enemy);
          }
        }
        for (SpaceEnemy enemy : enemies){//checks where the enemies should be painted, only if their not destroyed
          if (!enemy.destroyed){
            g.setColor(enemy.color);
            g.fillPolygon(new int[] {enemy.x-r,enemy.x,enemy.x}, new int[] {enemy.y, enemy.y+r/2, enemy.y-r/2}, 3 );
            enemy.x-=2*step;
            if (enemy.x<-r){
                enemy.createNewEnemy();//to save memory we keep the same enemy-objects when we reaches the end
                reachingEnd = true;
            }
            for (SpaceShot shot : shots){//to see if they have been hit
                if (shot.shotY < enemy.y+r/2 && shot.shotY > enemy.y-r/2 && shot.shotX >= enemy.x){
                  enemy.hitPoint--;
                  shots.remove(shot);
                  if (enemy.hitPoint==0){
                    score++;
                    scoreLabel.setText("Score: " + score);
                    enemy.destroyed = true;
                    destroyed.add(enemy);
                  }
                }
            }
            if (enemy.y+r/2 >= y-r/3 && enemy.y+r/3 <= y+r/2 && enemy.x >= x && enemy.x <= x+r || enemy.y-r/2 >= y-r/3 && enemy.y-r/2 <= y+r/3 && enemy.x >= x && enemy.x <= x+r){//to see if our spaceship has been hit
              timer.stop();
              ended = true;
              g.setColor(ORANGE);
              g.fillOval(x,y-r/2,r,r);
              readHighScore();
            }
          }
        }
        for (SpaceEnemy enemy : destroyed){//paint exploded enemies
          enemy.bang--;
          g.setColor(ORANGE);
          g.fillOval(enemy.x,enemy.y-r/2,r,r);
          if (enemy.bang==0){
            destroyed.remove(enemy);
            enemy.createNewEnemy();
          }
        }
        for (SpaceShot shot : shots){//paint the shots
          g.setColor(ORANGE);
          g.fillOval(shot.shotX,shot.shotY,step/3,step/3);
          shot.shotX+=step;
          if (shot.shotX>frameSizeX){
              shots.remove(shot);
          }
        }
      }
      catch (ConcurrentModificationException e){}
    }

    public void actionPerformed(ActionEvent e) {//updates constantly with the timer
      repaint();
    }

    public void keyPressed(KeyEvent e){
      int key = e.getKeyCode();
      if (!ended){
        if (key == KeyEvent.VK_UP) {//move our space ship up
          if (y>step+r/3){
            y-=step;
          }
        }
        if (key == KeyEvent.VK_DOWN) {//move our space ship down
          if (y<frameSizeY-2*r/3-step){
            y+=step;
          }
        }
        if (key == KeyEvent.VK_RIGHT) {//move our space ship forward
          repaint();
        }
        if (key == KeyEvent.VK_SPACE) {//here we shoot
          shots.add(new SpaceShot(x+r,y));
        }
      }
    }
    public void keyReleased(KeyEvent e){}
    public void keyTyped(KeyEvent e){}
  }

  public static void main(String[] args){
    SwingUtilities.invokeLater(new Runnable(){
          public void run()
          {
            JFrame frame = new SpaceGame();
            frame.pack();
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          }
      });
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getSource().equals(newGame)){//starts a new game
      init();
      if (!timer.isRunning()){
        timer.restart();
      }
      spacePanel.repaint();
      spacePanel.requestFocus();
    }
  }
}

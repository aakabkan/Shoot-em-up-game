import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.Timer.*;
import javax.swing.JOptionPane.*;
import java.text.*;

public class SpaceController extends JFrame{
  public SpaceArea area;
  public SpaceListener listener;
  public SpaceHighScore highScoreObj;
  public Canvas spaceCanvas;
  public static int frameSizeX, frameSizeY;
  public Timer timer;
  private static final int interval=40;//time between every update in ms
  public JLabel scoreLabel, highScoreLabel;
  public JButton newGame, pauseBut, saveBut, saveAsBut, loadBut, qLoadBut;
  private JPanel subPanel, bigPanel;
  private JMenu menu;
  public File saveFile, allSaves;
  public JMenuItem itHow, itNew, itQSave, itSave, itLoad, itQLoad, itDelete;
  public boolean gameEnded;

  public SpaceController(){
    setFrameSize();
    listener = new SpaceListener(this);
    highScoreObj = new SpaceHighScore(this);
    createCanvas();
    createSavesFile();
    createSubPanel();
    createMenu();
    init();
    addToBigPanel();
  }

  public void setFrameSize(){
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    frameSizeY = screenSize.height-200;
    frameSizeX = screenSize.width;
  }

  public void init(){
    area = new SpaceArea(this);
    scoreLabel.setText("Score: " + area.score);
    highScoreObj.updateHighScoreLabel();
    pauseBut.setEnabled(true);
    saveBut.setEnabled(true); itSave.setEnabled(true);
    saveAsBut.setEnabled(true); itQSave.setEnabled(true);
  }

  public void createCanvas(){
    spaceCanvas = new SpaceCanvas();
    spaceCanvas.setPreferredSize(new Dimension(frameSizeX,frameSizeY));
    spaceCanvas.setBackground(Color.black);
  }

  public void createSavesFile(){
    allSaves = new File("saves.txt");
    try{
      if (!allSaves.isFile()){
        allSaves.createNewFile();
      }
    }
    catch(IOException e){}
  }

  public void createSubPanel(){
    scoreLabel = new JLabel("Score: ");
    highScoreLabel = new JLabel("High Score: ");
    Font labelFont = new Font("Serif", Font.BOLD,16);
    scoreLabel.setFont(labelFont);
    highScoreLabel.setFont(labelFont);
    subPanel = new JPanel();
    subPanel.setBackground(Color.gray);
    subPanel.add(scoreLabel);
    createButtons();
    subPanel.add(highScoreLabel);
  }

  public void createButtons(){
    pauseBut = new JButton("Pause");
    newGame = new JButton("New game (F1)");
    saveBut = new JButton("Quicksave (F2)");
    saveAsBut = new JButton("Save as (F3)");
    loadBut = new JButton("Load (F6)");
    qLoadBut = new JButton("Quickload (F7)");
    initButton(pauseBut); initButton(newGame); initButton(saveBut);
    initButton(saveAsBut); initButton(loadBut); initButton(qLoadBut);
  }

  public void initButton(JButton button){
    button.addActionListener(listener);
    subPanel.add(button);
  }

  public void createMenu(){
    JMenuBar menuBar  = new JMenuBar();;
    setJMenuBar(menuBar);
    menu = new JMenu("Menu");
    menuBar.add(menu);
    itHow = new JMenuItem("How to play");
    itNew = new JMenuItem("New game");
    itQSave = new JMenuItem("Quicksave");
    itSave = new JMenuItem("Save");
    itLoad = new JMenuItem("Load");
    itQLoad = new JMenuItem("Quickload");
    itDelete = new JMenuItem("Delete saves");
    addToMenu(itHow); addToMenu(itNew); addToMenu(itQSave); addToMenu(itSave); addToMenu(itLoad); addToMenu(itQLoad); addToMenu(itDelete);
  }

  public void addToMenu(JMenuItem it){
    it.addActionListener(listener);
    menu.add(it);
  }

  public void addToBigPanel(){
    bigPanel = new JPanel();
    bigPanel.setLayout(new BoxLayout(bigPanel, BoxLayout.Y_AXIS));
    bigPanel.add(spaceCanvas);
    bigPanel.add(subPanel);
    add(bigPanel);
  }

  class SpaceCanvas extends Canvas implements ActionListener{
    public SpaceCanvas(){
      repaint();
      timer = new Timer(interval, this);
      timer.start();
      setFocusable(true);
      addKeyListener(listener);
    }

    public void update(Graphics g){
      paint(g);
    }

    public void paint(Graphics g){
      g.drawImage(area.paintArea(createImage(frameSizeX, frameSizeY)), 0, 0, null);
    }

    public void actionPerformed(ActionEvent e) {
      repaint();
    }
  }
}

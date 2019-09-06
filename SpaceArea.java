import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class SpaceArea{
  private SpaceController control;
  public SpaceShip us;
  public int shipSize;
  private static final int goldWidth=15;
  private static final int beamWidth=10;
  private static final int initStep=10;//initial number of pixels for a move
  public int step;//number of pixels for a move
  public LinkedList<SpaceShip> enemies, bigEnemies, destroyed, allies;//all existing enemies and allies
  public LinkedList<SpaceArtifact> shots, boxes, golds, diamonds;//all existing artifacts
  public LinkedList<SpaceBeam> beams;//all existing beams
  private Graphics g2;
  public boolean updated;//will only allow one shot or move per repaint
  public int score;
  public boolean hasCatchedBox;
  private int beamRecentlyCreated;//is zero if no beam has recently been created, otherwise see for how long time it was
  private static final Color ORANGE = new Color(226,128,0);

  public SpaceArea(SpaceController control){
    this.control = control;
    createLists();
    setConditionsForShip();
    initVariables();
  }

  public void createLists(){
    enemies = new LinkedList<SpaceShip>();
    bigEnemies = new LinkedList<SpaceShip>();
    shots = new LinkedList<SpaceArtifact>();
    destroyed = new LinkedList<SpaceShip>();
    beams = new LinkedList<SpaceBeam>();
    boxes = new LinkedList<SpaceArtifact>();
    golds = new LinkedList<SpaceArtifact>();
    diamonds = new LinkedList<SpaceArtifact>();
    allies = new LinkedList<SpaceShip>();
  }

  public void setConditionsForShip(){
    us = new SpaceShip(control);
    shipSize = control.frameSizeY/25;
    us.setX(20);
    us.setY(control.frameSizeX/4);
    us.color=Color.green;
  }

  public void initVariables(){
    updated = true;
    score = 0;
    step = initStep;
    control.gameEnded = false;
    hasCatchedBox = false;
  }

  public Image paintArea(Image image){
    g2 = image.getGraphics();
    paintSpaceShip(us);
    checkIfEnemyShouldBeCreated();
    paintEnemies(enemies);
    paintEnemies(bigEnemies);
    paintEnemies(allies);
    paintExplodedEnemies();
    paintShots();
    paintBeams();
    paintArtifacts(boxes);
    paintArtifacts(golds);
    paintArtifacts(diamonds);
    updated = true;
    return image;
  }

  private void paintSpaceShip(SpaceShip ship){
    g2.setColor(ship.color);
    g2.fillOval(ship.getX(),ship.getY(),shipSize,shipSize/5);
    g2.fillOval(ship.getX()+shipSize/3,ship.getY()-shipSize/4,shipSize/5,2*shipSize/3);
    if (hasCatchedBox && ship.equals(us)){
      g2.setColor(Color.white);
      g2.drawRect(ship.getX(),ship.getY()-shipSize/4,shipSize,2*shipSize/3);
    }
  }

  private void checkIfEnemyShouldBeCreated(){
    if (beamRecentlyCreated == 0){
      double rand = Math.random();
      if (rand<1.0/5000){
        SpaceObject allied = createObject(true);
        ((SpaceShip)allied).hitPoint += 20;
        ((SpaceShip)allied).friendly = true;
        allies.add((SpaceShip)allied);
      }
      else if (rand<1.0/1500){
        beams.add(new SpaceBeam(control));
        beamRecentlyCreated = 4;
      }
      else if (rand<1.0/700){
        if (!hasCatchedBox){
          SpaceObject box = createObject(false);
          boxes.add((SpaceArtifact)box);
        }
      }
      else if (rand<1.0/550){
        SpaceObject diamond = createObject(false);
        diamonds.add((SpaceArtifact)diamond);
      }
      else if (rand<1.0/200){
        SpaceObject gold = createObject(false);
        golds.add((SpaceArtifact)gold);
      }
      else if (rand<1.0/4){
        SpaceObject enemy = createObject(true);
        if (Math.random()<1.0/4000){
          ((SpaceShip)enemy).hitPoint = 5*((SpaceShip)enemy).hitPoint + 50;
          ((SpaceShip)enemy).big = true;
          bigEnemies.add((SpaceShip)enemy);
        }
        else{
          enemies.add((SpaceShip)enemy);
        }
      }
    }
    else{
      beamRecentlyCreated--;
    }
  }

  private SpaceObject createObject(boolean ship){//creates either new enemy (ship==true), or requested artifact.
    boolean ok = false;
    SpaceObject obj = null;
    while (!ok){
      obj = ship ? new SpaceShip(control) : new SpaceArtifact(control);
      ok = checkIfEnemyIsTooClose(obj.getY());
    }
    return obj;
  }

  private boolean checkIfEnemyIsTooClose(int objY){//to check if it crashes with another enemy
    LinkedList<SpaceObject>[] lists = new LinkedList[] {enemies, boxes, golds, allies, bigEnemies};
    int width = shipSize;
    for (int i=0; i<lists.length; i++){
      if (lists.equals(bigEnemies)){
        width = 3*shipSize;
      }
      Iterator itr = lists[i].descendingIterator();
      while (itr.hasNext()){
        SpaceObject nextObj = (SpaceObject)itr.next();
        if (nextObj.getX()<control.frameSizeX-width){
          break;
        }
        if (objY > nextObj.getY()-shipSize && objY < nextObj.getY()+shipSize){
          return false;
        }
      }
    }
    return true;
  }

  private void paintEnemies(LinkedList<SpaceShip> enemyList){//paints both small and big enemies, also allies comes here but are requested to paintSpaceShip.
    if (!enemyList.isEmpty()){
      if ((enemyList.equals(enemies) && enemyList.getFirst().getX()<-shipSize) || (enemyList.equals(bigEnemies) && enemyList.getFirst().getX()<-3*shipSize)){
        enemyList.remove(enemyList.getFirst());
      }
    }
    for (SpaceShip enemy : enemyList){
      if (enemy.hitPoint>0){
        g2.setColor(enemy.color);
        if (enemy.hitPoint>0){
          if (enemyList.equals(enemies)){
            g2.fillPolygon(new int[] {enemy.getX()-shipSize,enemy.getX()-3*shipSize/5,enemy.getX()-3*shipSize/5,enemy.getX()-2*shipSize/5,enemy.getX(),enemy.getX(),enemy.getX()-2*shipSize/5,enemy.getX()-3*shipSize/5,enemy.getX()-3*shipSize/5},
                           new int[] {enemy.getY(), enemy.getY()+shipSize/5, enemy.getY()+shipSize/3, enemy.getY()+3*shipSize/10, enemy.getY()+shipSize/2, enemy.getY()-shipSize/2, enemy.getY()-3*shipSize/10, enemy.getY()-shipSize/3, enemy.getY()-shipSize/5}, 9);
          }
          else if (enemyList.equals(bigEnemies)){
            g2.fillPolygon(new int[] {enemy.getX()-shipSize, enemy.getX()-2*shipSize/5, enemy.getX(), enemy.getX()+shipSize/5, enemy.getX()+2*shipSize/5, enemy.getX()+8*shipSize/5, enemy.getX()+2*shipSize, enemy.getX()+2*shipSize, enemy.getX()+8*shipSize/5, enemy.getX()+2*shipSize/5, enemy.getX()+shipSize/5, enemy.getX(), enemy.getX()-2*shipSize/5, enemy.getX()-shipSize},
                           new int[] {enemy.getY()-2*shipSize/5, enemy.getY()-shipSize, enemy.getY()-shipSize, enemy.getY()-3*shipSize/5, enemy.getY()-shipSize, enemy.getY()-shipSize, enemy.getY()-2*shipSize/5, enemy.getY()+2*shipSize/5, enemy.getY()+shipSize, enemy.getY()+shipSize, enemy.getY()+3*shipSize/5, enemy.getY()+shipSize, enemy.getY()+shipSize, enemy.getY()+2*shipSize/5}, 14);
          }
          else if (enemyList.equals(allies)){
            paintSpaceShip(enemy);
          }
          reduceHitPointOnHit(enemy);
          if (enemy.getY()+(enemy.big ? shipSize : shipSize/2) >= us.getY()-(enemy.friendly ? shipSize/6 : shipSize/3) && enemy.getY()+(enemy.friendly ? shipSize/6 : shipSize/3) <= us.getY()+(enemy.big ? shipSize : shipSize/2) && enemy.getX()+(enemy.big ? 3*shipSize/2 : 0) >= us.getX() && enemy.getX() <= us.getX()+(enemy.big ? 3*shipSize/2 : 5*shipSize/4)
             || enemy.getY()-(enemy.big ? shipSize : shipSize/2) >= us.getY()-(enemy.friendly ? shipSize/6 : shipSize/3) && enemy.getY()-(enemy.big ? shipSize : shipSize/2) <= us.getY()+(enemy.friendly ? shipSize/6 : shipSize/3) && enemy.getX() >= us.getX() - (enemy.big ? 3*shipSize/2 : 0) && enemy.getX() <= us.getX()+(enemy.big ? 3*shipSize/2 : 5*shipSize/4)){
            weDied();
          }
        }
      }
      enemy.setX(enemy.getX()-step);
    }
  }

  private void reduceHitPointOnHit(SpaceShip enemy){//checks if the enemy ship has been hit.
    LinkedList<SpaceArtifact> toRemove = new LinkedList();
    for (SpaceArtifact shot : shots){
        if (shot.getY() < enemy.getY() + (enemy.big ? shipSize : (shipSize/2)) && shot.getY() > enemy.getY() - (enemy.big ? shipSize : (shipSize/2)) && shot.getX() >= enemy.getX()){
          enemy.hitPoint--;
          toRemove.add(shot);
          if (enemy.hitPoint==0){
            if (enemy.big){
              updateScore(100);
            }
            else if (enemy.friendly){
              updateScore(score>150 ? 150 : score);
            }
            else{
              updateScore(1);
            }
            destroyed.add(enemy);
          }
        }
    }
    for (SpaceArtifact shot : toRemove){
      shots.remove(shot);
    }
  }

  private void weDied(){//stops the game.
      control.timer.stop();
      control.gameEnded = true;
      g2.setColor(ORANGE);
      g2.fillOval(us.getX(),us.getY()-13*shipSize/25,5*shipSize/4,5*shipSize/4);
      control.highScoreObj.updateHighScoreLabel();
      control.pauseBut.setEnabled(false);
      control.saveBut.setEnabled(false); control.itSave.setEnabled(false);
      control.saveAsBut.setEnabled(false); control.itQSave.setEnabled(false);
  }

  private void paintExplodedEnemies(){//paints the orange circles.
    g2.setColor(ORANGE);
    LinkedList<SpaceShip> toRemove = new LinkedList();
    for (SpaceShip enemy : destroyed){
      enemy.bang--;
      g2.fillOval(enemy.getX()-5*shipSize/8,enemy.getY()-5*shipSize/8,5*shipSize/4,5*shipSize/4);
      if (enemy.bang==0){
        toRemove.add(enemy);
      }
    }
    removeObj(toRemove, destroyed);
    removeObj(toRemove, enemies);
  }

  private void paintShots(){
    g2.setColor(ORANGE);
    if (!shots.isEmpty()){
      if (shots.getFirst().getX()>control.frameSizeX){
        shots.remove(shots.getFirst());
      }
    }
    for (SpaceArtifact shot : shots){
      g2.fillOval(shot.getX(),shot.getY(),step/3,step/3);
      shot.setX(shot.getX()+step);
    }
  }

  private void paintBeams(){//paints the beam and test if we crashed in it.
    LinkedList<SpaceBeam> toRemove = new LinkedList();
    for (SpaceBeam beam : beams){
      if (beam.getX()<-shipSize){
          toRemove.add(beam);
      }
      if (beam.light>30){
        g2.setColor(new Color(51,154,255));
        g2.fillRect(beam.getX(),0,beamWidth,control.frameSizeY);
        if (us.getX()+shipSize > beam.getX() && us.getX() < beam.getX()+beamWidth){
          if (!hasCatchedBox){
            weDied();
          }
          else{
            hasCatchedBox = false;
            toRemove.add(beam);
          }
        }
      }
      beam.setX(beam.getX()-step);
      beam.light=(39+beam.light)%40;
    }
    removeObj(toRemove, beams);
  }

  private void paintArtifacts(LinkedList<SpaceArtifact> list){//paints golds, diamonds and boxes; checks if the artifact has been catched.
      LinkedList<SpaceArtifact> toRemove = new LinkedList();
      for (SpaceArtifact art : list){
        if (art.getX()<-shipSize){
            toRemove.add(art);
        }
        else if (list.equals(boxes)){
            g2.setColor(Color.white);
            g2.drawRect(art.getX(),art.getY(),shipSize,3*shipSize/4);
            if (art.getX() < us.getX()+shipSize && us.getX() < art.getX() + shipSize && art.getY() < us.getY() + 5*shipSize/12 && us.getY() < art.getY() + 13*shipSize/12){
              toRemove.add(art);
              hasCatchedBox = true;
            }
        }
        else{
            if (list.equals(golds)){
              g2.setColor(new Color(166,124,0));
              g2.fillPolygon(new int[] {art.getX(),art.getX()+goldWidth/5,art.getX()+4*goldWidth/5,art.getX()+goldWidth,art.getX()+goldWidth/2},new int[] {art.getY(),art.getY()+goldWidth/2,art.getY()+goldWidth/2,art.getY(),art.getY()-goldWidth/3},5);
            }
            else{
              g2.setColor(new Color(154,181,178));
              g2.fillPolygon(new int[] {art.getX(),art.getX(),art.getX()+goldWidth/2,art.getX()+goldWidth,art.getX()+goldWidth,art.getX()+goldWidth/2},new int[] {art.getY(),art.getY()+goldWidth/2,art.getY()+goldWidth,art.getY()+goldWidth/2,art.getY(),art.getY()-goldWidth/2},6);
            }
            if (us.getY() < art.getY()+5*shipSize/12+(list.equals(diamonds) ? goldWidth/2 : 0) && us.getY() > art.getY()-goldWidth-shipSize/4 && us.getX() > art.getX()-shipSize/2 && us.getX() < art.getX()+goldWidth+shipSize/2){
              toRemove = catchedArtifact(toRemove,art,list.equals(diamonds));
            }
        }
        art.setX(art.getX()-step);
      }
      removeObj(toRemove, list);
  }

  private LinkedList<SpaceArtifact> catchedArtifact(LinkedList<SpaceArtifact> toRemove, SpaceArtifact art, boolean diamond){
    toRemove.add(art);
    updateScore(diamond ? 15 : 5);
    return toRemove;
  }

  private void removeObj(LinkedList toRemove, LinkedList list){
      for (Object obj : toRemove){
        list.remove((SpaceObject)obj);
      }
  }

  private void updateScore(int addScore){
    score+=addScore;
    step = initStep + score/100;
    control.scoreLabel.setText("Score: " + score);
  }
}

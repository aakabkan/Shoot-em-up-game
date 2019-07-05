import java.util.*;
import java.lang.*;
import java.awt.*;

public class SpaceEnemy{
  public int x, y;//coordinates
  public int initBang=2;
  public int bang;//how long the bang will be seen
  public int hitPoint;//how many hits it is able to take
  public Color color;
  public int frameSizeX, frameSizeY;
  public boolean destroyed = false;

  public SpaceEnemy(int fsx,int fsy){
    frameSizeX = fsx;
    frameSizeY = fsy;
    createNewEnemy();
  }

  public void createNewEnemy(){
    bang=initBang;
    destroyed = false;
    hitPoint=(int)(8*Math.random())+1;
    x=frameSizeX;
    y=(int)(frameSizeY*Math.random());
    color = new Color((int)(255*Math.random()),(int)(255*Math.random()),(int)(255*Math.random()));
  }
}

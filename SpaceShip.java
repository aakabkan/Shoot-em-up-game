import java.awt.*;

public class SpaceShip extends SpaceObject{
  private int x, y;//position
  public int hitPoint;//how many hits it is able to take
  public static final int initBang=2;
  public int bang;//how long the bang will be seen
  public Color color;
  public boolean big;
  public boolean friendly;

  public SpaceShip(SpaceController control){
    hitPoint=(int)(10*Math.random())+1;
    x=control.frameSizeX;
    y=(int)(control.frameSizeY*Math.random());
    giveColor();
  }

  public SpaceShip(int x, int y, int hitPoint){
    this.x=x;
    this.y=y;
    this.hitPoint = hitPoint;
    giveColor();
  }

  private void giveColor(){
    bang=initBang;
    color = new Color((int)(255*Math.random()),(int)(255*Math.random()),(int)(255*Math.random()));
  }

  @Override
  public int getX(){return x;}

  @Override
  public int getY(){return y;}

  @Override
  public void setX(int x){
    this.x=x;
  }

  @Override
  public void setY(int y){
    this.y=y;
  }
}

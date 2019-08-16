public class SpaceBeam extends SpaceObject{
  private int x;//coordinate
  public static final int width = 10;
  public int light;

  public SpaceBeam(SpaceController control){
    x = control.frameSizeX;
  }

  public SpaceBeam(int x){
    this.x = x;
  }

  @Override
  public int getX(){return x;}

  @Override
  public int getY(){return 0;}

  @Override
  public void setX(int x){
    this.x=x;
  }

  @Override
  public void setY(int y){}
}

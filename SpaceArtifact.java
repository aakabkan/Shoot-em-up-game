public class SpaceArtifact extends SpaceObject{
  private int x, y;//coordinates

  public SpaceArtifact(SpaceController control){
    x=control.frameSizeX;
    y=(int)(control.frameSizeY*Math.random());
  }

  public SpaceArtifact(int x, int y){
    this.x=x;
    this.y=y;
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

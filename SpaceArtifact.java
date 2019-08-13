public class SpaceArtifact extends SpaceObject{
  private int x, y;//coordinates

  public SpaceArtifact(SpaceGameMain main){
    x=main.frameSizeX;
    y=(int)(main.frameSizeY*Math.random());
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

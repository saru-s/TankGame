package tankgame.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Wall extends GameObject implements Collidable{
    protected Rectangle hitBox;

    public Wall(float y, float x, BufferedImage img){
        super(x, y, img);
        this.hitBox = new Rectangle((int)x, (int)y, this.img.getWidth(), this.img.getHeight());
    }

    @Override
    public String toString(){
        return "Wall{" + "y=" + y + ", x=" + x + "}";
    }

    @Override
    public void drawImage(Graphics g){
        Graphics2D buffer = (Graphics2D) g;
        buffer.drawImage(img, (int)x, (int)y, null);
        buffer.setColor(Color.GRAY);
        buffer.drawRect((int)x, (int)y, this.img.getWidth(), this.img.getHeight());
    }

    @Override
    public void update(GameWorld gw){this.hitBox.setLocation((int) x,(int)y);}

    @Override
    public Rectangle getHitBox(){
        return this.hitBox.getBounds();
    }

    @Override
    public void handleCollision(Collidable with){}

}



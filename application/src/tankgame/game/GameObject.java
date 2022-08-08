package tankgame.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class GameObject {
    protected float x, y;

    boolean alive;
    protected BufferedImage img;
    protected Rectangle hitBox;
    public GameObject(float x, float y, BufferedImage img){
        this.x = x;
        this.y = y;
        this.img = img;
        this.alive = true;
        this.hitBox = new Rectangle((int)x, (int)y, this.img.getWidth(), this.img.getHeight());
    }

    public float getX(){
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public boolean isAlive() {
        return this.alive;
    }


    public abstract void drawImage(Graphics g);

    public abstract void update(GameWorld gw);

    public Rectangle getHitBox(){
        return hitBox.getBounds();
    }
}
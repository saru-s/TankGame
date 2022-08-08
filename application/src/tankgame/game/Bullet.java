package tankgame.game;

import tankgame.GameConstants;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Bullet extends GameObject implements Collidable{
    private float angle;
    private String ownerTank;
    float scaleFactor = 1f;
    private float R = 2f;
    private Rectangle hitBox;
    public String getOwnerTank(){return this.ownerTank;}


    Bullet(float x, float y, float angle, float charge, BufferedImage img, String ownerTank) {
        super(x, y, img);
        this.scaleFactor = charge;
        this.angle = angle;
        this.ownerTank = ownerTank;
        this.hitBox = new Rectangle((int) x, (int) y, this.img.getWidth(), this.img.getHeight());
    }

    void setPosition(float x, float y, float angle){
        this.x = x;
        this.y = y;
        this.angle = angle;
        //add hitbox move
    }

    @Override
    public void update(GameWorld gw){
        if(this.alive){
            moveForwards();
        }else{
            gw.gameObjects.remove(this);
        }
        this.hitBox.setLocation((int) x,(int)y);
    }



    private void moveForwards() {
        x += Math.round(R * Math.cos(Math.toRadians(angle)));
        y += Math.round(R * Math.sin(Math.toRadians(angle)));
        //checkBorder();
    }


    private void checkBorder() {
        if (x < 30) {
            x = 30;
        }
        if (x >= GameConstants.WORLD_WIDTH - 88) {
            x = GameConstants.WORLD_WIDTH - 88;
        }
        if (y < 40) {
            y = 40;
        }
        if (y >= GameConstants.WORLD_HEIGHT - 80) {
            y = GameConstants.WORLD_HEIGHT - 80;
        }


    }

    @Override
    public String toString() {
        return "x=" + x + ", y=" + y + ", angle=" + angle;
    }


    @Override
    public void drawImage(Graphics g) {
        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle), this.img.getWidth() / 2.0, this.img.getHeight() / 2.0);
        rotation.scale(scaleFactor, scaleFactor);
        Graphics2D g2d = (Graphics2D) g;

        g2d.drawImage(this.img, rotation, null);
        g2d.setColor(Color.LIGHT_GRAY);
        //g2d.rotate(Math.toRadians(angle), bounds.x + bounds.width/2, bounds.y + bounds.height/2);
        g2d.drawRect((int)x,(int)y,this.img.getWidth()*(int)this.scaleFactor, this.img.getHeight()*(int)this.scaleFactor);

    }

    public void setScaleFactor(float scaleFactor) {
        if(scaleFactor >= 15f) scaleFactor = 15f;
        this.scaleFactor = scaleFactor;
    }

    @Override
    public void handleCollision(Collidable with) {
        if(with instanceof Tank) {
            if(((Tank)with).getTankName().equals(this.ownerTank)){
                return;
            }
        }
            this.x = -30;
            this.y = -30;
            this.alive = false;



    }

    @Override
    public Rectangle getHitBox(){
        return this.hitBox.getBounds();
    }

}

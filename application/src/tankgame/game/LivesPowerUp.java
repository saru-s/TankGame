package tankgame.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class LivesPowerUp extends GameObject implements Collidable{
    private Rectangle hitBox;

    public LivesPowerUp(float x, float y, BufferedImage img){
        super(x, y, img);
        this.hitBox = new Rectangle((int)x, (int)y, img.getWidth(), img.getHeight());
    }

    @Override
    public void update(GameWorld gw){}


    @Override
    public void handleCollision(Collidable with){
        if(with instanceof Tank && ((Tank) with).getLives() < 4){
            this.alive = false;
            ((Tank) with).setLives(0);
        }
    }


    @Override
    public void drawImage(Graphics g){
        Graphics2D buffer = (Graphics2D) g;
        buffer.drawImage(img, (int)x, (int)y, null);
        buffer.setColor(Color.GRAY);
        buffer.drawRect((int)x, (int)y, this.img.getWidth(), this.img.getHeight());
    }

    @Override
    public Rectangle getHitBox(){
        return this.hitBox.getBounds();
    }

}

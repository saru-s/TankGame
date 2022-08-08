package tankgame.game;

import java.awt.image.BufferedImage;

public class Breakable extends Wall implements Collidable{
    private int life = 2;

    public Breakable(float y, float x, BufferedImage img){
        super(y, x, img);
    }

    public void depleteHealth(){
        if(life > 0){
            life--;
        }else{
            life = 0;
            this.alive = false;
        }
    }


    @Override
    public void handleCollision(Collidable with) {
        if(with instanceof Bullet){
            if(this.alive){
                depleteHealth();
            }
        }
    }


}

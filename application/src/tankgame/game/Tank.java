package tankgame.game;

import tankgame.GameConstants;
import tankgame.Resources;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author anthony-pc
 */
public class Tank extends GameObject implements Collidable{
    private float vx;
    private float vy;
    private float angle;

    int health = 100;
    int lives = 3;

    private int initX, initY, prevX, prevY;

    private float R = 5;
    private float ROTATIONSPEED = 3.0f;

    float fireDelay = 120f;
    float coolDown = 0f;
    float rateOfFire = 1f;

    float charge = 1f;
    float chargeRate = .05f;

    private boolean UpPressed;
    private boolean DownPressed;
    private boolean RightPressed;
    private boolean LeftPressed;

    private boolean shootPressed;

    private Rectangle hitBox;

    private String tankName;
    private int count = 0;

    List<Bullet> ammo = new ArrayList<>();
    Bullet b;




    Tank(float x, float y, float vx, float vy, float angle, BufferedImage img, String tankName) {
       super(x, y, img);
        this.vx = vx;
        this.vy = vy;
        this.angle = angle;
        this.initX = (int)x;
        this.initY = (int)y;
        this.tankName = tankName;
        this.hitBox = new Rectangle((int) x, (int) y, this.img.getWidth(), this.img.getHeight());
    }

    void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        this.hitBox.setLocation((int) x, (int) y);
    }

    public String getTankName(){return this.tankName;}
    public void setSpeed(int value) {
        this.R += value;
    }

    public void setHealth(int value) {
        this.health = value;
    }

    public void setLives(int value) {
        this.lives += value;
    }


    public int getHealth(){return this.health;}
    public int getLives(){return this.lives;}

    void toggleUpPressed() {
        this.UpPressed = true;
    }

    void toggleDownPressed() {
        this.DownPressed = true;
    }

    void toggleRightPressed() {
        this.RightPressed = true;
    }

    void toggleLeftPressed() {
        this.LeftPressed = true;
    }

    void unToggleUpPressed() {
        this.UpPressed = false;
    }

    void unToggleDownPressed() {
        this.DownPressed = false;
    }

    void unToggleRightPressed() {
        this.RightPressed = false;
    }

    void unToggleLeftPressed() {
        this.LeftPressed = false;
    }

    void toggleShootPressed() {
        this.shootPressed = true;
    }

    void unToggleShootPressed() {
        this.shootPressed = false;
    }

    @Override
    public void update(GameWorld gw) {
        this.prevX = (int)x;
        this.prevY = (int)y;

        if (this.UpPressed) {
            this.moveForwards();
        }

        if (this.DownPressed) {
            this.moveBackwards();
        }

        if (this.LeftPressed) {
            this.rotateLeft();
        }

        if (this.RightPressed) {
            this.rotateRight();
        }

        if (this.shootPressed && this.coolDown >= this.fireDelay) {
            this.coolDown = 0;
            b = new Bullet(this.setBulletStartX(), this.setBulletStartY(), angle, 1f, Resources.getImage("bullet"), this.tankName);
            gw.gameObjects.add(b);
            this.ammo.add(b);
        }
        this.coolDown += this.rateOfFire;

      //  this.ammo.forEach(b -> b.update(gw));

            this.setPosition((int)x, (int)y);
    }


    private int setBulletStartX() {
        float cx = 29f * (float) Math.cos(Math.toRadians(angle));
        return (int) x + this.img.getWidth() / 2 + (int) cx - 4;
    }

    private int setBulletStartY() {
        float cy = 29f * (float) Math.sin(Math.toRadians(angle));
        return (int) y + this.img.getHeight() / 2 + (int) cy - 4;
    }


    private void rotateLeft() {
        this.angle -= this.ROTATIONSPEED;
    }

    private void rotateRight() {
        this.angle += this.ROTATIONSPEED;
    }

    private void moveBackwards() {
        vx = Math.round(R * Math.cos(Math.toRadians(angle)));
        vy = Math.round(R * Math.sin(Math.toRadians(angle)));
        x -= vx;
        y -= vy;
        checkBorder();
        this.hitBox.setLocation((int) x, (int) y);
    }

    private void moveForwards() {
        vx = Math.round(R * Math.cos(Math.toRadians(angle)));
        vy = Math.round(R * Math.sin(Math.toRadians(angle)));
        x += vx;
        y += vy;
        checkBorder();
        this.hitBox.setLocation((int) x, (int) y);
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

    private void depleteHealth(){
        if(health > 0){
            health-=5;
        }else{
            if(lives > 0 && health == 0){
                lives--;
                x = initX;
                y = initY;
                health = 100;
            }
            if(lives == 0){
                alive = false;
            }
        }
    }


    @Override
    public void handleCollision(Collidable with){
        if(with instanceof Wall || (with instanceof Tank && !((Tank)with).getTankName().equals(this.tankName))){
            if(this.UpPressed){
                moveBackwards();
            }else{
                moveForwards();
            }
            this.hitBox.setLocation((int)x,(int)y);
            System.out.println("tank hit a wall");
        }
        if(with instanceof Bullet && !Objects.equals(((Bullet) with).getOwnerTank(), this.tankName)){
            this.depleteHealth();
        }
        if(with instanceof SpeedPowerUp){
            this.setSpeed(3);
        }
        if(with instanceof LivesPowerUp && lives < 4){
            this.setLives(1);
        }
        if(with instanceof HealthPowerUp && health < 100){
            this.setHealth(100);
        }

    }

    

    @Override
    public void drawImage(Graphics g) {
        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle), this.img.getWidth() / 2.0, this.img.getHeight() / 2.0);
        Graphics2D g2d = (Graphics2D) g;

            g2d.drawImage(this.img, rotation, null);

            g2d.setColor(Color.ORANGE);
            g2d.drawRect((int) x, (int) y, this.img.getWidth(), this.img.getHeight());

            //health bar
            g2d.setColor(Color.BLUE);
            g2d.drawRect((int) x, (int) y - 30, 85, 15);
            if (this.health >= 70) g2d.setColor(Color.GREEN);
            else if (this.health >= 50) g2d.setColor(Color.YELLOW);
            else g2d.setColor(Color.RED);
            g2d.fillRect((int) x, (int) y - 30, 85, 15);
            g2d.drawString("" + this.health, (int) x, (int) y - 35);

            //lives count
            for (int i = 0; i < this.lives; i++) {
                g2d.drawOval((int)x+(i*20), (int)y+55, 15, 15);
                g2d.fillOval((int)x+(i*20), (int)y+55, 15, 15);
        }

    }


    @Override
    public Rectangle getHitBox(){
        return this.hitBox.getBounds();
    }
}
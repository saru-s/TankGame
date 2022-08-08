/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tankgame.game;


import tankgame.GameConstants;
import tankgame.Launcher;
import tankgame.Resources;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * @author anthony-pc
 */
public class GameWorld extends JPanel implements Runnable {

    private BufferedImage world;

    private Tank t1;
    private Tank t2;
    private Launcher lf;
    private long tick = 0;
    boolean p1Won = false;
    boolean p2Won = false;


    List<Wall> walls = new ArrayList<>();
    List<GameObject> gameObjects = new ArrayList<>();
    List<Collidable> colliding = new ArrayList<>();
//    public void addGameObject(GameObject obj){
//        this.gameObjects.add(obj);
//    }



    /**
     * 
     * @param lf
     */
    public GameWorld(Launcher lf) {
        this.lf = lf;
        try {

            world = ImageIO.read(this.getClass().getClassLoader().getResource("Background.bmp"));
        } catch (IOException e) {
            System.out.println("Error cant read menu background");
            e.printStackTrace();
            System.exit(-3);
        }
    }

    @Override
    public void run() {

        try {
            this.resetGame();
            while (true) {
                this.tick++;
                this.t1.update(this); // update tank
                this.t2.update(this); //
                this.gameUpdate();

                this.checkCollisions();
                this.repaint();   // redraw game
//                if(p1Won || p2Won){
//                    break;
//                }

                
                /*
                 * Sleep for 1000/144 ms (~6.9ms). This is done to have our 
                 * loop run at a fixed rate per/sec. 
                */
                Thread.sleep(1000 / 144);


            }
        } catch (InterruptedException ignored) {
            System.out.println(ignored);
        }
    }

    /**
     * Reset game to its initial state.
     */
    public void resetGame() {
        this.tick = 0;
        this.t1.setPosition(500, 100);
        this.t2.setPosition(1200, 100);
    }

    /**
     * Load all resources for Tank Wars Game. Set all Game Objects to their
     * initial state as well.
     */
    public void InitializeGame() {
        Resources.initImages();
        this.world = new BufferedImage(GameConstants.WORLD_WIDTH,
                GameConstants.WORLD_HEIGHT,
                BufferedImage.TYPE_INT_RGB);


        t1 = new Tank(300, 300, 0, 0, (short) 0, Resources.getImage("tank1"), "Player 1");
        gameObjects.add(t1);
        TankControl tc1 = new TankControl(t1, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SPACE);
        t2 = new Tank(300, 300, 0, 0, (short) 0, Resources.getImage("tank2"), "Player 2");
        gameObjects.add(t2);
        TankControl tc2 = new TankControl(t2, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_ENTER);
        this.lf.getJf().addKeyListener(tc1);
        this.lf.getJf().addKeyListener(tc2);

        try(BufferedReader mapReader = new BufferedReader(new InputStreamReader(GameWorld.class.getClassLoader().getResourceAsStream("map.csv")))){
            String[] size = mapReader.readLine().split(",");
            for(int i = 0; mapReader.ready(); i++){
                String[] items = mapReader.readLine().split(",");
                for (int j = 0; j < items.length; j++) {
                    switch(items[j]){
                        case"3", "9" -> {
                            Wall w = new Wall(i*30, j*30, Resources.getImage("unbreak"));
                            walls.add(w);
                            gameObjects.add(w);
                            colliding.add(w);
                        }
                        case"2" -> {
                            Breakable bw = new Breakable(i*30, j*30, Resources.getImage("break"));
                            walls.add(bw);
                            gameObjects.add(bw);
                            colliding.add(bw);
                        }
                        case"4" -> {
                            SpeedPowerUp spu = new SpeedPowerUp(i*30, j*30, Resources.getImage("speed"));
                            gameObjects.add(spu);
                            colliding.add(spu);
                        }
                        case"5" -> {
                            HealthPowerUp hpu = new HealthPowerUp(i*30, j*30, Resources.getImage("health"));
                            gameObjects.add(hpu);
                            colliding.add(hpu);
                        }
                        case"6" -> {
                            LivesPowerUp lpu = new LivesPowerUp(i*30, j*30, Resources.getImage("lives"));
                            gameObjects.add(lpu);
                            colliding.add(lpu);
                        }
                    }
                }
            }
        }catch(IOException e){
            System.out.println(e);
            System.exit(-2);
        }
    }


    //X-coordinate for split screen
    private int getXCoordinate(Tank t){
        int x = (int) t.getX();
        if(x < GameConstants.GAME_SCREEN_WIDTH / 4)
            x = GameConstants.GAME_SCREEN_WIDTH / 4;
        if(x > GameConstants.GAME_SCREEN_WIDTH - GameConstants.GAME_SCREEN_WIDTH / 4)
            x = GameConstants.GAME_SCREEN_WIDTH - GameConstants.GAME_SCREEN_WIDTH / 4;
        return x;
    }

    //Y coordinate for split screen
    public int getYCoordinate(Tank t){
        int y = (int) t.getY();
        if(y < GameConstants.GAME_SCREEN_HEIGHT / 2)
            y = GameConstants.GAME_SCREEN_HEIGHT / 2;
        if(y > GameConstants.GAME_SCREEN_HEIGHT - GameConstants.GAME_SCREEN_HEIGHT / 2)
            y = GameConstants.GAME_SCREEN_HEIGHT - GameConstants.GAME_SCREEN_HEIGHT / 2;
        return y ;
    }

    //Draw floor tiles
    void drawFloor(Graphics2D buffer){
        for(int i =0; i < GameConstants.WORLD_WIDTH; i+=320){
            for(int j = 0; j < GameConstants.WORLD_HEIGHT; j+=240){
                buffer.drawImage(Resources.getImage("floor"), i, j, null);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Graphics2D buffer = world.createGraphics();
        buffer.setColor(Color.BLACK);
        buffer.fillRect(0, 0, GameConstants.WORLD_WIDTH, GameConstants.WORLD_HEIGHT);
        drawFloor(buffer);

        for(int i = 0; i < gameObjects.size(); i++){
            gameObjects.get(i).drawImage(buffer);
        }
        //Split Screem
        BufferedImage lh = world.getSubimage(getXCoordinate(t1) - GameConstants.GAME_SCREEN_WIDTH / 4, getYCoordinate(t1) - GameConstants.GAME_SCREEN_HEIGHT / 2, GameConstants.GAME_SCREEN_WIDTH / 2, GameConstants.GAME_SCREEN_HEIGHT);
        BufferedImage rh = world.getSubimage(getXCoordinate(t2) - GameConstants.GAME_SCREEN_WIDTH / 4, getYCoordinate(t2) - GameConstants.GAME_SCREEN_HEIGHT / 2, GameConstants.GAME_SCREEN_WIDTH / 2, GameConstants.GAME_SCREEN_HEIGHT);
        g2.drawImage(lh, 0, 0, null);
        g2.drawImage(rh, GameConstants.GAME_SCREEN_WIDTH/2, 0, null);
        //Minimap
        BufferedImage mm = world.getSubimage(0, 0, GameConstants.WORLD_WIDTH, GameConstants.WORLD_HEIGHT);
        g2.scale(.2, .2);
        g2.drawImage(mm, 2500, 2500, null);
        /**
        // at = new AffineTransform();
        //at.translate(GameConstants.GAME_SCREEN_WIDTH/2f - (GameConstants.WORLD_WIDTH*.2)/2f,
        //                 GameConstants.GAME_SCREEN_HEIGHT - GameConstants.WORLD_HEIGHT *.2);
       // at.scale(.2, .2);
       // g2.drawImage(mm, at, null);
       **/

        //Display Winners
        if(p1Won || p2Won){
//            g2.setFont(new Font("SansSerif", Font.BOLD, 80));
//            g2.setColor(Color.WHITE);
//            g2.drawString("GAME OVER ", GameConstants.GAME_SCREEN_WIDTH / 4 + 50, GameConstants.GAME_SCREEN_HEIGHT / 4);
            if(p1Won) {
                g2.drawString("PLAYER1 WINS! ", GameConstants.GAME_SCREEN_WIDTH / 4, GameConstants.GAME_SCREEN_HEIGHT / 2);
 //               Thread.sleep(1000 / 144);
//                this.InitializeGame();
//                this.lf.setFrame("end");
            }
            if(p2Won) {
                g2.drawString("PLAYER2 WINS! ", GameConstants.GAME_SCREEN_WIDTH / 4, GameConstants.GAME_SCREEN_HEIGHT / 2);
//                this.InitializeGame();
//                this.lf.setFrame("end");
            }
        }
    }

    //Update all game objects
    private void gameUpdate(){
        for(int i = 0; i < gameObjects.size(); i++){
            if(gameObjects.get(i).isAlive()){
                gameObjects.get(i).update(this);
            }else{
                if(gameObjects.get(i) instanceof Tank){
                    if(((Tank)gameObjects.get(i)).getTankName().equals("Player 2")){
                        p1Won = true;
                        this.InitializeGame();
                        this.lf.setFrame("end");


                    }else{
                        p2Won = true;
                        this.InitializeGame();
                        this.lf.setFrame("end");


                    }
                }
                gameObjects.remove(i);
                i--;
            }
        }
    }

    //Check collision
    private void checkCollisions(){
        for(int i = 0; i < gameObjects.size(); i++){
            Collidable co1 = (Collidable) gameObjects.get(i);
            for(int j = 0; j < gameObjects.size(); j++){
                if(i==j)continue;
                Collidable co2 = (Collidable) gameObjects.get(j);
                if(co1.getHitBox().getBounds().intersects(co2.getHitBox().getBounds())){
                    co1.handleCollision(co2);
                    co2.handleCollision(co1);
                }
            }
        }
    }

//    private void checkCollisions(){
//        int count = 0;
//        for (int i = 0; i < this.colliding.size(); i++) {
//            Collidable c = this.colliding.get(i);
//            if(c instanceof Wall) continue;
//            for (int j = 0; j < this.colliding.size(); j++) {
//                if(j == i) continue;
//                Collidable co = this.colliding.get(j);
//                if(c instanceof Tank && co instanceof Bullet){
//                    final Tank t = (Tank)c;
//                }else if(c instanceof Bullet && co instanceof Tank){
//                    final Tank t = (Tank)co;
//                }else if(c instanceof Bullet && co instanceof Wall){
//                    final Wall w = (Wall)co;
//                }else if(c instanceof Tank && (co instanceof SpeedPowerUp || co instanceof HealthPowerUp || co instanceof LivesPowerUp)){
//                    final Tank t = (Tank)c;
//                }
//                if(co.getClass() == c.getClass()) continue;
//                if(c.getHitBox().getBounds().intersects(co.getHitBox().getBounds())){
//                    c.handleCollision(co);
//                }
//            }
//        }
//    }






}


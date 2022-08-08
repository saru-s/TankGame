package tankgame;

import tankgame.game.GameWorld;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Resources {
    private static Map<String, BufferedImage> images = new HashMap<>();
    private static Map<String, Clip> sounds = new HashMap<>();
    private static Map<String, List<BufferedImage>> animations = new HashMap();


    public static BufferedImage getImage(String key){
        return Resources.images.get(key);
    }

    public static Clip getSound(String key){
        return Resources.sounds.get(key);
    }

    public static List<BufferedImage> getAnimation(String key){
        return Resources.animations.get(key);
    }


    public static void initImages(){
        try {
            Resources.images.put("tank1", ImageIO.read(GameWorld.class.getClassLoader().getResource("tank1.png")));
            Resources.images.put("tank2", ImageIO.read(GameWorld.class.getClassLoader().getResource("tank2.png")));
            Resources.images.put("floor", ImageIO.read(GameWorld.class.getClassLoader().getResource("Background.bmp")));
            Resources.images.put("unbreak", ImageIO.read(GameWorld.class.getClassLoader().getResource("unbreak.png")));
            Resources.images.put("break", ImageIO.read(GameWorld.class.getClassLoader().getResource("break.png")));
            Resources.images.put("bullet", ImageIO.read(GameWorld.class.getClassLoader().getResource("bullet.png")));
            Resources.images.put("health", ImageIO.read(GameWorld.class.getClassLoader().getResource("health.png")));
            Resources.images.put("speed", ImageIO.read(GameWorld.class.getClassLoader().getResource("speed.png")));
            Resources.images.put("lives", ImageIO.read(GameWorld.class.getClassLoader().getResource("lives.png")));
            Resources.images.put("large", ImageIO.read(GameWorld.class.getClassLoader().getResource("large.png")));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void initSounds(){
        try{
            AudioInputStream as;
            Clip clip;

            as = AudioSystem.getAudioInputStream(Resources.class.getClassLoader().getResource("launch.wav"));
            clip = AudioSystem.getClip();
            clip.open(as);
            Resources.sounds.put("bullet", clip);

            as = AudioSystem.getAudioInputStream(Resources.class.getClassLoader().getResource("explosion.wav"));
            clip = AudioSystem.getClip();
            clip.open(as);
            Resources.sounds.put("explosion", clip);
        }catch(IOException | UnsupportedAudioFileException | LineUnavailableException e){
            System.err.println(e);
            e.printStackTrace();
            System.exit(-2);
        }
    }

    public static void initAnimations(){

    }

    public static void main(String[] args){
        Resources.initImages();
        Resources.initAnimations();
    }
}

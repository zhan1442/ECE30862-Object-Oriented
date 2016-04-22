package com.brackeen.javagamebook.tilegame;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Iterator;

import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioFormat;

import com.brackeen.javagamebook.graphics.*;
import com.brackeen.javagamebook.sound.*;
import com.brackeen.javagamebook.input.*;
import com.brackeen.javagamebook.test.GameCore;
import com.brackeen.javagamebook.tilegame.sprites.*;
import com.brackeen.javagamebook.tilegame.sprites.PowerUp.Star;

/**
    GameManager manages all parts of the game.
*/
public class GameManager extends GameCore {

    public static void main(String[] args) {
        new GameManager().run();
    }

    // uncompressed, 44100Hz, 16-bit, mono, signed, little-endian
    private static final AudioFormat PLAYBACK_FORMAT =
        new AudioFormat(44100, 16, 1, true, false);

    private static final int DRUM_TRACK = 1;

    public static final float GRAVITY = 0.002f;

    private Point pointCache = new Point();
    private TileMap map;
    private MidiPlayer midiPlayer;
    private SoundManager soundManager;
    private ResourceManager resourceManager;
    private Sound prizeSound;
    private Sound boopSound;
    private Sound hp;
    private Sound shooting;
    private Sound tnt;
    private Sound shouting;
    private Sound gas;
    private InputManager inputManager;
    private TileMapRenderer renderer;

    private GameAction moveLeft;
    private GameAction moveRight;
    private GameAction moveUp;
    private GameAction moveDown;
    private GameAction jump;
    private GameAction exit;
    private GameAction shoot;
    private static int direction = 1;
    private static long wudi_StartT = 0;
    
    public static int fire_time = 0;
    public static int bullet_left = 10;
    


    public void init() {
        super.init();

        // set up input manager
        initInput();

        // start resource manager
        resourceManager = new ResourceManager(
        screen.getFullScreenWindow().getGraphicsConfiguration());

        // load resources
        renderer = new TileMapRenderer();
        renderer.setBackground(
            resourceManager.loadImage("background.png"));

        // load first map
        map = resourceManager.loadNextMap();

        // load sounds
        soundManager = new SoundManager(PLAYBACK_FORMAT);
        prizeSound = soundManager.getSound("sounds/prize.wav");
        boopSound = soundManager.getSound("sounds/boop2.wav");
        hp = soundManager.getSound("sounds/hp.wav");
        shooting = soundManager.getSound("sounds/shooting.wav");
        tnt = soundManager.getSound("sounds/tnt.wav");
        shouting = soundManager.getSound("sounds/shouting.wav");
        gas = soundManager.getSound("sounds/gas.wav");

        // start music
        midiPlayer = new MidiPlayer();
        Sequence sequence =
            midiPlayer.getSequence("sounds/music.midi");
        midiPlayer.play(sequence, true);
        toggleDrumPlayback();
    }


    /**
        Closes any resurces used by the GameManager.
    */
    public void stop() {
        super.stop();
        midiPlayer.close();
        soundManager.close();
    }


    private void initInput() {
        moveLeft = new GameAction("moveLeft");
        moveRight = new GameAction("moveRight");
        moveUp = new GameAction("moveUp");
        moveDown = new GameAction("moveUDown"); 
        
        moveUp = new GameAction("moveUp",
                GameAction.DETECT_INITAL_PRESS_ONLY);
        moveDown = new GameAction("moveDown",
                GameAction.DETECT_INITAL_PRESS_ONLY);
        jump = new GameAction("jump",
            GameAction.DETECT_INITAL_PRESS_ONLY);
        exit = new GameAction("exit",
            GameAction.DETECT_INITAL_PRESS_ONLY);
        shoot = new GameAction("shoot");

        inputManager = new InputManager(
            screen.getFullScreenWindow());
        inputManager.setCursor(InputManager.INVISIBLE_CURSOR);

        inputManager.mapToKey(moveLeft, KeyEvent.VK_LEFT);
        inputManager.mapToKey(moveRight, KeyEvent.VK_RIGHT);
        inputManager.mapToKey(moveUp, KeyEvent.VK_UP);
        inputManager.mapToKey(moveDown, KeyEvent.VK_DOWN);
        inputManager.mapToKey(jump, KeyEvent.VK_SPACE);
        inputManager.mapToKey(exit, KeyEvent.VK_ESCAPE);
        inputManager.mapToKey(shoot, KeyEvent.VK_S);
    }


    private void checkInput(long elapsedTime) {
    	
        if (exit.isPressed()) {
            stop();
        }

        Player player = (Player)map.getPlayer();
        if (player.isAlive()) {
            float velocityX = 0;
            if (moveLeft.isPressed()) {
                velocityX-=player.getMaxSpeed();
                direction = -1;
            }
            if (moveRight.isPressed()) {
                velocityX+=player.getMaxSpeed();
                direction = 1;
            }
            if (jump.isPressed()) {
                player.jump(false);
                GameCore.Timer = 0;
            }
            if (moveUp.isPressed()) {
                player.jump(false);
                GameCore.Timer = 0;
            }
            if (moveDown.isPressed()) {
            	player.setVelocityY(10);
                GameCore.Timer = 0;
            }
            
            if (!GameCore.nofire && shoot.isPressed() && !shoot.pp) {
            	bulletlist.add(new Bullet(player.getX(),player.getY(),direction));
//add sound here 
            	soundManager.play(shooting);
            }
            if (shoot.getAmount() > 1) {//hold time to continues mode 
            	shoot.pp = true;
            	GameCore.nofire = true;
            }
            if (GameCore.nofire) {
            	if (hitgas) {
            		bullet_left = 0;
            		Timer_nofire = 3000;
            		hitgas = false;
            		
            	}
            	if (bullet_left> 0 && GameCore.Timer_nofire > fire_time) {
            			bulletlist.add(new Bullet(player.getX(),player.getY(),direction));
//add sound here	
            			soundManager.play(shooting);
            			fire_time += 300;//fire rate in continues mode
            			bullet_left--;
            		
            	}            	
            }
 
            //creature to fire.........................................................
            Iterator i = map.getSprites();
            while (i.hasNext()) {
                Sprite sp = (Sprite)i.next();
                if (sp instanceof Grub && ((Grub) sp).timmer != -1 && GameCore.cTime-((Grub) sp).timmer > 500 ) {//wait time for creature to fire
                	if (((Grub) sp).nextt_fire == -1) {
                		((Grub) sp).nextt_fire = GameCore.cTime;
                	}
                	else if (GameCore.cTime > ((Grub) sp).nextt_fire){
                		gbulletlist.add(new Gbullet(sp.getX(),sp.getY(),((Grub)sp).direction));
                		((Grub) sp).nextt_fire += 600;//rate of Creature fire
                	}	
                }
            }
           
            
            player.setVelocityX(velocityX);
        }

    }


    public void draw(Graphics2D g) {
        renderer.draw(g, map,
            screen.getWidth(), screen.getHeight());
    }


    /**
        Gets the current map.
    */
    public TileMap getMap() {
        return map;
    }


    /**
        Turns on/off drum playback in the midi music (track 1).
    */
    public void toggleDrumPlayback() {
        Sequencer sequencer = midiPlayer.getSequencer();
        if (sequencer != null) {
            sequencer.setTrackMute(DRUM_TRACK,
                !sequencer.getTrackMute(DRUM_TRACK));
        }
    }


    /**
        Gets the tile that a Sprites collides with. Only the
        Sprite's X or Y should be changed, not both. Returns null
        if no collision is detected.
    */
    public Point getTileCollision(Sprite sprite,
        float newX, float newY)
    {
        float fromX = Math.min(sprite.getX(), newX);
        float fromY = Math.min(sprite.getY(), newY);
        float toX = Math.max(sprite.getX(), newX);
        float toY = Math.max(sprite.getY(), newY);

        // get the tile locations
        int fromTileX = TileMapRenderer.pixelsToTiles(fromX);
        int fromTileY = TileMapRenderer.pixelsToTiles(fromY);
        int toTileX = TileMapRenderer.pixelsToTiles(
            toX + sprite.getWidth() - 1);
        int toTileY = TileMapRenderer.pixelsToTiles(
            toY + sprite.getHeight() - 1);

        // check each tile for a collision
        for (int x=fromTileX; x<=toTileX; x++) {
            for (int y=fromTileY; y<=toTileY; y++) {
                if (x < 0 || x >= map.getWidth() ||
                    map.getTile(x, y) != null)
                {
                    // collision found, return the tile
                    pointCache.setLocation(x, y);
                    return pointCache;
                }
            }
        }

        // no collision found
        return null;
    }


    /**
        Checks if two Sprites collide with one another. Returns
        false if the two Sprites are the same. Returns false if
        one of the Sprites is a Creature that is not alive.
    */
    public boolean isCollision(Sprite s1, Sprite s2) {
        // if the Sprites are the same, return false
        if (s1 == s2) {
            return false;
        }

        // if one of the Sprites is a dead Creature, return false
        if (s1 instanceof Creature && !((Creature)s1).isAlive()) {
            return false;
        }
        if (s2 instanceof Creature && !((Creature)s2).isAlive()) {
            return false;
        }

        // get the pixel location of the Sprites
        int s1x = Math.round(s1.getX());
        int s1y = Math.round(s1.getY());
        int s2x = Math.round(s2.getX());
        int s2y = Math.round(s2.getY());

        // check if the two sprites' boundaries intersect
        if (s1 instanceof Bullet) {
        	return (s1x < s2x + s2.getWidth() &&
                    s2x < s1x + 23 &&
                    s1y < s2y + s2.getHeight() &&
                    s2y < s1y + 23);
        }
        
        
        return (s1x < s2x + s2.getWidth() &&
            s2x < s1x + s1.getWidth() &&
            s1y < s2y + s2.getHeight() &&
            s2y < s1y + s1.getHeight());
    }


    /**
        Gets the Sprite that collides with the specified Sprite,
        or null if no Sprite collides with the specified Sprite.
    */
    public Sprite getSpriteCollision(Sprite sprite) {

        // run through the list of Sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite otherSprite = (Sprite)i.next();
            if (isCollision(sprite, otherSprite)) {
                // collision found, return the Sprite
                return otherSprite;
            }
            
            
            
        }

        // no collision found
        return null;
    }


    /**
        Updates Animation, position, and velocity of all Sprites
        in the current map.
    */
    public void update(long elapsedTime) {
        Creature player = (Creature)map.getPlayer();


        // player is dead! start map over
        if (player.getState() == Creature.STATE_DEAD) {      
        	map = resourceManager.reloadMap();
        	Player.health = 20;//////////////
        	Player.move1u = 0;
            return;
        }

        // get keyboard/mouse input
        checkInput(elapsedTime);

        // update player
        updateCreature(player, elapsedTime);
        player.update(elapsedTime);

        // update other sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite sprite = (Sprite)i.next();
            if (sprite instanceof Creature) {
                Creature creature = (Creature)sprite;
                if (creature.getState() == Creature.STATE_DEAD) {
                    i.remove();
                }
                else {
                    updateCreature(creature, elapsedTime);
                }
            }
            // normal update
            sprite.update(elapsedTime);
        }
    }


    /**
        Updates the creature, applying gravity for creatures that
        aren't flying, and checks collisions.
    */
    private void updateCreature(Creature creature,
        long elapsedTime)
    {

        // apply gravity
        if (!creature.isFlying()) {
            creature.setVelocityY(creature.getVelocityY() +
                GRAVITY * elapsedTime);
        }

        // change x
        float dx = creature.getVelocityX();
        float oldX = creature.getX();
        float newX = oldX + dx * elapsedTime;
        Point tile =
            getTileCollision(creature, newX, creature.getY());
        if (tile == null) {
            creature.setX(newX);
        }
        else {
            // line up with the tile boundary
            if (dx > 0) {
                creature.setX(
                    TileMapRenderer.tilesToPixels(tile.x) -
                    creature.getWidth());
            }
            else if (dx < 0) {
                creature.setX(
                    TileMapRenderer.tilesToPixels(tile.x + 1));
            }
            creature.collideHorizontal();
        }
        if (creature instanceof Player) {
            checkPlayerCollision((Player)creature, false);
        }

        // change y
        float dy = creature.getVelocityY();
        float oldY = creature.getY();
        float newY = oldY + dy * elapsedTime;
        tile = getTileCollision(creature, creature.getX(), newY);
        if (tile == null) {
            creature.setY(newY);
        }
        else {
            // line up with the tile boundary
            if (dy > 0) {
                creature.setY(
                    TileMapRenderer.tilesToPixels(tile.y) -
                    creature.getHeight());
            }
            else if (dy < 0) {
                creature.setY(
                    TileMapRenderer.tilesToPixels(tile.y + 1));
            }
            creature.collideVertical();
        }
        if (creature instanceof Player) {
            boolean canKill = (oldY < creature.getY());
            checkPlayerCollision((Player)creature, canKill);
        } 
        
        

    }


    /**
        Checks for Player collision with other Sprites. If
        canKill is true, collisions with Creatures will kill
        them.
    */
    public void checkPlayerCollision(Player player,
        boolean canKill)
    {
    	//check for bullets collision with sprites//////////////////////////////////////////////////
    	for (int i = 0; i < bulletlist.size(); i++) {
    		Sprite collisionb = getSpriteCollision(bulletlist.get(i));
    		if (collisionb instanceof Creature) {
    			Creature badguy = (Creature)collisionb;
    			
    			soundManager.play(shouting);
                badguy.setState(Creature.STATE_DYING);
                bulletlist.remove(i);
    		}
    		else if (collisionb != null) {
    			bulletlist.remove(i);
    		}
    	}
    	
    	if (!player.isAlive()) {
            return;
        }

        // check for player collision with other sprites
        Sprite collisionSprite = getSpriteCollision(player);
        if (collisionSprite instanceof PowerUp) {
            acquirePowerUp((PowerUp)collisionSprite);
        }       
        else if (collisionSprite instanceof Creature) {
            Creature badguy = (Creature)collisionSprite;
            if (canKill) {
                // kill the badguy and make player bounce
                soundManager.play(shouting);
                badguy.setState(Creature.STATE_DYING);
                player.setY(badguy.getY() - player.getHeight());
                player.jump(true);
            }
            else if (System.currentTimeMillis() - wudi_StartT < 1000) {
            	soundManager.play(shouting);
                badguy.setState(Creature.STATE_DYING);
            }
            else {
                // player dies!
                player.setState(Creature.STATE_DYING);
            }
        }
        
        if (collisionSprite instanceof Star) {
        	wudi_StartT = System.currentTimeMillis();
        	System.out.println("wudi start!");
        }
        
        
        for (int i = 0; i < gbulletlist.size(); i++) {//check for player collision with bullets....................................
        	if (isCollision(gbulletlist.get(i), player)) {			
    			soundManager.play(boopSound);
    			if (System.currentTimeMillis() - wudi_StartT > 1000) {
    				Player.health -= 5;
    			}
    			if (Player.health <= 0) {
    				player.setState(Creature.STATE_DYING);
    			}
                gbulletlist.remove(i);
    		}
    	}
        
        
        
    }


    /**
        Gives the player the speicifed power up and removes it
        from the map.
    */
    public void acquirePowerUp(PowerUp powerUp) {
        // remove it from the map
        map.removeSprite(powerUp);

        if (powerUp instanceof PowerUp.Star) {
            // do something here, like give the player points
            soundManager.play(prizeSound);
        }
        else if (powerUp instanceof PowerUp.Music) {
            // change the music
            soundManager.play(prizeSound);
            toggleDrumPlayback();
        }
        else if (powerUp instanceof PowerUp.Goal) {
            // advance to next map
            soundManager.play(prizeSound,
                new EchoFilter(2000, .7f), false);
            map = resourceManager.loadNextMap();
            wudi_StartT = 0;
        }
        else if (powerUp instanceof PowerUp.Mushroom) {
            // do something here, like give the player points
            soundManager.play(hp);
            Player.health += 5;
        }
        else if (powerUp instanceof PowerUp.Bomb) {
            // do something here, like give the player points
            if (System.currentTimeMillis() - wudi_StartT > 1000) {
            	Player.health -= 10;
            	soundManager.play(tnt);
            }
        }
        else if (powerUp instanceof PowerUp.Gas) {
            // do something here, like give the player points
        	if (System.currentTimeMillis() - wudi_StartT > 1000) { 
	        	soundManager.play(gas);
	            hitgas = true; 
	            nofire = true;
        	}
        }
    }

}

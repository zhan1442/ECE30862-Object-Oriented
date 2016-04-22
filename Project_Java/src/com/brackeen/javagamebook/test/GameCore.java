package com.brackeen.javagamebook.test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.brackeen.javagamebook.graphics.ScreenManager;
import com.brackeen.javagamebook.tilegame.GameManager;
import com.brackeen.javagamebook.tilegame.sprites.Bullet;
import com.brackeen.javagamebook.tilegame.sprites.Gbullet;
import com.brackeen.javagamebook.tilegame.sprites.Player;

/**
    Simple abstract class used for testing. Subclasses should
    implement the draw() method.
*/


public abstract class GameCore {

    protected static final int FONT_SIZE = 24;

    private static final DisplayMode POSSIBLE_MODES[] = {
        new DisplayMode(800, 600, 16, 0),
        new DisplayMode(800, 600, 32, 0),
        new DisplayMode(800, 600, 24, 0),
        new DisplayMode(640, 480, 16, 0),
        new DisplayMode(640, 480, 32, 0),
        new DisplayMode(640, 480, 24, 0),
        new DisplayMode(1024, 768, 16, 0),
        new DisplayMode(1024, 768, 32, 0),
        new DisplayMode(1024, 768, 24, 0),
    };
    
    public static long cTime;
    private boolean isRunning;
    protected ScreenManager screen;
    public static long Timer; //for one second not move;
    public static long Timer_nofire; //1 second no fire
    public static boolean nofire;
    public static boolean hitgas;
   
    public static LinkedList<Bullet> bulletlist = new LinkedList<Bullet>();    
    public static LinkedList<Gbullet> gbulletlist = new LinkedList<Gbullet>();   
    public static BufferedImage bulletimg;    
    public static BufferedImage gbulletimg;
    
    /**
        Signals the game loop that it's time to quit
    */
    public void stop() {
        isRunning = false;
    }


    /**
        Calls init() and gameLoop()
    */
    public void run() {
        try {
            init();
            gameLoop();
        }
        finally {
            screen.restoreScreen();
            lazilyExit();
        }
    }


    /**
        Exits the VM from a daemon thread. The daemon thread waits
        2 seconds then calls System.exit(0). Since the VM should
        exit when only daemon threads are running, this makes sure
        System.exit(0) is only called if neccesary. It's neccesary
        if the Java Sound system is running.
    */
    public void lazilyExit() {
        Thread thread = new Thread() {
            public void run() {
                // first, wait for the VM exit on its own.
                try {
                    Thread.sleep(2000);
                }
                catch (InterruptedException ex) { }
                // system is still running, so force an exit
                System.exit(0);
            }
        };
        thread.setDaemon(true);
        thread.start();
    }


    /**
        Sets full screen mode and initiates and objects.
    */
    public void init() {
        screen = new ScreenManager();
        DisplayMode displayMode =
            screen.findFirstCompatibleMode(POSSIBLE_MODES);
        screen.setFullScreen(displayMode);

        Window window = screen.getFullScreenWindow();
        window.setFont(new Font("Dialog", Font.PLAIN, FONT_SIZE));
        window.setBackground(Color.blue);
        window.setForeground(Color.white);
        
        try {
			bulletimg = ImageIO.read(new File("images/bullet.png"));
			gbulletimg = ImageIO.read(new File("images/gbullet.png"));
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
        
        isRunning = true;
    }


    public Image loadImage(String fileName) {
        return new ImageIcon(fileName).getImage();
    }


    /**
        Runs through the game loop until stop() is called.
    */
    public void gameLoop() {
        long startTime = System.currentTimeMillis();
        long currTime = startTime;
        hitgas = false;

        while (isRunning) {
            long elapsedTime =
                System.currentTimeMillis() - currTime;
            currTime += elapsedTime;
            cTime = currTime;
            
            //timer for 1 second for increase health
            Timer += elapsedTime;
            Player.calHealth();
            
            //timer for 1 second no-fire
            if (nofire) {
            	Timer_nofire += elapsedTime;
            }
            if (Timer_nofire >4000) {
            	nofire = false;
            	Timer_nofire = 0;
            	GameManager.fire_time = 0;
            	GameManager.bullet_left = 10;
            }
            
            

            // update
            update(elapsedTime);

            // draw the screen
            Graphics2D g = screen.getGraphics();
            draw(g);
            g.dispose();
            screen.update();
            

            for (int i = 0; i < bulletlist.size(); i++) {
            	if (Math.abs(bulletlist.get(i).getX()-bulletlist.get(i).getiX()) < 400) {
            		bulletlist.get(i).setX(bulletlist.get(i).getX()+elapsedTime* (float) 1.2*bulletlist.get(i).getD()); 
            	}
            	else {
            		bulletlist.remove(i);
            	}
            }
            for (int i = 0; i < gbulletlist.size(); i++) {
            	if (Math.abs(gbulletlist.get(i).getX()-gbulletlist.get(i).getiX()) < 400) {
            		gbulletlist.get(i).setX(gbulletlist.get(i).getX()+elapsedTime* (float) 0.6*gbulletlist.get(i).getD()); 
            	}
            	else {
            		gbulletlist.remove(i);
            	}
            }
         
            
            
            
            
            // don't take a nap! run as fast as possible
            /*try {
                Thread.sleep(20);
            }
            catch (InterruptedException ex) { }*/
        }
    }
    
    

    /**
        Updates the state of the game/animation based on the
        amount of elapsed time that has passed.
    */
    public void update(long elapsedTime) {
        // do nothing
    }


    /**
        Draws to the screen. Subclasses must override this
        method.
    */
    public abstract void draw(Graphics2D g);
}

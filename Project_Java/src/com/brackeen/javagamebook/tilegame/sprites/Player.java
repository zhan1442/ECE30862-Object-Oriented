package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;
import com.brackeen.javagamebook.test.GameCore;

/**
    The Player.
*/
public class Player extends Creature {

    private static final float JUMP_SPEED = -.95f;

    private boolean onGround;
    public static int health = 20;
    public static int move1u = 0;

    public Player(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
    }

    public void collideHorizontal() {
        setVelocityX(0);
    }


    public void collideVertical() {
        // check if collided with ground
        if (getVelocityY() > 0) {
            onGround = true;
        }
        setVelocityY(0);
    }


    public void setY(float y) {
        // check if falling
        if (Math.round(y) > Math.round(getY())) {
            onGround = false;
            GameCore.Timer = 0;
        }
        super.setY(y);
    }


    public void wakeUp() {
        // do nothing
    }


    /**
        Makes the player jump if the player is on the ground or
        if forceJump is true.
    */
    public void jump(boolean forceJump) {
        if (onGround || forceJump) {
            onGround = false;
            setVelocityY(JUMP_SPEED);
        }
    }


    public float getMaxSpeed() {
        return 0.5f;
    }
    
    public int getHealth() {
    	if (health < 0) {
    		health = 0;
    		return health;
    	}
    	else if (health <= 40) {return health;}
    	else {
    		health = 40;
    		return health;
    	} 
    }
    
    public static void calHealth() {
    	//System.out.println(GameCore.Timer);
    	if (GameCore.Timer > 1000) {
    		health += 5;
    		GameCore.Timer = 0;
    	}
    	if (Player.move1u > 250){
    		health += 1;
    		move1u = 0;
    	}
    	
    }

}

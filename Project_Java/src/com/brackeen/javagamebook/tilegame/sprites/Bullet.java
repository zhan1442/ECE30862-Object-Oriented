package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Sprite;


public class Bullet extends Sprite{

	//public bullet(Animation left, Animation right, Animation deadLeft,
			//Animation deadRight) {
		//super(left, right, deadLeft, deadRight);
		//this.setVelocityX(10);
	//}
	
	
	
	
	private float ix;
	private float iy;
	private float x;
	private float y;
	private int direction;
	//private boolean effective;
	public Bullet(float x, float y, int direction){//1 right,-1 left
		this.ix = x;
		this.iy = y;
		this.x = x;
		this.y = y;
		this.direction = direction;
		//this.effective = true;
	}
	//public shootB() {
		//BufferedImage bulletimg = ImageLoader.loadImage("/bullet.png");
		//g.drawImage(bulletimg, x, y, 23, 23, null);
	//}
	
	public float getiX() {
		return ix;
	}
	public float getiY() {
		return iy;
	}
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
	public void setX(float x) {
		this.x = x;
	}
	public int getD() {
		return direction;
	}
}

package com.burnt_toast.dungeons_n_stuff;

import java.util.HashMap;
import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.burnt_toast.monster_generator.Poolable;

public abstract class Character extends Poolable{
//used for the super class of archer and warrior and stuff. Just in case you forget.
	protected float health;
	protected float exp;
	protected int level;
	protected float nextLevelExp;
	protected float meeleePause; 
	protected int[] meeleeDamage;//range of 2 numbers
	protected int[] rangedDamage;//range of 2 numbers
	protected boolean isMoving;
	protected char direction;//facing direction
	protected boolean flipped;//used when flipping the character.
	protected float movementSpeed;

	protected float frameSizeX;
	protected float frameSizeY;
	protected float meleeSizeX;
	protected float meleeSizeY;
	protected TextureRegion[] frames;
	private String temp;
	private float tempX;
	private float tempY;
	
	
	protected Rectangle collisionRect;
	protected float attackMaxTime;//how long the player attacks for,
									//or how long Melee box is active
	protected float attackTimer;//counts down how long meleeRect is active
	
	protected float animationSpeed;
	protected int animationIndex;//used for animation, which frame. 0 or 1.
	protected float animationTimer;
	
	public Character(TextureRegion[] passedFrames){//i pass the frames for sizes
		super(null);//this is needed because of the poolable thing. Even though not all characters are POOLABLE ANDREW.
		movementSpeed = 32;//32 pixels per second.
		animationSpeed = 0.25f;//animates every fourth a second.
		//it's having a problems with frames[1] here.
		frames = passedFrames;
		collisionRect = new Rectangle(0, 0, passedFrames[1].getRegionWidth(), passedFrames[1].getRegionHeight() / 2);
		meeleeDamage = new int[2];
		frameSizeX = frames[1].getRegionWidth();
		frameSizeY = frames[1].getRegionHeight();
		
	}
	
	public void setIfMoving(boolean passMoving){isMoving = passMoving;}
	public boolean getIfMoving(){return isMoving;}
	public char getDirection(){return direction;}
	public void setDirection(char passDirection){
		System.out.println("CALLED");
		direction = passDirection;
		if(passDirection == 'l')flipped = true;
		else if (passDirection == 'r') flipped = false;
	}
	/**
	 * 
	 * @param passDirection
	 */
	public boolean move(char passDirection, float custSpeed){
		this.direction = passDirection;
		return move(custSpeed);
	}
	public boolean move(){
		return move(-1);//if the method gets -1, it just uses the default moving speed
	}
	/**
	 * the move function with collision involved.
	 * @param custSpeed the speed you want to move by
	 * @return returns true if it was able to move, false if it hit something.
	 */
	public boolean move(float custSpeed){//custom speed to move
		tempX = collisionRect.x;
		tempY = collisionRect.y;
		if(direction == 'u'){
			//collision goes here.
			if(!PlayScreen.checkCollisionAt(collisionRect.x, collisionRect.y + (movementSpeed* Gdx.graphics.getDeltaTime()),
					collisionRect.getWidth(), collisionRect.getHeight()) && 
					!PlayScreen.checkCharacterCollision(collisionRect.x,
							collisionRect.y + (movementSpeed * Gdx.graphics.getDeltaTime()), collisionRect)
					){//if no collision.

				this.collisionRect.y += (custSpeed == -1? this.movementSpeed : custSpeed) * Gdx.graphics.getDeltaTime();
				return true;
			}
			else{
				return false;
			}
		}
		else if(direction == 'd'){
			if(!PlayScreen.checkCollisionAt(collisionRect.x, collisionRect.y - (movementSpeed* Gdx.graphics.getDeltaTime()),
					collisionRect.getWidth(), collisionRect.getHeight()) &&
					!PlayScreen.checkCharacterCollision(collisionRect.x,
							collisionRect.y - (movementSpeed * Gdx.graphics.getDeltaTime()), collisionRect)){//if no collision.
				collisionRect.y -= (custSpeed == -1? this.movementSpeed : custSpeed) * Gdx.graphics.getDeltaTime();
				return true;
			}
			else{
				return false;
			}
		}
		else if(direction == 'r'){
			if(!PlayScreen.checkCollisionAt(collisionRect.x + (movementSpeed* Gdx.graphics.getDeltaTime()), collisionRect.y,
					collisionRect.getWidth(), collisionRect.getHeight()) &&
					!PlayScreen.checkCharacterCollision(collisionRect.x + (movementSpeed * Gdx.graphics.getDeltaTime()),
							collisionRect.y, collisionRect)){//if no collision.
				collisionRect.x += (custSpeed == -1? this.movementSpeed : custSpeed) * Gdx.graphics.getDeltaTime();
				return true;
			}
			else{
				return false;
			}
		}
		else if(direction == 'l'){
			if(!PlayScreen.checkCollisionAt(collisionRect.x - (movementSpeed* Gdx.graphics.getDeltaTime()), collisionRect.y,
					collisionRect.getWidth(), collisionRect.getHeight()) &&
					!PlayScreen.checkCharacterCollision(collisionRect.x - (movementSpeed * Gdx.graphics.getDeltaTime()),
							collisionRect.y, collisionRect)){//if no collision.
				collisionRect.x -= (custSpeed == -1? this.movementSpeed : custSpeed) * Gdx.graphics.getDeltaTime();
				return true;
			}
			else{
				return false;
			}
		}
		if(PlayScreen.checkCharacterCollision(collisionRect.x, collisionRect.y, collisionRect)){
			collisionRect.x = tempX;
			collisionRect.y = tempY;
			return true;
		}
		assert false;//should never reach this.
		return true;
	}
	public abstract void draw(SpriteBatch batch);
	public void update(){
		if(isMoving){
			animationTimer += Gdx.graphics.getDeltaTime();
			if(animationTimer >= animationSpeed){
				animationTimer = 0;
				animationIndex = animationIndex == 0? 1:0;//if it's 0, than make it one, if not, then 0
			}
			//move();
		}
		if(attackTimer > 0){
			attackTimer -= Gdx.graphics.getDeltaTime();
			if(attackTimer <=0) attackTimer = 0;//if overshot
		}
	}
	

	
	public void hit(float damage){
		health -= damage;System.out.println("HIT" + damage);
		if(health < 0){
			health = 0;
		}
	}
	public void heal(float amount){health += amount;}
	public void expPickup(float expAmount){
		if(exp + expAmount >= nextLevelExp){
			//levelUp()
			exp = (exp + expAmount) - nextLevelExp;//make exp equal to whatever is left over after levelUp
		}
	}
	protected float calculateNextLvExp(int currLevel){
		return (10^currLevel+100);
	}
	public void levelUp(){
		level++;//level up yeah
		//change stats like health and stuff EXPONENTIALLY WOAOAOOAHOAHOH
	}
	public void setPosition(float passX, float passY){
		collisionRect.x = passX;
		collisionRect.y = passY;
	}
	public float getX(){return collisionRect.x;}
	public float getY(){return collisionRect.y;}
	public float getHealth(){return this.health;}
	public void setX(float x){
		this.collisionRect.setX(x);
	}
	public void setY(float y){
		this.collisionRect.setY(y);
	}
	public void setHealth(float health){
		this.health = health;
	}
	public Rectangle getRectangle(){
		return this.collisionRect;
	}
	
}

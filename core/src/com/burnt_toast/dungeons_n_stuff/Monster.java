package com.burnt_toast.dungeons_n_stuff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.burnt_toast.monster_generator.Pool;
import com.burnt_toast.monster_generator.Poolable;

public abstract class Monster extends Character{

	protected int level;
	protected float meleeDamage;
	protected char direction;
	protected float attackTimer;
	protected float animationTimer;
	protected float patienceTime;//the default of when it will give up.
	protected float giveUpTimer; //the amount of time this goes back to being a placeholder
	protected float hitPause;
	protected float hitPauseTimer;
	
	protected float hurtMax;
	protected float hurtTimer;//tracks
	@SuppressWarnings("rawtypes")
	protected Pool parentPool;
	//these next 3 are for moving more smoothly. actually these next 5.
	protected float totalDistance;
	protected float distanceX;
	protected float distanceY;
	protected float velocityX;
	protected float velocityY;
	
	
	@SuppressWarnings("rawtypes")
	public Monster(TextureRegion[] passFrames, Pool parentPool) {
		super(passFrames);
		this.isMoving = true;
		this.parentPool = parentPool;
		hitPause = 1.0f;
		hurtMax = 0.25f;
		this.setDirection('n');//n for nunya business
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public void draw(SpriteBatch batch) {
		// TODO Auto-generated method stub
		if(hurtTimer > 0){
			batch.setColor(1, 1, 1, 0.5f);
		}
		if(flipped == true){
			batch.draw(this.frames[this.animationIndex], this.getX(), this.getY(),
					this.frameSizeX * -1, this.frameSizeY);
		}
		else{
			batch.draw(this.frames[this.animationIndex], this.getX(), this.getY(),
					this.frameSizeX, this.frameSizeY);
		}
		batch.setColor(Color.WHITE);
		
	}
	public int attack(){
		return (int)(Math.random()*(Math.abs(this.meeleeDamage[0] - this.meeleeDamage[1])+meeleeDamage[0]));
	}

	public void setGiveUpTime(float passPatience){
		patienceTime = passPatience;
		giveUpTimer = passPatience;
	}
	public boolean update(Player currentPlayer){
		if(hurtTimer > 0){
			hurtTimer -= Gdx.graphics.getDeltaTime();
		}
		if(this.health <=0){
			//retire object
			this.toggleInUse();
		}
		move(currentPlayer.getX(), currentPlayer.getY());
		update();
		if(hitPauseTimer > 0){
			//color change when I'mhit
			
		}
		if(PlayScreen.distForm(currentPlayer.getX()+currentPlayer.getRectangle().getWidth()/2,
				currentPlayer.getY()+currentPlayer.getRectangle().getHeight()/2,
				getX()+collisionRect.getWidth()/2, getY()+collisionRect.getHeight()/2) <= 10){
			hitPauseTimer -= Gdx.graphics.getDeltaTime();
			//if center of player is a certain distance away, then hit it
			if(hitPauseTimer <= 0){
			currentPlayer.hit(this.attack());
			hitPauseTimer = hitPause;
			}
		}
		else
			hitPauseTimer = hitPause;
		
		if(giveUpTimer > 0){
			giveUpTimer -= Gdx.graphics.getDeltaTime();
		}
		else{
			giveUpTimer = patienceTime;
			return true;
		}


		return false;
	}
	public void move(float playerX, float playerY){
		distanceX = Math.abs(this.getX() - playerX);
		distanceY = Math.abs(this.getY() - playerY);
		totalDistance = Math.abs(distanceX) + Math.abs(distanceY);
		velocityX = (distanceX / totalDistance) * movementSpeed;
		velocityY = (distanceY / totalDistance) * movementSpeed;

			if(playerX < this.getX()){
				if(!this.move('l', velocityX)){
					this.direction = 'l';
					velocityY += velocityX;
				}
				else this.direction = 'l';//it was able to move, make it l
				//this.collisionRect.x -= this.velocityX * Gdx.graphics.getDeltaTime();
			}
			else if(playerX > this.getX()){
				if(!this.move('r', velocityX)){
					this.direction = 'r';
					velocityY += velocityX;
				}
				else this.direction = 'r';//it was able to move 
				//this.collisionRect.x += this.velocityX * Gdx.graphics.getDeltaTime();
			}
			if(playerY < this.getY()){
				if(!this.move('d', velocityY)){
					this.move(this.direction, velocityY);//it it can't move, 
				}
				//this.collisionRect.y -= this.velocityY * Gdx.graphics.getDeltaTime();
			}
			else if(playerY > this.getY()){
				this.move('u', velocityY);
				//this.collisionRect.y += this.velocityY * Gdx.graphics.getDeltaTime();
			}
		//this.move("slime");
		//System.out.println("trying to move" + this.collisionRect.height);
		//collision and stuff math
		
	}
	
	@Override
	public void hit(float damage){
		super.hit(damage);
		this.hitPauseTimer = hitPause;
		this.hurtTimer = this.hurtMax;
	}
	
	
	/**
	 * calculates the health for the current level of floor and such.
	 * @return
	 */
	public abstract float calcHealth();
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		this.inUse = true;
		this.health = this.calcHealth();
		this.meeleeDamage[0] = 1;
		this.meeleeDamage[1] = 3;
		//set all the waling speed and stuff based off floor level
	}
	@Override
	public void retire() {
		// TODO Auto-generated method stub
		this.inUse = false;
	}
	
	

}

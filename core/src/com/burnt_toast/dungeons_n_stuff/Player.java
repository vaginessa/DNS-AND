package com.burnt_toast.dungeons_n_stuff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Player extends Character {

	protected float specialCharge;// PLAYER
	protected boolean specialCooling;// used to know if the special is used and
										// cooling down or not.
	protected float coolDownIncrement;// how much it cools a second
	protected float[] specialDamage;// range of 2 numbers
	protected float maxHealth;
	protected Rectangle meleeRect;

	public Player(TextureRegion[] frames) {
		super(frames);
		// TODO Auto-generated constructor stub
		// TEMP CODE
		this.maxHealth = 50;
		this.health = maxHealth;

		meleeRect = new Rectangle(0, 0, frames[2].getRegionWidth(), frames[2].getRegionHeight());
		meleeSizeX = frames[2].getRegionWidth();
		meleeSizeY = frames[2].getRegionHeight();
	}

	@Override
	public void draw(SpriteBatch batch) {
		// TODO Auto-generated method stub

	}

	public void attack() {
		if (PlayScreen.getCharactersAt(meleeRect.x, meleeRect.y) != null) {
			for (Character tempChar : PlayScreen.getCharactersAt(meleeRect.x, meleeRect.y)) {
				if (tempChar.getRectangle().overlaps(this.meleeRect)){
					tempChar.hit(
							(int) (Math.random() * (this.meeleeDamage[1] - meeleeDamage[0] + 1) + meeleeDamage[0])
									+ 1);
					return;
				}
			}
		}
		if (PlayScreen.getCharactersAt(meleeRect.x + meleeRect.width, meleeRect.y + meleeRect.width) != null) {
			for (Character tempChar : PlayScreen.getCharactersAt(meleeRect.x + meleeRect.width,
					meleeRect.y + meleeRect.width)) {
				if (tempChar.getRectangle().overlaps(this.meleeRect)){
					tempChar.hit(
							(int) (Math.random() * (this.meeleeDamage[1] - meeleeDamage[0] + 1) + meeleeDamage[0])
									+ 1);
					return;
				}
			}
		}
		if (PlayScreen.getCharactersAt(meleeRect.x + meleeRect.width, meleeRect.y) != null) {
			for (Character tempChar : PlayScreen.getCharactersAt(meleeRect.x + meleeRect.width,
					meleeRect.y)) {
				if (tempChar.getRectangle().overlaps(this.meleeRect)){
					tempChar.hit(
							(int) (Math.random() * (this.meeleeDamage[1] - meeleeDamage[0] + 1) + meeleeDamage[0])
									+ 1);
					return;
				}
			}
		}
		if (PlayScreen.getCharactersAt(meleeRect.x, meleeRect.y + meleeRect.width) != null) {
			for (Character tempChar : PlayScreen.getCharactersAt(meleeRect.x,
					meleeRect.y + meleeRect.width)) {
				if (tempChar.getRectangle().overlaps(this.meleeRect)){
					tempChar.hit(
							(int) (Math.random() * (this.meeleeDamage[1] - meeleeDamage[0] + 1) + meeleeDamage[0])
									+ 1);
					return;
				}
			}
		}

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void retire() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean move() {
		super.move();
		if (direction == 'u') {
			meleeRect.y = collisionRect.y + collisionRect.height;
			meleeRect.x = collisionRect.x - (Math.abs(collisionRect.getWidth() - meleeRect.getWidth()) / 2);
		} else if (direction == 'd') {
			meleeRect.y = collisionRect.y - meleeRect.height;
			// places below character, subtract meelee not just frame size
			meleeRect.x = collisionRect.x - (Math.abs(collisionRect.getWidth() - meleeRect.getWidth()) / 2);
		} else if (direction == 'r') {
			meleeRect.x = collisionRect.x + frameSizeX;
			meleeRect.y = collisionRect.y - (Math.abs(collisionRect.getHeight() - meleeRect.getHeight()) / 2);
		} else if (direction == 'l') {
			meleeRect.x = collisionRect.x - meleeRect.width;
			meleeRect.y = collisionRect.y - (Math.abs(collisionRect.getHeight() - meleeRect.getHeight()) / 2);
		}
		return true;
	}
	
	public void update(){
		super.update();
		if(isMoving)
		move();
	}
	
	public void setDamage(){
		this.meeleeDamage[1] = (int) (10 + (MenuScreen.getDamageMod() * 2));
		this.meeleeDamage[0] = (int) (meeleeDamage[1] - 5);
	}

	public void setMovementSpeed() {
		this.movementSpeed =  32 + (MenuScreen.getSpeedMod()-1) * 3;//3 is just a guesstimate
	}


	public void setMaxHealth() {
		this.maxHealth = 50 + (MenuScreen.getHealthMod() * 15);//just a guesstimate
	}
	public float getMaxHealth() {
		return this.maxHealth;
	}

	@Override
	public void setDirection(char passDirection) {
		if (direction != passDirection) {
			attackTimer = 0;
		}
		direction = passDirection;
		if (passDirection == 'l')
			flipped = true;
		else if (passDirection == 'r')
			flipped = false;
		switch (direction) {
		case 'u':
			meleeRect.width = frames[1].getRegionWidth();
			meleeRect.height = frames[1].getRegionHeight();
			meleeRect.y = collisionRect.y + frameSizeY;
			meleeRect.x = collisionRect.x - (Math.abs(collisionRect.getWidth() - meleeRect.getWidth()) / 2);
			break;
		case 'd':
			meleeRect.width = frames[1].getRegionWidth();
			meleeRect.height = frames[1].getRegionHeight();
			meleeRect.y = collisionRect.y - meleeRect.height;
			// places below character, subtract meelee not just frame size
			meleeRect.x = collisionRect.x - (Math.abs(collisionRect.getWidth() - meleeRect.getWidth()) / 2);
			break;
		case 'l':
			meleeRect.width = frames[1].getRegionHeight();
			meleeRect.height = frames[1].getRegionWidth();
			meleeRect.x = collisionRect.x - meleeRect.width;
			meleeRect.y = collisionRect.y - (Math.abs(collisionRect.getHeight() - meleeRect.getHeight()) / 2);
			break;
		case 'r':
			meleeRect.width = frames[1].getRegionHeight();
			meleeRect.height = frames[1].getRegionWidth();
			meleeRect.x = collisionRect.x + frameSizeX;
			meleeRect.y = collisionRect.y - (Math.abs(collisionRect.getHeight() - meleeRect.getHeight()) / 2);
			break;
		}
	}
	
	public String toString(){
		return "Player toString: "
				+ "\n Damage: " + meeleeDamage[1] + "~Speed: " + this.movementSpeed + "~MaxHealth: " + this.maxHealth;
	}

}

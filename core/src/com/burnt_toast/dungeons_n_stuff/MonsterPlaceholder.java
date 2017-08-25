package com.burnt_toast.dungeons_n_stuff;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.burnt_toast.dungeons_n_stuff.monsters.Slime;
import com.burnt_toast.monster_generator.Pool;
import com.burnt_toast.monster_generator.Poolable;

public class MonsterPlaceholder extends Poolable{
	private float x;
	private float y;
	private float health;
	private float sightRadius;
	private TextureRegion image;
	private boolean activated;
	private Vector3 tempRect;
	private MonsType myType;
	
	public enum MonsType{SLIME, WIZARD, HP}
	
	
	public MonsterPlaceholder(float passX, float passY,
			float passSightRadius, TextureRegion passImage, Pool<MonsterPlaceholder> parentPool){
		super(parentPool);
		image = passImage;
		sightRadius = passSightRadius;
		x = passX;
		y = passY;
		activated = false;
		
	}
	
	public void copyInfo(Slime sl){
		sl.setX(this.getX());
		sl.setY(this.getY());
		if(health != 0)
			sl.setHealth(this.health);
	}
	public void setSightRadius(float passSight){
		this.sightRadius = passSight;
	}
	public float getSightRadius(){
		return this.sightRadius;
	}
	public void setX(float passX){
		x = passX;
	}
	
	public void setY(float passY){
		y = passY;
	}
	public float getX(){
		return x;
	}
	public float getY(){
		return y;
	}
	public void setIfActivated(boolean passActive){
		activated = passActive;
	}
	public boolean getIfActivated(){
		return activated;
	}
	public void setImage(TextureRegion passImg){
		image = passImg;
	}
	public TextureRegion getImage(){
		return image;
	}
	public void draw(SpriteBatch batch){
		batch.draw(image, x,  y);
	}
	//wow am I glad I commented what I use that for because I had absolutely no idea.
	/**
	 * This method is used primarily for hashing the visibilty
	 * of the monster
	 * @return
	 */
	public Vector3  getRect(){
		tempRect.x = this.x - this.sightRadius;
		tempRect.y = this.y - this.sightRadius;
		tempRect.z = sightRadius;//the z holds the size of the square.
		return tempRect;
	}
	public void checkVisibility(float passX, float passY){
		if(Math.sqrt(Math.pow(Math.abs(passX-x), 2) + Math.pow(Math.abs(passY-y), 2)) 
				< sightRadius){//if the distance between the two is less than vis.
			activated = true;//then it saw something.
		}
	}
	
	public MonsType getMonsType(){
		return myType;
	}
	public void setMonsType(MonsType passType){
		myType = passType;
	}

	@Override
	public void reset() {
		//reset anykthing that needs reseting. 
		
	}

	@Override
	public void retire() {
		// TODO Auto-generated method stub
		activated = false;
		x = 0;
		y = 0;
		image = null;
	}

	
	
	
}

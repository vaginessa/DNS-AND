package com.burnt_toast.dungeons_n_stuff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimatedObject {
	private TextureRegion[] frames;
	private float animSpeed;
	private boolean loop;
	private boolean active;
	private int frameNum;
	private float timer;
	private float x;
	private float y;
	
	public AnimatedObject(TextureRegion[] frames){
		this.frames = frames; //YOU AT LEAST HAVE TO PUT FRAMES IN
		//you can change this later if it bugs you
	}
	
	public AnimatedObject(TextureRegion[] frames, float animSpeed, boolean passLoop,
			float passX, float passY){
		this.frames = frames;
		loop = passLoop;
		this.animSpeed = animSpeed;
		x = passX;
		y = passY;
	}

	public void draw(SpriteBatch batch){
		batch.draw(frames[frameNum], x, y);
	}
	
	public void update(){
		if(active){
			timer+=Gdx.graphics.getDeltaTime();
			if(timer >= animSpeed){
				frameNum++;
				timer = 0;
				if(frameNum == frames.length-1 && !loop){
					//active = false;//if we're not looping then we're done.
					//we leave frames at end, that's the animation
					this.stop();
				}
				if(frameNum == frames.length && loop){
					frameNum = 0;//reset frames if over
				}
			}
		}
	}
	
	public void play(){
		active = true;
		if(!loop){
			frameNum=0;
		}
	}

	/**
	 * returns if the animated object is playing.
	 * @return true or false if active.
	 */
	public boolean isActive(){
		if(active)return true;//if it's running, return true
		return false;//we're active, return false
	}
	
	public void stop(){
		active = false;
	}
	
	//GETTERS AND SETTERS
	
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}


	public TextureRegion[] getFrames() {
		return frames;
	}

	public void setFrames(TextureRegion[] frames) {
		this.frames = frames;
	}

	public float getFramesPerSec() {
		return animSpeed;
	}

	public void setFramesPerSec(float framesPerSec) {
		this.animSpeed = framesPerSec;
	}

	public boolean isLoop() {
		return loop;
	}

	public void setLoop(boolean loop) {
		this.loop = loop;
	}

}

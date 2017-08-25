package com.burnt_toast.dungeons_n_stuff;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Warrior extends Player{

	public Warrior() {
		super(MainFrame.warriorFrames);
		this.attackMaxTime = 0.25f;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void draw(SpriteBatch batch) {
		// TODO Auto-generated method stub
		//if flipped, then it draws, if not, then it doesn't draw flipped. long function holy macaroni.

		if(attackTimer > 0 ){//if we're attacking
			batch.setColor(1, 1, 1, attackTimer/attackMaxTime*0.75f);
			
			if(direction == 'r' || direction == 'l'){
			batch.draw(MainFrame.warriorFrames[2],
					flipped? meleeRect.x + meleeRect.getWidth():meleeRect.x,
					meleeRect.y,
					flipped?
					MainFrame.warriorFrames[2].getRegionWidth()*-1:MainFrame.warriorFrames[2].getRegionWidth(),
					MainFrame.warriorFrames[2].getRegionHeight());
			}//end if
			else if(direction == 'u' || direction == 'd'){
			batch.draw(MainFrame.warriorFrames[2], 					
					(direction=='d')? meleeRect.x:meleeRect.x,
					(direction == 'd')?(meleeRect.y + 8): meleeRect.y + 3,
					 0, 0,
					 (direction == 'd')?//if flipped y
					MainFrame.warriorFrames[2].getRegionWidth()*-1 : MainFrame.warriorFrames[2].getRegionWidth(),

					MainFrame.warriorFrames[2].getRegionHeight()*-1, 1, 1, 90);
			}//emd else
			batch.setColor(1, 1, 1, 1);
			
		}
		batch.draw(MainFrame.warriorFrames[animationIndex],
				flipped? collisionRect.x + MainFrame.warriorFrames[animationIndex].getRegionWidth():collisionRect.x,
				collisionRect.y,
				flipped?
				MainFrame.warriorFrames[animationIndex].getRegionWidth()*-1:MainFrame.warriorFrames[animationIndex].getRegionWidth(),
				MainFrame.warriorFrames[animationIndex].getRegionHeight());
		//this 2 line of code shows where the melee rect is at 
		//batch.draw(MainFrame.silverFrame, meleeRect.x, meleeRect.y, meleeRect.getWidth(),
				//meleeRect.getHeight());
	}

	@Override
	public void attack() {
		// TODO Auto-generated method stub
		this.attackTimer = this.attackMaxTime;
		super.attack();
	}

}

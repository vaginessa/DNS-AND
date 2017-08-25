package com.burnt_toast.dungeons_n_stuff.monsters;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.burnt_toast.dungeons_n_stuff.MainFrame;
import com.burnt_toast.dungeons_n_stuff.Monster;
import com.burnt_toast.dungeons_n_stuff.PlayScreen;
import com.burnt_toast.monster_generator.Pool;
import com.burnt_toast.monster_generator.Poolable;

public class Slime extends Monster{

	public Slime(Pool parentPool) {
		super(MainFrame.slimeFrames, parentPool);
		movementSpeed = 5;
		health = 2;
		// TODO Auto-generated constructor stub
	}

	@Override
	public float calcHealth() {
		// TODO Auto-generated method stub
		this.health = 2 + PlayScreen.floorLevel * 5;
		this.meeleeDamage[1] = 3 + (PlayScreen.floorLevel * 1);
		this.movementSpeed = 5 + 2 * PlayScreen.floorLevel;
		this.hitPause = 1.0f - (PlayScreen.floorLevel-1) * 0.2f;
		if(PlayScreen.floorLevel >= 6)this.hitPause = 1.0f - 4 * 0.2f;
		return 3;
		
	}


}

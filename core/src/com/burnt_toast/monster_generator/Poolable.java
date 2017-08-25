package com.burnt_toast.monster_generator;

import com.burnt_toast.dungeons_n_stuff.Monster;

public abstract class Poolable {
	protected boolean inUse;
	protected Pool parentPool;
	
	public  Poolable(Pool parentPool){
		this.parentPool = parentPool;
	}
	public static <M extends Poolable> M getANewMe(){
		//does nothing, can't return abstract class
		return null;
	}

	public abstract void reset();//resets the object
	public abstract void retire();//retires object
	public boolean getIfInUse(){
		return inUse;
	}
	public void toggleInUse(){
		if(inUse)inUse = false;
		else inUse = true;
	}
}

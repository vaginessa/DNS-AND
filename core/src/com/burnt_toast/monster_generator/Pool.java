package com.burnt_toast.monster_generator;

import java.util.LinkedList;

import com.burnt_toast.dungeons_n_stuff.Arrow;
import com.burnt_toast.dungeons_n_stuff.Monster;
import com.burnt_toast.dungeons_n_stuff.MonsterPlaceholder;
import com.burnt_toast.dungeons_n_stuff.MonsterPlaceholder.MonsType;
import com.burnt_toast.dungeons_n_stuff.monsters.Slime;


public  class Pool<G extends Poolable> {
	
		private LinkedList<G> retiredStuff;
		String poolCode;

        public Pool(){
            retiredStuff = new LinkedList<G>();
            this.poolCode = "null";
        }
		public Pool(String poolCode){
            this();
            this.poolCode = poolCode;
		}
		
		public void retireObject(G obj){
			retiredStuff.add(obj);
		}
		
		public G getObject(){
			if(retiredStuff.size() == 0)return makeAnObject();
			return retiredStuff.removeFirst();
		}
		
		private G makeAnObject(){
			if(poolCode == "slime"){
				return (G)new Slime(this);
			}
			if(poolCode == "arrow"){
				return (G)new Arrow(this);
			}
			return null;
		}
		

	
}

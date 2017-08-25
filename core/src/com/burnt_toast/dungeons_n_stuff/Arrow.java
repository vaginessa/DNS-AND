package com.burnt_toast.dungeons_n_stuff;


import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.burnt_toast.monster_generator.Pool;
import com.burnt_toast.monster_generator.Poolable;

/**
 * Created by Andrew Miles on 8/18/2017.
 */

public class Arrow extends Poolable{
    private Rectangle collisionRect;
    private Vector2 location;
    private Vector2 lastLocation;
    //the Rectangle is just for frame by frame collision, just in case it skips stuff.
    //so the arrow needs it's own place of x and y.
    //last location is for the collisionRect.

    private char direction;//'U'p 'L'eft 'R'ight or 'D'own
    private float speed;

    public Arrow(Pool parentPool){
        super(parentPool);
        location = new Vector2();
        collisionRect = new Rectangle();
    }

    public void update(){

    }

    public void set(int x, int y, float speed){

    }




    @Override
    public void reset(){
        direction = 'u';
    }
    @Override
    public void retire(){

    }
}

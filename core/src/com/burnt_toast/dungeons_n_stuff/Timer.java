package com.burnt_toast.dungeons_n_stuff;

/**
 * Created by andrewmiles on 7/13/17.
 */

public class Timer {
    float timerMax;
    float timerCurrent;
    boolean on;//if timer is on or off.

    public Timer(float passMax){
        timerMax = passMax;
        timerCurrent = 0;
    }

    public boolean update(float passTime){
        timerCurrent += passTime;
        if(timerCurrent >= timerMax){
            timerCurrent = timerMax + 1;
            return true;
        }
        return false;//timer AIN'T done yet, return false.
    }

    public void reset(){
        timerCurrent = 0.0f;
    }

    public void start(){
        on = true;
    }

    public void stop(){
        on = false;
    }
}

package com.burnt_toast.dungeons_n_stuff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class loop {
	public static void main(String[] args){
				char direction = 'l';
				float temp = 0;
				Vector2 dragCoordsThisFrame = new Vector2();
				Vector2 stopCoords = new Vector2();
				
		
				stopCoords.x = 50;
				stopCoords.y = 50;
				dragCoordsThisFrame.x = 77;
				dragCoordsThisFrame.y = 50;
		

				System.out.println("Start direction: " + direction);
				if(direction == 'd'){
					temp = (float)Math.toDegrees(Math.asin((1/distForm(stopCoords, dragCoordsThisFrame) *
							distForm(stopCoords.x, dragCoordsThisFrame.y, dragCoordsThisFrame.x, dragCoordsThisFrame.y))));
					//gets the angle

					if(temp <= 22.5){
						//then it's to the left. Angle is yeah
						System.out.print("is this hit?");
						direction = 'u';
					}
					else{
						//left or right
						if(dragCoordsThisFrame.x - stopCoords.x < 0) direction = 'l';
						else direction = 'r';
					}
				}
				else if(direction == 'u'){
					temp = (float)Math.toDegrees(Math.asin((1/distForm(stopCoords, dragCoordsThisFrame) *
							distForm(stopCoords.x, dragCoordsThisFrame.y, dragCoordsThisFrame.x, dragCoordsThisFrame.y))));
					//gets the angle
					if(temp<= 22.5){
						direction = 'd';
					}
					else{
						//left or right
						if(dragCoordsThisFrame.x - stopCoords.x < 0) direction = 'l';
						else direction = 'r';
					}
				}
				else if(direction == 'r' || direction == 'l'){
					temp = (float)Math.toDegrees(Math.asin((1/distForm(stopCoords, dragCoordsThisFrame) *
						distForm(dragCoordsThisFrame.x, stopCoords.y, dragCoordsThisFrame.x, dragCoordsThisFrame.y))));
					//gets the angle
					if(temp <= 22.5) {
						if (direction == 'r')direction = 'l';
						else if (direction == 'l')direction = 'r';
					}
					else{
						if(dragCoordsThisFrame.y > stopCoords.y)direction = 'u';
						else if(dragCoordsThisFrame.y <= stopCoords.y)direction = 'd';
					}
				}
				System.out.println("angle: " + temp);
				System.out.println("ending direct: " + direction);
			
			}
	
	/**
	 * Literally just made this for the touch dragged method.
	 * @param point1
	 * @param point2
     * @return returnst the Math.abs of the distance between the two points.
     */
	public static float distForm(Vector2 point1, Vector2 point2){
		return Math.abs(distForm(point1.x, point1.y, point2.x, point2.y));
	}
	public static float distForm(float x1, float y1, float x2, float y2){
		return (float) Math.sqrt(Math.abs(Math.pow(x2 - x1, 2) + (Math.pow(y2 - y1, 2))));
	}

}

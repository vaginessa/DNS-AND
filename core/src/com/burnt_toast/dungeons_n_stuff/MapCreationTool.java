package com.burnt_toast.dungeons_n_stuff;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.burnt_toast.maze_generator.MazeGenerator;

import java.util.LinkedList;

public class MapCreationTool {

	int[][] smallCollisionMap;
	int[][] currentCollisionMap;
	int[][] idMap;
	LinkedList<Vector2> deadEndCoords;//the coords of the dead ends.
	TiledMapTileLayer visualLayer;
	TiledMapTile floor;
	TiledMapTile wall;
	MazeGenerator mazeGen;
	int sizeUpFactor = 3;
	
	TiledMapTile topLeftTile;
	TiledMapTile topTile;
	TiledMapTile topRightTile;
	TiledMapTile rightTile;
	TiledMapTile leftTile;
	TiledMapTile bottomTile;
	TiledMapTile bottomLeftTile;
	TiledMapTile bottomRightTile;
	TiledMapTile middleBrickTile;
	TiledMapTile leftBrickTile;
	TiledMapTile rightBrickTile;
	TiledMapTile topRightCorner;
	TiledMapTile topLeftCorner;
	TiledMapTile botLeftCorner;
	TiledMapTile botRightCorner;
	TiledMapTile doorTile;

	public static int FLOOR = 0;
	public static int WALL = 1;
	//2 - 3 - 4
	//5 - - - 6
	//7 - 8 - 9
	public static int TOP_LEFT = 2;
	public static int TOP_RIGHT = 4;
	public static int TOP = 3;
	public static int RIGHT = 6;
	public static int LEFT = 5;
	public static int BOTTOM = 8;
	public static int BOTTOM_RIGHT = 9;
	public static int BOTTOM_LEFT = 7;
	//10 - 11 - 12
	public static int BRICK_MIDDLE = 11;
	public static int BRICK_RIGHT = 12;
	public static int BRICK_LEFT = 10;
	//13 - 14
	//-  -  -
	//15 - 16
	public static int TOP_LEFT_CORNER = 13;
	public static int TOP_RIGHT_CORNER = 14;
	public static int BOT_LEFT_CORNER = 15;
	public static int BOT_RIGHT_CORNER = 16;

	public static int DOOR_TILE = 17;
	
	//used for making map look pretty
	int idSum;
	
	public MapCreationTool(TiledMapTileLayer passVisLayer, TiledMapTileSet tileSet){
		visualLayer = passVisLayer;
		floor = tileSet.getTile(20);
		
		wall = tileSet.getTile(6);
		mazeGen = new MazeGenerator();

		topLeftTile = tileSet.getTile(3);
		topRightTile = tileSet.getTile(5);
		topTile = tileSet.getTile(4);
		rightTile = tileSet.getTile(21);
		leftTile = tileSet.getTile(19);
		bottomTile = tileSet.getTile(36);
		bottomRightTile = tileSet.getTile(37);
		bottomLeftTile = tileSet.getTile(35);
		middleBrickTile = tileSet.getTile(22);
		rightBrickTile = tileSet.getTile(54);
		leftBrickTile = tileSet.getTile(38);

		topLeftCorner = tileSet.getTile(1);
		topRightCorner = tileSet.getTile(2);
		botLeftCorner = tileSet.getTile(17);
		botRightCorner = tileSet.getTile(18);
		doorTile = tileSet.getTile(7);
		

	}
	
	/**
	 * This function makes the thing look so good. 
	 */
	public void makeItLookPretty(){


		/*
		 * double for loop to loop through every single block
		 * check 
		 */
		for(int k = currentCollisionMap.length-1; k > 0; k--){
			for(int i = 0; i < currentCollisionMap.length;i++){
				if(currentCollisionMap[i][k] == 0)continue;//if it is a floor, don't change
				idSum = 0;
				//System.out.println("Test------------------------------------------------");
				if(k != currentCollisionMap.length-1){//if not on top
					if(i != 0){//if not on left
						if(currentCollisionMap[i-1][k+1] == 0){//if top left is floor
							idSum+= 128;
						}//end if top left is floor
					}//end if not left
					if(i != currentCollisionMap.length-1){//if not on right
						if(currentCollisionMap[i + 1][k + 1] == 0){//if top right is floor
							idSum += 2;
						}//end if floor top right
					}//end if not on right
					
					if(currentCollisionMap[i][k+1] == 0){
						idSum +=1;//if top is floor, add.
					}
				}//end if not on top
				if(k != 0){//if not on bottom
					if(i != 0){//if not on left
						if(currentCollisionMap[i-1][k-1]== 0){//if bottom left is floor
							idSum+=32;//add to sum
						}//end if bottom left is not floor
					}//if not on left
					if(i != currentCollisionMap.length-1){//if not on right side of map
						if(currentCollisionMap[i+1][k-1] == 0){//if bottom right is floor
							idSum += 8;//add 8 to sum if that there is floor
						}//end if bottom right is floor
					}//end if not on right
					if(currentCollisionMap[i][k-1] == 0){//if direkt below is floor
						idSum += 16;//down
					}//end check below
				}//end if not on bottom
				if(i != 0){//if not on left
					if(currentCollisionMap[i-1][k] == 0){//if to the left is floor
						idSum+= 64;
					}
				}//end if not on left
				if(i != currentCollisionMap.length-1){//if not on right
					if(currentCollisionMap[i+1][k] == 0){//if right is floor
						idSum+= 4;
					}
				}
				switch(idSum){
				case 6:case 12:case 14://if left
					visualLayer.getCell(i, k).setTile(leftTile);//sets left
					idMap[i][k] = LEFT;
					break;
				case 0:
					//don't set anything
					break;
				case 143: //if top right corner
					visualLayer.getCell(i, k).setTile(topRightCorner);
					idMap[i][k] = TOP_RIGHT_CORNER;
					break;
				case 227: //if top left
					visualLayer.getCell(i, k).setTile(topLeftCorner);
					idMap[i][k] = TOP_LEFT_CORNER;
					break;
				case 248: //if bottom left
					visualLayer.getCell(i,k).setTile(leftBrickTile);
					visualLayer.getCell(i, k+1).setTile(botLeftCorner);
					idMap[i][k] = BRICK_LEFT;
					idMap[i][k+1] = BOT_LEFT_CORNER;
					break;
				case 62://if bottom right
					visualLayer.getCell(i, k).setTile(rightBrickTile);
					visualLayer.getCell(i, k+1).setTile(botRightCorner);
					idMap[i][k] = BRICK_RIGHT;
					idMap[i][k+1] = BOT_RIGHT_CORNER;
					break;
				case 96:case 192:case 224://if right
					visualLayer.getCell(i, k).setTile(rightTile);
						idMap[i][k] = RIGHT;
					break;
				case 129:case 130:case 131:case 3://if down
					visualLayer.getCell(i, k).setTile(bottomTile);
						idMap[i][k] = BOTTOM;
					break;
				case 32://if top right
					visualLayer.getCell(i, k).setTile(rightTile);
					idMap[i][k] = TOP_RIGHT;
					break;
				case 8://if top left
					visualLayer.getCell(i, k).setTile(leftTile);
					idMap[i][k] = LEFT;
					break;
				case 2://if bottom left
					visualLayer.getCell(i, k).setTile(bottomLeftTile);
					idMap[i][k] = BOTTOM_LEFT;
					break;
				case 128://if bottom right
					visualLayer.getCell(i, k).setTile(bottomRightTile);
					idMap[i][k] = BOTTOM_RIGHT;
					break;
				case 56://brick wall
					visualLayer.getCell(i, k).setTile(middleBrickTile);
					visualLayer.getCell(i, k+1).setTile(topTile);
					idMap[i][k] = BRICK_MIDDLE;
					idMap[i][k+1] = TOP;
					break;
				case 24://left brick wall
					visualLayer.getCell(i, k).setTile(middleBrickTile);
					visualLayer.getCell(i, k+1).setTile(topTile);
					visualLayer.getCell(i-1, k+1).setTile(topLeftTile);
					idMap[i][k] = BRICK_LEFT;
					idMap[i][k+1] = TOP;
					idMap[i-1][k+1] = TOP_LEFT;
					break;
				case 48://right brick wall
					visualLayer.getCell(i, k).setTile(middleBrickTile);
					visualLayer.getCell(i, k+1).setTile(topTile);
					visualLayer.getCell(i+1, k+1).setTile(topRightTile);
					idMap[i][k] = BRICK_RIGHT;
					idMap[i][k+1] = TOP;
					idMap[i+1][k+1] = TOP_RIGHT;
					break;
				case 4://left wall
					visualLayer.getCell(i, k).setTile(leftTile);
					idMap[i][k] = LEFT;
					break;
				default:
						//don't set anything it's a floor
					break;
				}//end switch
				System.out.print(idSum + ";");
			}//end column for
			System.out.println("");
		}//end row for
		visualLayer.getCell(currentCollisionMap.length-5, currentCollisionMap.length - 3).setTile(doorTile);
		idMap[currentCollisionMap.length-5][ currentCollisionMap.length - 3] = DOOR_TILE;
		//NOTES: DO NOT DELETE
		//visualLayer.getCell(1, 1).setTile(this.middleBrickTile);
		//visualLayer.getCell(1, 2).setTile(this.middleBrickTile); UP
		//visualLayer.getCell(2, 1).setTile(this.middleBrickTile); RIGHT
		//END NOTES
	}
	public int[][] getSmallCollisionMap(){
		return smallCollisionMap;
	}
	public void drawMap(SpriteBatch batch){
		for(int i = 0; i < idMap.length; i++){
			for(int k = 0; k < idMap.length; k++){
				batch.draw(MainFrame.mapTiles[idMap[i][k]], i*MainFrame.TILE_SIZE, k * MainFrame.TILE_SIZE);
			}
		}
	}

	public void clearMap(){
		//double for loop to clear map
		for(int k = visualLayer.getWidth()-1; k > 0; k--) {
			for (int i = 0; i < visualLayer.getHeight(); i++) {
				if(visualLayer.getCell(k, i).getTile().getId() != wall.getId()){//if this tile is not a wall
					visualLayer.getCell(k, i).setTile(wall);//sets them all to walls
					idMap[k][i] = WALL;
				}
			}
		}
	}

	/**
	 * The only parameter is the SMALL version of the size. 
	 * The small version is what the maze generator uses,
	 * and then the maze generation tool AUTOMATICALLY makes a 
	 * size that's double the size so yeah. Just do the small.
	 * @param mapSizeSmall So yeah this is the size that will be doubled.
	 */
	public int[][] prepareMap(int mapSizeSmall){



		smallCollisionMap = mazeGen.generateMaze(mapSizeSmall, true);
		currentCollisionMap = new int[(smallCollisionMap.length * sizeUpFactor)][(smallCollisionMap.length * sizeUpFactor)];
		idMap = currentCollisionMap;

		//CALC DEAD ENDS HERE.



		
		for(int i = 0; i < smallCollisionMap.length; i++){//Row Normal
			for(int k = 0; k < sizeUpFactor; k++){//row doubled 
				for(int l = 0; l < smallCollisionMap.length; l++){//column normal
					for(int m = 0; m < sizeUpFactor; m++){//column normal
						currentCollisionMap[i*sizeUpFactor+k][l*sizeUpFactor+m] = smallCollisionMap[i][l];
					}
				}
			}
		}
		for(int i = 0; i < currentCollisionMap.length; i++){
			for(int k = 0; k < currentCollisionMap.length; k++){
				System.out.print(currentCollisionMap[i][k]);
			}
			System.out.println();
		}
		for(int i = 0; i < currentCollisionMap.length;i++){
			for(int k = 0; k < currentCollisionMap.length;k++){
				if(currentCollisionMap[i][k] == 0)
				visualLayer.getCell(i, k).setTile(floor);
				else if(currentCollisionMap[i][k] == 1)
				visualLayer.getCell(i, k).setTile(wall);
			}
		}
		makeItLookPretty();
		return currentCollisionMap;
	}
}

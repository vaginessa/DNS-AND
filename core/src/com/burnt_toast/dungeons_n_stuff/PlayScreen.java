package com.burnt_toast.dungeons_n_stuff;

import java.util.HashMap;
import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.burnt_toast.dungeons_n_stuff.MonsterPlaceholder.MonsType;
import com.burnt_toast.dungeons_n_stuff.monsters.Slime;
import com.burnt_toast.monster_generator.Pool;
import com.dungeons_n_stuff.dungeon_layout.DungeonButton;

public class PlayScreen implements Screen, InputProcessor{

	private MainFrame main;
	private OrthogonalTiledMapRenderer otmr;
	private TiledMap mazeMap;
	private MapCreationTool mapTool;
	private MiniMap miniMap;
	private static int[][] collisionMap;
	private Stage playStage;
	private Stage hudStage;
	private OrthographicCamera orthoCam;
	private boolean pause;
	private boolean gameOver;
	private static float temp;
	private static Rectangle tempRect;
	
	public static int floorLevel;
	public static int score;
	private TiledMapTileLayer map;
	//hud
	private TextureRegion healthBar;
	private TextureRegion hpTag;
	private TextureRegion healthBorder;
	private DungeonButton miniMapBut;
	private DungeonButton pauseBut;
	private TextureRegion pixelForPause;//shown on screen when paused.
	
	private float widthWithZoom;
	private float heightWithZoom;
	
	//MONSTER STUFF
	private static LinkedList<Slime> activeSlimes;
	private Pool<Slime> slimePool;
	private Pool<Arrow> arrowPool;
	private LinkedList<MonsterPlaceholder> activePlaceholders;
	private Pool<MonsterPlaceholder> placeholderPool;
	private MonsterPlaceholder testPlaceholder;
	
	//HASH MAP STUFF
	static HashMap<Float, LinkedList<Character>> characterHash;
	HashMap<Float, LinkedList<MonsterPlaceholder>> placeholderHash;
	
	//moving character DRAG STUFF
	float dragDifX;
	private float dragDifY;
	private Vector2 dragCoordsThisFrame;
	private Vector2 dragCoordsLastFrame;
	private float dragChangeX;
	private float dragChangeY;
	private float dragDeadZoneSame;
	private float dragDeadZoneOpposite;

	//NEW DRAG STUFF
	Timer stopTimer;
	float stoppingSpeed = 30;//the pixels per second that it guesses you're stopping
	// /|\ 5 is a guess.
	Vector2 stopCoords;//the last stop point
	Vector2 movePoint;//the point the finger is at moving
	float pixsPerSecond;
	float moveSlope;
	private float anchorDirection;

	
	private Vector2 touchCoordsTemp;
	
	private static Player currentPlayer;
	
	private int endButIndexX;//the block where it's randomed x and y
	private int endButIndexY;
	private Rectangle endDoorRect;
	//private Rectangle buttonRect;
	private AnimatedObject endDoor;
	//private boolean doorButtonPressed;
	private boolean doorIsOpen;
	private boolean keyFound;
	private Rectangle keyRect;
	private TextureRegion key;

	private String tempStr;
	
	private boolean firstTimeShown;//to know if the screen has been shown before.
	
	
	public PlayScreen(MainFrame passedMain){
		main = passedMain;
		orthoCam = new OrthographicCamera(MainFrame.SCREEN_WIDTH, MainFrame.SCREEN_HEIGHT);
		playStage = new Stage(new ExtendViewport(MainFrame.SCREEN_WIDTH, MainFrame.SCREEN_HEIGHT, orthoCam));
		hudStage = new Stage(new ExtendViewport(MainFrame.SCREEN_WIDTH, MainFrame.SCREEN_HEIGHT));
		mazeMap = main.mapLoader.load("maps/rogueMap.tmx");
		otmr = new OrthogonalTiledMapRenderer(mazeMap);
		pause = false;
		pixelForPause = new TextureRegion(MainFrame.mainTileset, 65, 34, 1, 1);
		miniMap = new MiniMap(new TextureRegion(MainFrame.mainTileset, 65, 34, 1, 1), //seen
				new TextureRegion(MainFrame.mainTileset, 65, 32, 1, 1),//unseen
				new TextureRegion(MainFrame.mainTileset, 64, 34, 1, 1),//you are here
				new TextureRegion(MainFrame.mainTileset, 0, 0, 1, 1),
				hudStage.getWidth()/2, hudStage.getHeight()/2);
		collisionMap = new int[50][50];
		
		//DRAG STUFF
		dragCoordsThisFrame = new Vector2();
		dragCoordsLastFrame = new Vector2();
		dragDeadZoneSame = 5;//just a beginners estimate
		dragDeadZoneOpposite = 3;
		touchCoordsTemp = new Vector2();

//		//NEW DRAG STUFF
//		upCoord = new Vector2(MainFrame.SCREEN_WIDTH/6, MainFrame.SCREEN_HEIGHT / 3 * 2);
//		downCoord = new Vector2(MainFrame.SCREEN_WIDTH/6, 0);
//		leftCoord = new Vector2(0, MainFrame.SCREEN_HEIGHT/3);
//		rightCoord = new Vector2(MainFrame.SCREEN_WIDTH/6 * 2, MainFrame.SCREEN_HEIGHT/3);

		
		//MAP CREATION
		mapTool = new MapCreationTool(((TiledMapTileLayer)mazeMap.getLayers().get(0)),
				mazeMap.getTileSets().getTileSet(0));
		
		//HUD
		pauseBut = new DungeonButton("Pause", main);
		pauseBut.setWindowSize(hudStage.getWidth()/5, 20);
		pauseBut.setWindowCoords(hudStage.getWidth()/5 * 4, hudStage.getViewport().getWorldHeight()-
				pauseBut.getHeight());
		pauseBut.setBorderScale(2);
		pauseBut.setTextColor(Color.WHITE);
		pauseBut.setTextSize(1.5f);
		miniMapBut = new DungeonButton("Map", main);
		miniMapBut.setWindowSize(hudStage.getWidth()/5, 20);
		miniMapBut.setWindowCoords(0, hudStage.getViewport().getWorldHeight() - miniMapBut.getHeight());
		miniMapBut.setBorderScale(1.5f);
		miniMapBut.setTextColor(Color.WHITE);
		
		
		
		healthBar = new TextureRegion(MainFrame.mainTileset, 44, 49, 43, 4);
		hpTag = new TextureRegion(MainFrame.mainTileset, 44, 53, 9, 4);
		healthBorder = new TextureRegion(MainFrame.mainTileset, 43, 41, 45, 6);
		
		//Monster Generator
		//testPlaceholder = new MonsterPlaceholder(MainFrame.TILE_SIZE * 8, MainFrame.TILE_SIZE * 4,
		//		3 * MainFrame.TILE_SIZE/*sight radius*/, MainFrame.slimeFrames[0]);
		activePlaceholders = new LinkedList<MonsterPlaceholder>();
		placeholderPool = new Pool<MonsterPlaceholder>();
		activeSlimes = new LinkedList<Slime>();
		slimePool = new Pool<Slime>("slime");
		arrowPool = new Pool<Arrow>("arrow");
		tempRect = new Rectangle();
		
		characterHash = new HashMap<Float, LinkedList<Character>>();
		placeholderHash = new HashMap<Float, LinkedList<MonsterPlaceholder>>();
		
		orthoCam.zoom -= 0.5;
		
		key = MainFrame.key;
		keyRect = new Rectangle(0, 0, key.getRegionWidth(), key.getRegionHeight());
		endDoorRect = new Rectangle(0, 0, 6, 2);
		endDoor = new AnimatedObject(MainFrame.doorFrames, 0.05f, false, 0, 0);
		floorLevel = 1;

		//NEW DRAG STUFF
		stopTimer = new Timer(0.0001f);
		stopCoords = new Vector2();
		movePoint = new Vector2();
		
		floorLevel = 0;
	}
	
	
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		main.addInputProcessor(this);
//		if(floorLevel != 0){
//
//			//main.fadeIn = true;
//			activePlaceholders.clear();
//			activeSlimes.clear();
//			
//			loadMap();
//			currentPlayer.setHealth(currentPlayer.getHealth());
//		}
		pauseBut.setButtonText("Pause");
		currentPlayer.setMaxHealth();
		currentPlayer.setDamage();
		currentPlayer.setMovementSpeed();
		System.out.println(currentPlayer);
		
		if(floorLevel == 0){
			gameOver = false;
			main.menuScreen.setAttackRadMod(1);
			main.menuScreen.setHealthMod(1);
			main.menuScreen.setSpeedMod(1);
			main.menuScreen.setDamageMod(1);//reset upgrade stuff.
			mapTool.clearMap();
			
		}
			activePlaceholders.clear();
			activeSlimes.clear();
			loadMap();
			currentPlayer.setHealth(currentPlayer.getMaxHealth());
			otmr.setMap(mazeMap);
			//main.fadeIn = true;
			//main.fadeOut = false;
			orthoCam.update();

			miniMap.setMidOfScreen(hudStage.getWidth()/2, hudStage.getHeight()/2);
			
			
			
			


		
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		Gdx.gl.glClearColor(
				55f * (1f/255f) * main.fadeTracker,
				65f * (1f/255f) * main.fadeTracker,
				113f * (1f/255f) * main.fadeTracker, 1
		);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
			//if fading
			tempStr = main.updateFade();
			main.fade(playStage.getBatch());
			main.fade(otmr.getBatch());
			main.fade(main.gameFont);
			if (tempStr.equals("next level")) {
				if(floorLevel % 2 == 0){
				main.setScreen(main.menuScreen);
				}
				else{
					main.setFadeCode("nah ya'll");
					loadMap();
				}
			}
			else if(tempStr.equals("game over")){
				main.adHandler.showAds();
				main.setScreen(main.menuScreen);
			}


		
		
		//ERIC
		otmr.setView(orthoCam);
		playStage.act();
		playStage.draw();
		hudStage.act();
		hudStage.draw();
		
		//otmr.render();
		otmr.getBatch().begin();
		//otmr.renderTileLayer((TiledMapTileLayer)mazeMap.getLayers().get(0));
		otmr.getBatch().end();
		

		
		playStage.getBatch().begin();
		mapTool.drawMap((SpriteBatch)playStage.getBatch());
		//stuff drawn in order of placement in front of stuff
		if(keyFound){
			endDoor.draw((SpriteBatch)playStage.getBatch());
		}
		else{
			playStage.getBatch().draw(key, keyRect.x, keyRect.y);
		}
		for(Slime sl: activeSlimes){
			sl.draw((SpriteBatch)playStage.getBatch());
		}
		currentPlayer.draw((SpriteBatch)playStage.getBatch());
		//draw the test slime
		

		for(MonsterPlaceholder m: activePlaceholders){
			if(m!= null)
			m.draw((SpriteBatch)playStage.getBatch());
		}

		miniMap.drawVisibilityOnMap((SpriteBatch)playStage.getBatch());
		//main.gameFont.draw(playStage.getBatch(), "VER 1.0 7/5/2017", 0, 0);
		playStage.getBatch().end();
		
		
		
		hudStage.getBatch().begin();
		if(keyFound)hudStage.getBatch().draw(key, pauseBut.getX() - key.getRegionWidth(), pauseBut.getY(), key.getRegionWidth() * 2, key.getRegionHeight() * 2);//draws at top right corner. Not pretty, fix later.
		//TEMP CODE ABOVE
		
		hudStage.getBatch().draw(healthBorder, hudStage.getWidth()/2- healthBorder.getRegionWidth(), hudStage.getHeight()-35,
				healthBorder.getRegionWidth() * 2, healthBorder.getRegionHeight() * 2);
		hudStage.getBatch().draw(healthBar, hudStage.getWidth()/2 - healthBar.getRegionWidth(),  hudStage.getHeight()-35+2,
				(currentPlayer.getHealth()/currentPlayer.getMaxHealth())*43 * 2, healthBar.getRegionHeight() * 2);
		hudStage.getBatch().draw(hpTag, hudStage.getWidth()/2- hpTag.getRegionWidth(),
				hudStage.getHeight()-35+2,
				hpTag.getRegionWidth() * 2, hpTag.getRegionHeight() * 2);
//
		//this next line is if any time I want the input to be showing on screen.
		//hudStage.getBatch().draw(MainFrame.buttonFrames[0], stopCoords.x, stopCoords.y);
		main.gameFont.draw(hudStage.getBatch(), "Floor Level: " + floorLevel + "\t Score: " + score, 0, 20);
		if(pause && !currentPlayer.getRectangle().overlaps(endDoorRect)){
			hudStage.getBatch().setColor(0.2f, 0.2f, 0.2f, 0.7f);
			hudStage.getBatch().draw(pixelForPause, 0, 0, hudStage.getViewport().getWorldWidth(),
					hudStage.getViewport().getWorldHeight());
			hudStage.getBatch().setColor(1, 1, 1, 1);
			if(!miniMap.getIfOnScreen())//only say paused if map isn't on screen && not game over
			main.gameFont.draw(hudStage.getBatch(), (gameOver? "GAME OVER \nscore: " + score + "\n Version: " + MainFrame.VERSION: "PAUSED")
					, hudStage.getViewport().getWorldWidth()/2 - 6,
					hudStage.getViewport().getWorldHeight()/2 - 3);
		}
		miniMap.draw((SpriteBatch)hudStage.getBatch());
		if((pause && miniMap.getIfOnScreen()) || (!pause && !miniMap.getIfOnScreen())){
			miniMapBut.draw((SpriteBatch)hudStage.getBatch(), main.gameFont);
		}
		if(!miniMap.getIfOnScreen()) pauseBut.draw((SpriteBatch)hudStage.getBatch(), main.gameFont);
		hudStage.getBatch().end();
		
		if(currentPlayer.getHealth() <= 0){
			pauseBut.setButtonText("Reset");
			gameOver = true;
			pause = true;
			//floorLevel = 0;
		}
		
		
		
		//INPUT
		if(!pause && !main.fadeIn && !main.fadeOut){
			//CALCULATE
			//clear hash
			this.clearHash();
			//rehash everything
			for(Slime sl: activeSlimes){
				hashCharacter(sl);
			}
			this.hashCharacter(currentPlayer);
			
			for(int i = 0; i < activeSlimes.size(); i++){
				activeSlimes.get(i).update(currentPlayer);
				if(!activeSlimes.get(i).getIfInUse()){//if it's no longer alive then
					generateHealthAt(activeSlimes.get(i).getX(), activeSlimes.get(i).getY());
					activeSlimes.get(i).retire();
					score += 20 + 5*floorLevel;//higher score addition every floor

					activeSlimes.remove(i);
					
				}
			}
			for(MonsterPlaceholder m: activePlaceholders){
				m.checkVisibility(currentPlayer.getX(), currentPlayer.getY());
			}
			for(int i = 0; i < activePlaceholders.size(); i++){
				if(activePlaceholders.get(i).getIfActivated()){
					//remove the placholder from the linked list here
					activePlaceholders.get(i).toggleInUse();
					if(activePlaceholders.get(i).getMonsType() == MonsterPlaceholder.MonsType.SLIME){
						PlayScreen.activeSlimes.add(slimePool.getObject());
						if(activeSlimes.getLast() == null){//if there were none in the pool
							System.out.println("RETURNED NULL FROM POOL");
							//System.out.println("Yeah");
							activeSlimes.removeLast();
							activeSlimes.add(new Slime(slimePool));
						}//end if slime is null
						activeSlimes.getLast().calcHealth();
						activeSlimes.getLast().toggleInUse();
						activePlaceholders.get(i).copyInfo(activeSlimes.getLast());//pass the info along
					}
					if(activePlaceholders.get(i).getMonsType() == MonsType.HP){
						currentPlayer.heal(2 + floorLevel);
						if(currentPlayer.getHealth() > currentPlayer.getMaxHealth()){
							currentPlayer.setHealth(currentPlayer.getMaxHealth());
						}
					}
					activePlaceholders.get(i).retire();
					activePlaceholders.remove(i);
				}
			}
			endDoor.update();
			currentPlayer.update();
			
			if(currentPlayer.getRectangle().overlaps(keyRect)){
				if(!keyFound){
				this.keyFound = true;

				}
			}
			if(distForm(currentPlayer.getX(), currentPlayer.getY(), endDoorRect.getX(), endDoorRect.getY()) < MainFrame.TILE_SIZE * 1.5
					&& !doorIsOpen && !endDoor.isActive()){
				endDoor.play();
			}
			if(keyFound && currentPlayer.getRectangle().overlaps(endDoorRect)){
				if(doorIsOpen) {//stepped in place and door is open.
					//finished the level WOOP now:
					currentPlayer.setIfMoving(false);
					pause = true;
					main.fadeOut();
					main.setFadeCode("next level");
					if (floorLevel % 2 == 0) {
						main.menuScreen.addUpgPoint();
					}
					score += 100;
				}
			}
		}//end if not pause

		
		widthWithZoom = playStage.getWidth() * ((OrthographicCamera)(playStage.getCamera())).zoom;
		heightWithZoom = playStage.getHeight() * ((OrthographicCamera)(playStage.getCamera())).zoom;

		orthoCam.position.set(currentPlayer.getX(), currentPlayer.getY(), 0);
		miniMap.update();
		miniMap.activateBlock(round(currentPlayer.getX(),  24, false)/24,
				round(currentPlayer.getY(), 24, false)/24, this);
		
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		playStage.getViewport().update(width, height, true);
		orthoCam.update();
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		pause = true;
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		main.removeInputProcessor(this);
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}


	public void loadMap(){//mapWidth = menuMap.getProperties().get("width" , Integer.class)
//		//load collision:
//		for(int i = 0; i < (5 );i++){
//			for(int j = 0; j < (5);j++){
//				if(((TiledMapTileLayer)mazeMap.getLayers().get(2)).getCell(i, j) != null){
//					if(((TiledMapTileLayer)mazeMap.getLayers().get("collision")).getCell(i, j).getTile().getId() == 32){
//						//tile is red in collision map
//						collisionMap[i][j] = 1;//passable
//					}//end if red
//					else{
//						collisionMap[i][j] = 0;//unpassable
//						System.out.println(((TiledMapTileLayer)mazeMap.getLayers().get("collision")).getCell(i, j).getTile().getId());
//						System.out.println(mazeMap.getProperties().get("width" , Integer.class) + "ALKSJDF");
//					}//end else
//				}//endif null
//				else{
//					collisionMap[i][j] = 0;//unpassable
//				}
//			}
//		}//end nested for loop for loading collision
//		System.out.println(collisionMap[0].length-1);
//		for(int i = 0; i < collisionMap[0].length-1;i++){
//			for (int j = 0; j < collisionMap[0].length-1;j++){
//				System.out.print(collisionMap[i][j]);
//			}
//			System.out.println();
//		}
		if(gameOver){
			gameOver = false;
			pause = false;
		}
		floorLevel++;
		main.fadeIn();
		
		//I decided to change it to plus
		collisionMap = mapTool.prepareMap(4 + (2*(PlayScreen.floorLevel-1)));

		this.miniMap.setMapVerbose(mapTool.getSmallCollisionMap(), true);
		
		endDoor.setX((collisionMap.length-5)*8);
		endDoor.setY((collisionMap.length-3)*8);
		endDoorRect.setX((collisionMap.length - 5) * 8);
		endDoorRect.setY((collisionMap.length-3) * 8 - endDoorRect.getHeight());
		if(floorLevel < 3){//if first two levels, then button is next to door
			keyRect.x = (collisionMap.length-5)* MainFrame.TILE_SIZE;
			keyRect.y = (collisionMap.length-6)* MainFrame.TILE_SIZE;
		}
		else{
			//if the floor level is greater than 2.
			for(int i = 0; i < 100; i++){
				endButIndexX = (int)(Math.random() * collisionMap.length);
				endButIndexY = (int)(Math.random() * collisionMap.length);
				if(collisionMap[endButIndexX][endButIndexY] == 0)break;
				//if we finally found one, we're out. One that's open.
				if(i == 99){
					keyRect.x = 1 * (MainFrame.TILE_SIZE * 3) + MainFrame.TILE_SIZE;
					keyRect.y = 3 * (MainFrame.TILE_SIZE * 3) + MainFrame.TILE_SIZE;
				}
			}
		}
		

		
		currentPlayer.setPosition(3 * MainFrame.TILE_SIZE, 3 * MainFrame.TILE_SIZE);
		currentPlayer.setDirection('u');//up at default.
		System.out.println("ACTIVATED FIRST BLOCK");
		miniMap.activateBlock(1, 1, this, false);
		pause = false;
		doorIsOpen = false;
		if(floorLevel >=6) currentPlayer.maxHealth = 100;
		//main.fadeIn();
		
	}//end load map
	public void buttonCode(String buttonName){
		
	}
	

	
	//check collision
	public static boolean checkCollisionAt(float x, float y,float width, float height){
		if(collisionMap[round(x, 8, false)/8][round(y,8, false)/8] == 1 ||//bottom left corner
				collisionMap[round(x, 8, false)/8][round(y + height, 8, false)/8] == 1 ||//top left corner
				collisionMap[round(x + width, 8, false)/8][round(y + height, 8, false)/8] == 1 ||//top right corner
				collisionMap[round(x + width, 8, false)/8][round(y, 8, false)/8] == 1){
			return true;}//bottom right corner. return true if collide
		else{return false;}
	}
	/**
	 * rounds num to the round number with an option to round up or down. (true = round up::false = round down)
	 * same rounding function as round(num, round) just with option of up or down
	 * @param num number to round
	 * @param round factor to round to
	 * @param roundDirection true = round up::false = round down
	 * @return
	 */
	public static int round(float num, int round, boolean roundDirection){
		if(roundDirection){//round num up
			return (int) (num + (round - (num % round)));//return the uppper round
		}
		else{//round num down
			return (int) (num - (num % round));//return the lower round
		}
	}
	public void setCharacter(char character){
		switch(character){
		case 'a':
			//archer
			currentPlayer = new Archer();
			break;
		case 'w':
			//wizard
			currentPlayer = new Wizard();
			break;
		case 'r':
			//warrior
			currentPlayer = new Warrior();
			break;
		default:
			//archer
			currentPlayer = new Archer();
		}
	}

	//HASH MAP STUFF
	public static LinkedList<Character> getCharactersAt(float x, float y){
		temp = hash(x, y);
		if(!characterHash.containsKey(temp)){
			return null;
		}
		return characterHash.get(temp);
	}
	public static float hash(float x, float y){
		return (float) ((int)(MainFrame.TILE_SIZE) * Math.sqrt((int)(y/MainFrame.TILE_SIZE)));
	}
	public void clearHash(){
		for(Float key: characterHash.keySet()){
			characterHash.get(key).clear();
		}
	}
	public void generateHealthAt(float x, float y){
		//if floor level is over 15 then as long as it's bigger than 0.5,
		//because at floor 15 it has to be bigger than .95
		if(Math.random() >= (floorLevel>15?0.9 : (0.6 + (0.07 * (floorLevel/3))))){
			activePlaceholders.add(placeholderPool.getObject());
			if(activePlaceholders.getLast() == null){
				activePlaceholders.removeLast();
				activePlaceholders.add(new MonsterPlaceholder(x, y, 
						MainFrame.TILE_SIZE/*sight radius*/, this.hpTag, placeholderPool/*pool that it belongs to*/));
			}
			activePlaceholders.getLast().setSightRadius(MainFrame.TILE_SIZE);
			activePlaceholders.getLast().setImage(this.hpTag);
			activePlaceholders.getLast().setMonsType(MonsType.HP);
		}
	}
	public void generateMonsterAt(float x, float y){
		if(Math.random() >= (0.9 - 0.1 * floorLevel)){
		activePlaceholders.add(placeholderPool.getObject());//
		if(activePlaceholders.getLast() == null){
			activePlaceholders.removeLast();
			activePlaceholders.add(new MonsterPlaceholder(x + MainFrame.TILE_SIZE, y + MainFrame.TILE_SIZE,
						(3  + floorLevel) * MainFrame.TILE_SIZE/*sight radius*/, MainFrame.slimeFrames[0], placeholderPool));
		}
		activePlaceholders.getLast().setSightRadius((3+floorLevel) * MainFrame.TILE_SIZE);
		activePlaceholders.getLast().setImage(MainFrame.slimeFrames[0]);
		activePlaceholders.getLast().setMonsType(MonsType.SLIME);
		}
	}
	
	public <G extends Character> void hashCharacter(Character passChar){
		//bottom right corner
		temp = hash(passChar.getX(), passChar.getY()); //get hash key
		if(!PlayScreen.characterHash.containsKey(temp)){//if list doesn't exist
			characterHash.put(temp, new LinkedList<Character>());//make it
		}
		characterHash.get(temp).add(passChar);//add character to hash
		//top right corner next
		temp = hash(passChar.getX() + passChar.getRectangle().width,
				passChar.getY() + passChar.getRectangle().width);
		if(!PlayScreen.characterHash.containsKey(temp)){
			characterHash.put(temp, new LinkedList<Character>());
		}
		characterHash.get(temp).add(passChar);
	}
	
	public void hashPlaceholder(MonsterPlaceholder passMonst){
		
	}
	public static Player getCurrentPlayer(){
		return currentPlayer;
	}
	
	public static boolean checkCharacterCollision(float x, float y, Rectangle rect){
		
		tempRect.setWidth(rect.getWidth());
		tempRect.setHeight(rect.getHeight());
		tempRect.setX(x);
		tempRect.setY(y);
		for(Slime sl: activeSlimes){
			if(sl.getRectangle().overlaps(tempRect) && !sl.getRectangle().equals(rect)){
				return true;
			}
		}
		if(currentPlayer.getRectangle().overlaps(rect) && !currentPlayer.getRectangle().equals(rect)){
			return true;
		}
		return false;
	}
	
	public static float distForm(float x1, float y1, float x2, float y2){
		return (float) Math.sqrt(Math.abs(Math.pow(x2 - x1, 2) + (Math.pow(y2 - y1, 2))));
	}
	public int randomBetween(float num1, float num2){
		if(num2 < num1){
			System.err.println("Num2 must be < num1 in randomBetween(num1, num2)");
		}
		return (int)(Math.random() * (num2 - num1) +num1);
	}
	
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		if(main.fadeIn || main.fadeOut)return false;
		if(keycode == Keys.SPACE){
//			if(gameOver){
//				main.setScreen(main.menuScreen);
//				main.fadeIn = true;
//				main.fadeOut = false;
//				return true;
//			}
			currentPlayer.attack();
			System.out.println("One n Done");
			return true;
		}
		if(keycode == Keys.ENTER){
			if(gameOver){
				//main.fadeOut = true;
				//main.fadeIn = false;
				return true;
			}
		}
		else if(keycode == Keys.W){
			currentPlayer.setDirection('u');
			currentPlayer.setIfMoving(true);
			return true;
		}
		else if(keycode == Keys.A){
			currentPlayer.setDirection('l');
			currentPlayer.setIfMoving(true);
			return true;
		}
		else if(keycode == Keys.S){
			currentPlayer.setDirection('d');
			currentPlayer.setIfMoving(true);
			return true;
		}
		else if(keycode == Keys.D){
			currentPlayer.setDirection('r');
			currentPlayer.setIfMoving(true);
			return true;
		}
		else if(keycode == Keys.P){
			if(pause && (!gameOver)){
				pause = false;
			}
			else
				pause = true;
		}
		else if(keycode == Keys.G){
			doorIsOpen = true;
			currentPlayer.setPosition(endDoorRect.getX(), endDoorRect.getY());
		}
		else if(keycode == Keys.UP){
			main.menuScreen.addUpgPoint();
		}
		else if(keycode == Keys.M){
			miniMap.toggleIfOnScreen();
			return true;
		}
		return false;
	}


	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		if(keycode == Keys.A ||keycode == Keys.S || keycode == Keys.D || keycode == Keys.W){
			if(!Gdx.input.isKeyPressed(Keys.A) &&
			!Gdx.input.isKeyPressed(Keys.S) &&
			!Gdx.input.isKeyPressed(Keys.D) &&
			!Gdx.input.isKeyPressed(Keys.W)){
				currentPlayer.setIfMoving(false);
			}
			if(Gdx.input.isKeyPressed(Keys.A)){
				currentPlayer.setDirection('l');
			}
			else if(Gdx.input.isKeyPressed(Keys.W)){
				currentPlayer.setDirection('u');
			}
			else if(Gdx.input.isKeyPressed(Keys.S)){
				currentPlayer.setDirection('d');
			}
			else if(Gdx.input.isKeyPressed(Keys.D)){
				currentPlayer.setDirection('r');
			}
		}
		return false;
	}


	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
	
		touchCoordsTemp.x = screenX;
		touchCoordsTemp.y = screenY;
		hudStage.getViewport().unproject(touchCoordsTemp);
		if(miniMapBut.checkClick(touchCoordsTemp.x, touchCoordsTemp.y)){
			miniMap.setIfOnScreen(miniMap.getIfOnScreen()? false:true);
			pause = pause?false:true;
			return true;
		}
		if(pauseBut.checkClick(touchCoordsTemp.x, touchCoordsTemp.y) && !miniMap.getIfOnScreen()){
			if(gameOver){
				main.setFadeCode("game over");
				main.fadeOut();
				floorLevel = 0;
				return true;
			}
			else {
				pause = pause ? false : true;//toggle pause
				return true;
			}

		}
		if(pause)return false;
		touchCoordsTemp.x = screenX;
		touchCoordsTemp.y = screenY;
		playStage.getViewport().unproject(touchCoordsTemp);
		if(screenX < Gdx.graphics.getWidth() / 2){

			//if its on the left side,
			//currentPlayer.setIfMoving(true);//start moving
//			touchDragged(screenX, screenY, pointer);
			stopCoords.x = touchCoordsTemp.x;
			stopCoords.y = touchCoordsTemp.y;
			return true;
		}
		else if(screenX > Gdx.graphics.getWidth() / 2){
			//if on the right side
			
			currentPlayer.attack();
			return true;
		}
		
		return false;
	}


	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		if(pause)return false;
		touchCoordsTemp.x = screenX;
		touchCoordsTemp.y = screenY;
		playStage.getViewport().unproject(touchCoordsTemp);
		if(screenX < Gdx.graphics.getWidth() / 2){
			//if its on the left side,
			currentPlayer.setIfMoving(false);//stop moving, 
			dragCoordsThisFrame.x = -1;
			dragCoordsThisFrame.y = -1;
			return true;
		}
		return false;
	}

/*
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {

		if(screenX < Gdx.graphics.getWidth() / 2) {
			if (dragCoordsLastFrame.x == -1) {
				dragCoordsLastFrame.x = dragCoordsThisFrame.x;
				dragCoordsLastFrame.y = dragCoordsThisFrame.y;
				dragCoordsThisFrame.x = screenX;
				dragCoordsThisFrame.y = screenY;//set this frame's
				hudStage.getViewport().unproject(dragCoordsThisFrame);
			} else {
				dragCoordsLastFrame.x = dragCoordsThisFrame.x;
				dragCoordsLastFrame.y = dragCoordsThisFrame.y;
				dragCoordsThisFrame.x = screenX;
				dragCoordsThisFrame.y = screenY;//set this frame's
				hudStage.getViewport().unproject(dragCoordsThisFrame);
				dragDifX = dragCoordsThisFrame.x - dragCoordsLastFrame.x;//difference of x drags.
				dragDifY = dragCoordsThisFrame.y - dragCoordsLastFrame.y;//difference of y drags.
			}
			System.out.println(dragDifX + ", " + dragDifY);

			if (dragCoordsThisFrame.x > widthWithZoom / 2) {
				//currentPlayer.setIfMoving(false);//they dragged out of moving
				//System.out.println(screenX + ", " + screenY + ", " + (widthWithZoom/2));
			}
			if (currentPlayer.getIfMoving() == false && dragCoordsThisFrame.x < widthWithZoom / 2) {
				//if they're not moving and they dragged into the moving, then move.
				//currentPlayer.setIfMoving(true);
			}

			if (dragCoordsThisFrame.x < Gdx.graphics.getWidth() / 2) {//in moving bounds.
				if (currentPlayer.getDirection() == 'r') {//GOING RIGHT
					if (Math.abs(dragDifY) >= Math.abs(dragDifX)) {//if y is bigger
						if (Math.abs(dragDifX) > dragDeadZoneOpposite) {//if y is greater than dead zone
							if (dragDifY > 0) {
								//going up
								currentPlayer.setDirection('u');
							} else {
								currentPlayer.setDirection('d');
							}
						}
					} else {
						if (dragDifX < dragDeadZoneSame * -1)
							//going left
							currentPlayer.setDirection('l');
					}

				}//END GOING RIGHT
				if (currentPlayer.getDirection() == 'l') {//GOING LEFT
					if (Math.abs(dragDifY) >= Math.abs(dragDifX)) {//if y diff is bigger than x
						if (Math.abs(dragDifY) > dragDeadZoneOpposite) {
							if (dragDifY > 0) {
								//going up
								currentPlayer.setDirection('u');//set up
							}
							if (dragDifY < 0) {
								//going down
								currentPlayer.setDirection('d');
							}
						}
					} else {
						if (dragDifX > dragDeadZoneSame) {
							//going right
							currentPlayer.setDirection('r');
						}
					}

				}//END GOING LEFT
				if (currentPlayer.getDirection() == 'u') {//GOING UP
					if (Math.abs(dragDifX) >= Math.abs(dragDifY)) {//if x diff is bigger than y
						if (Math.abs(dragDifX) > dragDeadZoneOpposite) {
							if (dragDifX > 0) {
								//going right
								currentPlayer.setDirection('r');//set right
							}
							if (dragDifX < 0) {
								//going left
								currentPlayer.setDirection('l');
							}
						}
					} else {
						if (dragDifY < dragDeadZoneSame * -1) {
							//going down
							currentPlayer.setDirection('d');
						}
					}

				}//END GOING UP
				if (currentPlayer.getDirection() == 'd') {//GOING DOWN
					if (Math.abs(dragDifX) >= Math.abs(dragDifY)) {//if x diff is bigger than y
						if (Math.abs(dragDifX) > dragDeadZoneOpposite) {
							if (dragDifX > 0) {
								//going right
								currentPlayer.setDirection('r');//set right
							}
							if (dragDifX < 0) {
								//going left
								currentPlayer.setDirection('l');
							}
						}
					} else {
						if (dragDifY > dragDeadZoneSame) {
							//going up
							currentPlayer.setDirection('u');
						}
					}

				}//END GOING DOWN
			}//END IF IN MOVING BOUNDS

		}
*/

	/**
	 * Literally just made this for the touch dragged method.
	 * @param point1
	 * @param point2
     * @return returnst the Math.abs of the distance between the two points.
     */
	public float distForm(Vector2 point1, Vector2 point2){
		return Math.abs(distForm(point1.x, point1.y, point2.x, point2.y));
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(pause)return false;
		if(screenX < Gdx.graphics.getWidth()/2 ) {

			if (dragCoordsLastFrame.x == -1) {//if it's the first check of this drag
				dragCoordsLastFrame.x = dragCoordsThisFrame.x;
				dragCoordsLastFrame.y = dragCoordsThisFrame.y;
				dragCoordsThisFrame.x = screenX;
				dragCoordsThisFrame.y = screenY;//set this frame's
				hudStage.getViewport().unproject(dragCoordsThisFrame);
			} else {
				dragCoordsLastFrame.x = dragCoordsThisFrame.x;
				dragCoordsLastFrame.y = dragCoordsThisFrame.y;
				dragCoordsThisFrame.x = screenX;
				dragCoordsThisFrame.y = screenY;//set this frame's
				hudStage.getViewport().unproject(dragCoordsThisFrame);
				dragDifX = dragCoordsThisFrame.x - stopCoords.x;//difference of x drags.
				dragDifY = dragCoordsThisFrame.y - stopCoords.y;//difference of y drags.
			}
			if(distForm(dragCoordsThisFrame, stopCoords) > 30) {//if out of dead zone
				currentPlayer.setIfMoving(true);//start moving, as we change direction.
				pixsPerSecond = distForm(dragCoordsThisFrame, dragCoordsLastFrame) / Gdx.graphics.getDeltaTime();
				if (pixsPerSecond < stoppingSpeed) {//if the last pixs per second this frame < stopping
					if (stopTimer.update(Gdx.graphics.getDeltaTime())) {
						//if the timer is maxed out. Stopped within the time.
						stopCoords.x = dragCoordsThisFrame.x;
						stopCoords.y = dragCoordsThisFrame.y;
						anchorDirection = currentPlayer.getDirection();

					}//end if timer maxed
				}//end if drag small enough
				else {//bigger than stopping speed
					movePoint = dragCoordsThisFrame;
					stopTimer.reset();
					moveSlope = dragDifY / dragDifX;

					if(anchorDirection == 'd'){
						temp = (float)Math.toDegrees(Math.asin((1/distForm(stopCoords, dragCoordsThisFrame) *
								distForm(stopCoords.x, dragCoordsThisFrame.y, dragCoordsThisFrame.x, dragCoordsThisFrame.y))));
						//gets the angle

						if(temp <= 22.5){
							//then it's to the left. Angle is yeah
							if(dragDifY >= 0)
							currentPlayer.direction = 'u';
						}
						else{
							//left or right
							if(dragCoordsThisFrame.x - stopCoords.x < 0) currentPlayer.setDirection('l');
							else currentPlayer.setDirection('r');
						}
					}
					else if(anchorDirection == 'u'){
						temp = (float)Math.toDegrees(Math.asin((1/distForm(stopCoords, dragCoordsThisFrame) *
								distForm(stopCoords.x, dragCoordsThisFrame.y, dragCoordsThisFrame.x, dragCoordsThisFrame.y))));
						//gets the angle
						if(temp<= 22.5 ){
							if(dragDifY <= 0)
							currentPlayer.setDirection('d');
						}
						else{
							//left or right
							if(dragCoordsThisFrame.x - stopCoords.x < 0) currentPlayer.setDirection('l');
							else currentPlayer.setDirection('r');
						}
					}
					else if(anchorDirection == 'r' || anchorDirection == 'l'){
						temp = (float)Math.toDegrees(Math.asin((1/distForm(stopCoords, dragCoordsThisFrame) *
								distForm(dragCoordsThisFrame.x, stopCoords.y, dragCoordsThisFrame.x, dragCoordsThisFrame.y))));
						//gets the angle
						if(temp <= 22.5) {
							if (dragDifX >= 0)currentPlayer.setDirection('r');
							else if (dragDifX < 0)currentPlayer.setDirection('l');
						}
						else{
							if(dragDifY > 0)currentPlayer.setDirection('u');
							else if(dragDifY <= 0)currentPlayer.setDirection('d');
						}
					}
					System.out.println(currentPlayer.direction + ", " + temp);
					/*
					if (dragDifX <= 0) {//if dragging left

						if (moveSlope > 0.5) currentPlayer.setDirection('d');
							//going down
						else if (moveSlope < 0.5 && moveSlope > -0.5)
							currentPlayer.setDirection('l');
							//going left
						else if (moveSlope < -0.5) currentPlayer.setDirection('u');
						//going right
					}//end if dragging left
					else {//dragging right
						if (moveSlope > 0.5) currentPlayer.setDirection('u');//up
						if (moveSlope < 0.5 && moveSlope > -0.5)
							currentPlayer.setDirection('r');//right
						else if (moveSlope < -0.5) currentPlayer.setDirection('d');//down
					}
					*///TEMP
				}
			}

		}//END IF ON RIGHT SIDE OF SCREEN

		return false;
	}


	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		orthoCam.zoom += 0.1;
		orthoCam.update();
		return false;
	}
	

}

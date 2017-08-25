
package com.burnt_toast.dungeons_n_stuff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.burnt_toast.toast_layout.Label;
import com.burnt_toast.toast_layout.MenuLayout;
import com.burnt_toast.toast_layout.Sheet;
import com.burnt_toast.toast_layout.Sheet.Allignment;
import com.dungeons_n_stuff.dungeon_layout.DungeonButton;

public class MenuScreen implements Screen, InputProcessor{

	private TiledMap menuMap;
	private MainFrame main;
	private Stage menuStage;
	private float widthWithZoom;
	private float heightWithZoom;
	private OrthographicCamera orthoCam;
	
	private MenuLayout mainMenu;//play and options buttons.
	private MenuLayout characterPick;//choose your character
	private MenuLayout upgradeMenu;//choose the upgrades between levels
	private MenuLayout currentLayout;//the layout currently being shown.
	
	private DungeonButton playButton;
	private DungeonButton optionsButton;
	
	//upgrade menu stuff.
	private DungeonButton damageUpg;
	private DungeonButton healthUpg;
	private DungeonButton speedUpg;
	private DungeonButton attackRadUpg;
	private DungeonButton nextLvBut;
	private static int damageMod;
	private static int healthMod;
	private static int speedMod;
	private static int attackRadMod;
	private int upgPointsLeft;
	
	
	//character pick stuff

	private DungeonButton playWithCharacter;
	
	private int animationIndex;
	private float animationTimer;//used to animate
	private float animationTimerMax = 0.5f;//switches ever half second
	
	private float mapWidth;
	private float mapHeight;
	
	private Player currentPlayer;
	

	
	private Vector3 temp;//used to unproject click coords
	
	public MenuScreen(MainFrame passedMain){
		main = passedMain;
		orthoCam = new OrthographicCamera(MainFrame.SCREEN_WIDTH, MainFrame.SCREEN_HEIGHT);
		menuStage = new Stage(new ExtendViewport(MainFrame.SCREEN_WIDTH, MainFrame.SCREEN_HEIGHT, orthoCam));
		menuMap = main.mapLoader.load("maps/menuMap.tmx");
		main.otmr = new OrthogonalTiledMapRenderer(menuMap);
		
		((OrthographicCamera)(menuStage.getCamera())).zoom -= 0;//REMOVE?
		widthWithZoom = menuStage.getWidth() * ((OrthographicCamera)(menuStage.getCamera())).zoom;
		heightWithZoom = menuStage.getHeight() * ((OrthographicCamera)(menuStage.getCamera())).zoom;
		System.out.println("MenuWidth: " + menuStage.getWidth());
		

		
		//CHARACTER STUFF
		currentPlayer = new Warrior();

		//calculate the map width and height
		mapWidth = menuMap.getProperties().get("width" , Integer.class) *
				8;
		mapHeight = menuMap.getProperties().get("height", Integer.class) *
				 8;
		//menu layouts
		mainMenu = new MenuLayout();
		characterPick = new MenuLayout();
		upgradeMenu = new MenuLayout();
		currentLayout = mainMenu;
		
		//buttons
		playButton = new DungeonButton("Play", passedMain);
		playButton.setWindowSize(MainFrame.TILE_SIZE * 20, MainFrame.TILE_SIZE * 4);
		//"0 + " because the tiledmap always is rendered at 0. No other way to render another way.
		playButton.setWindowCoords(0 + 11 * MainFrame.TILE_SIZE - 2,  0 + 8 * MainFrame.TILE_SIZE);
		playButton.setBorderScale(2);
		playButton.setTextColor(Color.WHITE);
		playButton.setTextSize(2);
		mainMenu.addSheet(playButton);
		
//		optionsButton = new DungeonButton("Options", passedMain);
//		optionsButton.setWindowSize(MainFrame.TILE_SIZE * 10, MainFrame.TILE_SIZE * 4);
//		optionsButton.setWindowCoords(0 + 21 * MainFrame.TILE_SIZE + 2, 0 + 8 * MainFrame.TILE_SIZE);
//		optionsButton.setBorderScale(2);
//		optionsButton.setTextColor(Color.WHITE);
//		optionsButton.setTextSize(2);
//		mainMenu.addSheet(optionsButton);
		
//		//upgrade menu stuff.
//		private DungeonButton damageUpg;
//		private DungeonButton healthUpg;
//		private DungeonButton speedUpg;
//		private DungeonButton attackRadUpg;
//		private DungeonButton nextLvBut;
		damageUpg = new DungeonButton("Damage-" + 1, passedMain);
		damageUpg.setWindowSize(menuStage.getWidth() / 2 - 10, menuStage.getHeight()/3 - 10);
		damageUpg.setWindowCoords(5, menuStage.getHeight()/3 + 5);
		damageUpg.setBorderScale(2);
		damageUpg.setTextColor(Color.WHITE);
		damageUpg.setTextSize(2);
		//damageUpg.setButtonText("Damage");
		upgradeMenu.addSheet(damageUpg);
		damageMod = 1;
		
		healthUpg = new DungeonButton("Health-" + 1, passedMain);
		healthUpg.setWindowSize(menuStage.getWidth() / 2 - 10, menuStage.getHeight()/3 - 10);
		healthUpg.setWindowCoords(5, 5);
		healthUpg.setBorderScale(2);
		healthUpg.setTextColor(Color.WHITE);
		healthUpg.setTextSize(2);
		upgradeMenu.addSheet(healthUpg);
		healthMod = 1;
		
		speedUpg = new DungeonButton("Speed-" + 1, passedMain);
		speedUpg.setWindowSize(menuStage.getWidth()/2 - 10, menuStage.getHeight() / 3 - 10);
		speedUpg.setWindowCoords(menuStage.getWidth() / 2 + 5, 0 + 5);
		speedUpg.setBorderScale(2);
		speedUpg.setTextColor(Color.WHITE);
		speedUpg.setTextSize(2);
		upgradeMenu.addSheet(speedUpg);
		speedMod = 1;
		
		attackRadUpg = new DungeonButton("Coming Soon-ish", passedMain);
		attackRadUpg.setWindowSize(menuStage.getWidth() / 2 - 10, menuStage.getHeight()/3 - 10);
		attackRadUpg.setWindowCoords(menuStage.getWidth() / 2 + 5, menuStage.getHeight() / 3 + 5);
		attackRadUpg.setBorderScale(2);
		attackRadUpg.setTextColor(Color.GRAY);
		attackRadUpg.setTextSize(2);
		upgradeMenu.addSheet(attackRadUpg);
		attackRadMod = 1;
		
		nextLvBut = new DungeonButton("Done ->", passedMain);
		nextLvBut.setWindowSize(main.getWidthOf(nextLvBut.getButtonText()) + 10,
				main.getHeightOf(nextLvBut.getButtonText())+10);
		nextLvBut.setWindowCoords(menuStage.getWidth() - nextLvBut.getWidth() - 5, menuStage.getHeight()/3*2);
		upgradeMenu.addSheet(nextLvBut);
		

		
		//UPGRADE MENU SCREEN
		
		
		
		//CHARACTER PICK SCREEN
		animationTimer = 0;
		
		
		
		
		//PLAY button to play with selected character
		playWithCharacter = new DungeonButton("Start", passedMain);
		playWithCharacter.setWindowSize(80, 40);
		//position was at bottom right but I wanted it in the middle instead.
		playWithCharacter.setWindowCoords(widthWithZoom/2 - playWithCharacter.getWidth()/2, 0);
		playWithCharacter.setBorderScale(2);
		playWithCharacter.setTextColor(Color.WHITE);
		playWithCharacter.setTextSize(2);
		characterPick.addSheet(playWithCharacter);
		
		System.out.println(widthWithZoom);
		
		//UPGRADE MENU
		
		//current selection frame
		//meele label
		//ranged label
		//special label
		//meelee descripLabel
		//ranged descripLabel
		//specialDescripLabel
		//playCharButton
		
		temp = new Vector3();

		
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		/*
		if(main.fadeCodename.equals("next level")){
			if(PlayScreen.floorLevel %2 != 0){
				main.setScreen(main.playScreen);
				
				return;
			}
			currentLayout = upgradeMenu;
		}
		else //if(main.fadeCodename.equals("game over")){
		{
			currentLayout = mainMenu;
		}
		*/
		//move camera to place not viewport mmk
		main.fadeIn();
		main.addInputProcessor(this);
		menuStage.getCamera().update();
		widthWithZoom = menuStage.getWidth() * ((OrthographicCamera)(menuStage.getCamera())).zoom;
		heightWithZoom = menuStage.getHeight() * ((OrthographicCamera)(menuStage.getCamera())).zoom;
		if(main.playScreen.floorLevel == 0){
			this.currentLayout = this.mainMenu;
			//got here from start
			System.out.println(widthWithZoom);
			menuStage.getViewport().apply();

			
			System.out.println(((OrthographicCamera)(menuStage.getCamera())).zoom);
			
		}
		else{
			currentLayout = this.upgradeMenu;
			if(PlayScreen.floorLevel == 2){
			}
		}

		menuStage.getViewport().apply();
		
		
		orthoCam.update();
	}
	public void setDamageMod(int pass){
		damageMod = pass;
		damageUpg.setButtonText("Damage-" + damageMod);
	}
	public void setSpeedMod(int pass){
		speedMod = pass;
		speedUpg.setButtonText("Speed-" + speedMod);
	}
	public void setAttackRadMod(int pass){
		attackRadMod = pass;
		//attackRadUpg.setButtonText("Attack Radius-" + attackRadMod);
	}
	public void setHealthMod(int pass){
		healthMod = pass;
		healthUpg.setButtonText("Health-" + healthMod);
	}
	public void addUpgPoint(){
		upgPointsLeft++;
	}

	@Override
	public void render(float delta) {
		//ERASE
		//ha I can make the clear color fade too cool huh
		//except I have to make them ALL FLOATS
		if(currentLayout == mainMenu){
			Gdx.gl.glClearColor(
					55f * (1f/255f) * main.fadeTracker,
					65f * (1f/255f) * main.fadeTracker,
					113f * (1f/255f) * main.fadeTracker, 1
					);
		}
		else{
			Gdx.gl.glClearColor(0, 0, 0, 1);
		}
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// TODO Auto-generated method stub
		//ERIC
		if(currentLayout == characterPick)
		orthoCam.position.set(widthWithZoom / 2, heightWithZoom / 2, 0);
		if(currentLayout == mainMenu)
		orthoCam.position.set(mapWidth / 2 , mapHeight / 2, 0);
	


		
		//REDRAW
		//batch fade
			fadeCode(main.updateFade());
			main.fade(menuStage.getBatch());
			main.fade(main.gameFont);
			main.fade(main.otmr.getBatch());
			main.fade(main.gameFont);
			
		menuStage.act();
		menuStage.draw();
		main.otmr.setView(orthoCam);	
		
		//if it's the main menu show the map. otherwise don't.
		if(currentLayout == mainMenu){
			main.otmr.render();
		}
		
		menuStage.getBatch().begin();
		currentLayout.draw((SpriteBatch) menuStage.getBatch(), main.gameFont);
		if(currentLayout == characterPick)this.actionsPerRenderCharPick();
		currentLayout.update(temp);
		
		if(currentLayout == mainMenu){
			main.gameFont.draw(menuStage.getBatch(), "Dungeons N Stuff",
					playButton.getX(), playButton.getY() + playButton.getHeight() + 20);
					/*
					menuStage.getWidth()/2 - main.getWidthOf("Dungeons N Stuff") / 2 - 12, 
					menuStage.getHeight()/2 - main.getHeightOf("Dungeons N Stuff") * 2 / 2);//20 is a guesstimation.
		*/
		}
		
		if(currentLayout == characterPick){
			main.gameFont.draw(menuStage.getBatch(), "Drag on the \nleft side \nto move,", 5, heightWithZoom/10 * 8);
			main.gameFont.draw(menuStage.getBatch(), "Tap on the \nright side \nto attack.", widthWithZoom/7*5,
					heightWithZoom/10*8);
			/*
			main.gameFont.draw(menuStage.getBatch(), 
					"Find the door to the next floor." + 
					"\nMonsters get harder every floor" + 
					"\nThe maze gets bigger every floor." + 
					"\nYou upgrade every other floor.", 5, 
					heightWithZoom/10 * 5);
					*/
		}
		
		if(currentLayout == upgradeMenu){
			//upgrade menu! 
			main.gameFont.draw(menuStage.getBatch(), "Upgrade Points left: " + upgPointsLeft,
					10, menuStage.getHeight()/3 * 2 + 5);
		}
		menuStage.getBatch().end();
		


		
		//CALCULATE
		temp.x = Gdx.input.getX();
		temp.y = Gdx.input.getY();
		orthoCam.unproject(temp);//unproject the coords getting sent to the current layout
		currentLayout.update(temp);
		orthoCam.update(true);
		menuStage.getViewport().apply();
		
		//character select animation
		if(currentLayout == characterPick){
			animationTimer += Gdx.graphics.getDeltaTime();
			if(animationTimer >= animationTimerMax){
				//timer maxed, animate that boyeeee
				animationIndex = (animationIndex == 0)? 1 : 0;//is the index == 0? true then it's equal to 1, false then it's 0.
				animationTimer = 0;
			}
			
		}
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		menuStage.getViewport().update(width, height, true);
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
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
	
	public void buttonCode(String buttonName){

		System.out.println("BUTTON CODDE: " + buttonName);
		if(buttonName == null)return;
		if(currentLayout == mainMenu){

			if(buttonName == "Play"){
				
				//main.fadeOut = true;
				main.adHandler.loadAds();
				main.setFadeCode("play");
				main.fadeOut();
			}
		}
		if(currentLayout == characterPick){

			if(buttonName == "Start"){//start with character
				main.playScreen.setCharacter('r');
				//main.fadeOut = true;
				System.out.println("fade code is start");
				
				main.setFadeCode("Start");
				main.fadeOut();

			}
		}
		if(currentLayout == upgradeMenu){
			if(upgPointsLeft > 0){
				if(buttonName.contains("Damage")){
					PlayScreen.getCurrentPlayer().setDamage();
					upgPointsLeft--;
					setDamageMod(++damageMod);
				}
				else if(buttonName.contains("Speed")){
					upgPointsLeft--;
					PlayScreen.getCurrentPlayer().setMovementSpeed();
					setDamageMod(++speedMod);
				}
				else if(buttonName.contains("Attack")){//attack radius
					//upgPointsLeft--;
					//set attack radius on player
					//setAttackRadMod(++attackRadMod);
				}
				else if(buttonName.contains("Health")){
					upgPointsLeft--;
					PlayScreen.getCurrentPlayer().setMaxHealth();
					PlayScreen.getCurrentPlayer().setHealth(PlayScreen.getCurrentPlayer().getMaxHealth());
					setHealthMod(++healthMod);
				}
			}
			if(buttonName.contains("Done")){
				main.setFadeCode("Done");
				main.fadeOut();
			}
		}
	}
	public void fadeCode(String fadeCodename){
		//if(fadeCodename != "null")System.out.println("Fade Codename: " + fadeCodename);
		if(currentLayout == mainMenu){

			if(fadeCodename == "play"){
				currentLayout = characterPick;
				main.fadeIn();
			}

		}
		if(currentLayout == characterPick){
			if(fadeCodename == "Start"){
				//play with character
				
				main.setScreen(main.playScreen);
				main.fadeIn();
				//main.fadeOut = false;
			}
		}
		if(fadeCodename == "Done"){
			main.setScreen(main.playScreen);
			main.fadeIn();
		}
	}
	public void actionsPerRenderCharPick(){
		menuStage.getBatch().draw(MainFrame.warriorFrames[animationIndex],
				widthWithZoom / 2 - MainFrame.warriorFrames[0].getRegionWidth()/2 - 10,
				heightWithZoom / 10 * 8,
				MainFrame.warriorFrames[0].getRegionWidth()*2, MainFrame.wizardFrames[0].getRegionHeight()*2);
	}
	
	public static float getHealthMod(){return healthMod;}
	public static float getDamageMod(){return damageMod;}
	public static float getAttackRadMod(){return attackRadMod;}
	public static float getSpeedMod(){return speedMod;}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		
		if(keycode == Keys.UP){
			this.addUpgPoint();
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(main.fadeIn || main.fadeOut)return false;
		
		
		temp.x = screenX;
		temp.y = screenY;
		orthoCam.unproject(temp);
		buttonCode(currentLayout.checkButtonsClickString(temp));
		
		//if x is less than half screen, then move character
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {

		
		
		
		
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
		return false;
	}

}

package com.burnt_toast.dungeons_n_stuff;

import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class SplashScreen implements Screen{

	MainFrame main;
	private Stage splashStage;
	private TextureRegion toastImg;
	private float imgScale = 10;
	
	private int loadIndex;
	
	private float loadTimerMax = 2.5f;//when this is reached, it shows loading on screen
	private float loadTimer = 0;
	
	
	public SplashScreen(MainFrame passMain){
		main = passMain;
		main.assets.load("textures/mainTileset.png", Texture.class);
		main.assets.load("textures/spriteSheet.png", Texture.class);
		
		main.generator = new FreeTypeFontGenerator(Gdx.files.internal("8bitOperator.ttf"));
		main.parameter = new FreeTypeFontParameter();
		main.parameter.size = 12;
		main.gameFont = main.generator.generateFont(main.parameter);//generate font manually
		
		//load code stuff. this doesn't actually run right here, it just que's it for loading later
		loadIndex = 4;//max it out and go down to -1.
		assignLoaded();//assings loaded assets. And finished loading assets.

	}
	
	/**
	 * finish loading everything and assign the variables to the
	 * loaded assets
	 */
	public void assignLoaded(){
		main.assets.finishLoading();
		
		main.mainTileset = main.assets.get("textures/mainTileset.png", Texture.class);
		main.characterTexture = main.assets.get("textures/spriteSheet.png", Texture.class);
		toastImg = new TextureRegion(main.mainTileset, 120, 16, 8, 8);
		splashStage = new Stage(new ExtendViewport(MainFrame.SCREEN_WIDTH, MainFrame.SCREEN_HEIGHT));
		
		main.glyphLayout = new GlyphLayout();

		
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		//main.fadeIn = true;
		//main.fadeOut = false;
		main.fadeIn();
		//about to show screen set to fade in
		main.gameFont.getData().scale(0.5f);
	}

	@Override
	public void render(float delta) {
		//ERASE
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// TODO Auto-generated method stub
		//ERIC
		
		
		//REDRAW
			
			if(main.updateFade() == "toMenu"){
				main.setScreen(main.menuScreen);
			}
			main.fade(splashStage.getBatch());
			main.fade(main.gameFont);
		
		splashStage.act();
		splashStage.draw();
		
		splashStage.getBatch().begin();
		splashStage.getBatch().draw(toastImg, splashStage.getWidth()/2 - (toastImg.getRegionWidth()*imgScale/2),
				(splashStage.getHeight()/2 - (toastImg.getRegionHeight()*imgScale / 2) /*+100*/),
				toastImg.getRegionWidth() * imgScale, (toastImg.getRegionHeight() * imgScale));
		main.gameFont.draw(splashStage.getBatch(), "Burnt_Toast", (splashStage.getWidth()/2) - (main.getWidthOf("Burnt_Toast") / 2), 
				(splashStage.getHeight()/2) - (main.getHeightOf("Burnt_Toast") / 2) - toastImg.getRegionHeight()*imgScale/2);
	
		if(loadTimer >= loadTimerMax && main.assets.getQueuedAssets() != 0){//if it's taking too long tell them it's loading.
			main.gameFont.draw(splashStage.getBatch(), "Loading...", splashStage.getWidth() / 2 - main.getWidthOf("Loading..")/2,
					splashStage.getHeight()/2 - main.getHeightOf("Loading...")/2 - 100);
			//main.assets.finishLoading();
		}
		
		
		//LOADING TIMER
		//okay so if I do it like this it never works so sorry.
		
		if(loadTimer < loadTimerMax){
			loadTimer+= Gdx.graphics.getDeltaTime();
		}
		else{
			if(main.assets.getQueuedAssets() == 0 && loadIndex < 0){
				//we're done!
				//main.fadeOut = true;
				main.setFadeCode("toMenu");
				if(loadIndex == -1){
				main.fadeOut(); loadIndex = -2;
				}
				if(main.fadeTracker == 0)main.setScreen(main.menuScreen);
			}
		}
		
		splashStage.getBatch().end();
		
		//INPUT

		//CALCULATE
		if(loadIndex > -1){
			loadSomeCode();
			loadIndex--;
		}
		main.assets.update();
		//checks if last asset loaded
		
	}
	public void loadSomeCode(){
		switch (loadIndex){
		case 4://THIS ONE HAPPENS FIRST AND GOES DOWN
			main.mapLoader = new TmxMapLoader();
			main.assets.load("textures/spriteSheet.png", Texture.class);
			break;
		case 3:
			main.archerFrames = new TextureRegion[5];
			main.archerFrames[0] = new TextureRegion(main.characterTexture, 0, 10, 9, 10);//person
			main.archerFrames[1] = new TextureRegion(main.characterTexture, 9, 10, 8, 9);//person step attack
			main.archerFrames[2] = new TextureRegion(main.mainTileset, 33, 40, 6, 9);//meelee
			main.archerFrames[3] = new TextureRegion(main.characterTexture, 25, 12, 3, 5);//blue normal arrow
			main.archerFrames[4] = new TextureRegion(main.characterTexture, 28, 12, 3, 5);//green poison arrow
			
			main.arrow = new TextureRegion(main.mainTileset, 30, 49, 3, 5);
			main.arrowBox = new TextureRegion(main.mainTileset, 0, 70, 9, 9);
			main.swordStages = new TextureRegion[3];
			main.swordStages[1] = new TextureRegion(main.mainTileset, 10, 49, 10, 10);
			main.swordStages[2] = new TextureRegion(main.mainTileset, 20, 49, 10, 10);
			
			//ALL DEM SLIME FRAMES
			//main.slimeFrames[0] = new TextureRegion(main.characterTexture)
			break;
		case 2:
			main.wizardFrames = new TextureRegion[5];
			main.wizardFrames[0] = new TextureRegion(main.characterTexture, 0, 20, 8, 9);//person
			main.wizardFrames[1] = new TextureRegion(main.characterTexture, 8, 20, 8, 9);//person step & attack
			main.wizardFrames[2] = new TextureRegion(main.characterTexture, 17, 20, 8, 8);//melee

			//TILES:
			main.mapTiles = new TextureRegion[18];
			//wall
			main.mapTiles[1] = new TextureRegion(main.mainTileset, 40, 0, 8, 8);
			//floor
			main.mapTiles[0] = new TextureRegion(main.mainTileset, 24, 8, 8, 8);//floor
			//moar tiles.
			main.mapTiles[2] = new TextureRegion(main.mainTileset, 16, 0, 8, 8);//top left
			main.mapTiles[3] = new TextureRegion(main.mainTileset, 24, 0, 8, 8);//top
			main.mapTiles[4] = new TextureRegion(main.mainTileset, 32, 0, 8, 8);//top right
			main.mapTiles[5] = new TextureRegion(main.mainTileset, 16, 8, 8, 8);//left
			main.mapTiles[6] = new TextureRegion(main.mainTileset, 32, 8, 8, 8);//right
			main.mapTiles[7] = new TextureRegion(main.mainTileset, 16, 16, 8, 8);//bottom left
			main.mapTiles[8] = new TextureRegion(main.mainTileset, 24, 16, 8, 8);//bottom
			main.mapTiles[9] = new TextureRegion(main.mainTileset, 32, 16, 8, 8);//bottom right
			main.mapTiles[10] = new TextureRegion(main.mainTileset, 40, 16, 8, 8);//left brick
			main.mapTiles[11] = new TextureRegion(main.mainTileset, 40, 8, 8, 8);//middle brick
			main.mapTiles[12] = new TextureRegion(main.mainTileset, 40, 24, 8, 8);//right brick
			main.mapTiles[13] = new TextureRegion(main.mainTileset, 0, 0, 8, 8);//top left corner
			main.mapTiles[14] = new TextureRegion(main.mainTileset, 8, 0, 8, 8);//top right corner
			main.mapTiles[15] = new TextureRegion(main.mainTileset, 0, 8, 8, 8);//bottom left corner
			main.mapTiles[16] = new TextureRegion(main.mainTileset, 8, 8, 8, 8);//bottom right corner
			main.mapTiles[17] = new TextureRegion(main.mainTileset, 48, 0, 8, 8);//door tile

			break;
		case 1:
			main.warriorFrames = new TextureRegion[5];
			main.warriorFrames[0] = new TextureRegion(main.characterTexture, 0, 0, 8, 9);//person 
			main.warriorFrames[1] = new TextureRegion(main.characterTexture, 8, 0, 9, 9);//person step and attack
			main.warriorFrames[2] = new TextureRegion(main.mainTileset, 26, 40, 5, 9);//meelee.
			main.slimeFrames = new TextureRegion[3];
			main.slimeFrames[0] = new TextureRegion(main.characterTexture, 0, 30, 8, 4);
			main.slimeFrames[1] = new TextureRegion(main.characterTexture, 8, 29, 8, 5);
			main.slimeFrames[2] = new TextureRegion(main.mainTileset, 33, 40, 5, 7);//melee thing.
			break;
		case 0:
			MainFrame.doorFrames = new TextureRegion[4];
			main.doorFrames[0] = new TextureRegion(main.mainTileset, 96, 0, 8, 8);
			main.doorFrames[1] = new TextureRegion(main.mainTileset, 104, 0, 8, 8);
			main.doorFrames[2] = new TextureRegion(main.mainTileset, 112, 0, 8, 8);
			main.doorFrames[3] = new TextureRegion(main.mainTileset, 120, 0, 8, 8);
			main.buttonFrames = new TextureRegion[2];
			main.buttonFrames[0] = new TextureRegion(MainFrame.mainTileset, 96, 16, 8, 8);
			main.buttonFrames[1] = new TextureRegion(MainFrame.mainTileset, 96, 24, 8, 8);
			main.key = new TextureRegion(main.mainTileset, 0, 40, 3, 6);

			main.silverFrame = new TextureRegion(main.mainTileset, 112, 24, 8, 8);
			Gdx.input.setInputProcessor(main.getInputMultiplexer());
			main.menuScreen = new MenuScreen(main);
			main.playScreen = new PlayScreen(main);
			break;
		}
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		splashStage.getViewport().update(width, height);
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
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		splashStage.dispose();
		
	}

}

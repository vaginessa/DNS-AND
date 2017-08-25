package com.burnt_toast.dungeons_n_stuff;



import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class MainFrame extends Game {
	public SpriteBatch batch;
	public AssetManager assets;
	
	private InputMultiplexer inputMulti;
	
	public static Texture mainTileset;
	public Texture characterTexture;
	
	public PlayScreen playScreen;
	public MenuScreen menuScreen;
	public SplashScreen splashScreen;
	
	public GlyphLayout glyphLayout;
	public BitmapFont gameFont;
	public FreeTypeFontGenerator generator;
	public FreeTypeFontParameter parameter;//used for loading fonts
	
	//Texture Regions
	public static TextureRegion silverFrame;
	
	public static TextureRegion[] archerFrames;//0 and 1 are person and 2 is meelee and 3 is arrow
	public static TextureRegion[] wizardFrames;//0 and 1 are person and 2 is meelee 
	public static TextureRegion[] warriorFrames;//0 and 1 are person and 2 is meelee and 3 is throwing sword.

	//MAP STUFF
	public static TextureRegion[] mapTiles; //a general dictionary of just the tiles that are used.
	//initilized in the splash screen.
	
	//teh monstas YEAH
	public static TextureRegion[] slimeFrames;//0 and 1 are person and 2 is meelee and 3 is ranged
	
	public static TextureRegion[] swordStages;//0 1 and 2 are the 3 levels
	public static TextureRegion arrow;
    public static TextureRegion arrowBox;
	
	public static TextureRegion[] doorFrames;
	public static TextureRegion[] buttonFrames;
    public static TextureRegion key;
	
	public float fadeTracker;//to track the fade function
	public float fadeFactor = 2;
	public boolean fadeIn, fadeOut;
	private String fadeCodename;//since I can't use lambda's I have to return a codename of who called me
	
	public static final float SCREEN_WIDTH = 12 * 30;//480
	public static final float SCREEN_HEIGHT = 6 * 30;
	
	public static final String VERSION = "1.2";
	
	public static final float TILE_SIZE = 8;//8 * 2
	
	TmxMapLoader mapLoader;
	public OrthogonalTiledMapRenderer otmr;

	//FOR ADS
	IActivityRequestHandler adHandler;

	public MainFrame(IActivityRequestHandler handler){
		this();
		adHandler = handler;
	}


	public MainFrame(){
		inputMulti = new InputMultiplexer();
		fadeCodename = "none";
	}
	
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		assets = new AssetManager();
		splashScreen = new SplashScreen(this);
		this.setScreen(splashScreen);
	}

	@Override
	public void render () {
		super.render();
	}
	@Override
	public void dispose(){
		assets.dispose();
		batch.dispose();
	}
	public void setFadeCode(String code){
		fadeCodename = code;
	}
	public String updateFade(){
		if(!fadeIn && !fadeOut)return "false";
		if(fadeIn){//fading in
			if(fadeTracker < 1){
				fadeTracker += (fadeFactor * Gdx.graphics.getDeltaTime());
				if(fadeTracker > 1){
					fadeTracker = 1;
					fadeIn = false;
					return fadeCodename;//finished now at 1
				}
				return "null";//not finished
			}
			return fadeCodename;//finished if not less than 0
		}
		if(fadeOut){//fading out
			if(fadeTracker > 0){
				fadeTracker -= (fadeFactor * Gdx.graphics.getDeltaTime());
				if(fadeTracker < 0){
					fadeTracker = 0;
					fadeOut = false;
					return fadeCodename;//finished, now at 0
				}
				return "null";//not finished
			}
			return fadeCodename;//finished if not larger than 0
		}
		return "null";//returns if neither fadeIn or fadeOut were true;
	}
	public <T> void fade(T t){
		if(!fadeIn && !fadeOut)return;
		if(t != null){
			if(t.getClass() == Music.class){
				//
				((Music)t).setVolume(fadeTracker);
				System.out.println(fadeTracker);
			}
			if(t.getClass() == SpriteBatch.class){
				((SpriteBatch)t).setColor(fadeTracker, fadeTracker, fadeTracker, 1);
			}
			if(t.getClass() == BitmapFont.class){
				((BitmapFont)t).setColor(((BitmapFont)t).getColor().r, ((BitmapFont)t).getColor().g,
						((BitmapFont)t).getColor().b, fadeTracker);
			}
		}
	}
	public void fadeIn(){
		fadeIn = true;
	}
	public void fadeOut(){
		System.out.println("That thing was hit");
		if(this.getScreen() == playScreen){
			assert false;
		}
		fadeOut = true;
	}
	public float getHeightOf(String str){
		glyphLayout.setText(gameFont, str);
		return glyphLayout.height;
	}
	public float getWidthOf(String str){
		glyphLayout.setText(gameFont, str);
		return glyphLayout.width;
	}
	/**
	 * adds passed processor to the input multiplexer found in MainFrame
	 * @param passProcessor
	 */
	public void addInputProcessor(InputProcessor passProcessor){
		inputMulti.addProcessor(passProcessor);
	}
	/**
	 * removes the passed processor from the Input Multiplexer found in MainFrame
	 * @param passProcessor
	 */
	public void removeInputProcessor(InputProcessor passProcessor){
		inputMulti.removeProcessor(passProcessor);
	}
	/**
	 * returns the input multiplexer for setting in the splash screen.
	 * @return
	 */
	public InputMultiplexer getInputMultiplexer(){
		return inputMulti;
	}
}

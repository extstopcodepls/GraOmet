package com.game.resourcemanager;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import android.graphics.Color;

import com.game.omet.GameActivity;

/**
 * @author Mateusz Mysliwiec
 * @author www.matim-dev.com
 * @version 1.0
 */
public class ResourcesManager
{
	// ---------------------------------------------
	// VARIABLES
	// ---------------------------------------------
	public Font								font;
	private boolean firstRun = true;

	private static final ResourcesManager	INSTANCE	= new ResourcesManager();

	public Engine							engine;
	public GameActivity						activity;
	public BoundCamera						camera;
	public VertexBufferObjectManager		vbom;

	// ---------------------------------------------
	// TEXTURES & TEXTURE REGIONS
	// ---------------------------------------------
	public ITextureRegion					splash_region;
	private BitmapTextureAtlas				splashTextureAtlas;

	public ITextureRegion					menu_background_region;
	public ITextureRegion					play_region;
	public ITextureRegion					options_region;

	private BuildableBitmapTextureAtlas		menuTextureAtlas;

	// Game Texture
	public BuildableBitmapTextureAtlas		gameTextureAtlas;

	// Game Texture Regions
	public ITextureRegion					platform1_region;
	public ITextureRegion					platform2_region;
	public ITextureRegion					platform3_region;
	public ITextureRegion					coin_region;

	// Player Regions

	public ITiledTextureRegion				player_region;

	// Level Complete

	public ITextureRegion					complete_window_region;
	public ITiledTextureRegion				complete_stars_region;

	// Buttons
	public ITextureRegion					left_button;
	public ITextureRegion					right_button;

	public ITextureRegion					jump_button;

	public ITextureRegion					back_button;

	public ITextureRegion					tlo1;

	public ITextureRegion					tlo2;
	
	public ITextureRegion	tlo3;

	private BuildableBitmapTextureAtlas	endTextureAtlas;

	private BuildableBitmapTextureAtlas	backgroundTextureAtlas;

	public Music	jumpMusic;

	public Music	themeMusic;

	public Music	dieMusic;
	public ITextureRegion	tlo4;

	// ---------------------------------------------
	// CLASS LOGIC
	// ---------------------------------------------

	public void loadMenuResources()
	{
		loadMenuGraphics();
		loadMenuAudio();
		loadMenuFonts();
	}

	public void loadGameResources()
	{
		loadGameGraphics();
		loadGameFonts();
		loadGameAudio();
	}

	private void loadMenuGraphics()
	{
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_background.png");
		play_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "play.png");
		options_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "options.png");

		try
		{
			this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.menuTextureAtlas.load();
		}
		catch (final TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}
	}

	private void loadMenuAudio()
	{

	}

	private void loadGameGraphics()
	{
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
		gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		backgroundTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 8192, 8192, TextureOptions.BILINEAR);

		platform1_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "platform1.png");
		platform2_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "platform2.png");
		platform3_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "platform3.png");
		coin_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "coin.png");
		player_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "player.png", 3, 4);
		complete_window_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "levelCompleteWindow.png");
		complete_stars_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "star.png", 2, 1);
		left_button = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "leftb.png");
		right_button = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "rightb.png");
		jump_button = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "jump.png");
		tlo1 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(backgroundTextureAtlas, activity, "tlo1.jpg");
		tlo2 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(backgroundTextureAtlas, activity, "tlo2.jpg");
		tlo3 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(backgroundTextureAtlas, activity, "tlo3.jpg");

		try
		{
			this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameTextureAtlas.load();
			
			this.backgroundTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0,1,0));
			this.backgroundTextureAtlas.load();
			
		}
		catch (final TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}
	}
	
	public void loadEndTextures()
	{
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
		endTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		
		back_button = BitmapTextureAtlasTextureRegionFactory.createFromAsset(endTextureAtlas, activity, "leftb.png");
		
		try
		{
			this.endTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.endTextureAtlas.load();
		}
		catch (final TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}
		
	}
	
	public void unloadEndTextures()
	{
		endTextureAtlas.unload();
	}

	private void loadGameFonts()
	{

	}

	private void loadMenuFonts()
	{
		FontFactory.setAssetBasePath("fonts/");
		final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "font.ttf", 50, true, Color.WHITE, 2, Color.BLACK);
		font.load();
	}

	private void loadGameAudio()
	{
		try
		{
			jumpMusic = MusicFactory.createMusicFromAsset(activity.getMusicManager(), activity, "music/game/jump.mp3");
			themeMusic = MusicFactory.createMusicFromAsset(activity.getMusicManager(), activity, "music/game/musictheme.ogg");
			dieMusic = MusicFactory.createMusicFromAsset(activity.getMusicManager(), activity, "music/game/jump.mp3");
		}
		catch (IOException e)
		{
			
			e.printStackTrace();
		}
	}

	public void loadSplashScreen()
	{
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 500, 375, TextureOptions.BILINEAR);
		splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "splash.png", 0, 0);
		splashTextureAtlas.load();
	}

	public void unloadSplashScreen()
	{
		splashTextureAtlas.unload();
		splash_region = null;
	}

	public void unloadMenuTextures()
	{
		menuTextureAtlas.unload();
	}

	public void loadMenuTextures()
	{
		menuTextureAtlas.load();
	}

	public void unloadGameTextures()
	{
		gameTextureAtlas.unload();
		backgroundTextureAtlas.unload();
	}

	/**
	 * @param engine
	 * @param activity
	 * @param camera
	 * @param vbom
	 * <br>
	 * <br>
	 *            We use this method at beginning of game loading, to prepare
	 *            Resources Manager properly, setting all needed parameters, so
	 *            we can latter access them from different classes (eg. scenes)
	 */
	public static void prepareManager(Engine engine, GameActivity activity, BoundCamera camera, VertexBufferObjectManager vbom)
	{
		getInstance().engine = engine;
		getInstance().activity = activity;
		getInstance().camera = camera;
		getInstance().vbom = vbom;
	}

	// ---------------------------------------------
	// GETTERS AND SETTERS
	// ---------------------------------------------

	public static ResourcesManager getInstance()
	{
		return INSTANCE;
	}

	
}

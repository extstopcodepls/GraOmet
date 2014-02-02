package com.game.base;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

import com.game.resourcemanager.ResourcesManager;
import com.game.scene.EndScene;
import com.game.scene.GameScene;
import com.game.scene.LoadingScene;
import com.game.scene.MainMenuScene;
import com.game.scene.SplashScene;

public class SceneManager
{
	// ---------------------------------------------
	// SCENES
	// ---------------------------------------------

	private BaseScene					splashScene;
	private BaseScene					menuScene;
	private BaseScene					gameScene;
	private BaseScene					loadingScene;
	private BaseScene					endScene;

	// ---------------------------------------------
	// VARIABLES
	// ---------------------------------------------

	private static final SceneManager	INSTANCE			= new SceneManager();

	private SceneType					currentSceneType	= SceneType.SCENE_SPLASH;

	private BaseScene					currentScene;

	private Engine						engine				= ResourcesManager.getInstance().engine;

	public enum SceneType
	{
		SCENE_SPLASH, SCENE_MENU, SCENE_GAME, SCENE_LOADING, SCENE_END,
	}

	// ---------------------------------------------
	// CLASS LOGIC
	// ---------------------------------------------

	public void setScene(BaseScene scene)
	{
		engine.setScene(scene);
		currentScene = scene;
		currentSceneType = scene.getSceneType();
	}

	public void setScene(SceneType sceneType)
	{
		switch (sceneType)
		{
		case SCENE_MENU:
			setScene(menuScene);
			break;
		case SCENE_GAME:
			setScene(gameScene);
			break;
		case SCENE_SPLASH:
			setScene(splashScene);
			break;
		case SCENE_LOADING:
			setScene(loadingScene);
			break;
		case SCENE_END:
			setScene(endScene);
			break;
		default:
			break;
		}
	}

	public void createSplashScene(OnCreateSceneCallback pOnCreateSceneCallback)
	{
		ResourcesManager.getInstance().loadSplashScreen();
		splashScene = new SplashScene();
		currentScene = splashScene;
		pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
	}

	private void disposeSplashScene()
	{
		ResourcesManager.getInstance().unloadSplashScreen();
		splashScene.disposeScene();
		splashScene = null;
	}

	public void createMenuScene()
	{
		ResourcesManager.getInstance().loadMenuResources();
		menuScene = new MainMenuScene();
		loadingScene = new LoadingScene();
		SceneManager.getInstance().setScene(menuScene);
		disposeSplashScene();
	}

	public void loadGameScene(final Engine mEngine)
	{
		if(gameScene != null && !gameScene.isDisposed())
		{
			gameScene.disposeScene();
			gameScene = null;
			System.gc();
			
		}
		setScene(loadingScene);
		ResourcesManager.getInstance().unloadMenuTextures();
		mEngine.registerUpdateHandler(new TimerHandler(1f, new ITimerCallback()
		{
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				mEngine.unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.getInstance().loadGameResources();
				gameScene = new GameScene();
				setScene(gameScene);
			}
		}));
	}

	public void loadMenuScene(final Engine mEngine)
	{
		
		if (gameScene != null && !gameScene.isDisposed())
		{
			gameScene.disposeScene();
			gameScene = null;
			ResourcesManager.getInstance().unloadGameTextures();
		}
		if (endScene != null && !endScene.isDisposed())
		{
			endScene.disposeScene();
			endScene = null;
			ResourcesManager.getInstance().unloadEndTextures();
		}
		
		
		setScene(loadingScene);
		
		
		mEngine.registerUpdateHandler(new TimerHandler(0.5f, new ITimerCallback()
		{
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				mEngine.unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.getInstance().loadMenuTextures();
				setScene(menuScene);
			}
		}));
	}
	
	public void loadEndScene(final Engine mEngine)
	{
		if (!gameScene.isDisposed())
		{
			gameScene.disposeScene();
			gameScene = null;
		}
		ResourcesManager.getInstance().unloadGameTextures();
		mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback()
		{
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				mEngine.unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.getInstance().loadEndTextures();
				endScene = new EndScene();
				setScene(endScene);
			}
		}));
		
	}

	// ---------------------------------------------
	// GETTERS AND SETTERS
	// ---------------------------------------------

	public static SceneManager getInstance()
	{
		return INSTANCE;
	}

	public SceneType getCurrentSceneType()
	{
		return currentSceneType;
	}

	public BaseScene getCurrentScene()
	{
		return currentScene;
	}
}
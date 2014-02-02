package com.game.scene;

import org.andengine.audio.music.Music;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;

import android.view.MotionEvent;

import com.game.base.BaseScene;
import com.game.base.SceneManager;
import com.game.base.SceneManager.SceneType;
import com.game.extras.LevelScoreCollector;
import com.game.resourcemanager.ResourcesManager;

public class EndScene extends BaseScene
{
	private Text	allScoreText;
	private HUD		backHUD;
	private Sprite	backButton;
	private Text	endGameText;
	private Music themeMusic;

	@Override
	public void createScene()
	{
		createBackgound();
		createScoreText();
		createEndGameText();
		createHUD();
		createMusic();

	}

	private void createMusic()
	{
		themeMusic = resourcesManager.themeMusic;
		
	}

	private void createEndGameText()
	{
		final TextOptions textOptions = new TextOptions(HorizontalAlign.CENTER);
		endGameText = new Text(camera.getCenterX(), camera.getCenterY() + 50, resourcesManager.font, "Koniec Gry!", textOptions, resourcesManager.vbom);
	}

	private void createScoreText()
	{
		final TextOptions textOptions = new TextOptions(HorizontalAlign.CENTER);
		allScoreText = new Text(0, 0, resourcesManager.font, "Zdobyles az " + LevelScoreCollector.getAllScore() + " punktów! Gratuluje!", textOptions, resourcesManager.vbom);
	}

	private void createHUD()
	{
		backHUD = new HUD();

		backButton = new Sprite(camera.getCenterX() - 16, camera.getCenterY() - 100, resourcesManager.back_button, resourcesManager.vbom)
		{
			public boolean onAreaTouched(TouchEvent touchEvent, float x, float y)
			{

				super.onAreaTouched(touchEvent, x, y);

				if (touchEvent.getAction() == MotionEvent.ACTION_DOWN)
				{

					onBackKeyPressed();

				}

				return true;
			}

		};
		//backButton.setScale(3f);

		backHUD.attachChild(backButton);
		backHUD.attachChild(allScoreText);
		backHUD.attachChild(endGameText);

		backHUD.registerTouchArea(backButton);

		camera.setHUD(backHUD);
		backHUD.centerEntityInCamera(allScoreText);

	}

	private void createBackgound()
	{
		setBackground(new Background(new Color(Color.GREEN)));
	}

	@Override
	public void onBackKeyPressed()
	{
		GameScene.levelNumber = 1;
		LevelScoreCollector.allScore = 0;
		resourcesManager.themeMusic.stop();
		SceneManager.getInstance().loadMenuScene(engine);
	}

	@Override
	public SceneType getSceneType()
	{
		return SceneType.SCENE_END;
	}

	@Override
	public void disposeScene()
	{
		camera.setHUD(null);
		
		allScoreText.detachSelf();
		allScoreText.dispose();

		backHUD.detachSelf();
		backHUD.dispose();
		
		endGameText.detachSelf();
		endGameText.dispose();

		this.detachSelf();
		this.dispose();

	}

}

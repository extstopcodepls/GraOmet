package com.game.scene;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.IBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.SAXUtils;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;
import org.andengine.util.level.EntityLoader;
import org.andengine.util.level.constants.LevelConstants;
import org.andengine.util.level.simple.SimpleLevelEntityLoaderData;
import org.andengine.util.level.simple.SimpleLevelLoader;
import org.xml.sax.Attributes;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.game.base.BaseScene;
import com.game.base.SceneManager;
import com.game.base.SceneManager.SceneType;
import com.game.extras.LevelCompleteWindow;
import com.game.extras.LevelScoreCollector;
import com.game.extras.LevelCompleteWindow.StarsCount;
import com.game.object.Player;
import com.game.resourcemanager.ResourcesManager;

public class GameScene extends BaseScene {
	
	private HUD gameHUD;
	private Text scoreText;
	
	private Sprite background1;
	private Sprite background2;
	private Sprite background3;
	
	// Music
	
	private Music themeMusic;
	private Music jumpMusic;
	
	
	
	// Level Loader
	
	private static final String TAG_ENTITY = "entity";
	private static final String TAG_ENTITY_ATTRIBUTE_X = "x";
	private static final String TAG_ENTITY_ATTRIBUTE_Y = "y";
	private static final String TAG_ENTITY_ATTRIBUTE_TYPE = "type";
	    
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM1 = "platform1";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM2 = "platform2";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM3 = "platform3";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_COIN = "coin";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_LEVEL_COMPLETE = "levelComplete";
	
	// player
	
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER = "player";
    
	private Player player;
	
	// Level number
	public static int levelNumber = 1;
	
	// game over
	private Text gameOverText;
	
	// score in int
	private int score;
	
	private boolean gameFinished = false;
	// -----------------
	
	// Level Complete
	private LevelCompleteWindow levelCompleteWindow;
	
	// Contact listener
	
	private ContactListener contactListener()
	{
	    ContactListener contactListener = new ContactListener()
	    {
	        public void beginContact(Contact contact)
	        {
	            final Fixture x1 = contact.getFixtureA();
	            final Fixture x2 = contact.getFixtureB();

	            if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null)
	            {
	                if (x2.getBody().getUserData().equals("player"))
	                {
	                    player.increaseFootContacts();
	                }
	            }
	            
	            if (x1.getBody().getUserData().equals("platform3") && x2.getBody().getUserData().equals("player"))
	            {
	                x1.getBody().setType(BodyType.DynamicBody);
	            }
	            
	            if (x1.getBody().getUserData().equals("platform2") && x2.getBody().getUserData().equals("player"))
	            {
	                engine.registerUpdateHandler(new TimerHandler(0.2f, new ITimerCallback()
	                {                                    
	                    public void onTimePassed(final TimerHandler pTimerHandler)
	                    {
	                        pTimerHandler.reset();
	                        engine.unregisterUpdateHandler(pTimerHandler);
	                        x1.getBody().setType(BodyType.DynamicBody);
	                    }
	                }));
	            }
	        }

	        public void endContact(Contact contact)
	        {
	            final Fixture x1 = contact.getFixtureA();
	            final Fixture x2 = contact.getFixtureB();

	            if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null)
	            {
	                if (x2.getBody().getUserData().equals("player"))
	                {
	                    player.decreaseFootContacts();
	                }
	            }
	            
	            
	        }

	        public void preSolve(Contact contact, Manifold oldManifold)
	        {

	        }

	        public void postSolve(Contact contact, ContactImpulse impulse)
	        {

	        }
	    };
	    return contactListener;
	}
	
	
	// -------------------------------

	private void loadLevel(int levelID)
	{
	    final SimpleLevelLoader levelLoader = new SimpleLevelLoader(vbom);
	    
	    final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
	    
	    levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(LevelConstants.TAG_LEVEL)
	    {
	        public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException 
	        {
	            final int width = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_WIDTH);
	            final int height = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_HEIGHT);
	            
	            camera.setBounds(0, 0, width, height); // here we set camera bounds
	            camera.setBoundsEnabled(true);

	            return GameScene.this;
	        }
	    });
	    
	    levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(TAG_ENTITY)
	    {
	        public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException
	        {
	            final int x = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_X);
	            final int y = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_Y);
	            final String type = SAXUtils.getAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_TYPE);
	            
	            final Sprite levelObject;
	            
	            if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM1))
	            {
	                levelObject = new Sprite(x, y, resourcesManager.platform1_region, vbom);
	                PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyType.StaticBody, FIXTURE_DEF).setUserData("platform1");
	            } 
	            else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM2))
	            {
	                levelObject = new Sprite(x, y, resourcesManager.platform2_region, vbom);
	                final Body body = PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyType.StaticBody, FIXTURE_DEF);
	                body.setUserData("platform2");
	                physicsWorld.registerPhysicsConnector(new PhysicsConnector(levelObject, body, true, false));
	            }
	            else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM3))
	            {
	                levelObject = new Sprite(x, y, resourcesManager.platform3_region, vbom);
	                final Body body = PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyType.StaticBody, FIXTURE_DEF);
	                body.setUserData("platform3");
	                physicsWorld.registerPhysicsConnector(new PhysicsConnector(levelObject, body, true, false));
	            }
	            else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_COIN))
	            {
	                levelObject = new Sprite(x, y, resourcesManager.coin_region, vbom)
	                {
	                    @Override
	                    protected void onManagedUpdate(float pSecondsElapsed) 
	                    {
	                        super.onManagedUpdate(pSecondsElapsed);
	                        
	                        if (player.collidesWith(this))
	                        {
	                            addToScore(20);
	                            LevelScoreCollector.addToScore(20);
	                            LevelScoreCollector.addToStars(1);
	                            this.setVisible(false);
	                            this.setIgnoreUpdate(true);
	                        }
	                    }
	                };
	                levelObject.registerEntityModifier(new LoopEntityModifier(new ScaleModifier(1, 1, 1.3f)));
	            }
	            else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER))
	            {
	                player = new Player(x, y, vbom, camera, physicsWorld)
	                {
	                    @Override
	                    public void onDie()
	                    {
	                    	if (!gameFinished)
	                        {
	                    		displayGameOverText();
	                        }
	                    }
	                };
	                levelObject = player;
	            }
	            else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_LEVEL_COMPLETE))
	            {
	                levelObject = new Sprite(x, y, resourcesManager.complete_stars_region, vbom)
	                {
	                    @Override
	                    protected void onManagedUpdate(float pSecondsElapsed) 
	                    {
	                        super.onManagedUpdate(pSecondsElapsed);

	                        if (player.collidesWith(this))
	                        {
	                            if( score >= 0 && score < 30 ) 
	                            {
	                            	levelCompleteWindow.display(StarsCount.ONE, GameScene.this, camera);
	                            	gameFinished = true;
	                            	engine.registerUpdateHandler(new TimerHandler(4f, new ITimerCallback() 
	                        		{
	                                    public void onTimePassed(final TimerHandler pTimerHandler) 
	                                    {
	                                    	engine.unregisterUpdateHandler(pTimerHandler);
	                                    	
	                                    	levelNumber++;
	                                    	Log.i("LvlNumber = ", "" + levelNumber);
	                                    	if(levelNumber > 3)
	                                    	{
	                                    		disposeScene();
	                                    		SceneManager.getInstance().loadEndScene(engine);
	                                    	}
	                                    	else
	                                    	{
	                                    		loadNextLevel();
	                                    	}
	                                    }
	                        		}));
	                            	
	                            	
	                            	
	                            }
	                            else if ( score > 30 && score < 50)
	                            {
	                            	levelCompleteWindow.display(StarsCount.TWO, GameScene.this, camera);
	                            	gameFinished = true;
	                            	engine.registerUpdateHandler(new TimerHandler(4f, new ITimerCallback() 
	                        		{
	                                    public void onTimePassed(final TimerHandler pTimerHandler) 
	                                    {
	                                    	engine.unregisterUpdateHandler(pTimerHandler);
	                                    	levelNumber++;
	                                    	Log.i("LvlNumber = ", "" + levelNumber);
	                                    	if(levelNumber > 3)
	                                    	{
	                                    		disposeScene();
	                                    		SceneManager.getInstance().loadEndScene(engine);
	                                    	}
	                                    	else
	                                    	{
	                                    		
	                                    		loadNextLevel();
	                                    	}
	                                    }
	                        		}));
	                            	
	                            	
	                            	
	                            }
	                            else if ( score > 50 )
	                            {
	                            	
	                            	levelCompleteWindow.display(StarsCount.THREE, GameScene.this, camera);
	                            	gameFinished = true;
	                            	engine.registerUpdateHandler(new TimerHandler(4f, new ITimerCallback() 
	                        		{
	                                    public void onTimePassed(final TimerHandler pTimerHandler) 
	                                    {
	                                    	engine.unregisterUpdateHandler(pTimerHandler);
	                                    	levelNumber++;
	                                    	Log.i("LvlNumber = ", "" + levelNumber);
	                                    	if(levelNumber > 3)
	                                    	{
	                                    		disposeScene();
	                                    		SceneManager.getInstance().loadEndScene(engine);
	                                    	}
	                                    	else
	                                    	{
	                                    		
	                                    		loadNextLevel();
	                                    	}
	                                    }
	                        		}));
	                            }
	                            this.setVisible(false);
	                            this.setIgnoreUpdate(true);
	                        }
	                    }

						
	                };
	                levelObject.registerEntityModifier(new LoopEntityModifier(new ScaleModifier(1, 1, 1.3f)));
	            }
	            else
	            {
	                throw new IllegalArgumentException();
	            }

	            levelObject.setCullingEnabled(true);

	            return levelObject;
	        }
	    });

	    levelLoader.loadLevelFromAsset(activity.getAssets(), "level/" + levelID + ".lvl");
	    
	    
	    jumpMusic = resourcesManager.jumpMusic;
	    resourcesManager.themeMusic.setLooping(true);
	}
	
	private void loadNextLevel()
	{
		//this.disposeScene();
		resourcesManager.themeMusic.stop();
		SceneManager.getInstance().setScene(new LoadingScene());
		engine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() 
		{
            public void onTimePassed(final TimerHandler pTimerHandler) 
            {
            	engine.unregisterUpdateHandler(pTimerHandler);
            	ResourcesManager.getInstance().unloadGameTextures();
                SceneManager.getInstance().loadGameScene(engine);
//            	GameScene gameScene = new GameScene();
//                SceneManager.getInstance().setScene(gameScene);
            }
		}));
	}
	
	
	private void createGameOverText()
	{
	    gameOverText = new Text(0, 0, resourcesManager.font, "Game Over!", vbom);
	}
	
	private void displayGameOverText()
	{
	    camera.setChaseEntity(null);
	    gameOverText.setPosition(camera.getCenterX(), camera.getCenterY());
	    attachChild(gameOverText);
	    gameFinished = true;
	    
	}
	
	//
	
	private void createHUD()
	{
	    gameHUD = new HUD();
	    
	    final TextOptions textOptions = new TextOptions(HorizontalAlign.LEFT);

	 // CREATE SCORE TEXT
	    scoreText = new Text(20, 420, resourcesManager.font, "Score: 0123456789", textOptions ,vbom);
	    scoreText.setAnchorCenter(0, 0);    
	    scoreText.setText("Score: 0");
	    gameHUD.attachChild(scoreText);
	    
	 // Attach buttons
	    Sprite leftb = new Sprite(50, 40, resourcesManager.left_button, vbom)
	    {
	    	
	    	public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
				
	    		super.onAreaTouched(touchEvent, x, y);
	    		
	    		if(touchEvent.getAction() == MotionEvent.ACTION_DOWN){
	    			
	    			player.moveLeft();
	    			
	    		}
	    		else if(touchEvent.getAction() == MotionEvent.ACTION_UP) {
	    			
	    			player.stop();
	    			
	    		}
	    		else if(touchEvent.getAction() == MotionEvent.ACTION_OUTSIDE) {
	    			
	    			player.stop();
	    			
	    		}
	    		else if(touchEvent.getAction() == MotionEvent.ACTION_CANCEL) {
	    			
	    			player.stop();
	    			
	    		}
	    		
	    		return true;
			}
	    	
	    };
	    Sprite rightb = new Sprite(133, 40, resourcesManager.right_button, vbom){
	    	
	    	public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
				
	    		super.onAreaTouched(touchEvent, x, y);
	    		
	    		
	    		if(touchEvent.getAction() == MotionEvent.ACTION_DOWN){
	    			
	    			player.moveRight();
	    			
	    		}
	    		else if(touchEvent.getAction() == MotionEvent.ACTION_UP) {
	    			
	    			player.stop();
	    			
	    		}
	    		else if(touchEvent.getAction() == MotionEvent.ACTION_OUTSIDE) {
	    			
	    			player.stop();
	    			
	    		}
	    		else if(touchEvent.getAction() == MotionEvent.ACTION_CANCEL) {
	    			
	    			player.stop();
	    			
	    		}
	    		
	    		return true;
	    	}
	    	
	    	
	    };
	    Sprite jump = new Sprite(650, 40, resourcesManager.jump_button, vbom){
	    	
	    	public boolean onAreaTouched(TouchEvent touchEvent, float x, float y) {
	    		
	    		super.onAreaTouched(touchEvent, x, y);
	    		
	    		
	    		if(touchEvent.getAction() == MotionEvent.ACTION_DOWN){
	    			
	    			player.jump();
	    			if (jumpMusic.isReleased())
					{
						jumpMusic.stop();
					}
	    			jumpMusic.play();
	    			
	    		}
	    		
	    		
	    		return true;
	    	}
	    	
	    	
	    };
	    leftb.setScale(2.3f);
	    rightb.setScale(2.3f);
	    jump.setScale(2.3f);
	    
	    
	    gameHUD.registerTouchArea(leftb);
	    gameHUD.registerTouchArea(rightb);
	    gameHUD.registerTouchArea(jump);
	    
	    gameHUD.attachChild(leftb);
	    gameHUD.attachChild(rightb);
	    gameHUD.attachChild(jump);
	    
	    camera.setHUD(gameHUD);
	}
	

	private void addToScore(int i)
	{
	    score += i;
	    scoreText.setText("Score: " + score);
	}
	
	private PhysicsWorld physicsWorld;

	private void createPhysics()
	{
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, -17), false); 
	    physicsWorld.setContactListener(contactListener());
	    registerUpdateHandler(physicsWorld);
	}
	
	private void createBackground() {
		
		//setBackground(new Background(Color.BLUE));
		
		switch(levelNumber)
		{
			case 1:
				background1 = new Sprite(500, 500, resourcesManager.tlo1, resourcesManager.vbom);
				this.attachChild(background1);
				break;
			case 2:
				background2 = new Sprite(500, 500, resourcesManager.tlo2, resourcesManager.vbom);
				this.attachChild(background2);
				break;
			case 3:
				background3 = new Sprite(500, 500, resourcesManager.tlo3, resourcesManager.vbom);
				this.attachChild(background3);
				break;
			default:
				break;
		
		}
	}
	
	@Override
	public void createScene()
	{
		createBackground();
	    createHUD();
	    createPhysics();
	    createGameOverText();
	    loadLevel(levelNumber);
	    levelCompleteWindow = new LevelCompleteWindow(vbom);
	    resourcesManager.themeMusic.play();
	    
	}

	@Override
	public void onBackKeyPressed()
	{
		resourcesManager.themeMusic.stop();
		LevelScoreCollector.allScore = 0;
		levelNumber = 1;
		SceneManager.getInstance().loadMenuScene(engine);
	}



	@Override
	public SceneType getSceneType() {
		
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene()
	{
	    camera.setHUD(null);
	    camera.setCenter(400, 240);
	    camera.setChaseEntity(null);
	    
	    gameHUD.detachSelf();
	    gameHUD.dispose();
	    
//	    if(gameFinished)
//	    {
//	    	levelNumber = 1;
//	    }
	    
	    if(background1 != null)
	    {
	    	
	    	background1.detachSelf();
	    	background1.dispose();
	    }
	    
	    if(background2 != null)
	    {
	    	background2.detachSelf();
	    	background2.dispose();
	    	
	    }
	    
	    if(background3 != null)
	    {
	    	background3.detachSelf();
	    	background3.dispose();
	    	
	    }
	    
	    gameOverText.detachSelf();
	    gameOverText.dispose();
	    
	    scoreText.detachSelf();
	    scoreText.dispose();
	    
	    levelCompleteWindow.detachSelf();
	    levelCompleteWindow.dispose();
	    
	    player.detachSelf();
	    player.dispose();
	    
	    this.detachSelf();
		this.dispose();
		
	}

}

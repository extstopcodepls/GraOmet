package com.game.object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.R.bool;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.game.resourcemanager.ResourcesManager;

/**
 * @author Mateusz Mysliwiec
 * @author www.matim-dev.com
 * @version 1.0
 */
public abstract class Player extends AnimatedSprite
{
	// ---------------------------------------------
	// VARIABLES
	// ---------------------------------------------

	private Body	body;

	private int		footContacts	= 0;

	// ---------------------------------------------
	// MOVE PLAYER
	// ---------------------------------------------

	public enum movePlayer
	{
		MOVELEFT, MOVERIGHT,

	}

	// ---------------------------------------------
	// CONSTRUCTOR
	// ---------------------------------------------

	public Player(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld)
	{
		super(pX, pY, ResourcesManager.getInstance().player_region, vbo);
		createPhysics(camera, physicsWorld);
		camera.setChaseEntity(this);
	}

	// ---------------------------------------------
	// METHODS
	// ---------------------------------------------

	public abstract void onDie();

	public void increaseFootContacts()
	{
		footContacts++;
	}

	public void decreaseFootContacts()
	{
		footContacts--;
	}
	

	private boolean	canRun	= false;

	private boolean	running = false;

	private boolean	jumped = false;

	public void setRunning()
	{
		canRun = true;

		final long[] PLAYER_ANIMATE = new long[] { 70, 70, 70 };

		animate(PLAYER_ANIMATE, 0, 2, true);
	}

	public void jump()
	{
		if (footContacts < 1)
		{
			return;
		}
		body.setLinearVelocity(new Vector2(body.getLinearVelocity().x, 12));
		
	}

	public void moveLeft()
	{
		body.setLinearVelocity(new Vector2(-5.0f, body.getLinearVelocity().y));
		final long[] PLAYER_ANIMATE = new long[] { 70, 70, 70 };

		animate(PLAYER_ANIMATE, 3, 5, true);
	}

	public void moveRight()
	{
		body.setLinearVelocity(new Vector2(5.0f, body.getLinearVelocity().y));
		final long[] PLAYER_ANIMATE = new long[] { 70, 70, 70 };

		animate(PLAYER_ANIMATE, 6, 8, true);
	}
	
	public void stop()
	{
		if (footContacts < 1)
		{
			return;
		}
		body.setLinearVelocity(0,0);
		stopAnimation();
	}

	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld)
	{
		body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));

		body.setUserData("player");
		body.setFixedRotation(true);
		

		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false)
		{
			@Override
			public void onUpdate(float pSecondsElapsed)
			{
				super.onUpdate(pSecondsElapsed);
				camera.onUpdate(0.1f);
				
				//Log.i("x: " + getX(), "y: " + getY());
				if (getY() <= 0)
				{
					onDie();
					
				}
				// TODO jesli klawisze nie dzialaja to tutaj poprawic bieganie
//				if (canRun)
//				{
//					body.setLinearVelocity(new Vector2(5f, body.getLinearVelocity().y));
//				}
			}
		});
	}

	

}

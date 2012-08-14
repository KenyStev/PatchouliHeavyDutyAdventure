package rosalila.studio.sokochuy;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITextureRegion;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Box extends Sprite {

	public Body body;
	
	public Box(float pX, float pY,
			ITextureRegion pTextureRegion,PhysicsWorld mPhysicsWorld) {
		super(pX, pY, pTextureRegion, Global.vertex_buffer_object_manager);
		
        //Smaller boxes
        this.setHeight(this.getHeight()*(float)0.7);
        this.setWidth(this.getWidth()*(float)0.7);

		final FixtureDef playerFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 0.5f);
		this.body = PhysicsFactory.createBoxBody(mPhysicsWorld, this, BodyType.DynamicBody, playerFixtureDef);
        mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false){
                @Override
                public void onUpdate(float pSecondsElapsed){
                        super.onUpdate(pSecondsElapsed);
                        body.setLinearVelocity(0,0);
                }
        });
	}
}

package rosalila.studio.sokochuy;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;

public class Point extends Sprite {
	public Point(float pX, float pY,
			ITextureRegion pTextureRegion) {
		super(pX, pY, pTextureRegion, Global.vertex_buffer_object_manager);

	}
}

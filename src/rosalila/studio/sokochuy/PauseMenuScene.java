package rosalila.studio.sokochuy;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.content.Context;
import android.opengl.GLES20;

public class PauseMenuScene extends MenuScene implements IOnMenuItemClickListener{

	public static final int MENU_RESET = 0;
	public static final int MENU_MAIN = MENU_RESET + 1;
	public static final int MENU_QUIT = MENU_MAIN + 1;

	private BitmapTextureAtlas mMenuTexture;
	private ITextureRegion mMenuResetTextureRegion;
	private ITextureRegion mMenuMainTextureRegion;
	private ITextureRegion mMenuQuitTextureRegion;

	public PauseMenuScene(Camera camera)
	{
		super(camera);

		this.setBackgroundEnabled(false);

		this.mMenuTexture = new BitmapTextureAtlas((TextureManager) Global.texture_manager, 256, 328, TextureOptions.BILINEAR);
		this.mMenuResetTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mMenuTexture, (Context)Global.main_activity, "menu_reset.png", 0, 0);
		this.mMenuMainTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mMenuTexture, (Context)Global.main_activity, "menu_main.png", 0, 50);
		this.mMenuQuitTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mMenuTexture, (Context)Global.main_activity, "menu_quit.png", 0, 150);
		this.mMenuTexture.load();

		final SpriteMenuItem resetMenuItem = new SpriteMenuItem(MENU_RESET, this.mMenuResetTextureRegion, (VertexBufferObjectManager)Global.vertex_buffer_object_manager );
		resetMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		this.addMenuItem(resetMenuItem);

		final SpriteMenuItem mainMenuItem = new SpriteMenuItem(MENU_MAIN, this.mMenuMainTextureRegion, (VertexBufferObjectManager)Global.vertex_buffer_object_manager);
		mainMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		this.addMenuItem(mainMenuItem);

		final SpriteMenuItem quitMenuItem = new SpriteMenuItem(MENU_QUIT, this.mMenuQuitTextureRegion, (VertexBufferObjectManager)Global.vertex_buffer_object_manager);
		quitMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		this.addMenuItem(quitMenuItem);

		this.buildAnimations();

		this.setOnMenuItemClickListener(this);
	}
	public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) {
		
		PauseMenuScene main_menu = (PauseMenuScene) pMenuScene;
		int item_id=pMenuItem.getID();
		if(main_menu.MENU_RESET==item_id)
		{
			if(Global.engine.getScene() instanceof MapScene)
			{
				MapScene map_scene=(MapScene)Global.engine.getScene();
				Global.engine.setScene(new MapScene(map_scene.level_id));
				return true;
			}
		}
		else if(main_menu.MENU_MAIN==item_id)
		{
			Global.engine.getScene().back();
			Global.engine.setScene(new MainMenuScene(Global.mZoomCamera));
			return true;
		}
		else if(main_menu.MENU_QUIT==item_id)
		{
			/* End Activity. */
			Global.main_activity.finish();
			return true;
		}

		return false;
	}
}
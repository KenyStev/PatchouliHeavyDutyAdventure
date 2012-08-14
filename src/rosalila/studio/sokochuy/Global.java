package rosalila.studio.sokochuy;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.content.res.AssetManager;

public class Global {
	public static VertexBufferObjectManager vertex_buffer_object_manager;
	public static TextureManager texture_manager;
	public static MainActivity main_activity;
	public static AssetManager assest_manager;
	public static ZoomCamera mZoomCamera;
	
	public static int SCREEN_WIDTH = 480;
	public static int SCREEN_HEIGHT = 320;
	
	public static float MIN_ZOOM_BOUND = (float)0.1;
	public static float MAX_ZOOM_BOUND = (float)10;
}
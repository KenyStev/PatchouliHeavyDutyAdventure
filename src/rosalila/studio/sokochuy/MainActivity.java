package rosalila.studio.sokochuy;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.view.RenderSurfaceView;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga
 *
 * @author Nicolas Gramlich
 * @since 13:58:48 - 19.07.2010
 */
public class MainActivity extends SimpleBaseGameActivity {

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	@Override
	protected void onSetContentView()
	{
	    final FrameLayout frameLayout = new FrameLayout(this);
	    final FrameLayout.LayoutParams frameLayoutLayoutParams =
	            new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
	                                         FrameLayout.LayoutParams.FILL_PARENT);

	    final AdView adView = new AdView(this, AdSize.BANNER, "a14fdaed341e595");
	    adView.refreshDrawableState();
	    adView.setVisibility(AdView.VISIBLE);
	    final FrameLayout.LayoutParams adViewLayoutParams =
	            new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
	                                         FrameLayout.LayoutParams.WRAP_CONTENT,
	                                         Gravity.CENTER_HORIZONTAL);

	    // top of AD is at middle of the screen
	    adViewLayoutParams.topMargin = 0;

	    AdRequest adRequest = new AdRequest();
	    adRequest.addTestDevice( AdRequest.TEST_EMULATOR);
	    adView.loadAd(adRequest);

	    this.mRenderSurfaceView = new RenderSurfaceView(this);
	    mRenderSurfaceView.setRenderer(mEngine,this);

	    final android.widget.FrameLayout.LayoutParams surfaceViewLayoutParams =
	            new FrameLayout.LayoutParams(super.createSurfaceViewLayoutParams());

	    frameLayout.addView(this.mRenderSurfaceView, surfaceViewLayoutParams);
	    frameLayout.addView(adView, adViewLayoutParams);

	    this.setContentView(frameLayout, frameLayoutLayoutParams);
	}
	
	@Override
	public EngineOptions onCreateEngineOptions() {

		Global.mZoomCamera = new ZoomCamera(0, 0, Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT), Global.mZoomCamera);
	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		Global.vertex_buffer_object_manager=this.getVertexBufferObjectManager();
		Global.texture_manager=this.getTextureManager();
		Global.main_activity=this;
		Global.assest_manager=this.getAssets();
		Global.engine=this.mEngine;
	}	

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		return new MainMenuScene(Global.mZoomCamera);
	}

	
	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent)
	{
		if(pEvent.getAction() == TouchEvent.ACTION_DOWN) {
			if(pKeyCode==82//menu
					&& (Global.engine.getScene() instanceof MapScene//is mapscene
							|| Global.engine.getScene() instanceof PauseMenuScene))//or pause
			{
				if(this.mEngine.getScene().hasChildScene()) {
					/* Remove the menu and reset it. */
					this.mEngine.getScene().back();
				} else {
					/* Attach the menu. */
					this.mEngine.getScene().setChildScene(new PauseMenuScene(Global.mZoomCamera), false, true, true);
				}
				return true;
			}
			
			if(pKeyCode==4)//back
			{
				if(this.mEngine.getScene().hasChildScene()) {
					this.mEngine.getScene().back();
				} else {
					if(Global.engine.getScene() instanceof MainMenuScene)
						this.finish();
					if(Global.engine.getScene() instanceof MapScene)
						Global.engine.setScene(new MainMenuScene(Global.mZoomCamera));
				}
				return true;
			}
		} else {
			return super.onKeyDown(pKeyCode, pEvent);
		}
		return false;
	}
	
	public void printToastString(final String str)
	{
		runOnUiThread(new Runnable() {

	        @Override
	        public void run() {
	            Toast.makeText(Global.main_activity, str, Toast.LENGTH_SHORT).show();
	        }
	    });
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}

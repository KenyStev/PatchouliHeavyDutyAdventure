package rosalila.studio.patcheshda;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
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
import org.andengine.util.debug.Debug;

import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.iozm.rwei160144.AdCallbackListener;
import com.iozm.rwei160144.AdCallbackListener.MraidCallbackListener;
import com.iozm.rwei160144.AdView;
import com.iozm.rwei160144.Airpush;

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
	
	private Music mMusic;
	
//	Airpush airpush;
	
	@Override
	protected void onSetContentView()
	{
	    final FrameLayout frameLayout = new FrameLayout(this);
	    final FrameLayout.LayoutParams frameLayoutLayoutParams =
	            new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
	                                         FrameLayout.LayoutParams.FILL_PARENT);

	    final FrameLayout.LayoutParams adViewLayoutParams =
	            new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
	                                         FrameLayout.LayoutParams.WRAP_CONTENT,
	                                         Gravity.CENTER_HORIZONTAL);

	    this.mRenderSurfaceView = new RenderSurfaceView(this);
	    mRenderSurfaceView.setRenderer(mEngine,this);

	    final android.widget.FrameLayout.LayoutParams surfaceViewLayoutParams =
	            new FrameLayout.LayoutParams(super.createSurfaceViewLayoutParams());

	    frameLayout.addView(this.mRenderSurfaceView, surfaceViewLayoutParams);
//	    frameLayout.addView(adView, adViewLayoutParams);

	    
	    
	    
	    //Ad
	    AdView adView=new AdView(this, AdView.BANNER_TYPE_IN_APP_AD, AdView.PLACEMENT_TYPE_INTERSTITIAL, 45, false, false, 
	    	     AdView.ANIMATION_TYPE_LEFT_TO_RIGHT);
	    	     adView.setAdListener(adlistener);
	    	     
	    frameLayout.addView(adView, adViewLayoutParams);
	    
	    this.setContentView(frameLayout, frameLayoutLayoutParams);
	    
	    Airpush airpush=new Airpush(getApplicationContext(), null);
		
		airpush.startPushNotification(false);
		airpush.startIconAd();
		airpush.startDialogAd();
		airpush.startAppWall();
		airpush.startLandingPageAd();
		airpush.showRichMediaInterstitialAd();
	}
	
	AdCallbackListener.MraidCallbackListener adlistener = new AdCallbackListener.MraidCallbackListener() {

	     @Override
	     public void onAdClickListener()
	     {
	     //This will get called when ad is clicked.
	     }

	     @Override
	     public void onAdLoadedListener()
	     {
	     //This will get called when an ad has loaded.
	     }

	     @Override
	     public void onAdLoadingListener()
	     {
	     //This will get called when a rich media ad is loading.
	     }

	     @Override
	     public void onAdExpandedListner()
	     {
	     //This will get called when an ad is showing on a user's screen. This may cover the whole UI.
	     }

	     @Override
	     public void onCloseListener()
	     {
	     //This will get called when an ad is closing/resizing from an expanded state.
	     }

	     @Override
	     public void onErrorListener(String message)
	     {
	     //This will get called when any error has occurred. This will also get called if the SDK notices any integration mistakes.
	     }
	};
	
	
	@Override
	public EngineOptions onCreateEngineOptions() {

		Global.mZoomCamera = new ZoomCamera(0, 0, Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT);
		
		EngineOptions eo=new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT), Global.mZoomCamera);
		eo.getAudioOptions().setNeedsMusic(true);
		return eo;
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
		
        MusicFactory.setAssetBasePath("mfx/");
        try {
                this.mMusic = MusicFactory.createMusicFromAsset(this.mEngine.getMusicManager(), this, "music.ogg");
                this.mMusic.setLooping(true);
                this.mMusic.play();
        } catch (final IOException e) {
                Debug.e("Error", e);
        }

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
	
	@Override
	public void onResumeGame() {
	    super.onResumeGame();
		if(!mMusic.isPlaying())
			mMusic.play();
	}


	@Override
	protected void onPause() {
		if(mMusic.isPlaying())
			mMusic.pause();
	    super.onPause();
	}



	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}

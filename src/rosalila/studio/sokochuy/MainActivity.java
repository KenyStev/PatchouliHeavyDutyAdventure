package rosalila.studio.sokochuy;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.view.IRendererListener;
import org.andengine.opengl.view.RenderSurfaceView;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.view.Gravity;
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
public class MainActivity extends SimpleBaseGameActivity implements IOnSceneTouchListener, IOnAreaTouchListener, IScrollDetectorListener, IPinchZoomDetectorListener {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	
	private float mPinchZoomStartedCameraZoomFactor;	
	
	private SurfaceScrollDetector mScrollDetector;
	private PinchZoomDetector mPinchZoomDetector;
	
	public Scene mScene;

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
//		final FrameLayout frameLayout = new FrameLayout(this);
//		final FrameLayout.LayoutParams frameLayoutParams = 
//				new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
//											 FrameLayout.LayoutParams.FILL_PARENT);
//		final AdView adView = new AdView(this,AdSize.BANNER,"chii");
//		
//		adView.refreshDrawableState();
//		adView.setVisibility(AdView.VISIBLE);
//		final FrameLayout.LayoutParams adViewLayoutParams = 
//				new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
//											 FrameLayout.LayoutParams.WRAP_CONTENT,
//											 Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
//		int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
//		adViewLayoutParams.topMargin = height/2;
//		
//		AdRequest adRequest = new AdRequest();
//		adRequest.addTestDevice(AdRequest.TEST_EMULATOR);//
//		adView.loadAd(adRequest);
//		
//		this.mRenderSurfaceView = new RenderSurfaceView(this);
//		mRenderSurfaceView.setRenderer(mEngine);
//		final android.widget.FrameLayout.LayoutParams surfaceViewLayoutParams = 
//				new FrameLayout.LayoutParams(super.createSurfaceViewLayoutParams());
//		frameLayout.addView(this.mRenderSurfaceView,surfaceViewLayoutParams);
//		frameLayout.addView(adView,adViewLayoutParams);
//		
//		this.setContentView(frameLayout,frameLayoutParams);
		
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
	/*    
	    int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
	            getResources ().getDisplayMetrics ());*/
	    // top of AD is at middle of the screen
	    adViewLayoutParams.topMargin = 0;

	    AdRequest adRequest = new AdRequest();
	    adRequest.addTestDevice( AdRequest.TEST_EMULATOR);
	    adView.loadAd(adRequest);

	    //this.mRenderSurfaceView = new RenderSurfaceView(this);
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
		Toast.makeText(this, "The tile the player is walking on will be highlighted.", Toast.LENGTH_LONG).show();

		Global.mZoomCamera = new ZoomCamera(0, 0, Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT);
		
		this.mScrollDetector = new SurfaceScrollDetector(this);
		this.mPinchZoomDetector = new PinchZoomDetector(this);
		
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(Global.SCREEN_WIDTH, Global.SCREEN_HEIGHT), Global.mZoomCamera);
	}

	@Override
	public void onCreateResources() {
		Global.vertex_buffer_object_manager=this.getVertexBufferObjectManager();
		Global.texture_manager=this.getTextureManager();
		Global.main_activity=this;
		Global.assest_manager=this.getAssets();
	}	

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		mScene = new MapScene("tmx/level_1.tmx");
		mScene.setOnSceneTouchListener(this);
		
		return mScene;
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		this.mPinchZoomDetector.onTouchEvent(pSceneTouchEvent);

		if(this.mPinchZoomDetector.isZooming()) {
			this.mScrollDetector.setEnabled(false);
		} else {
			if(pSceneTouchEvent.isActionDown()) {
				MapScene map_scene=(MapScene)mScene;
				map_scene.onTouch(pSceneTouchEvent);
				
				this.mScrollDetector.setEnabled(true);
			}
			this.mScrollDetector.onTouchEvent(pSceneTouchEvent);
		}
		
		return false;
	}

	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		if(pSceneTouchEvent.isActionDown()) {
			return true;
		}
		return false;
	}
	
	@Override
	public void onScrollStarted(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
		final float zoomFactor = Global.mZoomCamera.getZoomFactor();
		Global.mZoomCamera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY / zoomFactor);
	}

	@Override
	public void onScroll(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
		final float zoomFactor = Global.mZoomCamera.getZoomFactor();
		Global.mZoomCamera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY / zoomFactor);
	}
	
	@Override
	public void onScrollFinished(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
		final float zoomFactor = Global.mZoomCamera.getZoomFactor();
		Global.mZoomCamera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY / zoomFactor);
	}

	@Override
	public void onPinchZoomStarted(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent) {
		this.mPinchZoomStartedCameraZoomFactor = Global.mZoomCamera.getZoomFactor();
	}

	@Override
	public void onPinchZoom(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent, final float pZoomFactor) {
		float final_zoom = this.mPinchZoomStartedCameraZoomFactor * pZoomFactor;
		if(final_zoom>Global.MIN_ZOOM_BOUND && final_zoom<Global.MAX_ZOOM_BOUND)
		{
			Global.mZoomCamera.setZoomFactor(final_zoom);
		}
	}

	@Override
	public void onPinchZoomFinished(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent, final float pZoomFactor) {
		float final_zoom = this.mPinchZoomStartedCameraZoomFactor * pZoomFactor;
		if(final_zoom>Global.MIN_ZOOM_BOUND && final_zoom<Global.MAX_ZOOM_BOUND)
		{
			Global.mZoomCamera.setZoomFactor(final_zoom);
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}

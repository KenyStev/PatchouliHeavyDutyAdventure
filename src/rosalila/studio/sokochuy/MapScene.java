package rosalila.studio.sokochuy;

import java.util.ArrayList;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLayerProperty;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.andengine.extension.tmx.TMXObject;
import org.andengine.extension.tmx.TMXObjectGroup;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXProperty;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Editable;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.View;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class MapScene extends Scene implements IOnSceneTouchListener,IOnAreaTouchListener,IScrollDetectorListener, IPinchZoomDetectorListener, KeyListener{
	
	Chuy chuy;
	ArrayList<Box> boxes;
	ArrayList<Point> points;
	
	public TMXTiledMap mTMXTiledMap;
	public TMXLayer tmxLayer; 
	
	private TiledTextureRegion mChuyTextureRegion;
	private BitmapTextureAtlas mChuyTextureAtlas;
	
	private TiledTextureRegion mBoxTextureRegion;
	private BitmapTextureAtlas mBoxTextureAtlas;
	
	private TiledTextureRegion mPointTextureRegion;
	private BitmapTextureAtlas mPointTextureAtlas;
	
	private PhysicsWorld mPhysicsWorld;
	
	private float mPinchZoomStartedCameraZoomFactor;	
	
	private SurfaceScrollDetector mScrollDetector;
	private PinchZoomDetector mPinchZoomDetector;
	
	int level_id;
	
	TextureRegion mCompletedTextureRegion;
	Sprite level_completed;
	
	String text_intro;
	
	public MapScene(int level_id)
	{
		super();
		
		text_intro="";
		
		this.level_id=level_id;
		
		setOnAreaTouchTraversalFrontToBack();
		setTouchAreaBindingOnActionDownEnabled(true);
		
		this.mScrollDetector = new SurfaceScrollDetector(this);
		this.mPinchZoomDetector = new PinchZoomDetector(this);
		
		setOnSceneTouchListener(this);
		
		loadResouces();

		loadMap("tmx/level_"+level_id+".tmx");
		
		registerUpdateHandler();
		
		setupCamera();
		
		if(text_intro!="")
			Global.main_activity.printToastString(text_intro);
	}
	
	public void loadResouces()
	{
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		this.mChuyTextureAtlas = new BitmapTextureAtlas(Global.texture_manager, 516, 688, TextureOptions.DEFAULT);
		this.mChuyTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mChuyTextureAtlas, Global.main_activity, "chuy.png", 0, 0, 3, 4);
		this.mChuyTextureAtlas.load();
		
		this.mBoxTextureAtlas = new BitmapTextureAtlas(Global.texture_manager, 171, 171, TextureOptions.DEFAULT);
		this.mBoxTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBoxTextureAtlas, Global.main_activity, "box.png", 0, 0, 1, 1);
		this.mBoxTextureAtlas.load();
		
		this.mPointTextureAtlas = new BitmapTextureAtlas(Global.texture_manager, 30, 30, TextureOptions.DEFAULT);
		this.mPointTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mPointTextureAtlas, Global.main_activity, "point.png", 0, 0, 1, 1);
		this.mPointTextureAtlas.load();
		
		BitmapTextureAtlas mCompletedTextureAtlas = new BitmapTextureAtlas(Global.texture_manager, 720, 480, TextureOptions.BILINEAR);
		mCompletedTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mCompletedTextureAtlas, Global.main_activity, "level_completed.png", 0, 0);
		mCompletedTextureAtlas.load();
	}
	
	public void loadMap(String tmx_path)
	{
		this.mPhysicsWorld = new FixedStepPhysicsWorld(30, new Vector2(0, 0), false, 8, 1);
		registerUpdateHandler(this.mPhysicsWorld);
		
		boxes = new ArrayList<Box>();
		points = new ArrayList<Point>();
		
		try {
			final TMXLoader tmxLoader = new TMXLoader(Global.assest_manager, Global.texture_manager, TextureOptions.BILINEAR_PREMULTIPLYALPHA, Global.vertex_buffer_object_manager, new ITMXTilePropertiesListener() {
				@Override
				public void onTMXTileWithPropertiesCreated(final TMXTiledMap pTMXTiledMap, final TMXLayer pTMXLayer, final TMXTile pTMXTile, final TMXProperties<TMXTileProperty> pTMXTileProperties) {
					/* We are going to count the tiles that have the property "cactus=true" set. */
					if(pTMXTileProperties.containsTMXProperty("wall", "true")) {
				        final Rectangle rect = new Rectangle(pTMXTile.getTileX()+1,pTMXTile.getTileY()+1,pTMXTile.getTileWidth()-2,pTMXTile.getTileHeight()-2, Global.vertex_buffer_object_manager);
				        final FixtureDef boxFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 1f);
				        PhysicsFactory.createBoxBody(mPhysicsWorld, rect, BodyType.StaticBody, boxFixtureDef);
				        rect.setVisible(false);
				        attachChild(rect);
					}
				}
			});
			this.mTMXTiledMap = tmxLoader.loadFromAsset(tmx_path);

		} catch (final TMXLoadException e) {
			//Debug.e(e);
		}

		this.tmxLayer = this.mTMXTiledMap.getTMXLayers().get(0);
		
		//set text
		TMXProperties<TMXLayerProperty> tmx_layer_properties = tmxLayer.getTMXLayerProperties();
		for(int i=0;i<tmx_layer_properties.size();i++)
		{
			TMXProperty property = tmx_layer_properties.get(i);
			if(property.getName().equals("text"))
			{
				text_intro=property.getValue();
			}
		}
		
		attachChild(tmxLayer);

		/* Load objects in the map. */
		
		final TMXObjectGroup objectsLayer = this.mTMXTiledMap.getTMXObjectGroups().get(0);
		
		ArrayList<TMXObject> objects = objectsLayer.getTMXObjects();
		
		for(int i=0;i<objects.size();i++)
		{
			TMXObject object = objects.get(i);

			if(object.getName().equals("Box"))
			{
				addBox(object.getX(),object.getY());
			}
			if(object.getName().equals("Chuy"))
			{
				addChuy(object.getX(),object.getY());
			}
			if(object.getName().equals("Point"))
			{
				addPoint(object.getX(),object.getY());
			}
		}
		
		this.level_completed = new Sprite(tmxLayer.getWidth()/2-mCompletedTextureRegion.getWidth()/2, tmxLayer.getHeight()/2-mCompletedTextureRegion.getHeight()/2, mCompletedTextureRegion, Global.vertex_buffer_object_manager)
		{
			@Override
			public boolean onAreaTouched(
					TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				// TODO Auto-generated method stub
				if(this.isVisible())
				{
					if(level_id+1<=15)
					{
						Global.engine.setScene(new MapScene(level_id+1));
					}else
					{
						Global.engine.setScene(new MainMenuScene(Global.mZoomCamera));
					}
				}
				return super
						.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		level_completed.setVisible(false);
		registerTouchArea(level_completed);
		attachChild(level_completed);
	}
	
	public void registerUpdateHandler()
	{		
		registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				if(isGameOver())
				{
					SharedPreferences preferences = Global.main_activity.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
					Editor editor = preferences.edit();
	    			editor.putBoolean("level "+level_id+" completed", true);
	    			editor.commit();
	    			
	    			level_completed.setVisible(true);
				}
			}
		});
	}
	
	public void setupCamera()
	{
		//Set zoom
		float div_x = (float)Global.SCREEN_WIDTH/(float)tmxLayer.getWidth();
		float div_y = (float)Global.SCREEN_HEIGHT/(float)tmxLayer.getHeight();
		float div_res;
		if(div_x<div_y)
			div_res=div_x;
		else
			div_res=div_y;
		
		if(div_res<Global.MIN_ZOOM_BOUND)
			div_res=Global.MIN_ZOOM_BOUND;
		
		if(div_res>Global.MAX_ZOOM_BOUND)
			div_res=Global.MAX_ZOOM_BOUND;
		
		Global.mZoomCamera.setZoomFactor((float)div_res);
		
		//Center camera
		Global.mZoomCamera.setCenter(tmxLayer.getWidth()/2, tmxLayer.getHeight()/2);
		
		//Set bounds
		Global.mZoomCamera.setBounds(0, 0, tmxLayer.getHeight(), tmxLayer.getWidth());
		Global.mZoomCamera.setBoundsEnabled(true);
	}

	public void onTouch(TouchEvent pSceneTouchEvent)
	{
		chuy.move(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
	}
	
	public void addChuy(int pos_x,int pos_y)
	{
		Chuy chuy = new Chuy(pos_x,pos_y, this.mChuyTextureRegion,mPhysicsWorld);
		attachChild(chuy);
		this.chuy=chuy;
	}
	
	public void addBox(int pos_x,int pos_y)
	{
		final Box box = new Box(pos_x,pos_y, this.mBoxTextureRegion,mPhysicsWorld);
		attachChild(box);
		boxes.add(box);
	}
	
	public void addPoint(int pos_x,int pos_y)
	{
		final Point point = new Point(pos_x,pos_y, this.mPointTextureRegion);
		attachChild(point);
		points.add(point);
	}
	
	public boolean isGameOver()
	{
		for(int i=0;i<points.size();i++)
		{
			boolean colides=false;
			for(int j=0;j<boxes.size();j++)
			{
				if(points.get(i).collidesWith(boxes.get(j)))
					colides=true;
			}
			if(!colides)
				return false;
		}
		return true;
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		this.mPinchZoomDetector.onTouchEvent(pSceneTouchEvent);

		if(this.mPinchZoomDetector.isZooming()) {
			this.mScrollDetector.setEnabled(false);
		} else {
			if(pSceneTouchEvent.isActionDown()) {
				onTouch(pSceneTouchEvent);
				
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

	@Override
	public void clearMetaKeyState(View view, Editable content, int states) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getInputType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean onKeyDown(View view, Editable text, int keyCode,
			KeyEvent event) {
		// TODO Auto-generated method stub
		System.exit(0);
		if(Global.engine.getScene().hasChildScene()) {
			/* Remove the menu and reset it. */
			Global.engine.getScene().back();
		} else {
			/* Attach the menu. */
			Global.engine.getScene().setChildScene(new PauseMenuScene(Global.mZoomCamera), false, true, true);
		}
		return true;
	}

	@Override
	public boolean onKeyOther(View view, Editable text, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onKeyUp(View view, Editable text, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
}

package rosalila.studio.sokochuy;

import java.util.ArrayList;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.andengine.extension.tmx.TMXObject;
import org.andengine.extension.tmx.TMXObjectGroup;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.debug.Debug;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class MapScene extends Scene{
	
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
	
	public MapScene(String tmx_path)
	{
		super();
		setOnAreaTouchTraversalFrontToBack();
		setTouchAreaBindingOnActionDownEnabled(true);
		
		loadResouces();

		loadMap(tmx_path);
		
		registerUpdateHandler();
		
		setupCamera();
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
			Debug.e(e);
		}

		this.tmxLayer = this.mTMXTiledMap.getTMXLayers().get(0);		
		
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
	}
	
	public void registerUpdateHandler()
	{		
		registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				if(isGameOver())
					System.exit(0);
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
}

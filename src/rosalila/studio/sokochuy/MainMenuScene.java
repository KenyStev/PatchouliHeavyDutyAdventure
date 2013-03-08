package rosalila.studio.sokochuy;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.Entity;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;

public class MainMenuScene extends MenuScene implements IOnMenuItemClickListener{

	private Sprite bg;
	private Sprite splash_screen;
	private Sprite ending_bg;
	private Sprite info_bg;
	private SpriteMenuItem ending_button;
	private SpriteMenuItem info_button;
	private SpriteMenuItem continue_button;

	public static final int MENU_RESET = 0;
	public static final int MENU_QUIT = MENU_RESET + 1;
	public static final int MENU_SHOOTER = MENU_QUIT + 1;
	public static final int MENU_DATINGSIM = MENU_SHOOTER + 1;

	ITextureRegion mDatingSimTextureRegion;
	ITextureRegion mItemBgTextureRegion;
	ITextureRegion mItemCompletedBgTextureRegion;
	
	public static SharedPreferences preferences;
	
	public MainMenuScene(Camera camera)
	{
		super(camera);
		
		Global.main_activity.printToastString("loading...");
		
		preferences = Global.main_activity.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);

		this.setOnMenuItemClickListener(this);
		this.setBackgroundEnabled(false);
		
		//Background textures
		BitmapTextureAtlas mBitmapTextureAtlas = new BitmapTextureAtlas(Global.texture_manager, 720, 480, TextureOptions.BILINEAR);
		ITextureRegion mBgTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, Global.main_activity, "main_background.png", 0, 0);
		mBitmapTextureAtlas.load();
		
		BitmapTextureAtlas mBitmapTextureAtlasSplash = new BitmapTextureAtlas(Global.texture_manager, 720, 480, TextureOptions.BILINEAR);
		ITextureRegion mSplashTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlasSplash, Global.main_activity, "splash_screen.png", 0, 0);
		mBitmapTextureAtlasSplash.load();
		
		BitmapTextureAtlas mBitmapTextureAtlasBgEnding = new BitmapTextureAtlas(Global.texture_manager, 720, 480, TextureOptions.BILINEAR);
		ITextureRegion mBgEndingTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlasBgEnding, Global.main_activity, "ending_background.png", 0, 0);
		mBitmapTextureAtlasBgEnding.load();
		
		BitmapTextureAtlas mBitmapTextureAtlasBgInfo = new BitmapTextureAtlas(Global.texture_manager, 720, 480, TextureOptions.BILINEAR);
		ITextureRegion mBgInfoTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlasBgInfo, Global.main_activity, "info_background.png", 0, 0);
		mBitmapTextureAtlasBgInfo.load();
		
		//Button textures
		BitmapTextureAtlas mBitmapTextureAtlasEndingButton = new BitmapTextureAtlas(Global.texture_manager, 80, 110, TextureOptions.BILINEAR);
		ITextureRegion mEndingButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlasEndingButton, Global.main_activity, "ending_button.png", 0, 0);
		mBitmapTextureAtlasEndingButton.load();
		
		BitmapTextureAtlas mBitmapTextureAtlasInfoButton = new BitmapTextureAtlas(Global.texture_manager, 80, 110, TextureOptions.BILINEAR);
		ITextureRegion mInfoButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlasInfoButton, Global.main_activity, "info_button.png", 0, 0);
		mBitmapTextureAtlasInfoButton.load();
		
		BitmapTextureAtlas mBitmapTextureAtlasContinueButton = new BitmapTextureAtlas(Global.texture_manager, 80, 110, TextureOptions.BILINEAR);
		ITextureRegion mContinueButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlasContinueButton, Global.main_activity, "continue_button.png", 0, 0);
		mBitmapTextureAtlasContinueButton.load();
		
		//Item textures
		BitmapTextureAtlas mItemBGTextureAtlas = new BitmapTextureAtlas(Global.texture_manager, 90, 90, TextureOptions.BILINEAR);
		mItemBgTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mItemBGTextureAtlas, Global.main_activity, "item_bg.png", 0, 0);
		mItemBGTextureAtlas.load();
		
		BitmapTextureAtlas mItemCompletedBGTextureAtlas = new BitmapTextureAtlas(Global.texture_manager, 90, 90, TextureOptions.BILINEAR);
		mItemCompletedBgTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mItemCompletedBGTextureAtlas, Global.main_activity, "item_bg_completed.png", 0, 0);
		mItemCompletedBGTextureAtlas.load();
		
		for(int i=0;i<3;i++)
			this.attachChild(new Entity());
		
		//Background
		bg = new Sprite(0, 0, mBgTextureRegion, (VertexBufferObjectManager) Global.vertex_buffer_object_manager);
		this.getChildByIndex(0).attachChild(bg);
		
		//Screens
		splash_screen = new Sprite(0,0, mSplashTextureRegion, (VertexBufferObjectManager) Global.vertex_buffer_object_manager);
		splash_screen.setVisible(true);
		this.getChildByIndex(2).attachChild(splash_screen);
		ending_bg = new Sprite(0,0, mBgEndingTextureRegion, (VertexBufferObjectManager) Global.vertex_buffer_object_manager);
		ending_bg.setVisible(false);
		this.getChildByIndex(2).attachChild(ending_bg);
		info_bg = new Sprite(0,0, mBgInfoTextureRegion, (VertexBufferObjectManager) Global.vertex_buffer_object_manager);
		info_bg.setVisible(false);
		this.getChildByIndex(2).attachChild(info_bg);
		
		//Items
		Font mFont = FontFactory.create(Global.main_activity.getFontManager(), Global.texture_manager, 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 70,Color.GREEN);
		mFont.load();
		
		for(int i=0;i<Global.AREAS_QUANTITY;i++)
		{
			if(i+1<10)
			{
				TextMenuItem ti=new TextMenuItem(i+1, mFont, "0"+(i+1), Global.vertex_buffer_object_manager);
				this.addMenuItem(ti);
			}else
			{
				TextMenuItem ti=new TextMenuItem(i+1, mFont, ""+(i+1), Global.vertex_buffer_object_manager);
				this.addMenuItem(ti);
			}
		}
		
		ending_button = new SpriteMenuItem(15, mEndingButtonTextureRegion, (VertexBufferObjectManager) Global.vertex_buffer_object_manager);
		ending_button .setVisible(false);
		this.addMenuItem(ending_button);
		ending_button.setPosition(100, 0);
		info_button = new SpriteMenuItem(16, mInfoButtonTextureRegion, (VertexBufferObjectManager) Global.vertex_buffer_object_manager);
		info_button.setVisible(false);
		this.addMenuItem(info_button);
		info_button.setPosition(0, 0);
		continue_button = new SpriteMenuItem(17, mContinueButtonTextureRegion, (VertexBufferObjectManager) Global.vertex_buffer_object_manager);
		this.addMenuItem(continue_button);

		this.buildAnimations();
	}
	
	@Override public void buildAnimations()
	{
		super.buildAnimations();

        float offset_x=50,offset_y=100;
        float current_x=0,current_y=0;
        float max_x=Global.SCREEN_WIDTH;
        float max_height=0;
        float separation_x=95,separation_y=95;
        for(int i = 0; i < Global.AREAS_QUANTITY; i++) {
            final IMenuItem menuItem = this.mMenuItems.get(i);

            menuItem.setPosition(current_x+offset_x, current_y+offset_y);
            menuItem.setVisible(false);
            
    		if(preferences.getBoolean("level "+(i+1)+" completed", true))
    		{
                Sprite item_bg= new Sprite(current_x+offset_x, current_y+offset_y, this.mItemCompletedBgTextureRegion, Global.vertex_buffer_object_manager);
                this.getChildByIndex(1).attachChild(item_bg);
    		}else
    		{
                Sprite item_bg= new Sprite(current_x+offset_x, current_y+offset_y, this.mItemBgTextureRegion, Global.vertex_buffer_object_manager);
                this.getChildByIndex(1).attachChild(item_bg);
    		}
            
            //current_x+=menuItem.getWidthScaled()+separation_x;
    		current_x+=separation_x;
            if(menuItem.getHeightScaled()>max_height)
            	max_height=menuItem.getHeightScaled();
            
            if(current_x+offset_x+menuItem.getWidthScaled()>=max_x)
            {
            	current_x=0;
            	//current_y+=max_height+separation_y;
            	current_y+=separation_y;
            	max_height=0;
            }
        }
        ending_button.setPosition(480,370);
        info_button.setPosition(560,370);
        continue_button.setPosition(640,370);
	};

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		// TODO Auto-generated method stub
		if(pMenuItem.getID()<=Global.AREAS_QUANTITY)
			Global.engine.setScene(new MapScene(pMenuItem.getID()));
		else if(pMenuItem.getID()==15 && pMenuItem.isVisible())
		{
			for(int i=0;i<Global.AREAS_QUANTITY;i++)
			{
				IMenuItem mi = mMenuItems.get(i);
				mi.setVisible(false);
			}
			info_button.setVisible(false);
			ending_button.setVisible(false);
			continue_button.setVisible(true);
			ending_bg.setVisible(true);
		}else if(pMenuItem.getID()==16 && pMenuItem.isVisible())
		{
			for(int i=0;i<Global.AREAS_QUANTITY;i++)
			{
				IMenuItem mi = mMenuItems.get(i);
				mi.setVisible(false);
			}
			info_button.setVisible(false);
			ending_button.setVisible(false);
			continue_button.setVisible(true);
			info_bg.setVisible(true);
		}else if(pMenuItem.getID()==17 && pMenuItem.isVisible())
		{
			for(int i=0;i<Global.AREAS_QUANTITY;i++)
			{
				IMenuItem mi = mMenuItems.get(i);
				mi.setVisible(true);
			}
			info_button.setVisible(true);
			if(gameComplete())
				ending_button.setVisible(true);
			continue_button.setVisible(false);
			ending_bg.setVisible(false);
			info_bg.setVisible(false);
			splash_screen.setVisible(false);
		}
		return false;
	}
	
	boolean gameComplete()
	{
        for(int i = 0; i < Global.AREAS_QUANTITY; i++)
        {
    		if(preferences.getBoolean("level "+(i+1)+" completed", true)==false)
    		{ 			
                return false;
    		}
        }
        return true;
	}
}
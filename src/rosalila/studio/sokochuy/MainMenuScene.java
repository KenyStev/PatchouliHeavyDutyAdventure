package rosalila.studio.sokochuy;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.Entity;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
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
		
		preferences = Global.main_activity.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);

		this.setOnMenuItemClickListener(this);
		this.setBackgroundEnabled(false);
		BitmapTextureAtlas mBitmapTextureAtlas = new BitmapTextureAtlas(Global.texture_manager, 720, 480, TextureOptions.BILINEAR);
		ITextureRegion mDatingSimTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, Global.main_activity, "main_background.png", 0, 0);
		mBitmapTextureAtlas.load();
		
		BitmapTextureAtlas mItemBGTextureAtlas = new BitmapTextureAtlas(Global.texture_manager, 90, 90, TextureOptions.BILINEAR);
		mItemBgTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mItemBGTextureAtlas, Global.main_activity, "item_bg.png", 0, 0);
		mItemBGTextureAtlas.load();
		
		BitmapTextureAtlas mItemCompletedBGTextureAtlas = new BitmapTextureAtlas(Global.texture_manager, 90, 90, TextureOptions.BILINEAR);
		mItemCompletedBgTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mItemCompletedBGTextureAtlas, Global.main_activity, "item_bg_completed.png", 0, 0);
		mItemCompletedBGTextureAtlas.load();
		
		for(int i=0;i<2;i++)
			this.attachChild(new Entity());
		

		bg= new Sprite(0, 0, mDatingSimTextureRegion, (VertexBufferObjectManager) Global.vertex_buffer_object_manager);
		this.getChildByIndex(0).attachChild(bg);
		
		Font mFont = FontFactory.create(Global.main_activity.getFontManager(), Global.texture_manager, 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 70,Color.GREEN);
		mFont.load();
		
		for(int i=0;i<15;i++)
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

		this.buildAnimations();

	}
	
	@Override public void buildAnimations()
	{
		super.buildAnimations();
		
        final int menuItemCount = this.mMenuItems.size();
        float offset_x=50,offset_y=100;
        float current_x=0,current_y=0;
        float max_x=Global.SCREEN_WIDTH;
        float max_height=0;
        float separation_x=95,separation_y=95;
        for(int i = 0; i < menuItemCount; i++) {
            final IMenuItem menuItem = this.mMenuItems.get(i);

            menuItem.setPosition(current_x+offset_x, current_y+offset_y);
            
    		if(preferences.getBoolean("level "+(i+1)+" completed", false))
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
	};

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		// TODO Auto-generated method stub
		Global.engine.setScene(new MapScene(pMenuItem.getID()));
		return false;
	}
}
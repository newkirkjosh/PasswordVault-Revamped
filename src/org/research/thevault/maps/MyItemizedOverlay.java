package org.research.thevault.maps;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class MyItemizedOverlay extends ItemizedOverlay{

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	private Paint mPaint;
	private Point p1, p2;
	private Path path;
	private Projection mProjection;
	
	public MyItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}
	
	public MyItemizedOverlay(Drawable defaultMarker, Context context, Projection projection) {
		  super(boundCenterBottom(defaultMarker));
		  mContext = context;
		  this.mProjection = projection;
		}
	
	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}
	
	@Override
	protected OverlayItem createItem(int i) {
	  return mOverlays.get(i);
	}
	
	@Override
	protected boolean onTap(int index) {
		OverlayItem item = mOverlays.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.show();
		return true;
	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapv, boolean shadow){
		
		if( !mOverlays.isEmpty() )
		{
			super.draw(canvas, mapv, shadow);
			
			// Settings for the line
			mPaint = new Paint();
			mPaint.setDither(true);
			mPaint.setColor(Color.RED);
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			mPaint.setStrokeJoin(Paint.Join.ROUND);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
			mPaint.setStrokeWidth(2);
		
			// Goes through starting from the first point, gets the next point, draws a line to the next one
			// Does this until it reaches the last point in the ArrayList
			// Note: GeoPoint uses integers and multiplies it by 1E6 (1 * 10^6), it is still getting their specific lat, long
			for(int i = 0; i < mOverlays.size() - 1; i++)
			{	
				OverlayItem l1 = (OverlayItem) mOverlays.get(i);
				OverlayItem l2 = (OverlayItem) mOverlays.get(i+1);
				
				p1 = new Point();
				p2 = new Point();
				path = new Path();
				
				mProjection.toPixels(l1.getPoint(), p1);
				mProjection.toPixels(l2.getPoint(), p2);
				
				path.moveTo(p2.x, p2.y);
				path.lineTo(p1.x, p1.y);
				
				canvas.drawPath(path, mPaint);
			}
		}
	}
}

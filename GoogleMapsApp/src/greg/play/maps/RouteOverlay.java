package greg.play.maps;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class RouteOverlay extends Overlay {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	GeoPoint prePoint=null, currentPoint=null;
	Paint paint=new Paint();
	
	public RouteOverlay(GeoPoint prePoint, GeoPoint currentPoint) {
		this.currentPoint=currentPoint;
	    this.prePoint = prePoint;
	}

	@Override
    public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
        Projection projection = mapView.getProjection();
        if (shadow == false) {

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            Point point = new Point();
            projection.toPixels(prePoint, point);
            paint.setColor(Color.BLUE);
            Point point2 = new Point();
            projection.toPixels(currentPoint, point2);
            paint.setStrokeWidth(2);
            canvas.drawLine((float) point.x, (float) point.y, (float) point2.x,(float) point2.y, paint);
        }
        return super.draw(canvas, mapView, shadow, when);
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        // TODO Auto-generated method stub

        super.draw(canvas, mapView, shadow);
    }
}

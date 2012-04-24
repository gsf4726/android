package greg.play.maps;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class RouteOverlay extends Overlay {

	List<GeoPoint> wayPoints;
	Paint paint=new Paint();
	
	public RouteOverlay(GeoPoint prePoint, GeoPoint currentPoint) {
	    this.wayPoints = new ArrayList<GeoPoint>();
	    addWayPoint(prePoint);
	    addWayPoint(currentPoint);
	}
	
	public RouteOverlay() {
	    this.wayPoints = new ArrayList<GeoPoint>();
	}

	public void addWayPoint(GeoPoint newPoint)
	{
		wayPoints.add(newPoint);
	}

	@Override
    public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
        Projection projection = mapView.getProjection();
        if (shadow == false) {

        	if (wayPoints.size() >= 2)
        	{        		
	            Paint paint = new Paint();
	            paint.setAntiAlias(true);
	            paint.setStyle(Style.STROKE);
	            paint.setColor(Color.BLUE);
	            paint.setStrokeWidth(5);
	            
	            Path path = new Path();
	            Point wayPoint = new Point();
	            projection.toPixels(wayPoints.get(0), wayPoint);
	            path.moveTo(wayPoint.x, wayPoint.y);
	            for(int i = 1; i < wayPoints.size(); i++)
	            {
	            	projection.toPixels(wayPoints.get(i), wayPoint);
	            	path.lineTo(wayPoint.x, wayPoint.y);
	            }
	            
	            canvas.drawPath(path, paint);
        	}
        }
        return super.draw(canvas, mapView, shadow, when);
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        // TODO Auto-generated method stub
        super.draw(canvas, mapView, shadow);
    }
}

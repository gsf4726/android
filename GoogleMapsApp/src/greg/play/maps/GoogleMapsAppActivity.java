package greg.play.maps;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

public class GoogleMapsAppActivity extends MapActivity {
    
	private LocationManager myLocationManager;
	private LocationListener myLocationListener;
	private TextView myLongitude, myLatitude, distanceTravelled;
	private MapView myMapView;
	private MapController myMapController;
	private Location previousLocation;
	private float totalDistanceInMeters;
	
	private void CenterLocation(GeoPoint centerGeoPoint)
	{
		myMapController.animateTo(centerGeoPoint);
  		myLongitude.setText("Longitude: "+ String.valueOf((float)centerGeoPoint.getLongitudeE6()/1000000));
		myLatitude.setText("Latitude: "+ String.valueOf((float)centerGeoPoint.getLatitudeE6()/1000000));
	};
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        myMapView = (MapView)findViewById(R.id.mapview);
        myLongitude = (TextView)findViewById(R.id.longitude);
        myLatitude = (TextView)findViewById(R.id.latitude);
        distanceTravelled = (TextView)findViewById(R.id.distanceTravelled);
        myMapView.setSatellite(true); //Set satellite view
        myMapController = myMapView.getController();
        myMapController.setZoom(20); //Fixed Zoom Level

        myLocationManager = (LocationManager)getSystemService(
          Context.LOCATION_SERVICE);

        myLocationListener = new MyLocationListener();
        myLocationManager.requestLocationUpdates(
        	    LocationManager.GPS_PROVIDER,
        	    0,
        	    0,
        	    myLocationListener);
        	  
/*        //Get the current location in start-up
        GeoPoint initGeoPoint = new GeoPoint(
        		(int)(myLocationManager.getLastKnownLocation(
        	    LocationManager.GPS_PROVIDER)
        	    .getLatitude()*1000000),
        	   (int)(myLocationManager.getLastKnownLocation(
        	    LocationManager.GPS_PROVIDER)
        	    .getLongitude()*1000000));
      
        CenterLocation(initGeoPoint);        
*/
        previousLocation = null;
        totalDistanceInMeters = 0;
        myMapView.setBuiltInZoomControls(true);
    }
    
    @Override
    protected void onStop() {
    	myLocationManager.removeUpdates(myLocationListener);
        super.onStop();
    }
	
    private class MyLocationListener implements LocationListener{
    	
    	public void onLocationChanged(Location currentLocation) {
    		float distanceMeters = 0;
    		if (previousLocation != null)
    		{
    			distanceMeters = previousLocation.distanceTo(currentLocation);    		
    		}
    		else
    		{
    			previousLocation = currentLocation;
    		}
    		
    		if (distanceMeters >= 2)
    		{
				// Get geo points to update current location and draw the route tracer
    			GeoPoint currentGeoPoint = new GeoPoint(
	    				(int)(currentLocation.getLatitude()*1000000),
	    				(int)(currentLocation.getLongitude()*1000000));
	    		GeoPoint previousGeoPoint = new GeoPoint(
	    				(int)(previousLocation.getLatitude()*1000000),
	    				(int)(previousLocation.getLongitude()*1000000));
	    		CenterLocation(currentGeoPoint);
	    		myMapView.getOverlays().add(new RouteOverlay(previousGeoPoint, currentGeoPoint));

	    		// Update distance text
	   			totalDistanceInMeters  += distanceMeters;
	    		distanceTravelled.setText("Meters Travelled: "+ String.valueOf((float)totalDistanceInMeters));
	    		
	    		// Store current as previous for next update
	    		previousLocation = currentLocation;
    		}
    	}

    	public void onProviderDisabled(String provider) {
    		// TODO Auto-generated method stub
    	}

    	public void onProviderEnabled(String provider) {
    		// TODO Auto-generated method stub
    	}

    	public void onStatusChanged(String provider,
    	    int status, Bundle extras) {
    		// TODO Auto-generated method stub
    	}
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}
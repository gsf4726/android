package greg.play.maps;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GoogleMapsAppActivity extends MapActivity{
    
	private static final String FILENAME = "greg_play_map_lastroute";
	private LocationManager locationManager;
	private LocationListener locationListener;
	private TextView textLongitude, textLatitude, textDistanceTravelled;
	private MapView mapView;
	private MapController mapController;
	private Location previousLocation;
	private float totalDistanceInMeters;
	private Boolean isTracking;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        isTracking = false;
        mapView = (MapView)findViewById(R.id.mapview);
        textLongitude = (TextView)findViewById(R.id.longitude);
        textLatitude = (TextView)findViewById(R.id.latitude);
        textDistanceTravelled = (TextView)findViewById(R.id.distanceTravelled);
        mapView.setSatellite(true); //Set satellite view
        mapController = mapView.getController();
        mapController.setZoom(20); //Fixed Zoom Level

        locationManager = (LocationManager)getSystemService(
          Context.LOCATION_SERVICE);

        locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(
        	    LocationManager.GPS_PROVIDER,
        	    0,
        	    0,
        	    locationListener);
        	  
        previousLocation = null;
        totalDistanceInMeters = 0;
        mapView.setBuiltInZoomControls(true);
    }
	
	public void resetRoute(View view)
	{
		mapView.getOverlays().clear();
		previousLocation = null;
		totalDistanceInMeters = 0;
		mapView.invalidate();
	}

	public void toggleTrack(View view)
	{
		Button button = (Button)view;
		String text = button.getText().toString();
		if (text.contains("Start Route"))
		{
			isTracking = true;
			button.setText("End Route");
		}
		else
		{
			isTracking = false;
			button.setText("Start Route");
		}
	}
	
	public void openRoute(View view)
	{
		FileInputStream fis = null;
		StringBuffer contentsString = null;
		try 
		{
			fis = openFileInput(FILENAME);
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		
		try
		{
			if (fis != null)
			{
				contentsString = new StringBuffer("");

				byte[] buffer = new byte[1024];
				while (fis.read(buffer) != -1) {
					contentsString.append(new String(buffer));
				}
			}
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			if (fis != null)
			{
				fis.close();	
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		if (contentsString.length() > 0)
		{
    		RouteOverlay routeOverlay = null;
    		if ((mapView.getOverlays().size()) == 0)
    		{
    			routeOverlay = new RouteOverlay();
    			mapView.getOverlays().add(routeOverlay);
    		}
    		else
    		{
    			routeOverlay = (RouteOverlay)mapView.getOverlays().get(0);
    		}
			
			String[] geoPoints = contentsString.toString().split(",");
			for (String geoPoint : geoPoints)
			{
				if (geoPoint != null)
				{
					GeoPoint gP = null;
					String []latLong = geoPoint.split(":");
					if (latLong.length == 2)
					{					
						gP = new GeoPoint(Integer.parseInt(latLong[0]), Integer.parseInt(latLong[1]));
		    			routeOverlay.addWayPoint(gP);
		    			CenterLocation(gP);
					}
				}
			}
			
			mapView.invalidate();
			if (isTracking)
			{
				toggleTrack((Button)findViewById(R.id.toggleTrack));
			}
		}
	}
	
	public void saveRoute(View view)
	{
		if (mapView.getOverlays().size() > 0)
		{
			RouteOverlay routeOverlay = (RouteOverlay)mapView.getOverlays().get(0);
			
			String contentsString = "";
			
			for (GeoPoint gP : routeOverlay.wayPoints)
			{
				contentsString += gP.getLatitudeE6() + ":" + gP.getLongitudeE6() + ",";		
			}
			
			FileOutputStream fos = null;
			try 
			{
				fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
			} 
			catch (FileNotFoundException e1) 
			{
				e1.printStackTrace();
			}
			
			try
			{
				if (fos != null)
				{
					fos.write(contentsString.getBytes());
				}
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			try
			{
				if (fos != null)
				{
					fos.close();	
				}
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}	
	}
	
    @Override
    protected void onStop() {
    	locationManager.removeUpdates(locationListener);
        super.onStop();
    }
	
	private void CenterLocation(GeoPoint centerGeoPoint)
	{
		mapController.animateTo(centerGeoPoint);
  		textLongitude.setText("Longitude: "+ String.valueOf((float)centerGeoPoint.getLongitudeE6()/1000000));
		textLatitude.setText("Latitude: "+ String.valueOf((float)centerGeoPoint.getLatitudeE6()/1000000));
	}
	
    private class MyLocationListener implements LocationListener{
    	
    	public void onLocationChanged(Location currentLocation) {
    		if (isTracking)
    		{
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
					// Get GeoPoints to update current location and draw the route tracer
	    			GeoPoint currentGeoPoint = new GeoPoint(
		    				(int)(currentLocation.getLatitude()*1000000),
		    				(int)(currentLocation.getLongitude()*1000000));
		    		GeoPoint previousGeoPoint = new GeoPoint(
		    				(int)(previousLocation.getLatitude()*1000000),
		    				(int)(previousLocation.getLongitude()*1000000));
		    		CenterLocation(currentGeoPoint);
		    		
		    		RouteOverlay routeOverlay = null;
		    		if (mapView.getOverlays().size() == 0)
		    		{
		    			routeOverlay = new RouteOverlay(previousGeoPoint, currentGeoPoint);
		    			mapView.getOverlays().add(routeOverlay);
		    		}
		    		else
		    		{
		    			routeOverlay = (RouteOverlay)mapView.getOverlays().get(0);
		    			routeOverlay.addWayPoint(currentGeoPoint);
		    			mapView.invalidate();
		    		}
		    		
		    		// Update distance text
		   			totalDistanceInMeters  += distanceMeters;
		    		textDistanceTravelled.setText("Meters Travelled: "+ String.valueOf((float)totalDistanceInMeters));
		    		
		    		// Store current as previous for next update
		    		previousLocation = currentLocation;
	    		}
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
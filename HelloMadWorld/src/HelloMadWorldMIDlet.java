import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.midlet.MIDlet;
import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;

public class HelloMadWorldMIDlet extends MIDlet implements 
			CommandListener 
//			,LocationListener 
															{
	
	//COMMAND
	static final Command EXIT_CMD =  new Command("Exit", Command.EXIT, 0);
    static final Command BACK_CMD =  new Command("Back", Command.BACK, 1);
    static final Command OK_CMD =  new Command("Ok", Command.OK, 1);
    static final Command STORE_CMD =  new Command("Store", Command.OK, 1);
    static final Command VERIFY_CMD =  new Command("Verify", Command.OK, 1);
    static final Command VIEW_CMD =  new Command("View", Command.OK, 1);
    
    //FORM ELEMENT
	private Form mainForm, selectComicsForm, selectCoordinatesForm, presentationForm; 
	private ChoiceGroup comics; 
	private int currentIndex; 
	private int comicsIndex; 
	private String choicedComics;

	//LOCATION [JSR 179]
	private double tempLat, tempLon;
	private String lat,lon;
	private Criteria criteria;
	private LocationProvider locationProvider = null;
	private Location location = null;

	//MEDIA [JSR 135]
	private Player p;
//	private VolumeControl vc = null;
    
    public HelloMadWorldMIDlet() {
    	initMainScreen();
        initSelectComicsScreen();
        initSelectCoordinatesScreen();
        initPresentationScreen();
    }
    
    private void switchCurrentScreen(Displayable displayable) {
        Display.getDisplay(this).setCurrent(displayable);
    }

    private void initMainScreen() {
        mainForm = new Form("Comics, and their Voices ");
        mainForm.append(new StringItem("", "Select Comics sound to be played at home "));
        mainForm.addCommand(OK_CMD);
        mainForm.addCommand(EXIT_CMD);
        mainForm.setCommandListener(this);
    }
    
    private void initSelectComicsScreen() {
    	selectComicsForm = new Form("Voices ");    	
    	comics = new ChoiceGroup("Enter Your Choice... ", Choice.EXCLUSIVE);
        comics.append("Homer ", null);
        currentIndex = comics.append("Bunny ", null);
        comics.setSelectedIndex(currentIndex, true);        
        comicsIndex = selectComicsForm.append(comics);
        
        selectComicsForm.addCommand(BACK_CMD);
        selectComicsForm.addCommand(EXIT_CMD);
        selectComicsForm.addCommand(STORE_CMD);
        selectComicsForm.setCommandListener(this);
    }
    
    private void initSelectCoordinatesScreen() {
    	selectCoordinatesForm = new Form("Here is my home ");
    	selectCoordinatesForm.addCommand(BACK_CMD);
    	selectCoordinatesForm.addCommand(EXIT_CMD);
    	selectCoordinatesForm.addCommand(STORE_CMD);
    	selectCoordinatesForm.addCommand(VIEW_CMD);
    	selectCoordinatesForm.setCommandListener(this);
    	
    	criteria = new Criteria();
    	criteria.setHorizontalAccuracy(500);
    	criteria.setVerticalAccuracy(500);
    	criteria.setAltitudeRequired(false);
    	criteria.setPreferredPowerConsumption(Criteria.POWER_USAGE_LOW);
    }
    
    private void initPresentationScreen() {
    	presentationForm = new Form("Waiting for Home... ");
    	presentationForm.addCommand(EXIT_CMD);
    	presentationForm.addCommand(VERIFY_CMD);
    	presentationForm.setCommandListener(this);
    }
    
    public void startApp() {
        switchCurrentScreen(mainForm);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

	public void commandAction(Command command, Displayable d) {
		if (command == EXIT_CMD)
	    {
	          destroyApp(false);
	          notifyDestroyed();
	    }	    
		else if (command == OK_CMD)					//Only in Main Form 
	    {
	    	switchCurrentScreen(selectComicsForm);
	    }
		else if (command == BACK_CMD)
	    {
			if(d==selectComicsForm){
				this.choicedComics = "";
				switchCurrentScreen(mainForm);
			}
			else if(d==selectCoordinatesForm){
				this.lat="";
				this.lon="";
				switchCurrentScreen(selectComicsForm);
			}
	    }
	    
	    else if (command == STORE_CMD) 				//IN Comics and Coordinates Form 
	    {
	    	if (d==selectComicsForm){
	    		choicedComics= comics.getString(comics.getSelectedIndex()).trim();
		    	switchCurrentScreen(selectCoordinatesForm);
	    	}
	    	else if (d==selectCoordinatesForm){	    		
	    		presentationForm.append(new StringItem("", "Latitude: "+lat+"; \nLongitude: "+
						lon+"; \nVoice: "+choicedComics+". \n"));
		    	switchCurrentScreen(presentationForm);
	    	}
	    }
	    else if (command == VIEW_CMD){				//Only Coordinates Form
	    	Hashtable coordinates = new Hashtable();
			coordinates=quickCoordinates(criteria);
    		lat = (String) coordinates.get("Latitude");
    		lon = (String) coordinates.get("Longitude");
	    	//ho usato append invece di delete per veder le variazioni delle coordinate
	    	selectCoordinatesForm.append(new StringItem("", "\n\n"+lat+"\n"+lon));
	    }
	    else if (command == VERIFY_CMD){ 			//Only Presentation Form
			Hashtable coordinates = new Hashtable();
			coordinates=quickCoordinates(criteria);
			String tempLatitude = (String)coordinates.get("Latitude");
			String tempLongitude = (String)coordinates.get("Longitude");
    		
    		if(lat.equals(tempLatitude) && lon.equals(tempLongitude)){
				startPlayer(p, choicedComics);
//				startPlayer(p, "Homer");
    			presentationForm.append("Well Done, you are at home");
            	presentationForm.removeCommand(VERIFY_CMD);
    		}
    		else
//    			presentationForm.append(new StringItem("", coordinates.get("Latitude") +"+++"+ coordinates.get("Longitude")));
    			presentationForm.append(new StringItem("", tempLatitude +"+++"+ tempLongitude));
	    }
    }
	
	private void startPlayer (Player player, String type){
    	if (type.equals("Homer")){
            InputStream inHomer = getClass().getResourceAsStream("/homer_thatisanicedonut.wav");
			try {
				player = Manager.createPlayer(inHomer, "audio/x-wav");
	    		player.start();
			} 
	    	catch (IOException e) {e.printStackTrace();}
	    	catch (MediaException e) {e.printStackTrace();}
    	}
    	else if (type.equals("Bunny")){
    		InputStream inBunny = getClass().getResourceAsStream("/bugs_goodbye.wav");
    		try {
				player = Manager.createPlayer(inBunny, "audio/x-wav");
	    		player.start();
			} 
	    	catch (IOException e) {e.printStackTrace();}
	    	catch (MediaException e) {e.printStackTrace();}
    	}
	}
	
	//per un basso numero di elementi predefinito potrei usare array, ma da problemi in debug
	//aggiorna lo stato della classe (tempLat & tempLon)
	//mi restituisce i paramtri in un hashtable (string)
	//distruggo-ricreo location provider per risparmiare risorse...
	private Hashtable quickCoordinates (Criteria criteriaParam){
		Hashtable res = new Hashtable();
		QualifiedCoordinates qc=null;
		try{
    		locationProvider = LocationProvider.getInstance(criteriaParam);
    	} 
    	catch (Exception e){
    		System.out.println(e.getMessage());
			e.printStackTrace();	
    	}
		
		try {
			System.out.println("stato provider = "+locationProvider.getState()); 				//TEST
			location =locationProvider.getLocation(60);
		} catch (LocationException e) {
			System.out.println("Problems with location provider ! " + e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
    	qc=location.getQualifiedCoordinates();
    	tempLat = qc.getLatitude();
    	tempLon = qc.getLongitude();
    	res.put("Latitude", Double.toString(tempLat));
    	res.put("Longitude", Double.toString(tempLon));
    	//risorse...
    	locationProvider = null;
		return res;
	}
}


//public void locationUpdated(LocationProvider provider, Location location) {
//if (location != null && location.isValid()) {
//    //get the coordinates
//    Coordinates coordinates = location.getQualifiedCoordinates();
//
//    if (coordinates != null) {
//        //get the latitude and longitude of the coordinates.
//        tempLat = coordinates.getLatitude();
//        tempLon = coordinates.getLongitude();
//        System.out.println("\nNew Coord = "+tempLat + "\t" + tempLon + ". \n");
//        if(tempLat==lat && tempLon==lon){
//        	startPlayer(p, choicedComics);
//        	
//        	Alert alert = new Alert("DONE", "The MIDlet has finisched !",null, AlertType.INFO);
//        	alert.setTimeout(Alert.FOREVER);
//        	Display.getDisplay(this).setCurrent(alert);
//        	presentationForm.append("Well Done, you are at home");
//        	presentationForm.removeCommand(VERIFY_CMD);
//        	locationProvider.setLocationListener(this, 0, 0, 0);
//        }
//    }
//    else { /*no valid coordinates*/}
//}		
//}
//
//public void providerStateChanged(LocationProvider provider, int newState) {
//if (newState == LocationProvider.OUT_OF_SERVICE || newState == LocationProvider.TEMPORARILY_UNAVAILABLE) {
//    System.out.println("GPS inactive");
//    selectCoordinatesForm.append(new StringItem("", "GPS Inactive"));
//}		
//}
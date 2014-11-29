ENVIRONMENT
1. iscrizione a sito nokia come sviluppatore
2. Java SE (jdk)
3. download & install nokia asha 
4. launch Nokia IDE for Java ME (Eclipse) v3.1
5. installare particolare versione di SDK per target devices' configuration (una serie di)
6. nokia IDE si collega da solo a emulator (anche per debug, porta locale):
	a) altrimenti dal menù si può aprire una MIDlet (Jar-Jad) debugable
7.
8.

BIBLIO
1) [Getting Start] http://developer.nokia.com/resources/library/Java/getting-started/creating-your-first-midlet-in-nokia-ide-for-java-me.html
2) [Multimedia MIDlet] https://today.java.net/article/2005/09/22/j2me-tutorial-part-4-multimedia-and-midp-20
3) [Location MIDlet] http://developer.nokia.com/community/wiki/Best_practises_for_listening_to_location_updates_with_Java_ME
4) [Emulator] http://docs.oracle.com/javame/8.1/sdk-dev-guide/emulator.htm#sthref58

CODE
1) HelloMadWorldMIDlet extends MIDlet implements CommandListener,LocationListener
2) ci sono public void startApp-pauseApp-destroyApp;
3) Form mainForm = new Form("Title.. ");
4) Command EXIT_CMD =  new Command("Exit",Command.EXIT, 0);
5) form.append(new StringItem("", "Text...")); form.addCommand(EXIT_CMD);
6) form.setCommandListener(this);
7) Display.getDisplay(this).setCurrent(form);
8) Location [JSR 179]
	a. criteria = new Criteria();
	b. criteria.setHorizontalAccuracy(500); criteria.setVerticalAccuracy(500);
	c. criteria.setPreferredPowerConsumption(Criteria.POWER_USAGE_LOW); 
	d. locationProvider = LocationProvider.getInstance(criteriaParam);
	e. QualifiedCoordinates qc = location.getQualifiedCoordinates();
	f. double lat = qc.getLatitude(); double lon = qc.getLongitude();
9) Media [JSR 135]
	a. private Player player;
	b. InputStream in= getClass().getResourceAsStream("*.wav");
	c. player = Manager.createPlayer(in, "audio/x-wav");

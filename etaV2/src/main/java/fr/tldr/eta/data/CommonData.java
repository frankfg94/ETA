package fr.tldr.eta.data;

public class CommonData {
	private static final String sysName = System.getProperty("os.name");
	
	
	public static String getSysName() {
		return sysName;
	}
}

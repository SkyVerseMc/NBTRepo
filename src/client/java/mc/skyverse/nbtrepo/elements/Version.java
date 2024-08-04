package mc.skyverse.nbtrepo.elements;

public class Version {

	final String version;
	
	public Version(String name) {
		
		version = name;
	}
	
	public String get() {
		
		return version;
	}
	
	public boolean isRange() {
		
		return version.contains("-");
	}
	
	public boolean isInRange(Version version) {
		
		String[] v = this.get().split("-");
		
		String min = build(v[0]);
		String max = build(v[1]);
		String ver = build(version.get());
		
		for (int i = 0; i < ver.length(); i++) {
			
			char cmin = min.charAt(i);
			char cv = ver.charAt(i);
			
			if (isNumeric(cmin)) {
				
				if (cv > cmin) break;
				if (cmin > cv) return false;
		
			} else {
				
				if (cmin != '*') return false;
			}
		}
		
		for (int i = 0; i < ver.length(); i++) {
			
			char cmax = max.charAt(i);
			char cv = ver.charAt(i);
			
			if (isNumeric(cmax)) {
				
				if (cv < cmax) return true;
				if (cmax < cv) return false;
		
			} else {
				
				if (cmax != '*') return false;
			}
		}
		
		return true;
	}
	
	private String build(String string) {
		
		String v = "";
		for (String s : string.split("\\.")) {
			
			if (s.length() < 2) {
				s = 0 + s;
			}
			v += s;
		}
		return v;
	}
	
	private boolean isNumeric(char c) {
		
		return c >= 0x30 && c <= 0x39;
	}
}

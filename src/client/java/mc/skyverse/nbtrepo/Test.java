package mc.skyverse.nbtrepo;

import mc.skyverse.nbtrepo.elements.Version;

public class Test {

	public static void main(String[] args) {
		
        Version v = new Version("1.19.*-1.20.4");
		boolean g = v.isInRange(new Version("1.20"));
		System.out.println(g);
	}
}

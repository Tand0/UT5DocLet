package jp.ruru.park.ando.ut5;


import java.util.*;

/**
 * JavaDoc group class
 */
public class UT5Group {
	/**
	 * constructor
	 * @param name group name
	 */
	 public UT5Group(String name) {
		 this.name = name;
	 }
    
	/**
	 * get name
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * get class list
	 * @return class list
	 */
	public Set<UT5Package> getPackageSet() {
		return packageSet;
	}
	
	/**
	 * find package
	 * @param name package name
	 * @return class list
	 */
	public UT5Package findPacakge(String name) {
		for (UT5Package myPackage : this.packageSet) {
			if (name.equals(myPackage.getName())) {
				return myPackage;
			}
		}
		return null;
	}
	/** name */
	private final String name;
	
	
	/** class list */
	public final Set<UT5Package> packageSet = new TreeSet<UT5Package>();
} 
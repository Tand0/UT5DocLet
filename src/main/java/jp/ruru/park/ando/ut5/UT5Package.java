package jp.ruru.park.ando.ut5;

import java.util.Set;
import java.util.TreeSet;

/**
 * PackageDoc for JUnit.
 * @author t-ando
 *
 */
public class UT5Package implements Comparable<UT5Package> {
	
	/**
	 * constructor
	 * @param name package name
	 * @param message package message
	 */
	public UT5Package(String name,String message) {
		this.name = name;
		this.message = message;
	}
	/**
	 * get name
	 * @return name
	 */
	public String getName() {
		return name;
	}
	/**
	 * get message
	 * @return message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * get class list
	 * @return class list
	 */
	public Set<UT5Class> getClassSet() {
		return classSet;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(UT5Package o) {
		boolean fromFlag = this.getName().indexOf(UT5GroupFactory.FINDBUGS_PACK) == 0;
		boolean toFlag = o.getName().indexOf(UT5GroupFactory.FINDBUGS_PACK) == 0;
		if (fromFlag != toFlag) {
			if (fromFlag) {
				return +1;
			} else {
				return -1;
			}
		}
		return this.getName().compareTo(o.getName());
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof UT5Package) {
			return this.getName().compareTo(((UT5Package)o).getName())== 0;
		}
		return super.equals(o);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		  return this.getName().hashCode();
	}
	
	/** name */
	private final String name;
	
	/** message */
	private final String message;

	/** class list */
	public final Set<UT5Class> classSet = new TreeSet<UT5Class>();

}

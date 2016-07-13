package jp.ruru.park.ando.ut5;


import com.sun.javadoc.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JavaDoc group class factory.
 */
public class UT5GroupFactory {
	/** find bugs string */
	public static final String FINDBUGS_PACK = "findbugs.";

	/**
	 * constructor
	 */
	 public UT5GroupFactory() {
	 }

    /**
     * Since we need to sort the keys in the reverse order(longest key first),
     * the compare method in the implementing class is doing the reverse
     * comparison.
     */
    private Comparator<String> mapKeyComparator = new Comparator<String>() {
        public int compare(String key1, String key2) {
            return key2.length() - key1.length();
        }
    };
     
    /**       
     * Depending upon the format of the package name provided in the "-group"
     * option, generate two separate maps. There will be a map for mapping  
     * regular expression(only meta character allowed is '*' and that is at the
     * end of the regular expression) on to the group name. And another map
     * for mapping (possible) package names(if the name format doesen't contain
     * meta character '*', then it is assumed to be a package name) on to the 
     * group name. This will also sort all the regular expressions found in the 
     * reverse order of their lengths, i.e. longest regular expression will be
     * first in the sorted list.
     *
     * @param groupname       The name of the group from -group option.
     * @param pkgNameFormList List of the package name formats.
     * @param reporter        Error reporter object.
     * @return true is success , false is failer
     */
    public boolean checkPackageGroups(
    		String groupname, 
    		String pkgNameFormList,
    		DocErrorReporter reporter) {
        StringTokenizer strtok = new StringTokenizer(pkgNameFormList, ":");
        if (groupList.stream().anyMatch(group->group.getName().equals(groupname))) {
            reporter.printError("Groupname already used name=" + groupname);
            return false;
        }
        UT5Group group = new UT5Group(groupname);
        groupList.add(group);
        while (strtok.hasMoreTokens()) {
            String id = strtok.nextToken();
            if (id.length() == 0) {
                reporter.printError("Error in packagelist group=" + groupname + " package=" + pkgNameFormList);
                return false;
            }
            if (id.endsWith("*")) {
                id = id.substring(0, id.length() - 1);
                if (this.foundGroupFormat(regExpGroupMap, id, reporter)) {
                    return false;
                } 
                regExpGroupMap.put(id, group);
                sortedRegExpList.add(id);
            } else {
                if (this.foundGroupFormat(pkgNameGroupMap, id, reporter)) {
                    return false;
                } 
                pkgNameGroupMap.put(id, group);
            }
        }
        Collections.sort(sortedRegExpList,mapKeyComparator);
        return true;
    }

    /**
     * Search if the given map has given the package format.
     *
     * @return true if package name format found in the map, else false.
     * @param map Map to be searched.
     * @param pkgFormat The pacakge format to search.
     * @param reporter Error reporter object.
     */
    private boolean foundGroupFormat(
    		Map<String,UT5Group> map, String pkgFormat,
    		DocErrorReporter reporter) {
        if (map.containsKey(pkgFormat)) { 
            reporter.printError("Same package name used pkg="+pkgFormat);
            return true;
        }
        return false;
    }

    /**
     *  create group and package data
     * @param rootDoc rootDoc for JavaDoc.
     * @param allClassList ClassDoc for JUnit.
     */
    public void groupPackages(RootDoc rootDoc,List<UT5Class> allClassList) {
    	UT5Group defaultGroup = 
				new UT5Group((pkgNameGroupMap.isEmpty() && regExpGroupMap.isEmpty())? 
						"Packages" : "Other Packages");
		//
		for (UT5Class toClass : allClassList) {
			//
			String packageNmae = toClass.getPackageName();
			//
    		UT5Group group = pkgNameGroupMap.get(packageNmae);
    		// if this package is not explictly assigned to a group,
    		// try matching it to group specified by regular expression 
    		if (group == null) {
    			group = this.regExpGroupName(packageNmae);
    		}
    		// if it is in neither group map, put it in the default
    		// group
    		if (group == null) {
        		if (! groupList.contains(defaultGroup)) {
        			groupList.add(defaultGroup);
        		}
    			group = defaultGroup;
    		}
			//
			boolean flag =  0 == packageNmae.indexOf(FINDBUGS_PACK);
			UT5Package myPackage = group.findPacakge(packageNmae);
			if (myPackage == null) {
				String realPackageName = flag
						? packageNmae.substring(FINDBUGS_PACK.length())
								: packageNmae;
				PackageDoc packageDoc = rootDoc.packageNamed(realPackageName);
				String packMessage;
				if (packageDoc != null) {
					packMessage = UT5.createMultiMessage(packageDoc.commentText());
				} else {
					packMessage = "";
				}
				myPackage = new UT5Package(packageNmae,packMessage);
				group.getPackageSet().add(myPackage);				
			}
			//
			//
			List<UT5Class> fromTargetClasList =
					myPackage.getClassSet().stream()
					.filter(target->target.getName().equals(toClass.getName()))
					.collect(Collectors.toList());
			if (fromTargetClasList.isEmpty()) {
				myPackage.getClassSet().add(toClass);
			} else {
				myPackage.getClassSet().removeAll(fromTargetClasList);
				//
				for (UT5Class fromClass : fromTargetClasList) {
					UT5Class newClass;
					UT5Class oldClass;
					if (toClass.getRawTimestamp() < fromClass.getRawTimestamp()) {
						newClass = fromClass;
						oldClass = toClass;
					} else {
						newClass = toClass;
						oldClass = fromClass;
					}
					//
					newClass.updateOldClass(oldClass);
					//
					myPackage.getClassSet().add(newClass);
				}
			}
		}

    }
     
    /**
     * Search for package name in the sorted regular expression 
     * list, if found return the group name.  If not, return null.
     * 
     * @param pkgName
     *         Name of package to be found in the regular expression list.
     * @return package group.
     */
    private UT5Group regExpGroupName(String pkgName) { 
        for (int j = 0; j < sortedRegExpList.size(); j++) {
            String regexp = sortedRegExpList.get(j); 
            if (pkgName.startsWith(regexp)) {
                return regExpGroupMap.get(regexp);
            } 
        }
        return null;
    }

    
    /**
     * get group list
     * @return group list
     */
    public List<UT5Group> getGroupList() {
    	return this.groupList;
    }

    /**
     * Map of regular expressions with the corresponding group name.
     */
    private final Map<String,UT5Group> regExpGroupMap = new HashMap<String,UT5Group>();

    /**
     * Map of non-regular expressions(possible package names) with the 
     * corresponding group name.
     */
    private final Map<String,UT5Group> pkgNameGroupMap = new HashMap<String,UT5Group>();

    /**
     * List of regular expressions sorted according to the length. Regular 
     * expression with longest length will be first in the sorted order.
     */
    private final List<String> sortedRegExpList = new ArrayList<String>();

    /**
     * List of group names in the same order as given on the command line.
     */
    private final List<UT5Group> groupList = new ArrayList<UT5Group>();

   
    /**
     * get find bugs time stamp
     * @return find bugs time stamp
     */
    public long getFindbugsTimestamp() {
		return findbugsTimestamp;
	}

    /** 
     * set find bugs time stamp
     * @param findbugsTimestamp find bugs time stamp
     */
	public void setFindbugsTimestamp(long findbugsTimestamp) {
		this.findbugsTimestamp = findbugsTimestamp;
	}

	/**
	 * get find bugs old time stamp
	 * @return find bugs old time stamp
	 */
	public long getFindbugsOldTimestamp() {
		return findbugsOldTimestamp;
	}

	/**
	 * set find bugs old time stamp
	 * @param findbugsOldTimestamp find bugs old time stamp
	 */
	public void setFindbugsOldTimestamp(long findbugsOldTimestamp) {
		this.findbugsOldTimestamp = findbugsOldTimestamp;
	}

	/** find bugs time stamp */
    private long findbugsTimestamp = (new Date()).getTime();
    
    /** find bugs old time stamp */
    private long findbugsOldTimestamp = Long.MAX_VALUE;
} 
package jp.ruru.park.ando.ut5;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.javadoc.*;


/**
 * DOCLET: JUnit report include JavaDoc
 * @author t-ando
 *
 */
public class UT5Doclet {
	
	/** doclet */
	private static final String ST_DOCLET = "-doclet";
	
	/** doclet path */
	private static final String ST_DOCLET_PATH = "-docletpath";
	
	/** -d */
	private static final String ST_D = "-d";
	
	/** encoding */
	private static final String ST_ENCODING = "-encoding";
	
	/** doc encoding */
	private static final String ST_DOCENCODING = "-docencoding";
	
	/** char set*/
	private static final String ST_CHARSET = "-charset";
	
	/** doc title */
	private static final String ST_DOCTITLE = "-doctitle";
	
	/** bottom */
	private static final String ST_BUTTOM = "-bottom";
	
	/** classpath*/
	private static final String ST_CLASSPATH = "-classpath";
	
	/** sourcepath*/
	private static final String ST_SOURCEPATH = "-sourcepath";

	/** source */
	private static final String ST_SOURCE = "-source";
	
	/** link */
	private static final String ST_LINK =  "-link";
	
	/** TEST-*.xml location */
	private static final String ST_JUNIT =  "-junit";
	
	/** bcel-fb.xml location */
	private static final String ST_FINDBUGS =  "-findbugs";
	
	/** tree paramater */
	private static final String ST_JUNITTREE =  "-junittree";
	
	/** windo wtitle */
	private static final String ST_WINDOWTITLE = "-windowtitle";
	
	/** header */
	private static final String ST_HEADER =  "-header";

	/** overview */
	private static final String ST_OVERVIEW =  "-overview";

	/** style sheet file */
	private static final String ST_STHKESHEETFILE =  "-stylesheetfile";
	
	/** footer */
	private static final String ST_FOOTER =  "-footer";
	
	/** locale */
	private static final String ST_LOCALE =  "-locale";
	
	/** serialwarn */
	private static final String ST_SERIALWARN = "-serialwarn";

	/** nodeprecated */
	private static final String ST_NODEPRECATED = "-nodeprecated";

	/** use */
	private static final String ST_USE = "-use";

	/** nodeprecatedlist*/
	private static final String ST_NODEPRECATEDLIST = "-nodeprecatedlist";
	
	/** noindex */
	public static final String ST_NOINDEX = "-noindex";
	
	/** notree */
	private static final String ST_NOTREE = "-notree";
	
	/** nonavbar*/
	private static final String ST_NONAVBAR = "-nonavbar";
	
	/** nohelp */
	private static final String ST_NOHELP = "-nohelp";
	
	/** verbose */
	private static final String ST_VERBOSE = "-verbose";
	
	/** access(ignore option) */
	private static final String ST_PUBLIC = "-public";
	
	/** access(ignore option) */
	private static final String ST_PROTRCTED = "-protected";
	
	/** access(ignore option) */
	private static final String ST_PACKAGE = "-package";
	
	/** access(ignore option) */
	private static final String ST_PRIVATE = "-private";
	
	/** splitindex */
	private static final String ST_SPLOTINDEX = "-splitindex";
	
	/** help */
	private static final String ST_HELP = "-help";
	
	/** group */
	private static final String ST_GROUP = "-group";
	
	/** output file */
	private static final String OUTPUT_NORMAL = "ut5.html";
	
	/** output file */
	private static final String OUTPUT_TREE = "ut5tree.html";

	/**
	 * option name to option length.
	 * (Doclet required method)
	 * @param option option name
	 * @return option length
	 */
	public static int optionLength(String option) {
		int result;
		//root.printError("option option=" + option);
		switch (option) {
		case ST_GROUP:
			result = 3;
			break;
		case ST_DOCLET:
		case ST_DOCLET_PATH: 
		case ST_D:
		case ST_ENCODING:
		case ST_DOCENCODING: 
		case ST_CHARSET:
		case ST_DOCTITLE: 
		case ST_BUTTOM:
		case ST_CLASSPATH:
		case ST_SOURCEPATH: 
		case ST_SOURCE:
		case ST_LINK:
		case ST_JUNIT:
		case ST_FINDBUGS:
		case ST_JUNITTREE:
		case ST_WINDOWTITLE:
		case ST_HEADER:
		case ST_FOOTER:
		case ST_LOCALE:
		case ST_OVERVIEW:
		case ST_STHKESHEETFILE:
			result = 2;
			break;
		case ST_NOINDEX:
		case ST_NOTREE:
		case ST_NONAVBAR:
		case ST_NOHELP:
		case ST_VERBOSE:
		case ST_NODEPRECATED:
		case ST_SERIALWARN:
		case ST_USE:
		case ST_NODEPRECATEDLIST:
		case ST_PUBLIC:
		case ST_PROTRCTED:
		case ST_PACKAGE:
		case ST_PRIVATE:
		case ST_SPLOTINDEX:
		case ST_HELP:
			result = 1;
			break;
		default:
			System.err.println("unkown option e=" + option);
			result = 0;
			break;
		}
		return result;
	}

	/**
	 * Getting start!
	 * (Doclet required method)
	 * @param root root document.
	 * @return normal is true, if abnormal is false
	 */
	public static boolean start(RootDoc root) {
		UT5Doclet doclet = new UT5Doclet();
		return doclet.run(root);
	}
	
	/** Getting start!
	 * (Main program)
	 * @param rootDoc root document.
	 * @return normal is true, if abnormal is false
	 */
	public boolean run(RootDoc rootDoc) {
		if (!validateOption(rootDoc)) {
			return false;
		}
		if (verbose) {
			rootDoc.printError("start class" + this.getClass().getName());
		}
		final StringBuffer buffer = new StringBuffer();
		printHeader(rootDoc,buffer);
		//
		List<UT5Class> allClassList = 
		junitDirList.stream().flatMap(this::findFile)
		.filter(e->0 <= e.getName().indexOf("TEST-"))
		.filter(e->0 < e.getName().indexOf(".xml"))
		.flatMap(file->this.redadFileForJunit(rootDoc,file))
		.collect(Collectors.toList());
		//
		if (!findbugsDirList.isEmpty()) {
			List<Doc> errorDoc = new ArrayList<Doc>();
			List<Doc> failerDoc = new ArrayList<Doc>();
			findbugsDirList.stream().flatMap(this::findFile)
			.filter(e->0 < e.getName().indexOf(".xml"))
			.forEachOrdered(file->createFindBugs(rootDoc,file,errorDoc,failerDoc));
			List<UT5Class> findbugClassList = createFindBugsClass(rootDoc,errorDoc,failerDoc);
			//
			allClassList = Stream.concat(allClassList.stream(),
					findbugClassList.stream()).collect(Collectors.toList());
		}
		//
		groupFactory.groupPackages(rootDoc,allClassList);	
		//
		if (!noindex) {
			printSummary(buffer);
			groupFactory.getGroupList().stream().forEachOrdered(group->printPackages(buffer,group));
		}
		//
		groupFactory.getGroupList().stream().forEachOrdered(
				group->group.getPackageSet().stream()
				.forEachOrdered(ut5package->printPackage(buffer,ut5package)));
		//
		groupFactory.getGroupList().stream()
		.flatMap(group->group.getPackageSet().stream())
		.forEachOrdered(mypackage->mypackage.getClassSet().stream().forEachOrdered(myClass->printTestCase(buffer,myClass)));
		buffer.append("<hr size=\"1\" width=\"95%\" align=\"left\">\r\n");
		//
		printFooter(buffer);
        writeJUnitReport(buffer,OUTPUT_NORMAL);
        //
        //
        if (!notree) {
        	buffer.delete(0, buffer.length());
    		printTree(rootDoc,buffer);
    		writeJUnitReport(buffer,OUTPUT_TREE);
        }
        //
        return errorFlag;
	}

	
	/**
	 * validate option.
	 * and setting fields.
	 * @param root root document.
	 * @return normal is true, if abnormal is false
	 */
	protected boolean validateOption(RootDoc root) {
		for (int i = 0; i < root.options().length; i++) {
			String[] opt = root.options()[i];
			switch (opt[0]) {
			case ST_DOCLET:
			case ST_DOCLET_PATH: 
			case ST_CLASSPATH:
			case ST_SOURCEPATH:
			case ST_SOURCE:
			case ST_NODEPRECATED:
			case ST_NONAVBAR:
			case ST_NOHELP:
			case ST_SERIALWARN:
			case ST_USE:
			case ST_NODEPRECATEDLIST:
			case ST_PUBLIC:
			case ST_PROTRCTED:
			case ST_PACKAGE:
			case ST_PRIVATE:
			case ST_SPLOTINDEX:
			case ST_HELP:
				break;
			case ST_D:
				dstDir = new File(opt[1]);
 				if (!dstDir.isDirectory()) {
 					root.printError(opt[0] + "=" + opt[1] + " is not directory d=");
 					return false;
 				}
				break;
			case ST_ENCODING: 
				encoding = opt[1];
				break;
			case ST_DOCENCODING: 
				docencoding = opt[1];
				break;
			case ST_DOCTITLE: 
				doctitle = opt[1];
				break;
			case ST_WINDOWTITLE:
				windowtitle = opt[1];
				break;
			case ST_HEADER:
				header = opt[1];
				break;
			case ST_FOOTER:
				footer = opt[1];
				break;
			case ST_BUTTOM:
				bottom = opt[1];
				break;
			case ST_LINK:
				link = opt[1];
				break;
			case ST_JUNIT:
 				File junitDir = new File(opt[1]);
 				if (!junitDir.isDirectory()) {
 					root.printError(opt[0] + "=" + junitDir.getAbsolutePath() + " is not directory");
 					return false;
 				}
 				junitDirList.add(junitDir);
				break;
			case ST_FINDBUGS:
 				File findbugsDir = new File(opt[1]);
 				if (!findbugsDir.exists()) {
 					root.printError(opt[0] + "=" + findbugsDir.getAbsolutePath() + " is not exists");
 					return false;
 				}
 				findbugsDirList.add(findbugsDir);
				break;
			case ST_JUNITTREE:
				junittree = opt[1];
				break;
			case ST_LOCALE:
				locale = opt[1];
				break;
			case ST_CHARSET:
				charset = opt[1];
				break;
			case ST_NOINDEX:
				noindex = true;
				break;
			case ST_NOTREE:
				notree = true;
				break;
			case ST_VERBOSE:
				verbose = true;
				break;
			case ST_OVERVIEW:
 				overview = new File(opt[1]);
 				if (!overview.isFile()) {
 					root.printError(opt[0] + "=" + overview.getAbsolutePath() + " is not file");
 					return false;
 				}
 				break;
			case ST_STHKESHEETFILE:
				stylesheetfile = new File(opt[1]);
 				if (!stylesheetfile.isFile()) {
 					root.printError(opt[0] + "=" + overview.getAbsolutePath() + " is not file");
 					return false;
 				}
 				break;
			case ST_GROUP:
				//root.printError(opt[0] + "=1 : " + opt[1]);
				//root.printError(opt[0] + "=2 : " + opt[2]);
				//
				if (!groupFactory.checkPackageGroups(opt[1],opt[2],root)) {
					return false;
				}
				//
				break;
			default:
				root.printError(opt[0] + " is unkown");
			}
		}
		if (dstDir == null) {
			root.printError(ST_D + " not found.");
			return false;
		}
		//if (junitDirList.isEmpty()) {
		//	root.printError(ST_JUNIT + " not found.");
		//	return false;
		//}
		if (encoding == null) {
			encoding = System.getProperty("file.encoding");
		}
		if (docencoding == null) {
			docencoding = System.getProperty("file.encoding");
		}
		if (windowtitle == null) {
			windowtitle = "Unit Test Results.";
		}
		if (doctitle == null) {
			doctitle = "Unit Test Results.";
		}
		if (locale == null) {
			locale = Locale.getDefault().getLanguage();
		}
		if (header == null) {
			 StringBuffer b = new StringBuffer();
			 b.append("<table width=\"100%\">\r\n");
			 b.append("<tr>\r\n");
			 b.append("<td align=\"left\"></td><td align=\"right\">Designed for use with <a href=\"http://www.junit.org\">JUnit</a>.</td>\r\n");
			 b.append("</tr>\r\n");
			 b.append("</table>\r\n");
			 header = b.toString();
		}
		if (bottom == null) {
			bottom = "<a href=\"#top\">Back to top</a>\r\n<p></p>\r\n<p></p>\r\n";
		}
		if (footer == null) {
			footer = "";
		}
		if (junittree == null) {
			junittree = "abc def ghi jkl";
		}
		if (junittree.indexOf('h') < 0) {
			junittree = junittree + "h"; // test name
		}
		return true;
	}
	
	/**
	 * print header
	 * @param rootDoc RootDoc for JavaDoc
	 * @param b output buffer.
	 */
	protected void printHeader(RootDoc rootDoc,StringBuffer b) {
		b.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n");
		b.append("<html lang=\""+ locale + "\" xmlns:string=\"xalan://java.lang.String\" xmlns:lxslt=\"http://xml.apache.org/xslt\">\r\n");
		b.append("<head>\r\n");
		if (charset != null) {
			b.append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=" + charset + "\">\r\n");
		}
		b.append("<title>" + windowtitle + "</title>\r\n");
		b.append("<style type=\"text/css\">\r\n");
		b.append("      body {\r\n");
		b.append("        font:normal 68% verdana,arial,helvetica;\r\n");
		b.append("        color:#000000;\r\n");
		b.append("      }\r\n");
		b.append("      table tr td, table tr th {\r\n");
		b.append("          font-size: 68%;\r\n");
		b.append("      }\r\n");
		b.append("      table.bodyTable th{\r\n");
		b.append("        font-weight: bold;\r\n");
		b.append("        text-align:left;\r\n");
		b.append("        background:#a6caf0;\r\n");
		b.append("      }\r\n");
		b.append("      table.bodyTable td{\r\n");
		b.append("        background:#eeeee0;\r\n");
		b.append("      }\r\n");
		b.append("      p {\r\n");
		b.append("        line-height:1.5em;\r\n");
		b.append("        margin-top:0.5em; margin-bottom:1.0em;\r\n");
		b.append("      }\r\n");
		b.append("      h1 {\r\n");
		b.append("        margin: 0px 0px 5px; font: 165% verdana,arial,helvetica\r\n");
		b.append("      }\r\n");
		b.append("      h2 {\r\n");
		b.append("        margin-top: 1em; margin-bottom: 0.5em; font: bold 125% verdana,arial,helvetica\r\n");
		b.append("      }\r\n");
		b.append("      h3 {\r\n");
		b.append("        margin-bottom: 0.5em; font: bold 115% verdana,arial,helvetica\r\n");
		b.append("      }\r\n");
		b.append("      h4 {\r\n");
		b.append("        margin-bottom: 0.5em; font: bold 100% verdana,arial,helvetica\r\n");
		b.append("      }\r\n");
		b.append("      h5 {\r\n");
		b.append("        margin-bottom: 0.5em; font: bold 100% verdana,arial,helvetica\r\n");
		b.append("      }\r\n");
		b.append("      h6 {\r\n");
		b.append("        margin-bottom: 0.5em; font: bold 100% verdana,arial,helvetica\r\n");
		b.append("      }\r\n");
		b.append("      .Error {\r\n");
		b.append("        font-weight:bold; color:red;\r\n");
		b.append("      }\r\n");
		b.append("      .Failure {\r\n");
		b.append("        font-weight:bold; color:purple;\r\n");
		b.append("      }\r\n");
		b.append("      .Properties {\r\n");
		b.append("        text-align:right;\r\n");
		b.append("      }\r\n");
		//
		if (stylesheetfile != null) {
			try (FileInputStream fis = new FileInputStream(stylesheetfile);
					InputStreamReader isr = new InputStreamReader(fis,encoding);
					BufferedReader br = new BufferedReader(isr)) {
				br.lines().forEachOrdered(str->b.append(str).append("\r\n"));
			} catch(Exception e) {
				rootDoc.printError(e.getMessage());
				errorFlag = true;
			}
		}
	    //
		b.append("      </style>\r\n");
	    //
		b.append("</head>\r\n");
		b.append("<body>\r\n");
		b.append("<a name=\"top\"></a>\r\n");

		b.append("<h1>" + doctitle + "</h1>\r\n");
		b.append(header + "\r\n");
		b.append("<hr size=\"1\">\r\n");
		if (overview != null) {
		    try (FileInputStream fis = new FileInputStream(overview);
		    		InputStreamReader isr = new InputStreamReader(fis,encoding);
		    		BufferedReader br = new BufferedReader(isr)) {
		    	br.lines().forEachOrdered(str->b.append(str).append("\r\n"));
		    } catch(Exception e) {
				rootDoc.printError(e.getMessage());
		    	errorFlag = true;
		    }
		}
	}
	
	/**
	 * print summary
	 * @param b output buffer.
	 */
	protected void printSummary(StringBuffer b) {
		//
		DecimalFormat dp = new DecimalFormat("##0.00%");
		DecimalFormat dt = new DecimalFormat("0.000");
		//
		b.append("<h2>Summary</h2>\r\n");
		b.append("<table class=\"bodyTable\" border=\"0\" cellpadding=\"5\" cellspacing=\"2\" width=\"95%\">\r\n");
		b.append("<tr valign=\"top\">\r\n");
		b.append("<th>---</th><th>Tests</th><th>Failures</th><th>Errors</th><th>Skipped</th><th>Success rate</th><th>Time</th>\r\n");
		b.append("</tr>\r\n");
		//
		for (UT5Group myGroup : groupFactory.getGroupList()) {
			List<UT5Class> myClassList = myGroup.getPackageSet().stream()
					.flatMap(myPackage->myPackage.getClassSet().stream()).collect(Collectors.toList());
			int sumTest = myClassList.stream().mapToInt(e->e.getTests()).sum();
			int sumFailures = myClassList.stream().mapToInt(e->e.getFailures()).sum();
			int sumErrors = myClassList.stream().mapToInt(e->e.getErrors()).sum();
			int sumSkipped = myClassList.stream().mapToInt(e->e.getSkipped()).sum();
			String sumSuccessRate = dp.format(1.0 * (sumTest - sumFailures - sumErrors) / sumTest);
			double sumTimeInt = myClassList.stream().mapToDouble(e->e.getTime()).sum();
			String sumTime;
			if (sumTimeInt <= 0) {
				sumTime = "---";
			} else {
				sumTime = dt.format(sumTimeInt);
			}
			//
			String htmlClass;
			if (0 < sumFailures) {
				htmlClass = UT5Method.ST_FAIILURE;
			} else if (0 < sumErrors) {
				htmlClass = UT5Method.ST_ERROR;
			} else {
				htmlClass = "";
			}
			b.append("<tr valign=\"top\" class=\"" + htmlClass +  "\">\r\n");
			b.append("<td>" + myGroup.getName() + "</td>");
			b.append("<td>" + sumTest + "</td><td>" + sumFailures + "</td><td>" + sumErrors + "</td><td>" + sumSkipped + "</td><td>" + sumSuccessRate + "</td><td>" + sumTime + "</td>\r\n");
			b.append("</tr>\r\n");
		}
		if (1 < groupFactory.getGroupList().size()) {
			List<UT5Class> myClassList = groupFactory.getGroupList().stream().flatMap(
					group->group.getPackageSet().stream())
					.flatMap(myPackage->myPackage.getClassSet().stream()).collect(Collectors.toList());
			int sumTest = myClassList.stream().mapToInt(e->e.getTests()).sum();
			int sumFailures = myClassList.stream().mapToInt(e->e.getFailures()).sum();
			int sumErrors = myClassList.stream().mapToInt(e->e.getErrors()).sum();
			int sumSkipped = myClassList.stream().mapToInt(e->e.getSkipped()).sum();
			String sumSuccessRate = dp.format(1.0 * (sumTest - sumFailures - sumErrors) / sumTest);
			double sumTimeInt = myClassList.stream().mapToDouble(e->e.getTime()).sum();
			String sumTime;
			if (sumTimeInt <= 0) {
				sumTime = "---";
			} else {
				sumTime = dt.format(sumTimeInt);
			}
			//
			String htmlClass;
			if (0 < sumFailures) {
				htmlClass = UT5Method.ST_FAIILURE;
			} else if (0 < sumErrors) {
				htmlClass = UT5Method.ST_ERROR;
			} else {
				htmlClass = "";
			}
			b.append("<tr valign=\"top\" class=\"" + htmlClass +  "\">\r\n");
			b.append("<th>All</th>");
			b.append("<th>" + sumTest + "</th><th>" + sumFailures + "</th><th>" + sumErrors + "</th><th>" + sumSkipped + "</th><th>" + sumSuccessRate + "</th><th>" + sumTime + "</th>\r\n");
			b.append("</tr>\r\n");
		}
		b.append("</table>\r\n");
		b.append("<table border=\"0\" width=\"95%\">\r\n");
		b.append(" <tr>\r\n");
		b.append("   <td style=\"text-align: justify;\">\r\n");
		b.append("    Note: <i>failures</i> are anticipated and checked for with assertions while <i>errors</i> are unanticipated.\r\n");
		b.append("   </td>\r\n");
		b.append(" </tr>\r\n");
		b.append("</table>\r\n");
		b.append("<hr size=\"1\" width=\"95%\" align=\"left\">\r\n");
		
	}
	
	/**
	 * print packages
	 * @param b output buffer.
	 * @param group package group information.
	 */
	public void printPackages(StringBuffer b,UT5Group group) {
		b.append("<h2>" + group.getName() + "</h2>\r\n");
		b.append("Note: package statistics are not computed recursively, they only sum up all of its testsuites numbers.\r\n");
		b.append("<table class=\"bodyTable\" border=\"0\" cellpadding=\"5\" cellspacing=\"2\" width=\"95%\">\r\n");
		b.append("<tr valign=\"top\">\r\n");
		b.append(" <th>Name</th><th>Message</th><th>Tests</th><th>Errors</th><th>Failures</th><th>Skipped</th><th nowrap>Time(s)</th><th nowrap>Time Stamp</th>\r\n");
		b.append("</tr>\r\n");
		group.getPackageSet().stream().forEachOrdered(myPackage->printPackagesOne(b,myPackage));
		b.append("</table>\r\n");
		b.append("<hr size=\"1\" width=\"95%\" align=\"left\">\r\n");
		//
	}
	
	/**
	 * print package (for one package)
	 * @param b output buffer.
	 * @param myPackage list of PckageDoc for JUnit.
	 */
	public void printPackagesOne(StringBuffer b,UT5Package myPackage) {
		DecimalFormat dt = new DecimalFormat("0.000");
		int sumTest = myPackage.getClassSet().stream().mapToInt(e->e.getTests()).sum();
		int sumFailures = myPackage.getClassSet().stream().mapToInt(e->e.getFailures()).sum();
		int sumErrors = myPackage.getClassSet().stream().mapToInt(e->e.getErrors()).sum();
		int sumSkipped = myPackage.getClassSet().stream().mapToInt(e->e.getSkipped()).sum();
		double sumTimeInt = myPackage.getClassSet().stream().mapToDouble(e->e.getTime()).sum();
		String sumTime;
		if (sumTimeInt <= 0) {
			sumTime = "---";
		} else {
			sumTime = dt.format(sumTimeInt);
		}

		//
		String htmlClass;
		if (0 < sumFailures) {
			htmlClass = UT5Method.ST_FAIILURE;
		} else if (0 < sumErrors) {
			htmlClass = UT5Method.ST_ERROR;
		} else {
			htmlClass = "";
		}
		String timeStamp = null;
		for (UT5Class myClass : myPackage.getClassSet()) {
			if ((timeStamp == null) || (0 < timeStamp.compareTo(myClass.getTimestamp()))) {
				timeStamp = myClass.getTimestamp();
			}
		}
		if (timeStamp == null) {
			timeStamp = "";
		}
		//
		b.append("<tr valign=\"top\" class=\"" + htmlClass + "\">\r\n");
		b.append("<td><a href=\"#" + myPackage.getName() + "\">" + myPackage.getName() + "</a></td>");
		b.append("<td>" + myPackage.getMessage() + "</td>");
		b.append("<td>" + sumTest + "</td>");
		b.append("<td>" + sumErrors + "</td>");
		b.append("<td>" + sumFailures + "</td>");
		b.append("<td>" + sumSkipped + "</td>");
		b.append("<td>" + sumTime + "</td>\r\n");
		b.append("<td>" + timeStamp + "</td>\r\n");
		b.append("</tr>\r\n");
	}
	/**
	 * print package
	 * @param b output buffer.
	 * @param myPackage list of PackageDoc for JUnit.
	 */
	protected void printPackage(StringBuffer b,UT5Package myPackage) {
		//
		b.append("<a name=\"" + myPackage.getName() + "\"></a>\r\n");
		b.append("<h3>Package " + myPackage.getName() + "</h3>\r\n");
		b.append("<table class=\"bodyTable\" border=\"0\" cellpadding=\"5\" cellspacing=\"2\" width=\"95%\">\r\n");
		
		String packMessage = myPackage.getMessage();
		if ((packMessage != null) && (!packMessage.equals(""))) {
			b.append("<tr valign=\"top\">\r\n");
			b.append(" <th colspan=\"8\">Package</th>\r\n");
			b.append("</tr>\r\n");
			b.append("<tr valign=\"top\">\r\n");
			b.append(" <td colspan=\"8\">" + packMessage + "</td>\r\n");
			b.append("</tr>\r\n");
		}
		b.append("<tr valign=\"top\">\r\n");
		b.append("<th>Name</th><th>Message</th><th>Tests</th><th>Errors</th><th>Failures</th><th>Skipped</th><th nowrap>Time(s)</th><th nowrap>Time Stamp</th>\r\n");
		b.append("</tr>\r\n");
		myPackage.getClassSet().stream().forEachOrdered(e->printPackageOne(b,e));
		b.append("</table>\r\n");
		//
		b.append(bottom);
		//
		b.append("<hr size=\"1\" width=\"95%\" align=\"left\">\r\n");
	}

	
	/**
	 * print package (for one package)
	 * @param b output buffer.
	 * @param myClass ClassDoc for JUnit.
	 */
	protected void printPackageOne(StringBuffer b,UT5Class myClass) {
		b.append("<tr valign=\"top\" class=\"" + myClass.getHtmlClass() + "\">\r\n");
		b.append("<td><a href=\"#" + myClass.getName() + "\">" + myClass.getClassName() + "</a></td>");
		b.append("<td>" + myClass.getMessage() + "</td>");
		b.append("<td>" + myClass.getTests() + "</td>");
		b.append("<td>" + myClass.getErrors() + "</td>");
		b.append("<td>" + myClass.getFailures() + "</td>");
		b.append("<td>" + myClass.getSkipped() + "</td>");
		b.append("<td>" + myClass.getTimeString() + "</td>\r\n");
		b.append("<td>" + myClass.getTimestamp()+ "</td>");
		b.append("</tr>\r\n");
	}
	
	/**
	 * print ClassDoc.
	 * @param b output buffer.
	 * @param myClass ClassDoc for JUnit.
	 */
	protected void printTestCase(StringBuffer b,UT5Class myClass) {
		b.append("<a name=\"" + myClass.getName() + "\"></a>\r\n");
		b.append("<h3>TestCase " + myClass.getClassName() + "</h3>\r\n");
		//
		b.append("<table class=\"bodyTable\" border=\"0\" cellpadding=\"5\" cellspacing=\"2\" width=\"95%\">\r\n");

		String message = myClass.getMessage();
		if ( ! message.equals("")) {
			b.append("<tr valign=\"top\">\r\n");
			b.append("<th colspan=\"4\">TestCase</th>\r\n");
			b.append("</tr>\r\n");
			b.append("<tr valign=\"top\">\r\n");
			b.append("<td colspan=\"4\">" + message + "</td>\r\n");
			b.append("</tr>\r\n");
		}
		b.append("<tr valign=\"top\">\r\n");
		b.append("<th>Name</th><th width=\"80%\">Message</th><th>Status</th><th nowrap>Time(s)</th>\r\n");
		b.append("</tr>\r\n");	
		//
		List<UT5Method> anoCaseList = myClass.getAnoMethodList();
		anoCaseList.stream().forEachOrdered(myMehtod->printTestCaseOne(b,myMehtod));
		//
		myClass.getMethodList().stream().sorted().forEachOrdered(method->printTestCaseOne(b,method));
		//
		b.append("</table>\r\n");
		//
		//
		if (bottom != null) {
			b.append(bottom);
		}
	}
	
	/**
	 * print ClassDoc (for one MethodDoc)
	 * @param b output buffer.
	 * @param myMethod MethodDoc fot JUnit
	 */
	protected void printTestCaseOne(StringBuffer b,UT5Method myMethod) {
		b.append("<tr valign=\"top\" class=\"" + myMethod.getHtmlClass() + "\">\r\n");
		b.append("<td>" + myMethod.getName() + "</td>");
		b.append("<td>" + myMethod.getMessage() + "</td>");
		b.append("<td>" + myMethod.getStatus() + "</td>");
		b.append("<td>" + myMethod.getTimeString() + "</td>\r\n");
		b.append("</tr>\r\n");
	}
	/**
	 * print ClassDoc (for annotation).
	 * @param b output buffer.
	 * @param ano annotation.
	 * @param myClass ClassDoc for JUnit.
	 */
	protected void printTestCaseAnoOne(StringBuffer b,String ano,UT5Class myClass) {
		myClass.getAnoMethodList().stream().forEachOrdered(myMehtod->printTestCaseOne(b,myMehtod));

	}
	
	/**
	 * print footer
	 * @param b output buffer.
	 */
	protected void printFooter(StringBuffer b) {
		if (link != null) {
			b.append("<p>\r\n");
			b.append("@see <a href=\"" + link + "\">" + link + "</a>");
			b.append("</p>\r\n");
		}
		b.append(footer + "\r\n");
		b.append("</body>\r\n");
		b.append("</html>\r\n");
	}

	/**
	 * print tree
	 * @param rootDoc RootDoc for JavaDoc
	 * @param b output buffer.
	 */
	protected void printTree(RootDoc rootDoc,StringBuffer b) {
    	printHeader(rootDoc,b);
		this.printSummary(b);
		groupFactory.getGroupList().stream().forEachOrdered(group->printTree(b,group));
		b.append("<hr size=\"1\">\r\n");
    	//
    	printFooter(b);
	}

	
	/**
	 * print tree
	 * @param b output buffer.
	 * @param group package group information.
	 */
	protected void printTree(StringBuffer b,UT5Group group) {
		//
    	// create annotation map.
		//
    	//
		b.append("<h2>" + group.getName() + "</h2>\r\n");
		b.append("<table class=\"bodyTable\" border=\"0\" cellpadding=\"5\" cellspacing=\"2\" width=\"95%\">\r\n");
		b.append("<tr valign=\"top\">\r\n");
		for (int i = 0 ; i < junittree.length(); i ++) {
			switch (junittree.charAt(i)) {
			case 'a':
				b.append("<th>No</th>");
				break;
			case 'b':
				b.append("<th>Package</th>");				
				break;
			case 'c':
				b.append("<th>Message</th>");				
				break;
			case 'd':
				b.append("<th>No</th>");
				break;
			case 'e':
				b.append("<th>Class</th>");				
				break;
			case 'f':
				b.append("<th>Message</th>");				
				break;
			case 'g':
				b.append("<th>No</th>");
				break;
			case 'h':
				b.append("<th>TestCase</th>");				
				break;
			case 'i':
				b.append("<th>Message</th>");				
				break;
			case 'j':
				b.append("<th>Status</th>");				
				break;
			case 'k':
				b.append("<th>TimeStamp</th>");				
				break;
			case 'l':
				b.append("<th>Time</th>");				
				break;
			case 'm':
				b.append("<th>Author</th>");				
				break;
			case 'n':
				b.append("<th>Version</th>");				
				break;
			case 'o':
			case 'p':
			case 'q':
			default:
				b.append("<th>-</th>");
				break;
			case 'r': //Precondition
				b.append("<th>Precondition</th>");
				break;
			case 's': //small author
				b.append("<th>Author</th>");
				break;
			case 't': //small time stamp
			case 'u': //old time stamp
			case 'v': //small old time stamp
				b.append("<th>TimeStamp</th>");
				break;
			case 'w': //pass the test code
				b.append("<th>Expected Result</th>");
				break;
			case ' ':
			case '\t':
				break;
			}
		}		
		b.append("</tr>\r\n");

		int packageNum = 0;
		List<UT5Package> sortKeyList =
				group.getPackageSet().stream().collect(Collectors.toList());
		for (UT5Package key : sortKeyList) {
			packageNum++;
			String packMessage = key.getMessage();
			if ((packMessage != null) && (!packMessage.equals(""))) {
				packMessage = UT5.createMultiMessage(packMessage);
			}
			if (packMessage == null) {
				packMessage = "";
			}
			int classNum = 0;
			Set<UT5Class> myClassList = key.getClassSet();
			int packageRowspan = myClassList.stream()
					.mapToInt(myClass-> myClass.getMethodList().size() + myClass.getAnoMethodList().size()).sum();
			for (UT5Class myClass : myClassList) {
				classNum++;
				//
				int methodNum = 0;
				List<UT5Method> myMethodList = myClass.getMethodList().stream().sorted().collect(Collectors.toList());
				List<UT5Method> anoCaseList = myClass.getAnoMethodList();
				List<UT5Method> allList = Stream.concat(anoCaseList.stream(),myMethodList.stream()).collect(Collectors.toList());	
				for (UT5Method myMethod : allList) {
					methodNum++;
			    	String htmlClass = myMethod.getHtmlClass();
					//
					b.append("<tr  valign=\"top\" class=\"" + htmlClass + "\">\r\n");
					for (int i = 0 ; i < junittree.length(); i ++) {
						switch (junittree.charAt(i)) {
						case 'a':
							b.append("<td>" + packageNum + " </td>");
							break;
						case 'b':
							if ((classNum ==1) && (methodNum == 1)) {
								b.append("<td rowspan=\"" + packageRowspan + "\">" + key.getName() + "</td>");
							}
							break;
						case 'c':
							if ((classNum ==1) && (methodNum == 1)) {
								b.append("<td rowspan=\"" + packageRowspan + "\">" + packMessage + "</td>");
							}
							break;
						case 'd':
							b.append("<td>" + classNum + " </td>");
							break;
						case 'e':
							if (methodNum == 1) {
								b.append("<td rowspan=\"" + allList.size() + "\">" + myClass.getClassName() + "</td>");
							}
							break;
						case 'f':
							if (methodNum == 1) {
								b.append("<td rowspan=\"" + allList.size() + "\">" + myClass.getMessage() + "</td>");
							}
							break;
						case 'g':
							b.append("<td>" + methodNum + " </td>");
							break;
						case 'h':
							b.append("<td>" + myMethod.getName() + "</td>");
							break;
						case 'i':
							b.append("<td>" + myMethod.getMessage() + "</td>");
							break;
						case 'j':
							b.append("<td>" + myMethod.getStatus() + " </td>");
							break;
						case 'k':
							b.append("<td>" + myMethod.getTimestamp() + " </td>");
							break;
						case 'l':
							b.append("<td>" + myMethod.getTimeString() + " </td>");
							break;
						case 'm':
							b.append("<td>" + myMethod.getAuthor() + " </td>");
							break;
						case 'n':
							b.append("<td>" + myMethod.getVersion() + " </td>");
							break;
						case 'o': // packageBrank
							if ((classNum ==1) && (methodNum == 1)) {
								b.append("<td rowspan=\"" + packageRowspan + "\"></td>");
							}
							break;
						case 'p': // classBrank
							if (methodNum == 1) {
								b.append("<td rowspan=\"" + allList.size() + "\"></td>");
							}
							break;
						case 'q': // methodBrank
						default:
							b.append("<td></td>");
							break;
						case 'r': //Precondition
							if (methodNum == 1) {
								b.append("<td rowspan=\"" + allList.size() + "\">");
								for (UT5Method anoClass : anoCaseList) {
									b.append(anoClass.getStatus());
									b.append("<br />&nbsp;&nbsp;");
									b.append(anoClass.getName());
									b.append("<br />");
								}
								b.append("</td>");
							}
							break;
						case 's': //small author

							b.append("<td>" + myMethod.getSmallAuthor() + " </td>");
							break;
						case 't': //small time stamp
							b.append("<td>" + myMethod.getSmallTimestamp() + " </td>");
							break;
						case 'u': //small time stamp
							b.append("<td>" + myMethod.getOldTimestamp() + " </td>");
							break;
						case 'v': //small time stamp
							b.append("<td>" + myMethod.getSmallOldTimestamp() + " </td>");
							break;
						case 'w': //Expected Result
							b.append("<td>Pass the test code.</td>");
							break;
						case ' ':
						case '\t':
							break;
						}
					}
					//
					b.append("</tr>\r\n");		
				}
			}
		}
		//
		b.append("</table>\r\n");

	}

	
	/**
	 * output to file.
	 * @param b output buffer.
	 * @param file output file
	 */
	protected void writeJUnitReport(StringBuffer b,String file) {
		File fileName = new File(dstDir,file);
		try (FileOutputStream fos = new FileOutputStream(fileName);
			OutputStreamWriter osw = new OutputStreamWriter( fos , docencoding);
			BufferedWriter fp = new BufferedWriter( osw );) {
			//
			fp.write ( b.toString() );
			//
		} catch (IOException e) {
			errorFlag = true;
		}
	}
	
	/**
	 * find all files for directory
	 * @param file target file
	 * @return file stream
	 */
	public Stream<File> findFile(File file) {
		if (file.isFile()) {
			return Stream.of(file);
		} else if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				return Arrays.asList(files).stream().flatMap(this::findFile);
			}
		}
		return Stream.empty();
	}
	
	/**
	 * read for ClassDoc and methodDoc for JUnit.
	 * @param rootDoc rootDoc for JavaDoc
	 * @param file TEST-*.xml file.
	 * @return ClassDoc for JUNit stream.
	 */
	protected Stream<UT5Class> redadFileForJunit(RootDoc rootDoc,File file) {
		UT5Class toClass = null;
		try {
			Element rootElement = DocumentBuilderFactory
					.newInstance()
					.newDocumentBuilder()
					.parse(file).getDocumentElement();
			// create class
			toClass = new UT5Class(rootDoc,rootElement);
			//

		} catch (DOMException | SAXException | IOException | ParserConfigurationException e) {
			rootDoc.printError(e.getMessage());
			errorFlag = true;
			return Stream.empty();
		}
		return Arrays.asList(toClass).stream();
	}
	
	/**
	 * create find bugs class
	 * @param rootDoc rootDoc for JavaDoc
	 * @param errorDoc error target doc for JavaDoc
	 * @param failurerDoc failurerDoc target doc for JavaDoc
	 * @return ClassDoc for JUnit
	 */
	protected List<UT5Class> createFindBugsClass(RootDoc rootDoc,List<Doc> errorDoc,List<Doc> failurerDoc) {
		long timestamp = this.groupFactory.getFindbugsTimestamp();
		long oldTimestamp = this.groupFactory.getFindbugsOldTimestamp();
		return 
				Arrays.asList(rootDoc.classes()).stream().map(classDoc->{
					String name = UT5GroupFactory.FINDBUGS_PACK + classDoc.qualifiedName();
					UT5Class myClass = new UT5Class(rootDoc,name,0,timestamp);
					//
					int testCount = 0;
					int errorCount = 0;
					int failurerCount = 0;
					List<MemberDoc> constMethodDocList =
							Stream.concat(
							Arrays.asList(classDoc.constructors()).stream(),
							Arrays.asList(classDoc.methods()).stream()).collect(Collectors.toList());
					constMethodDocList =
							Stream.concat(
									constMethodDocList.stream(),
									Arrays.asList(classDoc.fields()).stream())
							.collect(Collectors.toList());
					for (MemberDoc doc : constMethodDocList) {
						testCount++;
						String status;
						if (errorDoc.contains(doc)) {
							errorCount++;
							status = UT5Method.ST_ERROR;
						} else if (failurerDoc.contains(doc)) {
							errorCount++;
							status = UT5Method.ST_FAIILURE;
						} else {
							status = UT5Method.ST_SUCCES;
						}
						String signature;
						if (doc instanceof ExecutableMemberDoc) {
							signature = ((ExecutableMemberDoc)doc).signature();
						} else {
							signature = UT5Method.FIELD_SIGN;
						}
						UT5Method myMethod = new UT5Method(
								classDoc, doc.name(),signature,
								0.0,status,timestamp,oldTimestamp);
						myClass.getMethodList().add(myMethod);
					}
					//
					testCount++;
					String status;
					if (errorDoc.contains(classDoc)) {
						errorCount++;
						status = UT5Method.ST_ERROR;
					} else if (failurerDoc.contains(classDoc)) {
						failurerCount++;
						status = UT5Method.ST_FAIILURE;
					} else {
						status = UT5Method.ST_SUCCES;
					}
					// other class..
					myClass.getAnoMethodList().add(
							new UT5Method(classDoc,"@Class","-",-1.0,status,
									timestamp,oldTimestamp));
					//
					myClass.setTests(testCount);
					myClass.setErrors(errorCount);
					myClass.setSkipped(0);
					myClass.setFailures(failurerCount);
					//
					return myClass;
				}).collect(Collectors.toList());
	}
	
	/**
	 * update from find bugs.
	 * @param rootDoc RootDoc for JavaDoc
	 * @param file findbugs.xml list
	 * @param errorDoc error target doc for JavaDoc
	 * @param failurerDoc failurerDoc target doc for JavaDoc
	 */
	protected void createFindBugs(RootDoc rootDoc,File file,List<Doc> errorDoc,List<Doc> failurerDoc) {
		try {
			Element rootElement = DocumentBuilderFactory
					.newInstance()
					.newDocumentBuilder()
					.parse(file).getDocumentElement();
			// create class
			createFindBugs(rootDoc,rootElement,errorDoc,failurerDoc);
			//
		} catch (DOMException | SAXException | IOException | ParserConfigurationException e) {
			errorFlag = true;
			rootDoc.printError(e.getMessage());
			e.printStackTrace();
			return;
		}
	}
	/**
	 * update from find bugs.
	 * @param rootDoc RootDoc for JavaDoc
	 * @param element element for findbugs.xml
	 * @param errorDoc error target doc for JavaDoc
	 * @param failurerDoc failure target doc for JavaDoc
	 */
	protected void createFindBugs(RootDoc rootDoc,Element element,List<Doc> errorDoc,List<Doc> failurerDoc) {
		String analysisTimestamp = element.getAttribute("analysisTimestamp");
		//rootDoc.printError("createFindBugs() start tag=" + element.getTagName() + " ans=" + analysisTimestamp);
		try {
			if ((analysisTimestamp != null) && (!analysisTimestamp.equals(""))) {
				long timestamp = Long.parseLong(analysisTimestamp);
				if (this.groupFactory.getFindbugsTimestamp() < timestamp) {
					this.groupFactory.setFindbugsTimestamp(timestamp);
				}
				//rootDoc.printError(" time=" + timestamp);
				//rootDoc.printError("dtime=" + (new Date().getTime()));
			}
		} catch (NumberFormatException e) {
			//EMPTY
		}
		String timeStampString = element.getAttribute("timestamp");
		try {
			if ((timeStampString != null) && (!timeStampString.equals(""))) {
				long timestamp = Long.parseLong(timeStampString);
				if (timestamp < this.groupFactory.getFindbugsOldTimestamp()) {
					this.groupFactory.setFindbugsOldTimestamp(timestamp);
				}
			}
		} catch (NumberFormatException e) {
			//EMPTY
		}
		if (element.getTagName().equals("SourceLine")) {
			String classname = element.getAttribute("classname");
			String startString = element.getAttribute("start");
			int start = 1;
			try {
				if ((startString != null) && (!startString.equals(""))) {
					start = Integer.parseInt(startString);
				}
			} catch (NumberFormatException e) {
				//EMPTY
			}
			ClassDoc classDoc = rootDoc.classNamed(classname);
			if (classDoc == null) {
				rootDoc.printError("JavaDoc: " + classname + " not found");
				return;
			}
			MemberDoc target = null;
			int max = Integer.MAX_VALUE;
			List<MemberDoc> constMethodDocList =
					Stream.concat(
							Arrays.asList(classDoc.constructors()).stream(),
							Arrays.asList(classDoc.methods()).stream())
					.collect(Collectors.toList());
			constMethodDocList =
					Stream.concat(
							constMethodDocList.stream(),
							Arrays.asList(classDoc.fields()).stream())
					.collect(Collectors.toList());
			//
			for (MemberDoc methodDoc: constMethodDocList) {
				SourcePosition position = methodDoc.position();
				int line = position.line();
				if ((start < max) && (line <= start)) {
					max = start;
					target = methodDoc;
				}
			}
			if (target == null) {
				errorDoc.add(classDoc);
				return;
			}
			failurerDoc.add(target);
			return;
		}
		// 
		NodeList nodeList = element.getChildNodes();
		if (nodeList == null) {
			return;
		}
		IntStream.range(0, nodeList.getLength())
		.mapToObj(nodeList::item)
		.filter(e->e instanceof Element)
		.map(e->(Element)e)
		.forEach(e->createFindBugs(rootDoc,e,errorDoc,failurerDoc));
	}
	
	/** JUnit XML-*.xml location */
	private final List<File> junitDirList = new ArrayList<File>();

	/** bcel-fb.xml location */
	private final List<File> findbugsDirList = new ArrayList<File>();
	
	/** destroy directory? */
	private File dstDir = null;
	
	/** locale */
	private String locale = null;
	
	/** encoding */
	private String encoding = null;

	/** doc encoding */
	private String docencoding = null;
	
	/** window title */
	private String windowtitle = null;
	
	/** doc title  */
	private String doctitle = null;
	
	/** header */
	private String header = null;
	
	/** footer */
	private String footer = null;
	
	/** bottom */
	private String bottom = null;
	
	/** link */
	private String link = null;
	
	/** no index */
	private boolean noindex = false;
	
	/** no tree  */
	private boolean notree = false;
	
	/** verbose */
	private boolean verbose = false;
	
	/** errorFlag */
	private boolean errorFlag = false;
	
	/** char set */
	private String charset = null;
	
	/** over view */
	private File overview = null;
	
	/** stylesheetfile */
	private File stylesheetfile = null;
	
	/** junit tree */
	private String junittree = null;
	
	/** group factory */
	public final UT5GroupFactory groupFactory = new UT5GroupFactory();
}

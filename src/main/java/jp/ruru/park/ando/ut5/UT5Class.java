package jp.ruru.park.ando.ut5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;


/**
 * ClassDoc for JUnit
 * @author t-ando
 *
 */
public class UT5Class extends UT5 implements Comparable<UT5Class> {
	
	/**
	 * Constructor.
	 * Crate ClassDoc for JUNit.
	 * @param rootDoc rootDoc for JavaDoc
	 * @param rootElement classDoc for Element
	 */
	public UT5Class(RootDoc rootDoc,Element rootElement) {
		this(
				rootDoc,
				rootElement.getAttribute("name"),
				Double.parseDouble(rootElement.getAttribute("time")),
				UT5.createStringToDate(rootElement.getAttribute("timestamp")));
		
		this.setTests(Integer.parseInt(rootElement.getAttribute("tests")));
		this.setErrors(Integer.parseInt(rootElement.getAttribute("errors")));
		this.setSkipped(Integer.parseInt(rootElement.getAttribute("skipped")));
		this.setFailures(Integer.parseInt(rootElement.getAttribute("failures")));
		//
		ClassDoc classDoc = this.getClassDoc(rootDoc);
		//
		this.createListAnoMethod(rootDoc,classDoc);
		// 
		NodeList nodeList = rootElement.getChildNodes();
		if (nodeList == null) {
			return;
		}
		IntStream.range(0, nodeList.getLength())
		.mapToObj(nodeList::item)
		.filter(e->e instanceof Element)
		.map(e->(Element)e)
		.filter(e->e.getTagName().equals("testcase"))
		.forEach(e->this.getMethodList().add(new UT5Method(rootDoc,classDoc,e,this.getRawTimestamp(),this.getRawOldTimesamp())));
	}
	/**
	 * Constructor.
	 * Crate ClassDoc for JUNit.
	 * @param rootDoc rootDoc for JavaDoc
	 * @param name class name
	 * @param time time
	 * @param timestamp time stamp
	 */
	public UT5Class(RootDoc rootDoc,String name,double time,long timestamp) {
		super(name,time,timestamp);
		//
		ClassDoc classDoc = this.getClassDoc(rootDoc);
		//
		String message = this.findCdocString(rootDoc, classDoc);
		this.setMessage(createMultiMessage(message));
	}
	
	/**
	 * ClassDoc for JUnit to ClassDoc for Doclet.
	 * @param root root document.
	 * @return ClassDoc
	 */
	protected ClassDoc getClassDoc(RootDoc root) {
		//
		String realName = 0 == this.getName().indexOf(UT5GroupFactory.FINDBUGS_PACK)
				? this.getName().substring(UT5GroupFactory.FINDBUGS_PACK.length())
						: this.getName();
		List<ClassDoc> classDocList =
				Arrays.asList(root.classes()).stream()
				.filter(e->e.toString().equals(realName))
				.collect(Collectors.toList());
		if (classDocList.isEmpty()) {
			return null;
		}
		return classDocList.get(0);

	}
	
	/**
	 * get package name
	 * @return package name
	 */
	public String getPackageName() {
		int index = getName().lastIndexOf('.');
		if (index < 0) {
			return "";
		}
		return getName().substring(0,index);
	}
	
	/**
	 * get class name (exclude package)
	 * @return class name (exclude package)
	 */
	public String getClassName() {
		int index = getName().lastIndexOf('.');
		if (index < 0) {
			return getName();
		}
		return getName().substring(index+1,getName().length());
	}
	
	/**
	 * class for HTML style sheet
	 * @return class for HTML
	 */
	@Override
	public String getHtmlClass() {
		if (0 < failures) {
			return UT5Method.ST_FAIILURE;
		} else if (0 < errors) {
			return UT5Method.ST_ERROR;
		}
		return "";
	}
	
	/**
	 * crate UT5Case list for annotation.
	 * @param rootDoc rootDoc for JavaDoc
	 * @param classDoc ClassDoc for JavaDoc
	 */
	protected void createListAnoMethod(RootDoc rootDoc,ClassDoc classDoc) {
		Stream<UT5Method> all =
				Stream.concat(createListAnoMethod(rootDoc,classDoc,"BeforeClass"),
						createListAnoMethod(rootDoc,classDoc,"Before"));
		all = Stream.concat(all,createListAnoMethod(rootDoc,classDoc,"After"));
		all = Stream.concat(all,createListAnoMethod(rootDoc,classDoc,"AfterClass"));
		this.annoList.addAll(all.collect(Collectors.toList()));
	}
	
	/**
	 * crate UT5Case list for annotation.
	 * @param rootDoc rootDoc for JavaDoc
	 * @param classDoc classDoc for JavaDoc
	 * @param ano annotation.
	 * @return MethodDoc list for JUnit
	 */
	protected Stream<UT5Method> createListAnoMethod(RootDoc rootDoc,ClassDoc classDoc,String ano) {
		if (classDoc == null) {
			return Stream.empty();
		}
		ClassDoc superClass = classDoc.superclass();
		Stream<UT5Method> parent;
		if (superClass != null) {
			parent = createListAnoMethod(rootDoc,superClass,ano);
		} else {
			parent = Stream.empty();
		}
		List<MethodDoc> methodDocList =
				Arrays.asList(classDoc.methods()).stream()
				.filter(method->Arrays.asList(method.annotations()).stream()
						.map(annotation->annotation.annotationType())
						.map(annotationType->annotationType.toString())
						.anyMatch(string->string.equals("org.junit." + ano)))
				.collect(Collectors.toList());
		List<UT5Method> myCaseList = methodDocList.stream().map(method->
			(UT5Method)new UT5Method(rootDoc,classDoc,method.name(),"()",-1.0,"@" + ano,
					this.getRawTimestamp(),this.getRawOldTimesamp())).collect(Collectors.toList());
		return Stream.concat(parent, myCaseList.stream());
	}
	
	
	/**
	 * get the message for ClassDoc.
	 * @param root root document.
	 * @param classDoc ClassDoc
	 * @return message
	 */
	protected String findCdocString(RootDoc root,ClassDoc classDoc)  {
		if (classDoc == null) {
        	return null;
        }
		//
		String message = classDoc.commentText();
		if ((message != null) && (!message.equals(""))) {
			return message;
		}
		ClassDoc parent = classDoc.superclass();
		message = this.findCdocString(root,parent);
		if (message != null) {
			return message;
		}
		return null;
	}
	
	/**
	 * update old class
	 * @param oldClass old class
	 */
	public void updateOldClass(UT5Class oldClass) {
		this.setOldTimesamp(oldClass.getRawOldTimesamp());
		oldClass.getMethodList().stream()
		.forEachOrdered(oldMethod->
			this.getMethodList().stream()
			.filter(newMethod->oldMethod.getName().equals(newMethod.getName()))
			.forEachOrdered(newMethod->newMethod.setOldTimesamp(oldClass.getRawOldTimesamp())));
		//
		oldClass.getAnoMethodList().stream()
		.forEachOrdered(oldMethod->
			this.getAnoMethodList().stream()
			.filter(newMethod->oldMethod.getName().equals(newMethod.getName()))
			.forEachOrdered(newMethod->newMethod.setOldTimesamp(oldClass.getRawOldTimesamp())));
		//
	}
	
	/**
	 * get tests
	 * @return tests
	 */
	public int getTests() {
		return tests;
	}

	/**
	 * get errors
	 * @return errors
	 */
	public int getErrors() {
		return errors;
	}

	/**
	 * get skipped
	 * @return skipped
	 */
	public int getSkipped() {
		return skipped;
	}

	/** get failures
	 * @return failures
	 */
	public int getFailures() {
		return failures;
	}

	/**
	 * get MehotdDoc for JUnit
	 * @return MethodDoc for JUnit
	 */
	public List<UT5Method> getMethodList() {
		return methodList;
	}

	/**
	 * got the annotation method List
	 * @return MethodDoc for JUnit
	 */
	public List<UT5Method> getAnoMethodList() {
		return this.annoList;
	}
	
	/**
	 * set tests
	 * @param tests tests
	 */
	public void setTests(int tests) {
		this.tests = tests;
	}
	/**
	 * set errors
	 * @param errors errors
	 */
	public void setErrors(int errors) {
		this.errors = errors;
	}
	/**
	 * set skipped
	 * @param skipped skipped
	 */
	public void setSkipped(int skipped) {
		this.skipped = skipped;
	}
	/**
	 * set failures
	 * @param failures failures
	 */
	public void setFailures(int failures) {
		this.failures = failures;
	}
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(UT5Class o) {
		return getName().compareTo(o.getName());
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof UT5Class) {
			return this.getName().compareTo(((UT5Class)o).getName())== 0;
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
		
	/** tests */
	private int tests;
	
	/** errors */
	private int errors;
	
	/** skipped */
	private int skipped;
	
	/** failures */
	private int failures;
	
	/** MethodDoc list */
	private final List<UT5Method> methodList = new ArrayList<UT5Method>();
	
	/** annotation MethodDoc List */
	private List<UT5Method> annoList = new ArrayList<UT5Method>();

}

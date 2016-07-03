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
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Tag;

/**
 * MethodDoc for JUnit.
 * @author t-ando
 *
 */
public class UT5Method extends UT5 implements Comparable<UT5Method> {

	/** filed sign */
	public static final String FIELD_SIGN = "-";

	/**
	 * constructor 
	 * @param classDoc classDoc for JavaDoc
	 * @param name method name
	 * @param signature method signature
	 * @param time time
	 * @param status status
	 * @param timestamp time stamp
	 * @param oldTimeStamp old time stamp
	 */
	public UT5Method(ClassDoc classDoc,String name,String signature,double time,String status,long timestamp,long oldTimeStamp) {
		super(name,time,timestamp,oldTimeStamp);
		this.signature = signature;
		this.status = status;
		this.author = findTag(classDoc,"author");
		this.version = findTag(classDoc,"version");
		//
		String message = findMdocString(classDoc);
		this.setMessage(createMultiMessage(message));
		//
	}
	
	/**
	 * constructor 
	 * Crate MethodDoc for JUNit.
	 * @param classDoc classDoc for JavaDoc
	 * @param element MethodDoc for Element
	 * @param timestamp time stamp
	 * @param oldTimeStamp old time stamp
	 */
	public UT5Method(ClassDoc classDoc,Element element,long timestamp,long oldTimeStamp) {
		this(classDoc,element.getAttribute("name"),
				"()",
				Double.valueOf(element.getAttribute("time")),
				ST_SUCCES,
				timestamp,oldTimeStamp);
		//
		NodeList nodeList = element.getChildNodes();
		if (nodeList == null) {
			return;
		}
		List<Element> targetList = IntStream.range(0, nodeList.getLength())
		.mapToObj(nodeList::item)
		.filter(e->e instanceof Element)
		.map(e->(Element)e).collect(Collectors.toList());
		//
		plusMessage = "";
		for (Element target: targetList) {
			switch (target.getTagName()) {
			case "failure": // attention! this is tag!
				status = ST_FAIILURE;
				plusMessage = target.getAttribute("type");
				break;
			case "skipped": // attention! this is tag!
				status = ST_SKIPPED;
				plusMessage = target.getAttribute("");
				break;
			case "error": // attention! this is tag!
				status = ST_ERROR;
				plusMessage = target.getAttribute("message");
				break;
			default:
				break;
			}
		}
		// HTML to String.
		plusMessage = plusMessage.replaceAll("&", "&amp;");
		plusMessage = plusMessage.replaceAll("<", "&lt;");
		plusMessage = plusMessage.replaceAll(">", "&gt;");
		//
		//
		if (getMessage() == null) {
			setMessage(plusMessage);
		} else if ( ! plusMessage.equals("")) {
			setMessage(getMessage() + "\n\n"+ plusMessage);
		}
	}


	/**
	 * get the message for MethodDoc.
	 * @param classDoc ClassDoc for JavaDoc.
	 * @return message
	 */
	protected String findMdocString(ClassDoc classDoc)  {
		if (classDoc == null) {
			return null;
		}
		//
		// this is field!
		if (this.getSignature().equals(UT5Method.FIELD_SIGN)) {
			for (FieldDoc doc : classDoc.fields()) {
				if (this.getName().equals(doc.name())) {
					// hit!
					String message = doc.commentText();
					if ((message != null) && (!message.equals(""))) {
						return message;
					}
					break;
				}
			}
			return "This if field.";
		}
		//
		// this is constructor
		if (this.getName().equals(classDoc.name())) {
			//
			for (ConstructorDoc constrcutr : classDoc.constructors()) {
				if (this.getSignature().equals(constrcutr.signature())) {
					//hit!
					String message = constrcutr.commentText();
					if ((message != null) && (!message.equals(""))) {
						return message;
					}
					break;
				}
			}
			//
			return "This is constructor.";
		}
		//
		// this is method
		List<MethodDoc> mdocList = findMdocList(classDoc).collect(Collectors.toList());
		//
		String oversee = null;
		boolean overrideTag = false;
		for (MethodDoc mdoc : mdocList) {
			//System.out.println("Signtture m=" + mdoc.name() + " sig=" + mdoc.signature());
			String message = mdoc.commentText();
			if ((message != null) && (!message.equals(""))) {
				return message;
			}
			if (mdoc.containingClass() != classDoc) {
				oversee = "@see " + mdoc.containingClass().qualifiedName() + "#" + getName();
			}
			Arrays.asList(mdoc).stream().forEachOrdered(e->System.out.println("e->" + e));
			Tag[] tag = mdoc.tags("java.lang.Override");
			if ((tag != null) && (0 < tag.length)) {
				overrideTag = true;
			}
		}
		if (oversee != null) {
			return oversee;
		}
		if (overrideTag) {
			return "@Override";
		}
		return null;
	}
	
	/**
	 * get the message for MethodDoc.
	 * @param classDoc ClassDoc for JavaDoc.
	 * @return message
	 */
	protected Stream<MethodDoc> findMdocList(ClassDoc classDoc)  {
		// this is method
		List<MethodDoc> mdocList = 
				Arrays.asList(classDoc.methods()).stream()
				.filter(mdoc->mdoc.signature().equals(getSignature())) // for test case
				.filter(mdoc->mdoc.name().equals(this.getName()))
				.collect(Collectors.toList());
		

		List<ClassDoc> parentList = new ArrayList<ClassDoc>();
		if (classDoc.superclass() != null) {
			parentList.add(classDoc.superclass());
		}
		if (classDoc.interfaces() != null) {
			parentList.addAll(Arrays.asList(classDoc.interfaces()));
		}
		//
		mdocList = 
				Stream.concat(
						mdocList.stream(),
						parentList.stream().flatMap(parent->findMdocList(parent)))
				.collect(Collectors.toList());
		//
		return mdocList.stream();
	}
	
	/**
	 * find parameter to parameter message 
	 * @param classDoc ClassDoc for JavaDoc.
	 * @param tagname tag name
	 * @return tag message
	 */
	protected String findTag(ClassDoc classDoc,String tagname) {
		if (classDoc == null) {
			return "";
		}
		List<MethodDoc> mdocList = this.findMdocList(classDoc).collect(Collectors.toList());
		//
		for (MethodDoc mdoc : mdocList) {
			for (Tag tag : mdoc.tags(tagname)) {
				return tag.text();
			}
		}
		//
		for (Tag tag : classDoc.tags(tagname)) {
			return tag.text();
		}
		//
		String result = findTag(classDoc.superclass(),tagname);
		if ((result != null) && (!result.equals(""))) {
			return result;
		}
		for (ClassDoc near : classDoc.interfaces()) {
			result = findTag(near,tagname);
			if ((result != null) && (!result.equals(""))) {
				return result;
			}
		}
		return "";
	}
	
	/**
	 * set status 
	 * @param status status
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * get status 
	 * @return status
	 */
	public String getStatus() {
		return status == null ? "null" : status;
	}

	/**
	 * class for HTML style sheet
	 * @return class for HTML
	 */
	@Override
	public String getHtmlClass() {
		if (status.equals(ST_FAIILURE)) {
			return ST_FAIILURE;
		}
		if (status.equals(ST_ERROR)) {
			return ST_ERROR;
		}
		return "";
	}
	
	/**
	 * get author 
	 * @return author
	 */
	public String getAuthor() {
		return author == null ? "" : author;
	}
	/**
	 * get small author 
	 * @return author
	 */
	public String getSmallAuthor() {
		String smallAuthor = this.getAuthor();
		int index = smallAuthor.indexOf('@');
		if (0 < index) {
			smallAuthor = smallAuthor.substring(0,index);
		}
		return smallAuthor;
	}
	/**
	 * get version 
	 * @return version
	 */
	public String getVersion() {
		return version == null ? "" : version;
	}
	
	/**
	 * get signature
	 * @return signature
	 */
	public String getSignature() {
		return this.signature;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(UT5Method o) {
		int result = getName().compareTo(o.getName());
		if (result == 0) {
			result = signature.compareTo(o.signature);
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof UT5Method) {
			return this.compareTo((UT5Method)o)== 0;
		}
		return super.equals(o);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		  return this.getName().hashCode() + this.signature.hashCode();
	}
	/** signature */
	private final String signature;
	
	/** status */
	private String status;

	/** author */
	private String author;

	/** version */
	private String version;
	
	/** plus message */
	private String plusMessage;

}

/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : MerInfoBaseChgProc
*   작성자     : (주)미디어포스 권영만
*   내용        : 비씨탑 시스템 설정 정보. (가맹점용)
*   적용범위  : Golf
*   작성일자  : 2009-04-08
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
* 
***************************************************************************************************/
package com.bccard.golf.common.config;

import java.util.HashMap;
import java.util.Properties;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.bccard.waf.core.ConfigConstants;
import com.bccard.waf.resource.Resource;
import com.bccard.waf.resource.ResourceFactory;



/******************************************************************************
* Golf : ConfigResource
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/


public class ConfigResource extends Resource {
	/** XML 파서명 */
	private String parserName;
	private Boolean ing;

	/******************************************************************************
	 * CodeResourceFactory 와 properties 정보로부터 액션자원 생성.
	 * @param factory ResourceFactory
	 * @param properties 부가설정.
	 **************************************************************************** */
	ConfigResource(ResourceFactory factory, Properties properties) {
		super( factory.getConfig() );
		this.ing = new Boolean(false);
		try {
			this.parserName = properties.getProperty(ConfigConstants.PROPERTY_XML_SAX_PARSER,ConfigConstants.DEFAULT_XML_SAX_PARSER);
		} catch(Throwable t) {}
		init();
	} 

	/******************************************************************************
	 * XML 파일에서 자원 로드.
	 **************************************************************************** */
	public void init() {
		synchronized ( this.ing ) {
			this.ing = new Boolean(true);
			resourceMap = new HashMap();

			XmlHandler handler = new XmlHandler();
			try{
				if ( this.parserName == null ) this.parserName = ConfigConstants.DEFAULT_XML_SAX_PARSER;
				XMLReader parser = (XMLReader)Class.forName(this.parserName).newInstance();
				parser.setContentHandler( handler );
				parser.parse( config );
			} catch( Throwable t ) {
				log("ConfigResource \"" + config + "\" load failed." ,t);
			}
			this.ing = new Boolean(false);
		}
	}

	/******************************************************************************
	 * XML Parsing 을 위한 SubClass.
	 **************************************************************************** */
	class XmlHandler extends DefaultHandler {
		private String pKey;
		private StringBuffer contBuff;

		/**
		 * @return void
		 */
		public void startDocument() throws SAXException {}

		/**
		 * @return void 
		 */
		public void endDocument() throws SAXException {}

		/**
		 * @param String uri 
		 * @param String localName
		 * @param String qName
		 * @param Attributes atts
		 * @return void 
		 */
		public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
			if ( "property".equalsIgnoreCase(localName) ) {
				String key = atts.getValue(atts.getIndex("key"));
				if ( key == null ) throw new SAXException("property 태그의 key 요소가 정의되어 있지 않습니다.");
				contBuff = new StringBuffer();
				pKey = key;

			}
		}

		/**
		 * @param String uri 
		 * @param String localName
		 * @param String qName
		 * @return void 
		 */
		public void endElement(String uri, String localName,String qName) throws SAXException {
			if ( "property".equalsIgnoreCase(localName) ) {
				resourceMap.put(pKey, contBuff.toString() );
				pKey = null;
				contBuff = null;
			}
		}

		/**
		 * @param char ch[] 
		 * @param int start
		 * @param int length
		 * @return void 
		 */
		public void characters( char[] ch, int start, int len ) {
			try { contBuff.append(new String(ch, start, len) ); } catch(Throwable t) { }
		}

		/**
		 * @param char ch[] 
		 * @param int start
		 * @param int length
		 * @return void
		 */
		public void ignorableWhitespace(char ch[], int start, int length ) throws SAXException{}
	}

}

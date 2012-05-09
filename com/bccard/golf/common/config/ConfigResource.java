/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : MerInfoBaseChgProc
*   �ۼ���     : (��)�̵������ �ǿ���
*   ����        : ��ž �ý��� ���� ����. (��������)
*   �������  : Golf
*   �ۼ�����  : 2009-04-08
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
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
* @author	(��)�̵������
* @version	1.0
******************************************************************************/


public class ConfigResource extends Resource {
	/** XML �ļ��� */
	private String parserName;
	private Boolean ing;

	/******************************************************************************
	 * CodeResourceFactory �� properties �����κ��� �׼��ڿ� ����.
	 * @param factory ResourceFactory
	 * @param properties �ΰ�����.
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
	 * XML ���Ͽ��� �ڿ� �ε�.
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
	 * XML Parsing �� ���� SubClass.
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
				if ( key == null ) throw new SAXException("property �±��� key ��Ұ� ���ǵǾ� ���� �ʽ��ϴ�.");
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

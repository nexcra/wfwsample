/**
 ***************************************************
 *  �� �ҽ��� �ߺ�ī�� �����Դϴ�. 
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
 *  Ŭ������	:   AppConfig.java 
 *  �ۼ���		:   e4net
 *  ����		:   ������Ƽ ������ �о����
 *  �������	:   etax
 *  �ۼ�����	:  2008.01.04
 ************************** �����̷� *****************
 * ����            ������         ������� 
 *
 ****************************************************
 */

package com.bccard.golf.common;
   
import java.io.IOException;
import java.util.Properties;

/**  
 * ������Ƽ ������ �о����
 * @version 2008.01.04
 * @author e4net
 */ 
public class AppConfig { 
    
	/** ������Ƽ ���ϸ� */
    private static final String PROPERTY_FILENAME = "golf.properties";
    private static final String DATACODE = "golfDataCode.properties";

	public final static String FILE_RECEIPT_DIR	 = "/BCWEB/DOWNLOAD/bcext/golfloung/";	 // �ٿ�ε� ���丮

    private static Properties appProp = null;
    private static Properties appDataProp = null;

	/**
    * getAppProperty
    * @param property String Object
    * @version 2006.12.27
 	* @author e4net
    * @return String Object      
    */		
    public static String getAppProperty(String property) throws IOException {
        if ( appProp == null ) {
            appProp = new Properties();
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if ( classLoader == null ) classLoader = AppConfig.class.getClassLoader();
            appProp.load( classLoader.getResourceAsStream(PROPERTY_FILENAME) );
        }
        
      //���ϰ˼� 2009.9.15 
        property = property.replaceAll(".jsp","txt").replaceAll(".asp",".txt").replaceAll(".php",".txt").replaceAll("\\.\\.","");
        
        return appProp.getProperty(property);
    }
    
    public static String getDataCodeProp(String property) throws IOException {
        if ( appDataProp == null ) {
        	appDataProp = new Properties();
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if ( classLoader == null ) classLoader = AppConfig.class.getClassLoader();
            appDataProp.load( classLoader.getResourceAsStream(DATACODE) );
        }
        
      //���ϰ˼� 2009.9.15 
        property = property.replaceAll(".jsp","txt").replaceAll(".asp",".txt").replaceAll(".php",".txt").replaceAll("\\.\\.","");
        
        return appDataProp.getProperty(property);
    }    

}

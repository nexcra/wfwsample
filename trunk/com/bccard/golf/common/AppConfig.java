/**
 ***************************************************
 *  이 소스는 ㈜비씨카드 소유입니다. 
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 *  클래스명	:   AppConfig.java 
 *  작성자		:   e4net
 *  내용		:   프로퍼티 설정값 읽어오기
 *  적용범위	:   etax
 *  작성일자	:  2008.01.04
 ************************** 수정이력 *****************
 * 일자            수정자         변경사항 
 *
 ****************************************************
 */

package com.bccard.golf.common;
   
import java.io.IOException;
import java.util.Properties;

/**  
 * 프로퍼티 설정값 읽어오기
 * @version 2008.01.04
 * @author e4net
 */ 
public class AppConfig { 
    
	/** 프로퍼티 파일명 */
    private static final String PROPERTY_FILENAME = "golf.properties";
    private static final String DATACODE = "golfDataCode.properties";

	public final static String FILE_RECEIPT_DIR	 = "/BCWEB/DOWNLOAD/bcext/golfloung/";	 // 다운로드 디렉토리

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
        
      //보완검수 2009.9.15 
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
        
      //보완검수 2009.9.15 
        property = property.replaceAll(".jsp","txt").replaceAll(".asp",".txt").replaceAll(".php",".txt").replaceAll("\\.\\.","");
        
        return appDataProp.getProperty(property);
    }    

}

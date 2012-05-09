/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : JunMoonProcFactory
*   작성자    : (주)미디어포스 이경희
*   내용      : 전문 프로세스 팩토리
*   적용범위  : golf
*   작성일자  : 2011-01-12
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.factory;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.bccard.waf.core.Code;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.logging.WaLogger;

/**
 * @author Administrator
 *
 */
public class JunMoonProcFactory {
	
	ResourceBundle serNameProp  = null ;
	ResourceBundle paraQProp  = null ;	
	
	public JunMoonProcFactory() {
		// TODO Auto-generated constructor stub
	}
	
	private transient Logger logger;
	
    private void initLogger() {
        logger = (Logger)WaLogger.getLogger(this.getClass().getName());
    }	
	
    protected void debug(String log) {
        if ( logger == null ) initLogger();
        logger.debug(log);
    }    
	
	public JunMoonFactory getJumMoon(WaContext context){
		
		JunMoonFactory jmf = null;
		String type = "";	//기본전문호출방식 (J:Jolt, M:MCA)

		Code junMoType = context.getCode("10","GOLFLOUNG");
    	type = junMoType.getDetail();   	
    	type = "J";
    	try {
    		
			if (type.equals("J")){
				debug ("******jolt________________start----------");
				serNameProp = ResourceBundle.getBundle("oldJunMoon");
				paraQProp = ResourceBundle.getBundle("oldJMReqPar");				
				jmf = new JoltFactory(serNameProp, paraQProp);
				
			}else if (type.equals("M")){
				debug ("******MCA________________start--------------");
				serNameProp = ResourceBundle.getBundle("newJunMoon");
				paraQProp = ResourceBundle.getBundle("newJMReqPar");				
				jmf = new MCAFactory(serNameProp, paraQProp);
				
			}
			
		} catch (Exception e) {				
			e.printStackTrace();
		}	

		return jmf;
		
	}

}

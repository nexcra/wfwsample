/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : JunMoonProcess
*   작성자    : (주)미디어포스 이경희
*   내용      : 전문 프로세스
*   적용범위  : golf
*   작성일자  : 2011-01-18
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.common;

import java.util.HashMap;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.bccard.waf.core.WaContext;
import com.bccard.waf.logging.WaLogger;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.mca.McaInput;




/**
 * @author Administrator
 *
 */
public class JunMoonProc {
	
	private transient Logger logger;
	ResourceBundle serNameProp;
	ResourceBundle paraQProp;
	ResourceBundle paraSProp;		

	/**
	 * 
	 */
	public JunMoonProc() {
		
		serNameProp = ResourceBundle.getBundle("JeonMoon");
		paraQProp = ResourceBundle.getBundle("JMReqPar");
		paraSProp = ResourceBundle.getBundle("JMResPar");
	
	}
	
    private void initLogger() {
        logger = (Logger)WaLogger.getLogger(this.getClass().getName());
    }	
	
    protected void debug(String log) {
        if ( logger == null ) initLogger();
        logger.debug(log);
    }	
	
	public HashMap procss(WaContext context, HashMap hmap) {

		TaoConnection tcon = null;
		HashMap hm = new HashMap();	
		
		
		debug ("__MCA start__");
		
		try {	
			
			tcon = context.getTaoConnection("mca",null);			
			
			// 1. 요청 전문 데이터 설정
			McaInput input = new McaInput();
			
			Object [] setStr = hmap.keySet().toArray();		
			debug ("__MCA 2__" + setStr.length);			

			for(int i=0; i<setStr.length; i++){			

				if ( !setStr[i].toString().equals("serName") ){
					
					input.setString( paraQProp.getString(setStr[i].toString()), hmap.get(setStr[i]).toString() );
					debug ("__MCA setStr["+i+"] : [" +setStr[i] +"], [" 
							+ hmap.get(setStr[i]).toString()+"], ["+ paraQProp.getString(setStr[i].toString()) +"]");					

				}
				
			}				
			debug ("__MCA 444__" + hmap.get("serName").toString());
			debug ("__MCA 4440__" + serNameProp.getString(hmap.get("serName").toString()) );
			// 2. 서비스 호출 hmap.put("serName", "JM1002");//전문명
//			 /serNameProp.getString(hmap.get("serName").toString());
			 
			//TaoResult output = tcon.execute(hmap.get("serName").toString() ,input);
			TaoResult output = tcon.execute(serNameProp.getString(hmap.get("serName").toString()) ,input);
			//TaoResult output = tcon.execute("AA55" ,input);
			debug ("__MCA 4__" + output.size());
			
			
			debug ("__MCA 5__[" + output.getString("HEADER.errorCode") +"]");
			debug ("__MCA 52__[" + output.getString("HEADER.EerrorCode") +"]");
			
			// 3. 
			
			//if ( output.getString("HEADER.errorCode") != null || output.getString("HEADER.errorCode") != "") {
			
				if (output.getString("HEADER.errorCode").equals("0")){
					debug ("정상처리");
					hm.put("value",output);
				}else if (output.getString("HEADER.errorCode").equals("-1")){
					debug ("MCA 연동 시 시스템 오류가 발생하였습니다. HEADER.errorCode -1 : " );
					throw new TaoException();
				}else if (output.getString("HEADER.errorCode").equals("-9999")){
					hm.put("value",output); 
					
					debug ("카드계 서비스에서 오류가 발생하였습니다. HEADER.MSG_CD : " 
							+ output.getString("HEADER.MSG_CD") + ", HEADER.MSG_CNTNT : " + output.getString("HEADER.MSG_CNTNT"));
					
					//throw new TaoException();
					//System.exit(-1);
				} else {
					hm.put("value",output);
					debug ("MCA 연동 시 시스템 오류가 발생하였습니다.  error : [" + output.getString("HEADER.errorCode") + "]");
					//throw new TaoException();
				}
			
			//}
			
			debug ("+++++++++++++++++++ output : " + output);
			//if (output.isNext())  //containsKey
				
			//debug ("+++++++++++++++++++ containsKey : " + output.containsKey(paraSProp.getString("paraS2000")));
			//debug ("+++++++++++++++++++ paraS2000 : " + output.getString(paraSProp.getString("paraS2000")));
			
			//output.getString(paraSProp.getString("paraS2000"));
			//resultCode_pt = output.getString(paraSProp.getString("paraS2000"));//응답코드
			
//			while( output.isNext() ) {
//				output.next();
////				/output.getString("rspn_cd");
//				debug ("+++++++++++++++++++ rspn_cd : " + output.getString("rspn_cd"));
//				debug ("+++++++++++++++++++ paraS1001 : " + output.getString(paraSProp.getString("paraS1001")));
//				
//				}
			
			
			//hm.put("value",output);
			debug ("+++++++++++++++++++ end");
			
		} catch(TaoException te) {
			te.getMessage();
			te.printStackTrace();

			debug ("+++++++++++++++++++ error 1 : " + te.getMessage());
			logger.error("+++++++++++++++++++ error 111", te);

		} catch (Exception e) {
			e.getMessage();
			e.printStackTrace();
			debug ("+++++++++++++++++++ error 2 : " + e.getMessage());
			logger.error("+++++++++++++++++++ error 222", e);
		} finally {
			try {
				if (tcon!=null) tcon.close(); 
			} catch(Throwable ignore) {}
		}		
		
		return hm;
	}
	
	
	public String getType(){
		
//		Code junMoType = context.getCode("10","GOLFLOUNG");    	    	
//		return junMoType.getDetail();
		
		String junMoType = "";
		junMoType = "M";				
		//junMoType = "J";
		return junMoType;
		
	}		

	public ResourceBundle getProperties(){    	    	
		return paraSProp;
	}	

}
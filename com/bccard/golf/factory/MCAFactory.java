/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : MCAFactory
*   작성자    : (주)미디어포스 이경희
*   내용      : MCA Factory
*   적용범위  : golf
*   작성일자  : 2011-01-12
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.factory;

import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.bccard.waf.core.WaContext;
import com.bccard.waf.logging.WaLogger;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;
import com.bccard.waf.tao.mca.McaInput;



/**
 * @author Administrator
 *
 */
public class MCAFactory extends JunMoonFactory{
	
	
	private transient Logger logger;
	ResourceBundle serNameProp;
	ResourceBundle paraQProp;	
	
    private void initLogger() {
        logger = (Logger)WaLogger.getLogger(this.getClass().getName());
    }	
	
    protected void debug(String log) {
        if ( logger == null ) initLogger();
        logger.debug(log);
    }

	/**
	 * 
	 */
	public MCAFactory(ResourceBundle serNameProp, ResourceBundle paraQProp) {
		this.serNameProp = serNameProp;
		this.paraQProp = paraQProp;		
	}

	
	public HashMap procss(WaContext context, HttpServletRequest request,
			Properties prop1, HashMap hmap) {

		TaoConnection tcon = null;
		HashMap hm = new HashMap();
		debug ("******MCA________________start----");
		try {	
			debug ("******MCA________________0_________");
			tcon = context.getTaoConnection("mca",null);
			debug ("******MCA________________1_________");
			// 1. 요청 전문 데이터 설정
			McaInput input = new McaInput();
//			input.setString("indv_corp_clcd","1");
//			input.setString("jumin_no",jumin_no);
//			input.setString("cotn_inqr_key",next_key);
//			input.setString("svc_clss","3");
			
			Object [] setStr = hmap.keySet().toArray();		
			debug ("******MCA________________2_________ : " + setStr.length);

			for(int i=0; i<setStr.length; i++){			

				if ( setStr[i].toString().equals("fml_trcode") ){
					input.setString(setStr[i].toString(), serNameProp.getString(hmap.get(setStr[i]).toString()));
					debug ("******MCA________________1 setStr["+i+"] : [" +setStr[i] +"], [" + setStr[i].toString()+"], [" 
							+ hmap.get(setStr[i]).toString()+"], ["+ serNameProp.getString(hmap.get(setStr[i]).toString()) +"]");
					
				}else if ( setStr[i].toString().equals("serName") ){
					input.setString(setStr[i].toString(), "BSNINPT");		
					debug ("*****MCA________________2 setStr["+i+"] : [" +setStr[i] +"], [" + setStr[i].toString()+"]");
					
				}else {
					input.setString( paraQProp.getString(setStr[i].toString()), hmap.get(setStr[i]).toString() );					
					debug ("*****MCA________________3 setStr["+i+"] : [" +setStr[i] +"], [" + setStr[i].toString()+"], [" 
							+ hmap.get(setStr[i]).toString()+"], ["+ paraQProp.getString(setStr[i].toString()) +"]");
				}
				
			}				
			
			// 2. 서비스 호출
			//TaoResult output = tcon.execute("${Service}",input);
			TaoResult output = tcon.execute(hmap.get("serName").toString() ,input);
			
			// 3. 응답 전문 데이터 조회
			// 단건 컬럼 조회
//			output.getString("cotn_data");
//			// 복수건 컬럼 조회
//			while( output.isNext() ) {
//				output.next();
//				output.getString("cotn_data");
//			}
			
			hm.put("value",output);	
		

		} catch(TaoException te) {
			te.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (tcon!=null) tcon.close(); 
			} catch(Throwable ignore) {}
		}		
		
		return null;
	}

}

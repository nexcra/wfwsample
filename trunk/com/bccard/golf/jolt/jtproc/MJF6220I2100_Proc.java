/***************************************************************************************************
*   클래스명  : MJF6220I2100_Proc.java(탑포인트 전환신청)
*   작성자    : 강선영
*   내용      : 탑포인트 전환신청
*   적용범위  : golfloung
*   작성일자  : 2009.07.03
***************************************************************************************************/
package com.bccard.golf.jolt.jtproc;

import com.bccard.waf.tao.jolt.JoltInput;
import com.bccard.waf.tao.jolt.JoltOutput;
import com.bccard.waf.tao.TaoException;
import com.bccard.golf.jolt.JtTransactionProc;


/** ***************************************
* renewal 
********************************************** */
public class MJF6220I2100_Proc extends JtTransactionProc {


/** ******************************************************************************** 
* @version 2009.07.03 
* @param input JoltInput객체.
* @param output JoltOutput객체.
* @return  boolean 
***********************************************************************************/ 
	public boolean decision(JoltInput input, JoltOutput output) throws TaoException{
		boolean commit	= false;
		String rtnCode	= output.getString("fml_ret1").trim();
					
		if ("0000".equals(rtnCode)) {
			//super.debug("********** MJF6220I2100_Proc(탑포인트 전환신청) 성공 *************");
			commit = true;
		}else{
			//super.debug("********** MJF6220I2100_Proc(탑포인트 전환신청) 실패 *************");
		}
		return commit;
	}


/** ******************************************************************************** 
* @version 2008.07.25 
* @param input JoltInput객체.
* @param output JoltOutput객체.
* @return  String 
********************************************************************************** */ 
	public String getErrorCode(JoltInput input, JoltOutput output){
		try{
			return output.getString("fml_ret1").trim();
		}catch(TaoException e){
			return super.SYS_ERROR;
		}
	}

}

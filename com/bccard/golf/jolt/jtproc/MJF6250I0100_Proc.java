/***************************************************************************************************
*   클래스명  : MJF6250I0100_Proc.java(포인트 취소)
*   작성자    : 강선영[yskkang@bccard.com
*   내용      : 
*   적용범위  : golfloung
*   작성일자  : 2009.07.03
***************************************************************************************************/
package com.bccard.golf.jolt.jtproc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.AbstractEntity;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoException;

import com.bccard.waf.tao.jolt.JoltInput;
import com.bccard.waf.tao.jolt.JoltOutput;
import com.bccard.golf.jolt.JtTransactionProc;


/** ***************************************
* renewal 
********************************************** */
public class MJF6250I0100_Proc extends JtTransactionProc {


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
			//super.debug("********** MJF6250I0100_Proc(포인트 취소) 성공 *************");
			commit = true;
		}else{
			//super.debug("********** MJF6250I0100_Proc(포인트 취소) 실패 *************");
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

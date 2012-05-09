/***************************************************************************************************
*   클래스명  : UGA003_TE_CANProc.java
*   작성자    : csj007
*   내용      : JOLT Proc
*   적용범위  : etax
*   작성일자  : 2008.07.25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
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

/** ****************************************************************************
 * Commonly Usable Proc Class for BCCARD TUXEDO SERVICE.
 ******************************************************************************/

/** ***************************************
* renewal 
* @version 2008.07.25 
* @author 채상직
********************************************** */

public class UGA003_TE_CANProc extends JtTransactionProc {


/** ******************************************************************************** 
* 기업
* @version 2008.07.25 
* @author 채상직
* @param input JoltInput객체.
* @param output JoltOutput객체.
* @return  boolean 
***********************************************************************************/ 
	public boolean decision(JoltInput input, JoltOutput output) throws TaoException{
		boolean commit = false;

		String retCode ="";
		if(output.containsKey("fml_ret2") ){
			retCode = output.getString("fml_ret2").trim();
			debug("JOLT RESPOND CODE = "+retCode);
		}
		if("2".equals(retCode)){
			commit = true;
		}
		return commit;
	}


/** ******************************************************************************** 
* 기업
* @version 2008.07.25 
* @author 채상직
* @param input JoltInput객체.
* @param output JoltOutput객체.
* @return  String 
********************************************************************************** */ 
	public String getErrorCode(JoltInput input, JoltOutput output){
		try{
			return output.getString("fml_ret2").trim();
		}catch(TaoException e){
			return super.SYS_ERROR;
		}
	}
}

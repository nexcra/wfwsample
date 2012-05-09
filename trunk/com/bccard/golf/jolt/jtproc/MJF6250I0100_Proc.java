/***************************************************************************************************
*   Ŭ������  : MJF6250I0100_Proc.java(����Ʈ ���)
*   �ۼ���    : ������[yskkang@bccard.com
*   ����      : 
*   �������  : golfloung
*   �ۼ�����  : 2009.07.03
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
	* @param input JoltInput��ü.
	* @param output JoltOutput��ü.
	* @return  boolean 
	***********************************************************************************/ 
	public boolean decision(JoltInput input, JoltOutput output) throws TaoException{
		boolean commit	= false;
		String rtnCode	= output.getString("fml_ret1").trim();
					
		if ("0000".equals(rtnCode)) {
			//super.debug("********** MJF6250I0100_Proc(����Ʈ ���) ���� *************");
			commit = true;
		}else{
			//super.debug("********** MJF6250I0100_Proc(����Ʈ ���) ���� *************");
		}
		return commit;
	}

	/** ******************************************************************************** 
	* @version 2008.07.25 
	* @param input JoltInput��ü.
	* @param output JoltOutput��ü.
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

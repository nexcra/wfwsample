/***************************************************************************************************
*   Ŭ������  : UGA003_TE_CANProc.java
*   �ۼ���    : csj007
*   ����      : JOLT Proc
*   �������  : etax
*   �ۼ�����  : 2008.07.25
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
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
* @author ä����
********************************************** */

public class UGA003_TE_CANProc extends JtTransactionProc {


/** ******************************************************************************** 
* ���
* @version 2008.07.25 
* @author ä����
* @param input JoltInput��ü.
* @param output JoltOutput��ü.
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
* ���
* @version 2008.07.25 
* @author ä����
* @param input JoltInput��ü.
* @param output JoltOutput��ü.
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

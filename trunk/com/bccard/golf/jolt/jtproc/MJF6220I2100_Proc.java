/***************************************************************************************************
*   Ŭ������  : MJF6220I2100_Proc.java(ž����Ʈ ��ȯ��û)
*   �ۼ���    : ������
*   ����      : ž����Ʈ ��ȯ��û
*   �������  : golfloung
*   �ۼ�����  : 2009.07.03
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
* @param input JoltInput��ü.
* @param output JoltOutput��ü.
* @return  boolean 
***********************************************************************************/ 
	public boolean decision(JoltInput input, JoltOutput output) throws TaoException{
		boolean commit	= false;
		String rtnCode	= output.getString("fml_ret1").trim();
					
		if ("0000".equals(rtnCode)) {
			//super.debug("********** MJF6220I2100_Proc(ž����Ʈ ��ȯ��û) ���� *************");
			commit = true;
		}else{
			//super.debug("********** MJF6220I2100_Proc(ž����Ʈ ��ȯ��û) ���� *************");
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

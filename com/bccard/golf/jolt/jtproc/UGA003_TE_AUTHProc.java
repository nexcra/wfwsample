/***************************************************************************************************
*   Ŭ������  : UGA003_TE_AUTHProc.java
*   �ۼ���    : csj007
*   ����      : JOLT Proc
*   �������  : etax
*   �ۼ�����  : 2008.07.25
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.jolt.jtproc;

import com.bccard.golf.jolt.JtTransactionProc;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.jolt.JoltInput;
import com.bccard.waf.tao.jolt.JoltOutput;

/** **************************************************************************** 
 * Commonly Usable Proc Class for BCCARD TUXEDO SERVICE.
 **************************************************************************** */

/** ***************************************
* renewal 
* @version 2008.07.25 
* @author ä����
********************************************** */

public class UGA003_TE_AUTHProc extends JtTransactionProc {


/** ******************************************************************************** 
* ���
* @version 2008.07.25 
* @author ä����
* @param input JoltInput��ü.
* @param output JoltOutput��ü.
* @return  boolean 
***********************************************************************************/ 
/*	public boolean decision(JoltInput input, JoltOutput output) throws TaoException{
		boolean commit = false;

		String retCode ="";
		if(output.containsKey("fml_ret5") ){
			retCode = output.getString("fml_ret5").trim();
			debug("JOLT RESPOND CODE = "+retCode);
		}
		if("1".equals(retCode)){
			commit = true;
		}
		return commit;
	}*/
	/**
	 * UGA003_TE_AUTHProc ���� �����ڵ� Ret5 �������� = 1
	 * @version 2004 02 17
	 * @param input  JoltInput
	 * @param output  JoltOutput 
	 * @author  PARK SUNG WOO
	 * @return  boolean
	 */
	public boolean decision(JoltInput input, JoltOutput output)
		throws TaoException {
		
		boolean commit = true;
		String retCode = output.getString("fml_ret5").trim();
		//String bankCode = input.getString("fml_arg3").substring(4, 6);

		/*
		 * ���� ���� ������. ������ commit �� ��Ű�� 30,47,88 �϶��� rollback ��. 2005.08.01 ����
		 * �������� : ����(06),�츮(20,22,24),�ϳ�(33),�ѹ�(27,36), ����(25) , ����(21), ���(03),����(11,12)
		 *
		 * ���� �ڵ�
		 *      - 30: ���� format Error
		 *      - 47: Db rowlock
		 *      - 88: System Error
		 */
		debug("## UGA003_TE_AUTHProc JOLT RESPOND CODE = "+retCode);
		if ("30".equals(retCode)
			|| "47".equals(retCode)
			|| "88".equals(retCode)) {
			commit = false;
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
			return output.getString("fml_ret1").trim();
		}catch(TaoException e){
			return super.SYS_ERROR;
		}
	}

}

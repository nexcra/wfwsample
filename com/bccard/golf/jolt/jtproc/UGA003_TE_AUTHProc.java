/***************************************************************************************************
*   클래스명  : UGA003_TE_AUTHProc.java
*   작성자    : csj007
*   내용      : JOLT Proc
*   적용범위  : etax
*   작성일자  : 2008.07.25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
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
* @author 채상직
********************************************** */

public class UGA003_TE_AUTHProc extends JtTransactionProc {


/** ******************************************************************************** 
* 기업
* @version 2008.07.25 
* @author 채상직
* @param input JoltInput객체.
* @param output JoltOutput객체.
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
	 * UGA003_TE_AUTHProc 전문 응답코드 Ret5 정상응답 = 1
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
		 * 독자 비독자 없어짐. 무조건 commit 을 시키고 30,47,88 일때만 rollback 함. 2005.08.01 수정
		 * 독자은행 : 주택(06),우리(20,22,24),하나(33),한미(27,36), 서울(25) , 조흥(21), 기업(03),농협(11,12)
		 *
		 * 에러 코드
		 *      - 30: 전문 format Error
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
* 기업
* @version 2008.07.25 
* @author 채상직
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

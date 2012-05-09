/*******************************************************************************
 *   클래스명 : UHL017_Ind_RetProc
 *   작 성 자 : 
 *   내    용 : 
 *   적용범위 : 
 *   작성일자 : 
 ********************************** 수정사항 ************************************
 *	일자		버전		작성자		변경사항
 *******************************************************************************/
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
 * Proc Class for improper BCCARD TUXEDO SERVICE.   
 **************************************************************************** */
public class UHL017_Ind_RetProc extends JtTransactionProc {
	/**
	 *  decision
	 **/
	public boolean decision(JoltInput input, JoltOutput output) throws TaoException {
		boolean commit = false;
		try {
			String pFactor = output.getString("fml_ret1").trim();
			debug("UHL017_Ind_RetProc code :: " + pFactor);
			if ("01".equals(pFactor)) { 
				commit = true; 
			} else if ("03".equals(pFactor)) {	// case of false password
				commit = true;
			} else if ("04".equals(pFactor)) {	// case of false password
				commit = true;
			}

		} catch (TaoException e) {
			debug("UHL017_Ind_RetProc message :: " + e.getMessage());
			throw new TaoException("Cannot fetch decision factors.");
		}

		return commit;
	}
	/**
	 *  getErrorCode
	 **/
	public String getErrorCode(JoltInput input, JoltOutput output) {
		try {
			return output.getString("fml_ret1").trim();
		} catch (TaoException e) {
			return SYS_ERROR;
		}
	}
}



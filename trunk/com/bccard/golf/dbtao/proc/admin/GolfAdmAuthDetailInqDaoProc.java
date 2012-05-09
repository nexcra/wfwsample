/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmAuthDetailInqDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 > 운영자 관리 > 상세보기 PROC
*   적용범위  : Golf
*   작성일자  : 2009-05-06  
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult; 
import com.bccard.waf.action.AbstractProc;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.ResultException;

/** ****************************************************************************
 * Media4th 
 * @author
 * @version 2009-05-06
 **************************************************************************** */
public class GolfAdmAuthDetailInqDaoProc extends AbstractProc{

	public static final String TITLE = "관리자 운영자 관리 상세보기 PROC";
	private String temporary;

	/** ***********************************************************************
	* Proc 실행.
	* @param con Connection
	* @param dataSet 조회조건정보
	************************************************************************ */
	public TaoResult execute(WaContext context, HttpServletRequest request, TaoDataSet dataSet) throws DbTaoException {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		DbTaoResult result = null;
		Connection con = null;

		try{
			//debug("==== GolfAdmAuthDetailInqDaoProc Start ===");
			//조회 조건 Validation
			String p_idx	= dataSet.getString("p_idx");

			String sql = this.getSelectQuery();

			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql);
			int pidx = 0;

			pstmt.setString(++pidx, p_idx);
			rset = pstmt.executeQuery();

			result = new DbTaoResult(TITLE);
			boolean existsData = false;

			while(rset.next()){
				if(!existsData){
					result.addString("RESULT", "00");
				}
				
				result.addString("ACCOUNT",	rset.getString("ID"));
				result.addString("PASSWD",rset.getString("PASSWD"));
				result.addString("HG_NM",rset.getString("HG_NM"));
				result.addString("COM_NM",rset.getString("COM_NM"));
				result.addString("PRS_NM",rset.getString("PRS_NM"));
				result.addString("ZIPCODE1",rset.getString("ZIPCODE1"));
				result.addString("ZIPCODE2",rset.getString("ZIPCODE2"));
				result.addString("ZIPADDR",rset.getString("ZIPADDR"));
				result.addString("DETAILADDR",rset.getString("DETAILADDR"));				
				result.addString("EMAIL",rset.getString("EMAIL_ID"));
				result.addString("TEL1",rset.getString("CHG_DDD_NO"));
				result.addString("TEL2",rset.getString("CHG_TEL_HNO"));
				result.addString("TEL3",rset.getString("CHG_TEL_SNO"));
				result.addString("FX_DDD_NO",rset.getString("FX_DDD_NO"));
				result.addString("FX_TEL_HNO",rset.getString("FX_TEL_HNO"));
				result.addString("FX_TEL_SNO",rset.getString("FX_TEL_SNO"));
				result.addString("HP_DDD_NO",rset.getString("HP_DDD_NO"));
				result.addString("HP_TEL_HNO",rset.getString("HP_TEL_HNO"));
				result.addString("HP_TEL_SNO",rset.getString("HP_TEL_SNO"));
				result.addString("MEMO",rset.getString("MEMO"));
				result.addString("RC_CONN_DATE",rset.getString("RC_CONN_DATE"));
				result.addString("RC_CONN_TIME",rset.getString("RC_CONN_TIME"));
				result.addString("HASH_PASWD",rset.getString("HASH_PASWD"));
				result.addString("INDV_INFO_RPES_NM",rset.getString("INDV_INFO_RPES_NM"));
				result.addString("JUMIN_NO",rset.getString("JUMIN_NO"));
				existsData = true;
			}

			if(!existsData){
				result.addString("RESULT","01");
			}
			
			//debug("==== GolfAdmAuthDetailInqDaoProc End ===");
		}catch ( Exception e ) {
			//debug("==== GolfAdmAuthDetailInqDaoProc ERROR Start ===");
			
			//debug("==== GolfAdmAuthDetailInqDaoProc ERROR End ===");
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"UNPREDICTABLE_TR_EXCEPTION", null);
            throw new DbTaoException(msgEtt,e);
		}finally{
			try{ if(rset  != null) rset.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;
	}

	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getSelectQuery() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n 	SELECT	MGR_ID AS ID, PASWD AS PASSWD, HG_NM, FIRM_NM AS COM_NM, RSV_NM AS PRS_NM,	");
		sql.append("\n 		SUBSTR(ZP,1,3) AS ZIPCODE1, SUBSTR(ZP,4,6) AS ZIPCODE2, ");
		sql.append("\n 		ADDR AS ZIPADDR, DTL_ADDR AS DETAILADDR, EMAIL AS EMAIL_ID, ");
		sql.append("\n 		DDD_NO AS CHG_DDD_NO, TEL_HNO AS CHG_TEL_HNO, TEL_SNO AS CHG_TEL_SNO, ");
		sql.append("\n 		FAX_DDD_NO AS FX_DDD_NO, FAX_TEL_HNO AS FX_TEL_HNO, FAX_TEL_SNO AS FX_TEL_SNO, ");
		sql.append("\n 		HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, ");
		sql.append("\n 		MEMO_CTNT AS MEMO, (TO_CHAR(TO_DATE(RC_CONN_DATE),'YYYY.MM.DD')) AS RC_CONN_DATE,  ");
		sql.append("\n 		(CASE WHEN RC_CONN_TIME IS NULL THEN '' ELSE (SUBSTR(RC_CONN_TIME,1,2)||':'||SUBSTR(RC_CONN_TIME,3,2)) END ) AS RC_CONN_TIME, RC_LOGIN_IP_ADDR,	");
		sql.append("\n 		REG_DATE, CHNG_DATE AS CORR_DATE, HASH_PASWD, INDV_INFO_RPES_NM, JUMIN_NO	");
		sql.append("\n 	FROM BCDBA.TBGMGRINFO		");
		sql.append("\n 	WHERE MGR_ID=?		");

		return sql.toString();
	} 

	/** ***********************************************************************
	* 문자열 parsing 
	************************************************************************ */
	private void parseStr(String orig_word, String final_word) {
		for(int index = 0; (index = temporary.indexOf(orig_word, index)) >= 0; index += final_word.length() )
			temporary = temporary.substring(0, index) + final_word + temporary.substring(index + orig_word.length());
	}

	/** ***********************************************************************
	* 문자 변환
	************************************************************************ */
	public void br2nl() {
		this.parseStr("<br>", "\n");
		this.parseStr("&#39;", "'");
		this.parseStr("&#34;", "\"");
		this.parseStr("&#60;&#37;", "<%");
		this.parseStr("&#37;&#62;", "%>");
		this.parseStr("&#60;", "<");
		this.parseStr("&#62;", ">");
	}

	/** ***********************************************************************
	* 문자 변환  (글 수정시)
	************************************************************************ */
	public void br2nlup() {
		this.parseStr("&#39;", "'");
		this.parseStr("&#34;", "\"");
		this.parseStr("&#60;&#37;", "<%");
		this.parseStr("&#37;&#62;", "%>");
		this.parseStr("&#60;", "<");
		this.parseStr("&#62;", ">");
		this.parseStr("<br>", "\n");
	}

	/** ***********************************************************************
	* 문자열 Setting
	************************************************************************ */
	public void strInput(String source) {
		temporary = source;
	}

	/** ***********************************************************************
	* 문자열 Getting
	************************************************************************ */
	public String strOutput() {
		return temporary;
	}

}

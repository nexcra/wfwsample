/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmAuthUserUpdDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 수정(proc)
*   적용범위  : Golf
*   작성일자  : 2009-05-06 
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
//import java.sql.ResultSet;

//import javax.servlet.http.HttpServletRequest;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.common.GolfException;
//import com.bccard.golf.dbtao.DbTaoResult;
//import com.bccard.golf.dbtao.DbTaoProc;
//import com.bccard.waf.common.StrUtil;
//import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.TaoDataSet;
//import com.bccard.waf.tao.TaoException;
import com.bccard.waf.action.AbstractProc;

import com.bccard.waf.common.BaseException;
//import com.bccard.golf.common.ResultException;

/** ****************************************************************************
 * golf 
 * @author
 * @version 2009-05-06 
 **************************************************************************** */
public class GolfAdmAuthUserUpdDaoProc extends AbstractProc{

	public static final String TITLE = "수정PROC";
	private String temporary;
	/** *****************************************************************
	 * GolfAdmAuthUserUpdDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmAuthUserUpdDaoProc() { 

	}
	
	/****************************************************************
	* DB UPDATE
	****************************************************************/
	public int execute(WaContext context, TaoDataSet data) throws BaseException  {
		int res = 0;
		PreparedStatement pstmt = null;
		Connection con = null;

		try{
			//debug("==== GolfAdmAuthUserUpdDaoProc 수정처리 Start ===");
			//등록 value

//			this.strInput(comm_cont);
//			this.nl2br();
//			comm_cont = this.strOutput();

			StringBuffer sql = new StringBuffer();			
			sql.append("	UPDATE BCDBA.TBGMGRINFO			");
			sql.append("	SET							");
			sql.append("	HG_NM=?, FIRM_NM=?, RSV_NM=?, ZP=?			");
			sql.append("	, ADDR=?, DTL_ADDR=?, EMAIL=?			");
			sql.append("	, DDD_NO=?, TEL_HNO=?, TEL_SNO=?	");
			sql.append("	, FAX_DDD_NO=?, FAX_TEL_HNO=?, FAX_TEL_SNO=?		");
			sql.append("	, HP_DDD_NO=?, HP_TEL_HNO=?, HP_TEL_SNO=?		");
			sql.append("	, MEMO_CTNT=? , CHNG_DATE=to_char(sysdate,'yyyymmdd'), INDV_INFO_RPES_NM=?, JUMIN_NO=?");
			sql.append("	where MGR_ID=?			");
			//debug("==== GolfPointMainUpdDaoProc SQL ===" + sql.toString());

			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql.toString());
			int i = 0;		
		
			pstmt.setString(++i, data.getString("name").trim());		// 성명
			pstmt.setString(++i, data.getString("com_nm").trim());		// 업체명
			pstmt.setString(++i, data.getString("prs_nm").trim());		// 대표자명
			pstmt.setString(++i, data.getString("zipcode1")+data.getString("zipcode2"));		// 우편번호
			pstmt.setString(++i, data.getString("zipaddr").trim());		// 주소1
			pstmt.setString(++i, data.getString("detailaddr").trim());	// 주소2
			pstmt.setString(++i, data.getString("email").trim());		// 이메일
			pstmt.setString(++i, data.getString("tel1").trim());		// 전화1
			pstmt.setString(++i, data.getString("tel2").trim());		// 전화2
			pstmt.setString(++i, data.getString("tel3").trim());		// 전화3
			pstmt.setString(++i, data.getString("fax1").trim());		// 팩스1
			pstmt.setString(++i, data.getString("fax2").trim());		// 팩스2
			pstmt.setString(++i, data.getString("fax3").trim());		// 팩스3
			pstmt.setString(++i, data.getString("hp_tel_no1").trim());	// 핸드폰1
			pstmt.setString(++i, data.getString("hp_tel_no2").trim());	// 핸드폰2
			pstmt.setString(++i, data.getString("hp_tel_no3").trim());	// 핸드폰3
			pstmt.setString(++i, data.getString("memo").trim());		// 메모내용
			pstmt.setString(++i, data.getString("INDV_INFO_RPES_NM").trim());		// 메모내용
			pstmt.setString(++i, data.getString("jumin_no").trim());		// 메모내용
			pstmt.setString(++i, data.getString("p_idx").trim());		// seq

			
			res = pstmt.executeUpdate();

			if ( res == 1 ) {
				//debug("==== 수정 success  ===");
				con.commit();
			}else{
				//debug("==== 수정 fail  ===");
				con.rollback();
			}

			//debug("==== GolfAdmAuthUserUpdDaoProc 수정처리 End ===");
		}catch ( Exception e ) {
			//debug("==== GolfAdmAuthUserUpdDaoProc 수정처리 Error Start ===");
			
			//debug("==== GolfAdmAuthUserUpdDaoProc 수정처리 Error End ===");			
			MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"UNPREDICTABLE_TR_EXCEPTION", null);
            throw new GolfException(msgEtt, e);
		}finally{
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return res;
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
	public void nl2br() {
		this.parseStr("\n", "<br>");
		this.parseStr("'", "&#39;");
		this.parseStr("\"", "&#34;");
		this.parseStr("<%", "&#60;&#37;");
		this.parseStr("%>", "&#37;&#62;");
		this.parseStr("<", "&#60;");
		this.parseStr(">", "&#62;");
		this.parseStr("|", ":");
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

/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfPointMainUpdDaoProc
*   작성자    : (주)미디어포스 진현구
*   내용      : 수정(proc)
*   적용범위  : Golf
*   작성일자  : 2009-03-23
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.bbs;

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
 * @version 2008-12-07
 **************************************************************************** */
public class GolfMainUpdDaoProc extends AbstractProc{

	public static final String TITLE = "글 수정PROC";
	private String temporary;
	/** *****************************************************************
	 * GolfPointMainUpdDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfMainUpdDaoProc() { 

	}
	
	/****************************************************************
	* DB UPDATE
	****************************************************************/
	public int execute(WaContext context, TaoDataSet dataSet) throws BaseException  {
		int res = 0;
		PreparedStatement pstmt = null;
		Connection con = null;

		try{
			//debug("==== GolfPointMainUpdDaoProc 수정처리 Start ===");
			//등록 value
			String contTitle		= dataSet.getString("contTitle"); 				// 제목
			String contText			= dataSet.getString("contText"); 			// 내용1
			String comm_seqno		= dataSet.getString("comm_seqno");			//글 시퀀스 번호
			String ip_addr			= dataSet.getString("ip_addr");				//아이피
//			String file_yn			= dataSet.getString("file_yn"); 			//파일첨부여부
//			String filesystem_name	= dataSet.getString("filesystem_name"); 	//업로드된 파일이름
//			String orgin_file_name	= dataSet.getString("orgin_file_name"); 	//실제 파일이름
//			long fsize				= dataSet.getLong("fsize"); 				//파일사이즈
//			String upFilePath		= dataSet.getString("upFilePath");			//파일 path

//			this.strInput(comm_cont);
//			this.nl2br();
//			comm_cont = this.strOutput();

			StringBuffer sql = new StringBuffer();
			//debug("==== contTitle ===" + contTitle + "/n");
			
			sql.append("	UPDATE BBS					");
			sql.append("	SET							");
			sql.append("	TITLE  = ?					");
			sql.append("	, CONTENT = ?				");
			sql.append("	, UPD_DATE = sysdate		");
			sql.append("	, UPD_IP = ?				");
			sql.append("	WHERE SEQ = ?				");
			//debug("==== GolfPointMainUpdDaoProc SQL ===" + sql.toString());
			
			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql.toString());
			int pidx = 0;
			pstmt.setString(++pidx, contTitle);
			pstmt.setString(++pidx, contText);
			pstmt.setString(++pidx, ip_addr);
			pstmt.setString(++pidx, comm_seqno);

			res = pstmt.executeUpdate();

			if ( res == 1 ) {
				//debug("==== 수정 success  ===");
				con.commit();
			}else{
				//debug("==== 수정 fail  ===");
				con.rollback();
			}

			//debug("==== GolfPointMainUpdDaoProc 수정처리 End ===");
		}catch ( Exception e ) {
			//debug("==== GolfPointMainUpdDaoProc 수정처리 Error Start ===");
			e.printStackTrace();
			//debug("==== GolfPointMainUpdDaoProc 수정처리 Error End ===");			
			MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"UNPREDICTABLE_TR_EXCEPTION", null);
            throw new GolfException(msgEtt, e);
		}finally{
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
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

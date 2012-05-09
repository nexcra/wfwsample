/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmPreTimeDelDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 부킹프리미엄 티타임 다중 삭제 처리
*   적용범위  : golf
*   작성일자  : 2009-05-21
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.booking.premium;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;

import jxl.Sheet;

import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;
import java.sql.ResultSet;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0  
******************************************************************************/
public class GolfadmPreTimeExlUpdDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 프리미엄부킹 티타임 다중 삭제 처리";

	/** *****************************************************************
	 * GolfAdmLessonMutiDelDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfadmPreTimeExlUpdDaoProc() {}
	
	/**
	 * 관리자 프리미엄부킹 티타임 다중 삭제 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,Sheet sheet) throws BaseException {

		String title = "aaa";
		Connection conn = null;
		PreparedStatement pstmt = null;
		int iCount = 0;
		PreparedStatement pstmtChkTime 	= null;	

		DbTaoResult  result =  new DbTaoResult(title);
		ResultSet rsChkTime				= null;
		int idx = 0;

		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			//조회 ----------------------------------------------------------

			String gr_id = "";				// 골프장 아이디
			String gr_id_pre = "";			// 골프장 아이디 이전
			String gr_seq = "10";				// 골프장 seq
			String bk_date = "";			// 부킹일자
			String bk_date_seq = "10";		// 부킹일자 seq
			String bk_time = "1200";			// 부킹 시간
			String bk_time_seq = "";		// 부킹시간 seq
			String gr_cs = "";				// 코스
			String view_yn = "";			// 노출여부
			
			pstmtChkTime = null;
			rsChkTime = null;
			// 티타임 등록되어 있는지 알아보기 getChkTimeQuery
			idx = 0;
			pstmtChkTime = conn.prepareStatement(getChkTimeQuery());
			pstmtChkTime.setString(++idx, bk_date_seq);
			pstmtChkTime.setString(++idx, gr_seq);
			pstmtChkTime.setString(++idx, bk_time);
			rsChkTime = pstmtChkTime.executeQuery();
			if ( rsChkTime.next() ){
				bk_time_seq = rsChkTime.getString("RSVT_ABLE_BOKG_TIME_SEQ_NO");
				debug("aaaaa");
//			}else{
//				
			}
			debug("bk_time_seq : " + bk_time_seq);
		
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	
	
    /** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getDeleteQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("DELETE BCDBA.TBGRSVTABLEBOKGTIMEMGMT 	\n");
		sql.append("\t  WHERE RSVT_ABLE_BOKG_TIME_SEQ_NO = ?	\n");
        return sql.toString();
    }
    
	
	/** ***********************************************************************
	* 부킹시간 검색
	************************************************************************ */
	private String getChkTimeQuery() throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n	SELECT RSVT_ABLE_BOKG_TIME_SEQ_NO FROM BCDBA.TBGRSVTABLEBOKGTIMEMGMT WHERE RSVT_ABLE_SCD_SEQ_NO=? AND AFFI_GREEN_SEQ_NO=? AND BOKG_ABLE_TIME=?	");
		return sql.toString();
	}
	
}

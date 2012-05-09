/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMtGradePayDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 마이티박스 > 나의 정보 > 회원등급 업그레이드 팝업
*   적용범위  : golf 
*   작성일자  : 2010-07-12 
************************** 수정이력 ****************************************************************
* 커멘드구분자	변경일		변경자	변경내용
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.mytbox.myInfo;

import java.io.Reader;
import java.io.Writer;
import java.io.CharArrayReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.user.entity.UcusrinfoEntity;

import javax.servlet.http.HttpServletRequest;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfMtGradePayDaoProc extends AbstractProc {

	public static final String TITLE = "마이티박스 > 나의 정보 > 회원등급 업그레이드 팝업";

	public GolfMtGradePayDaoProc() {}

	// 회원기간 연장하기에서 회원정보 가져오는 함수
	public DbTaoResult execute(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {

		String title = TITLE;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DbTaoResult result =  new DbTaoResult(title);

		
		try {
			// 01. 세션정보체크
			String ctgo_seq = data.getString("ctgo_seq");

			conn = context.getDbConnection("default", null);	
			
			
			//조회 ----------------------------------------------------------			
			String sql = this.getSelectQuery();   

			// 입력값 (INPUT)  
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, ctgo_seq);
			rs = pstmt.executeQuery();
						
			if(rs != null) {
				while(rs.next())  {

					result.addString("GRD_SEQ" 			,rs.getString("GRD_SEQ") );
					result.addString("GRD_NM" 			,rs.getString("GRD_NM") );
					result.addString("GRD_SQ2" 			,rs.getString("GRD_SQ2") );
					result.addString("ANL_FEE" 			,GolfUtil.comma(rs.getString("ANL_FEE")) );
					result.addString("MO_MSHP_FEE" 		,GolfUtil.comma(rs.getString("MO_MSHP_FEE")) );
					result.addString("RESULT", "00"); //정상결과 
				}
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}
			
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
    * 유료회원제 리스트를 가져온다.    
    ************************************************************************ */ 
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();

        sql.append("\n");
		sql.append("\t SELECT CTGO.CDHD_CTGO_SEQ_NO GRD_SEQ, CODE.GOLF_CMMN_CODE_NM GRD_NM	\n");
		sql.append("\t , CTGO.ANL_FEE, CTGO.MO_MSHP_FEE, TO_NUMBER(CTGO.CDHD_SQ2_CTGO) GRD_SQ2	\n");
		sql.append("\t FROM BCDBA.TBGGOLFCDHDCTGOMGMT CTGO	\n");
		sql.append("\t JOIN BCDBA.TBGCMMNCODE CODE ON CODE.GOLF_CMMN_CODE=CTGO.CDHD_SQ2_CTGO AND CODE.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t WHERE CTGO.USE_CLSS='Y' AND CTGO.CDHD_SQ2_CTGO=? 	\n");
		sql.append("\t ORDER BY CTGO.SORT_SEQ ASC	\n");
		
		return sql.toString();
    }                  
}

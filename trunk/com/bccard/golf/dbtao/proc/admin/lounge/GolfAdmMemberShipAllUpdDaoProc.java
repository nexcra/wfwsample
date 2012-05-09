/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmMemberShipAllUpdDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 회원권 시세 전체수정 처리
*   적용범위  : golf
*   작성일자  : 2009-05-28
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.lounge;

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

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmMemberShipAllUpdDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 회원권 시세 전체수정 처리";

	/** *****************************************************************
	 * GolfAdmMemberShipAllUpdDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmMemberShipAllUpdDaoProc() {}
	
	/**
	 * 관리자 회원권 시세 전체수정 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data, String[] gf_seq_no, String[] today_fee) throws DbTaoException  {
		
		int iCount = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Writer writer = null;
		Reader reader = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
            /*****************************************************************************/
            
			sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			for (int i = 0; i < gf_seq_no.length; i++) {				
				if (gf_seq_no[i] != null && gf_seq_no[i].length() > 0) {
		            
		         	pstmt.setLong(1, Integer.parseInt(today_fee[i])); 
					pstmt.setString(2, data.getString("ADMIN_NO") );
					pstmt.setLong(3, Integer.parseInt(gf_seq_no[i])); 
					pstmt.setString(4,  data.getString("FEE_DATE"));
					
					iCount += pstmt.executeUpdate();
			    }
            }
			
			if(pstmt != null) pstmt.close();
            /*********************************************************************** ******/
			
		    if(iCount == gf_seq_no.length) {
				conn.commit();
			} else {
				conn.rollback();
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

		return iCount;
	}
	
	
    /** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGGREENQUTMGMT SET	\n");
		sql.append("\t  QUT=?, CHNG_MGR_ID=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') 	\n");
		sql.append("\t WHERE AFFI_GREEN_SEQ_NO = ?	");
		sql.append("\n AND QUT_DATE = ?	");
        return sql.toString();
    }

}

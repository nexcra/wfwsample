/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmRangeTimeUpdDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 드림 골프레인지 일정시간 수정 처리
*   적용범위  : golf
*   작성일자  : 2009-07-01
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.drivrange;

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

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmRangeTimeUpdDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 드림 골프레인지 일정시간 수정 처리";

	/** *****************************************************************
	 * GolfAdmRangeTimeUpdDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmRangeTimeUpdDaoProc() {}
	
	/**
	 * 관리자 드림 골프레인지 일정시간 수정 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data, String[] rsvttime_sql_no, String[] start_hh, String[] start_mi, String[] end_hh, String[] end_mi, String[] day_rsvt_num) throws DbTaoException  {
		
		int result = 0;
		int iCount = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String flag = "";
			
			/*
			 * '일정 수정'에서 공휴일선택시 해당 골프장만 처리됨.
			 *시간 및 인원설정/예약제한인원이 골프장마다 상이 할 수 있으로 각기 처리 
			 */
			if (data.getString("SLS_END_YN").equals("Y")){
				flag = "Y";
			}else if (data.getString("HOLY_YN").equals("Y")){
				flag = "H";
			}	
			
            /*****************************************************************************/
			
			sql = this.getInsertQuery1();//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			//pstmt.setString(++idx, data.getString("SLS_END_YN") ); 
			pstmt.setString(++idx, flag );
			pstmt.setLong(++idx, data.getLong("RSVT_TOTAL_NUM") );
			pstmt.setString(++idx, data.getString("ADMIN_NO") );
			pstmt.setLong(++idx, data.getLong("RSVTDIALY_SQL_NO") );
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
			
            for (int i = 0; i < rsvttime_sql_no.length; i++) {				
				if (rsvttime_sql_no[i] != null && rsvttime_sql_no[i].length() > 0) {
		            
		            sql = this.getInsertQuery2();//Insert Query
					pstmt = conn.prepareStatement(sql);
					
					idx = 0;
					
					pstmt.setString(++idx, start_hh[i]+start_mi[i] ); 
					pstmt.setString(++idx, end_hh[i]+end_mi[i] ); 
					pstmt.setString(++idx, day_rsvt_num[i] );
					pstmt.setString(++idx, data.getString("ADMIN_NO") );
					pstmt.setString(++idx, rsvttime_sql_no[i] );
					
					iCount += pstmt.executeUpdate();
			    }
            }
            /*****************************************************************************/
            
            if(result > 0 && iCount == rsvttime_sql_no.length) {
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

		return result;
	}
	
	
    /** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getInsertQuery1(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");		
		sql.append("UPDATE BCDBA.TBGRSVTABLESCDMGMT SET	\n");
		sql.append("\t  RESM_YN=?, DLY_RSVT_ABLE_PERS=?, CHNG_MGR_ID=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') 	\n");
		sql.append("\t WHERE RSVT_ABLE_SCD_SEQ_NO=?	\n");		
        return sql.toString();
    }
    
    private String getInsertQuery2(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");		
		sql.append("UPDATE BCDBA.TBGRSVTABLEBOKGTIMEMGMT SET	\n");
		sql.append("\t  RSVT_STRT_TIME=?, RSVT_END_TIME=?, DLY_RSVT_ABLE_PERS_NUM=?, CHNG_MGR_ID=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') 	\n");
		sql.append("\t WHERE RSVT_ABLE_BOKG_TIME_SEQ_NO=?	\n");		
        return sql.toString();
    }
}

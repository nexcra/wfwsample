/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmGrUpdDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 부킹 골프장 수정 처리
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.booking.premium;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0  
******************************************************************************/
public class GolfadmPreRsUpdDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 부킹 골프장 수정 처리";

	/** *****************************************************************
	 * GolfadmGrUpdDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfadmPreRsUpdDaoProc() {}
	
	/**
	 * 관리자 레슨프로그램 등록 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;

		String sv_code = "";
		String cdhd_id = "";
		String bokg_able = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
            /*****************************************************************************/
			String rsvt_YN = data.getString("RSVT_YN");
            String appr_opion = data.getString("APPR_OPION");
            String add_appr_opion = data.getString("ADD_APPR_OPION");
            String user_id = data.getString("USER_ID");

			sql = this.getInsertQuery(rsvt_YN);//Insert Query
			pstmt = conn.prepareStatement(sql);

            
			int idx = 0;
			pstmt.setString(++idx, data.getString("RSVT_YN") );
			pstmt.setString(++idx, data.getString("CTNT") );
			pstmt.setString(++idx, data.getString("RSVT_SQL_NO") );
			
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            
            
            // 다시 부킹 할 수 있는 상태로 돌려준다. 예약취소 : N / 임박취소 : I
            if(rsvt_YN.equals("N") || rsvt_YN.equals("I")){
				sql = this.getUpdateQuery();//Insert Query
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setString(1, data.getString("RSVT_ABLE_BOKG_TIME_SEQ_NO") );
				result = pstmt.executeUpdate();
	            if(pstmt != null) pstmt.close();
	            
	            // 임박취소를 할 경우 그 후의 예약은 모두 예약취소시킨다.
	            if(rsvt_YN.equals("I")){
		            // 해당 예약 번호를 가지고 예약 일정과 아이디를 가져온다
					sql = this.getDetailQuery();//Insert Query
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, data.getString("RSVT_SQL_NO") );
					rs = pstmt.executeQuery();

					if(rs != null) {			 
						while(rs.next())  {	
							sv_code = rs.getString("SV_CODE");
							cdhd_id = rs.getString("CDHD_ID");
							bokg_able = rs.getString("BOKG_ABLE");
							
				            // 해당 아이디로 예약되고 해당 일정 이후의 예약은 모두 취소 시킨다. 
			            	sql = this.getUpdAllQuery();//Update Query
							pstmt = conn.prepareStatement(sql);
							
							pstmt.setString(1, sv_code );
							pstmt.setString(2, cdhd_id );
							pstmt.setString(3, bokg_able );
							pstmt.executeUpdate();
				            if(pstmt != null) pstmt.close();
						}
						
					}

		            if(pstmt != null) pstmt.close();
		            if(rs != null) rs.close();
	            }
            }
            /////////////////////////////////////////////

			if(result>0) {
				conn.commit();
			} else {
				conn.rollback();
			}	
			
			conn.setAutoCommit(true);
			
            //회원평가 수정 : / 추가
            if(!add_appr_opion.equals("")){
            	sql = this.getUpdCdhdQuery();
            	pstmt = conn.prepareStatement(sql);
            	
            	//공백이 아니면 /추가
            	if(!appr_opion.equals("")) add_appr_opion = "/"+ add_appr_opion;
            	
            	pstmt.setString(1, appr_opion + add_appr_opion);
            	pstmt.setString(2, user_id);
            	result = pstmt.executeUpdate();
            	
            }
			
		
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(rs != null) rs.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	
	
    /** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getInsertQuery(String rsvt_YN){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGRSVTMGMT SET	\n");
		sql.append("\t  RSVT_YN=?, CTNT=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");

        if(rsvt_YN.equals("N") || rsvt_YN.equals("I")){
        	sql.append("\t  , CNCL_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
        }else{
        	sql.append("\t  , CNCL_ATON=''	\n");
        }

		sql.append("\t WHERE GOLF_SVC_RSVT_NO=?	\n");
        return sql.toString();
    }
     
 	/** ***********************************************************************
	* CLOB Query를 생성하여 리턴한다.    - 다시 부킹할 수 있는 상태로 돌린다.
	************************************************************************ */
	private String getUpdateQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t	UPDATE BCDBA.TBGRSVTABLEBOKGTIMEMGMT SET	\n");
		sql.append("\t	BOKG_RSVT_STAT_CLSS='0001'					\n");
		sql.append("\t	WHERE RSVT_ABLE_BOKG_TIME_SEQ_NO=?			\n");
		    	  
		return sql.toString();
	}
      
   	/** ***********************************************************************
    * 임박취소의 경우 해당아이디의 이후 예약건수들을 모두 취소시킨다.
    ************************************************************************ */
    private String getUpdAllQuery(){
      StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	UPDATE (SELECT * FROM BCDBA.TBGRSVTMGMT T1	\n");
		sql.append("\t	JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_BOKG_TIME_SEQ_NO=T2.RSVT_ABLE_BOKG_TIME_SEQ_NO	\n");
		sql.append("\t	JOIN BCDBA.TBGRSVTABLESCDMGMT T3 ON T2.RSVT_ABLE_SCD_SEQ_NO=T3.RSVT_ABLE_SCD_SEQ_NO	\n");
		sql.append("\t	WHERE SUBSTR(GOLF_SVC_RSVT_NO,5,1)=? AND CDHD_ID=? AND RSVT_YN='Y'	\n");
		sql.append("\t	AND T3.BOKG_ABLE_DATE||T2.BOKG_ABLE_TIME>?	\n");
		sql.append("\t	) SET RSVT_YN='N'	\n");      	  
		return sql.toString();
    }
      
   	/** ***********************************************************************
    * 예약번호에 대한 일정을 가져온다
    ************************************************************************ */
    private String getDetailQuery(){
      StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	SELECT SUBSTR(T1.GOLF_SVC_RSVT_MAX_VAL,5,1) SV_CODE, T1.CDHD_ID, (TRIM(T3.BOKG_ABLE_DATE)||TRIM(T2.BOKG_ABLE_TIME)) BOKG_ABLE	\n");
		sql.append("\t	FROM BCDBA.TBGRSVTMGMT T1	\n");
		sql.append("\t	JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_BOKG_TIME_SEQ_NO=T2.RSVT_ABLE_BOKG_TIME_SEQ_NO	\n");
		sql.append("\t	JOIN BCDBA.TBGRSVTABLESCDMGMT T3 ON T2.RSVT_ABLE_SCD_SEQ_NO=T3.RSVT_ABLE_SCD_SEQ_NO	\n");
		sql.append("\t	WHERE T1.GOLF_SVC_RSVT_NO=?	\n");      	  
		return sql.toString();
    }
    
 	/** ***********************************************************************
     * 해당회원에대한 평가수정쿼리
     ************************************************************************ */
     private String getUpdCdhdQuery(){
       StringBuffer sql = new StringBuffer();
 		sql.append("\n  UPDATE BCDBA.TBGGOLFCDHD   SET						");
 		sql.append("\n   APPR_OPION =  ?									");
 		sql.append("\n  WHERE CDHD_ID = ?									");
 		
     return sql.toString();
     }
     
}

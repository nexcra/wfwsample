/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmBkTimeRegDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 프리미엄부킹 티타임 등록 처리
*   적용범위  : golf
*   작성일자  : 2009-05-20
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.booking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/
public class GolfadmBkTimeRegDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 티타임 등록 처리";

	/** *****************************************************************
	 * GolfAdmLessonInsDaoProc 프로세스 생성자 
	 * @param N/A
	 ***************************************************************** */
	public GolfadmBkTimeRegDaoProc() {}
	
	/**
	 * 관리자 부킹티타임 등록 처리 
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		int result2 = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		
		GolfAdminEtt userEtt = null;
		String reg_ID = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			// 00. 등록 변수 셋팅
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){	
				reg_ID		= (String)userEtt.getMemId();
			}
			
			String bkps_DATE = data.getString("BKPS_DATE");
			String par_FREE = data.getString("PAR_FREE");
			String sort = data.getString("SORT");
			String golf_RSVT_DAY_CLSS = "";
			
			bkps_DATE = GolfUtil.rplc(bkps_DATE, "-", "");
			par_FREE = GolfUtil.rplc(par_FREE, "-", "");
			
			// 프리미엄 부킹 예약시 디비 구분자 추가	golf_RSVT_DAY_CLSS =>	P:프리미엄부킹 D:드림골프레인지, T:TOP골프부킹
			if(sort.equals("0001")){
				golf_RSVT_DAY_CLSS = "P";
			}
			else if(sort.equals("1000")){
				golf_RSVT_DAY_CLSS = "T";
			}
				
			
			
			
			// 01. 예약가능일정 등록
			
			long max_DAY_SEQ_NO = 0L;

			/**SEQ_NO 가져오기**************************************************************/
            // 00. 이미 등록한 일정인지 알아보기
			sql = this.getSearchQuery();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, bkps_DATE);
			pstmt.setString(2, data.getString("COURSE"));
			pstmt.setString(3, data.getString("SKY_CODE"));
	        
			rs = pstmt.executeQuery();
			if(rs.next()){
				max_DAY_SEQ_NO = rs.getLong("RSVT_ABLE_SCD_SEQ_NO");
			}else {
			
				sql = this.getNextValQuery(); 
	            pstmt = conn.prepareStatement(sql);
	            rs = pstmt.executeQuery();		
				if(rs.next()){
					max_DAY_SEQ_NO = rs.getLong("DAY_SEQ_NO");
				}
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
            /**Insert************************************************************************/
            sql = this.getInsertQuery();
			pstmt = conn.prepareStatement(sql);
			
			String flag = "";
			
			
			if (data.getString("CLOSE_YN").equals("Y")){
				flag = "Y";
			}else if (data.getString("HOLY_YN").equals("Y")){
				flag = "H";
			}else {
				flag = data.getString("FREE_YN");
			}
			
			
			int idx = 0;
			pstmt.setLong(++idx, max_DAY_SEQ_NO );					//예약가능일정일련번호
			pstmt.setString(++idx, data.getString("GR_SEQ_NO") );	//제휴골프장번호
			pstmt.setString(++idx, data.getString("COURSE") );		//예약코스
			pstmt.setString(++idx, bkps_DATE );						//부킹가능날짜
			//pstmt.setString(++idx, data.getString("FREE_YN") );		//휴장여부
			pstmt.setString(++idx, flag );							//공휴일/휴장여부
			
			pstmt.setString(++idx, par_FREE );						//파3부킹휴장일
			pstmt.setString(++idx, data.getString("FREE_MEMO") );	//휴장일 사유
			pstmt.setString(++idx, StrUtil.isNull(data.getString("SKY_CODE"), "") );	//스카이72홀코드
			pstmt.setString(++idx, reg_ID );						//등록관리자ID
			pstmt.setString(++idx, golf_RSVT_DAY_CLSS );			//골프예약일구분코드
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
			

			String bkps_TIME = data.getString("BKPS_TIME");
			String[] arr_bkps_time= bkps_TIME.split(",");
			String bkps_TIME_VAL = "";
			int bkps_TIME_LEN = 0;
			
			for(int i = 0 ; i < arr_bkps_time.length; i++) {
				
				bkps_TIME_VAL = arr_bkps_time[i].trim();
				bkps_TIME_LEN = bkps_TIME_VAL.length();
				
				if (bkps_TIME_LEN == 4){
					// 02. 예약가능티타임관리
					/**SEQ_NO 가져오기**************************************************************/
					sql = this.getNextValQuery2(); 
		            pstmt = conn.prepareStatement(sql);
		            rs = pstmt.executeQuery();			
					long max_TIME_SEQ_NO = 0L;
					if(rs.next()){
						max_TIME_SEQ_NO = rs.getLong("TIME_SEQ_NO");
					}
					if(rs != null) rs.close();
		            if(pstmt != null) pstmt.close();
		            
		            /**Insert************************************************************************/
		            if("1000".equals(sort)){
		            	sql = this.getInsertQuery3();
		            }
		            else
		            {
		            	sql = this.getInsertQuery2();
		            }
		           
		            
					pstmt = conn.prepareStatement(sql);
					
					idx = 0;
					pstmt.setLong(++idx, max_TIME_SEQ_NO );					//예약가능시간일련번호
					pstmt.setLong(++idx, max_DAY_SEQ_NO );					//예약가능일정일련번호
					pstmt.setString(++idx, data.getString("GR_SEQ_NO") );	//제휴골프장번호
					pstmt.setString(++idx, bkps_TIME_VAL );					//부킹가능시간
					pstmt.setString(++idx, reg_ID );						//등록관리자ID
					pstmt.setString(++idx, sort );
					if("1000".equals(sort)){
					pstmt.setString(++idx, data.getString("evnt_yn") );
			        }
					result2 = pstmt.executeUpdate();
		            if(pstmt != null) pstmt.close();
				}
			}
			            
			// 03. 처리 결과
			if(result > 0 || result2 > 0) {
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
    private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("INSERT INTO BCDBA.TBGRSVTABLESCDMGMT (	\n");
		sql.append("\t  RSVT_ABLE_SCD_SEQ_NO, AFFI_GREEN_SEQ_NO, GOLF_RSVT_CURS_NM, BOKG_ABLE_DATE, RESM_YN, 	\n");
		sql.append("\t  PAR_3_BOKG_RESM_DATE, RESM_DAY_RSON_CTNT, SKY72_HOLE_CODE, REG_MGR_ID, REG_ATON, GOLF_RSVT_DAY_CLSS 	\n");
		sql.append("\t ) VALUES (	\n");	
		sql.append("\t  ?,?,?,?,?,	\n");
		sql.append("\t  ?,?,?,?,TO_CHAR(SYSDATE,'YYYYMMDD'),?	\n");
		sql.append("\t \n)");
        return sql.toString();
    }
    
    /** ***********************************************************************
     * Query를 생성하여 리턴한다.    
     ************************************************************************ */
     private String getInsertQuery2(){
         StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("INSERT INTO BCDBA.TBGRSVTABLEBOKGTIMEMGMT (	\n");
 		sql.append("\t  RSVT_ABLE_BOKG_TIME_SEQ_NO, RSVT_ABLE_SCD_SEQ_NO, AFFI_GREEN_SEQ_NO, BOKG_ABLE_TIME, REG_MGR_ID, REG_ATON, EPS_YN ,BOKG_RSVT_STAT_CLSS   	\n");
 		sql.append("\t ) VALUES (	\n");	
 		sql.append("\t  ?,?,?,?,?,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), 'N', ?	\n");
 		sql.append("\t \n)");
        return sql.toString();
     }
     /** ***********************************************************************
      * Query를 생성하여 리턴한다.    
      ************************************************************************ */
      private String getInsertQuery3(){
          StringBuffer sql = new StringBuffer();
  		sql.append("\n");
  		sql.append("INSERT INTO BCDBA.TBGRSVTABLEBOKGTIMEMGMT (	\n");
  		sql.append("\t  RSVT_ABLE_BOKG_TIME_SEQ_NO, RSVT_ABLE_SCD_SEQ_NO, AFFI_GREEN_SEQ_NO, BOKG_ABLE_TIME, REG_MGR_ID, REG_ATON, EPS_YN ,BOKG_RSVT_STAT_CLSS, EVNT_YN   	\n");
  		sql.append("\t ) VALUES (	\n");	
  		sql.append("\t  ?,?,?,?,?,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), 'N', ?, ?	\n");
  		sql.append("\t \n)");
         return sql.toString();
      }

     /** ***********************************************************************
     * 현재 등록되어있는 해당날짜를 리턴한다. 생성하여 리턴한다. 
     ************************************************************************ */
     private String getSearchQuery(){
         StringBuffer sql = new StringBuffer();
 		sql.append("\n");
        sql.append("SELECT RSVT_ABLE_SCD_SEQ_NO FROM BCDBA.TBGRSVTABLESCDMGMT  \n");
        sql.append("\t WHERE BOKG_ABLE_DATE = ? AND GOLF_RSVT_CURS_NM = ?  AND SKY72_HOLE_CODE = ? \n"); 

        
 		return sql.toString();
     }
     
    /** ***********************************************************************
    * Max IDX Query를 생성하여 리턴한다. 
    ************************************************************************ */
    private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
        sql.append("SELECT NVL(MAX(RSVT_ABLE_SCD_SEQ_NO),0)+1 DAY_SEQ_NO FROM BCDBA.TBGRSVTABLESCDMGMT \n");
		return sql.toString();
    }
    
    /** ***********************************************************************
     * Max IDX Query를 생성하여 리턴한다. 
     ************************************************************************ */
     private String getNextValQuery2(){
         StringBuffer sql = new StringBuffer();
 		sql.append("\n");
         sql.append("SELECT NVL(MAX(RSVT_ABLE_BOKG_TIME_SEQ_NO),0)+1 TIME_SEQ_NO FROM BCDBA.TBGRSVTABLEBOKGTIMEMGMT \n");
 		return sql.toString();
     }
}

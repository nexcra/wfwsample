/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntBkMMDaoProc
*   작성자    : 이포넷 은장선
*   내용      : 명문 골프장 부킹 이벤트 상세보기
*   적용범위  : golf
*   작성일자  : 2009-09-16
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
 * Golf
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfEvntBkMMDaoProc extends AbstractProc {
	public static final String TITLE = "명문 골프장 이벤트 처리";
	/** *****************************************************************
	 * GolfEvntBkInqDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfEvntBkMMDaoProc() {}	

	
	
	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context,  TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			
			//조회 ----------------------------------------------------------			
			String sql = this.getReserveListSQL();   
			String cdhd_id = data.getString("cdhd_id");
			
			// 입력값 (INPUT)         
		
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, cdhd_id);

			rs = pstmt.executeQuery();
			
			if(rs != null) {			                                              

				while(rs.next())  {				
					result.addString("APLC_SEQ_NO",rs.getString("APLC_SEQ_NO"));
					result.addString("GOLF_CMMN_CODE",rs.getString("GOLF_CMMN_CODE"));
					result.addString("GOLF_CMMN_CODE_NM",rs.getString("GOLF_CMMN_CODE_NM"));
					result.addString("GREEN_NM",rs.getString("GREEN_NM"));
					result.addString("TEOF_DATE",DateUtil.format(rs.getString("TEOF_DATE"),"yyyyMMdd","yyyy/MM/dd"));					
					result.addString("CNCL_DATE",rs.getString("CNCL_DATE"));
					result.addString("REG_ATON",DateUtil.format(rs.getString("REG_ATON"),"yyyyMMdd","yyyy/MM/dd"));
					//result.addString("GRADE",rs.getString("GRADE"));
					result.addString("PU_TIME",rs.getString("PU_TIME"));
					result.addString("DPRT_PL_INFO",rs.getString("DPRT_PL_INFO"));
					result.addString("MEMO_EXPL",rs.getString("MEMO_EXPL"));
					result.addString("RN",rs.getString("RN"));

					String teof_time = rs.getString("TEOF_TIME");
					teof_time = teof_time.substring(0,2) + "시대";

					result.addString("TEOF_TIME",teof_time);
					
					String pu_date = rs.getString("PU_DATE");
					if(pu_date != null){
						pu_date = DateUtil.format(pu_date,"yyyyMMdd","yyyy-MM-dd");
					}
					result.addString("PU_DATE",pu_date);	
					
					String cncl_date = rs.getString("CNCL_DATE");
					if(!cncl_date.equals("N")){
						cncl_date = cncl_date.replaceAll("/","");
						String currDate = DateUtil.currdate("yyyyMMdd");

						String cncl_yn = "N";
						
						if(Integer.parseInt(currDate) <= Integer.parseInt(cncl_date)){
							cncl_yn = "Y";
						}
						debug("cncl_yn>>>>>>>>>>>>>>>>>>>>>>>>>>" + cncl_yn);
						result.addString("CNCL_YN",cncl_yn);
					}

					result.addString("RESULT", "00"); //정상결과
				}
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	
	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult getReserveList(WaContext context,  TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			
			//조회 ----------------------------------------------------------			
			String sql = this.getSelectQuery();   
			String userId = data.getString("userId");
			String intMemGrade = data.getString("intMemGrade");
			
			// 입력값 (INPUT)         
		
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, intMemGrade);
			pstmt.setString(2, userId);  

			rs = pstmt.executeQuery();
			 
			if(rs != null) {			 

				while(rs.next())  {		
					if("".equals(rs.getString("TOT")) || rs.getString("TOT") == null){
						result.addString("TOT","0");
					}else{
						result.addString("TOT",rs.getString("TOT"));
					}
					result.addString("CNT",rs.getString("CNT"));
					
					result.addString("RESULT", "00"); //정상결과
				}
			}
 
			if(result.size() < 1) {
				result.addString("TOT","0");
				result.addString("CNT","0");
				result.addString("RESULT", "01");			
			}
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	
	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public int doInsert(WaContext context, TaoDataSet dataSet) throws DbTaoException  {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);
			// 신청 중복 체크 
			int recvOverLapChk = this.getRecvOverLapChk(conn, dataSet);
			
			String cdhd_id				= dataSet.getString("cdhd_id");		//사용자ID
			String co_nm				= dataSet.getString("co_nm");		//사용자이름
			String email				= dataSet.getString("email");		//email
			String hp_ddd_no			= dataSet.getString("hp_ddd_no");	//사용자 핸드폰 번호 010,011등등
			String hp_tel_hno			= dataSet.getString("hp_tel_hno");  //사용자 핸드폰 번호 
			String hp_tel_sno			= dataSet.getString("hp_tel_sno");  //사용자 핸드폰 번호			
			String teof_date			= dataSet.getString("teof_date");   //신청일자
			String teof_time			= dataSet.getString("teof_time");   //신청시간
			String ridg_pers_num		= dataSet.getString("handy");       //핸디
			String green_nm				= dataSet.getString("green_nm");    //신청골프장명
			String memo_expl			= dataSet.getString("memo_expl");   //요청사항

			if (recvOverLapChk == 0) {
				conn.setAutoCommit(false);
	            /*****************************************************************************/
				
				sql = this.getNextValQuery(); //신청번호 쿼리
				pstmt = conn.prepareStatement(sql);
	            rs = pstmt.executeQuery();			
				String aplc_seq_no = "";
				if(rs.next()){
					aplc_seq_no = rs.getString("APLC_SEQ_NO");
				}
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
	            /*****************************************************************************/
	            
				sql = this.getInsertQuery();//Insert Query
				pstmt = conn.prepareStatement(sql);
				
				int idx = 0;

				pstmt.setString(++idx, aplc_seq_no );
				pstmt.setString(++idx, cdhd_id );
				pstmt.setString(++idx, co_nm );	
				pstmt.setString(++idx, email );			
				pstmt.setString(++idx, hp_ddd_no );	
				pstmt.setString(++idx, hp_tel_hno );
				pstmt.setString(++idx, hp_tel_sno );				
				pstmt.setString(++idx, teof_date );
				pstmt.setString(++idx, teof_time );
				pstmt.setString(++idx, green_nm );
				pstmt.setString(++idx, ridg_pers_num );
				pstmt.setString(++idx, memo_expl );
				
				result = pstmt.executeUpdate();
				
				if(result > 0) {
					conn.commit();
				} else {
					conn.rollback();
				}
			} else {
				result = 2;
			}
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			e.printStackTrace();
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}

	/**
	 * 신청 중복 체크
	 * @param context
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int getRecvOverLapChk(Connection conn, TaoDataSet data) throws DbTaoException {

		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";

		try {
			
			sql = this.getDuplSelectQuery();//Select Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;		
			
			pstmt.setString(++idx, data.getString("cdhd_id") );
			pstmt.setString(++idx, data.getString("teof_date") );
			
			rs = pstmt.executeQuery();
			
			if(rs != null && rs.next()) {
				result++;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}

		}
		
		return result;
	}	
	/**
	 * 가지고있는 모든 등급을 GET해서 TEXT화 시킴
	 * @param context
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public String getAllGrade(WaContext context, TaoDataSet data , String cdhd_id) throws DbTaoException {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		String result = "";
		
		try{
			conn = context.getDbConnection("default", null);
			sql = this.getAllGradeQuery();
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				if(!"".equals(result)){
					result = result +  " / "+ rs.getString("GOLF_CMMN_CODE_NM");
				}else{
					result = rs.getString("GOLF_CMMN_CODE_NM");
				}
				
			}
			
		}catch(Exception e) {
			e.printStackTrace();
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn != null) conn.close(); } catch (Exception ignored) {}

		}
		
		return result;
	}
	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT COUNT(*) CNT,DECODE(?,'1',4,'2',2,'3',1,'0',0,'4',0) TOT    ");
		sql.append("\n	 FROM BCDBA.TBGAPLCMGMT			     					");
		sql.append("\n  WHERE GOLF_SVC_APLC_CLSS = '9001'						");
		sql.append("\n	  AND CDHD_ID = ?		             					");
		sql.append("\n	  AND NUM_DDUC_YN = 'Y'              					");
		sql.append("\n	  AND SUBSTR(TEOF_DATE,1,6) = TO_CHAR(SYSDATE,'YYYYMM') ");

		return sql.toString();
    }  

	 /** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
	private String getDuplSelectQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("SELECT	                                                     \n");
		sql.append("\t 	APLC_SEQ_NO	                                             \n");
		sql.append("\t FROM BCDBA.TBGAPLCMGMT	                                 \n");
		sql.append("\t WHERE GOLF_SVC_APLC_CLSS = '9001'	                     \n");		
		sql.append("\t AND CDHD_ID = ?	                                         \n");
		sql.append("\t AND TEOF_DATE = ?	                                     \n");
        return sql.toString();
    }
	
     /** ***********************************************************************
    * Max IDX Query를 생성하여 리턴한다.    
    ************************************************************************ */
	private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT NVL(MAX(APLC_SEQ_NO),0)+1 APLC_SEQ_NO FROM BCDBA.TBGAPLCMGMT \n");
		return sql.toString();
    }

	 /** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
	private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  INSERT INTO BCDBA.TBGAPLCMGMT (	                                                  \n");
		sql.append("\t  APLC_SEQ_NO, GOLF_SVC_APLC_CLSS,PGRS_YN, PRZ_WIN_YN, CDHD_ID, CO_NM, EMAIL,	      \n");
		sql.append("\t  HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, TEOF_DATE, TEOF_TIME, \n");
		sql.append("\t  GREEN_NM,RIDG_PERS_NUM, MEMO_EXPL, REG_ATON,NUM_DDUC_YN                           \n");
		sql.append("\t ) VALUES (	                                                                      \n");	
		sql.append("\t  ? ,'9001', 'R','N',?, ?, ?,	                                                      \n");
		sql.append("\t 	?, ?, ?, ?, ?,                                                                    \n");	
		sql.append("\t  ?, ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), 'N' )                               \n");	
        return sql.toString();
    }

	 /** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
	private String getReserveListSQL(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n                                                                                       ");
		sql.append("\t SELECT ROWNUM RN,APLC_SEQ_NO,GOLF_CMMN_CODE,GOLF_CMMN_CODE_NM,GREEN_NM,TEOF_TIME,				\n");
		sql.append("\t        DECODE(PU_DATE,NULL,'N',TO_CHAR(TO_DATE(PU_DATE)-7,'YYYY/MM/DD')) CNCL_DATE,				\n");
		sql.append("\t        TEOF_DATE,PU_DATE,PU_TIME,DPRT_PL_INFO,SUBSTR(A.MEMO_EXPL,1,20) MEMO_EXPL,				\n");
		sql.append("\t        SUBSTR(A.REG_ATON,1,8) REG_ATON															\n");
		//sql.append("\t        ,DECODE(C.CDHD_CTGO_SEQ_NO,'8','화이트','7','골드','6','블루','5','챔피온') GRADE 				\n");
		sql.append("\t   FROM BCDBA.TBGAPLCMGMT A,																		\n");
		sql.append("\t        (SELECT  GOLF_CMMN_CODE_NM,GOLF_CMMN_CODE													\n");
		sql.append("\t           FROM BCDBA.TBGCMMNCODE																	\n");
		sql.append("\t          WHERE GOLF_CMMN_CLSS='0053' AND USE_YN ='Y'                                             \n");	
		sql.append("\t          ORDER BY SORT_SEQ) B                                       							 \n");
		//sql.append("\t			, BCDBA.TBGGOLFCDHDGRDMGMT C															\n");
		sql.append("\t 	WHERE A.CDHD_ID = ?                                                                             \n");	
		sql.append("\t    AND A.GOLF_SVC_APLC_CLSS = '9001'                                                             \n");	
		sql.append("\t    AND A.PGRS_YN = B.GOLF_CMMN_CODE                                                              \n");
		//sql.append("\t    AND A.CDHD_ID = C.CDHD_ID                                                                     \n");
		sql.append("\t  ORDER BY ROWNUM DESC                                                                            \n");
        return sql.toString();
    }
	 /** ***********************************************************************
	    * Query를 생성하여 리턴한다.    
	    ************************************************************************ */
		private String getAllGradeQuery(){
			StringBuffer sql = new StringBuffer();
			
			sql.append(" 	SELECT T4.GOLF_CMMN_CODE_NM,T3.CDHD_CTGO_SEQ_NO,T1.CDHD_ID,T1.HG_NM  							\n");
			sql.append("	FROM BCDBA.TBGGOLFCDHD T1 																		\n");
			sql.append("	JOIN BCDBA.TBGGOLFCDHDGRDMGMT T2 ON T1.CDHD_ID = T2.CDHD_ID 									\n");
			sql.append("	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T3 ON T2.CDHD_CTGO_SEQ_NO = T3.CDHD_CTGO_SEQ_NO 					\n");
			sql.append("	JOIN BCDBA.TBGCMMNCODE T4 ON T3.CDHD_SQ2_CTGO = T4.GOLF_CMMN_CODE AND T4.GOLF_CMMN_CLSS='0005' 	\n");
			sql.append("	WHERE T2.CDHD_ID=? 																				\n");
			
			
			return sql.toString();
		}
}

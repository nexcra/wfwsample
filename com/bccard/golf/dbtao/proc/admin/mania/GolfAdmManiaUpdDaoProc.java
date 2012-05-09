/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmManiaChgDaoProc
*   작성자    : (주)만세커뮤니케이션 김인겸
*   내용      : 관리자 골프장리무진할인신청관리 수정처리
*   적용범위  : golf
*   작성일자  : 2009-05-20
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.mania;

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
public class GolfAdmManiaUpdDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 골프장리무진할인신청관리 수정처리";

	/** *****************************************************************
	 * GolfAdmManiaChgDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmManiaUpdDaoProc() {}
	
	/**
	 * 관리자 리무진할인신청 프로그램 수정 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Writer writer = null;
		Reader reader = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			// 키값에따라 결제진행/취소/ 일반수정 구분 ---------------------------------------------
			String prize_yn		= data.getString("PRIZE_YN");
			String sttl_amt		= data.getString("STTL_AMT");
			String str_plc		= data.getString("STR_PLC");
			String pic_date		= data.getString("PIC_DATE");
			String start_hh		= data.getString("START_HH");
			String start_mi		= data.getString("START_MI");
			String toff_date 	= data.getString("TOFF_DATE");
			String end_hh		= data.getString("END_HH");
			String end_mi		= data.getString("END_MI");
			String gcc_nm		= data.getString("GCC_NM");
			String ckd_code		= data.getString("CKD_CODE");
			String tk_prs		= data.getString("TK_PRS");
			
			String zp1			= data.getString("ZP1");
			String zp2			= data.getString("ZP2");
			String addr			= data.getString("ADDR");
			String addr2		= data.getString("ADDR2");
			
            /*****************************************************************************/
            
			sql = this.getInsertQuery(prize_yn,sttl_amt,addr);//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			   int idx = 0;
			   
			   
			   if (!GolfUtil.isNull(prize_yn))	pstmt.setString(++idx, data.getString("PRIZE_YN") 	);	//진행여부 (진행/취소) 수정시
			   if (GolfUtil.isNull(prize_yn)) pstmt.setString(++idx, data.getString("CNSL_YN")    	);	//상담여부 (완료/대기) 수정시
			   
			  // if (!GolfUtil.isNull(sttl_amt)) pstmt.setString(++idx, data.getString("STTL_AMT") 	);	//골프장리무진상세보기 수정시
			   if (!GolfUtil.isNull(sttl_amt)) pstmt.setString(++idx, data.getString("STR_PLC")  	);	//골프장리무진상세보기 수정시
			   if (!GolfUtil.isNull(sttl_amt)) pstmt.setString(++idx, data.getString("PIC_DATE") 	);	//골프장리무진상세보기 수정시
			   if (!GolfUtil.isNull(sttl_amt)) pstmt.setString(++idx, start_hh+start_mi 		 	);	//골프장리무진상세보기 수정시
			   if (!GolfUtil.isNull(sttl_amt)) pstmt.setString(++idx, data.getString("TOFF_DATE") 	);	//골프장리무진상세보기 수정시
			   if (!GolfUtil.isNull(sttl_amt)) pstmt.setString(++idx, end_hh+end_mi 		  		);	//골프장리무진상세보기 수정시
			   if (!GolfUtil.isNull(sttl_amt)) pstmt.setString(++idx, data.getString("GCC_NM")    	);	//골프장리무진상세보기 수정시
			   if (!GolfUtil.isNull(sttl_amt)) pstmt.setString(++idx, data.getString("CKD_CODE")  	);	//골프장리무진상세보기 수정시
			   if (!GolfUtil.isNull(sttl_amt)) pstmt.setString(++idx, data.getString("TK_PRS")    	);	//골프장리무진상세보기 수정시
			   
			   pstmt.setString(++idx, zp1+""+zp2						);	//골프잡지 수정시
			   if (!GolfUtil.isNull(addr)) pstmt.setString(++idx, data.getString("ADDR")  			);	//골프잡지 수정시
			   if (!GolfUtil.isNull(addr)) pstmt.setString(++idx, data.getString("ADDR2")    		);	//골프잡지 수정시
			   
			   pstmt.setString(++idx, data.getString("ADMIN_NO") );
			   pstmt.setLong(++idx, data.getLong("RECV_NO") );
			   
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
			
			
			if(result > 0) {
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
	
	//9개추가됨
    /** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getInsertQuery(String prize_yn, String sttl_amt, String addr){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGAPLCMGMT SET	\n");
		
		if (!GolfUtil.isNull(prize_yn)) sql.append("\t  PGRS_YN=?,  \n"); 		//진행여부 (진행/취소) 수정시
		if (GolfUtil.isNull(prize_yn)) sql.append("\t  CSLT_YN=?,  	\n");		//상담여부 (완료/대기) 수정시
		
		//if (!GolfUtil.isNull(sttl_amt)) sql.append("\t  STTL_AMT=?,  	\n");	//골프장리무진상세보기 수정시
		if (!GolfUtil.isNull(sttl_amt)) sql.append("\t  DPRT_PL_INFO=?,  	\n");	//골프장리무진상세보기 수정시
		if (!GolfUtil.isNull(sttl_amt)) sql.append("\t  PU_DATE=?,  	\n");	//골프장리무진상세보기 수정시
		if (!GolfUtil.isNull(sttl_amt)) sql.append("\t  PU_TIME=?,  	\n");	//골프장리무진상세보기 수정시
		if (!GolfUtil.isNull(sttl_amt)) sql.append("\t  TEOF_DATE=?,  	\n");	//골프장리무진상세보기 수정시
		if (!GolfUtil.isNull(sttl_amt)) sql.append("\t  TEOF_TIME=?,  	\n");	//골프장리무진상세보기 수정시
		if (!GolfUtil.isNull(sttl_amt)) sql.append("\t  GREEN_NM=?,  		\n");	//골프장리무진상세보기 수정시
		if (!GolfUtil.isNull(sttl_amt)) sql.append("\t  GOLF_LMS_CAR_KND_CLSS=?,  	\n");	//골프장리무진상세보기 수정시
		if (!GolfUtil.isNull(sttl_amt)) sql.append("\t  RIDG_PERS_NUM=?,  		\n");	//골프장리무진상세보기 수정시
		
		sql.append("\t  ZP=?,  		\n");			//골프잡지 수정시
		if (!GolfUtil.isNull(addr)) sql.append("\t  ADDR=?,  	\n");			//골프잡지 수정시
		if (!GolfUtil.isNull(addr)) sql.append("\t  DTL_ADDR=?,  		\n");		//골프잡지 수정시
		
		sql.append("\t  CHNG_MGR_ID=?, 	\n");
		sql.append("\t  CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDD') 	\n");
		sql.append("\t WHERE APLC_SEQ_NO=?	\n");
        return sql.toString();
    }
    
}
/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkTimeReserveDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 부킹 > xgolf > 예약처리
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.booking;

import java.io.Reader;
import java.io.Writer;
import java.io.CharArrayReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

import oracle.jdbc.driver.OracleResultSet; 
import oracle.sql.CLOB;

import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(주)미디어포스 
* @version	1.0 
******************************************************************************/
public class GolfBkTimeReserveDaoProc extends AbstractProc {

	public static final String TITLE = "부킹 > xgolf > 예약처리";

	public GolfBkTimeReserveDaoProc() {}
	
	/**
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);		

			String memb_id = data.getString("memb_id");		// 아이디
			String xFA = data.getString("xFA");				// 주중 : 1 / 주중 : 2 / 그린피 할인 : 3
			String xPrc = data.getString("xPrc");			// 포인트차감 : 1 / 횟수차감 : 2
			String xReser = data.getString("xReser");		// 예약 : 1 / 취소 : 2
			String rsvt_cdhd_grd_seq_no = data.getInt("intBkGrade")+"";	// 차감권한
			
			String golf_svc_aplc_clss = "";	//골프서비스신청구분코드 - 주말 : 0006 / 주중 : 0007 / 주중그린피 : 0008
			String pgrs_yn = "";	// 결제상태 - Y:예약 N:취소
			String cslt_yn = "";	// 결제수단 - Y:횟수 N:포인트
			int ridg_pers_num = 0;	// 결제금액
			String cbmo_use_clss = "";	// 사이버머니사용구분코드 0001:일반주중부킹 0002:일반주말부킹 0003:주중그린피할인 0004:파3부킹 0005:Sky72드림듄스 0006:Sky72드라이빙레인지
			
			
			if(xFA.equals("1")){
				golf_svc_aplc_clss = "0006";
				cbmo_use_clss = "0001";
			}else if(xFA.equals("2")){
				golf_svc_aplc_clss = "0007";
				cbmo_use_clss = "0002";
			}else{
				golf_svc_aplc_clss = "0008";
				cbmo_use_clss = "0003";
			}
			
			if(xPrc.equals("1")){
				cslt_yn = "Y";
			}else{
				cslt_yn = "N";
			}
			
			if(xReser.equals("1")){
				pgrs_yn = "Y";
			}else{
				pgrs_yn = "N";
			}
			
			debug("=================GolfBkTimeReserveDaoProc============= =>memb_id " + memb_id);
			debug("=================GolfBkTimeReserveDaoProc============= =>xFA " + xFA);
			debug("=================GolfBkTimeReserveDaoProc============= =>xPrc " + xPrc);
			debug("=================GolfBkTimeReserveDaoProc============= =>xReser " + xReser);
			debug("=================GolfBkTimeReserveDaoProc============= =>golf_svc_aplc_clss " + golf_svc_aplc_clss);
			debug("=================GolfBkTimeReserveDaoProc============= =>pgrs_yn " + pgrs_yn);
			debug("=================GolfBkTimeReserveDaoProc============= =>rsvt_cdhd_grd_seq_no " + rsvt_cdhd_grd_seq_no);

			// 01. 신청관리 페이지에 등록해준다. (예약, 취소를 다 입력해준다.)
			debug("=================GolfBkTimeReserveDaoProc============= 01. 신청관리 페이지에 등록해준다.");
				
            /**SEQ_NO 가져오기**************************************************************/
			sql = this.getNextValQuery(); 
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();			
			long max_seq_no = 0L;
			if(rs.next()){
				max_seq_no = rs.getLong("SEQ_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
            /**결제금액 가져오기**************************************************************/
			sql = this.getCyberMoneyInfoQuery(); 
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
			if(rs.next()){
				if(xPrc.equals("1")){
					ridg_pers_num = rs.getInt("GEN_WKD_BOKG_AMT");
				}else if(xPrc.equals("2")){
					ridg_pers_num = rs.getInt("GEN_WKE_BOKG_AMT");
				}else{
					ridg_pers_num = rs.getInt("WKD_GREEN_DC_AMT");
				}
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
            /**Insert************************************************************************/
            sql = this.getInsertQuery();
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
        	pstmt.setLong(++idx, max_seq_no );
        	pstmt.setString(++idx, golf_svc_aplc_clss ); 
        	pstmt.setString(++idx, pgrs_yn ); 
        	pstmt.setString(++idx, cslt_yn ); 
        	pstmt.setInt(++idx, ridg_pers_num ); 
        	pstmt.setString(++idx, memb_id); 
        	pstmt.setString(++idx, rsvt_cdhd_grd_seq_no); 
        	
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();      

            // 02. 예약이며 사이버 머니를 사용했을 경우 사이버 머니를 차감한다.
			if(xReser.equals("1") && xPrc.equals("1")){
				debug("=================GolfBkTimeReserveDaoProc============= 02. 예약이며 사이버 머니를 사용했을 경우 사이버 머니를 차감한다.");
	            	
            	// 02-0. 현재 사이버 머니 금액을 가져온다.
    			sql = this.getCyberMoneyQuery(); 
                pstmt = conn.prepareStatement(sql);
            	pstmt.setString(1, memb_id ); 	
                rs = pstmt.executeQuery();			
    			int cyberMoney = 0;
    			if(rs.next()){
    				cyberMoney = rs.getInt("TOT_AMT");
    			}
    			if(rs != null) rs.close();
                if(pstmt != null) pstmt.close();
                
            	// 02-1. 사이버머니 사용내역 테이블에 금액을 넣어준다.
    			debug("=================GolfBkTimeReserveDaoProc============= 02-1. 사이버머니 사용내역 테이블에 금액을 넣어준다.");
                /**SEQ_NO 가져오기**************************************************************/
    			String sql2 = this.getCyberMoneyNextValQuery(); 
                PreparedStatement pstmt2 = conn.prepareStatement(sql2);
                ResultSet rs2 = pstmt2.executeQuery();			
    			long cyber_money_max_seq_no = 0L;
    			if(rs2.next()){
    				cyber_money_max_seq_no = rs2.getLong("SEQ_NO");
    			}
    			if(rs2 != null) rs2.close();
                if(pstmt2 != null) pstmt2.close();
                
                /**Insert************************************************************************/
                
                String sql3 = this.getMemberTmInfoQuery();
    			PreparedStatement pstmt3 = conn.prepareStatement(sql3);
    			
    			int totCyberMoney = cyberMoney-ridg_pers_num;
    			
    			idx = 0;
    			pstmt3.setLong(++idx, cyber_money_max_seq_no ); 		//COME_SEQ_SEQ_NO
    			pstmt3.setString(++idx, memb_id ); 		//CDHD_ID
    			pstmt3.setInt(++idx, ridg_pers_num );					//ACM_DDUC_AMT
    			pstmt3.setInt(++idx, totCyberMoney );					//REST_AMT
    			pstmt3.setString(++idx, cbmo_use_clss );				//cbmo_use_clss : 0001:일반주중부킹 0002:일반주말부킹 0003:주중그린피할인 0004:파3부킹 0005:Sky72드림듄스 0006:Sky72드라이빙레인지
    			pstmt3.setLong(++idx, max_seq_no );					//GOLF_SVC_RSVT_NO : 골프서비스예약번호  	

            	
    			result = pstmt3.executeUpdate();
                if(pstmt3 != null) pstmt3.close();
    			
                
            	// 02-2. 회원테이블에 사이버머니 내역을 업데이트 해준다.
    			debug("=================GolfBkTimeReserveDaoProc============= 02-2. 회원테이블에 사이버머니 내역을 업데이트 해준다.");
                sql3 = this.getMemberUpdateQuery(ridg_pers_num);
    			pstmt3 = conn.prepareStatement(sql3);
    			pstmt3.setString(1, memb_id ); 		//CDHD_ID
    			
    			//result = pstmt3.executeUpdate();
                if(pstmt3 != null) pstmt3.close();
            }
	

			
			
			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
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

	

	
    /** ***********************************************************************
    * Query를 생성하여 리턴한다.     
    ************************************************************************ */
    private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
        
		sql.append("\n");
		sql.append("\t  INSERT INTO BCDBA.TBGAPLCMGMT(												\n");
		sql.append("\t  		APLC_SEQ_NO, GOLF_SVC_APLC_CLSS, PGRS_YN, CSLT_YN, RIDG_PERS_NUM	\n");
		sql.append("\t  		, CDHD_ID, REG_ATON, RSVT_CDHD_GRD_SEQ_NO							\n");
		sql.append("\t  		) VALUES (															\n");
		sql.append("\t  		?, ?, ?, ?, ?														\n");
		sql.append("\t  		, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), ?							\n");
		sql.append("\t  		)																	\n");

        return sql.toString();
    }

    /** ***********************************************************************
    * Max IDX Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
        sql.append("SELECT NVL(MAX(APLC_SEQ_NO),0)+1 SEQ_NO FROM BCDBA.TBGAPLCMGMT \n");
		return sql.toString();
    }

	/** ***********************************************************************
    * 사이버머니 금액 정책 가져오기    
    ************************************************************************ */ 
    private String getCyberMoneyInfoQuery(){
        StringBuffer sql = new StringBuffer();

		sql.append("\n");  
		sql.append("\t  SELECT 													\n");  
		sql.append("\t  GEN_WKD_BOKG_AMT, GEN_WKE_BOKG_AMT, WKD_GREEN_DC_AMT	\n");  
		sql.append("\t  FROM BCDBA.TBGCBMOPLCYMGMT								\n");
		
		return sql.toString();
    }


 	/** ***********************************************************************
	* 현재 사이버머니 총액 가져오기    
	************************************************************************ */
	private String getCyberMoneyQuery(){
		StringBuffer sql = new StringBuffer();		

		sql.append("\n");
		sql.append("\t  SELECT (CBMO_ACM_TOT_AMT-CBMO_DDUC_TOT_AMT) AS TOT_AMT		\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD										\n");
		sql.append("\t  WHERE CDHD_ID=?												\n");
		
		return sql.toString();
	}

    /** ***********************************************************************
    * Max IDX Query를 생성하여 리턴한다. - 사이버머니 적립 최대 idx    
    ************************************************************************ */
    private String getCyberMoneyNextValQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
        sql.append("SELECT NVL(MAX(COME_SEQ_SEQ_NO),0)+1 SEQ_NO FROM BCDBA.TBGCBMOUSECTNTMGMT \n");
		return sql.toString();
    }
	
 	/** ***********************************************************************
	* 사이버머니 등록하기    
	************************************************************************ */
	private String getMemberTmInfoQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  INSERT INTO BCDBA.TBGCBMOUSECTNTMGMT (													\n");
		sql.append("\t  		COME_SEQ_SEQ_NO, CDHD_ID, ACM_DDUC_CLSS, ACM_DDUC_AMT, REST_AMT, COME_ATON		\n");
		sql.append("\t  		, CBMO_USE_CLSS, GOLF_SVC_RSVT_NO												\n");
		sql.append("\t  		) VALUES (																		\n");
		sql.append("\t  		?, ?, 'N', ?, ?, TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'), ?, ?						\n");
		sql.append("\t  		)																				\n");
		return sql.toString();
	}
	
 	/** ***********************************************************************
	* 회원정보 업데이트하기
	************************************************************************ */
	private String getMemberUpdateQuery(int cyberMoney){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  	UPDATE BCDBA.TBGGOLFCDHD								\n");
		sql.append("\t  	SET CBMO_DDUC_TOT_AMT=CBMO_DDUC_TOT_AMT+"+cyberMoney+"	\n");
		sql.append("\t  	WHERE CDHD_ID=?											\n");
		return sql.toString();
	}
    
}

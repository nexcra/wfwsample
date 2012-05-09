/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemInsDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 회원 > 회원가입처리
*   적용범위  : golf 
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.member;

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
public class GolfMemPresentDaoProc extends AbstractProc {

	public static final String TITLE = "사이버머니 > 등록처리";

	public GolfMemPresentDaoProc() {}
	
	public int execute(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt_del = null;
		String sql = "";
		String sql_del = "";
		ResultSet rs = null;
		String userId = "";

				
		try {

			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount(); 
			 } 
						 
			 String flag = data.getString("flag");
			 
			 if (flag == null) flag = "";
			 if (flag != null) flag.trim();
			 			 
			 String socid = data.getString("socid");
			 if (socid == null) socid = "";
			 if (socid != null) socid.trim();
			 
			 //String socid = data.getString("socid").trim();
			 		 
			 
			 /*KT Olleh Club으로 신청 결제시 정식 회원가입 전이므로 id가 존재하지 않는다
			  * 따라서 신청시에는 CDHD_ID에 주민번호를 할당하고,
			  * 정식 회원 가입 완료시 CDHD_ID로 업데이트 한다
			  */
			 if (flag != "" && flag.equals("KT")){
				 userId = socid;
			 }
			 
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			// 01. 현재 신청 내역이 있는지 확인한다.
			sql = this.getPreQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, userId ); 	
            rs = pstmt.executeQuery();	
			if(rs.next()){
				// 02. 신청 내역이 있으면 삭제한다.
				
				sql_del = this.getPreDelQuery();
				pstmt_del = conn.prepareStatement(sql_del);
				pstmt_del.setString(1, userId ); 		//CDHD_ID
				
				pstmt_del.executeUpdate();
	            if(pstmt_del != null) pstmt_del.close();
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
            
            // 03. 등록한다.
            /**SEQ_NO 가져오기**************************************************************/
			String sql2 = this.getNextValQuery(); 
            PreparedStatement pstmt2 = conn.prepareStatement(sql2);
            ResultSet rs2 = pstmt2.executeQuery();			
			long max_seq_no = 0L;
			if(rs2.next()){
				max_seq_no = rs2.getLong("SEQ_NO");
			}
			if(rs2 != null) rs2.close();
            if(pstmt2 != null) pstmt2.close();
            
            /**Insert************************************************************************/
            
            String sql3 = this.getMemberTmInfoQuery();
			PreparedStatement pstmt3 = conn.prepareStatement(sql3);

			String gds_code 				= data.getString("gds_code").trim();
			String rcvr_nm 					= data.getString("rcvr_nm").trim();
			String zp 						= data.getString("zp").trim();
			String addr 					= data.getString("addr").trim();
			String dtl_addr 				= data.getString("dtl_addr").trim();
			String hp_ddd_no 				= data.getString("hp_ddd_no").trim();
			String hp_tel_hno 				= data.getString("hp_tel_hno").trim();
			String hp_tel_sno 				= data.getString("hp_tel_sno").trim();
			String addr_clss 				= data.getString("addr_clss").trim();
			
			int idx = 0;
			pstmt3.setLong(++idx, max_seq_no ); 
			pstmt3.setString(++idx, userId ); 
			pstmt3.setString(++idx, rcvr_nm ); 
			pstmt3.setString(++idx, gds_code ); 
			pstmt3.setString(++idx, hp_ddd_no ); 
			pstmt3.setString(++idx, hp_tel_hno ); 
			pstmt3.setString(++idx, hp_tel_sno ); 
			pstmt3.setString(++idx, zp ); 
			pstmt3.setString(++idx, addr ); 
			pstmt3.setString(++idx, dtl_addr );			
			pstmt3.setString(++idx, addr_clss );
        	
			result = pstmt3.executeUpdate();

			debug("=========result proc : " + result);
			
            if(pstmt3 != null) pstmt3.close();
			
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

	


 	/** ***********************************************************************
	* 현재 등록여부 가져오기    
	************************************************************************ */
	private String getPreQuery(){
		StringBuffer sql = new StringBuffer();	
		sql.append("\n");
		sql.append("\t  SELECT SEQ_NO	\n");
		sql.append("\t  FROM BCDBA.TBGCDHDRIKMGMT	\n");
		sql.append("\t  WHERE CDHD_ID=?	\n");
		return sql.toString();
	}

    /** ***********************************************************************
    * Max IDX Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
        sql.append("SELECT NVL(MAX(SEQ_NO),0)+1 SEQ_NO FROM BCDBA.TBGCDHDRIKMGMT \n");
		return sql.toString();
    }
	
 	/** ***********************************************************************
	* 사이버머니 등록하기    
	************************************************************************ */
	private String getMemberTmInfoQuery(){

		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t	INSERT INTO BCDBA.TBGCDHDRIKMGMT (SEQ_NO, CDHD_ID, RCVR_NM, GOLF_TMNL_GDS_CODE, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, ZP	\n");
		sql.append("\t			, ADDR, DTL_ADDR, SND_YN, ACRG_CDHD_JONN_DATE, APLC_ATON, NW_OLD_ADDR_CLSS) VALUES (	\n");
		sql.append("\t			?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'N', TO_CHAR(SYSDATE,'YYYYMMDD'), TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), ?	\n");
		sql.append("\t			)	\n");
		return sql.toString();
	}
	
 	/** ***********************************************************************
	* 신청내역 삭제하기
	************************************************************************ */
	private String getPreDelQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  	DELETE FROM BCDBA.TBGCDHDRIKMGMT WHERE CDHD_ID=?	\n");
		return sql.toString();
	}
    
}

/*****************************************************************************
*  클래스명	:	GolfLoungTMProc
*  작 성 자	:	강선영
*  내    용	:	골프라운지 TM 가입
*  적용범위	:	bccard
*  작성일자	:	2009.07.03
*
**************** 수정이력 ****************************************************
*    일자     작성자   변경사항
*20110401 	이경희 	TM 구분 12, 13에 따른 코드추가
*20110412   이경희	아웃룩익스프레스 사용으로 한글 제목 깨지는 현상 처리
*****************************************************************************/
package com.bccard.golf.action.admin.tm_member;

import java.io.Serializable;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Vector;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.WaContext;
 


/**
 *  골프라운지 TM 가입 PROC
 * @version 2009.07.03
 */
public class GolfLoungTMProc extends AbstractProc implements Serializable{

	private final String sqlTM;
	private final String sqlMojib;
	private final String sqlMember;
	private final String sqlUpRslt;
	private final String sqlInFee;
	private final String sqlUpFeeCancel;
	private final String SqlResult;

	private final String sqlUpRsltMojib;
	private final String sqlUpJumin;
	private final String sqlMailSend;
	private final String sqlTmCnt;



    /*********************************************************************
	* GolfLoungTMProc Constructor
	* @param N/A
	**********************************************************************/
	public GolfLoungTMProc(){
		
		/* TM 등록대상 조회 */
		StringBuffer sql = new StringBuffer();
        sql.append(" SELECT JUMIN_NO,GOLF_CDHD_GRD_CLSS,MB_CDHD_NO,HG_NM,EMAIL_ID,AUTH_CLSS,CARD_NO,JOIN_CHNL,VALD_LIM,RECP_DATE, \n");
		sql.append("		SUBSTRB(HP_DDD_NO,2,3) HP_DDD_NO ,DECODE(SUBSTRB(HP_TEL_HNO,1,1),'0','',SUBSTRB(HP_TEL_HNO,1,1))  ||  SUBSTRB(HP_TEL_HNO,2,3)  HP_TEL_HNO , HP_TEL_SNO, \n");
		sql.append("		DISC_CLSS,DC_AMT,RCRU_PL_CLSS,SCAL_LCAL_CLSS	  \n"); 
		sql.append("   FROM BCDBA.TBLUGTMCSTMR  \n");
		sql.append("  WHERE RECP_DATE = TO_CHAR(SYSDATE,'yyyyMMdd')  \n"); //수신일자(로딩한일자)
 // 99:미처리 01:회원가입성공 02:기회원가입 03:포인트잔액부족 04:포인트차감실패 05:포인트 취소처리(유료회원절차시 web DB 오류사항) 06:포인트취소 실패[긴급오류]
 // 07:포인트조회실패
		sql.append("    AND TB_RSLT_CLSS = '99'  \n");
		sql.append("    AND RND_CD_CLSS ='2'  \n"); //2:골프라운지 1:
		sql.append("    AND ACPT_CHNL_CLSS ='1'  \n"); //2:골프라운지 1:라운지

		sqlTM = sql.toString(); 


		/* 모집인 등록대상 조회 */
		StringBuffer sql0 = new StringBuffer();
        sql0.append(" SELECT JUMIN_NO,GOLF_CDHD_GRD_CLSS,MB_CDHD_NO,HG_NM,EMAIL_ID,AUTH_CLSS,CARD_NO,JOIN_CHNL,HP_DDD_NO,HP_TEL_HNO,HP_TEL_SNO,RECP_DATE,VALD_LIM,DISC_CLSS,DC_AMT,RECP_DATE	  \n"); 
		sql0.append("   FROM BCDBA.TBLUGTMCSTMR  \n");
		sql0.append("  WHERE TB_RSLT_CLSS = '99' \n"); //수신일자(로딩한일자)
 // 99:미처리 01:회원가입성공 02:기회원가입 03:포인트잔액부족 04:포인트차감실패 05:포인트 취소처리(유료회원절차시 web DB 오류사항) 06:포인트취소 실패[긴급오류]
 // 07:포인트조회실패
		sql0.append("    AND RND_CD_CLSS ='2'  \n"); //2:골프라운지 1:
		sql0.append("    AND ACPT_CHNL_CLSS ='2'  \n"); 

		sqlMojib = sql0.toString();



		/* 골프라운지 TM 대상 조회(유효기간 지난고객 가능, 해지회원 가능 ,멤버쉽 고객 아닌 등급 가능) */
		StringBuffer sql2 = new StringBuffer();
		//sql2.append(" SELECT JOIN_CHNL  FROM BCDBA.TBGGOLFCDHD				  \n");
		//sql2.append(" WHERE JUMIN_NO = ? \n");

		sql2.append(" SELECT JOIN_CHNL  FROM BCDBA.TBGGOLFCDHD		");
		sql2.append(" WHERE JUMIN_NO = ? AND NVL(SECE_YN,'N') = 'N' ");			
		sql2.append(" AND  ACRG_CDHD_JONN_DATE <= TO_CHAR(SYSDATE,'yyyyMMdd') AND ACRG_CDHD_END_DATE >= TO_CHAR(SYSDATE,'yyyyMMdd') ");
		sql2.append(" AND  CDHD_CTGO_SEQ_NO IN (SELECT CDHD_CTGO_SEQ_NO ");
		sql2.append("                            FROM BCDBA.TBGCMMNCODE T1 ");
		sql2.append("                            JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.GOLF_CMMN_CODE = T2.CDHD_SQ2_CTGO ");
		sql2.append("                            WHERE T1.GOLF_URNK_CMMN_CLSS='0000' AND T1.GOLF_URNK_CMMN_CODE='0005' AND CDHD_SQ1_CTGO='0002' ");
		sql2.append("                            AND CDHD_CTGO_SEQ_NO NOT IN ('8','18','16') ) --무료,기업은행골드,골멤 제외  ");
	
		
	
		sqlMember = sql2.toString();

		/* 골프라운지 TM 결과 처리 */
		StringBuffer sql3 = new StringBuffer();
		sql3.append(" UPDATE BCDBA.TBLUGTMCSTMR  			\n"); 
		sql3.append("    SET TB_RSLT_CLSS = ? ,  	 \n");
		sql3.append("        REJ_RSON = ? , 	 \n");
		sql3.append("		 VALD_LIM = ? , 	 \n");
		sql3.append("		 STTL_FAIL_RSON_CTNT = ?  	 \n");
		sql3.append("  WHERE JUMIN_NO = ?				\n");
		sql3.append("  AND RECP_DATE = TO_CHAR(SYSDATE,'yyyyMMdd')	\n");
		sql3.append("  AND RND_CD_CLSS='2'	\n");

		sqlUpRslt = sql3.toString();	 

		/* 골프라운지 모집인 결과 처리 */
		StringBuffer sql13 = new StringBuffer();
		sql13.append(" UPDATE BCDBA.TBLUGTMCSTMR  			\n"); 
		sql13.append("    SET TB_RSLT_CLSS = ? ,  	 \n");
		sql13.append("        REJ_RSON = ? , 	 \n");
		sql13.append("		 VALD_LIM = ? , 	 \n");
		sql13.append("		 STTL_FAIL_RSON_CTNT = ?  	 \n");
		sql13.append("  WHERE JUMIN_NO = ?				\n");
		sql13.append("  AND RECP_DATE = ?	\n");
		sql13.append("  AND RND_CD_CLSS='2'	\n");

		sqlUpRsltMojib = sql13.toString();	 



		

		/* 골프라운지 연회비 내역  */
		StringBuffer sql5 = new StringBuffer(); 
		sql5.append(" INSERT INTO  BCDBA.TBGLUGANLFEECTNT  	\n");
		sql5.append("   (JUMIN_NO,AUTH_NO,CARD_NO, AUTH_DATE,AUTH_TIME,AUTH_AMT,AUTH_CLSS,RND_CD_CLSS,MB_CDHD_NO) 	 \n");
		sql5.append("  VALUES ( ?, ?,?, TO_CHAR(SYSDATE,'yyyyMMdd'), TO_CHAR(SYSDATE,'hh24miss') , ? , ?, '2', ? )	\n");
 
		sqlInFee = sql5.toString();	

		/* 골프라운지 연회비 내역 처리*/
		StringBuffer sql6 = new StringBuffer();
		sql6.append(" UPDATE BCDBA.TBGLUGANLFEECTNT SET  	\n");
		sql6.append("  CNCL_DATE =TO_CHAR(SYSDATE,'yyyyMMdd'), CNCL_TIME =TO_CHAR(SYSDATE,'hh24miss') 	 \n");
		sql6.append(" WHERE JUMIN_NO = ? 	\n");
		sql6.append(" AND  AUTH_NO =? 	\n");
		sql6.append(" AND  CARD_NO =? 	\n");
		sql6.append(" AND  RND_CD_CLSS = '2' 	\n");
		//sql6.append(" AND  AUTH_DATE = TO_CHAR(SYSDATE,'yyyyMMdd') 	\n");


		
		sqlUpFeeCancel = sql6.toString();	





		/* 골프라운지 TM 실패 결과 조회 */ 
		StringBuffer sql10 = new StringBuffer();
        sql10.append(" SELECT HG_NM, JUMIN_NO, TB_RSLT_CLSS, REJ_RSON ,NVL(SQ1_TCALL_DATE,TO_CHAR(SYSDATE,'yyyyMMdd')) SQ1_TCALL_DATE, NVL(STTL_FAIL_RSON_CTNT,' ')  STTL_FAIL_RSON_CTNT,   \n"); 
		sql10.append("        DECODE(JOIN_CHNL,'03','H&C','10','CIC코리아','11','TCK','12','리딩아이','13','윌앤비전', (SELECT  GOLF_CMMN_CODE_NM  FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS='0050' AND A.JOIN_CHNL = GOLF_CMMN_CODE)) JOIN_CHNL,  \n");
		sql10.append("        DECODE(GOLF_CDHD_GRD_CLSS,'1','Gold','2','Blue','3','Champion','')  GOLF_CDHD_GRD_CLSS \n");
		sql10.append("   FROM BCDBA.TBLUGTMCSTMR A \n");
		sql10.append("  WHERE RECP_DATE = TO_CHAR(SYSDATE,'yyyyMMdd')  \n"); //수신일자(로딩한일자)
		sql10.append("    AND TB_RSLT_CLSS NOT IN ('00', '01')  \n");
		sql10.append("    AND RND_CD_CLSS ='2'  \n"); //2:골프라운지 1:라운지
		sql10.append("    ORDER BY JOIN_CHNL  \n"); //2:골프라운지 1:라운지

		SqlResult = sql10.toString(); 

		/* 골프라운지 주민번호 마스킹 update */
		StringBuffer sql14 = new StringBuffer();
		sql14.append(" UPDATE BCDBA.TBLUGTMCSTMR  			\n"); 
		sql14.append("    SET JUMIN_NO = ?   	 \n");
		sql14.append("  WHERE MB_CDHD_NO = ?				\n");
		sql14.append("  AND RECP_DATE = ?	\n");
		sql14.append("  AND RND_CD_CLSS='2'	\n");

		sqlUpJumin = sql14.toString();	 
 
		/* 메일send */
		StringBuffer sql15 = new StringBuffer();
        sql15.append(" SELECT JUMIN_NO,GOLF_CDHD_GRD_CLSS,MB_CDHD_NO,HG_NM,EMAIL_ID,AUTH_CLSS,CARD_NO,JOIN_CHNL,VALD_LIM,RECP_DATE, \n");
		sql15.append("		SUBSTRB(HP_DDD_NO,2,3) HP_DDD_NO ,DECODE(SUBSTRB(HP_TEL_HNO,1,1),'0','',SUBSTRB(HP_TEL_HNO,1,1))  ||  SUBSTRB(HP_TEL_HNO,2,3)  HP_TEL_HNO , HP_TEL_SNO, \n");
		sql15.append("		DISC_CLSS,DC_AMT	  \n"); 
		sql15.append("   FROM BCDBA.TBLUGTMCSTMR  \n");
		sql15.append("  WHERE RECP_DATE = ?  \n"); //수신일자(로딩한일자)
		sql15.append("    AND TB_RSLT_CLSS IN ('01','00')  \n");
		sql15.append("    AND RND_CD_CLSS ='2'  \n"); //2:골프라운지 1:
		sql15.append("    AND ACPT_CHNL_CLSS ='1'  \n"); //1.TM
		sql15.append("    AND JOIN_CHNL ='11'  \n"); //TCK

		sqlMailSend = sql15.toString(); 

		StringBuffer sql16 = new StringBuffer();
		 		 		
		/* TM 성공 횟수  */
		sql16.append("SELECT  AA,  BB , BB-AA CC \n");
		sql16.append("FROM ( \n");
		sql16.append("    SELECT NVL(SUM(DECODE(TB_RSLT_CLSS,'00',0,'01',0,1)),0) AA , COUNT(*) BB  \n");
		sql16.append("    FROM BCDBA.TBLUGTMCSTMR A  \n");
		sql16.append("    WHERE RECP_DATE = TO_CHAR(SYSDATE,'yyyyMMdd')  \n");
		sql16.append("    AND RND_CD_CLSS ='2'  \n");
		sql16.append("    AND ACPT_CHNL_CLSS ='1' )  \n");
		
		sqlTmCnt = sql16.toString(); 


	} 
 


	/*****************************************************************
	* 1.  TM 등록대상 조회 
	******************************************************************/
	public Vector getListTMLoungApply (WaContext context) throws Exception{

		Connection         conn = null;
		PreparedStatement pstmt = null;
		ResultSet            rs = null;	
		Vector           result = new Vector();

		try{
			conn = context.getDbConnection("default", null);

			pstmt = conn.prepareStatement(sqlTM);			

			rs = pstmt.executeQuery();            
 
			while (rs.next())	{
				Hashtable data = new Hashtable();
				
				data.put("JUMIN_NO",rs.getString("JUMIN_NO").trim());
				data.put("GOLF_CDHD_GRD_CLSS",rs.getString("GOLF_CDHD_GRD_CLSS").trim());
				data.put("MB_CDHD_NO",rs.getString("MB_CDHD_NO").trim());
				data.put("HG_NM",rs.getString("HG_NM").trim());
				data.put("EMAIL_ID",StrUtil.isNull(rs.getString("EMAIL_ID"),""));
				data.put("AUTH_CLSS",StrUtil.isNull(rs.getString("AUTH_CLSS"),""));
				data.put("CARD_NO",StrUtil.isNull(rs.getString("CARD_NO"),""));
				data.put("JOIN_CHNL",StrUtil.isNull(rs.getString("JOIN_CHNL"),""));
				data.put("HP_DDD_NO",StrUtil.isNull(rs.getString("HP_DDD_NO"),""));
				data.put("HP_TEL_HNO",StrUtil.isNull(rs.getString("HP_TEL_HNO"),""));
				data.put("HP_TEL_SNO",StrUtil.isNull(rs.getString("HP_TEL_SNO"),""));
				data.put("RECP_DATE",StrUtil.isNull(rs.getString("RECP_DATE"),""));
				data.put("VALD_LIM",StrUtil.isNull(rs.getString("VALD_LIM"),""));
				data.put("DISC_CLSS",StrUtil.isNull(rs.getString("DISC_CLSS"),""));
				data.put("DC_AMT",StrUtil.isNull(rs.getString("DC_AMT"),""));
				data.put("RECP_DATE",StrUtil.isNull(rs.getString("RECP_DATE"),""));
				data.put("RCRU_PL_CLSS",StrUtil.isNull(rs.getString("RCRU_PL_CLSS"),""));
				data.put("PRODUCT_NO",StrUtil.isNull(rs.getString("SCAL_LCAL_CLSS"),""));
				
				
				
				

				//JUMIN_NO,GOLF_CDHD_GRD_CLSS,MB_CDHD_NO,HG_NM,EMAIL_ID,AUTH_CLSS,CARD_NO,JOIN_CHNL,VALD_LIM,RECP_DATE,RCRU_PL_CLSS,SCAL_LCAL_CLSS

				result.add(data);
			}				

		}catch(Exception e){
			info("GolfLoungTMProc|getListTMLoungApply Exceptioin : ", e);
			throw e;

		}finally{
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}

		return result;
	}
 

	/*****************************************************************
	* 1-1.  모집인 등록대상 조회 
	******************************************************************/
	public Vector getListMojibLoungApply (WaContext context) throws Exception{

		Connection         conn = null;
		PreparedStatement pstmt = null;
		ResultSet            rs = null;	
		Vector           result = new Vector();

		try{
			conn = context.getDbConnection("default", null);

			pstmt = conn.prepareStatement(sqlMojib);			

			rs = pstmt.executeQuery();            
 
			while (rs.next())	{
				Hashtable data = new Hashtable();
				data.put("JUMIN_NO",rs.getString("JUMIN_NO").trim());
				data.put("GOLF_CDHD_GRD_CLSS",rs.getString("GOLF_CDHD_GRD_CLSS").trim());
				data.put("MB_CDHD_NO",rs.getString("MB_CDHD_NO").trim());
				data.put("HG_NM",rs.getString("HG_NM").trim());
				data.put("EMAIL_ID",StrUtil.isNull(rs.getString("EMAIL_ID"),""));
				data.put("AUTH_CLSS",StrUtil.isNull(rs.getString("AUTH_CLSS"),""));
				data.put("CARD_NO",StrUtil.isNull(rs.getString("CARD_NO"),""));
				data.put("JOIN_CHNL",StrUtil.isNull(rs.getString("JOIN_CHNL"),""));
				data.put("HP_DDD_NO",StrUtil.isNull(rs.getString("HP_DDD_NO"),""));
				data.put("HP_TEL_HNO",StrUtil.isNull(rs.getString("HP_TEL_HNO"),""));
				data.put("HP_TEL_SNO",StrUtil.isNull(rs.getString("HP_TEL_SNO"),""));
				data.put("RECP_DATE",StrUtil.isNull(rs.getString("RECP_DATE"),""));
				data.put("VALD_LIM",StrUtil.isNull(rs.getString("VALD_LIM"),""));
				data.put("DISC_CLSS",StrUtil.isNull(rs.getString("DISC_CLSS"),""));
				data.put("DC_AMT",StrUtil.isNull(rs.getString("DC_AMT"),""));
				data.put("RECP_DATE",StrUtil.isNull(rs.getString("RECP_DATE"),""));

				
				

				result.add(data);
			}				

		}catch(Exception e){
			info("GolfLoungTMProc|getListMojibLoungApply Exceptioin : ", e);
			throw e;

		}finally{
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}

		return result; 
	} 


	/*****************************************************************
	* 1. 골프라운지 TM 주민번호 마스킹 처리 실주민번호로 UPDATE
	******************************************************************/
	public int getUpJuminNo (WaContext context, String jumin_no, String mb_cdhd_no, String str_date) throws Exception{

		Connection         conn = null;
		PreparedStatement pstmt = null;
		int				cnt = 0;

		try{
			conn = context.getDbConnection("default", null);

			pstmt = conn.prepareStatement(sqlUpJumin);
			pstmt.setString(1, jumin_no);
			pstmt.setString(2, mb_cdhd_no);
			pstmt.setString(3, str_date);

			cnt = pstmt.executeUpdate();            


		}catch(Exception e){
			info("GolfLoungTMProc|getUpJuminNo Exceptioin : ", e);
			throw e;

		}finally{
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}

		return cnt;
	}




	/*****************************************************************
	* 2. 골프라운지 유료회원 여부 조회  Y:가입불가능고객(현유료회원임) N:가입가능고객
	******************************************************************/
	public String getIsFeeFree (WaContext context, String jumin_no) throws Exception{

		Connection         conn = null;
		PreparedStatement pstmt = null;
		ResultSet            rs = null;	
		String				result = "N";

		try{
			conn = context.getDbConnection("default", null);

			pstmt = conn.prepareStatement(sqlMember);
			pstmt.setString(1, jumin_no);

			rs = pstmt.executeQuery();            

			if (rs.next())	{				
				result= "Y"; //rs.getString(1);					
			}				

		}catch(Exception e){
			info("GolfLoungTMProc|getIsFeeFree Exceptioin : ", e);
			throw e;

		}finally{
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}

		return result;
	}

	/**************************************************************
	* 3. 골프라운지 TM결과 처리 (01:성공 02:기가입 03:포인트부족 99:미처리) 
	**************************************************************/
	public int updateIsFree(WaContext context, String socid, String code, String vald_lim, String fail_ret,String recp_date ) throws Exception{
		Connection          conn = null;
		PreparedStatement  pstmt = null;
		String				rson = null;
		int cnt		= 0; 

		try{
			conn = context.getDbConnection("default", null);

			if ("".equals(recp_date)) {
				pstmt = conn.prepareStatement(sqlUpRslt);
			}else {
				pstmt = conn.prepareStatement(sqlUpRsltMojib);
			}
			 // 99:미처리 01:회원가입성공 02:기회원가입 03:포인트잔액부족 04:포인트차감실패 05:포인트 취소처리(유료회원절차시 web DB 오류사항) 06:포인트취소 실패[긴급오류]
 // 07:포인트조회실패
			String rseult = code.substring(0,2);
			
			debug("rseult="+rseult+",code="+code);
			
			if ("01".equals(rseult)) rson ="회원가입성공";
			else if ("00".equals(rseult)) rson = code.substring(2, code.length());
			else if ("02".equals(rseult)) rson ="기회원가입";
			else if ("09".equals(rseult)) rson ="회원사회원번호 없음-주민번호미존재"; //2010-03-22 추가
			else if ("03".equals(rseult)) rson ="포인트잔액부족";
			else if ("04".equals(rseult)) rson ="포인트차감실패";
			else if ("05".equals(rseult)) rson ="포인트취소처리";
			else if ("06".equals(rseult)) rson ="포인트취소실패[긴급오류확인대상]";
			else if ("07".equals(rseult)) rson ="포인트조회실패";
			else if ("11".equals(rseult)) rson ="카드조회실패";
			else if ("12".equals(rseult)) rson ="카드승인실패";
			else if ("15".equals(rseult)) rson ="카드취소처리";
			else if ("16".equals(rseult)) rson ="카드취소실패[긴급오류확인대상]";
			
			int idx = 0;

			pstmt.setString(++idx, rseult);
			pstmt.setString(++idx, rson);
			pstmt.setString(++idx, vald_lim);
			pstmt.setString(++idx, fail_ret);

			pstmt.setString(++idx, socid);
			if (!"".equals(recp_date)) {
				pstmt.setString(++idx, recp_date);
			}


			cnt = pstmt.executeUpdate();            

		}catch(Exception e){
			info("GolfLoungTMProc|updateIsFree Exceptioin : ", e);
			throw e;
		}finally{
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}		
		}
		return cnt;
	}

	


	/**************************************************************
	* 5. 골프라운지 연회비 내역 처리
	**************************************************************/
	public int insertGolfFee(WaContext context, String jumin_no, String auth_no, String card_no, String auth_amt, String auth_clss , String mb_no ) throws Exception, Throwable{
		Connection          conn = null;
		PreparedStatement  pstmt = null;
		int cnt		= 0; 

		try{
			conn = context.getDbConnection("default", null);

			//취소일경우
			if ("9".equals(auth_clss))
			{

				pstmt = conn.prepareStatement(sqlUpFeeCancel);

				pstmt.setString(1, jumin_no);
				pstmt.setString(2, auth_no);
				pstmt.setString(3, card_no);


			} else {
				
				pstmt = conn.prepareStatement(sqlInFee);

				pstmt.setString(1, jumin_no);
				pstmt.setString(2, auth_no);
				pstmt.setString(3, card_no);
				pstmt.setString(4, auth_amt);
				pstmt.setString(5, auth_clss);
				pstmt.setString(6, mb_no);

			}

			cnt = pstmt.executeUpdate();            

		}catch(Exception e){
			info("GolfLoungTMProc|insertGolfFee Exceptioin : ", e);
			throw e;
		}finally{
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}
		return cnt;
	}


	/**************************************************************
	* 6. 메일발송 처리 (메일,성명,회원등급)
	**************************************************************/
	public void sendMail(String email , String hg_nm, String memGrade, String path ) throws Exception, Throwable{

			/*메일발송*/
			String emailAdmin = "\"골프라운지\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
			String imgPath = "<img src=\"";   //"<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
			String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
			
			EmailSend sender = new EmailSend();
			EmailEntity emailEtt = new EmailEntity("EUC_KR");
			
			//debug("====================GolfMemInsActn === TM 유료회원 가입 === ");
			String emailTitle = "";
			String emailFileNm = "";
			 
			//if ("movie".equals(path)){
				emailTitle = "[Golf Loun.G]골프라운지 TM 회원가입 영화예매권";
				emailFileNm = "/eamil_tm_movie.html"; //http://www.golfloung.com/app/golfloung/html/email/eamil_tm_movie.html (/BCWEB/WAS/bcext/golfloung/html/email/eamil_tm_movie.html)
			//} else if ("oil".equals(path)) {
			//	emailTitle = "[Golf Loun.G]골프라운지 TM 회원가입 SK주유권";
			//	emailFileNm = "/eamil_tm_oill.html"; //http://www.golfloung.com/app/golfloung/html/email/eamil_tm_oill.html (/BCWEB/WAS/bcext/golfloung/html/email/eamil_tm_oil.html)
			//}
			info(emailTitle);
						
			emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, hg_nm+"|"+memGrade);
			
			emailEtt.setFrom(emailAdmin);
			emailEtt.setSubject(emailTitle); 
			emailEtt.setTo(email);
			sender.send(emailEtt);

	}

	

	/**************************************************************
	* 8. 골프라운지 무료회원 -> 유료회원 변경 처리
	**************************************************************/
	public String updateGolfcdhd(WaContext context, String jumin_no, String join_chnl ) throws Exception, Throwable{
		Connection          conn = null;
		PreparedStatement  pstmt = null;
		PreparedStatement  pstmt2 = null;
		ResultSet           rs = null;	

		String ret		= "01"; 

		try{
			conn = context.getDbConnection("default", null);
			
			//데이타 있는지 조회 (우선순위 : 정상데이타, 유료회원종료일이  최근인 데이타)
			StringBuffer sql = new StringBuffer();
			
	        sql.append(" SELECT CDHD_ID,NVL(SECE_YN,'N')  SECE_YN ,MEMBER_CLSS,CDHD_CTGO_SEQ_NO, ACRG_CDHD_JONN_DATE, NVL(ACRG_CDHD_END_DATE,'20090701') ACRG_CDHD_END_DATE \n");
			sql.append("   FROM BCDBA.TBGGOLFCDHD    \n");
			sql.append("  WHERE JUMIN_NO = ?  \n"); 
			sql.append(" ORDER BY SECE_YN DESC , ACRG_CDHD_END_DATE  \n");
			
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, jumin_no);

			rs = pstmt.executeQuery(); 
			
			String cdhd_id = "";
			String sece_yn = "";
			String member_clss = "";
			String cdhd_ctgo_seq_no = "";
			
			int cnt = 0;

			while (rs.next())	{		
				cdhd_id	= rs.getString("CDHD_ID");
				sece_yn	= rs.getString("SECE_YN");
				member_clss	= rs.getString("MEMBER_CLSS"); // 1: 개인 , 2:법인
				cdhd_ctgo_seq_no = rs.getString("CDHD_CTGO_SEQ_NO"); //등급
				cnt ++;
				ret = "00" + cdhd_id; //기존에 회원데이타가 있음.
			}
			
			if(pstmt != null) try{ pstmt.close(); }catch(Exception e){}
			
			
			
			//있다면 UPDATE (유효기간,대표등급,해지여부,해지일자)
			if (cnt > 0) {
				//7:골드(우량)25,000  6:블루(골드)50,000 5:챔피온(VIP) 200,000 10:블랙 120,000
				String grd = "";
				
				if ("0003".equals(join_chnl)) grd = "7"; 
				else if ("0002".equals(join_chnl)) grd = "6";
				else if ("0004".equals(join_chnl)) grd = "5";
				else if ("0005".equals(join_chnl)) grd = "10";
				else if ("0103".equals(join_chnl)) grd = "7"; //카젠골드

				// 유료회원 갱신 처리
				StringBuffer sqlcdhd  = new StringBuffer();
				sqlcdhd.append(" UPDATE BCDBA.TBGGOLFCDHD  			\n");    
				sqlcdhd.append("    SET JOIN_CHNL = ? ,  	 \n");
				sqlcdhd.append("        ACRG_CDHD_JONN_DATE = TO_CHAR(SYSDATE,'yyyyMMdd') , 	 \n");
				sqlcdhd.append("        ACRG_CDHD_END_DATE = TO_CHAR(ADD_MONTHS(SYSDATE,12),'yyyyMMdd') ,  	 \n");
				sqlcdhd.append("        SECE_YN = NULL ,  	 \n");
				sqlcdhd.append("        SECE_ATON = NULL ,  	 \n");				
				sqlcdhd.append("        CDHD_CTGO_SEQ_NO = ?  	 \n");
				sqlcdhd.append("  WHERE CDHD_ID IN ( ? )	\n");
			
				pstmt = conn.prepareStatement(sqlcdhd.toString());

				pstmt.setString(1, join_chnl);
				pstmt.setString(2, grd);
				pstmt.setString(3, cdhd_id);

				int upcnt = pstmt.executeUpdate(); 
				
				if(pstmt != null) try{ pstmt.close(); }catch(Exception e){}


				//등급변경 ()
		 		StringBuffer sqlhistory = new StringBuffer();
		 		sqlhistory.append("	\n");
		 		sqlhistory.append("\t  INSERT INTO BCDBA.TBGCDHDGRDCHNGHST	\n");
		 		sqlhistory.append("\t  SELECT (SELECT MAX(NVL(SEQ_NO,0))+1 FROM BCDBA.TBGCDHDGRDCHNGHST)	\n");
		 		sqlhistory.append("\t  , GRD.CDHD_GRD_SEQ_NO, GRD.CDHD_ID, GRD.CDHD_CTGO_SEQ_NO, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
		 		sqlhistory.append("\t  , B.ACRG_CDHD_JONN_DATE , B.ACRG_CDHD_END_DATE , B.JOIN_CHNL	");
		 		sqlhistory.append("\t  FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD , BCDBA.TBGGOLFCDHD B	\n");
		 		sqlhistory.append("\t  WHERE  GRD.CDHD_ID = B.CDHD_ID AND GRD.CDHD_ID=? AND GRD.CDHD_CTGO_SEQ_NO= ?	\n");
		 		
		 		

				//등급변경
				pstmt = conn.prepareStatement(sqlhistory.toString());

				pstmt.setString(1, cdhd_id);
				pstmt.setString(2, cdhd_ctgo_seq_no);
				
				int upcnt3 = pstmt.executeUpdate(); 
				
				if(pstmt != null) try{ pstmt.close(); }catch(Exception e){}
				
				//등급변경 ()
				StringBuffer sqlgrd = new StringBuffer();
				sqlgrd.append(" UPDATE BCDBA.TBGGOLFCDHDGRDMGMT \n");
				sqlgrd.append("    SET CDHD_CTGO_SEQ_NO = ?  , 	 \n");
				sqlgrd.append("        CHNG_ATON = TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),   	 \n");	
				sqlgrd.append("        CHNG_RSON_CTNT = 'TM등급변경'   	 \n");				 	
				sqlgrd.append("  WHERE CDHD_ID = ? 	");
				sqlgrd.append("  AND   CDHD_CTGO_SEQ_NO = ? 	");

				//등급변경
				pstmt = conn.prepareStatement(sqlgrd.toString());

				pstmt.setString(1, grd );
				pstmt.setString(2, cdhd_id);
				pstmt.setString(3, cdhd_ctgo_seq_no);
				
				int upcnt2 = pstmt.executeUpdate(); 


				
			}
			


		}catch(Exception e){
			info("GolfLoungTMProc|insertGolfcdhd Exceptioin : ", e);
			throw e;
		}finally{
			if(rs != null) try{ rs.close(); }catch(Exception e){}
			if(pstmt != null) try{ pstmt.close(); }catch(Exception e){}
			if(pstmt2 != null) try{ pstmt2.close(); }catch(Exception e){}
			if(conn != null)  try{ conn.close();  }catch(Exception e){}			
		}
		return ret;
	}

	/**************************************************************
	* 9. 관리자에게 결과 메일 발송 (총건수, 성공건수)
	**************************************************************/
	public void sendMailAdmin(WaContext context,int tot, int sucess ) throws Exception, Throwable{


		Connection         conn = null;
		PreparedStatement pstmt = null;
		ResultSet            rs = null;	
		PreparedStatement pstmt2 = null;
		ResultSet            rs2 = null;	

		try{
			int fail = 0;
			
			conn = context.getDbConnection("default", null);
			
			//성공횟수 체크 
			pstmt2 = conn.prepareStatement(sqlTmCnt);

			rs2 = pstmt2.executeQuery(); 
			if (rs2.next())	{
				fail = rs2.getInt("AA") ; //실패횟수
				tot = rs2.getInt("BB") ; //전체횟수
				sucess = rs2.getInt("CC") ; //성공횟수
			}
			
			//실패내역조회	
			pstmt = conn.prepareStatement(SqlResult);

			rs = pstmt.executeQuery(); 
			
			
			
			info("[골프라운지 TM 처리결과 관리자 메일발송]");

			StringBuffer resultDesc_desc = new StringBuffer();
			StringBuffer resultDesc_html_desc = new StringBuffer();
			StringBuffer resultDesc = new StringBuffer();
			StringBuffer resultDesc_html = new StringBuffer(); 

			int i =0;
			String resultString = "";
			String tm_Date =DateUtil.currdate("yyyyMMdd") ;
			//resultString = 노현철_73년생_포인트잔액부족_20090717   ; HG_NM, JUMIN_NO, TB_RSLT_CLSS, REJ_RSON ,SQ1_TCALL_DATE
			
			while (rs.next())	{	
				i++;
				resultString = i + ") "+ rs.getString("HG_NM") ;
				resultString +="_"+  rs.getString("JUMIN_NO") ;
				resultString +="_"+  rs.getString("REJ_RSON") ;
				
				tm_Date=StrUtil.isNull(rs.getString("SQ1_TCALL_DATE"),"99999999") ;
				resultString +="_"+  tm_Date ;
				resultString +="_"+  StrUtil.isNull(rs.getString("GOLF_CDHD_GRD_CLSS"),"") ;
				
				resultString +="_"+  StrUtil.isNull(rs.getString("JOIN_CHNL"),"") ;
				resultString +="_"+  StrUtil.isNull(rs.getString("STTL_FAIL_RSON_CTNT"),"") ;
				
				resultDesc_desc.append(resultString+ "\n");
				resultDesc_html_desc.append(resultString+ "<br>");
			}

			resultDesc.append(DateUtil.format(tm_Date,"yyyyMMdd","yyyy-MM-dd") + " TM 건에 대한 결과는,  총 "+tot+" 건 중 성공 : "+ sucess+" 건, 실패 : "+fail+"건 \n\n");
			if (i > 0 )	resultDesc.append(resultDesc_desc.toString());

			resultDesc_html.append(DateUtil.format(tm_Date,"yyyyMMdd","yyyy-MM-dd") + " TM 건에 대한 결과는, 총 "+tot+" 건 중 성공 : "+ sucess+" 건, 실패 : "+fail+"건 <br><br>");
			if (i > 0 ) resultDesc_html.append(resultDesc_html_desc.toString());

			debug(resultDesc.toString());

			String serverip = "";  // 서버아이피
			String devip = "";	   // 개발기 ip 정보

			try {
				serverip = InetAddress.getLocalHost().getHostAddress();
			} catch(Throwable t) {}

			try {
				devip = AppConfig.getAppProperty("DV_WAS_1ST");
			} catch(Throwable t) {}
			
			String emailTitle = "" ;
			String emailAdmin = "" ;
			String toMail =""; 
			
			info("[골프라운지 TM 서버IP="  + serverip );	
			info("[골프라운지 TM 개발IP="  + devip );

			/*메일발송*/
			if (devip.equals(serverip)) {  //개발기
				//emailTitle = "[개발기 테스트 서버 - 골프라운지 TM 결과 내역 통보] 처리완료일시 : " + DateUtil.currdate("yyyy.MM.dd:HH.mm") ;
				emailTitle = "[Developer Test Server golfloung TM Result] " + DateUtil.currdate("yyyy.MM.dd:HH.mm") ;
				emailAdmin = "\"DEV골프라운지\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
				resultDesc.append("\n추신>현재 이 메일은 개발기에서 테스트용으로 보내는 것 이므로 삭제하여 주십시요.");
				resultDesc_html.append("<br>현재 이 메일은 개발기에서 테스트용으로 보내는 것이므로 삭제하여 주십시요.");
				
				//toMail="yskkang@bccard.com;mongina@bccard.com";
				toMail="yskkang@bccard.com";
				
			} else {	// 운영기
				//emailTitle = "[골프라운지 TM 결과 내역 통보] 처리완료일시 : " + DateUtil.currdate("yyyy.MM.dd:HH.mm") ;
				emailTitle = "[golfloung TM Result] complete Date : " + DateUtil.currdate("yyyy.MM.dd:HH.mm") ;
				emailAdmin = "\"골프라운지\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";

				toMail="yskkang@bccard.com;mongina@bccard.com;steve2@bccard.com";
			} 

			info("[골프라운지 TM emailTitle="  + emailTitle );	
			info("[골프라운지 TM emailAdmin="  + emailAdmin );
			info("[골프라운지 TM toMail="  + toMail );

			 
			EmailSend sender = new EmailSend();
			EmailEntity emailEtt = new EmailEntity("EUC_KR");
			//EmailEntity emailEtt = new EmailEntity("UTF-8");	
			//US-ASCII,UTF-8,ks_c_5601-7987,iso-8859-1
			
			sender.setHost("211.181.255.38"); //메일 host변경 (기존 211.181.255.109)
			//sender.setHost("211.181.254.66"); //메일 host변경 (기존 211.181.255.109)
			
			emailEtt.setFrom(emailAdmin);
			emailEtt.setSubject(emailTitle);
			emailEtt.setContents(resultDesc.toString(),resultDesc_html.toString());
			emailEtt.setTo(toMail);
			sender.send(emailEtt);


		}catch(Exception e){
			info("GolfLoungTMProc|getIsFeeFree Exceptioin : ", e);
			throw e;

		}finally{
			try { if(rs2 != null) {rs2.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt2 != null) {pstmt2.close();} else{} } catch (Exception ignored) {}
			if(rs != null)    try{ rs.close();    }catch(Exception e){}
			if(pstmt != null) try{ pstmt.close(); }catch(Exception e){}
			if(conn != null)  try{ conn.close();  }catch(Exception e){}
		}

		return ;


	}

	/*****************************************************************
	* 메일재발송
	******************************************************************/
	public Vector getMailSend (WaContext context, String recp_date) throws Exception{

		Connection         conn = null;
		PreparedStatement pstmt = null;
		ResultSet            rs = null;	
		Vector           result = new Vector();

		try{
			conn = context.getDbConnection("default", null);

			pstmt = conn.prepareStatement(sqlMailSend);			
 
			pstmt.setString(1, recp_date);

			rs = pstmt.executeQuery();            
 
			while (rs.next())	{
				Hashtable data = new Hashtable();
				
				data.put("JUMIN_NO",rs.getString("JUMIN_NO").trim());
				data.put("GOLF_CDHD_GRD_CLSS",rs.getString("GOLF_CDHD_GRD_CLSS").trim());
				data.put("MB_CDHD_NO",rs.getString("MB_CDHD_NO").trim());
				data.put("HG_NM",rs.getString("HG_NM").trim());
				data.put("EMAIL_ID",StrUtil.isNull(rs.getString("EMAIL_ID"),""));
				data.put("AUTH_CLSS",StrUtil.isNull(rs.getString("AUTH_CLSS"),""));
				data.put("CARD_NO",StrUtil.isNull(rs.getString("CARD_NO"),""));
				data.put("JOIN_CHNL",StrUtil.isNull(rs.getString("JOIN_CHNL"),""));
				data.put("HP_DDD_NO",StrUtil.isNull(rs.getString("HP_DDD_NO"),""));
				data.put("HP_TEL_HNO",StrUtil.isNull(rs.getString("HP_TEL_HNO"),""));
				data.put("HP_TEL_SNO",StrUtil.isNull(rs.getString("HP_TEL_SNO"),""));
				data.put("RECP_DATE",StrUtil.isNull(rs.getString("RECP_DATE"),""));
				data.put("VALD_LIM",StrUtil.isNull(rs.getString("VALD_LIM"),""));
				data.put("DISC_CLSS",StrUtil.isNull(rs.getString("DISC_CLSS"),""));
				data.put("DC_AMT",StrUtil.isNull(rs.getString("DC_AMT"),""));
				data.put("RECP_DATE",StrUtil.isNull(rs.getString("RECP_DATE"),""));
 
				result.add(data);
			}				

		}catch(Exception e){
			info("GolfLoungTMProc|getMailSend Exceptioin : ", e);
			throw e;

		}finally{
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}

		return result;
	}
 


	

}
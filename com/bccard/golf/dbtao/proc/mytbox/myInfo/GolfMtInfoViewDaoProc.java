/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkPreGrViewDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 부킹 골프장 보기 처리
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*  20091006			  진현구   월 단위 기준으로 되어 있는 것을 일단위(30일 기준)으로 프로그램을 수정해 줄 것으로 요청합니다. (by 김승환&강선영) 149 Line
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.mytbox.myInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.info.GolfPointInfoResetJtProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.login.TopPointInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import javax.servlet.http.HttpSession;
import com.bccard.golf.common.GolfUserEtt; 

/******************************************************************************
 * Golf
 * @author	미디어포스
 * @version	1.0
 ******************************************************************************/
public class GolfMtInfoViewDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfBkPreGrViewDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfMtInfoViewDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data, HttpServletRequest request) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			// 01.세션정보체크
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			String memID = userEtt.getAccount();
			String memClss = userEtt.getMemberClss();
			
			//HttpSession session = request.getSession(false); 
			//GolfUserEtt ett   = (GolfUserEtt)session.getAttribute("GOLF_ENTITY"); //- 기본정보
			//String memClss = "5";

			conn = context.getDbConnection("default", null);
			
			int topPoint = 0;
			String golfPointComma = "0";
			String sql = ""; 


//			if("5".equals(memClss)) {	// 법인
//				topPoint = 0;
//				golfPointComma = "0";
//
//				//조회 ----------------------------------------------------------			
//				sql = this.getSelect5Query();   
//			} else 
				
			if("1".equals(memClss)) {
				
				try {
					GolfPointInfoResetJtProc resetProc = (GolfPointInfoResetJtProc)context.getProc("GolfPointInfoResetJtProc");
					TopPointInfoEtt pointInfo = resetProc.getTopPointInfoEtt(context, request , userEtt.getSocid());
		
					topPoint = pointInfo.getTopPoint().getPoint();
					golfPointComma = GolfUtil.comma(topPoint+"");
				} catch (Throwable t){
					golfPointComma = "0";
				}
				
				//조회 ----------------------------------------------------------			
				sql = this.getSelectQuery();
			} else {
				topPoint = 0;
				golfPointComma = GolfUtil.comma(topPoint+"");
				sql = this.getSelectQuery();
			}

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, memID);
			rs = pstmt.executeQuery();
			
			String ctgo_seq = "";		// 회원 등급 번호
			String join_chnl = "";		// 가입 경로
			String upd_pay = "";		// 업그레이드 금액 할인 여부 half:반액 all:전액 
						
			if(rs != null) {
				while(rs.next())  {

					result.addString("CDHD_ID" 				,rs.getString("CDHD_ID") );
					result.addString("NAME" 				,rs.getString("HG_NM") );
					result.addString("BIRTH" 				,rs.getString("BIRTH") );
					result.addString("PHONE" 				,rs.getString("PHONE") );
					result.addString("MOBILE" 				,rs.getString("MOBILE") );
					result.addString("ADDR" 				,rs.getString("ADDR") );
					result.addString("GOLF_CMMN_CODE_NM" 	,rs.getString("GOLF_CMMN_CODE_NM") );
					result.addString("TOT_AMT" 				,GolfUtil.comma(rs.getString("TOT_AMT")) );
					result.addString("EMAIL_RECP_YN" 		,rs.getString("EMAIL_RECP_YN") );
					result.addString("SMS_RECP_YN" 			,rs.getString("SMS_RECP_YN") );
					result.addInt("CDHD_SQ2_CTGO" 			,rs.getInt("CDHD_SQ2_CTGO") );
					result.addString("golfPointComma" 		,golfPointComma );				
					result.addString("JOIN_DATE" 			,rs.getString("JOIN_DATE") );				
					result.addString("ACRG_DATE" 			,rs.getString("ACRG_DATE") );					
					result.addString("EXTEND_YN" 			,rs.getString("EXTEND_YN") );	
					
					if(GolfUtil.empty(rs.getString("APLC_SEQ_NO"))){
						result.addString("payWay" 			,"yr" );	
					}else{
						result.addString("payWay" 			,"mn" );
					}
					
					// 20100309 투어멤버스 블랙 회원은 한달이내 가입해도 전액 결제 되도록 한다.
					if(!GolfUtil.empty(rs.getString("CTGO_SEQ"))){
						ctgo_seq = rs.getString("CTGO_SEQ");
					}else{
						ctgo_seq = "";
					}
					
					if(!GolfUtil.empty(rs.getString("JOIN_CHNL"))){
						join_chnl = rs.getString("JOIN_CHNL");
					}else{
						join_chnl = "";
					}
					
					if(!GolfUtil.empty(rs.getString("UPD_PAY"))){
						upd_pay = rs.getString("UPD_PAY");
					}else{
						upd_pay = "";
					}

					if(ctgo_seq.equals("11") && join_chnl.equals("3000")){
						upd_pay = "all";
					}

					result.addString("CTGO_SEQ" 			,ctgo_seq );				
					result.addString("JOIN_CHNL" 			,join_chnl );
					result.addString("UPD_PAY" 				,upd_pay );						
					result.addString("ANL_FEE" 				,rs.getString("ANL_FEE") );					
					result.addString("MO_MSHP_FEE" 			,rs.getString("MO_MSHP_FEE") );
					//debug("UPD_PAY : " + upd_pay);
					
					result.addString("RESULT", "00"); //정상결과
				}
			}
			
			sql = this.getSelectQuery();

			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}

		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}

		return result;
	}	
	
	public DbTaoResult execute_card(WaContext context, TaoDataSet data, HttpServletRequest request) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			// 01.세션정보체크
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			String memID = userEtt.getAccount();
			
			conn = context.getDbConnection("default", null);
			String sql = this.getSelectCardQuery();

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, memID);
			pstmt.setString(++idx, memID);
			pstmt.setString(++idx, memID);
			rs = pstmt.executeQuery();
						
			if(rs != null) {
				while(rs.next())  {

					result.addString("CARD_SEQ" 			,rs.getString("CARD_SEQ"));
					result.addString("CARD_NM" 				,rs.getString("CARD_NM"));
					result.addString("CARD_ST" 				,rs.getString("CARD_ST"));
					result.addString("CARD_ED" 				,rs.getString("CARD_ED"));
					
					result.addString("RESULT", "00"); //정상결과
				}
			}
			
			sql = this.getSelectQuery();

			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}

		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}

		return result;
	}	
	
	public DbTaoResult execute_list(WaContext context, TaoDataSet data, HttpServletRequest request) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			// 01.세션정보체크
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			String memID = userEtt.getAccount();
			int intMemGrade = userEtt.getIntMemGrade();
			debug("execute_list11111 :: intMemGrade : " + intMemGrade);
			String grd_seq = "";
			String payWay = data.getString("payWay");
			
			debug("payWay :: payWay : " + payWay);
			conn = context.getDbConnection("default", null);
			// intMemGrade = echamp회원은 champ로만 업데이트 가능
			String sql = this.getSelectListQuery(intMemGrade, payWay);

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, memID);
			rs = pstmt.executeQuery();
						
			if(rs != null) {
				while(rs.next())  {

					result.addString("GRD_SEQ" 			,rs.getString("GRD_SEQ"));
					result.addString("GRD_NM" 			,rs.getString("GRD_NM"));
					result.addString("ANL_FEE" 			,rs.getString("ANL_FEE"));
					result.addString("MO_MSHP_FEE" 		,rs.getString("MO_MSHP_FEE"));
					result.addString("GRD_SQ2" 			,rs.getString("GRD_SQ2"));
					result.addString("VIEW_YN" 			,rs.getString("VIEW_YN"));
					

					grd_seq = rs.getString("GRD_SEQ");
			debug("grd_seq :: grd_seq : " + grd_seq);
					if("5".equals(grd_seq)){
						result.addString("GRD_IMG" 			,"/golf/img/popup/btn_vip_upgrade.gif");
					}else if("6".equals(grd_seq)){
						result.addString("GRD_IMG" 			,"/golf/img/popup/btn_gold_upgrade.gif");
					}else if("7".equals(grd_seq)){
						result.addString("GRD_IMG" 			,"/golf/img/common/btn/btn_upgrade_g.jpg");
					}else if("11".equals(grd_seq)){
						result.addString("GRD_IMG" 			,"/golf/img/common/btn/btn_black_upgrade.gif");
					}else if("17".equals(grd_seq)){
						result.addString("GRD_IMG" 			,"/golf/img/popup/btn_echampion_upgrade.gif");
					}
					
					result.addString("RESULT", "00"); //정상결과
				}
			}
			
			sql = this.getSelectQuery();

			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}

		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}

		return result;
	}	
	
	

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */ 
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t  SELECT T_CDHD.CDHD_ID, T_CDHD.HG_NM, T_CDHD.PHONE, T_CDHD.MOBILE , T_CDHD.EMAIL_RECP_YN, T_CDHD.SMS_RECP_YN	\n");
		sql.append("\t  , TO_CHAR(TO_DATE((CASE SUBSTR(T_CDHD.JUMIN_NO,7,1) WHEN '1' THEN '19' WHEN'2' THEN '19' ELSE '20' END)||SUBSTR(T_CDHD.JUMIN_NO,1,6)),'YYYY\"년 \"MM\"월 \"DD\"일\"') AS BIRTH	\n");
		sql.append("\t  , (T_CDHD.ZIPADDR||' '||T_CDHD.DETAILADDR) AS ADDR	\n");
		sql.append("\t  , NVL((T_CDHD.CBMO_ACM_TOT_AMT-T_CDHD.CBMO_DDUC_TOT_AMT),0) AS TOT_AMT	\n");
		sql.append("\t  , CASE T_CDHD.JONN_ATON WHEN NULL THEN '' ELSE TO_CHAR(TO_DATE(SUBSTR(T_CDHD.JONN_ATON,1,8)),'YYYY\"년 \"MM\"월 \"DD\"일\"') END AS JOIN_DATE	\n");
		sql.append("\t  , CASE T_CDHD.ACRG_CDHD_JONN_DATE WHEN NULL THEN '' ELSE TO_CHAR(TO_DATE(T_CDHD.ACRG_CDHD_JONN_DATE),'YYYY\"년 \"MM\"월 \"DD\"일\"') END AS ACRG_DATE	\n");
		sql.append("\t  , CASE WHEN to_date(T_CDHD.ACRG_CDHD_JONN_DATE,'YYYYMMDD')+30 >=SYSDATE THEN 'half' ELSE 'all' END UPD_PAY	\n");
		sql.append("\t  , T_CTGO.CDHD_SQ2_CTGO, T_CODE.GOLF_CMMN_CODE_NM, LST.APLC_SEQ_NO, T_CDHD.CDHD_CTGO_SEQ_NO CTGO_SEQ	\n");
		sql.append("\t  , CASE WHEN ADD_MONTHS(T_CDHD.ACRG_CDHD_END_DATE,-1)<=SYSDATE THEN 'Y' ELSE 'N' END EXTEND_YN	\n");
		sql.append("\t  , T_CDHD.JOIN_CHNL, T_CTGO.ANL_FEE, T_CTGO.MO_MSHP_FEE	\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD T_CDHD	\n");
		sql.append("\t	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T_CTGO ON T_CDHD.CDHD_CTGO_SEQ_NO=T_CTGO.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t  JOIN BCDBA.TBGCMMNCODE T_CODE ON T_CTGO.CDHD_SQ2_CTGO=T_CODE.GOLF_CMMN_CODE AND T_CODE.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t  LEFT JOIN BCDBA.TBGAPLCMGMT LST ON LST.CDHD_ID=T_CDHD.CDHD_ID AND LST.GOLF_SVC_APLC_CLSS='1001' AND LST.PGRS_YN='Y'	\n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T_PAY ON T_CDHD.CDHD_CTGO_SEQ_NO=T_PAY.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t  WHERE T_CDHD.CDHD_ID=?	\n");					
		return sql.toString();
    }
    
	/** ***********************************************************************
     * Query를 생성하여 리턴한다. - 법인회원 개인정보 가져오기 
     ************************************************************************ */ 
     private String getSelect5Query(){
         StringBuffer sql = new StringBuffer();

         /*
 		sql.append("\n");
		sql.append("\t SELECT T1.CDHD_ID, T2.USER_NM as NAME, T4.CDHD_SQ2_CTGO														\n");
		//sql.append("\t  , CASE T2.BIRTH WHEN NULL THEN TO_CHAR(TO_DATE((CASE SUBSTR(T2.SOCID,7,1) WHEN '1' THEN '19' WHEN'2' THEN '19' ELSE '20' END)||SUBSTR(T2.SOCID,1,6)),'YYYY\"년 \"MM\"월 \"DD\"일\"') ELSE TO_CHAR(TO_DATE(T2.BIRTH),'YYYY\"년 \"MM\"월 \"DD\"일\"') END AS BIRTH	\n");
		sql.append("\t  , TO_CHAR(TO_DATE((CASE SUBSTR(T2.USER_JUMIN_NO,7,1) WHEN '1' THEN '19' WHEN'2' THEN '19' ELSE '20' END)||SUBSTR(T2.USER_JUMIN_NO,1,6)),'YYYY\"년 \"MM\"월 \"DD\"일\"') AS BIRTH	\n");
		sql.append("\t  , T2.USER_TEL_NO as PHONE, T2.USER_MOB_NO as MOBILE																			\n");
		sql.append("\t  , ' ' AS ADDR														\n");
		sql.append("\t  , T5.GOLF_CMMN_CODE_NM																			\n");
		sql.append("\t  , NVL((T1.CBMO_ACM_TOT_AMT-T1.CBMO_DDUC_TOT_AMT),0) AS TOT_AMT									\n");
		sql.append("\t  , T1.EMAIL_RECP_YN, T1.SMS_RECP_YN																\n");
		sql.append("\t  , CASE T1.JONN_ATON WHEN NULL THEN '' ELSE TO_CHAR(TO_DATE(SUBSTR(T1.JONN_ATON,1,8)),'YYYY\"년 \"MM\"월 \"DD\"일\"') END AS JOIN_DATE	\n");
		sql.append("\t  , CASE T1.ACRG_CDHD_JONN_DATE WHEN NULL THEN '' ELSE TO_CHAR(TO_DATE(T1.ACRG_CDHD_JONN_DATE),'YYYY\"년 \"MM\"월 \"DD\"일\"') END AS ACRG_DATE	\n");
		sql.append("\t  , to_char(TO_DATE(T3.REG_ATON,'yyyymmddhh24miss'),'yyyy.mm.dd') as REG_ATON		\n");
		sql.append("\t  , to_char(TO_DATE(T3.REG_ATON,'yyyymmddhh24miss') + INTERVAL '1' YEAR,'yyyy.mm.dd') as REG_END_ATON	\n");
		//sql.append("\t  , CASE SUBSTR(T1.ACRG_CDHD_JONN_DATE,1,6) WHEN TO_CHAR(SYSDATE, 'YYYYMM') THEN 'half' ELSE 'all' END UPD_PAY	\n");
		sql.append("\t  , CASE WHEN to_date(T1.ACRG_CDHD_JONN_DATE,'YYYYMMDD')+30 >=to_date(T1.ACRG_CDHD_JONN_DATE,'YYYYMMDD') THEN 'half' ELSE 'all' END UPD_PAY	\n");	
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD T1																		\n");
		sql.append("\t  JOIN BCDBA.TBENTPUSER T2 ON T1.CDHD_ID=T2.ACCOUNT												\n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDGRDMGMT T3 ON T3.CDHD_ID=T1.CDHD_ID										\n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T4 ON T4.CDHD_CTGO_SEQ_NO=T3.CDHD_CTGO_SEQ_NO					\n");
		sql.append("\t  JOIN BCDBA.TBGCMMNCODE T5 ON T5.GOLF_CMMN_CODE=T4.CDHD_SQ2_CTGO AND T5.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t  WHERE T1.CDHD_ID=?																				\n");
		sql.append("\t  ORDER BY T3.REG_ATON DESC		\n");
 		*/

 		sql.append("\n");
		sql.append("\t SELECT T1.CDHD_ID, B2.USER_NM as NAME, T4.CDHD_SQ2_CTGO														\n");
		//sql.append("\t  , CASE NVL(T2.BIRTH, '-') WHEN '-' THEN TO_CHAR(TO_DATE((CASE SUBSTR(B2.USER_JUMIN_NO,7,1) WHEN '1' THEN '19' WHEN'2' THEN '19' ELSE '20' END)||SUBSTR(B2.USER_JUMIN_NO,1,6)),'YYYY\"년 \"MM\"월 \"DD\"일\"') ELSE TO_CHAR(TO_DATE(T2.BIRTH),'YYYY\"년 \"MM\"월 \"DD\"일\"') END AS BIRTH	\n");
		//sql.append("\t  , TO_CHAR(TO_DATE((CASE SUBSTR(T2.SOCID,7,1) WHEN '1' THEN '19' WHEN'2' THEN '19' ELSE '20' END)||SUBSTR(T2.SOCID,1,6)),'YYYY\"년 \"MM\"월 \"DD\"일\"') AS BIRTH	\n");
		sql.append("\t  , TO_CHAR(TO_DATE((CASE SUBSTR(B2.USER_JUMIN_NO,7,1) WHEN '1' THEN '19' WHEN'2' THEN '19' ELSE '20' END)||SUBSTR(B2.USER_JUMIN_NO,1,6)),'YYYY\"년 \"MM\"월 \"DD\"일\"') AS BIRTH	\n");
		sql.append("\t  , B2.USER_TEL_NO as PHONE, B2.USER_MOB_NO as MOBILE																			\n");
		sql.append("\t  , (T2.ZIPADDR||' '||T2.DETAILADDR) AS ADDR														\n");
		sql.append("\t  , T5.GOLF_CMMN_CODE_NM																			\n");
		sql.append("\t  , NVL((T1.CBMO_ACM_TOT_AMT-T1.CBMO_DDUC_TOT_AMT),0) AS TOT_AMT									\n");
		sql.append("\t  , T1.EMAIL_RECP_YN, T1.SMS_RECP_YN																\n");
		sql.append("\t  , CASE T1.JONN_ATON WHEN NULL THEN '' ELSE TO_CHAR(TO_DATE(SUBSTR(T1.JONN_ATON,1,8)),'YYYY\"년 \"MM\"월 \"DD\"일\"') END AS JOIN_DATE	\n");
		sql.append("\t  , CASE T1.ACRG_CDHD_JONN_DATE WHEN NULL THEN '' ELSE TO_CHAR(TO_DATE(T1.ACRG_CDHD_JONN_DATE),'YYYY\"년 \"MM\"월 \"DD\"일\"') END AS ACRG_DATE	\n");
		sql.append("\t  , to_char(TO_DATE(T3.REG_ATON,'yyyymmddhh24miss'),'yyyy.mm.dd') as REG_ATON		\n");
		sql.append("\t  , to_char(TO_DATE(T3.REG_ATON,'yyyymmddhh24miss') + INTERVAL '1' YEAR,'yyyy.mm.dd') as REG_END_ATON	\n");
		//sql.append("\t  , CASE SUBSTR(T1.ACRG_CDHD_JONN_DATE,1,6) WHEN TO_CHAR(SYSDATE, 'YYYYMM') THEN 'half' ELSE 'all' END UPD_PAY	\n");
		//sql.append("\t  , CASE WHEN to_date(T1.ACRG_CDHD_JONN_DATE,'YYYYMMDD')+30 >=to_date(T1.ACRG_CDHD_JONN_DATE,'YYYYMMDD') THEN 'half' ELSE 'all' END UPD_PAY	\n");	
		sql.append("\t  , CASE WHEN to_date(T1.ACRG_CDHD_JONN_DATE,'YYYYMMDD')+30 >=SYSDATE THEN 'half' ELSE 'all' END UPD_PAY	\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD T1																		\n");
		sql.append("\t  JOIN BCDBA.UCUSRINFO T2 ON T1.CDHD_ID=T2.ACCOUNT												\n");
		sql.append("\t  JOIN BCDBA.TBENTPUSER B2 ON T1.CDHD_ID=B2.ACCOUNT												\n");
		sql.append("\t  JOIN BCDBA.TBENTPMEM  B3 ON B3.mem_id=B2.mem_id													\n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDGRDMGMT T3 ON T3.CDHD_ID=T1.CDHD_ID										\n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T4 ON T4.CDHD_CTGO_SEQ_NO=T3.CDHD_CTGO_SEQ_NO					\n");
		sql.append("\t  JOIN BCDBA.TBGCMMNCODE T5 ON T5.GOLF_CMMN_CODE=T4.CDHD_SQ2_CTGO AND T5.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t  WHERE T1.CDHD_ID=?																				\n");
		sql.append("\t  and B3.mem_stat='2' and B3.mem_CLSS='6' and B3.sec_Date is null									\n");
		sql.append("\t  ORDER BY T3.REG_ATON DESC		\n");
 		return sql.toString();
     }
     
 	/** ***********************************************************************
      * 카드리스트 쿼리    
      ************************************************************************ */ 
      private String getSelectCardQuery(){
        StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
  		sql.append("\t  SELECT GRD.CDHD_CTGO_SEQ_NO CARD_SEQ, CODE.GOLF_CMMN_CODE_NM CARD_NM	\n");	
  		sql.append("\t  ,DECODE( GRD.CDHD_CTGO_SEQ_NO ,											\n");
  		sql.append("\t  		 (SELECT  SUBSTR(A.GOLF_CMMN_CODE,3)							\n");
  		sql.append("\t  		  FROM BCDBA.TBGCMMNCODE  A										\n");
  		sql.append("\t  		  WHERE A.GOLF_CMMN_CLSS='0064'									\n");
  		sql.append("\t  		  AND A.GOLF_CMMN_CODE = GRD.CDHD_CTGO_SEQ_NO					\n");
  		sql.append("\t  		  AND A.GOLF_CMMN_CODE = '0028'									\n");
  		sql.append("\t  		  ),  (SELECT   TO_CHAR(TO_DATE(CAMP_STRT_DATE,'YYYYMMDDHH24MISS'), 'YYYY.MM.DD')	\n");
  		sql.append("\t  			   FROM BCDBA.TBACRGCDHDLODNTBL 							\n");
  		sql.append("\t   			   WHERE MEMO_EXPL = '0028'									\n");
  		sql.append("\t  			   AND PROC_RSLT_CTNT = ? 	) 							    \n");
  		sql.append("\t  		  , TO_CHAR(TO_DATE(GRD.REG_ATON,'YYYYMMDDHH24MISS'), 'YYYY.MM.DD') 	\n");
  		sql.append("\t  )CARD_ST  	\n");
  		sql.append("\t   , DECODE ( GRD.CDHD_CTGO_SEQ_NO ,								\n");  		
  		sql.append("\t   				(SELECT  SUBSTR(A.GOLF_CMMN_CODE,3) 			\n");
  		sql.append("\t   				 FROM BCDBA.TBGCMMNCODE  A						\n");
  		sql.append("\t   			 	 WHERE A.GOLF_CMMN_CLSS='0064' 					\n");
  		sql.append("\t   				 AND A.GOLF_CMMN_CODE = GRD.CDHD_CTGO_SEQ_NO	\n");
  		sql.append("\t   				 AND A.GOLF_CMMN_CODE NOT IN ('0025', '0027', '0028')),	\n");
  		sql.append("\t   			TO_CHAR(TO_DATE(GRD.REG_ATON,'YYYYMMDDHH24MISS')+INTERVAL '3' MONTH, 'YYYY.MM.DD'),	\n");//스마트(기업 오퍼) 월회비 종료일
  		sql.append("\t   				(SELECT  SUBSTR(A.GOLF_CMMN_CODE,3) 			\n");
  		sql.append("\t   				 FROM BCDBA.TBGCMMNCODE  A						\n");
  		sql.append("\t   			 	 WHERE A.GOLF_CMMN_CLSS='0064' 					\n");
  		sql.append("\t   				 AND A.GOLF_CMMN_CODE = GRD.CDHD_CTGO_SEQ_NO	\n");
  		sql.append("\t   				 AND A.GOLF_CMMN_CODE = '0027' ),				\n");
  		sql.append("\t   			'',													\n"); //스마트(회원이 직접 가입) 월회비 종료일(해지할때가지 이므로 종료일 없음)
  		
  		sql.append("\t   				(SELECT  SUBSTR(A.GOLF_CMMN_CODE,3)				\n");
  		sql.append("\t   				FROM BCDBA.TBGCMMNCODE  A						\n");
  		sql.append("\t   				WHERE A.GOLF_CMMN_CLSS='0064'					\n");
  		sql.append("\t   				AND A.GOLF_CMMN_CODE = GRD.CDHD_CTGO_SEQ_NO		\n");
  		sql.append("\t   				AND A.GOLF_CMMN_CODE = '0028' ),				\n");
  		sql.append("\t   				(SELECT   TO_CHAR(TO_DATE(CAMP_END_DATE,'YYYYMMDDHH24MISS'), 'YYYY.MM.DD')		\n");
  		sql.append("\t   				FROM BCDBA.TBACRGCDHDLODNTBL 					\n");
  		sql.append("\t   				WHERE MEMO_EXPL = '0028'						\n");
  		sql.append("\t   				AND PROC_RSLT_CTNT = ? ),						\n");
  		
  		sql.append("\t   			TO_CHAR(TO_DATE(GRD.REG_ATON,'YYYYMMDDHH24MISS')+INTERVAL '1' YEAR, 'YYYY.MM.DD')	\n");//그외 카드는 종료일 +12M
  		sql.append("\t   )CARD_ED																						\n");  		
  		
  		sql.append("\t  FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD	\n");
  		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT GTGO ON GRD.CDHD_CTGO_SEQ_NO=GTGO.CDHD_CTGO_SEQ_NO	\n");
  		sql.append("\t  JOIN BCDBA.TBGCMMNCODE CODE ON CODE.GOLF_CMMN_CODE=GTGO.CDHD_SQ2_CTGO AND CODE.GOLF_CMMN_CLSS='0005'	\n");
  		sql.append("\t  WHERE GRD.CDHD_ID=? AND GTGO.CDHD_SQ1_CTGO='0001'	\n");  		
  		sql.append("\t  ORDER BY GRD.CDHD_CTGO_SEQ_NO	\n");
  		
  		return sql.toString();
      }
      
   	/** ***********************************************************************
        * 업그레이드 등급 리스트    
        ************************************************************************ */ 
        private String getSelectListQuery(int intMemGrade, String payWay){
          StringBuffer sql = new StringBuffer();

  		if(intMemGrade==12){
    		sql.append("	\n");
    		sql.append("\t  SELECT GRD_SEQ, GRD_NM, ANL_FEE, MO_MSHP_FEE, GRD_SQ2, (CASE GRD_SEQ WHEN 5 THEN 'Y' ELSE 'N' END) VIEW_YN	\n");
    		sql.append("\t  FROM (	\n");
    		sql.append("\t      SELECT CTGO.CDHD_CTGO_SEQ_NO GRD_SEQ, CODE.GOLF_CMMN_CODE_NM GRD_NM	\n");
    		sql.append("\t      , CTGO.ANL_FEE, CTGO.MO_MSHP_FEE, TO_NUMBER(CTGO.CDHD_SQ2_CTGO) GRD_SQ2	\n");
    		sql.append("\t      , (CASE WHEN CTGO.ANL_FEE>(	\n");
    		sql.append("\t        SELECT ANL_FEE	\n");
    		sql.append("\t        FROM BCDBA.TBGGOLFCDHDGRDMGMT T2_GRD	\n");
    		sql.append("\t        JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2_CTGO ON T2_GRD.CDHD_CTGO_SEQ_NO=T2_CTGO.CDHD_CTGO_SEQ_NO	\n");
    		sql.append("\t        WHERE T2_GRD.CDHD_ID=? AND T2_CTGO.CDHD_SQ1_CTGO='0002'	\n");
    		sql.append("\t      ) THEN 'Y' ELSE 'N' END) AS VIEW_YN	\n");
    		sql.append("\t      FROM BCDBA.TBGGOLFCDHDCTGOMGMT CTGO	\n");
    		sql.append("\t      JOIN BCDBA.TBGCMMNCODE CODE ON CODE.GOLF_CMMN_CODE=CTGO.CDHD_SQ2_CTGO AND CODE.GOLF_CMMN_CLSS='0005'	\n");
    		sql.append("\t      WHERE CTGO.USE_CLSS='Y'	\n");
    		sql.append("\t      ORDER BY CTGO.SORT_SEQ ASC	\n");
    		sql.append("\t  )	\n");  			
  		}else if(intMemGrade==13){		// IBK기업은행 Gold 회원만 e-champion 회원 노출
    		sql.append("	\n");
    		sql.append("\t  SELECT CTGO.CDHD_CTGO_SEQ_NO GRD_SEQ, CODE.GOLF_CMMN_CODE_NM GRD_NM	\n");
    		sql.append("\t  , CTGO.ANL_FEE, CTGO.MO_MSHP_FEE, TO_NUMBER(CTGO.CDHD_SQ2_CTGO) GRD_SQ2	\n");
    		sql.append("\t  , (CASE WHEN CTGO.ANL_FEE>(	\n");
    		sql.append("\t      SELECT ANL_FEE	\n");
    		sql.append("\t      FROM BCDBA.TBGGOLFCDHDGRDMGMT T2_GRD	\n");
    		sql.append("\t      JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2_CTGO ON T2_GRD.CDHD_CTGO_SEQ_NO=T2_CTGO.CDHD_CTGO_SEQ_NO	\n");
    		sql.append("\t      WHERE T2_GRD.CDHD_ID=? AND T2_CTGO.CDHD_SQ1_CTGO='0002'	\n");
    		sql.append("\t  ) THEN 'Y' ELSE 'N' END) AS VIEW_YN	\n");
    		sql.append("\t  FROM BCDBA.TBGGOLFCDHDCTGOMGMT CTGO	\n");
    		sql.append("\t  JOIN BCDBA.TBGCMMNCODE CODE ON CODE.GOLF_CMMN_CODE=CTGO.CDHD_SQ2_CTGO AND CODE.GOLF_CMMN_CLSS='0005'	\n");
    		sql.append("\t  WHERE CTGO.USE_CLSS='Y'	\n");
    		sql.append("\t  ORDER BY CTGO.SORT_SEQ ASC	\n");
  		}else{
    		sql.append("	\n");
    		sql.append("\t  SELECT CTGO.CDHD_CTGO_SEQ_NO GRD_SEQ, CODE.GOLF_CMMN_CODE_NM GRD_NM	\n");
    		sql.append("\t  , CTGO.ANL_FEE, CTGO.MO_MSHP_FEE, TO_NUMBER(CTGO.CDHD_SQ2_CTGO) GRD_SQ2	\n");
    		sql.append("\t  , (CASE WHEN CTGO.ANL_FEE>(	\n");
    		sql.append("\t      SELECT ANL_FEE	\n");
    		sql.append("\t      FROM BCDBA.TBGGOLFCDHDGRDMGMT T2_GRD	\n");
    		sql.append("\t      JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2_CTGO ON T2_GRD.CDHD_CTGO_SEQ_NO=T2_CTGO.CDHD_CTGO_SEQ_NO	\n");
    		sql.append("\t      WHERE T2_GRD.CDHD_ID=? AND T2_CTGO.CDHD_SQ1_CTGO='0002'	\n");
    		sql.append("\t  ) THEN 'Y' ELSE 'N' END) AS VIEW_YN	\n");
    		sql.append("\t  FROM BCDBA.TBGGOLFCDHDCTGOMGMT CTGO	\n");
    		sql.append("\t  JOIN BCDBA.TBGCMMNCODE CODE ON CODE.GOLF_CMMN_CODE=CTGO.CDHD_SQ2_CTGO AND CODE.GOLF_CMMN_CLSS='0005'	\n");
    		sql.append("\t  WHERE CTGO.USE_CLSS='Y' AND CDHD_CTGO_SEQ_NO<>17	\n");
    		if(payWay.equals("mn")){
    			sql.append("\t  AND CDHD_CTGO_SEQ_NO<>11	\n");
    		}
    		sql.append("\t  ORDER BY CTGO.SORT_SEQ ASC	\n");
  		}
  		
    		return sql.toString();
        }
      
}

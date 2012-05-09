/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntCouponOrdListProc
*   작성자    : (주)미디어포스 이경희
*   내용      : 관리자 > 이벤트 > 쇼핑 > 쿠폰구매리스트
*   적용범위  : Golf
*   작성일자  : 2011-04-13
************************** 수정이력 ****************************************************************
*    일자    작성자   변경사항
*20110425   이경희   결제취소 검색기능 추가
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.event.coupon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.BcUtil;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

public class GolfEvntCouponOrdListProc  extends AbstractProc {
	
	
	public static final String TITLE = "관리자 > 이벤트 > 쇼핑 > 쿠폰구매리스트";
	
	public DbTaoResult getList(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title		= data.getString("TITLE");
		String sch_state	= data.getString("sch_state");		//검색조건 
		String sch_text		= data.getString("sch_text");       //검색어
		String sch_date_st	= data.getString("sch_date_st");    //시작일
		String sch_date_ed	= data.getString("sch_date_ed");    //종료일
		String excelYn		= data.getString("excelYn");    	//엑셀구분

		ResultSet rs = null;
		ResultSet rs2 = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		DbTaoResult  result =  new DbTaoResult(title);
		
		try {
			conn = context.getDbConnection("default", null);						
			 
			//조회 ----------------------------------------------------------   
			String sql = "";
				
			if (excelYn.equals("Y")){
				sql = this.getSelectDetExcelQuery(sch_state,sch_date_st,sch_date_ed);
			}else {
				sql = this.getOdrList(sch_state,sch_date_st,sch_date_ed);
			}	

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			
			if (excelYn.equals("N")){
				pstmt.setLong(++idx, data.getLong("record_size"));
				pstmt.setLong(++idx, data.getLong("page_no"));
			}
			
			if( sch_state.length() > 1){
				pstmt.setString(++idx, sch_text);
			}
			
			if(!(sch_date_st.equals("") || sch_date_ed.equals(""))){
				pstmt.setString(++idx, sch_date_st.replaceAll("-",""));
				pstmt.setString(++idx, sch_date_ed.replaceAll("-",""));
			}
			
			if (excelYn.equals("N"))
				pstmt.setLong(++idx, data.getLong("page_no"));
			
			rs = pstmt.executeQuery();

			int art_num_no = 0;
			String str ="";
			String jumin_no = "";
			
			if(rs != null) {		
				
				while(rs.next())  {
					
					//쿠폰리스트 가져오기
					pstmt2 = conn.prepareStatement(getCouponList());
					pstmt2.setString(1, rs.getString("EA_INFO"));
					rs2	 = pstmt2.executeQuery();
	
					int cnt = 0;
					if(rs2 != null) {
						while(rs2.next())  {
							str += rs2.getString("CUPN_NO") + "<br>";
							cnt++;
						}
					}
					
					rs2.close();
					pstmt2.close();
					
					if (excelYn.equals("N")){
						result.addInt("ART_NUM" 		,rs.getInt("ART_NUM")-art_num_no );
					}
					result.addString("EA_INFO"		,rs.getString("EA_INFO") );
					art_num_no++;
					
					if (excelYn.equals("N"))
						result.addString("TOT_CNT"		,rs.getString("TOT_CNT") );
					
					jumin_no = rs.getString("JUMIN_NO");					
					if(!GolfUtil.empty(jumin_no)) jumin_no = jumin_no.substring(0, 6)+"-"+jumin_no.substring(6, 13);
					
					result.addString("JUMIN_NO" 	,jumin_no );
					result.addString("GUBUN"		,rs.getString("GUBUN") );
					result.addString("GRD_NM"		,rs.getString("GRD_NM") );
					result.addString("HG_NM"		,rs.getString("HG_NM") );					
					result.addString("HP" 			,rs.getString("HP") );
					result.addString("CNT" 			,Integer.toString(cnt) );
					result.addString("STTL_AMT" 	,rs.getString("STTL_AMT") );
					result.addString("INS_MCNT" 	,rs.getString("INS_MCNT")); 
					result.addString("SMC"			,rs.getString("SMC"));					
					result.addString("COUPONNO" 	,str);
					result.addString("STTL_ATON" 	,rs.getString("STTL_ATON"));
					result.addString("CNCL_ATON" 	,rs.getString("CNCL_ATON"));		
					result.addString("STTL_STAT_CLSS" 	,rs.getString("STTL_STAT_CLSS"));
					
					result.addString("RESULT", "00"); //정상결과 		
					
					str = "";
					
				}
				
			}
			
			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(rs2 != null) rs2.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(pstmt2 != null) pstmt2.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}
	
    /*************************************************************************
	 * 결제 취소    
	 ************************************************************************ */
	public int payCancel(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		int result_upd = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String odr_no = data.getString("ord_no");		// 주문번호
			String cdhd_id = data.getString("cdhd_id");		// 아이디
        		
    		// 결제취소처리 
    		boolean payCancelResult = false;
    		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// 승인정보
    		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");

			// 결제변수
			String sttl_amt = "";	// 결제금액
			String mer_no = "";		// 가맹점번호
			String card_no = "";	// 카드번호
			String vald_date = "";	// 유효일자
			String ins_mcnt = "";	// 할부개월수
			String auth_no = "";	// 승인번호
			String ip = request.getRemoteAddr();  // 단말번호(IP, '.'제외)
			String sttl_mthd_clss = "";	// 결제방법구분코드
			String sttl_gds_clss = "";	// 결제상품구분코드
			

			// 결제 내역 조회
			sql = this.setPayBackListQuery(); 
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, odr_no );
            rs = pstmt.executeQuery();	
            
            while(rs.next()){
            	
            	payCancelResult = false;
            	
				sttl_amt = rs.getString("STTL_AMT");
				mer_no = rs.getString("MER_NO");
				card_no = rs.getString("CARD_NO");
				vald_date = rs.getString("VALD_DATE");
				ins_mcnt = rs.getString("INS_MCNT");
				auth_no = rs.getString("AUTH_NO");				
				sttl_mthd_clss = rs.getString("STTL_MTHD_CLSS");
				sttl_gds_clss = rs.getString("STTL_GDS_CLSS");				
				
				payEtt.setMerMgmtNo(mer_no);		// 가맹점 번호
				payEtt.setCardNo(card_no);			// isp카드번호
				payEtt.setValid(vald_date);			// 만료 일자
				payEtt.setAmount(sttl_amt);			// 결제금액	
				payEtt.setInsTerm(ins_mcnt);		// 할부개월수
				payEtt.setRemoteAddr(ip);			// ip 주소
				payEtt.setUseNo(auth_no);			// 승인번호

				String host_ip = java.net.InetAddress.getLocalHost().getHostAddress();
				if( "211.181.255.40".equals(host_ip)) {
					payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// 취소전문 호출
				} else {
					payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// 취소전문 호출
				}
				debug("payCancelResult========> " + payCancelResult);
				
				if(payCancelResult){
					
					sql = this.getPayUpdateQuery(); 
		            pstmt = conn.prepareStatement(sql);
		        	pstmt.setString(1, odr_no );
					result_upd = pstmt.executeUpdate();
					
					debug("ODR_NO========> " + odr_no);
					debug("result_upd========> " + result_upd);

				}else{	// 전문취소실패 내역 저장
					
					GolfPaymentRegDaoProc payFailProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");						
					debug("========================= sttl_gds_clss========> " + sttl_gds_clss);
					DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);						
					dataSet.setString("CDHD_ID", cdhd_id);						//회원아이디
					dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);		//결제방법구분코드 :0001 : BC카드 / 0002:BC카드 + TOP포인트 / 0003:타사카드 / 0004:계좌이체 
					dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);			//결제상품구분코드 0001:챔피온연회비 0002:블루연회비 0003:골드연회비 0008:블랙연회비 
					dataSet.setString("STTL_STAT_CLSS", "Y");					//결제여부 N:결제완료 / Y:결재취소
						
					int result_fail = payFailProc.failExecute(context, dataSet, request, payEtt);
					
					debug("전문취소실패 내역 저장 결과========> " + result_fail);						
					
				}
            }
						
		} catch(Exception e) {
			try	{
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			//e.printStackTrace();
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
	        try { if(rs != null) rs.close(); } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	} 
	
    
	/*************************************************************************
	 * 쿠폰 구매 리스트 가져오기
	 ************************************************************************ */	
    private String getOdrList(String sch_state,String sch_date_st,String sch_date_ed){
    	
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT *	\n");
		sql.append(" FROM (   	\n");
		sql.append(" 	SELECT ROWNUM RNUM, CEIL(ROWNUM/?) AS PAGE, MAX(RNUM) OVER() TOT_CNT, ((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM,	\n");
		sql.append(" 			JUMIN_NO,GUBUN, GRD_NM, HG_NM, HP, CNT, STTL_AMT, INS_MCNT, SMC, EA_INFO, STTL_ATON, CNCL_ATON, STTL_STAT_CLSS	\n");
		sql.append(" 	FROM (	\n");
		sql.append(" 		SELECT ROWNUM RNUM,	\n");
		sql.append(" 		JUMIN_NO,GUBUN, GRD_NM, HG_NM, HP, CNT, STTL_AMT, INS_MCNT, SMC, EA_INFO, \n");
		sql.append(" 		STTL_ATON, CNCL_ATON, STTL_STAT_CLSS	\n");
		sql.append(" 		FROM (	\n");
		sql.append(" 			SELECT	 A.JUMIN_NO, DECODE (CODE.GOLF_CMMN_CODE_NM, null, '비회원','회원')GUBUN, CODE.GOLF_CMMN_CODE_NM GRD_NM, A.HG_NM,	\n");
		sql.append(" 			A.HP_DDD_NO||'-'||A.HP_TEL_NO1||'-'||A.HP_TEL_NO2 HP, COUNT(A.CUPN_NO) CNT, B.STTL_AMT, B.INS_MCNT,	\n");
		sql.append(" 			DECODE(B.STTL_MTHD_CLSS, '0001', '비씨카드', '0002', '복합결제', B.STTL_MINS_NM) SMC,	\n");
		sql.append(" 			A.EA_INFO, TO_CHAR(TO_DATE(SUBSTR(B.STTL_ATON,1,8)),'YYYY-MM-DD')STTL_ATON, TO_CHAR(TO_DATE(SUBSTR(NVL(B.CNCL_ATON,''),1,8)),'YYYY-MM-DD') CNCL_ATON, 	\n");
		sql.append(" 			B.STTL_STAT_CLSS	\n");		
		sql.append(" 			FROM BCDBA.TBEVNTLOTPWIN A 	\n");
		sql.append(" 			JOIN BCDBA.TBGSTTLMGMT b ON  A.EA_INFO = B.ODR_NO	\n");
		sql.append(" 			LEFT JOIN BCDBA.TBGGOLFCDHD MEM ON  A.JUMIN_NO=MEM.JUMIN_NO	\n");
		sql.append(" 			LEFT JOIN BCDBA.TBGGOLFCDHDCTGOMGMT GRD ON  MEM.CDHD_CTGO_SEQ_NO=GRD.CDHD_CTGO_SEQ_NO	\n");
		sql.append(" 			LEFT JOIN BCDBA.TBGCMMNCODE CODE ON GRD.CDHD_SQ2_CTGO=CODE.GOLF_CMMN_CODE AND CODE.GOLF_CMMN_CLSS='0005'	\n");
		sql.append(" 			WHERE A.SITE_CLSS = '10'	\n");
		sql.append(" 			AND EVNT_NO = '122'	\n");
		
		if(sch_state.equals("name")){
			sql.append("\n                  AND A.HG_NM LIKE '%'||?||'%'                         ");
		}else if(sch_state.equals("socid")){
			sql.append("\n                  AND A.JUMIN_NO LIKE '%'||?||'%'                      ");
		}else if(sch_state.equals("cancel")){
			sql.append("\n                   AND B.STTL_STAT_CLSS = 'Y'  and ( A.HG_NM LIKE '%'||?||'%')                    ");
		}
		
		if(!(sch_date_st.equals("") || sch_date_ed.equals(""))){
			sql.append("\n                  AND A.PWIN_DATE BETWEEN ? AND ?                   ");
		}		
		
		sql.append(" 			GROUP BY A.EA_INFO,A.JUMIN_NO, A.HG_NM,  A.HP_DDD_NO, A.HP_TEL_NO1, A.HP_TEL_NO2, CODE.GOLF_CMMN_CODE_NM,	\n");
		sql.append(" 			B.STTL_ATON, B.INS_MCNT,  B.CNCL_ATON, B.STTL_MINS_NM, B.STTL_MTHD_CLSS,  B.STTL_AMT, B.STTL_STAT_CLSS	\n");
		sql.append(" 			ORDER BY B.STTL_ATON DESC	\n");
		sql.append(" 		)	\n");
		sql.append(" 	 )	\n");
		sql.append(" 	 ORDER BY RNUM 	\n");
		sql.append("  )	\n");
		sql.append("  WHERE PAGE= ?	\n");

		return sql.toString();
		
	}
    
    
	/*************************************************************************
	 * 쿠폰 구매 리스트 가져오기
	 ************************************************************************ */	
    private String getSelectDetExcelQuery(String sch_state,String sch_date_st,String sch_date_ed){
    	
		StringBuffer sql = new StringBuffer();
		
		sql.append(" 			SELECT	 A.JUMIN_NO, DECODE (CODE.GOLF_CMMN_CODE_NM, null, '비회원','회원')GUBUN, CODE.GOLF_CMMN_CODE_NM GRD_NM, A.HG_NM,	\n");
		sql.append(" 			A.HP_DDD_NO||'-'||A.HP_TEL_NO1||'-'||A.HP_TEL_NO2 HP, COUNT(A.CUPN_NO) CNT, B.STTL_AMT, B.INS_MCNT,	\n");
		sql.append(" 			DECODE(B.STTL_MTHD_CLSS, '0001', '비씨카드', '0002', '복합결제', B.STTL_MINS_NM) SMC,	\n");
		sql.append(" 			A.EA_INFO, TO_CHAR(TO_DATE(SUBSTR(B.STTL_ATON,1,8)),'YYYY-MM-DD')STTL_ATON, TO_CHAR(TO_DATE(SUBSTR(NVL(B.CNCL_ATON,''),1,8)),'YYYY-MM-DD') CNCL_ATON, 	\n");
		sql.append(" 			B.STTL_STAT_CLSS	\n");		
		sql.append(" 			FROM BCDBA.TBEVNTLOTPWIN A 	\n");
		sql.append(" 			JOIN BCDBA.TBGSTTLMGMT b ON  A.EA_INFO = B.ODR_NO	\n");
		sql.append(" 			LEFT JOIN BCDBA.TBGGOLFCDHD MEM ON  A.JUMIN_NO=MEM.JUMIN_NO	\n");
		sql.append(" 			LEFT JOIN BCDBA.TBGGOLFCDHDCTGOMGMT GRD ON  MEM.CDHD_CTGO_SEQ_NO=GRD.CDHD_CTGO_SEQ_NO	\n");
		sql.append(" 			LEFT JOIN BCDBA.TBGCMMNCODE CODE ON GRD.CDHD_SQ2_CTGO=CODE.GOLF_CMMN_CODE AND CODE.GOLF_CMMN_CLSS='0005'	\n");
		sql.append(" 			WHERE A.SITE_CLSS = '10'	\n");
		sql.append(" 			AND EVNT_NO = '122'	\n");		
		
		if(sch_state.equals("name")){
			sql.append("\n                  AND A.HG_NM LIKE '%'||?||'%'                         ");
		}else if(sch_state.equals("socid")){
			sql.append("\n                  AND A.JUMIN_NO LIKE '%'||?||'%'                      ");
		}else if(sch_state.equals("cancel")){
			sql.append("\n                   AND B.STTL_STAT_CLSS = 'Y'  and ( A.HG_NM LIKE '%'||?||'%')                    ");
		}
		
		if(!(sch_date_st.equals("") || sch_date_ed.equals(""))){
			sql.append("\n                  AND A.PWIN_DATE BETWEEN ? AND ?                   ");
		}		
		
		sql.append(" 			GROUP BY A.EA_INFO,A.JUMIN_NO, A.HG_NM,  A.HP_DDD_NO, A.HP_TEL_NO1, A.HP_TEL_NO2, CODE.GOLF_CMMN_CODE_NM,	\n");
		sql.append(" 			B.STTL_ATON, B.INS_MCNT,  B.CNCL_ATON, B.STTL_MINS_NM, B.STTL_MTHD_CLSS,  B.STTL_AMT, B.STTL_STAT_CLSS	\n");
		sql.append(" 			ORDER BY B.STTL_ATON DESC	\n");

		return sql.toString();
		
	}    
    
    
	/*************************************************************************
	 * 주문번호에 따라 쿠폰리스트 가져오기
	 ************************************************************************ */
    private String getCouponList(){    	
    	
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT	CUPN_NO \n");
		sql.append(" FROM BCDBA.TBEVNTLOTPWIN  \n");
		sql.append(" WHERE SITE_CLSS = '10' \n");
		sql.append(" AND EVNT_NO = '122' \n");
		sql.append(" AND EA_INFO = ? \n");
		
		return sql.toString();
		
	}	
    
    
	/*************************************************************************
	 * 승인 내역 가져오기
	 ************************************************************************ */
	private String setPayBackListQuery(){
		StringBuffer sql = new StringBuffer();        
		sql.append("\n	SELECT STTL_AMT, MER_NO, CARD_NO, VALD_DATE, INS_MCNT, AUTH_NO, STTL_MTHD_CLSS, STTL_GDS_CLSS	\n");
		sql.append("\t	FROM BCDBA.TBGSTTLMGMT	\n");
		sql.append("\t	WHERE ODR_NO=?	\n");
		return sql.toString();
	}  
	
	
    /*************************************************************************
	 * 결제 취소 업데이트    
	 ************************************************************************ */
    private String getPayUpdateQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t	UPDATE BCDBA.TBGSTTLMGMT	\n");
 		sql.append("\t	SET STTL_STAT_CLSS='Y', CNCL_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
 		sql.append("\t	WHERE ODR_NO=?	\n");
        return sql.toString();
    }	
	
}


/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmOrdListDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 > 이벤트 > 쇼핑 > 주문리스트
*   적용범위  : golf
*   작성일자  : 2009-05-14
************************** 수정이력 ****************************************************************
*    일자   작성자   변경사항
* 20110323  이경희 	보이스캐디 쇼핑    
* 20110425  이경희 	골프퍼팅3홀컵 + 골프퍼팅매트세트  -> 상품 증가를 대비해 코드화
* 20120308 SHIN CHEONG GWI 골프버디보이스 추가
****************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.event.shop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.golf.common.AppConfig;

/******************************************************************************
 * Golf
 * @author	미디어포스
 * @version	1.0 
 ******************************************************************************/
public class GolfAdmOrdListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmOrdListDaoProc 프로세스 생성자   
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmOrdListDaoProc() {}

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);	
			
			// 검색값

			String sch_yn				= data.getString("sch_yn");
			String sch_date_st			= data.getString("sch_date_st");
			String sch_date_ed			= data.getString("sch_date_ed");	
			String sch_type				= data.getString("sch_type");
			String sch_text				= data.getString("sch_text");
			String sch_brand			= data.getString("sch_brand");			// 제휴코드
			String sch_ord_dtl_clss		= data.getString("sch_ord_dtl_clss");	// 구매여부 => 구매/비구매 ODR_DTL_CLSS:10/20
			String sch_dlv_yn			= data.getString("sch_dlv_yn");			// 발송여부 => 발송/미발송 DLV_YN:Y/N
			String sch_ord_stat_clss	= data.getString("sch_ord_stat_clss");	// 환불여부 => 환불/해당사항없음 ODR_STAT_CLSS : 61/else
			String gubun				= data.getString("gubun");
			String productName			= data.getString("productName");   //상품유형
			
			String sql = this.getSelectQuery(sch_yn, sch_date_st, sch_date_ed, sch_type, sch_text, 
									sch_brand, sch_ord_dtl_clss, sch_dlv_yn, sch_ord_stat_clss, gubun, productName);

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, 20);
			pstmt.setLong(++idx, data.getLong("page_no"));
			
			if(!GolfUtil.empty(sch_yn)){
				if(!GolfUtil.empty(sch_brand))			pstmt.setString(++idx, sch_brand);
				if(!GolfUtil.empty(sch_ord_dtl_clss))	pstmt.setString(++idx, sch_ord_dtl_clss);
				if(!GolfUtil.empty(sch_dlv_yn))			pstmt.setString(++idx, sch_dlv_yn);
			}
			
			pstmt.setLong(++idx, data.getLong("page_no"));
						
			rs = pstmt.executeQuery();
			
			// 리스트 출력 값
			String jumin_no = "";
			String grd_nm = "";
			String send_mobile = "";
			String give_mobile = "";
			String buy_yn = "";
			String refund_yn = "";
			String gds_nm = "";	// 제품명
			String opt_nm = "";	// 옵션명

			int art_num_no = 0;
			if(rs != null) {			 

				while(rs.next())  {	
				        
					result.addInt("ART_NUM" 			,rs.getInt("ART_NUM")-art_num_no );
					result.addString("TOT_CNT"			,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("RNUM"				,rs.getString("RNUM") );
					art_num_no++;
					
					jumin_no = rs.getString("JUMIN_NO");
					if(!GolfUtil.empty(jumin_no)) jumin_no = jumin_no.substring(0, 6)+"-"+jumin_no.substring(6, 13);
					
					grd_nm = rs.getString("GRD_NM");
					if(GolfUtil.empty(grd_nm))	grd_nm = "비회원";
					
					if(!GolfUtil.empty(rs.getString("HP_DDD_NO")) && !GolfUtil.empty(rs.getString("HP_TEL_HNO")) && !GolfUtil.empty(rs.getString("HP_TEL_SNO"))){
						send_mobile = rs.getString("HP_DDD_NO")+"-"+rs.getString("HP_TEL_HNO")+"-"+rs.getString("HP_TEL_SNO");
					}else{
						send_mobile = "";
					}
					if(!GolfUtil.empty(rs.getString("RECEIVER_TEL1")) && !GolfUtil.empty(rs.getString("RECEIVER_TEL2")) && !GolfUtil.empty(rs.getString("RECEIVER_TEL3"))){
						give_mobile = rs.getString("RECEIVER_TEL1")+"-"+rs.getString("RECEIVER_TEL2")+"-"+rs.getString("RECEIVER_TEL3");
					}else{
						give_mobile = "";
					}
					
					if(rs.getString("ODR_DTL_CLSS").equals("10")){
						buy_yn = GolfUtil.comma(rs.getString("ODR_AMT"));
					}else{
						buy_yn = "-";
					}
					
					if(rs.getString("ODR_STAT_CLSS").equals("61")){
						refund_yn = "Y";
					}else{
						refund_yn = "N";
					}
					
					if ( gubun.equals("A")){
						
						gds_nm = rs.getString("GDS_NM");
						opt_nm = rs.getString("OPT_NM");
						if(!GolfUtil.empty(opt_nm)){
							gds_nm = gds_nm+"["+opt_nm+"]";  
						}
						
					}
					
					result.addString("ODR_NO" 			,rs.getString("ODR_NO") );
					result.addString("JUMIN_NO" 		,jumin_no );					
					result.addString("GRD_NM" 			,grd_nm );
					result.addString("SEND_NM"			,rs.getString("HG_NM") );
					result.addString("GIVE_NM" 			,rs.getString("EMAIL_ID") );
					result.addString("SEND_MOBILE" 		,send_mobile );
					result.addString("GIVE_MOBILE" 		,give_mobile );
					result.addString("ODR_DATE" 		,rs.getString("ODR_DATE") );
					
					if ( gubun.equals("A")){
						result.addString("GDS_NM" 			,gds_nm );						
						result.addString("BRND_NM" 			,rs.getString("BRND_NM") );
					}
					
					if ( gubun.equals("B")){ 
						result.addString("INS_MCNT" 	,rs.getString("INS_MCNT") ); //할부개월수
						result.addString("ACPT_QTY" 	,rs.getString("ACPT_QTY") ); //수량
						result.addString("SMC" 			,rs.getString("SMC") );		 //결제유형(카드유형)
						result.addString("PRODUCTNAME"	,rs.getString("PRODUCTNAME") );	//상품명
						
					}					
					
					result.addString("BUY_YN" 			,buy_yn );
					result.addString("DLV_YN" 			,rs.getString("DLV_YN") );
					result.addString("REFUND_YN" 		,refund_yn );
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
	 * 엑셀 리스트
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute_xls(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
				
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);	
			
			// 검색값

			String sch_yn				= data.getString("sch_yn");
			String sch_date_st			= data.getString("sch_date_st");
			String sch_date_ed			= data.getString("sch_date_ed");	
			String sch_type				= data.getString("sch_type");
			String sch_text				= data.getString("sch_text");
			String sch_brand			= data.getString("sch_brand");			// 제휴코드
			String sch_ord_dtl_clss		= data.getString("sch_ord_dtl_clss");	// 구매여부 => 구매/비구매 ODR_DTL_CLSS:10/20
			String sch_dlv_yn			= data.getString("sch_dlv_yn");			// 발송여부 => 발송/미발송 DLV_YN:Y/N
			String sch_ord_stat_clss	= data.getString("sch_ord_stat_clss");	// 환불여부 => 환불/해당사항없음 ODR_STAT_CLSS : 61/else
			String gubun				= data.getString("gubun");
			String productName			= data.getString("productName");   //상품유형
			
			String sql = this.getXlsQuery(sch_yn, sch_date_st, sch_date_ed, sch_type, sch_text, 
								sch_brand, sch_ord_dtl_clss, sch_dlv_yn, sch_ord_stat_clss, gubun, productName);

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			
			if(!GolfUtil.empty(sch_yn)){
				if(!GolfUtil.empty(sch_brand))			pstmt.setString(++idx, sch_brand);
				if(!GolfUtil.empty(sch_ord_dtl_clss))	pstmt.setString(++idx, sch_ord_dtl_clss);
				if(!GolfUtil.empty(sch_dlv_yn))			pstmt.setString(++idx, sch_dlv_yn);
			}
						
			rs = pstmt.executeQuery();
			
			// 리스트 출력 값
			String jumin_no = "";
			String grd_nm = "";
			String send_mobile = "";
			String give_mobile = "";
			String buy_yn = "";
			String buyAtm = "";
			String refund_yn = "";
			String give_zip = "";
			String gds_nm = "";	// 제품명
			String opt_nm = "";	// 옵션명	
			
			if(rs != null) {			 
			
				while(rs.next())  {
					
					jumin_no = rs.getString("JUMIN_NO");
					if(!GolfUtil.empty(jumin_no)) jumin_no = jumin_no.substring(0, 6)+"-"+jumin_no.substring(6, 13);
					
					grd_nm = rs.getString("GRD_NM");
					if(GolfUtil.empty(grd_nm))	grd_nm = "비회원";
					
					if(!GolfUtil.empty(rs.getString("HP_DDD_NO")) && !GolfUtil.empty(rs.getString("HP_TEL_HNO")) && !GolfUtil.empty(rs.getString("HP_TEL_SNO"))){
						send_mobile = rs.getString("HP_DDD_NO")+"-"+rs.getString("HP_TEL_HNO")+"-"+rs.getString("HP_TEL_SNO");
					}else{
						send_mobile = "";
					}
					if(!GolfUtil.empty(rs.getString("RECEIVER_TEL1")) && !GolfUtil.empty(rs.getString("RECEIVER_TEL2")) && !GolfUtil.empty(rs.getString("RECEIVER_TEL3"))){
						give_mobile = rs.getString("RECEIVER_TEL1")+"-"+rs.getString("RECEIVER_TEL2")+"-"+rs.getString("RECEIVER_TEL3");
					}else{
						give_mobile = "";
					}
					
					//구매금액
					if(rs.getString("ODR_DTL_CLSS").equals("10")){
						buyAtm = GolfUtil.comma(rs.getString("ODR_AMT"));
					}else{
						buyAtm = "-";
					}
					
					//구매여부
					if(rs.getString("ODR_DTL_CLSS").equals("10")){
						buy_yn = "Y";
					}else{
						buy_yn = "N";
					}					
					
					if(rs.getString("ODR_STAT_CLSS").equals("61")){
						refund_yn = "Y";
					}else{
						refund_yn = "N";
					}

					give_zip = rs.getString("DLV_PL_ZP");
					if(!GolfUtil.empty(give_zip)) give_zip = give_zip.substring(0, 3)+"-"+give_zip.substring(3, 6);
						
					if ( gubun.equals("A")){

						gds_nm = rs.getString("GDS_NM");
						opt_nm = rs.getString("OPT_NM");
						if(!GolfUtil.empty(opt_nm)){
							gds_nm = gds_nm+"["+opt_nm+"]"; 
						}
					}else {							
						result.addString("PRODUCTNAME"	,rs.getString("PRODUCTNAME") );	//상품명
					}
					
					result.addString("ODR_NO" 			,rs.getString("ODR_NO") );
					result.addString("JUMIN_NO" 		,jumin_no );					
					result.addString("GRD_NM" 			,grd_nm );
					result.addString("SEND_NM"			,rs.getString("HG_NM") );
					result.addString("GIVE_NM" 			,rs.getString("EMAIL_ID") );
					result.addString("SEND_MOBILE" 		,send_mobile );
					result.addString("GIVE_MOBILE" 		,give_mobile );
					result.addString("ODR_DATE" 		,rs.getString("ODR_DATE") );
					if ( gubun.equals("A")){
						result.addString("BRND_NM" 			,rs.getString("BRND_NM") );
					}
					result.addString("BUY_YN" 			,buy_yn );					
					result.addString("BUY_ATM" 			,buyAtm );
					result.addString("DLV_YN" 			,rs.getString("DLV_YN") );
					result.addString("REFUND_YN" 		,refund_yn );
					result.addString("GIVE_ZIP" 		,give_zip );
					result.addString("GDS_NM" 			,gds_nm );
					result.addString("DLV_PL_DONG_OVR_ADDR" ,rs.getString("DLV_PL_DONG_OVR_ADDR") );
					result.addString("DLV_PL_DONG_BLW_ADDR" ,rs.getString("DLV_PL_DONG_BLW_ADDR") );
					
					if ( gubun.equals("B")){
						
						result.addString("INS_MCNT" 		,rs.getString("INS_MCNT") );
						result.addString("ACPT_QTY" 		,rs.getString("ACPT_QTY") );
						result.addString("SMC" 				,rs.getString("SMC") );
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


	/** ***********************************************************************
    * 구매 리스트
    ************************************************************************ */
    private String getSelectQuery(String sch_yn, String sch_date_st, String sch_date_ed, String sch_type, String sch_text, 
    					String sch_brand, String sch_ord_dtl_clss, String sch_dlv_yn, String sch_ord_stat_clss, String gubun, String productName){
    	
        StringBuffer sql = new StringBuffer();
        
        if ( gubun.equals("A")){
        	
			sql.append("\n	SELECT *	\n");
			sql.append("\t	FROM (	\n");
			sql.append("\t	    SELECT ROWNUM RNUM, CEIL(ROWNUM/?) AS PAGE, MAX(RNUM) OVER() TOT_CNT, ((MAX(RNUM) OVER())-(?-1)*20) AS ART_NUM	\n");
			sql.append("\t	        , ODR_NO, JUMIN_NO, GRD_NM, HG_NM, EMAIL_ID	\n");
			sql.append("\t	        , HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, RECEIVER_TEL1, RECEIVER_TEL2, RECEIVER_TEL3	\n");
			sql.append("\t	        , GDS_NM, ODR_DATE, BRND_NM, ODR_DTL_CLSS, ODR_AMT, DLV_YN, ODR_STAT_CLSS, OPT_NM	\n");
			sql.append("\t	    FROM (	\n");
			sql.append("\t	        SELECT ROWNUM RNUM	\n");
			sql.append("\t	            , ODR.ODR_NO, ODR.JUMIN_NO, CODE.GOLF_CMMN_CODE_NM GRD_NM, ODR.HG_NM, ODR.EMAIL_ID	\n");
			sql.append("\t	            , ODR.HP_DDD_NO, ODR.HP_TEL_HNO, ODR.HP_TEL_SNO	\n");
			sql.append("\t	            , ODR.RECEIVER_TEL1, ODR.RECEIVER_TEL2, ODR.RECEIVER_TEL3	\n");
			sql.append("\t	            , GDS.GDS_NM, TO_CHAR(TO_DATE(SUBSTR(ODR.ODR_ATON,1,8)),'YYYY-MM-DD') ODR_DATE, GDS.BRND_NM, ODR.ODR_DTL_CLSS, ODR.ODR_AMT	\n");
			sql.append("\t	            , ODR.DLV_YN, ODR.ODR_STAT_CLSS, OPT.SGL_LST_ITM_DTL_CTNT OPT_NM	\n");
			sql.append("\t	        FROM BCDBA.TBGLUGODRCTNT ODR	\n");
			sql.append("\t	            LEFT JOIN BCDBA.TBGGOLFCDHD MEM ON ODR.CDHD_ID=MEM.CDHD_ID	\n");
			sql.append("\t	            LEFT JOIN BCDBA.TBGGOLFCDHDCTGOMGMT GRD ON MEM.CDHD_CTGO_SEQ_NO=GRD.CDHD_CTGO_SEQ_NO	\n");
			sql.append("\t	            LEFT JOIN BCDBA.TBGCMMNCODE CODE ON GRD.CDHD_SQ2_CTGO=CODE.GOLF_CMMN_CODE AND CODE.GOLF_CMMN_CLSS='0005'	\n");
			sql.append("\t	            JOIN BCDBA.TBGDS GDS ON ODR.GDS_CODE=GDS.GDS_CODE	\n");
			sql.append("\t	            LEFT JOIN BCDBA.TBSGLGDS OPT ON ODR.DC_RT=SGL_LST_ITM_CODE AND OPT.GDS_CODE=ODR.GDS_CODE	\n");
			sql.append("\t	        WHERE ODR.ODR_NO IS NOT NULL	\n");
			
			if(GolfUtil.empty(sch_ord_dtl_clss))	sql.append("\t	            AND ODR_DTL_CLSS<>'20'	\n");
	
			if(!GolfUtil.empty(sch_yn)){
				if(!GolfUtil.empty(sch_date_st))		sql.append("\t	            AND ODR.ODR_ATON>='"+sch_date_st+"000000'	\n");
				if(!GolfUtil.empty(sch_date_ed))		sql.append("\t	            AND ODR.ODR_ATON<='"+sch_date_ed+"999999'	\n");
				if(!GolfUtil.empty(sch_brand))			sql.append("\t	            AND GDS.INTNT_BRND_CLSS=?	\n");
				if(!GolfUtil.empty(sch_ord_dtl_clss))	sql.append("\t	            AND ODR.ODR_DTL_CLSS=?	\n");
				if(!GolfUtil.empty(sch_dlv_yn))			sql.append("\t	            AND ODR.DLV_YN=?	\n");
	
				if(!GolfUtil.empty(sch_type) && !GolfUtil.empty(sch_text)){
					if("MOBILE".equals(sch_type)){
						sql.append("\t	            AND (HP_DDD_NO LIKE '%"+sch_text+"%' OR HP_TEL_HNO LIKE '%"+sch_text+"%' OR HP_TEL_SNO LIKE '%"+sch_text+"%'	\n");
						sql.append("\t	            OR RECEIVER_TEL1 LIKE '%"+sch_text+"%' OR RECEIVER_TEL2 LIKE '%"+sch_text+"%' OR RECEIVER_TEL3 LIKE '%"+sch_text+"%')	\n");
					}else{
						sql.append("\t	            AND "+sch_type+" LIKE '%"+sch_text+"%'	\n");
					}
				}
				
				if(!GolfUtil.empty(sch_ord_stat_clss)){
					if("61".equals(sch_ord_stat_clss)){
						sql.append("\t	            AND ODR.ODR_STAT_CLSS=61	\n");
					}else{
						sql.append("\t	            AND ODR.ODR_STAT_CLSS<>61	\n");
					}
				}
			}
			
			sql.append("\t	        ORDER BY ODR.ODR_ATON DESC	\n");
			sql.append("\t	    )	\n");
			sql.append("\t	    ORDER BY RNUM	\n");
			sql.append("\t	)	\n");
			sql.append("\t	WHERE PAGE=?	\n");
		
        }else {
        	
			sql.append("\n	SELECT *	\n");
			sql.append("\t	FROM (	\n");
			sql.append("\t	    SELECT ROWNUM RNUM, CEIL(ROWNUM/?) AS PAGE, MAX(RNUM) OVER() TOT_CNT, ((MAX(RNUM) OVER())-(?-1)*20) AS ART_NUM	\n");
			sql.append("\t	        , ODR_NO, JUMIN_NO, GRD_NM, HG_NM, EMAIL_ID	\n");
			sql.append("\t	        , HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, RECEIVER_TEL1, RECEIVER_TEL2, RECEIVER_TEL3	\n");
			sql.append("\t	        , ODR_DATE, ODR_DTL_CLSS, ODR_AMT, DLV_YN, ODR_STAT_CLSS, INS_MCNT, ACPT_QTY \n");
			sql.append("\t	        , DECODE(STTL_MTHD_CLSS, '0001', '비씨카드', '0002', '복합결제', STTL_MINS_NM) SMC, PRODUCTNAME \n");			
			sql.append("\t	    FROM (	\n");
			sql.append("\t	        SELECT ROWNUM RNUM	\n");
			sql.append("\t	            , ODR.ODR_NO, ODR.JUMIN_NO, CODE.GOLF_CMMN_CODE_NM GRD_NM, ODR.HG_NM, ODR.EMAIL_ID	\n");
			sql.append("\t	            , ODR.HP_DDD_NO, ODR.HP_TEL_HNO, ODR.HP_TEL_SNO	\n");
			sql.append("\t	            , ODR.RECEIVER_TEL1, ODR.RECEIVER_TEL2, ODR.RECEIVER_TEL3	\n");
			sql.append("\t	            , TO_CHAR(TO_DATE(SUBSTR(ODR.ODR_ATON,1,8)),'YYYY-MM-DD') ODR_DATE, ODR.ODR_DTL_CLSS, ODR.ODR_AMT	\n");
			sql.append("\t	            , ODR.DLV_YN, ODR.ODR_STAT_CLSS, STT.INS_MCNT, ODR.ACPT_QTY, STT.STTL_MTHD_CLSS, STT.STTL_MINS_NM   \n");
			sql.append("\t	            , DECODE (GDS_CODE, '2011040101','보이스캐디','2011040102','골프퍼팅3홀컵 <br>+ 골프퍼팅매트세트','2011040103','골프버디보이스') PRODUCTNAME   \n"); 
			sql.append("\t	        FROM BCDBA.TBGLUGODRCTNT ODR	\n");
			sql.append("\t	            LEFT JOIN BCDBA.TBGGOLFCDHD MEM ON ODR.CDHD_ID=MEM.CDHD_ID	\n");
			sql.append("\t	            LEFT JOIN BCDBA.TBGGOLFCDHDCTGOMGMT GRD ON MEM.CDHD_CTGO_SEQ_NO=GRD.CDHD_CTGO_SEQ_NO	\n");
			sql.append("\t	            LEFT JOIN BCDBA.TBGCMMNCODE CODE ON GRD.CDHD_SQ2_CTGO=CODE.GOLF_CMMN_CODE AND CODE.GOLF_CMMN_CLSS='0005'	\n");
			sql.append("\t	            LEFT JOIN BCDBA.TBGSTTLMGMT STT ON ODR.ODR_NO=STT.ODR_NO	\n");			
			sql.append("\t	        WHERE ODR.ODR_NO IS NOT NULL	\n");
			
			if(GolfUtil.empty(sch_ord_dtl_clss))	sql.append("\t	            AND ODR_DTL_CLSS<>'20'	\n");
	
			if(!GolfUtil.empty(sch_yn)){
				if(!GolfUtil.empty(sch_date_st))		sql.append("\t	            AND ODR.ODR_ATON>='"+sch_date_st+"000000'	\n");
				if(!GolfUtil.empty(sch_date_ed))		sql.append("\t	            AND ODR.ODR_ATON<='"+sch_date_ed+"999999'	\n");				
				if(!GolfUtil.empty(sch_ord_dtl_clss))	sql.append("\t	            AND ODR.ODR_DTL_CLSS=?	\n");
				if(!GolfUtil.empty(sch_dlv_yn))			sql.append("\t	            AND ODR.DLV_YN=?	\n");
	
				if(!GolfUtil.empty(sch_type) && !GolfUtil.empty(sch_text)){
					if("MOBILE".equals(sch_type)){
						sql.append("\t	            AND (HP_DDD_NO LIKE '%"+sch_text+"%' OR HP_TEL_HNO LIKE '%"+sch_text+"%' OR HP_TEL_SNO LIKE '%"+sch_text+"%'	\n");
						sql.append("\t	            OR RECEIVER_TEL1 LIKE '%"+sch_text+"%' OR RECEIVER_TEL2 LIKE '%"+sch_text+"%' OR RECEIVER_TEL3 LIKE '%"+sch_text+"%')	\n");
					}else{
						sql.append("\t	            AND "+sch_type+" LIKE '%"+sch_text+"%'	\n");
					}
				}
				
				if(!GolfUtil.empty(sch_ord_stat_clss)){
					if("61".equals(sch_ord_stat_clss)){
						sql.append("\t	            AND ODR.ODR_STAT_CLSS=61	\n");
					}else{
						sql.append("\t	            AND ODR.ODR_STAT_CLSS<>61	\n");
					}
				}
			}
			
			if (productName.equals("2011040101")){
				sql.append("\t	        AND GDS_CODE = '2011040101'	\n");
			}else if (productName.equals("2011040102")){
				sql.append("\t	        AND GDS_CODE = '2011040102'	\n");
			}else if (productName.equals("2011040103")){						// 골프버디보이스  (2012.03.08 추가)
				sql.append("\t	        AND GDS_CODE = '2011040103'	\n");
			}else {			
				sql.append("\t	        AND GDS_CODE > '201104'	\n");
			}
			
			sql.append("\t	        ORDER BY ODR.ODR_ATON DESC	\n");
			sql.append("\t	    )	\n");
			sql.append("\t	    ORDER BY RNUM	\n");
			sql.append("\t	)	\n");
			sql.append("\t	WHERE PAGE=?	\n");        	
        	
        }
		
		return sql.toString();
    }

	/** ***********************************************************************
    * 구매 리스트 - 엑셀
    ************************************************************************ */
    private String getXlsQuery(String sch_yn, String sch_date_st, String sch_date_ed, String sch_type, String sch_text, 
    							String sch_brand, String sch_ord_dtl_clss, String sch_dlv_yn, String sch_ord_stat_clss, String gubun, String productName){
        StringBuffer sql = new StringBuffer();
        
        if ( gubun.equals("A")){        
        	
			sql.append("\n	        SELECT ODR.ODR_NO, ODR.JUMIN_NO, CODE.GOLF_CMMN_CODE_NM GRD_NM, ODR.HG_NM, ODR.EMAIL_ID	\n");
			sql.append("\t	            , ODR.HP_DDD_NO, ODR.HP_TEL_HNO, ODR.HP_TEL_SNO	\n");
			sql.append("\t	            , ODR.RECEIVER_TEL1, ODR.RECEIVER_TEL2, ODR.RECEIVER_TEL3	\n");
			sql.append("\t	            , GDS.GDS_NM, TO_CHAR(TO_DATE(SUBSTR(ODR.ODR_ATON,1,8)),'YYYY-MM-DD') ODR_DATE, GDS.BRND_NM, ODR.ODR_DTL_CLSS, ODR.ODR_AMT	\n");
			sql.append("\t	            , ODR.DLV_YN, ODR.ODR_STAT_CLSS, OPT.SGL_LST_ITM_DTL_CTNT OPT_NM	\n");
			sql.append("\t	            , ODR.DLV_PL_ZP, ODR.DLV_PL_DONG_OVR_ADDR, ODR.DLV_PL_DONG_BLW_ADDR	\n");
			sql.append("\t	        FROM BCDBA.TBGLUGODRCTNT ODR	\n");
			sql.append("\t	            LEFT JOIN BCDBA.TBGGOLFCDHD MEM ON ODR.CDHD_ID=MEM.CDHD_ID	\n");
			sql.append("\t	            LEFT JOIN BCDBA.TBGGOLFCDHDCTGOMGMT GRD ON MEM.CDHD_CTGO_SEQ_NO=GRD.CDHD_CTGO_SEQ_NO	\n");
			sql.append("\t	            LEFT JOIN BCDBA.TBGCMMNCODE CODE ON GRD.CDHD_SQ2_CTGO=CODE.GOLF_CMMN_CODE AND CODE.GOLF_CMMN_CLSS='0005'	\n");
			sql.append("\t	            JOIN BCDBA.TBGDS GDS ON ODR.GDS_CODE=GDS.GDS_CODE	\n");
			sql.append("\t	            LEFT JOIN BCDBA.TBSGLGDS OPT ON ODR.DC_RT=SGL_LST_ITM_CODE AND OPT.GDS_CODE=ODR.GDS_CODE	\n");
			sql.append("\t	        WHERE ODR.ODR_NO IS NOT NULL	\n");
	
			if(!GolfUtil.empty(sch_yn)){
				if(!GolfUtil.empty(sch_date_st))		sql.append("\t	            AND ODR.ODR_ATON>='"+sch_date_st+"000000'	\n");
				if(!GolfUtil.empty(sch_date_ed))		sql.append("\t	            AND ODR.ODR_ATON<='"+sch_date_ed+"999999'	\n");
				if(!GolfUtil.empty(sch_brand))			sql.append("\t	            AND GDS.INTNT_BRND_CLSS=?	\n");
				if(!GolfUtil.empty(sch_ord_dtl_clss))	sql.append("\t	            AND ODR.ODR_DTL_CLSS=?	\n");
				if(!GolfUtil.empty(sch_dlv_yn))			sql.append("\t	            AND ODR.DLV_YN=?	\n");
	
				if(!GolfUtil.empty(sch_type) && !GolfUtil.empty(sch_text)){
					if("MOBILE".equals(sch_type)){
						sql.append("\t	            AND (HP_DDD_NO LIKE '%"+sch_text+"%' OR HP_TEL_HNO LIKE '%"+sch_text+"%' OR HP_TEL_SNO LIKE '%"+sch_text+"%'	\n");
						sql.append("\t	            OR RECEIVER_TEL1 LIKE '%"+sch_text+"%' OR RECEIVER_TEL2 LIKE '%"+sch_text+"%' OR RECEIVER_TEL3 LIKE '%"+sch_text+"%')	\n");
					}else{
						sql.append("\t	            AND "+sch_type+" LIKE '%"+sch_text+"%'	\n");
					}
				}
				
				if(!GolfUtil.empty(sch_ord_stat_clss)){
					if("61".equals(sch_ord_stat_clss)){
						sql.append("\t	            AND ODR.ODR_STAT_CLSS=61	\n");
					}else{
						sql.append("\t	            AND ODR.ODR_STAT_CLSS<>61	\n");
					}
				}
			}
			
			sql.append("\t	        ORDER BY ODR.ODR_ATON DESC	\n");
			
        }else {
        	
			sql.append("\n	        SELECT ODR.ODR_NO, ODR.JUMIN_NO, CODE.GOLF_CMMN_CODE_NM GRD_NM, ODR.HG_NM, ODR.EMAIL_ID	\n");
			sql.append("\t	            , ODR.HP_DDD_NO, ODR.HP_TEL_HNO, ODR.HP_TEL_SNO	\n");
			sql.append("\t	            , ODR.RECEIVER_TEL1, ODR.RECEIVER_TEL2, ODR.RECEIVER_TEL3	\n");
			sql.append("\t	            , TO_CHAR(TO_DATE(SUBSTR(ODR.ODR_ATON,1,8)),'YYYY-MM-DD') ODR_DATE, ODR.ODR_DTL_CLSS, ODR.ODR_AMT	\n");
			sql.append("\t	            , ODR.DLV_YN, ODR.ODR_STAT_CLSS \n");
			sql.append("\t	            , ODR.DLV_PL_ZP, ODR.DLV_PL_DONG_OVR_ADDR, ODR.DLV_PL_DONG_BLW_ADDR, STT.INS_MCNT, ODR.ACPT_QTY	\n");
			sql.append("\t	            , DECODE(STTL_MTHD_CLSS, '0001', '비씨카드', '0002', '복합결제', STTL_MINS_NM) SMC	\n");
			sql.append("\t	            , DECODE (GDS_CODE, '2011040101','보이스캐디','2011040102','골프퍼팅3홀컵 + 골프퍼팅매트세트','2011040103','골프버디보이스') PRODUCTNAME   \n");
			sql.append("\t	        FROM BCDBA.TBGLUGODRCTNT ODR	\n");
			sql.append("\t	            LEFT JOIN BCDBA.TBGGOLFCDHD MEM ON ODR.CDHD_ID=MEM.CDHD_ID	\n");
			sql.append("\t	            LEFT JOIN BCDBA.TBGGOLFCDHDCTGOMGMT GRD ON MEM.CDHD_CTGO_SEQ_NO=GRD.CDHD_CTGO_SEQ_NO	\n");
			sql.append("\t	            LEFT JOIN BCDBA.TBGCMMNCODE CODE ON GRD.CDHD_SQ2_CTGO=CODE.GOLF_CMMN_CODE AND CODE.GOLF_CMMN_CLSS='0005'	\n");
			sql.append("\t	            LEFT JOIN BCDBA.TBGSTTLMGMT STT ON ODR.ODR_NO=STT.ODR_NO	\n");			
			sql.append("\t	        WHERE ODR.ODR_NO IS NOT NULL	\n");
			
			
			if (productName.equals("2011040101")){
				sql.append("\t	        AND GDS_CODE = '2011040101'	\n");
			}else if (productName.equals("2011040102")){
				sql.append("\t	        AND GDS_CODE = '2011040102'	\n");
			}else {			
				sql.append("\t	        AND GDS_CODE > '201104'	\n");
			}			
			
	
			if(!GolfUtil.empty(sch_yn)){
				if(!GolfUtil.empty(sch_date_st))		sql.append("\t	            AND ODR.ODR_ATON>='"+sch_date_st+"000000'	\n");
				if(!GolfUtil.empty(sch_date_ed))		sql.append("\t	            AND ODR.ODR_ATON<='"+sch_date_ed+"999999'	\n");				
				if(!GolfUtil.empty(sch_ord_dtl_clss))	sql.append("\t	            AND ODR.ODR_DTL_CLSS=?	\n");
				if(!GolfUtil.empty(sch_dlv_yn))			sql.append("\t	            AND ODR.DLV_YN=?	\n");
	
				if(!GolfUtil.empty(sch_type) && !GolfUtil.empty(sch_text)){
					if("MOBILE".equals(sch_type)){
						sql.append("\t	            AND (HP_DDD_NO LIKE '%"+sch_text+"%' OR HP_TEL_HNO LIKE '%"+sch_text+"%' OR HP_TEL_SNO LIKE '%"+sch_text+"%'	\n");
						sql.append("\t	            OR RECEIVER_TEL1 LIKE '%"+sch_text+"%' OR RECEIVER_TEL2 LIKE '%"+sch_text+"%' OR RECEIVER_TEL3 LIKE '%"+sch_text+"%')	\n");
					}else{
						sql.append("\t	            AND "+sch_type+" LIKE '%"+sch_text+"%'	\n");
					}
				}
				
				if(!GolfUtil.empty(sch_ord_stat_clss)){
					if("61".equals(sch_ord_stat_clss)){
						sql.append("\t	            AND ODR.ODR_STAT_CLSS=61	\n");
					}else{
						sql.append("\t	            AND ODR.ODR_STAT_CLSS<>61	\n");
					}
				}
			}
			
			sql.append("\t	        ORDER BY ODR.ODR_ATON DESC	\n");
			
        }
		
		return sql.toString();
    }
    

}

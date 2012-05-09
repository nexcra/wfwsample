/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmGrUpdFormDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 부킹 골프장 수정 폼 처리
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자    작성자   변경사항
*20110323  이경희 	보이스캐디 쇼핑
*20110425  이경희 	골프퍼팅3홀컵 + 골프퍼팅매트세트
*20120308 SHIN CHEONG GWI 골프버디보이스 추가
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.event.shop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.golf.common.AppConfig;

/******************************************************************************
 * Golf
 * @author	미디어포스
 * @version	1.0 
 * golfloung  20100302 임은혜 관리자 회원등급 변경내역 수정
 ******************************************************************************/
public class GolfAdmOrdUpdFormDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfadmGrUpdFormDaoProc 프로세스 생성자 
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmOrdUpdFormDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
						 
			//조회 ----------------------------------------------------------
			String odr_no = (String)data.getString("odr_no");
			String gubun = (String)data.getString("gubun");
			String sql = this.getSelectQuery(gubun);
			
			
			// 입력값 (INPUT)  
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, odr_no);
			rs = pstmt.executeQuery();

			// 출력 값
			String cdhd_id = "";
			String grd_nm = "";
			String send_mobile = "";
			String give_mobile = "";
			String give_zip = "";
			String gds_nm = "";	// 제품명
			String opt_nm = "";	// 옵션명
			
			if(rs != null) {
				while(rs.next())  {					

					grd_nm = rs.getString("GRD_NM");
					cdhd_id = "";
					if(GolfUtil.empty(grd_nm)){
						grd_nm = "비회원";
					}else{
						cdhd_id = rs.getString("CDHD_ID");
					}
					
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
					
					give_zip = rs.getString("DLV_PL_ZP");
					if(!GolfUtil.empty(give_zip)) give_zip = give_zip.substring(0, 3)+"-"+give_zip.substring(3, 6);
					
					
					if ( gubun.equals("A")){
						
						gds_nm = rs.getString("GDS_NM");
						opt_nm = rs.getString("OPT_NM");
						if(!GolfUtil.empty(opt_nm)){
							gds_nm = gds_nm+"["+opt_nm+"]";  
						}
						
					}else{
						gds_nm = rs.getString("PRODUCTNAME");
					}

					result.addString("CDHD_ID"				,cdhd_id );					
					result.addString("GRD_NM" 				,grd_nm );
					result.addString("JUMIN_NO" 			,rs.getString("JUMIN_NO") );
					result.addString("SEND_NM"				,rs.getString("HG_NM") );
					result.addString("GIVE_NM" 				,rs.getString("EMAIL_ID") );
					result.addString("SEND_MOBILE" 			,send_mobile );
					result.addString("GIVE_MOBILE" 			,give_mobile );
					result.addString("ODR_DATE" 			,rs.getString("ODR_DATE") );
					result.addString("GDS_NM" 				,gds_nm );

					if ( gubun.equals("A")){						
						result.addString("BRND_NM" 				,rs.getString("BRND_NM") );						
					}
					
					result.addString("BUY_YN" 				,rs.getString("ODR_DTL_CLSS") );
					result.addString("DLV_YN" 				,rs.getString("DLV_YN") );
					result.addString("REFUND_YN" 			,rs.getString("ODR_STAT_CLSS") );
					result.addString("HDLV_MSG_CTNT" 		,rs.getString("HDLV_MSG_CTNT") );
					result.addString("CONG_MSG_CTNT" 		,rs.getString("CONG_MSG_CTNT") );
					result.addString("GIVE_ZIP" 			,give_zip );
					result.addString("DLV_PL_DONG_OVR_ADDR" ,rs.getString("DLV_PL_DONG_OVR_ADDR") );
					result.addString("DLV_PL_DONG_BLW_ADDR" ,rs.getString("DLV_PL_DONG_BLW_ADDR") );
					result.addString("ACPT_QTY" 			,rs.getString("ACPT_QTY") );
					result.addString("ODR_AMT" 				,rs.getString("ODR_AMT") );
					result.addString("STTL_STAT_CLSS" 		,rs.getString("STTL_STAT_CLSS") );
					
					if ( gubun.equals("B")){ 
						result.addString("INS_MCNT" 		,rs.getString("INS_MCNT") );
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
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery(String gubun){
    	
    	StringBuffer sql = new StringBuffer();
        
        if ( gubun.equals("A")){    	
    	
	    	sql.append("\n	SELECT ODR.CDHD_ID, ODR.JUMIN_NO, CODE.GOLF_CMMN_CODE_NM GRD_NM, ODR.HG_NM, ODR.EMAIL_ID	\n");
			sql.append("\t	    , ODR.HP_DDD_NO, ODR.HP_TEL_HNO, ODR.HP_TEL_SNO	\n");
			sql.append("\t	    , ODR.RECEIVER_TEL1, ODR.RECEIVER_TEL2, ODR.RECEIVER_TEL3	\n");
			sql.append("\t	    , GDS.GDS_NM, TO_CHAR(TO_DATE(SUBSTR(ODR.ODR_ATON,1,8)),'YYYY-MM-DD')ODR_DATE	\n");
			sql.append("\t	    , GDS.BRND_NM, ODR.ODR_DTL_CLSS, ODR.ODR_AMT, ODR.ACPT_QTY	\n");
			sql.append("\t	    , ODR.DLV_YN, ODR.ODR_STAT_CLSS, ODR.HDLV_MSG_CTNT, OPT.SGL_LST_ITM_DTL_CTNT OPT_NM	\n");
			sql.append("\t	    , ODR.CONG_MSG_CTNT, ODR.DLV_PL_ZP, ODR.DLV_PL_DONG_OVR_ADDR, ODR.DLV_PL_DONG_BLW_ADDR	\n");			
			sql.append("\t	    , PAY.STTL_STAT_CLSS	\n");
			sql.append("\t	FROM BCDBA.TBGLUGODRCTNT ODR	\n");
			sql.append("\t	    LEFT JOIN BCDBA.TBGGOLFCDHD MEM ON ODR.CDHD_ID=MEM.CDHD_ID	\n");
			sql.append("\t	    LEFT JOIN BCDBA.TBGGOLFCDHDCTGOMGMT GRD ON MEM.CDHD_CTGO_SEQ_NO=GRD.CDHD_CTGO_SEQ_NO	\n");
			sql.append("\t	    LEFT JOIN BCDBA.TBGCMMNCODE CODE ON GRD.CDHD_SQ2_CTGO=CODE.GOLF_CMMN_CODE AND CODE.GOLF_CMMN_CLSS='0005'	\n");
			sql.append("\t	    JOIN BCDBA.TBGDS GDS ON ODR.GDS_CODE=GDS.GDS_CODE	\n");
			sql.append("\t	    LEFT JOIN BCDBA.TBSGLGDS OPT ON ODR.DC_RT=SGL_LST_ITM_CODE AND OPT.GDS_CODE=ODR.GDS_CODE	\n");
			sql.append("\t	    LEFT JOIN BCDBA.TBGSTTLMGMT PAY ON ODR.ODR_NO=PAY.ODR_NO	\n");
			sql.append("\t	WHERE ODR.ODR_NO=?	\n");
	
        }else {

	    	sql.append("\n	SELECT ODR.CDHD_ID, ODR.JUMIN_NO, CODE.GOLF_CMMN_CODE_NM GRD_NM, ODR.HG_NM, ODR.EMAIL_ID	\n");
			sql.append("\t	    , ODR.HP_DDD_NO, ODR.HP_TEL_HNO, ODR.HP_TEL_SNO	\n");
			sql.append("\t	    , ODR.RECEIVER_TEL1, ODR.RECEIVER_TEL2, ODR.RECEIVER_TEL3	\n");
			sql.append("\t	    , TO_CHAR(TO_DATE(SUBSTR(ODR.ODR_ATON,1,8)),'YYYY-MM-DD')ODR_DATE	\n");
			sql.append("\t	    , ODR.ODR_DTL_CLSS, ODR.ODR_AMT, ODR.ACPT_QTY	\n");
			sql.append("\t	    , ODR.DLV_YN, ODR.ODR_STAT_CLSS, ODR.HDLV_MSG_CTNT  \n");
			sql.append("\t	    , ODR.CONG_MSG_CTNT, ODR.DLV_PL_ZP, ODR.DLV_PL_DONG_OVR_ADDR, ODR.DLV_PL_DONG_BLW_ADDR	\n");			
			sql.append("\t	    , PAY.STTL_STAT_CLSS, PAY.INS_MCNT	\n");
			sql.append("\t	    , DECODE(STTL_MTHD_CLSS, '0001', '비씨카드', '0002', '복합결제', STTL_MINS_NM) SMC	\n");
			sql.append("\t	    , DECODE (GDS_CODE, '2011040101','보이스캐디','2011040102','골프퍼팅3홀컵 + 골프퍼팅매트세트','2011040103','골프버디보이스') PRODUCTNAME	\n");
			sql.append("\t	FROM BCDBA.TBGLUGODRCTNT ODR	\n");
			sql.append("\t	    LEFT JOIN BCDBA.TBGGOLFCDHD MEM ON ODR.CDHD_ID=MEM.CDHD_ID	\n");
			sql.append("\t	    LEFT JOIN BCDBA.TBGGOLFCDHDCTGOMGMT GRD ON MEM.CDHD_CTGO_SEQ_NO=GRD.CDHD_CTGO_SEQ_NO	\n");
			sql.append("\t	    LEFT JOIN BCDBA.TBGCMMNCODE CODE ON GRD.CDHD_SQ2_CTGO=CODE.GOLF_CMMN_CODE AND CODE.GOLF_CMMN_CLSS='0005'	\n");
			sql.append("\t	    LEFT JOIN BCDBA.TBGSTTLMGMT PAY ON ODR.ODR_NO=PAY.ODR_NO	\n");			
			sql.append("\t	WHERE ODR.ODR_NO=?	\n");
		
        }
		
		return sql.toString();
    }
    

}

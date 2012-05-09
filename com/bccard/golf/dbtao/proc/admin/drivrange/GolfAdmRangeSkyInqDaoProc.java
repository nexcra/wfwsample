/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmRangeSkyInqDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 드림 골프레인지 신청(sky72) 상세보기
*   적용범위  : golf
*   작성일자  : 2009-05-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.drivrange;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Topn
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfAdmRangeSkyInqDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmRangeSkyInqDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmRangeSkyInqDaoProc() {}	

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

			// 회원통합테이블 관련 수정사항 진행
			//조회 ----------------------------------------------------------			
			String sql = this.getSelectQuery();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("RSVT_SQL_NO"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					
					String str_appr_opion = rs.getString("APPR_OPION");
					result.addString("RSVT_SQL_NO" 		,rs.getString("GOLF_SVC_RSVT_NO") );
					
					String rsvt_able_date = rs.getString("RSVT_ABLE_DATE");
					if (!GolfUtil.isNull(rsvt_able_date)) rsvt_able_date = DateUtil.format(rsvt_able_date, "yyyyMMdd", "yyyy년 MM월 dd일");
					result.addString("RSVT_DATE"		,rsvt_able_date);
					
					result.addString("RSVT_TIME" 		,rs.getString("RSVT_TIME") );
					result.addString("HAN_NM" 			,rs.getString("HG_NM") );
					result.addString("GF_ID" 			,rs.getString("CDHD_ID") );
					result.addString("JUMIN"			,rs.getString("JUMIN") );
					result.addString("EMAIL"			,rs.getString("EMAIL1") );
					result.addString("PHONE"			,rs.getString("PHONE") );
					result.addString("HP_DDD_NO"		,rs.getString("HP_DDD_NO") );
					result.addString("HP_TEL_HNO"		,rs.getString("HP_TEL_HNO") );
					result.addString("HP_TEL_SNO"		,rs.getString("HP_TEL_SNO") );
					result.addString("REG_ATON"			,rs.getString("REG_ATON") );
					result.addString("CNCL_ATON"		,rs.getString("CNCL_ATON") );
					result.addString("RSVT_YN"			,rs.getString("RSVT_YN") );
					result.addString("ATD_YN"			,rs.getString("ATTD_YN") );
					result.addString("APPR_OPION"		,str_appr_opion);
					result.addString("TOTAL_CNT"		,rs.getString("TOTAL_CNT") );
					
					if(!"".equals(str_appr_opion) && str_appr_opion != null){
						result.addString("APPR_OPION_SIZE",""+str_appr_opion.length());
						
					}else{
						result.addString("APPR_OPION_SIZE","0");
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
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
        
        sql.append("\n SELECT														");
		sql.append("\n 	TGR.GOLF_SVC_RSVT_NO,  				");
		sql.append("\n 	(CASE WHEN LENGTH(TRIM(TGRD.RSVT_ABLE_DATE ))<8 THEN TO_CHAR(TO_DATE(TGRD.RSVT_ABLE_DATE,'YYYYMM'),'YYYY-MM')  	");
		sql.append("\n		ELSE  TO_CHAR(TO_DATE(TGRD.RSVT_ABLE_DATE,'YYYYMMDD'),'YYYY-MM-DD') END)AS RSVT_ABLE_DATE	,				");
		sql.append("\n 	TO_CHAR (TO_DATE (TGRT.RSVT_STRT_TIME, 'HH24MI'), 'HH24:MI') || '~' || TO_CHAR (TO_DATE (TGRT.RSVT_END_TIME, 'HH24MI'), 'HH24:MI') RSVT_TIME,	 ");
		sql.append("\n 	TGU.HG_NM, TGU.CDHD_ID,	TGU.APPR_OPION, 		 ");
		sql.append("\n 	(SUBSTR(TGU.JUMIN_NO, 1, 6) ||'-*******')AS JUMIN,	 ");
		sql.append("\n 	TGU.EMAIL AS EMAIL1, TGU.PHONE,							 ");
		sql.append("\n 	TGR.HP_DDD_NO, TGR.HP_TEL_HNO, TGR.HP_TEL_SNO,	 ");
		sql.append("\n 	TO_CHAR (TO_DATE (TGR.REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD HH24:MI') REG_ATON,	 ");
		sql.append("\n 	TO_CHAR (TO_DATE (TGR.CNCL_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD HH24:MI') CNCL_ATON,	 ");
		sql.append("\n 	DECODE (TGR.RSVT_YN, 'Y', '예약', 'N', '취소') RSVT_YN, TGR.ATTD_YN,	 ");
		sql.append("\n	(SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT  WHERE CDHD_ID = TGR.CDHD_ID AND SUBSTR(GOLF_SVC_RSVT_NO,5,1)='D')AS TOTAL_CNT");
		sql.append("\n FROM BCDBA.TBGRSVTMGMT TGR, BCDBA.TBGRSVTABLESCDMGMT TGRD, BCDBA.TBGRSVTABLEBOKGTIMEMGMT TGRT, BCDBA.TBGGOLFCDHD TGU	");
		sql.append("\n WHERE TGU.CDHD_ID = TGR.CDHD_ID(+)	");
		sql.append("\n AND TGR.RSVT_ABLE_BOKG_TIME_SEQ_NO = TGRT.RSVT_ABLE_BOKG_TIME_SEQ_NO	");		
		sql.append("\n AND TGRD.RSVT_ABLE_SCD_SEQ_NO = TGRT.RSVT_ABLE_SCD_SEQ_NO	");		
		sql.append("\n AND TGR.GOLF_SVC_RSVT_NO = ?	");
		return sql.toString();
    }
}

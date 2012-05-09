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
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.booking.par;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.Reader;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;

/******************************************************************************
 * Golf
 * @author	미디어포스 
 * @version	1.0
 ******************************************************************************/
public class GolfBkParGrViewDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfBkPreGrViewDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfBkParGrViewDaoProc() {}	

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
			// 이미지 경로지정
			String mapDir 				= AppConfig.getAppProperty("CONT_IMG_URL_MAPPING_DIR");	
			mapDir = mapDir.replaceAll("\\.\\.","");
			String imgPath 				= AppConfig.getAppProperty("BK_GREEN");	
			imgPath = imgPath.replaceAll("\\.\\.","");
			String realPath				= mapDir + imgPath + "/";
						 
			//조회 ----------------------------------------------------------			
			String sql = this.getSelectQuery();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(++idx, data.getLong("SEQ_NO"));
			rs = pstmt.executeQuery();
			
			
			if(rs != null) {
				while(rs.next())  {

					result.addLong("SEQ_NO" 		,rs.getLong("SEQ_NO") );
					result.addString("SORT" 		,rs.getString("SORT") );
					result.addString("GR_NM" 		,rs.getString("GR_NM") );
					result.addString("GR_INFO" 		,rs.getString("GR_INFO") );
					result.addString("BANNER" 		,rs.getString("BANNER") );
					result.addString("GR_URL" 		,rs.getString("GR_URL") );
					result.addString("GR_ADDR" 		,rs.getString("GR_ADDR") );
					result.addString("MAP_NM" 		,rs.getString("MAP_NM") );
					result.addString("COURSE" 		,rs.getString("COURSE") );
					
					result.addString("SUBEQUIP" 	,rs.getString("SUBEQUIP") );
					result.addLong("DEL_LIMIT" 		,rs.getLong("DEL_LIMIT") );
					result.addLong("PER_LIMIT" 		,rs.getLong("PER_LIMIT") );

					result.addString("PIC1_DESC" 	,rs.getString("PIC1_DESC") );
					result.addString("PIC2_DESC" 	,rs.getString("PIC2_DESC") );
					result.addString("PIC3_DESC" 	,rs.getString("PIC3_DESC") );
					result.addString("PIC4_DESC" 	,rs.getString("PIC4_DESC") );
					result.addString("PIC5_DESC" 	,rs.getString("PIC5_DESC") );
					result.addString("PIC6_DESC" 	,rs.getString("PIC6_DESC") );
					result.addString("GR_NOTI" 		,rs.getString("GR_NOTI") );
					

					result.addString("BANNER" 		,rs.getString("BANNER") );
					result.addString("MAP_NM" 		,rs.getString("MAP_NM") );
					result.addString("PIC1" 		,rs.getString("PIC1") );
					result.addString("PIC2" 		,rs.getString("PIC2") );
					result.addString("PIC3" 		,rs.getString("PIC3") );
					result.addString("PIC4" 		,rs.getString("PIC4") );
					result.addString("PIC5" 		,rs.getString("PIC5") );
					result.addString("PIC6" 		,rs.getString("PIC6") );
					
					result.addString("SRC_BANNER" 		,realPath + rs.getString("BANNER") );
					result.addString("SRC_MAP_NM" 		,realPath + rs.getString("MAP_NM") );
					result.addString("SRC_PIC1" 		,realPath + rs.getString("PIC1") );
					result.addString("SRC_PIC2" 		,realPath + rs.getString("PIC2") );
					result.addString("SRC_PIC3" 		,realPath + rs.getString("PIC3") );
					result.addString("SRC_PIC4" 		,realPath + rs.getString("PIC4") );
					result.addString("SRC_PIC5" 		,realPath + rs.getString("PIC5") );
					result.addString("SRC_PIC6" 		,realPath + rs.getString("PIC6") );
										
					
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

		sql.append("\n SELECT");
		sql.append("\t  AFFI_GREEN_SEQ_NO AS SEQ_NO, AFFI_FIRM_CLSS AS SORT, GREEN_NM AS GR_NM, GREEN_ID AS GR_ID, 	\n");
		sql.append("\t  BANNER_IMG AS BANNER, GREEN_HPGE_URL AS GR_URL, GREEN_EXPL AS GR_INFO, DTL_ADDR AS GR_ADDR, 	\n");
		sql.append("\t  OLM_IMG AS MAP_NM, CURS_SCAL_INFO AS COURSE, INCI_FACI_CTNT AS SUBEQUIP, BOKG_CNCL_ABLE_TRM AS DEL_LIMIT, 	\n");
		sql.append("\t  BOKG_TIME_COLL_TRM AS PER_LIMIT, GALR_1_IMG_EXPL AS PIC1_DESC, GALR_1_IMG AS PIC1, GALR_2_IMG_EXPL AS PIC2_DESC, 	\n");
		sql.append("\t  GALR_2_IMG AS PIC2, GALR_3_IMG_EXPL AS PIC3_DESC, GALR_3_IMG AS PIC3, GALR_4_IMG_EXPL AS PIC4_DESC, GALR_4_IMG AS PIC4, 	\n");
		sql.append("\t  GALR_5_IMG_EXPL AS PIC5_DESC, GALR_5_IMG AS PIC5, GALR_6_IMG_EXPL AS PIC6_DESC, GALR_6_IMG AS PIC6, CAUT_MTTR_CTNT AS GR_NOTI 	\n");
		sql.append("\n FROM");
		sql.append("\n BCDBA.TBGAFFIGREEN");
		sql.append("\n WHERE AFFI_GREEN_SEQ_NO = ?	");		
		
		return sql.toString();
    }
}

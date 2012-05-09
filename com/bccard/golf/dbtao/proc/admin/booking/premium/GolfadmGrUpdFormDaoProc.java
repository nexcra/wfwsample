/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmGrUpdFormDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 부킹 골프장 수정 폼 처리
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.booking.premium;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.Reader;

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
 ******************************************************************************/
public class GolfadmGrUpdFormDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfadmGrUpdFormDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfadmGrUpdFormDaoProc() {}	

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

					result.addLong("SEQ_NO" 				,rs.getLong("SEQ_NO") );
					result.addString("SORT" 				,rs.getString("SORT") );
					result.addString("GR_NM" 				,rs.getString("GR_NM") );
					result.addString("RL_GREEN_NM" 			,rs.getString("RL_GREEN_NM") );
					result.addString("GR_ID" 				,rs.getString("GR_ID") );
					result.addString("GR_URL" 				,rs.getString("GR_URL") );
					result.addString("GR_ADDR" 				,rs.getString("GR_ADDR") );
					result.addString("COURSE" 				,rs.getString("COURSE") );
					
					result.addString("SUBEQUIP" 			,rs.getString("SUBEQUIP") );
					result.addLong("DEL_LIMIT" 				,rs.getLong("DEL_LIMIT") );
					result.addLong("PER_LIMIT" 				,rs.getLong("PER_LIMIT") );
					result.addString("REG_MGR_SEQ_NO" 		,rs.getString("REG_MGR_SEQ_NO") );
					result.addString("CORR_MGR_SEQ_NO" 		,rs.getString("CORR_MGR_SEQ_NO") );
					result.addString("PIC1_DESC" 			,rs.getString("PIC1_DESC") );
					
					result.addString("PIC2_DESC" 			,rs.getString("PIC2_DESC") );
					result.addString("PIC3_DESC" 			,rs.getString("PIC3_DESC") );
					result.addString("PIC4_DESC" 			,rs.getString("PIC4_DESC") );
					result.addString("PIC5_DESC" 			,rs.getString("PIC5_DESC") );
					result.addString("PIC6_DESC" 			,rs.getString("PIC6_DESC") );
					
					String gr_noti = rs.getString("GR_NOTI");
					gr_noti = GolfUtil.replace(gr_noti, "\n","<br>");
					result.addString("GR_NOTI" 				,gr_noti);
					
					result.addString("GR_INFO" 				,rs.getString("GR_INFO") );
					result.addString("MAX_ACPT_PNUM" 		,rs.getString("MAX_ACPT_PNUM") );		// 일일최대접대인원

					result.addString("SRC_BANNER" 			,realPath + rs.getString("BANNER") );
					result.addString("SRC_MAP_NM" 			,realPath + rs.getString("MAP_NM"));
					result.addString("SRC_PIC1" 			,realPath + rs.getString("PIC1") );
					result.addString("SRC_PIC2" 			,realPath + rs.getString("PIC2") );
					result.addString("SRC_PIC3" 			,realPath + rs.getString("PIC3") );
					result.addString("SRC_PIC4" 			,realPath + rs.getString("PIC4") );
					result.addString("SRC_PIC5" 			,realPath + rs.getString("PIC5") );
					result.addString("SRC_PIC6" 			,realPath + rs.getString("PIC6") );
					result.addString("SRC_MAIN_BANNER_IMG" 	,realPath + rs.getString("MAIN_BANNER_IMG") );
					
					result.addString("BANNER" 				,rs.getString("BANNER") );
					result.addString("MAP_NM" 				,rs.getString("MAP_NM"));
					result.addString("PIC1" 				,rs.getString("PIC1") );
					result.addString("PIC2" 				,rs.getString("PIC2") );
					result.addString("PIC3" 				,rs.getString("PIC3") );
					result.addString("PIC4" 				,rs.getString("PIC4") );
					result.addString("PIC5" 				,rs.getString("PIC5") );
					result.addString("PIC6" 				,rs.getString("PIC6") );
					result.addString("MAIN_BANNER_IMG" 		,rs.getString("MAIN_BANNER_IMG") );
					
					result.addString("MAIN_EPS_YN" 			,rs.getString("MAIN_EPS_YN") );
					result.addString("CO_NM" 				,rs.getString("CO_NM") );
					

					Reader reader = null;
					StringBuffer bufferSt = new StringBuffer();
					reader = rs.getCharacterStream("COURSE_INFO");
					if( reader != null )  {
						char[] buffer = new char[1024]; 
						int byteRead; 
						while((byteRead=reader.read(buffer,0,1024))!=-1) 
							bufferSt.append(buffer,0,byteRead); 
						reader.close();
					}
					result.addString("COURSE_INFO", bufferSt.toString());
					
					
					reader = null;
					bufferSt = new StringBuffer();
					reader = rs.getCharacterStream("CH_INFO");
					if( reader != null )  {
						char[] buffer = new char[1024]; 
						int byteRead; 
						while((byteRead=reader.read(buffer,0,1024))!=-1) 
							bufferSt.append(buffer,0,byteRead); 
						reader.close();
					}
					result.addString("CH_INFO", bufferSt.toString());
					
					
					
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
		sql.append("\t  AFFI_GREEN_SEQ_NO AS SEQ_NO, AFFI_FIRM_CLSS AS SORT, GREEN_NM AS GR_NM, RL_GREEN_NM, GREEN_ID AS GR_ID, 	\n");
		sql.append("\t  BANNER_IMG AS BANNER, GREEN_HPGE_URL AS GR_URL, GREEN_EXPL AS GR_INFO, DTL_ADDR AS GR_ADDR, 	\n");
		sql.append("\t  OLM_IMG AS MAP_NM, CURS_SCAL_INFO AS COURSE, CURS_INTD_CTNT AS COURSE_INFO, INCI_FACI_CTNT AS SUBEQUIP, 	\n");
		sql.append("\t  BOKG_CNCL_ABLE_TRM AS DEL_LIMIT, BOKG_TIME_COLL_TRM AS PER_LIMIT, REG_MGR_ID AS REG_MGR_SEQ_NO, CHNG_MGR_ID AS CORR_MGR_SEQ_NO, 	\n");
		sql.append("\t  TO_CHAR(TO_DATE(SUBSTR(REG_ATON,1,8)),'YYYY-MM-DD') REG_DATE, TO_CHAR(TO_DATE(SUBSTR(CHNG_ATON,1,8)),'YYYY-MM-DD') CORR_DATE, 	\n");
		sql.append("\t  GALR_1_IMG_EXPL AS PIC1_DESC, GALR_1_IMG AS PIC1, GALR_2_IMG_EXPL AS PIC2_DESC, GALR_2_IMG AS PIC2, 	\n");
		sql.append("\t  GALR_3_IMG_EXPL AS PIC3_DESC, GALR_3_IMG AS PIC3, GALR_4_IMG_EXPL AS PIC4_DESC, GALR_4_IMG AS PIC4, 	\n");
		sql.append("\t  GALR_5_IMG_EXPL AS PIC5_DESC, GALR_5_IMG AS PIC5, GALR_6_IMG_EXPL AS PIC6_DESC, GALR_6_IMG AS PIC6, 	\n");
		sql.append("\t  CAUT_MTTR_CTNT AS GR_NOTI, GREEN_CHRG_INFO AS CH_INFO, MAX_ACPT_PNUM, MAIN_BANNER_IMG, MAIN_EPS_YN, CO_NM 	\n");
		sql.append("\n FROM");
		sql.append("\n BCDBA.TBGAFFIGREEN");
		sql.append("\n WHERE AFFI_GREEN_SEQ_NO = ?	");		
		
		return sql.toString();
    }
}

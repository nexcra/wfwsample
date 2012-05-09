/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmSLessonListDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 BC Golf 이벤트 
*   적용범위  : golf
*   작성일자  : 2009-05-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfAdmSLessonListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmEvntBkListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmSLessonListDaoProc() {}	
		
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
		String sql = "";

		try {
			conn = context.getDbConnection("default", null);
			
			//조회 ----------------------------------------------------------
	
			String search_sel		= data.getString("search_sel");
			String search_word		= data.getString("search_word");
			String sevnt_from		= data.getString("sevnt_from");
			String sevnt_to			= data.getString("sevnt_to");
			String search_yn		= data.getString("search_yn");
			long page_no			= data.getLong("page_no");
			long page_size			= data.getLong("page_size");
			boolean eof 			= false;
			
			int pidx = 0;
			
			sql = this.getSelectQuery(search_sel, search_word, search_yn, sevnt_from, sevnt_to);
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(++pidx, page_size );
			pstmt.setLong(++pidx, page_no);
			if(!"".equals(search_yn) && !"ALL".equals(search_yn)){
				pstmt.setString(++pidx, search_yn);
			}
			if(!"".equals(search_word)){
				if(!"".equals(search_sel)){
					if("ALL".equals(search_sel)){
						pstmt.setString(++pidx,"%"+ search_word +"%");
						pstmt.setString(++pidx,"%"+ search_word +"%");
					}else{
						pstmt.setString(++pidx,"%"+ search_word +"%");
					}
				}
			}
			if(!"".equals(sevnt_from) && !"".equals(sevnt_to)){
				pstmt.setString(++pidx, sevnt_to);
				pstmt.setString(++pidx, sevnt_from);
			}
			pstmt.setLong(++pidx, page_no );
            rs = pstmt.executeQuery();
            int art_num_seq = 0;
            
            while(rs.next()){
            	if(!eof) result.addString("RESULT", "00");
            		result.addString("RNUM", 		rs.getString("RNUM"));
                    result.addLong("SEQ_NO", 		rs.getLong("EVNT_SEQ_NO"));
                    result.addString("EVNT_NM",	 	rs.getString("EVNT_NM"));
                    result.addString("EVNT_ST", 	rs.getString("EVNT_STRT_DATE"));
                    result.addString("EVNT_EN", 	rs.getString("EVNT_END_DATE"));
                    result.addString("DISP_YN", 	rs.getString("BLTN_YN"));
                    result.addString("STATUS", 		rs.getString("STATUS"));
                    result.addString("PE_NUM", 		rs.getString("RCRU_PE_ORG_NUM"));;
                    result.addString("BNFT_EXPL", 	rs.getString("EVNT_BNFT_EXPL"));
                    result.addString("TOTAL_CNT", 	rs.getString("TOT_CNT"));
                    result.addString("CURR_PAGE", 	rs.getString("PAGE"));
                    result.addInt("LIST_NO", 		rs.getInt("ART_NUM")-art_num_seq);
                    
                    eof = true;
                    art_num_seq++;
                }

            if(!eof) result.addString("RESULT", "01");
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}
	
	/** *************************************************************************************************
	 *  List Query를 리턴한다.
	 ************************************************************************************************** **/
	 private String getSelectQuery(String search_sel, String search_word, String search_yn, 
			 String sevnt_from, String sevnt_to) throws Exception{
		 StringBuffer sql = new StringBuffer();
		 
		 sql.append("\n SELECT * 																					");
         sql.append("\n FROM (SELECT ROWNUM RNUM																	");
         sql.append("\n   		 ,EVNT_SEQ_NO 																		");
         sql.append("\n   		 ,EVNT_NM																			");
         sql.append("\n   		 ,EVNT_STRT_DATE  																	");
         sql.append("\n   		 ,EVNT_END_DATE  																	");
         sql.append("\n     	 ,RCRU_PE_ORG_NUM																	");
         sql.append("\n   		 ,BLTN_YN  																			");
         sql.append("\n    		 ,EVNT_BNFT_EXPL																	");
         sql.append("\n    		 ,(CASE WHEN STATUS ='0' THEN '이전' WHEN STATUS='1' THEN '진행' WHEN STATUS='2' THEN '마감' ELSE '-' END)AS STATUS ");
         sql.append("\n    		 ,CEIL(ROWNUM/?) AS PAGE 															");
         sql.append("\n    		 ,MAX(RNUM) OVER() TOT_CNT 															");
         sql.append("\n    		 ,((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM   										");
         sql.append("\n   	 FROM (SELECT ROWNUM RNUM 																");
         sql.append("\n     			,EVNT_SEQ_NO     															");
         sql.append("\n     			,EVNT_NM 																	");
         sql.append("\n     			,BLTN_YN 																	");         
         sql.append("\n     			,RCRU_PE_ORG_NUM															");
         sql.append("\n    				,EVNT_BNFT_EXPL																");
         sql.append("\n     			,TO_CHAR(TO_DATE(EVNT_STRT_DATE), 'YYYY-MM-DD') EVNT_STRT_DATE  			");
         sql.append("\n     			,TO_CHAR(TO_DATE(EVNT_END_DATE), 'YYYY-MM-DD') EVNT_END_DATE				");
         sql.append("\n     			,(CASE WHEN TO_NUMBER(EVNT_STRT_DATE) > TO_NUMBER(TO_CHAR(SYSDATE,'YYYYMMDD')) THEN '0'		");
         sql.append("\n     				   WHEN TO_NUMBER(EVNT_STRT_DATE) <= TO_NUMBER(TO_CHAR(SYSDATE,'YYYYMMDD')) AND TO_NUMBER(TO_CHAR(SYSDATE,'YYYYMMDD')) <= TO_NUMBER(EVNT_END_DATE) THEN '1'	");
         sql.append("\n     				   WHEN TO_NUMBER(EVNT_END_DATE) < TO_NUMBER(TO_CHAR(SYSDATE,'YYYYMMDD')) THEN '2' END)AS STATUS		");
         sql.append("\n    		  FROM  BCDBA.TBGEVNTMGMT															");
         sql.append("\n     	  WHERE EVNT_CLSS = '0003'  														");
         
         if(!"".equals(search_yn) && !"ALL".equals(search_yn)){
         	sql.append("\n          AND BLTN_YN = ? 																");
         }
		 if(!"".equals(search_word)){
			if(!"".equals(search_sel)){
				if("ALL".equals(search_sel)){
					 sql.append("\n          AND (	EVNT_NM LIKE  ?  OR EVNT_BNFT_EXPL LIKE  ?	 ) 					");
				}else{
					sql.append("\n          AND  "+search_sel+" LIKE  ? 											");
				}
			}
		 }
         if(!"".equals(sevnt_from) && !"".equals(sevnt_to)){
        	 sql.append("\n          AND 	( TO_DATE(EVNT_STRT_DATE,'YYYY-MM-DD') <= TO_DATE( ? ) AND TO_DATE(EVNT_END_DATE,'YYYY-MM-DD') >= TO_DATE(?)	)	");
         }
         sql.append("\n     	ORDER BY EVNT_SEQ_NO DESC 															");
         sql.append("\n   			 ) 																				");
         sql.append("\n  ORDER BY RNUM 																				");
         sql.append("\n 	 ) 																						");
         sql.append("\n WHERE PAGE = ? 																				");
		 
		 return sql.toString();
	 }
		
}
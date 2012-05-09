/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfTopGolfCardRoundingAfterListDaoProc
*   작성자    : 장성재
*   내용      : 탑골프카드 라운딩 후기 리스트
*   적용범위  : golf
*   작성일자  : 2010-11-03
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.booking.premium;

import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	
 * @version	1.0
 ******************************************************************************/
public class GolfTopGolfCardRoundingAfterListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfTopGolfCardRoundingAfterListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfTopGolfCardRoundingAfterListDaoProc() {}	

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
			
			//조회 ----------------------------------------------------------
			String searchSel		= data.getString("searchSel");
			String searchWord		= data.getString("searchWord");
			long pageNo             = data.getLong("pageNo"); // 페이지번호
			long recordSize         = data.getLong("recordSize"); // 페이지당출력수

			if (pageNo <= 0) {
				pageNo = 1L;
			}
			long startRecord = (pageNo-1L) * recordSize + 1L;
			long endRecord = pageNo * recordSize;

debug("searchSel :" +searchSel  );	
debug("searchWord :" +searchWord  );	
debug("pageNo :" +pageNo  );	
debug("recordSize :" +recordSize  );	
debug("startRecord :" +startRecord  );	
debug("endRecord :" +endRecord  );	

			
			String sql = this.getSelectQuery(searchSel, searchWord);   

debug("sql :" +sql  );	

			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, data.getString("boardCd"));

debug("boardCd :" +data.getString("boardCd")  );	
			
			
			if (!GolfUtil.isNull(searchWord)) {
				if (searchSel.equals("ALL")) {
					pstmt.setString(++idx, "%"+searchWord+"%");
					pstmt.setString(++idx, "%"+searchWord+"%");
					pstmt.setString(++idx, "%"+searchWord+"%");		
				} else {
					pstmt.setString(++idx, "%"+searchWord+"%");
				}
			}
			pstmt.setString(++idx, data.getString("boardCd"));
			if (!GolfUtil.isNull(searchWord)) {
				if (searchSel.equals("ALL")) {
					pstmt.setString(++idx, "%"+searchWord+"%");
					pstmt.setString(++idx, "%"+searchWord+"%");
					pstmt.setString(++idx, "%"+searchWord+"%");		
				} else {
					pstmt.setString(++idx, "%"+searchWord+"%");
				}
			}

			pstmt.setLong(++idx, endRecord);
			pstmt.setLong(++idx, startRecord);
			pstmt.setLong(++idx, endRecord);
			
			rs = pstmt.executeQuery();

debug("rs :" +"before"  );	

			
			if(rs != null) {			 

debug("rs :" +"after"  );	

				while(rs.next())  {	

debug("rs.next() :" +"after"  );	
					
					if(rs.getString("SCOR_APPL_YN") == null){
	                	result.addString("SCOR_APPL_YN","0");
	                }else{
	                    result.addString("SCOR_APPL_YN"  ,rs.getString("SCOR_APPL_YN")  );
	                }
			
                    result.addLong  ("BOARD_NO"      ,rs.getLong  ("BOARD_NO")      );
                    result.addString("BOARD_SUBJ"    ,rs.getString("BOARD_SUBJ")    );
                    result.addString("HOT_INFO_YN"   ,rs.getString("HOT_INFO_YN")   );
                    result.addLong  ("READ_CNT"      ,rs.getLong  ("READ_CNT")      );
                    result.addString("LIST_INQ_CLSS" ,rs.getString("LIST_INQ_CLSS") );  
                    result.addString("LIST_IMG_PATH" ,rs.getString("LIST_IMG_PATH") ); 
                    result.addLong  ("SORT_KEY"      ,rs.getLong  ("SORT_KEY")      );
                    result.addLong  ("REF_NO"        ,rs.getLong  ("REF_NO")        );
                    result.addLong  ("ANS_STG"       ,rs.getLong  ("ANS_STG")       );
                    result.addLong  ("ANS_LEV"       ,rs.getLong  ("ANS_LEV")       );
                    result.addString("REG_DATE"      ,rs.getString("REG_DATE")      );
                    result.addString("REG_DATE2"     ,rs.getString("REG_DATE2")     );
                    result.addString("TODAY"         ,rs.getString("TODAY")         );
                    result.addString("REG_NM"        ,rs.getString("REG_NM")        );
                    result.addString("REG_IP"        ,rs.getString("REG_IP")        );
                    result.addString("ATC_FILE_YN"   ,rs.getString("ATC_FILE_YN")   );
                    result.addString("NEWYN"         ,rs.getString("NEWYN")         );
                    result.addString  ("REG_ACCOUNT" ,rs.getString("REG_ACCOUNT")   ); 
                    result.addLong  ("RECORD_CNT"    ,rs.getLong  ("RECORD_CNT")    );
                    result.addLong  ("ROW_NUM"       ,rs.getLong  ("ROW_NUM")       );

					result.addString("RESULT", "00"); //정상결과   
					
debug( "BOARD_NO"      +" : " + rs.getLong  ("BOARD_NO"       ));
debug( "BOARD_SUBJ"    +" : " + rs.getString("BOARD_SUBJ"     ));
debug( "HOT_INFO_YN"   +" : " + rs.getString("HOT_INFO_YN"    ));
debug( "READ_CNT"      +" : " + rs.getLong  ("READ_CNT"       ));
debug( "LIST_INQ_CLSS" +" : " + rs.getString("LIST_INQ_CLSS"  ));  
debug( "LIST_IMG_PATH" +" : " + rs.getString("LIST_IMG_PATH"  )); 
debug( "SORT_KEY"      +" : " + rs.getLong  ("SORT_KEY"       ));
debug( "REF_NO"        +" : " + rs.getLong  ("REF_NO"         ));
debug( "ANS_STG"       +" : " + rs.getLong  ("ANS_STG"        ));
debug( "ANS_LEV"       +" : " + rs.getLong  ("ANS_LEV"        ));
debug( "REG_DATE"      +" : " + rs.getString("REG_DATE"       ));
debug( "REG_DATE2"     +" : " + rs.getString("REG_DATE2"      ));
debug( "TODAY"         +" : " + rs.getString("TODAY"          ));
debug( "REG_NM"        +" : " + rs.getString("REG_NM"         ));
debug( "REG_IP"        +" : " + rs.getString("REG_IP"         ));
debug( "ATC_FILE_YN"   +" : " + rs.getString("ATC_FILE_YN"    ));
debug( "NEWYN"         +" : " + rs.getString("NEWYN"          ));
debug( "REG_ACCOUNT"   +" : " + rs.getString  ("REG_ACCOUNT"    )); 
debug( "RECORD_CNT"    +" : " + rs.getLong  ("RECORD_CNT"     ));
debug( "ROW_NUM"       +" : " + rs.getLong  ("ROW_NUM"        ));
					
					
				}
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");
			}

//debug("RESULT :" +result.getString("RESULT")  );	

			
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
    private String getSelectQuery(String search_sel, String search_word){
        StringBuffer sql = new StringBuffer();

        sql.append("\n SELECT * ");
        sql.append("\n FROM  (SELECT x.*, ");
        sql.append("\n               y.RECORD_CNT, ");
        sql.append("\n               ROWNUM ROW_NUM ");
        sql.append("\n        FROM  (SELECT a.BOARD_NO, ");  
        sql.append("\n                      a.BOARD_SUBJ, ");
        sql.append("\n                      a.HOT_INFO_YN, ");
        sql.append("\n                      a.READ_CNT, ");
        sql.append("\n                      a.SCOR_APPL_YN, ");
        sql.append("\n                      a.LIST_INQ_CLSS, "); 
        sql.append("\n                      a.LIST_IMG_PATH, ");
        sql.append("\n                      a.SORT_KEY, ");  
        sql.append("\n                      a.REF_NO, "); 
        sql.append("\n                      a.ANS_STG, "); 
        sql.append("\n                      a.ANS_LEV, "); 
        sql.append("\n                      TO_CHAR(TO_DATE(a.REG_DATE, 'YYYYMMDDHH24MISS'), 'YYYY.MM.DD') REG_DATE, ");
        sql.append("\n                      TO_CHAR(TO_DATE(a.REG_DATE, 'YYYYMMDDHH24MISS'),'YYYYMMDD') REG_DATE2, ");
        sql.append("\n                      TO_CHAR(SYSDATE, 'YYYYMMDD') TODAY, "); 
        sql.append("\n                      a.REG_NM, "); 
        sql.append("\n                      a.REG_IP, ");
        sql.append("\n                      a.ATC_FILE_YN, ");
        sql.append("\n                      CASE WHEN TO_CHAR(SYSDATE,'YYYYMMDD') >= TO_CHAR(TO_DATE(REG_DATE,'YYYY-MM-DD HH24:MI:SS'),'YYYYMMDD') ");
        sql.append("\n                           AND  TO_CHAR(SYSDATE,'YYYYMMDD') <= TO_CHAR(TO_DATE(REG_DATE,'YYYY-MM-DD HH24:MI:SS'),'YYYYMMDD')+4 ");
        sql.append("\n                           THEN '<img src=\"/golf/img/booking/ico_new.gif\">' ");
        sql.append("\n                           ELSE '' ");
        sql.append("\n                      END AS NEWYN ");
        sql.append("\n                     ,NVL((SELECT COUNT(c.BOARD_NO) ");
        sql.append("\n                           FROM   BCDBA.TBGFBRDADD c ");
        sql.append("\n                           WHERE  c.BOARD_NO=a.BOARD_NO), 0) ADD_CNT "); 
        sql.append("\n                     ,NVL(uc.ACCOUNT, ' ') REG_ACCOUNT  "); 
        sql.append("\n               FROM   BCDBA.TBGFBOARD a  "); 
        sql.append("\n                     ,BCDBA.UCUSRINFO uc "); 
        sql.append("\n               WHERE  a.BOARD_CD = ? ");

		if (!GolfUtil.isNull(search_word)) {
			if (search_sel.equals("ALL")) {
				sql.append("\n 				 AND (a.BOARD_SUBJ LIKE ? OR ");
				sql.append("\n 				      a.BOARD_TEXT LIKE ? OR ");	
				sql.append("\n 				      a.REG_NM LIKE ? )	");	
			} else {
				sql.append("\n 				 AND "+search_sel+" LIKE ?	");
			}
		}

        sql.append("\n               AND    uc.MEMID(+) = a.REG_NO ");
        sql.append("\n               ORDER BY a.REF_NO DESC, a.ANS_STG ASC ");
        sql.append("\n               ) x, ");
        sql.append("\n              (SELECT COUNT(*) RECORD_CNT ");
        sql.append("\n               FROM   BCDBA.TBGFBOARD a ");
        sql.append("\n                     ,BCDBA.UCUSRINFO uc ");
        sql.append("\n               WHERE  a.BOARD_CD = ? ");

		if (!GolfUtil.isNull(search_word)) {
			if (search_sel.equals("ALL")) {
				sql.append("\n 				 AND (a.BOARD_SUBJ LIKE ? OR ");
				sql.append("\n 				      a.BOARD_TEXT LIKE ? OR ");	
				sql.append("\n 				      a.REG_NM LIKE ? )	");	
			} else {
				sql.append("\n 				 AND "+search_sel+" LIKE ?	");
			}
		}
        	
        sql.append("\n               AND    uc.MEMID(+) = a.REG_NO ");
        sql.append("\n               ) y "); 
        sql.append("\n        WHERE ROWNUM <= ? ) ");
        sql.append("\n WHERE  ROW_NUM >= ? ");
        sql.append("\n AND    ROW_NUM <= ? ");

		return sql.toString();
    
    }
    
}

/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmMemBkScoreDaoProc
*   작성자    : (주)미디어포스 이경희
*   내용      : 관리자>회원리스트>상세(부킹점수)
*   적용범위  : golf
*   작성일자  : 2011-05-03
************************** 수정이력 ****************************************************************
*    일자    작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	미디어포스
 * @version	1.0
 ******************************************************************************/
public class GolfAdmMemBkScoreDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmMemListDaoProc 프로세스 생성자    
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmMemBkScoreDaoProc() {}	

	/**
	 * 탑골프회원인지 확인
	 * @param WaContext context
	 * @param HttpServletRequest request
	 * @param TaoDataSet data
	 * @return boolean
	 */
	public boolean execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		boolean result = false ;

		try {		
			conn = context.getDbConnection("default", null);	
			String cdhd_id		= data.getString("CDHD_ID");
			int cnt = 0;
			 
			//조회 ----------------------------------------------------------
			String sql = this.topGolfYN();
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, cdhd_id);
			rs = pstmt.executeQuery();

			if(rs != null) {				

				if(rs.next())  {	
					cnt = rs.getInt("CNT");
				}
				
			}

			if(cnt > 0) {
				result = true;
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
	 * 회원정보
	 * @param WaContext context 
	 * @param TaoDataSet data
	 * @return HashMap
	 */	
	public HashMap getMemberInfo(WaContext context, TaoDataSet data) throws Exception{
	
		String title = data.getString("TITLE");
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		ResultSet rs2 = null;
		PreparedStatement pstmt2 = null;		
		
		DbTaoResult result =  new DbTaoResult(title);
		HashMap hash =	null;
		String socId = "";
		try {
			conn = context.getDbConnection("default", null);
        
			//조회 ----------------------------------------------------------			
			String sql = this.getPrivateInfo();  
			
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("CDHD_ID"));
			rs = pstmt.executeQuery();
			
			hash = new HashMap();
			
			if(rs != null) {
				
				if (rs.next())  {
					
					//개인정보
					if (!rs.getString("MEMBER_CLSS").equals("5")){
					
						hash.put("MEMID",		rs.getString("MEMID"));
						hash.put("SOCID",		rs.getString("SOCID"));
						hash.put("MEMBERCLSS",	rs.getString("MEMBER_CLSS"));
						 
					//법인정보
					}else {
						
						String sql2 = this.getCoInfo();  
						
						int idx2 = 0;
						pstmt2 = conn.prepareStatement(sql2);
						pstmt2.setString(++idx2, data.getString("CDHD_ID"));
						rs2 = pstmt2.executeQuery();		
												
						if(rs2 != null) {
							
							if (rs2.next())  {
								
								//지정이면 주민번호 할당 MEM_CLSS
								if (rs2.getString("MEM_CLSS").equals("6")){ 
									
									hash.put("MEMID",		rs2.getString("MEM_ID"));
									hash.put("SOCID",		rs2.getString("USER_JUMIN_NO"));
									hash.put("MEMBERCLSS",	rs2.getString("MEM_CLSS"));	
									
								// 공용이면 법인번호 할당
								}else {
									
									hash.put("MEMID",		rs2.getString("MEM_ID"));
									hash.put("SOCID",		rs2.getString("BUZ_NO"));
									hash.put("MEMBERCLSS",	rs2.getString("MEM_CLSS"));	
									
								}
								
							}
							
						}
						
					}
				}
			}
			
		} catch (BaseException e) {
			throw new BaseException(e);			
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(rs2 != null) rs2.close(); } catch (Exception ignored) {}
			try { if(pstmt2 != null) pstmt2.close(); } catch (Exception ignored) {}			
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}	
		
		return hash;
		
	}	
	


	/*************************************************************************
    * 탑골프회원인지 조회    
    ************************************************************************ */
    private String topGolfYN(){
    	
        StringBuffer sql = new StringBuffer();
        
		sql.append("\n	SELECT COUNT(CDHD_GRD_SEQ_NO)CNT	\n");
		sql.append("	FROM  BCDBA.TBGGOLFCDHDGRDMGMT		\n");
		sql.append("	WHERE CDHD_ID = ?					\n");
		sql.append("	AND CDHD_CTGO_SEQ_NO = '21'			\n");  
		
		return sql.toString();
		
    }
    
	/*************************************************************************
     * 개인정보   
     ************************************************************************ */    
    private String getPrivateInfo(){
    	
        StringBuffer sql = new StringBuffer(); 
        
        sql.append("\n  SELECT MEMID, SOCID, MEMBER_CLSS		\n");
        sql.append("	FROM BCDBA.UCUSRINFO					\n");
        sql.append("\t  WHERE ACCOUNT = ? 						\n");

		return sql.toString();
		
    }    
    
	/*************************************************************************
     * 법인정보   
     ************************************************************************ */    
    private String getCoInfo(){
    	
        StringBuffer sql = new StringBuffer();
        
        /* 로그인시 법인 세션 정보 가져오는 방식과 같음
         * 로그인시는  looping해서 마지막 정보 가져온다, 이것을 아래와 같이 마지막 정보가져오도록 변경
         * 큰 틀을 바꾸기 전까지는 기존 방식 그대로 사용하기로 함
         */
        sql.append("\n  SELECT MEM_ID, BUZ_NO, MEM_CLSS, USER_JUMIN_NO				\n");
        sql.append("	FROM (  													\n");        
        sql.append("		SELECT B.MEM_ID, B.BUZ_NO, D.MEM_CLSS, B.USER_JUMIN_NO	\n");        
        sql.append("		FROM BCDBA.TBENTPUSER B 								\n");
        sql.append("		INNER JOIN BCDBA.TBENTPMEM D ON D.MEM_ID = B.MEM_ID		\n");
        sql.append("		WHERE  B.ACCOUNT = ?									\n");
        sql.append("		AND D.MEM_STAT = '2' AND D.SEC_DATE IS NULL				\n");        
        sql.append("		ORDER BY ROWNUM DESC									\n");
        sql.append("	)															\n");
        sql.append("	WHERE ROWNUM = 1 											\n");

		return sql.toString();
		
    }        
    
}

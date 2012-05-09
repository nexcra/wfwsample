/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmMemBkScoreDaoProc
*   �ۼ���    : (��)�̵������ �̰���
*   ����      : ������>ȸ������Ʈ>��(��ŷ����)
*   �������  : golf
*   �ۼ�����  : 2011-05-03
************************** �����̷� ****************************************************************
*    ����    �ۼ���   �������
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
 * @author	�̵������
 * @version	1.0
 ******************************************************************************/
public class GolfAdmMemBkScoreDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmMemListDaoProc ���μ��� ������    
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmMemBkScoreDaoProc() {}	

	/**
	 * ž����ȸ������ Ȯ��
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
			 
			//��ȸ ----------------------------------------------------------
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
	 * ȸ������
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
        
			//��ȸ ----------------------------------------------------------			
			String sql = this.getPrivateInfo();  
			
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("CDHD_ID"));
			rs = pstmt.executeQuery();
			
			hash = new HashMap();
			
			if(rs != null) {
				
				if (rs.next())  {
					
					//��������
					if (!rs.getString("MEMBER_CLSS").equals("5")){
					
						hash.put("MEMID",		rs.getString("MEMID"));
						hash.put("SOCID",		rs.getString("SOCID"));
						hash.put("MEMBERCLSS",	rs.getString("MEMBER_CLSS"));
						 
					//��������
					}else {
						
						String sql2 = this.getCoInfo();  
						
						int idx2 = 0;
						pstmt2 = conn.prepareStatement(sql2);
						pstmt2.setString(++idx2, data.getString("CDHD_ID"));
						rs2 = pstmt2.executeQuery();		
												
						if(rs2 != null) {
							
							if (rs2.next())  {
								
								//�����̸� �ֹι�ȣ �Ҵ� MEM_CLSS
								if (rs2.getString("MEM_CLSS").equals("6")){ 
									
									hash.put("MEMID",		rs2.getString("MEM_ID"));
									hash.put("SOCID",		rs2.getString("USER_JUMIN_NO"));
									hash.put("MEMBERCLSS",	rs2.getString("MEM_CLSS"));	
									
								// �����̸� ���ι�ȣ �Ҵ�
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
    * ž����ȸ������ ��ȸ    
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
     * ��������   
     ************************************************************************ */    
    private String getPrivateInfo(){
    	
        StringBuffer sql = new StringBuffer(); 
        
        sql.append("\n  SELECT MEMID, SOCID, MEMBER_CLSS		\n");
        sql.append("	FROM BCDBA.UCUSRINFO					\n");
        sql.append("\t  WHERE ACCOUNT = ? 						\n");

		return sql.toString();
		
    }    
    
	/*************************************************************************
     * ��������   
     ************************************************************************ */    
    private String getCoInfo(){
    	
        StringBuffer sql = new StringBuffer();
        
        /* �α��ν� ���� ���� ���� �������� ��İ� ����
         * �α��νô�  looping�ؼ� ������ ���� �����´�, �̰��� �Ʒ��� ���� ������ �������������� ����
         * ū Ʋ�� �ٲٱ� �������� ���� ��� �״�� ����ϱ�� ��
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

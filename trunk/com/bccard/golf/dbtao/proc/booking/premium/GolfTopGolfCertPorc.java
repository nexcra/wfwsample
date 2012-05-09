/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfTopGolfCertPorc
*   �ۼ���    : �̰���
*   ����      : TOP����ī�� ���� ��ŷ> ����ī�� ����
*   �������  : Golf
*   �ۼ�����  : 2010-11-18
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.booking.premium;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;


import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;


/******************************************************************************
 * Golf
 * @author	�̵������ 
 * @version	1.0
 ******************************************************************************/
public class GolfTopGolfCertPorc extends AbstractProc {
	
	/** 
	 * Proc ����, ������ ����ī�� üũ
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */    	
	public String getStateCheck(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String getId = "";
		String sql0 = "",  sql1 = "";
		String returnVal = "";
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result =  0;
		int idx = 0;

		try {
			//����ȸ�� üũ
			getId	= data.getString("ID");
			sql0 = this.getCntPriCor();
			
			conn = context.getDbConnection("default", null);			
			pstmt = conn.prepareStatement(sql0.toString());
			pstmt.setString(++idx, getId);
			rs = pstmt.executeQuery();

			if ( rs != null ) {
				while(rs.next())  {	
					result = rs.getInt("CNT");
				}	
			}
			
			if (result >0)	returnVal = "PRIVATE";			
			
			if(rs != null) rs.close(); 
			if(pstmt != null) pstmt.close();			
			
			//����ī�� üũ
			idx = 0;
			result =  0;
			sql1 = this.getCntIdQuery("4");
			pstmt = conn.prepareStatement(sql1.toString());
			pstmt.setString(++idx, getId);
			rs = pstmt.executeQuery();
			
			if ( rs != null ) {
				while(rs.next())  {	
					result = rs.getInt("CNT");
				}	
			}			
			
			if (result >0)	returnVal = "JIJUNG";
			
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return returnVal;
	}	

	/** 
	 * Proc ���̵�, ����� ��Ϲ�ȣ, �ֹι�ȣ ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */    
	public int getCount(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String getId = "", getBiz = "", getGubun = "";
		String sql = "";		
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result =  0;
		int idx = 0;

		try {
			
			getId	= data.getString("ID");
			getBiz	= data.getString("BIZ");			
			getGubun = data.getString("GUBUN");	

			idx = 0;
			result =  0;

			if (getGubun.equals("ID")){
				sql = this.getCntIdQuery("1");// ���̵� üũ
			}else if (getGubun.equals("BIZ")){
				sql = this.getCntIdQuery("2");// ����ڵ�Ϲ�ȣ üũ
			}else if (getGubun.equals("JUMIN")){
				sql = this.getCntIdQuery("3");// �ֹε�Ϲ�ȣ üũ
			}
			
			conn = context.getDbConnection("default", null);			
			pstmt = conn.prepareStatement(sql.toString());

			if (getGubun.equals("ID")){
				pstmt.setString(++idx, getId);
			}else if (getGubun.equals("BIZ") || getGubun.equals("JUMIN")){
				pstmt.setString(++idx, getId);
				pstmt.setString(++idx, getBiz);
			}
			
			rs = pstmt.executeQuery();

			if ( rs != null ) {
				while(rs.next())  {	
					result = rs.getInt("CNT");
				}
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
	 * Proc ��� ���� Ȯ�� �� �������� ������ ������Ʈ
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public int modifyJuminNO(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String getId = "", getJumin = "";
		String sql = "";
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int  result =  0, result2 =  0;
		int idx = 0;

		try {

			getId	= data.getString("ID");
			getJumin	= data.getString("JUMIN");
		
			sql	= this.getUpdateQuery();	
			
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);			
			
			//�ֹι�ȣ ������Ʈ
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, getJumin);
			pstmt.setString(++idx, getId);			
			
			result = pstmt.executeUpdate();
			if(pstmt != null) pstmt.close();			
			
			//������ �����
			idx = 0;
            sql = this.getInsertQuery();
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, getId);
			
			result2 = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();          
           
			if(result > 0 && result2 > 0) {				
				conn.commit();
			} else {				
				result = 0;
				conn.rollback();
			}

		} catch (Throwable t) {
			try{
				result = 0;				
				conn.rollback();				
			}catch(Exception e){
				debug(e.getMessage());
			}				
			throw new BaseException(t);	
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}
		
		return result;
	}

	
    /*************************************************************************
     * ����, ���� üũ
     ************************************************************************ */
     private String getCntPriCor(){
        
    	StringBuffer sql = new StringBuffer();
    	
 		sql.append("SELECT COUNT(account) CNT 	\n");
 		sql.append("FROM BCDBA.UCUSRINFO	\n");
		sql.append("WHERE ACCOUNT = ?  	\n");
		sql.append("AND MEMBER_CLSS IN ('1','4')   	\n");		
 		
 		return sql.toString();
 		
     }
     
	
    /*************************************************************************
     * ���̵�, ����� ��Ϲ�ȣ ����.
     ************************************************************************ */
     private String getCntIdQuery(String gubun){
    	 
    	StringBuffer sql = new StringBuffer();
    	
 		sql.append("SELECT COUNT(A.MEM_ID) CNT 	\n");
 		sql.append("FROM BCDBA.TBENTPUSER A, BCDBA.TBENTPMEM B  	\n");
 		
    	if (gubun.equals("1") || gubun.equals("4")){//���̵�
    		sql.append("WHERE A.ACCOUNT = ? 	\n");
    	}else if (gubun.equals("2")||gubun.equals("3")){//����ڵ�Ϲ�ȣ
    		sql.append("WHERE A.ACCOUNT = ? 	\n");
    		sql.append("AND A.BUZ_NO = ? 	\n");
    	}
    	
    	if (gubun.equals("3")){//�ֹι�ȣ    		
    		sql.append("AND A.USER_JUMIN_NO IS NULL	\n");
    	}
    	
    	if (gubun.equals("4")){//����ī��    		
    		sql.append("AND B.MEM_CLSS = '6'	\n");
    	}    	
    	
 		sql.append("AND A.MEM_ID = B.MEM_ID		\n");
 		sql.append("AND  B.SEC_DATE IS NULL		\n");
    	
 		return sql.toString();
 		
     }
     
     
     /*************************************************************************
      * �ֹε�Ϲ�ȣ ������Ʈ
      ************************************************************************ */
      private String getUpdateQuery(){
    	  
        StringBuffer sql = new StringBuffer();
          
  		sql.append("	UPDATE  BCDBA.TBENTPUSER 	\n");
  		sql.append("	SET USER_JUMIN_NO  = ? 	\n");
  		sql.append("    WHERE ACCOUNT = ?	\n");  		

  		return sql.toString();
  		
      }     
	
      
	/*************************************************************************
	 * ������ �����
	 ************************************************************************ */
	private String getInsertQuery(){
		
	    StringBuffer sql = new StringBuffer();
		
	    sql.append("INSERT INTO BCDBA.TBGCDHDGRDCHNGHST( SEQ_NO , CDHD_GRD_SEQ_NO , CDHD_ID , CDHD_CTGO_SEQ_NO , REG_ATON, JOIN_CHNL) \n");		
		sql.append("VALUES ((SELECT MAX(NVL(SEQ_NO,0))+1 FROM BCDBA.TBGCDHDGRDCHNGHST) , 0 , ?, 0 , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') , '9999')	\n");
		
        return sql.toString();
        
	}	
	
}

/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmMojibProc
*   �ۼ���    : E4NET ���弱
*   ����      : ���� �����Ͻ� ����
*   �������  : Golf
*   �ۼ�����  : 2009-09-03  
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.tm_member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.common.GolfUtil;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
 
/** *****************************************************************
 * GolfAdmMojibProc ���μ��� ������
 * @param N/A
 ***************************************************************** */
public class GolfAdmMojibEzReturnProc extends AbstractProc {
		
	//private static final String TITLE = "������ ����";		
	
	/** *****************************************************************
	 * GolfAdmMojibProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmMojibEzReturnProc() {}
	
	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult 
	 */
	
	/*����*/
	public int updState(WaContext context, TaoDataSet dataSet) throws BaseException {		

		//String title = dataSet.getString("TITLE");
		Connection con = null;
		PreparedStatement pstmt = null;
		int result = 0;
		String 	sStrSql = "";
		ResultSet rs = null;
			
		try{
			con = context.getDbConnection("default", null);	

			String aspOrderNum		= dataSet.getString("aspOrderNum");
			String orderNum			= dataSet.getString("orderNum");
			String jumin_no			= dataSet.getString("jumin_no");
			
			if(GolfUtil.empty(jumin_no)){
				// ��Ҵ���� �ֹε�Ϲ�ȣ �˻�
				sStrSql = this.getTmJuminNo();
				pstmt = con.prepareStatement(sStrSql);
				pstmt.setString(1, aspOrderNum);
				pstmt.setString(2, orderNum);
				rs = pstmt.executeQuery(); 
				if (rs.next())	{		
					jumin_no	= rs.getString("JUMIN_NO");
				}
			}
			   
			// Tm ���̺� ���� ��ҷ� ������Ʈ
			sStrSql = this.getUpdTmSql();
			pstmt = con.prepareStatement(sStrSql);
			pstmt.setString(1, jumin_no);
			result = pstmt.executeUpdate(); 
			
			if(result>0){
				
				// ȸ��������̺� ����
				sStrSql = this.getDelMemGradeSql();
				pstmt = con.prepareStatement(sStrSql);
				pstmt.setString(1, jumin_no);
				result = pstmt.executeUpdate(); 
				
				// ȸ�����̺� ����
				sStrSql = this.getDelMemSql();
				pstmt = con.prepareStatement(sStrSql);
				pstmt.setString(1, jumin_no);
				result = pstmt.executeUpdate(); 
				
				// ��û���̺� ������Ʈ
				sStrSql = this.getUpdAplSql();
				pstmt = con.prepareStatement(sStrSql);
				pstmt.setString(1, jumin_no);
				result = pstmt.executeUpdate(); 
			}
			
			
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {	
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}		
		
		return result;	
	}



	/* ������ ����� �ֹε�Ϲ�ȣ �������� */
	public String getTmJuminNo(){
		StringBuffer sb = new StringBuffer();
		sb.append("\n SELECT JUMIN_NO FROM BCDBA.TBGLUGANLFEECTNT WHERE MB_CDHD_NO='ezwel' AND AUTH_NO=? AND CARD_NO=?	");
		return sb.toString();
	}

	/* TM ���̺� ������Ʈ */
	public String getUpdTmSql(){
		StringBuffer sb = new StringBuffer();
		sb.append("\n UPDATE BCDBA.TBLUGTMCSTMR SET TB_RSLT_CLSS='03', REJ_RSON='������ ���' WHERE JUMIN_NO = ?	");
		return sb.toString();
	}

	/* ��û���̺� ������Ʈ */
	public String getUpdAplSql(){
		StringBuffer sb = new StringBuffer();
		sb.append("\n UPDATE BCDBA.TBGAPLCMGMT SET PGRS_YN='F' WHERE GOLF_SVC_APLC_CLSS='0012' AND JUMIN_NO=?	");
		return sb.toString();
	}

	/* ��� ���̺� ���� */
	public String getDelMemGradeSql(){
		StringBuffer sb = new StringBuffer();
		sb.append("\n DELETE FROM BCDBA.TBGGOLFCDHDGRDMGMT WHERE CDHD_ID = (SELECT CDHD_ID FROM BCDBA.TBGGOLFCDHD WHERE JUMIN_NO=?)	");
		return sb.toString();
	}
	
	/* ȸ�����̺� ���� */
	public String getDelMemSql(){
		StringBuffer sb = new StringBuffer();
		sb.append("\n DELETE FROM BCDBA.TBGGOLFCDHD WHERE JUMIN_NO=?	");
		return sb.toString();
	}


}



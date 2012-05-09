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
import com.bccard.golf.common.security.cryptography.Base64Encoder;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
 
/** *****************************************************************
 * GolfAdmMojibProc ���μ��� ������
 * @param N/A
 ***************************************************************** */
public class GolfAdmMojibEzUpdProc extends AbstractProc {
		
	//private static final String TITLE = "������ ����";		
	
	/** *****************************************************************
	 * GolfAdmMojibProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmMojibEzUpdProc() {}
	
	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult 
	 */
	
	/*����*/
	public DbTaoResult getPay(WaContext context, TaoDataSet dataSet) throws BaseException {		

		String title = dataSet.getString("TITLE");
		Connection con = null;
		PreparedStatement pstmt = null;
		//int count = 0;
		DbTaoResult  result =  new DbTaoResult(title);
		ResultSet rs = null;
			
		try{
			con = context.getDbConnection("default", null);	

			String jumin_no		= dataSet.getString("jumin_no");

			String aspOrderNum 	= "";	// �ֹ���ȣ (���޻���) -> ��������� �ֹ���ȣ
			String orderNum 	= "";	// �ֹ���ȣ (��������)
			String cspCd		= "golfrounge";	// ���޻�(CP) ��ü�ڵ� 
			String command		= "102";	// ��ɾ� ����
			
			String enc_aspOrderNum = "";
			String enc_orderNum = "";
			String enc_cspCd = "";
			String enc_command = "";
			
			   
			String 	sStrSql = this.getPaySql();
			pstmt = con.prepareStatement(sStrSql);
			pstmt.setString(1, jumin_no);
			rs = pstmt.executeQuery(); 
			if (rs.next())	{		
				aspOrderNum	= rs.getString("AUTH_NO");
				orderNum	= rs.getString("CARD_NO");
			}

			if(!GolfUtil.empty(aspOrderNum))	enc_aspOrderNum	= new String(Base64Encoder.encode(aspOrderNum.getBytes()));
			if(!GolfUtil.empty(orderNum))		enc_orderNum 	= new String(Base64Encoder.encode(orderNum.getBytes()));
			if(!GolfUtil.empty(cspCd))			enc_cspCd 		= new String(Base64Encoder.encode(cspCd.getBytes()));
			if(!GolfUtil.empty(command))		enc_command 	= new String(Base64Encoder.encode(command.getBytes()));
			
			
			result.addString("dec_aspOrderNum"	, aspOrderNum);
			result.addString("dec_orderNum"		, orderNum);
			result.addString("dec_cspCd"		, cspCd);
			result.addString("dec_command"		, command);
			
			result.addString("aspOrderNum"		, enc_aspOrderNum);
			result.addString("orderNum"			, enc_orderNum);
			result.addString("cspCd"			, enc_cspCd);
			result.addString("command"			, enc_command);
			
//			debug("aspOrderNum : " + enc_aspOrderNum + " / orderNum : " + enc_orderNum + " / cspCd : " + enc_cspCd + " / command : " + enc_command);
//			debug("aspOrderNum : " + aspOrderNum + " / orderNum : " + orderNum + " / cspCd : " + cspCd + " / command : " + command);
			
			
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {	
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}		
		
		return result;	
	}




	/* �������̺� ���� �������� */
	public String getPaySql(){
		StringBuffer sb = new StringBuffer();
		sb.append("\n SELECT AUTH_NO, CARD_NO FROM  BCDBA.TBGLUGANLFEECTNT WHERE JUMIN_NO=? AND MB_CDHD_NO='ezwel'	");
		return sb.toString();
	}


}



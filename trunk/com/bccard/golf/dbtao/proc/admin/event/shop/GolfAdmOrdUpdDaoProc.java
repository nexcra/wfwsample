/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmOrdUpdDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ > �̺�Ʈ > ���� > ���� ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.event.shop;

import java.io.Reader;
import java.io.Writer;
import java.io.CharArrayReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.dbtao.*;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.msg.MsgEtt;

import oracle.jdbc.driver.OracleResultSet; 
import oracle.sql.CLOB;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfAdmOrdUpdDaoProc extends AbstractProc {

	public static final String TITLE = "������ > �̺�Ʈ > ���� > ���� ���� ó��";

	/** *****************************************************************
	 * GolfAdmOrdUpdDaoProc ���μ��� ������
	 * @param N/A 
	 ***************************************************************** */
	public GolfAdmOrdUpdDaoProc() {}

	/** *****************************************************************
	 * GolfAdmOrdUpdDaoProc ���� ���� ó�� �޼ҵ�
	 * @param N/A
	 ***************************************************************** */
	public int execute(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {
		int result = 0;
		int result_upd = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String refund_yn = data.getString("refund_yn");	// ���°�
			String odr_no = data.getString("ord_no");		// �ֹ���ȣ
			String cdhd_id = data.getString("cdhd_id");		// ���̵�
			String sttl_stat_clss = data.getString("sttl_stat_clss");		// �ֹ����� Y:�������, N:����
            /*****************************************************************************/
			
			debug("odr_no : " + odr_no);

        	if("61".equals(refund_yn) && "N".equals(sttl_stat_clss)){
        		
        		// ��ȸ�� ȯ��ó�� ���ش�.
        		boolean payCancelResult = false;
        		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// ��������
        		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
	

				// ��������
				String sttl_amt = "";	// �����ݾ�
				String mer_no = "";		// ��������ȣ
				String card_no = "";	// ī���ȣ
				String vald_date = "";	// ��ȿ����
				String ins_mcnt = "";	// �Һΰ�����
				String auth_no = "";	// ���ι�ȣ
				String ip = request.getRemoteAddr();  // �ܸ���ȣ(IP, '.'����)
				String sttl_mthd_clss = "";	// ������������ڵ�
				String sttl_gds_clss = "";	// ������ǰ�����ڵ�
				

				// ���� ���� ��ȸ
				sql = this.setPayBackListQuery(); 
	            pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, odr_no );
	            rs = pstmt.executeQuery();	
	            
	            while(rs.next()){
	            	payCancelResult = false;

					sttl_amt = rs.getString("STTL_AMT");
					mer_no = rs.getString("MER_NO");
					card_no = rs.getString("CARD_NO");
					vald_date = rs.getString("VALD_DATE");
					ins_mcnt = rs.getString("INS_MCNT");
					auth_no = rs.getString("AUTH_NO");
					sttl_mthd_clss = rs.getString("STTL_MTHD_CLSS");
					sttl_gds_clss = rs.getString("STTL_GDS_CLSS");

					// ��ī�� �Ǵ� ���հ����� ���
					if("0001".equals(sttl_mthd_clss) || "0002".equals(sttl_mthd_clss)) {
					
						payEtt.setMerMgmtNo(mer_no);		// ������ ��ȣ
						payEtt.setCardNo(card_no);			// ispī���ȣ
						payEtt.setValid(vald_date);			// ���� ����
						payEtt.setAmount(sttl_amt);			// �����ݾ�	
						payEtt.setInsTerm(ins_mcnt);		// �Һΰ�����
						payEtt.setRemoteAddr(ip);			// ip �ּ�
						payEtt.setUseNo(auth_no);			// ���ι�ȣ

						String host_ip = java.net.InetAddress.getLocalHost().getHostAddress();
						if( "211.181.255.40".equals(host_ip)) {
							payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// ������� ȣ��
							//payCancelResult=true;

						} else {

							payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// ������� ȣ��
						}
						debug("payCancelResult========> " + payCancelResult);
					}
					
					// Ÿ��ī�� �Ǵ� ������ü�� ���(�þ�����)
					else if("0003".equals(sttl_mthd_clss) || "0004".equals(sttl_mthd_clss)) {
					
						String payType = "";
						if("0003".equals(sttl_mthd_clss))			payType = "CARD";	// �ſ�ī��
						else if("0004".equals(sttl_mthd_clss))		payType = "ABANK";	// ������ü

						String sShopid  = "bcgolfshop";							// �����̵�
						String sCrossKey  = "2a50a7795aaff3397e26d250b24d942f";	// ũ�ν�Ű
						
						payEtt.setOrderNo(odr_no);			// �ֹ���ȣ
						payEtt.setAmount(sttl_amt);			// �����ݾ�	
						payEtt.setPayType(payType);			// �������
						payEtt.setShopId(sShopid);
						payEtt.setCrossKey(sCrossKey);

						payCancelResult = payProc.executePayAuthCancel_Allat(context, payEtt);		// ������� ȣ��	
						
						debug("payCancelResult========> " + payCancelResult + " | odr_no : " + odr_no + " | sttl_amt : " + sttl_amt + " | payType : " + payType);
					}					
					
					if(payCancelResult){
						
						sql = this.getPayUpdateQuery(); 
			            pstmt = conn.prepareStatement(sql);
			        	pstmt.setString(1, odr_no );
						result_upd = pstmt.executeUpdate();
						
						debug("ODR_NO========> " + odr_no);
						debug("result_upd========> " + result_upd);

					}
					else{	// �������н� ���� ���� 2009.11.26
						
						GolfPaymentRegDaoProc payFailProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");						
						
						DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);						
						dataSet.setString("CDHD_ID", cdhd_id);						//ȸ�����̵�
						dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);		//������������ڵ� :0001 : BCī�� / 0002:BCī�� + TOP����Ʈ / 0003:Ÿ��ī�� / 0004:������ü 
						dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);			//������ǰ�����ڵ� 0001:è�ǿ¿�ȸ�� 0002:��翬ȸ�� 0003:��忬ȸ�� 0008:����ȸ�� 
						dataSet.setString("STTL_STAT_CLSS", "Y");					//�������� N:�����Ϸ� / Y:�������
							
						int result_fail = payFailProc.failExecute(context, dataSet, request, payEtt);
						
						debug("�������г���������========> " + result_fail);						
						
					}
	            }						
        	}else{ 
        		result_upd = 1;
        	}

            /*****************************************************************************/
			
        	if(result_upd > 0){
				sql = this.getUpdQuery();//Insert Query
				pstmt = conn.prepareStatement(sql);
	
				int idx = 0;
				pstmt.setString(++idx, data.getString("buy_yn") );
				pstmt.setString(++idx, refund_yn );
				pstmt.setString(++idx, data.getString("dlv_yn") );
				pstmt.setString(++idx, odr_no );		
				
				result = pstmt.executeUpdate();
				
				conn.setAutoCommit(true);
        	}
						
		} catch(Exception e) {
			try	{
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			//e.printStackTrace();
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
	        try { if(rs != null) rs.close(); } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
		
	
    /** ***********************************************************************
    * ���� ���¸� �����Ѵ�.
    ************************************************************************ */
	private String getUpdQuery(){
		StringBuffer sql = new StringBuffer();

		sql.append("\n	UPDATE BCDBA.TBGLUGODRCTNT	\n");
		sql.append("\t	SET ODR_DTL_CLSS = ?, ODR_STAT_CLSS = ?, DLV_YN = ?	\n");
		sql.append("\t	WHERE ODR_NO=?	\n");
				
		return sql.toString();
	}

	/** ***********************************************************************
	 * ���� ���� ��������
	 ************************************************************************ */
	private String setPayBackListQuery(){
		StringBuffer sql = new StringBuffer();        
		sql.append("\n	SELECT STTL_AMT, MER_NO, CARD_NO, VALD_DATE, INS_MCNT, AUTH_NO, STTL_MTHD_CLSS, STTL_GDS_CLSS	\n");
		sql.append("\t	FROM BCDBA.TBGSTTLMGMT	\n");
		sql.append("\t	WHERE ODR_NO=?	\n");
		return sql.toString();
	}
	
    /** ***********************************************************************
	    * ���� ��� ������Ʈ    
	    ************************************************************************ */
	    private String getPayUpdateQuery(){
	        StringBuffer sql = new StringBuffer();
	 		sql.append("\n");
	 		sql.append("\t	UPDATE BCDBA.TBGSTTLMGMT	\n");
	 		sql.append("\t	SET STTL_STAT_CLSS='Y', CNCL_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
	 		sql.append("\t	WHERE ODR_NO=?	\n");
	        return sql.toString();
	    }
	
}



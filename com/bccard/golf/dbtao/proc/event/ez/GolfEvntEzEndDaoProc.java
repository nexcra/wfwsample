/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntKvpDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : �̺�Ʈ > ����ȸ > ���ó��
*   �������  : Golf
*   �ۼ�����  : 2010-02-20
************************** �����̷� ****************************************************************
* Ŀ��屸����	������		������	���泻��
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event.ez;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.BcUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

import java.net.InetAddress;
import com.bccard.golf.common.AppConfig;
/******************************************************************************
 * Golf
 * @author	�̵������
 * @version	1.0  
 ******************************************************************************/
public class GolfEvntEzEndDaoProc extends AbstractProc {
	
	public GolfEvntEzEndDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public int updEvntFunction(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int bcMemCnt = 0;	// BC ��� ���̺� ����

		try {
			conn = context.getDbConnection("default", null);

			// BC ȸ�� ����
			String aspOrderNum 	= data.getString("aspOrderNum");	// �ֹ���ȣ (���޻���) -> ��������� �ֹ���ȣ
			
			pstmt = conn.prepareStatement(getBcMemCnt());
			pstmt.setString(1, aspOrderNum);
			rs = pstmt.executeQuery(); 
			while (rs.next())	{		
				bcMemCnt	= rs.getInt("CNT");
			}

		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return bcMemCnt;
	}

	


	/** ***********************************************************************
    * �̺�Ʈ ���� ��������
    ************************************************************************ */
    private String getBcMemCnt(){
        StringBuffer sql = new StringBuffer();	
		sql.append(" \n");
		sql.append("\t	SELECT COUNT(*) CNT \n");
		sql.append("\t	FROM BCDBA.TBGAPLCMGMT APL \n");
		sql.append("\t	JOIN BCDBA.UCUSRINFO BCM ON APL.JUMIN_NO=BCM.SOCID \n");
		sql.append("\t	WHERE GOLF_SVC_APLC_CLSS='0012' AND APLC_SEQ_NO=? \n");
		return sql.toString();
    }
}

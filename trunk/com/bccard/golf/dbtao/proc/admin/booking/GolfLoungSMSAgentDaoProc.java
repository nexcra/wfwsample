/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkPreTimeRsViewDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ ��� Ȯ�� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-28
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.booking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.io.Reader;
import java.net.InetAddress;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;

/******************************************************************************
 * Golf
 * @author	�̵������ 
 * @version	1.0
 ******************************************************************************/
public class GolfLoungSMSAgentDaoProc extends AbstractProc {
	
	public static final String TITLE = "��ŷ ��� Ȯ�� ó��";
	
	/** *****************************************************************
	 * GolfBkPreTimeRsViewDaoProc ��ŷ ��� Ȯ�� ó��
	 * @param N/A
	 ***************************************************************** */
	public GolfLoungSMSAgentDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 * @throws BaseException 
	 */
	public int execute(WaContext context) throws BaseException  {

		int result = 0;
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = context.getDbConnection("default", null);						
			String sql = this.getReserveQuery(); 
            
			pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();	

			String hostAddress = InetAddress.getLocalHost().getHostAddress();
			HashMap smsMap = new HashMap();
			String smsClss = "634";
			String message = "";
            
			while(rs.next()){

				smsMap.put("ip", hostAddress);
				smsMap.put("sName", rs.getString("HG_NM"));
				smsMap.put("sPhone1", rs.getString("HP_DDD_NO"));
				smsMap.put("sPhone2", rs.getString("HP_TEL_HNO"));
				smsMap.put("sPhone3", rs.getString("HP_TEL_SNO"));

				message = "[VIP��ŷ] "
					+rs.getString("HG_NM")+"�� "
					+rs.getString("GREEN_NM")+" "
					+rs.getString("CURS_NM")+" "
					+rs.getString("BK_DATE")+" "
					+rs.getString("BK_TIME")+" ����- Golf Loun.G";
				SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
				String smsRtn = smsProc.send(smsClss, smsMap, message);
				//debug(smsRtn + "SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.." + message + "|" + rs.getString("HP_DDD_NO"));
	        	result++;
			}
			
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();			

		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);

		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}	
	

    
	/** ***********************************************************************
	 * ��Ͽ��θ� �����Ѵ�.    
	 ************************************************************************ */
	private String getReserveQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t 	SELECT RSV.HP_DDD_NO, RSV.HP_TEL_HNO , RSV.HP_TEL_SNO, CDHD.HG_NM	\n");
		sql.append("\t 	, GR.GREEN_NM, BKD.GOLF_RSVT_CURS_NM CURS_NM	\n");
		sql.append("\t 	, TO_CHAR(TO_DATE(BKD.BOKG_ABLE_DATE),'YY.MM.DD') BK_DATE	\n");
		sql.append("\t 	, SUBSTR(BKT.BOKG_ABLE_TIME,1,2)||':'||SUBSTR(BKT.BOKG_ABLE_TIME,3,2) BK_TIME	\n");
		
		sql.append("\t	FROM BCDBA.TBGRSVTMGMT RSV	\n");
		sql.append("\t 	JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT BKT ON RSV.RSVT_ABLE_BOKG_TIME_SEQ_NO=BKT.RSVT_ABLE_BOKG_TIME_SEQ_NO	\n");
		sql.append("\t 	JOIN BCDBA.TBGRSVTABLESCDMGMT BKD ON BKT.RSVT_ABLE_SCD_SEQ_NO=BKD.RSVT_ABLE_SCD_SEQ_NO	\n");
		sql.append("\t 	JOIN BCDBA.TBGGOLFCDHD CDHD ON CDHD.CDHD_ID=RSV.CDHD_ID	\n");
		sql.append("\t 	JOIN BCDBA.TBGAFFIGREEN GR ON GR.AFFI_GREEN_SEQ_NO=BKD.AFFI_GREEN_SEQ_NO	\n");
		
		sql.append("\t	WHERE SUBSTR(RSV.GOLF_SVC_RSVT_MAX_VAL,5,1)='M' AND RSV.RSVT_YN='Y'	\n");
		sql.append("\t 	AND BKD.BOKG_ABLE_DATE = TO_CHAR(SYSDATE+7,'YYYYMMDD') 	\n");
		//sql.append("\t 	AND RSV.HP_TEL_SNO = '4738' AND ROWNUM=1	\n");
		sql.append("\t 	ORDER BY BKD.BOKG_ABLE_DATE DESC	\n");
		return sql.toString();
	}
 
}

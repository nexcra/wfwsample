/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmBusPeopleDetailInqDaoProc
*   �ۼ���    : (��)�̵������ �ǿ���
*   ����      : ������ > �̺�Ʈ->��������������̺�Ʈ->��û���� �󼼺���
*   �������  : Golf
*   �ۼ�����  : 2009-09-25
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.event.golfbus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;


/** ****************************************************************************
 *  Golf
 * @author	(��)�̵������ 
 * @version 1.0
 **************************************************************************** */
public class GolfAdmBusPeopleDetailInqDaoProc extends DbTaoProc {
	
	/**
	 * Proc ����. 
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public TaoResult execute(Connection con, TaoDataSet dataSet) throws TaoException {

		PreparedStatement pstmt			= null;
		ResultSet rs 					= null;
		String title					= dataSet.getString("TITLE");
		String actnKey 					= null;
		DbTaoResult result				= new DbTaoResult(title);

		try {
			
			actnKey 					= dataSet.getString("actnKey");						
			int pidx 					= 0;
			
			pstmt = con.prepareStatement(getSelectQuery());		
			pidx = 0;
			pstmt.setString(++pidx, dataSet.getString("p_idx"));
			
			
			rs = pstmt.executeQuery();
			
			if ( rs.next() ) {
				//GolfUtil.toTaoResultBoard(result, rs, serial);
				GolfUtil.toTaoResult(result, rs);
				result.addString("RESULT", "00");
			} else {
				result.addString("RESULT", "01");
			}		
			
		} catch(Throwable t){
			MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "SYSTEM_ERROR", null );
			msgEtt.addEvent( actnKey + ".do", "bt_ok.gif");
			throw new DbTaoException(msgEtt,t);
		} finally {
			try { if( rs != null ){ rs.close(); } else {} } catch(Throwable ignore) {}
			try { if( pstmt != null ){ pstmt.close(); } else {} } catch(Throwable ignore) {}
			try { if( con != null ){ con.close(); } else {} } catch(Throwable ignore) {}
		}

		return result;
	}
	
	/** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
	
    private String getSelectQuery() throws BaseException{
		StringBuffer sql = new StringBuffer();

		sql.append("\n     SELECT to_char(TO_DATE(SUBSTR(A.REG_ATON,1,8),'yyyymmdd'),'yyyy.mm.dd') as REG_ATON,B.GOLF_CMMN_CODE,A.CO_NM,A.CDHD_ID,	 ");
		sql.append("\n            ( select DECODE(CDHD_CTGO_SEQ_NO,'8','ȭ��Ʈ','7','���','6','���','5','è�ǿ�') GRADE from BCDBA.TBGGOLFCDHDGRDMGMT where CDHD_ID = A.CDHD_ID ) AS GRADE , 			 ");
		//sql.append("\n            DECODE(C.CDHD_CTGO_SEQ_NO,'8','ȭ��Ʈ','7','���','6','���','5','è�ǿ�') GRADE,			 ");
		sql.append("\n            A.HP_DDD_NO,A.HP_TEL_HNO,A.HP_TEL_SNO,A.EMAIL,			     ");
		sql.append("\n            A.RIDG_PERS_NUM,A.MEMO_EXPL,A.GREEN_NM, TEOF_DATE,             ");
		sql.append("\n            A.DTL_ADDR                            ");		
		sql.append("\n       FROM BCDBA.TBGAPLCMGMT A,                                            ");
		sql.append("\n            (SELECT  GOLF_CMMN_CODE_NM,GOLF_CMMN_CODE                                                  ");
		sql.append("\n               FROM BCDBA.TBGCMMNCODE                                                                  ");
		sql.append("\n              WHERE GOLF_CMMN_CLSS='0054' AND USE_YN ='Y'                                              ");
		sql.append("\n              ORDER BY SORT_SEQ) B                                                ");
		sql.append("\n      WHERE A.GOLF_SVC_APLC_CLSS = '9002'                                                              ");
		sql.append("\n        AND A.APLC_SEQ_NO = ?                                                                          ");
		sql.append("\n        AND A.PGRS_YN = B.GOLF_CMMN_CODE           ");
		//sql.append("\n        AND A.CDHD_ID = C.CDHD_ID           ");
        

		return sql.toString();
	}

		

}

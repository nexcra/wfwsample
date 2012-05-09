/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmMbInfoSelInqDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ �����Ͻ� ���̵� ���� üũ
*   �������  : golf
*   �ۼ�����  : 2009-06-23
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.drivrange;

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

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmMbInfoSelInqDaoProc extends AbstractProc {

	public static final String TITLE = "������ �����Ͻ� ���̵� ���� üũ";

	/** *****************************************************************
	 * GolfAdmMbInfoSelInqDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmMbInfoSelInqDaoProc() {}
	
	/**
	 * ������ �����Ͻ� ���̵� ���� üũ
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		boolean flag = false;
		DbTaoResult result =  new DbTaoResult(title);
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
				
		try {
			conn = context.getDbConnection("default", null);
			 
			//��ȸ ----------------------------------------------------------			
			String sql = this.getSelectQuery();   
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("GF_ID"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					String cdhd_id = rs.getString("CDHD_ID"); //ȸ�����̵�
					String userclss = rs.getString("USERCLSS"); //ȸ�����
					long cbmo_tot_amt = rs.getLong("CBMO_TOT_AMT"); //���̹��Ӵ�
					String hg_nm = rs.getString("HG_NM"); //�ѱۼ���
					
					if (cbmo_tot_amt >= 5000){
						flag = true;
					} else {
						if (userclss.indexOf("VIP") > -1 || userclss.indexOf("���") > -1 || userclss.indexOf("���Ǿ����÷�Ƽ��") > -1){
							flag = true;
						}
					}
				
					if (flag){
						result.addString("USERCLSS", userclss ); 			
						result.addString("HAN_NM", hg_nm );
						
						result.addString("RESULT", "00"); //������
					}
				}
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}
			
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
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
        
        sql.append("\n SELECT");
		sql.append("\n 	CDHD_ID, DECODE (USERCLSS, '()', '��޾���', USERCLSS) USERCLSS, NVL (CBMO_TOT_AMT, 0) CBMO_TOT_AMT, HG_NM 	 ");
		sql.append("\n  	FROM (SELECT T.CDHD_ID,	 ");
		sql.append("\n  				SUBSTR (MAX (SYS_CONNECT_BY_PATH ((GOLF_CMMN_CODE_NM1 || '(' || GOLF_CMMN_CODE_NM2 || ')' ), ',' )), 2) AS USERCLSS,	 ");
		sql.append("\n  				(SELECT (CBMO_ACM_TOT_AMT - CBMO_DDUC_TOT_AMT) CBMO_TOT_AMT FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID = T.CDHD_ID) CBMO_TOT_AMT,	 ");
		sql.append("\n  				(SELECT HG_NM FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID = T.CDHD_ID) HG_NM	 ");
		sql.append("\n  			FROM (SELECT CDHD_ID, GOLF_CMMN_CODE_NM1, GOLF_CMMN_CODE_NM2, ROW_NUMBER () OVER (PARTITION BY CDHD_ID ORDER BY CDHD_ID) CNT 	");
		sql.append("\n  					FROM (SELECT TGUC.CDHD_ID, TMC1.GOLF_CMMN_CODE_NM GOLF_CMMN_CODE_NM1, TMC2.GOLF_CMMN_CODE_NM GOLF_CMMN_CODE_NM2 	");
		sql.append("\n  							FROM BCDBA.TBGGOLFCDHDGRDMGMT TGUC, BCDBA.TBGGOLFCDHDCTGOMGMT TGUD,  ");
		sql.append("\n									(SELECT GOLF_CMMN_CODE, GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS = '0001') TMC1,  ");
		sql.append("\n									(SELECT GOLF_CMMN_CODE, GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS = '0005') TMC2  ");
		sql.append("\n								WHERE TGUC.CDHD_CTGO_SEQ_NO = TGUD.CDHD_CTGO_SEQ_NO(+)  ");
		sql.append("\n								AND TGUD.CDHD_SQ1_CTGO = TMC1.GOLF_CMMN_CODE(+)  ");
		sql.append("\n								AND TGUD.CDHD_SQ2_CTGO = TMC2.GOLF_CMMN_CODE(+)   ");
		sql.append("\n								AND TGUC.CDHD_ID = ?  ");
		sql.append("\n								)  ");
		sql.append("\n						) T  ");
		sql.append("\n		START WITH CNT = 1  ");
		sql.append("\n		CONNECT BY PRIOR CNT = CNT - 1 ");
		sql.append("\n		GROUP BY CDHD_ID)  ");
		
		return sql.toString();
    }

}

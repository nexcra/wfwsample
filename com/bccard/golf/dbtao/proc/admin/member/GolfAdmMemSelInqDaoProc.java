/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmMemSelInqDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ ����ǰ���� ���̵� ���� üũ
*   �������  : golf
*   �ۼ�����  : 2009-08-24
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.member;

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
public class GolfAdmMemSelInqDaoProc extends AbstractProc {

	public static final String TITLE = "������ ����ǰ���� ���̵� ���� üũ";

	/** *****************************************************************
	 * GolfAdmMbInfoSelInqDaoProc ���μ��� ������ 
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmMemSelInqDaoProc() {}
	
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
			pstmt.setString(++idx, data.getString("cdhd_id"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					String cdhd_id = rs.getString("CDHD_ID"); //ȸ�����̵�
					String userclss = rs.getString("USERCLSS"); //ȸ�����		
					String cdhd_jonn_date = rs.getString("ACRG_CDHD_JONN_DATE"); //ȸ�����	
					
					if (userclss.indexOf("5") > -1 ){
						flag = true;				
					}

					if (flag){
						
						result.addString("USERCLSS", userclss ); 
						result.addString("ACRG_CDHD_JONN_DATE", cdhd_jonn_date ); 
						
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
        
        sql.append("\n SELECT																								");
		sql.append("\n 	TBG1.CDHD_ID, TBG2.CDHD_CTGO_SEQ_NO AS USERCLSS, TBG1.ACRG_CDHD_JONN_DATE 						");
		sql.append("\n 		FROM BCDBA.TBGGOLFCDHD TBG1 																	");
		sql.append("\n		JOIN BCDBA.TBGGOLFCDHDGRDMGMT TBG2 ON TBG1.CDHD_ID = TBG2.CDHD_ID AND TBG2.CDHD_CTGO_SEQ_NO = 5					");
		sql.append("\n		WHERE to_char(to_date(TBG1.ACRG_CDHD_END_DATE,'yyyymmddhh'), 'yyyy-mm-dd') >= TO_CHAR(SYSDATE, 'YYYY-MM-DD')	");
		sql.append("\n		AND ( TBG1.SECE_YN = 'N' OR  TBG1.SECE_YN is null )					");
		sql.append("\n		AND TBG1.CDHD_ID = ?			");
		
		return sql.toString();
    }

}

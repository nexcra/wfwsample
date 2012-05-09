/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMtGradePayDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ����Ƽ�ڽ� > ���� ���� > ȸ����� ���׷��̵� �˾�
*   �������  : golf 
*   �ۼ�����  : 2010-07-12 
************************** �����̷� ****************************************************************
* Ŀ��屸����	������		������	���泻��
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.mytbox.myInfo;

import java.io.Reader;
import java.io.Writer;
import java.io.CharArrayReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.user.entity.UcusrinfoEntity;

import javax.servlet.http.HttpServletRequest;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfMtGradePayDaoProc extends AbstractProc {

	public static final String TITLE = "����Ƽ�ڽ� > ���� ���� > ȸ����� ���׷��̵� �˾�";

	public GolfMtGradePayDaoProc() {}

	// ȸ���Ⱓ �����ϱ⿡�� ȸ������ �������� �Լ�
	public DbTaoResult execute(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {

		String title = TITLE;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DbTaoResult result =  new DbTaoResult(title);

		
		try {
			// 01. ��������üũ
			String ctgo_seq = data.getString("ctgo_seq");

			conn = context.getDbConnection("default", null);	
			
			
			//��ȸ ----------------------------------------------------------			
			String sql = this.getSelectQuery();   

			// �Է°� (INPUT)  
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, ctgo_seq);
			rs = pstmt.executeQuery();
						
			if(rs != null) {
				while(rs.next())  {

					result.addString("GRD_SEQ" 			,rs.getString("GRD_SEQ") );
					result.addString("GRD_NM" 			,rs.getString("GRD_NM") );
					result.addString("GRD_SQ2" 			,rs.getString("GRD_SQ2") );
					result.addString("ANL_FEE" 			,GolfUtil.comma(rs.getString("ANL_FEE")) );
					result.addString("MO_MSHP_FEE" 		,GolfUtil.comma(rs.getString("MO_MSHP_FEE")) );
					result.addString("RESULT", "00"); //������ 
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
    * ����ȸ���� ����Ʈ�� �����´�.    
    ************************************************************************ */ 
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();

        sql.append("\n");
		sql.append("\t SELECT CTGO.CDHD_CTGO_SEQ_NO GRD_SEQ, CODE.GOLF_CMMN_CODE_NM GRD_NM	\n");
		sql.append("\t , CTGO.ANL_FEE, CTGO.MO_MSHP_FEE, TO_NUMBER(CTGO.CDHD_SQ2_CTGO) GRD_SQ2	\n");
		sql.append("\t FROM BCDBA.TBGGOLFCDHDCTGOMGMT CTGO	\n");
		sql.append("\t JOIN BCDBA.TBGCMMNCODE CODE ON CODE.GOLF_CMMN_CODE=CTGO.CDHD_SQ2_CTGO AND CODE.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t WHERE CTGO.USE_CLSS='Y' AND CTGO.CDHD_SQ2_CTGO=? 	\n");
		sql.append("\t ORDER BY CTGO.SORT_SEQ ASC	\n");
		
		return sql.toString();
    }                  
}

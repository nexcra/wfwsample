/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemInsDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ȸ�� > ȸ������ó��
*   �������  : golf 
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
* Golf
* @author	(��)�̵������  
* @version	1.0  
******************************************************************************/
public class GolfMemJoinNocardDaoProc extends AbstractProc {

	public static final String TITLE = "ȸ������ó��";

	public GolfMemJoinNocardDaoProc() {}
	
	public DbTaoResult execute(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {

		String title = TITLE;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DbTaoResult result =  new DbTaoResult(title);
		
		try {
			// 01. ��������üũ
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			String memId = userEtt.getAccount();

			conn = context.getDbConnection("default", null);
			//��ȸ ----------------------------------------------------------			
			String sql = this.getSelectQuery();   

			if(!"".equals(memId) && memId != null)
			{			 
				// �Է°� (INPUT)         
				int idx = 0;
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(++idx, memId);
				rs = pstmt.executeQuery();
				
				boolean ckh = false;
				boolean ckh2 = false;
				int smartGrd = 0;
				int memGrd = 0;
							
				if(rs != null) {
					while(rs.next())  {
	
						result.addString("CDHD_ID" 		,rs.getString("CDHD_ID") );
						result.addString("SECE_YN" 		,rs.getString("SECE_YN") );
						result.addString("JUMIN_NO" 	,rs.getString("JUMIN_NO") );						
						result.addString("memGrade"		,rs.getString("GOLF_CMMN_CODE_NM") );
						result.addString("REJOIN"		,rs.getString("REJOIN") );
						result.addString("CTGO_SEQ"		,rs.getString("CTGO_SEQ") );
						result.addString("END_DATE"		,rs.getString("END_DATE") );
						result.addString("RESULT", "00"); //������
												
						if (rs.getInt("GOLF_CMMN_CODE") == 22){
							smartGrd = rs.getInt("GOLF_CMMN_CODE"); // ��ȸ����							
						}
						
						if (rs.getInt("GOLF_CMMN_CODE") != 22){							
							memGrd = rs.getInt("GOLF_CMMN_CODE"); // !��ȸ����
						}
					
					}

					if ( (smartGrd>0 && memGrd > 0) || (smartGrd == 0 && memGrd > 0) ){
						result.addInt("smartGrd", smartGrd ); // ��ȸ����
						result.addInt("intMemGrade", memGrd ); // ��������
					}
					
					if ( smartGrd>0 && memGrd == 0){
						result.addInt("smartGrd", smartGrd ); // ��ȸ����
						result.addInt("intMemGrade", smartGrd ); // !��ȸ����
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
			

	public DbTaoResult tm_execute(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {

		String title = TITLE;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DbTaoResult result =  new DbTaoResult(title);

		
		try {
			// 01. ��������üũ
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			String memSocid = "";
			memSocid = userEtt.getSocid();
			
			if(GolfUtil.empty(memSocid)){
				memSocid = data.getString("memSocid");
			}
			
			conn = context.getDbConnection("default", null);
			//��ȸ ----------------------------------------------------------			
			String sql = this.getTmSelectQuery();   

			if(!"".equals(memSocid) && memSocid != null )
			{			
				// �Է°� (INPUT)         
				int idx = 0;
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(++idx, memSocid);
				rs = pstmt.executeQuery();
							
				if(rs != null) {
					while(rs.next())  {
	
						result.addString("JOIN_CHNL"	,rs.getString("GOLF_CDHD_GRD_CLSS") );
						result.addString("JUMIN_NO" 	,rs.getString("JUMIN_NO") );
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
        
		sql.append("\n SELECT T1.CDHD_ID, SECE_YN, GOLF_CMMN_CODE, GOLF_CMMN_CODE_NM, T1.JUMIN_NO	");
		sql.append("\n , CASE WHEN ADD_MONTHS(TO_DATE(SUBSTR(T1.SECE_ATON,1,8)),1)>SYSDATE THEN 'Y' ELSE 'N' END AS REJOIN	");
		sql.append("\n , T3.CDHD_CTGO_SEQ_NO CTGO_SEQ	");
		sql.append("\n , TO_CHAR(TO_DATE(ACRG_CDHD_END_DATE),'YYYY-MM-DD') END_DATE	");
		sql.append("\n FROM BCDBA.TBGGOLFCDHD T1	");
		sql.append("\n JOIN BCDBA.TBGGOLFCDHDGRDMGMT T2 ON T1.CDHD_ID=T2.CDHD_ID	");
		sql.append("\n JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T3 ON T2.CDHD_CTGO_SEQ_NO=T3.CDHD_CTGO_SEQ_NO	");
		sql.append("\n JOIN BCDBA.TBGCMMNCODE T4 ON T3.CDHD_SQ2_CTGO=T4.GOLF_CMMN_CODE AND T4.GOLF_CMMN_CLSS='0005'	");
		sql.append("\n WHERE T1.CDHD_ID=?	");
		sql.append("\n ORDER BY T3.SORT_SEQ	");
		
		return sql.toString();
    }

	/** ***********************************************************************
    * TM ȸ������ �˾ƺ���.    
    ************************************************************************ */ 
    private String getTmSelectQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\t	SELECT ROWNUM RNUM, MB_CDHD_NO, TB_RSLT_CLSS, JUMIN_NO, RND_CD_CLSS, GOLF_CDHD_GRD_CLSS, WK_DATE, RCRU_PL_CLSS	\n");
		sql.append("\t	FROM (	\n");
		sql.append("\t	    SELECT MB_CDHD_NO, TB_RSLT_CLSS, JUMIN_NO, RND_CD_CLSS, GOLF_CDHD_GRD_CLSS, WK_DATE, RCRU_PL_CLSS	\n");
		sql.append("\t	    FROM BCDBA.TBLUGTMCSTMR	\n");
		sql.append("\t	    WHERE TB_RSLT_CLSS='01' AND RND_CD_CLSS='2' AND JUMIN_NO=? AND WK_DATE>=TO_CHAR(SYSDATE-365,'YYYYMMDD')	\n");
		sql.append("\t	    ORDER BY GOLF_CDHD_GRD_CLSS DESC, WK_DATE DESC	\n");
		sql.append("\t	) WHERE ROWNUM=1	\n");
		return sql.toString();
    }
    
}

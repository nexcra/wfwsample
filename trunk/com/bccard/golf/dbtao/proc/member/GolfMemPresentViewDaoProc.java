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

import com.bccard.golf.common.AppConfig;
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
public class GolfMemPresentViewDaoProc extends AbstractProc {

	public static final String TITLE = "ȸ������ó��";

	public GolfMemPresentViewDaoProc() {}
	
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

			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, memId);
			rs = pstmt.executeQuery();
						
			if(rs != null) {
				while(rs.next())  {

					result.addString("PRE_NAME" 		,rs.getString("PRE_NAME") );
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
	
	public DbTaoResult execute_mem(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {

		String title = TITLE;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DbTaoResult result =  new DbTaoResult(title);

		
		try {
			String cpn_code = data.getString("cpn_code");
			String vipCardPayAmt = data.getString("vipCardPayAmt");
			debug("## ȸ����� �������� execute_mem | vipCardPayAmt : "+vipCardPayAmt);
			
			conn = context.getDbConnection("default", null);
			//��ȸ ----------------------------------------------------------			
			String sql = this.getSelectMemQuery(cpn_code);   

			// �Է°� (INPUT)        
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
						
			if(rs != null) {
				while(rs.next())  {

					result.addString("GRD_SEQ" 			,rs.getString("GRD_SEQ") );
					
					if (rs.getString("GRD_SEQ").equals("27")){//27 : Smart1000->������1000��
						result.addString("GRD_NM" 			,rs.getString("GRD_NM") );
						result.addString("GRD_NM2" 			, rs.getString("GRD_NM")+"(������1,000��)");
					}else {
						result.addString("GRD_NM" 			,rs.getString("GRD_NM") );
					}
					
					result.addString("GRD_SQ2" 			,rs.getString("GRD_SQ2") );
					
					if(!"0".equals(vipCardPayAmt))
					{
						if("Gold".equals(rs.getString("GRD_NM")))
						{
							result.addString("ANL_FEE" 			,GolfUtil.comma("15000") );
						}
						else
						{
							if (rs.getString("GRD_SEQ").equals(AppConfig.getDataCodeProp("0052CODE11"))){//27 : Smart1000->������1000��								
								result.addString("ANL_FEE" 		,GolfUtil.comma(rs.getString("MO_MSHP_FEE")) );
							}else{								
								result.addString("ANL_FEE" 			,GolfUtil.comma(rs.getString("ANL_FEE")) );
							}
						}
					}else if (rs.getString("GRD_SEQ").equals(AppConfig.getDataCodeProp("0052CODE11"))){//27 : Smart1000->������1000��						
						result.addString("ANL_FEE" 		,GolfUtil.comma(rs.getString("MO_MSHP_FEE")) );
					}else{						
						result.addString("ANL_FEE" 			,GolfUtil.comma(rs.getString("ANL_FEE")) );
					}													
					
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
	
	// ȸ���Ⱓ �����ϱ⿡�� ȸ������ �������� �Լ�
	public DbTaoResult execute_extend(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {

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
			String sql = this.getSelectMemExtendQuery();   

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
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */ 
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
        
        sql.append("\n");
		sql.append("\n SELECT GOLF_CMMN_CODE_NM AS PRE_NAME 	");
		sql.append("\n FROM BCDBA.TBGCDHDRIKMGMT T1	");
		sql.append("\n JOIN BCDBA.TBGCMMNCODE T2 ON T1.GOLF_TMNL_GDS_CODE=T2.GOLF_CMMN_CODE AND GOLF_CMMN_CLSS='0044'	");
		sql.append("\n WHERE CDHD_ID=?	");
		
		return sql.toString();
    }
			
	/** ***********************************************************************
    * ����ȸ���� ����Ʈ�� �����´�.   
    * �����ȸ�� ����Ʈ�� ī��ȸ�� �߰�(���ΰ��� ��ȸ�� Smart1000) - 20110621
    ************************************************************************ */ 
    private String getSelectMemQuery(String cpn_code){
        StringBuffer sql = new StringBuffer();

        sql.append("\n");
		sql.append("\t SELECT CTGO.CDHD_CTGO_SEQ_NO GRD_SEQ, CODE.GOLF_CMMN_CODE_NM GRD_NM	\n");
		sql.append("\t , CTGO.ANL_FEE, CTGO.MO_MSHP_FEE, TO_NUMBER(CTGO.CDHD_SQ2_CTGO) GRD_SQ2, EXPL	\n");
		sql.append("\t FROM BCDBA.TBGGOLFCDHDCTGOMGMT CTGO	\n");
		sql.append("\t JOIN BCDBA.TBGCMMNCODE CODE ON CODE.GOLF_CMMN_CODE=CTGO.CDHD_SQ2_CTGO AND CODE.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t WHERE ( CTGO.USE_CLSS='Y' AND ANL_FEE>0 OR CTGO.CDHD_CTGO_SEQ_NO='27')			\n"); //27 : Smart1000->������1000��
		
		// e-champ ȸ�� ���Խÿ��� ������ ����Ǵ� ����
		if("EVENTECHAMP201007".equals(cpn_code.toString().toUpperCase()) || "EVENTLETTER08".equals(cpn_code.toString().toUpperCase()) || "EVENTTHEBC08".equals(cpn_code.toString().toUpperCase())){
			sql.append("\t AND CTGO.CDHD_CTGO_SEQ_NO='17'	\n");
		}
		
		sql.append("\t ORDER BY CTGO.SORT_SEQ ASC	\n");
		
		return sql.toString();
    }
			
	/** ***********************************************************************
    * ����ȸ���� ����Ʈ�� �����´�.    
    ************************************************************************ */ 
    private String getSelectMemExtendQuery(){
        StringBuffer sql = new StringBuffer();

        sql.append("\n");
		sql.append("\t SELECT CTGO.CDHD_CTGO_SEQ_NO GRD_SEQ, CODE.GOLF_CMMN_CODE_NM GRD_NM	\n");
		sql.append("\t , CTGO.ANL_FEE, CTGO.MO_MSHP_FEE, TO_NUMBER(CTGO.CDHD_SQ2_CTGO) GRD_SQ2	\n");
		sql.append("\t FROM BCDBA.TBGGOLFCDHDCTGOMGMT CTGO	\n");
		sql.append("\t JOIN BCDBA.TBGCMMNCODE CODE ON CODE.GOLF_CMMN_CODE=CTGO.CDHD_SQ2_CTGO AND CODE.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t WHERE CTGO.USE_CLSS='Y' AND CTGO.CDHD_CTGO_SEQ_NO=? 	\n");
		sql.append("\t ORDER BY CTGO.SORT_SEQ ASC	\n");
		
		return sql.toString();
    }
    
}

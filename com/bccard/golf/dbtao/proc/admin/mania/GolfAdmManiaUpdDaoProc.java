/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmManiaChgDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���ΰ�
*   ����      : ������ �����帮�������ν�û���� ����ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-20
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.mania;

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

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmManiaUpdDaoProc extends AbstractProc {

	public static final String TITLE = "������ �����帮�������ν�û���� ����ó��";

	/** *****************************************************************
	 * GolfAdmManiaChgDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmManiaUpdDaoProc() {}
	
	/**
	 * ������ ���������ν�û ���α׷� ���� ó��
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Writer writer = null;
		Reader reader = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			// Ű�������� ��������/���/ �Ϲݼ��� ���� ---------------------------------------------
			String prize_yn		= data.getString("PRIZE_YN");
			String sttl_amt		= data.getString("STTL_AMT");
			String str_plc		= data.getString("STR_PLC");
			String pic_date		= data.getString("PIC_DATE");
			String start_hh		= data.getString("START_HH");
			String start_mi		= data.getString("START_MI");
			String toff_date 	= data.getString("TOFF_DATE");
			String end_hh		= data.getString("END_HH");
			String end_mi		= data.getString("END_MI");
			String gcc_nm		= data.getString("GCC_NM");
			String ckd_code		= data.getString("CKD_CODE");
			String tk_prs		= data.getString("TK_PRS");
			
			String zp1			= data.getString("ZP1");
			String zp2			= data.getString("ZP2");
			String addr			= data.getString("ADDR");
			String addr2		= data.getString("ADDR2");
			
            /*****************************************************************************/
            
			sql = this.getInsertQuery(prize_yn,sttl_amt,addr);//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			   int idx = 0;
			   
			   
			   if (!GolfUtil.isNull(prize_yn))	pstmt.setString(++idx, data.getString("PRIZE_YN") 	);	//���࿩�� (����/���) ������
			   if (GolfUtil.isNull(prize_yn)) pstmt.setString(++idx, data.getString("CNSL_YN")    	);	//��㿩�� (�Ϸ�/���) ������
			   
			  // if (!GolfUtil.isNull(sttl_amt)) pstmt.setString(++idx, data.getString("STTL_AMT") 	);	//�����帮�����󼼺��� ������
			   if (!GolfUtil.isNull(sttl_amt)) pstmt.setString(++idx, data.getString("STR_PLC")  	);	//�����帮�����󼼺��� ������
			   if (!GolfUtil.isNull(sttl_amt)) pstmt.setString(++idx, data.getString("PIC_DATE") 	);	//�����帮�����󼼺��� ������
			   if (!GolfUtil.isNull(sttl_amt)) pstmt.setString(++idx, start_hh+start_mi 		 	);	//�����帮�����󼼺��� ������
			   if (!GolfUtil.isNull(sttl_amt)) pstmt.setString(++idx, data.getString("TOFF_DATE") 	);	//�����帮�����󼼺��� ������
			   if (!GolfUtil.isNull(sttl_amt)) pstmt.setString(++idx, end_hh+end_mi 		  		);	//�����帮�����󼼺��� ������
			   if (!GolfUtil.isNull(sttl_amt)) pstmt.setString(++idx, data.getString("GCC_NM")    	);	//�����帮�����󼼺��� ������
			   if (!GolfUtil.isNull(sttl_amt)) pstmt.setString(++idx, data.getString("CKD_CODE")  	);	//�����帮�����󼼺��� ������
			   if (!GolfUtil.isNull(sttl_amt)) pstmt.setString(++idx, data.getString("TK_PRS")    	);	//�����帮�����󼼺��� ������
			   
			   pstmt.setString(++idx, zp1+""+zp2						);	//�������� ������
			   if (!GolfUtil.isNull(addr)) pstmt.setString(++idx, data.getString("ADDR")  			);	//�������� ������
			   if (!GolfUtil.isNull(addr)) pstmt.setString(++idx, data.getString("ADDR2")    		);	//�������� ������
			   
			   pstmt.setString(++idx, data.getString("ADMIN_NO") );
			   pstmt.setLong(++idx, data.getLong("RECV_NO") );
			   
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
			
			
			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
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
	
	//9���߰���
    /** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getInsertQuery(String prize_yn, String sttl_amt, String addr){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGAPLCMGMT SET	\n");
		
		if (!GolfUtil.isNull(prize_yn)) sql.append("\t  PGRS_YN=?,  \n"); 		//���࿩�� (����/���) ������
		if (GolfUtil.isNull(prize_yn)) sql.append("\t  CSLT_YN=?,  	\n");		//��㿩�� (�Ϸ�/���) ������
		
		//if (!GolfUtil.isNull(sttl_amt)) sql.append("\t  STTL_AMT=?,  	\n");	//�����帮�����󼼺��� ������
		if (!GolfUtil.isNull(sttl_amt)) sql.append("\t  DPRT_PL_INFO=?,  	\n");	//�����帮�����󼼺��� ������
		if (!GolfUtil.isNull(sttl_amt)) sql.append("\t  PU_DATE=?,  	\n");	//�����帮�����󼼺��� ������
		if (!GolfUtil.isNull(sttl_amt)) sql.append("\t  PU_TIME=?,  	\n");	//�����帮�����󼼺��� ������
		if (!GolfUtil.isNull(sttl_amt)) sql.append("\t  TEOF_DATE=?,  	\n");	//�����帮�����󼼺��� ������
		if (!GolfUtil.isNull(sttl_amt)) sql.append("\t  TEOF_TIME=?,  	\n");	//�����帮�����󼼺��� ������
		if (!GolfUtil.isNull(sttl_amt)) sql.append("\t  GREEN_NM=?,  		\n");	//�����帮�����󼼺��� ������
		if (!GolfUtil.isNull(sttl_amt)) sql.append("\t  GOLF_LMS_CAR_KND_CLSS=?,  	\n");	//�����帮�����󼼺��� ������
		if (!GolfUtil.isNull(sttl_amt)) sql.append("\t  RIDG_PERS_NUM=?,  		\n");	//�����帮�����󼼺��� ������
		
		sql.append("\t  ZP=?,  		\n");			//�������� ������
		if (!GolfUtil.isNull(addr)) sql.append("\t  ADDR=?,  	\n");			//�������� ������
		if (!GolfUtil.isNull(addr)) sql.append("\t  DTL_ADDR=?,  		\n");		//�������� ������
		
		sql.append("\t  CHNG_MGR_ID=?, 	\n");
		sql.append("\t  CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDD') 	\n");
		sql.append("\t WHERE APLC_SEQ_NO=?	\n");
        return sql.toString();
    }
    
}
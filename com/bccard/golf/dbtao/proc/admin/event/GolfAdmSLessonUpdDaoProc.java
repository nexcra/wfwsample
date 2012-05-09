/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmSLessonUpdDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ������ BC Golf �̺�Ʈ 
*   �������  : golf
*   �ۼ�����  : 2009-05-25
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.event;

import java.io.CharArrayReader;
import java.io.Reader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

public class GolfAdmSLessonUpdDaoProc extends AbstractProc{
	
	public static final String TITLE = "������ Ư�� ���� �̺�Ʈ ���� ó��";

	/** *****************************************************************
	 * GolfAdmLessonChgDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	
	public GolfAdmSLessonUpdDaoProc(){}
	
	/**
	 * ������ Ư�� ���� �̺�Ʈ ���� ó��
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data, Map paramMap) throws DbTaoException  {
		
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
            /*****************************************************************************/
            String img_nm = data.getString("IMG_NM");
			String evnt_clss				= "0003";
			sql = this.getInsertQuery(img_nm);//Insert Query
			pstmt = conn.prepareStatement(sql);
			int idx = 0;
			if (!GolfUtil.isNull(img_nm))	pstmt.setString(++idx, img_nm);		
			pstmt.setString(++idx, data.getString("EVNT_NM") ); 
			
			
			pstmt.setString(++idx, data.getString("EVNT_ST") );
			pstmt.setString(++idx, data.getString("EVNT_EN") );
			pstmt.setString(++idx, data.getString("LES_ST") );
			pstmt.setString(++idx, data.getString("LES_EN") );
			pstmt.setString(++idx, data.getString("LES_PAY_NOR") );
			pstmt.setString(++idx, data.getString("LES_PAY_DC") );
			
			pstmt.setString(++idx, data.getString("MO_PE") ); 
			pstmt.setString(++idx, data.getString("DISP_YN") ); 
			pstmt.setString(++idx, data.getString("EVNT_BNF") );
			pstmt.setString(++idx, data.getString("AFFI_FIRM") );
			pstmt.setString(++idx, evnt_clss );
			pstmt.setLong(++idx, data.getLong("EVNT_SEQ_NO") );

			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
			
			sql = this.getSelectForUpdateQuery();//������ȣ ����
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, data.getLong("EVNT_SEQ_NO"));
			debug("===================//// check :////////////////////3333333333333333 " +data.getLong("EVNT_SEQ_NO"));
            rs = pstmt.executeQuery();

//			if(rs.next()) {
//				java.sql.Clob clob = rs.getClob("CTNT");
//				writer = ((weblogic.jdbc.common.OracleClob)clob).getCharacterOutputStream();
//				reader = new CharArrayReader(data.getString("CTNT").toCharArray());
//				
//				char[] buffer = new char[1024];
//				int read = 0;
//				while ((read = reader.read(buffer,0,1024)) != -1) {
//					writer.write(buffer,0,read);
//				}
//				writer.flush();
//			}
			if (rs  != null) rs.close();
			if (pstmt != null) pstmt.close();
			
			if(result > 0) {
				conn.commit();
				paramMap.put("RESULTMOD", "00");
			} else {
				conn.rollback();
				paramMap.put("RESULTMOD", "01");
			}
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	
	
    /** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getInsertQuery(String img_nm){
        StringBuffer sql = new StringBuffer();
		sql.append("UPDATE BCDBA.TBGEVNTMGMT SET	\n");
		if (!GolfUtil.isNull(img_nm)) sql.append("\t 	 IMG_FILE_PATH=?,	");
		sql.append("\t  EVNT_NM=?, EVNT_STRT_DATE=?, EVNT_END_DATE=?, LESN_STRT_DATE=?, LESN_END_DATE=?, 	\n");
		sql.append("\t  LESN_NORM_COST=?, LESN_DC_COST=?, RCRU_PE_ORG_NUM=?, CTNT=EMPTY_CLOB(), BLTN_YN=?, 	\n");
		sql.append("\t  EVNT_BNFT_EXPL=?, AFFI_FIRM_EXPL=? \n");
		sql.append("\t WHERE EVNT_CLSS=? AND EVNT_SEQ_NO = ? \n");
        return sql.toString();
    }
    
	/** ***********************************************************************
     * CLOB Query�� �����Ͽ� �����Ѵ�.    
     ************************************************************************ */
     private String getSelectForUpdateQuery(){
 		 StringBuffer sql = new StringBuffer();
         sql.append("SELECT CTNT FROM BCDBA.TBGEVNTMGMT \n");
         sql.append("WHERE EVNT_SEQ_NO = ? \n");
         sql.append("FOR UPDATE \n");
 		return sql.toString();
 		
 		
     }
}

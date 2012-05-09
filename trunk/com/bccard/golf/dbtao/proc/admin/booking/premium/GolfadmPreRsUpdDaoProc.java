/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmGrUpdDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ ��ŷ ������ ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.booking.premium;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0  
******************************************************************************/
public class GolfadmPreRsUpdDaoProc extends AbstractProc {

	public static final String TITLE = "������ ��ŷ ������ ���� ó��";

	/** *****************************************************************
	 * GolfadmGrUpdDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfadmPreRsUpdDaoProc() {}
	
	/**
	 * ������ �������α׷� ��� ó��
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;

		String sv_code = "";
		String cdhd_id = "";
		String bokg_able = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
            /*****************************************************************************/
			String rsvt_YN = data.getString("RSVT_YN");
            String appr_opion = data.getString("APPR_OPION");
            String add_appr_opion = data.getString("ADD_APPR_OPION");
            String user_id = data.getString("USER_ID");

			sql = this.getInsertQuery(rsvt_YN);//Insert Query
			pstmt = conn.prepareStatement(sql);

            
			int idx = 0;
			pstmt.setString(++idx, data.getString("RSVT_YN") );
			pstmt.setString(++idx, data.getString("CTNT") );
			pstmt.setString(++idx, data.getString("RSVT_SQL_NO") );
			
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            
            
            // �ٽ� ��ŷ �� �� �ִ� ���·� �����ش�. ������� : N / �ӹ���� : I
            if(rsvt_YN.equals("N") || rsvt_YN.equals("I")){
				sql = this.getUpdateQuery();//Insert Query
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setString(1, data.getString("RSVT_ABLE_BOKG_TIME_SEQ_NO") );
				result = pstmt.executeUpdate();
	            if(pstmt != null) pstmt.close();
	            
	            // �ӹ���Ҹ� �� ��� �� ���� ������ ��� ������ҽ�Ų��.
	            if(rsvt_YN.equals("I")){
		            // �ش� ���� ��ȣ�� ������ ���� ������ ���̵� �����´�
					sql = this.getDetailQuery();//Insert Query
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, data.getString("RSVT_SQL_NO") );
					rs = pstmt.executeQuery();

					if(rs != null) {			 
						while(rs.next())  {	
							sv_code = rs.getString("SV_CODE");
							cdhd_id = rs.getString("CDHD_ID");
							bokg_able = rs.getString("BOKG_ABLE");
							
				            // �ش� ���̵�� ����ǰ� �ش� ���� ������ ������ ��� ��� ��Ų��. 
			            	sql = this.getUpdAllQuery();//Update Query
							pstmt = conn.prepareStatement(sql);
							
							pstmt.setString(1, sv_code );
							pstmt.setString(2, cdhd_id );
							pstmt.setString(3, bokg_able );
							pstmt.executeUpdate();
				            if(pstmt != null) pstmt.close();
						}
						
					}

		            if(pstmt != null) pstmt.close();
		            if(rs != null) rs.close();
	            }
            }
            /////////////////////////////////////////////

			if(result>0) {
				conn.commit();
			} else {
				conn.rollback();
			}	
			
			conn.setAutoCommit(true);
			
            //ȸ���� ���� : / �߰�
            if(!add_appr_opion.equals("")){
            	sql = this.getUpdCdhdQuery();
            	pstmt = conn.prepareStatement(sql);
            	
            	//������ �ƴϸ� /�߰�
            	if(!appr_opion.equals("")) add_appr_opion = "/"+ add_appr_opion;
            	
            	pstmt.setString(1, appr_opion + add_appr_opion);
            	pstmt.setString(2, user_id);
            	result = pstmt.executeUpdate();
            	
            }
			
		
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(rs != null) rs.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	
	
    /** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getInsertQuery(String rsvt_YN){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGRSVTMGMT SET	\n");
		sql.append("\t  RSVT_YN=?, CTNT=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");

        if(rsvt_YN.equals("N") || rsvt_YN.equals("I")){
        	sql.append("\t  , CNCL_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
        }else{
        	sql.append("\t  , CNCL_ATON=''	\n");
        }

		sql.append("\t WHERE GOLF_SVC_RSVT_NO=?	\n");
        return sql.toString();
    }
     
 	/** ***********************************************************************
	* CLOB Query�� �����Ͽ� �����Ѵ�.    - �ٽ� ��ŷ�� �� �ִ� ���·� ������.
	************************************************************************ */
	private String getUpdateQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t	UPDATE BCDBA.TBGRSVTABLEBOKGTIMEMGMT SET	\n");
		sql.append("\t	BOKG_RSVT_STAT_CLSS='0001'					\n");
		sql.append("\t	WHERE RSVT_ABLE_BOKG_TIME_SEQ_NO=?			\n");
		    	  
		return sql.toString();
	}
      
   	/** ***********************************************************************
    * �ӹ������ ��� �ش���̵��� ���� ����Ǽ����� ��� ��ҽ�Ų��.
    ************************************************************************ */
    private String getUpdAllQuery(){
      StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	UPDATE (SELECT * FROM BCDBA.TBGRSVTMGMT T1	\n");
		sql.append("\t	JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_BOKG_TIME_SEQ_NO=T2.RSVT_ABLE_BOKG_TIME_SEQ_NO	\n");
		sql.append("\t	JOIN BCDBA.TBGRSVTABLESCDMGMT T3 ON T2.RSVT_ABLE_SCD_SEQ_NO=T3.RSVT_ABLE_SCD_SEQ_NO	\n");
		sql.append("\t	WHERE SUBSTR(GOLF_SVC_RSVT_NO,5,1)=? AND CDHD_ID=? AND RSVT_YN='Y'	\n");
		sql.append("\t	AND T3.BOKG_ABLE_DATE||T2.BOKG_ABLE_TIME>?	\n");
		sql.append("\t	) SET RSVT_YN='N'	\n");      	  
		return sql.toString();
    }
      
   	/** ***********************************************************************
    * �����ȣ�� ���� ������ �����´�
    ************************************************************************ */
    private String getDetailQuery(){
      StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	SELECT SUBSTR(T1.GOLF_SVC_RSVT_MAX_VAL,5,1) SV_CODE, T1.CDHD_ID, (TRIM(T3.BOKG_ABLE_DATE)||TRIM(T2.BOKG_ABLE_TIME)) BOKG_ABLE	\n");
		sql.append("\t	FROM BCDBA.TBGRSVTMGMT T1	\n");
		sql.append("\t	JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_BOKG_TIME_SEQ_NO=T2.RSVT_ABLE_BOKG_TIME_SEQ_NO	\n");
		sql.append("\t	JOIN BCDBA.TBGRSVTABLESCDMGMT T3 ON T2.RSVT_ABLE_SCD_SEQ_NO=T3.RSVT_ABLE_SCD_SEQ_NO	\n");
		sql.append("\t	WHERE T1.GOLF_SVC_RSVT_NO=?	\n");      	  
		return sql.toString();
    }
    
 	/** ***********************************************************************
     * �ش�ȸ�������� �򰡼�������
     ************************************************************************ */
     private String getUpdCdhdQuery(){
       StringBuffer sql = new StringBuffer();
 		sql.append("\n  UPDATE BCDBA.TBGGOLFCDHD   SET						");
 		sql.append("\n   APPR_OPION =  ?									");
 		sql.append("\n  WHERE CDHD_ID = ?									");
 		
     return sql.toString();
     }
     
}

/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmBkTimeRegDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ �����̾���ŷ ƼŸ�� ��� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-20
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.booking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/
public class GolfadmBkTimeRegDaoProc extends AbstractProc {

	public static final String TITLE = "������ ƼŸ�� ��� ó��";

	/** *****************************************************************
	 * GolfAdmLessonInsDaoProc ���μ��� ������ 
	 * @param N/A
	 ***************************************************************** */
	public GolfadmBkTimeRegDaoProc() {}
	
	/**
	 * ������ ��ŷƼŸ�� ��� ó�� 
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		int result2 = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		
		GolfAdminEtt userEtt = null;
		String reg_ID = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			// 00. ��� ���� ����
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){	
				reg_ID		= (String)userEtt.getMemId();
			}
			
			String bkps_DATE = data.getString("BKPS_DATE");
			String par_FREE = data.getString("PAR_FREE");
			String sort = data.getString("SORT");
			String golf_RSVT_DAY_CLSS = "";
			
			bkps_DATE = GolfUtil.rplc(bkps_DATE, "-", "");
			par_FREE = GolfUtil.rplc(par_FREE, "-", "");
			
			// �����̾� ��ŷ ����� ��� ������ �߰�	golf_RSVT_DAY_CLSS =>	P:�����̾���ŷ D:�帲����������, T:TOP������ŷ
			if(sort.equals("0001")){
				golf_RSVT_DAY_CLSS = "P";
			}
			else if(sort.equals("1000")){
				golf_RSVT_DAY_CLSS = "T";
			}
				
			
			
			
			// 01. ���డ������ ���
			
			long max_DAY_SEQ_NO = 0L;

			/**SEQ_NO ��������**************************************************************/
            // 00. �̹� ����� �������� �˾ƺ���
			sql = this.getSearchQuery();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, bkps_DATE);
			pstmt.setString(2, data.getString("COURSE"));
			pstmt.setString(3, data.getString("SKY_CODE"));
	        
			rs = pstmt.executeQuery();
			if(rs.next()){
				max_DAY_SEQ_NO = rs.getLong("RSVT_ABLE_SCD_SEQ_NO");
			}else {
			
				sql = this.getNextValQuery(); 
	            pstmt = conn.prepareStatement(sql);
	            rs = pstmt.executeQuery();		
				if(rs.next()){
					max_DAY_SEQ_NO = rs.getLong("DAY_SEQ_NO");
				}
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
            /**Insert************************************************************************/
            sql = this.getInsertQuery();
			pstmt = conn.prepareStatement(sql);
			
			String flag = "";
			
			
			if (data.getString("CLOSE_YN").equals("Y")){
				flag = "Y";
			}else if (data.getString("HOLY_YN").equals("Y")){
				flag = "H";
			}else {
				flag = data.getString("FREE_YN");
			}
			
			
			int idx = 0;
			pstmt.setLong(++idx, max_DAY_SEQ_NO );					//���డ�������Ϸù�ȣ
			pstmt.setString(++idx, data.getString("GR_SEQ_NO") );	//���ް������ȣ
			pstmt.setString(++idx, data.getString("COURSE") );		//�����ڽ�
			pstmt.setString(++idx, bkps_DATE );						//��ŷ���ɳ�¥
			//pstmt.setString(++idx, data.getString("FREE_YN") );		//���忩��
			pstmt.setString(++idx, flag );							//������/���忩��
			
			pstmt.setString(++idx, par_FREE );						//��3��ŷ������
			pstmt.setString(++idx, data.getString("FREE_MEMO") );	//������ ����
			pstmt.setString(++idx, StrUtil.isNull(data.getString("SKY_CODE"), "") );	//��ī��72Ȧ�ڵ�
			pstmt.setString(++idx, reg_ID );						//��ϰ�����ID
			pstmt.setString(++idx, golf_RSVT_DAY_CLSS );			//���������ϱ����ڵ�
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
			

			String bkps_TIME = data.getString("BKPS_TIME");
			String[] arr_bkps_time= bkps_TIME.split(",");
			String bkps_TIME_VAL = "";
			int bkps_TIME_LEN = 0;
			
			for(int i = 0 ; i < arr_bkps_time.length; i++) {
				
				bkps_TIME_VAL = arr_bkps_time[i].trim();
				bkps_TIME_LEN = bkps_TIME_VAL.length();
				
				if (bkps_TIME_LEN == 4){
					// 02. ���డ��ƼŸ�Ӱ���
					/**SEQ_NO ��������**************************************************************/
					sql = this.getNextValQuery2(); 
		            pstmt = conn.prepareStatement(sql);
		            rs = pstmt.executeQuery();			
					long max_TIME_SEQ_NO = 0L;
					if(rs.next()){
						max_TIME_SEQ_NO = rs.getLong("TIME_SEQ_NO");
					}
					if(rs != null) rs.close();
		            if(pstmt != null) pstmt.close();
		            
		            /**Insert************************************************************************/
		            if("1000".equals(sort)){
		            	sql = this.getInsertQuery3();
		            }
		            else
		            {
		            	sql = this.getInsertQuery2();
		            }
		           
		            
					pstmt = conn.prepareStatement(sql);
					
					idx = 0;
					pstmt.setLong(++idx, max_TIME_SEQ_NO );					//���డ�ɽð��Ϸù�ȣ
					pstmt.setLong(++idx, max_DAY_SEQ_NO );					//���డ�������Ϸù�ȣ
					pstmt.setString(++idx, data.getString("GR_SEQ_NO") );	//���ް������ȣ
					pstmt.setString(++idx, bkps_TIME_VAL );					//��ŷ���ɽð�
					pstmt.setString(++idx, reg_ID );						//��ϰ�����ID
					pstmt.setString(++idx, sort );
					if("1000".equals(sort)){
					pstmt.setString(++idx, data.getString("evnt_yn") );
			        }
					result2 = pstmt.executeUpdate();
		            if(pstmt != null) pstmt.close();
				}
			}
			            
			// 03. ó�� ���
			if(result > 0 || result2 > 0) {
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


	/** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("INSERT INTO BCDBA.TBGRSVTABLESCDMGMT (	\n");
		sql.append("\t  RSVT_ABLE_SCD_SEQ_NO, AFFI_GREEN_SEQ_NO, GOLF_RSVT_CURS_NM, BOKG_ABLE_DATE, RESM_YN, 	\n");
		sql.append("\t  PAR_3_BOKG_RESM_DATE, RESM_DAY_RSON_CTNT, SKY72_HOLE_CODE, REG_MGR_ID, REG_ATON, GOLF_RSVT_DAY_CLSS 	\n");
		sql.append("\t ) VALUES (	\n");	
		sql.append("\t  ?,?,?,?,?,	\n");
		sql.append("\t  ?,?,?,?,TO_CHAR(SYSDATE,'YYYYMMDD'),?	\n");
		sql.append("\t \n)");
        return sql.toString();
    }
    
    /** ***********************************************************************
     * Query�� �����Ͽ� �����Ѵ�.    
     ************************************************************************ */
     private String getInsertQuery2(){
         StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("INSERT INTO BCDBA.TBGRSVTABLEBOKGTIMEMGMT (	\n");
 		sql.append("\t  RSVT_ABLE_BOKG_TIME_SEQ_NO, RSVT_ABLE_SCD_SEQ_NO, AFFI_GREEN_SEQ_NO, BOKG_ABLE_TIME, REG_MGR_ID, REG_ATON, EPS_YN ,BOKG_RSVT_STAT_CLSS   	\n");
 		sql.append("\t ) VALUES (	\n");	
 		sql.append("\t  ?,?,?,?,?,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), 'N', ?	\n");
 		sql.append("\t \n)");
        return sql.toString();
     }
     /** ***********************************************************************
      * Query�� �����Ͽ� �����Ѵ�.    
      ************************************************************************ */
      private String getInsertQuery3(){
          StringBuffer sql = new StringBuffer();
  		sql.append("\n");
  		sql.append("INSERT INTO BCDBA.TBGRSVTABLEBOKGTIMEMGMT (	\n");
  		sql.append("\t  RSVT_ABLE_BOKG_TIME_SEQ_NO, RSVT_ABLE_SCD_SEQ_NO, AFFI_GREEN_SEQ_NO, BOKG_ABLE_TIME, REG_MGR_ID, REG_ATON, EPS_YN ,BOKG_RSVT_STAT_CLSS, EVNT_YN   	\n");
  		sql.append("\t ) VALUES (	\n");	
  		sql.append("\t  ?,?,?,?,?,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), 'N', ?, ?	\n");
  		sql.append("\t \n)");
         return sql.toString();
      }

     /** ***********************************************************************
     * ���� ��ϵǾ��ִ� �ش糯¥�� �����Ѵ�. �����Ͽ� �����Ѵ�. 
     ************************************************************************ */
     private String getSearchQuery(){
         StringBuffer sql = new StringBuffer();
 		sql.append("\n");
        sql.append("SELECT RSVT_ABLE_SCD_SEQ_NO FROM BCDBA.TBGRSVTABLESCDMGMT  \n");
        sql.append("\t WHERE BOKG_ABLE_DATE = ? AND GOLF_RSVT_CURS_NM = ?  AND SKY72_HOLE_CODE = ? \n"); 

        
 		return sql.toString();
     }
     
    /** ***********************************************************************
    * Max IDX Query�� �����Ͽ� �����Ѵ�. 
    ************************************************************************ */
    private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
        sql.append("SELECT NVL(MAX(RSVT_ABLE_SCD_SEQ_NO),0)+1 DAY_SEQ_NO FROM BCDBA.TBGRSVTABLESCDMGMT \n");
		return sql.toString();
    }
    
    /** ***********************************************************************
     * Max IDX Query�� �����Ͽ� �����Ѵ�. 
     ************************************************************************ */
     private String getNextValQuery2(){
         StringBuffer sql = new StringBuffer();
 		sql.append("\n");
         sql.append("SELECT NVL(MAX(RSVT_ABLE_BOKG_TIME_SEQ_NO),0)+1 TIME_SEQ_NO FROM BCDBA.TBGRSVTABLEBOKGTIMEMGMT \n");
 		return sql.toString();
     }
}

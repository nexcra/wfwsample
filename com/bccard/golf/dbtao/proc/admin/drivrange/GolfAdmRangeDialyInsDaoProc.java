/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmRangeDialyInsDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ �帲 ���������� ���� ��� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-26	
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.drivrange;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.GolfadmBkTimeRegFormDaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmRangeDialyInsDaoProc extends AbstractProc {

	public static final String TITLE = "������ �帲 ���������� ���� ��� ó��";

	/** *****************************************************************
	 * GolfAdmCouponMutiDelDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmRangeDialyInsDaoProc() {}
	
	/**
	 * ������ �帲 ���������� ���� ��� ó��
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, HttpServletRequest request, TaoDataSet data, String[] start_hh, 
						String[] start_mi, String[] end_hh, String[] end_mi, String[] day_rsvt_num) throws DbTaoException  {

		int result = 0;
		int iCount = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			
			// ���� �ߺ� üũ
			int rangeDialyChk = this.getRangeDialyChk(conn, data);
			
			if (rangeDialyChk == 0) {
				
				String flag = "";
				
				if (data.getString("SLS_END_YN").equals("Y")){
					flag = "Y";
				}else if (data.getString("HOLY_YN").equals("Y")){
					flag = "H";
				}				
				
				if (flag.equals("H")) {		
					
					DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
					dataSet.setString("SORT", AppConfig.getDataCodeProp("DrivingRange"));
					dataSet.setString("DrivR", AppConfig.getDataCodeProp("DrivingRangeClss"));
					
					// 04.���� ���̺�(Proc) ��ȸ - ������
					GolfadmBkTimeRegFormDaoProc proc2 = (GolfadmBkTimeRegFormDaoProc)context.getProc("admBkTimeRegFormDaoProc");
					DbTaoResult titimeGreenList = (DbTaoResult) proc2.execute(context, request , dataSet);
					
					String greenNo = "";	
					
					for (int j=0; j<titimeGreenList.size(); j++){
						
						if (titimeGreenList != null && titimeGreenList.isNext()) {
							
							titimeGreenList.next();
				
							//��ȸ ----------------------------------------------------------
							
							sql = this.getNextValQuery1(); //���డ�������Ϸù�ȣ ����
				            pstmt = conn.prepareStatement(sql);
				            rs = pstmt.executeQuery();			
							long rsvtdialy_sql_no = 0L;
							if(rs.next()){
								rsvtdialy_sql_no = rs.getLong("RSVTDIALY_SQL_NO");
							}
							if(rs != null) rs.close();
				            if(pstmt != null) pstmt.close();
				            /*****************************************************************************/
				            
							sql = this.getInsertQuery1();//Insert Query
							pstmt = conn.prepareStatement(sql);
							
							int idx = 0;
							
							greenNo =  Integer.toString(titimeGreenList.getInt("SEQ_NO"));
													
							pstmt.setLong(++idx, rsvtdialy_sql_no );
							pstmt.setString(++idx, data.getString("RSVT_DATE") );
							pstmt.setString(++idx, flag );
							pstmt.setString(++idx, data.getString("RSVT_TOTAL_NUM") );
							pstmt.setString(++idx, data.getString("ADMIN_NO") );
							pstmt.setString(++idx, greenNo);							
							
							result = pstmt.executeUpdate();
				            if(pstmt != null) pstmt.close();
				            
				            /*****************************************************************************/
				            
				            sql = this.getNextValQuery2(); //���డ��ƼŸ���Ϸù�ȣ ����
				            pstmt = conn.prepareStatement(sql);
				            rs = pstmt.executeQuery();			
							long rsvttime_sql_no = 0L;
							if(rs.next()){
								rsvttime_sql_no = rs.getLong("RSVTTIME_SQL_NO");
							}
							if(rs != null) rs.close();
				            if(pstmt != null) pstmt.close();
				            /*****************************************************************************/
				            
				            for (int i = 0; i < start_hh.length; i++) {				
								if (start_hh[i] != null && start_hh[i].length() > 0) {
						            
						            sql = this.getInsertQuery2();//Insert Query
									pstmt = conn.prepareStatement(sql);
									
									idx = 0;
									pstmt.setLong(++idx, rsvttime_sql_no );
									pstmt.setString(++idx, start_hh[i]+start_mi[i] ); 
									pstmt.setString(++idx, end_hh[i]+end_mi[i] ); 
									pstmt.setString(++idx, day_rsvt_num[i] );
									pstmt.setLong(++idx, rsvtdialy_sql_no );
									pstmt.setString(++idx, data.getString("ADMIN_NO") );
									
									iCount += pstmt.executeUpdate();
									
									++rsvttime_sql_no;
						        }
				            }
				            
						}
						
			            if(rs != null) rs.close();
			            if(pstmt != null) pstmt.close();
						
					}

	            /*****************************************************************************/
		            
				}else {
					
					//��ȸ ----------------------------------------------------------
					
					sql = this.getNextValQuery1(); //���డ�������Ϸù�ȣ ����
		            pstmt = conn.prepareStatement(sql);
		            rs = pstmt.executeQuery();			
					long rsvtdialy_sql_no = 0L;
					if(rs.next()){
						rsvtdialy_sql_no = rs.getLong("RSVTDIALY_SQL_NO");
					}
					if(rs != null) rs.close();
		            if(pstmt != null) pstmt.close();
		            /*****************************************************************************/
		            
					sql = this.getInsertQuery1();//Insert Query
					pstmt = conn.prepareStatement(sql);
					
					int idx = 0;
					
					pstmt.setLong(++idx, rsvtdialy_sql_no );
					pstmt.setString(++idx, data.getString("RSVT_DATE") );
					pstmt.setString(++idx, flag );
					pstmt.setString(++idx, data.getString("RSVT_TOTAL_NUM") );
					pstmt.setString(++idx, data.getString("ADMIN_NO") );
					pstmt.setString(++idx, data.getString("SCH_GR_SEQ_NO") );				
					
					result = pstmt.executeUpdate();
		            if(pstmt != null) pstmt.close();
		            
		            /*****************************************************************************/
		            
		            sql = this.getNextValQuery2(); //���డ��ƼŸ���Ϸù�ȣ ����
		            pstmt = conn.prepareStatement(sql);
		            rs = pstmt.executeQuery();			
					long rsvttime_sql_no = 0L;
					if(rs.next()){
						rsvttime_sql_no = rs.getLong("RSVTTIME_SQL_NO");
					}
					if(rs != null) rs.close();
		            if(pstmt != null) pstmt.close();
		            /*****************************************************************************/
		            
		            for (int i = 0; i < start_hh.length; i++) {				
						if (start_hh[i] != null && start_hh[i].length() > 0) {
				            
				            sql = this.getInsertQuery2();//Insert Query
							pstmt = conn.prepareStatement(sql);
							
							idx = 0;
							pstmt.setLong(++idx, rsvttime_sql_no );
							pstmt.setString(++idx, start_hh[i]+start_mi[i] ); 
							pstmt.setString(++idx, end_hh[i]+end_mi[i] ); 
							pstmt.setString(++idx, day_rsvt_num[i] );
							pstmt.setLong(++idx, rsvtdialy_sql_no );
							pstmt.setString(++idx, data.getString("ADMIN_NO") );
							
							iCount += pstmt.executeUpdate();
							
							++rsvttime_sql_no;
				        }
		            }
		            
		            if(rs != null) rs.close();
		            if(pstmt != null) pstmt.close();
	            /*****************************************************************************/		
		            
		        
		            
				}
			
				//if(result > 0 && iCount == start_hh.length) {
				if(result > 0 ) {					
					conn.commit();
				} else {
					conn.rollback();
				}
				
			} else {
				result = 9;				
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
	
	/**
	 * ���� �ߺ� üũ
	 * @param context
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int getRangeDialyChk(Connection conn, TaoDataSet data) throws DbTaoException {

		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";

		try {
			
			sql = this.getSelectQuery();//Select Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;	
			pstmt.setString(++idx, data.getString("RSVT_DATE") );
			pstmt.setString(++idx, data.getString("SCH_GR_SEQ_NO") );
			
			rs = pstmt.executeQuery();
			
			if(rs != null && rs.next()) {
				result++;
			}
			
		} catch(Exception e) {
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
		}
		
		return result;
	}	
	
	
	/** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getInsertQuery1(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("INSERT INTO BCDBA.TBGRSVTABLESCDMGMT (	\n");
		sql.append("\t  RSVT_ABLE_SCD_SEQ_NO, RSVT_ABLE_DATE, RESM_YN, DLY_RSVT_ABLE_PERS, REG_MGR_ID, CHNG_MGR_ID, REG_ATON, CHNG_ATON, GOLF_RSVT_DAY_CLSS, AFFI_GREEN_SEQ_NO 	\n");
		sql.append("\t ) VALUES (	\n");	
		sql.append("\t  ?,?,?,?,?,0,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),NULL,'D', ?	\n");
		sql.append("\t \n)");	
	    return sql.toString();
    }
    
    private String getInsertQuery2(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("INSERT INTO BCDBA.TBGRSVTABLEBOKGTIMEMGMT (	\n");
		sql.append("\t  RSVT_ABLE_BOKG_TIME_SEQ_NO, RSVT_STRT_TIME, RSVT_END_TIME, DLY_RSVT_ABLE_PERS_NUM, RSVT_ABLE_SCD_SEQ_NO, REG_MGR_ID, CHNG_MGR_ID, REG_ATON, CHNG_ATON  	\n");
		sql.append("\t ) VALUES (	\n");	
		sql.append("\t  ?,?,?,?,?,?,0,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),NULL		\n");
		sql.append("\t \n)");	
	    return sql.toString();
    }
    
    /** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
 	private String getSelectQuery(){
         StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("SELECT	\n");
 		sql.append("\t 	RSVT_ABLE_SCD_SEQ_NO	\n");
 		sql.append("\t FROM BCDBA.TBGRSVTABLESCDMGMT	\n");
 		sql.append("\t WHERE GOLF_RSVT_DAY_CLSS = 'D'	\n");
 		sql.append("\t AND RSVT_ABLE_DATE = ?	\n");
 		sql.append("\t AND AFFI_GREEN_SEQ_NO = ?	\n");
 	    return sql.toString();
     }
 	
    /** ***********************************************************************
    * Max IDX Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getNextValQuery1(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
        sql.append("SELECT NVL(MAX(RSVT_ABLE_SCD_SEQ_NO),0)+1 RSVTDIALY_SQL_NO FROM BCDBA.TBGRSVTABLESCDMGMT \n");
		return sql.toString();
    }
    
    private String getNextValQuery2(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
        sql.append("SELECT NVL(MAX(RSVT_ABLE_BOKG_TIME_SEQ_NO),0)+1 RSVTTIME_SQL_NO FROM BCDBA.TBGRSVTABLEBOKGTIMEMGMT \n");
		return sql.toString();
    }
    
}

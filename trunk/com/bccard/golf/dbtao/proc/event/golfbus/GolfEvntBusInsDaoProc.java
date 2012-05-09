/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntBusInsDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ����� > �̺�Ʈ->����������̺�Ʈ->��� ó��
*   �������  : Golf
*   �ۼ�����  : 2009-09-30
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.event.golfbus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;

/** ****************************************************************************
 *  Golf
 * @author	Media4th
 * @version 1.0
 **************************************************************************** */
public class GolfEvntBusInsDaoProc extends DbTaoProc {
	/**
	 * Proc ����. 
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public TaoResult execute(Connection con, TaoDataSet dataSet) throws TaoException {

		PreparedStatement pstmt				= null;
		ResultSet rs 						= null;
		String title						= dataSet.getString("title");
		String actnKey 						= null;
		DbTaoResult result					= new DbTaoResult(title);
		
		try {
			actnKey 						= dataSet.getString("actnKey");
			int res 						= 0;
			int pidx 						= 0;
			int cntVal						= 0;
			String pgrs_yn					= "Y";	// W	��� -  28�� �̻� Y	��û -  28 �̸�

			
			if(!"".equals(dataSet.getString("teof_date")) && dataSet.getString("teof_date") != null)
			{
				
				long maxValue = this.getMaxQuery(con);

				//�ش� ��¥�� ��ϵ� ���� (��ŷȮ��)
				pstmt = con.prepareStatement(getCheckQuery());
				pidx = 0;				
				pstmt.setString(++pidx, dataSet.getString("teof_date"));				
				rs = pstmt.executeQuery();

				if ( rs.next() ) {
					cntVal = rs.getInt("cnt");	// ��ŷȮ���� ����
				}

				if ( (28-cntVal) < dataSet.getInt("arrCnt") ) {
					pgrs_yn = "W";				// �����
				}
				pstmt = con.prepareStatement(getInsQuery());
				pidx = 0;
				pstmt.setLong(++pidx, maxValue);
				pstmt.setString(++pidx, "9002");
				pstmt.setString(++pidx, pgrs_yn);
				pstmt.setString(++pidx, dataSet.getString("userId"));		// ������ ���̵�
				pstmt.setString(++pidx, dataSet.getString("co_nm"));		// ������ �̸�
				pstmt.setString(++pidx, dataSet.getString("email"));		// �̸���
				pstmt.setString(++pidx, dataSet.getString("hp_ddd_no"));	// �޴���
				pstmt.setString(++pidx, dataSet.getString("hp_tel_hno"));	// �޴���
				pstmt.setString(++pidx, dataSet.getString("hp_tel_sno"));	// �޴���
				pstmt.setString(++pidx, dataSet.getString("teof_date"));	// ��û����
				pstmt.setString(++pidx, dataSet.getString("green_nm"));		// ������ ��
				
				pstmt.setString(++pidx, dataSet.getString("golf_mgz_dlv_pl_clss"));		// ������ ��			
				
				//pstmt.setInt(++pidx, dataSet.getInt("arrCnt"));		// �����ο���
				pstmt.setString(++pidx, dataSet.getString("trAllValue"));	// �޸� �Ǵ� Ư�̻���,�䱸����,��û�ڸ��
				res = pstmt.executeUpdate();
				if(res>0) {
					result.addString("RESULT","00");
					result.addString("pgrs_yn", pgrs_yn);		// ��� �˾�â �б�
					con.commit();
				}			
				else {
					result.addString("RESULT","01");
					con.rollback();
				}
				 
			}
			else {
				result.addString("RESULT","01");
			}
			
			
			
			

		} catch(Exception e){
			// Ʈ������ �����϶��� �ѹ�			
			try { if( !con.getAutoCommit() ){ con.rollback(); } else {} } catch(Throwable ignore) {}			
			
		} finally {
			try { if( rs != null ){ rs.close(); } else {} } catch(Throwable ignore) {}
			try { if( pstmt != null ){ pstmt.close(); } else {} } catch(Throwable ignore) {}
			try { if( con != null ){ con.close(); } else {} } catch(Throwable ignore) {}
		}
		return result;
	}	

	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getCheckQuery() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	SELECT NVL(sum(RIDG_PERS_NUM),0) as cnt FROM BCDBA.TBGAPLCMGMT		 					");
		sql.append("\n	WHERE GOLF_SVC_APLC_CLSS = '9002' AND TEOF_DATE = ? AND PGRS_YN='B' 	");	
		return sql.toString();
	}	

	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getInsQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
		sql.append("\n	INSERT INTO BCDBA.TBGAPLCMGMT	(							");
		sql.append("\n		APLC_SEQ_NO ,	 		");
		sql.append("\n		GOLF_SVC_APLC_CLSS ,	");
		sql.append("\n		NUM_DDUC_YN,  			");
		sql.append("\n		PRZ_WIN_YN, 			");
		
		sql.append("\n		PGRS_YN,  				");
		sql.append("\n		CDHD_ID,  				");
		sql.append("\n		CO_NM,  				");
		sql.append("\n		EMAIL,  				");
		sql.append("\n		HP_DDD_NO,  			");
		sql.append("\n		HP_TEL_HNO,				");
		sql.append("\n		HP_TEL_SNO,				");
		sql.append("\n		TEOF_DATE, 				");
		sql.append("\n		TEOF_TIME, 				");
		sql.append("\n		GREEN_NM,  				");
		sql.append("\n		RIDG_PERS_NUM, 			");
		sql.append("\n		MEMO_EXPL, 				");
		sql.append("\n		REG_ATON 				");
		
		sql.append("\n	) VALUES (					");
		sql.append("\n		?, ?, 'N', 'N',  ");
		sql.append("\n		?, ?, ?, ?, ?, ?, ?, ?, TO_CHAR(SYSDATE,'hh24MISS'), ?, ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDhh24MISS')  ");
		sql.append("\n	)	");
		return sql.toString();
	}

	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private long getMaxQuery(Connection con) throws BaseException {

		PreparedStatement pstmt1 = null;        
        ResultSet rset1 = null;        
        String sql = "SELECT NVL(MAX(APLC_SEQ_NO),0)+1 APLC_SEQ_NO FROM BCDBA.TBGAPLCMGMT";
        long pidx = 0;
        try {        	
            pstmt1 = con.prepareStatement(sql);
            rset1 = pstmt1.executeQuery();   
			if (rset1.next()) {				
                pidx = rset1.getLong(1);
			}
        } catch (Throwable t) {          // SQLException �� ���� ó�� : ������ ������ ������ �߻�
        	BaseException exception = new BaseException(t);          
            throw exception;
        } finally {
            try { if ( rset1  != null ) { rset1.close(); } else { ; }  } catch ( Throwable ignored) {}
            try { if ( pstmt1 != null ) { pstmt1.close(); } else { ; } } catch ( Throwable ignored) {}
            try { if ( con    != null ) { con.close(); } else { ; }    } catch ( Throwable ignored) {}
        }
        return pidx;

	}
}

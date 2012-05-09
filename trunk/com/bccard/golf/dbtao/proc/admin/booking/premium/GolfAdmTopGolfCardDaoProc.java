/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmTopGolfCardDaoProc
*   �ۼ���    : ������
*   ����      : ������ >  ��ŷ > TOP����ī�� ���� ��ŷ 
*   �������  : golf
*   �ۼ�����  : 2010-10-15
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   ������� 
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.booking.premium;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

public class GolfAdmTopGolfCardDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmSpecialBookingDaoProc ���μ��� ������
	 * @param N/A 
	 ***************************************************************** */
	public GolfAdmTopGolfCardDaoProc() {}	  

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			
			//��ȸ ----------------------------------------------------------
			String sql = this.getSelectQuery(data);   

			long page_no			= data.getLong("PAGE_NO");               //��������ȣ
			long record_size		= data.getLong("RECORD_SIZE");           //�������� ��µ� ����

			String green_nm			= data.getString("green_nm");            //����������
			String pgrs_yn			= data.getString("pgrs_yn");      //�����ڵ�
			
			String sch_date_gubun   = data.getString("sch_date_gubun");
			String sch_reg_aton_st	= data.getString("sch_reg_aton_st");     //��ȸ ��û ������ 
			String sch_reg_aton_ed	= data.getString("sch_reg_aton_ed"); 	 //��ȸ ��û ������
			
			String sch_type			= data.getString("sch_type");            //�̸�,ID��ȸ ����     
			String search_word		= data.getString("search_word");         //��ȸ ��     
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());

			pstmt.setLong(++idx, record_size);
			pstmt.setLong(++idx, record_size);
			pstmt.setLong(++idx, page_no);
			pstmt.setLong(++idx, record_size);
			pstmt.setLong(++idx, page_no);

				
				if(!green_nm.equals("")){
					pstmt.setString(++idx,green_nm);
				}
	
				if(!pgrs_yn.equals("")){
					pstmt.setString(++idx,pgrs_yn);
				}
				
				if(!sch_date_gubun.equals("")){
						pstmt.setString(++idx,sch_reg_aton_st.replaceAll("-",""));
						pstmt.setString(++idx,sch_reg_aton_ed.replaceAll("-",""));
				}

				if(!search_word.equals("")){
					if(sch_type.equals("ID")){
						pstmt.setString(++idx,search_word);
					}else if(sch_type.equals("NAME")){
						pstmt.setString(++idx,search_word);
					}else{
						pstmt.setString(++idx,search_word);
						pstmt.setString(++idx,search_word);
					}
				}
				pstmt.setLong(++idx, page_no);
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("APLC_SEQ_NO",rs.getString("APLC_SEQ_NO"));
					result.addString("PGRS_YN",rs.getString("PGRS_YN"));		//�������
					result.addString("GREEN_NM",rs.getString("GREEN_NM"));	//�������
					result.addString("TEOF_DATE",DateUtil.format(rs.getString("TEOF_DATE"),"yyyyMMdd","yyyy-MM-dd"));	//��ŷ����
					
					String teof_time = rs.getString("TEOF_TIME");
					teof_time = teof_time.substring(0,2) + "�ô�";
					result.addString("TEOF_TIME",teof_time);	
						
					
					result.addString("CO_NM",rs.getString("CO_NM"));		//��û���̸�
					result.addString("CDHD_ID",rs.getString("CDHD_ID"));	//��û���̵�
					result.addString("HP_DDD_NO",rs.getString("HP_DDD_NO"));
					result.addString("HP_TEL_HNO",rs.getString("HP_TEL_HNO"));
					result.addString("HP_TEL_SNO",rs.getString("HP_TEL_SNO"));
					result.addString("MEMO_EXPL",rs.getString("MEMO_EXPL"));
					result.addString("BKG_PE_NM",rs.getString("BKG_PE_NM"));
					result.addString("REG_ATON",DateUtil.format(rs.getString("REG_ATON"),"yyyyMMdd","yyyy-MM-dd"));	//��û��
					
					result.addString("GOLF_LESN_RSVT_NO",rs.getString("GOLF_LESN_RSVT_NO"));		//ƼŸ��
					
					result.addString("RNUM",rs.getString("RNUM"));
					result.addInt("PAGE",rs.getInt("PAGE"));
					result.addInt("TOT_CNT",rs.getInt("TOT_CNT"));
					result.addString("LIST_NO",rs.getString("LIST_NO"));
					result.addString("RESULT", "00"); //������*/
				}
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	
	
	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute_excel(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			
			//��ȸ ----------------------------------------------------------
			String sql = this.getSelectXlsQuery(data);   

			String green_nm			= data.getString("green_nm");            //����������
			String pgrs_yn			= data.getString("pgrs_yn");      //�����ڵ�
			
			String sch_date_gubun   = data.getString("sch_date_gubun");
			String sch_reg_aton_st	= data.getString("sch_reg_aton_st");     //��ȸ ��û ������ 
			String sch_reg_aton_ed	= data.getString("sch_reg_aton_ed"); 	 //��ȸ ��û ������
			
			String sch_type			= data.getString("sch_type");            //�̸�,ID��ȸ ����     
			String search_word		= data.getString("search_word");         //��ȸ ��     
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());


				
				if(!green_nm.equals("")){
					pstmt.setString(++idx,green_nm);
				}
	
				if(!pgrs_yn.equals("")){
					pstmt.setString(++idx,pgrs_yn);
				}
				
				if(!sch_date_gubun.equals("")){
						pstmt.setString(++idx,sch_reg_aton_st.replaceAll("-",""));
						pstmt.setString(++idx,sch_reg_aton_ed.replaceAll("-",""));
				}

				if(!search_word.equals("")){
					if(sch_type.equals("ID")){
						pstmt.setString(++idx,search_word);
					}else if(sch_type.equals("NAME")){
						pstmt.setString(++idx,search_word);
					}else{
						pstmt.setString(++idx,search_word);
						pstmt.setString(++idx,search_word);
					}
				}
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("RN",rs.getString("RN"));
					result.addString("APLC_SEQ_NO",rs.getString("APLC_SEQ_NO"));
					result.addString("PGRS_YN",rs.getString("PGRS_YN"));		//�������
					result.addString("GREEN_NM",rs.getString("GREEN_NM"));	//�������
					result.addString("TEOF_DATE",DateUtil.format(rs.getString("TEOF_DATE"),"yyyyMMdd","yyyy-MM-dd"));	//��ŷ����
					
					String teof_time = rs.getString("TEOF_TIME");
					teof_time = teof_time.substring(0,2) + "�ô�";
					result.addString("TEOF_TIME",teof_time);	
										
					result.addString("CO_NM",rs.getString("CO_NM"));		//��û���̸�
					result.addString("CDHD_ID",rs.getString("CDHD_ID"));	//��û���̵�
					result.addString("HP_DDD_NO",rs.getString("HP_DDD_NO"));
					result.addString("HP_TEL_HNO",rs.getString("HP_TEL_HNO"));
					result.addString("BKG_PE_NM",rs.getString("BKG_PE_NM"));
					result.addString("HP_TEL_SNO",rs.getString("HP_TEL_SNO"));
					result.addString("MEMO_EXPL",rs.getString("MEMO_EXPL"));
					result.addString("REG_ATON",DateUtil.format(rs.getString("REG_ATON"),"yyyyMMdd","yyyy-MM-dd"));	//��û��
					result.addString("RESULT", "00"); //������*/
				}
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}
			 
		} catch (Throwable t) {
			throw new BaseException(t); 
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult getDetail(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);

			// ȸ���������̺� ���� �������� ����
			//��ȸ ----------------------------------------------------------
			

			String sql = this.getDetailSQL();   
			  
			String aplc_seq_no	= data.getString("aplc_seq_no");      //�����ȣ
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());

			pstmt.setString(++idx, aplc_seq_no);		
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("PGRS_YN",rs.getString("PGRS_YN"));		//�������
					result.addString("REG_ATON",DateUtil.format(rs.getString("REG_ATON"),"yyyyMMdd","yyyy/MM/dd"));
					result.addString("CO_NM",rs.getString("CO_NM"));
					result.addString("CDHD_ID",rs.getString("CDHD_ID"));
					result.addString("HP_DDD_NO",rs.getString("HP_DDD_NO"));
					result.addString("HP_TEL_HNO",rs.getString("HP_TEL_HNO"));
					result.addString("HP_TEL_SNO",rs.getString("HP_TEL_SNO"));
					result.addString("MEMO_EXPL",rs.getString("MEMO_EXPL"));
					result.addString("EMAIL",rs.getString("EMAIL"));
					result.addString("GREEN_NM",rs.getString("GREEN_NM"));
					result.addString("TEOF_DATE",DateUtil.format(rs.getString("TEOF_DATE"),"yyyyMMdd","yyyy-MM-dd"));
					result.addString("TEOF_TIME",rs.getString("TEOF_TIME"));
					result.addString("CHNG_ATON",rs.getString("CHNG_ATON"));
					
				}
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}
	
	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public int execute_update(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");		
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {
			conn = context.getDbConnection("default", null);
			
			//��ȸ ----------------------------------------------------------
			
			String green_nm		= data.getString("green_nm");       //��û�������
			String pgrs_yn		= data.getString("pgrs_yn");        //���� 
			String teof_date		= data.getString("teof_date").replaceAll("-", "");        //��ŷ����
			String teof_time		= data.getString("teof_time");        //��ŷ�ð�
			String aplc_seq_no	= data.getString("aplc_seq_no");    //�����ȣ
		
			String sql = this.getUpdateUsrSQL(pgrs_yn); 
			
			
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			
			pstmt.setString(++idx, green_nm);
			pstmt.setString(++idx, pgrs_yn);
			pstmt.setString(++idx, teof_date);			
			pstmt.setString(++idx, teof_time);
			if(pgrs_yn.equals("B")){
				pstmt.setString(++idx, data.getString("chng_aton"));
			}
			pstmt.setString(++idx, aplc_seq_no);
			
			result = pstmt.executeUpdate();
			if(pstmt != null) pstmt.close();
			conn.setAutoCommit(false);
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {			
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}
	
	/**
	 * Proc ����.	ttime �������� ������Ʈ
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public int execute_epsYn(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = 0;

		try {
			conn = context.getDbConnection("default", null);
			
			//��ȸ ----------------------------------------------------------
			
			String rsv_able_bokg_time_seq_no	= data.getString("golf_lesn_rsvt_no");    //�����ȣ
			
			String sql = this.getUpdateTtimeSQL(); 
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			
			pstmt.setString(++idx, rsv_able_bokg_time_seq_no);
			
			result = pstmt.executeUpdate();
			if(pstmt != null) pstmt.close();
			conn.setAutoCommit(false);
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {			
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}
	
	
	

	/** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
	
    private String getSelectQuery(TaoDataSet data) throws BaseException{
        StringBuffer sql = new StringBuffer();

        String green_nm			= data.getString("green_nm");               
		String pgrs_yn	= data.getString("pgrs_yn");
		
		String sch_date_gubun	= data.getString("sch_date_gubun");
		String sch_reg_aton_st	= data.getString("sch_reg_aton_st"); 
		String sch_reg_aton_ed	= data.getString("sch_reg_aton_ed");
		
		String sch_type			= data.getString("sch_type");
		String search_word		= data.getString("search_word");   
		 
		sql.append("\n     SELECT E.*                                                                                                 ");
		sql.append("\n       FROM (SELECT D.*,ROWNUM RNUM, CEIL(ROWNUM/?) AS PAGE,MAX(RN) OVER() TOT_CNT,                           ");
		sql.append("\n                    (MAX(RN) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) AS LIST_NO                              ");
		sql.append("\n               FROM (SELECT ROWNUM RN,APLC_SEQ_NO,           ");
		sql.append("\n                            (CASE WHEN PGRS_YN ='A' THEN '�������'  WHEN PGRS_YN = 'R' THEN '�����û' WHEN PGRS_YN = 'W' THEN '��ŷ���' WHEN PGRS_YN = 'B' THEN '��ŷȮ��' WHEN PGRS_YN = 'F' THEN '����'  WHEN PGRS_YN = 'C' THEN '��ŷ���' END) AS PGRS_YN,               ");
		sql.append("\n                            GREEN_NM,TEOF_DATE,TEOF_TIME,CO_NM,CDHD_ID,BKG_PE_NM,                ");
		sql.append("\n                            HP_DDD_NO,HP_TEL_HNO,HP_TEL_SNO, GOLF_LESN_RSVT_NO ,              ");
		sql.append("\n                            SUBSTR(MEMO_EXPL,1,20) MEMO_EXPL,SUBSTR(REG_ATON,1,8) REG_ATON               ");
		sql.append("\n                       FROM BCDBA.TBGAPLCMGMT								                                 ");
		sql.append("\n                      WHERE GOLF_SVC_APLC_CLSS = '1000'                                                     ");
		sql.append("\n                                                                                         ");
		sql.append("\n                                                                                         ");
		
			if(!green_nm.equals("")){
				sql.append("\n                        AND GREEN_NM = ?                                                                ");
			}
	
			if(!pgrs_yn.equals("")){
				sql.append("\n                        AND PGRS_YN = ?                                                          ");
			}
			
			if(!sch_reg_aton_st.equals("") && !sch_reg_aton_ed.equals("")){
				if(sch_date_gubun.equals("join_date")){
					sql.append("\n                        AND REG_ATON BETWEEN ? AND TO_CHAR(TO_DATE(?)+1,'YYYYMMDD')	");	// ��û���� 
				}else{
					sql.append("\n                        AND TEOF_DATE BETWEEN ? AND TO_CHAR(TO_DATE(?)+1,'YYYYMMDD')	");	// ��ŷ����
				}
			} 
			
			if(!search_word.equals("")){
				if(sch_type.equals("ID")){
					sql.append("\n                        AND CDHD_ID LIKE '%'||?||'%'                                                    ");
				}else if(sch_type.equals("NAME")){
					sql.append("\n                        AND CO_NM LIKE '%'||?||'%'                                                      ");
				}else{
					sql.append("\n                        AND (CDHD_ID LIKE '%'||?||'%' OR CO_NM LIKE '%'||?||'%')                      ");
				}
			}

		sql.append("\n                ORDER BY APLC_SEQ_NO DESC          ) D                                                     ");
		sql.append("\n                                                                                                            ");
		sql.append("\n             ) E                                                                                             ");
		sql.append("\n      WHERE PAGE = ?   ");
		return sql.toString();
    } 
    
    /** ***********************************************************************
     * Query�� �����Ͽ� �����Ѵ�.    
     ************************************************************************ */
 	
     private String getSelectXlsQuery(TaoDataSet data) throws BaseException{
         StringBuffer sql = new StringBuffer();

         String green_nm			= data.getString("green_nm");               
 		String pgrs_yn	= data.getString("pgrs_yn");
 		
 		String sch_date_gubun	= data.getString("sch_date_gubun");
 		String sch_reg_aton_st	= data.getString("sch_reg_aton_st"); 
 		String sch_reg_aton_ed	= data.getString("sch_reg_aton_ed");
 		
 		String sch_type			= data.getString("sch_type");
 		String search_word		= data.getString("search_word");   
 		 
 		sql.append("\n     SELECT E.*                                                                                                 ");
 		sql.append("\n       FROM (SELECT D.*                           ");
 		sql.append("\n               FROM (SELECT ROWNUM RN,APLC_SEQ_NO,           ");
		sql.append("\n                            (CASE WHEN PGRS_YN ='A' THEN '�������'  WHEN PGRS_YN = 'R' THEN '�����û' WHEN PGRS_YN = 'W' THEN '��ŷ���' WHEN PGRS_YN = 'B' THEN '��ŷȮ��' WHEN PGRS_YN = 'F' THEN '����'  WHEN PGRS_YN = 'C' THEN '��ŷ���' END) AS PGRS_YN,               ");
 		sql.append("\n                            GREEN_NM,TEOF_DATE,TEOF_TIME,CO_NM,CDHD_ID,BKG_PE_NM,                ");
 		sql.append("\n                            HP_DDD_NO,HP_TEL_HNO,HP_TEL_SNO,                ");
 		sql.append("\n                            SUBSTR(MEMO_EXPL,1,20) MEMO_EXPL,SUBSTR(REG_ATON,1,8) REG_ATON               ");
 		sql.append("\n                       FROM BCDBA.TBGAPLCMGMT 								                                 ");
 		sql.append("\n                      WHERE GOLF_SVC_APLC_CLSS = '1000'                                                     ");
 		sql.append("\n                                                                                         ");
 		sql.append("\n                                                                                         ");
 		
 			if(!green_nm.equals("")){
 				sql.append("\n                        AND GREEN_NM = ?                                                                ");
 			}
 	
 			if(!pgrs_yn.equals("")){
 				sql.append("\n                        AND PGRS_YN = ?                                                          ");
 			}
 			
 			if(!sch_reg_aton_st.equals("") && !sch_reg_aton_ed.equals("")){
 				if(sch_date_gubun.equals("join_date")){
 					sql.append("\n                        AND REG_ATON BETWEEN ? AND TO_CHAR(TO_DATE(?)+1,'YYYYMMDD')	");	// ��û���� 
 				}else{
 					sql.append("\n                        AND TEOF_DATE BETWEEN ? AND TO_CHAR(TO_DATE(?)+1,'YYYYMMDD')	");	// ��ŷ����
 				}
 			}
 			
 			if(!search_word.equals("")){
 				if(sch_type.equals("ID")){
 					sql.append("\n                        AND CDHD_ID LIKE '%'||?||'%'                                                    ");
 				}else if(sch_type.equals("NAME")){
 					sql.append("\n                        AND CO_NM LIKE '%'||?||'%'                                                      ");
 				}else{
 					sql.append("\n                        AND (CDHD_ID LIKE '%'||?||'%' OR CO_NM LIKE '%'||?||'%')                      ");
 				}
 			}

 		sql.append("\n                ORDER BY APLC_SEQ_NO DESC          ) D                                                     ");
 		sql.append("\n                                                                                                            ");
 		sql.append("\n             ) E                                                                                             ");
 		return sql.toString();
     } 
    
    /** ***********************************************************************
     * Query�� �����Ͽ� �����Ѵ�.    
     ************************************************************************ */
 	
     private String getDetailSQL() throws BaseException{
 		StringBuffer sql = new StringBuffer();

 		sql.append("\n     SELECT SUBSTR(A.REG_ATON,1,8) REG_ATON,A.CO_NM,A.CDHD_ID,A.PU_TIME,A.PU_DATE,	 ");
 		sql.append("\n            A.HP_DDD_NO,A.HP_TEL_HNO,A.HP_TEL_SNO,A.EMAIL,A.CHNG_ATON,			     			");
 		sql.append("\n            A.MEMO_EXPL,A.GREEN_NM,A.TEOF_DATE,A.TEOF_TIME,A.PGRS_YN	            ");
 		sql.append("\n       FROM BCDBA.TBGAPLCMGMT A,                                          						  	 ");
 		sql.append("\n             BCDBA.TBGGOLFCDHD D                                                 ");
 		sql.append("\n      WHERE A.GOLF_SVC_APLC_CLSS = '1000'                                                              ");
 		sql.append("\n        AND A.APLC_SEQ_NO = ?                                                                          ");
 		sql.append("\n        AND A.CDHD_ID = D.CDHD_ID          ");
 		return sql.toString();
 	}
     
     /** ***********************************************************************
      * Query�� �����Ͽ� �����Ѵ�.    
      ************************************************************************ */
  	
      private String getUpdateUsrSQL(String status) throws BaseException{
  		StringBuffer sql = new StringBuffer();
  		sql.append("\n     UPDATE BCDBA.TBGAPLCMGMT            ");
  		sql.append("\n        SET GREEN_NM = ?,           ");
  		sql.append("\n            PGRS_YN = ?,               ");
  		sql.append("\n            TEOF_DATE = ?,               ");
  		sql.append("\n            TEOF_TIME = ?               ");
  		if(status.equals("B")){
  			sql.append("\n           , CHNG_ATON = ?            ");
  		}
  		sql.append("\n      WHERE APLC_SEQ_NO = ?                  ");
  		return sql.toString();
  	}
      
      /** ***********************************************************************
       * Query�� �����Ͽ� �����Ѵ�.    
       ************************************************************************ */
   	
       private String getUpdateTtimeSQL() throws BaseException{
   		StringBuffer sql = new StringBuffer();
   		sql.append("\n     UPDATE BCDBA.TBGRSVTABLEBOKGTIMEMGMT            ");
   		sql.append("\n        SET EPS_YN = 'N'           ");
   		sql.append("\n      WHERE RSVT_ABLE_BOKG_TIME_SEQ_NO = ?                  ");
   		return sql.toString();
   	}
       
      
       
       /** ***********************************************************************
        * �̺�Ʈ ttime seq ��������
        ************************************************************************ 
        private String getBootkingRsvtSeqQuery(){
            StringBuffer sql = new StringBuffer();
            
    		sql.append("\n SELECT RSVT_ABLE_BOKG_TIME_SEQ_NO from BCDBA.TBGRSVTABLESCDMGMT A 	\n");
    		sql.append("\n JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT B ON A.RSVT_ABLE_SCD_SEQ_NO=B.RSVT_ABLE_SCD_SEQ_NO 	\n");
    		sql.append("\n WHERE A.AFFI_GREEN_SEQ_NO = ? 	\n");
    		sql.append("\n AND B.BOKG_ABLE_TIME = ? 	\n");
    		sql.append("\n AND A.BOKG_ABLE_DATE = ? 	\n");
    		sql.append("\n AND B.BOKG_RSVT_STAT_CLSS='1000' 	\n");
    		
    		return sql.toString();
        }*/
       
       
}

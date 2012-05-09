/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmGrUpdFormDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ ��ŷ ������ ���� �� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
* Ŀ��屸����	������		������	���泻��
* GOLFLOUNG		20100513	������	���ᰡ���� ���� �߰�
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.golf.common.AppConfig;

/******************************************************************************
 * Golf
 * @author	�̵������
 * @version	1.0 
 * golfloung  20100302 ������ ������ ȸ����� ���泻�� ����
 ******************************************************************************/
public class GolfAdmMemUpdFormDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfadmGrUpdFormDaoProc ���μ��� ������ 
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmMemUpdFormDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
						 
			//��ȸ ----------------------------------------------------------
			String sql = "";
			String memClss = (String)data.getString("memClss");
			
//			if("5".equals(memClss)) {
//				sql = this.getSelectQuery5();
//			} else {
//				sql = this.getSelectQuery();
//			}
			// ȸ���������� �Ѱ����� ���
			sql = this.getSelectQuery();
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("CDHD_ID"));
			rs = pstmt.executeQuery();
			
			if(rs != null) {
				while(rs.next())  {

					result.addString("NAME" 				,rs.getString("NAME") );
					result.addString("SOCID_NO" 			,rs.getString("SOCID_NO") );
					result.addString("CDHD_ID" 				,rs.getString("CDHD_ID") );
					result.addString("BIRTH" 				,rs.getString("BIRTH") );
					result.addString("SEX" 					,rs.getString("SEX") );
					result.addString("PHONE" 				,rs.getString("PHONE") );
					result.addString("MOBILE" 				,rs.getString("MOBILE") );
					result.addString("ZIPCODE" 				,rs.getString("ZIPCODE") );
					result.addString("ADDR" 				,rs.getString("ADDR") );
					result.addString("EMAIL1" 				,rs.getString("EMAIL1") );
					result.addString("GOLF_CMMN_CODE_NM" 	,rs.getString("GOLF_CMMN_CODE_NM") );
					//result.addString("CDHD_SQ1_CTGO"		,rs.getString("CDHD_SQ1_CTGO").trim());
					result.addString("TOT_AMT" 				,GolfUtil.comma(rs.getString("TOT_AMT")) );
					result.addString("JONN_DATE" 			,rs.getString("JONN_DATE") );
					result.addString("PAY_DATE" 			,rs.getString("PAY_DATE") );
					result.addString("BOKG_LIMT_YN" 		,rs.getString("BOKG_LIMT_YN") );
					result.addString("BOKG_LIMT_FIXN_STRT_DATE" ,rs.getString("BOKG_LIMT_FIXN_STRT_DATE") );
					result.addString("BOKG_LIMT_FIXN_END_DATE" 	,rs.getString("BOKG_LIMT_FIXN_END_DATE") );
					result.addString("CDHD_CTGO_SEQ_NO" 		,rs.getString("CDHD_CTGO_SEQ_NO") );
					result.addString("REG_ATON" 			,rs.getString("REG_ATON") );
					result.addString("SECE_YN" 				,rs.getString("SECE_YN") );
					
					//2009.10.01 �߰�	
					result.addString("AFFI_FIRM_NM" 		,rs.getString("AFFI_FIRM_NM") );
					result.addString("ESTM_ITM_CLSS" 		,rs.getString("ESTM_ITM_CLSS") );
					result.addString("APPR_OPION" 			,rs.getString("APPR_OPION") );
					result.addString("CUPN_DTL_CTNT" 		,rs.getString("CUPN_DTL_CTNT") );
					
					result.addString("ACRG_CDHD_JONN_DATE" 	,rs.getString("ACRG_CDHD_JONN_DATE") );
					result.addString("ACRG_CDHD_END_DATE" 	,rs.getString("ACRG_CDHD_END_DATE") );
					result.addString("JUMIN_NO" 			,rs.getString("JUMIN_NO") );
					result.addString("MONTH_MEM_IDX" 		,rs.getString("MONTH_MEM_IDX") );
					result.addString("MEMBER_CLSS_HAN" 		,rs.getString("MEMBER_CLSS_HAN") );
					
					
					result.addString("RESULT", "00"); //������
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
	public Hashtable getSocid(WaContext context, TaoDataSet data) throws Exception
	{
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);
		Hashtable hash =	null;
		String socId = "";
		try {
			conn = context.getDbConnection("default", null);
        
			//��ȸ ----------------------------------------------------------			
			String sql = this.getSelectSocidQuery();  
			
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("CDHD_ID"));
			rs = pstmt.executeQuery();
			
			hash = new Hashtable();
			if(rs != null) {
				while(rs.next())  {
					hash.put("MEMBER_CLSS",	rs.getString("MEMBER_CLSS"));
					hash.put("SOCID",		rs.getString("SOCID"));
				}
			}

			
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}	
		
		return hash;
		
	}
	
	

	/**
	 * Proc ���� = ��ް�������
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute_grade(WaContext context,  HttpServletRequest request) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult("TITLE");

		try {
			conn = context.getDbConnection("default", null);
			
			String sql = this.getSelectGradeQuery(); 
			pstmt = conn.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();

			if(rs != null) {			 

				while(rs.next())  {	

					result.addString("GRD_NO"	,rs.getString("GRD_NO") );
					result.addString("GRD_NM" 	,rs.getString("GRD_NM") );
					result.addString("RESULT"	, "00"); //������ 
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
	 * Proc ���� = ����Ʈ�ڽ� ��޸���Ʈ ��������.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute_history(WaContext context,  HttpServletRequest request, TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult("TITLE");

		try {
			conn = context.getDbConnection("default", null);
			
			String sql = this.getGradeListQuery(); 
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, data.getString("CDHD_ID"));
			rs = pstmt.executeQuery();

			if(rs != null) {			 

				while(rs.next())  {	

					result.addString("REGDATE"	,rs.getString("REGDATE") );
					result.addString("GRD_NM" 	,rs.getString("GRD_NM") );
					result.addString("RESULT"	, "00"); //������ 
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
	 * Proc ���� = ����Ʈ�ڽ� ��޸���Ʈ ��������.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute_pay(WaContext context,  HttpServletRequest request, TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult("TITLE");

		try {
			
			conn = context.getDbConnection("default", null);
			
			String sql = this.getPayQuery(); 
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, data.getString("CDHD_ID"));
			pstmt.setString(2, data.getString("CDHD_ID"));
			pstmt.setString(3, data.getString("CDHD_ID"));
			rs = pstmt.executeQuery();
			
			String paySeq = "N";
			String payWay = "yr";
			String payBack = "N";

			if(rs != null) {			 

				while(rs.next())  {	
					
					if(!GolfUtil.empty(rs.getString("ODR_NO"))){
						paySeq = "Y";
					}
					
					// ��ȸ��� ȯ���� ���� �ʴ´�.
					if(!GolfUtil.empty(rs.getString("APLC_SEQ_NO"))){
						
						if (rs.getString("GOLF_SVC_APLC_CLSS").equals("1005")){
							payWay = "yr";
						}else {
							payWay = "mn";
						}
						
					}else {
						payWay = "yr";
					}
					
					// ����� ���� ������ ������, ��ȸ�� �� ��쿡�� ȯ�����ش�.
					if("Y".equals(paySeq) && "yr".equals(payWay)){
						payBack = "Y";
					}

				}
				
				debug (" ##------ payBack : " + payBack + ", payWay : " + payWay + ", paySeq : " + paySeq);
				
				result.addString("pay_seq"	, paySeq); 
				result.addString("pay_way"	, payWay); 
				result.addString("payBack"	, payBack); 
				result.addString("RESULT"	, "00"); //������ 
			}
			
			debug("paySeq : " + paySeq + " / payWay : " + payWay + " / payBack : " + payBack);

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


	/** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getSelectQuery(){
    	StringBuffer sql = new StringBuffer();
		sql.append("\n  SELECT T1.HG_NM NAME, SUBSTR(T1.JUMIN_NO,1,6) AS SOCID_NO, T1.CDHD_ID, T1.SECE_YN	\n");
		sql.append("\t  , TO_CHAR(TO_DATE((CASE SUBSTR(T1.JUMIN_NO,7,1)  WHEN '1' THEN '19' WHEN '2' THEN '19' ELSE '20' END)	\n");
		sql.append("\t  	||SUBSTR(T1.JUMIN_NO,1,6)), 'YYYY\"��\" MM\"��\" DD\"��\"') AS BIRTH	\n");
		sql.append("\t  , (CASE SUBSTR(T1.JUMIN_NO,7,1) WHEN '1' THEN '��' WHEN '3' THEN '��' ELSE '��' END)||'��' AS SEX	\n");
		sql.append("\t  , T1.PHONE, T1.MOBILE, T1.ZIP_CODE ZIPCODE, (T1.ZIPADDR||' '||T1.DETAILADDR) AS ADDR	\n");
		sql.append("\t  , T1.EMAIL EMAIL1, T3.GOLF_CMMN_CODE_NM	\n");
		sql.append("\t  , (T1.CBMO_ACM_TOT_AMT-T1.CBMO_DDUC_TOT_AMT) AS TOT_AMT	\n");
		sql.append("\t  , TO_CHAR(TO_DATE(SUBSTR(T1.JONN_ATON,1,8)),'YYYY\"��\" MM\"��\" DD\"��\"') AS JONN_DATE	\n");
		sql.append("\t  , CASE WHEN ACRG_CDHD_JONN_DATE IS NULL THEN ''	\n");
		sql.append("\t      ELSE TO_CHAR(TO_DATE(SUBSTR(T1.ACRG_CDHD_JONN_DATE,1,8)),'YYYY\"��\" MM\"��\" DD\"��\"')	\n");
		sql.append("\t      ||' ~ '||TO_CHAR(TO_DATE(SUBSTR(T1.ACRG_CDHD_END_DATE,1,8)),'YYYY\"��\" MM\"��\" DD\"��\"')	\n");
		sql.append("\t      END AS PAY_DATE	\n");
		sql.append("\t  , TO_CHAR(TO_DATE(ACRG_CDHD_JONN_DATE),'YYYY-MM-DD') ACRG_CDHD_JONN_DATE, TO_CHAR(TO_DATE(ACRG_CDHD_END_DATE),'YYYY-MM-DD') ACRG_CDHD_END_DATE	\n");
		sql.append("\t  , (CASE BOKG_LIMT_YN WHEN 'Y' THEN BOKG_LIMT_YN ELSE 'N' END) AS BOKG_LIMT_YN	\n");
		sql.append("\t  , (CASE WHEN BOKG_LIMT_FIXN_STRT_DATE IS NULL THEN ''	\n");
		sql.append("\t      ELSE SUBSTR(BOKG_LIMT_FIXN_STRT_DATE,1,4)||'-'||SUBSTR(BOKG_LIMT_FIXN_STRT_DATE,5,2)	\n");
		sql.append("\t      ||'-'||SUBSTR(BOKG_LIMT_FIXN_STRT_DATE,7,2) END) BOKG_LIMT_FIXN_STRT_DATE	\n");
		sql.append("\t  , (CASE WHEN BOKG_LIMT_FIXN_END_DATE IS NULL THEN ''	\n");
		sql.append("\t      ELSE SUBSTR(BOKG_LIMT_FIXN_END_DATE,1,4)||'-'||SUBSTR(BOKG_LIMT_FIXN_END_DATE,5,2)	\n");
		sql.append("\t      ||'-'||SUBSTR(BOKG_LIMT_FIXN_END_DATE,7,2) END) BOKG_LIMT_FIXN_END_DATE	\n");
		sql.append("\t  , T1.CDHD_CTGO_SEQ_NO , to_char(TO_DATE(T1.JONN_ATON,'YYYYMMDDHH24MISS'),'yyyy.mm.dd hh:mi:ss') as REG_ATON	\n");
		sql.append("\t  , T1.AFFI_FIRM_NM , T1.ESTM_ITM_CLSS , T1.APPR_OPION, T1.JUMIN_NO	\n");
		sql.append("\t  , (SELECT CUPN_CTNT || '(' || CUPN_DTL_CTNT || ')' FROM bcdba.TBEVNTUNIFCUPNINFO WHERE site_clss='10' and evnt_no in ('112','111') and T1.AFFI_FIRM_NM = CUPN_NO )  CUPN_DTL_CTNT	\n");
 		sql.append("\t	, (SELECT APLC_SEQ_NO FROM BCDBA.TBGAPLCMGMT WHERE GOLF_SVC_APLC_CLSS='1001' AND PGRS_YN='Y' AND CDHD_ID=T1.CDHD_ID) MONTH_MEM_IDX	\n");
 		sql.append("\t	, (CASE  \n");
 		sql.append("\t	        WHEN T1.MEMBER_CLSS in ('1','4') THEN '����' \n");
 		sql.append("\t	        WHEN T1.MEMBER_CLSS = '5' THEN '����' END ) MEMBER_CLSS_HAN 	\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD T1	\n");
		sql.append("\t  LEFT JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.CDHD_CTGO_SEQ_NO=T2.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t  LEFT JOIN BCDBA.TBGCMMNCODE T3 ON T2.CDHD_SQ2_CTGO=T3.GOLF_CMMN_CODE AND T3.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t  WHERE T1.CDHD_ID=?	\n");
       
		return sql.toString();
    }
    
	/** ***********************************************************************
     * Query�� �����Ͽ� �����Ѵ�.    
     ************************************************************************ */
     private String getSelectQuery5(){
    	 StringBuffer sql = new StringBuffer();

 		sql.append("\n SELECT T1.HG_NM as NAME, SUBSTR(T1.JUMIN_NO,1,6) AS SOCID_NO, T1.CDHD_ID, T1.SECE_YN	\n");
// 		sql.append("\t  , TO_CHAR(TO_DATE((CASE SUBSTR(T1.JUMIN_NO,7,1)  WHEN '1' THEN '19' WHEN '2' THEN '19' ELSE '20' END) 		\n");
// 		sql.append("\t  ||SUBSTR(T1.JUMIN_NO,1,6)), 'YYYY\"��\" MM\"��\" DD\"��\"') AS BIRTH 							\n");
 		sql.append("\t  , (CASE SUBSTR(T1.JUMIN_NO,7,1)  WHEN '1' THEN '19' WHEN '2' THEN '19' ELSE '20' END)||SUBSTR(T1.JUMIN_NO,1,2)||'�� '||SUBSTR(T1.JUMIN_NO,3,2)||'�� '||SUBSTR(T1.JUMIN_NO,5,2)||'�� ' AS BIRTH	 	\n");
 		sql.append("\t  , (CASE SUBSTR(T1.JUMIN_NO,7,1) WHEN '1' THEN '��' WHEN '3' THEN '��' ELSE '��' END)||'��' AS SEX 			\n");
 		sql.append("\t  , T1.PHONE as PHONE, T1.MOBILE as MOBILE, T1.ZIPCODE, (T1.ZIPADDR||' '||T1.DETAILADDR) AS ADDR 				\n");
 		sql.append("\t  , T1.EMAIL as EMAIL1, T5.GOLF_CMMN_CODE_NM 															\n");
 		sql.append("\t  , (T1.CBMO_ACM_TOT_AMT-T1.CBMO_DDUC_TOT_AMT) AS TOT_AMT 									\n");
 		sql.append("\t  , TO_CHAR(TO_DATE(SUBSTR(T1.JONN_ATON,1,8)),'YYYY\"��\" MM\"��\" DD\"��\"') AS JONN_DATE 	\n");
 		sql.append("\t  , CASE WHEN ACRG_CDHD_JONN_DATE IS NULL THEN '' 											\n");
 		sql.append("\t  ELSE TO_CHAR(TO_DATE(SUBSTR(T1.ACRG_CDHD_JONN_DATE,1,8)),'YYYY\"��\" MM\"��\" DD\"��\"') 	\n");
 		sql.append("\t  ||' ~ '||TO_CHAR(TO_DATE(SUBSTR(T1.ACRG_CDHD_END_DATE,1,8)),'YYYY\"��\" MM\"��\" DD\"��\"') 	\n");
 		sql.append("\t  END AS PAY_DATE, (CASE BOKG_LIMT_YN WHEN 'Y' THEN BOKG_LIMT_YN ELSE 'N' END) AS BOKG_LIMT_YN	\n");
		sql.append("\t  , TO_CHAR(TO_DATE(ACRG_CDHD_JONN_DATE),'YYYY-MM-DD') ACRG_CDHD_JONN_DATE, TO_CHAR(TO_DATE(ACRG_CDHD_END_DATE),'YYYY-MM-DD') ACRG_CDHD_END_DATE	\n");
 		
 		sql.append("\t   , (CASE WHEN BOKG_LIMT_FIXN_STRT_DATE IS NULL THEN ''	\n");
 		sql.append("\t   ELSE SUBSTR(BOKG_LIMT_FIXN_STRT_DATE,1,4)||'-'||SUBSTR(BOKG_LIMT_FIXN_STRT_DATE,5,2)	\n");
 		sql.append("\t   ||'-'||SUBSTR(BOKG_LIMT_FIXN_STRT_DATE,7,2) END) BOKG_LIMT_FIXN_STRT_DATE	\n");
 		sql.append("\t   , (CASE WHEN BOKG_LIMT_FIXN_END_DATE IS NULL THEN ''	\n");
 		sql.append("\t   ELSE SUBSTR(BOKG_LIMT_FIXN_END_DATE,1,4)||'-'||SUBSTR(BOKG_LIMT_FIXN_END_DATE,5,2)	\n");
 		sql.append("\t   ||'-'||SUBSTR(BOKG_LIMT_FIXN_END_DATE,7,2) END) BOKG_LIMT_FIXN_END_DATE	\n");
 		sql.append("\t   , T4.CDHD_CTGO_SEQ_NO , to_char(TO_DATE(T3.REG_ATON,'YYYYMMDDHH24MISS'),'yyyy.mm.dd hh:mi:ss') as REG_ATON	\n");
 		sql.append("\t   , T1.AFFI_FIRM_NM , T1.ESTM_ITM_CLSS , T1.APPR_OPION, T1.JUMIN_NO 	\n");
 		sql.append("\t   , (SELECT CUPN_CTNT || '(' || CUPN_DTL_CTNT || ')' FROM bcdba.TBEVNTUNIFCUPNINFO WHERE site_clss='10' and evnt_no in ('112','111') and T1.AFFI_FIRM_NM = CUPN_NO )  CUPN_DTL_CTNT	\n");
 		sql.append("\t	 , (SELECT APLC_SEQ_NO FROM BCDBA.TBGAPLCMGMT WHERE GOLF_SVC_APLC_CLSS='1001' AND PGRS_YN='Y' AND CDHD_ID=T1.CDHD_ID) MONTH_MEM_IDX	\n"); 
 		
 		sql.append("\t  FROM BCDBA.TBGGOLFCDHD T1 																	\n"); 		
 		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDGRDMGMT T3 ON T3.CDHD_ID=T1.CDHD_ID 									\n");
 		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T4 ON T4.CDHD_CTGO_SEQ_NO=T3.CDHD_CTGO_SEQ_NO 				\n");
 		sql.append("\t  JOIN BCDBA.TBGCMMNCODE T5 ON T5.GOLF_CMMN_CODE=T4.CDHD_SQ2_CTGO AND T5.GOLF_CMMN_CLSS='0005' \n");
 		sql.append("\t  WHERE T1.CDHD_ID=? 		\n");
 		
 		return sql.toString();
     }
    /** ***********************************************************************
     * Query�� �����Ͽ� �����Ѵ�.    
     ************************************************************************ */
     private String getSelectSocidQuery(){
         StringBuffer sql = new StringBuffer();
         
 		//sql.append("\n SELECT SOCID, MEMBER_CLSS						\n"); 		
 		//sql.append("\t  FROM BCDBA.UCUSRINFO 							\n");
 		//sql.append("\t  WHERE ACCOUNT = ? 							\n");
         
         sql.append("\n  SELECT NVL(JUMIN_NO, '') as SOCID, MEMBER_CLSS		\n");
         sql.append("\t  FROM BCDBA.TBGGOLFCDHD									\n");
         sql.append("\t  WHERE CDHD_ID = ? 									\n");

 		return sql.toString();
     }

	/** ***********************************************************************
	* ����� ��� ����Ʈ�� �����´�.  - �����丮 �߰���
	************************************************************************ */
	private String getSelectGradeQuery(){
		StringBuffer sql = new StringBuffer();
 		sql.append("\n	SELECT T1.CDHD_CTGO_SEQ_NO GRD_NO, T2.GOLF_CMMN_CODE_NM AS GRD_NM	\n");
 		sql.append("\t	FROM BCDBA.TBGGOLFCDHDCTGOMGMT T1	\n");
 		sql.append("\t	JOIN BCDBA.TBGCMMNCODE T2 ON T2.GOLF_CMMN_CODE=T1.CDHD_SQ2_CTGO AND GOLF_CMMN_CLSS='0005'	\n");
 		sql.append("\t	WHERE T1.CDHD_SQ1_CTGO = '0002' AND T2.USE_YN='Y'	\n");
 		sql.append("\t	ORDER BY T2.GOLF_CMMN_CODE_NM	\n");

 		return sql.toString();
     }


	/** ***********************************************************************
	* �����丮 ����Ʈ�� �����´�.     - �����丮 �߰���
	************************************************************************ */
	private String getGradeListQuery(){
		StringBuffer sql = new StringBuffer();        
 		sql.append("\n	SELECT ROWNUM, REGDATE, GRD_NM	\n");
 		sql.append("\t	FROM (	\n");
 		sql.append("\t	    SELECT TO_CHAR(TO_DATE(SUBSTR(T1.REG_ATON,1,8)),'YY.MM.DD') REGDATE, T3.GOLF_CMMN_CODE_NM GRD_NM	\n");
 		sql.append("\t	    FROM BCDBA.TBGCDHDGRDCHNGHST T1	\n");
 		sql.append("\t	    JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.CDHD_CTGO_SEQ_NO=T2.CDHD_CTGO_SEQ_NO	\n");
 		sql.append("\t	    JOIN BCDBA.TBGCMMNCODE T3 ON T2.CDHD_SQ2_CTGO=T3.GOLF_CMMN_CODE AND T3.GOLF_CMMN_CLSS='0005'	\n");
 		sql.append("\t	    WHERE T1.CDHD_ID=? AND T1.CDHD_CTGO_SEQ_NO<>'0'	\n");
 		sql.append("\t	    ORDER BY T1.REG_ATON DESC)	\n");
 		sql.append("\t	WHERE ROWNUM<=5	\n");
  		return sql.toString();
      }


	/** ***********************************************************************
	* ��ȸ�� ���� ������ �����´�. 
	************************************************************************ */
	private String getPayQuery(){
		
		StringBuffer sql = new StringBuffer();     
 		
		sql.append("\n	SELECT LST.APLC_SEQ_NO, PAY.ODR_NO, NVL(LST.GOLF_SVC_APLC_CLSS, 0) GOLF_SVC_APLC_CLSS	\n");
 		sql.append("\t	FROM BCDBA.TBGGOLFCDHD CDHD	\n");
 		sql.append("\t	LEFT JOIN BCDBA.TBGAPLCMGMT LST ON LST.CDHD_ID=CDHD.CDHD_ID AND LST.GOLF_SVC_APLC_CLSS = '1001' AND LST.PGRS_YN='Y'	\n");
 		sql.append("\t	LEFT JOIN BCDBA.TBGSTTLMGMT PAY ON PAY.CDHD_ID=CDHD.CDHD_ID AND PAY.STTL_ATON BETWEEN CDHD.ACRG_CDHD_JONN_DATE AND CDHD.ACRG_CDHD_END_DATE	\n");
 		sql.append("\t	LEFT JOIN BCDBA.TBGCMMNCODE CODE ON CODE.GOLF_CMMN_CODE=PAY.STTL_GDS_CLSS	\n");
 		sql.append("\t	WHERE CDHD.CDHD_ID=? AND (SECE_YN IS NULL OR SECE_YN='N')	\n");
 		sql.append("\t	AND CODE.GOLF_CMMN_CLSS='0016' AND (CODE.GOLF_CMMN_CODE_NM LIKE '�����%' OR CODE.GOLF_CMMN_CODE_NM='ȸ����� ���׷��̵�')		\n");
 		sql.append("\t	AND PAY.STTL_STAT_CLSS='N'	\n");
 		sql.append("\t	UNION	\n");
		sql.append("\n	SELECT LST.APLC_SEQ_NO, PAY.ODR_NO, LST.GOLF_SVC_APLC_CLSS	\n");
 		sql.append("\t	FROM BCDBA.TBGGOLFCDHD CDHD	\n");
 		sql.append("\t	JOIN BCDBA.TBGAPLCMGMT LST ON LST.CDHD_ID=CDHD.CDHD_ID AND LST.GOLF_SVC_APLC_CLSS = '1005' AND LST.PGRS_YN='N'	\n");
 		sql.append("\t	JOIN BCDBA.TBGSTTLMGMT PAY ON PAY.CDHD_ID=CDHD.CDHD_ID AND PAY.STTL_ATON>=TO_CHAR(SYSDATE-365,'YYYYMMDD')	\n");
 		sql.append("\t	JOIN BCDBA.TBGCMMNCODE CODE ON CODE.GOLF_CMMN_CODE=PAY.STTL_GDS_CLSS	\n");
 		sql.append("\t	WHERE CDHD.CDHD_ID=? AND (SECE_YN IS NULL OR SECE_YN='N')	\n");
 		sql.append("\t	AND CODE.GOLF_CMMN_CLSS='0016' AND (CODE.GOLF_CMMN_CODE_NM LIKE '�����%' OR CODE.GOLF_CMMN_CODE_NM='ȸ����� ���׷��̵�')		\n");
 		sql.append("\t	AND PAY.STTL_STAT_CLSS='N'	\n");
 		sql.append("\t	UNION	\n");
 		sql.append("\t	SELECT LST.APLC_SEQ_NO, PAY.ODR_NO, LST.GOLF_SVC_APLC_CLSS	\n");
 		sql.append("\t	FROM BCDBA.TBGGOLFCDHD CDHD		\n");
 		sql.append("\t	JOIN BCDBA.TBGAPLCMGMT LST ON LST.CDHD_ID=CDHD.CDHD_ID AND LST.GOLF_SVC_APLC_CLSS = '1004' AND LST.PGRS_YN='Y' 	\n");
 		sql.append("\t	JOIN BCDBA.TBGSTTLMGMT PAY ON LST.CDHD_ID=PAY.CDHD_ID AND LST.APLC_SEQ_NO = PAY.STTL_GDS_SEQ_NO	\n");
 		sql.append("\t	WHERE CDHD.CDHD_ID=?  AND (SECE_YN IS NULL OR SECE_YN='N')	\n");
 		
  		return sql.toString();
  		
      }

}

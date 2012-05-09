/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmWorldPrimeDaoProc.java
*   �ۼ���    : ������
*   ����      : ������ > �̺�Ʈ > ���������� >��û���� 
*   �������  : golf
*   �ۼ�����  : 2010-08-26
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	������ ���弱
 * @version	1.0
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
* 
 ******************************************************************************/
public class GolfAdmWorldPrimeDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmSpecialBookingDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmWorldPrimeDaoProc() {}	

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
			
			
			
			String aplc_seq_no	= data.getString("aplc_seq_no");      //�Ϸù�ȣ
			
			int idx = 0;
			String sql = this.getDetailSQL();   
			pstmt = conn.prepareStatement(sql.toString());

			pstmt.setString(++idx, aplc_seq_no);		
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("APLC_SEQ_NO",rs.getString("APLC_SEQ_NO"));
					result.addString("CDHD_ID",rs.getString("CDHD_ID"));
					result.addString("BKG_PE_NM",rs.getString("BKG_PE_NM"));
					result.addString("JUMIN_NO",rs.getString("JUMIN_NO"));
					result.addString("HP_DDD_NO",rs.getString("HP_DDD_NO"));
					result.addString("HP_TEL_HNO",rs.getString("HP_TEL_HNO"));
					result.addString("HP_TEL_SNO",rs.getString("HP_TEL_SNO"));
					result.addString("DDD_NO",rs.getString("DDD_NO"));
					result.addString("TEL_HNO",rs.getString("TEL_HNO"));
					result.addString("TEL_SNO",rs.getString("TEL_SNO"));
					result.addString("DTL_ADDR",rs.getString("DTL_ADDR"));
					result.addString("LESN_SEQ_NO",rs.getString("LESN_SEQ_NO"));
					result.addString("STTL_AMT",rs.getString("STTL_AMT"));
					result.addString("MEMO_EXPL",rs.getString("MEMO_EXPL"));	
					result.addString("CO_NM",rs.getString("CO_NM"));
					result.addString("PGRS_YN",rs.getString("PGRS_YN"));
					result.addString("GOLF_SVC_APLC_CLSS",rs.getString("GOLF_SVC_APLC_CLSS"));
					
					result.addString("GOLF_CMMN_CODE_NM",rs.getString("GOLF_CMMN_CODE_NM"));
					result.addString("GOLF_CMMN_CODE_NM2",rs.getString("GOLF_CMMN_CODE_NM2"));
					result.addString("CSLT_YN",rs.getString("CSLT_YN"));
					
					result.addString("ST_CARD_NO",rs.getString("ST_CARD_NO"));
					result.addString("ST_INS_MCNT",rs.getString("ST_INS_MCNT"));
					result.addString("ST_STTL_AMT",rs.getString("ST_STTL_AMT"));
					result.addString("ST_STTL_ATON",rs.getString("ST_STTL_ATON"));
					result.addString("PU_DATE2",rs.getString("PU_DATE2"));
					result.addString("GREEN_NM",rs.getString("GREEN_NM"));
					
					String pu_date = rs.getString("PU_DATE");
					if(pu_date != null){
						pu_date = DateUtil.format(pu_date,"yyyyMMdd","yyyy-MM-dd");
					}
					result.addString("PU_DATE",pu_date);	
					
					//String memGrade = this.getMemGradeNm(context, data, rs.getString("CDHD_ID"));
					
					/*if(!"".equals(memGrade) && memGrade != null){
						result.addString("GRADE", memGrade);
					}else{
						result.addString("GRADE","");
					}*/
				
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
	/** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.     
    ************************************************************************ */
	
    private String getDetailSQL() throws BaseException{
		StringBuffer sql = new StringBuffer();
		sql.append("\n 	 ");
		sql.append("\n SELECT	 ");
		sql.append("\n	APLC_SEQ_NO, CDHD_ID, BKG_PE_NM, JUMIN_NO, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, DDD_NO, TEL_HNO, TEL_SNO	 ");
		sql.append("\n	, TO_CHAR(TO_DATE(PU_DATE)+365,'YYYY.MM.DD') PU_DATE2, GREEN_NM,   CSLT_YN	 ");				
		sql.append("\n	, DTL_ADDR, LESN_SEQ_NO, STTL_AMT, PU_DATE, MEMO_EXPL, CO_NM, PGRS_YN, REG_ATON, GOLF_SVC_APLC_CLSS			     ");
		
		sql.append("\n	,NVL(( SELECT GOLF_CMMN_CODE_NM FROM BCDBA.TBGGOLFCDHD  T6, BCDBA.TBGGOLFCDHDCTGOMGMT T7, BCDBA.TBGCMMNCODE T8 	 ");
		sql.append("\n	WHERE CDHD_ID=A.CDHD_ID AND T6.CDHD_CTGO_SEQ_NO=T7.CDHD_CTGO_SEQ_NO  	 ");
		sql.append("\n	AND T7.CDHD_SQ2_CTGO=T8.GOLF_CMMN_CODE AND T8.GOLF_CMMN_CLSS='0005' ),'��ȸ��') AS GOLF_CMMN_CODE_NM 	 ");
		
		sql.append("\n	,NVL(( SELECT GOLF_CMMN_CODE_NM FROM BCDBA.TBGGOLFCDHD  T6, BCDBA.TBGGOLFCDHDCTGOMGMT T7, BCDBA.TBGCMMNCODE T8 	 ");
		sql.append("\n	WHERE JUMIN_NO=A.CDHD_ID AND T6.CDHD_CTGO_SEQ_NO=T7.CDHD_CTGO_SEQ_NO 	 ");
		sql.append("\n	AND T7.CDHD_SQ2_CTGO=T8.GOLF_CMMN_CODE AND T8.GOLF_CMMN_CLSS='0005' ),'��ȸ��') AS GOLF_CMMN_CODE_NM2 	 ");
		
		sql.append("\n 	, (SELECT CARD_NO FROM BCDBA.TBGSTTLMGMT WHERE ODR_NO = A.CO_NM ) AS ST_CARD_NO ");
		sql.append("\n 	, (SELECT INS_MCNT FROM BCDBA.TBGSTTLMGMT WHERE ODR_NO = A.CO_NM ) AS ST_INS_MCNT ");
		sql.append("\n 	, (SELECT STTL_AMT FROM BCDBA.TBGSTTLMGMT WHERE ODR_NO = A.CO_NM ) AS ST_STTL_AMT ");
		sql.append("\n 	, (SELECT TO_CHAR(TO_DATE(STTL_ATON, 'YYYYMMDDHH24MISS'), 'YYYYMMDD') FROM BCDBA.TBGSTTLMGMT WHERE ODR_NO = A.CO_NM ) AS ST_STTL_ATON ");
		
		
		
		sql.append("\n FROM BCDBA.TBGAPLCMGMT A                                         						  	 ");
		sql.append("\n WHERE GOLF_SVC_APLC_CLSS = '1003'                                                              ");
		sql.append("\n	AND APLC_SEQ_NO = ?                                                                          ");
		sql.append("\n 	 ");
		 
		//sql.append("\n            NVL(DECODE(C.CDHD_CTGO_SEQ_NO,'8','ȭ��Ʈ','7','���','6','���','5','è�ǿ�'), C.CDHD_CTGO_SEQ_NO) GRADE,			 ");
		return sql.toString();
	}
    /**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult getPayDetail(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);						
			
			//String aplc_seq_no	= data.getString("aplc_seq_no");      //�Ϸù�ȣ
			String payOrderNo	= data.getString("payOrderNo");       //�����ֹ���ȣ
			
			int idx = 0;
			
			pstmt = conn.prepareStatement(getPayDetailSQL());

			pstmt.setString(++idx, payOrderNo);		
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("ODR_NO",rs.getString("ODR_NO"));
					result.addString("STTL_MTHD_CLSS",rs.getString("STTL_MTHD_CLSS"));
					result.addString("STTL_GDS_CLSS",rs.getString("STTL_GDS_CLSS"));
					result.addString("STTL_STAT_CLSS",rs.getString("STTL_STAT_CLSS"));
					result.addString("CARD_NO",rs.getString("CARD_NO"));
					result.addString("AUTH_NO",rs.getString("AUTH_NO"));
					result.addString("INS_MCNT",rs.getString("INS_MCNT"));
					result.addString("STTL_ATON",rs.getString("STTL_ATON"));
					result.addString("STTL_MINS_NM",rs.getString("STTL_MINS_NM"));
					result.addString("CNCL_ATON",rs.getString("CNCL_ATON"));
					result.addString("CSLT_YN",rs.getString("CSLT_YN"));
					result.addString("STTL_AMT",rs.getString("STTL_AMT"));
					result.addString("RESULT", "00");	
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
	/** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.     
    ************************************************************************ */
	
    private String getPayDetailSQL() throws BaseException{
		StringBuffer sql = new StringBuffer();
		sql.append("\n 	 ");
		sql.append("\n SELECT	 ");
		sql.append("\n	ODR_NO, STTL_MTHD_CLSS, STTL_GDS_CLSS, STTL_STAT_CLSS, CARD_NO, AUTH_NO, INS_MCNT , STTL_MINS_NM 	 ");
		sql.append("\n	, TO_CHAR(TO_DATE(A.STTL_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') AS STTL_ATON , TO_CHAR(TO_DATE(A.CNCL_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') AS CNCL_ATON , STTL_AMT   	 ");
		sql.append("\n	, (SELECT MAX(CSLT_YN) FROM BCDBA.TBGAPLCMGMT WHERE CO_NM = A.ODR_NO ) AS CSLT_YN  	 ");
		sql.append("\n FROM BCDBA.TBGSTTLMGMT A                         ");
		sql.append("\n WHERE ODR_NO = ?                                 ");
		
		sql.append("\n 	 ");
		
		return sql.toString();
	}
    /**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult getBookingDetail(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset_comp = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);						
			
			String userJuminNo	= data.getString("userJuminNo");      	//�ֹι�ȣ
			//String aplc_seq_no	= data.getString("aplc_seq_no");         //�����ֹ���ȣ
			
			int idx = 0;
			
			pstmt = conn.prepareStatement(getBookingDetailSQL());

			pstmt.setString(++idx, userJuminNo);		
			
			rs = pstmt.executeQuery();
			
			int comp_num = 0;
			int comp_name_num = 0;
			//String comp_name = "";
			String comp_yn = "N";
			String aplc_seq_no = "";
			String ddate = "";
			
			String billYYYYMM = "";
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("APLC_SEQ_NO",rs.getString("APLC_SEQ_NO"));
					result.addString("APLC_ATON",rs.getString("APLC_ATON"));
					result.addString("RSVT_DATE",rs.getString("RSVT_DATE"));
					result.addString("CNCL_ATON",rs.getString("CNCL_ATON"));
					result.addString("HADC_NUM",rs.getString("HADC_NUM")); 	//������														
					result.addString("EVNT_PGRS_CLSS",rs.getString("EVNT_PGRS_CLSS"));	// �̺�Ʈ ���౸�� �ڵ� R:��û, A:���, P:��������, B:Ȯ��, C:�������, E:�������
					//result.addString("RSVT_DATE2",rs.getString("RSVT_DATE2"));
					
					ddate = rs.getString("RSVT_DATE").replaceAll("-", "");
					try { 	
						//currDate = new java.text.SimpleDateFormat("yyyyMMdd").format(ddate);
						billYYYYMM = DateUtil.dateAdd('d',2,ddate,"yyyyMMdd");
					} catch(Throwable t) {}
					debug("## ddate : "+ddate+" | billYYYYMM : "+billYYYYMM);
					
					result.addString("RSVT_DATE2",billYYYYMM);
					
					Calendar cal = Calendar.getInstance(); 
					cal.add(Calendar.DATE,-31); 
					
					result.addString("MGR_MEMO",rs.getString("MGR_MEMO"));
					result.addString("NOTE",rs.getString("NOTE"));
					result.addString("COMP_NUM",rs.getString("COMP_NUM"));
					result.addString("CUS_RMRK",rs.getString("CUS_RMRK"));
					
					result.addString("HP_DDD_NO",rs.getString("HP_DDD_NO"));
					result.addString("HP_TEL_HNO",rs.getString("HP_TEL_HNO"));
					result.addString("HP_TEL_SNO",rs.getString("HP_TEL_SNO"));
					result.addString("EMAIL",rs.getString("EMAIL"));
					
					comp_num = rs.getInt("COMP_NUM");
					aplc_seq_no = rs.getString("APLC_SEQ_NO");
					
					if(comp_num>0){
						comp_yn = "Y";

						pstmt = conn.prepareStatement(this.getCompQuery());
						int aidx = 0;
						pstmt.setString(++aidx, aplc_seq_no);

						
						rset_comp = pstmt.executeQuery();

						while(rset_comp.next()){
							//comp_name = rset_comp.getString("BKG_PE_NM");
							comp_name_num++;
							//debug("comp_name : " + comp_name + " / comp_name_num : " + comp_name_num);
							result.addString("comp_bkg_pe_nm_"+comp_name_num,		rset_comp.getString("BKG_PE_NM"));
						}
						
						if(comp_name_num<3){
							for(int u=3; u>comp_name_num; u--){
								result.addString("comp_bkg_pe_nm_"+u,		"");
								//debug("u : " + u);
							}
						}
					}

					result.addString("comp_yn",			comp_yn);
					
					result.addString("RESULT", "00");	
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
	/** ***********************************************************************
	    * Query�� �����Ͽ� �����Ѵ�.     
	    ************************************************************************ */
		
	    private String getBookingDetailSQL() throws BaseException{
			StringBuffer sql = new StringBuffer();
			sql.append("\n 	 ");
			sql.append("\n SELECT	 ");
			sql.append("\n	APLC_SEQ_NO  	 ");
			sql.append("\n	, TO_CHAR(TO_DATE(A.APLC_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') AS APLC_ATON  	 ");
			sql.append("\n	, TO_CHAR(TO_DATE(A.RSVT_DATE, 'YYYYMMDD'), 'YYYY-MM-DD') AS RSVT_DATE  	 ");
			sql.append("\n	, TO_CHAR(TO_DATE(A.CNCL_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') AS CNCL_ATON  	 ");
			sql.append("\n	, HADC_NUM  	 ");	
			sql.append("\n	, EVNT_PGRS_CLSS  	 ");
			//sql.append("\n	, TO_CHAR(TO_DATE(A.RSVT_DATE+HADC_NUM, 'YYYYMMDD'), 'YYYY-MM-DD') AS RSVT_DATE2  	 ");
			sql.append("\n	, MGR_MEMO  	 ");
			sql.append("\n	, NOTE  	 ");
			sql.append("\n	, (SELECT COUNT(*) CNT FROM BCDBA.TBGGOLFEVNTAPLCPE WHERE GOLF_SVC_APLC_CLSS='1003' AND APLC_SEQ_NO=A.APLC_SEQ_NO AND APLC_PE_CLSS='2') COMP_NUM 	 ");
			sql.append("\n	, CUS_RMRK  	 ");
			sql.append("\n	, HP_DDD_NO , HP_TEL_HNO , HP_TEL_SNO , EMAIL  	 ");
			
			sql.append("\n FROM BCDBA.TBGGOLFEVNTAPLC A                         ");
			sql.append("\n WHERE GOLF_SVC_APLC_CLSS = '1003' AND JUMIN_NO = ?                                 ");
			sql.append("\n ORDER BY A.APLC_SEQ_NO DESC	 ");
			
			sql.append("\n 	 ");
			
			return sql.toString();
		}
	    /** ***********************************************************************
		* ����ȸ ��û�� ���̺�
		************************************************************************ */
		private String getCompQuery() throws Exception{
			StringBuffer sql = new StringBuffer();
			
			sql.append("	\n");
			sql.append("\t	SELECT SEQ_NO, BKG_PE_NM, APLC_PE_CLSS	\n");
			sql.append("\t	FROM BCDBA.TBGGOLFEVNTAPLCPE	\n");
			sql.append("\t	WHERE GOLF_SVC_APLC_CLSS='1003' AND APLC_PE_CLSS='2' AND APLC_SEQ_NO=?	\n");
			sql.append("\t	ORDER BY SEQ_NO	\n");
			
			return sql.toString();
		}
	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		//ResultSet rs2 = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			
			//��ȸ ----------------------------------------------------------
			

			   

			long page_no			= data.getLong("PAGE_NO");               //��������ȣ
			long record_size		= data.getLong("RECORD_SIZE");           //�������� ��µ� ����
			String actnKey          = data.getString("actnKey");				 //�׼�Ű
			
			String sch_reg_aton_st	= data.getString("sch_reg_aton_st");     //��ȸ ��û ������ 
			String sch_reg_aton_ed	= data.getString("sch_reg_aton_ed"); 	 //��ȸ ��û ������ 
			//String sch_date			= data.getString("sch_date");            //�̸�,ID��ȸ ����  
			//String sch_type			= data.getString("sch_type");            //�̸�,ID��ȸ ����     
			String search_word		= data.getString("search_word");         //��ȸ ��     
			String golf_cmmn_code	= data.getString("golf_cmmn_code");      //�����ڵ�
			String green_nm			= data.getString("green_nm");            //����������
			//String sch_sttl_stat_clss	= data.getString("sch_sttl_stat_clss");  
			String sch_rsvt_date	= data.getString("sch_rsvt_date");  
			//String sch_rsv_time		= data.getString("sch_rsv_time");  
			String sch_yn			= data.getString("sch_yn");
						
			//String cmmn_code_nm		= "";
			
			// �Է°� (INPUT)         
			int idx = 0;
			
			String sql = "";
			if(actnKey.equals("admWorldPrimeAppExeclList"))
			{
				sql = this.getSelectExcelQuery(data); 
			}
			else
			{
				sql = this.getSelectQuery(data); 
			}
			
			
			pstmt = conn.prepareStatement(sql.toString());
			if(actnKey.equals("admWorldPrimeAppList")){
			pstmt.setLong(++idx, record_size);
			pstmt.setLong(++idx, record_size);
			pstmt.setLong(++idx, page_no);
			pstmt.setLong(++idx, record_size);
			pstmt.setLong(++idx, page_no);
			}
			if("Y".equals(sch_yn))
			{
				if(!"".equals(sch_reg_aton_st) && !"".equals(sch_reg_aton_ed)){
					pstmt.setString(++idx,sch_reg_aton_st.replaceAll("-",""));
					pstmt.setString(++idx,sch_reg_aton_ed.replaceAll("-",""));
				}
				if(!"".equals(search_word))
				{
					pstmt.setString(++idx,search_word);
				}
				if(!"".equals(green_nm))
				{
					pstmt.setString(++idx,green_nm);
				}
				if(!"".equals(sch_rsvt_date))
				{
					pstmt.setString(++idx,sch_rsvt_date);
				}
				
				
				
				
				
				
				
				
				
				if(!"".equals(golf_cmmn_code))
				{
					pstmt.setString(++idx,golf_cmmn_code);
				}
				
				
			}
			
			
			if(actnKey.equals("admWorldPrimeAppList")){
				pstmt.setLong(++idx, page_no);
			}			
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					
					if("admWorldPrimeAppList".equals(actnKey)){
						result.addString("LIST_NO",rs.getString("LIST_NO"));
						result.addString("RN",rs.getString("RN"));	//no.
						result.addInt("PAGE",rs.getInt("PAGE"));	//�ֱ�������
						result.addInt("TOT_CNT",rs.getInt("TOT_CNT"));	//�� ��
					}
					
					result.addString("APLC_SEQ_NO",rs.getString("APLC_SEQ_NO"));
					
					//result.addString("GOLF_CMMN_CODE",rs.getString("GOLF_CMMN_CODE"));
					//result.addString("GOLF_CMMN_CODE_NM",rs.getString("GOLF_CMMN_CODE_NM"));
					
					/*cmmn_code_nm = rs.getString("GOLF_CMMN_CODE_NM");
//					debug(">>>>>>>>>>>>> cmmn_code_nm :" + cmmn_code_nm);
					if("�����û".equals(cmmn_code_nm) || "�����".equals(cmmn_code_nm) || "��ŷ���".equals(cmmn_code_nm)) {
						cmmn_code_nm = "<font color='#9ACD32'>" + cmmn_code_nm + "</font>";		// ���λ�
					} else if("������".equals(cmmn_code_nm) || "��ŷ���".equals(cmmn_code_nm)) {
						cmmn_code_nm = "<font color='red'>" + cmmn_code_nm + "</font>";		// ������
					} else if("��ŷȮ��".equals(cmmn_code_nm)) {
						cmmn_code_nm = "<font color='blue'>" + cmmn_code_nm + "</font>";		// �Ķ���
					}*/
//					debug(">>>>>>>>>>>>> cmmn_code_nm2 :" + cmmn_code_nm);
					result.addString("CDHD_ID",rs.getString("CDHD_ID"));	//ȸ�����̵�
					result.addString("JUMIN_NO",rs.getString("JUMIN_NO"));	//�ֹι�ȣ1
					result.addString("BKG_PE_NM",rs.getString("BKG_PE_NM"));	//�̸�
					result.addString("HP_DDD_NO",rs.getString("HP_DDD_NO"));	//�ڵ�����ȣ1
					result.addString("HP_TEL_HNO",rs.getString("HP_TEL_HNO"));	//�ڵ�����ȣ2
					result.addString("HP_TEL_SNO",rs.getString("HP_TEL_SNO"));	//�ڵ�����ȣ3
					result.addString("DDD_NO",rs.getString("DDD_NO"));	//��ȭ��ȣ1
					result.addString("TEL_HNO",rs.getString("TEL_HNO"));	//��ȭ��ȣ2
					result.addString("TEL_SNO",rs.getString("TEL_SNO"));	//��ȭ��ȣ3
					result.addString("DTL_ADDR",rs.getString("DTL_ADDR"));	//�ּ�
					result.addString("LESN_SEQ_NO",rs.getString("LESN_SEQ_NO"));	//ȸ�� ������
					result.addString("REG_ATON",rs.getString("REG_ATON"));					
					
					
					result.addString("PGRS_YN",rs.getString("PGRS_YN"));
					result.addString("STTL_AMT",rs.getString("STTL_AMT"));
					result.addString("CSLT_YN",rs.getString("CSLT_YN"));
					result.addString("STTL_ATON",rs.getString("STTL_ATON"));
					result.addString("EVNT_PGRS_CLSS",rs.getString("EVNT_PGRS_CLSS"));
														
					
					
					
					String pu_date = rs.getString("PU_DATE");
					if(pu_date != null){
						pu_date = DateUtil.format(pu_date,"yyyyMMdd","yyyy-MM-dd");
					}
					result.addString("PU_DATE",pu_date);	
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
	
	/** ***********************************************************************
	    * Query�� �����Ͽ� �����Ѵ�.    
	    ************************************************************************ */
		
	    private String getSelectExcelQuery(TaoDataSet data) throws BaseException{
	        StringBuffer sql = new StringBuffer();

	        
	        String sch_reg_aton_st	= data.getString("sch_reg_aton_st");     //��ȸ ��û ������ 
			String sch_reg_aton_ed	= data.getString("sch_reg_aton_ed"); 	 //��ȸ ��û ������ 
			String sch_date			= data.getString("sch_date");            //�̸�,ID��ȸ ����  
			String sch_type			= data.getString("sch_type");            //�̸�,ID��ȸ ����     
			String search_word		= data.getString("search_word");         //��ȸ ��     
			String golf_cmmn_code	= data.getString("golf_cmmn_code");      //�����ڵ�
			String green_nm			= data.getString("green_nm");            //����������
			String sch_sttl_stat_clss	= data.getString("sch_sttl_stat_clss");  
			String sch_rsvt_date	= data.getString("sch_rsvt_date");  
			//String sch_rsv_time		= data.getString("sch_rsv_time");  
			String sch_yn			= data.getString("sch_yn");  
			

			sql.append("\n    SELECT * FROM ( SELECT ROWNUM RN,APLC_SEQ_NO,												           ");
			sql.append("\n                            A.CDHD_ID,BKG_PE_NM,JUMIN_NO, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, DDD_NO, TEL_HNO, TEL_SNO,DTL_ADDR,PU_DATE,                ");
			sql.append("\n                            DECODE(LESN_SEQ_NO,'1','���� ������','2','���� ���� ������') LESN_SEQ_NO ");				
			sql.append("\n                            ,TO_CHAR(TO_DATE(A.REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') AS REG_ATON               ");
			sql.append("\n                            ,(                 ");
			sql.append("\n                            SELECT EVNT_PGRS_CLSS                  ");
			sql.append("\n                            FROM (select EVNT_PGRS_CLSS, JUMIN_NO from BCDBA.TBGGOLFEVNTAPLC WHERE GOLF_SVC_APLC_CLSS = '1003'  ORDER BY APLC_SEQ_NO DESC)                  ");
			sql.append("\n                            WHERE  JUMIN_NO = A.JUMIN_NO AND ROWNUM < 2                  ");
			sql.append("\n                            ) AS EVNT_PGRS_CLSS                      ");
			sql.append("\n                            ,A.PGRS_YN    ");
			sql.append("\n                            ,A.STTL_AMT AS STTL_AMT    ");
			sql.append("\n                            ,NVL(A.CSLT_YN,'') AS CSLT_YN    ");
			sql.append("\n                            ,TO_CHAR(TO_DATE(B.STTL_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') AS STTL_ATON    ");	
			
			sql.append("\n                      FROM BCDBA.TBGAPLCMGMT	A ,  BCDBA.TBGSTTLMGMT B 							                                 ");			
			sql.append("\n                      WHERE A.CO_NM = B.ODR_NO(+)                                   ");			
			sql.append("\n                      AND A.GOLF_SVC_APLC_CLSS = '1003'          ");
						
			if("Y".equals(sch_yn))
			{
			
				if(!"".equals(sch_reg_aton_st) && !"".equals(sch_reg_aton_ed)){
					if(sch_date.equals("join_date")){
						sql.append("\n                        AND A.REG_ATON BETWEEN ? AND TO_CHAR(TO_DATE(?)+1,'YYYYMMDD')	");	// ��û���� 
					}else if(sch_date.equals("del_date")){
						sql.append("\n                        AND A.CHNG_ATON BETWEEN ? AND TO_CHAR(TO_DATE(?)+1,'YYYYMMDD')	");	// �������
					}else if(sch_date.equals("start_date")){
						sql.append("\n                        AND A.PU_DATE BETWEEN ? AND TO_CHAR(TO_DATE(?)+1,'YYYYMMDD')	");	// ��������						
					}else{
						sql.append("\n                        AND A.PU_DATE BETWEEN ? AND TO_CHAR(TO_DATE(?)+1,'YYYYMMDD')	");	// ��ŷ����
					}
				}
				
				
				if(!"".equals(search_word))
				{
					if("ID".equals(sch_type)){
						sql.append("\n                        AND A.CDHD_ID LIKE '%'||?||'%'                                                    ");
					}else if("NAME".equals(sch_type)){
						sql.append("\n                        AND A.BKG_PE_NM LIKE '%'||?||'%'                                                      ");
					}else if("JUMINNO".equals(sch_type)){
						sql.append("\n                        AND A.JUMIN_NO LIKE '%'||?||'%'                                                      ");
					}else if("MOBILE".equals(sch_type)){
						sql.append("\n                        AND A.HP_DDD_NO||HP_TEL_HNO||HP_TEL_SNO LIKE '%'||?||'%'                                                      ");
					}else{
						sql.append("\n                        AND (A.CDHD_ID LIKE '%'||?||'%' OR A.CO_NM LIKE '%'||?||'%'  OR A.JUMIN_NO LIKE '%'||?||'%' )                      ");
					}
				}
								
				if(!"".equals(sch_sttl_stat_clss))
				{
					if("1".equals(sch_sttl_stat_clss)) //�̰���
					{
						sql.append("\n                        AND A.PGRS_YN = 'I'                                                    ");
					}
					else if("2".equals(sch_sttl_stat_clss)) //�����Ϸ�
					{
						sql.append("\n                        AND ( A.PGRS_YN = 'Y' OR A.PGRS_YN = 'G' )                                  ");
					}
					else if("3".equals(sch_sttl_stat_clss)) //���
					{
						sql.append("\n                        AND A.PGRS_YN = 'C'                                                    ");
					}
					
				}
				if(!"".equals(green_nm))
				{
					sql.append("\n                        AND A.LESN_SEQ_NO = ?                                                    ");
				}
				
				if(!"".equals(sch_rsvt_date))
				{
					sql.append("\n                        AND A.CSLT_YN = ?                                                    ");
				}
				
				
			}
		  
			
			sql.append("\n                ORDER BY APLC_SEQ_NO DESC          ) D                                                     ");		
			if(!"".equals(golf_cmmn_code) && "Y".equals(sch_yn))
			{
			sql.append("\n             WHERE EVNT_PGRS_CLSS = ?                                                    ");
			}
			

			return sql.toString();
	    } 

	/** ***********************************************************************
	    * Query�� �����Ͽ� �����Ѵ�.    
	    ************************************************************************ */
		
	    private String getSelectQuery(TaoDataSet data) throws BaseException{
	        StringBuffer sql = new StringBuffer();

	        String actnKey          = data.getString("actnKey");
	        String sch_reg_aton_st	= data.getString("sch_reg_aton_st");     //��ȸ ��û ������ 
			String sch_reg_aton_ed	= data.getString("sch_reg_aton_ed"); 	 //��ȸ ��û ������ 
			String sch_date			= data.getString("sch_date");            //�̸�,ID��ȸ ����  
			String sch_type			= data.getString("sch_type");            //�̸�,ID��ȸ ����     
			String search_word		= data.getString("search_word");         //��ȸ ��     
			String golf_cmmn_code	= data.getString("golf_cmmn_code");      //�����ڵ�
			String green_nm			= data.getString("green_nm");            //����������
			String sch_sttl_stat_clss	= data.getString("sch_sttl_stat_clss");  
			String sch_rsvt_date	= data.getString("sch_rsvt_date");  
			//String sch_rsv_time		= data.getString("sch_rsv_time");  
			String sch_yn			= data.getString("sch_yn");  
			
			sql.append("\n     SELECT E.*                                                                                                 ");
			sql.append("\n       FROM (SELECT D.*,ROWNUM RNUM, CEIL(ROWNUM/?) AS PAGE,MAX(RN) OVER() TOT_CNT,                           ");
			sql.append("\n                    (MAX(RN) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) AS LIST_NO                              ");
			sql.append("\n               FROM (SELECT ROWNUM RN,APLC_SEQ_NO,												           ");
			sql.append("\n                            A.CDHD_ID,BKG_PE_NM,JUMIN_NO, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, DDD_NO, TEL_HNO, TEL_SNO,DTL_ADDR,PU_DATE,                ");
			sql.append("\n                            DECODE(LESN_SEQ_NO,'1','���� ������','2','���� ���� ������') LESN_SEQ_NO ");				
			sql.append("\n                            ,TO_CHAR(TO_DATE(A.REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') AS REG_ATON               ");
			sql.append("\n                            ,(                 ");
			sql.append("\n                            SELECT EVNT_PGRS_CLSS                  ");
			sql.append("\n                            FROM (select EVNT_PGRS_CLSS, JUMIN_NO from BCDBA.TBGGOLFEVNTAPLC WHERE GOLF_SVC_APLC_CLSS = '1003'  ORDER BY APLC_SEQ_NO DESC)                  ");
			sql.append("\n                            WHERE  JUMIN_NO = A.JUMIN_NO AND ROWNUM < 2                  ");
			sql.append("\n                            ) AS EVNT_PGRS_CLSS                      ");
			sql.append("\n                            ,A.PGRS_YN    ");
			sql.append("\n                            ,A.STTL_AMT AS STTL_AMT    ");
			sql.append("\n                            ,NVL(A.CSLT_YN,'') AS CSLT_YN    ");
			sql.append("\n                            ,TO_CHAR(TO_DATE(B.STTL_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') AS STTL_ATON    ");	
			
			sql.append("\n                      FROM BCDBA.TBGAPLCMGMT	A ,  BCDBA.TBGSTTLMGMT B 							                                 ");			
			sql.append("\n                      WHERE A.CO_NM = B.ODR_NO(+)                                   ");			
			sql.append("\n                      AND A.GOLF_SVC_APLC_CLSS = '1003'          ");
						
			if("Y".equals(sch_yn))
			{
			
				if(!"".equals(sch_reg_aton_st) && !"".equals(sch_reg_aton_ed)){
					if(sch_date.equals("join_date")){
						sql.append("\n                        AND A.REG_ATON BETWEEN ? AND TO_CHAR(TO_DATE(?)+1,'YYYYMMDD')	");	// ��û���� 
					}else if(sch_date.equals("del_date")){
						sql.append("\n                        AND A.CHNG_ATON BETWEEN ? AND TO_CHAR(TO_DATE(?)+1,'YYYYMMDD')	");	// �������
					}else if(sch_date.equals("start_date")){
						sql.append("\n                        AND A.PU_DATE BETWEEN ? AND TO_CHAR(TO_DATE(?)+1,'YYYYMMDD')	");	// ��������						
					}else{
						sql.append("\n                        AND A.PU_DATE BETWEEN ? AND TO_CHAR(TO_DATE(?)+1,'YYYYMMDD')	");	// ��ŷ����
					}
				}
				
				
				if(!"".equals(search_word))
				{
					if("ID".equals(sch_type)){
						sql.append("\n                        AND A.CDHD_ID LIKE '%'||?||'%'                                                    ");
					}else if("NAME".equals(sch_type)){
						sql.append("\n                        AND A.BKG_PE_NM LIKE '%'||?||'%'                                                      ");
					}else if("JUMINNO".equals(sch_type)){
						sql.append("\n                        AND A.JUMIN_NO LIKE '%'||?||'%'                                                      ");
					}else if("MOBILE".equals(sch_type)){
						sql.append("\n                        AND A.HP_DDD_NO||HP_TEL_HNO||HP_TEL_SNO LIKE '%'||?||'%'                                                      ");
					}else{
						sql.append("\n                        AND (A.CDHD_ID LIKE '%'||?||'%' OR A.CO_NM LIKE '%'||?||'%'  OR A.JUMIN_NO LIKE '%'||?||'%' )                      ");
					}
				}
								
				if(!"".equals(sch_sttl_stat_clss))
				{
					if("1".equals(sch_sttl_stat_clss)) //�̰���
					{
						sql.append("\n                        AND A.PGRS_YN = 'I'                                                    ");
					}
					else if("2".equals(sch_sttl_stat_clss)) //�����Ϸ�
					{
						sql.append("\n                        AND ( A.PGRS_YN = 'Y' OR A.PGRS_YN = 'G' )                                  ");
					}
					else if("3".equals(sch_sttl_stat_clss)) //���
					{
						sql.append("\n                        AND A.PGRS_YN = 'C'                                                    ");
					}
					
				}
				if(!"".equals(green_nm))
				{
					sql.append("\n                        AND A.LESN_SEQ_NO = ?                                                    ");
				}
				
				if(!"".equals(sch_rsvt_date))
				{
					sql.append("\n                        AND A.CSLT_YN = ?                                                    ");
				}
				
				
			}
		  

			sql.append("\n                ORDER BY APLC_SEQ_NO DESC          ) D                                                     ");		
			if(!"".equals(golf_cmmn_code) && "Y".equals(sch_yn))
			{
			sql.append("\n             WHERE EVNT_PGRS_CLSS = ?                                                    ");
			}
			sql.append("\n                                                                                                            ");
			sql.append("\n             ) E                                                                                             ");
			if(actnKey.equals("admWorldPrimeAppList")){
				sql.append("\n      WHERE PAGE = ?                                                                                          ");
			}

			return sql.toString();
	    } 

	
	/* Proc ����. 
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult setFinalCncl(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");		
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			
			//��ȸ ----------------------------------------------------------
			

			String sql = this.getFinalCnclSQL();   			
			
			String aplc_seq_no	= data.getString("aplc_seq_no");      //�����ȣ         			
			String cslt_yn      = data.getString("cslt_yn");
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			
			pstmt.setString(++idx, cslt_yn);	
			pstmt.setString(++idx, aplc_seq_no);			
			
			conn.setAutoCommit(false);
			if(pstmt.executeUpdate() > 0){
				conn.commit();
				conn.setAutoCommit(true);
				result.addString("RESULT","00");
			}else{
				result.addString("RESULT","01");				
			}
			
			
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {			
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
	public DbTaoResult setCancel(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");		
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			
			//��ȸ ----------------------------------------------------------
			

			String sql = this.getCancelSQL(data);   
			
			String cncl_param	= data.getString("cncl_param");       //���� ���� �ڵ��
			String aplc_seq_no	= data.getString("aplc_seq_no");      //�����ȣ         
			String cdhd_id		= data.getString("cdhd_id");          //ID
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());

			pstmt.setString(++idx, cncl_param);
			pstmt.setString(++idx, aplc_seq_no);
			pstmt.setString(++idx, cdhd_id);			
			
			conn.setAutoCommit(false);
			if(pstmt.executeUpdate() > 0){
				conn.commit();
				conn.setAutoCommit(true);
				result.addString("RESULT","00");
			}else{
				result.addString("RESULT","01");				
			}
			
			
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {			
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
	public DbTaoResult setUpdateEvnt(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");		
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			
			//��ȸ ----------------------------------------------------------
			

			String sql = this.getUpdateEvntSQL(data);   
			
			String cdhd_id			= data.getString("cdhd_id");              //ID
			String golf_cmmn_codes	= data.getString("golf_cmmn_codes");      //��������ڵ�
			String aplc_seq_no		= data.getString("aplc_seq_no");   	      //�����ȣ
			String green_nms	= data.getString("green_nms");                //��û�������
			String dprt_pl_info	= data.getString("dprt_pl_info");             //����������
			String pu_date		= data.getString("pu_date");                  //��ŷ����
			String pu_time		= data.getString("pu_time");                  //��ŷ�ð�
			String hp_ddd_no	= data.getString("hp_ddd_no");
			String hp_tel_hno	= data.getString("hp_tel_hno");
			String hp_tel_sno	= data.getString("hp_tel_sno");
			String co_nm		= data.getString("co_nm");
			String teof_date	= data.getString("teof_date");
			String teof_time		= data.getString("teof_time");
			String[] pudarry = pu_date.split("-");
			debug(">>>>>>>>>>>>>>>>>>pu_date :" + pu_date);
			pu_date = pu_date.replaceAll("-","");
			debug(">>>>>>>>>>>>>>>>>>pu_date2 :" + pu_date);
			debug(">>>>>>>>>>>>>>>>>>pu_time :" + pu_time);
			debug(">>>>>>>>>>>>>>>>>>pu_date.length() :" + pu_date.length());
			//debug(">>>>>>>>>>>>>>>>>>pudarry[0] :" + pudarry[0]);
			//debug(">>>>>>>>>>>>>>>>>>pudarry[1] :" + pudarry[1]);
			//debug(">>>>>>>>>>>>>>>>>>pudarry[2] :" + pudarry[2]);
			//debug(">>>>>>>>>>>>>>>>>>pu_date.substring(6,2) :" + pu_date.substring(6,2));


			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());

			pstmt.setString(++idx, green_nms);
			pstmt.setString(++idx, dprt_pl_info);
			pstmt.setString(++idx, golf_cmmn_codes);
			pstmt.setString(++idx, pu_date);
			pstmt.setString(++idx, pu_time);
			pstmt.setString(++idx, aplc_seq_no);			
			pstmt.setString(++idx, cdhd_id);
			
			conn.setAutoCommit(false);
			if(pstmt.executeUpdate() > 0){
				conn.commit();
				conn.setAutoCommit(true);
				result.addString("RESULT","00");
				
				if(("W".equals(golf_cmmn_codes) || "B".equals(golf_cmmn_codes)) && !"".equals(co_nm) && !"".equals(hp_ddd_no)) {
					HashMap smsMap = new HashMap();
					
					smsMap.put("ip", request.getRemoteAddr());
					smsMap.put("sName", co_nm);
					smsMap.put("sPhone1", hp_ddd_no);
					smsMap.put("sPhone2", hp_tel_hno);
					smsMap.put("sPhone3", hp_tel_sno);
					
					//debug("SMS����>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
					//- ���� ��û�� : [Golf Loun.G]000��,00��00��00�ô� ��Ⳳ�α� ������ ��û �Ͽ����ϴ�
					//- ��ŷ���� : [Golf Loun.G]000��,00��00��00:00�� �Ŷ�� ��ŷ�����̴� Ȯ���Ͽ� �ֽʽÿ�
					//- ��ŷȮ���� : [Golf Loun.G]000��,00��00��00:00�� �Ŷ�� ��ŷȮ���Ǿ����ϴ�
	
					String smsClss = "641";
					String message = "";
					
					//pu_date = pu_date.substring(8,2) + "�ô�";
					
					//if("R".equals(golf_cmmn_codes)) {		// ���� ��û��
						//message = "[Golf Loun.G]"+co_nm+"��," + teof_date.substring(4,2) + "��"+teof_date.substring(6,2)+"��"+teof_time+" " + green_nms + " ������ ��û �Ͽ����ϴ�";
					//} else if("W".equals(golf_cmmn_codes)) {	// ��ŷ����
					if("W".equals(golf_cmmn_codes)) {	// ��ŷ����
						message = "[Golf Loun.G]"+co_nm+"��," + pudarry[1] + "��"+pudarry[2]+"��"+pu_time+"�ÿ� "+dprt_pl_info+"���� ��ŷ�����̴� Ȯ���Ͽ� �ֽʽÿ�";						
					} else if("B".equals(golf_cmmn_codes)) {	// ��ŷȮ����
						message = "[Golf Loun.G]"+co_nm+"��," + pudarry[1] + "��"+pudarry[2]+"��"+pu_time+"�ÿ� "+dprt_pl_info+"���� ��ŷȮ�� �Ǿ����ϴ�";
					}
					SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
					String smsRtn = smsProc.send(smsClss, smsMap, message);
					//debug("SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);
				}
				
			}else{
				result.addString("RESULT","01");				
			}
			
			
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {			
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
	public DbTaoResult setUpdateUsr(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");		
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			
			//��ȸ ----------------------------------------------------------
			

			String sql = this.getUpdateUsrSQL();   
			
			String estm_itm_clss	= data.getString("estm_itm_clss");       //ȸ���� ���
			String cdhd_id	= data.getString("cdhd_id");                     //���̵�
			String appr_opion		= data.getString("appr_opion");          //ȸ���� ��
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());

			pstmt.setString(++idx, estm_itm_clss);
			pstmt.setString(++idx, appr_opion);
			pstmt.setString(++idx, cdhd_id);			
			
			conn.setAutoCommit(false);
			if(pstmt.executeUpdate() > 0){
				conn.commit();
				conn.setAutoCommit(true);
				result.addString("RESULT","00");
			}else{
				result.addString("RESULT","01");				
			}
			
			
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {			
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
	
	public String getMemGradeNm(WaContext context, TaoDataSet data, String cdhd_id) throws BaseException {
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);
		String mem_Grade = "";
		String apo_yn = "N";

		try {
			
			conn = context.getDbConnection("default", null);
			String sql = this.getGradeSelectQuery();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id);
			
			rs = pstmt.executeQuery();
			boolean eof = false;
			
			while(rs.next()){
				if(!eof) result.addString("RESULT", "00");
				
				if(!"".equals(mem_Grade)){
					mem_Grade = mem_Grade + " / "+rs.getString("GOLF_CMMN_CODE_NM");
				}else{
					mem_Grade = rs.getString("GOLF_CMMN_CODE_NM");
				}
				
				
				eof = true;
			}
			
						 
			
		}catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return mem_Grade;
		
	}
	

	

	

	/** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
	
    private String getCancelSQL(TaoDataSet data) throws BaseException{
		StringBuffer sql = new StringBuffer();

		String cd = data.getString("cncl_param");
		
		sql.append("\n     UPDATE BCDBA.TBGAPLCMGMT                    ");
		sql.append("\n        SET PGRS_YN = ?                          ");
		if(cd.equals("C") || cd.equals("E") || cd.equals("L")){
			sql.append("\n   ,CHNG_ATON = TO_CHAR(SYSDATE,'YYYYMMDD')  ");
		}else if(cd.equals("B")){
			sql.append("\n   ,NUM_DDUC_YN = 'Y'                        ");
		}
		sql.append("\n      WHERE APLC_SEQ_NO = ?                      ");
		sql.append("\n        AND GOLF_SVC_APLC_CLSS = '9001'          ");
		sql.append("\n        AND CDHD_ID = ?                          ");

		return sql.toString();
	}

	/** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
	
    private String getUpdateUsrSQL() throws BaseException{
		StringBuffer sql = new StringBuffer();
		
		sql.append("\n     UPDATE BCDBA.TBGGOLFCDHD            ");
		sql.append("\n        SET ESTM_ITM_CLSS = ?,           ");
		sql.append("\n            APPR_OPION = ?               ");
		sql.append("\n      WHERE CDHD_ID = ?                  ");

		return sql.toString();
	}

	/** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
	
    private String getUpdateEvntSQL(TaoDataSet data) throws BaseException{
		StringBuffer sql = new StringBuffer();

		String golf_cmmn_codes = data.getString("golf_cmmn_codes");
		
		sql.append("\n     UPDATE BCDBA.TBGAPLCMGMT               ");
		sql.append("\n        SET GREEN_NM = ?,                   ");
		sql.append("\n            DPRT_PL_INFO = ?,               ");
		sql.append("\n            PGRS_YN = ?,                    ");
		sql.append("\n            PU_DATE = ?,                    ");
		if(golf_cmmn_codes.equals("B")){
			sql.append("\n            NUM_DDUC_YN = 'Y',                    ");
		}
		sql.append("\n            PU_TIME = ?                     ");
		sql.append("\n      WHERE APLC_SEQ_NO = ?                 ");
		sql.append("\n        AND GOLF_SVC_APLC_CLSS = '9001'     ");
		sql.append("\n        AND CDHD_ID = ?                     ");

		return sql.toString();
	}

	/** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
	
    private String getFinalCnclSQL() throws BaseException{
		StringBuffer sql = new StringBuffer();
		
		sql.append("\n     UPDATE BCDBA.TBGAPLCMGMT               ");
		sql.append("\n        SET CSLT_YN = ?                     ");		
		sql.append("\n      WHERE APLC_SEQ_NO = ?                 ");
		sql.append("\n        AND GOLF_SVC_APLC_CLSS = '9001'     ");
		
		return sql.toString();
	}

    /** ***********************************************************************
     * Query�� �����Ͽ� �����Ѵ�.    
     ************************************************************************ */
     private String getGradeSelectQuery(){
         StringBuffer sql = new StringBuffer();
         
 		sql.append("	SELECT 	T3.GOLF_CMMN_CODE_NM   													\n");
 		sql.append("	FROM BCDBA.TBGGOLFCDHDGRDMGMT T1 					  							\n");
 		sql.append("	 JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.CDHD_CTGO_SEQ_NO = T2.CDHD_CTGO_SEQ_NO \n");
 		sql.append("	 JOIN BCDBA.TBGCMMNCODE T3 ON T3.GOLF_CMMN_CODE = T2.CDHD_SQ2_CTGO  			\n");
 		sql.append("	 JOIN BCDBA.TBGGOLFCDHDBNFTMGMT T4 ON T4.CDHD_SQ2_CTGO = T2.CDHD_SQ2_CTGO  		\n");
 		sql.append("	 WHERE T1.CDHD_ID = ?  AND T3.GOLF_CMMN_CLSS='0005'  							\n");
 		
 		
     return sql.toString();
     }

	
}

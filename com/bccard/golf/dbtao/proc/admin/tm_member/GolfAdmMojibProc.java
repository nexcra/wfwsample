/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmMojibProc
*   �ۼ���    : E4NET ���弱
*   ����      : ���� �����Ͻ� ����
*   �������  : Golf
*   �ۼ�����  : 2009-09-03  
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.tm_member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

import com.bccard.golf.dbtao.*;
 
/** *****************************************************************
 * GolfAdmMojibProc ���μ��� ������
 * @param N/A
 ***************************************************************** */
public class GolfAdmMojibProc extends AbstractProc {
		
	private static final String TITLE = "������ ����";		
	
	/** *****************************************************************
	 * GolfAdmMojibProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmMojibProc() {}
	
	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult 
	 */
	public DbTaoResult execute_partner(WaContext context, TaoDataSet dataSet) throws BaseException {		
		String title = dataSet.getString("TITLE");
		Connection con = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		ResultSet rset = null;

		int count = 0;
		DbTaoResult  result =  new DbTaoResult(title);
		
		/*1. tbggolfcdhd �� ȸ������ Ȯ��
		 * ����ȸ���ϰ�쿣 update ó�� (tm��� ���̺� 00ó��)
		 * ����ȸ���ϰ�쿣 ���� ó�� (�޼���ó���Ұ�) -> ��ó���� 99�� ����ó��
		 * ȸ���� �ƴҰ�쿣  (tm��� ���̺� 01 ó��)
		*/


		try{			
			con = context.getDbConnection("default", null);
			String sql = this.setGrdSql();
			
			pstmt = con.prepareStatement(sql);
			int pidx = 0;

			pstmt.setString(++pidx, dataSet.getString("jumin_no") );				//����

			rset = pstmt.executeQuery();	
			result = new DbTaoResult(TITLE);

			while(rset.next()){		
	
				result.addString("CDHD_ID", rset.getString("CDHD_ID"));
				result.addString("CDHD_CTGO_SEQ_NO", rset.getString("CDHD_CTGO_SEQ_NO"));
				result.addString("SECE_YN", rset.getString("SECE_YN"));
				result.addString("ACRG_CDHD_JONN_DATE", rset.getString("ACRG_CDHD_JONN_DATE"));
				result.addString("ACRG_CDHD_END_DATE", rset.getString("ACRG_CDHD_END_DATE"));

			}
			//������ ȸ��
			if (result.size() == 0) {
				result.addString("RESULT", "00"); //���� 
			} else {
				if (result != null ) {
					result.next();
				}

				//ȭ��Ʈ ȸ���̰ų� ����ȸ���ϰ�� 
				if ( "8".equals(result.getString("CDHD_CTGO_SEQ_NO")) || "Y".equals(result.getString("SECE_YN")) )
				{
										  
					pstmt2 = con.prepareStatement(this.getDeleteCDHDSql());
					int idx = 0;
					pstmt2.setString(++idx, result.getString("CDHD_ID")	 );
					int res = pstmt2.executeUpdate();

					pstmt2 = con.prepareStatement(this.getDeleteGRDSql());
				    idx = 0;
					pstmt2.setString(++idx, result.getString("CDHD_ID")	 );
					res = pstmt2.executeUpdate();

					if ( res == 1 ) {
						result = new DbTaoResult(TITLE);

						result.addString("RESULT", "00");
					}
				//����ȸ���ϰ��
				} else {
					result = new DbTaoResult(TITLE);

					result.addString("RESULT", "01"); //����

				}


			}
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {			
			try { if(rset  != null) rset.close();  } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(pstmt2 != null) pstmt2.close(); } catch (Exception ignored) {}
			try { if(con  != null) con.close();  } catch (Exception ignored) {}			
		}		
		return result;
	}		
	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult 
	 */
	/*�Է�*/
	public DbTaoResult execute(WaContext context, TaoDataSet dataSet) throws BaseException {		

		String title = dataSet.getString("TITLE");
		Connection con = null;
		PreparedStatement pstmt = null;
		int count = 0;
		DbTaoResult  result =  new DbTaoResult(title);
			
		try{
			con = context.getDbConnection("default", null);			
			
			String acpt_chnl_clss		= dataSet.getString("acpt_chnl_clss");
			String rcru_pl_clss			= dataSet.getString("rcru_pl_clss");
			String tb_rslt_clss			= dataSet.getString("tb_rslt_clss");
			String hg_nm				= dataSet.getString("hg_nm");
			String jumin_no				= dataSet.getString("jumin_no");
			String hp_ddd_no			= dataSet.getString("hp_ddd_no");
			String hp_tel_hno			= dataSet.getString("hp_tel_hno");
			String hp_tel_sno			= dataSet.getString("hp_tel_sno");
			String hom_zp				= dataSet.getString("hom_zp");
			String hom_dong_blw_addr	= dataSet.getString("hom_dong_blw_addr");
			String addr_clss			= dataSet.getString("addr_clss");
			String co_nm				= dataSet.getString("co_nm");
			String wkpl_ddd_no			= dataSet.getString("wkpl_ddd_no");
			String wkpl_tel_hno			= dataSet.getString("wkpl_tel_hno");
			String wkpl_tel_sno			= dataSet.getString("wkpl_tel_sno");
			String eng_nm				= dataSet.getString("eng_nm");
			String btdt					= dataSet.getString("btdt");
			String scal_lcal_clss		= dataSet.getString("scal_lcal_clss");
			String hom_ddd_no			= dataSet.getString("hom_ddd_no");
			String hom_tel_hno			= dataSet.getString("hom_tel_hno");
			String hom_tel_sno			= dataSet.getString("hom_tel_sno");
			String jobtl_nm				= dataSet.getString("jobtl_nm");
			String email_id				= dataSet.getString("email_id");
			String card_no				= dataSet.getString("card_no");
			String join_chnl			= dataSet.getString("join_chnl");
			String conc_date			= dataSet.getString("conc_date");
			String emp_no				= dataSet.getString("emp_no");
			String acpt_pl_chg_nm		= dataSet.getString("acpt_pl_chg_nm");
			String auth_clss            = dataSet.getString("auth_clss");
			String golf_cdhd_grd_clss   = dataSet.getString("golf_cdhd_grd_clss");
			String vald_lim				= dataSet.getString("vald_lim");
			String dc_amt				= dataSet.getString("dc_amt");
			String disc_clss            = dataSet.getString("disc_clss");
			   
			String 	sStrSql = this.getInsertSql();	
								  
			pstmt = con.prepareStatement(sStrSql);
			// �Է°� (INPUT)
			
			int idx = 0;
			
			pstmt.setString(++idx,acpt_chnl_clss	 );
			pstmt.setString(++idx,hg_nm				 );
			pstmt.setString(++idx,btdt				 );
			pstmt.setString(++idx,email_id			 );
			pstmt.setString(++idx,hom_ddd_no		 );
			pstmt.setString(++idx,hom_tel_hno		 );
			pstmt.setString(++idx,hom_tel_sno		 );
			pstmt.setString(++idx,wkpl_ddd_no		 );
			pstmt.setString(++idx,wkpl_tel_hno		 );
			pstmt.setString(++idx,wkpl_tel_sno		 );
			pstmt.setString(++idx,hp_ddd_no			 );
			pstmt.setString(++idx,hp_tel_hno		 );
			pstmt.setString(++idx,hp_tel_sno		 );
			pstmt.setString(++idx,tb_rslt_clss			 );
			pstmt.setString(++idx,jumin_no			 );
			pstmt.setString(++idx,conc_date			 );

			pstmt.setString(++idx,emp_no			 );
			pstmt.setString(++idx,join_chnl			 );
			pstmt.setString(++idx,auth_clss			 );
			pstmt.setString(++idx,card_no			 );
			pstmt.setString(++idx,scal_lcal_clss	 );
			pstmt.setString(++idx,eng_nm			 );
			pstmt.setString(++idx,co_nm				 );
			pstmt.setString(++idx,jobtl_nm			 );			
			pstmt.setString(++idx,hom_zp			 );
			pstmt.setString(++idx,hom_dong_blw_addr  );						
			pstmt.setString(++idx,acpt_pl_chg_nm	 );
			pstmt.setString(++idx,golf_cdhd_grd_clss );
			pstmt.setString(++idx,vald_lim );
			pstmt.setString(++idx,dc_amt );
			pstmt.setString(++idx,disc_clss );
			pstmt.setString(++idx,rcru_pl_clss );
			pstmt.setString(++idx,addr_clss  		 );
		
			int res = pstmt.executeUpdate();
			
			result = new DbTaoResult(TITLE);
			
			if ( res == 1 ) {
				result.addString("RESULT", "00");
			}else{
				result.addString("RESULT", "01");
			}
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {			
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}		
		
		return result;	
	}

	/*�����ȣ*/
	public DbTaoResult getList(WaContext context, TaoDataSet dataSet) throws BaseException {
		String title = dataSet.getString("TITLE");
		Connection con = null;
		PreparedStatement pstmt = null; 
		ResultSet rset = null;
		int count = 0;
		DbTaoResult  result =  new DbTaoResult(title);

		try{			
			con = context.getDbConnection("default", null);
			String sql = this.getSelectQuery(dataSet);
			
			String search_Keyword = dataSet.getString("Search_Keyword");						//����
			
			pstmt = con.prepareStatement(sql);
			int pidx = 0;

			pstmt.setString(++pidx,"%" + StrUtil.isNull(search_Keyword,"") + "%" );				//����

			rset = pstmt.executeQuery();			

			while(rset.next()){				
				result.addString("RESULT", "00");
				result.addString("ZIPCODE1", rset.getString("ZIPCODE1"));
				result.addString("ZIPCODE2", rset.getString("ZIPCODE2"));
				result.addString("SIDO", rset.getString("SIDO"));
				result.addString("SIGUGUN", rset.getString("SIGUGUN"));
				result.addString("DONG", rset.getString("DONG"));
				result.addString("DOSEO", rset.getString("DOSEO"));
				result.addString("BATCH_ADDR", rset.getString("BATCH_ADDR"));
				result.addString("BUNJI1", rset.getString("BUNJI1"));
				result.addString("BUNJI2", rset.getString("BUNJI2"));		
			}
			
			if (result.size() < 1) {
				result.addString("RESULT", "01");
			}
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {			
			try { if(rset  != null) rset.close();  } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(con  != null) con.close();  } catch (Exception ignored) {}			
		}		
		return result;
	}
	
	/* ������ ������ */
	public DbTaoResult getDetail(WaContext context, TaoDataSet dataSet) throws BaseException {
		String title = dataSet.getString("TITLE");
		Connection con = null;
		PreparedStatement pstmt = null; 
		ResultSet rset = null;
		int count = 0;
		DbTaoResult  result =  new DbTaoResult(title);

		try{			
			con = context.getDbConnection("default", null);
			String sql = this.getDetailSql();
			
			String jumin_no = dataSet.getString("jumin_no");
			String work_date = dataSet.getString("work_date");	

			pstmt = con.prepareStatement(sql);
			int pidx = 0;

			pstmt.setString(1, jumin_no);
			pstmt.setString(2, work_date);

			rset = pstmt.executeQuery();			

			while(rset.next()){		
				
				String addrClss = rset.getString("NW_OLD_ADDR_CLSS");
				
				if ( addrClss == null || addrClss.trim().equals("")){
					addrClss = "1";
				}
				
				result.addString("result", "00");
				result.addString("hg_nm",rset.getString("HG_NM"));
				result.addString("bthd1",rset.getString("BTHD1"));
				result.addString("bthd2",rset.getString("BTHD2"));
				result.addString("bthd3",rset.getString("BTHD3"));
				result.addString("email_id",rset.getString("EMAIL_ID"));
				result.addString("hom_ddd_no",rset.getString("HOM_DDD_NO"));
				result.addString("hom_tel_hno",rset.getString("HOM_TEL_HNO"));
				result.addString("hom_tel_sno",rset.getString("HOM_TEL_SNO"));
				result.addString("wkpl_ddd_no",rset.getString("WKPL_DDD_NO"));
				result.addString("wkpl_tel_hno",rset.getString("WKPL_TEL_HNO"));
				result.addString("wkpl_tel_sno",rset.getString("WKPL_TEL_SNO"));
				result.addString("hp_ddd_no",rset.getString("HP_DDD_NO"));
				result.addString("hp_tel_hno",rset.getString("HP_TEL_HNO"));
				result.addString("hp_tel_sno",rset.getString("HP_TEL_SNO"));
				result.addString("tb_rslt_clss",rset.getString("TB_RSLT_CLSS"));
				result.addString("recp_date",rset.getString("RECP_DATE"));
				result.addString("jumin_no1",rset.getString("JUMIN_NO1"));
				result.addString("jumin_no2",rset.getString("JUMIN_NO2"));
				result.addString("rnd_cd_clss",rset.getString("RND_CD_CLSS"));
				result.addString("conc_date",rset.getString("CONC_DATE"));
				result.addString("emp_no",rset.getString("EMP_NO"));
				result.addString("join_chnl",rset.getString("JOIN_CHNL"));
				result.addString("auth_clss_txt",rset.getString("AUTH_CLSS_TXT"));
				result.addString("card_no1",rset.getString("CARD_NO1"));
				result.addString("card_no2",rset.getString("CARD_NO2"));
				result.addString("card_no3",rset.getString("CARD_NO3"));
				result.addString("card_no4",rset.getString("CARD_NO4"));
				result.addString("golf_cdhd_grd_clss_txt",rset.getString("GOLF_CDHD_GRD_CLSS_TXT"));
				result.addString("acpt_chnl_clss",rset.getString("ACPT_CHNL_CLSS"));
				result.addString("scal_lcal_clss",rset.getString("SCAL_LCAL_CLSS"));
				result.addString("eng_nm",rset.getString("ENG_NM"));
				result.addString("co_nm",rset.getString("CO_NM"));
				result.addString("jobtl_nm",rset.getString("JOBTL_NM"));
				result.addString("hom_zp1",rset.getString("HOM_ZP1"));
				result.addString("hom_zp2",rset.getString("HOM_ZP2"));
				result.addString("zipaddr",rset.getString("ZIPADDR"));
				result.addString("detailaddr",rset.getString("DETAILADDR"));				
				result.addString("addrClss", addrClss );
				result.addString("acpt_pl_chg_nm",rset.getString("ACPT_PL_CHG_NM"));
				result.addString("auth_clss",rset.getString("AUTH_CLSS"));	
				result.addString("golf_cdhd_grd_clss",rset.getString("GOLF_CDHD_GRD_CLSS"));	
				result.addString("rej_rson",rset.getString("REJ_RSON"));
				result.addString("sttl_fail_rson_ctnt",rset.getString("STTL_FAIL_RSON_CTNT"));
				result.addString("vald_lim1",rset.getString("VALD_LIM1")); //��
				result.addString("vald_lim2",rset.getString("VALD_LIM2")); //��
				result.addString("dc_amt",rset.getString("DC_AMT"));	
				result.addString("disc_clss",rset.getString("DISC_CLSS")); 
				
			}
			
			if (result.size() < 1) {
				result.addString("RESULT", "01");
			}
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {	
			try { if(rset  != null) rset.close();  } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}		
		return result;
	}
	

	/*��������*/
	public DbTaoResult getCommonCode(WaContext context, TaoDataSet dataSet, String code ) throws BaseException {
		String title = dataSet.getString("TITLE");
		Connection con = null;
		PreparedStatement pstmt = null; 
		ResultSet rset = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try{			
			con = context.getDbConnection("default", null);
			String sql = this.getCommonCodeSql();			

			pstmt = con.prepareStatement(sql);	
			pstmt.setString(1, code);

			rset = pstmt.executeQuery();			

			while(rset.next()){				
				result.addString("result", "00");
				result.addString("golf_cmmn_code",rset.getString("GOLF_CMMN_CODE"));	
				result.addString("golf_cmmn_code_nm",rset.getString("GOLF_CMMN_CODE_NM"));			
			}
			
			if (result.size() < 1) {
				result.addString("RESULT", "01");
			}
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {	
			try { if(rset  != null) rset.close();  } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}		
		return result;
	}
	
	/*����*/
	public DbTaoResult setUpdate(WaContext context, TaoDataSet dataSet) throws BaseException {		

		String title = dataSet.getString("TITLE");
		Connection con = null;
		PreparedStatement pstmt = null;
		int count = 0;
		DbTaoResult  result =  new DbTaoResult(title);
			
		try{
			con = context.getDbConnection("default", null);			

			String hg_nm				= dataSet.getString("hg_nm");
			String jumin_no				= dataSet.getString("jumin_no");
			String hp_ddd_no			= dataSet.getString("hp_ddd_no");
			String hp_tel_hno			= dataSet.getString("hp_tel_hno");
			String hp_tel_sno			= dataSet.getString("hp_tel_sno");
			String hom_zp				= dataSet.getString("hom_zp");
			String hom_dong_blw_addr	= dataSet.getString("hom_dong_blw_addr");
			String co_nm				= dataSet.getString("co_nm");
			String wkpl_ddd_no			= dataSet.getString("wkpl_ddd_no");
			String wkpl_tel_hno			= dataSet.getString("wkpl_tel_hno");
			String wkpl_tel_sno			= dataSet.getString("wkpl_tel_sno");
			String eng_nm				= dataSet.getString("eng_nm");
			String btdt					= dataSet.getString("btdt");
			String scal_lcal_clss		= dataSet.getString("scal_lcal_clss");
			String hom_ddd_no			= dataSet.getString("hom_ddd_no");
			String hom_tel_hno			= dataSet.getString("hom_tel_hno");
			String hom_tel_sno			= dataSet.getString("hom_tel_sno");
			String jobtl_nm				= dataSet.getString("jobtl_nm");
			String email_id				= dataSet.getString("email_id");
			String card_no				= dataSet.getString("card_no");
			String join_chnl			= dataSet.getString("join_chnl");
			String conc_date			= dataSet.getString("conc_date");
			String emp_no				= dataSet.getString("emp_no");
			String acpt_pl_chg_nm		= dataSet.getString("acpt_pl_chg_nm");
			String auth_clss            = dataSet.getString("auth_clss");
			String work_date			= dataSet.getString("work_date");
			String golf_cdhd_grd_clss   = dataSet.getString("golf_cdhd_grd_clss");
			String vald_lim				= dataSet.getString("vald_lim");
			String dc_amt				= dataSet.getString("dc_amt");
			String disc_clss            = dataSet.getString("disc_clss");
			String addr_clss 			= dataSet.getString("addr_clss"); //�ּұ���(��:1, ��:2)
			   
			String 	sStrSql = this.setUpdateSql();	
								  
			pstmt = con.prepareStatement(sStrSql);
			// �Է°� (INPUT)
			
			int idx = 0;

			pstmt.setString(++idx, hg_nm			 );
			pstmt.setString(++idx, btdt				 );
			pstmt.setString(++idx, email_id			 );
			pstmt.setString(++idx, hom_ddd_no		 );
			pstmt.setString(++idx, hom_tel_hno		 );
			pstmt.setString(++idx, hom_tel_sno		 );
			pstmt.setString(++idx, wkpl_ddd_no		 );
			pstmt.setString(++idx, wkpl_tel_hno		 );
			pstmt.setString(++idx, wkpl_tel_sno		 );
			pstmt.setString(++idx, hp_ddd_no		 );
			pstmt.setString(++idx, hp_tel_hno		 );
			pstmt.setString(++idx, hp_tel_sno		 );
			pstmt.setString(++idx, jumin_no			 );
			pstmt.setString(++idx, conc_date		 );
			pstmt.setString(++idx, emp_no			 );
			pstmt.setString(++idx, join_chnl		 );
			pstmt.setString(++idx, auth_clss		 );
			pstmt.setString(++idx, card_no			 );
			pstmt.setString(++idx, scal_lcal_clss	 );
			pstmt.setString(++idx, eng_nm			 );
			pstmt.setString(++idx, co_nm			 );
			pstmt.setString(++idx, jobtl_nm			 );			
			pstmt.setString(++idx, hom_zp			 );
			pstmt.setString(++idx, hom_dong_blw_addr );			
			pstmt.setString(++idx, acpt_pl_chg_nm	 );
			pstmt.setString(++idx, golf_cdhd_grd_clss);
			pstmt.setString(++idx, vald_lim);
			pstmt.setString(++idx, dc_amt			 );
			pstmt.setString(++idx, disc_clss		 );
			pstmt.setString(++idx, addr_clss		 );

			//������
			pstmt.setString(++idx, jumin_no			 );
			pstmt.setString(++idx, work_date		 );
		
			int res = pstmt.executeUpdate();
			
			result = new DbTaoResult(TITLE);
			
			if ( res == 1 ) {
				result.addString("RESULT", "00");
			}else{
				result.addString("RESULT", "01");
			}
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {			
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}		
		
		return result;	
	}
	
	/*����*/
	public DbTaoResult setDelete(WaContext context, TaoDataSet dataSet) throws BaseException {		

		String title = dataSet.getString("TITLE");
		Connection con = null;
		PreparedStatement pstmt = null;
		int count = 0;
		DbTaoResult  result =  new DbTaoResult(title);
			
		try{
			con = context.getDbConnection("default", null);			

			
			String jumin_no				= dataSet.getString("jumin_no");			
			String work_date			= dataSet.getString("work_date");			
			   
			String 	sStrSql = this.setDeleteSql();	
								  
			pstmt = con.prepareStatement(sStrSql);
			// �Է°� (INPUT)
			
			int idx = 0;
			
			pstmt.setString(++idx, jumin_no			 );
			pstmt.setString(++idx, work_date		 );
		
			int res = pstmt.executeUpdate();
			
			result = new DbTaoResult(TITLE);
			
			if ( res == 1 ) {
				result.addString("RESULT", "00");
			}else{
				result.addString("RESULT", "01");
			}
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {			
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}		
		
		return result;	
	}


	/* SQL�� ����� �����Ѵ�. */
	public String getInsertSql(){
		
		StringBuffer sb = new StringBuffer();

		sb.append("\n INSERT INTO BCDBA.TBLUGTMCSTMR (MB_CDHD_NO,ACPT_CHNL_CLSS,HG_NM,BTHD,EMAIL_ID,HOM_DDD_NO,HOM_TEL_HNO,HOM_TEL_SNO,WKPL_DDD_NO,	");
		sb.append("\n WKPL_TEL_HNO,WKPL_TEL_SNO,HP_DDD_NO,HP_TEL_HNO,HP_TEL_SNO,TB_RSLT_CLSS,RECP_DATE,	WK_DATE, WK_TIME,								");
		sb.append("\n JUMIN_NO,RND_CD_CLSS,CONC_DATE,EMP_NO,JOIN_CHNL,AUTH_CLSS,CARD_NO,									");
		sb.append("\n SCAL_LCAL_CLSS,ENG_NM,CO_NM,JOBTL_NM,HOM_ZP,HOM_DONG_BLW_ADDR,ACPT_PL_CHG_NM,GOLF_CDHD_GRD_CLSS,VALD_LIM,DC_AMT,DISC_CLSS, RCRU_PL_CLSS, NW_OLD_ADDR_CLSS)					");
		sb.append("\n VALUES ('1', ?, ?, ?, ?, ?, ?, ?, ?,																					");
		sb.append("\n         ?, ?, ?, ?, ?, ?, to_char(sysdate,'yyyymmdd'), to_char(sysdate,'yyyymmdd'),to_char(sysdate,'hh24miss'),														");
		sb.append("\n         ?, '2', ?, ?, ?, ?, ?, 																				");
		sb.append("\n         ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?)																						");

		return sb.toString();
	}

	private String getSelectQuery(TaoDataSet dataSet) throws Exception{
		
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT	\n");
		sql.append("\t SUBSTR(ZIPCODE,1,3) AS ZIPCODE1,	\n");
		sql.append("\t SUBSTR(ZIPCODE,4,3) AS ZIPCODE2,	\n");
		sql.append("\t SIDO, SIGUGUN, DONG,	\n");
		sql.append("\t DECODE(DOSEO,'','no',' ','no',DOSEO) AS DOSEO,         ");
		sql.append("\t DECODE(BATCH_ADDR,'','no',' ','no',BATCH_ADDR) AS BATCH_ADDR, ");
		sql.append("\t DECODE(ST_BUNJI,'','no',' ','no',ST_BUNJI) AS BUNJI1, ");
		sql.append("\t DECODE(END_BUNJI,'','no',' ','no',END_BUNJI) AS BUNJI2  ");		
		sql.append("FROM BCDBA.TBPOST	\n");
		sql.append("WHERE DONG LIKE ? \n");

		return sql.toString();
	}

	private String getDetailSql(){
		StringBuffer sb = new StringBuffer(); 

		sb.append("\n     SELECT HG_NM,SUBSTR(BTHD,1,4) BTHD1,SUBSTR(BTHD,5,2) BTHD2,SUBSTR(BTHD,7,2) BTHD3,		");
		sb.append("\n     EMAIL_ID,HOM_DDD_NO,HOM_TEL_HNO,HOM_TEL_SNO,WKPL_DDD_NO,									");
		sb.append("\n     WKPL_TEL_HNO,WKPL_TEL_SNO,HP_DDD_NO,HP_TEL_HNO,HP_TEL_SNO,TB_RSLT_CLSS,RECP_DATE,			");
		sb.append("\n     SUBSTR(JUMIN_NO,1,6) JUMIN_NO1,SUBSTR(JUMIN_NO,7,7) JUMIN_NO2,RND_CD_CLSS,				");
		sb.append("\n     CONC_DATE,EMP_NO,JOIN_CHNL,		");
		sb.append("\n     DECODE(AUTH_CLSS,'1','ī�����','2','���հ���','3','����ó����') AUTH_CLSS_TXT,				");
		sb.append("\n     SUBSTR(CARD_NO,1,4) CARD_NO1,SUBSTR(CARD_NO,5,4) CARD_NO2,SUBSTR(CARD_NO,9,4) CARD_NO3,   ");
		sb.append("\n     SUBSTR(CARD_NO,13,4) CARD_NO4,															");
		sb.append("\n     DECODE(GOLF_CDHD_GRD_CLSS,'3','è�ǿ�','2','���','1','���') GOLF_CDHD_GRD_CLSS_TXT, ");
		sb.append("\n     ACPT_CHNL_CLSS,SCAL_LCAL_CLSS,ENG_NM,CO_NM,JOBTL_NM,SUBSTR(HOM_ZP,1,3) HOM_ZP1,			");
		sb.append("\n     SUBSTR(HOM_ZP,4,3) HOM_ZP2,AUTH_CLSS,GOLF_CDHD_GRD_CLSS,									");
		sb.append("\n     SUBSTR(HOM_DONG_BLW_ADDR,0,INSTR(HOM_DONG_BLW_ADDR,'|')-1) ZIPADDR,						");
		sb.append("\n     SUBSTR(HOM_DONG_BLW_ADDR,INSTR(HOM_DONG_BLW_ADDR,'|')+1) DETAILADDR, NW_OLD_ADDR_CLSS,    ");
		sb.append("\n     ACPT_PL_CHG_NM,  TB_RSLT_CLSS, REJ_RSON,  STTL_FAIL_RSON_CTNT,VALD_LIM	,   			");
		sb.append("\n     DECODE(VALD_LIM,NULL,'',SUBSTR(VALD_LIM,3,2)) VALD_LIM2, DECODE(VALD_LIM,NULL,'',SUBSTR(VALD_LIM,5,2)) VALD_LIM1,  ");
		sb.append("\n     DC_AMT,DISC_CLSS																			");

		sb.append("\n     FROM BCDBA.TBLUGTMCSTMR																	");
		sb.append("\n     WHERE JUMIN_NO = ?																		");
		sb.append("\n     AND RECP_DATE = ?																			");

		return sb.toString();
	}

	private String getCommonCodeSql(){
		StringBuffer sb = new StringBuffer();
		sb.append("\n     SELECT GOLF_CMMN_CODE, GOLF_CMMN_CODE_NM								  ");
		sb.append("\n       FROM BCDBA.TBGCMMNCODE   WHERE GOLF_CMMN_CLSS= ?  AND USE_YN ='Y' ");

		return sb.toString();
	}

	private String setUpdateSql(){
		StringBuffer sb = new StringBuffer();
		sb.append("\n     UPDATE BCDBA.TBLUGTMCSTMR 												");
		sb.append("\n     SET HG_NM = ?, BTHD = ?,	EMAIL_ID = ?, HOM_DDD_NO = ?,				");
		sb.append("\n         HOM_TEL_HNO = ?, HOM_TEL_SNO = ?, WKPL_DDD_NO = ?,				");
		sb.append("\n         WKPL_TEL_HNO = ?, WKPL_TEL_SNO = ?, HP_DDD_NO = ?,				");
		sb.append("\n         HP_TEL_HNO = ?, HP_TEL_SNO = ?, JUMIN_NO = ?, CONC_DATE = ?,		");
		sb.append("\n         EMP_NO = ?, JOIN_CHNL = ?, AUTH_CLSS = ?, CARD_NO = ?,			");
		sb.append("\n         SCAL_LCAL_CLSS = ?, ENG_NM = ?, CO_NM = ?, JOBTL_NM = ?,			");
		sb.append("\n         HOM_ZP = ?, HOM_DONG_BLW_ADDR = ?, ACPT_PL_CHG_NM = ?,			");
		sb.append("\n         GOLF_CDHD_GRD_CLSS = ? ,VALD_LIM = ?,								");
		sb.append("\n         DC_AMT = ? ,DISC_CLSS = ?, NW_OLD_ADDR_CLSS = ?					");
		sb.append("\n     WHERE JUMIN_NO = ? AND RECP_DATE = ?									");
		
		return sb.toString();
	}	

	private String setDeleteSql(){
		StringBuffer sb = new StringBuffer();

		sb.append("\n      DELETE FROM BCDBA.TBLUGTMCSTMR WHERE JUMIN_NO = ? AND RECP_DATE = ?    ");

		return sb.toString();
	}


	private String setGrdSql(){
		StringBuffer sb = new StringBuffer();

		sb.append("\n    SELECT CDHD_ID,CDHD_CTGO_SEQ_NO, NVL(SECE_YN,'N') SECE_YN,ACRG_CDHD_JONN_DATE,ACRG_CDHD_END_DATE FROM BCDBA.TBGGOLFCDHD WHERE JUMIN_NO = ?   ");

		return sb.toString();
	}

	private String getDeleteCDHDSql(){
		StringBuffer sb = new StringBuffer();

		sb.append("\n    DELETE FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID = ?   ");

		return sb.toString();
	}
	private String getDeleteGRDSql(){
		StringBuffer sb = new StringBuffer();

		sb.append("\n    DELETE FROM BCDBA.TBGGOLFCDHDGRDMGMT WHERE CDHD_ID = ?   ");

		return sb.toString();
	}

	


	/*�ڵ庯ȯ */
	public String getAnsiCode(String src){
		src = StrUtil.replace(src, "&", "&#38;");
		src = StrUtil.replace(src, "'", "&#39;");
		src = StrUtil.replace(src, "\"", "&#34;");
		src = StrUtil.replace(src, "<", "&#60;");
		src = StrUtil.replace(src, ">", "&#62;");
		src = StrUtil.replace(src, "\n", "<br>");
		src = StrUtil.replace(src, " ", "&nbsp;");
		return src;
	}
}



/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntBnstRegDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : �̺�Ʈ > ����ȸ > ���ó��
*   �������  : Golf
*   �ۼ�����  : 2010-02-20
************************** �����̷� ****************************************************************
* Ŀ��屸����	������		������	���泻��
* golfloung		20100524	������	6�� �̺�Ʈ
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event.alpensia;

import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	�̵������
 * @version	1.0
 ******************************************************************************/
public class GolfEvntAlpensiaRegDaoProc extends AbstractProc {
	
	public GolfEvntAlpensiaRegDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public int execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int  result =  0;
		int idx = 1;

		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			
			String golf_svc_aplc_clss	= data.getString("golf_svc_aplc_clss");	// ��ŷ ����
			String bkg_pe_nm 			= data.getString("bkg_pe_nm");			// ������ �̸�
			String trm_unt 				= data.getString("trm_unt");			// 1:1��2��, 2:2��3��
			String cdhd_id 				= data.getString("cdhd_id");			// ���̵�
			String cdhd_grd_seq_no 		= data.getString("cdhd_grd_seq_no");	// �ο���(1:1�� ��û, 2:��ü ��û)
			String hp_ddd_no 			= data.getString("hp_ddd_no");			// �ڵ���
			String hp_tel_hno 			= data.getString("hp_tel_hno");			// �ڵ���
			String hp_tel_sno 			= data.getString("hp_tel_sno");			// �ڵ���
			String hadc_num 			= data.getString("hadc_num");			// �ڵ�
			String rsvt_date 			= data.getString("rsvt_date");			// ������
			String rsv_time 			= data.getString("rsv_time");			// ����ð�
			String cus_rmrk 			= data.getString("cus_rmrk");			// ����û

			String jumin_no 			= data.getString("jumin_no");			// �ֹε�Ϲ�ȣ
			String ddd_no 				= data.getString("ddd_no");				// ��ȭ��ȣ
			String tel_hno 				= data.getString("tel_hno");			// ��ȭ��ȣ
			String tel_sno 				= data.getString("tel_sno");			// ��ȭ��ȣ
			String email 				= data.getString("email");				// �̸���
			int pnum 					= data.getInt("pnum");					// �ο���
			int tnum 					= data.getInt("tnum");					// ����
			String opt_yn 				= data.getString("opt_yn");				// ���ұ���-�ɼǻ�뱸���ڵ� Y:2��1�� N:4��1��
			String compn_opt_yn 		= data.getString("compn_opt_yn");		// ������ - ���ұ���-�ɼǻ�뱸���ڵ� Y:2��1�� N:4��1��
			String compn_bkg_pe_nm 		= data.getString("compn_bkg_pe_nm");	// ������ - �̸�
			String compn_hp_ddd_no 		= data.getString("compn_hp_ddd_no");	// ������ - ����ó
			String compn_hp_tel_hno 	= data.getString("compn_hp_tel_hno");	// ������ - ����ó
			String compn_hp_tel_sno 	= data.getString("compn_hp_tel_sno");	// ������ - ����ó
			
			int aplc_seq_no = 0;
			String sql = this.getEvtSeqQuery();  
			pstmt = conn.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				aplc_seq_no = rs.getInt("MAX_SEQ_NO");
			}

			// �̺�Ʈ ���
			idx = 1;
			sql = this.getEvtQuery();   
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt(idx++, 2);						// ����Ʈ�����ڵ�
			pstmt.setInt(idx++, aplc_seq_no);			// ��û�Ϸù�ȣ
			pstmt.setString(idx++, golf_svc_aplc_clss);	// �������񽺽�û�����ڵ�
			pstmt.setString(idx++, "����þ�");			// �������
			pstmt.setString(idx++, rsvt_date);			// ��������
			pstmt.setString(idx++, rsv_time);			// ����ð�
			pstmt.setString(idx++, bkg_pe_nm);			// �����ڼ���
			pstmt.setString(idx++, "");					// ȸ����ȸ�������ڵ�
			pstmt.setString(idx++, cdhd_id);			// ȸ�� ID

			pstmt.setString(idx++, jumin_no);			// �ֹε�Ϲ�ȣ
			pstmt.setString(idx++, cdhd_grd_seq_no);	// ȸ������Ϸù�ȣ = �ο�
			pstmt.setString(idx++, hp_ddd_no);			// �ڵ���
			pstmt.setString(idx++, hp_tel_hno);			// �ڵ���
			pstmt.setString(idx++, hp_tel_sno);			// �ڵ���
			pstmt.setString(idx++, email);				// �̸���
			pstmt.setString(idx++, hadc_num);			// �ڵ�ĸ��
			pstmt.setString(idx++, "R");				// �̺�Ʈ���౸���ڵ�
			pstmt.setString(idx++, "0");				// �������±����ڵ�
			pstmt.setInt(idx++, 0);						// �����ݾ�
			
			pstmt.setString(idx++, "");					// ����Ͻ�
			pstmt.setString(idx++, cus_rmrk);			// ����û����
			pstmt.setString(idx++, "");					// �����ڸ޸𳻿�
			pstmt.setString(idx++, ddd_no+"-"+tel_hno+"-"+tel_sno);	// ��� - ��ȭ��ȣ
			pstmt.setString(idx++, trm_unt);			// �Ⱓ�����ڵ�
			pstmt.setString(idx++, opt_yn);				// �ɼǻ�뱸���ڵ�   1: 2��1�� , 2: 4�� 1�� ��
			pstmt.setString(idx++, "");					// ����ȳ�
			result = pstmt.executeUpdate();
			
			if(golf_svc_aplc_clss.equals("8002") || golf_svc_aplc_clss.equals("8003")){

				String [] arr_compn_opt_yn 			= GolfUtil.split(compn_opt_yn, "||");
				String [] arr_compn_bkg_pe_nm 		= GolfUtil.split(compn_bkg_pe_nm, "||");
				String [] arr_compn_hp_ddd_no		= GolfUtil.split(compn_hp_ddd_no, "||");
				String [] arr_compn_hp_tel_hno		= GolfUtil.split(compn_hp_tel_hno, "||");
				String [] arr_compn_hp_tel_sno		= GolfUtil.split(compn_hp_tel_sno, "||");
				
				String str_compn_opt_yn = "";
				String str_compn_bkg_pe_nm = "";
				String str_compn_hp_ddd_no = "";
				String str_compn_hp_tel_hno = "";
				String str_compn_hp_tel_sno = "";
				int num = 0;
				
				for(int i=0; i<tnum; i++){

					if(arr_compn_opt_yn.length>i){
						str_compn_opt_yn = arr_compn_opt_yn[i];
					}else{
						str_compn_opt_yn = "";
					}
					
					for(int j=0; j<pnum; j++){

						if(arr_compn_bkg_pe_nm.length>num){
							str_compn_bkg_pe_nm = arr_compn_bkg_pe_nm[num];
						}else{
							str_compn_bkg_pe_nm = "";
						}
						if(arr_compn_hp_ddd_no.length>num){
							str_compn_hp_ddd_no = arr_compn_hp_ddd_no[num];
						}else{
							str_compn_hp_ddd_no = "";
						}
						if(arr_compn_hp_tel_hno.length>num){
							str_compn_hp_tel_hno = arr_compn_hp_tel_hno[num];
						}else{
							str_compn_hp_tel_hno = "";
						}
						if(arr_compn_hp_tel_sno.length>num){
							str_compn_hp_tel_sno = arr_compn_hp_tel_sno[num];
						}else{
							str_compn_hp_tel_sno = "";
						}
								
						// ������ ���
						idx = 1;
						sql = this.getEvtCompnQuery();   
						pstmt = conn.prepareStatement(sql.toString());
						pstmt.setInt(idx++, 2);						// ����Ʈ�����ڵ�
						pstmt.setInt(idx++, aplc_seq_no);			// ��û�Ϸù�ȣ
						pstmt.setInt(idx++, num+1);					// SEQ_NO ������ �ο�
						pstmt.setString(idx++, golf_svc_aplc_clss);	// �������񽺽�û�����ڵ�
						pstmt.setString(idx++, "2");				// ��û�ڱ����ڵ� 1:��û��, 2:������
						pstmt.setString(idx++, str_compn_bkg_pe_nm);// �����ڼ���
						pstmt.setString(idx++, "");					// ȸ����ȸ�������ڵ�
						pstmt.setString(idx++, "");					// ȸ�� ID
						pstmt.setString(idx++, "");					// �ֹε�Ϲ�ȣ
						pstmt.setString(idx++, cdhd_grd_seq_no);	// ȸ������Ϸù�ȣ = �ο�

						pstmt.setString(idx++, str_compn_hp_ddd_no);// �ڵ���
						pstmt.setString(idx++, str_compn_hp_tel_hno);// �ڵ���
						pstmt.setString(idx++, str_compn_hp_tel_sno);// �ڵ���
						pstmt.setString(idx++, "");					// �̸���
						pstmt.setString(idx++, "");			// �ڵ�ĸ��
						pstmt.setString(idx++, "0");				// �������±����ڵ�
						pstmt.setInt(idx++, 0);						// �����ݾ�
						pstmt.setString(idx++, "N");				// ��ҿ���
						pstmt.setString(idx++, "");					// ����Ͻ�
						pstmt.setString(idx++, "");					// ��� - ��ȭ��ȣ
						
						pstmt.setString(idx++, str_compn_opt_yn);	// �ɼǻ�뱸���ڵ�   1: 2��1�� , 2: 4�� 1�� ��
						pstmt.setInt(idx++, i+1);					// ����
						result = pstmt.executeUpdate();	
						num++;
					}
				}
			}
			
			

			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
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


	public int execute_jumin(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result =  0;
		int idx = 0;

		try {
			conn = context.getDbConnection("default", null);
			
			String jumin_no 	= data.getString("jumin_no");
			String green_nm 	= data.getString("green_nm");
			String rsvt_date 	= data.getString("rsvt_date");
			String rsv_time 	= data.getString("rsv_time");
			
			String sql = this.getCntJuminQuery();   
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, jumin_no);
			pstmt.setString(++idx, green_nm);
			pstmt.setString(++idx, rsvt_date);
			pstmt.setString(++idx, rsv_time);

			rs = pstmt.executeQuery();

			if ( rs != null ) {
				rs.next();
				result = rs.getInt("CNT");
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

	public int execute_hp(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int  result =  0;

		try {
			conn = context.getDbConnection("default", null);

			String hp_ddd_no	= data.getString("hp_ddd_no");
			String hp_tel_hno	= data.getString("hp_tel_hno");
			String hp_tel_sno	= data.getString("hp_tel_sno");
			String green_nm 	= data.getString("green_nm");
			String rsvt_date 	= data.getString("rsvt_date");
			String rsv_time 	= data.getString("rsv_time");
			
			String sql = this.getCntHpQuery();   
			pstmt = conn.prepareStatement(sql.toString());
			int idx = 0;
			pstmt.setString(++idx, hp_ddd_no);
			pstmt.setString(++idx, hp_tel_hno);
			pstmt.setString(++idx, hp_tel_sno);
			pstmt.setString(++idx, green_nm);
			pstmt.setString(++idx, rsvt_date);
			pstmt.setString(++idx, rsv_time);

			rs = pstmt.executeQuery();

			if ( rs != null ) {
				rs.next();
				result = rs.getInt("CNT");
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
    * �̺�Ʈ max_seq ��������
    ************************************************************************ */
    private String getEvtSeqQuery(){
        StringBuffer sql = new StringBuffer();
        
		sql.append("\n SELECT NVL(MAX(APLC_SEQ_NO),0)+1 MAX_SEQ_NO FROM BCDBA.TBGGOLFEVNTAPLC	\n");
		
		return sql.toString();
    }

	/** ***********************************************************************
    * �̺�Ʈ ��� 
    ************************************************************************ */
    private String getEvtQuery(){
        StringBuffer sql = new StringBuffer();	
		sql.append("\n	INSERT INTO BCDBA.TBGGOLFEVNTAPLC (	");
		sql.append("\n		SITE_CLSS, APLC_SEQ_NO, GOLF_SVC_APLC_CLSS, GREEN_NM, APLC_ATON, RSVT_DATE, RSV_TIME, BKG_PE_NM, CDHD_NON_CDHD_CLSS, CDHD_ID	");
		sql.append("\n	    , JUMIN_NO, CDHD_GRD_SEQ_NO, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, EMAIL, HADC_NUM, EVNT_PGRS_CLSS, STTL_STAT_CLSS, STTL_AMT	");
		sql.append("\n	    , CNCL_ATON, CUS_RMRK, MGR_MEMO, NOTE, TRM_UNT, OPT_YN, COMMON_RMRK	");
		sql.append("\n	) VALUES (	");
		sql.append("\n	    ?, ?, ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), ?, ?, ?, ?, ?	");
		sql.append("\n	    , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?	");
		sql.append("\n	    , ?, ?, ?, ?, ?, ?, ?	");
		sql.append("\n	)	");

		return sql.toString();
    }

	/** ***********************************************************************
    * ������ ���
    ************************************************************************ */
    private String getEvtCompnQuery(){
        StringBuffer sql = new StringBuffer();

		sql.append("\n	INSERT INTO BCDBA.TBGGOLFEVNTAPLCPE (	");
		sql.append("\n	    SITE_CLSS, APLC_SEQ_NO, SEQ_NO, GOLF_SVC_APLC_CLSS, APLC_PE_CLSS, BKG_PE_NM, CDHD_NON_CDHD_CLSS, CDHD_ID, JUMIN_NO, CDHD_GRD_SEQ_NO	");
		sql.append("\n	    , HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, EMAIL, HADC_NUM, STTL_STAT_CLSS, STTL_AMT, CNCL_YN, CNCL_ATON, NOTE	");
		sql.append("\n	    , OPT_YN, TEAM_NUM	");
		sql.append("\n	) VALUES (	");
		sql.append("\n	    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?	");
		sql.append("\n	    , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?	");
		sql.append("\n	    , ?, ?	");
		sql.append("\n	)	");
		
		return sql.toString();
    }

	/** ***********************************************************************
    * ���� �ֹε�� ��ȣ�� ��ϵ� ��û������ �ִ��� �˾ƺ���.
    ************************************************************************ */
    private String getCntJuminQuery(){
        StringBuffer sql = new StringBuffer();

		sql.append("\n	SELECT COUNT(*) CNT FROM BCDBA.TBGGOLFEVNTAPLC 	\n");
		sql.append("\t	WHERE JUMIN_NO=? AND GREEN_NM=? AND RSVT_DATE=? AND RSV_TIME=?	\n");
		
		return sql.toString();
    }

	/** ***********************************************************************
    * ���� �ڵ��� ��ȣ�� ��ϵ� ��û������ �ִ��� �˾ƺ���.
    ************************************************************************ */
    private String getCntHpQuery(){
        StringBuffer sql = new StringBuffer();

		sql.append("\n	SELECT COUNT(*) CNT FROM BCDBA.TBGGOLFEVNTAPLC	\n");
		sql.append("\t	WHERE HP_DDD_NO=? AND HP_TEL_HNO=? AND HP_TEL_SNO=? AND GREEN_NM=? AND RSVT_DATE=? AND RSV_TIME=?	\n");
		
		return sql.toString();
    }
}

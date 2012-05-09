/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmEvntBnstUpdDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ > �̺�Ʈ > ����ȸ > ���� ó��
*   �������  : golf
*   �ۼ�����  : 2010-03-24
************************** �����̷� ****************************************************************
* Ŀ��屸����	������		������	���泻��
* golfloung		20100524	������	6�� �̺�Ʈ
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.event.benest;

import java.io.Reader;
import java.io.Writer;
import java.io.CharArrayReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.*;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.msg.MsgEtt;

import oracle.jdbc.driver.OracleResultSet; 
import oracle.sql.CLOB;

/****************************************************************************** 
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfAdmEvntBnstUpdDaoProc extends AbstractProc {

	public static final String TITLE = "������ > �̺�Ʈ > ����ȸ > ���� ó��";

	/** *****************************************************************
	 * GolfAdmOrdUpdDaoProc ���μ��� ������
	 * @param N/A 
	 ***************************************************************** */
	public GolfAdmEvntBnstUpdDaoProc() {} 

	/** *****************************************************************
	 * execute_grd ��޺��� => ������ ���, �ݾ�, ������� �ݾ�
	 ***************************************************************** */
	public int execute_grd(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String aplc_seq_no = data.getString("aplc_seq_no");	
			String seq_no = data.getString("seq_no");
			String cdhd_grd_seq_no = data.getString("cdhd_grd_seq_no");
			String email = "";
			String sttl_amt = "";

			if(cdhd_grd_seq_no.equals("1")){
				sttl_amt = "150000";
			}else if(cdhd_grd_seq_no.equals("7")){
				sttl_amt = "160000";
			}else if(cdhd_grd_seq_no.equals("2")){
				sttl_amt = "160000";
			}else if(cdhd_grd_seq_no.equals("3")){ 
				sttl_amt = "160000";
			}else if(cdhd_grd_seq_no.equals("9")){
				sttl_amt = "150000";
			}else if(cdhd_grd_seq_no.equals("4")){ 
				sttl_amt = "180000";
			}
            /*****************************************************************************/
			
			sql = this.getGrdCompnUpdQuery();	// ������ ����
			pstmt = conn.prepareStatement(sql); 

			int idx = 0;
			pstmt.setString(++idx, cdhd_grd_seq_no );
			pstmt.setString(++idx, sttl_amt );
			pstmt.setString(++idx, email );
			pstmt.setString(++idx, aplc_seq_no );
			pstmt.setString(++idx, seq_no );
			result = pstmt.executeUpdate();
			
			if(result>0){
				sql = this.getGrdSttlUpdQuery();// ������� �ݾ� ����
				pstmt = conn.prepareStatement(sql);
	
				idx = 0;
				pstmt.setString(++idx, aplc_seq_no );
				pstmt.setString(++idx, aplc_seq_no );
				result = pstmt.executeUpdate();
			}

			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
						
		} catch(Exception e) {
			try	{
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			//e.printStackTrace(); 
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}

	/** *****************************************************************
	 * execute_sttl �������� ����
	 ***************************************************************** */
	public int execute_sttl(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null; 
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String aplc_seq_no = data.getString("aplc_seq_no");	
			String sttl_stat_clss = data.getString("sttl_stat_clss");
            /*****************************************************************************/
			
			sql = this.getSttlUpdQuery();	// ������ ����
			pstmt = conn.prepareStatement(sql);

			int idx = 0;
			pstmt.setString(++idx, sttl_stat_clss );
			pstmt.setString(++idx, aplc_seq_no );
			result = pstmt.executeUpdate();
			
			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
						
		} catch(Exception e) {
			try	{
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			//e.printStackTrace();
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}


	/** *****************************************************************
	 * execute_sttl �������� ����
	 ***************************************************************** */
	public int execute_evnt(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String aplc_seq_no = data.getString("aplc_seq_no");	
			String evnt_pgrs_clss = data.getString("evnt_pgrs_clss");
            /*****************************************************************************/
			
			sql = this.getEvntUpdQuery();	// ������ ����
			pstmt = conn.prepareStatement(sql);

			int idx = 0;
			pstmt.setString(++idx, evnt_pgrs_clss );
			pstmt.setString(++idx, aplc_seq_no );
			result = pstmt.executeUpdate();
			
			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
						
		} catch(Exception e) {
			try	{
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			//e.printStackTrace();
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}

	/** *****************************************************************
	 * execute_all ��ü���� ����
	 ***************************************************************** */
	public int execute_all(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs2 = null;
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String aplc_seq_no 		= data.getString("aplc_seq_no");
			String green_nm 		= data.getString("green_nm");
			String note 			= data.getString("note");
			String months 			= data.getString("months");
			String sttl_stat_clss 	= data.getString("sttl_stat_clss");
			String evnt_pgrs_clss 	= data.getString("evnt_pgrs_clss");
			String mgr_memo 		= data.getString("mgr_memo");
			String rsvt_date 		= data.getString("rsvt_date");
			String rsv_time 		= data.getString("rsv_time");
			
			int cnt 				= data.getInt("cnt");
			
			String trm_unt 		= data.getString("trm_unt");				//����ȸ seq
			String cdhd_grd_seq_no	= "";
			String email			= "";
			String sttl_amt 		= "";
			String email_amt		= "";
			int int_sttl_amt		= 0;
			int sttl_amt_all		= 0;
			int idx = 0;
			String del_yn			= "";
			String seq_no			= "";
			String bkg_pe_nm		= "";
			String hp_ddd_no		= "";
			String hp_tel_hno		= "";
			String hp_tel_sno		= "";
			String type_cal			= "";	// �ݾ� �հ豸��
            /*****************************************************************************/
			
			String input_cdhd_grd_seq_no="";
			String input_sttl_amt="";
			// 1. ������ �� ��ŭ ���� => ���, �ݾ�, ��ȭ��ȣ
			for(int i=1; i<=20; i++){

				if(!GolfUtil.empty(data.getString("seq_no"+i))){

					if(!GolfUtil.empty(data.getString("del_yn"+i))){
						del_yn = data.getString("del_yn"+i);
					}else{
						del_yn = "";
					}
					debug("del_yn : " + del_yn);
					seq_no = data.getString("seq_no"+i);
				
					if(del_yn.equals("Y")){
						
						
						sql = this.getGrdCompnDelQuery();	// ������ ����
						pstmt = conn.prepareStatement(sql);
						idx = 0;
						pstmt.setString(++idx, aplc_seq_no );
						pstmt.setString(++idx, seq_no );
	
					}else{
						
						if(!GolfUtil.empty(data.getString("cdhd_grd_seq_no"+i))){
							cdhd_grd_seq_no = data.getString("cdhd_grd_seq_no"+i);
							
							/*debug("@@@@cdhd_grd_seq_no :" + cdhd_grd_seq_no);
							//1,0,4
							String sql2 = this.getPayCost();  
							pstmt = conn.prepareStatement(sql2.toString());
							pstmt.setString(0, trm_unt );				//seq
							rs2 = pstmt.executeQuery();
							if (rs2.next()) {				//CPO_AMT, ACRG_CDHD_AMT, FREE_CDHD_AMT
								if(cdhd_grd_seq_no.equals("1")){
									input_sttl_amt = rs2.getString("CPO_AMT");
								}else if(cdhd_grd_seq_no.equals("0")){
									input_sttl_amt = rs2.getString("ACRG_CDHD_AMT");
								}else if(cdhd_grd_seq_no.equals("4")){
									input_sttl_amt = rs2.getString("FREE_CDHD_AMT");
								}
								debug("@@@@input_sttl_amt :" + input_sttl_amt);
							}*/
						}else{
							cdhd_grd_seq_no = "";
						}
						if(!GolfUtil.empty(data.getString("email"+i))){
							email = data.getString("email"+i);
						}else{
							email = "";
						}
						if(!GolfUtil.empty(data.getString("bkg_pe_nm"+i))){
							bkg_pe_nm = data.getString("bkg_pe_nm"+i);
						}else{
							bkg_pe_nm = "";
						}
						if(!GolfUtil.empty(data.getString("hp_ddd_no"+i))){
							hp_ddd_no = data.getString("hp_ddd_no"+i);
						}else{
							hp_ddd_no = "";
						}
						if(!GolfUtil.empty(data.getString("hp_tel_hno"+i))){
							hp_tel_hno = data.getString("hp_tel_hno"+i);
						}else{
							hp_tel_hno = "";
						}
						if(!GolfUtil.empty(data.getString("hp_tel_sno"+i))){
							hp_tel_sno = data.getString("hp_tel_sno"+i);
						}else{
							hp_tel_sno = "";
						}
												
						if(months.equals("05")){
							if(green_nm.equals("���򺣳׽�Ʈ ����Ŭ��")){
								if(cdhd_grd_seq_no.equals("1") || cdhd_grd_seq_no.equals("9")){
									sttl_amt = "160000";
								}else if(cdhd_grd_seq_no.equals("0")){
									sttl_amt = "170000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "180000";
								}else{
									sttl_amt = "0";
								}
							}else if(green_nm.equals("�Ŷ� ����Ŭ��")){
								if(cdhd_grd_seq_no.equals("1") || cdhd_grd_seq_no.equals("9")){
									sttl_amt = "125000";
								}else if(cdhd_grd_seq_no.equals("0")){
									sttl_amt = "135000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "145000";
								}else{
									sttl_amt = "0";
								}
							}
						}else if(months.equals("06")){

							if(note.equals("A")){
								if(cdhd_grd_seq_no.equals("99")){
									sttl_amt = "125000";
								}else{
									sttl_amt = "0";
								}
							}else if(note.equals("B")){
								if(cdhd_grd_seq_no.equals("1") || cdhd_grd_seq_no.equals("9")){
									sttl_amt = "125000";
								}else if(cdhd_grd_seq_no.equals("0")){
									sttl_amt = "135000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "145000";
								}else{
									sttl_amt = "0";
								}
							}else if(note.equals("C")){
								if(cdhd_grd_seq_no.equals("1") || cdhd_grd_seq_no.equals("9")){
									sttl_amt = "155000";
								}else if(cdhd_grd_seq_no.equals("0")){
									sttl_amt = "165000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "175000";
								}else{
									sttl_amt = "0";
								}
							}else if(note.equals("D")){
								if(cdhd_grd_seq_no.equals("1")){
									sttl_amt = "150000";
								}else if(cdhd_grd_seq_no.equals("2")){
									sttl_amt = "190000";
								}else if(cdhd_grd_seq_no.equals("3")){
									sttl_amt = "350000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "420000";
								}else if(cdhd_grd_seq_no.equals("5")){
									sttl_amt = "500000";
								}else if(cdhd_grd_seq_no.equals("6")){
									sttl_amt = "530000";
								}else if(cdhd_grd_seq_no.equals("7")){
									sttl_amt = "600000";
								}else if(cdhd_grd_seq_no.equals("8")){
									sttl_amt = "690000";
								}else{
									sttl_amt = "0";
								}

								if(email.equals("1")){
									email_amt = "40000";
									type_cal = "plus";
								}else if(email.equals("2")){
									email_amt = "50000";
									type_cal = "plus";
								}else if(email.equals("3")){
									email_amt = "80000";
									type_cal = "plus";
								}else if(email.equals("4")){
									email_amt = "100000";
									type_cal = "plus";
								}else if(email.equals("5")){
									email_amt = "10000";
									type_cal = "minus";
								}else if(email.equals("6")){
									email_amt = "20000";
									type_cal = "minus";
								}else if(email.equals("7")){
									email_amt = "50000";
									type_cal = "minus";
								}else{
									email_amt = "0";
								}
								
								//debug("sttl_amt : " + sttl_amt + " / email_amt : " + email_amt);
								
								if(type_cal.equals("minus")){
									int_sttl_amt = Integer.parseInt(sttl_amt)-Integer.parseInt(email_amt);
								}else{
									int_sttl_amt = Integer.parseInt(sttl_amt)+Integer.parseInt(email_amt);
								}
								sttl_amt = int_sttl_amt+"";
							}

						}else if(months.equals("07")){
							if(note.equals("A")){
								if(cdhd_grd_seq_no.equals("9")){
									sttl_amt = "120000";
								}else if(cdhd_grd_seq_no.equals("0")){
									sttl_amt = "110000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "130000";
								}else{
									sttl_amt = "0";
								}
							}
						}else if(months.equals("08")){
							if(note.equals("A")){
								if(cdhd_grd_seq_no.equals("9")){
									sttl_amt = "120000";
								}else if(cdhd_grd_seq_no.equals("0")){
									sttl_amt = "110000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "130000";
								}else{
									sttl_amt = "0";
								}
							}else if(note.equals("B")){
								if(cdhd_grd_seq_no.equals("9")){
									sttl_amt = "110000";
								}else if(cdhd_grd_seq_no.equals("0")){
									sttl_amt = "100000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "120000";
								}else{
									sttl_amt = "0";
								}
							}else if(note.equals("C")){
								if(cdhd_grd_seq_no.equals("1")){
									sttl_amt = "390000";
								}else if(cdhd_grd_seq_no.equals("9") || cdhd_grd_seq_no.equals("0")){
									sttl_amt = "410000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "420000";
								}else{
									sttl_amt = "0";
								}
							}
						}else if(months.equals("09")){
							if(note.equals("A")){    //�Ŷ���� Ŭ��(9��)
								if(cdhd_grd_seq_no.equals("0")){
									sttl_amt = "125000";
								}else if(cdhd_grd_seq_no.equals("9")){
									sttl_amt = "135000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "145000";
								}else{
									sttl_amt = "0";
								}
							}else if(note.equals("B")){	//�̺쵥��(9��) 
								if(cdhd_grd_seq_no.equals("0")){
									sttl_amt = "100000";
								}else if(cdhd_grd_seq_no.equals("9")){
									sttl_amt = "110000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "120000";
								}else{
									sttl_amt = "0";
								}
							}else if(note.equals("C")){ //��ũ�븮(9��)
								if(cdhd_grd_seq_no.equals("1")){
									sttl_amt = "390000";
								}else if(cdhd_grd_seq_no.equals("9") || cdhd_grd_seq_no.equals("0")){
									sttl_amt = "410000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "420000";
								}else{
									sttl_amt = "0";
								}
							}else if(note.equals("D")){ //��ũ�븮(9�� ����)
								if(cdhd_grd_seq_no.equals("1")){
									sttl_amt = "197500";
								}else if(cdhd_grd_seq_no.equals("9") || cdhd_grd_seq_no.equals("0")){
									sttl_amt = "197500";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "197500";
								}else{
									sttl_amt = "0";
								}
							}
						}else if(months.equals("10")){
							 if(note.equals("C")){ //��ũ�븮(10��)
								if(cdhd_grd_seq_no.equals("1")){
									sttl_amt = "390000";
								}else if(cdhd_grd_seq_no.equals("9") || cdhd_grd_seq_no.equals("0")){
									sttl_amt = "410000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "420000";
								}else{
									sttl_amt = "0";
								}
							}else if(note.equals("D")){ //��ũ�븮(10�� ����)
								if(cdhd_grd_seq_no.equals("1")){
									sttl_amt = "197500";
								}else if(cdhd_grd_seq_no.equals("9") || cdhd_grd_seq_no.equals("0")){
									sttl_amt = "197500";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "197500";
								}else{
									sttl_amt = "0";
								}
							}else if(note.equals("A")){    //�Ŷ���� Ŭ��(10��)
								if(cdhd_grd_seq_no.equals("0")){
									sttl_amt = "125000";
								}else if(cdhd_grd_seq_no.equals("9")){
									sttl_amt = "135000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "145000";
								}else{
									sttl_amt = "0";
								}
							}else if(note.equals("B")){	//�̺쵥��(10��) 
								if(cdhd_grd_seq_no.equals("0")){
									sttl_amt = "100000";
								}else if(cdhd_grd_seq_no.equals("9")){
									sttl_amt = "110000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "120000";
								}else{
									sttl_amt = "0";
								}
							}
						}
						
						sttl_amt_all += Integer.parseInt(sttl_amt);
			            /*****************************************************************************/
						
						if(del_yn.equals("N")){
								
							sql = this.getEvtCompnQuery();   // ������ ���
							pstmt = conn.prepareStatement(sql.toString());
							idx = 1;	// 13
							pstmt.setString(idx++, aplc_seq_no);
							pstmt.setString(idx++, seq_no);
							pstmt.setString(idx++, "2");
							pstmt.setString(idx++, bkg_pe_nm);
							pstmt.setString(idx++, "");
							pstmt.setString(idx++, "");
							pstmt.setString(idx++, "");
							pstmt.setString(idx++, cdhd_grd_seq_no);	//
							pstmt.setString(idx++, hp_ddd_no);
							pstmt.setString(idx++, hp_tel_hno);
							pstmt.setString(idx++, hp_tel_sno);
							pstmt.setString(idx++, email);
							pstmt.setString(idx++, "");
							pstmt.setString(idx++, sttl_amt);
							
						}else{
							
							sql = this.getGrdCompnUpdQuery();	// ������ ����
							pstmt = conn.prepareStatement(sql);
							idx = 0;
							pstmt.setString(++idx, cdhd_grd_seq_no );
							pstmt.setString(++idx, sttl_amt );
							pstmt.setString(++idx, hp_ddd_no );
							pstmt.setString(++idx, hp_tel_hno );
							pstmt.setString(++idx, hp_tel_sno );
							pstmt.setString(++idx, bkg_pe_nm );
							pstmt.setString(++idx, email );
							pstmt.setString(++idx, aplc_seq_no );
							pstmt.setString(++idx, seq_no );
							
						}
						
					}
					result += pstmt.executeUpdate();
				}
			}

			debug("sttl_amt_all : " + sttl_amt_all + " / cdhd_grd_seq_no : " + cdhd_grd_seq_no + " / months : " + months + " / note : " + note);
			
			
			// 2. �̺�Ʈ ���� => �ݾ�, ��������, ������ϻ���, ��¥
			if(result>0){
				sql = this.getEvtUpdQuery();// ������� �ݾ� ����
				pstmt = conn.prepareStatement(sql);
	
				idx = 0;
				pstmt.setString(++idx, aplc_seq_no );
				pstmt.setString(++idx, sttl_stat_clss );
				pstmt.setString(++idx, evnt_pgrs_clss );
				pstmt.setString(++idx, mgr_memo );
				pstmt.setString(++idx, rsvt_date );
				pstmt.setString(++idx, rsv_time );
				pstmt.setString(++idx, aplc_seq_no );
				result += pstmt.executeUpdate();
			}
			
			if(result > 1) {
				conn.commit();
			} else {
				conn.rollback();
			}
						
		} catch(Exception e) {
			try	{
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			//e.printStackTrace();
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
					
	
	/** *****************************************************************
	 * execute_all ��ü���� ����
	 ***************************************************************** */
	public int execute_update(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs2 = null;
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String aplc_seq_no 		= data.getString("aplc_seq_no");
			String rsvt_date 		= data.getString("rsvt_date");
			String rsv_time 		= data.getString("rsv_time");
			String sttl_stat_clss 	= data.getString("sttl_stat_clss");
			String evnt_pgrs_clss 	= data.getString("evnt_pgrs_clss");
			String mgr_memo 		= data.getString("mgr_memo");
			String trm_unt 		= data.getString("trm_unt");				//����ȸ seq
			
			int cnt 				= data.getInt("cnt");
			String cdhd_grd_seq_no	= "";
			String email			= "";
			String sttl_amt 		= "";
			String email_amt		= "";
			int int_sttl_amt		= 0;
			int sttl_amt_all		= 0;
			int idx = 0;
			String del_yn			= "";
			String seq_no			= "";
			String bkg_pe_nm		= "";
			String hp_ddd_no		= "";
			String hp_tel_hno		= "";
			String hp_tel_sno		= "";
			String type_cal			= "";	// �ݾ� �հ豸��
            /*****************************************************************************/
			debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			
			String input_sttl_amt="";
			// 1. ������ �� ��ŭ ���� => ���, �ݾ�, ��ȭ��ȣ
			for(int i=1; i<=20; i++){

				if(!GolfUtil.empty(data.getString("seq_no"+i))){

					if(!GolfUtil.empty(data.getString("del_yn"+i))){
						del_yn = data.getString("del_yn"+i);
					}else{
						del_yn = "";
					}
					debug("del_yn : " + del_yn);
					seq_no = data.getString("seq_no"+i);
				
					if(del_yn.equals("Y")){
						sql = this.getGrdCompnDelQuery();	// ������ ����
						pstmt = conn.prepareStatement(sql);
						idx = 0;
						pstmt.setString(++idx, aplc_seq_no );
						pstmt.setString(++idx, seq_no );
	
					}else{
						if(!GolfUtil.empty(data.getString("cdhd_grd_seq_no"+i))){
							cdhd_grd_seq_no = data.getString("cdhd_grd_seq_no"+i);
							
							debug("@@@@cdhd_grd_seq_no :" + cdhd_grd_seq_no);
							//1,0,4
							sql = this.getPayCost();  
							pstmt = conn.prepareStatement(sql.toString());
							pstmt.setString(1, trm_unt );				//seq
							rs2 = pstmt.executeQuery();
							if (rs2.next()) {				//CPO_AMT, ACRG_CDHD_AMT, FREE_CDHD_AMT
								if(cdhd_grd_seq_no.equals("1")){
									input_sttl_amt = rs2.getString("CPO_AMT");
								}else if(cdhd_grd_seq_no.equals("0")){
									input_sttl_amt = rs2.getString("ACRG_CDHD_AMT");
								}else if(cdhd_grd_seq_no.equals("4")){
									input_sttl_amt = rs2.getString("FREE_CDHD_AMT");
								}
								debug("@@@@input_sttl_amt :" + input_sttl_amt);
							}
						}else{
							cdhd_grd_seq_no = "";
						}
						if(!GolfUtil.empty(data.getString("bkg_pe_nm"+i))){
							bkg_pe_nm = data.getString("bkg_pe_nm"+i);
						}else{
							bkg_pe_nm = "";
						}
						if(!GolfUtil.empty(data.getString("hp_ddd_no"+i))){
							hp_ddd_no = data.getString("hp_ddd_no"+i);
						}else{
							hp_ddd_no = "";
						}
						if(!GolfUtil.empty(data.getString("hp_tel_hno"+i))){
							hp_tel_hno = data.getString("hp_tel_hno"+i);
						}else{
							hp_tel_hno = "";
						}
						if(!GolfUtil.empty(data.getString("hp_tel_sno"+i))){
							hp_tel_sno = data.getString("hp_tel_sno"+i);
						}else{
							hp_tel_sno = "";
						}
						
			            /*****************************************************************************/
						
						if(del_yn.equals("N")){
							debug("@@@@@@@@@@@2del_yn = N");
							sql = this.getEvtCompnQuery();   // ������ ���
							pstmt = conn.prepareStatement(sql.toString());
							idx = 1;	// 13
							pstmt.setString(idx++, aplc_seq_no);
							pstmt.setString(idx++, seq_no);
							pstmt.setString(idx++, "2");
							pstmt.setString(idx++, bkg_pe_nm);
							pstmt.setString(idx++, "");
							pstmt.setString(idx++, "");
							pstmt.setString(idx++, "");
							pstmt.setString(idx++, cdhd_grd_seq_no);
							pstmt.setString(idx++, hp_ddd_no);
							pstmt.setString(idx++, hp_tel_hno);
							pstmt.setString(idx++, hp_tel_sno);
							pstmt.setString(idx++, email);
							pstmt.setString(idx++, "");
							pstmt.setString(idx++, input_sttl_amt);
							
						}else{
							debug("@@@@@@@@@@@2del_yn = Y");
							sql = this.getGrdCompnUpdQuery();	// ������ ����
							pstmt = conn.prepareStatement(sql);
							idx = 0;
							pstmt.setString(++idx, cdhd_grd_seq_no );
							pstmt.setString(++idx, input_sttl_amt );
							pstmt.setString(++idx, hp_ddd_no );
							pstmt.setString(++idx, hp_tel_hno );
							pstmt.setString(++idx, hp_tel_sno );
							pstmt.setString(++idx, bkg_pe_nm );
							pstmt.setString(++idx, email );
							pstmt.setString(++idx, aplc_seq_no );
							pstmt.setString(++idx, seq_no );
							
						}
						
					}
					result += pstmt.executeUpdate();
				}
			}

			
			
			// 2. �̺�Ʈ ���� => �ݾ�, ��������, ������ϻ���, ��¥
			if(result>0){
				sql = this.getEvtUpdQuery();// ������� �ݾ� ����
				pstmt = conn.prepareStatement(sql);
	
				idx = 0;
				pstmt.setString(++idx, aplc_seq_no );
				pstmt.setString(++idx, sttl_stat_clss );
				pstmt.setString(++idx, evnt_pgrs_clss );
				pstmt.setString(++idx, mgr_memo );
				pstmt.setString(++idx, rsvt_date );
				pstmt.setString(++idx, rsv_time );
				pstmt.setString(++idx, aplc_seq_no );
				result += pstmt.executeUpdate();
			}
			
			if(result > 1) {
				conn.commit();
			} else {
				conn.rollback();
			}
						
		} catch(Exception e) {
			try	{
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			//e.printStackTrace();
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}

	/** *****************************************************************
	 * execute_del ���� ��� 
	 ***************************************************************** */
	public int execute_del(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {
		int result = 0;
		int result_upd = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String aplc_seq_no 		= data.getString("aplc_seq_no");
			

            /*****************************************************************************/
    		// ��ȸ�� ȯ��ó�� ���ش�.
    		boolean payCancelResult = false;
    		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// ��������
    		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");

			// ��������
			String odr_no = "";		// �ֹ���ȣ
			String sttl_amt = "";	// �����ݾ�
			String mer_no = "";		// ��������ȣ
			String card_no = "";	// ī���ȣ
			String vald_date = "";	// ��ȿ����
			String ins_mcnt = "";	// �Һΰ�����
			String auth_no = "";	// ���ι�ȣ
			String ip = request.getRemoteAddr();  // �ܸ���ȣ(IP, '.'����)
			String sttl_mthd_clss = "";	// ������������ڵ�
			String sttl_gds_clss = "";	// ������ǰ�����ڵ�
			String cdhd_id = "";
			

			// ���� ���� ��ȸ
			sql = this.setPayBackListQuery(); 
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, aplc_seq_no );
            rs = pstmt.executeQuery();	
            
            while(rs.next()){
            	payCancelResult = false;

            	odr_no = rs.getString("ODR_NO");
				sttl_amt = rs.getString("STTL_AMT");
				mer_no = rs.getString("MER_NO");
				card_no = rs.getString("CARD_NO");
				vald_date = rs.getString("VALD_DATE");
				ins_mcnt = rs.getString("INS_MCNT");
				auth_no = rs.getString("AUTH_NO");
				sttl_mthd_clss = rs.getString("STTL_MTHD_CLSS");
				sttl_gds_clss = rs.getString("STTL_GDS_CLSS");
				cdhd_id = rs.getString("CDHD_ID");

				// ��ī�� �Ǵ� ���հ����� ���
				if("0001".equals(sttl_mthd_clss) || "0002".equals(sttl_mthd_clss)) {
				
					payEtt.setMerMgmtNo(mer_no);		// ������ ��ȣ
					payEtt.setCardNo(card_no);			// ispī���ȣ
					payEtt.setValid(vald_date);			// ���� ����
					payEtt.setAmount(sttl_amt);			// �����ݾ�	
					payEtt.setInsTerm(ins_mcnt);		// �Һΰ�����
					payEtt.setRemoteAddr(ip);			// ip �ּ�
					payEtt.setUseNo(auth_no);			// ���ι�ȣ

					String host_ip = java.net.InetAddress.getLocalHost().getHostAddress();
					if( "211.181.255.40".equals(host_ip)) {
						payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// ������� ȣ��
					} else {
						payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// ������� ȣ��
					}
					debug("payCancelResult========> " + payCancelResult);
				}
				
				// Ÿ��ī�� �Ǵ� ������ü�� ���(�þ�����)
				else if("0003".equals(sttl_mthd_clss) || "0004".equals(sttl_mthd_clss)) {
				
					String payType = "";
					if("0003".equals(sttl_mthd_clss))			payType = "CARD";	// �ſ�ī��
					else if("0004".equals(sttl_mthd_clss))		payType = "ABANK";	// ������ü

					String sShopid  = "bcgolfpkg";							// �����̵�
					String sCrossKey  = "337475c07e73d2d8fb92230d09daa87a";	// ũ�ν�Ű
					
					payEtt.setOrderNo(odr_no);			// �ֹ���ȣ
					payEtt.setAmount(sttl_amt);			// �����ݾ�	
					payEtt.setPayType(payType);			// �������
					payEtt.setShopId(sShopid);
					payEtt.setCrossKey(sCrossKey);

					payCancelResult = payProc.executePayAuthCancel_Allat(context, payEtt);		// ������� ȣ��	
					
					debug("payCancelResult========> " + payCancelResult + " | odr_no : " + odr_no + " | sttl_amt : " + sttl_amt + " | payType : " + payType);
				}					
				
				if(payCancelResult){
					
					sql = this.getPayUpdateQuery(); 
		            pstmt = conn.prepareStatement(sql);
		        	pstmt.setString(1, odr_no );
					result_upd = pstmt.executeUpdate();
					
					debug("ODR_NO========> " + odr_no);
					debug("result_upd========> " + result_upd);

			    	if(result_upd > 0){
			    		
			    		// ������ ������Ʈ
						sql = this.getUpdCompnQuery();
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, aplc_seq_no );
						result_upd += pstmt.executeUpdate();
						
			    		// �̺�Ʈ ������Ʈ
						sql = this.getUpdEvtQuery();
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, aplc_seq_no );
						result_upd += pstmt.executeUpdate();
						
						result = 1;
						debug("������������������========> " + result_upd);		
			    	}

				}
				else{	// �������н� ���� ���� 2009.11.26
					
					GolfPaymentRegDaoProc payFailProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");						
					
					DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);						
					dataSet.setString("CDHD_ID", cdhd_id);						//ȸ�����̵�
					dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);		//������������ڵ� :0001 : BCī�� / 0002:BCī�� + TOP����Ʈ / 0003:Ÿ��ī�� / 0004:������ü 
					dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);			//������ǰ�����ڵ� 0001:è�ǿ¿�ȸ�� 0002:��翬ȸ�� 0003:��忬ȸ�� 0008:����ȸ�� 
					dataSet.setString("STTL_STAT_CLSS", "Y");					//�������� N:�����Ϸ� / Y:�������
						
					result_upd = payFailProc.failExecute(context, dataSet, request, payEtt);
					
					debug("�������г���������========> " + result_upd);		
					result = 0;
					
				}
            }		
	        /*****************************************************************************/

			debug("�����ڵ� result ========> " + result);		
			
			if(result_upd > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
			
						
		} catch(Exception e) {
			try	{
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			//e.printStackTrace();
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
	        try { if(rs != null) rs.close(); } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
								
	/** ***********************************************************************
	* ������ ���, �ݾ�, ��ȭ��ȣ ����
	************************************************************************ */
	private String getGrdCompnUpdQuery(){
		StringBuffer sql = new StringBuffer();

		sql.append("\n	UPDATE BCDBA.TBGGOLFEVNTAPLCPE	");
		sql.append("\n	SET CDHD_GRD_SEQ_NO=?, STTL_AMT=?, HP_DDD_NO=?, HP_TEL_HNO=?, HP_TEL_SNO=?, BKG_PE_NM=?, EMAIL=?	");
		sql.append("\n	WHERE APLC_SEQ_NO=? AND SEQ_NO=?	");
		
		return sql.toString();
	}
	
	/** ***********************************************************************
	* ������ ���, �ݾ�, ��ȭ��ȣ ����
	************************************************************************ */
	private String getGrdCompnUpdQuery2(){
		StringBuffer sql = new StringBuffer();

		sql.append("\n	UPDATE BCDBA.TBGGOLFEVNTAPLCPE	");
		sql.append("\n	SET CDHD_GRD_SEQ_NO=?, STTL_AMT=?, HP_DDD_NO=?, HP_TEL_HNO=?, HP_TEL_SNO=?, BKG_PE_NM=?	");
		sql.append("\n	WHERE APLC_SEQ_NO=? AND SEQ_NO=?	");
		
		return sql.toString();
	}
	
	/** ***********************************************************************
	* ������ ����
	************************************************************************ */
	private String getGrdCompnDelQuery(){
		StringBuffer sql = new StringBuffer();

		sql.append("\n	DELETE FROM BCDBA.TBGGOLFEVNTAPLCPE WHERE APLC_SEQ_NO=? AND SEQ_NO=?	");
		
		return sql.toString();
	}

	/** ***********************************************************************
    * ������ ���
    ************************************************************************ */
    private String getEvtCompnQuery(){
        StringBuffer sql = new StringBuffer();

		sql.append("\n	INSERT INTO BCDBA.TBGGOLFEVNTAPLCPE (	");
		sql.append("\n	    SITE_CLSS, APLC_SEQ_NO, SEQ_NO, GOLF_SVC_APLC_CLSS, APLC_PE_CLSS, BKG_PE_NM, CDHD_NON_CDHD_CLSS, CDHD_ID, JUMIN_NO, CDHD_GRD_SEQ_NO	");
		sql.append("\n	    , HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, EMAIL, HADC_NUM, STTL_STAT_CLSS, CNCL_YN, STTL_AMT	");
		sql.append("\n	) VALUES (	");
		sql.append("\n	    '2', ?, ?, '9003', ?, ?, ?, ?, ?, ?	");
		sql.append("\n	    , ?, ?, ?, ?, ?, '0', 'N', ?	");
		sql.append("\n	)	");
		
		return sql.toString();
    }
		
	/** ***********************************************************************
	* �̺�Ʈ �ݾ� ����
	************************************************************************ */
	private String getEvtUpdQuery(){
		StringBuffer sql = new StringBuffer();

		sql.append("\n	UPDATE BCDBA.TBGGOLFEVNTAPLC 	");
		sql.append("\n	SET STTL_AMT=(SELECT SUM(STTL_AMT) FROM BCDBA.TBGGOLFEVNTAPLCPE WHERE APLC_SEQ_NO=?), STTL_STAT_CLSS=?, EVNT_PGRS_CLSS=?, MGR_MEMO=?	");
		sql.append("\n	, RSVT_DATE=? , RSV_TIME=?	");
		sql.append("\n	WHERE APLC_SEQ_NO=?	");
		
		return sql.toString();
	}
	
	/** ***********************************************************************
	* �̺�Ʈ �ݾ� ����
	************************************************************************ */
	private String getGrdSttlUpdQuery(){
		StringBuffer sql = new StringBuffer();

		sql.append("\n	UPDATE BCDBA.TBGGOLFEVNTAPLC 	");
		sql.append("\n	SET STTL_AMT=(SELECT SUM(STTL_AMT) FROM BCDBA.TBGGOLFEVNTAPLCPE WHERE APLC_SEQ_NO=?)	");
		sql.append("\n	WHERE APLC_SEQ_NO=?	");
		
		return sql.toString();
	}
	
	/** ***********************************************************************
	* �̺�Ʈ �������� ����
	************************************************************************ */
	private String getSttlUpdQuery(){
		StringBuffer sql = new StringBuffer();

		sql.append("\n	UPDATE BCDBA.TBGGOLFEVNTAPLC SET STTL_STAT_CLSS=? WHERE APLC_SEQ_NO=? 	");
		
		return sql.toString();
	}
	
	/** ***********************************************************************
	* �̺�Ʈ ������ϼ���
	************************************************************************ */
	private String getEvntUpdQuery(){
		StringBuffer sql = new StringBuffer();

		sql.append("\n	UPDATE BCDBA.TBGGOLFEVNTAPLC SET EVNT_PGRS_CLSS=? WHERE APLC_SEQ_NO=? 	");
		
		return sql.toString();
	}


	/** ***********************************************************************
	 * ���� ���� ��������
	 ************************************************************************ */
	private String setPayBackListQuery(){
		StringBuffer sql = new StringBuffer();        
		sql.append("\n	SELECT ODR_NO, STTL_AMT, MER_NO, CARD_NO, VALD_DATE, INS_MCNT, AUTH_NO, STTL_MTHD_CLSS, STTL_GDS_CLSS, CDHD_ID	\n");
		sql.append("\t	FROM BCDBA.TBGSTTLMGMT	\n");
		sql.append("\t	WHERE STTL_GDS_SEQ_NO=? AND STTL_GDS_CLSS='0010' AND STTL_STAT_CLSS='N'	\n");
		return sql.toString();
	}
	
	
	  /** ***********************************************************************
     *  �������� ��������
     ************************************************************************ */
     public String getPayCost(){
 		StringBuffer sql = new StringBuffer();
 		sql.append("\n 			SELECT CPO_AMT, ACRG_CDHD_AMT, FREE_CDHD_AMT " );
 		sql.append("\n 			FROM BCDBA.TBGMMLYMTNG ");
 		sql.append("\n			WHERE SEQ_NO = ? 	");
 		return sql.toString();
 	}
	
    /** ***********************************************************************
    * ���� ��� ������Ʈ    
    ************************************************************************ */
    private String getPayUpdateQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t	UPDATE BCDBA.TBGSTTLMGMT	\n");
 		sql.append("\t	SET STTL_STAT_CLSS='Y', CNCL_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
 		sql.append("\t	WHERE ODR_NO=?	\n");
        return sql.toString();
    }
    
    /** ***********************************************************************
     * ���� ���¸� �����Ѵ�. - ������
     ************************************************************************ */
 	private String getUpdCompnQuery(){
 		StringBuffer sql = new StringBuffer();

 		sql.append("\n	UPDATE BCDBA.TBGGOLFEVNTAPLCPE SET STTL_STAT_CLSS='2' WHERE APLC_SEQ_NO=?	\n");
 				
 		return sql.toString();
 	}
    
    /** ***********************************************************************
     * ���� ���¸� �����Ѵ�. - �̺�Ʈ
     ************************************************************************ */
 	private String getUpdEvtQuery(){
 		StringBuffer sql = new StringBuffer();

 		sql.append("\n	UPDATE BCDBA.TBGGOLFEVNTAPLC SET STTL_STAT_CLSS='2', EVNT_PGRS_CLSS='E' WHERE APLC_SEQ_NO=?	\n");
 				
 		return sql.toString();
 	}
}



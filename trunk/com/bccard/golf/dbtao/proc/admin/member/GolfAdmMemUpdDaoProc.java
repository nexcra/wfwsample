/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmGrUpdDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ > ȸ������ > ����
*   �������  : golf
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
* Ŀ��屸����	������		������	���泻��
* GOLFLOUNG		20100513	������	���ᰡ���� ���� �߰�, ���������� TM ȸ���� ��� TMȸ�� ���̺� ���ó�� ������Ʈ
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfAdmMemUpdDaoProc extends AbstractProc {

	public static final String TITLE = "������ > ȸ������ > ȸ�� ���� ����";

	/** *****************************************************************
	 * GolfadmGrUpdDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmMemUpdDaoProc() {}

	/** *****************************************************************
	 * GolfadmGrUpdDaoProc ȸ������ ���� �޼ҵ�
	 * @param N/A
	 ***************************************************************** */
	public int execute(WaContext context, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
            /*****************************************************************************/
            
			//01.ȸ������ ���̺� ���� : 
			sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);

			int idx = 0;
			pstmt.setString(++idx, data.getString("BOKG_LIMT_YN") );
			pstmt.setString(++idx, data.getString("BOKG_LIMT_FIXN_STRT_DATE") );
			pstmt.setString(++idx, data.getString("BOKG_LIMT_FIXN_END_DATE") );
			pstmt.setString(++idx, data.getString("ACRG_CDHD_JONN_DATE") );
			pstmt.setString(++idx, data.getString("ACRG_CDHD_END_DATE") );
			pstmt.setString(++idx, data.getString("CDHD_ID") );		
			
			result = pstmt.executeUpdate();
           			
			
			conn.setAutoCommit(true);
						
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
	 * GolfadmGrUpdDaoProc ��޺��� �޼ҵ�
	 * @param N/A
	 ***************************************************************** */
	public int execute_grade(WaContext context, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		int result2 = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		
    	String strStDate = "";		// ����ȸ���������� 
    	String strEdDate = "";		// ����ȸ����������
    	int addMonth = 0;			// ����ȸ�� 

    	SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");   
    	GregorianCalendar cal = new GregorianCalendar();
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String cdhd_id = data.getString("CDHD_ID");		// ȸ�����̵�
			String grade_old = data.getString("GRADE_OLD");	// �������
			String grade_new = data.getString("GRADE_NEW");	// ������(�Ķ����)
			String grade_new_num = grade_new;	// ������(��������)
			if("SKI".equals(grade_new))	grade_new_num = "6";
			
			int idx = 0;									// ���� ���ڰ� �ε���
			int grade_seq = 0;								// ��ް��� ���̺� �Ϸù�ȣ
			
			// 01. ����� ����� �ִ��� �˻�
			sql = this.getMemGradeQuery(); 
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, cdhd_id );
            rs = pstmt.executeQuery();	
            
            if(rs.next()){
            	// ���� ����� ����� ������ �ִ� ��� => �����丮�� �����ϰ� ������Ʈ ���ش�.
            	grade_seq = rs.getInt("GRADE_SEQ");
            	
            	if(grade_seq > 0){
            		
            		// �����丮 �μ�Ʈ
					/**SEQ_NO ��������**************************************************************/
					sql = this.getMaxHistoryQuery(); 
		            pstmt = conn.prepareStatement(sql);
		            rs = pstmt.executeQuery();			
					long max_seq_no = 0L;
					if(rs.next()){
						max_seq_no = rs.getLong("MAX_SEQ_NO");
					}
					if(rs != null) rs.close();
		            if(pstmt != null) pstmt.close();
		            
		            /**Insert************************************************************************/
		            sql = this.setHistoryQuery();
					pstmt = conn.prepareStatement(sql);
					
					idx = 0;
					pstmt.setLong(++idx, max_seq_no );	// �����丮 ���̺� �Ϸù�ȣ
					pstmt.setLong(++idx, grade_seq );	// ����ȸ����ް��� ���̺� �Ϸù�ȣ
					
					result2 = pstmt.executeUpdate();
					
					if(result2 > 0){
		            
	            		// ��� ������Ʈ
	        			idx = 0;
	        			sql = this.updGradeQuery();
	        			pstmt = conn.prepareStatement(sql);
	        			
	        			pstmt.setString(++idx,  grade_new_num);
	        			pstmt.setInt(++idx,  grade_seq);
	        			
	        			result = pstmt.executeUpdate();
	        			
					}
            		
            	}
            }else{
            	// ����� ����� ���� ��� ����� ����� �μ�Ʈ �Ѵ�.
				/**SEQ_NO ��������**************************************************************/
				sql = this.getMaxGradeQuery(); 
	            pstmt = conn.prepareStatement(sql);
	            rs = pstmt.executeQuery();			
				long max_seq_no = 0L;
				if(rs.next()){
					max_seq_no = rs.getLong("MAX_SEQ_NO");
				}
				if(rs != null) rs.close();
	            if(pstmt != null) pstmt.close();
	            
	            /**Insert************************************************************************/
	            sql = this.setGradeQuery();
				pstmt = conn.prepareStatement(sql);
				
				idx = 0;
				pstmt.setLong(++idx, max_seq_no );	// �����丮 ���̺� �Ϸù�ȣ
				pstmt.setString(++idx, cdhd_id );	// ���̵�
				pstmt.setString(++idx, grade_new_num );	// ���ο� ��� �Ϸù�ȣ
				
				result = pstmt.executeUpdate();
            }
            
            if(result>0){

            	if(grade_new.equals("8")){
            		strStDate = "";
            		strEdDate = "";
            	}else{
            		
	            	if(grade_new.equals("17")){
	            		addMonth = 3;
	            	}else if(grade_new.equals("18")){
	            		addMonth = 2;
	            	}else{
	            		addMonth = 12;
	            	}
	            	
	            	Date stDate = cal.getTime();
	            	cal.add(cal.MONTH, addMonth);
	            	Date edDate = cal.getTime();
	
	            	strStDate = fmt.format(stDate);
	            	strEdDate = fmt.format(edDate);
            	}
	            	
            	
            	// ����� ���̺� ��� ������Ʈ
    			idx = 0;
    			sql = this.updGradeMemQuery(grade_old, grade_new);
    			pstmt = conn.prepareStatement(sql);
    			
    			pstmt.setString(++idx,  grade_new_num);
    			pstmt.setString(++idx,  strStDate);
    			pstmt.setString(++idx,  strEdDate);
    			pstmt.setString(++idx,  cdhd_id);
    			
    			result = pstmt.executeUpdate();
            }
			

			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
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
            try { if(rs != null) rs.close(); } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	

	/** *****************************************************************
	 * GolfadmGrUpdDaoProc �������� �޼ҵ�
	 * @param N/A
	 ***************************************************************** */
	public int execute_pay(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		int result_upd = 0;		// ��ȸ�� ȯ�� ��������
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String cdhd_id = data.getString("CDHD_ID");		// ȸ�����̵�
			String payBack = data.getString("payBack");		// ��ȸ��ȯ�޿���
			int idx = 0;									// ���� ���ڰ� �ε���
			int grade_seq = 0;								// ��ް��� ���̺� �Ϸù�ȣ
			
            
        	if("Y".equals(payBack)){
        		
        		// ��ȸ�� ȯ��ó�� ���ش�.
        		boolean payCancelResult = false;
        		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// ��������
        		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");

				String ip = request.getRemoteAddr();  // �ܸ���ȣ(IP, '.'����)
				String merMgmtNo = "";	// ��������ȣ
				String ispCardNo = "";	// ī���ȣ
				String valdlim = "";	// ��ȿ�Ⱓ. �⵵�� 2�ڸ� ���� (200507 �� 0507)
				String sum = "";		// ���αݾ�
				String useNo = "";		// ���ι�ȣ
				String ord_no = "";		// �ֹ���ȣ
				String ins_mcnt = "";	// �ҺαⰣ
				String sttl_mthd_clss = "";	// ������������ڵ�
				String sttl_gds_clss = "";	// ������ǰ�����ڵ�
				

				// ���� ���� ��ȸ
				idx = 0;
				sql = this.setPayBackListQuery(); 
	            pstmt = conn.prepareStatement(sql);
				pstmt.setString(++idx, cdhd_id );
	            rs = pstmt.executeQuery();	
	            
	            while(rs.next()){
	            	payCancelResult = false;

	            	ord_no = rs.getString("ODR_NO");
	            	sum = rs.getString("STTL_AMT");
	            	merMgmtNo = rs.getString("MER_NO");
	            	ispCardNo = rs.getString("CARD_NO");
	            	valdlim = rs.getString("VALD_DATE");
	            	useNo = rs.getString("AUTH_NO");
	            	ins_mcnt = rs.getString("INS_MCNT");
					sttl_mthd_clss = rs.getString("STTL_MTHD_CLSS");
					sttl_gds_clss = rs.getString("STTL_GDS_CLSS");


					// ��ī�� �Ǵ� ���հ����� ���
					if("0001".equals(sttl_mthd_clss) || "0002".equals(sttl_mthd_clss)) {
						payEtt.setMerMgmtNo(merMgmtNo);		
						payEtt.setCardNo(ispCardNo);
						payEtt.setValid(valdlim);	
						payEtt.setAmount(sum);
						payEtt.setRemoteAddr(ip);	
						payEtt.setUseNo(useNo);	
						payEtt.setInsTerm(ins_mcnt);
	
						String host_ip = java.net.InetAddress.getLocalHost().getHostAddress();
						if( "211.181.255.40".equals(host_ip)) {
							payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// ������� ȣ��
						} else {
							payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// ������� ȣ��
						}
						//debug("payCancelResult : " + payCancelResult);

					}
					
					// Ÿ��ī�� �Ǵ� ������ü�� ���(�þ�����)
					else if("0003".equals(sttl_mthd_clss) || "0004".equals(sttl_mthd_clss)) {
					
						String payType = "";
						if("0003".equals(sttl_mthd_clss))			payType = "CARD";	// �ſ�ī��
						else if("0004".equals(sttl_mthd_clss))		payType = "ABANK";	// ������ü

						String sShopid  = "bcgolf";							// �����̵�
						String sCrossKey  = "e3f8453680e39fc1d6dfe72079874219";	// ũ�ν�Ű
						
						payEtt.setOrderNo(ord_no);			// �ֹ���ȣ
						payEtt.setAmount(sum);			// �����ݾ�	
						payEtt.setPayType(payType);			// �������
						payEtt.setShopId(sShopid);
						payEtt.setCrossKey(sCrossKey);

						payCancelResult = payProc.executePayAuthCancel_Allat(context, payEtt);		// ������� ȣ��	
						
						//debug("payCancelResult : " + payCancelResult + " | odr_no : " + ord_no + " | sttl_amt : " + sum + " | payType : " + payType);
					}				
						
						
						
					if(payCancelResult){

						sql = this.getPayUpdateQuery(); 
			            pstmt = conn.prepareStatement(sql);
			        	pstmt.setString(1, ord_no );
						result_upd = pstmt.executeUpdate();

						
					}
					else{	// �������н� ���� ���� 2009.11.26
						
						GolfPaymentRegDaoProc payFailProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");						
						
						DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);						
						dataSet.setString("CDHD_ID", cdhd_id);						//ȸ�����̵�
						dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);		//������������ڵ� :0001 : BCī�� / 0002:BCī�� + TOP����Ʈ / 0003:Ÿ��ī�� / 0004:������ü 
						dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);			//������ǰ�����ڵ� 0001:è�ǿ¿�ȸ�� 0002:��翬ȸ�� 0003:��忬ȸ�� 0008:����ȸ�� 
						dataSet.setString("STTL_STAT_CLSS", "Y");					//�������� N:�����Ϸ� / Y:�������
							
						int result_fail = payFailProc.failExecute(context, dataSet, request, payEtt);
						
						debug("�������г��������� : " + result_fail + " / ord_no : " + ord_no + " / useNo : " + useNo);
												
					}
	            }						
        	}

        	if("N".equals(payBack) || ("Y".equals(payBack)) && result_upd>0){
				// Ż���Ų��.
	        	idx = 0;
				sql = this.setMemCcQuery(); 
	            pstmt = conn.prepareStatement(sql);
				pstmt.setString(++idx, cdhd_id );
	            result = pstmt.executeUpdate();	
        	}
			

			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
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
	        try { if(rs != null) rs.close(); } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}

	/** *****************************************************************
	 * GolfadmGrUpdDaoProc ȸ���������� �޼ҵ�
	 * @param N/A
	 ***************************************************************** */
	public int execute_del(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String cdhd_id = data.getString("CDHD_ID");		// ȸ�����̵�
			String jumin_no = data.getString("JUMIN_NO");
			
			/*
			������ ���̺�  = 16��
			TBGCLUBBBRD / ��ȣȸ�Խ��� -> CLUB_CDHD_SEQ_NO / ��ȣȸȸ���Ϸù�ȣ	
			TBGCLUBBBRDREPY / ��ȣȸ�Խ��Ǵ�� -> REG_PE_ID
			TBGCLUBCDHDMGMT / ��ȣȸȸ������
			
			TBGBBRD / �Խ��� => ID
			TBGBBRDREPY / �Խ��Ǵ�� => RGS_PE_ID
			TBGCUPNUSEHST / ��������̷�
			TBGRSVTMGMT / �������
			TBGAPLCMGMT / ��û����
			TBGSCORINFO / ���ھ�����
			TBGSCRAP / ��ũ��			
			TBGCBMOUSECTNTMGMT / ���̹��Ӵϻ�볻������
			TBGSTTLMGMT / ��������
			TBGCDHDGRDCHNGHST / ȸ����޺����̷�
			TBGCDHDRIKMGMT / ����ȸ������ǰ����
			
			TBGGOLFCDHDGRDMGMT / ����ȸ����ް���
			TBGGOLFCDHD / ����ȸ��
			
			TBLUGTMCSTMR / TMȸ�� �Խ��� - TM ȸ���� ��� ����ó�� ������Ʈ
			*/

			// Tm ȸ������ �˻��ؼ� ���ó�� ��� ������Ʈ
            sql = this.getTMquery(); 
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, jumin_no );
            rs = pstmt.executeQuery();	
            if(rs.next()){
    			sql = this.getTmUpdquery();
                pstmt = conn.prepareStatement(sql);
    			pstmt.setString(1, jumin_no );
                result = pstmt.executeUpdate();	
            }
            
//			TBGCLUBBBRD / ��ȣȸ�Խ��� -> CLUB_CDHD_SEQ_NO / ��ȣȸȸ���Ϸù�ȣ	
			sql = this.getTBGCLUBBBRDquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
            
//			TBGCLUBBBRDREPY / ��ȣȸ�Խ��Ǵ�� -> REG_PE_ID
			sql = this.getTBGCLUBBBRDREPYquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
			
//			TBGCLUBCDHDMGMT / ��ȣȸȸ������
			sql = this.getTBGCLUBCDHDMGMTquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
			
//			
//			TBGBBRD / �Խ��� => ID
			sql = this.getTBGBBRDquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
			
//			TBGBBRDREPY / �Խ��Ǵ�� => RGS_PE_ID
			sql = this.getTBGBBRDREPYquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
			
//			TBGCUPNUSEHST / ��������̷�
			sql = this.getTBGCUPNUSEHSTquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
			
//			TBGRSVTMGMT / �������
			sql = this.getTBGRSVTMGMTquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
			
//			TBGAPLCMGMT / ��û����
			sql = this.getTBGAPLCMGMTquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
			
//			TBGSCORINFO / ���ھ�����
			sql = this.getTBGSCORINFOquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
			
//			TBGSCRAP / ��ũ��	
			sql = this.getTBGSCRAPquery();		
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
			
//			TBGCBMOUSECTNTMGMT / ���̹��Ӵϻ�볻������
			sql = this.getTBGCBMOUSECTNTMGMTquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
			
//			TBGSTTLMGMT / ��������
			sql = this.getTBGSTTLMGMTquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
			
//			TBGCDHDGRDCHNGHST / ȸ����޺����̷�
			sql = this.getTBGCDHDGRDCHNGHSTquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
			
//			TBGCDHDRIKMGMT / ����ȸ������ǰ����
			sql = this.getTBGCDHDRIKMGMTquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
			
//			
//			TBGGOLFCDHDGRDMGMT / ����ȸ����ް���
			sql = this.getTBGGOLFCDHDGRDMGMTquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();	
			
//			TBGGOLFCDHD / ����ȸ��
			sql = this.getTBGGOLFCDHDquery();
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, cdhd_id );
            result = pstmt.executeUpdate();		

            if(pstmt != null) pstmt.close();
            
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
	
	
    /** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    :ȸ�����̺�
    ************************************************************************ */
	private String getInsertQuery(){
		StringBuffer sql = new StringBuffer();
		    
		sql.append("\n");
		sql.append("\t  UPDATE BCDBA.TBGGOLFCDHD SET											\n");
		sql.append("\t  BOKG_LIMT_YN=?, BOKG_LIMT_FIXN_STRT_DATE=?, BOKG_LIMT_FIXN_END_DATE=?	\n");
		sql.append("\t  , ACRG_CDHD_JONN_DATE=?, ACRG_CDHD_END_DATE=?	\n");
		sql.append("\t  WHERE CDHD_ID=?															\n");
					
		return sql.toString();
	}
	
	 
	/** ***********************************************************************
	* �ش�ȸ���� ����� ����� ������ �ִ��� �˻�
	************************************************************************ */
	private String getMemGradeQuery(){
		StringBuffer sql = new StringBuffer();
		      
		sql.append("\n	SELECT T2.CDHD_CTGO_SEQ_NO GRADE_NO, T1.CDHD_GRD_SEQ_NO GRADE_SEQ	\n");
		sql.append("\t 	FROM BCDBA.TBGGOLFCDHDGRDMGMT T1	\n");
		sql.append("\t 	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.CDHD_CTGO_SEQ_NO=T2.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t 	JOIN BCDBA.TBGCMMNCODE T3 ON T2.CDHD_SQ2_CTGO=T3.GOLF_CMMN_CODE AND T3.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t 	WHERE T1.CDHD_ID = ? AND T2.CDHD_SQ1_CTGO='0002'	\n");
					
		return sql.toString();
	}
	  
	/** ***********************************************************************
	* �����丮 ���̺� �Ϸù�ȣ �ִ밪 �������� 
	************************************************************************ */
	private String getMaxHistoryQuery(){
		StringBuffer sql = new StringBuffer();

		sql.append("\n");
		sql.append("\t	SELECT MAX(NVL(SEQ_NO,0))+1 MAX_SEQ_NO FROM BCDBA.TBGCDHDGRDCHNGHST 	\n");
					
		return sql.toString();
	}
	  
	/** ***********************************************************************
	* �����丮 ���̺� �����Ѵ�.
	************************************************************************ */
	private String setHistoryQuery(){
		StringBuffer sql = new StringBuffer();
		
		sql.append("\n	");
		sql.append("\n	INSERT INTO BCDBA.TBGCDHDGRDCHNGHST	");
		sql.append("\n	SELECT ?, A.CDHD_CTGO_SEQ_NO, A.CDHD_ID, A.CDHD_CTGO_SEQ_NO, to_char(sysdate,'YYYYMMDDHH24MISS')	");
		sql.append("\n	, B.ACRG_CDHD_JONN_DATE , B.ACRG_CDHD_END_DATE , B.JOIN_CHNL	");
		sql.append("\n	FROM BCDBA.TBGGOLFCDHDGRDMGMT A , BCDBA.TBGGOLFCDHD B	");
		sql.append("\n	WHERE A.CDHD_GRD_SEQ_NO= ?	");
		sql.append("\n	AND A.CDHD_ID = B.CDHD_ID	");
					
		return sql.toString();
	}
	   
	/** ***********************************************************************
	* ����� ������Ʈ �Ѵ�.
	************************************************************************ */
	private String updGradeQuery(){
		StringBuffer sql = new StringBuffer();

		sql.append("\n");
		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHDGRDMGMT  	\n");
		sql.append("\t 	SET CDHD_CTGO_SEQ_NO=?, CHNG_ATON=to_char(sysdate,'YYYYMMDDHH24MISS') 	\n");
		sql.append("\t	WHERE CDHD_GRD_SEQ_NO=?	\n");
				
		return sql.toString();
	}
	   
	/** ***********************************************************************
	* ȸ�����̺� ����� ������Ʈ �Ѵ�.
	************************************************************************ */
	private String updGradeMemQuery(String grade_old, String grade_new){
		StringBuffer sql = new StringBuffer();

		sql.append("\n");
		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD  	\n");
		sql.append("\t 	SET CDHD_CTGO_SEQ_NO=?, CHNG_ATON=to_char(sysdate,'YYYYMMDDHH24MISS') 	\n");
		sql.append("\t 	, ACRG_CDHD_JONN_DATE=?, ACRG_CDHD_END_DATE=?	\n");
		if("SKI".equals(grade_new)){	// ��罺Ű�ϰ�� ����
			sql.append("\t 	, JOIN_CHNL='2302', AFFI_FIRM_NM='SKI'	\n");
		}
		sql.append("\t	WHERE CDHD_ID=?	\n");
				
		return sql.toString();
	}
	  
	/** ***********************************************************************
	* ����ȸ����ް��� �Ϸù�ȣ �ִ밪�� �����´�. 
	************************************************************************ */
	private String getMaxGradeQuery(){
		StringBuffer sql = new StringBuffer();

		sql.append("\n");
		sql.append("\t	SELECT MAX(NVL(CDHD_GRD_SEQ_NO,0))+1 MAX_SEQ_NO FROM BCDBA.TBGGOLFCDHDGRDMGMT 	\n");
					
		return sql.toString();
	}
	
	/** ***********************************************************************
	 * ����� ����� ���� ��� �μ�Ʈ ���ش�.
	 ************************************************************************ */
	private String setGradeQuery(){
		StringBuffer sql = new StringBuffer();

		sql.append("\n");
		sql.append("\t	INSERT INTO BCDBA.TBGGOLFCDHDGRDMGMT VALUES(	\n");
		sql.append("\t 	?, ?, ?, to_char(sysdate,'YYYYMMDDHH24MISS'), '', ''	\n");
		sql.append("\t 	) 	\n");
				
		return sql.toString();
	}
	
	/** ***********************************************************************
	 * �������� ó��
	 ************************************************************************ */
	private String setMemCcQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD SET 	\n");
		sql.append("\t 	SECE_YN='Y', SECE_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
		sql.append("\t 	WHERE CDHD_ID=?	\n");
		return sql.toString();
	}
	
	/** ***********************************************************************
	 * ������� ����Ʈ ��������
	 ************************************************************************ */
	private String setPayBackListQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n	SELECT PAY.ODR_NO, PAY.STTL_AMT, PAY.MER_NO, PAY.CARD_NO, PAY.VALD_DATE, PAY.AUTH_NO, INS_MCNT, STTL_MTHD_CLSS, STTL_GDS_CLSS	\n");
		sql.append("\t 	FROM BCDBA.TBGGOLFCDHD CDHD	\n");
		sql.append("\t 	LEFT JOIN BCDBA.TBGSTTLMGMT PAY ON PAY.CDHD_ID=CDHD.CDHD_ID AND PAY.STTL_ATON BETWEEN CDHD.ACRG_CDHD_JONN_DATE AND CDHD.ACRG_CDHD_END_DATE	\n");
		sql.append("\t 	LEFT JOIN BCDBA.TBGCMMNCODE CODE ON CODE.GOLF_CMMN_CODE=PAY.STTL_GDS_CLSS	\n");
		sql.append("\t	WHERE CDHD.CDHD_ID=? AND (SECE_YN IS NULL OR SECE_YN='N')	\n");
		sql.append("\t 	AND CODE.GOLF_CMMN_CLSS='0016' AND (CODE.GOLF_CMMN_CODE_NM LIKE '%�����%' OR CODE.GOLF_CMMN_CODE_NM='ȸ����� ���׷��̵�' )		\n");
		sql.append("\t 	AND PAY.STTL_STAT_CLSS='N' AND STTL_AMT>0	\n");		
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
	* TBGCLUBBBRD / ��ȣȸ�Խ��� -> CLUB_CDHD_SEQ_NO / ��ȣȸȸ���Ϸù�ȣ	
	************************************************************************ */
	private String getTBGCLUBBBRDquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGCLUBBBRD WHERE CLUB_CDHD_SEQ_NO IN (	\n");
 		sql.append("\t	    SELECT CLUB_CDHD_SEQ_NO FROM BCDBA.TBGCLUBCDHDMGMT WHERE CDHD_ID=?	\n");
 		sql.append("\t	)	\n");
		return sql.toString();
	}

	/** ***********************************************************************
	* TBGCLUBBBRDREPY / ��ȣȸ�Խ��Ǵ�� -> REG_PE_ID	
	************************************************************************ */
	private String getTBGCLUBBBRDREPYquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGCLUBBBRDREPY WHERE REG_PE_ID=?	\n");
		return sql.toString();
	}

	/** ***********************************************************************
	* TBGCLUBCDHDMGMT / ��ȣȸȸ������	
	************************************************************************ */
	private String getTBGCLUBCDHDMGMTquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGCLUBCDHDMGMT WHERE CDHD_ID=?	\n");
		return sql.toString();
	}

	/** ***********************************************************************
	* TBGBBRD / �Խ��� => ID	
	************************************************************************ */
	private String getTBGBBRDquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGBBRD WHERE ID=?	\n");
		return sql.toString();
	}

	/** ***********************************************************************
	* TBGBBRDREPY / �Խ��Ǵ�� => RGS_PE_ID	
	************************************************************************ */
	private String getTBGBBRDREPYquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGBBRDREPY WHERE RGS_PE_ID=?	\n");
		return sql.toString();
	}

	/** ***********************************************************************
	* TBGCUPNUSEHST / ��������̷�	
	************************************************************************ */
	private String getTBGCUPNUSEHSTquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGCUPNUSEHST WHERE CDHD_ID=?	\n");
		return sql.toString();
	}
	
	/** ***********************************************************************
	* TBGRSVTMGMT / �������	
	************************************************************************ */
	private String getTBGRSVTMGMTquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGRSVTMGMT WHERE CDHD_ID=?	\n");
		return sql.toString();
	}

	/** ***********************************************************************
	* TBGAPLCMGMT / ��û����	
	************************************************************************ */
	private String getTBGAPLCMGMTquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGAPLCMGMT WHERE CDHD_ID=?	\n");
		return sql.toString();
	}

	/** ***********************************************************************
	* TBGSCORINFO / ���ھ�����	
	************************************************************************ */
	private String getTBGSCORINFOquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGSCORINFO WHERE CDHD_ID=?	\n");
		return sql.toString();
	}
		
	/** ***********************************************************************
	* TBGSCRAP / ��ũ��	
	************************************************************************ */
	private String getTBGSCRAPquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGSCRAP WHERE CDHD_ID=?	\n");
		return sql.toString();
	}

	/** ***********************************************************************
	* TBGCBMOUSECTNTMGMT / ���̹��Ӵϻ�볻������	
	************************************************************************ */
	private String getTBGCBMOUSECTNTMGMTquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGCBMOUSECTNTMGMT WHERE CDHD_ID=?	\n");
		return sql.toString();
	}
	
	/** ***********************************************************************
	* TBGSTTLMGMT / ��������	
	************************************************************************ */
	private String getTBGSTTLMGMTquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGSTTLMGMT WHERE CDHD_ID=?	\n");
		return sql.toString();
	}
	
	/** ***********************************************************************
	* TBGCDHDGRDCHNGHST / ȸ����޺����̷�	
	************************************************************************ */
	private String getTBGCDHDGRDCHNGHSTquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGCDHDGRDCHNGHST WHERE CDHD_ID=?	\n");
		return sql.toString();
	}

	/** ***********************************************************************
	* TBGCDHDRIKMGMT / ����ȸ������ǰ����
	************************************************************************ */
	private String getTBGCDHDRIKMGMTquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGCDHDRIKMGMT WHERE CDHD_ID=?	\n");
		return sql.toString();
	}

	/** ***********************************************************************
	* TBGGOLFCDHDGRDMGMT / ����ȸ����ް���	
	************************************************************************ */
	private String getTBGGOLFCDHDGRDMGMTquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGGOLFCDHDGRDMGMT WHERE CDHD_ID=?	\n");
		return sql.toString();
	}

	/** ***********************************************************************
	* TBGGOLFCDHD / ����ȸ��	
	************************************************************************ */
	private String getTBGGOLFCDHDquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	DELETE FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID=?	\n");
		return sql.toString();
	}
	
	/** ***********************************************************************
	* TMȸ������ �˾Ƴ���	
	************************************************************************ */
	private String getTMquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	SELECT * FROM BCDBA.TBLUGTMCSTMR	\n");
 		sql.append("\t	WHERE RND_CD_CLSS='2' AND  ACPT_CHNL_CLSS ='1' AND TB_RSLT_CLSS IN ('00','01') AND JUMIN_NO=?	\n");
		return sql.toString();
	}
	
	/** ***********************************************************************
	* TM ȸ�����̺� ���ó�� ������Ʈ
	************************************************************************ */
	private String getTmUpdquery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
 		sql.append("\t	UPDATE BCDBA.TBLUGTMCSTMR SET TB_RSLT_CLSS='98',REJ_RSON ='���ó��-����'	\n");
 		sql.append("\t	WHERE  RND_CD_CLSS='2' AND  ACPT_CHNL_CLSS ='1' AND TB_RSLT_CLSS IN ('00','01') AND JUMIN_NO=?	\n");
		return sql.toString();
	}
}



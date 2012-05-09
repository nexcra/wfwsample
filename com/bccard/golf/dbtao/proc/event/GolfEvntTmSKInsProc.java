/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntTmSKInsProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ī�� TM SK ������ �߱� �̺�Ʈ ó�� 
*   �������  : golf
*   �ۼ�����  : 2010-07-15
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event;

import java.io.IOException;
import java.util.HashMap;
import java.net.URL;
import java.net.URLConnection;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

import java.net.InetAddress;
import com.bccard.golf.common.AppConfig;

/******************************************************************************
 * Golf
 * @author	E4NET
 * @version	1.0
 ******************************************************************************/
public class GolfEvntTmSKInsProc extends AbstractProc {
	public static final String TITLE = "�̺�Ʈ > TM ��ȭ ���ű� �̺�Ʈ > ������ȣ ��� �˾� > ó��";
	
	/** *****************************************************************
	 * GolfEvntTmSKInsProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfEvntTmSKInsProc() {}		


	// ��û���� ����
	public DbTaoResult get_cp_bcd(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null; 
		DbTaoResult result = new DbTaoResult(title);
		 
		try {
			String cdhd_id = data.getString("cdhd_id");
			
			conn = context.getDbConnection("default", null);
			
			String sql = this.getAplCnt(); 
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1,cdhd_id);
			
			rs = pstmt.executeQuery();

			if(rs != null) {

				while(rs.next()){
					result.addString("CO_NM" 		,rs.getString("CO_NM") );
					result.addString("HP_DDD_NO" 	,rs.getString("HP_DDD_NO") );
					result.addString("HP_TEL_HNO" 	,rs.getString("HP_TEL_HNO") );
					result.addString("HP_TEL_SNO" 	,rs.getString("HP_TEL_SNO") );
					result.addString("DDD_NO" 		,rs.getString("DDD_NO") );
				}
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
	
	// ���
	public DbTaoResult sendCoup(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;  
		DbTaoResult result = new DbTaoResult(title);
		 
		try {
			String cp_bcd		= data.getString("cp_bcd");
			String cdhd_id		= data.getString("cdhd_id");
			String ddd_no		= data.getString("ddd_no");
			
			String hp_ddd_no 	= data.getString("hp_ddd_no");
			String hp_tel_hno 	= data.getString("hp_tel_hno");
			String hp_tel_sno 	= data.getString("hp_tel_sno");
			String min_no 		= hp_ddd_no+""+hp_tel_hno+""+hp_tel_sno;			
			String send_req_check_yn = "Y";	
			
			// �������϶��� �ߺ�üũ�� ���ش�.
			if(!GolfUtil.empty(cp_bcd)){
				send_req_check_yn = "N";
			}
			
			//debug("proc ::: cp_bcd : " + cp_bcd + " / send_req_check_yn : " + send_req_check_yn);
						

			String hostAddress = InetAddress.getLocalHost().getHostAddress(); 
			String devip = "";
			String urls = "";
			try {
				devip = AppConfig.getAppProperty("DV_WAS_1ST");
			} catch(Throwable t) {}
			
			
			if (devip.equals(hostAddress)) {  //���߱�
				urls = "http://devweb.entrac.co.kr:9110";
			} else { //���
				urls = "http://ntm.entrac.co.kr";
			}
			urls = "http://ntm.entrac.co.kr";
			
			//urls += "/jsp/Coupon.jsp?co_id=92&p_id=247&mc_id=0484&";
			urls += "/jsp/Coupon.jsp?co_id=123&p_id=2414&mc_id=2615&";
			urls += "trans_ver=0001&url_push_yn=N&call_back_cont=&mms_push_yn=Y&mms_subj=&mms_cont=&sms_push_yn=N&sms_cont=&size_fg=M&";
			urls += "min_no="+min_no+"&";
			urls += "req_co_id=&req_co_nm=&";
			urls += "cp_bcd="+cp_bcd+"&";
			urls += "cust_nm=&cust_num=&";
			urls += "send_req_check_yn="+send_req_check_yn+"&send_req_fg_1="+min_no+"&";
			urls += "send_req_fg_2=&send_req_fg_3=&send_req_fg_4=&send_req_fg_5=&send_req_fg_6=&req_id=skmc";
			
			debug("urls : " + urls);
			

			URL url = new URL(urls);
			URLConnection connection; //URL������ ������ ��ü
			InputStream is; //URL���ӿ��� ������ �б����� Stream
			InputStreamReader isr;
			BufferedReader br;
			connection = url.openConnection();
			
			//������ �о�������� InputStream��ü�� �����Ѵ�. 
			is = connection.getInputStream();
			//System.out.println(123123);
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);

            
			String buf = null;
            StringBuffer sb = new StringBuffer();

            while(true){
                buf = br.readLine();
                if(buf == null) break;
                sb.append(buf);
            }
            
            String retMsg = (sb.toString()).trim();
			debug("retMsg>>>>>>>>>>>>>>>>>>>>>>>>>>>" + retMsg);
			//retMsg>>>>>>>>>>>>>>>>>>>>>>>>>>>00||����||48705330

			
			String msgs[] = retMsg.split("\\|\\|");
			String return_code = "";
			String return_msg = "";
			String return_coupon = "";
			int msgs_size = msgs.length;
			int aplIns = 0;
			
			if(msgs_size>0) return_code = msgs[0];
			if(msgs_size>1) return_msg = msgs[1];
			if(msgs_size>2) return_coupon = msgs[2];			
			
			if(return_code.equals("00") && GolfUtil.empty(cp_bcd)){

				DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
				
				dataSet.setString("ddd_no", ddd_no);
				dataSet.setString("hp_ddd_no", hp_ddd_no);
				dataSet.setString("hp_tel_hno"	, hp_tel_hno);
				dataSet.setString("hp_tel_sno"	, hp_tel_sno);
				dataSet.setString("cdhd_id"	, cdhd_id);
				dataSet.setString("cp_bcd"	, return_coupon);				
				
				aplIns = aplIns(context, request, dataSet);
			}
			
			result.addString("return_code" 		,return_code );
			result.addString("return_msg" 		,return_msg );
			result.addString("return_coupon" 	,return_coupon );
			result.addInt("aplIns" 	,aplIns );

			
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}


	// ��û���� ����
	public int aplIns(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		String title = data.getString("TITLE");
		Connection conn = null;
		PreparedStatement pstmt = null;  
		int result = 0;
		 
		try {
			String pgrs_yn 		= data.getString("pgrs_yn");
			String cdhd_id		= data.getString("cdhd_id");
			String ddd_no		= data.getString("ddd_no");
			String hp_ddd_no 	= data.getString("hp_ddd_no");
			String hp_tel_hno 	= data.getString("hp_tel_hno");
			String hp_tel_sno 	= data.getString("hp_tel_sno");
			String cp_bcd 		= data.getString("cp_bcd");
			
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			
			String sql = this.getAplIns(); 
			pstmt = conn.prepareStatement(sql.toString());
			int idx = 0;
			pstmt.setString(++idx,pgrs_yn);
			pstmt.setString(++idx,cdhd_id);
			pstmt.setString(++idx,ddd_no);
			pstmt.setString(++idx,hp_ddd_no);
			pstmt.setString(++idx,hp_tel_hno);
			pstmt.setString(++idx,hp_tel_sno);
			pstmt.setString(++idx,cp_bcd);
			result = pstmt.executeUpdate();
			
			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}

			
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}
	/** ***********************************************************************
    * ��û���� ��
    ************************************************************************ */
	public String getAplCnt(){
		 StringBuffer sql = new StringBuffer();
		 sql.append("\n	SELECT CO_NM, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, DDD_NO FROM BCDBA.TBGAPLCMGMT WHERE GOLF_SVC_APLC_CLSS='0011' AND CDHD_ID=?	");
		 return sql.toString();
	}

	/** ***********************************************************************
    * ��û���� ����
    ************************************************************************ */
	public String getAplIns(){
		 StringBuffer sql = new StringBuffer();
		 sql.append("\n	INSERT INTO BCDBA.TBGAPLCMGMT (	");
		 sql.append("\n		APLC_SEQ_NO, GOLF_SVC_APLC_CLSS, PGRS_YN, CDHD_ID, DDD_NO	");
		 sql.append("\n		, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, REG_ATON, CO_NM	");
		 sql.append("\n	) VALUES (	");
		 sql.append("\n		(SELECT MAX(APLC_SEQ_NO)+1 FROM BCDBA.TBGAPLCMGMT), '0011', ?, ?, ?	");
		 sql.append("\n		, ?, ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), ?	");
		 sql.append("\n	)	");
		 return sql.toString();
	}

}

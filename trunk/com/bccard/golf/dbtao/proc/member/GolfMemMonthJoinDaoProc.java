/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemMonthJoinDaoProc
*   �ۼ���    : (��)�̵������ �̰���
*   ����      : ���� > ��ȸ�� ���� ����
*   �������  : golf 
*   �ۼ�����  : 20110622
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.member;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;

public class GolfMemMonthJoinDaoProc extends AbstractProc {
	

	public static final String TITLE = "��ȸ��(����Ʈ���) ����ó��";

	public GolfMemMonthJoinDaoProc() {}
		
	
	/**
	 *	<pre>
	 * 	<li> ��ȸ��(����Ʈ���) ����ó��
	 * 	</pre>
	 *  @return String returnGrd
	 */		
	public int execute(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {
		
		int result = 0;
		
		String sql = "";
		Connection conn = null;
		ResultSet rs = null;		
		PreparedStatement pstmt = null;
		
		try {
			
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			String memId				= userEtt.getAccount();			
			String socId				= userEtt.getSocid();
				
			String moneyType 			= data.getString("moneyType").trim();	
			if(GolfUtil.empty(moneyType)){moneyType = "4";}	
			
			String cdhd_sq2_ctgo = GolfUtil.lpad(moneyType+"", 4, "0");// ���� ���� ��û�� ���
			String cdhd_ctgo_seq_no = "";
			String sece_yn = ""; // Y:Żȸȸ��, N:�α��ΰ���ȸ��
			String newMemYn = "N"; // �ű�ȸ�� ����
			
			debug("GolfMemMonthJoinDaoProc / moneyType : " + moneyType);		
			
			if(!"".equals(cdhd_sq2_ctgo) && cdhd_sq2_ctgo != null)
			{						
				//��û�� ����� ����ȸ���з����� ���̺� �����ϴ��� Ȯ�� �� �ڵ� ��ȯ
				sql = this.getMemberLevelQuery();  
	            pstmt = conn.prepareStatement(sql);	        	
	        	pstmt.setString(1, cdhd_sq2_ctgo );
	            rs = pstmt.executeQuery();	
	            
				if(rs.next()){
					cdhd_ctgo_seq_no = rs.getString("CDHD_CTGO_SEQ_NO"); //ȸ�����
				}
				
				if(rs != null) rs.close();
	            if(pstmt != null) pstmt.close();
	            
	            if(!"".equals(cdhd_ctgo_seq_no) && cdhd_ctgo_seq_no != null){
	            
	            	//�̹� ��ϵ� ȸ������ �˾ƺ���. (��ϵǾ� �ִ� ���̵� �ִ��� �˻�);
					sql = this.getMemberedCheckQuery(); 
		            pstmt = conn.prepareStatement(sql);
		        	pstmt.setString(1, socId );
		        	pstmt.setString(2, memId );
		            rs = pstmt.executeQuery();	
		            
					if(rs.next()){// Żȸ Y �̳� Żȸ N ����
						sece_yn = rs.getString("SECE_YN");						
					}else {// �ű�ȸ��
						newMemYn = "Y";
					}
					
					if ( newMemYn.equals("Y")||sece_yn.equals("Y") ){
						
						// �ű԰���or�簡��ó��
						result = newMonthMemJoin(conn, cdhd_ctgo_seq_no, request); 
						
					}else {
						
						//�α��� ������ ȸ�� ó��
						result = exeMonthMember(conn, socId, memId); 
						
					}
					
					if (result==1){
						result = mnInsExecute(conn, memId);
					}
		            
	            }
	            
			}			
			
			if(result > 0) {				
				conn.commit();
				debug("GolfMemMonthJoinDaoProc memId:"+memId+"/��ȸ�� ��� ����");		
			} else {
				conn.rollback();
			}
						
		} catch(Exception e) {
            
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
            
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}			
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
		
	}
	
	
	/**
	 *	<pre>
	 * 	<li> ������ �����ϱ� 
	 * 	</pre>
	 *  @return int intResult
	 */		
	public int mnInsExecute(Connection conn, String memId) throws BaseException {
		
		int idx = 0;
		int result =  0;
		int intResult = 0;
		PreparedStatement pstmt = null;		

		try {
			
			String sql = "";
			String sttl_amt	= AppConfig.getDataCodeProp("monPay");// �����ݾ�
			
            sql = this.getPayMonthQuery();
			pstmt = conn.prepareStatement(sql);        	 
        	pstmt.setString(++idx, AppConfig.getDataCodeProp("monPayHis") );
        	pstmt.setString(++idx, memId );
        	pstmt.setString(++idx, sttl_amt );	
        	
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();

			if(result > 0) {	
				intResult = 1;
			}

		} catch(Exception e) {
			
			try	{
				conn.rollback();
			}catch (Exception c){}
			
	        MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "��ȸ�� �̷� ��� ����" );
	        throw new DbTaoException(msgEtt,e);
	        
		} finally {
	        try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
		}			

		return result;
	}	
	
	
	/**
	 *	<pre>
	 * 	<li> ������ �����ϱ� 
	 * 	</pre>
	 *  @return int intResult
	 */		
	public int getSeq(WaContext context, String memId) throws BaseException {	
		
		int idx = 0;				
		int seq = 0;
		PreparedStatement pstmt = null;
		
		String sql = "";
		Connection conn = null;
		ResultSet rs = null;		

		try {
			
			conn = context.getDbConnection("default", null);

            idx = 0;
			pstmt = conn.prepareStatement(getMonPaySeq());
			pstmt.setString(++idx, memId );			
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				
				seq = rs.getInt("SEQ");
			}			
			
			rs.close();
			

		} catch(Exception e) {
			
			try	{
				conn.rollback();
			}catch (Exception c){}
			
	        MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "��ȸ�� �̷� ��� ����" );
	        throw new DbTaoException(msgEtt,e);
	        
		} finally {
	        try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
	        try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return seq;
	}	
	
	
	/**
	 *	<pre>
	 * 	<li> �ű԰����̳� �簡��ó��
	 * 	</pre>
	 *  @return int intResult
	 */		
	public int newMonthMemJoin(Connection conn, String grd, HttpServletRequest request) throws DbTaoException  {

		int idx = 0;		

		String sql = "";
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;		
		
		ResultSet userInfoRs = null;
		PreparedStatement userInfoPstmt = null;
		
		int resultExecute = 0;
		int intResult = 0;
		
		String joinChnl				= "";	// ����ȸ�����̺� ���԰�α��� �ڵ�
		String cdhd_ctgo_seq_no		= "";	// ȸ���з��Ϸù�ȣ = ��ǥ���
		String email 				= "";	// �̸����ּ�
		String zipcode 				= "";	// �����ȣ
		String zipaddr 				= "";	// �ּ�1
		String detailaddr 			= "";	// �ּ�2
		String mobile 				= "";	// �����
		String phone 				= "";	// ����ȭ		
						
		try {
			
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			String memId				= userEtt.getAccount();
			String memNm				= userEtt.getName();
			String socId 				= userEtt.getSocid();
			String strMemClss			= userEtt.getMemberClss();
			
			sql = this.getUserInfoQuery(strMemClss);  	// ȸ����޹�ȣ 1:���� / 5:����
            userInfoPstmt = conn.prepareStatement(sql);
            userInfoPstmt.setString(1, memId );
            userInfoRs = userInfoPstmt.executeQuery();
            
            //ȸ�� ����
			if(userInfoRs.next()){
				joinChnl 			= AppConfig.getDataCodeProp("monjoinChnl"); 
				cdhd_ctgo_seq_no 	= grd ;
				email				= userInfoRs.getString("EMAIL");		// �̸���
				zipcode				= userInfoRs.getString("ZIPCODE");		// �����ȣ
				zipaddr				= userInfoRs.getString("ZIPADDR");		// �ּ�
				detailaddr			= userInfoRs.getString("DETAILADDR");	// ���ּ�
				mobile				= userInfoRs.getString("MOBILE");		// �ڵ�����ȣ
				phone				= userInfoRs.getString("PHONE");		// ��ȭ��ȣ				
			}
			
			if(userInfoRs != null) userInfoRs.close();
            if(userInfoPstmt != null) userInfoPstmt.close(); 
			
			// �̹� ���Ե� ID���� üũ
			pstmt = conn.prepareStatement(getMemberedCheckQuery());
			pstmt.setString(1, socId );
			pstmt.setString(1, memId );
        	rs = pstmt.executeQuery();
			
            if(!rs.next()){// �ű԰���ó��
		        
				// ����ȸ�����̺� �μ�Ʈ - ��ǥ���, ȸ������, �ֱ��������� ���
            	idx = 0;
            	pstmt2 = conn.prepareStatement(getInsertMemQuery());				
				pstmt2.setString(++idx, memId );
				pstmt2.setString(++idx, memNm );
				pstmt2.setString(++idx, socId );
				pstmt2.setString(++idx, joinChnl );
				pstmt2.setString(++idx, strMemClss );
				pstmt2.setString(++idx, cdhd_ctgo_seq_no );
				pstmt2.setString(++idx, mobile );
				pstmt2.setString(++idx, phone );
				pstmt2.setString(++idx, email );
				pstmt2.setString(++idx, zipcode );
				pstmt2.setString(++idx, zipaddr );
				pstmt2.setString(++idx, detailaddr );		        					        	
	        	resultExecute = pstmt2.executeUpdate();
	        	if(pstmt2 != null) pstmt2.close();	        	
	
	        	info(" �ű�ȸ�� ���̺� �μ�Ʈ ��� :: cdhdId : "+ memId + " | resultExecute : " + resultExecute);
	        	resultExecute = 0;
	            
            }else { //�簡�� ó��
            	
            	// �簡���� ��� ������ ������ ��� �����Ѵ�.
            	idx = 0;
            	pstmt2 = conn.prepareStatement(exeGradeDel());				
				pstmt2.setString(++idx, memId ); 
				pstmt2.executeUpdate();
				
	            if(pstmt2 != null) pstmt2.close();

            	// ����ȸ�����̺� Update	      
	            idx = 0;
	            pstmt2 = conn.prepareStatement(exeReJoin());
	            pstmt2.setString(++idx, joinChnl );
	            pstmt2.setString(++idx, cdhd_ctgo_seq_no );
	            pstmt2.setString(++idx, mobile );
	            pstmt2.setString(++idx, phone );
	            pstmt2.setString(++idx, email );
	            pstmt2.setString(++idx, zipcode );
	            pstmt2.setString(++idx, zipaddr );
	            pstmt2.setString(++idx, detailaddr );
	            pstmt2.setString(++idx, memId );
 	        	resultExecute = pstmt2.executeUpdate();
 	            if(pstmt2 != null) pstmt2.close();
 	            
 	            info(" �簡��ȸ�� ���̺� �μ�Ʈ ��� :: cdhdId : "+ memId + " | resultExecute : " + resultExecute);
 	            resultExecute = 0;
 	            
            }
            
	        if(rs != null) rs.close();
	        if(pstmt != null) pstmt.close();

			/*���� ���̵� ���� ������ �ٽ� ��ϵ��� �ʵ��� ���´�.
            	�űԴ� �ƿ� ����, �簡���� ������ �����Ѵµ� ���� �� �˻�? Ȥ�ó�? */
            idx = 0;
            pstmt = conn.prepareStatement(getChkGradeQuery());				
            pstmt.setString(++idx, memId ); 
            pstmt.setString(++idx, cdhd_ctgo_seq_no );
            rs = pstmt.executeQuery();

			if(!rs.next()){
			    
				// ȸ����� ���̺� ������Ʈ
				idx = 0;
			    pstmt2 = conn.prepareStatement(getInsertGradeQuery());
			    pstmt2.setString(++idx, memId ); 
			    pstmt2.setString(++idx, cdhd_ctgo_seq_no );					
			    resultExecute = pstmt2.executeUpdate();
			    if(pstmt2 != null) pstmt2.close();			    
			    info(" ����ȸ����ް��� ���̺� �μ�Ʈ ��� :: cdhdId : "+ memId + " | resultExecute : " + resultExecute);
	
			}	      
			
	        if(rs != null) rs.close();
	        if(pstmt != null) pstmt.close();				
	        
			if(resultExecute > 0) {
				intResult = 1;
			} 
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ű԰����̳� �簡��ó�� ����" );
            throw new DbTaoException(msgEtt,e);
		} finally {
			
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(userInfoRs != null) userInfoRs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}			
            try { if(pstmt2 != null) pstmt2.close(); } catch (Exception ignored) {}
            try { if(userInfoPstmt != null) userInfoPstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return intResult;
	}	
	
	
	/**
	 *	<pre>
	 * 	<li> �α��� ������ ȸ�� ó��
	 * 	</pre>
	 *  @return int intResult	 
	 * @throws IOException 
	 * @throws NumberFormatException 
	 *  
	 */		
	public int exeMonthMember(Connection con, String socId, String cdhdId) throws TaoException, NumberFormatException, IOException  {

		String title				= "��ȸ��  ȸ������ �˾ƺ���.";

		int grd						= Integer.parseInt(AppConfig.getDataCodeProp("0052CODE11"));// ���ν���Ʈ ���
		int whatGrade				= 0;		// ���		
		int successCnt				= 0;
		
		whatGrade = isMonthMember(con, grd, socId, cdhdId);  
		
		info("�б��ڵ� : " + whatGrade);
		
		if(whatGrade==0){ //��� ���̺�  ���� ��� �������� �ʾ� ��� �־��ش�				
			successCnt = execute_inGrd(con, socId, cdhdId, grd);			
		}else if(whatGrade==8){ // white ȸ���̸� ����Ʈ ������� ���׷��̵�			
			successCnt = execute_upgrade(con, socId, cdhdId, grd);
		}else { 
			/* 
			       ���� ����� �̹� ����; '�̹� ��ȸ���� ���� �Ǽ̽��ϴ�.'��� �޼��� ����;
			       ���� ���  ����� ������ �ڹ� ��ũ��Ʈ���� ������ , Ȥ�� �հ� ��������   �����Ѵ�.
			 */
			successCnt = 0;			
		}			
	
		return successCnt;	 
		
	}	
	
	
	/**
	 *	<pre>
	 * 	<li> ��ȸ�� ��� ������� Ȯ�� 
	 * 	</pre>
	 *  @return String returnGrd
	 */		
	private  int isMonthMember(Connection con, int grd, String socId, String cdhdId) throws DbTaoException  {
		
		String title				= "��ȸ�� ��� ������� Ȯ�� : isMonthMember()";
		
		String sql 					= "";
		int returnGrd				= 0;  
		ResultSet rs 				= null;		
		PreparedStatement pstmt		= null;
		
		try {
            
            //����ȸ����ް������̺� ���� ����� �����ϴ���
			sql = getMonthGrade();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, cdhdId);	
			pstmt.setInt(2, grd);
			rs = pstmt.executeQuery();
				
			if(rs.next()){			
				returnGrd = rs.getInt("CDHD_CTGO_SEQ_NO");				
			}else {
				returnGrd = 0; // ���� ��� ���� �ǹ�				
			}
			
			sql = null;
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();

            //���� ��� ������ ȭ��Ʈ ����� �����ϴ���
            if (returnGrd == 0){		

				sql = getMonthGrade();			
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, cdhdId);	
				pstmt.setString(2, "8");
				rs = pstmt.executeQuery();
					
				if(rs.next()){
					returnGrd = rs.getInt("CDHD_CTGO_SEQ_NO");							
				}
				
            }			

			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
			
		} catch(Exception e) {
			try	{
				con.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
		}
		
		return returnGrd;			
		
	}
	
	
	/**
	 *	<pre>
	 * 	<li>  ��� ���̺�  ���� ��� �������� �ʾ� �űԷ� in
	 * 	</pre>
	 */
	private int execute_inGrd(Connection con, String socId, String cdhdId, int grade) throws TaoException {

		String title				= "��� ��� ó�� �Ϸ�";
		PreparedStatement pstmt		= null;		
		
		int idx = 0;
		int result = 0, intResult =0;
		
		try {
			
			// ��ϵǾ� ���� �ʴٸ�  �μ�Ʈ ���ش�.
            /**Insert************************************************************************/
			pstmt = con.prepareStatement(getInsertGradeQuery());
        	pstmt.setString(++idx, cdhdId ); 
        	pstmt.setInt(++idx, grade );
        	result = pstmt.executeUpdate();
        	
            //��ǥ ��� ����
			topGradeChange(con, cdhdId, grade, "1");
            
	        info(" || cdhdId : "+ cdhdId + " | �űԷ� in ; ��ȸ�� ȸ�� ��� ó�� �Ϸ� ");
	        
			if(result > 0) {
				intResult = 1;
			} 
			
		} catch(Exception e) {
			try	{
				con.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {			
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
		}
		
		return result;
		
	}	
	
	
	/**
	 *	<pre>
	 * 	<li> ��ǥ ��� ������Ʈ
	 * 	</pre>
	 * @throws DbTaoException 
	 */
	private void topGradeChange(Connection con, String cdhdId, int grade, String gubun) throws DbTaoException {
	
		String title				= "��ǥ ��� ������Ʈ";		
		ResultSet rs 				= null;
		PreparedStatement pstmt		= null;		
		PreparedStatement pstmt2		= null;
		
		int idx = 0;
		String joinChnl				= "";	// ����ȸ�����̺� ���԰�α��� �ڵ�
		
		try {
        	
        	// ���� �ڱ��޺��� ���� ����� �ִ��� �˾ƺ���.
            idx = 0;
			pstmt = con.prepareStatement(getGrdChgYN());
			pstmt.setInt(++idx, grade);	
			pstmt.setString(++idx, cdhdId);	
			rs = pstmt.executeQuery();
			
			idx = 0;
			
			if(rs.next()){
				
				joinChnl 			= AppConfig.getDataCodeProp("monjoinChnl");
				
				if("Y".equals(rs.getString("CHG_YN"))){
					//��ȸ�� ��� - ����ȸ�� ���̺� ������Ʈ
					pstmt2 = con.prepareStatement(exeUpdTopGrade(gubun));
					pstmt2.setInt(++idx, grade);
					if (gubun.equals("2")){
						pstmt2.setString(++idx, joinChnl);
					}
					pstmt2.setString(++idx, cdhdId);	
					pstmt2.executeUpdate();		
			        info(" || cdhdId : "+ cdhdId + " | ��ǥ��� ["+grade +"]���� ����");
				}					
				//N �϶� ���԰�� ������Ʈ ���Ѵ�.
			}	    	
			
		} catch(Exception e) {
			try	{
				con.rollback();
			}catch (Exception c){}
			
	        MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "�ý��ۿ����Դϴ�." );
	        throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
	        try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}            
	        try { if(pstmt2 != null) pstmt2.close(); } catch (Exception ignored) {}
		}
		
	}
	

	/**
	 *	<pre>
	 * 	<li> White -> ��ȸ�� ������� ������Ʈ
	 * 	</pre>
	 *  @return int ��� cnt
	 */
	private int execute_upgrade(Connection con, String socId, String cdhdId, int grade) throws TaoException {

		String title				= "White -> ��ȸ�� ������� ������Ʈ";
		PreparedStatement pstmt		= null;		
		
		int idx = 0;
		int result = 0, intResult = 0;
		
		try {
			
			//������ �� ��� �����丮�� ���
			idx = 0;
			pstmt = con.prepareStatement(inGrdHistoryQuery());		
        	pstmt.setString(++idx, cdhdId );
        	pstmt.executeUpdate();			
            if(pstmt != null) pstmt.close();        	
  
    		//���  ���̺� ���׷��̵�				
			idx = 0;
			pstmt = con.prepareStatement(exeUpdGrd());
        	pstmt.setInt(++idx, grade );
        	pstmt.setString(++idx, cdhdId );
        	result = pstmt.executeUpdate();	
            if(pstmt != null) pstmt.close();
         	
            //��ǥ ��� ����
            topGradeChange(con, cdhdId, grade, "2");
            
			if(result > 0) {
				intResult = 1;
			} 
	        
	        info(" || cdhdId : "+ cdhdId + " | White -> ��ȸ��(����Ʈ)������� ������Ʈ �Ϸ� ");
			
		} catch(Exception e) {
			try	{
				con.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {			
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
		}
		
		return result;
		
	}	
						

	/**
	 *	<pre>
	 * 	<li> ȸ�� �з� ���� �������� - TBGGOLFCDHDCTGOMGMT 
	 * 	</pre>
	 *  @return String ����
	 */		
	private String getMemberLevelQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  SELECT CDHD_CTGO_SEQ_NO FROM				\n");
		sql.append("\t    BCDBA.TBGGOLFCDHDCTGOMGMT					\n");
		sql.append("\t    WHERE CDHD_SQ2_CTGO=?						\n");
		return sql.toString();
	}
	

	/**
	 *	<pre>
	 * 	<li> �����ϵ� ���̵����� �˾ƺ��� 
	 * 	</pre>
	 *  @return String ����
	 */		
	private String getMemberedCheckQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  SELECT CDHD_ID, NVL(SECE_YN,'N') AS SECE_YN		\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD							\n");
		sql.append("\t  WHERE JUMIN_NO=? AND CDHD_ID=?					\n");
		return sql.toString();
	}
		
	
	/**
	 *	<pre>
	 * 	<li> ȸ������ ��������    strMemClss // ȸ����޹�ȣ 1:���� / 5:����
	 * 	</pre>
	 *  @return String ����
	 */			
	private String getUserInfoQuery(String strMemClss){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		
		if("1".equals(strMemClss)){
			
			sql.append("\t  SELECT EMAIL1 EMAIL, ZIPCODE, ZIPADDR, DETAILADDR, MOBILE, PHONE	\n");
			sql.append("\t  FROM BCDBA.UCUSRINFO	\n");
			sql.append("\t  WHERE ACCOUNT = ?	\n");
			
		}else{					

			sql.append("\t  SELECT CMEM.USER_EMAIL EMAIL, CMEM.USER_MOB_NO MOBILE, CMEM.USER_TEL_NO PHONE	\n");
			sql.append("\t  , NMEM.ZIPCODE, NMEM.ZIPADDR, NMEM.DETAILADDR	\n");
			sql.append("\t  FROM BCDBA.TBENTPUSER CMEM	\n");
			sql.append("\t  LEFT JOIN BCDBA.UCUSRINFO NMEM ON CMEM.ACCOUNT=NMEM.ACCOUNT	\n");
			sql.append("\t  WHERE CMEM.ACCOUNT=?	\n");
			
		}
		
		return sql.toString();
	}	
	

	/**
	 *	<pre>
	 * 	<li> ����ȸ�������� �μ�Ʈ - TBGGOLFCDHD    
	 * 	</pre>
	 *  @return String ����
	 */		
    private String getInsertMemQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  INSERT INTO BCDBA.TBGGOLFCDHD (	\n");
		sql.append("\t  	CDHD_ID, HG_NM, JUMIN_NO, JOIN_CHNL	\n");
		sql.append("\t  	, JONN_ATON, EMAIL_RECP_YN, SMS_RECP_YN	\n");
		sql.append("\t  	, CBMO_ACM_TOT_AMT, CBMO_DDUC_TOT_AMT , MEMBER_CLSS	\n");
		sql.append("\t  	, CDHD_CTGO_SEQ_NO, MOBILE, PHONE, EMAIL, ZIP_CODE, ZIPADDR, DETAILADDR, LASTACCESS	\n");
		sql.append("\t  	, ACRG_CDHD_JONN_DATE, ACRG_CDHD_END_DATE	\n");
		sql.append("\t  ) VALUES (	\n");
		sql.append("\t  	?, ?, ?, ?	\n");
		sql.append("\t  	, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), 'Y', 'Y'	\n");
		sql.append("\t  	, 0, 0, ?	\n");
		sql.append("\t  	, ?, ?, ?, ?, ?, ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");		
		sql.append("\t  )	\n");
        return sql.toString();
    }	
    
    
 	/**
 	 *	<pre>
 	 * 	<li> ����ȸ����� ����
 	 * 	</pre>
 	 *  @return String ����
 	 */     
	private String exeGradeDel(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t  DELETE FROM BCDBA.TBGGOLFCDHDGRDMGMT WHERE CDHD_ID=?	\n");
		return sql.toString();
	}    
	
	
 	/**
 	 *	<pre>
 	 * 	<li>  ����ȸ�������� ������Ʈ - TBGGOLFCDHD => �簡��  
 	 * 	</pre>
 	 *  @return String ����
 	 */ 
     private String exeReJoin(){
         StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD SET CBMO_ACM_TOT_AMT='0', CBMO_DDUC_TOT_AMT='0'	\n");
 		sql.append("\t  , ACRG_CDHD_JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD'), ACRG_CDHD_END_DATE=TO_CHAR(SYSDATE+365,'YYYYMMDD')			\n");	// ����ȸ�� �Է�
 		sql.append("\t  , SECE_YN='N', SECE_ATON='', JONN_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')		\n");
 		sql.append("\t  , CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')		\n");
 		sql.append("\t  , EMAIL_RECP_YN='Y', SMS_RECP_YN='Y', JOIN_CHNL= ?		\n");
 		sql.append("\t  , CDHD_CTGO_SEQ_NO=?, MOBILE=?, PHONE=?, EMAIL=?, ZIP_CODE=?	\n");
 		sql.append("\t  , ZIPADDR=?, DETAILADDR=?, LASTACCESS=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
 		sql.append("\t  WHERE CDHD_ID=?		\n");
 		
         return sql.toString();
     }	

     
 	/**
 	 *	<pre>
 	 * 	<li>  ���� ����� ��ϵǾ� �ִ��� Ȯ��    
 	 * 	</pre>
 	 *  @return String ����
 	 */      
	private String getChkGradeQuery(){
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("	\n");
		sql.append("\t  SELECT CDHD_GRD_SEQ_NO FROM BCDBA.TBGGOLFCDHDGRDMGMT WHERE CDHD_ID=? AND CDHD_CTGO_SEQ_NO=? \n");
		
		return sql.toString();
		
	}
      
     
	/**
	 *	<pre>
	 * 	<li>  ����ȸ����ް��� �μ�Ʈ - TBGGOLFCDHDGRDMGMT
	 * 	</pre>
	 *  @return String ����
	 */       
	private String getInsertGradeQuery(){
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("	\n");
		sql.append("\t  INSERT INTO BCDBA.TBGGOLFCDHDGRDMGMT (	\n");
		sql.append("\t  	CDHD_GRD_SEQ_NO, CDHD_ID, CDHD_CTGO_SEQ_NO, REG_ATON	\n");
		sql.append("\t  ) VALUES (	\n");
		sql.append("\t  	(SELECT MAX(NVL(CDHD_GRD_SEQ_NO,0))+1 FROM BCDBA.TBGGOLFCDHDGRDMGMT), ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
		sql.append("\t  )	\n");
		
		return sql.toString();
		
	}        
       
       
	/**
 	 *	<pre>
	 * 	<li> ����ȸ����ް������̺� ���� ����� �����ϴ���
 	 * 	</pre>
	 *  @return String ����
	 */
   	private String getMonthGrade(){
   		
   		StringBuffer sql = new StringBuffer();

   		sql.append("	\n");
   		sql.append("\t	SELECT GRD.CDHD_CTGO_SEQ_NO	\n");
   		sql.append("\t	FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD		\n");
   		sql.append("\t	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT CTG ON GRD.CDHD_CTGO_SEQ_NO=CTG.CDHD_CTGO_SEQ_NO	\n");
   		sql.append("\t	WHERE CDHD_ID = ? AND CTG.CDHD_CTGO_SEQ_NO = ?	\n");
   		
   		return sql.toString();
   	
   	}       
   	
   	
    /**
   	 *	<pre>
  	 * 	<li>  ��ǥ��� ���濩�� ���
   	 * 	</pre>
  	 *  @return String ����
  	 */   	
  	private String getGrdChgYN(){
  		
  		StringBuffer sql = new StringBuffer();
  		
  		sql.append("	\n");
		sql.append("\t  SELECT (CASE WHEN T_CTGO.SORT_SEQ>(SELECT SORT_SEQ FROM BCDBA.TBGGOLFCDHDCTGOMGMT WHERE CDHD_CTGO_SEQ_NO=?) THEN 'Y' ELSE 'N' END) CHG_YN	\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD T_CDHD	\n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T_CTGO ON T_CDHD.CDHD_CTGO_SEQ_NO=T_CTGO.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t  WHERE CDHD_ID=?	\n");
		
  		return sql.toString();
  		
  	}   	
  	

    /**
   	 *	<pre>
  	 * 	<li>  ��ǥ��� ������Ʈ
   	 * 	</pre>
  	 *  @return String ����
  	 */     	
	private String exeUpdTopGrade(String gubun){
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("	\n");
		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD	\n");
		sql.append("\t  SET CDHD_CTGO_SEQ_NO = ?	\n");	
		if (gubun.equals("2")){
			sql.append("\t  , JOIN_CHNL = ?	\n");
		}
		sql.append("\t	WHERE CDHD_ID = ?	\n");
		
		return sql.toString();
		
	}
	
	
 	/**
  	 *	<pre>
 	 * 	<li> ��� �����丮 ���̺� �μ�Ʈ    
  	 * 	</pre>
 	 *  @return String ����
 	 */	
 	private String inGrdHistoryQuery(){
 		
 		StringBuffer sql = new StringBuffer();
 		
 		sql.append("	\n");
 		sql.append("\t  INSERT INTO BCDBA.TBGCDHDGRDCHNGHST	\n");
 		sql.append("\t  SELECT (SELECT MAX(NVL(SEQ_NO,0))+1 FROM BCDBA.TBGCDHDGRDCHNGHST)	\n");
 		sql.append("\t  , GRD.CDHD_GRD_SEQ_NO, GRD.CDHD_ID, GRD.CDHD_CTGO_SEQ_NO, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
 		sql.append("\n	, B.ACRG_CDHD_JONN_DATE , B.ACRG_CDHD_END_DATE , B.JOIN_CHNL	");
 		sql.append("\t  FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD	\n");
 		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT GRDM ON GRD.CDHD_CTGO_SEQ_NO=GRDM.CDHD_CTGO_SEQ_NO	\n");
 		sql.append("\t  JOIN BCDBA.TBGGOLFCDHD B ON GRD.CDHD_ID=B.CDHD_ID	\n");
 		sql.append("\t  WHERE GRD.CDHD_ID=? AND GRDM.CDHD_SQ1_CTGO='0002'	\n");
 		
 		return sql.toString(); 		
 		
 	}	 	
 	
    
    /**
   	 *	<pre>
  	 * 	<li> ȭ��Ʈ�� ����Ʈ�� ������Ʈ�ÿ��� REG_ATON�� ������Ʈ �Ѵ�
  	 * 	<li> REG_ATON�� �ſ� �ڵ������� ���� �����Ͱ� �ǹǷ� �� �ִ´�
  	 *  <li> ���� ����� ����ô� REG_ATON(�����)�� ������Ʈ ���ϰ�, CHNG_ATON(������)�� ������Ʈ ��
  	 *  <li> ���� ����Ʈ�� �� �� �����ϸ� �����Ҷ�����, ���� ���� �̹Ƿ� �ǹ̴� ������ ���� ����Ʈ���� �ϰ����� ���� REG_ATON�� ������Ʈ
   	 * 	</pre>
  	 *  @return String ����
  	 */     
  	private String exeUpdGrd(){
  		
  		StringBuffer sql = new StringBuffer();
  		
  		sql.append("	\n");
  		sql.append("\t  UPDATE BCDBA.TBGGOLFCDHDGRDMGMT	\n");
  		sql.append("\t  SET CDHD_CTGO_SEQ_NO=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), 	\n");
  		sql.append("\t  REG_ATON = TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), CHNG_RSON_CTNT = NULL		\n");
  		sql.append("\t  WHERE CDHD_GRD_SEQ_NO=(	\n");
  		sql.append("\t      SELECT GRD.CDHD_GRD_SEQ_NO	\n");
  		sql.append("\t      FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD	\n");
  		sql.append("\t      JOIN BCDBA.TBGGOLFCDHDCTGOMGMT GRDM ON GRD.CDHD_CTGO_SEQ_NO=GRDM.CDHD_CTGO_SEQ_NO	\n");
  		sql.append("\t      WHERE GRD.CDHD_ID=? AND GRDM.CDHD_SQ1_CTGO='0002' AND GRDM.CDHD_CTGO_SEQ_NO = '8'	\n");
  		sql.append("\t  )	\n");
	
  		return sql.toString();
  	} 
  	
  	
 	/** ***********************************************************************
	* ������ ����ϱ�
	************************************************************************ */
  	
    /**
   	 *	<pre>
  	 * 	<li> ������ ����ϱ�
   	 * 	</pre>
  	 *  @return String ����
  	 */      	
	private String getPayMonthQuery(){
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("	\n");			
		sql.append("\t  INSERT INTO BCDBA.TBGAPLCMGMT (APLC_SEQ_NO, GOLF_LESN_RSVT_NO	\n");
		sql.append("\t  , GOLF_SVC_APLC_CLSS, PGRS_YN, CDHD_ID, PU_DATE, CHNG_ATON, REG_ATON, STTL_AMT, RSVT_CDHD_GRD_SEQ_NO)	\n");
		sql.append("\t  (SELECT MAX(APLC_SEQ_NO)+1, 1, ?, 'Y', ?, TO_CHAR(ADD_MONTHS(SYSDATE,1),'YYYYMMDD')	\n");
		sql.append("\t  , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),? , 22	\n");
		sql.append("\t	FROM BCDBA.TBGAPLCMGMT)	\n");
		
		return sql.toString();
		
	}  	
	

    /**
   	 *	<pre>
  	 * 	<li>  ������ ��� ������ �������� 
   	 * 	</pre>
  	 *  @return String ����
  	 */   	
  	private String getMonPaySeq(){
  		
  		StringBuffer sql = new StringBuffer();
  		
  		sql.append("	\n");
		sql.append("\t  SELECT MAX(APLC_SEQ_NO)SEQ FROM BCDBA.TBGAPLCMGMT \n");
		sql.append("\t  WHERE GOLF_SVC_APLC_CLSS = '1004'	\n");
		sql.append("\t  AND CDHD_ID=?	\n");
		
  		return sql.toString();
  		
  	}   	
      
}

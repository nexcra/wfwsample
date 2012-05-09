/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemSkiInsDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��Ű�Ǳ� ���ó��
*   �������  : golf 
*   �ۼ�����  : 2009-12-01
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfMemSkiInsDaoProc extends AbstractProc {

	public static final String TITLE = "��Ű�Ǳ�ó��";

	public GolfMemSkiInsDaoProc() {}
	
	/** ***********************************************************************
	* �˾ƺ���    
	*********************************************************************** */
	public int executeSky(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {
		
		int result = 0;
		int idx = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		ResultSet rs4 = null;
		PreparedStatement userInfoPstmt = null;
		ResultSet userInfoRs = null;
				
		try {
			
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String payType 				= data.getString("payType").trim();		// 1:ī�� 2:ī��+����Ʈ
			String moneyType 			= data.getString("moneyType").trim();	// 1:è�ǿ�(200,000) 2:���(50,000) 3:���(25,000)
			if(GolfUtil.empty(moneyType)){moneyType = "4";}
			String memType 				= data.getString("memType").trim();		// ȸ������ - ��ȸ�� : 1 ��ȸ��:2
			String memId				= userEtt.getAccount();
			String memNm				= userEtt.getName();
			String socId				= userEtt.getSocid();
			String insType				= data.getString("insType").trim();		// ���԰�� - TM : 1 �Ϲ� : ""
			//String code					= data.getString("CODE_NO");	//����ó�ڵ�
			String code					= "SKI";	//����ó�ڵ�
			String join_chnl			= data.getString("JOIN_CHNL");	
			
			String strMemClss		= userEtt.getStrMemChkNum();		// ȸ��Clss  2009.10.30 �߰� .getStrMemChkNum()
			
			//�α������� ���
			int intUsrGrad 				= data.getInt("intUsrGrad");	

			// ȸ������
			String memEmail				= "";	// �̸���
			String memZipCode			= "";	// �����ȣ
			String memZipAddr			= "";	// �ּ�
			String memDetailAddr		= "";	// ���ּ�
			String memMobile			= "";	// �ڵ�����ȣ
			String memPhone				= "";	// ��ȭ��ȣ
						
			System.out.print("## GolfMemSkiInsDaoProc | memId : "+memId+" | strMemClss : "+strMemClss+"\n");
			if(!"5".equals(strMemClss)) strMemClss ="1";

			String cdhd_SQ1_CTGO		= "0002";				// cdhd_SQ1_CTGO -> 0001:����ī��� 0002:�������
			String cdhd_SQ2_CTGO		= "000" + moneyType;	// cdhd_SQ2_CTGO -> 0001:è�ǿ� 0002:��� 0003:��� 0004:�Ϲ�
			
			String cdhd_CTGO_SEQ_NO = "";
			String sece_yn = "";
			
			debug("GolfMemSkiInsDaoProc =============== payType => " + payType);
			debug("GolfMemSkiInsDaoProc =============== moneyType => " + moneyType);
			debug("GolfMemSkiInsDaoProc =============== insType => " + insType);
								            
			sql = this.getMemberLevelQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, cdhd_SQ1_CTGO );
        	pstmt.setString(2, cdhd_SQ2_CTGO );
            rs = pstmt.executeQuery();	
			if(rs.next()){
				cdhd_CTGO_SEQ_NO = rs.getString("CDHD_CTGO_SEQ_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
			
			debug("===GolfMemSkiInsDaoProc=======01. �̹� ��ϵ� ȸ������ �˾ƺ���. (��ϵǾ� �ִ� ���̵� �ִ��� �˻�)======");
			sql = this.getMemberedCheckQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, socId );
            rs = pstmt.executeQuery();	
			if(!rs.next()){
				
				// 2009.12.14 �߰� : ���� ������ �����ͼ� �־��ش�.
				sql = this.getUserInfoQuery(strMemClss);  	// ȸ����޹�ȣ 1:���� / 5:����
	            userInfoPstmt = conn.prepareStatement(sql);
	            userInfoPstmt.setString(1, memId );
	            userInfoRs = userInfoPstmt.executeQuery();	
				if(userInfoRs.next()){
					memEmail			= userInfoRs.getString("EMAIL");	// �̸���
					memZipCode			= userInfoRs.getString("ZIPCODE");	// �����ȣ
					memZipAddr			= userInfoRs.getString("ZIPADDR");	// �ּ�
					memDetailAddr		= userInfoRs.getString("DETAILADDR");	// ���ּ�
					memMobile			= userInfoRs.getString("MOBILE");	// �ڵ�����ȣ
					memPhone			= userInfoRs.getString("PHONE");	// ��ȭ��ȣ
				}
				if(userInfoRs != null) userInfoRs.close();
	            if(userInfoPstmt != null) userInfoPstmt.close();
				
				debug("===GolfMemSkiInsDaoProc=======00. ���ο� ȸ��============================");
				debug("===GolfMemSkiInsDaoProc=======140. ����ȸ�������� �μ�Ʈ - TBGGOLFCDHD ");	            
	            sql = this.getInsertMemQuery(moneyType);
				pstmt = conn.prepareStatement(sql);
				
	        	pstmt.setString(++idx, memId ); 
	        	pstmt.setString(++idx, memNm );
	        	pstmt.setString(++idx, socId );
				pstmt.setString(++idx, join_chnl );
				
				pstmt.setString(++idx, code ); //����ó�ڵ� �Է½� ����
				pstmt.setString(++idx, strMemClss ); 	// ȸ����޹�ȣ 1:���� / 5:����   2009.10.30 �߰�
				
				pstmt.setString(++idx, cdhd_CTGO_SEQ_NO );
				pstmt.setString(++idx, memMobile );
				pstmt.setString(++idx, memPhone );
				pstmt.setString(++idx, memEmail );
				pstmt.setString(++idx, memZipCode );
				pstmt.setString(++idx, memZipAddr );
				pstmt.setString(++idx, memDetailAddr );
				
	        	
				result = pstmt.executeUpdate();
	            if(pstmt != null) pstmt.close();
	  
				debug("===GolfMemSkiInsDaoProc=======152. ����ȸ����ް��� �μ�Ʈ - TBGGOLFCDHDGRDMGMT");
	            // 2009-07-29 ���� ���̵� ���� ������ �ٽ� ��ϵ��� �ʵ��� ���´�.
				sql = this.getChkGradeQuery(); 
	            pstmt = conn.prepareStatement(sql);
	        	pstmt.setString(1, memId ); 
	        	pstmt.setString(2, cdhd_CTGO_SEQ_NO );
	            rs = pstmt.executeQuery();	
				if(!rs.next() && !cdhd_CTGO_SEQ_NO.equals("0")){
	            
		            /**SEQ_NO ��������**************************************************************/
					sql = this.getNextValQuery(); 
		            pstmt = conn.prepareStatement(sql);
		            rs = pstmt.executeQuery();			
					long max_seq_no = 0L;
					if(rs.next()){
						max_seq_no = rs.getLong("SEQ_NO");
					}
					if(rs != null) rs.close();
		            if(pstmt != null) pstmt.close();
		            
		            /**Insert************************************************************************/
	
		            sql = this.getInsertGradeQuery();
					pstmt = conn.prepareStatement(sql);
					
					idx = 0;
		        	pstmt.setLong(++idx, max_seq_no ); 
		        	pstmt.setString(++idx, memId ); 
		        	pstmt.setString(++idx, cdhd_CTGO_SEQ_NO );
		        	
					result = pstmt.executeUpdate();
		            if(pstmt != null) pstmt.close();
		            
				}
				if(rs != null) rs.close();
	            if(pstmt != null) pstmt.close();
            
			
	            
			}else{
				debug("===GolfMemSkiInsDaoProc=======��ϵ� ���̵� �ִ�============================");

				sece_yn = rs.getString("SECE_YN");
				
				if(sece_yn.equals("Y")){
					debug("===GolfMemSkiInsDaoProc=======���� Ż��� ó���Ǿ��ִ�. => �簡�� ȸ��==================================");										
					debug("===GolfMemSkiInsDaoProc=======�簡�� ȸ�� : ����ȸ�������� ������Ʈ - ����ȸ������ ��¥ ������ - TBGGOLFCDHD ");
		            
		            sql = this.getReInsertMemQuery(moneyType);
					pstmt = conn.prepareStatement(sql);	
					pstmt.setString(++idx, join_chnl );
		        	pstmt.setString(++idx, code );
					pstmt.setString(++idx, memId ); 
		        	
					result = pstmt.executeUpdate();
		            if(pstmt != null) pstmt.close();

		            if(intUsrGrad == 3 || intUsrGrad ==4 ){
						debug("===GolfMemSkiInsDaoProc=======�簡�� ȸ�� : ������̺� - ȸ���з��Ϸù�ȣ ������Ʈ");
			            sql = this.getMemberGradeUpdateQuery();
						pstmt = conn.prepareStatement(sql);
						
						idx = 0;
			        	pstmt.setString(++idx, cdhd_CTGO_SEQ_NO );
			        	pstmt.setString(++idx, memId ); 
			        	
						result = pstmt.executeUpdate();
			            if(pstmt != null) pstmt.close();
		            }
					
				}else{
					sql = this.getMeCheckQuery(); 
		            pstmt = conn.prepareStatement(sql);
		        	pstmt.setString(1, memId );
		            rs4 = pstmt.executeQuery();	
		            int intchks = 0;
		            
		            
		            if(!rs4.next()){
		            	
		            	intchks = Integer.parseInt(rs4.getString("CDHD_CTGO_SEQ_NO")); //9,10:����ī��ȸ��
		            	debug("intchks : " + intchks);
		            	if(intchks > 8)
		            	{
		            		debug("ī�� ȸ�� ����");
		            		
		            		
		            		// 2009-07-29 ���� ���̵� ���� ������ �ٽ� ��ϵ��� �ʵ��� ���´�.
		        			sql = this.getChkGradeQuery(); 
		                    pstmt = conn.prepareStatement(sql);
		                	pstmt.setString(1, memId ); 
		                	pstmt.setString(2, cdhd_CTGO_SEQ_NO );
		                    rs = pstmt.executeQuery();	
		        			if(!rs.next() && !cdhd_CTGO_SEQ_NO.equals("0"))
		        			{
		                    
		                    
		        	            //����߰�
		        	            /**SEQ_NO ��������**************************************************************/
		        				sql = this.getNextValQuery(); 
		        	            pstmt = conn.prepareStatement(sql);
		        	            rs = pstmt.executeQuery();			
		        				long max_seq_no = 0L;
		        				if(rs.next()){
		        					max_seq_no = rs.getLong("SEQ_NO");
		        				}
		        				if(rs != null) rs.close();
		        	            if(pstmt != null) pstmt.close();
		        	            
		        	            /**Insert************************************************************************/            
		        	            sql = this.getInsertGradeQuery();
		        				pstmt = conn.prepareStatement(sql);			
		        				idx = 0;
		        	        	pstmt.setLong(++idx, max_seq_no ); 
		        	        	pstmt.setString(++idx, memId ); 
		        	        	pstmt.setString(++idx, cdhd_CTGO_SEQ_NO );
		        	        	
		        				result = pstmt.executeUpdate();
		        	            if(pstmt != null) pstmt.close();
		                    
		        			}
		            	}
		            	else //ī��ȸ���� �ƴѰ�
		            	{
		            		debug("�Ϲ� ��� ���׷��̵� ȸ�� ����");
		            		debug("===GolfMemSkiInsDaoProc======= ���� Ż�� �ƴϴ� => ȸ����� ���׷��̵�====================");	
		            		
		            		if(cdhd_CTGO_SEQ_NO.equals("5") || cdhd_CTGO_SEQ_NO.equals("6") || cdhd_CTGO_SEQ_NO.equals("7")){
			            		debug("===GolfMemSkiInsDaoProc======= ���׷��̵�(�Ϲݸ��������ȸ����) : ����ȸ�� ��������, �������� ������Ʈ ");
				
					            sql = this.getMemberUpdateQuery(code);
								pstmt = conn.prepareStatement(sql);

								int iindex = 0;
								if (!"".equals(code)) {
									pstmt.setString(++iindex, join_chnl );
									pstmt.setString(++iindex, code );
								}
								pstmt.setString(++iindex, socId );

								result = pstmt.executeUpdate();
					            if(pstmt != null) pstmt.close();
		            		}
				            
		            		 // ��� ȭ��Ʈ �ΰ�츸 ������Ʈ ó��
		            		if(intUsrGrad == 3 || intUsrGrad ==4 ){
								debug("===GolfMemSkiInsDaoProc======= ���׷��̵� : ȸ���з��Ϸù�ȣ ������Ʈ");
					            sql = this.getMemberGradeUpdateQuery();
								pstmt = conn.prepareStatement(sql);
								
								idx = 0;
					        	pstmt.setString(++idx, cdhd_CTGO_SEQ_NO );
					        	pstmt.setString(++idx, memId ); 
					        	
								result = pstmt.executeUpdate();
		            		}
		            	}
		            }else{
	            		
		            	debug("�Ϲ� ��� ���׷��̵� ȸ�� ����2 | cdhd_CTGO_SEQ_NO: "+cdhd_CTGO_SEQ_NO);
	            		debug("===GolfMemSkiInsDaoProc======= ���� Ż�� �ƴϴ� => ȸ����� ���׷��̵�====================");	
	            		
			            if(cdhd_CTGO_SEQ_NO.equals("5") || cdhd_CTGO_SEQ_NO.equals("6") || cdhd_CTGO_SEQ_NO.equals("7")){
		            		debug("===GolfMemSkiInsDaoProc======= ���׷��̵�(�Ϲݸ��������ȸ����) : ����ȸ�� ��������, �������� ������Ʈ");
			            	
							sql = this.getMemberUpdateQuery(code);
							pstmt = conn.prepareStatement(sql);

							int iindex = 0;
							if (!"".equals(code)) {
								pstmt.setString(++iindex, join_chnl );
								pstmt.setString(++iindex, code );
							}
				        	pstmt.setString(++iindex, socId );
				        	
							result = pstmt.executeUpdate();
				            if(pstmt != null) pstmt.close();
		            	}
			            
			            // ��� ȭ��Ʈ �ΰ�츸 ������Ʈ ó��
			            if(intUsrGrad == 3 || intUsrGrad ==4 ){
							debug("===GolfMemSkiInsDaoProc======= ���׷��̵� : ȸ���з��Ϸù�ȣ ������Ʈ");
				            sql = this.getMemberGradeUpdateQuery();
							pstmt = conn.prepareStatement(sql);
							
							idx = 0;
				        	pstmt.setString(++idx, cdhd_CTGO_SEQ_NO );
				        	pstmt.setString(++idx, memId ); 
				        	
							result = pstmt.executeUpdate();
	            		}
		            }
		            
		            if(pstmt != null) pstmt.close();
				}
				if(rs != null) rs.close();
				if(rs4 != null) rs4.close();
	            if(pstmt != null) pstmt.close();
			}
				
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();			
			
			debug("===GolfMemSkiInsDaoProc=======00. ȸ������ �Ϸ� ===============");

			if(result > 0) {				
				conn.commit();
			} else {
				conn.rollback();
			}
            /*
            debug("===GolfMemSkiInsDaoProc=======01. �̹� ��ϵ� ȸ������ �˾ƺ���. (��ϵǾ� �ִ� ���̵� �ִ��� �˻�)======");
			sql = this.getMemberedCheckQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, socId );
            rs = pstmt.executeQuery();	
			if(!rs.next()){

			}else{
				debug("===GolfMemSkiInsDaoProc=======��ϵ� ���̵� �ִ�============================");

	            sql = this.getMemberUpdateQuery(code);
				pstmt = conn.prepareStatement(sql);	
				pstmt.setString(++idx, join_chnl );
	        	pstmt.setString(++idx, code );
				pstmt.setString(++idx, socId ); 
	        	
				result = pstmt.executeUpdate();
	            if(pstmt != null) pstmt.close();

				debug("===GolfMemSkiInsDaoProc=======�簡�� ȸ�� : ������̺� - ȸ���з��Ϸù�ȣ ������Ʈ");
	            sql = this.getMemberGradeUpdateQuery();
				pstmt = conn.prepareStatement(sql);
				
				idx = 0;
	        	pstmt.setString(++idx, cdhd_CTGO_SEQ_NO );
	        	pstmt.setString(++idx, memId ); 
	        	
				result = pstmt.executeUpdate();

				if(rs != null) rs.close();
	            if(pstmt != null) pstmt.close();
					
			}
				
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();			
			
			debug("===GolfMemSkiInsDaoProc=======00. ��Ű�Ǳ��̺�Ʈó�� �Ϸ� ===============");

			if(result > 0) {				
				conn.commit();
			} else {
				conn.rollback();
			}
			*/
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(rs4 != null) rs4.close(); } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}

	/** ***********************************************************************
	* ��� �˾ƺ���    
	*********************************************************************** */
	public DbTaoResult gradeExecute(WaContext context, TaoDataSet data, HttpServletRequest request) throws BaseException {

		String title = "";
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
		
			String sql = this.getMemGradeQuery(); 
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, data.getString("moneyType").trim());
			rs = pstmt.executeQuery();			
			
			if(rs != null) {
				while(rs.next())  {

					result.addString("memGrade" 	,rs.getString("MEM_GRADE") );
					result.addInt("intMemGrade" 	,rs.getInt("INT_MEM_GRADE") );
					result.addString("RESULT", "00"); //������
					
					debug("MEM_GRADE : " + rs.getString("MEM_GRADE"));
					debug("INT_MEM_GRADE : " + rs.getInt("INT_MEM_GRADE"));
				}
			}else{
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
	* �����ϵ� ���̵����� �˾ƺ���    
	************************************************************************ */
	private String getMemberedCheckQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  SELECT CDHD_ID, NVL(SECE_YN,'N') AS SECE_YN		\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD		\n");
		sql.append("\t  WHERE JUMIN_NO=?				\n");
		return sql.toString();
	}

   	/** ***********************************************************************
    * ȸ�� �з� ���� �������� - TBGGOLFCDHDCTGOMGMT    
    ************************************************************************ */
    private String getMemberLevelQuery(){
      StringBuffer sql = new StringBuffer();
      sql.append("\n");
      sql.append("\t  SELECT CDHD_CTGO_SEQ_NO FROM					\n");
      sql.append("\t    BCDBA.TBGGOLFCDHDCTGOMGMT					\n");
      sql.append("\t    WHERE CDHD_SQ1_CTGO=? AND CDHD_SQ2_CTGO=?	\n");
      return sql.toString();
    }

    /** ***********************************************************************
    * ȸ�� ������Ʈ - ����ȸ������    
    ************************************************************************ */
    private String getMemberUpdateQuery(String code){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD SET												\n");
 		sql.append("\t		ACRG_CDHD_JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD')							\n");
 		sql.append("\t		, ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,12),'YYYYMMDD')			\n");
		if (!"".equals(code)) {
			sql.append("\t      , JOIN_CHNL= ? , AFFI_FIRM_NM= ?		\n");
		}
 		sql.append("\t		WHERE JUMIN_NO=?															\n");
        return sql.toString();
    }

    /** ***********************************************************************
    * ȸ�� ��ް��� ������Ʈ - ����ȸ������    
    ************************************************************************ */
    private String getMemberGradeUpdateQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHDGRDMGMT SET					\n");
 		sql.append("\t		CDHD_CTGO_SEQ_NO=?								\n");
 		sql.append("\t		WHERE CDHD_ID=? and (CDHD_CTGO_SEQ_NO='7' or CDHD_CTGO_SEQ_NO='8')			\n");
        return sql.toString();
    }
 
    /** ***********************************************************************
    * ȸ����� �������� 
    ************************************************************************ */
    private String getMemGradeQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	SELECT GOLF_CMMN_CODE_NM MEM_GRADE, SUBSTR(GOLF_CMMN_CODE,4,1) INT_MEM_GRADE	\n");
		sql.append("\t	FROM BCDBA.TBGCMMNCODE	\n");
		sql.append("\t	WHERE GOLF_CMMN_CLSS='0005' AND SUBSTR(GOLF_CMMN_CODE,4,1)=?	\n");
        return sql.toString();
    }

	/** ***********************************************************************
	* ����ȸ�������� �μ�Ʈ - TBGGOLFCDHD    
	************************************************************************ */
	private String getInsertMemQuery(String moneyType){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  INSERT INTO BCDBA.TBGGOLFCDHD (														\n");
		sql.append("\t  		CDHD_ID, HG_NM, JUMIN_NO													\n");
		sql.append("\t  		, ACRG_CDHD_JONN_DATE, ACRG_CDHD_END_DATE									\n");
		sql.append("\t  		, JONN_ATON, EMAIL_RECP_YN, SMS_RECP_YN, JOIN_CHNL							\n");
		sql.append("\t  		, CBMO_ACM_TOT_AMT, CBMO_DDUC_TOT_AMT										\n");
		sql.append("\t  		, AFFI_FIRM_NM, MEMBER_CLSS													\n");
		sql.append("\t  		, CDHD_CTGO_SEQ_NO, MOBILE, PHONE, EMAIL, ZIP_CODE, ZIPADDR, DETAILADDR, LASTACCESS	\n");
		sql.append("\t  		) VALUES (																	\n");
		sql.append("\t  		?, ?, ?																		\n");
		
		if(moneyType.equals("1") || moneyType.equals("2") || moneyType.equals("3") || moneyType.equals("7")){
			sql.append("\t  		, TO_CHAR(SYSDATE,'YYYYMMDD'), TO_CHAR(ADD_MONTHS(SYSDATE,12),'YYYYMMDD')	\n");
		}else{
			sql.append("\t  		, '', ''																	\n");
		}
		
		sql.append("\t  		, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), 'Y', 'Y', ?							\n");
		sql.append("\t  		, 0, 0																		\n");
		sql.append("\t  		, ?	,?																		\n");
		sql.append("\t  		, ?, ?, ?, ?, ?, ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
		sql.append("\t  		)																			\n");
		return sql.toString();
	}

	/** ***********************************************************************
	 * ���� ����� ��ϵǾ� �ִ��� Ȯ��    
	 ************************************************************************ */
	private String getChkGradeQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("SELECT CDHD_GRD_SEQ_NO FROM BCDBA.TBGGOLFCDHDGRDMGMT WHERE CDHD_ID=? AND CDHD_CTGO_SEQ_NO=? \n");
		return sql.toString();
	}
  
	/** ***********************************************************************
	 * Max IDX Query�� �����Ͽ� �����Ѵ�. = ����ȸ����ް���    
   	*********************************************************************** */
	private String getNextValQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("SELECT NVL(MAX(CDHD_GRD_SEQ_NO),0)+1 SEQ_NO FROM BCDBA.TBGGOLFCDHDGRDMGMT \n");
		return sql.toString();
	}

	/** ***********************************************************************
	 * ����ȸ����ް��� �μ�Ʈ - TBGGOLFCDHDGRDMGMT    
	 ************************************************************************ */
	private String getInsertGradeQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  INSERT INTO BCDBA.TBGGOLFCDHDGRDMGMT (							\n");
		sql.append("\t  		CDHD_GRD_SEQ_NO, CDHD_ID, CDHD_CTGO_SEQ_NO, REG_ATON	\n");
		sql.append("\t  		) VALUES (												\n");
		sql.append("\t  		?, ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')			\n");
		sql.append("\t  		)														\n");
		return sql.toString();
	}

	/** ***********************************************************************
	 * ����ȸ�������� ������Ʈ - TBGGOLFCDHD => �簡��    
	 ************************************************************************ */
	private String getReInsertMemQuery(String moneyType){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t          UPDATE BCDBA.TBGGOLFCDHD SET CBMO_ACM_TOT_AMT='0', CBMO_DDUC_TOT_AMT='0'	\n");

		if(moneyType.equals("1") || moneyType.equals("2") || moneyType.equals("3") || moneyType.equals("7")){
			sql.append("\t          , ACRG_CDHD_JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD')			\n");
			sql.append("\t          , ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,12),'YYYYMMDD')		\n");
		}
	
		sql.append("\t          , SECE_YN='N', SECE_ATON='', JONN_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')		\n");
		sql.append("\t          , CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')		\n");
		sql.append("\t          , EMAIL_RECP_YN='Y', SMS_RECP_YN='Y'		\n");
		sql.append("\t          , JOIN_CHNL= ? , AFFI_FIRM_NM= ?		\n");
		sql.append("\t          WHERE CDHD_ID=?		\n");

		return sql.toString();
	}

	/** ***********************************************************************
	 * �����ϵ� ���̵����� �˾ƺ���    
	 ************************************************************************ */
	private String getMeCheckQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  SELECT T2.CDHD_CTGO_SEQ_NO FROM BCDBA.TBGGOLFCDHD T1 JOIN BCDBA.TBGGOLFCDHDGRDMGMT T2 ON T1.CDHD_ID=T2.CDHD_ID	\n");
		sql.append("\t  WHERE T1.CDHD_ID=?		\n");
		sql.append("\t  ORDER BY T2.CDHD_CTGO_SEQ_NO DESC				\n");
		return sql.toString();
	}

 	/** ***********************************************************************
 	* ȸ�����̺� �����۾� ����
	* ȸ������ ��������    strMemClss // ȸ����޹�ȣ 1:���� / 5:����
	************************************************************************ */
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


}

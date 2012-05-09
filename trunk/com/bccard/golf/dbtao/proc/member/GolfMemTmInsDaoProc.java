/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemTmInsDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ȸ�� > ȸ������ó�� > TM
*   �������  : golf 
*   �ۼ�����  : 2009-07-28
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

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
public class GolfMemTmInsDaoProc extends AbstractProc {

	public static final String TITLE = "ȸ������ó�� > TM";

	public GolfMemTmInsDaoProc() {}
	
	public DbTaoResult execute(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {

		int idx = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		int resultExecute = 0;
		DbTaoResult result =  new DbTaoResult(TITLE);

				
		try {

			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String memId				= userEtt.getAccount();
			String memNm				= userEtt.getName();
			String socId				= userEtt.getSocid();
			String strMemClss			= userEtt.getMemberClss();
			
			int int_tm_grade			= 0;	// �����TM��� - ����ȸ����ޱ����ڵ�
			int int_mem_grade			= 0;	// ȸ����� = ����
			String str_mem_grade		= "";	// ȸ����޸�
			String joinChnl				= "";	// ����ȸ�����̺� ���԰�α��� �ڵ�
			String cdhd_ctgo_seq_no		= "";	// ȸ���з��Ϸù�ȣ = ��ǥ���
			String email1 				= "";	// �̸����ּ�
			String zipcode 				= "";	// �����ȣ
			String zipaddr 				= "";	// �ּ�1
			String detailaddr 			= "";	// �ּ�2
			String mobile 				= "";	// �����
			String phone 				= "";	// ����ȭ
			String member_clss			= strMemClss;	// ���:5 / ��ȸ��:1 / ��ȸ��:4
			
	    	String strStDate = "";		// ����ȸ���������� 
	    	String strEdDate = "";		// ����ȸ����������
	    	int addMonth = 0;			// TM ADD Month 

	    	SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");   
	    	GregorianCalendar cal = new GregorianCalendar();
	    	Date stDate = cal.getTime();
        	Date edDate = new Date();
			            
			sql = this.getMemberTmInfoQuery(strMemClss);  
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, userEtt.getSocid() );
            rs = pstmt.executeQuery();	
            
			if(rs.next()){

				int_tm_grade = rs.getInt("GOLF_CDHD_GRD_CLSS");
				joinChnl = rs.getString("RCRU_PL_CLSS");				
				
				if(int_tm_grade==1){ //���
					int_mem_grade = 3;
					cdhd_ctgo_seq_no = "7";
					str_mem_grade = "Gold";
				}else if(int_tm_grade==2){ //���  
					int_mem_grade = 2;
					cdhd_ctgo_seq_no = "6";
					str_mem_grade = "Blue";					
				}else if(int_tm_grade==3){ //è�Ǿ�
					int_mem_grade = 1;
					cdhd_ctgo_seq_no = "5";
					str_mem_grade = "Champion";					
				}else if(int_tm_grade==4){ //��
					int_mem_grade = 7;
					cdhd_ctgo_seq_no = "11";
					str_mem_grade = "Black";										
				}				
				
				// ī�� TM ȸ�� -> ���, SK ������ �߱� �� �ش�Ǵ� "0103" ���� ������ ����
				// �� �� Gold�� �ش�ǹǷ� ����
				 
				// 2113:Hmall ���3����, 2123:Hmall ���6����, �׿ܴ� 12����				
				if(joinChnl.equals("2113")){
					
			    	addMonth = 3;
		        	cal.add(cal.MONTH, addMonth);
		        	edDate = cal.getTime();		
		        	strStDate = fmt.format(stDate);
		        	strEdDate = fmt.format(edDate);
		        	
				}else if(joinChnl.equals("2123")){
					
			    	addMonth = 6;
		        	cal.add(cal.MONTH, addMonth);
		        	edDate = cal.getTime();		
		        	strStDate = fmt.format(stDate);
		        	strEdDate = fmt.format(edDate);			        	
		        	
				}else{
					
			    	addMonth = 12;
		        	cal.add(cal.MONTH, addMonth);
		        	edDate = cal.getTime();	
		        	strStDate = fmt.format(stDate);
		        	strEdDate = fmt.format(edDate);;	
		        	
				}
				
				if(!GolfUtil.empty(rs.getString("MEMBER_CLSS"))){
					member_clss = rs.getString("MEMBER_CLSS");
				}

				email1 = rs.getString("EMAIL1");
				zipcode = rs.getString("ZIPCODE");
				zipaddr = rs.getString("ZIPADDR");
				detailaddr = rs.getString("DETAILADDR");
				mobile = rs.getString("MOBILE");
				phone = rs.getString("PHONE");
								
				// �̹� ��ϵǾ� �ִ��� �˾ƺ���.
				sql = this.getMemberedCheckQuery(); 
	            pstmt = conn.prepareStatement(sql);
	        	pstmt.setString(1, socId );
	            rs = pstmt.executeQuery();	
				if(!rs.next()){
					// ȸ�����̺� �μ�Ʈ - ��ǥ���, ȸ������, �ֱ���������
		            sql = this.getInsertMemQuery();
					pstmt = conn.prepareStatement(sql);
					idx = 0;
		        	pstmt.setString(++idx, memId );
		        	pstmt.setString(++idx, memNm );
		        	pstmt.setString(++idx, socId );
		        	pstmt.setString(++idx, joinChnl );
	    			pstmt.setString(++idx, strStDate);
	    			pstmt.setString(++idx, strEdDate);
		        	pstmt.setString(++idx, member_clss );
		        	pstmt.setString(++idx, cdhd_ctgo_seq_no );
		        	pstmt.setString(++idx, mobile );
		        	pstmt.setString(++idx, phone );
		        	pstmt.setString(++idx, email1 );
		        	pstmt.setString(++idx, zipcode );
		        	pstmt.setString(++idx, zipaddr );
		        	pstmt.setString(++idx, detailaddr );
		        					        	
		        	resultExecute = pstmt.executeUpdate();
		            if(pstmt != null) pstmt.close();
		            
		            if(resultExecute>0){

						// ȸ����� ���̺� ������Ʈ
						sql = this.getChkGradeQuery(); 
						pstmt = conn.prepareStatement(sql);
						idx = 0;
						pstmt.setString(++idx, memId ); 
						pstmt.setString(++idx, cdhd_ctgo_seq_no );
						rs = pstmt.executeQuery();	
						if(!rs.next()){
						
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
							pstmt.setString(++idx, cdhd_ctgo_seq_no );
							
							resultExecute = pstmt.executeUpdate();
						    if(pstmt != null) pstmt.close();
						}
		            }
		            
	            }else{
	        		// �̹� ��ϵǾ� ���� ��� ������Ʈ �Ѵ�.
		            sql = this.getUpdMemQuery();
					pstmt = conn.prepareStatement(sql);
					idx = 0;
		        	pstmt.setString(++idx, joinChnl );
	    			pstmt.setString(++idx, strStDate);
	    			pstmt.setString(++idx, strEdDate);		        	
		        	pstmt.setString(++idx, cdhd_ctgo_seq_no );
		        	pstmt.setString(++idx, memId );
		        					        	
		        	resultExecute = pstmt.executeUpdate();
		            if(pstmt != null) pstmt.close();
		            
		            if(resultExecute>0){

			            sql = this.getMemberGradeUpdateQuery();
						pstmt = conn.prepareStatement(sql);
						
						idx = 0;
			        	pstmt.setString(++idx, cdhd_ctgo_seq_no );
			        	pstmt.setString(++idx, memId ); 
			        	
			        	resultExecute = pstmt.executeUpdate();
			            if(pstmt != null) pstmt.close();
						    
					}
					
	            }
		        
		        if(resultExecute>0){
					// TM ���̺� ������Ʈ - 2����ȭ����, TM��������ڵ� getUpdTmQuery
		            sql = this.getUpdTmQuery();
					pstmt = conn.prepareStatement(sql);
					idx = 0;
		        	pstmt.setString(++idx, memId );
		        	pstmt.setString(++idx, socId );
		        	pstmt.setInt(++idx, int_tm_grade ); 
		        					        	
		        	resultExecute = pstmt.executeUpdate();
		            if(pstmt != null) pstmt.close();
		            

		            // KT �÷� Ŭ��
		            if(joinChnl.equals("4200")){
		            	
			            sql = this.joinComplete();
						pstmt = conn.prepareStatement(sql);
						idx = 0;
			        	pstmt.setString(++idx, memId );
			        	pstmt.setString(++idx, socId );
			        					        	
			        	resultExecute = pstmt.executeUpdate();
			            if(pstmt != null) pstmt.close();
			            
			            sql = "";		            
			            sql = this.updGift();
						pstmt = conn.prepareStatement(sql);
						idx = 0;
			        	pstmt.setString(++idx, memId );
			        	pstmt.setString(++idx, socId );
			        					        	
			        	resultExecute = pstmt.executeUpdate();
			            if(pstmt != null) pstmt.close();    
			            
			            sql = this.updPayInfo();
						pstmt = conn.prepareStatement(sql);
						idx = 0;
			        	pstmt.setString(++idx, memId );
			        	pstmt.setString(++idx, socId );
			        	pstmt.setInt(++idx, int_mem_grade );
			        					        	
			        	resultExecute = pstmt.executeUpdate();
			            if(pstmt != null) pstmt.close();
			            
		            }		            
		            
		        }
            }
			            
				        
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
						
			
			conn.commit();
			result.addInt("intMemGrade", int_mem_grade);
			result.addInt("addResult", 1);
			result.addString("memGrade", str_mem_grade);
			result.addString("joinChnl", joinChnl);
			
		} catch(Exception e) {
			
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try	{
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	
	// ���ȸ���� ��� ���ñⰣ�� 2�� �÷��ش�.
	public int execute_ibk(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {

		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result =  0;
		int idx = 0;

		String sql 					= "";
		try {
			conn = context.getDbConnection("default", null);
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			conn.setAutoCommit(false);

			String socId				= userEtt.getSocid();
			String memId				= userEtt.getAccount();
			
			// ������� ȸ������ �˾ƺ���.
			sql = getIbkGoldInfoQuery();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, socId);	
			rs = pstmt.executeQuery();
			if(rs != null && rs.next()){
				
				// ���԰�� �����丮 �μ�Ʈ
				sql = getJoinChnlHistoryInsQuery();
				pstmt = conn.prepareStatement(sql);
				idx = 0;
				pstmt.setString(++idx, memId);	
				//result = pstmt.executeUpdate();	�׳� TM���� �Ӵϴ�.
	        	
	        	//if(result>0){

					// ȸ�����̺� - ����Ⱓ 2�� �ø���, (���԰�� ������Ʈ->�׳�TM���� �����Ѵ�.)
	        		sql = getIbkMemUpdQuery();
					pstmt = conn.prepareStatement(sql);
					idx = 0;
					//pstmt.setString(++idx, rs.getString("RCRU_PL_CLSS"));		// TM���� �����Ѵ�.
					pstmt.setString(++idx, memId);	
					result = pstmt.executeUpdate();
						
			        
					// ���� �� �̺�Ʈ ���̺� ������Ʈ
			        if(result>0){
			            sql = this.getUpdIbkGoldEndQuery();
						pstmt = conn.prepareStatement(sql);
						idx = 0;
			        	pstmt.setString(++idx, memId );
			        	pstmt.setString(++idx, socId );
			        					        	
			        	result = pstmt.executeUpdate();
						//debug("���� �� �̺�Ʈ ���̺� ������Ʈ :: resultExecute : " + resultExecute);
			        }
	        	//}
			}

			if(result > 0) {				
				conn.commit();
			} else {
				conn.rollback();
			}

		} catch(Exception e) {
			try	{
				try { if( rs != null ){ rs.close(); } else {} } catch(Throwable ignore) {}
				try { if( pstmt != null ){ pstmt.close(); } else {} } catch(Throwable ignore) {}
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			
	        MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
	        throw new DbTaoException(msgEtt,e);
		} finally {
	        try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
	        try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
	        try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			
		return result;
	}

	/** ***********************************************************************
	* TMȸ������ �˾ƺ���.    
	*********************************************************************** */
	public int isTmMemExecute(WaContext context, TaoDataSet data, HttpServletRequest request) throws BaseException {

		String title = "";
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result =  0;
		

		try {
			conn = context.getDbConnection("default", null);
			
			String sql = "";
			String jumin_no				= data.getString("memSocid");

            sql = this.getTmMemCntQuery();
			pstmt = conn.prepareStatement(sql);			
	        pstmt.setString(1, jumin_no ); 

			rs = pstmt.executeQuery();			
			
			if(rs != null) {
				rs.next();
				result = rs.getInt("CNT");
			}

		} catch(Exception e) {
			try	{
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			
	        MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
	        throw new DbTaoException(msgEtt,e);
		} finally {
	        try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
	        try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}	
	
 	/** ***********************************************************************
	* 01-2. TM ȸ������ �������� 
	************************************************************************ */
	private String getMemberTmInfoQuery(String strMemClss){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		
		if(strMemClss.equals("5") || strMemClss.equals("6")){
			sql.append("\t  SELECT TB_TM.RNUM, TB_TM.JUMIN_NO, TB_TM.GOLF_CDHD_GRD_CLSS, TB_TM.RCRU_PL_CLSS	\n");
			sql.append("\t  , TB_INFO.USER_EMAIL EMAIL1, '' ZIPCODE, '' ZIPADDR, '' DETAILADDR, '' MEMBER_CLSS	\n");
			sql.append("\t  , TB_INFO.USER_TEL_NO MOBILE, TB_INFO.USER_MOB_NO PHONE	\n");
			sql.append("\t  FROM (	\n");
			sql.append("\t    SELECT ROWNUM RNUM, JUMIN_NO, GOLF_CDHD_GRD_CLSS, RCRU_PL_CLSS	\n");
			sql.append("\t    FROM (	\n");
			sql.append("\t        SELECT JUMIN_NO, GOLF_CDHD_GRD_CLSS, RCRU_PL_CLSS	\n");
			sql.append("\t        FROM BCDBA.TBLUGTMCSTMR	\n");
			sql.append("\t        WHERE TB_RSLT_CLSS='01' AND RND_CD_CLSS='2' AND JUMIN_NO=?	\n");
			sql.append("\t        ORDER BY GOLF_CDHD_GRD_CLSS DESC, WK_DATE DESC	\n");
			sql.append("\t    ) WHERE ROWNUM=1	\n");
			sql.append("\t  ) TB_TM	\n");
			sql.append("\t  JOIN BCDBA.TBENTPUSER TB_INFO ON TB_TM.JUMIN_NO = TB_INFO.USER_JUMIN_NO	\n");
		}else{
			sql.append("\t  SELECT TB_TM.RNUM, TB_TM.JUMIN_NO, TB_TM.GOLF_CDHD_GRD_CLSS, TB_TM.RCRU_PL_CLSS	\n");
			sql.append("\t  , TB_INFO.EMAIL1, TB_INFO.ZIPCODE, TB_INFO.ZIPADDR, TB_INFO.DETAILADDR, TB_INFO.MOBILE, TB_INFO.PHONE, TB_INFO.MEMBER_CLSS	\n");
			sql.append("\t  FROM (	\n");
			sql.append("\t      SELECT ROWNUM RNUM, JUMIN_NO, GOLF_CDHD_GRD_CLSS, RCRU_PL_CLSS	\n");
			sql.append("\t      FROM (	\n");
			sql.append("\t          SELECT JUMIN_NO, GOLF_CDHD_GRD_CLSS, RCRU_PL_CLSS	\n");
			sql.append("\t          FROM BCDBA.TBLUGTMCSTMR	\n");
			sql.append("\t          WHERE TB_RSLT_CLSS='01' AND RND_CD_CLSS='2' AND JUMIN_NO=?	\n");
			sql.append("\t          ORDER BY GOLF_CDHD_GRD_CLSS DESC, WK_DATE DESC	\n");
			sql.append("\t      ) WHERE ROWNUM=1	\n");
			sql.append("\t   ) TB_TM	\n");
			sql.append("\t  JOIN BCDBA.UCUSRINFO TB_INFO ON TB_TM.JUMIN_NO = TB_INFO.SOCID	\n");
		}
		return sql.toString();
	} 
       
    /** ***********************************************************************
    * Max IDX Query�� �����Ͽ� �����Ѵ�. = ����ȸ����ް���    
    ************************************************************************ */
    private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
        sql.append("SELECT NVL(MAX(CDHD_GRD_SEQ_NO),0)+1 SEQ_NO FROM BCDBA.TBGGOLFCDHDGRDMGMT \n");
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
    * ����ȸ�������� �μ�Ʈ - TBGGOLFCDHD    
    ************************************************************************ */
    private String getInsertMemQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  INSERT INTO BCDBA.TBGGOLFCDHD (	\n");
		sql.append("\t  	CDHD_ID, HG_NM, JUMIN_NO, JOIN_CHNL	\n");
		sql.append("\t  	, ACRG_CDHD_JONN_DATE, ACRG_CDHD_END_DATE	\n");
		sql.append("\t  	, JONN_ATON, EMAIL_RECP_YN, SMS_RECP_YN	\n");
		sql.append("\t  	, CBMO_ACM_TOT_AMT, CBMO_DDUC_TOT_AMT , MEMBER_CLSS	\n");
		sql.append("\t  	, CDHD_CTGO_SEQ_NO, MOBILE, PHONE, EMAIL, ZIP_CODE, ZIPADDR, DETAILADDR, LASTACCESS	\n");
		sql.append("\t  ) VALUES (	\n");
		sql.append("\t  	?, ?, ?, ?	\n");
		sql.append("\t 		, ?, ?	\n");
		sql.append("\t  	, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), 'Y', 'Y'	\n");
		sql.append("\t  	, 0, 0, ?	\n");
		sql.append("\t  	, ?, ?, ?, ?, ?, ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
		sql.append("\t  )	\n");
        return sql.toString();
    }
              
    /** ***********************************************************************
    * ����� TM ��� ���̺� ���°� ������Ʈ    
    ************************************************************************ */
    private String getUpdTmQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  UPDATE BCDBA.TBLUGTMCSTMR SET 	\n");
		sql.append("\t      TB_RSLT_CLSS='00', SQ2_TCALL_DATE=TO_CHAR(SYSDATE,'YYYYMMDD'), REJ_RSON=? 	\n");
		sql.append("\t  WHERE TB_RSLT_CLSS='01' AND JUMIN_NO=? 	\n");
		sql.append("\t	AND RND_CD_CLSS='2' AND GOLF_CDHD_GRD_CLSS=? 	\n");
        return sql.toString();
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
	* �̹� ��ϵǾ� �ִ°�� ������Ʈ ���ش�.    
	************************************************************************ */
	private String getUpdMemQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD SET JOIN_CHNL=?	\n");
		sql.append("\t	, ACRG_CDHD_JONN_DATE=?, ACRG_CDHD_END_DATE=?	\n");
		sql.append("\t	, JONN_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
		sql.append("\t	, CDHD_CTGO_SEQ_NO=?, SECE_YN='N', SECE_ATON=''	\n");
		sql.append("\t	WHERE CDHD_ID=?	\n");
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
 		sql.append("\t		WHERE CDHD_ID=?									\n");
        return sql.toString();
    }

    /** ***********************************************************************
    * TMȸ�����̺� ������ �� ��������    
    ************************************************************************ */
    private String getTmMemCntQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
 		sql.append("\t	SELECT COUNT(*) CNT	\n");
 		sql.append("\t	FROM BCDBA.TBLUGTMCSTMR	\n");
 		sql.append("\t	WHERE TB_RSLT_CLSS='01' AND RND_CD_CLSS='2' AND JUMIN_NO=?	\n");
 		sql.append("\t	ORDER BY GOLF_CDHD_GRD_CLSS DESC, WK_DATE DESC	\n");
        return sql.toString();
    }

    /** ***********************************************************************
    * ������ ȸ������ �˾ƺ���.
    ************************************************************************ */
    private String getIbkGoldInfoQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
 		sql.append("\t	SELECT TM_IBK.JUMIN_NO, TM_IBK.RCRU_PL_CLSS	\n");
 		sql.append("\t	FROM  BCDBA.TBACRGCDHDLODNTBL TM_IBK	\n");
 		sql.append("\t	JOIN BCDBA.UCUSRINFO TB_INFO ON TM_IBK.JUMIN_NO = TB_INFO.SOCID	\n");
 		sql.append("\t	WHERE TM_IBK.SITE_CLSS='02' AND TM_IBK.RCRU_PL_CLSS='4003' AND TM_IBK.PROC_RSLT_CLSS<>'01'	\n");
 		sql.append("\t	AND TM_IBK.CAMP_STRT_DATE <= TO_CHAR(SYSDATE,'YYYYMMDD') AND  TM_IBK.CAMP_END_DATE >= TO_CHAR(SYSDATE,'YYYYMMDD')	\n");
 		sql.append("\t	AND TM_IBK.JUMIN_NO = ?	\n");
        return sql.toString();
    }         
    
    /** ***********************************************************************
     * �����̷� �����丮 �μ�Ʈ
     ************************************************************************ */
     private String getJoinChnlHistoryInsQuery(){
        StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
  		sql.append("\t	INSERT INTO BCDBA.TBGCDHDGRDCHNGHST	\n");
  		sql.append("\t	SELECT (SELECT MAX(SEQ_NO)+1 FROM BCDBA.TBGCDHDGRDCHNGHST), JOIN_CHNL, CDHD_ID, '0', TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
  		sql.append("\n	, ACRG_CDHD_JONN_DATE , ACRG_CDHD_END_DATE , JOIN_CHNL	");
  		sql.append("\t	FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID=?	\n");
  		
        return sql.toString();
     }               
     
     /** ***********************************************************************
      * �����̷� �����丮 �μ�Ʈ
      ************************************************************************ */
      private String getIbkMemUpdQuery(){
        StringBuffer sql = new StringBuffer();
   		sql.append("	\n");
   		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD	\n");
   		sql.append("\t	SET ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(TO_DATE(ACRG_CDHD_END_DATE),2),'YYYYMMDD')	\n");
   		//sql.append("\t	, JOIN_CHNL=?	\n");
   		sql.append("\t	WHERE CDHD_ID=?	\n");
        return sql.toString();
      }  

      /** ***********************************************************************
      * ������ȸ�� �Ϸ� �� ������Ʈ    
      ************************************************************************ */
      private String getUpdIbkGoldEndQuery(){
        StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
  		sql.append("\t  UPDATE BCDBA.TBACRGCDHDLODNTBL	\n");
  		sql.append("\t  SET JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD'), PROC_RSLT_CLSS='01', PROC_RSLT_CTNT=?	\n");
  		sql.append("\t  WHERE SITE_CLSS='02' AND RCRU_PL_CLSS='4003' AND JUMIN_NO=?	\n");
        return sql.toString();
      }

      /** ***********************************************************************
      *  KT �÷� Ŭ�� ���ԿϷ�    PGRS_YN='N'
      ************************************************************************ */
      private String joinComplete(){
        StringBuffer sql = new StringBuffer();
  		sql.append("	UPDATE BCDBA.TBGAPLCMGMT SET PGRS_YN='N', CDHD_ID=?	\n");
  		sql.append("\t   WHERE GOLF_SVC_APLC_CLSS='1005' AND PGRS_YN='Y' AND JUMIN_NO=?	\n");  		
        return sql.toString();
      }

      /*************************************************************************
       * KT �÷� Ŭ�� Champion��޿��� ��û�� ����ǰ������ cdhd_id ������Ʈ
       * ó�� ��û�ô� cdhd_id�� �ֹι�ȣ�� �� 
       * ( ó�� ��û���ϸ鼭 �����ô� ��������� �� ȸ���� -> ��ȸ�� �Ǹ鼭 cdhd_id�� ������Ʈ)   
       ************************************************************************ */
       private String updGift(){
    	   
    	   StringBuffer sql = new StringBuffer();
    	   
    	   sql.append("		UPDATE BCDBA.TBGCDHDRIKMGMT	\n");
    	   sql.append("\t	SET CDHD_ID = ?				\n");
    	   sql.append("\t	WHERE CDHD_ID = ?	 		\n");    	   
   		
    	   return sql.toString();
    	   
       }
       
       
       /*************************************************************************
        * KT �÷� Ŭ�� ��û�� cdhd_id ������Ʈ
        * ó�� ��û�ô� cdhd_id�� �ֹι�ȣ�� �� 
        * ( ó�� ��û���ϸ鼭 �����ô� ��������� �� ȸ���� -> ��ȸ�� �Ǹ鼭 cdhd_id�� ������Ʈ)   
        ************************************************************************ */
        private String updPayInfo(){
     	   
     	   StringBuffer sql = new StringBuffer();
     	   
     	   sql.append("		UPDATE BCDBA.TBGSTTLMGMT	\n");
     	   sql.append("\t	SET CDHD_ID = ?				\n");
     	   sql.append("\t	WHERE CDHD_ID = ?	 		\n");    
     	   sql.append("\t	AND STTL_GDS_CLSS = ? 		\n");
    		
     	   return sql.toString();
     	   
        }       

      
}

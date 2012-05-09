/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfRangeRsvtInsDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : SKY72�帲���������� �����û ó��
*   �������  : golf
*   �ۼ�����  : 2009-06-16
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.drivrange;

import java.io.Reader;
import java.io.Writer;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

import com.bccard.golf.user.entity.UcusrinfoEntity;

import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfRangeRsvtInsDaoProc extends AbstractProc {

	public static final String TITLE = "SKY72�帲���������� �����û ó��";

	/** *****************************************************************
	 * GolfAdmRangeRsvtInsDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfRangeRsvtInsDaoProc() {}
	
	/**
	 * SKY72�帲���������� �����û ó��
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		int result2 = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Writer writer = null;
		Reader reader = null;
		String sql = "";
		
		String userNm = ""; 
		String memClss ="";
		String userId = "";
		String isLogin = ""; 
		String juminno = ""; 
		String memGrade = ""; 
		int intMemGrade = 0; 
		int intCyberMoney = 0; 
		String email1 = ""; 
		
		UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		
		 if(usrEntity != null) {
			userNm		= (String)usrEntity.getName(); 
			memClss		= (String)usrEntity.getMemberClss();
			userId		= (String)usrEntity.getAccount(); 
			juminno 	= (String)usrEntity.getSocid(); 
			memGrade 	= (String)usrEntity.getMemGrade(); 
			intMemGrade	= (int)usrEntity.getIntMemGrade(); 
			intCyberMoney	= (int)usrEntity.getCyberMoney(); //���̹��Ӵ�
			email1 	= (String)usrEntity.getEmail1(); 
		}
		 
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		String nowYear = String.valueOf(cal.get(Calendar.YEAR));
		String nowMonth = String.valueOf(cal.get(Calendar.MONTH)+1);
		String nowDate = String.valueOf(cal.get(Calendar.DATE));
		String nowDay = nowYear +"�� "+ nowMonth +"�� "+ nowDate +"��";
		
		
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);	
			
			String hp_ddd_no = data.getString("HP_DDD_NO");
			String hp_tel_hno = data.getString("HP_TEL_HNO");
			String hp_tel_sno = data.getString("HP_TEL_SNO");
			String phone = hp_ddd_no + hp_tel_hno + hp_tel_sno;
			String regDate 		= data.getString("regDate");
			String sch_gr 		= data.getString("SCH_GR_SEQ_NO");
			String bkGRADE 		= data.getString("BKGRADE");
			
			//�ִ��û�ο��� ��������
			long maxCnt = 0L;
			pstmt = conn.prepareStatement(getMaxCntQuery());
			pstmt.setString(1, regDate);
			pstmt.setString(2, sch_gr);
			rs = pstmt.executeQuery();		
			
			if(rs.next()){
				maxCnt = rs.getLong("DLY_RSVT_ABLE_PERS");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
			debug("## GolfRangeRsvtInsDaoProc | �ִ��ϼ� maxCnt : "+maxCnt);
			
			//���� ��û�� �� ��������
			//String dayIngCnt = "";
			long dayIngCnt = 0L;
			pstmt = conn.prepareStatement(getDayCntQuery());
			pstmt.setString(1, data.getString("RSVT_CLSS"));
			pstmt.setLong(2, data.getLong("RSVTTIME_SQL_NO"));
			rs = pstmt.executeQuery();	
			if(rs.next()){
				dayIngCnt = rs.getLong("CCNT");
			}	
			debug("## GolfRangeRsvtInsDaoProc | ���� ��û�� dayIngCnt : "+dayIngCnt);	
			
			debug("## GolfRangeRsvtInsDaoProc | ���డ�ɿ��� maxCnt : "+maxCnt+" | dayIngCnt : "+dayIngCnt);
			 
			if(maxCnt > dayIngCnt)
			{
				sql = this.getNextValQuery(); //��ȣ ����
	            pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, data.getString("RSVT_CLSS"));
	            rs = pstmt.executeQuery();			
				String seq_no = "";
				if(rs.next()){
					seq_no = rs.getString("RSVT_SQL_NO");
				}
				if(rs != null) rs.close();
	            if(pstmt != null) pstmt.close();
	            /*****************************************************************************/
	            
	            sql = this.getSelectQuery(); //��ȣ ����
	            pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, data.getString("RSVT_CLSS"));
				pstmt.setString(2, data.getString("USERID"));
				pstmt.setLong(3, data.getLong("RSVTTIME_SQL_NO"));
	            rs = pstmt.executeQuery();			
				long rsvt_cnt = 0L;
				if(rs.next()){
					rsvt_cnt = rs.getLong("RSVT_CNT");
				}			
				if(rs != null) rs.close();
	            if(pstmt != null) pstmt.close();
	            /*****************************************************************************/
	            
	           
	            
	            if (rsvt_cnt == 0){ //������ ������ ������
					sql = this.getInsertQuery();//Insert Query
					pstmt = conn.prepareStatement(sql);
					
					int idx = 0;
					pstmt.setString(++idx, data.getString("RSVT_CLSS")+seq_no );
					pstmt.setString(++idx, data.getString("RSVT_CLSS") ); 
					pstmt.setString(++idx, data.getString("USERID") ); 
					pstmt.setLong(++idx, data.getLong("RSVTTIME_SQL_NO") );
					pstmt.setString(++idx, data.getString("HP_DDD_NO") );
					pstmt.setString(++idx, data.getString("HP_TEL_HNO") );
					pstmt.setString(++idx, data.getString("HP_TEL_SNO") );
					pstmt.setString(++idx, data.getString("USERID") );
					pstmt.setString(++idx,data.getString("BKGRADE"));
					
					
					result = pstmt.executeUpdate();
		            if(pstmt != null) pstmt.close();
		           
		            
		            // ���� ���̹� �Ӵ� �ݾ��� �����´�.
					sql = this.getCyberMoneyQuery(); 
		            pstmt = conn.prepareStatement(sql);
		        	pstmt.setString(1, data.getString("USERID") ); 	
		            rs = pstmt.executeQuery();			
					int cyberMoney = 0;
					if(rs.next()){
						cyberMoney = rs.getInt("TOT_AMT");
					}
					if(rs != null) rs.close();
		            if(pstmt != null) pstmt.close();
		            
		            if (data.getString("RNTB").equals("002") && intMemGrade > 1) {
			        	// ���̹��Ӵ� ��볻�� ���̺� �ݾ��� �־��ش�.
					    /**SEQ_NO ��������**************************************************************/
						String sql2 = this.getCyberMoneyNextValQuery(); 
			            PreparedStatement pstmt2 = conn.prepareStatement(sql2);
			            ResultSet rs2 = pstmt2.executeQuery();			
						long cyber_money_max_seq_no = 0L;
						if(rs2.next()){
							cyber_money_max_seq_no = rs2.getLong("SEQ_NO");
						}
						if(rs2 != null) rs2.close();
			            if(pstmt2 != null) pstmt2.close();
			            
			            /**Insert************************************************************************/
			            
			            String sql3 = this.getMemberTmInfoQuery();
						PreparedStatement pstmt3 = conn.prepareStatement(sql3);
		
						
						int totCyberMoney = cyberMoney-Integer.parseInt(data.getString("DRVR_AMT"));
						
						idx = 0;
						pstmt3.setLong(++idx, cyber_money_max_seq_no ); 		//COME_SEQ_SEQ_NO
						pstmt3.setString(++idx, data.getString("USERID") ); 		//CDHD_ID
						pstmt3.setInt(++idx, Integer.parseInt(data.getString("DRVR_AMT")) );					//ACM_DDUC_AMT
						pstmt3.setInt(++idx, totCyberMoney );					//REST_AMT
						pstmt3.setString(++idx, "0006" );						//CBMO_USE_CLSS :  0006:Sky72����̺�������
						pstmt3.setString(++idx, data.getString("RSVT_CLSS")+seq_no );					//GOLF_SVC_RSVT_NO : �������񽺿����ȣ  			
		
			        	
						result2 = pstmt3.executeUpdate();
			            if(pstmt3 != null) pstmt3.close();
						
			            
			        	// ȸ�����̺� ���̹��Ӵ� ������ ������Ʈ ���ش�.
				        sql3 = this.getMemberUpdateQuery(Integer.parseInt(data.getString("DRVR_AMT")));
						pstmt3 = conn.prepareStatement(sql3);
						pstmt3.setString(1, data.getString("USERID") ); 		//CDHD_ID
						
						result2 = pstmt3.executeUpdate();
			            if(pstmt3 != null) pstmt3.close();
			            
			            if(usrEntity != null) {
			            	usrEntity.setCyberMoney((int)totCyberMoney); //���̹��Ӵ�
						}
		            }
		          
					if(result > 0) {
						conn.commit();
						
						//����������ȸ ----------------------------------------------------------			
						sql = this.getRsvtSelectQuery();   
						
						// �Է°� (INPUT)         
						idx = 0;
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(++idx, data.getString("RSVT_CLSS")+seq_no);
						
						rs = pstmt.executeQuery();
						
						String golf_svc_rsvt_no = "";
						String rsvt_able_year = "";
						String rsvt_able_month = "";
						String rsvt_able_day = "";
						String rsvt_strt_hh = "";
						String rsvt_strt_mi = "";
						String rsvt_end_hh = "";
						String rsvt_end_mi = "";
						String cncl_date  = "";
						String green_nm  = "";
						
						if(rs != null) {
							while(rs.next())  {
								golf_svc_rsvt_no = rs.getString("GOLF_SVC_RSVT_NO");
								rsvt_able_year = rs.getString("RSVT_ABLE_YEAR");
								rsvt_able_month = rs.getString("RSVT_ABLE_MONTH");
								rsvt_able_day = rs.getString("RSVT_ABLE_DAY");
								rsvt_strt_hh = rs.getString("RSVT_STRT_HH");
								rsvt_strt_mi = rs.getString("RSVT_STRT_MI");
								rsvt_end_hh = rs.getString("RSVT_END_HH");
								rsvt_end_mi = rs.getString("RSVT_END_MI");
								cncl_date = rs.getString("CNCL_DATE");
								green_nm = rs.getString("GREEN_NM");
							}
						}
						if(pstmt != null) pstmt.close();
						if(rs != null) rs.close(); 
						
						
						// ���Ϲ߼�
						if (!email1.equals("")) {
							String emailAdmin = "\"���������\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
							//String emailTitle = userNm +"�� SKY72 �帲���������� ��û�� �Ϸ�Ǿ����ϴ�.";
							String emailTitle = "[Golf Loun.G] ����̺������� �����̿� ������ �Ϸ�Ǿ����ϴ�.";
							String emailFileNm = "/email_tpl06.html";
							String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
							String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
												
							EmailSend sender = new EmailSend();
							EmailEntity emailEtt = new EmailEntity("EUC_KR");
							
							emailEtt.setFrom(emailAdmin);
							emailEtt.setSubject(emailTitle);
							emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, 
									userNm +"|"+ nowDay +"|"+ golf_svc_rsvt_no +"|"+ rsvt_able_year+"/"+rsvt_able_month+"/"+rsvt_able_day +"|"+ rsvt_strt_hh+":"+rsvt_strt_mi+" ~ "+rsvt_end_hh+":"+rsvt_end_mi +"|"+ cncl_date);
							emailEtt.setTo(email1);
							//sender.send(emailEtt);
						}
						
						HashMap smsMap = new HashMap();
						
						smsMap.put("ip", request.getRemoteAddr());
						smsMap.put("sName", userNm);
						smsMap.put("sPhone1", hp_ddd_no);
						smsMap.put("sPhone2", hp_tel_hno);
						smsMap.put("sPhone3", hp_tel_sno);
						
						// sms�߼�
						if (!phone.equals("")) {
							debug("SMS����>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
							String smsClss = "643";
							//String message = "[�帲����������]"+ userNm +"�� "+ rsvt_able_month+"/"+rsvt_able_day +" "+ rsvt_strt_hh +"��~"+ rsvt_end_hh +"�� ����Ϸ� - Golf Loun.G";
							//String message = "[������Ʈ]"+ userNm +"�� "+ rsvt_able_month+"/"+rsvt_able_day +" ����Ϸ� - Golf Loun.G";
							String message = "[����̺�������-"+green_nm+"]"+ userNm +"�� "+ rsvt_able_month+"/"+rsvt_able_day +" ����Ϸ� - Golf Loun.G";
							SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
							String smsRtn = smsProc.send(smsClss, smsMap, message);
							debug("SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);
						}	
					} else {
						conn.rollback();
					}
	            }
			}
			else
			{
				debug("## ���ึ����");
				result = 3;
			}
			
			
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}			

		return result;
	}
	
	 /** ***********************************************************************
	* ���� ���̹��Ӵ� �Ѿ� ��������    
	************************************************************************ */
	private String getDayCntQuery(){
		StringBuffer sql = new StringBuffer();		

		sql.append("\n");
		sql.append("\t  SELECT COUNT(GOLF_SVC_RSVT_NO) AS CCNT 		\n");
		sql.append("\t  FROM BCDBA.TBGRSVTMGMT										\n");
		sql.append("\t  WHERE GOLF_SVC_RSVT_MAX_VAL = ? AND RSVT_ABLE_BOKG_TIME_SEQ_NO = ? AND 	RSVT_YN = 'Y'			\n");
		
		return sql.toString();
	}
	 /** ***********************************************************************
	* ���� ���̹��Ӵ� �Ѿ� ��������    
	************************************************************************ */
	private String getMaxCntQuery(){
		StringBuffer sql = new StringBuffer();		

		sql.append("\n");
		sql.append("\t  SELECT DLY_RSVT_ABLE_PERS 		\n");
		sql.append("\t  FROM BCDBA.TBGRSVTABLESCDMGMT										\n");
		sql.append("\t  WHERE GOLF_RSVT_DAY_CLSS = 'D' AND RSVT_ABLE_DATE = ?				\n");
		sql.append("\t  AND AFFI_GREEN_SEQ_NO = ?											\n");
		
		return sql.toString();
	}
	
    /** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		
		sql.append("INSERT INTO BCDBA.TBGRSVTMGMT (	\n");
		sql.append("\t  GOLF_SVC_RSVT_NO, GOLF_SVC_RSVT_MAX_VAL, CDHD_ID, RSVT_ABLE_BOKG_TIME_SEQ_NO, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, RSVT_YN, ATTD_YN, REG_MGR_ID,   	\n");
		sql.append("\t  CHNG_MGR_ID, REG_ATON, CHNG_ATON, RSVT_CDHD_GRD_SEQ_NO	\n");	
		sql.append("\t ) VALUES (	\n");	
		sql.append("\t  ?,?,?,?,?,?,?,'Y','Y',?	, \n");
		sql.append("\t  0,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),NULL, ?	\n");
		sql.append("\t \n)");	
        return sql.toString();
    }
    
    /** ***********************************************************************
     * Max IDX Query�� �����Ͽ� �����Ѵ�.    
     ************************************************************************ */
     private String getNextValQuery(){
         StringBuffer sql = new StringBuffer();
         sql.append("SELECT LPAD(TO_NUMBER(NVL(MAX(SUBSTR(GOLF_SVC_RSVT_NO,6,7)),0))+1,7,'0') RSVT_SQL_NO FROM BCDBA.TBGRSVTMGMT \n");
         sql.append("WHERE GOLF_SVC_RSVT_MAX_VAL = ? \n");
 		return sql.toString();
     }
     
     /** ***********************************************************************
     * ���� ���̵�� ���ð��� ��û���� �����Ѵ�.    
     ************************************************************************ */
	  private String getSelectQuery(){
	    StringBuffer sql = new StringBuffer();
	
	    sql.append("\n SELECT");
		sql.append("\n 	COUNT (GOLF_SVC_RSVT_NO) RSVT_CNT  ");
		sql.append("\n FROM BCDBA.TBGRSVTMGMT 	");
		sql.append("\n WHERE GOLF_SVC_RSVT_MAX_VAL = ?	");
		sql.append("\n AND CDHD_ID = ?	");	
		sql.append("\n AND RSVT_ABLE_BOKG_TIME_SEQ_NO = ?	");	
		sql.append("\n AND RSVT_YN = 'Y'	");	
		
		return sql.toString();
	  }

	 /** ***********************************************************************
	* ���� ���̹��Ӵ� �Ѿ� ��������    
	************************************************************************ */
	private String getCyberMoneyQuery(){
		StringBuffer sql = new StringBuffer();		

		sql.append("\n");
		sql.append("\t  SELECT (CBMO_ACM_TOT_AMT-CBMO_DDUC_TOT_AMT) AS TOT_AMT		\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD										\n");
		sql.append("\t  WHERE CDHD_ID=?												\n");
		
		return sql.toString();
	}

    /** ***********************************************************************
    * Max IDX Query�� �����Ͽ� �����Ѵ�. - ���̹��Ӵ� ���� �ִ� idx    
    ************************************************************************ */
    private String getCyberMoneyNextValQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
        sql.append("SELECT NVL(MAX(COME_SEQ_SEQ_NO),0)+1 SEQ_NO FROM BCDBA.TBGCBMOUSECTNTMGMT \n");
		return sql.toString();
    }
	
 	/** ***********************************************************************
	* ���̹��Ӵ� ����ϱ�    
	************************************************************************ */
	private String getMemberTmInfoQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  INSERT INTO BCDBA.TBGCBMOUSECTNTMGMT (													\n");
		sql.append("\t  		COME_SEQ_SEQ_NO, CDHD_ID, ACM_DDUC_CLSS, ACM_DDUC_AMT, REST_AMT, COME_ATON		\n");
		sql.append("\t  		, CBMO_USE_CLSS, GOLF_SVC_RSVT_NO												\n");
		sql.append("\t  		) VALUES (																		\n");
		sql.append("\t  		?, ?, 'N', ?, ?, TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'), ?, ?						\n");
		sql.append("\t  		)																				\n");
		return sql.toString();
	}
	
 	/** ***********************************************************************
	* ȸ������ ������Ʈ�ϱ�
	************************************************************************ */
	private String getMemberUpdateQuery(int cyberMoney){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  	UPDATE BCDBA.TBGGOLFCDHD								\n");
		sql.append("\t  	SET CBMO_DDUC_TOT_AMT=CBMO_DDUC_TOT_AMT+"+cyberMoney+"	\n");
		sql.append("\t  	WHERE CDHD_ID=?											\n");
		return sql.toString();
	}
	

	/** ***********************************************************************
    * ���������� �����Ѵ�.    
    ************************************************************************ */
    private String getRsvtSelectQuery(){
        StringBuffer sql = new StringBuffer();
		
        sql.append("\n SELECT 	");
		sql.append("\n 	GOLF_SVC_RSVT_NO, RSVT_ABLE_YEAR, RSVT_ABLE_MONTH, RSVT_ABLE_DAY, RSVT_STRT_HH, RSVT_STRT_MI, RSVT_END_HH, RSVT_END_MI, CNCL_DATE || ' 06:00' CNCL_DATE, GREEN_NM	");
		
		sql.append("\n FROM (SELECT TGR.GOLF_SVC_RSVT_NO, 	");
		sql.append("\n 			TO_CHAR (TO_DATE (TGRD.RSVT_ABLE_DATE, 'YYYYMMDD'), 'YYYY') RSVT_ABLE_YEAR,		");
      
		sql.append("\n 			TO_CHAR (TO_DATE (TGRD.RSVT_ABLE_DATE, 'YYYYMMDD'), 'MM') RSVT_ABLE_MONTH,	");
		sql.append("\n 			TO_CHAR (TO_DATE (TGRD.RSVT_ABLE_DATE, 'YYYYMMDD'), 'DD') RSVT_ABLE_DAY,	");
		sql.append("\n 			TO_CHAR (TO_DATE (TGRT.RSVT_STRT_TIME, 'HH24MI'), 'HH24') RSVT_STRT_HH,	");
		sql.append("\n 			TO_CHAR (TO_DATE (TGRT.RSVT_STRT_TIME, 'HH24MI'), 'MI') RSVT_STRT_MI,	");
		sql.append("\n 			TO_CHAR (TO_DATE (TGRT.RSVT_END_TIME, 'HH24MI'), 'HH24') RSVT_END_HH,	");
		sql.append("\n 			TO_CHAR (TO_DATE (TGRT.RSVT_END_TIME, 'HH24MI'), 'MI') RSVT_END_MI,	");
		sql.append("\n 			TO_CHAR (TO_DATE (TGRD.RSVT_ABLE_DATE, 'YYYYMMDD') - 3, 'YYYY/MM/DD') CNCL_DATE, TGRN.GREEN_NM		");
		sql.append("\n 		FROM BCDBA.TBGRSVTMGMT TGR, BCDBA.TBGRSVTABLESCDMGMT TGRD, BCDBA.TBGRSVTABLEBOKGTIMEMGMT TGRT	");
		sql.append("\n 			,BCDBA.TBGAFFIGREEN TGRN 	");
		sql.append("\n 		WHERE TGR.RSVT_ABLE_BOKG_TIME_SEQ_NO = TGRT.RSVT_ABLE_BOKG_TIME_SEQ_NO	");
		sql.append("\n 		AND TGRT.RSVT_ABLE_SCD_SEQ_NO = TGRD.RSVT_ABLE_SCD_SEQ_NO	");
		sql.append("\n 		AND TGRD.AFFI_GREEN_SEQ_NO = TGRN.AFFI_GREEN_SEQ_NO	");
		sql.append("\n 		AND TGR.GOLF_SVC_RSVT_NO = ?	)	");
		
        return sql.toString();
    }

}

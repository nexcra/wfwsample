/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfLoginLogInsProc
*   �ۼ���     : (��)�̵������ ������
*   ����        : �α��� �α� ����
*   �������  : Golf
*   �ۼ�����  : 2009-06-11
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*  2009.8.25          �ǿ���   ����ī�� �߰��۾�
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.GolfMemMonthInsDaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;

/** ****************************************************************************
 *  golf
 * @author	Media4th 
 * @version 1.0  
************************** �����̷� ****************************************************************
* Ŀ��屸����	������		������	���泻��
* golfloung		2010-02-25	������	������������->��ȸ�� ����	
* golfloung		2010-02-26	������	NH ȸ�� ����ä�� 3000���� �����°� ����	
* golfloung		2011-01-20 	�̰��� 	NH�йи� �߰�
 **************************************************************************** */
public class GolfLoginLogInsProc extends DbTaoProc {
	
	/** 
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */ 
	public TaoResult execute(Connection con, TaoDataSet dataSet) throws TaoException {

		String sql 					= "";
		PreparedStatement pstmt		= null;
		ResultSet rs 				= null;
		ResultSet rs2 				= null;
		ResultSet rs3 				= null;
		ResultSet rs4 				= null;
		ResultSet rs_grd			= null;		// ��ް������� 
		ResultSet rs_end_date		= null;		// ����ȸ�� ������ 
		ResultSet rs_membership		= null;		// ����� ��� ��������
		ResultSet rs_memInfo		= null;		// ȸ������
		String title				= dataSet.getString("title");
		String actnKey 				= null;
		DbTaoResult result			= new DbTaoResult(title);
		String reResult				= "";
		String strMESSAGE_KEY 		= "GolfTao_Common_reg";
		String benefit_no			= ""; 		// ���ð��� �Ϸù�ȣ
		PreparedStatement userInfoPstmt = null;
		ResultSet userInfoRs = null;
		int idx						= 0;
		String cdhd_ctgo_seq_no		= "";		// ����ȸ�� ���̺��� ȸ���з��Ϸù�ȣ : ��ǥ��޹�ȣ
		String acrg_cdhd_end_date	= "";		// ����ȸ�� ���� ������
		int isIbkGold				= 0; 		// ������ ȸ������
		int isSmart					= 0; 		// ����Ʈ ȸ������
		int isMembershiop			= 0;		// ����� ȸ������		
		
		int intMemGrade = 0;		//������
		int intMemberGrade = 0;		//��������ó��
		int intCardGrade = 0;		//ī����ó��
		String memGrade = "";		//��޸� 
		//WaContext context = null;
		//int result_upd = 0;			// ������� ��� ȸ�� �Ⱓ���� ���
		String end_date = "";		// ����Ⱓ ������
						
		try {
			
			System.out.print("## GolfLoginLogInsProc | sso id�� ���� ����ȸ�� ��ȸ ����  \n");
			
			actnKey 						= dataSet.getString("actnKey");
			UcusrinfoEntity userEtt 		= (UcusrinfoEntity)dataSet.getObject("userEtt");
									
			String sece_aton = "";				// Ż������
			String charge_mem = "";				// ����ȸ�� ���� => Y: ����ȸ��
			String join_re = "";				// �簡�� ���� ���� => N;�簡�� �Ұ�
			String jumin_no = "";				// �ֹε�Ϲ�ȣ
			String member_clss = "1";			// ��ī�� ȸ�� ���̺� ���Ŭ���� 5-> ����ȸ��, 1-> �Ϲ�ȸ��
			boolean goLogin = false;			// �α��� ó�� ����

			String vtl_jumin_no = "";			// (BC)�����ֹε�Ϲ�ȣ
			//String socid = "";					// (BC)�ֹε�Ϲ�ȣ

			String golfCardYn		= StrUtil.isNull(dataSet.getString("golfCardYn"),"N");			//����ī������
			String strCardJoinNo	= StrUtil.isNull(dataSet.getString("strCardJoinNo"),"");		//����ī���ڵ�	
			String golfCardNhYn		= StrUtil.isNull(dataSet.getString("golfCardNhYn"),"N");		//��������ī������
			String strCardNhType	= StrUtil.isNull(dataSet.getString("strCardNhType"),"");		//����ī������		
			String tourBlackYn		= StrUtil.isNull(dataSet.getString("tourBlackYn"),"");			//���� ��ȸ�� ����	
			String tourJuminNo		= StrUtil.isNull(dataSet.getString("tourJuminNo"),"");			//���� ��ȸ�� �ֹι�ȣ
			String vipCardYn		= StrUtil.isNull(dataSet.getString("vipCardYn"),"N");			//VIPī�� ����
			String topGolfCardYn	= StrUtil.isNull(dataSet.getString("topGolfCardYn"),"N");		//ž����ī�� ����
			//String topGolfCardNo	= StrUtil.isNull(dataSet.getString("topGolfCardNo"),"");
			String golfCardCoYn		= StrUtil.isNull(dataSet.getString("golfCardCoYn"),"N");		//ž��������ȸ�� ����			
			String richCardYn		= StrUtil.isNull(dataSet.getString("richCardYn"),"N");			//��ġī�� ����
			String jbCardYn		= StrUtil.isNull(dataSet.getString("jbCardYn"),"N");			//���Ͻñ״���ī�� ����
			
			debug("## GolfLoginLogInsProc | ID : "+userEtt.getAccount()+" | ��� : "+userEtt.getIntMemGrade()+" | golfCardYn : " + golfCardYn + "  | strCardJoinNo : " + strCardJoinNo + " | golfCardNhYn : " + golfCardNhYn + " | strCardNhType : " + strCardNhType + " | tourBlackYn : " + tourBlackYn + " | tourJuminNo : " + tourJuminNo + " | vipCardYn : "+vipCardYn+" | richCardYn : "+richCardYn+" | topGolfCardYn : "+topGolfCardYn+"| jbCardYn :"+jbCardYn +"\n");
			
			//��ȸ�� ���μ���
			GolfMemMonthInsDaoProc monthProc = new GolfMemMonthInsDaoProc();
			

			/**����/���� ȸ�� ����****����ȸ���� ����ȸ��(6)�� �α��� �����ϰ� **********************************************************/			
			//����ȸ��(����) TBENTPUSER ���̺�  üũ 
			// member_clss ���� �˻� 1:��ȸ�� / 4:��ȸ�� / 5:����ȸ��
			// ����ȸ���� BCDBA.TBENTPUSER , BCDBA.UCUSRINFO ����  / ����ȸ���� BCDBA.UCUSRINFO �����Ѵ�.
			// �ش���̵�� ����ȸ�� ���̺��� �˻��Ѵ�.	 
			pstmt = con.prepareStatement(getMemCoYnQuery());
			pstmt.setString(1, userEtt.getAccount());	
			rs3 = pstmt.executeQuery();
			String memCoChk2 = "";
			if(rs3 != null) {			 
				
				while(rs3.next())  {	
					
					if("6".equals(rs3.getString("MEM_CLSS")))
					{
						memCoChk2 = rs3.getString("MEM_CLSS");
					}
					else
					{
						if(!"6".equals(memCoChk2)) memCoChk2 = rs3.getString("MEM_CLSS");
					}														
											
				}
			}
			
			if(!"".equals(memCoChk2)) //�α��� ������ ����ȸ��
			{
				if("6".equals(memCoChk2))	// ����ī�� ������� ���
				{
					System.out.print("## GolfLoginLogInsProc  | setStrMemChkNum(5) ���� | ID : "+userEtt.getAccount()+" | memCoChk2 : "+memCoChk2+" \n");	
					member_clss = "5";
					userEtt.setStrMemChkNum("5");		//����/���� (  1,4:���� / 5:���� ) 
				}
				else{ // ����ī������ ���� ��� 
					
					// ž����ī�� ������ �ٽ� �ѹ� ����ī����� Ȯ��
					if("Y".equals(topGolfCardYn))
					{											
						
						
						if("Y".equals(golfCardCoYn))
						{
							System.out.print("## GolfLoginLogInsProc | ž����ī�� ���� ���ο��� ���� | ID : "+userEtt.getAccount()+" | memCoChk2 : "+memCoChk2+" \n");	
							member_clss = "5";
							userEtt.setStrMemChkNum("5");		//����/���� (  1,4:���� / 5:���� ) 
						}																		
						else
						{
							member_clss = "7";
						}
					}
					else
					{
						member_clss = "7";  //�Ұ��� ����
					}
					
				}
			}				
			else
			{
				
				//ȸ�� TBENTPUSER  ���̺� ������� ȸ�����̺� UCUSRINFO ��ȸ �Ϲ�ȸ���̶��
				pstmt = con.prepareStatement(getCountMemOnlyClssQuery());
				pstmt.setString(1, userEtt.getAccount());	
				rs4 = pstmt.executeQuery();
				if(rs4.next())  //�α��� ������ ����ȸ��
				{	
					member_clss = rs4.getString("MEMBER_CLSS");
				}
			
			}
			System.out.print("## GolfLoginLogInsProc  | member_clss üũ | ID : "+userEtt.getAccount()+" | member_clss : "+member_clss+" \n");
			/////////////////////////////////////////////////////////////
			// �������� �������� ���а� ett����
			userEtt.setStrMemChkNum(member_clss);		//����/���� (  1,4:���� / 5:���� ) 
			/////////////////////////////////////////////////////////////
			
			
			
			 
			/**ȸ���⺻���� ��������**************************************************************/
			String memEmail				= "";	// �̸���
			String memZipCode			= "";	// �����ȣ
			String memZipAddr			= "";	// �ּ�
			String memDetailAddr		= "";	// ���ּ�
			String memMobile			= "";	// �ڵ�����ȣ
			String memPhone				= "";	// ��ȭ��ȣ
			
			sql = this.getUserInfoQuery(member_clss);  	// ȸ����޹�ȣ 1:���� / 5:����
            userInfoPstmt = con.prepareStatement(sql);
            userInfoPstmt.setString(1, userEtt.getAccount() );
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
            

//			if("simijoa81".equals(userEtt.getAccount())){
//				tourBlackYn = "Y";
//				tourJuminNo = "8108212622793";
//				golfCardYn = "Y";			//9, 10 - ���� ����
//				strCardJoinNo = "030698";	//10 
//				strCardJoinNo = "394033";	//19 - �泲����
//				strCardJoinNo = "740276";	//20 - IBK APT �����̾�ī��
//				golfCardNhYn = "Y";			//12, 13 - NH  
//				strCardNhType = "03";		//12 - ƼŸ��
//				strCardNhType = "12";		//13 - �÷�Ƽ��
//			} 
/*			
//			if("khlplus01".equals(userEtt.getAccount())){
//				tourBlackYn = "Y";
//				tourJuminNo = "8108212622793";
				golfCardYn = "Y";			//9, 10 - ���� ����
				strCardJoinNo = "036948";	//10 
//				strCardJoinNo = "394033";	//19 - �泲����
//				strCardJoinNo = "740276";	//20 - IBK APT �����̾�ī��
//				golfCardNhYn = "Y";			//12, 13 - NH  
//				strCardNhType = "03";		//12 - ƼŸ��
//				strCardNhType = "12";		//13 - �÷�Ƽ��
				
//				debug("#---------------khlplus01");
//			} 			
*/			

			/*
			//�׽�Ʈ��
			if("ymkwun".equals(userEtt.getAccount())){
				golfCardYn = "Y";
				strCardJoinNo = "740276";	//20 - IBK APT �����̾�ī��
			} 
			*/
			
            //CtrlServ ���� ���� ������ ���������� 
			if(userEtt != null)
			{	
							
				/**�������� �����->������ ó��**************************************************************/
				String tour_grade_chage = "N";		// ������������ ������� ���濩��
				String tour_acrg_upd = "N";			// ����ȸ���Ⱓ ���� ����
				if("Y".equals(tourBlackYn)){		// ���� �� ȸ���ϰ�� �ڵ����� �α��� �ǵ����Ѵ�.	
					System.out.print("## GolfLoginLogInsProc | ������������->������ ó�� \n");	
					pstmt = con.prepareStatement(getChkTourQuery());
					pstmt.setString(1, userEtt.getAccount());
					rs= pstmt.executeQuery();
					
					if(rs.next()){
						if("Y".equals(rs.getString("SECE_YN"))){	
							pstmt = con.prepareStatement(getReInsertMemQuery());
							pstmt.setString(1, userEtt.getAccount());
							pstmt.executeQuery();
							
							tour_grade_chage = "Y";
							System.out.print("## GolfLoginLogInsProc | ������������->������ : Ż��ȸ�� �簡��ó��  \n");	
						}

						if("29".equals(rs.getString("CDHD_CTGO_SEQ_NO"))){
							tour_acrg_upd = "Y";
							System.out.print("## GolfLoginLogInsProc | ������������->��������ñ�Ƽó : ����Ⱓ  ����\n");
                                                }
						
						if("8".equals(rs.getString("CDHD_CTGO_SEQ_NO")) || GolfUtil.empty(rs.getString("CDHD_CTGO_SEQ_NO"))){
							tour_acrg_upd = "Y";
							tour_grade_chage = "Y";
							System.out.print("## GolfLoginLogInsProc | ������������->������ : ȭ��Ʈȸ�� ��� ���׷��̵� \n");
						}else{
							if("N".equals(rs.getString("ACRG_ABLE"))){
								tour_acrg_upd = "Y";
								tour_grade_chage = "Y";
								System.out.print("## GolfLoginLogInsProc | ������������->������ : ����Ⱓ ���� ȸ�� ��� ����  \n");
							}
						}
						
						System.out.print("## GolfLoginLogInsProc | ������������->������ | tour_acrg_upd : "+tour_acrg_upd+" | tour_grade_chage : "+tour_grade_chage+"  \n");
						
						if("Y".equals(tour_acrg_upd)){
							pstmt = con.prepareStatement(getAcrgUpdQuery());
							pstmt.setString(1, userEtt.getAccount());
							pstmt.executeQuery();
							System.out.print("## GolfLoginLogInsProc | ����Ⱓ ������Ʈ  \n");
						}
						
						if("Y".equals(tour_grade_chage)){
							//����ȸ����ް��� ���̺� ���� ������ ����
							pstmt = con.prepareStatement(getMemGradeDelQuery());
							pstmt.setString(1, userEtt.getAccount());
							pstmt.executeQuery();

							//����ȸ����ް����μ�Ʈ
							sql = this.getNextValQuery(); 
				            pstmt = con.prepareStatement(sql);
				            rs = pstmt.executeQuery();			
							long max_seq_no = 0L;
							if(rs.next()){
								max_seq_no = rs.getLong("SEQ_NO");
							}
							if(rs != null) rs.close();
				            if(pstmt != null) pstmt.close();
				            
				            sql = this.getInsertGradeQuery();
							pstmt = con.prepareStatement(sql);
							
				        	pstmt.setLong(1, max_seq_no ); 
				        	pstmt.setString(2, userEtt.getAccount() ); 
				        	pstmt.setString(3, "11" );
				        	
							pstmt.executeUpdate();
							System.out.print("## GolfLoginLogInsProc | �������������� ��޺���->������  \n");
							
							//����ȸ�����̺� ��ǥ���, ���԰��(3000) ������Ʈ getUpdGradeQuery
				            sql = this.getUpdGradeTourQuery();
							pstmt = con.prepareStatement(sql);
							
				        	pstmt.setString(1, "11" );
				        	pstmt.setString(2, userEtt.getAccount() ); 
				        	
							pstmt.executeUpdate();
						}
						
					}else{
			            
						//����ȸ�����̺� �μ�Ʈ
						pstmt = con.prepareStatement(getInsertMemQuery());
						idx = 0;
						pstmt.setString(++idx, userEtt.getAccount());
			        	pstmt.setString(++idx, userEtt.getName() );
			        	pstmt.setString(++idx, tourJuminNo );
						pstmt.setString(++idx, "11" );
						pstmt.setString(++idx, memMobile );
						pstmt.setString(++idx, memPhone );
						pstmt.setString(++idx, memEmail );
						pstmt.setString(++idx, memZipCode );
						pstmt.setString(++idx, memZipAddr );
						pstmt.setString(++idx, memDetailAddr );
						pstmt.executeQuery();

						//����ȸ����ް����μ�Ʈ
						sql = this.getNextValQuery(); 
			            pstmt = con.prepareStatement(sql);
			            rs = pstmt.executeQuery();			
						long max_seq_no = 0L;
						if(rs.next()){
							max_seq_no = rs.getLong("SEQ_NO");
						}
						if(rs != null) rs.close();
			            if(pstmt != null) pstmt.close();
			            
			            sql = this.getInsertGradeQuery();
						pstmt = con.prepareStatement(sql);
						
			        	pstmt.setLong(1, max_seq_no ); 
			        	pstmt.setString(2, userEtt.getAccount() ); 
			        	pstmt.setString(3, "11" );
			        	
						pstmt.executeUpdate();
						
						System.out.print("## GolfLoginLogInsProc | �������������� ����ó��->������  \n");
					}
					
				}	// ���� �� ȸ���ϰ�� �ڵ����� �α��� �ǵ����Ѵ�.
				else	// ����ȸ���� �ƴ����� 3000 ��ȸ���̸� ����ȸ���Ⱓ�� ������ �����Ѵ�.
				{
					pstmt = con.prepareStatement(getFreeBlackQuery());
					pstmt.setString(1, userEtt.getAccount());
					rs= pstmt.executeQuery();
					
					if(rs.next()){

			            sql = this.getUpdEndDateQuery();
						pstmt = con.prepareStatement(sql);
			        	pstmt.setString(1, userEtt.getAccount() );
			        	
						pstmt.executeUpdate();
						System.out.print("## GolfLoginLogInsProc | ����� -> ���� -> ����ȸ���������� ����  \n");
					}

					if(rs != null) rs.close();
		            if(pstmt != null) pstmt.close();
				}
				

				System.out.print("## GolfLoginLogInsProc | �������ī�� Ȯ�� ���� (�泲)================================ \n");
				
				debug("## 1:"+AppConfig.getDataCodeProp("Basic")
						+ ", "+ AppConfig.getDataCodeProp("Skypass") 
						+ ", "+ AppConfig.getDataCodeProp("AsianaClub")
						+ ", "+ AppConfig.getDataCodeProp("0052CODE9")
						+ ", strCardJoinNo : "+ strCardJoinNo
						+ ", vipCardYn: "+ vipCardYn);		

				
				if("Y".equals(golfCardYn)){	// ���ù�ȣ�� �����ش�.
					
					if("030698".equals(strCardJoinNo) || "031189".equals(strCardJoinNo) || "031176".equals(strCardJoinNo) ){
		        		benefit_no = "10";
					}else if("740276".equals(strCardJoinNo) || "740289".equals(strCardJoinNo) || "740292".equals(strCardJoinNo) ){		// IBK APT �����̾�ī�� 
		        		benefit_no = "20";
					}else if (strCardJoinNo.equals(AppConfig.getDataCodeProp("Basic"))
							||strCardJoinNo.equals(AppConfig.getDataCodeProp("Skypass"))
							||strCardJoinNo.equals(AppConfig.getDataCodeProp("AsianaClub"))){  
						benefit_no = AppConfig.getDataCodeProp("0052CODE9");
					}else if("394033".equals(strCardJoinNo)){	// �泲���� Family
		        		benefit_no = "19";
		        	}else{
		        		benefit_no = "9";
		        	}
				}
				execute_card(con, userEtt.getAccount(), golfCardYn, benefit_no, "ibk");

				
				System.out.print("## GolfLoginLogInsProc | ����ä��ī�� Ȯ�� ���� ================================ \n");
				if("Y".equals(golfCardNhYn)){	
					
					// ī������ : 03:ƼŸ�� , 12:�÷�Ƽ��, 48:�йи�ī��
					if(strCardNhType.equals("03")){	
						benefit_no = "12";
					}else if(strCardNhType.equals("12")) {
						benefit_no = "13";
					}else if(strCardNhType.equals("48")) {
						benefit_no = "14";
					}		
				}
				execute_card(con, userEtt.getAccount(), golfCardNhYn, benefit_no, "nh");
				
				/*
				System.out.print("## GolfLoginLogInsProc | VIPī�� Ȯ�� ���� ================================ \n");
				if("Y".equals(vipCardYn)){	
					benefit_no = "7";	
				}
				execute_card(con, userEtt.getAccount(), vipCardYn, benefit_no, "vip");
				*/
				
				System.out.print("## GolfLoginLogInsProc | ž����ī�� Ȯ�� ���� ================================ \n");
				if("Y".equals(topGolfCardYn)){	
					benefit_no = "21";	
				}
				execute_card(con, userEtt.getAccount(), topGolfCardYn, benefit_no, "topGolf");
				
				System.out.print("## GolfLoginLogInsProc | ��ġī�� Ȯ�� ���� ================================ \n");
				if("Y".equals(richCardYn)){	
					benefit_no = "22";	
				}
				execute_card(con, userEtt.getAccount(), richCardYn, benefit_no, "rich");
				
				System.out.print("## GolfLoginLogInsProc | ���Ͻñ״�óī�� Ȯ�� ���� ================================ \n");
				if("Y".equals(jbCardYn)){	
					benefit_no = "29";	
				}
				execute_card(con, userEtt.getAccount(), jbCardYn, benefit_no, "jb");
				
				
				//���� ��ȸ�� ����� ���� üũ
				boolean offerResult = monthProc.execute_newJoinMemYN(con, userEtt.getSocid());				

				System.out.print("## GolfLoginLogInsProc | ���������� ���� ================================ \n");
				//sso id �� ���� ����ȸ�� ���̺�(TBGGOLFCDHD) ���� ���� üũ
				pstmt = con.prepareStatement(getCountCkQuery());
				pstmt.setString(1, userEtt.getAccount());	
				rs = pstmt.executeQuery();
								
				if ( rs.next() ){	//����ȸ�����̺� ����
					
					
					sece_aton 			= StrUtil.isNull(rs.getString("SECE_ATON"), "");	// Ż������
					charge_mem 			= StrUtil.isNull(rs.getString("CHARGE_MEM"), "");	// ����ȸ�� ����
					join_re 			= StrUtil.isNull(rs.getString("JOIN_RE"), "");		// �簡�� ���ɱⰣ 
					jumin_no 			= StrUtil.isNull(rs.getString("JUMIN_NO"), "");		// �ֹε�Ϲ�ȣ
					vtl_jumin_no		= StrUtil.isNull(rs.getString("VRTL_JUMIN_NO"), "");// (BC)�����ֹε�Ϲ�ȣ
					if(GolfUtil.empty(vtl_jumin_no)) vtl_jumin_no="";
					//socid	 		= rs.getString("SOCID");		// (BC)�ֹε�Ϲ�ȣ
					cdhd_ctgo_seq_no	= StrUtil.isNull(rs.getString("CDHD_CTGO_SEQ_NO"), "");			// ����ȸ�����̺��� ��ǥ ��޹�ȣ
					acrg_cdhd_end_date 	= StrUtil.isNull(rs.getString("ACRG_CDHD_END_DATE"), "");	// ����ȸ�� ���� ������					
					
					
					
					System.out.print("## GolfLoginLogInsProc | ȸ�� | TBGGOLFCDHD ���� ����ȸ���� ��� ó������ | ID : "+userEtt.getAccount()+" | jumin_no : "+jumin_no+" | sece_aton : "+sece_aton+" | charge_mem : "+charge_mem+" | join_re : "+join_re+" \n");
										
					if(!GolfUtil.empty(sece_aton)){	// Ż�����ڰ� �ִ°��
			
					//�������� ���� TM ������ ���� ��� TM���� �������� ������. -> ���� �� 1���� ������ ��ŵ
					pstmt = con.prepareStatement(getMemTmQuery());
					pstmt.setString(1, userEtt.getSocid());	
					pstmt.setString(2, userEtt.getSocid());
					rs2 = pstmt.executeQuery();
										
					if(rs2.next()){						
						//1.Ƽ����� -> �̹� ������ �����̹Ƿ� ���Ǹ� �ִ� �������� ������.
						reResult = "01";
						System.out.print("## GolfLoginLogInsProc | �α���ȸ�� | TMȸ�� | TM �������� ������ (������Ʈ)1 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");			
					}else {							
						
						if(charge_mem.equals("Y") && join_re.equals("N"))
						{					
							reResult = "03";
							System.out.print("## GolfLoginLogInsProc | ȸ�� | �α��κҰ� | �Ѵ��̳��� Ż���� ����ȸ���� ��� �簡�ԺҰ� | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
						}
						else	// �簡�� : ����ī�尡 ������� �簡���� ī�尡���������� 2009.08.25 �ǿ��� �����߰� 2009.11.06 ������
						{	
							
							//�������� ���� TM ������ ���� ��� TM���� �������� ������. -> ���� �� 1���� ������ ��ŵ
							pstmt = con.prepareStatement(getMemTmQuery());
							pstmt.setString(1, userEtt.getSocid());	
							pstmt.setString(2, userEtt.getSocid());
							rs2 = pstmt.executeQuery();
												
							if(rs2.next()){						
								//1.Ƽ����� -> �̹� ������ �����̹Ƿ� ���Ǹ� �ִ� �������� ������.
								reResult = "01";
								System.out.print("## GolfLoginLogInsProc | �α���ȸ�� | TMȸ�� | TM �������� ������ (������Ʈ)2 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");			
							}else {
							
								//�켱������ ���� ����ó��							
								// ���IBKī�� ���޾�ü�ڵ� (����Ʈ ��޿� �ش�Ǵ� �����ڵ�)-�⺻ ,��ī���н�,�ƽþƳ�Ŭ��  -> ���������� �ش��
								// ��� ����Ʈ ����� VIPī�� ���� ȸ�� ���θ���� �������������� ���� - ������ ���� ��û
								if("Y".equals(golfCardYn) && (
																strCardJoinNo.equals(AppConfig.getDataCodeProp("Basic"))
																||strCardJoinNo.equals(AppConfig.getDataCodeProp("Skypass"))
																||strCardJoinNo.equals(AppConfig.getDataCodeProp("AsianaClub"))
																
																))
								{											
									reResult = "09";
									System.out.print("## GolfLoginLogInsProc | �̹�Ż�� �簡�� ȸ�� | ���IBKī�� ���޾�ü�ڵ� (Smart300  ��޿� �ش�Ǵ� �����ڵ�)-[�⺻ , ��ī���н�, �ƽþƳ�Ŭ��] strCardJoinNo : "
													+strCardJoinNo+"/"+userEtt.getAccount()+" | reResult : "+reResult+" \n");
								}
								// ��� ����Ʈ ����� VIPī�� ���� ȸ�� ���θ���� �������������� ���� - ������ ���� ��û
								else if("Y".equals(vipCardYn) && offerResult  ){
									
									reResult = "02";
									System.out.print("## GolfLoginLogInsProc | �̹�Ż�� �簡�� ȸ�� | Smart���  & vipī�� ����  : "+userEtt.getAccount()+" | offerResult  : " + offerResult  +" | reResult : "+reResult+" \n");									
									
								}	
								// 1���� IBK APT�����̾� ī��
								else if("Y".equals(golfCardYn) && ("740276".equals(strCardJoinNo) || "740289".equals(strCardJoinNo) || "740292".equals(strCardJoinNo)) )
								{											
									reResult = "09";
									System.out.print("## GolfLoginLogInsProc | �̹�Ż�� �簡�� ȸ�� | IBK APT �����̾�ī�� �ִ� ���� �簡�� ������ �̵� | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");															
								}
								// 2���� ž���� ī��
								else if("Y".equals(topGolfCardYn))
								{								
									reResult = "786";
									System.out.print("## GolfLoginLogInsProc | �̹�Ż�� �簡�� ȸ�� | ž����ī�� �ִ� ���� �簡�� ������ �̵� | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
								}
								
								// 3���� ����NHī��
								else if("Y".equals(golfCardNhYn))
								{
									reResult = "10";
									System.out.print("## GolfLoginLogInsProc | �̹�Ż�� �簡�� ȸ�� | ����NHī�� �ִ� ���� �簡�� ������ �̵� | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
								}	
								// 4���� �泲����ī��	
								else if("Y".equals(golfCardYn) && "394033".equals(strCardJoinNo) )
								{									
									reResult = "09";
									System.out.print("## GolfLoginLogInsProc | �̹�Ż�� �簡�� ȸ�� | �泲����ī�� �ִ� ���� �簡�� ������ �̵� | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");									
															
								}										
								// 5���� ��ġī��
								else if("Y".equals(richCardYn))
								{								
									reResult = "785";
									System.out.print("## GolfLoginLogInsProc | �̹�Ż�� �簡�� ȸ�� | ��ġī�� �ִ� ���� �簡�� ������ �̵� | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
								}
								// 6���� VIPī�� 			
								else if("Y".equals(vipCardYn))
								{
									System.out.print("## GolfLoginLogInsProc | �̹�Ż�� �簡�� ȸ�� | VIPī�� ����ȸ�� ó�� ����  \n");	
									
									String memCk = "N";
									memCk = memCk(con,cdhd_ctgo_seq_no);
									System.out.print("## GolfLoginLogInsProc | �̹�Ż�� �簡�� ȸ�� | VIPī�� ����ȸ�� ����ȸ������ Ȯ�� | ID : "+userEtt.getAccount()+" | memCk : "+memCk+"\n");	
									
									//VIPī�� �����ڰ� �������� �ƴҰ�� ���������������� �̵�
									if("N".equals(memCk))
									{
										reResult = "788";
									
									}
									
									//����ȸ����¥�� �����ϰ�� 1�� ��� ���������������� �̵�
									if(!"".equals(acrg_cdhd_end_date) && acrg_cdhd_end_date != null)
									{
										String toDate 		= DateUtil.currdate("yyyyMMdd");		//���ó�¥
										if( Integer.parseInt(toDate) > Integer.parseInt(acrg_cdhd_end_date) )
										{
											reResult = "788";																				
										}
									}	
									
									
									System.out.print("## GolfLoginLogInsProc | �̹�Ż�� �簡�� ȸ�� | VIPī�� ���� ������ �̵� | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
								}														
																					
								else
								{
									if("Y".equals(golfCardYn))
									{								
										reResult = "09";
										System.out.print("## GolfLoginLogInsProc | �̹�Ż�� �簡�� ȸ�� | ������ �������ī�� �ִ� ���� �簡�� ������ �̵� | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
									}
									else
									{
										reResult = "02";
										System.out.print("## GolfLoginLogInsProc | �̹�Ż�� �簡�� ȸ�� | �Ϲ� ���� ������ �̵� | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
									}
								}
								if("".equals(reResult)) reResult = "02";
								System.out.print("## GolfLoginLogInsProc | �̹�Ż�� �簡�� ȸ�� | ���Ժз�ó����� | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
								
								
							}
							
						}
					}
						
					}else{	// Ż�����ڰ� ���� ��� => ���� �α������� ����.

						/* ����ȸ�� �Ⱓ���� - TM START */
						if(!GolfUtil.empty(acrg_cdhd_end_date) && charge_mem.equals("N")){
							debug("����ȸ�� �Ⱓ���� : acrg_cdhd_end_date : " + acrg_cdhd_end_date + " / charge_mem : " + charge_mem + " / memId : " + userEtt.getAccount());
							execute_exPeriod(con, userEtt.getSocid(), userEtt.getAccount());
						}
						/* ����ȸ�� �Ⱓ���� - TM END */
						
						
						//�������� ���� TM ������ ���� ��� TM���� �������� ������. -> ���� �� 1���� ������ ��ŵ
						pstmt = con.prepareStatement(getMemTmQuery());
						pstmt.setString(1, userEtt.getSocid());	
						pstmt.setString(2, userEtt.getSocid());
						rs2 = pstmt.executeQuery();
											
						if(rs2.next()){						
							//1.Ƽ����� -> �̹� ������ �����̹Ƿ� ���Ǹ� �ִ� �������� ������.
							reResult = "01";
							System.out.print("## GolfLoginLogInsProc | �α���ȸ�� | TMȸ�� | TM �������� ������ (������Ʈ)3 | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");			
						}else{
							
							// BC ȸ�����̺��� ���� �ֹε�Ϲ�ȣ�� ���� ȸ�� ���̺��� �ֹε�Ϲ�ȣ�� ���� ��� �ֹε�Ϲ�ȣ �Է��������� �̵�
							if(vtl_jumin_no.equals(jumin_no)){
								reResult = "11";
								goLogin = true;
								System.out.print("## GolfLoginLogInsProc | �α���ȸ�� | �ֹε�Ϲ�ȣ �Է����� : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
							}else{
								
								// ���IBKī�� ���޾�ü�ڵ� (����Ʈ ��޿� �ش�Ǵ� �����ڵ�)-�⺻ ,��ī���н�,�ƽþƳ�Ŭ��  -> ���������� �ش��
								// ��� ����Ʈ ����� VIPī�� ���� ȸ�� ���θ���� �������������� ���� - ������ ���� ��û
								if("Y".equals(golfCardYn) && (
																strCardJoinNo.equals(AppConfig.getDataCodeProp("Basic"))
																||strCardJoinNo.equals(AppConfig.getDataCodeProp("Skypass"))
																||strCardJoinNo.equals(AppConfig.getDataCodeProp("AsianaClub"))
																
																))
								{											
									reResult = "00";
									goLogin = true;
									System.out.print("## ���IBKī�� ���޾�ü�ڵ� (Smart300  ��޿� �ش�Ǵ� �����ڵ�)-[�⺻ , ��ī���н�, �ƽþƳ�Ŭ��] strCardJoinNo : "+strCardJoinNo+"/"+userEtt.getAccount()+" | reResult : "+reResult+" \n");

								}										
								// ��� ����Ʈ ����� VIPī�� ���� ȸ�� ���θ���� �������������� ���� - ������ ���� ��û
								else if("Y".equals(vipCardYn) && offerResult ){
									
									reResult = "00";
									goLogin = true;
									
									monthProc.execute_MonthMember(con, userEtt.getSocid(), userEtt.getAccount());		
									
									System.out.print("## ���� �α��� Smart���  & vipī�� ����  : "+userEtt.getAccount()+" | offerResult : " + offerResult +" | reResult : "+reResult+" \n");									
									
								}
								// VIPī�� ����ȸ�� ó�� 2010.09.14 �ǿ���
								else if("Y".equals(vipCardYn))
								{
									System.out.print("## GolfLoginLogInsProc | �α���ȸ�� | VIPī�� ����ȸ�� ó�� ���� | ID : "+userEtt.getAccount()+" | cdhd_ctgo_seq_no : "+cdhd_ctgo_seq_no+" | acrg_cdhd_end_date : "+acrg_cdhd_end_date+"\n");	 
									
									String memCk = "N";
									memCk = memCk(con,cdhd_ctgo_seq_no);
									System.out.print("## GolfLoginLogInsProc | �α���ȸ�� | VIPī�� ����ȸ�� ����ȸ������ Ȯ�� | ID : "+userEtt.getAccount()+" | memCk : "+memCk+"\n");	
									
									//VIPī�� �����ڰ� �������� �ƴҰ�� ���������������� �̵�
									if("N".equals(memCk))
									{
										reResult = "788";
										goLogin = true;
									}
									
									//����ȸ����¥�� �����ϰ�� 1�� ��� ���������������� �̵�
									else if(!"".equals(acrg_cdhd_end_date) && acrg_cdhd_end_date != null)
									{


System.out.println("����ȸ����¥�� �����ϰ�� 1�� ��� ���������������� �̵�");
										String toDate 		= DateUtil.currdate("yyyyMMdd");		//���ó�¥
										if( Integer.parseInt(toDate) > Integer.parseInt(acrg_cdhd_end_date) ){
											
											reResult = "788";	
											goLogin = true;
System.out.println("reResult : " + reResult);
System.out.println("goLogin : " + goLogin);
											
										}else{
											
											// ���� �α��� ó��
											reResult = "00";
											goLogin = true;
											System.out.print("## GolfLoginLogInsProc | �α���ȸ�� | VIPī�� ���� �α��� ó�� | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" | goLogin : "+goLogin+" \n");
											
										}
										
									}else{
										
										// ���� �α��� ó��
										reResult = "00";
										goLogin = true;
										System.out.print("## GolfLoginLogInsProc | �α���ȸ�� | VIPī�� ���� �α��� ó�� | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" | goLogin : "+goLogin+" \n");
										
									}
									
									System.out.print("## GolfLoginLogInsProc | �α���ȸ�� | VIPī�� ���� ������ �̵� | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
									
								}else{
								
									// ���� �α��� ó��
									reResult = "00";
									goLogin = true;
									System.out.print("## GolfLoginLogInsProc  | ���� �α��� ó�� | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" | goLogin : "+goLogin+" \n");
									
									/* ��� ��� ȸ������ START */
									// ȭ��Ʈ ȸ���� ������ �̺�Ʈ ȸ���� ��� ������ �̺�Ʈ ȸ������ ������Ʈ ��Ų��. // 20100408
									// ����� ����ȸ���� ������ �̺�Ʈ ȸ���� ��� ����ȸ���Ⱓ�� 2�� �÷��ش�.		// 20100506
									isIbkGold 		= execute_isIbkGold(con, userEtt.getSocid());		// ������ ȸ�� ���� 
									isMembershiop 	= execute_isMembership(con, userEtt.getAccount());	// ����� ȸ�� ����
									debug("isIbkGold : " + isIbkGold + " / isMembershiop : " + isMembershiop);
									
									if(isMembershiop>0 && isIbkGold>0){
										if(isMembershiop==8){	// white ȸ���� ��� ������Ʈ
											int updIbkGold = execute_updIbkGold(con, userEtt.getSocid(), userEtt.getAccount());
											if(updIbkGold>0){
												reResult = "07";
											}
										}else{					// ����ȸ���� 2�� �Ⱓ����
											end_date = execute_updPeriodIbkGold(con, userEtt.getSocid(), userEtt.getAccount());
											if(!GolfUtil.empty(end_date)){
												reResult = "13";
											}
										}
									}									
									/* ������ ȸ������ END */
									
									
									/* TM ���� ��ȸ�� ��� ���� Start */												
									int isResult2 = monthProc.execute_MonthMember(con, userEtt.getSocid(), userEtt.getAccount());
									if(isResult2>0){
										reResult = "14";
									}
									/* ��ȸ�� ��� ���� End */
									
								}
							}
						}
					}

					if(GolfUtil.empty(jumin_no)){
						reResult = "04";
						System.out.print("## GolfLoginLogInsProc  | �α��κҰ� | �ֹι�ȣ �������� �ʴ� ��� | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");								
					}							
										
					if(!( member_clss.equals("1") || member_clss.equals("4")  || member_clss.equals("5")  )){	// 1:��ȸ�� / 4:��ȸ�� / 5:����ȸ��
						reResult = "05";
						System.out.print("## GolfLoginLogInsProc  | �α��κҰ� | ����/���� �� �ƴ� �ٸ������� ���� ȸ���� ������ ���  | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");		
					}
					
				//����ȸ���� �ƴ� ���
				}else{
					
					System.out.print("## GolfLoginLogInsProc | ��ȸ�� | TBGGOLFCDHD �������� ���� | ����ȸ���� �ƴ� ��� ó������ | ID : "+userEtt.getAccount()+" | socid : "+userEtt.getSocid()+" \n");					

					// ȸ���з��� 6(����ī��)�� ����ȸ������ üũ 
					pstmt = con.prepareStatement(getMemCoYnQuery());
					pstmt.setString(1, userEtt.getAccount());	
					rs4 = pstmt.executeQuery();
					
					String memCoChk = "";
					
					if(rs4 != null) {			 
						
						while(rs4.next())  {	
							
							if("6".equals(rs4.getString("MEM_CLSS")))
							{
								memCoChk = rs4.getString("MEM_CLSS");
							}
							else
							{
								if(!"6".equals(memCoChk)) memCoChk = rs4.getString("MEM_CLSS");
							}														
													
						}
					}					
					
					System.out.print("## GolfLoginLogInsProc | ��ȸ�� | ����ȸ���̸� ����ī��(6)���� üũ | ID : "+userEtt.getAccount()+" | memCoChk : "+memCoChk+" \n");
					
					if(!"".equals(memCoChk))  //�α��� ������ ����ȸ��
					{			
						if ("6".equals(memCoChk)) 
						{
							//���μ��� �ٽ� ���� �Ϲ� ������������ �̵����Ѷ�.
							//reResult = "00";
							//goLogin = true;
							//userEtt.setStrMemChkNum("5");		//����/���� (  1,4:���� / 5:���� )
														
							// ���IBKī�� ���޾�ü�ڵ� (Smart300  ��޿� �ش�Ǵ� �����ڵ�)-�⺻ ,��ī���н�,�ƽþƳ�Ŭ��  -> ���������� �ش��
							if("Y".equals(golfCardYn) && (strCardJoinNo.equals(AppConfig.getDataCodeProp("Basic"))
															||strCardJoinNo.equals(AppConfig.getDataCodeProp("Skypass"))
															||strCardJoinNo.equals(AppConfig.getDataCodeProp("AsianaClub")) )){ 
																		
								reResult = "09";
								System.out.print("## GolfLoginLogInsProc | ��ȸ�� | ����ī���� ����ȸ�� | Smart300 ���� ������ �̵� |  ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");															
							}	
							// ��� ����Ʈ ����� VIPī�� ���� ȸ�� ���θ���� �������������� ���� - ������ ���� ��û
							else if("Y".equals(vipCardYn) && offerResult  ){
								
								reResult = "15" ;
								System.out.print("## GolfLoginLogInsProc | ��ȸ�� | ����ī���� ����ȸ�� | Smart���  & vipī�� ����  : "+userEtt.getAccount()+" | offerResult  : " + offerResult  +" | reResult : "+reResult+" \n");									
							}								
							
							// 2���� ž���� ī��
							else if("Y".equals(topGolfCardYn))
							{																								
								reResult = "786";																						
								System.out.print("## GolfLoginLogInsProc | ��ȸ�� | ����ī���� ����ȸ�� | ž����ī�� ���� ������ �̵� | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");																							
							}
							// 5���� ��ġī��
							else if("Y".equals(richCardYn))
							{																
								reResult = "785";																						
								System.out.print("## GolfLoginLogInsProc | ��ȸ�� | ����ī���� ����ȸ�� | ��ġī�� ���� ������ �̵� | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
							}
							// 6���� VIPī�� 			
							else if("Y".equals(vipCardYn))
							{								
								reResult = "787";																						
								System.out.print("## GolfLoginLogInsProc | ��ȸ�� | ����ī���� ����ȸ�� | VIPī�� ���� ������ �̵� | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
							}
							else
							{														
								reResult = "02";
								System.out.print("## GolfLoginLogInsProc | ��ȸ�� | ����ī���� ����ȸ�� | ȸ���з��� 6(����ī��)�� ����ȸ��. �Ϲݰ����������� �̵� | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" | goLogin : "+goLogin+" \n");
							
							}
						}
						else
						{
							// ž����ī�� ������ �ٽ� �ѹ� ����ī����� Ȯ��
							if("Y".equals(topGolfCardYn))
							{											
								
								
								if("Y".equals(golfCardCoYn))
								{
									System.out.print("## GolfLoginLogInsProc | ��ȸ�� | ����ȸ�� | ž����ī�� ����ȸ�� ó�� ���� \n");									
									reResult = "786";																						
									System.out.print("## GolfLoginLogInsProc | ��ȸ�� | ����ȸ�� | ž����ī�� ���� ������ �̵� | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
								}																		
								else
								{
									reResult = "08";
									System.out.print("## GolfLoginLogInsProc | ��ȸ�� | ����ȸ�� | ȸ���з��� 6(����ī��)�̿��� ����ȸ��. alert ���� �̵�  | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");														
								}
							}
							else
							{
								reResult = "08";
								System.out.print("## GolfLoginLogInsProc | ��ȸ�� | ����ȸ�� | ȸ���з��� 6(����ī��)�̿��� ����ȸ��. alert ���� �̵�  | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");														
							}
							
						}						
					
					//�������̺� �������� ����
					}else{
						System.out.print("## GolfLoginLogInsProc | ��ȸ�� | ����ȸ���� �ƴ� ����ȸ�� ����ó�� ���� | ID : "+userEtt.getAccount()+" \n");
												
						//TMȸ������ üũ 
						pstmt = con.prepareStatement(getMemTmQuery());
						pstmt.setString(1, userEtt.getSocid());	
						pstmt.setString(2, userEtt.getSocid());
						rs2 = pstmt.executeQuery();
						
						if(rs2.next()){						
							//1.Ƽ����� -> �̹� ������ �����̹Ƿ� ���Ǹ� �ִ� �������� ������.
							reResult = "01";
							System.out.print("## GolfLoginLogInsProc | ��ȸ�� | TMȸ�� | TM �������� ������ | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
							
						}else{	
														
							// 1���� IBK APT�����̾� ī��
							if("Y".equals(golfCardYn) && ("740276".equals(strCardJoinNo) || "740289".equals(strCardJoinNo) || "740292".equals(strCardJoinNo)) )
							{											
								reResult = "09";
								System.out.print("## GolfLoginLogInsProc | ��ȸ�� | IBK APT�����̾� ī��ȸ�� | IBK APT�����̾� ī�尡������ ������ | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");			
							}
							// ��� ����Ʈ ����� VIPī�� ���� ȸ�� ���θ���� �������������� ���� - ������ ���� ��û
							else if("Y".equals(vipCardYn) && offerResult  ){
								
								reResult = "15" ;
								System.out.print("## GolfLoginLogInsProc | ��ȸ�� ���� |Smart���  & vipī�� ����  : "+userEtt.getAccount()+" | offerResult  : " + offerResult  +" | reResult : "+reResult+" \n");									
							}
							// 2���� ž���� ī��
							else if("Y".equals(topGolfCardYn))
							{								
								System.out.print("## GolfLoginLogInsProc | ��ȸ�� ž����ī�� ����ȸ�� ó�� ���� \n");									
								reResult = "786";																						
								System.out.print("## GolfLoginLogInsProc | ��ȸ�� | ž����ī�� ���� ������ �̵� | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
							}
							
							// 3���� ����NHī��
							else if("Y".equals(golfCardNhYn))
							{
								reResult = "10";
								System.out.print("## GolfLoginLogInsProc | ��ȸ�� | ����NHī�� ī��ȸ�� | ����NHī�� ī�尡������ ������ | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");			
							}	
							// 4���� �泲����ī��	
							else if("Y".equals(golfCardYn) && "394033".equals(strCardJoinNo) )
							{									
								reResult = "09";
								System.out.print("## GolfLoginLogInsProc | ��ȸ�� | ����ī��ȸ�� | ����ī�尡������ ������ | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");			
														
							}	
							// 5���� ��ġī��
							else if("Y".equals(richCardYn))
							{								
								System.out.print("## GolfLoginLogInsProc | ��ȸ�� ��ġī�� ����ȸ�� ó�� ���� \n");									
								reResult = "785";																						
								System.out.print("## GolfLoginLogInsProc | ��ȸ�� | ��ġī�� ���� ������ �̵� | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
							}
							// 6���� VIPī�� 			
							else if("Y".equals(vipCardYn))
							{
								System.out.print("## GolfLoginLogInsProc | ��ȸ�� | VIPī�� ����ȸ�� ó�� ���� \n");									
								reResult = "787";																						
								System.out.print("## GolfLoginLogInsProc | ��ȸ�� | VIPī�� ���� ������ �̵� | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
							}														
																				
							else
							{
								if("Y".equals(golfCardYn))
								{								
									reResult = "09";
									System.out.print("## GolfLoginLogInsProc | ��ȸ�� | ����ī��ȸ�� | ����ī�尡������ ������ | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");			
								}
								else
								{
									// I-pin ���� �߰� - �ֹε�Ϲ�ȣ�� ������ �ֹε�Ϲ�ȣ �Է��������� �ѱ��.

									sql = this.getUserInfoQuery(member_clss);  	// ȸ����޹�ȣ 1:���� / 5:����
						            userInfoPstmt = con.prepareStatement(sql);
						            userInfoPstmt.setString(1, userEtt.getAccount() );
						            userInfoRs = userInfoPstmt.executeQuery();	
						            
									if(userInfoRs.next()){
										jumin_no = userInfoRs.getString("SOCID");	// �̸���
									}
									
									if(userInfoRs != null) userInfoRs.close();
						            if(userInfoPstmt != null) userInfoPstmt.close();
						            
						            if(GolfUtil.empty(jumin_no) || jumin_no.equals("")){
										reResult = "11";
										System.out.print("## GolfLoginLogInsProc | ��ȸ�� | �ֹε�Ϲ�ȣ �Է����� : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
						            }else{
						            	reResult = "02";
										System.out.print("## GolfLoginLogInsProc | ��ȸ�� | �Ϲ�ȸ�� | �Ϲݰ������� ������ | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
						            }
								}
							}
							
							if("".equals(reResult)) reResult = "02";
							System.out.print("## GolfLoginLogInsProc | ��ȸ�� ���� ȸ�� | ���Ժз�ó����� | ID : "+userEtt.getAccount()+" | reResult : "+reResult+" \n");
														
						}					
					}					
					
				}
				
							
				if(!(member_clss.equals("1") || member_clss.equals("4")  || member_clss.equals("5") )){
					reResult = "05";
				}
				
				System.out.print("## GolfLoginLogInsProc  | ID : "+userEtt.getAccount()+" | MEMBER_CLSS : "+member_clss+" | reResult : "+reResult+" \n");			
				
				/////////////////////////////////////////////////////////////////////////////////
				
				// ������ ȸ��
				if(reResult.equals("02")){
					
					//������ ȸ��
					isIbkGold = execute_isIbkGold(con, userEtt.getSocid());
					if(isIbkGold>0){
						reResult = "06";
					}
					
				}				
				
				if(reResult.equals("02")){	// 02���� ���ο� �ش�Ǵ�..?
					
					//TM ���� ��ȸ�� ����� ����Ȯ�� �������� �̵�
					boolean isResult = monthProc.execute_newJoinMemYN(con, userEtt.getSocid());
					if(isResult){
						reResult = "15" ;
					}					
					
				}
				
				/////////////////////////////////////////////////////////////////////////////////
				//  goLogin �� True�� ��� �α��� ó�� ��޵� ����ó��
				String join_chnl = "";		// ���԰��
				if(goLogin){
					System.out.print("## GolfLoginLogInsProc | goLogin | 1-1.�α��ο� ���� ���� ���ǿ� �ֱ� ����  | ID : "+userEtt.getAccount()+" \n");	
					
					System.out.print("## GolfLoginLogInsProc | ��� �������� ================================ \n");
					pstmt = con.prepareStatement(getGrdQuery());
					pstmt.setString(1, userEtt.getAccount());	
					pstmt.setString(2, userEtt.getAccount());	
					rs_grd = pstmt.executeQuery();
					
					if(rs_grd != null){
						if(rs_grd.next()){
							intMemGrade = rs_grd.getInt("GRD_COMM");
							memGrade = rs_grd.getString("GRD_NM");
							intMemberGrade = rs_grd.getInt("GRD_MEM");
							intCardGrade = rs_grd.getInt("GRD_CARD");
							join_chnl = rs_grd.getString("JOIN_CHNL");
						}
					}

					System.out.print("## GolfLoginLogInsProc | ����Ÿ ���ռ� �Ͽ�ȭ ================================ \n");
					pstmt = con.prepareStatement(getMemSelQuery());
					idx = 0;
					pstmt.setString(++idx, userEtt.getAccount());
					pstmt.setString(++idx, userEtt.getAccount());
					rs_memInfo = pstmt.executeQuery();
					
					String upd_name = "N";
					String upd_mobile = "N";
					String upd_phone = "N";
					String upd_email = "N";
					String upd_zipcode = "N";
					String upd_addClss = "N";
					String upd_zipaddr = "N";
					String upd_detailaddr = "N";
					String upd_grade = "N";
					
					if(rs_memInfo != null){
						
						if(rs_memInfo.next()){
							
							if(!GolfUtil.empty(rs_memInfo.getString("HG_NM")) && !GolfUtil.empty(userEtt.getName())){ 
								if(!userEtt.getName().equals(rs_memInfo.getString("HG_NM"))) upd_name = "Y"; 
							}
							if(!GolfUtil.empty(rs_memInfo.getString("MOBILE")) && !GolfUtil.empty(userEtt.getMobile())){
								if(!userEtt.getMobile().equals(rs_memInfo.getString("MOBILE"))) upd_mobile = "Y"; 
							}
							if(!GolfUtil.empty(rs_memInfo.getString("PHONE")) && !GolfUtil.empty(userEtt.getPhone())){
								if(!userEtt.getPhone().equals(rs_memInfo.getString("PHONE"))) upd_phone = "Y"; 
							}
							if(!GolfUtil.empty(rs_memInfo.getString("EMAIL")) && !GolfUtil.empty(userEtt.getEmail1())){
								if(!userEtt.getEmail1().equals(rs_memInfo.getString("EMAIL"))) upd_email = "Y"; 
							}
							if(!GolfUtil.empty(rs_memInfo.getString("ZIP_CODE")) && !GolfUtil.empty(userEtt.getZipcode())){
								if(!userEtt.getZipcode().equals(rs_memInfo.getString("ZIP_CODE"))) upd_zipcode = "Y"; 
							}
							
							if(!GolfUtil.empty(rs_memInfo.getString("NW_OLD_ADDR_CLSS")) && !GolfUtil.empty(userEtt.getNwOldAddrClss())){
								if(!userEtt.getNwOldAddrClss().equals(rs_memInfo.getString("NW_OLD_ADDR_CLSS"))) upd_addClss = "Y"; 
							}
														
							if (  userEtt.getNwOldAddrClss() != null ){
								if ( userEtt.getNwOldAddrClss().equals("1")){
								
									if(!GolfUtil.empty(rs_memInfo.getString("ZIPADDR")) && !GolfUtil.empty(userEtt.getZipaddr())){
										if(!userEtt.getZipaddr().equals(rs_memInfo.getString("ZIPADDR"))) upd_zipaddr = "Y"; 
									}
									if(!GolfUtil.empty(rs_memInfo.getString("DETAILADDR")) && !GolfUtil.empty(userEtt.getDetailaddr())){
										if(!userEtt.getDetailaddr().equals(rs_memInfo.getString("DETAILADDR"))) upd_detailaddr = "Y"; 
									}								
									
								}else if ( userEtt.getNwOldAddrClss().equals("2")){
									
									if(!GolfUtil.empty(rs_memInfo.getString("ZIPADDR")) && !GolfUtil.empty(userEtt.getDongOvrNewAddr())){
										if(!userEtt.getDongOvrNewAddr().equals(rs_memInfo.getString("ZIPADDR"))) upd_zipaddr = "Y"; 
									}
									if(!GolfUtil.empty(rs_memInfo.getString("DETAILADDR")) && !GolfUtil.empty(userEtt.getDongBlwNewAddr())){
										if(!userEtt.getDongBlwNewAddr().equals(rs_memInfo.getString("DETAILADDR"))) upd_detailaddr = "Y"; 
									}								
									
								}
							}
							
							if(!GolfUtil.empty(rs_memInfo.getString("CDHD_CTGO_SEQ_NO")) && !GolfUtil.empty(rs_memInfo.getString("TOP_CDHD_CTGO_SEQ_NO"))){
								if(!rs_memInfo.getString("TOP_CDHD_CTGO_SEQ_NO").equals(rs_memInfo.getString("CDHD_CTGO_SEQ_NO"))) upd_grade = "Y";
							}
						
						}
						
					} 			
					
					pstmt = con.prepareStatement(getMemUpdQuery(intMemGrade, upd_name, upd_mobile, upd_phone, upd_email, upd_zipcode, upd_zipaddr, upd_detailaddr, upd_addClss, upd_grade));
					idx = 0;
					if(upd_name.equals("Y"))		pstmt.setString(++idx, userEtt.getName());	
					if(upd_mobile.equals("Y"))		pstmt.setString(++idx, userEtt.getMobile());
					if(upd_phone.equals("Y"))		pstmt.setString(++idx, userEtt.getPhone());
					if(upd_email.equals("Y"))		pstmt.setString(++idx, userEtt.getEmail1());
					if(upd_zipcode.equals("Y"))		pstmt.setString(++idx, userEtt.getZipcode());
					
					if (  userEtt.getNwOldAddrClss() != null ){
						if ( userEtt.getNwOldAddrClss().equals("1")){
							if(upd_zipaddr.equals("Y"))		pstmt.setString(++idx, userEtt.getZipaddr());
							if(upd_detailaddr.equals("Y"))	pstmt.setString(++idx, userEtt.getDetailaddr());
						}else if ( userEtt.getNwOldAddrClss().equals("2")){
							if(upd_zipaddr.equals("Y"))		pstmt.setString(++idx, userEtt.getDongOvrNewAddr());
							if(upd_detailaddr.equals("Y"))	pstmt.setString(++idx, userEtt.getDongBlwNewAddr());
							
						}
					}
					
					if(upd_addClss.equals("Y"))		pstmt.setString(++idx, userEtt.getNwOldAddrClss());
					if(upd_grade.equals("Y"))		pstmt.setString(++idx, rs_memInfo.getString("TOP_CDHD_CTGO_SEQ_NO"));
					pstmt.setString(++idx, userEtt.getAccount());
					pstmt.executeUpdate();
					
//					debug("name : " + userEtt.getName() + " / ���� : " + userEtt.getMobile() + " / ��ȭ : " + userEtt.getPhone() 
//							+ " / �̸��� : " + userEtt.getEmail1() + " / �����ȣ : " + userEtt.getZipcode() + " / �ּ� : " + userEtt.getZipaddr() 
//							+ " / ���ּ� : " + userEtt.getDetailaddr() + " / intMemGrade : " + intMemGrade
//							+ " / upd_name : " + upd_name + " / upd_mobile : " + upd_mobile + " / upd_phone : " + upd_phone + " / upd_email : " + upd_email
//							 + " / upd_zipcode : " + upd_zipcode + " / upd_zipaddr : " + upd_zipaddr + " / upd_detailaddr : " + upd_detailaddr + " / upd_grade : " + upd_grade
//							);  


					//�ֱ��������ڴ� ������ ���ռ��� �Բ�
//					System.out.print("## GolfLoginLogInsProc | �ֱ��������� ������Ʈ ================================ \n");
//					pstmt = con.prepareStatement(getUpdAccessQuery());
//		        	pstmt.setString(1, userEtt.getAccount() ); 
//					pstmt.executeUpdate();		

					System.out.print("## GolfLoginLogInsProc | ���̹��Ӵ� �������� ================================ \n");
					pstmt = con.prepareStatement(getCyberMoneyQuery());
					pstmt.setString(1, userEtt.getAccount());	
					rs3 = pstmt.executeQuery();
					
					if(rs3 != null){
						while(rs3.next()){
							userEtt.setCyberMoney((int)rs3.getInt("TOT_AMT"));
						}
					}
				}
				
				userEtt.setIntMemGrade(intMemGrade);		//������
				userEtt.setIntMemberGrade(intMemberGrade);	//��������ó��
				userEtt.setIntCardGrade(intCardGrade);		//ī����ó��
				userEtt.setMemGrade(memGrade);				//��޸�
				userEtt.setEmail1(memEmail);
				userEtt.setMobile(memMobile);
				
				debug("## GolfLoginLogInsProc | intMemGrade : "+userEtt.getIntMemGrade() + " / intMemberGrade : "+userEtt.getIntMemberGrade()
						+" / intCardGrade : "+userEtt.getIntCardGrade() + " / memGrade : "+userEtt.getMemGrade() + " / reResult : "+reResult);

				result.addString("RESULT", reResult);
				result.addString("end_date", end_date);
				result.addString("join_chnl", join_chnl);
								
			} 
							
			
		} catch(Exception e){
			// Ʈ������ �����϶��� �ѹ�
			try {
				if (!con.getAutoCommit()) {
					con.rollback();
				}
			} catch (Exception ex) {}
			
			MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, strMESSAGE_KEY, null );
			msgEtt.addEvent( actnKey + ".do", "/golf/img/common/btn/btn_confirmation.gif");
			throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs  != null) rs.close(); } catch( Exception ignored){}
			try { if(rs2  != null) rs2.close(); } catch( Exception ignored){}
			try { if(rs3  != null) rs3.close(); } catch( Exception ignored){}
			try { if(rs4  != null) rs4.close(); } catch( Exception ignored){}
			try { if(rs_grd  != null) rs_grd.close(); } catch( Exception ignored){}
			try { if(rs_membership  != null) rs_membership.close(); } catch( Exception ignored){}
			try { if(rs_end_date  != null) rs_end_date.close(); } catch( Exception ignored){}
			try { if(pstmt != null) pstmt.close(); } catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		
		return result;
			
		
	}
	public String memCk(Connection con, String intMemGrade) throws DbTaoException  {
		String strResult = "N";
		
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		
		try {		
			
			debug("## memCk | intMemGrade : "+intMemGrade);
			
			sql = this.getMemberCkFreeQuery(); 
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();	
           
            while (rs.next())	{
            	
            	String ckNum = rs.getString("CDHD_CTGO_SEQ_NO").replaceAll("0", "");
            	//debug("## memCk | ��ȿ��޿��� CDHD_CTGO_SEQ_NO : "+rs.getString("CDHD_CTGO_SEQ_NO")+"==> ckNum : "+ckNum);
            	
            	
            	if(intMemGrade.equals(ckNum))
            	{
            		strResult = "Y";
            	}
            	
            }
                            
            //���Ῥȸ�� ī�� �߰� 12 (8) : NHƼŸ�� , 13 (9) : NH�÷�Ƽ�� , 14 () : NH�йи�, 20 (15) : APT �����̾�, 19 (14) : �泲���� Familyī��, 21 (16): ž����, 22 (17): ��ġī��, ���Ͻñ״�ó 29
            if("12".equals(intMemGrade) || "13".equals(intMemGrade) || "14".equals(intMemGrade) 
            		|| "20".equals(intMemGrade) || "19".equals(intMemGrade) || "21".equals(intMemGrade) || "22".equals(intMemGrade) 
                        || "29".equals(intMemGrade) )
            {
            	strResult = "Y";
            }  
            
			
            debug("## memCk | strResult : "+strResult);
			
		
		} catch(Exception e) {
						
            //MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "�ý��ۿ����Դϴ�." );
            //throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}            
		}
		return strResult;
	}
	/** ***********************************************************************
	* �����ϵ� ���̵����� �˾ƺ���    
	************************************************************************ */
	private String getMemberCkFreeQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  SELECT CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHDCTGOMGMT A		\n");
		sql.append("\t  WHERE A.CDHD_SQ1_CTGO = '0002' AND ANL_FEE > 0			\n");
		return sql.toString();
	}
	/** 
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */ 
	public int execute_card(Connection con, String memId, String cardYn, String bnNo, String cardGb) throws TaoException {

		String title				= "ī�� ���ó��";
		String sql 					= "";
		PreparedStatement pstmt		= null;
		ResultSet rs 				= null;
		int result					= 0;		
		
		try {

			String cardRegDate 	= "";	//ī���ϳ�¥
			String toDate 		= DateUtil.currdate("yyyyMMdd");		//���ó�¥
			String tolDate 		= DateUtil.currdate("yyyyMMddHHmmss");		//���ó�¥
			String fromDate		= "";
			String toChgDate	= "";
			String memGrade		= "";	// ȸ����ǥ���
			String bnNo2		= "";
			
			debug("## golfCardLog | execute_card | memId : "+memId);
			debug("## golfCardLog | execute_card | cardYn : "+cardYn);
			debug("## golfCardLog | execute_card | bnNo : "+bnNo);
			debug("## golfCardLog | execute_card | cardGb : "+cardGb);

			if("Y".equals(cardYn))
			{
				System.out.print("## golfCardLog : ī�� ���� \n");
				
				//VIP�� ���� �������� �ڵ�ī���޺ο�
				if(!"vip".equals(cardGb))
				{
					// �ش� ī�尡 ��ϵǾ� �ִ��� Ȯ�� getGrdDateQuery
					pstmt = con.prepareStatement(getGrdDateQuery());
					pstmt.setString(1, memId);	
					pstmt.setString(2, bnNo);	
					rs = pstmt.executeQuery();
					if(rs != null && rs.next())
					{
						
						debug("## execute_card | �ش� ī�� ��� �̹� ����");
						// ī���ϳ�¥ ��ȸ 
						cardRegDate = rs.getString("REG_ATON").substring(0,8);									
						fromDate	= DateUtil.dateAdd('M', 12, cardRegDate, "yyyyMMdd");

						//ī��ȸ���� ī���ϳ�¥�� ����� ��� �ڵ����� ó��
						//if( Float.valueOf(toDate).floatValue() > Float.valueOf(fromDate).floatValue() )
						if( Integer.parseInt(toDate) > Integer.parseInt(fromDate) )
						{	
							System.out.print("## golfCardLog : ī���ϳ�¥ �ڵ����� ó�� | fromDate : "+fromDate+" | toDate : "+toDate+"\n");
							toChgDate	= DateUtil.dateAdd('M', 12, tolDate, "yyyyMMddHHmmss");
							
							pstmt = con.prepareStatement(getCardDateUpdQuery());
							pstmt.setString(1, toChgDate);
							pstmt.setString(2, memId);
							pstmt.executeUpdate();
						}
						
					}
					else
					{
						debug("## execute_card | �ش� ī�� ��� ���� ī���� �ֱ�");
						pstmt = con.prepareStatement(getMemTopGolfQuery());
						pstmt.setString(1, memId ); 
						rs = pstmt.executeQuery();
						if(rs.next()){
							
							String memGrd = rs.getString("CDHD_CTGO_SEQ_NO"); //ȸ��������
							
							debug("## ���� ȸ���� ID : "+memId+" | memGrd : "+memGrd);
							
							// ��ϵǾ� ���� �ʴٸ� ī�� �μ�Ʈ ���ش�.
							/**SEQ_NO ��������**************************************************************/
							sql = this.getNextValQuery(); 
				            pstmt = con.prepareStatement(sql);
				            rs = pstmt.executeQuery();			
							long max_seq_no = 0L;
							if(rs.next()){
								max_seq_no = rs.getLong("SEQ_NO");
							}
				            
				            /**Insert************************************************************************/
				            sql = this.getInsertGradeQuery();
							pstmt = con.prepareStatement(sql);
							
				        	pstmt.setLong(1, max_seq_no ); 
				        	pstmt.setString(2, memId ); 
				        	pstmt.setString(3, bnNo );
				        	
							pstmt.executeUpdate();
							
							// ���� �ڱ��޺��� ���� ����� �ִ��� �˾ƺ���.getGrdChgQuery => �ٲ��ش�.
							pstmt = con.prepareStatement(getGrdChgQuery());
							pstmt.setString(1, bnNo);	
							pstmt.setString(2, memId);	
							rs = pstmt.executeQuery();
							if(rs != null)
							{
								if(rs.next())
								{
									if("Y".equals(rs.getString("CHG_YN")))
									{
										
										if("topGolf".equals(cardGb) || "rich".equals(cardGb) 
                                                                                  || "jb".equals(cardGb))
										{
											pstmt = con.prepareStatement(getUpdGradeDateQuery());
										}
										else
										{
											pstmt = con.prepareStatement(getUpdGradeQuery());
										}
										
										pstmt.setString(1, bnNo);	
										pstmt.setString(2, memId);	
										pstmt.executeQuery();
									}
								}
							}
						}
						else
						{
							debug("## ���� ȸ���� �ƴ�");
						}
					}
					
				}

				if(bnNo.equals("12")){
					bnNo2 = "13";
				}else{
					bnNo2 = "12";
				}

				pstmt = con.prepareStatement(getGrdDateQuery());
				pstmt.setString(1, memId);	
				pstmt.setString(2, bnNo2);	
				rs = pstmt.executeQuery();
				
				if(rs != null && rs.next()){

					pstmt = con.prepareStatement(getGrdDelQuery());
					pstmt.setString(1, rs.getString("CDHD_GRD_SEQ_NO"));	
					pstmt.executeUpdate();
				}
				
			}
			else
			{
				System.out.print("## golfCardLog : ī�� ���� \n");
				
				//VIP�� ���� �������� �ڵ�ī���޺ο�
				if(!"vip".equals(cardGb))
				{
					pstmt = con.prepareStatement(getChkGradeQuery(cardGb));
					pstmt.setString(1, memId);	
					rs = pstmt.executeQuery();
					
					if(rs != null)
					{
						if(rs.next())
						{
							memGrade = rs.getString("CDHD_CTGO_SEQ_NO");
							// ����	
							pstmt = con.prepareStatement(getDelGrdQuery(cardGb));
							pstmt.setString(1, memId);	
							pstmt.executeQuery();
							
							// ����� �ϳ��� ������ Ż��ó�� getCntGrdQuery -> 20100310 ȭ��Ʈ�� ������.
							pstmt = con.prepareStatement(getCntGrdQuery());
							pstmt.setString(1, memId);	
							rs = pstmt.executeQuery();
							
							if(rs != null)
							{
								if(rs.next())
								{
									if(rs.getInt("CNT_GRD")==0)
									{
										// ����ó��
//										pstmt = con.prepareStatement(getClssmUpdQuery());	
//										pstmt.setString(1, memId);
//										pstmt.executeUpdate();	
										
										// ȭ��Ʈ ȸ��ó�� - ������̺�
										pstmt = con.prepareStatement(getWhiteUpdateQuery());	
										pstmt.setString(1, memId);
										pstmt.executeUpdate();	

										// ȭ��Ʈ ȸ��ó�� - ������̺�
										sql = this.getNextValQuery(); 
							            pstmt = con.prepareStatement(sql);
							            rs = pstmt.executeQuery();			
										long max_seq_no = 0L;
										if(rs.next()){
											max_seq_no = rs.getLong("SEQ_NO");
										}
										if(rs != null) rs.close();
							            if(pstmt != null) pstmt.close();
							            
							            sql = this.getInsertGradeQuery();
										pstmt = con.prepareStatement(sql);
										
							        	pstmt.setLong(1, max_seq_no ); 
							        	pstmt.setString(2, memId ); 
							        	pstmt.setString(3, "8" );
							        	
										pstmt.executeUpdate();
										
									}
									else
									{
										// ����� ���� ������, ��ǥ����� ī���� ��� ��ǥ����� �������ش�.									
										if( ( ( "ibk".equals(cardGb) || "vip".equals(cardGb) ) 
												&& ( "9".equals(memGrade) || "10".equals(memGrade) || "19".equals(memGrade) )
											) 
											|| 
											( "nh".equals(cardGb) 
											  && ("12".equals(memGrade) || "13".equals(memGrade) || "14".equals(memGrade))
											)
										){
											
											pstmt = con.prepareStatement(getTopGradeQuery());
											pstmt.setString(1, memId);	
											rs = pstmt.executeQuery();
											if(rs != null)
											{
												if(rs.next())
												{
													pstmt = con.prepareStatement(getUpdGradeQuery());
													pstmt.setString(1, rs.getString("CDHD_CTGO_SEQ_NO"));
													pstmt.setString(2, memId);
													pstmt.executeUpdate();	
												}
											}
												
										}
									}
								}
							}
							
						}
					}
					
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
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
            try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}			
		return result;
		
	}	

	// ������� ��� ȸ������ �˾ƺ���.
	public int execute_isIbkGold(Connection con, String socId) throws TaoException {

		String title				= "������� ��� ȸ������ �˾ƺ���.";
		String sql 					= "";
		PreparedStatement pstmt		= null;
		ResultSet rs 				= null;
		ResultSet rs_again 			= null;
		ResultSet rs_used 			= null;
		int result					= 0;
		
		try {

			sql = getIbkGoldQuery();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, socId);	
			rs = pstmt.executeQuery();
			if(rs != null && rs.next()){
				result = 1;
			}else{
				
				sql = getUsedIbkGoldQuery();			// �̹� ����� ������ �ִ°�?
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, socId);	
				rs_again = pstmt.executeQuery();
				if(rs_again != null && rs_again.next()){
					
					sql = getUsedIbkGoldMemQuery();		// ���� ȸ�������� ���볻���� �ִ°�?
					pstmt = con.prepareStatement(sql);
					pstmt.setString(1, socId);	
					pstmt.setString(2, socId);	
					rs_used = pstmt.executeQuery();
					if(!rs_used.next()){				// �̹� ����Ǿ����� ���� ȸ���������� ����� ������ ������ �ٽ� �ڰ� ȹ��
						result = 1;
					}
				}
				
			}


			if(rs != null) rs.close();
			if(rs_again != null) rs_again.close();
			if(rs_used != null) rs_again.close();
            if(pstmt != null) pstmt.close();
			
		} catch(Exception e) {
			try	{
				con.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
            try { if(rs_again  != null) rs_again.close();  } catch (Exception ignored) {}
            try { if(rs_used  != null) rs_used.close();  } catch (Exception ignored) {}
            try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}			
		return result;
		
	}

	// ����� ����� ������ �ִ��� �˾ƺ���.
	public int execute_isMembership(Connection con, String userId) throws TaoException {

		String title				= "����� ����� ������ �ִ��� �˾ƺ���.";
		String sql 					= "";
		PreparedStatement pstmt		= null;
		ResultSet rs 				= null;
		int result					= 0;
		
		try {
			
			sql = getMemberShipQuery();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, userId);	
			rs = pstmt.executeQuery();
			if(rs != null){
				while(rs.next()){
					result = rs.getInt("CDHD_CTGO_SEQ_NO");
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
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
            try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}			
		return result;
		
	}
	

	// ������� ��� ȸ������ ������Ʈ�Ѵ�.
	public int execute_updIbkGold(Connection con, String socId, String memId) throws TaoException {

		String title				= "������� ��� ȸ������ �˾ƺ���.";
		String sql 					= "";
		PreparedStatement pstmt		= null;
		ResultSet rs 				= null;
		
		int idx = 0;
		int resultExecute = 0;
							
		
		try {

			String joinChnl				= "";	// ����ȸ�����̺� ���԰�α��� �ڵ�
			String cdhd_ctgo_seq_no		= "";	// ȸ���з��Ϸù�ȣ = ��ǥ���
			
			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");   
			GregorianCalendar cal = new GregorianCalendar(); 
	        Date stdate = cal.getTime();
	        String strStDate = fmt.format(stdate);	// ����ȸ���Ⱓ ������
	        
	        cal.add(cal.MONTH, 2);
	        Date edDate = cal.getTime();
	        String strEdDate = fmt.format(edDate);	// ����ȸ���Ⱓ ������
	        
			sql = getIbkGoldInfoQuery();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, socId);	
			rs = pstmt.executeQuery();
			if(rs != null && rs.next()){

				joinChnl 	= rs.getString("RCRU_PL_CLSS");
				cdhd_ctgo_seq_no 	= "18";

				// ȸ�����̺� ������Ʈ - ��ǥ���, ����ä��
				DbTaoDataSet dataSet = new DbTaoDataSet(title);
				dataSet.setString("moneyType", "13");
				dataSet.setString("joinChnl", joinChnl);
				dataSet.setString("cdhd_ctgo_seq_no", cdhd_ctgo_seq_no);
				dataSet.setString("strStDate", strStDate);
				dataSet.setString("strEdDate", strEdDate);
				dataSet.setString("memId", memId);
				dataSet.setString("socId", socId);
				
				// ȸ�� ���̺� ������Ʈ
				resultExecute = execute_updMem(con, dataSet);
				//debug("ȸ�� ���̺� ������Ʈ ��� :: resultExecute : " + resultExecute);
		        
				// ���� �� �̺�Ʈ ���̺� ������Ʈ
		        if(resultExecute>0){
		            sql = this.getUpdIbkGoldEndQuery();
					pstmt = con.prepareStatement(sql);
					idx = 0;
		        	pstmt.setString(++idx, memId );
		        	pstmt.setString(++idx, socId );
		        					        	
		        	resultExecute = pstmt.executeUpdate();
					//debug("���� �� �̺�Ʈ ���̺� ������Ʈ :: resultExecute : " + resultExecute);
		        }
			}

			
		} catch(Exception e) {
			try	{
				con.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
            try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}			
		return resultExecute;
		
	}	

	// ���������� ������ �ִ�, ������� ��� ȸ�� => ����ȸ���Ⱓ 2�� ����
	public String execute_updPeriodIbkGold(Connection con, String socId, String memId) throws TaoException {

		String title				= "���������� ������ �ִ�, ������� ��� ȸ�� => ����ȸ���Ⱓ 2�� ����";
		PreparedStatement pstmt		= null;
		ResultSet rs 				= null;
		
		int resultExecute = 0;
		String end_date = "";
		
		try {

			con.setAutoCommit(false);			

			// 2�� ����
			pstmt = con.prepareStatement(getIbkMemUpdQuery());
			pstmt.setString(1, memId);	
			resultExecute = pstmt.executeUpdate();
			
			if(resultExecute>0){

				// ������� ȸ�� ���̺� ���ԿϷ�� ������Ʈ
				pstmt = con.prepareStatement(getUpdIbkGoldEndQuery());
	        	pstmt.setString(1, memId );
	        	pstmt.setString(2, socId );
	        	resultExecute = pstmt.executeUpdate();
	        	
			}
			

			if(resultExecute>0){			
				con.commit();
				
				// ������ ��������
				pstmt = con.prepareStatement(getMemEndQuery());
				pstmt.setString(1, memId);	
				rs = pstmt.executeQuery();
				if(rs.next()){
					end_date = rs.getString("END_DATE");		        	
				}
			}else{
				con.rollback();
			}


			
		} catch(Exception e) {
			try	{
				con.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
            try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}			
		return end_date;
		
	}

	// ��������� ȸ�� ������Ʈ
	public int execute_updMem(Connection con, TaoDataSet data) throws TaoException {

		String title				= "ȸ�� ������Ʈ";
		String sql 					= "";
		PreparedStatement pstmt		= null;
		ResultSet rs 				= null;
		int result					= 0;
		int idx						= 0;
		
		try {

			sql = this.getInsGrdHistoryQuery();
			pstmt = con.prepareStatement(sql);
			idx = 0;
        	pstmt.setString(++idx, data.getString("memId") );
        	result = pstmt.executeUpdate();
        	
        	if(result>0){
				sql = this.getUpdMemIbkGoldQuery();
				pstmt = con.prepareStatement(sql);
				idx = 0;
	        	pstmt.setString(++idx, data.getString("cdhd_ctgo_seq_no") );
	        	pstmt.setString(++idx, data.getString("joinChnl") );
	        	pstmt.setString(++idx, data.getString("strStDate") );
	        	pstmt.setString(++idx, data.getString("strEdDate") );
	        	pstmt.setString(++idx, data.getString("memId") );
	        	result = pstmt.executeUpdate();

				sql = this.getUpdGrdQuery();
				pstmt = con.prepareStatement(sql);
				idx = 0;
	        	pstmt.setString(++idx, data.getString("cdhd_ctgo_seq_no") );
	        	pstmt.setString(++idx, data.getString("memId") );
	        	result = pstmt.executeUpdate();
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
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
            try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}			
		return result;
		
	}
	

	// ����ȸ���Ⱓ ���� - TM
	public int execute_exPeriod(Connection con, String socId, String memId) throws TaoException {

		String title				= "����ȸ���Ⱓ ����";
		String sql 					= "";
		PreparedStatement pstmt		= null;
		ResultSet rs 				= null;
		int result					= 0;
		int idx						= 0;
		
		try {
			String tm_golf_cdhd_grd_clss = "";		// ����ȸ�� ��� �����ڵ� 1:���, 2:���, 3:è�ǿ�
			String golf_cdhd_grd_clss = "";			// ������Ʈ�� ȸ�� ���
			
			// TM ���̺� ���� ���� ��������
			sql = this.getExPeriodQuery();
			pstmt = con.prepareStatement(sql);
			idx = 0;
        	pstmt.setString(++idx, socId );
			rs = pstmt.executeQuery();
			if(rs != null && rs.next()){

				// ���� ����ʵ�� ��޹�ȣ��������
				int isMembershiop 	= execute_isMembership(con, memId);	// ����� ȸ�� ����
				
				// ����ȸ���Ⱓ ������Ʈ 
				pstmt = con.prepareStatement(getAcrgUpdQuery());
				pstmt.setString(1, memId);
				result = pstmt.executeUpdate();
				
				if(result>0){
				
					// ������Ʈ ��� �˾ƺ���
					tm_golf_cdhd_grd_clss = rs.getString("GOLF_CDHD_GRD_CLSS");
					if(tm_golf_cdhd_grd_clss.equals("1")){
						golf_cdhd_grd_clss = "7";
					}else if(tm_golf_cdhd_grd_clss.equals("2")){
						golf_cdhd_grd_clss = "6";
					}else if(tm_golf_cdhd_grd_clss.equals("3")){
						golf_cdhd_grd_clss = "5";
					}
					
					// �����ް� ���� ����� ���� �ʴٸ� ����� ������Ʈ ���ش�.
					if(isMembershiop!=Integer.parseInt(golf_cdhd_grd_clss)){
	
						// ��� �����丮 ���̺� �μ�Ʈ���ش�.
						pstmt = con.prepareStatement(getInsGrdHistoryQuery());
						idx = 0;
			        	pstmt.setString(++idx, memId );
			        	result = pstmt.executeUpdate();
						
			        	if(result>0){
							// ��� ������Ʈ - getGrdUpdTMQuery
							pstmt = con.prepareStatement(getGrdUpdTMQuery());
							idx = 0;
							pstmt.setString(++idx, golf_cdhd_grd_clss);
							pstmt.setString(++idx, "����ȸ���Ⱓ ���� TM");
							pstmt.setString(++idx, isMembershiop+"");
							pstmt.setString(++idx, memId);
							result = pstmt.executeUpdate();		
							
							if(result>0){
							// ���� �ڱ��޺��� ���� ����� �ִ��� �˾ƺ���.getGrdChgQuery => �ٲ��ش�.
								pstmt = con.prepareStatement(getGrdChgQuery());
								idx = 0;
								pstmt.setString(++idx, golf_cdhd_grd_clss);	
								pstmt.setString(++idx, memId);	
								rs = pstmt.executeQuery();
								if(rs != null)
								{
									if(rs.next())
									{
										if("Y".equals(rs.getString("CHG_YN")))
										{
											// ������ ����� ���� ��޵麸�� ���ٸ� ��ǥ ����� ������Ʈ���ش�.
											pstmt = con.prepareStatement(getUpdGradeQuery());
											idx = 0;
											pstmt.setString(++idx, golf_cdhd_grd_clss);	
											pstmt.setString(++idx, memId);	
											result = pstmt.executeUpdate();
										}
									}
								}
							}
			        	}
						
					}

					if(result>0){
						// TM ���̺� ������Ʈ getUpdTmQuery
						pstmt = con.prepareStatement(getUpdTmQuery());
						idx = 0;
						pstmt.setString(++idx, memId);	
						pstmt.setString(++idx, socId);	
						pstmt.setString(++idx, tm_golf_cdhd_grd_clss);	
						result = pstmt.executeUpdate();
					}
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
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
            try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}			
		return result;
		
	}	
	/**
	 * ī���ϳ�¥ ���� ������Ʈ
	 * @param b_where
	 * @param m_where
	 * @param s_order_kind
	 * @return
	 */
	private String getMemTopGolfQuery() {
	  
		StringBuffer sql = new StringBuffer();

		sql.append("\t	SELECT CDHD_CTGO_SEQ_NO FROM BCDBA.TBGGOLFCDHD  	\n");
		sql.append("\t	WHERE CDHD_ID=?		\n");

		return sql.toString();
		
	}
	
	/**
	 * ī��ȸ������ Ż��ó��
	 * @param b_where
	 * @param m_where
	 * @param s_order_kind
	 * @return
	 */
	private String getClssmUpdQuery() {
	  
		StringBuffer sql = new StringBuffer();

		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD 	\n");
		sql.append("\t	SET SECE_YN = 'Y' , SECE_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')  	\n");
		sql.append("\t	WHERE CDHD_ID=?		\n");

		return sql.toString();
		
	}
	/**
	 * ī���ϳ�¥ ���� ������Ʈ
	 * @param b_where
	 * @param m_where
	 * @param s_order_kind
	 * @return
	 */
	private String getCardDateUpdQuery() {
	  
		StringBuffer sql = new StringBuffer();

		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHDGRDMGMT 	\n");
		sql.append("\t	SET REG_ATON = ?	\n");
		sql.append("\t	WHERE CDHD_ID=?		\n");

		return sql.toString();
		
	}
	
	/**
	 * ��� ����
	 * @param b_where
	 * @param m_where
	 * @param s_order_kind
	 * @return
	 */
	private String getCountQuery() {
	  
		StringBuffer sql = new StringBuffer();

		sql.append("\n");
		sql.append("\t	SELECT T1.CDHD_ID, T1.ACRG_CDHD_END_DATE, SUBSTR(T1.SECE_ATON,1,8) AS SECE_ATON	\n");
		sql.append("\t	, TO_CHAR(ADD_MONTHS(TO_DATE(SUBSTR(T1.SECE_ATON,1,8)),1),'YYYYMMDD') AS SECE_ADD_MONTH	\n");
		sql.append("\t	, TO_CHAR(SYSDATE,'YYYYMMDD') AS NOW_DATE, T1.JUMIN_NO, T2.MEMBER_CLSS	\n");
		sql.append("\t	, CASE WHEN ACRG_CDHD_END_DATE>SUBSTR(T1.SECE_ATON,1,8) THEN 'Y' ELSE 'N' END AS CHARGE_MEM	\n");
		sql.append("\t	, CASE WHEN ADD_MONTHS(TO_DATE(SUBSTR(T1.SECE_ATON,1,8)),1)>SYSDATE THEN 'N' ELSE 'Y' END AS JOIN_RE	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHD T1	\n");
		sql.append("\t	JOIN BCDBA.UCUSRINFO T2 ON T1.CDHD_ID=T2.ACCOUNT	\n");
		sql.append("\t	WHERE T1.CDHD_ID=?		\n");
		return sql.toString();		
	}
	
	/**
	 * ����ȸ�� MemClss ��� ����
	 * @param b_where
	 * @param m_where
	 * @param s_order_kind
	 * @return
	 */
	private String getCountMemClssQuery() {
	  
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t	SELECT T2.MEMBER_CLSS	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHD T1	\n");
		sql.append("\t	JOIN BCDBA.UCUSRINFO T2 ON T1.CDHD_ID=T2.ACCOUNT	\n");
		sql.append("\t	WHERE T1.CDHD_ID=?		\n");
		return sql.toString();		
	}
	
	/**
	 * ����ȸ�� MemClss ��� ����
	 * @param b_where
	 * @param m_where
	 * @param s_order_kind
	 * @return
	 */
	private String getCountMemOnlyClssQuery() {
	  
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t	SELECT T2.MEMBER_CLSS	\n");
		sql.append("\t	FROM BCDBA.UCUSRINFO T2 	\n");
		sql.append("\t	WHERE T2.ACCOUNT=?		\n");
		return sql.toString();		
	}
	
	
	
	/**
	 * sso id �� ���� ����ȸ�� ���̺�(TBGGOLFCDHD) ���� ���� üũ
	 * @param b_where
	 * @param m_where
	 * @param s_order_kind
	 * @return
	 */
	private String getCountCkQuery() {
	  
		StringBuffer sql = new StringBuffer();

		sql.append("\n");
		sql.append("\t	SELECT T1.CDHD_ID, T1.ACRG_CDHD_END_DATE, SUBSTR(T1.SECE_ATON,1,8) AS SECE_ATON	\n");
		sql.append("\t	, TO_CHAR(ADD_MONTHS(TO_DATE(SUBSTR(T1.SECE_ATON,1,8)),1),'YYYYMMDD') AS SECE_ADD_MONTH	\n");
		sql.append("\t	, TO_CHAR(SYSDATE,'YYYYMMDD') AS NOW_DATE, T1.JUMIN_NO	\n");
		sql.append("\t	, CASE WHEN ACRG_CDHD_END_DATE>SUBSTR(T1.SECE_ATON,1,8) THEN 'Y' ELSE 'N' END AS CHARGE_MEM	\n");
		sql.append("\t	, CASE WHEN ADD_MONTHS(TO_DATE(SUBSTR(T1.SECE_ATON,1,8)),1)>SYSDATE THEN 'N' ELSE 'Y' END AS JOIN_RE	\n");
		sql.append("\t	, T2.VRTL_JUMIN_NO, T2.SOCID, T1.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHD T1	\n");
		sql.append("\t	LEFT JOIN BCDBA.UCUSRINFO T2 ON T1.CDHD_ID=T2.ACCOUNT	\n");
		sql.append("\t	WHERE T1.CDHD_ID=?		\n");
		return sql.toString();		
	}
	
	
	
	/**
	 * Ƽ������ ����ߴ��� �˻�
	 * @param b_where
	 * @param m_where
	 * @param s_order_kind 
	 * @return
	 */
	private String getMemTmQuery() {
	  
		StringBuffer sql = new StringBuffer();
		//���� ���� 2009.09.08 //���� ���� 2009.12.10
		sql.append("\n");
		sql.append("\t	SELECT ROWNUM RNUM, MB_CDHD_NO, TB_RSLT_CLSS, JUMIN_NO, RND_CD_CLSS, GOLF_CDHD_GRD_CLSS, WK_DATE, RCRU_PL_CLSS	\n");
		sql.append("\t	FROM (	\n");
		sql.append("\t	    SELECT MB_CDHD_NO, TB_RSLT_CLSS, JUMIN_NO, RND_CD_CLSS, GOLF_CDHD_GRD_CLSS, WK_DATE, RCRU_PL_CLSS	\n");
		sql.append("\t	    FROM BCDBA.TBLUGTMCSTMR	\n");
		
		sql.append("\t	  	WHERE ( (TB_RSLT_CLSS='01' AND RND_CD_CLSS='2' AND JUMIN_NO=? AND WK_DATE>=TO_CHAR(SYSDATE-365,'YYYYMMDD')) 	\n");
		sql.append("\t	   			 OR  (TB_RSLT_CLSS='01' AND RND_CD_CLSS='2' AND JUMIN_NO=? AND RCRU_PL_CLSS = '4200') )	\n");//KT�÷�Ŭ��
		
		sql.append("\t	    ORDER BY GOLF_CDHD_GRD_CLSS DESC, WK_DATE DESC	\n");
		sql.append("\t	) WHERE ROWNUM=1	\n");
		return sql.toString();

		
	}
	
	/**
	 * ���ο��� ���̺� ��ȸ ���� ���� |  | 2009.10.29 | �ǿ���
	 * @param b_where
	 * @param m_where
	 * @param s_order_kind
	 * @return
	 */
	private String getMemEntPcYnQuery() {
	  
		StringBuffer sql = new StringBuffer();
		sql.append("\n").append("	SELECT  CARD_NO  FROM BCDBA.TBENTPCDHD WHERE CARD_NO = ?   ");
		return sql.toString();
		
	}
	/**
	 * ����ȸ�� ���̺� ��ȸ ���� ���� |  | 2009.10.29 | �ǿ���
	 * @param b_where
	 * @param m_where
	 * @param s_order_kind
	 * @return
	 */
	private String getMemCoYnQuery() {
	  
		StringBuffer sql = new StringBuffer();		
		sql.append("\n").append("	SELECT  A.ACCOUNT, B.MEM_CLSS, A.BUZ_NO   FROM BCDBA.TBENTPUSER A INNER JOIN BCDBA.TBENTPMEM B ON A.MEM_ID = B.MEM_ID JOIN  BCDBA.UCUSRINFO C ON C.ACCOUNT=A.ACCOUNT  WHERE A.ACCOUNT = ? AND B.MEM_STAT = '2' AND B.SEC_DATE IS NULL  ");
		return sql.toString();
		
	}
	
	
	
	/**
	 * ���ȸ������ �˻�
	 * @param b_where
	 * @param m_where
	 * @param s_order_kind
	 * @return
	 */
	private String getMemComQuery() {
	  
		StringBuffer sql = new StringBuffer();

		sql.append("\n").append("		SELECT MEMBER_CLSS FROM BCDBA.UCUSRINFO WHERE ACCOUNT = ? 	");


		return sql.toString();
		
	}
	/**
	 * �����ȸ������ �˻�
	 * @param b_where
	 * @param m_where
	 * @param s_order_kind
	 * @return
	 */
	private String getMemCheckQuery() {
	  
		StringBuffer sql = new StringBuffer();

		sql.append("\n").append("	SELECT T1.CDHD_CTGO_SEQ_NO CDHD_CTGO_SEQ_NO, T2.CDHD_SQ2_CTGO CDHD_SQ2_CTGO, T3.GOLF_CMMN_CODE_NM GOLF_CMMN_CODE_NM	");
		sql.append("\n").append("	FROM BCDBA.TBGGOLFCDHDGRDMGMT T1	");
		sql.append("\n").append("	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.CDHD_CTGO_SEQ_NO=T2.CDHD_CTGO_SEQ_NO	");
		sql.append("\n").append("	JOIN BCDBA.TBGCMMNCODE T3 ON T2.CDHD_SQ2_CTGO=T3.GOLF_CMMN_CODE AND T3.GOLF_CMMN_CLSS='0005'	");
		sql.append("\n").append("	WHERE CDHD_ID = ? AND T2.CDHD_SQ1_CTGO='0002'	");

		return sql.toString();
		
	}
	
	
	
	/**
	 * ȸ������ ��������
	 */
	private String getMemTypeQuery() {
	  
		StringBuffer sql = new StringBuffer();

		sql.append("\n");
		sql.append("\t	SELECT T4.GOLF_CMMN_CODE_NM AS GOLF_CMMN_CODE_NM	\n");
		sql.append("\t		, CAST(T3.CDHD_SQ2_CTGO AS INT) AS CDHD_SQ2_CTGO	\n");
		sql.append("\t		, EMAIL1, MOBILE , T2.REG_ATON	\n");
		sql.append("\t		FROM BCDBA.TBGGOLFCDHD T1	\n");
		sql.append("\t		JOIN BCDBA.TBGGOLFCDHDGRDMGMT T2 ON T1.CDHD_ID=T2.CDHD_ID	\n");
		sql.append("\t		JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T3 ON T2.CDHD_CTGO_SEQ_NO=T3.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t		JOIN BCDBA.TBGCMMNCODE T4 ON T4.GOLF_CMMN_CODE=T3.CDHD_SQ2_CTGO AND GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t		JOIN BCDBA.UCUSRINFO T5 ON T5.ACCOUNT=T1.CDHD_ID	\n");
		sql.append("\t	WHERE T1.CDHD_ID=?	\n");

		return sql.toString();
		
	}
	
	/**
	 * ȸ������ �������� ( ���ΰ���ȸ�����̺��)
	 */
	private String getMemTypeCoQuery() {
	  
		StringBuffer sql = new StringBuffer();

		sql.append("\n");
		sql.append("\t	SELECT T4.GOLF_CMMN_CODE_NM AS GOLF_CMMN_CODE_NM														\n");
		sql.append("\t		, CAST(T3.CDHD_SQ2_CTGO AS INT) AS CDHD_SQ2_CTGO													\n");
		sql.append("\t		, T5.USER_EMAIL AS EMAIL1, T5.USER_MOB_NO AS MOBILE , T2.REG_ATON									\n");
		sql.append("\t		FROM BCDBA.TBGGOLFCDHD T1																			\n");
		sql.append("\t		JOIN BCDBA.TBGGOLFCDHDGRDMGMT T2 ON T1.CDHD_ID=T2.CDHD_ID											\n");
		sql.append("\t		JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T3 ON T2.CDHD_CTGO_SEQ_NO=T3.CDHD_CTGO_SEQ_NO						\n");
		sql.append("\t		JOIN BCDBA.TBGCMMNCODE T4 ON T4.GOLF_CMMN_CODE=T3.CDHD_SQ2_CTGO AND GOLF_CMMN_CLSS='0005'			\n");
		sql.append("\t		JOIN BCDBA.TBENTPUSER T5 ON T5.ACCOUNT=T1.CDHD_ID							\n");
		sql.append("\t	WHERE T1.CDHD_ID=?																						\n");

		return sql.toString();
		
	}
	
	
	
	/**
	 * ���̹��Ӵ� ��������
	 */
	private String getCyberMoneyQuery() {
	  
		StringBuffer sql = new StringBuffer();

		sql.append("\n");
		sql.append("\t  SELECT (CBMO_ACM_TOT_AMT-CBMO_DDUC_TOT_AMT) AS TOT_AMT		\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD										\n");
		sql.append("\t  WHERE CDHD_ID=?												\n");
		return sql.toString();
		
	}
	

    /** ***********************************************************************
     * ���� ����� ��ϵǾ� �ִ��� Ȯ��     
     ************************************************************************ */
     private String getChkGradeQuery(String cardGb){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n");
        sql.append("SELECT CDHD_GRD_SEQ_NO, CDHD_CTGO_SEQ_NO FROM BCDBA.TBGGOLFCDHDGRDMGMT WHERE CDHD_ID=? \n");
        if("ibk".equals(cardGb))
        {
        	sql.append("AND CDHD_CTGO_SEQ_NO IN ('9', '10', '19', '20') \n");
        }
        else if("nh".equals(cardGb))
        {
        	sql.append("AND CDHD_CTGO_SEQ_NO IN ('12', '13', '14') \n");
        }
        else if("vip".equals(cardGb))
        {
        	sql.append("AND CDHD_CTGO_SEQ_NO IN ('7') \n");
        }
        else if("topGolf".equals(cardGb))
        {
        	sql.append("AND CDHD_CTGO_SEQ_NO IN ('21') \n");
        }
        else if("rich".equals(cardGb))
        {
        	sql.append("AND CDHD_CTGO_SEQ_NO IN ('22') \n");
        }
        else if("jb".equals(cardGb))
        {
        	sql.append("AND CDHD_CTGO_SEQ_NO IN ('29') \n");
        }
        
 		return sql.toString();
     }
 	

     /** ***********************************************************************
      * �ش� ��� ����    
      ************************************************************************ */
      private String getDelGrdQuery(String cardGb){
          StringBuffer sql = new StringBuffer();
  		sql.append("\n");
          sql.append("DELETE FROM BCDBA.TBGGOLFCDHDGRDMGMT WHERE CDHD_ID=?  \n");
          if("ibk".equals(cardGb))
          {
          	sql.append("AND CDHD_CTGO_SEQ_NO IN ('9', '10', '19', '20') \n");
          }
          else if("nh".equals(cardGb))
          {
          	sql.append("AND CDHD_CTGO_SEQ_NO IN ('12', '13', '14') \n");
          }
          else if("vip".equals(cardGb))
          {
          	sql.append("AND CDHD_CTGO_SEQ_NO IN ('7') \n");
          }
          else if("topGolf".equals(cardGb))
          {
          	sql.append("AND CDHD_CTGO_SEQ_NO IN ('21') \n");
          }
          else if("rich".equals(cardGb))
          {
          	sql.append("AND CDHD_CTGO_SEQ_NO IN ('22') \n");
          }
          else if("jb".equals(cardGb))
          {
          	sql.append("AND CDHD_CTGO_SEQ_NO IN ('29') \n");
          }
  		return sql.toString();
      }
   	

      /** ***********************************************************************
       * ��� � �ִ��� �˾ƺ���    
       ************************************************************************ */
       private String getCntGrdQuery(){
           StringBuffer sql = new StringBuffer();
   		sql.append("\n");
           sql.append("SELECT COUNT(*) CNT_GRD FROM BCDBA.TBGGOLFCDHDGRDMGMT WHERE CDHD_ID=? \n");
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
     * ����ȸ����޸� ��������    
     ************************************************************************ */
     private String getGradeQuery(){
         StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
 		sql.append("\t  SELECT T1.CDHD_SQ2_CTGO AS GRADE_NO, T2.GOLF_CMMN_CODE_NM AS GRADE_NAME	\n");
 		sql.append("\t  FROM BCDBA.TBGGOLFCDHDCTGOMGMT T1	\n");
 		sql.append("\t  JOIN BCDBA.TBGCMMNCODE T2 ON T1.CDHD_SQ2_CTGO=T2.GOLF_CMMN_CODE AND GOLF_CMMN_CLSS='0005'	\n");
 		sql.append("\t  WHERE T1.CDHD_CTGO_SEQ_NO = ?	\n");
         return sql.toString();
     }
     
     /** ***********************************************************************
      * ������������ Ȯ��    
      ************************************************************************ */
      private String getChkTourQuery(){
        StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
  		sql.append("\t  SELECT T1.ACRG_CDHD_JONN_DATE, T1.ACRG_CDHD_END_DATE, T1.SECE_YN, T1.SECE_ATON	\n");
  		sql.append("\t  , T3.CDHD_CTGO_SEQ_NO, T3.CDHD_SQ1_CTGO, T3.CDHD_SQ2_CTGO	\n");
  		sql.append("\t  , (CASE WHEN T1.ACRG_CDHD_END_DATE<TO_CHAR(SYSDATE,'YYYYMMDD') THEN 'N' ELSE 'Y' END) ACRG_ABLE	\n");
  		sql.append("\t  FROM BCDBA.TBGGOLFCDHD T1	\n");
  		sql.append("\t  LEFT JOIN BCDBA.TBGGOLFCDHDGRDMGMT T2 ON T1.CDHD_ID=T2.CDHD_ID	\n");
  		sql.append("\t  LEFT JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T3 ON T2.CDHD_CTGO_SEQ_NO=T3.CDHD_CTGO_SEQ_NO AND T3.CDHD_SQ1_CTGO='0002'	\n");
  		sql.append("\t  WHERE T1.CDHD_ID=?	\n");
        return sql.toString();
      }
      
      /** ***********************************************************************
      * ����ȸ�������� ������Ʈ   
      ************************************************************************ */
      private String getReInsertMemQuery(){
        StringBuffer sql = new StringBuffer();
  		sql.append("\n");
  		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD SET CBMO_ACM_TOT_AMT='0', CBMO_DDUC_TOT_AMT='0'	\n");
  		sql.append("\t	, ACRG_CDHD_JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD')			\n");
  		sql.append("\t	, ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,12),'YYYYMMDD')		\n");
  		sql.append("\t	, SECE_YN='N', SECE_ATON='', JONN_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')		\n");
  		sql.append("\t	, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')		\n");
  		sql.append("\t	, EMAIL_RECP_YN='Y', SMS_RECP_YN='Y'		\n");
  		sql.append("\t	WHERE CDHD_ID=?		\n");
          	
        return sql.toString();
      }
      
      /** ***********************************************************************
      * ����ȸ������ ���̺� ����ȸ���Ⱓ ������Ʈ    
      ************************************************************************ */
      private String getAcrgUpdQuery(){
        StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
  		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD SET	\n");
  		sql.append("\t	ACRG_CDHD_JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD'), ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,12),'YYYYMMDD')	\n");
  		sql.append("\t  , CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')		\n");
  		sql.append("\t	WHERE CDHD_ID=?	\n");
          	
        return sql.toString();
      }
      
      /** ***********************************************************************
      * ����ȸ����ް��� ����� ��� ����    
      ************************************************************************ */
      private String getMemGradeDelQuery(){
        StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
  		sql.append("\t	DELETE FROM BCDBA.TBGGOLFCDHDGRDMGMT T1 WHERE CDHD_ID=? 	\n");
  		sql.append("\t	AND (SELECT CDHD_SQ1_CTGO FROM BCDBA.TBGGOLFCDHDCTGOMGMT WHERE CDHD_CTGO_SEQ_NO=T1.CDHD_CTGO_SEQ_NO)='0002'	\n");
          	
        return sql.toString();
      }
      
      /** ***********************************************************************
      * ����ȸ�������� �μ�Ʈ - TBGGOLFCDHD    
      ************************************************************************ */
      private String getInsertMemQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  INSERT INTO BCDBA.TBGGOLFCDHD (														\n");
		sql.append("\t  		CDHD_ID, HG_NM, JUMIN_NO													\n");
		sql.append("\t  		, ACRG_CDHD_JONN_DATE, ACRG_CDHD_END_DATE									\n");
		sql.append("\t  		, JONN_ATON, EMAIL_RECP_YN, SMS_RECP_YN, JOIN_CHNL							\n");
		sql.append("\t  		, CBMO_ACM_TOT_AMT, CBMO_DDUC_TOT_AMT										\n");
		sql.append("\t  		,  MEMBER_CLSS																\n");
		sql.append("\t  		, CDHD_CTGO_SEQ_NO, MOBILE, PHONE, EMAIL, ZIP_CODE, ZIPADDR, DETAILADDR, LASTACCESS	\n");	// 20091214 �߰�
		sql.append("\t  		) VALUES (																	\n");
		sql.append("\t  		?, ?, ?																		\n");
		sql.append("\t  		, TO_CHAR(SYSDATE,'YYYYMMDD'), TO_CHAR(ADD_MONTHS(SYSDATE,12),'YYYYMMDD')	\n");
		sql.append("\t  		, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), 'Y', 'Y', '3000'						\n");
		sql.append("\t  		, 0, 0, 1																	\n");
		sql.append("\t  		, ?, ?, ?, ?, ?, ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
		sql.append("\t  		)																			\n");
		return sql.toString();
      }         
      
      /** ***********************************************************************
      * �ֱ��������� ������Ʈ    
      ************************************************************************ */
      private String getUpdAccessQuery(){
        StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
  		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD	\n");
  		sql.append("\t	SET LASTACCESS = TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
  		sql.append("\t	WHERE CDHD_ID = ?	\n");
          return sql.toString();
      }       
      
      /** ***********************************************************************
       * ��ǥ��� ������Ʈ    
       ************************************************************************ */
       private String getUpdGradeDateQuery(){
         StringBuffer sql = new StringBuffer();
   		sql.append("	\n");
   		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD	\n");
   		sql.append("\t	SET ACRG_CDHD_JONN_DATE = TO_CHAR(SYSDATE,'YYYYMMDD') , ACRG_CDHD_END_DATE = TO_CHAR(ADD_MONTHS(SYSDATE,12),'YYYYMMDD') , CDHD_CTGO_SEQ_NO = ?	\n");
   		sql.append("\t	WHERE CDHD_ID = ?	\n");
           return sql.toString();
       }     
      /** ***********************************************************************
      * ��ǥ��� ������Ʈ    
      ************************************************************************ */
      private String getUpdGradeQuery(){
        StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
  		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD	\n");
  		sql.append("\t	SET CDHD_CTGO_SEQ_NO = ?	\n");
  		sql.append("\t	WHERE CDHD_ID = ?	\n");
          return sql.toString();
      }
      
      /** ***********************************************************************
      * ��ǥ��� ������Ʈ - ����ȸ����
      ************************************************************************ */
      private String getUpdGradeTourQuery(){
        StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
  		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD	\n");
  		sql.append("\t	SET CDHD_CTGO_SEQ_NO = ?, JOIN_CHNL='3000'	\n");
  		sql.append("\t	WHERE CDHD_ID = ?	\n");
          return sql.toString();
      }

   	/** ***********************************************************************
  	* ȸ������ ��������    strMemClss // ȸ����޹�ȣ 1:���� / 5:����
  	************************************************************************ */
  	private String getUserInfoQuery(String strMemClss){
  		StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
  		
  		if("5".equals(strMemClss)){

  			sql.append("\t  SELECT USER_EMAIL EMAIL, '' ZIPCODE, '' ZIPADDR, '' DETAILADDR	\n");
  			sql.append("\t  , USER_MOB_NO MOBILE, USER_TEL_NO PHONE, USER_JUMIN_NO SOCID	\n");
  			sql.append("\t  FROM BCDBA.TBENTPUSER	\n");
  			sql.append("\t  WHERE ACCOUNT=?	\n");
  			
  		}else{			  		
  			
  			sql.append("\t  SELECT EMAIL1 EMAIL, ZIPCODE, ZIPADDR, DETAILADDR, MOBILE, PHONE, SOCID	\n");
  			sql.append("\t  FROM BCDBA.UCUSRINFO	\n");
  			sql.append("\t  WHERE ACCOUNT = ?	\n");
  		}
  		
  		return sql.toString();
  	}

   	/** ***********************************************************************
  	* �ش� ����� ��ϵǾ� �ִ��� Ȯ��, ������� ��������
  	************************************************************************ */
  	private String getGrdDateQuery(){
  		StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
		sql.append("\t  SELECT T_GRD.REG_ATON, T_GRD.CDHD_CTGO_SEQ_NO, T_GRD.CDHD_GRD_SEQ_NO	\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHDGRDMGMT T_GRD	\n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T_CTGO ON T_GRD.CDHD_CTGO_SEQ_NO=T_CTGO.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t  JOIN BCDBA.TBGCMMNCODE T_CODE ON T_CODE.GOLF_CMMN_CODE=T_CTGO.CDHD_SQ2_CTGO AND T_CODE.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t  WHERE T_GRD.CDHD_ID = ? AND T_GRD.CDHD_CTGO_SEQ_NO = ?	\n");
  		return sql.toString();
  	}

   	/** ***********************************************************************
  	* �ش� ����� ��ϵǾ� �ִ��� Ȯ��, ������� �������� => ����
  	************************************************************************ */
  	private String getGrdDelQuery(){
  		StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
		sql.append("\t  DELETE BCDBA.TBGGOLFCDHDGRDMGMT WHERE CDHD_GRD_SEQ_NO=?	\n");
  		return sql.toString();
  	}

   	/** ***********************************************************************
  	* ��ǥ��� ���濩�� ���
  	************************************************************************ */
  	private String getGrdChgQuery(){
  		StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
		sql.append("\t  SELECT (CASE WHEN T_CTGO.SORT_SEQ>(SELECT SORT_SEQ FROM BCDBA.TBGGOLFCDHDCTGOMGMT WHERE CDHD_CTGO_SEQ_NO=?) THEN 'Y' ELSE 'N' END) CHG_YN	\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD T_CDHD	\n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T_CTGO ON T_CDHD.CDHD_CTGO_SEQ_NO=T_CTGO.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t  WHERE CDHD_ID=?	\n");
  		return sql.toString();
  	}

   	/** ***********************************************************************
  	* ��ް�������
  	************************************************************************ */
  	private String getGrdQuery(){
  		StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
		sql.append("\t  SELECT T_CTGO.CDHD_SQ2_CTGO GRD_COMM, T_CODE.GOLF_CMMN_CODE_NM GRD_NM, T_CDHD.JOIN_CHNL	\n");
		sql.append("\t  	, (SELECT T_CTGO.CDHD_SQ2_CTGO	\n");
		sql.append("\t  	FROM BCDBA.TBGGOLFCDHDGRDMGMT T_GRD	\n");
		sql.append("\t  	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T_CTGO ON T_GRD.CDHD_CTGO_SEQ_NO=T_CTGO.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t  	WHERE T_GRD.CDHD_ID=T_CDHD.CDHD_ID AND T_CTGO.CDHD_SQ1_CTGO='0002') GRD_MEM	\n");
		sql.append("\t  	, (SELECT CDHD_SQ2_CTGO FROM (	\n");
		sql.append("\t  	SELECT ROWNUM RNUM, T_CTGO.CDHD_SQ2_CTGO, T_CTGO.SORT_SEQ	\n");
		sql.append("\t  	FROM BCDBA.TBGGOLFCDHDGRDMGMT T_GRD	\n");
		sql.append("\t  	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T_CTGO ON T_GRD.CDHD_CTGO_SEQ_NO=T_CTGO.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t  	WHERE T_GRD.CDHD_ID=? AND T_CTGO.CDHD_SQ1_CTGO<>'0002'	\n");
		sql.append("\t      ORDER BY T_CTGO.SORT_SEQ)	\n");
		sql.append("\t      WHERE RNUM=1) GRD_CARD	\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD T_CDHD	\n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T_CTGO ON T_CDHD.CDHD_CTGO_SEQ_NO=T_CTGO.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t  JOIN BCDBA.TBGCMMNCODE T_CODE ON T_CODE.GOLF_CMMN_CLSS='0005' AND T_CODE.GOLF_CMMN_CODE=T_CTGO.CDHD_SQ2_CTGO	\n");
		sql.append("\t  WHERE T_CDHD.CDHD_ID=?	\n");
  		return sql.toString();
  	}

   	/** ***********************************************************************
  	* ������ �ִ� ����� ���� ���� ��� ��������
  	************************************************************************ */
  	private String getTopGradeQuery(){
  		StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
		sql.append("\t  SELECT CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t  FROM (	\n");
		sql.append("\t      SELECT GRD.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t      FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD	\n");
		sql.append("\t      JOIN BCDBA.TBGGOLFCDHDCTGOMGMT CTGO ON GRD.CDHD_CTGO_SEQ_NO=CTGO.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t      WHERE GRD.CDHD_ID=?	\n");
		sql.append("\t      ORDER BY CTGO.SORT_SEQ	\n");
		sql.append("\t  ) WHERE ROWNUM=1	\n");
  		return sql.toString();
  	}

   	/** ***********************************************************************
  	* �����ȸ������ �˾ƺ���.
  	************************************************************************ */
  	private String getFreeBlackQuery(){
  		StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
		sql.append("\t  SELECT CDHD_ID	\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD	\n");
		sql.append("\t  WHERE CDHD_ID=? AND JOIN_CHNL='3000' AND CDHD_CTGO_SEQ_NO='11' AND ACRG_CDHD_END_DATE>TO_CHAR(SYSDATE,'YYYYMMDD')	\n");
  		return sql.toString();
  	}

   	/** ***********************************************************************
  	* ����ȸ���������ڸ� ������ �ٲ��ش�.
  	************************************************************************ */
  	private String getUpdEndDateQuery(){
  		StringBuffer sql = new StringBuffer();
  		sql.append("	\n");
		sql.append("\t  UPDATE BCDBA.TBGGOLFCDHD	\n");
		sql.append("\t  SET ACRG_CDHD_END_DATE=TO_CHAR(SYSDATE-1,'YYYYMMDD')	\n");
		sql.append("\t  WHERE CDHD_ID=? AND JOIN_CHNL='3000' AND CDHD_CTGO_SEQ_NO='11' AND ACRG_CDHD_END_DATE>TO_CHAR(SYSDATE,'YYYYMMDD')	\n");
  		return sql.toString();
  	}


    /** ***********************************************************************
    * ȸ�� ������Ʈ - ȭ��Ʈ ȸ������
    ************************************************************************ */
    private String getWhiteUpdateQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD SET	\n");
 		sql.append("\t		ACRG_CDHD_JONN_DATE='',	\n");
 		sql.append("\t		ACRG_CDHD_END_DATE='', 	\n");
 		sql.append("\t		CDHD_CTGO_SEQ_NO = '8'	\n");
 		sql.append("\t		WHERE CDHD_ID=?	\n");
        return sql.toString();
    }    


    /** ***********************************************************************
    * ������ �̺�Ʈ ȸ������ �˾ƺ���.
    ************************************************************************ */
    private String getIbkGoldQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
 		sql.append("\t	SELECT * FROM  BCDBA.TBACRGCDHDLODNTBL	\n");
 		sql.append("\t	WHERE SITE_CLSS='02' AND RCRU_PL_CLSS='4003' AND PROC_RSLT_CLSS<>'01'	\n");
 		sql.append("\t	AND CAMP_STRT_DATE <= TO_CHAR(SYSDATE,'YYYYMMDD') AND  CAMP_END_DATE >= TO_CHAR(SYSDATE,'YYYYMMDD')	\n");
 		sql.append("\t	AND JUMIN_NO = ?	\n");
        return sql.toString();
    }

    /** ***********************************************************************
    * ������ �̺�Ʈ ȸ������ �˾ƺ���. - �̹̻���� ���·� ����Ǿ����� ������ ���� ���ѻ���� �˻�
    ************************************************************************ */
    private String getIbkGoldAgainQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
 		sql.append("\t	SELECT *	\n");
 		sql.append("\t	FROM BCDBA.TBACRGCDHDLODNTBL IBK	\n");
 		sql.append("\t	JOIN BCDBA.TBGGOLFCDHD MEM ON IBK.PROC_RSLT_CTNT=MEM.CDHD_ID	\n");
 		sql.append("\t	WHERE SITE_CLSS='02' AND RCRU_PL_CLSS='4003' AND PROC_RSLT_CLSS='01'	\n");
 		sql.append("\t	AND CAMP_STRT_DATE <= TO_CHAR(SYSDATE,'YYYYMMDD') AND  CAMP_END_DATE >= TO_CHAR(SYSDATE,'YYYYMMDD')	\n");
 		sql.append("\t	AND MEM.CDHD_CTGO_SEQ_NO<>'18' AND MONTHS_BETWEEN(ACRG_CDHD_END_DATE, ACRG_CDHD_JONN_DATE) IN (12, 3)	\n");
 		sql.append("\t	AND MEM.JUMIN_NO=?	\n");
        return sql.toString();
    }

    /** ***********************************************************************
    * ������ �̺�Ʈ ����� ȸ������ �˾ƺ���. - ������ȸ�� ���̺��� ���ؼ�
    ************************************************************************ */
    private String getUsedIbkGoldQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
 		sql.append("\t	SELECT *	\n");
 		sql.append("\t	FROM BCDBA.TBACRGCDHDLODNTBL	\n");
 		sql.append("\t	WHERE SITE_CLSS='02' AND RCRU_PL_CLSS='4003' AND PROC_RSLT_CLSS='01'	\n");
 		sql.append("\t	AND CAMP_STRT_DATE <= TO_CHAR(SYSDATE,'YYYYMMDD') AND  CAMP_END_DATE >= TO_CHAR(SYSDATE,'YYYYMMDD')	\n");
 		sql.append("\t	AND JUMIN_NO=?	\n");
        return sql.toString();
    }

    /** ***********************************************************************
    * ������ �̺�Ʈ ����� ȸ������ �˾ƺ���. - ȸ�� ���̺��� ���ؼ�
    ************************************************************************ */
    private String getUsedIbkGoldMemQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
 		sql.append("\t	SELECT CDHD_CTGO_SEQ_NO FROM BCDBA.TBGGOLFCDHD WHERE JUMIN_NO=? AND CDHD_CTGO_SEQ_NO='18'	\n");
 		sql.append("\t	UNION ALL	\n");
 		sql.append("\t	SELECT CDHD_CTGO_SEQ_NO FROM BCDBA.TBGGOLFCDHD WHERE JUMIN_NO=?	\n");
 		sql.append("\t	AND CDHD_CTGO_SEQ_NO<>'18' AND MONTHS_BETWEEN(ACRG_CDHD_END_DATE, ACRG_CDHD_JONN_DATE) NOT IN (14, 5)	\n");
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
 		sql.append("\t	WHERE TM_IBK.SITE_CLSS='02' AND TM_IBK.RCRU_PL_CLSS='4003'	\n");
 		sql.append("\t	AND TM_IBK.CAMP_STRT_DATE <= TO_CHAR(SYSDATE,'YYYYMMDD') AND  TM_IBK.CAMP_END_DATE >= TO_CHAR(SYSDATE,'YYYYMMDD')	\n");
 		sql.append("\t	AND TM_IBK.JUMIN_NO = ?	\n");
        return sql.toString();
    }
    
 	/** ***********************************************************************
 	* ��� �����丮 ���̺� �μ�Ʈ    
 	************************************************************************ */
 	private String getInsGrdHistoryQuery(){
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

    /** ***********************************************************************
    * ������ȸ�� ����ȸ�� ���̺� ������Ʈ    
    ************************************************************************ */
    private String getUpdMemIbkGoldQuery(){
       StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t  UPDATE BCDBA.TBGGOLFCDHD 	\n");
		sql.append("\t  SET CDHD_CTGO_SEQ_NO=?, JOIN_CHNL=?, ACRG_CDHD_JONN_DATE=?, ACRG_CDHD_END_DATE=?	\n");
		sql.append("\t  WHERE CDHD_ID=? 	\n");
       return sql.toString();
    }
   

 	/** ***********************************************************************
 	* ��� �����丮 ���̺� �μ�Ʈ    
 	************************************************************************ */
 	private String getUpdGrdQuery(){
 		StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
 		sql.append("\t  UPDATE BCDBA.TBGGOLFCDHDGRDMGMT	\n");
 		sql.append("\t  SET CDHD_CTGO_SEQ_NO=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
 		sql.append("\t  WHERE CDHD_GRD_SEQ_NO=(	\n");
 		sql.append("\t      SELECT GRD.CDHD_GRD_SEQ_NO	\n");
 		sql.append("\t      FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD	\n");
 		sql.append("\t      JOIN BCDBA.TBGGOLFCDHDCTGOMGMT GRDM ON GRD.CDHD_CTGO_SEQ_NO=GRDM.CDHD_CTGO_SEQ_NO	\n");
 		sql.append("\t      WHERE GRD.CDHD_ID=? AND GRDM.CDHD_SQ1_CTGO='0002'	\n");
 		sql.append("\t  )	\n");
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
      * �����̷� �����丮 �μ�Ʈ 
      ************************************************************************ */
      private String getIbkMemUpdQuery(){
        StringBuffer sql = new StringBuffer();
        sql.append("	\n");
        sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD	\n");
        sql.append("\t	SET ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(TO_DATE(ACRG_CDHD_END_DATE),2),'YYYYMMDD')	\n");
        sql.append("\t	WHERE CDHD_ID=?	\n");
        return sql.toString();
      }      
     
     
     /** ***********************************************************************
     * ����ȸ�� ���� �Ⱓ ��������
     ************************************************************************ */
     private String getMemEndQuery(){
       StringBuffer sql = new StringBuffer();
       sql.append("	\n");
       sql.append("\t	SELECT TO_CHAR(TO_DATE(ACRG_CDHD_END_DATE),'YYYY/MM/DD') END_DATE FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID=?	\n");
       return sql.toString();
     }                 
      
     /** ***********************************************************************
     * ����� ����� �ִ��� �˾ƺ���.
     ************************************************************************ */
     private String getMemberShipQuery(){
       StringBuffer sql = new StringBuffer();
       sql.append("	\n");
       sql.append("\t	SELECT CTG.CDHD_CTGO_SEQ_NO	\n");
       sql.append("\t	FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD	\n");
       sql.append("\t	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT CTG ON GRD.CDHD_CTGO_SEQ_NO=CTG.CDHD_CTGO_SEQ_NO	\n");
       sql.append("\t	WHERE CDHD_ID=? AND CDHD_SQ1_CTGO='0002'	\n");
       return sql.toString();
     }
     
     /** ***********************************************************************
     * ����ȸ�� �Ⱓ ����� tm �ִ��� Ȯ��
     ************************************************************************ */
     private String getExPeriodQuery(){
       StringBuffer sql = new StringBuffer();
       sql.append("	\n");
       sql.append("\t	SELECT ROWNUM RNUM, MB_CDHD_NO, TB_RSLT_CLSS, JUMIN_NO, RND_CD_CLSS, GOLF_CDHD_GRD_CLSS, WK_DATE, RCRU_PL_CLSS	\n");
       sql.append("\t	FROM (	\n");
       sql.append("\t		SELECT MB_CDHD_NO, TB_RSLT_CLSS, JUMIN_NO, RND_CD_CLSS, GOLF_CDHD_GRD_CLSS, WK_DATE, RCRU_PL_CLSS	\n");
       sql.append("\t		FROM BCDBA.TBLUGTMCSTMR	\n");
       sql.append("\t		WHERE TB_RSLT_CLSS='01' AND RND_CD_CLSS='2' AND JUMIN_NO=? AND CONC_DATE>TO_CHAR(SYSDATE-365,'YYYYMMDD')	\n");
       sql.append("\t		ORDER BY GOLF_CDHD_GRD_CLSS DESC, WK_DATE DESC	\n");
       sql.append("\t	) WHERE ROWNUM=1	\n");
       return sql.toString();
     }
     
     /** ***********************************************************************
     * ����ȸ����ް��� ���̺� ������Ʈ - TM ����ȸ���Ⱓ �����
     ************************************************************************ */
     private String getGrdUpdTMQuery(){
       StringBuffer sql = new StringBuffer();
       sql.append("	\n");
       sql.append("\t	UPDATE BCDBA.TBGGOLFCDHDGRDMGMT SET	\n");
       sql.append("\t	CDHD_CTGO_SEQ_NO=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), CHNG_RSON_CTNT=?	\n");
       sql.append("\t	WHERE CDHD_CTGO_SEQ_NO=? AND CDHD_ID=?	\n");
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
     * ȸ�����̺� ���ռ� ������Ʈ
     ************************************************************************ */
     private String getMemUpdQuery(int intMemGrade, String upd_name, String upd_mobile, String upd_phone, 
    		 String upd_email, String upd_zipcode, String upd_zipaddr, String upd_detailaddr, String upd_addClss, String upd_grade){
        StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
 		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD	\n");
  		sql.append("\t	SET LASTACCESS = TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");

 		sql.append("\t	");
  		if(upd_name.equals("Y")) 		sql.append(" , HG_NM=? ");
  		if(upd_mobile.equals("Y")) 		sql.append(" , MOBILE=? ");
  		if(upd_phone.equals("Y")) 		sql.append(" , PHONE=? ");
  		if(upd_email.equals("Y")) 		sql.append(" , EMAIL=? ");
  		if(upd_zipcode.equals("Y")) 	sql.append(" , ZIP_CODE=? ");
  		if(upd_zipaddr.equals("Y")) 	sql.append(" , ZIPADDR=? ");
  		if(upd_detailaddr.equals("Y"))	sql.append(" , DETAILADDR=? ");
  		if(upd_addClss.equals("Y")) 	sql.append(" , NW_OLD_ADDR_CLSS=? ");
  		if(upd_grade.equals("Y")) 		sql.append(" , CDHD_CTGO_SEQ_NO=? ");
  		
 		if(intMemGrade==4 && !"Y".equals(upd_grade)){
 		sql.append("\t		, ACRG_CDHD_JONN_DATE='', ACRG_CDHD_END_DATE=''	\n");
 		} 
 		
 		sql.append("\t	    WHERE CDHD_ID=?	\n");
        return sql.toString();
     }     
     
     /** ***********************************************************************
     * ȸ�����̺� ������������
     ************************************************************************ */
     private String getMemSelQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
 		sql.append("\t	SELECT HG_NM, CDHD_CTGO_SEQ_NO, MOBILE, PHONE, EMAIL, ZIP_CODE, NVL(ZIPADDR, ' ')ZIPADDR, NVL(DETAILADDR, ' ')DETAILADDR, NVL(NW_OLD_ADDR_CLSS, ' ') NW_OLD_ADDR_CLSS	\n");
 		sql.append("\t	, (SELECT CDHD_CTGO_SEQ_NO	\n");
 		sql.append("\t	        FROM (SELECT GRD.CDHD_CTGO_SEQ_NO	\n");
 		sql.append("\t	            FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD	\n");
 		sql.append("\t	            JOIN BCDBA.TBGGOLFCDHDCTGOMGMT CTGO ON GRD.CDHD_CTGO_SEQ_NO=CTGO.CDHD_CTGO_SEQ_NO	\n");
 		sql.append("\t	            WHERE CDHD_ID=?	\n");
 		sql.append("\t	            ORDER BY SORT_SEQ	\n");
 		sql.append("\t	    ) WHERE ROWNUM=1) TOP_CDHD_CTGO_SEQ_NO	\n");
 		sql.append("\t	FROM BCDBA.TBGGOLFCDHD	\n");
 		sql.append("\t	WHERE CDHD_ID=?	\n");
        return sql.toString();
     }          
}


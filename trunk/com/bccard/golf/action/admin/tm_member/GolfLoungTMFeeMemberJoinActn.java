/**************************************************************************************************
*  Ŭ������	: GolfLoungTMFeeMemberJoinActn
*  �� �� ��	: ������ [yskkang@bccard.com]
*  ��    ��	: ���� ����� TM ����� ����Ʈ ���� ����ȸ������ ó��
*  �������	: golfloung
*  �ۼ�����	: 2009.07.02
* http://develop.bccard.com:13300/app/golfloung/admTmMember.do

1. ��������� TM���� ���� ��ȸ(����� ���̺�- bcdba.TBLUGTMCSTMR) - List
2. ���ȸ�� �������� Ȯ�� (����ȸ�� ���̺� - bcdba.TBGGOLFCDHD) JONN_CHNL_CLSS='03'  �����̸� JOIN_CHNL : 0002  �����̸� JOIN_CHNL : 0003
3. ���ȸ�� ����Ʈ ��ȸ  
4. ����Ʈ ������� ����Ʈ ���� (MJF6220I2100)
	- ���ȸ�� ����(����ȸ�� ���̺� insert)
	- ��ȸ�� �������� ���̺� insert
		* BCDBA.TBGGOLFCDHD		(���������ȸ��    table)
		* bcdba.TBGLUGANLFEECTNT (������γ���  table)
5. ����Ʈ ������� 
	- �������̺� ���� ���� ���
	- 

************************* �����̷� ***************************************************************** 
*    ����       �ۼ���      �������
* 2011.03.30    �̰���	   TMä�� �߰� (��������, ���غ���)
* 2011.04.12    �̰���	   ����ȸ�� ���԰�α����ڵ����
* 2011.04.20    �̰���	   ���غ��� ������ ��ȣ 767445661�߰�
***************************************************************************************************/
package com.bccard.golf.action.admin.tm_member;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.ResultException;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.jolt.JtProcess;
import com.bccard.golf.jolt.JtTransactionProc;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.BcUtil;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;
import com.bccard.waf.tao.jolt.JoltOutput;


/**
 * ���� ����� TM
 * @version 2009.07.02
 * @author  ������ [yskkang@bccard.com]
 */
public class GolfLoungTMFeeMemberJoinActn extends AbstractAction  {

	
	/** *****************************************************************
	 * Action excecution method
	 * @param context		WaContext Object
	 * @param request		HttpServletRequest Object
	 * @param response		HttpServletResponse Object
	 * @return				ActionResponse Object
	 * @exception IOException, ServletException, BaseException if errors occur
	 ***************************************************************** */

	public ActionResponse execute(WaContext context
						,HttpServletRequest request
						,HttpServletResponse response)
	throws IOException, ServletException, BaseException {

		ResultException rx = null;
		HttpSession session	= request.getSession(false);		
		RequestParser parser = context.getRequestParser("default", request, response);

		String goPage	 = "javascript:window.open('history.go(-1)', '_blank', '');self.close();";
		String title	 = "���� ����� TM ���ᰡ��";
		String addButton = "<img src='/golf/img/common/btn/btn_definite.gif' border='0'>";//Ȯ�ι�ư
		String responseKey = "default";
		

		Vector juminNoList = new Vector();			

		String  jumin_no		= "";
		String  auth_no			= "";
		String  mb_cdhd_no		= "";
		String	golf_clss		= "";  //��� 1:��� 2: ��� 3:è�ǿ�
		String  hg_nm			= "";
		String  email_addr		= "";
		String	pay_amt			= "0";
		String	memkind			= "";					
		String	ret_code		= "";
		String	ret_msg			= "";
		String	ret_code2		= "";
		String	ret_msg2		= "";
		String	golf_class_nm	= "";
		String  strCtgo			= "8"; //5:è�ǿ� 6:��� 7:��� 8:ȭ��Ʈ

		String	auth_clss		= "";
		String	card_no			= "";
		String	vald_lim		= "";
		String	tm_buz			= "";
		String	hp_ddd_no		= "";
		String	hp_tel_hno		= "";
		String	hp_tel_sno		= "";
		String	hp			= "";
		String  recp_date	= "";
		String  getvald_lim	= "";
		String  rcru_pl_clss = "";
		String  product_no = "";
		String  mail_clss =  "";
		String  cdhd_ret ="";

		String  disc_clss	= "";
		String  dc_amt		= "";

		String  tb_rslt_clss ="01";

		boolean auth	= false;		

		String action_key = super.getActionKey(context);
		debug(action_key);
		String str = "***";
		String str_hp = "0000";

		boolean  bln_sms = false;
		boolean  bln_email = false;


		try {             	
				info("[��������� TM ����ȸ�� ���� ����] GolfLoungTMFeeMemberJoinActn START ���۽ð�:" + DateUtil.currdate("yyyy.MM.dd:HH.mm.ss") );
 
				GolfLoungTMProc proc = new GolfLoungTMProc();
				
 
				if ("admTmMember".equals(action_key)){
					juminNoList = proc.getListTMLoungApply(context);         //TM ��ϴ�� ��ȸ : �ֹι�ȣList	
				} else if ("admMojib".equals(action_key)) {
					juminNoList = proc.getListMojibLoungApply(context);         //������ ��ϴ�� ��ȸ : �ֹι�ȣList	
				}
	
				String hostAddress = InetAddress.getLocalHost().getHostAddress(); 
				String devip = "";
				
				try {
					devip = AppConfig.getAppProperty("DV_WAS_1ST");
				} catch(Throwable t) {}


				info("[��������� TM ����IP="  + hostAddress );	
				info("[��������� TM ����IP="  + devip );

				int a = 0;        //   �� ó���Ǽ�
				int b = 0;        // ���� ó���Ǽ�
				int memcnt = 0;   // 
				info("[��������� TM ��ϴ�� ��ȸ ��] "+juminNoList.size());

				while(a < juminNoList.size())	{ 
					// email, sms �߼ۿ��� �ʱ�ȭ
					bln_sms = false;
					bln_email = false;
					auth = false;
					hp = "";
					recp_date = "" ; //�ʱ�ȭ
					jumin_no = "";

					Hashtable data = new Hashtable();
					data = (Hashtable)juminNoList.get(a);

					jumin_no =(String)data.get("JUMIN_NO"); //�ֹι�ȣ
					debug("�ʱ�jumin_no==========>"+jumin_no);
					golf_clss =(String)data.get("GOLF_CDHD_GRD_CLSS"); //��� 1:��� 2: ��� 3:è�ǿ� 4:��
					mb_cdhd_no =(String)data.get("MB_CDHD_NO");
					hg_nm =(String)data.get("HG_NM");	//�ѱ۸�
					email_addr =(String)data.get("EMAIL_ID"); //�̸���
					auth_clss =(String)data.get("AUTH_CLSS"); //�������� 1:ī�� 2:���հ��� 3:����Ʈ
					card_no =(String)data.get("CARD_NO");
					tm_buz =(String)data.get("JOIN_CHNL"); //03:H&C, 10:CIC�ڸ���, 11:TCK, 12:��������, 13:���غ���
					getvald_lim =(String)data.get("VALD_LIM");
					rcru_pl_clss =(String)data.get("RCRU_PL_CLSS"); // 0002:��� 0003:��� 0103:���(ī��)
					product_no =(String)data.get("PRODUCT_NO"); //1:�Ǽ� Gold : 35,000�� 2:���� Gold : 54,000�� 3:VIP Gold : 86,000��
										
					
					recp_date	=	(String)data.get("RECP_DATE");

					if	(  "1".equals(golf_clss) ) pay_amt ="25000";
					else if (  "2".equals(golf_clss) ) pay_amt ="50000";
					else if (  "3".equals(golf_clss) ) pay_amt ="200000";
					else if (  "4".equals(golf_clss) ) pay_amt ="120000";
					
					if ("0103".equals(rcru_pl_clss) ){
						if ( product_no ==null ) product_no="";
						if ("1".equals(product_no) ){
							pay_amt ="35000";
						}else if ("2".equals(product_no) ){
							pay_amt ="54000";
						}else if ("3".equals(product_no) ){	
							pay_amt ="86000";
						}
					}
					debug("|��ȿ�Ⱓ|"+getvald_lim + "|�����ݾ�|"+pay_amt );

					hp_ddd_no	=	(String)data.get("HP_DDD_NO");
					hp_tel_hno	=	(String)data.get("HP_TEL_HNO");
					hp_tel_sno	=	(String)data.get("HP_TEL_SNO");
					 
					disc_clss	=	(String)data.get("DISC_CLSS");
					dc_amt	=	(String)data.get("DC_AMT");
					
					if (hp_ddd_no.length()>=3 && hp_tel_hno.length()>=3 && hp_tel_sno.length()>=4 )	{
						if ( hp_tel_sno.indexOf(str_hp) ==  -1 ) {
							hp = hp_ddd_no + hp_tel_hno + hp_tel_sno;
						}
					}
					debug("disc_clss="+disc_clss+",dc_amt="+dc_amt);

					//������ ó�� �ϰ��
					if ("admMojib".equals(action_key)) {
					
						//�������ϰ�� ��������
						if ("1".equals(disc_clss))	//������
						{
							int sale_amt = Integer.parseInt(dc_amt);
							double div = ((double)(100-(double)sale_amt)/100); //������ (0.9)
							int sttl_amt = (int)(Double.parseDouble(pay_amt) * div); //�ǰ����ݾ�
							pay_amt = String.valueOf(sttl_amt) ; //�ǰ����ݾ�
							debug("�ǰ����ݾ�="+pay_amt+",������="+div);

						} else if ("3".equals(disc_clss))	//���αݾ�
						{
							int dc = Integer.parseInt(pay_amt) - Integer.parseInt(dc_amt); //���αݾ�
							pay_amt = String.valueOf(dc); //���αݾ�
							debug("�ǰ����ݾ�="+pay_amt+",���αݾ�="+dc);
						}
					
					// TM�ϰ�� �ֹι�ȣ *** ����ŷ���� ���� ���� �ϳ��� �� ź�� 2010.03.17 �߰�
					} else {


						TaoResult taoResult_jumin = null;

						JoltInput entity_jumin = null;

						entity_jumin = new JoltInput();

						entity_jumin.setServiceName("BSNINPT");
						entity_jumin.setString("fml_trcode", "MHL0420R0100"); //ȸ����ȸ����ȣ
						entity_jumin.setString("fml_channel", "WEB");
						entity_jumin.setString("fml_sec50", "bcadmin");
						entity_jumin.setString("fml_sec51", hostAddress);
						entity_jumin.setString("fml_arg1", mb_cdhd_no); //ȸ����ȸ����ȣ
						entity_jumin.setString("fml_arg2", "0");  //��ȸŰ �ʱ� 0
						entity_jumin.setString("fml_arg3", "1"); //1:ȸ����ȸ����ȣ 2:����ȣ

						java.util.Properties prop = new java.util.Properties();
						prop.setProperty("RETURN_CODE","fml_ret1");

						JtProcess jtproc_jumin = new JtProcess();
						taoResult_jumin = jtproc_jumin.call(context, request, entity_jumin, prop);
						debug(taoResult_jumin.toString());
						String ret_jumin = taoResult_jumin.getString("fml_ret1").trim(); 

						if("00".equals(ret_jumin) || "01".equals(ret_jumin)){	   // resultCode -> 00:���� 01:����Ű���� 02:ȸ����ȸ����ȣ������ 99:�ý��ۿ���
							// �� �����ö���...
							jumin_no = taoResult_jumin.getString("fml_ret3").trim(); 
							debug("���� jumin_no==========>"+jumin_no);

							int upcnt = proc.getUpJuminNo(context, jumin_no,mb_cdhd_no,recp_date);
						} 
						

					}


					memcnt = a + 1;
					info("[��������� TM "+memcnt+" ��] �ֹι�ȣ |"+jumin_no+"| ���(1:���,2:���,3:è�ǿ�,4:��) |"+golf_clss+"|��������|"+auth_clss);

					memkind    = "";					
					ret_code   = "";
					ret_msg    = "";
					ret_code2  = "";
					ret_msg2   = "";
					auth_no    = "";

					int insertgolfcdhd    = 0;
					int insertgolffeectn  = 0;
					int	updatecnt		  = 0;
					
					memkind = proc.getIsFeeFree(context, jumin_no);        // loung ���� ȸ������ ��ȸ
					
					info("[��������� TM ����ȸ�� ���� ] �ֹι�ȣ |"+jumin_no+"| memkind (Y:���ԺҰ��ɰ�(������ȸ����) N:���԰��ɰ�) |"+memkind);

					// ȸ���� ȸ����ȣ 
					if ( jumin_no.indexOf(str) >=  0 ) {

						info("[��������� TM ȸ���� ȸ����ȣ Ȯ��] ȸ���� ȸ����ȣ ���� |" + jumin_no);
						updatecnt = proc.updateIsFree(context, jumin_no,"09","","ȸ����ȸ����ȣ������",recp_date);
						

					// ���� ����ȸ�� �� 
					} else if( memkind.equals("Y") ) { 

						info("[��������� TM �Ⱑ�� ����ȸ��] �ֹι�ȣ |" + jumin_no);
						updatecnt = proc.updateIsFree(context, jumin_no,"02","","|�Ⱑ�԰�|",recp_date);
						//����Ȯ�ο� �Ⱑ��ó��(TB_RSLT_CLSS = 02 )


					} else if(  memkind.equals("N") ) {
						info("[��������� TM ���� ���ȸ��] �ֹι�ȣ |" + jumin_no);
						
						/*  2010.07.15 ����
						 
						if (memkind.equals("0001"))	{
							//5:è�ǿ� 6:��� 7:��� 8:ȭ��Ʈ(����)
							strCtgo = proc.getGolfClass(context, jumin_no); 
						} else {
							strCtgo="8"; //�ʱ�ȭ
						}
						
						if ( !"8".equals(strCtgo) )	{
							info("[��������� TM �Ⱑ�� ����ȸ��] �ֹι�ȣ |" + jumin_no);
							updatecnt = proc.updateIsFree(context, jumin_no,"02","","|����ڵ�|"+strCtgo,recp_date); //����Ȯ�ο� �Ⱑ��ó��(TB_RSLT_CLSS = 02 )
						} else {
						*/
							
					
							//����Ʈ�����ϰ��
							if ("3".equals(auth_clss)) {
						
							
								/********************************************
								* MJF6010R0100 - 0000:���� 
								* ž����Ʈ ��ȸ ���� :  for COMMIT  *
								*********************************************/	

								TaoResult taoResult = null;

								JoltInput entity0 = null;
		//find ./ -name 'was1_bcext_7201_20090714.log' | xargs grep '5411201400321'

								entity0 = new JoltInput();

								entity0.setServiceName("BSNINPT");
								entity0.setString("fml_trcode", "MJF6010R0100"); //����Ʈ��ȸ
								entity0.setString("fml_channel", "WEB");
								entity0.setString("fml_sec50", "bcadmin");
								entity0.setString("fml_sec51", hostAddress);
							
								entity0.setString("fml_arg1", "1"); //1:�ֹι�ȣ 2:ī���ȣ 3:����ڹ�ȣ
								entity0.setString("fml_arg2", "99000000"+jumin_no+"   ");  //1.�ֹι�ȣ:99+���޾�ü�ڵ�(000000)+�ֹι�ȣ(13)+����(3) 
								entity0.setString("fml_arg3", "1"); //1:���� 2:���

								java.util.Properties prop = new java.util.Properties();
								prop.setProperty("RETURN_CODE","fml_ret1");

								JtProcess jtproc = new JtProcess();
								taoResult = jtproc.call(context, request, entity0, prop);
								debug(taoResult.toString());

								// �� �����ö���...
								String top_ret_code = taoResult.getString("fml_ret1").trim(); 
								String top_ret_msg = taoResult.getString("fml_ret2").trim();  
								String top_point = "0";
	//		top_ret_code = "0000"; //���߱������׽�Ʈ
								
								if(!"0000".equals(top_ret_code)){
									//����Ʈ ����
									info("[��������� TM ����Ʈ ��ȸ ����] �ֹι�ȣ |" + jumin_no+"|fml_ret1|"+top_ret_code+"|fml_ret2|"+top_ret_msg);
									updatecnt = proc.updateIsFree(context, jumin_no, "07","" ,top_ret_msg,recp_date); //����Ʈ��ȸ����
								} else {
									if(taoResult.size() > 0) {
										taoResult.first();
										for(int i=0; i < taoResult.size(); i++) {
											debug(i+"��° ����Ʈ ��ȸ");
											top_point= taoResult.getString("fml_ret23").trim();
											taoResult.next();
										}
									} 
	//		top_point= "300000"; //���߱������׽�Ʈ

									//����Ʈ ����ó��
									if ( Integer.parseInt(top_point) >= Integer.parseInt(pay_amt))
									{

										/*********************************************
										* MJF6220I2100 - 0000:���� 
										* ž����Ʈ ���� �������� :  for COMMIT       *
										**********************************************/	
										JoltInput entity = null;

										entity = new JoltInput();

										entity.setServiceName("BSXINPT");
										entity.setString("fml_trcode", "MJF6220I2100");
										entity.setString("fml_channel", "WEB");
										entity.setString("fml_sec50", "bcadmin");
										entity.setString("fml_sec51", hostAddress);
									
										entity.setString("fml_arg1"  , "1"							 );			// 1.�ֹι�ȣ, 2.ī���ȣ, 3.����ڹ�ȣ
										entity.setString("fml_arg2"  , "99000000" + jumin_no + "   ");
										entity.setString("fml_arg3"  , "1"			      		 	 );			// 1.����, 2.���
										entity.setString("fml_arg4"  , "42"						 );			// 42: ��������� TM
										entity.setString("fml_arg5"  , pay_amt						 );
										entity.setString("fml_arg6"  , ""							 );
				debug("  entity= ["+entity+"]");

										JtTransactionProc pgProc = null;

										pgProc = (JtTransactionProc)context.getProc("MJF6220I2100");

										JoltOutput output = pgProc.execute(context,entity);

										debug(output.toString());
										ret_code   =  output.getString("fml_ret1");
										ret_msg    =  output.getString("fml_ret2");


	 // ret_code="0000"; //���߱� �׽�Ʈ����
										if(ret_code.equals("0000")) {

											auth_no = output.getString("fml_ret6");                           // Toppoint���� �Ϸù�ȣ
											info("[��������� TM ����Ʈ ���� ����] �ֹι�ȣ |" + jumin_no + "|�����Ϸù�ȣ|" + auth_no);
											
											//��������� ȸ������ó��(BCDBA.TBGGOLFCDHD)
											String	join_chnl = "0000";
											if ("admTmMember".equals(action_key)){

												if	(  "1".equals(golf_clss) ) {join_chnl ="0003"; golf_class_nm= "���"; }//���
												else if (  "2".equals(golf_clss) ) {join_chnl ="0002"; golf_class_nm= "���";} //���
												else if (  "3".equals(golf_clss) ) {join_chnl ="0004"; golf_class_nm= "è�ǿ�";} //è�ǿ�
												else if (  "4".equals(golf_clss) ) {join_chnl ="0005"; golf_class_nm= "��";} //��
												
												//ī����ǰ �߰� 2010.07.15
												if  (  "0103".equals(rcru_pl_clss) ) {join_chnl ="0103"; golf_class_nm= "���"; }//���

				
											} else if ("admMojib".equals(action_key)) {

												if	(  "1".equals(golf_clss) ) {join_chnl ="1003"; golf_class_nm= "���"; }//���
												else if (  "2".equals(golf_clss) ) {join_chnl ="1002"; golf_class_nm= "���";} //���
												else if (  "3".equals(golf_clss) ) {join_chnl ="1001"; golf_class_nm= "è�ǿ�";} //è�ǿ�
												else if (  "4".equals(golf_clss) ) {join_chnl ="1005"; golf_class_nm= "��";} //��


											}

											tb_rslt_clss ="01";
											
											//ȸ�����Խ� insert�� update ó�� 2010.07.15 ����(��ȸ���϶� : 00 �ʿ�)
											tb_rslt_clss   = proc.updateGolfcdhd(context, jumin_no, join_chnl);  //����ȸ������ ���Ե� ��쿣 00ó����.

											info("[��������� TM ����ȸ�� ���̺� update �Ϸ�] �ֹι�ȣ |" + jumin_no +"|updateGolfcdhd|"+tb_rslt_clss );

											//�ֹι�ȣ,���ι�ȣ,ī���ȣ,���αݾ�,���α����ڵ� 1:���� 9:���,����Ʈ����,ȸ����ȸ����ȣ
											//��������� ��ȸ�񳻿�(BCDBA.TBGLUGANLFEECTN)
											
											insertgolffeectn = proc.insertGolfFee(context, jumin_no, auth_no, "point", pay_amt, auth_clss, mb_cdhd_no);  // log insert & mail send
											//insertgolffeectn = 1 ;
											info("[��������� TM ��ȸ�� ���̺� insert �Ϸ�] �ֹι�ȣ |" + jumin_no+"|insertgolffeectn|"+insertgolffeectn );
								
											updatecnt = proc.updateIsFree(context, jumin_no,tb_rslt_clss,"","",recp_date);
											info("[��������� TM ���� ���̺� update �Ϸ�] �ֹι�ȣ |" + jumin_no);

											if( insertgolffeectn != 0 && updatecnt != 0){       //  DB processed successfully   

												info("[��������� TM ����ȸ�� ������ �Ϸ�] �ֹι�ȣ |" + jumin_no + "|hp|"+hp );
												b++;
												
												

												
												/********************************************
												* SMS �߼� 
												******************************************** */
												if (hp.length() > 9)
												{
													bln_sms = true;
												} 


												
												if ("admTmMember".equals(action_key)){ //TM�� ��ȭ���Ź߼�

													/********************************************
													* �̸��� �߼� 
													*********************************************/
													bln_email = true;	

												}


											}else{												              //  DB processed unsuccessfully	

												/********************************************
												* MJF6250I0100 - 0000:���� 
												* ž����Ʈ ���� ������� ���� :  for COMMIT  *
												*********************************************/	
												info("[��������� TM ����Ʈ ���� �� DBó�� ���� -> ����Ʈ ��ҿ�û] �ֹι�ȣ |" + jumin_no );
												
												JoltInput entity2 = null;

												entity2 = new JoltInput();

												entity2.setServiceName("BSXINPT");		
												
												entity2.setString("fml_channel", "WEB"			           );
												entity2.setString("fml_sec51" , hostAddress            );
												entity2.setString("fml_sec50" , "bcadmin"                    );
												entity2.setString("fml_trcode", "MJF6250I0100"               );
												entity2.setString("fml_arg1"  , jumin_no					   );			
												entity2.setString("fml_arg2"  , auth_no					   );
												entity2.setString("fml_arg3"  , "42"			      		   );			
												
												debug("  entity2= ["+entity2+"]");

												JtTransactionProc pgProc2 = null;

												pgProc2 = (JtTransactionProc)context.getProc("MJF6250I0100");

												JoltOutput output2 = pgProc2.execute(context,entity2);

												debug(output2.toString());

												ret_code2   =  output2.getString("fml_ret1");
												ret_msg2    =  output2.getString("fml_ret2");

												if(output2.getString("fml_ret1").equals("0000")) {
													info("[��������� TM ����Ʈ ��� ���� ] �ֹι�ȣ |" + jumin_no + "|�����Ϸù�ȣ|" + auth_no);
													insertgolffeectn = proc.insertGolfFee(context, jumin_no, auth_no, "point", pay_amt, "9", mb_cdhd_no);  // log insert & mail send
													updatecnt = proc.updateIsFree(context, jumin_no, "05","","",recp_date); //����Ʈ ��� 

												}else{
													info("[��������� TM ����Ʈ ��� ���� ] �ֹι�ȣ |" + jumin_no + "|fml_ret1|" + ret_code2+ "|fml_ret2|" + ret_msg2);
													updatecnt = proc.updateIsFree(context, jumin_no, "06","",ret_msg2,recp_date); //����Ʈ ��� ����[��޿���]
												}
											}


										}else{
											info("[��������� TM ����Ʈ �������� ó�� ����] �ֹι�ȣ |" + jumin_no+"|fml_ret1|"+ret_code+"|fml_ret2|"+ret_msg);
											updatecnt = proc.updateIsFree(context, jumin_no, "04","",ret_msg,recp_date); //����Ʈ ���� ����
										}	
										/* ����Ʈ ����ó�� ��*/
									

									//����Ʈ ���� ��ȸ�� ����Ʈ ��ȸ �����ϰ��(����ó��)
									} else {
										info("[��������� TM ����Ʈ ���� ����] �ֹι�ȣ |" + jumin_no+"|�ܿ�����Ʈ|"+top_point);
										updatecnt = proc.updateIsFree(context, jumin_no, "03","","|�ܿ�����Ʈ|"+top_point,recp_date); //�ܾ׺���
										
										
									}

								}	

							//���հ��� Ȥ�� ī������ϰ��	
							} else if ("1".equals(auth_clss) || "2".equals(auth_clss)) {

								info("[��������� TM ���� ����] �ֹι�ȣ |" + jumin_no+"|�������� 1:ī�� 2:���հ���|"+auth_clss);
								
								if ( "".equals(getvald_lim) )
								{

									//���ȣ ��ȸ  
									/********************************************
									* MHA0010R0700 - 00:���� 
									* ���ȣ ��ȸ   :  for COMMIT  *
									*********************************************/

									TaoResult binResult = null;

									JoltInput entity_bin = null;

									entity_bin = new JoltInput();

									entity_bin.setServiceName("BSNINPT");
									entity_bin.setString("fml_trcode", "MHA0010R0700"); //�� ��ȣ��ȸ
									entity_bin.setString("fml_channel", "WEB");
									entity_bin.setString("fml_sec50", "bcadmin");
									entity_bin.setString("fml_sec51", hostAddress);
								
									entity_bin.setString("fml_arg1", card_no.substring(0,6) );

									java.util.Properties prop_bin = new java.util.Properties();
									prop_bin.setProperty("RETURN_CODE","fml_ret1");

									JtProcess jtproc_bin = new JtProcess();
									binResult = jtproc_bin.call(context, request, entity_bin, prop_bin);
									debug(binResult.toString());
									String bin_ret_code = binResult.getString("fml_ret1").trim(); 
									String bin_mb_no = "00"; 
									if ("00".equals(bin_ret_code))
									{
										bin_mb_no = binResult.getString("fml_ret2").trim(); 
									}
									info("[��������� TM ���ȣ ��ȸ] �ֹι�ȣ |" + jumin_no+"|fml_ret1|"+bin_ret_code+"|ȸ�����ȣ|"+bin_mb_no);

								
									//ī������ ��ȸ  
									/********************************************
									* MHL0260R0100 - 00,01:���� 
									* ī������ ��ȸ  :  for COMMIT  *
									*********************************************/
			
									TaoResult taoResult = null;

									JoltInput entity0 = null;

									entity0 = new JoltInput();
									
									// 2010�� 4�� 26�� ���� (���̻��ī���� ��� ȸ�����ȣ��  ���ȣ������ 13���� �Ѿ����, ī����ȸ�ÿ�  14���� �Ѱܾ� ��.
									if ( "13".equals(bin_mb_no)) {
										bin_mb_no = "00";
										
									}

									entity0.setServiceName("BSNINPT");
									entity0.setString("fml_trcode", "MHL0260R0100"); //����ī��������ȸ
									entity0.setString("fml_channel", "WEB");
									entity0.setString("fml_sec50", "bcadmin");
									entity0.setString("fml_sec51", hostAddress);
								
									entity0.setString("fml_arg1", "1"); //1:�ֹι�ȣ 2:����ڹ�ȣ 3: ī���ȣ
									entity0.setString("fml_arg2", jumin_no);  //1.�ֹι�ȣ 
									entity0.setString("fml_arg3", bin_mb_no); //ȸ����ȸ����ȣ
									entity0.setString("fml_arg4", "0"); 

									java.util.Properties prop = new java.util.Properties();
									prop.setProperty("RETURN_CODE","fml_ret1");

									JtProcess jtproc = new JtProcess();
									taoResult = jtproc.call(context, request, entity0, prop);
									debug(taoResult.toString());

									// �� �����ö���...
									String auth_ret_code = taoResult.getString("fml_ret1").trim(); 
									String auth_ret_msg = taoResult.getString("fml_ret2").trim();  

									info("[��������� TM ī���ȣ ��ȸ] �ֹι�ȣ |" + jumin_no+"|ī����ȸ���� ����|"+auth_ret_code);
									
									String jt_card_no = null;
									String jt_card_no_fmt = null;
									String str_ret2 = null; //������ȸ��
									vald_lim ="";
									int icardcnt = 0;
										
									if ("00".equals(auth_ret_code) || "01".equals(auth_ret_code))
									{
										

										if(taoResult.size() > 0) {
											taoResult.first();
											for(int i=0; i < taoResult.size(); i++) {
												jt_card_no = taoResult.getString("fml_ret3").trim();
												str_ret2=taoResult.getString("fml_ret2").trim();
												icardcnt++;
												debug("1��° ȣ�� ���� "+icardcnt+"��°ī���ȣ ="+ jt_card_no);
												int istrfind = card_no.indexOf("****");
												if (istrfind != -1){
													jt_card_no_fmt = jt_card_no.substring(0,8)+"****"+jt_card_no.substring(12,16);	
												} else {
													jt_card_no_fmt = jt_card_no;
												}
												if (card_no.equals(jt_card_no_fmt))
												{
													card_no  = jt_card_no;
													vald_lim = taoResult.getString("fml_ret6").trim(); //��ȿ�Ⱓ
													auth= true;
												}
												taoResult.next();
											}
										}
										//����ī�尡 �������
										if ( "01".equals(auth_ret_code) )
										{
											int ijoltcallcnt = 1;
											while(	Integer.parseInt(auth_ret_code) > 0 ) {
												ijoltcallcnt++;

												info("[��������� TM ī���ȣ ��ȸ "+ijoltcallcnt+"] �ֹι�ȣ |" + jumin_no+"|ī����ȸ���� ����|"+auth_ret_code);
												
												entity0.setString("fml_arg4", str_ret2 );
												debug("�ââ� fml_arg4 �϶� �ââ�" + str_ret2);

												taoResult = jtproc.call(context, request, entity0, prop);
												debug(taoResult.toString());

												auth_ret_code = taoResult.getString("fml_ret1");
												
												//������ �ϴ� ������ ī�尡 ��10���ΰ�� ���ϰ��� 01�� �Ѿ����,
												//2��° ȣ��� fml_ret1,fml_ret2 �� 00 ���� �Ѿ�ͼ� fml_ret3 ���� null ������ Exception �߻�
												String taoRetCode = "";
												try{
													String mTest = taoResult.getString("fml_ret3"); //
													taoRetCode ="OK";   
												} catch (TaoException ex) {
													taoRetCode ="ERROR"; //ī����ȸ ��������
												}
												//������ �ϴ� ���� ��

												if ("OK".equals(taoRetCode))
												{
													taoResult.first();
													for(int i=0; i < taoResult.size(); i++) {
														jt_card_no = taoResult.getString("fml_ret3").trim();
														str_ret2=taoResult.getString("fml_ret2").trim();
														icardcnt++;
														debug(ijoltcallcnt+"��° ȣ�� ����"+icardcnt+"��°ī���ȣ ="+ jt_card_no);

														int istrfind = card_no.indexOf("****");
														if (istrfind != -1){
															jt_card_no_fmt = jt_card_no.substring(0,8)+"****"+jt_card_no.substring(12,16);	
														} else {
															jt_card_no_fmt = jt_card_no;
														}

														if (card_no.equals(jt_card_no_fmt))
														{
															card_no  = jt_card_no;
															vald_lim = taoResult.getString("fml_ret6").trim(); //��ȿ�Ⱓ
															auth= true;
														}
														taoResult.next();
													}

												}

											}

										}
										//ī���ȣ�� �� ã�Ұų�, ������� 
										if ("".equals(vald_lim))
										{
											info("[��������� TM ī����ȸ ���� �ش�ī�����] �ֹι�ȣ |" + jumin_no );
											updatecnt = proc.updateIsFree(context, jumin_no, "11","","ī����ȸ ���� �ش�ī�����",recp_date); //ī����ȸ ����
										}
									
									} else {

										info("[��������� TM ī����ȸ ����] �ֹι�ȣ |" + jumin_no );
										updatecnt = proc.updateIsFree(context, jumin_no, "11","","ī����ȸ ���� �ش�ī�����",recp_date); //ī����ȸ ����
									}


								//Ÿ��ī���ϰ��
								} else {
									vald_lim = getvald_lim;
									auth= true;
								}
								info("[��������� TM ī���ȣ ��ȸ] �ֹι�ȣ |" + jumin_no+"|ī���ȣ|"+card_no+"|��ȿ�Ⱓ|"+vald_lim);

								////////////////////////////////////////////////////////����/////////////////////////////

								if ( auth == true )
								{

									debug(vald_lim);
									/********************************************
									* MJF6010R0100 - 01(fml_ret5):���� 
									* ���� ���� :  for COMMIT  *
									*********************************************/
									GolfPayAuthEtt payEtt = new GolfPayAuthEtt();

									String ins_term ="00"; 
									String merno = "765943401"; //��������ȣ (��ȸ��)

									if ("admTmMember".equals(action_key)){
										if (tm_buz.equals("03")){
											merno = "767445661"; //H&C��Ʈ��ũ
										} else if (tm_buz.equals("11"))	{
											merno = "767445687"; //Ʈ�����ڽ��� �ڸ���
										} else if (tm_buz.equals("12"))	{
											merno = "767445687"; //��������
										} else if (tm_buz.equals("13"))	{
											merno = "767445661"; //���غ���
										}
									}
									
									if ( "1".equals(auth_clss) ) ins_term ="00";
									else if	( "2".equals(auth_clss) ) ins_term ="60";

									payEtt.setMerMgmtNo(merno); //��������ȣ
									payEtt.setCardNo(card_no); 
									payEtt.setValid(vald_lim.substring(2,6));			
									payEtt.setAmount(pay_amt);
									payEtt.setInsTerm(ins_term); //�Һΰ�����
									payEtt.setRemoteAddr(hostAddress);

									GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
									boolean payResult = false;

									payResult = payProc.executePayAuth(context, request, payEtt);
									info("[��������� TM ����] �ֹι�ȣ |" + jumin_no+"|���|"+payResult);

									//����
									if (payResult) {
										
										auth_no = payEtt.getUseNo();                           // ī����� �Ϸù�ȣ
										
										info("[��������� TM ī����� ����] �ֹι�ȣ |" + jumin_no + "|�����Ϸù�ȣ|" + auth_no);
										
										//��������� ȸ������ó��(BCDBA.TBGGOLFCDHD)
										String	join_chnl = "0000";
										if ("admTmMember".equals(action_key)){

											if	(  "1".equals(golf_clss) ) {join_chnl ="0003"; golf_class_nm= "���"; }//���
											else if (  "2".equals(golf_clss) ) {join_chnl ="0002"; golf_class_nm= "���";} //���											
											else if (  "3".equals(golf_clss) ) {join_chnl ="0004"; golf_class_nm= "è�ǿ�";} //è�ǿ�
											else if (  "4".equals(golf_clss) ) {join_chnl ="0005"; golf_class_nm= "��";} //��
											//ī����ǰ �߰� 2010.07.15
											if  (  "0103".equals(rcru_pl_clss) ) {join_chnl ="0103"; golf_class_nm= "���"; }//���

			
										} else if ("admMojib".equals(action_key)) {

											if	(  "1".equals(golf_clss) ) {join_chnl ="1003"; golf_class_nm= "���"; }//���
											else if (  "2".equals(golf_clss) ) {join_chnl ="1002"; golf_class_nm= "���";} //���
											else if (  "3".equals(golf_clss) ) {join_chnl ="1001"; golf_class_nm= "è�ǿ�";} //è�ǿ�
											else if (  "4".equals(golf_clss) ) {join_chnl ="1005"; golf_class_nm= "��";} //��


										}
										
										tb_rslt_clss ="01";
										
										//ȸ�����Խ� insert�� update ó�� 2010.07.15 ����(��ȸ���϶� : 00 �ʿ�)
										tb_rslt_clss   = proc.updateGolfcdhd(context, jumin_no, join_chnl);  //����ȸ������ ���Ե� ��쿣 00ó����.

										info("[��������� TM ����ȸ�� ���̺� update �Ϸ�] �ֹι�ȣ |" + jumin_no +"|updateGolfcdhd|"+tb_rslt_clss );
										
										
										
										
										insertgolffeectn = proc.insertGolfFee(context, jumin_no, auth_no, card_no, pay_amt, auth_clss, mb_cdhd_no);  // log insert & mail send
										//insertgolffeectn = 1 ;
										info("[��������� TM ��ȸ�� ���̺� insert �Ϸ�] �ֹι�ȣ |" + jumin_no+"|insertgolffeectn|"+insertgolffeectn );
							
										updatecnt = proc.updateIsFree(context, jumin_no,tb_rslt_clss,vald_lim,"",recp_date);
										info("[��������� TM ���� ���̺� update �Ϸ�] �ֹι�ȣ |" + jumin_no);

										if( insertgolffeectn != 0 && updatecnt != 0){       //  DB processed successfully   

											info("[��������� TM ����ȸ�� ������ �Ϸ�] �ֹι�ȣ |" + jumin_no + "|hp|" + hp);
											b++;
											/********************************************
											* SMS �߼� 
											******************************************** */

											if (hp.length() > 9)
											{
												bln_sms = true;
											} 

											
											if ("admTmMember".equals(action_key)){ // TM�� ��ȭ���� ���� �߼�

												bln_email=true;

											}

										}else {
											//ī�������� ����
											
											/********************************************
											* MGA0030I0800 - 2(fml_ret2):���� 
											* ���� ���� ���:  for COMMIT  *
											*********************************************/
 
											boolean payCancelResult = false;

											payCancelResult = payProc.executePayAuthCancel(context, payEtt);

											String cancel_ret_code = payEtt.getResCode(); 
											String cancel_ret_msg = payEtt.getResMsg();
											info("[��������� TM �������] �ֹι�ȣ |" + jumin_no+"|���|"+cancel_ret_code+"|����|"+cancel_ret_msg);
											if(cancel_ret_code.equals("2")) {
												info("[��������� TM ī�� ��� ���� ] �ֹι�ȣ |" + jumin_no + "|�����Ϸù�ȣ|" + auth_no);
												insertgolffeectn = proc.insertGolfFee(context, jumin_no, auth_no, card_no, pay_amt, "9", mb_cdhd_no);  // log insert & mail send
												updatecnt = proc.updateIsFree(context, jumin_no, "15","","",recp_date); //ī�� ��� 

											}else{
												info("[��������� TM ī�� ��� ���� ] �ֹι�ȣ |" + jumin_no + "|���|" + cancel_ret_code+ "|����|" + cancel_ret_msg);
												updatecnt = proc.updateIsFree(context, jumin_no, "16","",cancel_ret_msg,recp_date); //ī�� ��� ����[��޿���]
											}

 
										} 


									} else {
										//���ν��н�
										info("[��������� TM �������� ó�� ����] �ֹι�ȣ |" + jumin_no+"|fml_ret5|"+payEtt.getResCode()+"|fml_ret6|"+payEtt.getResMsg());
										updatecnt = proc.updateIsFree(context, jumin_no, "12",vald_lim,payEtt.getResMsg(),recp_date); //�������� ó�� ����

									}
								}



							}/*if ("3".equals(auth_clss)) */

						// } /*8�� �ƴҶ� 2010.07.15 ���� */ 

	
					}/* if memkind ó�� */ 
					
					//sms �߼�ó��
					if (bln_sms==true){
						// SMS ���� ����
						HashMap smsMap = new HashMap();
						
						smsMap.put("ip", request.getRemoteAddr());
						smsMap.put("sName", hg_nm);
						smsMap.put("sPhone1", hp_ddd_no);
						smsMap.put("sPhone2", hp_tel_hno);
						smsMap.put("sPhone3", hp_tel_sno);
						smsMap.put("sCallCenter", "15666578");
						
						String smsClss = "674";

						String message = "[Golf Loun.G]"+hg_nm+"�� ���������(www.golfloung.com)ȸ�������������ֽñ�ٶ��ϴ�" ;
						SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
						String smsRtn = "";
						
						//SMS�߼�
						if (devip.equals(hostAddress)) {  //���߱�
							//smsRtn = smsProc.send(smsClss, smsMap, message);
							info("[��������� TM SMS ���߱�� �߼۾ȵ˴ϴ�. ] �ڵ�����ȣ |" + hp_ddd_no +"-"+  hp_tel_hno +"-"+ hp_tel_sno + "|�޼���|" + message);
						} else { //���
							smsRtn = smsProc.send(smsClss, smsMap, message);
							info("[��������� TM SMS �߼�] �ڵ�����ȣ |" + hp_ddd_no +"-"+  hp_tel_hno +"-"+ hp_tel_sno + "|�޼���|" + message);
						}	
						bln_sms=false;
					}
					 
					//email �߼�ó��
					if (bln_email==true){
						
						try{
							String[] email = BcUtil.getEmailArray(email_addr);
							int email_cnt = email.length;
	
							if ( !(email_addr=="" || "".equals(email_addr))  && email_cnt == 2  )
							{
								//if ("0103".equals(rcru_pl_clss)){
								//	mail_clss="oil"; //ī����ǰ�� SK������ �߼�
								//} else {
									mail_clss="movie"; //�׿ܴ� ��ȭ���� �߼�
								//}
								if (devip.equals(hostAddress)) {  //���߱�
									//proc.sendMail(email_addr,hg_nm,golf_class_nm,mail_clss);
									info("[��������� TM ���Ϲ߼� ���߱�� �߼۾ȵ˴ϴ�.] �ֹι�ȣ |" + jumin_no + "|email_addr|" + email_addr);
								}else {
									try{
										proc.sendMail(email_addr,hg_nm,golf_class_nm,mail_clss);
										info("[��������� TM ���Ϲ߼� �Ϸ�] �ֹι�ȣ |" + jumin_no + "|email_addr|" + email_addr);
									}catch (Exception ex) {
										info("[��������� TM ���Ϲ߼� ����] �ֹι�ȣ |" + jumin_no + "|email_addr|" + email_addr);
									}
									
									
								}
							}
						} catch(Throwable t) {}
						bln_email=false;
					}
					
								
					a++;
					
				} /*while��*/ 

				info("[��������� TM ȸ�� GolfLoung ���ᰡ��ó�� END : ��" + a + "�� ó�� �� " + b + "�� ������ ����ð�:"  + DateUtil.currdate("yyyy.MM.dd:HH.mm.ss") );										

				/********************************************
				* ��������� ����ڿ��� TM ��� �뺸
				********************************************/
				if ("admTmMember".equals(action_key)){
					proc.sendMailAdmin(context,a,b); 
				} else if ("admMojib".equals(action_key)) {
					 //������ ��ϴ�� ��ȸ : 	
				}
				

        } catch (Throwable ex) {

			warn("GolfLoungTMFeeMemberJoinActn Throwable | jumin_no:" + jumin_no + " | auth_no:" + auth_no + " ... ó����ERR ", ex);

            rx = new ResultException(ex);			
			rx.setTitleImage("error");			
			rx.addButton(goPage, addButton);
			rx.setKey("SYSTEM_ERR");

			throw rx;
        }
	
//���ϸ� �׽�Ʈ	
//	try {  	
//		
//		GolfLoungTMProc proc = new GolfLoungTMProc();
//		proc.sendMailAdmin(context,10,10); 
//
//      } catch (Throwable ex) {
//
//		warn("GolfLoungTMFeeMemberJoinActn Throwable | jumin_no:" + jumin_no + " | auth_no:" + auth_no + " ... ó����ERR ", ex);
//
//        rx = new ResultException(ex);			
//		rx.setTitleImage("error");			
//		rx.addButton(goPage, addButton);
//		rx.setKey("SYSTEM_ERR");
//
//		throw rx;
//    }
        
		return super.getActionResponse(context, responseKey);
		
	}

}

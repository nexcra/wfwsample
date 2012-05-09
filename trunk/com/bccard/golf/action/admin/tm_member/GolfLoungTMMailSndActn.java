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
***************************************************************************************************/
package com.bccard.golf.action.admin.tm_member;

import java.io.IOException;
import java.util.Vector;
import java.util.Hashtable;
import java.util.HashMap;
import java.net.InetAddress;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.ResultException;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.common.DateUtil;

import com.bccard.golf.jolt.JtTransactionProc;
import com.bccard.golf.jolt.JtProcess;

import com.bccard.waf.tao.jolt.JoltOutput;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput; 

import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.waf.common.BcUtil;
import com.bccard.waf.tao.TaoException;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.AppConfig;


/**
 * ���� ����� TM
 * @version 2009.07.02
 * @author  ������ [yskkang@bccard.com]
 */
public class GolfLoungTMMailSndActn extends AbstractAction  {

	
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

		String  disc_clss	= "";
		String  dc_amt		= "";

		String  tb_rslt_clss ="01";

		boolean auth	= false;		

		String action_key = super.getActionKey(context);
		debug(action_key);
		String str = "***";
		String str_hp = "0000";



		try {             	
				info("[��������� TM ����ȸ�� ���� ����] GolfLoungTMMailSndActn START ���۽ð�:" + DateUtil.currdate("yyyy.MM.dd:HH.mm.ss") );
 
				GolfLoungTMProc proc = new GolfLoungTMProc();
				
				juminNoList = proc.getMailSend(context,"����yyyymmdd");         //TM ��ϴ�� ��ȸ : �ֹι�ȣList	
	
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

					Hashtable data = new Hashtable();
					data = (Hashtable)juminNoList.get(a);

					jumin_no =(String)data.get("JUMIN_NO"); //�ֹι�ȣ
					golf_clss =(String)data.get("GOLF_CDHD_GRD_CLSS"); //��� 1:��� 2: ��� 3:è�ǿ� 4:��
					hg_nm =(String)data.get("HG_NM");	//�ѱ۸�
					email_addr =(String)data.get("EMAIL_ID"); //�̸���

					if	(  "1".equals(golf_clss) ) pay_amt ="25000";
					else if (  "2".equals(golf_clss) ) pay_amt ="50000";
					else if (  "3".equals(golf_clss) ) pay_amt ="200000";
					else if (  "4".equals(golf_clss) ) pay_amt ="120000";

					hp_ddd_no	=	(String)data.get("HP_DDD_NO");
					hp_tel_hno	=	(String)data.get("HP_TEL_HNO");
					hp_tel_sno	=	(String)data.get("HP_TEL_SNO");
					 
					disc_clss	=	(String)data.get("DISC_CLSS");
					dc_amt	=	(String)data.get("DC_AMT");

					debug("hphp  ���� hp_ddd_no + hp_tel_hno + hp_tel_sno=>>"+ hp_ddd_no + hp_tel_hno + hp_tel_sno );

					hp = "";
					if (hp_ddd_no.length()>=3 && hp_tel_hno.length()>=3 && hp_tel_sno.length()>=4 )	{
						debug("hphp   hp_ddd_no + hp_tel_hno + hp_tel_sno=>>"+ hp_ddd_no + hp_tel_hno + hp_tel_sno );
						debug("hphp   hp_tel_sno.indexOf(str_hp)=>>"+ hp_tel_sno.indexOf(str_hp));

						if ( hp_tel_sno.indexOf(str_hp) ==  -1 ) {
							hp = hp_ddd_no + hp_tel_hno + hp_tel_sno;
						}
					}
					debug("hphphphphphphphphphphphphphp=>>"+hp);
					debug("disc_clss="+disc_clss+",dc_amt="+dc_amt);

												
					/********************************************
					* SMS �߼� 
					******************************************** 

					if (hp.length() > 9)
					{
						// SMS ���� ����
						HashMap smsMap = new HashMap();
						
						smsMap.put("ip", request.getRemoteAddr());
						smsMap.put("sName", hg_nm);
						smsMap.put("sPhone1", hp_ddd_no);
						smsMap.put("sPhone2", hp_tel_hno);
						smsMap.put("sPhone3", hp_tel_sno);
						smsMap.put("sCallCenter", "15666578");
						
						debug("SMS����>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
						String smsClss = "674";

						String message = "[Golf Loun.G]"+hg_nm+"�� ���������(www.golfloung.com)ȸ�������������ֽñ�ٶ��ϴ�" ;
						SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
						String smsRtn = "";
						
						//SMS�߼�
						if (devip.equals(hostAddress)) {  //���߱�
							//smsRtn = smsProc.send(smsClss, smsMap, message);
							debug("���߱� SMS>>>>>>>>>>>"+ hp_ddd_no +"-"+  hp_tel_hno +"-"+ hp_tel_sno+">>>>>>>>>>>>>>>>>>>>>.."+message) ;
							info("[��������� SMS ���߱�� �߼۾ȵ˴ϴ�. ] �ڵ�����ȣ |" + hp_ddd_no +"-"+  hp_tel_hno +"-"+ hp_tel_sno + "|�޼���|" + message);

						} else { //���
							smsRtn = smsProc.send(smsClss, smsMap, message);
							info("[��������� SMS �߼�] �ڵ�����ȣ |" + hp_ddd_no +"-"+  hp_tel_hno +"-"+ hp_tel_sno + "|�޼���|" + message);
							//debug("��� SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);
						}	

					} */



					/********************************************
					* �̸��� �߼� 
					*********************************************/
					String[] email = BcUtil.getEmailArray(email_addr);
					int email_cnt = email.length;
					String mail_clss="oil";
		

					if ( !(email_addr=="" || "".equals(email_addr))  && email_cnt == 2  )
					{
						info("[��������� TM ���Ϲ߼� ����] �ֹι�ȣ |" + jumin_no + "|����]"+hg_nm+"|email_addr|" + email_addr);
						proc.sendMail(email_addr,hg_nm,golf_class_nm,mail_clss);
						info("[��������� TM ���Ϲ߼� �Ϸ�] �ֹι�ȣ |" + jumin_no + "|����]"+hg_nm+"|email_addr|" + email_addr);
					}


	
								
					a++;
				} /*while��*/ 

				info("[��������� TM ȸ�� GolfLoung ���ᰡ��ó�� END : ��" + a + "�� ó�� �� " + b + "�� ������ ����ð�:"  + DateUtil.currdate("yyyy.MM.dd:HH.mm.ss") );										

				

        } catch (Throwable ex) {

			warn("GolfLoungTMFeeMemberJoinActn Throwable | jumin_no:" + jumin_no + " | auth_no:" + auth_no + " ... ó����ERR ", ex);

            rx = new ResultException(ex);			
			rx.setTitleImage("error");			
			rx.addButton(goPage, addButton);
			rx.setKey("SYSTEM_ERR");

			throw rx;
        }
		return super.getActionResponse(context, responseKey);
	}

}

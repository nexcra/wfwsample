/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfLsnVodListActn
*   �ۼ���	: (��)�̵������ 
*   ����		: ����� > ���� > �����̾� ������ > ����Ʈ
*   �������	: Golf
*   �ۼ�����	: 2009-12-07
************************** �����̷� ****************************************************************
*    ����     �ۼ���   �������
*  20110304  �̰���   [http://www.bccard.com/->VIP����/����/����VIP������]���ٽ� Full����������
*  20110512  �̰���   [http://golfloung.familykorail.com/-> Home > ���縶�� > �������� > �����󷹽�] ���ӽ� Full����������
***************************************************************************************************/
package com.bccard.golf.action.lesson;

import java.io.IOException;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.Base64Encoder;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.security.cryptography.StringEncrypter;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.dbtao.proc.lesson.GolfLsnVodListDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.initech.eam.nls.CookieManager;
/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfLsnPreVodListActn extends GolfActn{ 
	
	public static final String TITLE = "����� > ���� > �����̾� ������ > ����Ʈ";

	/***************************************************************************************
	* ���� ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String userId = "";
		String isLogin = ""; 
		String isLoginEnc = "";		// �α������� ��ȣȭ - �������� ���� �� 
		String isView = "";			// ���󺸱� ���� ���� 
		String isViewEnc = "";		// ���󺸱� ���� ���� ��ȣȭ - �������� ���� �� 
		String script = "";
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		String in = request.getAttribute("actnKey").toString();
		
		try {
			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userId = (String)usrEntity.getAccount(); 
			}

			if(userId != null && !"".equals(userId)){
				isLogin = "Y";
			} else {
				isLogin = "N";
				isView = "N";
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.put("imgUrlPath", AppConfig.getAppProperty("IMG_URL_REAL")+"/lesson");

			// Request �� ����
			long page_no			= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			long record_size		= parser.getLongParameter("record_size", 10);		// ����������¼�
			String search_sel		= parser.getParameter("search_sel", "");
			String search_word		= parser.getParameter("search_word", "");

			String svod_clss		= parser.getParameter("svod_clss", "0001"); //0001:�ؿ��������η��� 0002:�ܰ躰�������� 0003:�����ӷ��� 0004:��Ȳ������ 0005:ȿ�����ο������
			String svod_lsn_clss	= parser.getParameter("svod_lsn_clss", "0001"); //0001:����׷���ƮƼ�Ľ� 0002:����Ŭ�ƴ㽺..	

			paramMap.put("svod_clss", svod_clss);
			paramMap.put("svod_lsn_clss", svod_lsn_clss);
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			dataSet.setString("SVOD_CLSS", svod_clss);
			dataSet.setString("SVOD_LSN_CLSS", svod_lsn_clss);
			dataSet.setString("PRE_YN", "Y");	// �����̾������󱸺� (Y:�����̾�������, N:�Ϲݵ�����)

			// ���� ���̺�(Proc) ��ȸ
			GolfLsnVodListDaoProc proc = (GolfLsnVodListDaoProc)context.getProc("GolfLsnVodListDaoProc");
			DbTaoResult lsnVodListResult = (DbTaoResult) proc.execute(context, request, dataSet);
	
			// ��ü 0��  [ 0/0 page] ���� ��������
			long totalRecord = 0L;
			long currPage = 0L;
			long totalPage = 0L;
				
			if (lsnVodListResult != null && lsnVodListResult.isNext()) {
				lsnVodListResult.first();
				lsnVodListResult.next();
				if (lsnVodListResult.getObject("RESULT").equals("00")) {
					totalRecord = Long.parseLong((String)lsnVodListResult.getString("TOTAL_CNT"));
					currPage = Long.parseLong((String)lsnVodListResult.getString("CURR_PAGE"));
					totalPage = (totalRecord % record_size == 0) ? (totalRecord / record_size) : (totalRecord / record_size)+1;
				}
			}
		
		
			//  Full������ ���� 
			if (in.substring(in.length()- 4, in.length()).equals("InBC") 
					|| in.substring(in.length()- 8, in.length()).equals("InKorail")){
				
				/*
				 * ������  �������� 1�������� �ƴ� Full������ �ϱ� ���ؼ�  isLogin, isView �� ���� 'Y'�̰�, 
				 * ������ seq�� ����� ���̵� �Ѱ� ����� 
				 * ���⼭ isLogin, isView, ����ھ��̵�(MID)�� ��ȣȭ�Ͽ� ����
				 * ~*.jsp�� MID�� ������ ���̵�� ���� �Ҵ�		
				 * ������̵�� ��ȣȭ�� ��� ���̵� �Ҵ� �� �� ����		 
				*/				
				
				isLogin = "Y";
				isView = "Y";
				
				/* 
				 *[ http://www.bccard.com/->VIP����/����/VIP ������ ]���� ���ӽ�
				*/
				if (in.substring(in.length()- 4, in.length()).equals("InBC") ) {
					info("## "+this.getClass().getName()+" | 'http://www.bccard.com/->VIP����/����/���� VIP������'���� ���� " );
				}
				
				/*
				 * [�Ʒ� Note�� �ڷ��Ͽ��� ���� �޴� Ŭ���� ���ӵǴ� URL]
				 * 
				 * 1. ���߱��  ���ӽ� (211.181.255.164 , ��Ʈ : 13300  ->���߱� ������ )
				 *  http://golfloung.familykorail.com:13300/app/golfloung/join_frame2.do?url=/app/golfloung/html/lesson/premium/familykorail.jsp
				 * 		1) ���߱� �׽�Ʈ�ÿ��� ������ hosts���Ͽ� ������ ���� �߰� �Ѵ�
				 * 			->211.181.255.164	golfloung.familykorail.com					
				 * 
				 * 2. ����  ���ӽ� (211.181.254.13)
				 *   http://golfloung.familykorail.com/app/golfloung/join_frame2.do?url=/app/golfloung/html/lesson/premium/familykorail.jsp
				 *  
				 *  
				 *  3. WCP�� decrypt �� sample
				 *   -> domain=www.familykorail.com&userno=0000003&username=�׽���3&logintime=20110512131647&user_level=1&fuserno=1111113&userck=f31c147335274c56d801f833d3c26a70
				 *   
				 *  4. �ڷ��� �׽�Ʈ ���̵� -> www.familykorail.com -> testok/1234 
				 *///�ڷ��� VIP������ ����
				if (in.substring(in.length()- 8, in.length()).equals("InKorail") ) {
					
					info("## "+this.getClass().getName()+ " | 'http://golfloung.familykorail.com/ -> '���� ���� " );
					
					//�ڷ��� ��Ű get
					String wcp = getValue("WCP", request);					
					wcp = GolfUtil.sqlInjectionFilter(wcp);
	
					//secret key �Ҵ�
					String key 	= "esoft_cp_asp_key";
					String iv 	= "esoft_initial_iv";
					StringEncrypter encrypter = new StringEncrypter(key, iv);
					
					/*
					 * ��ȣȭ(decrypt)�� Tip
					 * �Ʒ��� ���� Exception �߻��� encrypt�ÿ� decrypt�ÿ� ���Ű(secret key)�� ���� �ʾƼ� �߻�
					 * 'key'��  Ȯ���� �� ��
					 * javax.crypto.BadPaddingException: Given final block not properly padded
					 */  
					String dWCP = encrypter.decrypt(wcp);	
					//String dWCP = "domain=www.familykorail.com&userno=&username=�׽���3&logintime=20110512131647&user_level=1&fuserno=1111113&userck=f31c147335274c56d801f833d3c26a70";
					StringTokenizer st = new StringTokenizer(dWCP,"&");
					
					boolean usrLevVeri = false; //�ڷ��� ����� ����  ( user_level ���� 1�� 5�̻�  ���� �ش� )
					boolean usrLoginVeri = false; // �ڷ��� ����� �α��� ����
					
					String korailLev = "", korailId = "", korailUsrName = "";
					
					while (st.hasMoreTokens()) {
						 
						String tokenValue = st.nextToken();
						
						//��� ���ȸ� �α� ��� (�ڷ��� ����ȭ�ñ���)
						debug(" ## st.nextToken( : "+tokenValue);
						
						int idx = tokenValue.indexOf('=');
						
						String strKey =  StrUtil.isNull(tokenValue.substring(0,idx),"");
						String strVal =  StrUtil.isNull(tokenValue.substring(idx+1),"");	
						
						//�ڷ��� ����� ���� üũ
						if (strKey.equals("user_level") ){							
							if ( !strVal.equals("")){								
								if ( Integer.parseInt(strVal) == 1 || Integer.parseInt(strVal) >= 5 ){								
									usrLevVeri = true;
									korailLev = strVal;
								}
							}
						}						
						
						//�ڷ��� ����� �α��� ����  üũ
						if (strKey.equals("userno") ){							
							if ( !strVal.equals("")){
								usrLoginVeri = true;
								korailId = strVal;							
							}							
						}
						
						//�ڷ��� ����� ����
						if (strKey.equals("username") ){
							if ( !strVal.equals("")){	
								korailUsrName = strVal;
							}
						}
						 
					}
					
					//usrLevVeri = false;
					if ( usrLevVeri == true ){
						info("## "+this.getClass().getName()+" | '�ڷ��� ����� ���� : ["+korailLev+"] ���Ӱ����� ; id(userno) : ["+korailId+"], ���� : ["+korailUsrName+"]" );
					}else {
						//���� �Ұ�
						info("## "+this.getClass().getName()+" | '�ڷ��� ����� ����  ����: ["+korailLev+"] ���ӺҰ�;  id(userno) : ["+korailId+"], ���� : ["+korailUsrName+"]" );
						script = "alert('���ٱ����� �����ϴ�.');parent.parent.location.href='http://www.familykorail.com/';";
						request.setAttribute("script", script);
						subpage_key = "korail";
					}
					
					//usrLoginVeri = false;
					if ( usrLoginVeri == true ){
						info("## "+this.getClass().getName()+" | '�ڷ��� ����� �α��� ��; �ڷ��� ���� id(userno) : ["+korailId+"], ���� : ["+korailUsrName+"]"  );
					}else {
						
						info("## "+this.getClass().getName()+" | '�ڷ��� ����� �α���  ���� ����; �ڷ��� ���� id(userno) : ["+korailId+"], ���� : ["+korailUsrName+"]"  );
						
						//�α����� �ȵǾ� ���� ��� �α����� �ϰ� ���ϴ� �������� �Ѿ����; �α��� �����̷���
						String current_url = "http://golfloung.familykorail.com/app/golfloung/join_frame2.do?url=/app/golfloung/html/lesson/premium/familykorail.jsp";
						script = "alert('�α��� �� ���� �̿��� �����մϴ�.'); parent.parent.location.href='http://www.familykorail.com/login.php?backurl="+new String(Base64Encoder.encode(current_url.getBytes()))+"';";
						request.setAttribute("script", script);
						subpage_key = "korail";
						
					}
					
					usrLevVeri = false;
					usrLoginVeri = false;
					

				}
				/*//2012.01.02 09�� ���ڷ� ���� ����
				 *
				 * [�Ʒ� Note�� ���ѱ������ֿ���  ���� �޴� Ŭ���� ���ӵǴ� URL]
				 * 
				 * 1. ���߱��  ���ӽ� (211.181.255.164 , ��Ʈ : 13300  ->���߱� ������ )
				 *  http://golf.waf.co.kr:13300/app/golfloung/join_frame2.do?url=/app/golfloung/html/lesson/premium/shinhan_waf.jsp
				 * 		1) ���߱� �׽�Ʈ�ÿ��� ������ hosts���Ͽ� ������ ���� �߰� �Ѵ�
				 * 			->211.181.255.164	golf.waf.co.kr 				
				 * 
				 * 2. ����  ���ӽ� (211.181.254.13)
				 *   http://golf.waf.co/app/golfloung/join_frame2.do?url=/app/golfloung/html/lesson/premium/shinhan_waf.jsp
				 *  
				 *  
				 *  3. WCP�� decrypt �� sample
				 *   -> domain=golf.waf.co&waf_no=1111&waf_name=�׽���
				 *   
				 *  4. �׽�Ʈ ���̵� ->  ���ѱ������ִ� �׽�Ʈ ���̵� �������� ���� �Ʒ�  String dWCP �ּ��� Ǯ�� �׽�Ʈ, 
				 *                     ���ѱ������� �  �׽�Ʈ�ô� �̼���Ʈ�� �����Ͽ� �׽�Ʈ ���̵� �޾ƾ���
				 *   
				 ///���ѱ������� VIP������ ����				 * 
				else if (in.substring(in.length()- 9, in.length()).equals("InShinHan") ) {
					
					info("## "+this.getClass().getName()+ " | 'http://health.waf.co.kr -> '���� ���� " );
					
					//���ѱ�������  ��Ű get
					String wcp = getValue("WCP", request);					
					wcp = GolfUtil.sqlInjectionFilter(wcp);

	
					//secret key �Ҵ�
					String key 	= "!shinhanway12345";
					String iv 	= "1234567890abcde!";

					StringEncrypter encrypter = new StringEncrypter(key, iv);
					 
					String dWCP = encrypter.decrypt(wcp);
					//String dWCP = "domain=golf.waf.co&waf_no=1111&waf_name=�׽���";
					StringTokenizer st = new StringTokenizer(dWCP,"&");
					
					boolean usrLevVeri = false; //�ڷ��� ����� ����  ( user_level ���� 1�� 5�̻�  ���� �ش� )
					boolean usrLoginVeri = false; // �ڷ��� ����� �α��� ����
							
					String shinhanId = "";
					
					while (st.hasMoreTokens()) {
						 
						String tokenValue = st.nextToken();
						
						int idx = tokenValue.indexOf('=');
						
						String strKey =  StrUtil.isNull(tokenValue.substring(0,idx),"");
						String strVal =  StrUtil.isNull(tokenValue.substring(idx+1),"");	
						
						debug(" ## strKey : "+strKey + ", strVal : " + strVal);
						
						//�ڷ��� ����� �α��� ����  üũ
						if (strKey.equals("waf_no") ){							
							if ( !strVal.equals("")){
								usrLoginVeri = true;
								shinhanId = strVal;							
							}							
						}
						
					}
					
					if ( usrLoginVeri == true ){
						info("## "+this.getClass().getName()+" | '���� ��������  �α��� ��; ���� ��������  ���� id(userno) : ["+shinhanId+"]"  );
					}else {
						
						info("## "+this.getClass().getName()+" | '���� �������� �α���  ���� ����; ���� ��������  ���� id(userno) : ["+shinhanId+"]"  );
						
						//�α����� �ȵǾ� ���� ��� �α����� �ϰ� ���ϴ� �������� �Ѿ����; �α��� �����̷���
						script = "alert('�α��� �� ���� �̿��� �����մϴ�.'); parent.parent.location.href='http://www.waf.co.kr/member/login.do';";
						 
						request.setAttribute("script", script);
						subpage_key = "shinhan";
						
					}
					
					usrLoginVeri = false;

				}				
				*/
				
			}else {
				
				// ���ٱ��� ��ȸ	
				String permissionColum = "";
				if(isLogin.equals("Y")){
					try{
						permissionColum = "PMI_LESN_" + String.valueOf(Integer.parseInt(svod_clss)) + "_APO_YN";
						GolfBkPermissionDaoProc proc_permission = (GolfBkPermissionDaoProc)context.getProc("GolfBkPermissionDaoProc");
						DbTaoResult permissionView = proc_permission.execute(context, dataSet, userId, permissionColum);
	
						permissionView.next();
						if(permissionView.getString("RESULT").equals("00")){
							isView = permissionView.getString("LIMT_YN");
						}else{
							isView = "N";
						}										
					}
					catch(Throwable t){
						isView = "N";
					}
				}
				
				debug("## GolfLsnPreVodListActn | userId : " + userId + " | isLogin : " + isLogin + " | isView : " + isView + " | permissionColum : " + permissionColum + "\n");
			
			}			
			
			// �������� ���� �� ��ȣȭ
			StringEncrypter sender = new StringEncrypter("adf34alkjdf", "efef897akdjfkl");
			isLoginEnc = sender.encrypt(isLogin); 
			isViewEnc = sender.encrypt(isView); 

			paramMap.put("isLoginEnc", isLoginEnc);
			paramMap.put("isViewEnc", isViewEnc);	
			paramMap.put("totalRecord", String.valueOf(totalRecord));
			paramMap.put("currPage", String.valueOf(currPage));
			paramMap.put("totalPage", String.valueOf(totalPage));				
			paramMap.put("resultSize", String.valueOf(lsnVodListResult.size()));
			
			request.setAttribute("lsnVodListResult", lsnVodListResult);
			request.setAttribute("record_size", String.valueOf(record_size));
	        request.setAttribute("paramMap", paramMap);
	       	        
		} catch(Throwable t) {
			
			if (in.substring(in.length()- 8, in.length()).equals("InKorail") ) {
				
				//���� �߻��� �ڷ��� ������������ �����̷���				
				debug(TITLE, t);
				script = "location.href='http://www.familykorail.com/error/error_500.html';";
				request.setAttribute("script", script);
				subpage_key = "korail";
				
			}else {	
				throw new GolfException(TITLE, t);				
			}
			
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
	
	public String getValue(String key, HttpServletRequest request) {		
		return CookieManager.getCookieValue(key, request);
	}	
	
	
}

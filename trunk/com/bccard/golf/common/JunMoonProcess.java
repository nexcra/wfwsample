/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : JunMoonProcess
*   �ۼ���    : (��)�̵������ �̰���
*   ����      : ���� ���μ���
*   �������  : golf
*   �ۼ�����  : 2011-01-12
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.common;

import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.factory.JunMoonFactory;
import com.bccard.golf.factory.JunMoonProcFactory;
import com.bccard.waf.core.Code;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoException;

/**
 * @author Administrator
 *
 */
public class JunMoonProcess {


	JunMoonFactory jmf = null;
	
	public JunMoonProcess(WaContext context) {
		jmf = new JunMoonProcFactory().getJumMoon(context);		
	}
	
	public HashMap junMoonInfo(WaContext context		
					            , HttpServletRequest request				           
					            , Properties prop, HashMap hmap){
		HashMap rHmap = new HashMap();
		rHmap = jmf.procss(context, request, prop, hmap);	
		
		return rHmap;
		
	}	
	
	public ResourceBundle getProperties(WaContext context){
		
		ResourceBundle paraSProp = null ;
		
//		Code junMoType = context.getCode("10","GOLFLOUNG");
//    	
//    	if (junMoType.getDetail().equals("J")){
//    		paraSProp = ResourceBundle.getBundle("oldJMResPar");
//    	}else if (junMoType.getDetail().equals("M")){
//    		paraSProp = ResourceBundle.getBundle("newJMResPar");
//    	}
    	
		Code junMoType = context.getCode("10","GOLFLOUNG");
		
		String type = "";
		type = "J";
    	
    	if (type.equals("J")){
    		paraSProp = ResourceBundle.getBundle("oldJMResPar");
    	}else if (type.equals("M")){
    		paraSProp = ResourceBundle.getBundle("newJMResPar");
    	}
    	    	
		return paraSProp;
		
	}		

}

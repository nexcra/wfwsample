/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : JoltFactory
*   �ۼ���    : (��)�̵������ �̰���
*   ����      : Jolt Factory
*   �������  : golf
*   �ۼ�����  : 2011-01-12
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.factory;

import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.jolt.JtProcess;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.jolt.JoltInput;
import com.bccard.waf.tao.jolt.JoltOutput;
import org.apache.log4j.Logger;
import com.bccard.waf.logging.WaLogger;

/**
 * @author Administrator
 *
 */
public class JoltFactory extends JunMoonFactory{

	JtProcess jt_pt = new JtProcess();
	JoltOutput output = null;
	JoltInput cardInput_pt = null;
	private transient Logger logger;
	ResourceBundle serNameProp;
	ResourceBundle paraQProp;	
	
    private void initLogger() {
        logger = (Logger)WaLogger.getLogger(this.getClass().getName());
    }	
	
    protected void debug(String log) {
        if ( logger == null ) initLogger();
        logger.debug(log);
    }	
	
	public JoltFactory(ResourceBundle serNameProp, ResourceBundle paraQProp) {
		this.serNameProp = serNameProp;
		this.paraQProp = paraQProp;		
	}
	

	public HashMap procss(WaContext context, HttpServletRequest request,
			Properties prop1, HashMap hmap) {
		
		JtProcess jt_pt1 = new JtProcess();
		JoltOutput output1 = null;
		JoltInput cardInput_pt1 = null;
		HashMap hm = new HashMap();
		
		try{
			
			//set  
			cardInput_pt1 = new JoltInput( hmap.get("serName").toString() );			
			cardInput_pt1.setServiceName(hmap.get("serName").toString());			
			
			Object [] setStr = hmap.keySet().toArray();
			
			debug(" ##  11-----------  hmap.keySet() : " + hmap.keySet());
			
			for(int i=0; i<setStr.length; i++){
			
				/*	cardInput_pt1.setString(A, B)
				 *  A�� 'fml_trcode'�̸� B��  *JunMoon.properties�� �����ŷ� �ڵ� �� �Ҵ�
				 * 	A�� 'serName'�̸� B��  'BSNINPT'�� ����  ( BSNINPT:�����ŷ� �ڵ� ��ȸ   �����ӿ� ��ȸ���� )
				 *  �׿� A��  *JMReqPar.properties�� �ش� �ڵ� �� �Ҵ�
				 */
				
				debug(" ##  --------------- 1 : " + setStr[i].toString());
				if ( setStr[i].toString().equals("fml_trcode") ){					
					cardInput_pt1.setString(setStr[i].toString(), serNameProp.getString(hmap.get(setStr[i]).toString()));
					debug ("******Jolt________________1 setStr["+i+"] : [" +setStr[i] +"], [" + setStr[i].toString()+"], [" 
							+ hmap.get(setStr[i]).toString()+"], ["+ serNameProp.getString(hmap.get(setStr[i]).toString()) +"]");
					
				}else if ( setStr[i].toString().equals("serName") ){
					cardInput_pt1.setServiceName("BSNINPT");			
					debug ("*****Jolt________________2 setStr["+i+"] : [" +setStr[i] +"], [" + setStr[i].toString()+"]");
					
				}else {
					cardInput_pt1.setString( paraQProp.getString(setStr[i].toString()), hmap.get(setStr[i]).toString() );
					debug ("*****Jolt________________3 setStr["+i+"] : [" +setStr[i] +"], [" + setStr[i].toString()+"], [" 
							+ hmap.get(setStr[i]).toString()+"], ["+ paraQProp.getString(setStr[i].toString()) +"]");					
				}
				
			}

			//call
			output1 = jt_pt1.call(context, request, cardInput_pt1, prop1);
					
			//get
			hm.put("value",output1);				
					
		
		}catch(TaoException e){
			e.printStackTrace();
		}
		
		return hm;	
	}

}

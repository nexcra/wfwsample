/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : JunMoonProcFactory
*   �ۼ���    : (��)�̵������ �̰���
*   ����      : ���� ���μ��� ���丮
*   �������  : golf
*   �ۼ�����  : 2011-01-12
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.factory;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.bccard.waf.core.Code;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.logging.WaLogger;

/**
 * @author Administrator
 *
 */
public class JunMoonProcFactory {
	
	ResourceBundle serNameProp  = null ;
	ResourceBundle paraQProp  = null ;	
	
	public JunMoonProcFactory() {
		// TODO Auto-generated constructor stub
	}
	
	private transient Logger logger;
	
    private void initLogger() {
        logger = (Logger)WaLogger.getLogger(this.getClass().getName());
    }	
	
    protected void debug(String log) {
        if ( logger == null ) initLogger();
        logger.debug(log);
    }    
	
	public JunMoonFactory getJumMoon(WaContext context){
		
		JunMoonFactory jmf = null;
		String type = "";	//�⺻����ȣ���� (J:Jolt, M:MCA)

		Code junMoType = context.getCode("10","GOLFLOUNG");
    	type = junMoType.getDetail();   	
    	type = "J";
    	try {
    		
			if (type.equals("J")){
				debug ("******jolt________________start----------");
				serNameProp = ResourceBundle.getBundle("oldJunMoon");
				paraQProp = ResourceBundle.getBundle("oldJMReqPar");				
				jmf = new JoltFactory(serNameProp, paraQProp);
				
			}else if (type.equals("M")){
				debug ("******MCA________________start--------------");
				serNameProp = ResourceBundle.getBundle("newJunMoon");
				paraQProp = ResourceBundle.getBundle("newJMReqPar");				
				jmf = new MCAFactory(serNameProp, paraQProp);
				
			}
			
		} catch (Exception e) {				
			e.printStackTrace();
		}	

		return jmf;
		
	}

}

/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : MCAFactory
*   �ۼ���    : (��)�̵������ �̰���
*   ����      : MCA Factory
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

import org.apache.log4j.Logger;

import com.bccard.waf.core.WaContext;
import com.bccard.waf.logging.WaLogger;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;
import com.bccard.waf.tao.mca.McaInput;



/**
 * @author Administrator
 *
 */
public class MCAFactory extends JunMoonFactory{
	
	
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

	/**
	 * 
	 */
	public MCAFactory(ResourceBundle serNameProp, ResourceBundle paraQProp) {
		this.serNameProp = serNameProp;
		this.paraQProp = paraQProp;		
	}

	
	public HashMap procss(WaContext context, HttpServletRequest request,
			Properties prop1, HashMap hmap) {

		TaoConnection tcon = null;
		HashMap hm = new HashMap();
		debug ("******MCA________________start----");
		try {	
			debug ("******MCA________________0_________");
			tcon = context.getTaoConnection("mca",null);
			debug ("******MCA________________1_________");
			// 1. ��û ���� ������ ����
			McaInput input = new McaInput();
//			input.setString("indv_corp_clcd","1");
//			input.setString("jumin_no",jumin_no);
//			input.setString("cotn_inqr_key",next_key);
//			input.setString("svc_clss","3");
			
			Object [] setStr = hmap.keySet().toArray();		
			debug ("******MCA________________2_________ : " + setStr.length);

			for(int i=0; i<setStr.length; i++){			

				if ( setStr[i].toString().equals("fml_trcode") ){
					input.setString(setStr[i].toString(), serNameProp.getString(hmap.get(setStr[i]).toString()));
					debug ("******MCA________________1 setStr["+i+"] : [" +setStr[i] +"], [" + setStr[i].toString()+"], [" 
							+ hmap.get(setStr[i]).toString()+"], ["+ serNameProp.getString(hmap.get(setStr[i]).toString()) +"]");
					
				}else if ( setStr[i].toString().equals("serName") ){
					input.setString(setStr[i].toString(), "BSNINPT");		
					debug ("*****MCA________________2 setStr["+i+"] : [" +setStr[i] +"], [" + setStr[i].toString()+"]");
					
				}else {
					input.setString( paraQProp.getString(setStr[i].toString()), hmap.get(setStr[i]).toString() );					
					debug ("*****MCA________________3 setStr["+i+"] : [" +setStr[i] +"], [" + setStr[i].toString()+"], [" 
							+ hmap.get(setStr[i]).toString()+"], ["+ paraQProp.getString(setStr[i].toString()) +"]");
				}
				
			}				
			
			// 2. ���� ȣ��
			//TaoResult output = tcon.execute("${Service}",input);
			TaoResult output = tcon.execute(hmap.get("serName").toString() ,input);
			
			// 3. ���� ���� ������ ��ȸ
			// �ܰ� �÷� ��ȸ
//			output.getString("cotn_data");
//			// ������ �÷� ��ȸ
//			while( output.isNext() ) {
//				output.next();
//				output.getString("cotn_data");
//			}
			
			hm.put("value",output);	
		

		} catch(TaoException te) {
			te.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (tcon!=null) tcon.close(); 
			} catch(Throwable ignore) {}
		}		
		
		return null;
	}

}

/**********************************************************************************************************************
*   Ŭ������  : BadExecWinDir
*   �ۼ���    : csj007
*   ����      : ���� ����Ʈ ��ȸ ��ɾ�
*   �������  :etax
*   �ۼ�����  : 2008.07.17
************************** �����̷� ***********************************************************************************
*    ����      ����   �ۼ���   �������
*
**********************************************************************************************************************/
package com.bccard.golf.common;

import java.util.*;
import java.io.*;
import com.bccard.waf.core.AbstractEntity;


/** ***************************************
* renewal 
* @version 2008.08.27 
* @author csj007
********************************************** */

public class BadExecWinDir extends AbstractEntity{

/** ******************************************************************************** 
* ���
* @version 2008.08.27 
* @author csj007
* @return  
********************************************************************************** */ 
	public ArrayList cmdLsExec(String dir){
		ArrayList result = new ArrayList();
		try
		{
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec("ls -lt " + dir);
			InputStream stdin = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(stdin);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			System.out.println("dir :"+dir);
			System.out.println("<OUTPUT>");
			while ( (line = br.readLine()) != null) {
				result.add(line);    
				System.out.println(line);
			}
			System.out.println("</OUTPUT>");
			int exitVal = proc.waitFor();
			System.out.println("Process exitValue: " + exitVal);
		} catch (Throwable t)
		{
			
		}
		return result;
	}



}
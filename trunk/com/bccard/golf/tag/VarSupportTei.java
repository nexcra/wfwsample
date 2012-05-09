/**********************************************************************************************************************
*   Ŭ������  : VarSupportTei
*   �ۼ���    : �̺���
*   ����      : VarSupportTei
*   �ۼ�����  : 2005.12.12
**********************************************************************************************************************/
package com.bccard.golf.tag;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/** ***************************************
* renewal 
* @version 2005 12 12 
* @author �̺���
********************************************** */
public class VarSupportTei extends TagExtraInfo {

/** ******************************************************************************** 
* ������ �����ϰ� Ŭ������ ������ ��´�.
* @version 2005 12 12 
* @author �̺���
* @param data TagData��ü.
* @return  VariableInfo[] 
********************************************************************************** */ 
	public VariableInfo[] getVariableInfo(TagData data) {
		VariableInfo[] variables = new VariableInfo[1];

		String var  = data.getAttributeString("var");
		String type = data.getAttributeString("type");
		//String declare = data.getAttributeString("declare");
		//boolean b_declare = StrUtil.parseBoolean(declare, true);

		int scope = VariableInfo.AT_END;
		if (var != null ) {
			variables[0] = new VariableInfo(var, type, true, scope );
		}
		VariableInfo[] result = new VariableInfo[1];
		System.arraycopy(variables, 0, result, 0, 1);
		return result;
	}
}

/**********************************************************************************************************************
*   클래스명  : VarSupportTei
*   작성자    : 이보아
*   내용      : VarSupportTei
*   작성일자  : 2005.12.12
**********************************************************************************************************************/
package com.bccard.golf.tag;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/** ***************************************
* renewal 
* @version 2005 12 12 
* @author 이보아
********************************************** */
public class VarSupportTei extends TagExtraInfo {

/** ******************************************************************************** 
* 변수를 생성하고 클래스를 변수에 담는다.
* @version 2005 12 12 
* @author 이보아
* @param data TagData객체.
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

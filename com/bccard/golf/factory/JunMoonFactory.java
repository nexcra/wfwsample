/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : JunMoonFactory
*   �ۼ���    : (��)�̵������ �̰���
*   ����      : ���� Factory
*   �������  : golf
*   �ۼ�����  : 2011-01-12
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.factory;

import java.util.HashMap;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.core.WaContext;

public abstract class JunMoonFactory {
	
	public abstract HashMap procss(WaContext context, HttpServletRequest request
								, Properties prop, HashMap hmap);

}

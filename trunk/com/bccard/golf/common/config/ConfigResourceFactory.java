/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : MerInfoBaseChgProc
*   �ۼ���     : (��)�̵������ �ǿ���
*   ����        : ��ž �ý��� ���� ����. (��������)
*   �������  : Golf
*   �ۼ�����  : 2009-04-08
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
* 
***************************************************************************************************/
package com.bccard.golf.common.config;

import java.util.Properties;
import com.bccard.waf.resource.ResourceFactory;
import com.bccard.waf.resource.Resource;

import com.bccard.golf.common.config.ConfigResource;




/******************************************************************************
* Golf : ConfigResourceFactory
* @author	(��)�̵������
* @version	1.0
******************************************************************************/

public class ConfigResourceFactory extends ResourceFactory {
	/**
	 * @param Properties properties ������Ƽ
	 * @return Resource 
	 */
    public Resource getResource(Properties properties) {
        return new ConfigResource(this,properties);
    }
}
 
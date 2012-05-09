/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : ResponseData
*   �ۼ���     : (��)�̵������ �ǿ���
*   ����        : XML ���
*   �������  : Golf
*   �ۼ�����  : 2009-04-16
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public final class ResponseData extends HashMap implements Map, Serializable {
	
	private String title;
	private Status status;
	
    /** ************************************************************************
     * ���� ���� ���� ������ ���� ��ü.
     * @param actionKey �׼�Ű
     ************************************************************************ */
	ResponseData(String title) {
        super();
        this.title = title;
    }

    /**
     * @param status
     */
    public void setStatus(Status status) {
  		this.status = status;
    }
    
    /**
     * @return
     */
    public Status getStatus() {
    	if ( this.status == null ) {
    		this.status = new RuntimeStatus(this.title);
    	}
    	return this.status;
    }

}
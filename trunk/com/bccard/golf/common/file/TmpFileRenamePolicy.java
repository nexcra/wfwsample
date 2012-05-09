/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : TmpFileRenamePolicy
*   �ۼ���     : (��)�̵������ ������
*   ����        : ÷������ ��Ͻ� �ӽ� ���� �̸� ������. (��������)
*   �������  : Golf
*   �ۼ�����  : 2009-05-13
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.common.file;

import java.io.File;
import java.util.*;
import com.bccard.waf.common.DateUtil;
import com.oreilly.servlet.multipart.FileRenamePolicy;

/******************************************************************************
* m4
* @author �ǿ��� 
* @version 2009.4.8
******************************************************************************/
public class TmpFileRenamePolicy implements FileRenamePolicy {
	/** ����Ű */
	//private String sid;

	/*************************************************************
	 * �⺻.
	 ************************************************************/
	public TmpFileRenamePolicy() {
	}

	/*************************************************************
	 * �̸�����ó��.
	 * @param file ��������
	 ************************************************************/
	public File rename(File file) {
		
		String currdate = DateUtil.currdate("yyyyMMddHHmmss");

		StringTokenizer st = new StringTokenizer(file.getName(),".");
		String[] temp = new String[st.countTokens()];
		
		int count = 0;
		while(st.hasMoreTokens()){
			temp[count] = st.nextToken();
			count++;
		}

		return new File(file.getParent(), currdate+"."+temp[count-1]);
	}
}
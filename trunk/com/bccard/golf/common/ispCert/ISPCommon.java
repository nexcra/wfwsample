/***************************************************************************************************
 *   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
 *   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
 *   Ŭ������  : ISPCommon.java
 *   �ۼ���    : (��)�̵������ �̰���
 *   ����      : ISP ���� Ŭ����
 *   �������  : Golf
 *   �ۼ�����  : 2011.02.10
 ************************** �����̷� ****************************************************************
*    ����       �ۼ���      �������
 ***************************************************************************************************/
package com.bccard.golf.common.ispCert;

import java.util.HashMap;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.waf.core.WaContext;

public class ISPCommon extends GolfActn{

	public void ispRecord(WaContext context, HashMap hmap) {				
		
		String ispAccessYn = hmap.get("ispAccessYn").toString();
		String veriResCode = hmap.get("veriResCode").toString();
		String title = hmap.get("title").toString();
		String memName = hmap.get("memName").toString();
		String memSocid = hmap.get("memSocid").toString();
		String ispCardNo = hmap.get("ispCardNo").toString();
		String cstIP = hmap.get("cstIP").toString();
		
		try {
			
            if(ispAccessYn.equals("Y")){
            	
                // ISP �α� ����
                String logSaveResultYn = "N";

                ISPCertification ispProc = (ISPCertification)context.getProc("ISPCertification");

                DbTaoDataSet input = new DbTaoDataSet(title);
                input.setString("userNm",       memName);           //���̸�
                input.setString("userSocid",    memSocid);          //�ֹι�ȣ
                input.setString("userCardNo",   ispCardNo);         //ISP���� �Ѿ�� ī���ȣ
                input.setString("userIp",       cstIP);             //Ŭ���̾�Ʈ IP
                input.setString("vfcRslt",      veriResCode);           //��������ڵ� (1: �����ֹ��Ϸ�   3:�ֹ�������)
                input.setString("urlType",      "BCGOLFN");         //���޴���
                
                logSaveResultYn = ispProc.insertIspLog(context, input);                    

                if(logSaveResultYn.equals("Y")){
                	debug("## ���� class name : " +hmap.get("className").toString() +
                			" | ISP �α� ���� ���� | logSaveResultYn : "+logSaveResultYn+ " | userSocid : "+memSocid);
                }else {                	
                	debug("## " +hmap.get("className").toString() +
                			" | ISP �α� ���� ���� �߻� | logSaveResultYn : "+logSaveResultYn+ " | userSocid : "+memSocid);
                }
                
            }

        } catch(Exception e) {        	
        	debug("## " +hmap.get("className").toString() +" | ISP �α� ���� ���� ���� �߻�"+e); 
        }
		
		
	}
}

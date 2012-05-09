/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfWeatherXmlActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ����
*   �������  : golf
*   �ۼ�����  : 2009-06-15
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.weather;

import java.io.*;
import java.util.*;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class GolfWeatherXmlProc {
	
	 public GolfWeatherXmlProc() { }
	 
	 public List readXml(String Code) throws Exception {
//		 ��������
		 
	  List xmlEtt = new ArrayList();
		  
	  try {	        
	    //�б� ���� ���� ����		   
		SAXBuilder oBuilder = new SAXBuilder();
		Document oDoc = oBuilder.build("http://hosting.kweather.co.kr/bccard_golf/"+Code+".xml");

		Element xmlRoot = oDoc.getRootElement(); // root element
		List trackListList = xmlRoot.getChildren(); // root element -> List : trackList
		Element trackListEle = null; // trackList
		Element trackEle = null; // track
		Element memberEle = null; // ett
		
		for (int i = 0; i < trackListList.size(); i++) { // trackList 
			trackListEle = (Element) trackListList.get(i); 
			List fctDaysList = trackListEle.getChildren();
			//System.out.println("--trackListEle.getChildren()- => " + trackListEle.getName());
			
			//if (trackListEle.getName().equals("fctDays")) {
				
				for (int j = 0; j < fctDaysList.size(); j++) { // track
					trackEle = (Element) fctDaysList.get(j);
					//System.out.println("--fctDaysEle- => " + trackEle.getChildren());						

					List memberList = trackEle.getChildren();
					for (int k = 0; k < memberList.size(); k++) { // member
						
						memberEle = (Element) memberList.get(k);
						String ett = memberEle.getText().toString();
						xmlEtt.add(ett);
						//System.out.println(memberEle.getName()+" : " +memberEle.getText());			
					}
				} 
			//}
		}		
		return xmlEtt;
		
	  } catch (Exception e) {
		
		return null;
	  }
	}
}

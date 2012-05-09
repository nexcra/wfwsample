/** ****************************************************************************
 * �� �ҽ��� �ߺ�ī�� �����Դϴ�.
 * �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
 * �ۼ� : csj007
 * ���� : ���漼 ���� �ý��� ����
 ************************** �����̷� *******************************************
 *    ����      ����   �ۼ���   �������
 *
 **************************************************************************** */
package com.bccard.golf.common;

import java.io.File; 
import java.io.IOException;
import java.util.Set;
import java.util.Iterator;
import java.util.List;

import jxl.write.WritableFont;
import net.e4net.vman.VManAppServer;

import com.bccard.golf.common.BcLog;
import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.MakeFile;
import com.bccard.golf.common.MakeFileResEtt;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BcUtil;
import com.bccard.golf.dbtao.DbTaoResult;


/** ***************************************
*  ��ȸ ���� �ٿ�ε� 
* @version 2005 12 12 
* @author �̺���
********************************************** */
public class DownloadData {
	private String m_user_nm;		// ȸ����ȣ
	private String m_data_clss;		// �ڷᱸ�� : ����, �̿�, û��
	private String m_file_clss;		// ���ϱ��� : ����, �ؽ�Ʈ
	private String m_file_name;		// �����̸�
	// -------------------------------------------------------------
	// Added By, Leee Eun Ho, 2004-09-19, Sun
	// -------------------------------------------------------------
	private String m_req_date;		// ������ û�����ϻ���(û������)
	private String m_buz_no;		// ������ û�����ϻ���(����ڹ�ȣ)
	// -------------------------------------------------------------
	private MakeFileResEtt resultEtt;

/** ******************************************************************************** 
* default constructor
* @version 2005 12 12 
* @author �̺���
* @param id String��ü.
* @param data_clss String��ü.
* @param file_clss String��ü.
* @return  
********************************************************************************** */ 
	public DownloadData(String id, String data_clss, String file_clss) {
		this.m_user_nm = id;
		this.m_data_clss = data_clss;
		this.m_file_clss = file_clss;
		resultEtt = new MakeFileResEtt();

	}

/** ******************************************************************************** 
* constructor : VManServlet ������
* @version 2005 12 12 
* @author �̺���
* @param id String��ü.
* @param file_name String��ü.
* @param file_clss String��ü.
* @param check boolean��ü.
* @return  
********************************************************************************** */ 
	public DownloadData(String id, String file_name, String file_clss, boolean check) {
		this.m_user_nm = id;
		this.m_file_name = file_name;
		this.m_file_clss = file_clss;
		resultEtt = new MakeFileResEtt();
	}

/** **********************************************
// Writted By, Lee Eun Ho, 2004-09-19, Sun
 *********************************************** */

/** ******************************************************************************** 
* ���
* @version 2005 12 12 
* @author �̺���
* @param req_date String��ü.
* @param buz_no String��ü.
* @param id String��ü.
* @param data_clss String��ü.
* @param file_clss String��ü.
* @return  
********************************************************************************** */ 
	public DownloadData(String req_date, String buz_no, String id, String data_clss, String file_clss) {
		this.m_req_date = req_date;
		this.m_buz_no = buz_no;
		this.m_user_nm = id;
		this.m_data_clss = data_clss;
		this.m_file_clss = file_clss;
		resultEtt = new MakeFileResEtt();
	}

/********************************************************************************** 
* MakeJsScript : VManServlet�� ���� �ڹٽ�ũ��Ʈ ����
* @version 2005 12 12 
* @author �̺���
* @return  MakeFileResEtt 
************************************************************************************/ 
	public MakeFileResEtt makeJsScript() {
		String js = "";
		String sessionId = "";

		// VManServlet ����
		String fileFilter = "Zip Files (*.xls)|*.xls|All Files (*.*)|*.*||";
		VManAppServer vman = new VManAppServer();

		vman.setRemoveFile(false);		// ���� ���� ����

		File v_man_file  = new File(this.m_file_name); 
			BcLog.accessLog("=============>authList this.m_file_name :"+this.m_file_name );
			BcLog.accessLog("=============>authList this.m_user_nm :"+this.m_user_nm );
			BcLog.accessLog("=============>authList v_man_file :" +v_man_file);
sessionId = vman.putFile(this.m_user_nm, v_man_file);   


		if( (sessionId != null) || (sessionId != "") ){
			// �ٿ�ε� Filter ���� 
			vman.setFileFilter(fileFilter);
			// ������ �ڹٽ�ũ��Ʈ ����
			js = vman.getJavaScript(); 
		}else{
		}

		// ���� ��ũ��Ʈ�� �� �Ѿ������ �˻�
		if(js.indexOf("ERROR") > -1){
			resultEtt.setErr_msg("���� ��ũ��Ʈ ���� ����");
		}

		String filename = m_file_name.substring(m_file_name.lastIndexOf("/")+1, m_file_name.length());	// ��������ϸ�
		resultEtt.setFile_name(filename);
		if ( "excel".equals(this.m_file_clss) ) {
			resultEtt.setFile_type("Excel (.xls)");
		} else {
			resultEtt.setFile_type("Text (.txt)");
		}
		File file_tmp = new File(m_file_name);
		String fileSize = "";
		long f_size = file_tmp.length();

		if(f_size < 1024){
			fileSize = f_size + " Byte";
		}else if(f_size >= 1024 && f_size < 1024*1024){
			fileSize = f_size/1024 + " KB";
		}else if(f_size >= 1024*1024 && f_size < 1024*1024*1024){
			fileSize = f_size/(1024*1024) + " MB";
		}else if(f_size >= 1024*1024*1024){
			fileSize = f_size/(1024*1024*1024) + " GB";
		}
		resultEtt.setFile_size(fileSize);
		resultEtt.setJs_str(js);

		return resultEtt;
	}

/** ******************************************************************************** 
* makefile : ���ϻ��� - ȸ������ ������
* @version 2005 12 12 
* @author �̺���
* @param data Collection��ü.
* @param define_yn String��ü.
* @return  MakeFileResEtt 
********************************************************************************** */ 
	public MakeFileResEtt makefile(DbTaoResult data, String define_yn) {
		if ( authList(data) ) {
			return makeJsScript();
		} else {
			return resultEtt;
		}
	}

/** ******************************************************************************** 
* ���
* @version 2005 12 12 
* @author �̺���
* @param src String��ü.
* @return  String 
********************************************************************************** */ 
	public String us2kr_1(String src)    {
        String ret = "";
        if ( src != null)
        {
            try {
                if(src.length() > 0)
                    ret = new String(src.getBytes("KSC5601"),"8859_1");
                } catch(IOException e) {
                    
                    }
        }
        return ret;
    }


/** ******************************************************************************** 
* authList : �������� ��ȸ
* @version 2005 12 12 
* @author �̺���
* @param data Collection��ü.
* @return  boolean 
********************************************************************************** */ 
	public boolean authList(DbTaoResult data) {
		try {

			MakeFile mf = new MakeFile(m_user_nm, m_data_clss, m_file_clss, "��������");
			if ( "excel".equals(m_file_clss) ) {			// ����
				int col = 0, row = 0 ;
				mf.setFontFormat("ARIAL", WritableFont.DEFAULT_POINT_SIZE, jxl.format.Colour.BLACK,
						jxl.format.Colour.GRAY_25, jxl.format.Alignment.CENTRE, jxl.format.Border.ALL,					
						jxl.format.BorderLineStyle.THIN, true, false, false);	

				// Ÿ��Ʋ
				//mf.setData("�����Ϸù�ȣ", col++, row);
				mf.setCellWidth(col,25);
				mf.setData("�����", col++, row); 
				mf.setCellWidth(col,30);
				mf.setData("���ι�ȣ", col++, row);
				mf.setCellWidth(col,10);
				mf.setData("�����ڸ�", col++, row); 
				mf.setCellWidth(col,15);
				mf.setData("��ȭ��ȣ", col++, row); 
				mf.setCellWidth(col,20);
				mf.setData("ī���ȣ", col++, row); 
				mf.setCellWidth(col,10);
				mf.setData("��������", col++, row); 
				mf.setCellWidth(col,15);
				mf.setData("�ݾ�", col++, row); 
				mf.setCellWidth(col,10);
				mf.setData("�������", col++, row); 
				mf.setCellWidth(col,10);
				mf.setData("���ι�ȣ", col++, row); 
				mf.setCellWidth(col,10);
				mf.setData("�ҺαⰣ", col++, row); 
				mf.setCellWidth(col,10);
				mf.setData("ī������ڸ�", col++, row); 
				mf.setCellWidth(col,20);
				mf.setData("ī������� �ֹι�ȣ", col++, row); 
				mf.setCellWidth(col,20);
				mf.setData("������ �ֹι�ȣ", col++, row); 
				//mf.setCellWidth(col,10);
				//mf.setData("�������", col++, row); 
				//mf.setData("���ι������", col++, row); 
				//mf.setData("���ⱸ��", col++, row); 
				//mf.setData("�����", col++, row); 
				
				data.first();
				if (data.isNext() ) {
					mf.setFontFormat("ARIAL", WritableFont.DEFAULT_POINT_SIZE, jxl.format.Colour.BLACK,
						jxl.format.Colour.WHITE, jxl.format.Alignment.RIGHT, jxl.format.Border.ALL,
						jxl.format.BorderLineStyle.THIN, false, false, true);
					do {
						col = 0 ;
						row++;
						data.next();
						//mf.setData(data.getString("�����Ϸù�ȣ"), col++, row);
						mf.setData(data.getString("�����"), col++,  row); 
						mf.setData(data.getString("���ι�ȣ"), col++,  row);
						mf.setData(data.getString("�����ڸ�"), col++,  row); 
						mf.setData(data.getString("��ȭ��ȣ"), col++,  row); 
						mf.setData(data.getString("ī���ȣ"), col++,  row); 
						mf.setData(data.getString("��������"), col++,  row); 
						mf.setData(data.getString("�ݾ�"), col++,  row); 
						mf.setData(data.getString("�������"), col++,  row); 
						mf.setData(data.getString("���ι�ȣ"), col++,  row); 
						mf.setData(data.getString("�ҺαⰣ"), col++,  row); 
						mf.setData(data.getString("ī�强��"), col++,  row); 
						//mf.setData(data.getString("ī����"), col++,  row); 
						mf.setData(data.getString("�������ֹι�ȣ"), col++,  row); 
						mf.setData(data.getString("�����ֹι�ȣ"), col++,  row); 
						//mf.setData(data.getString("�������"), col++,  row); 
						//mf.setData(data.getString("���ι������"), col++,  row); 
						//mf.setData(data.getString("���ⱸ��"), col++,  row); 
						//mf.setData(data.getString("�����"), col++,  row); 
					} while(data.isNext());
				} else {
					row++;
					mf.setFontFormat("ARIAL", WritableFont.DEFAULT_POINT_SIZE, jxl.format.Colour.RED,jxl.format.Colour.WHITE, jxl.format.Alignment.CENTRE, jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN, false, false, true);
					mf.mergeCell(col, row, col + 11, row);
					mf.setData("�˻��� ����� �����ϴ�.", col, row);
				}
			}

			if ( mf.write() ) {					// ���� ����
				this.m_file_name = mf.getFileName();
				/*
				if ( mf.makeZip() ) {			// zip ���� ����
			BcLog.accessLog("=============>authList this.m_file_name10 :"+this.m_file_name );
					this.m_file_name = mf.getFileName();
			BcLog.accessLog("=============>authList this.m_file_name11 :"+this.m_file_name );

				} else {
					resultEtt.setErr_msg("Zip ���� ������ ������ �߻��߽��ϴ�.<br>��� �� �ٽ� �õ��Ͻñ� �ٶ��ϴ�.");
					return false;
				}
				*/
			} else {
				resultEtt.setErr_msg("���� ������ ������ �߻��߽��ϴ�.<br>��� �� �ٽ� �õ��Ͻñ� �ٶ��ϴ�.");
				return false;
			}
			return true;
		} catch(Exception e){
			
			resultEtt.setErr_msg("���� ������ ������ �߻��߽��ϴ�.<br>��� �� �ٽ� �õ��Ͻñ� �ٶ��ϴ�.");
			return false;
		}
	}

}

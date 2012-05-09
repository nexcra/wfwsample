/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : Thumbnail
*   작성자     : (주)미디어포스 임은혜
*   내용        :  Thumbnail 이미지 만드는 클래스 (가맹점용)
*   적용범위  : Golf
*   작성일자  : 2009-05-13
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.common.file;

import java.awt.Color;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import javax.swing.ImageIcon;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/******************************************************************************
* m4 
* @author 권영만
* @version 2009.4.8
******************************************************************************/
public class Thumbnail {
	/*
	private String orig; // 원본이미지 (패스와 파일명)
	private String thumb; // 작게만들 이미지 (패스와 파일명)
	*/

	/**
	 * @info 작은 이미지를 만든다.
	 * @param String orig
	 * @param String thumb
	 * @param int maxDim
	 * @param int maxDimH
	 * @return void
	 */
	public void createThumbnail(String orig, String thumb, int maxDim, int maxDimH)
	{
		try {
			// Get the image from a file.
			Image inImage = new ImageIcon(orig).getImage();

			// Determine the scale.
			double scale = (double)maxDim/(double)inImage.getHeight(null);

			if (inImage.getWidth(null) > inImage.getHeight(null)) {
				scale = (double)maxDim/(double)inImage.getWidth(null);
			}

			// Determine size of new image.
			//One of them
			// should equal maxDim.
			int scaledW = (int)(scale*inImage.getWidth(null));
			int scaledH = maxDimH;

			if (maxDim>inImage.getWidth(null)){
				scaledW = inImage.getWidth(null);
			}
			if (maxDimH>inImage.getHeight(null)){
				scaledH = inImage.getHeight(null);
			}
			// Create an image buffer in
			//which to paint on.
			BufferedImage outImage = new BufferedImage(scaledW, scaledH,BufferedImage.TYPE_INT_RGB);

			// Set the scale.
			AffineTransform tx = new AffineTransform();

			// If the image is smaller than
			//the desired image size,
			// don't bother scaling.
			if (scale < 1.0d) {
				tx.scale(scale, scale);
			}

			// Paint image.
			Graphics2D g2d = outImage.createGraphics();
			g2d.setBackground(new Color(255,255,255));
			g2d.clearRect(0, 0, scaledW, scaledH);
			g2d.drawImage(inImage, tx, null);
			g2d.dispose();

			// JPEG-encode the image
			//and write to file.
			OutputStream os = new FileOutputStream(thumb);
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
			encoder.encode(outImage);
			os.close();
		} catch (IOException e) {

			
		}
	}
}
package com.huiminpay.common.cache.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.huiminpay.common.cache.domain.BusinessException;
import com.huiminpay.common.cache.domain.CommonErrorCode;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * <P>
 * 二维码工具类
 * </p>
 */
public class QRCodeUtil {
	/**
	 * 生成二维码
	 * @param content
	 * @param width
	 * @param height
	 * @return
	 */
	public static String createQRCode(String content, int width, int height) throws IOException {
		String resultImage = "";
		//除了尺寸，传入内容不能为空
		if (!StringUtils.isEmpty(content)) {
			ServletOutputStream stream = null;
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			//二维码参数
			@SuppressWarnings("rawtypes")
			HashMap<EncodeHintType, Comparable> hints = new HashMap<>();
			//指定字符编码为“utf-8”
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			//L M Q H四个纠错等级从低到高，指定二维码的纠错等级为M
			//纠错级别越高，可以修正的错误就越多，需要的纠错码的数量也变多，相应的二维吗可储存的数据就会减少
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
			//设置图片的边距
			hints.put(EncodeHintType.MARGIN, 1);

			try {
				//zxing生成二维码核心类
				QRCodeWriter writer = new QRCodeWriter();
				//把输入文本按照指定规则转成二维吗
				BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
				//生成二维码图片流
				BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
				//输出流
				ImageIO.write(bufferedImage, "png", os);
				/**
				 * 原生转码前面没有 data:image/png;base64 这些字段，返回给前端是无法被解析，所以加上前缀
				 */
				resultImage = new String("data:image/png;base64," + EncryptUtil.encodeBase64(os.toByteArray()));
				return resultImage;
			} catch (Exception e) {
				e.printStackTrace();
				throw new BusinessException(CommonErrorCode.E_200007);
			} finally {
				if (stream != null) {
					stream.flush();
					stream.close();
				}
			}
		}
		return null;
	}

	//测试
	public static void main(String[] args) throws IOException {
		String qrCode = QRCodeUtil.createQRCode("http://localhost:56050/transaction/alipayTest", 200, 200);
		System.out.println(qrCode);
	}

}

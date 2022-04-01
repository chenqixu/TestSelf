package com.cqx.common.utils.image;

import com.luciad.imageio.webp.WebPReadParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * webp图像处理
 *
 * @author chenqixu
 */
public class WebpDeal implements IDealImage {
    private static final Logger logger = LoggerFactory.getLogger(WebpDeal.class);
    private BufferedImage readImg;

    @Override
    public void read(String srcImg) throws IOException {
        File inputImg = new File(srcImg);
        if (inputImg.exists() && inputImg.isFile()) {
            // Obtain a WebP ImageReader instance
            ImageReader reader = ImageIO.getImageReadersByMIMEType("image/webp").next();

            // Configure decoding parameters
            WebPReadParam readParam = new WebPReadParam();
            readParam.setBypassFiltering(true);

            // Configure the input on the ImageReader
            reader.setInput(new FileImageInputStream(inputImg));

            // Decode the image
            logger.info("read webp img : {}", inputImg);
            readImg = reader.read(0, readParam);
        } else {
            throw new NullPointerException(String.format("%s 图片不存在！", srcImg));
        }
    }

    @Override
    public void save(String srcImg, EnumImage enumImage) throws IOException {
        File inputImg = new File(srcImg);
        String path = inputImg.getParent();
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        String fileName = inputImg.getName();
        // 去掉后缀
        fileName = fileName.replace(EnumImage.WEBP.getWithPointName(), "");
        path += fileName + enumImage.getWithPointName();
        File outputImg = new File(path);
        if (outputImg.exists()) {
            logger.info("{} 已经存在，不需要处理！", path);
        } else {
            // 读取图像
            read(srcImg);
            if (readImg != null) {
                logger.info("save to : {}", path);
                ImageIO.write(readImg, enumImage.getName(), outputImg);
            }
        }
    }
}

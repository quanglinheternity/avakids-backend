package com.example.avakids_backend.util.file.sevrice;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

public class ImageUtil {

    // Resize giữ tỉ lệ
    public static BufferedImage resize(BufferedImage original, int maxWidth) {
        if (original.getWidth() <= maxWidth) {
            return original;
        }

        int newHeight = (int) ((double) maxWidth / original.getWidth() * original.getHeight());

        BufferedImage resized = new BufferedImage(
                maxWidth,
                newHeight,
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(original, 0, 0, maxWidth, newHeight, null);
        g.dispose();

        return resized;
    }

    // Nén JPEG với quality (0.0 – 1.0)
    public static byte[] compressJpeg(BufferedImage image, float quality) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
            ImageWriter writer = writers.next();

            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);

            ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
            writer.setOutput(ios);
            writer.write(null, new IIOImage(image, null, null), param);

            ios.close();
            writer.dispose();

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("JPEG compression failed", e);
        }
    }
}
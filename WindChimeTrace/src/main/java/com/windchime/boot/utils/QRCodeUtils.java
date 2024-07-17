package com.windchime.boot.utils;

import cn.hutool.extra.qrcode.BufferedImageLuminanceSource;
import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;
import java.util.Hashtable;
import java.util.Random;

/**
 *@description: Use Google open source project zxing Generating and parsing QR codes
 *2018 year 4 month 11 Japan Afternoon 2:29:41
 */
public class QRCodeUtils {
    private static final String CHARSET = "utf-8";
    private static final String FORMAT_NAME = "JPG";
    // QR code size
    private static final int QRCODE_SIZE = 300;
    // LOGO Width
    private static final int WIDTH = 60;
    // LOGO Height
    private static final int HEIGHT = 60;
/********************* Two dimensional code generation method and overload method *********************/
    /**
     * Generate qr code ( Embedded LOGO)
     * @param content
     * Content
     * @param imgPath
     * LOGO Address
     * @param destPath
     * Storage directory
     * @param needCompress
     * Is it compressed? LOGO
     * @throws Exception
     */
    public static String encode(String content, String imgPath, String destPath,
                                 boolean needCompress) throws Exception {
        BufferedImage image = createImage(content, imgPath,
                needCompress);
        mkdirs(destPath);
        String file = new Random().nextInt(99999999)+".jpg";
        ImageIO.write(image, FORMAT_NAME, new File(destPath+"/"+file));
        return file;
    }

    public static String encodeParamFileName(String content, String imgPath, String destPath,
                                boolean needCompress,String fileName) throws Exception {
        BufferedImage image = createImage(content, imgPath,
                needCompress);
        mkdirs(destPath);
        String file = fileName+".jpg";
        ImageIO.write(image, FORMAT_NAME, new File(destPath+"/"+file));
        return file;
    }

    /**
     * Generate qr code ( Embedded LOGO)
     * @param content
     * Content
     * @param imgPath
     * LOGO Address
     * @param destPath
     * Storage address
     * @throws Exception
     */
    public static String encode(String content, String imgPath, String destPath)
            throws Exception {
        String fileName = encode(content, imgPath, destPath, true);
        return fileName;
    }
    /**
     * Generate qr code ( It doesn't contain LOGO)
     * @param content
     * Content
     * @param destPath
     * Storage address
     * @param needCompress
     * Is it compressed?
     * @throws Exception
     */
    public static void encode(String content, String destPath,
                              boolean needCompress) throws Exception {
        encode(content, null, destPath, needCompress);
    }

    public static void encode(String content, String destPath,
                              boolean needCompress,String fileName) throws Exception {
        encodeParamFileName(content, null, destPath, needCompress,fileName);
    }
    /**
     * Generate qr code ( It doesn't contain LOGO, And do not compress the picture )
     * @param content
     * Content
     * @param destPath
     * Storage address
     * @throws Exception
     */
    public static void encode(String content, String destPath) throws Exception {
        encode(content, null, destPath, false);
    }
    /**
     * Generate qr code ( Embedded LOGO)
     * @param content
     * Content
     * @param imgPath
     * LOGO Address
     * @param output
     * Output stream
     * @param needCompress
     * Is it compressed? LOGO
     * @throws Exception
     */
    public static void encode(String content, String imgPath,
                              OutputStream output, boolean needCompress) throws Exception {
        BufferedImage image = createImage(content, imgPath,
                needCompress);
        ImageIO.write(image, FORMAT_NAME, output);
    }
    /**
     * Generate qr code
     * @param content
     * Content
     * @param output
     * Output stream
     * @throws Exception
     */
    public static void encode(String content, OutputStream output)
            throws Exception {
        encode(content, null, output, false);
    }
/********************* Two dimensional code analysis method and overload *********************/
    /**
     * Analysis of QR code
     * @param file
     * QR code picture
     * @return
     * @throws Exception
     */
    public static String decode(File file) throws Exception {
        BufferedImage image;
        image = ImageIO.read(file);
        if (image == null) {
            return null;
        }
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result;
        Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
        hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
        result = new MultiFormatReader().decode(bitmap, hints);
        String resultStr = result.getText();
        return resultStr;
    }


    /**
     * Analysis of QR code
     * @param path
     * QR code image address
     * @return
     * @throws Exception
     */
    public static String decode(String path) throws Exception {
        return decode(new File(path));
    }
    /************************ In the use of ecode Method is a static method called when creating QR code **********************/
    private static BufferedImage createImage(String content, String imgPath,
                                             boolean needCompress) throws Exception {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
// Set up QR Error correction level of QR code （H At the highest level ） Specific level information
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
// The encoding used for the content
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
// Set the margin of the picture
        hints.put(EncodeHintType.MARGIN, 1);
// Generate matrix
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
                BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000
                        : 0xFFFFFFFF);
            }
        }
        if (imgPath == null || "".equals(imgPath)) {
            return image;
        }
// Insert a picture LOGO
        insertImage(image, imgPath, needCompress);
        return image;
    }

    public static void main(String[] args) {
        try {
            encode("prescriptionId=11785892?drugstoreId=DS10034","D:/qrcodeR.png",true,"11785892");
            File file = new File("D:/qrcodeR.png/11785892.jpg");
            InputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] result = null;
            byte[] buf = new byte[1024];
            //用来定义一个准备接收图片总长度的局部变量
            int len;
            //将流的内容读取到buf内存中
            while ((len = in.read(buf)) > 0) {
                //将buf内存中的内容从0开始到总长度输出出去
                out.write(buf, 0, len);
            }
            //将out中的流内容拷贝到一开始定义的字节数组中
            result = out.toByteArray();
            //通过util包中的Base64类对字节数组进行base64编码
            String base64 = Base64.getEncoder().encodeToString(result);
            System.out.println(base64);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Insert LOG Picture into the generated QR code
     * @param source
     * QR code picture
     * @param imgPath
     * LOGO Picture address
     * @param needCompress
     * Is it compressed?
     * @throws Exception
     */
    private static void insertImage(BufferedImage source, String imgPath,
                                    boolean needCompress) throws Exception {
        File file = new File(imgPath);
        if (!file.exists()) {
            System.err.println(""+imgPath+" The file does not exist ！");
            return;
        }
        Image src = ImageIO.read(new File(imgPath));
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        if (needCompress) { // Compress LOGO
            if (width > WIDTH) {
                width = WIDTH;
            }
            if (height > HEIGHT) {
                height = HEIGHT;
            }
            Image image = src.getScaledInstance(width, height,
                    Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // Draw a reduced image
            g.dispose();
            src = image;
        }
// Insert LOGO
        Graphics2D graph = source.createGraphics();
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }
    /**
     * @param destPath Storage directory
     */
    public static void mkdirs(String destPath) {
        File file = new File(destPath);
// When the folder does not exist ,mkdirs Automatically creates a multi-level directory , The difference in mkdir．(mkdir An exception is thrown if the parent directory does not exist )
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
    }
}
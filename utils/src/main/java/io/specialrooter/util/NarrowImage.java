//package io.specialrooter.util;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.FileOutputStream;
//
//public class NarrowImage {
//    /**
//     * @param im
//     *            原始图像
//     * @param resizeTimes
//     *            倍数,比如0.5就是缩小一半,0.98等等double类型
//     * @return 返回处理后的图像
//     */
//    public BufferedImage zoomImage(String src) {
//
//        BufferedImage result = null;
//
//        try {
//            File srcfile = new File(src);
//            if (!srcfile.exists()) {
//                System.out.println("文件不存在");
//
//            }
//            BufferedImage im = ImageIO.read(srcfile);
//
//            /* 原始图像的宽度和高度 */
//            int width = im.getWidth();
//            int height = im.getHeight();
//
//            // 压缩计算
//            float resizeTimes = 0.3f; /* 这个参数是要转化成的倍数,如果是1就是转化成1倍 */
//
//            /* 调整后的图片的宽度和高度 */
//            int toWidth = (int) (width * resizeTimes);
//            int toHeight = (int) (height * resizeTimes);
//
//            /* 新生成结果图片 */
//            result = new BufferedImage(toWidth, toHeight, BufferedImage.TYPE_INT_RGB);
//
//            result.getGraphics().drawImage(im.getScaledInstance(toWidth, toHeight, java.awt.Image.SCALE_SMOOTH), 0, 0,
//                    null);
//
//        } catch (Exception e) {
//            System.out.println("创建缩略图发生异常" + e.getMessage());
//        }
//
//        return result;
//
//    }
//
//    public boolean writeHighQuality(BufferedImage im, String fileFullPath) {
//        try {
//            /* 输出到文件流 */
//            FileOutputStream newimage = new FileOutputStream(fileFullPath);
//            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(newimage);
//            JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(im);
//            /* 压缩质量 */
//            jep.setQuality(0.9f, true);
//            encoder.encode(im, jep);
//            /* 近JPEG编码 */
//            newimage.close();
//            return true;
//        } catch (Exception e) {
//            return false;
//        } finally {
//            // 压缩完毕后，删除原文件
//            File file = new File(fileFullPath);
//            // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
//            if (file.exists() && file.isFile()) {
//                if (file.delete()) {
//                    System.out.println("删除文件" + fileFullPath + "成功！");
//                } else {
//                    System.out.println("删除文件" + fileFullPath + "失败！");
//                }
//            } else {
//                System.out.println("删除文件失败：" + fileFullPath + "不存在！");
//            }
//        }
//    }
//
//    /**
//     * 测试图片压缩
//     *
//     * @param args
//     */
//    public static void main(String[] args) {
//
//        String inputFoler = "C:\\Users\\PC\\Desktop\\bcfb1f83-ee77-4e82-bf2f-48905b1089b1.jpg";
//        /* 这儿填写你存放要缩小图片的文件夹全地址 */
//        String outputFolder = "C:\\Users\\PC\\Desktop\\T-bcfb1f83-ee77-4e82-bf2f-48905b1089b1.jpg";
//        /* 这儿填写你转化后的图片存放的文件夹 */
//
//        NarrowImage narrowImage = new NarrowImage();
//        narrowImage.writeHighQuality(narrowImage.zoomImage(inputFoler), outputFolder);
//    }
//}

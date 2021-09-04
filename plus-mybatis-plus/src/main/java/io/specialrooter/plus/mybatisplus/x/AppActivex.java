//package io.specialrooter.plus.mybatisplus.x;
//
//import java.io.*;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Path;
//
//public abstract class AppActivex {
//
////    public void startup( Date date){
////        super.getAppUUId();
////    }
////
////    public void shutdown(String id,Date date){
////        super.getAppUUId();
////    }
//
//    public static void readFile() {
//        String sourceFileName = "/Users/ai/Library/Caches/IntelliJIdea2019.3/LocalHistory/changes.storageRecordIndex的副本";
//        InputStream in = null;
//        try {
//            in = new FileInputStream(sourceFileName);
//
//// 读取字符串数据长度字节
//            byte[] txtLenByte = new byte[2];
//            in.read(txtLenByte);
//            int txtlen = byte2ToUnsignedShort(txtLenByte, 0);
//
//// 读取字符串字节
//            byte[] txtByte = new byte[txtlen];
//            in.read(txtByte);
////字符串为UTF-8编码
//            String txt = new String(txtByte, "UTF-8");
//// 输出字符串
//            System.out.println(txt);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (in != null) {
//                try {
//                    in.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//    }
//
//    /**
//     * @param bytes
//     * @param off
//     * @return
//     * @Description byte数组转换为无符号short整数
//     * @author wjggwm
//     * @data 2017年2月7日 上午11:05:58
//     */
//    public static int byte2ToUnsignedShort(byte[] bytes, int off) {
//// 注意高位在后面，即大小端问题
//        int low = bytes[off];
//        int high = bytes[off + 1];
//        return (high << 8 & 0xFF00) | (low & 0xFF);
//    }
//    private static final OutputStreamWriter log = null;
//
//        public static void main(String[] args) {
////        readFile();
//            Path file = new Path() {
//            }
//            log = new OutputStreamWriter(Files.newOutputStream(file.getParent().resolve(file.getFileName() + ".log")), StandardCharsets.UTF_8);
//
//
//        }
//}

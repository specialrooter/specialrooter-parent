package io.specialrooter.util;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.IOException;
import java.security.SecureRandom;

/**
 * 加解密工具类
 * MD5：Spring 官方MD5加密解密
 * DES：二次封装
 * 3DES：二次封装
 */
public class DigestUtils extends  org.springframework.util.DigestUtils{

    public static void main(String[] args) throws Exception {
        String xxx = DigestUtils.des3rdDecrypt("xxx", "11111111");
        System.out.println(xxx);
    }
    /**
     * DES加密
     * @param datasource，加密数据
     * @param password，密钥
     * @return
     */
    public static  byte[] encrypt(byte[] datasource, String password) {
        try{
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(password.getBytes());
            //创建一个密匙工厂，然后用它把DESKeySpec转换成
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            //Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DES");
            //用密匙初始化Cipher对象,ENCRYPT_MODE用于将 Cipher 初始化为加密模式的常量
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
            //现在，获取数据并加密
            //正式执行加密操作
            return cipher.doFinal(datasource); //按单部分操作加密或解密数据，或者结束一个多部分操作
        }catch(Throwable e){
            e.printStackTrace();
        }
        return null;
    }


    /**
     * DES解密
     * @param src 解密数据
     * @param password  密钥
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] src, String password) throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom random = new SecureRandom();
        // 创建一个DESKeySpec对象
        DESKeySpec desKey = new DESKeySpec(password.getBytes());
        // 创建一个密匙工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");//返回实现指定转换的 Cipher 对象
        // 将DESKeySpec对象转换成SecretKey对象
        SecretKey securekey = keyFactory.generateSecret(desKey);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES");
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, random);
        // 真正开始解密操作
        return cipher.doFinal(src);
    }

    /**
     * 3DES加密
     * @param srcData，加密字符串
     * @param dESKey ,加密密钥
     * @return
     * @throws Exception
     */
    public static String des3rdEncrypt(String srcData, String dESKey)
            throws Exception
    {
        SecureRandom sr = new SecureRandom();

        byte[] rawKeyData = DES.hexstr2ByteArr(dESKey);

        DESKeySpec dks = new DESKeySpec(rawKeyData);

        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");

        SecretKey key = keyFactory.generateSecret(dks);

        Cipher cipher = Cipher.getInstance("DESede");

        cipher.init(1, key, sr);

        byte[] data = srcData.getBytes("UTF8");

        byte[] encryptedData = cipher.doFinal(data);

        String enOut = DES.byteArr2HexStr(encryptedData);

        return enOut;
    }

    /**
     * 3DES解密
     * @param srcData
     * @param dESKey
     * @return
     * @throws Exception
     */
    public static String des3rdDecrypt(String srcData, String dESKey)
            throws Exception
    {
        SecureRandom sr = new SecureRandom();

        byte[] rawKeyData = DES.hexstr2ByteArr(dESKey);

        DESKeySpec dks = new DESKeySpec(rawKeyData);

        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");

        SecretKey key = keyFactory.generateSecret(dks);

        Cipher cipher = Cipher.getInstance("DESede");

        cipher.init(2, key, sr);

        byte[] data = DES.hexstr2ByteArr(srcData);

        byte[] decryptedData = cipher.doFinal(data);

        String out = new String(decryptedData, "UTF8");

        return out;
    }

    static class DES {
        /**
         * HEX转byte
         * @param strIn
         * @return
         */
        public static byte[] hexstr2ByteArr(String strIn)
        {
            byte[] arrB = strIn.getBytes();

            int iLen = arrB.length;

            byte[] arrOut = new byte[iLen / 2];

            for (int i = 0; i < iLen; i += 2)
            {
                String strTmp = new String(arrB, i, 2);

                arrOut[(i / 2)] = (byte)Integer.parseInt(strTmp, 16);
            }

            return arrOut;
        }

        /**
         * byte转HEX
         * @param arrB
         * @return
         * @throws IOException
         */
        public static String byteArr2HexStr(byte[] arrB)
                throws IOException
        {
            int iLen = arrB.length;

            StringBuffer sb = new StringBuffer(iLen * 2);

            for (int i = 0; i < iLen; ++i)
            {
                int intTmp = arrB[i];

                while (intTmp < 0)
                {
                    intTmp += 256;
                }

                if (intTmp < 16)
                {
                    sb.append("0");
                }

                sb.append(Integer.toString(intTmp, 16));
            }

            return sb.toString();
        }
    }
}

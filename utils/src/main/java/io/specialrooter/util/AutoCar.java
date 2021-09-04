package io.specialrooter.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AutoCar {
    String[] a = {
            "京A","京C","京E","京F","京H","京G","京B",
            "津A","津B", "津C", "津E",
            "沪A", "沪B", "沪D", "沪C",
            "渝A", "渝B", "渝C", "渝G", "渝H",
            "冀A", "冀B", "冀C", "冀D", "冀E", "冀F", "冀G", "冀H", "冀J", "冀R", "冀T",
            "豫A", "豫B", "豫C", "豫D", "豫E", "豫F","豫G","豫H","豫J","豫K","豫L","豫M","豫N","豫P","豫Q","豫R","豫S","豫U"
            ,"云A", "云C", "云D", "云E", "云F", "云G","云H","云J","云K","云L","云M","云N","云P","云Q","云R ","云S",
            "辽A", "辽B", "辽C", "辽D", "辽E", "辽F","辽G","辽H","辽J","辽K","辽L","辽M","辽N","辽P","辽V",
            "黑A", "黑B", "黑C", "黑D", "黑E", "黑F","黑G","黑H","黑J","黑K","黑L","黑M","黑N","黑P","黑R",
            "湘A", "湘B", "湘C", "湘D", "湘E", "湘F","湘G","湘H","湘J","湘K","湘L","湘M","湘N","湘U","湘S",
            "皖A", "皖B", "皖C", "皖D", "皖E", "皖F","皖G","皖H","皖J","皖K","皖L","皖M","皖N","皖P","皖Q","皖R","皖S",
            "鲁A", "鲁B", "鲁C", "鲁D", "鲁E", "鲁F","鲁G","鲁H","鲁J","鲁K","鲁L","鲁M","鲁N","鲁P","鲁Q","鲁R","鲁S","鲁U","鲁V","鲁Y",
            "新A", "新B", "新C", "新D", "新E", "新F","新G","新H","新J","新K","新L","新M","新N","新P","新Q","新R",
            "苏A", "苏B", "苏C", "苏D", "苏E", "苏F","苏G","苏H","苏J","苏K","苏L","苏M","苏N",
            "浙A", "浙B", "浙C", "浙D", "浙E", "浙F","浙G","浙H","浙J","浙K ","浙L",
            "赣A","赣B","赣C","赣D","赣E","赣F","赣G","赣H","赣J","赣K","赣L","赣M",
            "鄂A","鄂B","鄂C","鄂D","鄂E","鄂F","鄂G","鄂H","鄂J","鄂K" ,"鄂L","鄂M","鄂N","鄂P","鄂Q","鄂R","鄂S",
            "桂A","桂B","桂C","桂D","桂E","桂F","桂G","桂H","桂J","桂K","桂L","桂M","桂N","桂P" ,"桂R",
            "甘A","甘B","甘C","甘D","甘E","甘F","甘G","甘H","甘J","甘K","甘L","甘M" ,"甘N","甘P",
            "晋A" ,"晋B","晋C","晋D","晋E","晋F","晋H","晋J","晋K","晋L","晋M",
            "蒙A","蒙B","蒙C","蒙D","蒙E","蒙F","蒙G","蒙H","蒙J","蒙K","蒙L","蒙M",
            "陕A","陕B","陕C","陕D","陕E","陕F","陕G","陕H","陕J","陕K","陕U","陕V",
            "吉A","吉B","吉C","吉D","吉E","吉F","吉G","吉H","吉J",
            "闽A","闽B","闽C","闽D","闽E","闽F" ,"闽G","闽H","闽J","闽K",
            "贵A","贵B" ,"贵C","贵D","贵E" ,"贵F","贵G","贵H","贵J",
            "粤A","粤B","粤C","粤D","粤E","粤F","粤G","粤H","粤J","粤K","粤L","粤M","粤N","粤P","粤Q","粤R","粤S","粤T","粤U","粤V","粤W","粤X","粤Y","粤Z",
            "青A","青B","青C","青D","青E","青F","青G","青H",
            "藏A","藏B","藏C","藏D","藏E","藏F","藏G","藏H" ,"藏J",
            "川A","川B","川C","川D","川E","川F","川H","川J","川K","川L","川M","川Q","川R","川S","川T","川U","川V","川W","川X","川Y","川Z",
            "宁A","宁B","宁C","宁D",
            "琼A","琼B","琼C","琼D","琼E"
    };


    String[] c = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

    /**
     * 随机生
     * @Description:
     * @return

     * @date 2018年9月4日 下午5:27:45
     */
    public String car(){
        int count = 0;
        String carNo = "";
        while (count < 5) {
            Random random = new Random();
            String str2 = c[random.nextInt(26)];
            int num = random.nextInt(10);
            int j = 0;
            if (count > 2) {
                for (int i = 0; i < carNo.length(); i++) {
                    char c = carNo.charAt(i);
                    if ((c >= 'A' && c <= 'Z')) {
                        j++;
                    }
                }
            }
            if (j < 2) {
                // 字母与数字的概率相同
                if (random.nextBoolean()) {
                    carNo += num;
                } else {
                    carNo += str2;
                }
                count++;
            } else {
                carNo += num;
                count++;
            }
        }

        Random random1 = new Random();
        int num1 = random1.nextInt(31);
        String plate1 = a[num1]  + carNo;
        return plate1;
    }

    /** 自定义进制(0,1没有加入,容易与o,l混淆) */
    private static final char[] r=new char[]{'Q', 'W', 'E', '8', 'A', 'S', '2', 'D', 'Z', 'X', '9', 'C', '7', 'P', '5', 'I', 'K', '3', 'M', 'J', 'U', 'F', 'R', '4', 'V', 'Y', 'l', 'T', 'N', '6', 'B', 'G', 'H'};

    /** (不能与自定义进制有重复) */
    private static final char b='O';

    /** 进制长度 */
    private static final int binLen=r.length;

    /** 序列最小长度 */
    private static final int s=6;

    /**
     * 根据ID生成六位随机码
     * @param id ID
     * @return 随机码
     */
    public static String toSerialCode(long id) {
        char[] buf=new char[32];
        int charPos=32;

        while((id / binLen) > 0) {
            int ind=(int)(id % binLen);
            buf[--charPos]=r[ind];
            id /= binLen;
        }
        buf[--charPos]=r[(int)(id % binLen)];
        String str=new String(buf, charPos, (32 - charPos));
        // 不够长度的自动随机补全
        if(str.length() < s) {
            StringBuilder sb=new StringBuilder();
            sb.append(b);
            Random rnd=new Random();
            for(int i=1; i < s - str.length(); i++) {
                sb.append(r[rnd.nextInt(binLen)]);
            }
            str+=sb.toString();
        }
        return str;
    }

    public static long codeToId(String code) {
        char chs[]=code.toCharArray();
        long res=0L;
        for(int i=0; i < chs.length; i++) {
            int ind=0;
            for(int j=0; j < binLen; j++) {
                if(chs[i] == r[j]) {
                    ind=j;
                    break;
                }
            }
            if(chs[i] == b) {
                break;
            }
            if(i > 0) {
                res=res * binLen + ind;
            } else {
                res=ind;
            }
        }
        return res;
    }

    public static void main2() {
        Map<String, Object> map = new HashMap<String, Object>();
        int a=0;
        for (int i = 0; i < 10000000; i++) {
            String s = toSerialCode(i);
            System.out.println(s);
            if (map.get(s)!=null) {
                a++;
            }else {
                map.put(s, "2");
            }
        }
        System.out.println(a);
    }
    public static void main(String[] args) {
        AutoCar autoCar = new AutoCar();

        System.out.println(autoCar.car());

//        main2();
    }

}

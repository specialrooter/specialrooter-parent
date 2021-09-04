package io.specialrooter.web.exec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 系统命令执行器
 * @author suozq
 * @Link https://blog.csdn.net/suo082407128/article/details/107386044
 *    推荐使用数组方式，使用字符串最终还是需要转为字符串数组；
 *     Runtime对象是单例模式，无论调用多少次Runtime.getRuntime(),返回为同一对象;
 *     rt.exec()从源码看可以并发调用，并发使用时，资源耗用可能会很大；
 *     如果命令执行过慢，会导致不能读取到结果，请添加process.waitFor()方法，该方法会使当前线程等待直到process子进程结束，0代表正常结束
 */
public class LocalCmdExecutor {
    private static Runtime rt=Runtime.getRuntime();
    private static String charsetName = "utf-8";
    static {
        String os = System.getProperty("os.name");
        if(os.toLowerCase().contains("windows")) {
            charsetName="gb2312";
        }
    }

    public static String exec(String...cmd) throws IOException {
        return exec(0,cmd);
    }
    /**
     * 运行系统命令
     * @param cmd 例子：exec("javac","-version")或exec(new String[]{"javac","-version"})
     * @return
     * @throws IOException
     */
    public static String exec(int i,String...cmd) throws IOException {
        return handleProcess(rt.exec(cmd),i);
    }

    public static String exec(String cmd) throws IOException {
        return exec(cmd,0);
    }
    /**
     * 运行系统命令
     * @param cmd 例子：exec("javac -version")
     * @return
     * @throws IOException
     */
    public static String exec(String cmd,int i) throws IOException {
        return handleProcess(rt.exec(cmd),i);
    }

    private static String handleProcess(Process process,int i) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(),charsetName));
        if(i>0){
            for (int j = 0; j < i; j++) {
                reader.readLine();
            }
        }
        for(String s=reader.readLine();s!=null;s=reader.readLine()) {
            sb.append(s).append("\n");
        }
        reader.close();

        String s = sb.toString();
        if(s.length()>2){
            return s.substring(0,s.length()-1);
        }

        return sb.toString();
    }



//    public static void main(String[] args) throws IOException {
//        String s = getGitInstallPath() + File.separator + "bin" + File.separator + "git.exe";
//        System.out.println(exec(s+" config user.name"));
//
//    }
}

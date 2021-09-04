package io.specialrooter.util;

import java.io.File;
import java.io.IOException;

public class MavenUtils {
    public static void cleanLastUpdatedFiles(File dir,boolean bool) throws IOException {
        if (!dir.exists())
            throw new IllegalArgumentException("目录：" + dir + "不存在.");
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException(dir + " 不是目录。");
        }
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isDirectory())
                    //递归
                    cleanLastUpdatedFiles(file,bool);
                else{ // 删除以 lastUpdated 结尾的文件
                    String fileName = file.getName();
                    boolean isLastupdated = fileName.toLowerCase().endsWith("lastupdated");
                    if (isLastupdated){
                         boolean is_delete = file.delete();
                        System.out.println("删除的文件名 => " + file.getName() + "  || 是否删除成功？ ==> "  +is_delete);
                    }
                    if(bool){
                        // 特殊情况，删除 _remote.repositories
                        boolean isRemoteRepositories = fileName.toLowerCase().equals("_remote.repositories");
                        if (isRemoteRepositories){
                            boolean is_delete = file.delete();
                            System.out.println("删除的文件名 => " + file.getName() + "  || 是否删除成功？ ==> "  +is_delete);
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {

        MavenUtils.cleanLastUpdatedFiles(new File("/Users/ai/Work/App/mvn-repository/io/specialrooter"),false);
    }
}

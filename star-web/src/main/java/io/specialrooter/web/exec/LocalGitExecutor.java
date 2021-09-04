package io.specialrooter.web.exec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.specialrooter.web.model.GitLabGroup;
import io.specialrooter.web.model.GitLabProject;
import io.specialrooter.web.model.GitLabUser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class LocalGitExecutor implements Serializable {
    private static OkHttpClient client = new OkHttpClient();

    private static String git = "git";

    private static String gitName = "";

    private static String gitEmail = "";

    private static String gitDescribe = "";


    static {
        String os = System.getProperty("os.name");
        getGit();
        if (os.toLowerCase().contains("windows")) {
            try {
                if (StringUtils.isBlank(gitName)) {
                    git = getGitInstallPath() + File.separator + "bin" + File.separator + "git.exe";
                }
            } catch (IOException e) {
            }
        }
    }

    public static void getGit() {
        String projectPath = getProjectPathURL();
        try {
            Git git = openRpo(projectPath, "");
            if (git != null) {


                String url = git.getRepository().getConfig().getString("remote", "origin", "url");

                GitLabProject gitProject = getGitProject(url);
                if (gitProject != null) {
                    gitDescribe = gitProject.getDescription();
                }

                List<GitLabUser> projectUsers = getProjectUsers(gitProject.getId());
                Map<Integer, String> users = projectUsers.stream().collect(Collectors.toMap(m -> m.getId(), m -> m.getName()));

                String[] split = url.split(":");
                String server = split[0].split("@")[1];

                // 验证用户目录是否存在.ssh文件夹
                String property = System.getProperty("user.home");
//                System.out.println(property);
                File folder = new File(property + File.separator + ".ssh");
                if (folder.exists() && folder.isDirectory()) {
                    // 验证config文件是否存在
                    File configFile = new File(folder.getPath() + File.separator + "config");
                    if (configFile.exists()) {
                        String sshPub = null;
                        // 读取文件
                        try {
                            List<String> strings = FileUtils.readLines(configFile, "UTF-8");
                            for (int i = 0; i < strings.size(); i++) {
                                String s = strings.get(i);
                                if (s.indexOf(server) != -1) {
                                    for (int j = i; j < strings.size(); j++) {
                                        String sj = strings.get(j);
                                        if (StringUtils.isNotBlank(sj) && sj.startsWith("IdentityFile")) {
                                            sshPub = sj.substring(sj.lastIndexOf("/") + 1);
                                            break;
                                        }
                                        if (StringUtils.isBlank(sj)) {
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }

                            if (sshPub != null) {
                                File pubFile = new File(folder.getPath() + File.separator + sshPub + ".pub");
                                String s = FileUtils.readFileToString(pubFile, "UTF-8");
                                String[] s1 = s.split(" ");
                                if (s1.length == 3) {
                                    // 双向验证：连接服务器验证邮箱是否为本机器上传的SSH Key
                                    GitLabUser gitLabUser = getGitLabUser(s1[2]);
                                    if(gitLabUser!=null){
                                        List<String> gitLabUserKeys = getGitLabUserKeys(gitLabUser.getId());
                                        if (gitLabUserKeys.contains(s)) {
                                            gitEmail = s1[2];
                                            gitName = gitLabUser.getName();
                                        }
                                    }
                                }
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // 验证其他肯能
                        File[] files = folder.listFiles();
                        for (File file : files) {
                            String name1 = file.getName();
                            if (name1.toLowerCase(Locale.ROOT).endsWith(".pub") && file.isFile()) {
                                String s = FileUtils.readFileToString(file, "UTF-8").trim();
                                String[] s1 = s.split(" ");
                                if (s1.length == 3) {
                                    // 双向验证：连接服务器验证邮箱是否为本机器上传的SSH Key
                                    GitLabUser gitLabUser = getGitLabUser(s1[2]);
                                    if(gitLabUser!=null){
                                        List<String> gitLabUserKeys = getGitLabUserKeys(gitLabUser.getId());
                                        List<String> collect = gitLabUserKeys.stream().filter(s8 -> s8.trim().equals(s)).collect(Collectors.toList());

                                        if (collect.size()>0) {
                                            gitEmail = s1[2];
                                            gitName = gitLabUser.getName();
                                            break;
                                        }
                                    }

                                }
                            }
                        }
                    }
                } else {
                    StoredConfig config = git.getRepository().getConfig();
                    String name = config.getString("user", null, "name");
                    String email = config.getString("user", null, "email");
                    gitName = name;
                    gitEmail = email;
                }
            }
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
    }

    public static String getGitInstallPath() throws IOException {
        String exec = LocalCmdExecutor.exec("cmd /c reg query HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\");
        String[] split = exec.split("\n");
        for (String s : split) {
            String exec1 = LocalCmdExecutor.exec("cmd /c reg query " + s + " /v DisplayName", 2);
            if (StringUtils.isNotEmpty(exec1) && exec1.indexOf("Git version") != -1) {
                String exec2 = LocalCmdExecutor.exec("cmd /c reg query " + s + " /v InstallLocation", 2);
                if (StringUtils.isNotEmpty(exec2)) {
                    exec2 = exec2.replaceAll("InstallLocation    REG_SZ    ", "").trim();
                    return exec2;
                }
            }
        }
        return null;
    }

    public static String describe() {
        if (StringUtils.isNotBlank(gitDescribe)) {
            return gitDescribe;
        }
        return null;
    }

    public static String username() {

        try {
            if (StringUtils.isNotBlank(gitName)) {
                return gitName;
            }
            return LocalCmdExecutor.exec(git + " config user.name") + "(maybe)";
        } catch (IOException e) {
            // 未配置环境变量

//            log.warn("获取Git用户名异常：" + e.getLocalizedMessage());
        }
        return null;
    }

    public static String email() {
        try {
            if (StringUtils.isNotBlank(gitEmail)) {
                return gitEmail;
            }

            return LocalCmdExecutor.exec(git + " config user.email") + "(maybe)";
        } catch (IOException e) {
//            log.warn("获取Git邮箱异常：" + e.getLocalizedMessage());
        }
        return null;
    }

    public static String name() {
        String username = username();
        String hz = "";
        if(username!= null && username.trim().length()>0){
            if (username.endsWith("(maybe)")) {
                username = username.substring(0, username.length() - 7);
                hz = "(maybe)";
            }

            GitLabUser gitLabUser = getGitLabUser(username, hz);
            if (gitLabUser != null) {
                return gitLabUser.getName();
            }
        }else{
            return "PPP";
        }

        return null;
    }

    public static GitLabUser getGitLabUser(String name) {
        return getGitLabUser(name, "");
    }

    public static GitLabUser getGitLabUser(String name, String hz) {
        Request request = new Request.Builder().url("http://172.30.4.163/api/v4/users?search=" + name).header("PRIVATE-TOKEN", "_HNZtSAvSsbfn7Xo5vmw").build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();

            if (response.body().contentLength() > 0) {
                JSONArray userList = JSON.parseArray(response.body().string());
                if (userList.size() > 0) {
                    JSONObject user = (JSONObject) userList.get(0);
                    return user.toJavaObject(GitLabUser.class);
                }
            }
        } catch (IOException e) {
//            log.warn("获取Git用户信息异常：" + e.getLocalizedMessage());
        }
        return null;
    }

    public static List<String> getGitLabUserKeys(Integer id) {
        Request request = new Request.Builder().url("http://172.30.4.163/api/v4/users/" + id + "/keys").header("PRIVATE-TOKEN", "_HNZtSAvSsbfn7Xo5vmw").build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();

            if (response.body().contentLength() > 0) {
                JSONArray userList = JSON.parseArray(response.body().string());
                if (userList.size() > 0) {
                    List<String> keys = new ArrayList<>();
                    for (Object o : userList) {
                        JSONObject object = (JSONObject) o;
                        keys.add(object.getString("key"));
                    }
                    return keys;
                }
            }
        } catch (IOException e) {
//            log.warn("获取Git用户信息异常：" + e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * 获取链接
     *
     * @param dir
     * @return
     */
    public static Git openRpo(String dir, String gitRemoteUrl) throws GitAPIException {
        Git git = null;
        try {
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            Repository repository = builder.setGitDir(Paths.get(dir, ".git").toFile()).setMustExist(true).build();
            git = new Git(repository);
        } catch (IOException e) {

        }

        return git;
    }

    public static String getProjectName() {
        try {
            String rootPath = getProjectPathURL();
            MavenXpp3Reader reader = new MavenXpp3Reader();
            String myPom = rootPath + File.separator + "pom.xml";
            Model model = reader.read(new FileReader(myPom));
            return model.getArtifactId();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取项目
     */
    public static GitLabProject getGitProject(String url) {
        String[] split = url.split(":");
        String server = split[0].split("@")[1];
        String pathNamespace = split[1].substring(0, split[1].lastIndexOf(".git"));
        String[] split1 = pathNamespace.split("/");
        String project = split1[1];
        String namespace = split1[0];
        Request request = new Request.Builder().url("http://" + server + "/api/v4/projects?search=" + project + "&scope=projects&simple=yes").header("PRIVATE-TOKEN", "_HNZtSAvSsbfn7Xo5vmw").build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();

            if (response.body().contentLength() > 0) {
                JSONArray userList = JSON.parseArray(response.body().string());
                if (userList.size() > 0) {
                    for (Object o : userList) {
                        JSONObject object = (JSONObject) o;
                        String path_with_namespace = object.getString("path_with_namespace");
                        if (path_with_namespace.equals(pathNamespace)) {
                            return object.toJavaObject(GitLabProject.class);
                        }
                    }
                }
            }
        } catch (IOException e) {
//            log.warn("获取Git项目信息异常：" + e.getLocalizedMessage());
        }

        return null;
    }

    /**
     * 根据项目ID获取所有用户
     *
     * @param projectId
     * @return
     */
    public static List<GitLabUser> getProjectUsers(Integer projectId) {
        Request request = new Request.Builder().url("http://172.30.4.163/api/v4/projects/" + projectId + "/search?scope=users&search=").header("PRIVATE-TOKEN", "_HNZtSAvSsbfn7Xo5vmw").build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();

            if (response.body().contentLength() > 0) {
                JSONArray userList = JSON.parseArray(response.body().string());
                if (userList.size() > 0) {
                    return JSON.parseArray(userList.toJSONString(), GitLabUser.class);
                }
            }
        } catch (IOException e) {
//            log.warn("获取Git用户信息异常：" + e.getLocalizedMessage());
        }

        return null;
    }

    public static Map<String,List<GitLabProject>> getProjects(){
        Request request = new Request.Builder().url("http://172.30.4.163/api/v4/projects?search=&scope=projects&simple=yes").header("PRIVATE-TOKEN", "_HNZtSAvSsbfn7Xo5vmw").build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String string = response.body().string();
            if (string.length() > 0) {
                JSONArray userList = JSON.parseArray(string);
                if (userList.size() > 0) {
                    Map<String,List<GitLabProject>> project = new HashMap<>();
                    for (Object o : userList) {
                        JSONObject object = (JSONObject) o;
                        String path_with_namespace = object.getString("path_with_namespace");
                        if(StringUtils.isNotBlank(path_with_namespace)){
                            String[] split = path_with_namespace.split("/");
                            String groupName = split[0];
                            List<GitLabProject> gitLabProjects = project.get(groupName);
                            if(gitLabProjects!=null){
                                gitLabProjects.add(object.toJavaObject(GitLabProject.class));
                            }else{
                                List<GitLabProject> list =new ArrayList<>();
                                list.add(object.toJavaObject(GitLabProject.class));
                                project.put(groupName,list);
                            }
                        }
                    }
                    return project;
                }
            }
        } catch (IOException e) {
//            log.warn("获取Git项目信息异常：" + e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * 获取分组
     */
    public static GitLabGroup getGitGroup(String group) {

        Request request = new Request.Builder().url("http://172.30.4.163/api/v4/groups?search="+group).header("PRIVATE-TOKEN", "_HNZtSAvSsbfn7Xo5vmw").build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String s = response.body().string();
            if (s.length() > 0) {
                JSONArray userList = JSON.parseArray(s);
                if (userList.size() > 0) {
                    for (Object o : userList) {
                        JSONObject object = (JSONObject) o;
                        String path = object.getString("path");
                        if (path.equals(group)) {
                            return object.toJavaObject(GitLabGroup.class);
                        }
                    }
                }
            }
        } catch (IOException e) {
//            log.warn("获取Git分组信息异常：" + e.getLocalizedMessage());
        }

        return null;
    }

    public static String getProjectPathURL(){
        String path = System.getProperty("java.class.path");
        if(StringUtils.isNotBlank(path)){
            String s = path.split(":")[0];
            if(s.endsWith("/target/classes")){
                return s.substring(0,s.length()-15);
            }
        }
        return null;
    }

//    public static void main(String[] args) {
//        System.out.println(getProjectPathURL());
//        String group = "saas-scce";
//        List<GitLabProject> gitLabProjects = getProjects().get(group);
//        GitLabProject gitGroup = getGitGroup(group);
//        System.out.println("【"+gitGroup.getDescription()+"】");
//        for (GitLabProject gitLabProject : gitLabProjects) {
//            System.out.println(gitLabProject.getDescription() +" develop "+gitLabProject.getSsh_url_to_repo());
//        }


//        GitLabProject gitProject = getGitProject("scce-cos-ops");
//        if(gitProject!=null){
//            List<GitLabUser> projectUsers = getProjectUsers(gitProject.getId());
//            projectUsers.forEach(u->{
//                System.out.println(u);
//            });
//        }
//    }
}

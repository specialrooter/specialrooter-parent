package io.specialrooter.util;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ExcelUtils {

    /**
     * Excel 类型枚举
     */
    enum ExcelTypeEnum {
        XLS("xls"), XLSX("xlsx");
        private String value;

        ExcelTypeEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * excel 导出
     *
     * @param list           数据
     * @param title          标题
     * @param sheetName      sheet名称
     * @param pojoClass      pojo类型
     * @param fileName       文件名称
     * @param isCreateHeader 是否创建表头
     * @param response
     */
    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName, boolean isCreateHeader, HttpServletResponse response, String fileType) throws IOException {
        ExportParams exportParams = new ExportParams(title, sheetName, ExcelType.XSSF);
        exportParams.setCreateHeadRows(isCreateHeader);
        defaultExport(list, pojoClass, fileName, response, exportParams, fileType);
    }

    /**
     * excel 导出
     *
     * @param list      数据
     * @param title     标题
     * @param sheetName sheet名称
     * @param pojoClass pojo类型
     * @param fileName  文件名称
     * @param response
     */
    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName, HttpServletResponse response, String fileType) throws IOException {
        defaultExport(list, pojoClass, fileName, response, new ExportParams(title, sheetName, ExcelType.XSSF), fileType);
    }

    /**
     * excel 导出
     *
     * @param list         数据
     * @param pojoClass    pojo类型
     * @param fileName     文件名称
     * @param response
     * @param exportParams 导出参数
     */
    public static void exportExcel(List<?> list, Class<?> pojoClass, String fileName, ExportParams exportParams, HttpServletResponse response, String fileType) throws IOException {
        defaultExport(list, pojoClass, fileName, response, exportParams, fileType);
    }

    /**
     * excel 导出
     *
     * @param list     数据
     * @param fileName 文件名称
     * @param response
     */
    public static void exportExcel(List<Map<String, Object>> list, String fileName, HttpServletResponse response, String fileType) throws IOException {
        defaultExport(list, fileName, response, fileType);
    }

    /**
     * 默认的 excel 导出
     *
     * @param list         数据
     * @param pojoClass    pojo类型
     * @param fileName     文件名称
     * @param response
     * @param exportParams 导出参数
     */
    private static void defaultExport(List<?> list, Class<?> pojoClass, String fileName, HttpServletResponse response, ExportParams exportParams, String fileType) throws IOException {
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, pojoClass, list);
        downLoadExcel(fileName, response, workbook, fileType);
    }

    /**
     * 默认的 excel 导出
     *
     * @param list     数据
     * @param fileName 文件名称
     * @param response
     */
    private static void defaultExport(List<Map<String, Object>> list, String fileName, HttpServletResponse response, String fileType) throws IOException {
        Workbook workbook = ExcelExportUtil.exportExcel(list, ExcelType.HSSF);
        downLoadExcel(fileName, response, workbook, fileType);
    }

    /**
     * excel 模板导出
     *
     * @param map      数据
     * @param params   模板
     * @param fileName 文件名称
     * @param response
     */
    public static void templateExport(Map<String, Object> map, TemplateExportParams params, String fileName, HttpServletResponse response, String fileType) throws IOException {
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        downLoadExcel(fileName, response, workbook, fileType);
    }

    /**
     * excel workbook导出
     *
     * @param workbook 数据
     * @param fileName 文件名称
     * @param response
     */
    public static void workbookExport(Workbook workbook, String fileName, HttpServletResponse response, String fileType) throws IOException {
        downLoadExcel(fileName, response, workbook, fileType);
    }


    /**
     * 下载
     *
     * @param fileName 文件名称
     * @param response
     * @param workbook excel数据
     */
    private static void downLoadExcel(String fileName, HttpServletResponse response, Workbook workbook, String fileType) throws IOException {
        try {
            if (fileType == null) {
                fileType = ExcelTypeEnum.XLSX.getValue();
            }
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + "." + fileType, "UTF-8"));
            response.setHeader("Pragma", URLEncoder.encode(fileName + "." + fileType, "UTF-8"));
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public static <T> List<T> importExcel(String filePath, Integer titleRows, Integer headerRows, Class<T> pojoClass) throws IOException {
        return importExcel(filePath, titleRows, headerRows, pojoClass, "/excel/");
    }

    /**
     * excel 导入
     *
     * @param filePath   excel文件路径
     * @param titleRows  标题行
     * @param headerRows 表头行
     * @param pojoClass  pojo类型
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(String filePath, Integer titleRows, Integer headerRows, Class<T> pojoClass, String path) throws IOException {
        if (StringUtils.isBlank(filePath)) {
            return null;
        }

        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        params.setNeedSave(true);
        params.setSaveUrl(path);
        try {
            return ExcelImportUtil.importExcel(new File(filePath), pojoClass, params);
        } catch (NoSuchElementException e) {
            throw new IOException("模板不能为空");
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * excel 导入
     *
     * @param file      excel文件
     * @param pojoClass pojo类型
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(MultipartFile file, Class<T> pojoClass) throws IOException {
        return importExcel(file, 1, 1, pojoClass);
    }

    /**
     * excel 导入
     *
     * @param file       excel文件
     * @param titleRows  标题行
     * @param headerRows 表头行
     * @param pojoClass  pojo类型
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(MultipartFile file, Integer titleRows, Integer headerRows, Class<T> pojoClass) throws IOException {
        return importExcel(file, titleRows, headerRows, false, pojoClass);
    }

    /**
     * excel 导入
     *
     * @param file       上传的文件
     * @param titleRows  标题行
     * @param headerRows 表头行
     * @param needVerfiy 是否检验excel内容
     * @param pojoClass  pojo类型
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(MultipartFile file, Integer titleRows, Integer headerRows, boolean needVerfiy, Class<T> pojoClass) throws IOException {
        if (file == null) {
            return null;
        }
        try {
            return importExcel(file.getInputStream(), titleRows, headerRows, needVerfiy, pojoClass);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public static <T> List<T> importExcel(InputStream inputStream, Integer titleRows, Integer headerRows, boolean needVerify, Class<T> pojoClass) throws IOException {
        return importExcel(inputStream, titleRows, headerRows, needVerify, pojoClass, "/excel/");
    }

    /**
     * excel 导入
     *
     * @param inputStream 文件输入流
     * @param titleRows   标题行
     * @param headerRows  表头行
     * @param needVerify  是否检验excel内容
     * @param pojoClass   pojo类型
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(InputStream inputStream, Integer titleRows, Integer headerRows, boolean needVerify, Class<T> pojoClass, String path) throws IOException {
        if (inputStream == null) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        params.setSaveUrl(path);
        params.setNeedSave(true);
        params.setNeedVerify(needVerify);
        try {
            return ExcelImportUtil.importExcel(inputStream, pojoClass, params);
        } catch (NoSuchElementException e) {
            throw new IOException("excel文件不能为空");
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * 获取模板路径
     *
     * @param templateName
     * @return
     */
    public static String getExcelTemplatePath(String templateName) {
        ClassPathResource pathResource = new ClassPathResource("execl/" + templateName + ".xlsx");
        if (!pathResource.exists()) {
            throw new RuntimeException("模板文件不存在，请联系管理员");
        }
        return pathResource.getPath();
    }


    /**
     * 获取excle导出模板
     *
     * @param templateName
     * @return
     */
    public static TemplateExportParams getExcelTemplate(String templateName) {
        TemplateExportParams params = new TemplateExportParams(
                getExcelTemplatePath(templateName), true);
        return params;
    }

    /**
     * 生成File文件
     *
     * @param list
     * @param params
     * @param pojoClass
     * @param fileType
     * @param path
     * @return
     * @throws IOException
     */
    public static File exportFile(List<?> list, ExportParams params, Class<?> pojoClass, String fileType, String path) throws IOException {
        Workbook workbook = ExcelExportUtil.exportExcel(params, pojoClass, list);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        InputStream is = new ByteArrayInputStream(bos.toByteArray());
        // 兼容K8S，动态验证文件夹是否存在，不存在自动创建
        File filePath = new File(path);
        if (!filePath.exists() && !filePath.isDirectory()) {
            filePath.mkdirs();
        }

        File file = new File(path + UUID.randomUUID().toString() + "." + fileType);

        Files.copy(is, file.toPath(), new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
        return file;
    }

    /**
     * 生成File文件
     *
     * @param list
     * @param title
     * @param sheetName
     * @param exclusions 排出列
     * @param pojoClass
     * @param fileType
     * @param path
     * @return
     * @throws IOException
     */
    public static File exportFile(List<?> list, String title, String sheetName, String[] exclusions, Class<?> pojoClass, String fileType, String path) throws IOException {
        ExportParams params = new ExportParams(title, sheetName, ExcelType.XSSF);
        params.setExclusions(exclusions);
        return exportFile(list, params, pojoClass, fileType, path);
    }

    /**
     * 压缩文件
     *
     * @param zipFile  压缩后的文件
     * @param srcFiles 需要被压缩的文件
     */
    public static void toZip(File zipFile, File... srcFiles) throws Exception {
        if (zipFile == null) {
            return;
        }
        if (!zipFile.getName().endsWith(".zip")) {
            return;
        }
        try (FileOutputStream out = new FileOutputStream(zipFile); ZipOutputStream zos = new ZipOutputStream(out)) {
            for (File srcFile : srcFiles) {
                byte[] buf = new byte[StreamUtils.BUFFER_SIZE];
                zos.putNextEntry(new ZipEntry(srcFile.getName()));
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                in.close();
            }
        } finally {
            for (File file : srcFiles) {
                file.delete();
            }
        }
    }

}

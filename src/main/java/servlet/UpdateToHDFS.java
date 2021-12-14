package servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import utils.DfsUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static utils.DataFromCsv.dataFromCsvToHbase;


/**
 * Servlet implementation class UploadServlet
 *
 * @author：Parker
 */

@WebServlet(urlPatterns = "/UpdateToHDFS")
public class UpdateToHDFS extends HttpServlet {
    private static final long serialVersionUID = 1L;
    /**
     * @param MEMORY_THRESHOLD
     * 内存临界值 : 3MB
     * @param MAX_FILE_SIZE
     * 最大文件上传值 : 50MB
     * @param MAX_REQUEST_SIZE
     * 最大请求值 (包含文件和表单数据) : 60MB
     * @param URL
     * 上传成功后跳转的对象
     */
    private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3;
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 50;
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 60;
    private static final String URL = "http://localhost:8080/Group4Project/analyze/index.jsp";
    private static final String KV[] = {"up","success"};
    ServletContext ctxContext;

    /**
     * 上传数据及保存文件
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        ctxContext = config.getServletContext();
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {

        if (!(KV[1].equals(ctxContext.getAttribute(KV[0])))) {
            // 检测是否为多媒体上传
            if (!ServletFileUpload.isMultipartContent(request)) {
                // 如果不是则停止
                PrintWriter writer = response.getWriter();
                writer.println("Error: form must have enctype=multipart/form-data");
                writer.flush();
                return;
            }

            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(MEMORY_THRESHOLD);
            factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setFileSizeMax(MAX_FILE_SIZE);
            upload.setSizeMax(MAX_REQUEST_SIZE);
            upload.setHeaderEncoding("UTF-8");
            boolean b = false;
            try {
                List<FileItem> formItems = upload.parseRequest(request);

                if (formItems != null && formItems.size() > 0) {
                    for (FileItem item : formItems) {
                        if (!item.isFormField()) {
                            String fileName = new File(item.getName()).getName();
                            b = DfsUtil.putFilesInToHDFSUtil(item.getInputStream(), fileName);
                            if (b) {
                                dataFromCsvToHbase("hdfs://niit:9000" + DfsUtil.getPATH() + fileName);
                            }
                        }
                    }
                }
                ctxContext.setAttribute(KV[0], KV[1]);
            } catch (Exception ex) {
                request.setAttribute("message",
                        "错误信息: " + ex.getMessage());
            }
        }
        response.sendRedirect(URL);

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}

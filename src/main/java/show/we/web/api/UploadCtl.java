package show.we.web.api;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import net.coobird.thumbnailator.Thumbnails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.zb.common.Constant.ReCode;
import com.zb.core.conf.Config;
import com.zb.core.web.ReMsg;
import com.zb.service.cloud.StorageService;

@Controller
@RequestMapping(value = "/sys")
public class UploadCtl extends BaseCtl {
	static final Logger log = LoggerFactory.getLogger(UploadCtl.class);
	
	@Autowired
	StorageService storageService;

	// @RequestMapping("/headPic")
	public ReMsg uploadHeadPic(@RequestParam MultipartFile file,
			HttpServletRequest request, HttpServletResponse response, Long uid) {
		if (uid < 1) {
			return new ReMsg(ReCode.USER_ID_ERR);
		}
		try {
			String savePath = "user/" + uid / 1000000 + "/" + uid % 1000000
					/ 1000 + "/" + uid % 1000 + "/";
			String url = this.upload(file, "upload/", savePath, "" + uid,
					"png", this.getAllowSuffix(), this.getAllowSize(), request);
			log.debug("Upload:" + url);
			return new ReMsg(ReCode.OK, url);
		} catch (Exception e) {
			log.error("Upload", e);
		}
		return new ReMsg(ReCode.FAIL);
	}

	@ResponseBody
	@RequestMapping("/headPic")
	public ReMsg uploadHeadPic(@RequestParam MultipartFile file,
			HttpServletRequest request, HttpServletResponse response) {
		return uploadHeadPic(file, request, response, super.getUid());
	}

	@ResponseBody
	@RequestMapping("/uPic")
	public ReMsg uploadPic(@RequestParam MultipartFile file,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			String savePath = simplePath();
			String rootPath = Config.cur().get("img.root.path", "");
			String url = this.upload(file, rootPath, savePath,
					simpleFilename(), "png", this.getAllowSuffix(),
					this.getAllowSize(), request);
			log.debug("Upload:" + url);
			return new ReMsg(ReCode.OK, url);
		} catch (Exception e) {
			log.error("Upload", e);
		}
		return new ReMsg(ReCode.FAIL);
	}

	@ResponseBody
	@RequestMapping("/uTmpPic")
	public ReMsg uploadTmpPic(@RequestParam MultipartFile file,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			String savePath = simplePath();
			String rootPath = Config.cur().get("img.root.path", "") + "tmps/";

			String url = this.upload(file, rootPath, savePath,
					simpleFilename(), "png", this.getAllowSuffix(),
					this.getAllowSize(), request);
			log.debug("Upload:" + url);
			return new ReMsg(ReCode.OK, "/tmps" + url);
		} catch (Exception e) {
			log.error("Upload", e);
		}
		return new ReMsg(ReCode.FAIL);
	}

	@ResponseBody
	@RequestMapping("/uResPic")
	public ReMsg uploadResPic(@RequestParam MultipartFile file,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			String savePath = simplePath();
			String url = this.upload(file, "upload/", savePath,
					simpleFilename(), this.getAllowSuffix(),
					this.getAllowSize(), request);
			log.debug("Upload:" + url);
			return new ReMsg(ReCode.OK, url);
		} catch (Exception e) {
			log.error("Upload", e);
		}
		return new ReMsg(ReCode.FAIL);
	}
	
	private String allowSuffix = "jpg,png,gif,jpeg";// 允许文件格式
	private String allowAudioSuffix = "acc";// 允许音频格式   // XXX
	private long allowSize = 2L;// 允许文件大小
	private String fileName;
	private String[] fileNames;

	public String getAllowSuffix() {
		return allowSuffix;
	}

	public void setAllowSuffix(String allowSuffix) {
		this.allowSuffix = allowSuffix;
	}

	public long getAllowSize() {
		return allowSize * 1024 * 1024;
	}

	public void setAllowSize(long allowSize) {
		this.allowSize = allowSize;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String[] getFileNames() {
		return fileNames;
	}

	public void setFileNames(String[] fileNames) {
		this.fileNames = fileNames;
	}

	private static String simplePath() {
		SimpleDateFormat fmt = new SimpleDateFormat("yy/MMdd/HH/");
		return fmt.format(new Date());
	}

	static Random r = new Random();

	private static String simpleFilename() {
		int i = r.nextInt(1000);
		if (i < 10) {
			i = i * 100;
		} else if (i < 100) {
			i = i * 10;
		}
		return "" + System.currentTimeMillis() + i;
	}

	/**
	 * 上传文件基础方法
	 * 
	 * @param file
	 *            上传文件
	 * @param basePath
	 *            服务器基本路径 /带头绝对路径：/data/upload，非/带头，相对路径
	 *            upload将保存webapp的资源路径upload下
	 * @param savePath
	 *            子目录，这个与访问路径一致 2015/11/10/ 访问为/2015/11/10/
	 * @param fileName
	 *            文件命 比如 123
	 * @param ext
	 *            扩展名
	 * @param allowSuffix
	 *            上传文件允许后缀
	 * @param maxSize
	 *            上传文件允许大小
	 * @param request
	 * @return 返回 文件路径访问路径 /2015/11/10/123.png
	 * @throws Exception
	 */
	public String upload(MultipartFile file, String basePath, String savePath,
			String fileName, String ext, String allowSuffix, long maxSize,
			HttpServletRequest request) throws Exception {
		String fileUrl = null;
		try {
			String suffix = file.getOriginalFilename()
					.substring(file.getOriginalFilename().lastIndexOf(".") + 1)
					.toLowerCase();
			if (!allowSuffix.contains(suffix)) {
				throw new Exception("请上传允许格式的文件");
			}
			if (file.getSize() > maxSize) {
				throw new Exception("您上传的文件大小已经超出范围");
			}

			String realPath = null;
			if (basePath.startsWith("/")) {
				realPath = basePath + savePath;
			} else {
				realPath = request.getSession().getServletContext()
						.getRealPath("/")
						+ "/" + basePath + savePath;
			}
			File destFile = new File(realPath);
			if (!destFile.exists()) {
				destFile.mkdirs();
			}
			String realName = fileName + "." + ext;
			File f = new File(destFile.getAbsoluteFile() + "/" + realName);
			System.out.println(f.getAbsolutePath());
			if (ext.equals(suffix)) {
				file.transferTo(f);
				f.createNewFile();
			} else {
				Thumbnails.of(file.getInputStream()).scale(1d).toFile(f);
			}
			fileUrl = "/" + savePath + realName;
		} catch (Exception e) {
			throw e;
		}
		return fileUrl;
	}

	public String upload(MultipartFile file, String basePath, String savePath,
			String fileName, String allowSuffix, long maxSize,
			HttpServletRequest request) throws Exception {
		String fileUrl = null;
		try {
			String suffix = file.getOriginalFilename()
					.substring(file.getOriginalFilename().lastIndexOf(".") + 1)
					.toLowerCase();
			if (!allowSuffix.contains(suffix)) {
				throw new Exception("请上传允许格式的文件");
			}
			if (file.getSize() > maxSize) {
				throw new Exception("您上传的文件大小已经超出范围");
			}

			String realPath = null;
			if (basePath.startsWith("/")) {
				realPath = basePath + savePath;
			} else {
				realPath = request.getSession().getServletContext()
						.getRealPath("/")
						+ "/" + basePath + savePath;
			}
			File destFile = new File(realPath);
			if (!destFile.exists()) {
				destFile.mkdirs();
			}
			String realName = fileName + "." + suffix;
			File f = new File(destFile.getAbsoluteFile() + "/" + realName);
			System.out.println(f.getAbsolutePath());
			file.transferTo(f);
			f.createNewFile();
			fileUrl = "/" + savePath + realName;
		} catch (Exception e) {
			throw e;
		}
		return fileUrl;
	}

	public static void main(String[] args) {
		System.out.println(simpleFilename());
		System.out.println(simpleFilename());
		System.out.println(simpleFilename());
	}
	
	
	/** 上传音频 */
	@ResponseBody
	@RequestMapping("/uploadAudio")
	public ReMsg uploadAudio(@RequestParam MultipartFile file, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1)
					.toLowerCase();
			if (!allowAudioSuffix.contains(suffix)) {
				throw new Exception("请上传允许格式的文件");
			}
			if (file.getSize() > 2048) { // FIXME
				throw new Exception("您上传的文件大小已经超出范围");
			}
			return storageService.uploadAudio(file, request, response);
		} catch (Exception e) {
			log.error("Upload", e);
			return new ReMsg(ReCode.FAIL);
		}
	}
	
}

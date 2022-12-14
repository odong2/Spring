package com.util;

import java.io.File;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

public class HashMapBinder {
	Logger logger = Logger.getLogger(HashMapBinder.class);
	HttpServletRequest req = null;
	MultipartHttpServletRequest mreq = null;
	// 첨부 파일 처리에 필요한 변수 선언 - 바이너리 타입 처리를 위해
	MultipartRequest multi = null; // import MultipartRequest -> cos.jar의 클래스
	// 첨부 파일 업로드의 물리적인 경로 이름
	String realFolder = null;
	// 첨부파일 한글 처리
	String encType = "utf-8";
	// 첨부파일 최대크기
	int maxSize = 5*1024*1024; // 5MB
	// 바이너리 타입을 받을 땐 post방식으로 처리할 것
	// 단 기존 request.getParameter로 값을 못 읽어옴
	// 스프링에서는 이를 위해 MultipartHttpServletRequest를 지원한다.(사용자가 입력한 값을 받을 수 있다)
	public HashMapBinder (MultipartHttpServletRequest mreq) {
		this.mreq = mreq;
	}
	public HashMapBinder (HttpServletRequest req) {
		this.req = req;
		realFolder ="D:\\workspace_Java\\dev_web\\src\\main\\webapp\\pds"; // 파일생성 폴더 경로
	}
	public void multiBind(Map<String, Object> pMap) {
		pMap.clear();
		try {
			multi = new MultipartRequest(req, realFolder, maxSize, encType, new DefaultFileRenamePolicy());// request, 폴더경로, 사이즈, 인코딩타입, 옵저버(같은이름일때 정책)
		} catch (Exception e) {
			logger.info("Exception:"+e.toString());
		}
		Enumeration<String> em = multi.getParameterNames();
		while(em.hasMoreElements()) {
			String key = em.nextElement(); // key이름 얻음
			pMap.put(key, multi.getParameter(key));
		}
		// 기존 클라이언트에서 받아온 정보를 처리하기
		// 첨부파일 정보를 받아오기
		Enumeration<String> files = multi.getFileNames();
		// 만일 폼 전송에서 업로드할 파일명이 존재하면..
		if(files != null) {
			// 업로드 대상 파일을 객체로 만듦
			File file = null; // java.io 패키지
			while(files.hasMoreElements()) {
				String fname = files.nextElement(); // 파일 이름 담는다
				String filename = multi.getFilesystemName(fname);
				pMap.put("bs_file", filename);
				if(filename != null && filename.length()>1) {
					file = new File(realFolder +"\\" + filename); // 파일 객체 생성
				}
				logger.info(file);
			}
			// 첨부파일에 크기를 담을 수 있는 변수 선언
			double size = 0;
			if(file != null) {
				size = file.length();
				size = size/1024.0; // byte -> kbyte 변환
				pMap.put("bs_size", size);
			}
		}
		}
	public void bind(Map<String,Object> pMap) {
		// 사용자가 입력한 값을 담을 맵이 외부 클래스에서 인스턴스화 되어 넘어오니까
		// 초기화 처리 후 사용함
		pMap.clear(); // 초기화하고 사용
		// html화면에 정의된 input name값들을 모두 담아줌
		Enumeration<String> em = req.getParameterNames();
		while(em.hasMoreElements()) {
			// key값 꺼내기
			String key = em.nextElement(); // b_title, b_writer, b_content, b_pw등
			pMap.put(key, req.getParameter(key));
		}
		logger.info(pMap);
	}/////////// end of bind
	//GET방식 한글처리 - server.xml에서 URIEncoding="utf-8"
	//POST방식 한글처리 - HangulConversion.toUTF(meq.getParameter(key));
	public void mbind(Map<String,Object> pMap) {
		// 사용자가 입력한 값을 담을 맵이 외부 클래스에서 인스턴스화 되어 넘어오니까
		// 초기화 처리 후 사용함
		pMap.clear(); // 초기화하고 사용
		// html화면에 정의된 input name값들을 모두 담아줌
		Enumeration<String> em = mreq.getParameterNames();
		while(em.hasMoreElements()) {
			// key값 꺼내기
			String key = em.nextElement(); // b_title, b_writer, b_content, b_pw등
			pMap.put(key, HangulConversion.toUTF(mreq.getParameter(key)));
		}
		logger.info(pMap);
	}
}

package org.crescent.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.annotation.XmlAnyAttribute;

public class MakeJsonFormUpdateByFieldValueFileFromFiles {

	private static final Logger logger = LoggerFactory.getLogger(MakeJsonFormUpdateByFieldValueFileFromFiles.class);
	private FileInfo fileInfo = null;
	private List<Map<String, String>> targetFileList = new ArrayList<>();
	private int madeFileCount = 0;
	private BufferedWriter bw = null;
	
	public static void main(String[] args) throws IOException {
		if(args.length != 1) {
			throw new IllegalArgumentException("need indexing_file_info.xml location info");
		}

		MakeJsonFormUpdateByFieldValueFileFromFiles makeJsonFormFileFromFiles = new MakeJsonFormUpdateByFieldValueFileFromFiles();
		makeJsonFormFileFromFiles.makeFile(args[0]);
	}

	private void makeFile(String xmlFileLocation) throws IOException {
		initFileInfo(xmlFileLocation);
		
		File sourceFile = new File(fileInfo.getSrcDir());
		String now = new SimpleDateFormat("yyyyMMddHHmmss").format(OffsetDateTime.now(ZoneId.systemDefault()));
		
		File outputFile = new File(fileInfo.getOutputDir() + "/" + now + ".txt");
		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), fileInfo.getOutputEncoding()));
		
		writeFile(sourceFile);
		
		//50000개가 안되거나... 남아있는것들 
		ObjectMapper mapper = new ObjectMapper();
		
		Map<String, Object> indexingForm = new HashMap<String, Object>();
		indexingForm.put("command", "update_by_field_value");
		indexingForm.put("indexingType", "bulk");
		indexingForm.put("documentList", targetFileList);
		indexingForm.put("query", "filePath:*");
		
		//write file
		String jsonForm = mapper.writeValueAsString(indexingForm);
		
		bw.write(jsonForm);
		
		targetFileList.clear();
		
		bw.close();
	}
	
	private void writeFile(File sourceFile) throws IOException {
		if (sourceFile.canRead()) {
			if (sourceFile.isDirectory()) {
				String[] files = sourceFile.list();
				// an IO error could occur
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						writeFile(new File(sourceFile, files[i]));
					}
				}
			} else { //directory가 아니라 실제 파일이면..
				logger.info("is file....{} / {}", sourceFile.getPath(), sourceFile.getName());
				
				//match 확인
				Pattern pattern = Pattern.compile(".+\\.("+fileInfo.fileExtInclude+")$", Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(sourceFile.getName());
				
				if(!matcher.matches()) {
					return;
				}
				
				madeFileCount++;
				
				if(madeFileCount == fileInfo.getLimitFileCountOnOutput()) {
					madeFileCount = 0;
					
					ObjectMapper mapper = new ObjectMapper();
					
					Map<String, Object> indexingForm = new HashMap<String, Object>();
					indexingForm.put("command", "update_by_field_value");
					indexingForm.put("indexingType", "bulk");
					indexingForm.put("documentList", targetFileList);
					indexingForm.put("query", "filePath:*");
					
					//write file
					String jsonForm = mapper.writeValueAsString(indexingForm);
					bw.write(jsonForm);
					
					targetFileList.clear();
					
					//exchange BufferedWriter
					BufferedWriter newBw = exchangeWriter(bw);
					
					bw = null;
					bw = newBw;
				}

				
				String fileName = sourceFile.getName();
				String filePath = sourceFile.getPath();
				String modidate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(sourceFile.lastModified()));
				String fileExtName = sourceFile.getName().substring(sourceFile.getName().lastIndexOf(".")+1);
				
				StringBuilder contents = new StringBuilder();
				
				InputStream is = new FileInputStream(sourceFile);
				InputStreamReader isr = new InputStreamReader(is, fileInfo.getInputEncoding());
				BufferedReader br = new BufferedReader(isr);
				
				String t = "";
				while((t = br.readLine()) != null) {
					contents.append(t);
				}
				
				Map<String, String> oneFile = new HashMap<String, String>();
				oneFile.put("fileName", fileName);
				oneFile.put("filePath", filePath);
				oneFile.put("modiDate", modidate);
				oneFile.put("contents", contents.toString());
				oneFile.put("fileExtName", fileExtName);
				
				targetFileList.add(oneFile);
				
				br.close();
				isr.close();
				is.close();
			}
		} 
	}

	private BufferedWriter exchangeWriter(BufferedWriter oldWriter) throws IOException {
		oldWriter.close();
		String now = new SimpleDateFormat("yyyyMMddHHmmss").format(OffsetDateTime.now(ZoneId.systemDefault()));
		File outputFile = new File(fileInfo.getOutputDir() + "/" + now + ".txt");
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), fileInfo.getOutputEncoding()));
		
		return bw;
		
	}
	
	private void initFileInfo(String xmlFileLocation) throws FileNotFoundException {
		XStream xstream = new XStream();
		xstream.processAnnotations(FileInfo.class);
	
		InputStream is = new FileInputStream(xmlFileLocation);
		fileInfo = (FileInfo)xstream.fromXML(is);
	}
	
	@SuppressWarnings("unused")
	@XStreamAlias("fileInfo")
	private static class FileInfo {
		
		@XmlAnyAttribute
		private String srcDir;
		
		@XmlAnyAttribute
		private String outputDir;
		
		@XmlAnyAttribute
		private int limitFileCountOnOutput;
		
		@XmlAnyAttribute
		private String fileExtInclude;
		
		@XmlAnyAttribute
		private String inputEncoding;
		
		@XmlAnyAttribute
		private String outputEncoding;
		
		public String getInputEncoding() {
			return inputEncoding;
		}

		public void setInputEncoding(String inputEncoding) {
			this.inputEncoding = inputEncoding;
		}

		public String getOutputEncoding() {
			return outputEncoding;
		}

		public void setOutputEncoding(String outputEncoding) {
			this.outputEncoding = outputEncoding;
		}

		public String getSrcDir() {
			return srcDir;
		}
		
		public void setSrcDir(String srcDir) {
			this.srcDir = srcDir;
		}
		public String getOutputDir() {
			return outputDir;
		}
		public void setOutputDir(String outputDir) {
			this.outputDir = outputDir;
		}
		public int getLimitFileCountOnOutput() {
			return limitFileCountOnOutput;
		}
		public void setLimitFileCountOnOutput(int limitFileCountOnOutput) {
			this.limitFileCountOnOutput = limitFileCountOnOutput;
		}
		public String getFileExtInclude() {
			return fileExtInclude;
		}
		public void setFileExtInclude(String fileExtInclude) {
			this.fileExtInclude = fileExtInclude;
		}
	}
}

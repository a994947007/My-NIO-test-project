package nio01;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.Test;

public class ChannelTest {
	//使用间接缓冲区
	//@Test
	public void test() throws Exception{	//1064	1018	1018
		long start = System.currentTimeMillis();
		//建立输入输出流
		FileInputStream fis = new FileInputStream("G:/Feb_21_235618.hsn");
		FileOutputStream fos = new FileOutputStream("G:/copy_Feb_21_235618_1.hsn");
		//建立通道
		FileChannel inChannel = fis.getChannel();
		FileChannel outChannel = fos.getChannel();
		ByteBuffer buf = ByteBuffer.allocate(1024);
		while(inChannel.read(buf) != -1){
			buf.flip();
			outChannel.write(buf);
			buf.clear();
		}
		
		inChannel.close();
		outChannel.close();
		fis.close();
		fos.close();
		long end = System.currentTimeMillis();
		System.out.println(end - start);		
	}
	
	//直接使用IO流的方式读写
	//@Test	//307
	public void test2() throws Exception{	//286 299 307
		long start = System.currentTimeMillis();
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream("G:/Feb_21_235618.hsn"));
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("G:/copy_Feb_21_235618_2.hsn"));
		
		byte buf[] = new byte[1024];
		
		int len = 0;
		while((len = bis.read(buf)) != -1){
			bos.write(buf,0,len);
		}
		bos.flush();
		bis.close();
		bos.close();
		
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
	
	//使用直接缓冲区，但事实上如下文件传输还是将其复制到了JVM中，因为new了一个byte数组
	//@Test	//241
	public void test3() throws Exception{
		long start = System.currentTimeMillis();
		FileChannel inChannel = FileChannel.open(Paths.get("G:/Feb_21_235618.hsn"), StandardOpenOption.READ);
		FileChannel outChannel = FileChannel.open(Paths.get("G:/Feb_21_235618_3.hsn"), StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);
		
		MappedByteBuffer inMappedBuffer = inChannel.map(MapMode.READ_ONLY, 0, inChannel.size());
		MappedByteBuffer outMapBuffer = outChannel.map(MapMode.READ_WRITE, 0, inChannel.size());
	
		//直接对缓冲区进行数据的读写操作
		byte[] dst = new byte[inMappedBuffer.limit()];
		inMappedBuffer.get(dst);
		outMapBuffer.put(dst);
		inChannel.close();
		outChannel.close();
		long end = System.currentTimeMillis();
		System.out.println(end - start);		
	}
	
	//直接使用通道进行数据传输，使用的是直接缓冲区，也就是无需经过JVM内存
	@Test	//130
	public void test4() throws Exception{
		long start = System.currentTimeMillis();
		FileChannel inChannel = FileChannel.open(Paths.get("G:/Feb_21_235618.hsn"), StandardOpenOption.READ);
		FileChannel outChannel = FileChannel.open(Paths.get("G:/Feb_21_235618_3.hsn"), StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);
		inChannel.transferTo(0, inChannel.size(), outChannel);
		
		inChannel.close();
		outChannel.close();
		long end = System.currentTimeMillis();
		System.out.println(end - start);	
	}
}

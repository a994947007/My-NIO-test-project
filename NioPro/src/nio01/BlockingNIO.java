package nio01;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.Test;

public class BlockingNIO {
	@Test
	public void test() throws Exception{
		SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",9999));
		FileChannel inChannel = FileChannel.open(Paths.get("G:/Feb_21_235618.hsn"), StandardOpenOption.READ);

		ByteBuffer buf = ByteBuffer.allocate(1024);
		while(inChannel.read(buf) != -1){
			buf.flip();
			sChannel.write(buf);
			buf.clear();
		}
		sChannel.shutdownOutput();
		
		//接收来自服务端的反馈
		int len = 0;
		while((len = sChannel.read(buf)) != -1){
			buf.flip();
			System.out.println(new String(buf.array(),0,len));	
			buf.clear();
		}
		sChannel.shutdownInput();
		
		inChannel.close();
		sChannel.close();
	}
	
	@Test
	public void test2() throws Exception{
		ServerSocketChannel ssc = ServerSocketChannel.open();
		FileChannel outChannel = FileChannel.open(Paths.get("G:/Feb_21_235618_2.hsn"), StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);		
		ssc.bind(new InetSocketAddress(9999));
		SocketChannel sChannel = ssc.accept();
		
		ByteBuffer buf = ByteBuffer.allocate(1024);
		while(sChannel.read(buf) != -1){
			buf.flip();
			outChannel.write(buf);
			buf.clear();
		}
		sChannel.shutdownInput();
		
		//反馈给客户端
		buf.put("已经成功接收到来自客户端的数据".getBytes());
		buf.flip();
		sChannel.write(buf);
		sChannel.shutdownOutput();
		
		sChannel.close();
		outChannel.close();
		
	}
}

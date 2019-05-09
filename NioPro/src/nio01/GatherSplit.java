package nio01;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.Test;

public class GatherSplit {
	@Test
	public void test() throws Exception{
		FileChannel inChannel = FileChannel.open(Paths.get("g:/Result.txt"), StandardOpenOption.READ);
		FileChannel outChannel = FileChannel.open(Paths.get("g:/Result_copy"), StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);
		
		ByteBuffer[] bufs = {ByteBuffer.allocate(100),ByteBuffer.allocate(1024)};
		//分散读取
		inChannel.read(bufs);
		//切换成读模式
		for (ByteBuffer byteBuffer : bufs) {
			byteBuffer.flip();
		}
		//聚集写入
		outChannel.write(bufs);
	}
}

package nio01;

import java.nio.ByteBuffer;

import org.junit.Test;
/**
 * NIO缓冲区示例
 * ByteBuffer
 * CharBuffer
 * ShortBuffer
 * IntBuffer
 * LongBuffer
 * FloatBuffer
 * DoubleBuffer
 * 
 * 通过allocate()开辟缓冲区
 * 
 * put();	存入数据到缓冲区
 * get();	获取缓冲区中的数据
 * 
 * 缓冲区四个核心属性：
 * 	capacity：容量，表示缓冲区中最大存储数量的容量。一旦声明不能改变。
 *  limit：界限，缓冲区中可以操作数据的大小。
 *  position：位置，表示缓冲区中正在操作数据的位置
 *  mark：标记，记录当前position的位置，可以通过reset()恢复到mark位置。
 *  	先通过mark()标记position的位置，之后通过reset()可以恢复到mark()标记的位置。
 *  hasRemaining()：缓冲区是否还有可以操作的数据。
 *  remaining()：获取缓冲区中可以操作的数据的数量。
 *  
 *  position <= position <= capacity
 *
 */
public class BufferTest {

	@Test
	public void test() {
		String str = "abcde";
		ByteBuffer buf = ByteBuffer.allocate(1024);
		buf.put(str.getBytes());
		
		buf.flip();
		byte bs[] = new byte[buf.limit()];
		buf.get(bs);
		System.out.println(new String(bs));
		buf.clear();
	}

}

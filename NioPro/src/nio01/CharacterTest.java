package nio01;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import org.junit.Test;

public class CharacterTest {
	@Test
	public void test() throws Exception{
		Charset cs = Charset.forName("UTF-8");
		
		//获取编码器
		CharsetEncoder ce = cs.newEncoder();
		
		//获取解码器
		CharsetDecoder cd = cs.newDecoder();
		
		String data = "风萧萧兮易水寒";
		CharBuffer cb = CharBuffer.allocate(1024);
		cb.put(data);
		
		cb.flip();	//编码之前需先转换成读模式
		ByteBuffer bBuf = ce.encode(cb);	//编码，
		CharBuffer cBuf = cd.decode(bBuf);	//解码
		System.out.println(cBuf.toString());
	}
}

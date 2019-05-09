package nio01;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Scanner;

import org.junit.Test;

public class DatagramChannelTest {
	@Test
	public void client() throws Exception{
		DatagramChannel dc = DatagramChannel.open();
		dc.configureBlocking(false);
		ByteBuffer buf = ByteBuffer.allocate(1024);
		Scanner scanner = new Scanner(System.in);
		while(scanner.hasNext()){
			String str = scanner.next();
			buf.put(str.getBytes());
			buf.flip();
			dc.send(buf, new InetSocketAddress("127.0.0.1",9999));
			buf.clear();
		}
		scanner.close();
		dc.close();
	}
	
	@Test
	public void server() throws Exception{
		DatagramChannel dc = DatagramChannel.open();
		dc.bind(new InetSocketAddress(9999));
		
		dc.configureBlocking(false);
		Selector selector = Selector.open();
		dc.register(selector, SelectionKey.OP_READ);
		
		while(selector.select() > 0){
			Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			while(it.hasNext()){
				SelectionKey selectionKey = it.next();
				if(selectionKey.isReadable()){
					ByteBuffer buf = ByteBuffer.allocate(1024);
					dc.receive(buf);
					buf.flip();
					System.out.println(new String(buf.array(),0,buf.limit()));
				}
			}
			it.remove();
		}
	}
}

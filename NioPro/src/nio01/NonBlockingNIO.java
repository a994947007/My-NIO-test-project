package nio01;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import org.junit.Test;

public class NonBlockingNIO {

	@Test
	public void client() throws Exception{
		Scanner scan = new Scanner(System.in);
		SocketChannel sc = SocketChannel.open(new InetSocketAddress("127.0.0.1",9999));
		sc.configureBlocking(false);		//设置成非阻塞模式
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		while(scan.hasNext()){
			String str = scan.next();
			buf.put(str.getBytes());
			buf.flip();
			sc.write(buf);
			buf.clear();
		}
		
		scan.close();
		sc.close();
	}
	
	@Test
	public void server() throws Exception{
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);	//非阻塞模式
		ssc.bind(new InetSocketAddress(9999));
		
		Selector selector = Selector.open();
		ssc.register(selector, SelectionKey.OP_ACCEPT);	//将当前通道注册到选择器
		
		while(selector.select() > 0){		//轮询式的获取选择器上已经准备就绪的事件
			Set<SelectionKey> set = selector.selectedKeys();
			Iterator<SelectionKey> it = set.iterator();
			while(it.hasNext()){
				SelectionKey selectionKey = it.next();
				if(selectionKey.isAcceptable()){		//接收连接就绪，则获取连接
					SocketChannel sc = ssc.accept();
					sc.configureBlocking(false);		//将连接切换成非阻塞模式
					sc.register(selector, SelectionKey.OP_READ);
				}else if(selectionKey.isReadable()){
					ByteBuffer buf = ByteBuffer.allocate(1024);
					SocketChannel sc = (SocketChannel) selectionKey.channel();
					int len = 0;
					while((len = sc.read(buf)) > 0){
						buf.flip();
						System.out.println(new String(buf.array(),0,len));
						buf.clear();
					}
				}
				//取消选择键
				it.remove();
			}
		}
		selector.close();
		ssc.close();
	}
}

package nio01;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

/**
 * 单线程的NIO模式并发能力不足，当其中一个channel阻塞的时候，Selector不能继续进行后续的操作。因此，通过引入线程池，Selector
 * 注册完成Channel的事件之后，将其交给一个线程池来进行处理。
 * @author 路遥
 *
 */
public class NIOReactorTest {
	@Test
	public void client() throws IOException{
		Scanner scan = new Scanner(System.in);
		SocketChannel sc = SocketChannel.open(new InetSocketAddress("127.0.0.1",9999));
		sc.configureBlocking(false);
		
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
	public void server() throws IOException{
		ExecutorService pool = Executors.newFixedThreadPool(50);
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.bind(new InetSocketAddress(9999));
		ssc.configureBlocking(false);
		
		Selector selector = Selector.open();
		ssc.register(selector, SelectionKey.OP_ACCEPT);
		while(selector.select() > 0){
			Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			while(it.hasNext()){
				SelectionKey selectionKey = it.next();
				if(selectionKey.isAcceptable()){
					SocketChannel sc = ssc.accept();
					sc.configureBlocking(false);
					sc.register(selector, SelectionKey.OP_READ);
				}else if(selectionKey.isReadable()){
					pool.execute(new Handler(selectionKey));;
				}
				it.remove();
			}
		}
		
		selector.close();
		ssc.close();
		if(!pool.isShutdown()) pool.shutdown();
	}
	
	private class Handler implements Runnable{
		private SelectionKey selectionKey = null;
		public Handler(SelectionKey selectionKey){
			this.selectionKey = selectionKey;
		}
		@Override
		public void run() {
			SocketChannel sc = (SocketChannel) selectionKey.channel();
			ByteBuffer buf = ByteBuffer.allocate(1024);
			try {
				int len = 0;
				while((len = sc.read(buf)) > 0){
					buf.flip();
					System.out.println(new String(buf.array(),0,len));
					buf.clear();
				}
			} catch (IOException e) {
				e.printStackTrace();
				try {
					sc.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}

package nio01;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Scanner;

import org.junit.Test;

/**
 * Pipe通常用作线程间的通信
 * @author 路遥
 *
 */
public class PipeTest {
	
	public static void main(String[] args) throws IOException {
		Pipe pipe = Pipe.open();
		Thread t1 = new Thread(new NonBlockingSinkChannel(pipe));
		Thread t2 = new Thread(new NonBlockingSourceChannel(pipe));
		t1.start();
		t2.start();
	}

	@Test
	public void test() throws IOException{
		//获取管道
		Pipe pipe = Pipe.open();
		Thread t1 = new Thread(new SinkThread(pipe));
		Thread t2 = new Thread(new SourceThread(pipe));
		t1.start();
		t2.start();
	}
	
	@Test
	public void nonBlockingTest() throws IOException{
		Pipe pipe = Pipe.open();
		Thread t1 = new Thread(new NonBlockingSinkChannel(pipe));
		Thread t2 = new Thread(new NonBlockingSourceChannel(pipe));
		t1.start();
		t2.start();
	}
	
	private static class SinkThread implements Runnable{
		private Pipe pipe = null;
		public SinkThread(Pipe pipe){
			this.pipe = pipe;
		}
		@Override
		public void run() {
			Scanner scanner = new Scanner(System.in);
			Pipe.SinkChannel sinkChannel = pipe.sink();
			ByteBuffer buf = ByteBuffer.allocate(1024);
			try {		
				while(scanner.hasNext()){
					String str = scanner.next();
					buf.put(str.getBytes());
					buf.flip();
					sinkChannel.write(buf);
					buf.clear();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				scanner.close();
				try {
					sinkChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static class SourceThread implements Runnable{
		private Pipe pipe = null;
		
		public SourceThread(Pipe pipe){
			this.pipe = pipe;
		}
		
		@Override
		public void run() {
			Pipe.SourceChannel sourceChannel = pipe.source();
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			try {
				int len = 0;
				while((len = sourceChannel.read(buffer)) > 0){
					buffer.flip();
					System.out.println(new String(buffer.array(),0,len));
					buffer.clear();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					sourceChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static class NonBlockingSinkChannel implements Runnable{
		private Pipe pipe = null;
		public NonBlockingSinkChannel(Pipe pipe) {
			this.pipe = pipe;
		}
		@Override
		public void run() {
			Scanner scanner = new Scanner(System.in);
			Pipe.SinkChannel sinkChannel = pipe.sink();
			try {
				sinkChannel.configureBlocking(false);
				ByteBuffer buffer = ByteBuffer.allocate(1024);
				while(scanner.hasNext()){
					String str = scanner.next();
					buffer.put(str.getBytes());
					buffer.flip();
					sinkChannel.write(buffer);
					buffer.clear();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally{
				scanner.close();
				try {
					sinkChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static class NonBlockingSourceChannel implements Runnable{
		private Pipe pipe = null;
		public NonBlockingSourceChannel(Pipe pipe) {
			this.pipe = pipe;
		}
		@Override
		public void run() {
			Pipe.SourceChannel souceChannel = pipe.source();
			try {
				souceChannel.configureBlocking(false);
				Selector selector = Selector.open();
				souceChannel.register(selector, SelectionKey.OP_READ);
				while(selector.select() > 0){
					Iterator<SelectionKey> it = selector.selectedKeys().iterator();
					while(it.hasNext()){
						SelectionKey selectionKey = it.next();
						if(selectionKey.isReadable()){
							ByteBuffer buffer = ByteBuffer.allocate(1024);
							int len = 0;
							while((len = souceChannel.read(buffer)) > 0){
								buffer.flip();
								System.out.println(new String(buffer.array(),0,len));
								buffer.clear();
							}
						}
						it.remove();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					souceChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

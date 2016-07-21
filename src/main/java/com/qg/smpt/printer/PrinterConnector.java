package com.qg.smpt.printer;

import com.qg.smpt.util.Level;
import com.qg.smpt.util.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Created by tisong on 7/20/16.
 */
public class PrinterConnector implements Runnable, Lifecycle{

    private static final Logger LOGGER = Logger.getLogger(PrinterConnector.class.getName());

    private String address = null;

    private int bufferSize = 2048;

    private int curProcessors = 0;

    private int minProcessors = 5;

    private int maxProcessors = 20;

    private int port = 8080;

    private String threadName;

    /* 关于生命周期的标志 */
    private boolean initizlized = false;

    private boolean started = false;

    private boolean stopped = false;

    /**
     * 已经创建但还未被使用
     */
    private Stack<PrinterProcessor> processors = new Stack<PrinterProcessor>();

    /**
     * 创建的所有线程
     */
    private List<PrinterProcessor> createdProcessor = new LinkedList<PrinterProcessor>();

    private ServerSocketChannel ssc = null;

    private Selector selector = null;

    /**
     *创建 ServerSocketChannel
     */
    public void initialize() {

        LOGGER.log(Level.DEBUG, "PrinterConnector initizlize, create serversocketchannel");
        try {
            ssc = ServerSocketChannel.open();

            ssc.configureBlocking(false);

            ssc.socket().bind(new InetSocketAddress(port));

            selector = Selector.open();

            ssc.register(selector, SelectionKey.OP_ACCEPT);
        } catch (final ClosedChannelException e) {
            LOGGER.log(Level.ERROR, "Initializes PrinterConnector failed", e);
        } catch (final IOException e) {
            LOGGER.log(Level.ERROR, "Initializes PrinterConnector failed", e);
        }
    }


    public void start() throws LifecycleException{
        if (started) {
            throw new LifecycleException("printerConnector alreadyStarted");
        }

        started = true;

        threadName = "PrinterConnector[" + port + "]";

        threadStart();

        while (curProcessors < minProcessors) {
            if (maxProcessors > 0 && maxProcessors < curProcessors)
                break;
            PrinterProcessor processor = newProcessor();
            recycle(processor);
        }
    }

    public void stop() {

    }

    /**
     * 开启接收并处理客户端信息的线程
     */
    private void threadStart() {

        LOGGER.log(Level.DEBUG, "printerConnector starting");

        Thread thread = new Thread(this, threadName);

        thread.setDaemon(true);

        thread.start();
    }

    private void threadStop() {

    }

    public void run() {
        // ServerSocketChannel 阻塞接收 SocketChannel
        // 将接收到的Socket（长连接），如果是读操作：分发给一个线程去处理。
        // 如果是写操作：写入订单数据，分发给同样的线程去处理 Processor
        try {
            while (true) {
                selector.select();

                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    switch (key.readyOps()) {
                        case SelectionKey.OP_ACCEPT:
                            acceptSocket(key);
                            break;
                        case SelectionKey.OP_READ:
                            // 当有多个 SocketChannel时, 会自动筛选哪一个SocketChannel 触发了事件
                            // 1. 连接后的一个请求：将打印机id-主控板（用户）id绑定，将打印机id-SocketChannel绑定
                            PrinterProcessor processor = createProcessor();
                            processor.assign((SocketChannel) key.channel());
                            break;
                        default: // something was wrong
                            break;
                    }

                    it.remove();
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "serverSocketChannel ");
        }
    }

    private PrinterProcessor newProcessor() {

        PrinterProcessor processor = new PrinterProcessor(curProcessors++);

        processor.start();

        createdProcessor.add(processor);

        return processor;
    }

    private void recycle(PrinterProcessor processor) {

        processors.push(processor);
    }

    private PrinterProcessor createProcessor() {

        if (processors.size() > 0) {
            return processors.pop();
        } else {
            return newProcessor();
        }
    }


    private void acceptSocket(SelectionKey key) {

        try {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();

            SocketChannel sc = server.accept();

            sc.configureBlocking(false);

            sc.register(selector, SelectionKey.OP_READ);

            // 根据请求打印机id 进行 printer-socketchannel 匹配

        } catch (final IOException e) {

        }

    }
}

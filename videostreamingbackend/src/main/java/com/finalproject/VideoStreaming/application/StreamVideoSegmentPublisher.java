package com.finalproject.VideoStreaming.application;

import java.io.FileOutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.finalproject.VideoStreaming.domain.StreamVideoSegment;

import reactor.core.publisher.FluxSink;

/*
 * Publisher for the video segments. The StreamingClientWebSocketHandler puts the segments in the LinkedBlockingQueue
 * We take each segment and emit it
 * Needs an executor for the while loop.
 */
public class StreamVideoSegmentPublisher implements 
        Consumer<FluxSink<StreamVideoSegment>> {
    private final Executor executor;
    private final BlockingQueue<StreamVideoSegment> queue =
            new LinkedBlockingQueue<>();

    public StreamVideoSegmentPublisher(Executor executor) {
        this.executor = executor;
    }

    public void receiveSegment(StreamVideoSegment segment) {
    	queue.offer(segment);
    }
    
    @Override
    public void accept(FluxSink<StreamVideoSegment> sink) {
    	//the while loop blocks so we run it in a seperate thread using the executor 
        this.executor.execute(() -> {
            while (true)
                try {
                    StreamVideoSegment segment = queue.take();
                    
                    sink.next(segment);
                } catch (InterruptedException e) {
                    ReflectionUtils.rethrowRuntimeException(e);
                }
        });
    }
}

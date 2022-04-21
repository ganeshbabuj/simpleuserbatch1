package com.example.simpleuserbatch1.listener;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

@Component
public class ItemCountListener implements ChunkListener {

    @Override
    public void beforeChunk(ChunkContext context) {
    }

    @Override
    public void afterChunk(ChunkContext context) {

        int count = context.getStepContext().getStepExecution().getReadCount();
        System.out.println("Chunk | Cumulative processed count: " + count);
    }

    @Override
    public void afterChunkError(ChunkContext context) {
    }
}

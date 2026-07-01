package com.payment.personal.service;

import com.payment.personal.models.ChunkText;
import org.springframework.stereotype.Component;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class DocumentChunker {

    private static final int MAX_CHUNK_SIZE = 1200;

    public List<ChunkText> chunk(String text) {

        List<ChunkText> chunks = new ArrayList<>();

        BreakIterator iterator =
                BreakIterator.getSentenceInstance(Locale.US);

        iterator.setText(text);

        StringBuilder currentChunk = new StringBuilder();

        int chunkNo = 1;

        int start = iterator.first();

        for (int end = iterator.next();
             end != BreakIterator.DONE;
             start = end, end = iterator.next()) {

            String sentence = text.substring(start, end).trim();

            if (sentence.isEmpty()) {
                continue;
            }

            if (currentChunk.length() + sentence.length() > MAX_CHUNK_SIZE
                    && !currentChunk.isEmpty()) {

                chunks.add(ChunkText.builder()
                        .chunkNo(chunkNo++)
                        .characterCount(currentChunk.length())
                        .content(currentChunk.toString().trim())
                        .build());

                currentChunk.setLength(0);
            }

            currentChunk.append(sentence)
                    .append(" ");
        }

        if (!currentChunk.isEmpty()) {
            chunks.add(ChunkText.builder()
                    .chunkNo(chunkNo)
                    .characterCount(currentChunk.length())
                    .content(currentChunk.toString().trim())
                    .build());
        }

        return chunks;
    }
}

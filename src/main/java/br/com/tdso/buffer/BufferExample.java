package br.com.tdso.buffer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class BufferExample {

    public static void main(String[] args){

        Consumer<ByteBuffer> printBuffer = (buffer) -> {
            byte[] data = new byte[buffer.limit()];
            buffer.get(data);
            System.out.printf("\"%s\" ", new String(data, StandardCharsets.UTF_8) );
        };

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        doOperation("Iniciando", byteBuffer, (b) -> System.out.println(" " + b));
        doOperation("put", byteBuffer, (b) -> b.put("It is test".getBytes(StandardCharsets.UTF_8)));
        doOperation("flip ( WRITE to READ )", byteBuffer, ByteBuffer::flip);
        doOperation("read", byteBuffer, printBuffer);

        doOperation("flip ( READ to WRITE )", byteBuffer, ByteBuffer::flip);
        doOperation("compact", byteBuffer, ByteBuffer::compact);
        doOperation("append", byteBuffer, (b) -> b.put(" It is SECOND test".getBytes(StandardCharsets.UTF_8)));
        doOperation("flip ( WRITE to READ )", byteBuffer, ByteBuffer::flip);
        doOperation("read", byteBuffer, printBuffer);

/*
        após mudar de leitura para gravacao (flip) temos que :

        1 - mover position to end of text
        doOperation("mover POSITION to end (LIMIT) ", byteBuffer, (b) -> b.position(b.limit()));

        2 - mover capacity to limit
        doOperation("mover CAPACITY to LIMIT", byteBuffer, (b) -> b.limit(b.capacity()));

        3 - o método compact acima faz isso !! oara podermos fazer o append
        doOperation("append", byteBuffer, (b) -> b.put(" It is SECOND test".getBytes(StandardCharsets.UTF_8)));
*/
        doOperation("flip ( READ to WRITE )", byteBuffer, ByteBuffer::flip);
        doOperation("compact", byteBuffer, ByteBuffer::compact);
        doOperation("append", byteBuffer, (b) -> b.put(" It is THREE test".getBytes(StandardCharsets.UTF_8)));
        /* posso ler o buffer sem mudar o estado (flip) usando o método slice
         que retorna uma nova referência de buffer - testar com java 21
         */
        //doOperation("read with slice", byteBuffer.slice(0, byteBuffer.position()), printBuffer);

    }
    private static void doOperation(String op, ByteBuffer buffer, Consumer<ByteBuffer> c){
        System.out.println("Operacao: " + op);
        c.accept(buffer);
        System.out.printf("Capacity: %s, Limity: %s, Position: %s, Remaining: %s", buffer.capacity(), buffer.limit(), buffer.position(), buffer.remaining() );
        System.out.println(" ");
    }
}

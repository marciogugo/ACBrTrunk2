/// ACBrMonitor - Exemplo de Comunicação utilizando com Java
///  - Autor.......: Jose Mauro da Silva Sandy
///  - Colaboração : Celso Marigo Junior
///
import java.nio.ByteBuffer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class JavaNIOSocketExample {

    private static final int BUFFER_SIZE = 1024;
    private static final String END_COMMAND = System.getProperty("line.separator") + "." + System.getProperty("line.separator");

    private static String[] messages = {
        "BOLETO.ListaBancos",
        "ACBr.Ativo",
    };

    public static void main(String[] args) throws InterruptedException {
        logger("Iniciando a conexão com o ACBrMonitorPLUS...");
        try {
            int port = 3434;
            InetAddress inetAddress = InetAddress.getLocalHost();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, port);
            SocketChannel socketChannelClient = SocketChannel.open(inetSocketAddress);
            socketChannelClient.configureBlocking(false);
            
            logger(String.format("Tentando conectar em %s:%d...", inetSocketAddress.getHostName(), inetSocketAddress.getPort()));

            Thread.sleep(100);
            
            // Lendo mensagem de boas vindas do ACBrMonitor
            ByteBuffer buffer_out = ByteBuffer.allocate(1024);
            int idx = socketChannelClient.read(buffer_out);
            while (idx > 0 && socketChannelClient.isConnected()) {
                buffer_out.flip();
                while (buffer_out.hasRemaining()) {
                        System.out.print((char) buffer_out.get());
                }
                buffer_out.clear();
                idx = socketChannelClient.read(buffer_out);
            }                       
			
            // Envia comandos e lê as respostas
            for (String message : messages) {
                ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                buffer.put(message.getBytes());
                buffer.put(END_COMMAND.getBytes());
                buffer.flip();
                socketChannelClient.write(buffer);
                logger(String.format("\nEnviando comando: \"%s\"", message));
				
                Thread.sleep(100);                
                logger("Resposta do ACBrMonitorPLUS:");
                buffer_out = ByteBuffer.allocate(1024);
                idx = socketChannelClient.read(buffer_out);
                while (idx > 0 && socketChannelClient.isConnected()) {
                        buffer_out.flip();
                        while (buffer_out.hasRemaining()) {
                                System.out.print((char) buffer_out.get());
                        }
                        buffer_out.clear();
                        idx = socketChannelClient.read(buffer_out);
                }
            }
            
            logger("\nFechando a conexão...");
            socketChannelClient.close();
        } catch (Exception e) {
            logger(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void logger(String msg) {
        System.out.println(msg);
    }
}

import java.io.*;
import java.net.*; // classes que fazem o trabalho de rede TCP em si.
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Servidor TCP simples para testes de recebimento de dados de dispositivos.
 *
 * Compilar:  javac ServerTCP.java
 * Rodar:     java ServerTCP [porta]
 *            (porta padrão: 5000, se não informada)
 */
public class ServerTCP {

    private static final DateTimeFormatter TS_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                                            // send network error to take out application resposible
    public static void main(String[] args) throws IOException {
        int port = 5000;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log("Servidor TCP escutando na porta " + port + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                // Cada conexão é tratada em uma thread separada,
                // assim vários dispositivos podem conectar ao mesmo tempo.
                new Thread(() -> handleClient(clientSocket)).start();
            }

        }
    }

    private static void handleClient(Socket clientSocket) {
        String clientInfo = clientSocket.getInetAddress().getHostAddress()
                + ":" + clientSocket.getPort(); // monta uma string tipo 192.168.1.50:54321
        log("Conexão aberta de " + clientInfo);

                                // getInputStream pega o caminho de conexao
        try (InputStream in = clientSocket.getInputStream()) {

            byte[] buffer = new byte[4096]; // veriable to take (temporaly) how many bytes comes on each read
            int bytesRead;

            // Lê tudo o que o cliente enviar até fechar a conexão.
            // veja o uso do 'in' do InputStram in = clientSocket.getInputStram
            while ((bytesRead = in.read(buffer)) != -1) {
                byte[] data = new byte[bytesRead];

                // buffer variable, 0 is position to start put string, data is variable destination, 0 is position to copy, bytesRead is the size total
                System.arraycopy(buffer, 0, data, 0, bytesRead);

                log(String.format("Recebido de %s (%d bytes)", clientInfo, bytesRead));
                log("  Como texto: " + new String(data).trim());
                log("  Como hex:   " + bytesToHex(data)); // Forms to monitor received datas
            }

                // Case error connection middle transport infos
        } catch (IOException e) {
            log("Erro na conexão com " + clientInfo + ": " + e.getMessage());
        } finally {
            log("Conexão encerrada: " + clientInfo);
            try {
                clientSocket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }

    private static synchronized void log(String message) {
        String timestamp = LocalDateTime.now().format(TS_FORMAT);
        System.out.println("[" + timestamp + "] " + message);
    }
}